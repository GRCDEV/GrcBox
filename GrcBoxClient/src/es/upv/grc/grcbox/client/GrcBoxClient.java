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

package es.upv.grc.grcbox.client;

import java.net.InetAddress;
import java.util.List;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import es.upv.grc.grcbox.common.*;
import es.upv.grc.grcbox.common.AppsResource.IdSecret;

/**
 * AndroPiClient Read Information from the server and print it
 */
public class GrcBoxClient {
		private static IdSecret myIdSecret;
    public static void main(String[] args) throws Exception {
        ClientResource clientResource = new ClientResource("http://localhost:8080");
        /*
         * Get the status of the server
         */
        RootResource rootResource = clientResource.getChild("/", RootResource.class);
        GrcBoxStatus status = rootResource.getAndroPiStatus();
        
        /*
         * Register a new application
         */
    	Client client = new Client(new Context(), Protocol.HTTPS);
    	Series<Parameter> parameters = client.getContext().getParameters();
    	parameters.add("truststorePath",
    			"src/org/restlet/example/book/restlet/ch05/clientTrust.jks");
    	parameters.add("truststorePassword", "password");
    	parameters.add("truststoreType", "JKS");
    	clientResource.setNext(client);
        AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);
        AppResource appResource = null;
        GrcBoxAppInfo myInfo;
        for(int i = 0; i < 10; i++){
        	try {
        		myIdSecret = appsResource.newApp("TestApp"+i);
        	} 
        	catch(ResourceException re){
        		Status st = re.getStatus();
        		if(st.equals(Status.CONNECTOR_ERROR_COMMUNICATION))
        			myIdSecret = appsResource.newApp("TestApp"+i);
        		else
        			throw re;
        	}

        	System.out.println("I've just registered as a new AndroPi App, ID:"+myIdSecret.getAppId() + " Secret:"+myIdSecret.getSecret());

        	appResource = clientResource.getChild("/apps/"+myIdSecret.getAppId(), AppResource.class);

        	/*
        	 * Dinamically generate the new resource
        	 */
        	

        	appResource = clientResource.getChild("/apps/"+myIdSecret.getAppId(), AppResource.class);
        	myInfo = appResource.retrieve();

        	System.out.println("I was registered with name "+myInfo.getName()+ " and Id "+ myInfo.getAppId());
        }
    	ChallengeResponse authentication = new ChallengeResponse(
    			ChallengeScheme.HTTP_BASIC, Integer.toString(myIdSecret.getAppId()), Integer.toString(myIdSecret.getSecret()).toCharArray());
    	clientResource.setChallengeResponse(authentication);

    	appResource = clientResource.getChild("/apps/"+myIdSecret.getAppId(), AppResource.class);
        appResource.keepAlive();
        
    	myInfo = appResource.retrieve();
        /*
         * Create a new resource for rules of this App 
         */

        ChallengeResponse authentication2 = new ChallengeResponse(
       		 ChallengeScheme.HTTP_BASIC, Integer.toString(myIdSecret.getAppId()), Integer.toString(myIdSecret.getSecret()).toCharArray());
        clientResource.setChallengeResponse(authentication2);
        
        RulesResource rulesResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules", RulesResource.class);
        
        List<GrcBoxRule> myRules;
    	myRules = rulesResource.getList();
    	GrcBoxRule rule = null;
    	for(int i = 0; i < 4; i++){
    		int port = 20+i;
    		rule = new GrcBoxRuleIn(-1, GrcBoxRule.Protocol.TCP, 12, "wlan0", System.currentTimeMillis()+200, 1648, port, null, null, port, "192.168.5.147");
    		
    		try{
    			/*
    			 * Create a new rule
    			 */
    			rule = rulesResource.newRule(rule);
    		}
    		catch(ResourceException re){
    			if(re.getStatus().equals(Status.CONNECTOR_ERROR_COMMUNICATION)){
    				rule = rulesResource.newRule(rule);
    			}
    			else{
    				throw re;
    			}
    		}
    	}
        myRules = rulesResource.getList();
        System.out.println("I've defined :"+myRules.size()+ " rules");
        /*
         * Check that the new rule exists
         */
        RuleResource ruleResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules/"+rule.getId(),RuleResource.class);
        GrcBoxRule rule2 = ruleResource.retrieve();
        
        /*
         * remove the rule
         */
        ruleResource.remove();
    }
}
