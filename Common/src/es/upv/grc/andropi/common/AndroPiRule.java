package es.upv.grc.andropi.common;

import java.util.Date;

public class AndroPiRule {
	public enum Protocol{
		TCP, UDP
	}
	private static int maxId = 0;
	private int id;
	private int appid;
	private int ifIndex;
	private Date expire;
	private int srcPort;
	private int dstPort;
	private int srcAddr;
	private int dstAddr;
	private int dstFwdPort;
	private int dstFwdAddr;
	
	public AndroPiRule(int id, int appid, int ifIdex, Date expire, int srcPort,
			int dstPort, int srcAddr, int dstAddr, int dstFwdPort,
			int dstFwdAddr) {
		super();
		this.id = id;
		this.ifIndex = ifIdex;
		this.appid = appid;
		this.expire = expire;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcAddr = srcAddr;
		this.dstAddr = dstAddr;
		this.dstFwdPort = dstFwdPort;
		this.dstFwdAddr = dstFwdAddr;
	}
	
	public AndroPiRule(int appid, int ifIdex, Date expire, int srcPort,
			int dstPort, int srcAddr, int dstAddr, int dstFwdPort,
			int dstFwdAddr) {
		super();
		this.id = maxId++;
		this.appid = appid;
		this.ifIndex = ifIdex;
		this.expire = expire;
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

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
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
}
