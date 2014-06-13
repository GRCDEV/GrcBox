/**
 * 
 */
package es.upv.grc.grcbox.server;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.AndroPiApp;
import es.upv.grc.grcbox.common.AndroPiAppInfo;
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
	public AndroPiAppInfo retrieve() {
		AndroPiAppInfo info = AndroPiServerApplication.getDb().getAppInfo(appId);
		return info;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#modify(int, int, java.lang.String)
	 */
	@Override
	public void keepAlive() {
		AndroPiServerApplication.getDb().keepAliveApp(appId);
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#remove(int, int)
	 */
	@Override
	public void rm() {
		AndroPiServerApplication.getDb().rmApp(appId);
		AndroPiServerApplication.getVerifier().getLocalSecrets().remove(appId);
	}

	@Override
	protected void doInit() throws ResourceException {
		appId = Integer.parseInt(getAttribute("appId"));
		AndroPiApp app= AndroPiServerApplication.getDb().getApp(appId);
		if(app == null){
			throw new ResourceException(404);
		}
	}

}
