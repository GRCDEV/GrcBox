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


import java.io.File;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import org.restlet.security.MethodAuthorizer;
import org.restlet.util.Series;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManager;
import es.upv.grc.grcbox.server.resources.AppServerResource;
import es.upv.grc.grcbox.server.resources.AppsServerResource;
import es.upv.grc.grcbox.server.resources.IfaceServerResource;
import es.upv.grc.grcbox.server.resources.IfacesServerResource;
import es.upv.grc.grcbox.server.resources.RootServerResource;
import es.upv.grc.grcbox.server.resources.RuleServerResource;
import es.upv.grc.grcbox.server.resources.RulesServerResource;


/**
 * Routing to annotated server resources.
 */
public class GrcBoxServerApplication extends Application {
	private static final Logger LOG = Logger.getLogger(NetworkInterfaceManager.class.getName()); 

	private static final String configFile = "config.json";
	private static GrcBoxConfig config;
	private static MapVerifier verifier = new MapVerifier();

	public GrcBoxServerApplication(){
		setName("GRCBox Server");
		setDescription("Connectivity for smartphones");
		setOwner("GRC");
		setAuthor("Sergio Martínez Tornell and Subhadeep Patra");
	}

	
	public static GrcBoxConfig getConfig() {
		return config;
	}

	protected static void setConfig(GrcBoxConfig config) {
		GrcBoxServerApplication.config = config;
	}

	/**
	 * Launches the application with an HTTP server.
	 * 
	 * @param args
	 *            The arguments.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//Load Config File
		LOG.info("Working Directory = " + System.getProperty("user.dir"));
		File file = new File("./config.json");
		ObjectMapper mapper = new ObjectMapper();
		config = mapper.readValue(file, GrcBoxConfig.class);
		if(!Collections.disjoint(config.getInnerInterfaces(), config.getOuterInterfaces())){
			System.err.print("InnerInterfaces and Outerinterfaces has elements in common. Aborting execution.");
			System.exit(-1);
		}
		
		LinkedList<String> innerInterfaces = config.getInnerInterfaces();
		RulesDB.setInnerInterfaces(innerInterfaces);
		RulesDB.initialize();
		for (String string : innerInterfaces) {
			startServer(string);
		}
	}

	private static void startServer(String string) throws Exception {
		Component androPiComponent = new Component();
		NetworkInterface iface = NetworkInterface.getByName(string);
		if(iface == null){
			System.err.println("ERROR: No inner  interface called "+ string + " exists");
			System.exit(-1);
		}
		List<InterfaceAddress> addresses =  iface.getInterfaceAddresses();
		InterfaceAddress addr = null;
		for (InterfaceAddress interfaceAddress : addresses) {			
			if(interfaceAddress.getAddress() instanceof Inet4Address){
				addr = interfaceAddress;
				break;
			}
		}
		
		if(addr != null){
			Server server = androPiComponent.getServers().add(Protocol.HTTP, addr.getAddress().getHostAddress(), 8080);
			androPiComponent.getDefaultHost().attach(new GrcBoxServerApplication());
			androPiComponent.start();
		}
		else {
			LOG.severe("The server could not be initialized. No Ipv4 address on innerinterface present");
			System.exit(1);
		}
	}

	public class Tracer extends Filter {
		public Tracer (Context context) {
			super(context);
		}

		/* (non-Javadoc)
		 * @see org.restlet.routing.Filter#beforeHandle(org.restlet.Request, org.restlet.Response)
		 */
		@Override
		protected int beforeHandle(Request request, Response response) {
			LOG.info(
					"Method: " + request.getMethod()
					+ "\nUser ID:" + request.getClientInfo().getUser()
					+ "\nResource URI : " + request.getResourceRef()
					+ "\nIP address: " + request.getClientInfo().getAddress()
					+ "\nAgent name: " + request.getClientInfo().getAgentName()
					+ "\nAgent version: " + request.getClientInfo().getAgentVersion()
					);
			return super.beforeHandle(request, response);
		}
	}

	
	@Override
	public Restlet createInboundRoot(){
		Router router = new Router(getContext());
		router.attach("/", RootServerResource.class);
		router.attach("/apps", AppsServerResource.class);
		router.attach("/ifaces", IfacesServerResource.class);
		router.attach("/ifaces/{ifaceId}", IfaceServerResource.class);
		router.attach("/apps/{appId}", authenticated(AppServerResource.class));
		router.attach("/apps/{appId}/rules", authenticated(RulesServerResource.class));
		router.attach("/apps/{appId}/rules/{ruleId}", authenticated(RuleServerResource.class));
		
		return router;
	}

    /**
     * Wraps a resource with a Tracer, then wraps that with a ChallengeAuthenticator.
     */
    private Restlet authenticated(Class<? extends ServerResource> targetClass) {
        Tracer tracer = new Tracer(getContext());
        
 
        ChallengeAuthenticator authenticator = new ChallengeAuthenticator(
                getContext(), ChallengeScheme.HTTP_BASIC, "AndroPi");
        authenticator.setVerifier(verifier);
        authenticator.setOptional(true);
        MethodAuthorizer authorizer = new MethodAuthorizer();
        authorizer.getAnonymousMethods().add(Method.GET);
        authorizer.getAuthenticatedMethods().add(Method.GET);
        authorizer.getAuthenticatedMethods().add(Method.POST);
        authorizer.getAuthenticatedMethods().add(Method.PUT);
        authorizer.getAuthenticatedMethods().add(Method.DELETE);
        authenticator.setNext(tracer);
        tracer.setNext(authorizer);
        authorizer.setNext(targetClass);
        return authenticator;
    }

	public static MapVerifier getVerifier() {
		return verifier;
	}


	public static String getConfigfile() {
		return configFile;
	}

}
