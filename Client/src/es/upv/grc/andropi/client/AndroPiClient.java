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

package es.upv.grc.andropi.client;



import java.net.Inet4Address;
import java.net.Inet6Address;


import org.restlet.resource.ClientResource;

import es.upv.grc.andropi.common.*;

/**
 * AndroPiClient Read Information from the server and print it
 */
public class AndroPiClient {
    public static void main(String[] args) throws Exception {
        RootResource clientResource = ClientResource.create("http://localhost:8111/", RootResource.class);
        AndroPiStatus status = clientResource.retrieve();
        for(int i=0; i< status.getInterfaces().size();i++){
        	System.out.println("Name:"+ status.getInterfaces().get(i).getName()+ " Addresses:");
        	byte [] ipAddress = status.getInterfaces().get(i).getIpAddress();
        	if(ipAddress.length == 4){
        		System.out.println(i+":"+ Inet4Address.getByAddress(ipAddress).toString());	
        	}
        	else{
        		System.out.println(i+":"+ Inet6Address.getByAddress(ipAddress).toString());
        	}
        }
        for(int i=0; i< status.getFlows().size();i++){
        	System.out.println(status.getFlows().get(i).getId());
        }
    }
}
