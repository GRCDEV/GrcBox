package es.upv.grc.grcbox.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxRule {
	public enum Protocol{
		TCP, UDP;
	}
	
	public enum RuleType{
		INCOMING, OUTGOING, MULTICAST;
	}
	
	/*
	 * Common parameters
	 */
	protected int id; //Unique Id of this rule
	protected Protocol proto;	//Protocol
	protected RuleType type;
	protected int appid;	//AppId of the owner app
	protected String ifName; // Outgoing or incomming interface
	protected long expireDate;
	protected int srcPort;
	protected int dstPort;
	protected String srcAddr;
	protected String dstAddr;
	protected String mcastPlugin;
	/*
	 * Parameters needed for incomming rules
	 */
	protected int dstFwdPort;
	protected String dstFwdAddr;

	/*
	 * Common constructor used for incomming or outgoing flows.
	 * If incomming is false dstFwdPort and dstFwdAddr must be -1 and will be ignored.
	 */
	public GrcBoxRule(int id, Protocol proto, RuleType type, int appid,
			String ifName, long expireDate, int srcPort, int dstPort,
			String srcAddr,String dstAddr, int dstFwdPort, String dstFwdAddr) {
		super();
		this.id = id;
		this.proto = proto;
		this.type = type;
		this.appid = appid;
		this.ifName = ifName;
		this.expireDate = expireDate;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcAddr = srcAddr;
		this.dstAddr = dstAddr;
		this.dstFwdPort = dstFwdPort;
		this.dstFwdAddr = dstFwdAddr;
		this.mcastPlugin = null;
	}

	/*
	 * Common constructor used for incomming or outgoing flows.
	 * If incomming is false dstFwdPort and dstFwdAddr must be -1 and will be ignored.
	 */
	public GrcBoxRule(int id, Protocol proto, RuleType type, int appid,
			String ifName, long expireDate, int srcPort, int dstPort,
			String srcAddr,String dstAddr, int dstFwdPort, String dstFwdAddr, String mcastPlugin) {
		super();
		this.id = id;
		this.proto = proto;
		this.type = type;
		this.appid = appid;
		this.ifName = ifName;
		this.expireDate = expireDate;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcAddr = srcAddr;
		this.dstAddr = dstAddr;
		this.dstFwdPort = dstFwdPort;
		this.dstFwdAddr = dstFwdAddr;
		this.mcastPlugin = mcastPlugin;
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

	public RuleType getType() {
		return type;
	}

	public void setType(RuleType type) {
		this.type = type;
	}
	
	@Override
	public String toString(){
		String value = null;
		if(type.equals(RuleType.INCOMING)){
			value = "Id: "+ getId() + " Type:" +getType() + " Protocol:" + getProto()  +
				" Port: " + getDstPort();
		}
		else if(type.equals(RuleType.OUTGOING)){
			value = " Id: "+ getId() + " Type:" +getType() + " Protocol:" + getProto() +
					" DstAddr:"+ getDstAddr() + " Port: " + getDstPort();
		}
		else if(type.equals(RuleType.MULTICAST)){
			value = " Id: "+ getId() + " Type:" +getType() + "Address " + getDstAddr() +
					" Port: " + getDstPort();
		}
		return value;
	}
}
