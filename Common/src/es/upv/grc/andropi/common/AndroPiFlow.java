package es.upv.grc.andropi.common;

public class AndroPiFlow {
	public enum Protocol{
		TCP, UDP
	}
	private static int maxId = 0;
	private int id;
	private int srcPort;
	private int dstPort;
	private String srcInetAddr;
	private String dstInetAddr;
	
	public AndroPiFlow(int srcPort, int dstPort, String srcInetAddr,
			String dstInetAddt) {
		super();
		this.id = maxId++;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcInetAddr = srcInetAddr;
		this.dstInetAddr = dstInetAddt;
	}
	
	public AndroPiFlow(){
		
	}
	
	public int getId() {
		return id;
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

	public String getSrcInetAddr() {
		return srcInetAddr;
	}

	public void setSrcInetAddr(String srcInetAddr) {
		this.srcInetAddr = srcInetAddr;
	}

	public String getDstInetAddr() {
		return dstInetAddr;
	}

	public void setDstInetAddr(String dstInetAddr) {
		this.dstInetAddr = dstInetAddr;
	}
	
	
}
