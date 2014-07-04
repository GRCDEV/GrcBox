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
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManager;


/**
 * Routing to annotated server resources.
 */
public class GrcBoxServerApplication extends Application {

	private static final String configFile = "/res/config.json";
	private static GrcBoxConfig config;
	private static MapVerifier verifier = new MapVerifier();
	private static RulesDB db;
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	/*
	 * TODO Move this thread to the DB class FUTURE
	 */
	final static Runnable dbMonitor = new Runnable() {
		@Override
		public void run() {
			long timeout = config.getKeepAliveTime();
			long now = System.currentTimeMillis();
			List<GrcBoxApp> appList = db.getApps();
			for (GrcBoxApp androPiApp : appList) {
				long diff = now - androPiApp.getLastKeepAlive();
				if(diff > timeout ){
					System.out.println(diff);
					db.rmApp(androPiApp.getAppId());
					GrcBoxServerApplication.getVerifier().getLocalSecrets().remove(androPiApp.getAppId());
				}
				else{
					List<GrcBoxRule> rules = db.getRulesByApp(androPiApp.getAppId());
					for (GrcBoxRule rule : rules) {
						if(rule.getExpire() < now){
							db.rmRule(androPiApp.getAppId(), rule.getId());
						}
					}
				}
			}
		}
	};
	
	public GrcBoxServerApplication(){
		setName("GRCBox Server");
		setDescription("Connectivity for smartphones");
		setOwner("GRC");
		setAuthor("Sergio Martínez Tornell and Subhadeep Patra");
	}

	
	protected static GrcBoxConfig getConfig() {
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
		URL uri = GrcBoxServerApplication.class.getResource(configFile);
		File file = new File(uri.getPath());
		ObjectMapper mapper = new ObjectMapper();
		config = mapper.readValue(file, GrcBoxConfig.class);
		
		if(!Collections.disjoint(config.getInnerInterfaces(), config.getOuterInterfaces())){
			System.err.print("InnerInterfaces and Outerinterfaces has elements in common. Aborting execution.");
			System.exit(-1);
		}
		
		db = new RulesDB();
		
		/*
		 * TODO Move this into the RulesDB class FUTURE
		 */
		final ScheduledFuture<?> monitorHandle = scheduler.scheduleAtFixedRate(dbMonitor, config.getKeepAliveTime(), config.getKeepAliveTime(), TimeUnit.MILLISECONDS);
		
		LinkedList<String> innerInterfaces = config.getInnerInterfaces();
		db.setInnerInterfaces(innerInterfaces);
		db.setOuterInterfaces(config.getOuterInterfaces());
		db.initialize();
		for (String string : innerInterfaces) {
			startServer(string);
		}
	}

	private static void startServer(String string) throws Exception {
		Component androPiComponent = new Component();
		NetworkInterface iface = NetworkInterface.getByName(string);
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
			Series<Parameter> parameters = server.getContext().getParameters();
			parameters.add("keystorePath",
					"src/res/serverKey.jks");
			parameters.add("keystorePassword", "password");
			parameters.add("keystoreType", "JKS");
			parameters.add("keyPassword", "password");
			androPiComponent.getDefaultHost().attach(new GrcBoxServerApplication());
			androPiComponent.start();
		}
		else {
			System.out.println("The server could not be initialized. No Ipv4 address on innerinterface present");
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
			System.out.println(
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
        tracer.setNext(targetClass);
 
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
        authorizer.setNext(tracer);
        
        authenticator.setNext(authorizer);
        
        return authenticator;
    }

	public static MapVerifier getVerifier() {
		return verifier;
	}


	public static String getConfigfile() {
		return configFile;
	}

	public static RulesDB getDb() {
		return db;
	}
}
