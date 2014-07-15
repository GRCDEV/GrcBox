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
		super(id, proto, true, appid, ifName, expireDate, srcPort, dstPort, srcAddr, dstAddr, dstFwdPort, null);
	}
	
	public GrcBoxRuleIn(GrcBoxRule rule){
		super(rule.id, rule.proto, true, rule.appid, rule.ifName, rule.expireDate, rule.srcPort, rule.dstPort, rule.srcAddr, rule.dstAddr, rule.dstFwdPort, rule.dstFwdAddr);
	}
}
