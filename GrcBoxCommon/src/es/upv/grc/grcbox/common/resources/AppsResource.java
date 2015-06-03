/**
 * Copyright 2005-2014 Restlet
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet
 */

package es.upv.grc.grcbox.common.resources;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import es.upv.grc.grcbox.common.GrcBoxAppInfoList;
/**
 * Annotated box resource interface.
 */
public interface AppsResource {
 
	/**
	 * The Class IdSecret.
	 */
	public class IdSecret{
		
		/** The app id. */
		int appId;
		
		/** The secret. */
		int secret;
		
		/** The update period. */
		long updatePeriod;
		
		/**
		 * Instantiates a new id secret.
		 */
		public IdSecret(){
			
		}
		
		/**
		 * Instantiates a new id secret.
		 *
		 * @param id the id
		 * @param secret2 the secret2
		 * @param updatePeriod the update period
		 */
		public IdSecret(int id, int secret2, long updatePeriod) {
			this.appId = id;
			this.secret = secret2;
			this.updatePeriod = updatePeriod;
		}
		
		/**
		 * Gets the app id.
		 *
		 * @return the app id
		 */
		public int getAppId() {
			return appId;
		}
		
		/**
		 * Gets the secret.
		 *
		 * @return the secret
		 */
		public int getSecret() {
			return secret;
		}

		/**
		 * Gets the update period.
		 *
		 * @return the update period
		 */
		public long getUpdatePeriod() {
			return updatePeriod;
		}
	}
	
    /**
     * Gets the list.
     *
     * @return the list
     */
    @Get("json")
    public GrcBoxAppInfoList getList();
    
    /**
     * New app.
     *
     * @param name the name
     * @return the id secret containing appId and the secret that must be used
     * for later modifications
     */
    @Post
    public IdSecret newApp(String name);
}
