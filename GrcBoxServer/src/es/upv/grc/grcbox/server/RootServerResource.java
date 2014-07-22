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

package es.upv.grc.grcbox.server;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterface.State;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleIn;
import es.upv.grc.grcbox.common.GrcBoxStatus;
import es.upv.grc.grcbox.common.RootResource;
import es.upv.grc.grcbox.common.GrcBoxInterface.Type;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManagerThreadNotRunning;

/**
 * Mail server resource implementing the {@link MailResource} interface.
 */
public class RootServerResource extends ServerResource implements RootResource {
	
	@Override
    public GrcBoxStatus getAndroPiStatus() {
		RulesDB db = GrcBoxServerApplication.getDb();
		long start = System.currentTimeMillis();
		if(GrcBoxServerApplication.getConfig().isDebug())
			System.out.println("New Root Resource called " + start);
		
    	GrcBoxStatus status;
    	System.out.println(System.currentTimeMillis());
    	String name = GrcBoxServerApplication.getCurrent().getName();
		try {
			if(GrcBoxServerApplication.getConfig().isDebug())
				System.out.println(System.currentTimeMillis());
			int numIfaces = db.getOuterInterfaces().size();
			if(GrcBoxServerApplication.getConfig().isDebug())
				System.out.println(System.currentTimeMillis());
			int appSize = db.getApps().size();
			if(GrcBoxServerApplication.getConfig().isDebug())
				System.out.println(System.currentTimeMillis());
			int ruleSize =  db.getAllRules().size();
			if(GrcBoxServerApplication.getConfig().isDebug())
				System.out.println(System.currentTimeMillis());
			status = new GrcBoxStatus(name, numIfaces, appSize, ruleSize);
			if(GrcBoxServerApplication.getConfig().isDebug())
				System.out.println(System.currentTimeMillis());
		} catch (NetworkInterfaceManagerThreadNotRunning e) {
			e.printStackTrace();
			throw new ResourceException(503);
		}
		if(GrcBoxServerApplication.getConfig().isDebug()){
			System.out.println("New Root Resource finalized " + (System.currentTimeMillis() - start));
		}
    	return status;
    }
}
