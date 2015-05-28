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
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import org.restlet.security.MethodAuthorizer;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManager;
import es.upv.grc.grcbox.server.resources.AppServerResource;
import es.upv.grc.grcbox.server.resources.AppsServerResource;
import es.upv.grc.grcbox.server.resources.IfaceServerResource;
import es.upv.grc.grcbox.server.resources.IfacesServerResource;
import es.upv.grc.grcbox.server.resources.RootServerResource;
import es.upv.grc.grcbox.server.resources.RuleServerResource;
import es.upv.grc.grcbox.server.resources.RulesServerResource;
import es.upv.grc.grcbox.server.rulesdb.RulesDB;

/**
 * Routing to annotated server resources.
 */
public class GrcBoxServerApplication extends Application {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(NetworkInterfaceManager.class.getName()); 

	/** The Constant configFile. */
	private static final String configFile = "config.json";
	
	/** The config. */
	private static GrcBoxConfig config;
	
	/** The verifier, used to store registered applications and their secrets. 
	 * TODO: Move it to rulesDb?
	 * */
	private static MapVerifier verifier = new MapVerifier();

	/**
	 * Instantiates a new grc box server application.
	 */
	public GrcBoxServerApplication(){
		setName("GRCBox Server");
		setDescription("Connectivity for smartphones");
		setOwner("GRC");
		setAuthor("Sergio Martínez Tornell and Subhadeep Patra");
	}

	
	/**
	 * Gets the config.
	 *
	 * @return the config
	 */
	public static GrcBoxConfig getConfig() {
		return config;
	}

	/**
	 * Sets the config.
	 *
	 * @param config the new config
	 */
	protected static void setConfig(GrcBoxConfig config) {
		GrcBoxServerApplication.config = config;
	}

	/**
	 * Launches the application with an HTTP server.
	 *
	 * @param args            The arguments.
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		//Load Config File
		LOG.info("Working Directory = " + System.getProperty("user.dir"));
		File file = new File("./config.json");
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		ObjectMapper mapper = new ObjectMapper();
		config = mapper.readValue(file, GrcBoxConfig.class);
		if(!Collections.disjoint(config.getInnerInterfaces(), config.getOuterInterfaces())){
			System.err.print("InnerInterfaces and Outerinterfaces has elements in common. Aborting execution.");
			System.exit(-1);
		}
		/*
		 * Get teh list of inner interfaces from the config file
		 */
		LinkedList<String> innerInterfaces = config.getInnerInterfaces();
		RulesDB.setInnerInterfaces(innerInterfaces);
		RulesDB.initialize();
		/**
		 * The server listen on all inner interfaces
		 */
		for (String string : innerInterfaces) {
			startServer(string);
		}
	}

	/**
	 * Start a server.
	 *
	 * @param string the string
	 * @throws Exception the exception
	 */
	private static void startServer(String string) throws Exception {
		Component grcBoxComponent = new Component();
		NetworkInterface iface = NetworkInterface.getByName(string);
		
		if(iface == null){
			System.err.println("ERROR: No inner  interface called "+ string + " exists");
			System.exit(-1);
		}
		
		/**
		 * Only the first address of every interface is used, this may be a problem
		 */
		List<InterfaceAddress> addresses =  iface.getInterfaceAddresses();
		InterfaceAddress addr = null;
		
		for (InterfaceAddress interfaceAddress : addresses) {			
			if(interfaceAddress.getAddress() instanceof Inet4Address){
				addr = interfaceAddress;
				break;
			}
		}
		
		/*
		 * Start the server only if a valid ipv4 address exist
		 */
		if(addr != null){
			Server server = grcBoxComponent.getServers().add(Protocol.HTTP, addr.getAddress().getHostAddress(), 8080);
			grcBoxComponent.getDefaultHost().attach(new GrcBoxServerApplication());
			grcBoxComponent.start();
		}
		else {
			LOG.severe("The server could not be initialized. No Ipv4 address on innerinterface present");
			System.exit(1);
		}
	}

	/**
	 * This tracer is used to trace some calls on resources
	 */
	public class Tracer extends Filter {
		
		/**
		 * Instantiates a new tracer.
		 *
		 * @param context the context
		 */
		public Tracer (Context context) {
			super(context);
		}

		/**
		 * Prints information abour the request
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
					+ "\nContent: " + request.getEntityAsText()
					);
			return super.beforeHandle(request, response);
		}
	}

	
	/**
	 * Defines a server resource for every url
	 */
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
     *
     * @param targetClass the target class
     * @return the restlet
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

	/**
	 * Gets the verifier.
	 *
	 * @return the verifier
	 */
	public static MapVerifier getVerifier() {
		return verifier;
	}


	/**
	 * Gets the configfile.
	 *
	 * @return the configfile
	 */
	public static String getConfigfile() {
		return configFile;
	}

}
