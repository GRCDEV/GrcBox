package es.upv.grc.grcbox.androlib;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import es.upv.grc.grcbox.common.GrcBoxRule;


public class GrcBoxDatagramSocket extends DatagramSocket {

	private GrcBoxClientService owner;
	private GrcBoxRule rule;
	private DatagramSocket socket;
	public GrcBoxDatagramSocket(GrcBoxClientService owner, GrcBoxRule rule,
			DatagramSocket socket) throws SocketException {
		super();
		this.owner = owner;
		this.rule = rule;
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
