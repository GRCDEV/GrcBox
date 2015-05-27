package es.upv.grc.grcbox.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class GrcBoxRule.
 * This class represents a GRCBox rule
 * GRCBox rules directly maps to Iptables rules
 * Currently only TCP and UDP protocols are supported.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxRule {

	/**
	 * The Enum Protocol.
	 */
	public enum Protocol{

		/** The tcp. */
		TCP, 
		/** The udp. */
		UDP;
	}

	/**
	 * The Enum RuleType.
	 */
	public enum RuleType{

		/** The incoming. */
		INCOMING, 
		/** The outgoing. */
		OUTGOING, 
		/** The multicast. */
		MULTICAST;
	}

	/**
	 * ATTRIBUTES of the rule
	 */
	/** The unique id. */
	protected int id; 

	/** The protocol UDP or TCP. */
	protected Protocol proto;

	/** The type INCOMING, OUTGOING or MULTICAST. */
	protected RuleType type;

	/** The appid of the owner app */
	protected int appid;	

	/** The interface name which this rule is associated to. */
	protected String ifName; 

	/** The expire date. */
	protected long expireDate;

	/** The source port. */
	protected int srcPort;

	/** The destination port. */
	protected int dstPort;

	/** The source addr. */
	protected String srcAddr;

	/** The destination addr. */
	protected String dstAddr;

	/** The multicast plugin. 
	 * 	The list of implemented plugins should be checked using the RootResource 
	 * */
	protected String mcastPlugin;
	
	
	/**
	 * TARGETS of the rule
	 */
	/** The destination forwarding port for incoming rules. */
	protected int dstFwdPort;

	/** The destination forwarding address for incoming rules. */
	protected String dstFwdAddr;

	/**
	 * Instantiates a new GRCbox rule.
	 */
	public GrcBoxRule(){

	}

	/**
	 * Instantiates a new grc box rule.
	 *
	 * @param id the id, this value is ignored when registering a rules on the
	 * server
	 * @param proto the proto
	 * @param type the type
	 * @param appid the appid
	 * @param ifName the interface name
	 * @param expireDate the expire date
	 * @param srcPort the source port, if -1 any port is used
	 * @param dstPort the destination port, if -1 any port is used
	 * @param srcAddr the source address, if null any address is used 
	 * @param dstAddr the destination address
	 * @param dstFwdPort the destination forwarding port, is ignored for outgoing
	 * rules, must be defined for incoming rules
	 * @param dstFwdAddr the destination forwarding address, is ignored for outgoing
	 * rules, must be defined for incoming rules
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

	/**
	 * Instantiates a new grc box rule.
	 *
	 * @param id the id, this value is ignored when registering a rules on the
	 * server
	 * @param proto the proto
	 * @param type the type
	 * @param appid the appid
	 * @param ifName the interface name
	 * @param expireDate the expire date
	 * @param srcPort the source port, if -1 any port is used
	 * @param dstPort the destination port, if -1 any port is used
	 * @param srcAddr the source address, if null any address is used 
	 * @param dstAddr the destination address
	 * @param dstFwdPort the destination forwarding port, is ignored for outgoing
	 * rules, must be defined for incoming rules
	 * @param dstFwdAddr the destination forwarding address, is ignored for outgoing
	 * rules, must be defined for incoming rules
	 * @param mcastPlugin the multicast plugin. A list of valid multicast plugins
	 * must be obtained from the server 
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the proto.
	 *
	 * @return the proto
	 */
	public Protocol getProto() {
		return proto;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public RuleType getType() {
		return type;
	}


	/**
	 * Gets the appid.
	 *
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}

	/**
	 * Sets the appid.
	 *
	 * @param appid the appid to set
	 */
	public void setAppid(int appid) {
		this.appid = appid;
	}

	/**
	 * Gets the if name.
	 *
	 * @return the ifName
	 */
	public String getIfName() {
		return ifName;
	}

	/**
	 * Gets the expire date.
	 *
	 * @return the expireDate
	 */
	public long getExpireDate() {
		return expireDate;
	}

	/**
	 * Gets the src port.
	 *
	 * @return the srcPort
	 */
	public int getSrcPort() {
		return srcPort;
	}

	/**
	 * Gets the dst port.
	 *
	 * @return the dstPort
	 */
	public int getDstPort() {
		return dstPort;
	}

	/**
	 * Gets the src addr.
	 *
	 * @return the srcAddr
	 */
	public String getSrcAddr() {
		return srcAddr;
	}

	/**
	 * Sets the src addr.
	 *
	 * @param srcAddr the srcAddr to set
	 */
	public void setSrcAddr(String srcAddr) {
		this.srcAddr = srcAddr;
	}

	/**
	 * Gets the dst addr.
	 *
	 * @return the dstAddr
	 */
	public String getDstAddr() {
		return dstAddr;
	}

	/**
	 * Gets the mcast plugin.
	 *
	 * @return the mcastPlugin
	 */
	public String getMcastPlugin() {
		return mcastPlugin;
	}

	/**
	 * Gets the dst fwd port.
	 *
	 * @return the dstFwdPort
	 */
	public int getDstFwdPort() {
		return dstFwdPort;
	}

	/**
	 * Gets the dst fwd addr.
	 *
	 * @return the dstFwdAddr
	 */
	public String getDstFwdAddr() {
		return dstFwdAddr;
	}

	/**
	 * Sets the dst fwd addr.
	 *
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

	/* 
	 * The ID is not included
	 * Only attributes are used
	 * Target are ignored
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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


	/**
	 * Iptables size.
	 * @return the number of defined fields
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

	/**
	 * Includes.
	 *
	 * @param other the other
	 * @return true, if if this rule includes the given rule
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

	/**
	 * Conflicts.
	 *
	 * @param other the other
	 * @return true, if this rule conflicts with the given rule
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
