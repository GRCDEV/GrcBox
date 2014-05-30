/**
 * 
 */
package es.upv.grc.andropi.server;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.andropi.common.AndroPiRule;
import es.upv.grc.andropi.common.RuleResource;
import es.upv.grc.andropi.server.db.DatabaseManager;

/**
 * @author sertinell
 *
 */
public class RuleServerResource extends ServerResource implements RuleResource {

	private int appId;
	private int ruleId;
	private DatabaseManager db;
	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#retrieve()
	 */
	@Override
	public AndroPiRule retrieve() {
		AndroPiRule rule = db.getRuleById(ruleId);
		return rule;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.RuleResource#remove(es.upv.grc.andropi.common.AndroPiRule, int)
	 */
	@Override
	public void remove() {
		db.rmRuleId(ruleId);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		ruleId = Integer.parseInt(getAttribute("ruleId"));
		db = AndroPiServerApplication.getRulesDB();
		AndroPiRule rule = AndroPiServerApplication.getRulesDB().getRuleFromId(ruleId);
		if(rule == null){
			throw new ResourceException(404);
		}
	}
}
