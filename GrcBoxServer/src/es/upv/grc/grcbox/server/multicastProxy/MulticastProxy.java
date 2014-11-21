package es.upv.grc.grcbox.server.multicastProxy;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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
			LOG.info("Initializing Multicast proxy: Interfaces "+ innerIface + "," + outerIface+", Address: " + subscribeAddr+  " Port:"+ listenPort);
			/*
			 * find the Ipv4 of the innerIface
			 */
			inAddr = null;
			Enumeration<InetAddress> addrs = NetworkInterface.getByName(innerIface).getInetAddresses();
			while( addrs.hasMoreElements()) {
				inAddr = addrs.nextElement();
				if(inAddr instanceof Inet4Address){
					break;
				}
			}
			
			/*
			 * find the ipv4 of the innerIface
			 */
			addrs = NetworkInterface.getByName(outerIface).getInetAddresses();
			outAddr = null;
			while( addrs.hasMoreElements()) {
				outAddr = addrs.nextElement();
				if(outAddr instanceof Inet4Address){
					break;
				}
			}
			
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
					String srcIp = rcvdPacket.getSourceAsInetAddress().getHostAddress();
					if(!srcIp.equals(outAddr.getHostAddress()) && 
							!sent.contains(rcvdPacket.getIPChecksum())){
						rcvdPacket.setIPHeaderLength(rcvdPacket.getIPHeaderLength());

						int offset = rcvdPacket.getCombinedHeaderByteLength();
						byte[] payload = Arrays.copyOfRange(rcvdBuf, offset, rcvdPacket.getIPPacketLength());
						String dstIp = rcvdPacket.getDestinationAsInetAddress().getHostAddress();
						
						int dstPort = rcvdPacket.getDestinationPort();
						boolean outgoing = srcIp.equals(clientAddr);
						String type = outgoing?"outgoing":"incomming";
			
						if(dstIp.equals(subscribeAddr) && 
								dstPort == listenPort){
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
								byte [] srcAddr = outAddr.getAddress();
								int newSrc = (srcAddr[0]<<24)&0xff000000|
										(srcAddr[1]<<16)&0x00ff0000|
										(srcAddr[2]<< 8)&0x0000ff00|
										(srcAddr[3]<< 0)&0x000000ff;
								newPacket.setSourceAsWord(newSrc);
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
}
