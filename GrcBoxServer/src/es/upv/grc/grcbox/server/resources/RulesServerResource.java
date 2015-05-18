/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import java.io.IOException;
import java.util.ArrayList;

import org.restlet.Request;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.common.GrcBoxRuleList;
import es.upv.grc.grcbox.common.resources.RulesResource;
import es.upv.grc.grcbox.server.RulesDB;

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
		Request request = this.getRequest();
		String jsonreq = request.getEntityAsText();
		ObjectMapper mapper = new ObjectMapper();
		GrcBoxRule rule2;
		try {
			rule2 = mapper.readValue(jsonreq, GrcBoxRule.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(rule.getType() == RuleType.INCOMING){
			rule.setDstFwdAddr(clientIp);
		}
		else {
			rule.setSrcAddr(clientIp);
		}
		GrcBoxRuleList list = new GrcBoxRuleList();
		list.setList(new ArrayList<GrcBoxRule>(RulesDB.addRule(appId, rule)));
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
