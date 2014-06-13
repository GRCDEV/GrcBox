/**
 * 
 */
package es.upv.grc.grcbox.server;

import java.util.List;

import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.AppsResource;

/**
 * @author sertinell
 *
 */
public class AppsServerResource extends ServerResource implements AppsResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppsResource#getList()
	 */
	@Override
	public List<GrcBoxApp> getList() {
		return GrcBoxServerApplication.getDb().getApps();
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppsResource#newApp(java.lang.String)
	 */
	@Override
	public IdSecret newApp(String name) {
		int secret = (name+System.currentTimeMillis()).hashCode();
		int id = GrcBoxServerApplication.getDb().addApp(name);
		MapVerifier verifier = GrcBoxServerApplication.getVerifier();
		verifier.getLocalSecrets().put(Integer.toString(id), Integer.toString(secret).toCharArray());
		System.out.println("Adding a new application to DB:"+Integer.toString(id)+" " +  Integer.toString(secret)
				+ "There are " + verifier.getLocalSecrets().size() +" pairs registered.");

		IdSecret idSecret = new IdSecret(id, secret,GrcBoxServerApplication.getConfig().getDatabase().getUpdateTime());
		return idSecret;
	}
}
