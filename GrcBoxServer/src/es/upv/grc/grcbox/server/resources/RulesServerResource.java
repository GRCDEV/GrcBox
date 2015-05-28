/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import java.io.IOException;
import java.util.ArrayList;

import org.restlet.Request;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.common.GrcBoxRuleList;
import es.upv.grc.grcbox.common.resources.RulesResource;
import es.upv.grc.grcbox.server.rulesdb.GrcBoxApp;
import es.upv.grc.grcbox.server.rulesdb.RulesDB;

/**
 * @author sertinell
 *
 */
public class RulesServerResource extends ServerResource implements RulesResource {

	private int appId;
	private String clientIp;
	
	@Override
	public GrcBoxRuleList getList() {
		GrcBoxRuleList list = new GrcBoxRuleList();
		list.setList(RulesDB.getRulesByApp(appId));
		return list;
	}



	@Override
	public GrcBoxRuleList newRule(GrcBoxRule rule) {
		/*
		 * Workaround to read the arguments from the request representation
		 * I do not know why rule is null.
		 */
		Request req = this.getRequest();
		String jsonContent = req.getEntityAsText();
		ObjectMapper mapper = new ObjectMapper();
		GrcBoxRule rule2 = null;
		try {
			rule2 = mapper.readValue(jsonContent, GrcBoxRule.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rule2.getType() == RuleType.INCOMING){
			rule2.setDstFwdAddr(clientIp);
		}
		else {
			rule2.setSrcAddr(clientIp);
		}
		GrcBoxRuleList list = new GrcBoxRuleList();
		list.setList(new ArrayList<GrcBoxRule>(RulesDB.addRule(appId, rule2)));
		return  list;
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		Request req = getRequest();
		clientIp = req.getClientInfo().getAddress();
		GrcBoxApp app = RulesDB.getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}
}
