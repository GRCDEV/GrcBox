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
			String ifName, long expireDate, int srcPort, int dstPort,String dstAddr) {
		super(id, proto, false, appid, ifName, expireDate, srcPort, dstPort, null, dstAddr, -1, null);
	}
	
	public GrcBoxRuleOut(GrcBoxRule rule){
		super(rule.id, rule.proto, false, rule.appid, rule.ifName, rule.expireDate, rule.srcPort, rule.dstPort, rule.srcAddr, rule.dstAddr, rule.dstFwdPort, rule.dstFwdAddr);
	}	
}
