package es.upv.grc.grcbox.common;

import org.restlet.resource.ResourceException;

public class GrcBoxRule {
	public enum Protocol{
		TCP, UDP;

		public static Protocol fromInt(int n){
			return Protocol.values()[n];
		}
		
		public static int toInt(Protocol proto){
			return proto.ordinal();
		}
	}
	/*
	 * Common parameters
	 */
	protected int id; //Unique Id of this rule
	protected Protocol proto;	//Protocol
	protected boolean incomming;
	protected int appid;	//AppId of the owner app
	protected String ifName; // Outgoing or incomming interface
	protected long expireDate;
	protected int srcPort;
	protected int dstPort;
	protected String srcAddr;
	protected String dstAddr;
	/*
	 * Parameters needed for incomming rules
	 */
	protected int dstFwdPort;
	protected String dstFwdAddr;

	/*
	 * Common constructor used for incomming or outgoing flows.
	 * If incomming is false dstFwdPort and dstFwdAddr must be -1 and will be ignored.
	 */
	public GrcBoxRule(int id, Protocol proto, boolean incomming, int appid,
			String ifName, long expireDate, int srcPort, int dstPort,
			String srcAddr,String dstAddr, int dstFwdPort, String dstFwdAddr) {
		super();
		this.id = id;
		this.proto = proto;
		this.incomming = incomming;
		this.appid = appid;
		this.ifName = ifName;
		this.expireDate = expireDate;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcAddr = srcAddr;
		this.dstAddr = dstAddr;
		this.dstFwdPort = dstFwdPort;
		this.dstFwdAddr = dstFwdAddr;
	}

	public GrcBoxRule(){
		
	}
	
	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public long getExpire() {
		return expireDate;
	}

	public void setExpire(long expire) {
		this.expireDate = expire;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public int getDstPort() {
		return dstPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	public String getSrcAddr() {
		return srcAddr;
	}

	public void setSrcAddr(String srcAddr) {
		this.srcAddr = srcAddr;
	}

	public String getDstAddr() {
		return dstAddr;
	}

	public void setDstAddr(String dstAddr) {
		this.dstAddr = dstAddr;
	}

	public int getDstFwdPort() {
		return dstFwdPort;
	}

	public void setDstFwdPort(int dstFwdPort) {
		this.dstFwdPort = dstFwdPort;
	}

	public String getDstFwdAddr() {
		return dstFwdAddr;
	}

	public void setDstFwdAddr(String dstFwdAddr) {
		this.dstFwdAddr = dstFwdAddr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Protocol getProto() {
		return proto;
	}

	public void setProto(Protocol proto) {
		this.proto = proto;
	}

	public boolean isIncomming() {
		return incomming;
	}

	public void setIncomming(boolean incomming) {
		this.incomming = incomming;
	}
}
