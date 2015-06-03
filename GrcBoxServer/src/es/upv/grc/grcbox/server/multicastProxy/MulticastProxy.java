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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.savarese.vserv.tcpip.UDPPacket;

import com.savarese.rocksaw.net.RawSocket;


/**
 * A simple multicast proxy that forwards multicast packets
 * between both interfaces. If a protocol requires to modified
 * any field of the packet payload, this class must be specialised
 * by implementing the method processPayloadIncoming(byte[] payload) and 
 * processPayloadOutgoing(byte[] payload).
 */
public class MulticastProxy implements Runnable{
	
	/** The Constant LOG. */
	protected static final Logger LOG = Logger.getLogger(MulticastProxy.class.getName());
	/*
	 * Instance variables
	 */
	/** The app id. */
	private int appId;
	
	/** The outer iface. */
	private String outerIface;
	
	/** The inner iface. */
	private String innerIface;
	
	/** The subscribe addr. */
	private String subscribeAddr;
	
	/** The client addr. */
	private String clientAddr;
	
	/** The in net mask length. */
	private int inNetMaskLength;
	
	/** The listen port. */
	int listenPort;
	
	/** The raw listen sock. */
	private RawSocket rawListenSock;
	
	/** The raw in sock. */
	private RawSocket rawInSock; //Socket bound to inIface
	
	/** The raw out sock. */
	private RawSocket rawOutSock; //Socket bound to outIface
	
	/** The out addr. */
	private InetAddress outAddr;
	
	/** The in addr. */
	private InetAddress inAddr;
	
	/** The multi sck. */
	private MulticastSocket multiSck;
	
	/** The running. */
	volatile boolean running = false;
	
	/** The sent. */
	private HashSet<Integer> sent = new HashSet<>();
	
	/**
	 * Instantiates a new multicast proxy.
	 * All the parameters need to be defined
	 * @param appId the app id
	 * @param innerIface the inner interface
	 * @param outerIface the outer interface
	 * @param clientAddr the client address
	 * @param subscribeAddr the subscribe address
	 * @param listenPort the listen port
	 */
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
	
	/**
	 * Process payload incomming.
	 *
	 * @param payload the payload
	 * @return the byte[]
	 */
	protected byte[] processPayloadIncomming(byte[] payload){
		return payload;
	}
	
	/**
	 * Process payload outgoing.
	 *
	 * @param payload the payload
	 * @return the byte[]
	 */
	protected byte[] processPayloadOutgoing(byte[] payload){
		return payload;
	}

	/**
	 * Run the MulticastProxy
	 */
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
			
			/*
			 * Multicast packets are not processed if there is no application subscribed to the group
			 */
			multiSck = new MulticastSocket(listenPort);
			multiSck.setNetworkInterface(NetworkInterface.getByName(innerIface));
			multiSck.joinGroup(InetAddress.getByName(subscribeAddr));
			multiSck.setNetworkInterface(NetworkInterface.getByName(outerIface));
			multiSck.joinGroup(InetAddress.getByName(subscribeAddr));
			
			/*
			 * Raw socket are used to receive packets
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
						/*
						 * Playing with raw sockets and packets
						 */
						rcvdPacket.setIPHeaderLength(rcvdPacket.getIPHeaderLength());
						int dstIp = rcvdPacket.getDestinationAsWord();
						int dstPort = rcvdPacket.getDestinationPort();
						
						/*
						 * Check if the message must be processed
						 */
						if(dstIp == subscribeAddrInt &&
								dstPort == listenPort){
							/*
							 * Get the size of the IP+UDP headers
							 */
							int offset = rcvdPacket.getCombinedHeaderByteLength();
							
							/*
							 * Extract the payload from the datagram
							 */
							byte[] payload = Arrays.copyOfRange(rcvdBuf, offset, 
									rcvdPacket.getIPPacketLength());

							boolean outgoing = srcIp == clientAddrInt;
							String type = outgoing?"outgoing":"incomming";
							
							/*
							 * Copy the old header to the new header
							 * Some fields must modified and checksum must be recalculated
							 */
							byte [] newHeader = Arrays.copyOf(rcvdBuf, 
									rcvdPacket.getCombinedHeaderByteLength());
							/*
							 * Get the new payload after processing
							 */
							byte [] newPayload = outgoing?
									processPayloadOutgoing(payload):
										processPayloadIncomming(payload);
							
							/*
							 * Allocate some memory for the new packet
							 */
							ByteBuffer outBuff = ByteBuffer.allocate(newHeader.length+newPayload.length);
							outBuff.put(newHeader);
							outBuff.put(newPayload);

							byte [] newData = outBuff.array();
							UDPPacket newPacket = new UDPPacket(newData.length);
							newPacket.setData(newData);
							newPacket.setUDPPacketLength(newPayload.length+UDPPacket.LENGTH_UDP_HEADER);
							newPacket.setIPPacketLength(newPayload.length+UDPPacket.LENGTH_UDP_HEADER +
									newPacket.getIPHeaderByteLength());

							/*
							 * If it is an outgoing packet, change the source address
							 */
							if(outgoing){
								newPacket.setSourceAsWord(outAddrInt);
							}
							newPacket.setIdentification(newPacket.getIdentification()-100);
							
							/*
							 * Compute the new checksums
							 */
							newPacket.computeUDPChecksum();
							newPacket.computeIPChecksum();

							/*
							 * store the packet at sent set
							 */
							sent.add(newPacket.getIPChecksum());
							try{
								if(outgoing){
									rawOutSock.write(InetAddress.getByName(subscribeAddr),
											newData, 0, newData.length);
								}
								else{
									rawInSock.write(InetAddress.getByName(subscribeAddr),
											newData, 0, newData.length);
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

	/**
	 * Equal subnet.
	 *
	 * @param ip1 the first ip
	 * @param ip2 the second Ip
	 * @param netMask the net mask length
	 * @return true, if both ips are inside the same subnet
	 */
	private boolean equalSubnet(int ip1, int ip2,
			int netMask) {
		int mask = 0xffffffff << (32 - netMask);
		int srcNet = ip1 & mask;
		int clientNet = ip2 & mask;
		return (srcNet == clientNet);
	}

	/**
	 * Stop.
	 */
	public void stop(){
		running = false;
		multiSck.close();
		try {
			rawListenSock.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Gets the app id.
	 *
	 * @return the app id
	 */
	public int getAppId() {
		return appId;
	}

	/**
	 * Sets the app id.
	 *
	 * @param appId the new app id
	 */
	public void setAppId(int appId) {
		this.appId = appId;
	}

	/**
	 * Gets the outer iface.
	 *
	 * @return the outer iface
	 */
	public String getOuterIface() {
		return outerIface;
	}

	/**
	 * Sets the outer iface.
	 *
	 * @param outerIface the new outer iface
	 */
	public void setOuterIface(String outerIface) {
		this.outerIface = outerIface;
	}

	/**
	 * Gets the inner iface.
	 *
	 * @return the inner iface
	 */
	public String getInnerIface() {
		return innerIface;
	}

	/**
	 * Sets the inner iface.
	 *
	 * @param innerIface the new inner iface
	 */
	public void setInnerIface(String innerIface) {
		this.innerIface = innerIface;
	}

	/**
	 * Gets the client addr.
	 *
	 * @return the client addr
	 */
	public String getClientAddr() {
		return clientAddr;
	}

	/**
	 * Sets the client addr.
	 *
	 * @param clientAddr the new client addr
	 */
	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}

	/**
	 * Gets the listen port.
	 *
	 * @return the listen port
	 */
	public int getListenPort() {
		return listenPort;
	}

	/**
	 * Sets the listen port.
	 *
	 * @param listenPort the new listen port
	 */
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	/**
	 * Byte array2int.
	 *
	 * @param array the array
	 * @return the int
	 */
	private int byteArray2int(byte[] array){
		int result = 0;
		for (byte b: array)  
		{  
		    result = result << 8 | (b & 0xFF);  
		}
		return result; 
	}
}
