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

import java.util.Collection;
import java.util.List;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import es.upv.grc.grcbox.common.*;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.common.resources.AppResource;
import es.upv.grc.grcbox.common.resources.AppsResource;
import es.upv.grc.grcbox.common.resources.IfacesResource;
import es.upv.grc.grcbox.common.resources.RootResource;
import es.upv.grc.grcbox.common.resources.RuleResource;
import es.upv.grc.grcbox.common.resources.RulesResource;
import es.upv.grc.grcbox.common.resources.AppsResource.IdSecret;

/**
 * AndroPiClient Read Information from the server and print it
 */
public class GrcBoxClient {
		private static IdSecret myIdSecret;
    public static void main(String[] args) throws Exception {
    	Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
        ClientResource clientResource = new ClientResource("http://grcbox:8080");
        /*
         * Get the status of the server
         */
        RootResource rootResource = clientResource.getChild("/", RootResource.class);
        GrcBoxStatus status = rootResource.getGrcBoxStatus();
        
        /*
         * Get list of interfaces
         */
        IfacesResource ifacesResource = clientResource.getChild("/ifaces", IfacesResource.class);
        GrcBoxInterfaceList ifacesList = ifacesResource.getList();
        Collection<GrcBoxInterface> ifaces = ifacesList.getList();
        System.out.println("The server has " +ifaces.size() + " ifaces");
        
        GrcBoxInterface iface = null;
        /*
         * chose the first CONNECTED interface
         */
        for (GrcBoxInterface grcBoxInterface : ifaces) {
			if(grcBoxInterface.isUp()){
				iface = grcBoxInterface;
				break;
			}
		}
        
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
        
        /*
         * Create a new resource for rules of this App 
         */

        ChallengeResponse authentication2 = new ChallengeResponse(
       		 ChallengeScheme.HTTP_BASIC, Integer.toString(myIdSecret.getAppId()), Integer.toString(myIdSecret.getSecret()).toCharArray());
        clientResource.setChallengeResponse(authentication2);
        
        RulesResource rulesResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules", RulesResource.class);
        
        GrcBoxRuleList rulesList =rulesResource.getList();
    	List<GrcBoxRule> myRules = rulesList.getList();
    	GrcBoxRule ruleIn = null;
    	GrcBoxRule ruleOut = null;
        appResource.keepAlive();
    	for(int i = 0; i < 4; i++){
    		int port = 20+i;
    		ruleIn = new GrcBoxRule(-1, GrcBoxRule.Protocol.TCP, RuleType.INCOMMING, 12, "wlan0", System.currentTimeMillis()+200, 1648, port, null, null, port, null);
    		ruleOut = new GrcBoxRule(-1, GrcBoxRule.Protocol.TCP, RuleType.OUTGOING, 12, "wlan0", System.currentTimeMillis()+200, 1648, port, null, null, port, null);
    		try{
    			/*
    			 * Create a new rule
    			 */
    			ruleIn = rulesResource.newRule(ruleIn);
    			ruleOut = rulesResource.newRule(ruleOut);
    		}
    		catch(ResourceException re){
    			if(re.getStatus().equals(Status.CONNECTOR_ERROR_COMMUNICATION)){
    				ruleIn = rulesResource.newRule(ruleIn);
    			}
    			else{
    				throw re;
    			}
    		}
    	}
        rulesList = rulesResource.getList();
        myRules = rulesList.getList();
        System.out.println("I've defined "+myRules.size()+ " rules");
        /*
         * Check that the new rule exists
         */
        RuleResource ruleResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules/"+ruleIn.getId(),RuleResource.class);
        GrcBoxRule rule2 = ruleResource.retrieve();
        
        /*
         * remove the rule
         */
        ruleResource.remove();
    }
}
