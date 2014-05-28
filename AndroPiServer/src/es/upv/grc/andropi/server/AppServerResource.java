/**
 * 
 */
package es.upv.grc.andropi.server;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;
import org.restlet.security.Verifier;

import es.upv.grc.andropi.common.AndroPiApp;
import es.upv.grc.andropi.common.AndroPiAppInfo;
import es.upv.grc.andropi.common.AppResource;

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
		AndroPiAppInfo info = AndroPiServerApplication.getRulesDB().getAppInfo(appId);
		return info;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#modify(int, int, java.lang.String)
	 */
	@Override
	public boolean modify(int appId, String name) {
		AndroPiServerApplication.getRulesDB().modifyApp(appId, name);
		return true;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#remove(int, int)
	 */
	@Override
	public void rm() {
		AndroPiServerApplication.getRulesDB().purgeApp(appId);
		AndroPiServerApplication.getVerifier().getLocalSecrets().remove(appId);
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
