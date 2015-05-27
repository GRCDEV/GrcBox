package es.upv.grc.grcbox.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
 * This class represents a GRCBox rule
 * GRCBox rules directly maps to Iptables rules
 * Currently only TCP and UDP protocols are supported.
 */
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
	protected String ifName; // Outgoing or incoming interface
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

	public GrcBoxRule(){
		
	}
	
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
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the proto
	 */
	public Protocol getProto() {
		return proto;
	}

	/**
	 * @return the type
	 */
	public RuleType getType() {
		return type;
	}


	/**
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}

	/**
	 * @param appid the appid to set
	 */
	public void setAppid(int appid) {
		this.appid = appid;
	}

	/**
	 * @return the ifName
	 */
	public String getIfName() {
		return ifName;
	}

	/**
	 * @return the expireDate
	 */
	public long getExpireDate() {
		return expireDate;
	}

	/**
	 * @return the srcPort
	 */
	public int getSrcPort() {
		return srcPort;
	}

	/**
	 * @return the dstPort
	 */
	public int getDstPort() {
		return dstPort;
	}

	/**
	 * @return the srcAddr
	 */
	public String getSrcAddr() {
		return srcAddr;
	}

	/**
	 * @param srcAddr the srcAddr to set
	 */
	public void setSrcAddr(String srcAddr) {
		this.srcAddr = srcAddr;
	}

	/**
	 * @return the dstAddr
	 */
	public String getDstAddr() {
		return dstAddr;
	}

	/**
	 * @return the mcastPlugin
	 */
	public String getMcastPlugin() {
		return mcastPlugin;
	}

	/**
	 * @return the dstFwdPort
	 */
	public int getDstFwdPort() {
		return dstFwdPort;
	}

	/**
	 * @return the dstFwdAddr
	 */
	public String getDstFwdAddr() {
		return dstFwdAddr;
	}

	/**
	 * @param dstFwdAddr the dstFwdAddr to set
	 */
	public void setDstFwdAddr(String dstFwdAddr) {
		this.dstFwdAddr = dstFwdAddr;
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
	
	/*
	 * return the number of defined fields
	 */
	@JsonIgnore
	public int ipTablesSize(){
		int ipTablesLength = 0;
		if(srcPort != -1){
			ipTablesLength++;
		}
		if(dstPort != -1){
			ipTablesLength++;
		}
		if(srcAddr != null){
			ipTablesLength++;
		}
		if(dstAddr != null){
			ipTablesLength++;
		}
		return ipTablesLength;
	}
	
	/*
	 * return true if this rule includes the given rule
	 */
	@JsonIgnore
	public boolean includes(GrcBoxRule other){
		if(type != other.type){
			return false;
		}
		if(proto != other.proto){
			return false;
		}
		if(srcPort != -1){
			if(srcPort != other.srcPort){
				return false;
			}
		}
		if(dstPort != -1){
			if(dstPort != other.dstPort){
				return false;
			}
		}
		if(srcAddr != null){
			if(!srcAddr.equals(other.srcAddr)){
				return false;
			}
		}
		if(dstAddr != null){
			if(!dstAddr.equals(other.dstAddr)){
				return false;
			}
		}
		return true;
	}
	
	/*
	 * return true if this rule includes the given rule
	 */
	@JsonIgnore
	public boolean conflicts(GrcBoxRule other){
		if(type != other.type){
			return false;
		}
		if(proto != other.proto){
			return false;
		}
		if(srcPort != other.srcPort){
			return false;
		}
		if(dstPort != other.dstPort){
			return false;
		}
		if(srcAddr != null){
			if(other.srcAddr != null){
				if(!srcAddr.equals(other.srcAddr)){
					return false;
				}
			}
		}
		if(dstAddr != null){
			if(other.dstAddr != null){
				if(!dstAddr.equals(other.dstAddr)){
					return false;
				}
			}
		}
		
		/*
		 * If the rules are the same, check their targets
		 */
		if(type.equals(GrcBoxRule.RuleType.INCOMING)){
			if(dstFwdAddr != null){
				if(dstFwdAddr.equals(other.dstFwdAddr)){
					if(dstFwdPort == other.dstFwdPort){
						return false;
					}
				}
			}
		}
		else if(type.equals(GrcBoxRule.RuleType.OUTGOING)){
			if(ifName != null){
				if( ifName.equals(other.ifName) ){
					return false;
				}
			}
		}
		else if(type.equals(GrcBoxRule.RuleType.MULTICAST) ){
			
		}
		return true;
	}
}
