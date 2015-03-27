package es.upv.grc.grcbox.server.multicastProxy;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.savarese.vserv.tcpip.UDPPacket;

import com.savarese.rocksaw.net.RawSocket;


/*
 * A simple multicast proxy that forwards multicast packets
 * between both interfaces. If a protocol requires to modified
 * any field of the packet payload, this class must be specialized
 * by implementing the method processPayload(byte[] payload).
 */
public class MulticastProxy implements Runnable{
	protected static final Logger LOG = Logger.getLogger(MulticastProxy.class.getName());
	/*
	 * Instance variables
	 */
	private int appId;
	private String outerIface;
	private String innerIface;
	private String subscribeAddr;
	private String clientAddr;
	private int inNetMaskLength;
	int listenPort;
	
	private RawSocket rawListenSock;
	private RawSocket rawInSock; //Socket bound to inIface
	private RawSocket rawOutSock; //Socket bound to outIface
	private InetAddress outAddr;
	private InetAddress inAddr;
	private MulticastSocket multiSck;
	volatile boolean running = false;
	
	private HashSet<Integer> sent = new HashSet<>();
	
	public MulticastProxy(int appId, String innerIface, String outerIface, String clientAddr, 
			String subscribeAddr, int listenPort) {
		super();
		this.appId = appId;
		this.outerIface = outerIface;
		this.innerIface = innerIface;
		this.clientAddr = clientAddr;
		this.subscribeAddr = subscribeAddr;
		this.listenPort = listenPort;
	}
	
	protected byte[] processPayloadIncomming(byte[] payload){
		return payload;
	}
	
	protected byte[] processPayloadOutgoing(byte[] payload){
		return payload;
	}

	public void run() {
		try {
			/*
			 * Convert clientAddr and subscribe address to integer
			 */
			byte[] clientAddrByte = InetAddress.getByName(clientAddr).getAddress();
			int clientAddrInt = byteArray2int(clientAddrByte);
			byte[] subscribeAddrByte = InetAddress.getByName(subscribeAddr).getAddress();
			int subscribeAddrInt = byteArray2int(subscribeAddrByte);
			/*
			 * find the Ipv4 of the innerIface
			 */
			inAddr = null;
			List<InterfaceAddress> addrs = NetworkInterface.getByName(innerIface).getInterfaceAddresses();
			for (InterfaceAddress interfaceAddress : addrs) {
				inNetMaskLength = interfaceAddress.getNetworkPrefixLength();
				inAddr = interfaceAddress.getAddress();
				if(inAddr instanceof Inet4Address){
					break;
				}
			}
			
			byte[] inAddrByte = inAddr.getAddress();
			int inAddrInt = byteArray2int(inAddrByte);
			/*
			 * find the ipv4 of the outerIface
			 */
			addrs = NetworkInterface.getByName(outerIface).getInterfaceAddresses();
			outAddr = null;
			for (InterfaceAddress addr : addrs) {
				outAddr = addr.getAddress();
				if(outAddr instanceof Inet4Address){
					break;
				}
			}
			byte[] outAddrByte = outAddr.getAddress();
			int outAddrInt = byteArray2int(outAddrByte);
			
			LOG.info("Initializing Multicast proxy: Interfaces "+ innerIface + "," + outerIface+
					", Address: " + subscribeAddr+  " Port:"+ listenPort +
					"\n outAddrInt " + outAddrInt +
					"\n inAddrInt " +inAddrInt
					);

			/*
			 * Multicast packets are not processed if there is no application subscribed to the group
			 */
			multiSck = new MulticastSocket(listenPort);
			multiSck.setNetworkInterface(NetworkInterface.getByName(innerIface));
			multiSck.joinGroup(InetAddress.getByName(subscribeAddr));
			multiSck.setNetworkInterface(NetworkInterface.getByName(outerIface));
			multiSck.joinGroup(InetAddress.getByName(subscribeAddr));
			
			/*
			 * Raw socket are using to receive packets
			 */
			rawListenSock = new RawSocket();
			rawListenSock.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
			rawListenSock.setIPHeaderInclude(true);
			rawListenSock.setReceiveTimeout(1000);
			
			rawInSock = new RawSocket();
			rawInSock.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
			rawInSock.bindDevice(innerIface);
			rawInSock.setIPHeaderInclude(true);
			
			rawOutSock = new RawSocket();
			rawOutSock.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
			rawOutSock.bindDevice(outerIface);
			rawOutSock.setIPHeaderInclude(true);
			
			running=true;
			while(running){
				try {
					int size = 2512;
					byte[] rcvdBuf = new byte[size];
					UDPPacket rcvdPacket = new UDPPacket(size);
					rcvdPacket.setData(rcvdBuf);
					rawListenSock.read(rcvdBuf);
					int srcIp = rcvdPacket.getSourceAsWord();

					/*
					 * If the plugin have sent the message, ignore it
					 */
					if( srcIp == outAddrInt || sent.contains(rcvdPacket.getIPChecksum())){
					}
					/*
					 * If the src address is inside the inner network, but it is not the owner of the rule, ignore the packet
					 */
					else if(equalSubnet(srcIp, clientAddrInt, inNetMaskLength) && srcIp != clientAddrInt){
						continue;
					}
					else{
						rcvdPacket.setIPHeaderLength(rcvdPacket.getIPHeaderLength());
						int dstIp = rcvdPacket.getDestinationAsWord();
						int dstPort = rcvdPacket.getDestinationPort();

						int offset = rcvdPacket.getCombinedHeaderByteLength();
						byte[] payload = Arrays.copyOfRange(rcvdBuf, offset, rcvdPacket.getIPPacketLength());

						boolean outgoing = srcIp == clientAddrInt;
						String type = outgoing?"outgoing":"incomming";

						if(dstIp == subscribeAddrInt &&
								dstPort == listenPort){
							LOG.info("Time " + System.currentTimeMillis());
							LOG.info("An "+ type + " Raw UDP packet has been received " +
									" Source " +  srcIp +
									" Checksum " +rcvdPacket.getIPChecksum() +
									" Destination: "  + dstIp +
									" Protocol " + rcvdPacket.getProtocol() +
									" Version " + rcvdPacket.getIPVersion() +
									" IP Header Size " + rcvdPacket.getIPHeaderByteLength() +
									" IP Length  " +rcvdPacket.getIPPacketLength() +
									" SrcPort " + rcvdPacket.getSourcePort() +
									" DstPort " + dstPort+
									" Combined Header Length " + rcvdPacket.getCombinedHeaderByteLength()+
									" Payload Length " + payload.length +
									" Payload " + (new String(payload, 0, payload.length)) );

							byte [] newHeader = Arrays.copyOf(rcvdBuf, rcvdPacket.getCombinedHeaderByteLength());

							byte [] newPayload = outgoing?processPayloadOutgoing(payload):processPayloadIncomming(payload);

							ByteBuffer outBuff = ByteBuffer.allocate(newHeader.length+newPayload.length);
							outBuff.put(newHeader);
							outBuff.put(newPayload);

							byte [] newData = outBuff.array();
							UDPPacket newPacket = new UDPPacket(newData.length);
							newPacket.setData(newData);
							newPacket.setUDPPacketLength(newPayload.length+UDPPacket.LENGTH_UDP_HEADER);
							newPacket.setIPPacketLength(newPayload.length+UDPPacket.LENGTH_UDP_HEADER+newPacket.getIPHeaderByteLength());

							if(outgoing){
								newPacket.setSourceAsWord(outAddrInt);
							}
							newPacket.setIdentification(newPacket.getIdentification()-100);

							newPacket.computeUDPChecksum();
							newPacket.computeIPChecksum();
							LOG.info("Fordwarding packet to " + outerIface +" with address " + outAddr.getHostName() );
							LOG.info("Contents " +
									" Source " + newPacket.getSourceAsInetAddress().getHostAddress() +
									" Checksum " +newPacket.getIPChecksum() +
									" Destination: "  + newPacket.getDestinationAsInetAddress().getHostAddress()+
									" Protocol " + newPacket.getProtocol() +
									" Version " + newPacket.getIPVersion() +
									" IP Header Size " + newPacket.getIPHeaderByteLength() +
									" IP Length  " +newPacket.getIPPacketLength() +
									" SrcPort " + newPacket.getSourcePort() +
									" DstPort " + newPacket.getDestinationPort()+
									" Combined Header Length " + newPacket.getCombinedHeaderByteLength()+
									" UDP Packet Size" + newPacket.getUDPPacketLength() +
									" Payload " + (new String(newPayload, 0, newPayload.length)) );
							sent.add(newPacket.getIPChecksum());
							try{
								if(outgoing){
									rawOutSock.write(InetAddress.getByName(subscribeAddr), newData, 0, newData.length);
								}
								else{
									rawInSock.write(InetAddress.getByName(subscribeAddr), newData, 0, newData.length);
								}
							}
							catch(Exception e){
								LOG.severe("ERROR FORWARDING A PACKET");
								e.printStackTrace();
							}
						}
						else{
							sent.remove(rcvdPacket.getIPChecksum());
						}
					}
				}

				catch (InterruptedIOException e){
					/*
					 * Ignore interrupted IOException.
					 * We need to set up a timeout in the socket to be able to
					 * stop the proxy, rawSockets are not interrupted when closed :(
					 */
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			stop();
			e.printStackTrace();
		}
	}

	private boolean equalSubnet(int srcIp, int clientAddrInt,
			int inNetMaskLength2) {
		int mask = 0xffffffff << (32 - inNetMaskLength2);
		int srcNet = srcIp & mask;
		int clientNet = clientAddrInt & mask;
		return (srcNet == clientNet);
	}

	public void stop(){
		running = false;
		multiSck.close();
		try {
			rawListenSock.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getOuterIface() {
		return outerIface;
	}

	public void setOuterIface(String outerIface) {
		this.outerIface = outerIface;
	}

	public String getInnerIface() {
		return innerIface;
	}

	public void setInnerIface(String innerIface) {
		this.innerIface = innerIface;
	}

	public String getClientAddr() {
		return clientAddr;
	}

	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	private int byteArray2int(byte[] array){
		int result = 0;
		for (byte b: array)  
		{  
		    result = result << 8 | (b & 0xFF);  
		}
		return result; 
	}
}
