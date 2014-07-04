package es.upv.grc.grcbox.androlib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import es.upv.grc.grcbox.common.GrcBoxRule;

/*
 * TODO Multicast Socket are currently unsupported.
 * Rules must be created when join a leave group methods are called.
 */

public class GrcBoxMulticasSocket extends MulticastSocket {
	
	
	private GrcBoxClient owner;
	private GrcBoxRule rule;
	private MulticastSocket socket;
	
	public GrcBoxMulticasSocket(GrcBoxClient owner,
			MulticastSocket socket) throws IOException {
		super();
		this.owner = owner;
		this.socket = socket;
	}
	
	/**
	 * @param localAddr
	 * @throws SocketException
	 * @see java.net.DatagramSocket#bind(java.net.SocketAddress)
	 */
	public void bind(SocketAddress localAddr) throws SocketException {
		socket.bind(localAddr);
	}
	
	/**
	 * 
	 * @see java.net.DatagramSocket#close()
	 */
	public void close() {
		socket.close();
		owner.removeRule(rule);
	}
	/**
	 * @param peer
	 * @throws SocketException
	 * @see java.net.DatagramSocket#connect(java.net.SocketAddress)
	 */
	public void connect(SocketAddress peer) throws SocketException {
		socket.connect(peer);
	}
	/**
	 * @param address
	 * @param port
	 * @see java.net.DatagramSocket#connect(java.net.InetAddress, int)
	 */
	public void connect(InetAddress address, int port) {
		socket.connect(address, port);
	}
	/**
	 * 
	 * @see java.net.DatagramSocket#disconnect()
	 */
	public void disconnect() {
		socket.disconnect();
	}
	/**
	 * @param o
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return socket.equals(o);
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.DatagramSocket#getBroadcast()
	 */
	public boolean getBroadcast() throws SocketException {
		return socket.getBroadcast();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getChannel()
	 */
	public DatagramChannel getChannel() {
		return socket.getChannel();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.MulticastSocket#getInterface()
	 */
	public InetAddress getInterface() throws SocketException {
		return socket.getInterface();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getLocalPort()
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.MulticastSocket#getLoopbackMode()
	 */
	public boolean getLoopbackMode() throws SocketException {
		return socket.getLoopbackMode();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.MulticastSocket#getNetworkInterface()
	 */
	public NetworkInterface getNetworkInterface() throws SocketException {
		return socket.getNetworkInterface();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getPort()
	 */
	public int getPort() {
		return socket.getPort();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.DatagramSocket#getReceiveBufferSize()
	 */
	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.DatagramSocket#getReuseAddress()
	 */
	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.DatagramSocket#getSendBufferSize()
	 */
	public int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.DatagramSocket#getSoTimeout()
	 */
	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}
	
	/**
	 * @return
	 * @throws IOException
	 * @see java.net.MulticastSocket#getTimeToLive()
	 */
	public int getTimeToLive() throws IOException {
		return socket.getTimeToLive();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.DatagramSocket#getTrafficClass()
	 */
	public int getTrafficClass() throws SocketException {
		return socket.getTrafficClass();
	}
	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return socket.hashCode();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#isBound()
	 */
	public boolean isBound() {
		return socket.isBound();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#isClosed()
	 */
	public boolean isClosed() {
		return socket.isClosed();
	}
	/**
	 * @return
	 * @see java.net.DatagramSocket#isConnected()
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}
	/**
	 * @param groupAddr
	 * @throws IOException
	 * @see java.net.MulticastSocket#joinGroup(java.net.InetAddress)
	 */
	public void joinGroup(InetAddress groupAddr) throws IOException {
		socket.joinGroup(groupAddr);
	}
	/**
	 * @param groupAddress
	 * @param netInterface
	 * @throws IOException
	 * @see java.net.MulticastSocket#joinGroup(java.net.SocketAddress, java.net.NetworkInterface)
	 */
	public void joinGroup(SocketAddress groupAddress,
			NetworkInterface netInterface) throws IOException {
		socket.joinGroup(groupAddress, netInterface);
	}
	/**
	 * @param groupAddr
	 * @throws IOException
	 * @see java.net.MulticastSocket#leaveGroup(java.net.InetAddress)
	 */
	public void leaveGroup(InetAddress groupAddr) throws IOException {
		socket.leaveGroup(groupAddr);
	}
	/**
	 * @param groupAddress
	 * @param netInterface
	 * @throws IOException
	 * @see java.net.MulticastSocket#leaveGroup(java.net.SocketAddress, java.net.NetworkInterface)
	 */
	public void leaveGroup(SocketAddress groupAddress,
			NetworkInterface netInterface) throws IOException {
		socket.leaveGroup(groupAddress, netInterface);
	}
	/**
	 * @param pack
	 * @throws IOException
	 * @see java.net.DatagramSocket#receive(java.net.DatagramPacket)
	 */
	public void receive(DatagramPacket pack) throws IOException {
		socket.receive(pack);
	}
	
	/**
	 * @param pack
	 * @throws IOException
	 * @see java.net.DatagramSocket#send(java.net.DatagramPacket)
	 */
	public void send(DatagramPacket pack) throws IOException {
		socket.send(pack);
	}
	/**
	 * @param broadcast
	 * @throws SocketException
	 * @see java.net.DatagramSocket#setBroadcast(boolean)
	 */
	public void setBroadcast(boolean broadcast) throws SocketException {
		socket.setBroadcast(broadcast);
	}
	/**
	 * @param address
	 * @throws SocketException
	 * @see java.net.MulticastSocket#setInterface(java.net.InetAddress)
	 */
	public void setInterface(InetAddress address) throws SocketException {
		socket.setInterface(address);
	}
	/**
	 * @param disable
	 * @throws SocketException
	 * @see java.net.MulticastSocket#setLoopbackMode(boolean)
	 */
	public void setLoopbackMode(boolean disable) throws SocketException {
		socket.setLoopbackMode(disable);
	}
	/**
	 * @param networkInterface
	 * @throws SocketException
	 * @see java.net.MulticastSocket#setNetworkInterface(java.net.NetworkInterface)
	 */
	public void setNetworkInterface(NetworkInterface networkInterface)
			throws SocketException {
		socket.setNetworkInterface(networkInterface);
	}
	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.DatagramSocket#setReceiveBufferSize(int)
	 */
	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}
	/**
	 * @param reuse
	 * @throws SocketException
	 * @see java.net.DatagramSocket#setReuseAddress(boolean)
	 */
	public void setReuseAddress(boolean reuse) throws SocketException {
		socket.setReuseAddress(reuse);
	}
	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.DatagramSocket#setSendBufferSize(int)
	 */
	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}
	/**
	 * @param timeout
	 * @throws SocketException
	 * @see java.net.DatagramSocket#setSoTimeout(int)
	 */
	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}
	
	/**
	 * @param ttl
	 * @throws IOException
	 * @see java.net.MulticastSocket#setTimeToLive(int)
	 */
	public void setTimeToLive(int ttl) throws IOException {
		socket.setTimeToLive(ttl);
	}
	/**
	 * @param value
	 * @throws SocketException
	 * @see java.net.DatagramSocket#setTrafficClass(int)
	 */
	public void setTrafficClass(int value) throws SocketException {
		socket.setTrafficClass(value);
	}
	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return socket.toString();
	}
	
}
