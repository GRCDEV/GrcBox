/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.resources.AppResource;
import es.upv.grc.grcbox.server.rulesdb.GrcBoxApp;
import es.upv.grc.grcbox.server.rulesdb.RulesDB;

/**
 * @author sertinell
 *
 */
public class AppServerResource  extends ServerResource implements AppResource{

	int appId;
	
	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#retrieve()
	 */
	@Override
	public GrcBoxAppInfo retrieve() {
		GrcBoxAppInfo info = RulesDB.getAppInfo(appId);
		return info;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#modify(int, int, java.lang.String)
	 */
	@Override
	public void keepAlive() {
		RulesDB.keepAliveApp(appId);
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#remove(int, int)
	 */
	@Override
	public void rm() {
		RulesDB.rmApp(appId);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		GrcBoxApp app= RulesDB.getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}

}
