/**
 * 
 */
package es.upv.grc.andropi.server;

import java.util.List;

import es.upv.grc.andropi.common.AndroPiRule;
import es.upv.grc.andropi.common.RulesResource;

/**
 * @author sertinell
 *
 */
public class RulesServerResource implements RulesResource {

	@Override
	public List<AndroPiRule> getList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AndroPiRule newInRule(int ifIndex, int srcPort, int dstPort,
			int srcAddr, int dstAddr, int dstFwdPort, int dstFwdAddr, int secret) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AndroPiRule newOutRule(int ifIndex, int srcPort, int dstPort,
			int srcAddr, int dstAddr, int secret) {
		// TODO Auto-generated method stub
		return null;
	}
}
