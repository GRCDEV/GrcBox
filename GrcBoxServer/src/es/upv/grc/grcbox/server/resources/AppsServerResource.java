/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;

import es.upv.grc.grcbox.common.GrcBoxAppInfoList;
import es.upv.grc.grcbox.common.resources.AppsResource;
import es.upv.grc.grcbox.server.GrcBoxServerApplication;
import es.upv.grc.grcbox.server.RulesDB;

/**
 * @author sertinell
 *
 */
public class AppsServerResource extends ServerResource implements AppsResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppsResource#getList()
	 */
	@Override
	public GrcBoxAppInfoList getList() {
		GrcBoxAppInfoList appList =  new GrcBoxAppInfoList();
		appList.setList(RulesDB.getAppInfos());
		return appList;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppsResource#newApp(java.lang.String)
	 */
	@Override
	public IdSecret newApp(String name) {
		int secret = (name+System.currentTimeMillis()).hashCode();
		int id = RulesDB.addApp(name);
		MapVerifier verifier = GrcBoxServerApplication.getVerifier();
		verifier.getLocalSecrets().put(Integer.toString(id), Integer.toString(secret).toCharArray());
		System.out.println("Adding a new application to DB:"+Integer.toString(id)+" " +  Integer.toString(secret)
				+ " There are " + verifier.getLocalSecrets().size() +" pairs registered.");

		IdSecret idSecret = new IdSecret(id, secret,GrcBoxServerApplication.getConfig().getKeepAliveTime());
		return idSecret;
	}
}
