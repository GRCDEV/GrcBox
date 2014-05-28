/**
 * 
 */
package es.upv.grc.andropi.server;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.andropi.common.AndroPiRule;
import es.upv.grc.andropi.common.RuleResource;

/**
 * @author sertinell
 *
 */
public class RuleServerResource extends ServerResource implements RuleResource {

	private int appId;
	private int ruleId;
	
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
	public boolean modify(AndroPiRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#remove(es.upv.grc.andropi.common.AndroPiRule, int)
	 */
	@Override
	public void remove() {
		//TODO
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		ruleId = Integer.parseInt(getAttribute("ruleId"));
		AndroPiRule rule = AndroPiServerApplication.getRulesDB().getRuleFromId(ruleId);
		if(rule == null){
			throw new ResourceException(404);
		}
	}
}
