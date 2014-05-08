/**
 * 
 */
package es.upv.grc.andropi.server;

import es.upv.grc.andropi.common.AndroPiRule;
import es.upv.grc.andropi.common.RuleResource;

/**
 * @author sertinell
 *
 */
public class RuleServerResource implements RuleResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#retrieve()
	 */
	@Override
	public AndroPiRule retrieve() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#modify(es.upv.grc.andropi.common.AndroPiRule, int)
	 */
	@Override
	public boolean modify(AndroPiRule rule, int secret) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#remove(es.upv.grc.andropi.common.AndroPiRule, int)
	 */
	@Override
	public boolean remove(int secret) {
		// TODO Auto-generated method stub
		return false;
	}

}
