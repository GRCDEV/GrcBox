package es.upv.grc.grcbox.common;

import org.restlet.resource.ResourceException;

import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;

public class GrcBoxRuleIn extends GrcBoxRule {
	public GrcBoxRuleIn() {
		super();
		this.incomming = true;
	}
	
	/*
	 * Common constructor used for incomming or outgoing flows.
	 * If incomming is false dstFwdPort and dstFwdAddr must be -1 and will be ignored.
	 */
	public GrcBoxRuleIn(int id, Protocol proto, int appid,
			String ifName, long expireDate, int srcPort, int dstPort,
			String srcAddr,String dstAddr, int dstFwdPort) {
		super(id, proto, true, appid, ifName, expireDate, srcPort, dstPort, srcAddr, dstAddr, dstFwdPort);
	}
	
	public GrcBoxRuleIn(GrcBoxRule rule){
		super(rule.id, rule.proto, true, rule.appid, rule.ifName, rule.expireDate, rule.srcPort, rule.dstPort, rule.srcAddr, rule.dstAddr, rule.dstFwdPort);
	}
	
	/*
	 * return an iptables rule to perform DNAT to packets matching this rule
	 */
	public String createIptablesRule(){
		String ruleStr = "iptables -t nat -A PREROUTING -i " + ifName + " -p " +proto.toString().toLowerCase();
		if(dstPort == -1){
			throw new ResourceException(412);
		}
		ruleStr += " --dport " + dstPort;
		
		if(srcPort != -1)
			ruleStr += " --sport "+ srcPort;
		
		if(srcAddr != null)
			ruleStr += " --s " + srcAddr;
		
		if(dstFwdPort == -1 || dstFwdAddr == null){
			throw new ResourceException(412);
		}
		ruleStr += " -j DNAT --to-destination " +dstFwdAddr + ":" + dstFwdPort;
		
		return ruleStr;
	}
	
	/*
	 * return an iptables rule to perform DNAT to packets matching this rule
	 */
	public String deleteIptablesRule(){
		String ruleStr = "iptables -t nat -D PREROUTING -i " + ifName + " -p " +proto.toString().toLowerCase();
		if(dstPort == -1){
			throw new ResourceException(412);
		}
		ruleStr += " --dport " + dstPort;

		if(srcPort != -1)
			ruleStr += " --sport "+ srcPort;

		if(srcAddr != null)
			ruleStr += " --s " + srcAddr;

		if(dstFwdPort == -1 || dstFwdAddr == null){
			throw new ResourceException(412);
		}
		return ruleStr;
	}
}
