/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.resources.RuleResource;
import es.upv.grc.grcbox.server.GrcBoxServerApplication;
import es.upv.grc.grcbox.server.RulesDB;

/**
 * @author sertinell
 *
 */
public class RuleServerResource extends ServerResource implements RuleResource {

	private int appId;
	private int ruleId;
	private RulesDB db;
	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#retrieve()
	 */
	@Override
	public GrcBoxRule retrieve() {
		GrcBoxRule rule = db.getRule(appId, ruleId);
		return rule;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#remove(es.upv.grc.andropi.common.AndroPiRule, int)
	 */
	@Override
	public void remove() {
		RulesDB.rmRule(appId, ruleId);
	}

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
