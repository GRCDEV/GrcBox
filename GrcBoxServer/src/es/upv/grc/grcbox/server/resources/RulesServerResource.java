/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import org.restlet.Request;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleList;
import es.upv.grc.grcbox.common.resources.RulesResource;
import es.upv.grc.grcbox.server.GrcBoxServerApplication;
import es.upv.grc.grcbox.server.RulesDB;

/**
 * @author sertinell
 *
 */
public class RulesServerResource extends ServerResource implements RulesResource {

	private int appId;
	private RulesDB db;
	private String clientIp;
	
	@Override
	public GrcBoxRuleList getList() {
		GrcBoxRuleList list = new GrcBoxRuleList();
		list.setList(db.getRulesByApp(appId));
		return list;
	}



	@Override
	public GrcBoxRule newRule(GrcBoxRule rule) {
		if(rule.isIncomming()){
			rule.setDstFwdAddr(clientIp);
		}
		else {
			rule.setSrcAddr(clientIp);
		}
		return GrcBoxServerApplication.getDb().addRule(appId, rule);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		db = GrcBoxServerApplication.getDb();
		Request req = getRequest();
		clientIp = req.getClientInfo().getAddress();
		GrcBoxApp app = GrcBoxServerApplication.getDb().getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}
}
