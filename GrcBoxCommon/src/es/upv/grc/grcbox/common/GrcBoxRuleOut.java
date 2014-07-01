package es.upv.grc.grcbox.common;

import org.restlet.resource.ResourceException;

import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;

public class GrcBoxRuleOut extends GrcBoxRule {
	public GrcBoxRuleOut() {
		super();
		this.incomming = true;
	}
	
	/*
	 * Common constructor used for incomming or outgoing flows.
	 * If incomming is false dstFwdPort and dstFwdAddr must be -1 and will be ignored.
	 */
	public GrcBoxRuleOut(int id, Protocol proto, int appid,
			String ifName, long expireDate, int srcPort, int dstPort,
			String srcAddr,String dstAddr) {
		super(id, proto, false, appid, ifName, expireDate, srcPort, dstPort, srcAddr, dstAddr, -1);
	}
	
	public GrcBoxRuleOut(GrcBoxRule rule){
		super(rule.id, rule.proto, false, rule.appid, rule.ifName, rule.expireDate, rule.srcPort, rule.dstPort, rule.srcAddr, rule.dstAddr, rule.dstFwdPort);
	}
	
	/*
	 * Return an iptables rule to mark packets matching this rule with mark
	 */
	public String createIptablesRule(int mark){
		String ruleStr = "";
		ruleStr += "iptables -t mangle -A PREROUTING -i " + ifName + " -p " + proto.toString().toLowerCase();

		if(dstPort != -1 )
			ruleStr += " --dport " + dstPort;

		if(dstAddr != null)
			ruleStr +=  " -d "+ dstAddr;

		if(srcPort != -1 )
			ruleStr += " --sport " + srcPort;

		if(srcAddr != null)
			ruleStr += " -s " + srcAddr;

		ruleStr += " -j MARK --set-mark "+ mark;
		return ruleStr;
	}
	
	/*
	 * Return an iptables rule to mark packets matching this rule with mark
	 */
	public String deleteIptablesRule(){
		String ruleStr = "";
		ruleStr += "iptables -t mangle -D PREROUTING -i " + ifName + " -p " + proto.toString().toLowerCase();

		if(dstPort != -1 )
			ruleStr += " --dport " + dstPort;

		if(dstAddr != null)
			ruleStr +=  " -d "+ dstAddr;

		if(srcPort != -1 )
			ruleStr += " --sport " + srcPort;

		if(srcAddr != null)
			ruleStr += " -s " + srcAddr;

		return ruleStr;
	}
	
}
