/**
 * 
 */
package es.upv.grc.andropi.server;

import es.upv.grc.andropi.common.AndroPiAppInfo;
import es.upv.grc.andropi.common.AppResource;

/**
 * @author sertinell
 *
 */
public class AppServerResource implements AppResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#retrieve()
	 */
	@Override
	public AndroPiAppInfo retrieve() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#modify(int, int, java.lang.String)
	 */
	@Override
	public boolean modify(int appId, int secret, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.AppResource#remove(int, int)
	 */
	@Override
	public boolean remove(int appId, int secret) {
		// TODO Auto-generated method stub
		return false;
	}

}
