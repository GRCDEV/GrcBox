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
import java.util.List;

import org.restlet.resource.ClientResource;

import es.upv.grc.andropi.common.*;
import es.upv.grc.andropi.common.AppsResource.IdSecret;

/**
 * AndroPiClient Read Information from the server and print it
 */
public class AndroPiClient {
		private static IdSecret myIdSecret;
    public static void main(String[] args) throws Exception {
        ClientResource clientResource = new ClientResource("http://localhost:8080");
        /*
         * Get the status of the server
         */
        RootResource rootResource = clientResource.getChild("/", RootResource.class);
        AndroPiStatus status = rootResource.getAndroPiStatus();
        
        /*
         * Register a new application
         */
        AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);
        myIdSecret = appsResource.newApp("TestApp1");
        System.out.println("I've just registered as a new AndroPi App, ID:"+myIdSecret.getAppId() + " Secret:"+myIdSecret.getSecret());
        
        /*
         * Dinamically generate the new resource
         */
        AppResource appResource = clientResource.getChild("/apps/"+myIdSecret.getAppId(), AppResource.class);
        
        /*
         * Check the information
         */
        AndroPiAppInfo myInfo = appResource.retrieve();
        System.out.println("I was registered with name "+myInfo.getName()+ "and Id "+ myInfo.getAppId());
        
        /*
         * Create a new resource for rules of this App 
         */
        RulesResource rulesResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules", RulesResource.class);
        List<AndroPiRule> myRules = rulesResource.getList();
        
        /*
         * Create a new rule
         */
        AndroPiRule rule = rulesResource.newOutRule(0, -1, 22, 15, 18, myIdSecret.getSecret());
        
        /*
         * Check that the new rule exists
         */
        RuleResource ruleResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules/"+rule.getId(),RuleResource.class);
        AndroPiRule rule2 = ruleResource.retrieve();
        
        /*
         * remove the rule
         */
        ruleResource.remove(myIdSecret.getSecret());
    }
}
