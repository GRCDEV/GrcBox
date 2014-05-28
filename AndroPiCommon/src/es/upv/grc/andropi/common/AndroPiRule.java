package es.upv.grc.andropi.common;

public class AndroPiRule {
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
	private int id; //Unique Id of this rule
	Protocol proto;	//Protocol
	private boolean incomming;
	private int appid;	//AppId of the owner app
	private int ifIndex; // Outgoing or incomming interface
	private long expireDate;
	private int srcPort;
	private int dstPort;
	private int srcAddr;
	private int dstAddr;
	/*
	 * Parameters needed for incomming rules
	 */
	private int dstFwdPort;
	private int dstFwdAddr;

	/*
	 * Common constructor used for incomming or outgoing flows.
	 * If incomming is false dstFwdPort and dstFwdAddr must be -1 and will be ignored.
	 */
	public AndroPiRule(int id, Protocol proto, boolean incomming, int appid,
			int ifIndex, long expireDate, int srcPort, int dstPort,
			int srcAddr, int dstAddr, int dstFwdPort, int dstFwdAddr) {
		super();
		this.id = id;
		this.proto = proto;
		this.incomming = incomming;
		this.appid = appid;
		this.ifIndex = ifIndex;
		this.expireDate = expireDate;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcAddr = srcAddr;
		this.dstAddr = dstAddr;
		this.dstFwdPort = dstFwdPort;
		this.dstFwdAddr = dstFwdAddr;
	}

	public AndroPiRule(){
		
	}
	
	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
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

	public int getSrcAddr() {
		return srcAddr;
	}

	public void setSrcAddr(int srcAddr) {
		this.srcAddr = srcAddr;
	}

	public int getDstAddr() {
		return dstAddr;
	}

	public void setDstAddr(int dstAddr) {
		this.dstAddr = dstAddr;
	}

	public int getDstFwdPort() {
		return dstFwdPort;
	}

	public void setDstFwdPort(int dstFwdPort) {
		this.dstFwdPort = dstFwdPort;
	}

	public int getDstFwdAddr() {
		return dstFwdAddr;
	}

	public void setDstFwdAddr(int dstFwdAddr) {
		this.dstFwdAddr = dstFwdAddr;
	}

	public int getId() {
		return id;
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
