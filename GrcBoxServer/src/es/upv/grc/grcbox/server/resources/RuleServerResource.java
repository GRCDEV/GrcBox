/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.resources.RuleResource;
import es.upv.grc.grcbox.server.rulesdb.RulesDB;

/**
 * The Class RuleServerResource.
 *
 * @author sertinell
 */
public class RuleServerResource extends ServerResource implements RuleResource {

	/** The app id. */
	private int appId;
	
	/** The rule id. */
	private int ruleId;
	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#retrieve()
	 */
	@Override
	public GrcBoxRule retrieve() {
		GrcBoxRule rule = RulesDB.getRule(appId, ruleId);
		return rule;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#remove(es.upv.grc.andropi.common.AndroPiRule, int)
	 */
	@Override
	public void remove() {
		RulesDB.rmRule(appId, ruleId);
	}

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		ruleId = Integer.parseInt(getAttribute("ruleId"));
		RulesDB.keepAliveApp(appId);
		GrcBoxRule rule = RulesDB.getRule(appId, ruleId);
		if(rule == null){
			throw new ResourceException(404);
		}
	}
}
