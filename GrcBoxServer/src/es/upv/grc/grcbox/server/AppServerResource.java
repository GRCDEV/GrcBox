/**
 * 
 */
package es.upv.grc.grcbox.server;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.AppResource;

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
		GrcBoxAppInfo info = GrcBoxServerApplication.getDb().getAppInfo(appId);
		return info;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#modify(int, int, java.lang.String)
	 */
	@Override
	public void keepAlive() {
		GrcBoxServerApplication.getDb().keepAliveApp(appId);
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#remove(int, int)
	 */
	@Override
	public void rm() {
		GrcBoxServerApplication.getDb().rmApp(appId);
		GrcBoxServerApplication.getVerifier().getLocalSecrets().remove(appId);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		GrcBoxApp app= GrcBoxServerApplication.getDb().getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}

}
