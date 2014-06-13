/**
 * 
 */
package es.upv.grc.grcbox.server;

import java.util.List;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.RulesResource;

/**
 * @author sertinell
 *
 */
public class RulesServerResource extends ServerResource implements RulesResource {

	private int appId;
	private RulesDB db;
	
	@Override
	public List<GrcBoxRule> getList() {
		return db.getRulesByApp(appId);
	}



	@Override
	public GrcBoxRule newRule(GrcBoxRule rule) {
		return GrcBoxServerApplication.getDb().addRule(appId, rule);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		db = GrcBoxServerApplication.getDb();

		GrcBoxApp app = GrcBoxServerApplication.getDb().getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}
}
