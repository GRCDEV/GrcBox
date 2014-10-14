package es.upv.grc.grcbox.androlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import es.upv.grc.grcbox.common.GrcBoxRule;

public class GrcBoxSocket {

	private GrcBoxClientService owner;
	private GrcBoxRule rule;
	private Socket socket;

	public GrcBoxSocket(GrcBoxClientService owner, GrcBoxRule rule, Socket socket) {
		super();
		this.owner = owner;
		this.rule = rule;
		this.socket = socket;
	}

	public GrcBoxRule getRule() {
		return rule;
	}

	public void close() throws IOException{
		socket.close();
		owner.removeRule(rule);
	}

	/**
	 * @param localAddr
	 * @throws IOException
	 * @see java.net.Socket#bind(java.net.SocketAddress)
	 */
	public void bind(SocketAddress localAddr) throws IOException {
		socket.bind(localAddr);
	}



	/**
	 * @param remoteAddr
	 * @param timeout
	 * @throws IOException
	 * @see java.net.Socket#connect(java.net.SocketAddress, int)
	 */
	public void connect(SocketAddress remoteAddr, int timeout)
			throws IOException {
		socket.connect(remoteAddr, timeout);
	}



	/**
	 * @param remoteAddr
	 * @throws IOException
	 * @see java.net.Socket#connect(java.net.SocketAddress)
	 */
	public void connect(SocketAddress remoteAddr) throws IOException {
		socket.connect(remoteAddr);
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
	 * @see java.net.Socket#getChannel()
	 */
	public SocketChannel getChannel() {
		return socket.getChannel();
	}



	/**
	 * @return
	 * @see java.net.Socket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}



	/**
	 * @return
	 * @throws IOException
	 * @see java.net.Socket#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getKeepAlive()
	 */
	public boolean getKeepAlive() throws SocketException {
		return socket.getKeepAlive();
	}



	/**
	 * @return
	 * @see java.net.Socket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}



	/**
	 * @return
	 * @see java.net.Socket#getLocalPort()
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}



	/**
	 * @return
	 * @see java.net.Socket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getOOBInline()
	 */
	public boolean getOOBInline() throws SocketException {
		return socket.getOOBInline();
	}



	/**
	 * @return
	 * @throws IOException
	 * @see java.net.Socket#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}



	/**
	 * @return
	 * @see java.net.Socket#getPort()
	 */
	public int getPort() {
		return socket.getPort();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getReceiveBufferSize()
	 */
	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}



	/**
	 * @return
	 * @see java.net.Socket#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getReuseAddress()
	 */
	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getSendBufferSize()
	 */
	public int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getSoLinger()
	 */
	public int getSoLinger() throws SocketException {
		return socket.getSoLinger();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getSoTimeout()
	 */
	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getTcpNoDelay()
	 */
	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}



	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getTrafficClass()
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
	 * @see java.net.Socket#isBound()
	 */
	public boolean isBound() {
		return socket.isBound();
	}



	/**
	 * @return
	 * @see java.net.Socket#isClosed()
	 */
	public boolean isClosed() {
		return socket.isClosed();
	}



	/**
	 * @return
	 * @see java.net.Socket#isConnected()
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}



	/**
	 * @return
	 * @see java.net.Socket#isInputShutdown()
	 */
	public boolean isInputShutdown() {
		return socket.isInputShutdown();
	}



	/**
	 * @return
	 * @see java.net.Socket#isOutputShutdown()
	 */
	public boolean isOutputShutdown() {
		return socket.isOutputShutdown();
	}



	/**
	 * @param value
	 * @throws IOException
	 * @see java.net.Socket#sendUrgentData(int)
	 */
	public void sendUrgentData(int value) throws IOException {
		socket.sendUrgentData(value);
	}



	/**
	 * @param keepAlive
	 * @throws SocketException
	 * @see java.net.Socket#setKeepAlive(boolean)
	 */
	public void setKeepAlive(boolean keepAlive) throws SocketException {
		socket.setKeepAlive(keepAlive);
	}



	/**
	 * @param oobinline
	 * @throws SocketException
	 * @see java.net.Socket#setOOBInline(boolean)
	 */
	public void setOOBInline(boolean oobinline) throws SocketException {
		socket.setOOBInline(oobinline);
	}



	/**
	 * @param connectionTime
	 * @param latency
	 * @param bandwidth
	 * @see java.net.Socket#setPerformancePreferences(int, int, int)
	 */
	public void setPerformancePreferences(int connectionTime, int latency,
			int bandwidth) {
		socket.setPerformancePreferences(connectionTime, latency, bandwidth);
	}



	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.Socket#setReceiveBufferSize(int)
	 */
	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}



	/**
	 * @param reuse
	 * @throws SocketException
	 * @see java.net.Socket#setReuseAddress(boolean)
	 */
	public void setReuseAddress(boolean reuse) throws SocketException {
		socket.setReuseAddress(reuse);
	}



	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.Socket#setSendBufferSize(int)
	 */
	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}



	/**
	 * @param on
	 * @param timeout
	 * @throws SocketException
	 * @see java.net.Socket#setSoLinger(boolean, int)
	 */
	public void setSoLinger(boolean on, int timeout) throws SocketException {
		socket.setSoLinger(on, timeout);
	}



	/**
	 * @param timeout
	 * @throws SocketException
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}



	/**
	 * @param on
	 * @throws SocketException
	 * @see java.net.Socket#setTcpNoDelay(boolean)
	 */
	public void setTcpNoDelay(boolean on) throws SocketException {
		socket.setTcpNoDelay(on);
	}



	/**
	 * @param value
	 * @throws SocketException
	 * @see java.net.Socket#setTrafficClass(int)
	 */
	public void setTrafficClass(int value) throws SocketException {
		socket.setTrafficClass(value);
	}



	/**
	 * @throws IOException
	 * @see java.net.Socket#shutdownInput()
	 */
	public void shutdownInput() throws IOException {
		socket.shutdownInput();
	}



	/**
	 * @throws IOException
	 * @see java.net.Socket#shutdownOutput()
	 */
	public void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}



	/**
	 * @return
	 * @see java.net.Socket#toString()
	 */
	public String toString() {
		return socket.toString();
	}
	
	
}
