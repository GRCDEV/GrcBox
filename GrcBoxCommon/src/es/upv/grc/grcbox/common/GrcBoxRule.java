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
	
	public String getMcastPlugin() {
		return mcastPlugin;
	}

	public void setMcastPlugin(String mcastPlugin) {
		this.mcastPlugin = mcastPlugin;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + appid;
		result = prime * result + ((dstAddr == null) ? 0 : dstAddr.hashCode());
		result = prime * result
				+ ((dstFwdAddr == null) ? 0 : dstFwdAddr.hashCode());
		result = prime * result + dstFwdPort;
		result = prime * result + dstPort;
		result = prime * result + ((ifName == null) ? 0 : ifName.hashCode());
		result = prime * result
				+ ((mcastPlugin == null) ? 0 : mcastPlugin.hashCode());
		result = prime * result + ((proto == null) ? 0 : proto.hashCode());
		result = prime * result + ((srcAddr == null) ? 0 : srcAddr.hashCode());
		result = prime * result + srcPort;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrcBoxRule other = (GrcBoxRule) obj;
		if (appid != other.appid)
			return false;
		if (dstAddr == null) {
			if (other.dstAddr != null)
				return false;
		} else if (!dstAddr.equals(other.dstAddr))
			return false;
		if (dstFwdAddr == null) {
			if (other.dstFwdAddr != null)
				return false;
		} else if (!dstFwdAddr.equals(other.dstFwdAddr))
			return false;
		if (dstFwdPort != other.dstFwdPort)
			return false;
		if (dstPort != other.dstPort)
			return false;
		if (ifName == null) {
			if (other.ifName != null)
				return false;
		} else if (!ifName.equals(other.ifName))
			return false;
		if (mcastPlugin == null) {
			if (other.mcastPlugin != null)
				return false;
		} else if (!mcastPlugin.equals(other.mcastPlugin))
			return false;
		if (proto != other.proto)
			return false;
		if (srcAddr == null) {
			if (other.srcAddr != null)
				return false;
		} else if (!srcAddr.equals(other.srcAddr))
			return false;
		if (srcPort != other.srcPort)
			return false;
		if (type != other.type)
			return false;
		return true;
	};
	
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
