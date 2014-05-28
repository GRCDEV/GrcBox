/**
 * 
 */
package es.upv.grc.andropi.server;

import java.util.List;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.andropi.common.AndroPiApp;
import es.upv.grc.andropi.common.AndroPiRule;
import es.upv.grc.andropi.common.RulesResource;

/**
 * @author sertinell
 *
 */
public class RulesServerResource extends ServerResource implements RulesResource {

	private int appId;
	
	
	@Override
	public List<AndroPiRule> getList() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public AndroPiRule newRule(AndroPiRule rule) {
		return AndroPiServerApplication.getRulesDB().addRule(rule);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		AndroPiApp app= AndroPiServerApplication.getRulesDB().getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}
}
