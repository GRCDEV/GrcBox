package es.upv.grc.grcbox.androlib;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;

import es.upv.grc.grcbox.common.GrcBoxRule;

public class GrcBoxServerSocket{

	private GrcBoxClient owner;
	private GrcBoxRule rule;
	private ServerSocket socket;
	
	
	public GrcBoxServerSocket(GrcBoxClient owner, GrcBoxRule rule,
			ServerSocket socket) {
		super();
		this.owner = owner;
		this.rule = rule;
		this.socket = socket;
	}
	
	
	
	/**
	 * @return the rule
	 */
	public GrcBoxRule getRule() {
		return rule;
	}



	/**
	 * @return
	 * @throws IOException
	 * @see java.net.ServerSocket#accept()
	 */
	public Socket accept() throws IOException {
		return socket.accept();
	}
	/**
	 * @param localAddr
	 * @param backlog
	 * @throws IOException
	 * @see java.net.ServerSocket#bind(java.net.SocketAddress, int)
	 */
	public void bind(SocketAddress localAddr, int backlog) throws IOException {
		socket.bind(localAddr, backlog);
	}
	/**
	 * @param localAddr
	 * @throws IOException
	 * @see java.net.ServerSocket#bind(java.net.SocketAddress)
	 */
	public void bind(SocketAddress localAddr) throws IOException {
		socket.bind(localAddr);
	}
	/**
	 * @throws IOException
	 * @see java.net.ServerSocket#close()
	 */
	public void close() throws IOException {
		socket.close();
		owner.removeRule(rule);
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
	 * @see java.net.ServerSocket#getChannel()
	 */
	public ServerSocketChannel getChannel() {
		return socket.getChannel();
	}
	/**
	 * @return
	 * @see java.net.ServerSocket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}
	/**
	 * @return
	 * @see java.net.ServerSocket#getLocalPort()
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}
	/**
	 * @return
	 * @see java.net.ServerSocket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.ServerSocket#getReceiveBufferSize()
	 */
	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}
	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.ServerSocket#getReuseAddress()
	 */
	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}
	/**
	 * @return
	 * @throws IOException
	 * @see java.net.ServerSocket#getSoTimeout()
	 */
	public int getSoTimeout() throws IOException {
		return socket.getSoTimeout();
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
	 * @see java.net.ServerSocket#isBound()
	 */
	public boolean isBound() {
		return socket.isBound();
	}
	/**
	 * @return
	 * @see java.net.ServerSocket#isClosed()
	 */
	public boolean isClosed() {
		return socket.isClosed();
	}
	/**
	 * @param connectionTime
	 * @param latency
	 * @param bandwidth
	 * @see java.net.ServerSocket#setPerformancePreferences(int, int, int)
	 */
	public void setPerformancePreferences(int connectionTime, int latency,
			int bandwidth) {
		socket.setPerformancePreferences(connectionTime, latency, bandwidth);
	}
	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.ServerSocket#setReceiveBufferSize(int)
	 */
	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}
	/**
	 * @param reuse
	 * @throws SocketException
	 * @see java.net.ServerSocket#setReuseAddress(boolean)
	 */
	public void setReuseAddress(boolean reuse) throws SocketException {
		socket.setReuseAddress(reuse);
	}
	/**
	 * @param timeout
	 * @throws SocketException
	 * @see java.net.ServerSocket#setSoTimeout(int)
	 */
	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}
	/**
	 * @return
	 * @see java.net.ServerSocket#toString()
	 */
	public String toString() {
		return socket.toString();
	}
	
}
