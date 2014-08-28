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

package es.upv.grc.grcbox.server.resources;



import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxStatus;
import es.upv.grc.grcbox.common.resources.RootResource;
import es.upv.grc.grcbox.server.GrcBoxServerApplication;
import es.upv.grc.grcbox.server.RulesDB;

/**
 * Mail server resource implementing the {@link MailResource} interface.
 */
public class RootServerResource extends ServerResource implements RootResource {

	@Override
	public GrcBoxStatus getGrcBoxStatus() {
		RulesDB db = GrcBoxServerApplication.getDb();
		GrcBoxStatus status;
		String name = GrcBoxServerApplication.getCurrent().getName();
		int numIfaces = db.getAllInterfaces().size();
		int appSize = db.getApps().size();
		int ruleSize =  db.getAllRules().size();
		status = new GrcBoxStatus(name, numIfaces, appSize, ruleSize);
		return status;
	}
}
