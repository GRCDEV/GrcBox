package es.upv.grc.grcbox.androlib;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

import es.upv.grc.grcbox.common.AppResource;
import es.upv.grc.grcbox.common.AppsResource;
import es.upv.grc.grcbox.common.AppsResource.IdSecret;
import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleIn;
import es.upv.grc.grcbox.common.GrcBoxRuleOut;
import es.upv.grc.grcbox.common.GrcBoxStatus;
import es.upv.grc.grcbox.common.IfacesResource;
import es.upv.grc.grcbox.common.RootResource;
import es.upv.grc.grcbox.common.RuleResource;
import es.upv.grc.grcbox.common.RulesResource;



/*
 * TODO Check if the application is registered an throw an exception if not in all the methods. 
 */
public class GrcBoxClient {
	private static String SERVER_URL = "http://grcbox:8080";

	private static ClientResource clientResource = new ClientResource(SERVER_URL);;
	private static AppResource appResource = null;
	private static GrcBoxApp app;
	private static long keepAliveTime;
	private volatile static boolean registered;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static ScheduledFuture<?> keepAliveMonitor;
	
	
	private final static Runnable sendKeepAlive = new Runnable() {
		@Override
		public void run() {
			if(isRegistered()){
				appResource = clientResource.getChild("/apps/"+app.getAppId(), AppResource.class);
				appResource.keepAlive();
			}
			else{
				keepAliveMonitor.cancel(true);
			}
		}
	};
	
	/**
	 * @return the registered
	 */
	public static boolean isRegistered() {
		return registered;
	}

	/*
	 * Check if the GrcBox Server is available
	 */
	public boolean isServerAvailable(){
		RootResource rootResource = clientResource.getChild("/", RootResource.class);
		GrcBoxStatus status = rootResource.getAndroPiStatus();
		return true;
	}

	/*
	 * Register this app into the server.
	 */
	public boolean register(String appName){
		/*
		 * Register a new application
		 */
		Client client = new Client(new Context(), org.restlet.data.Protocol.HTTPS);
		Series<Parameter> parameters = client.getContext().getParameters();
		parameters.add("truststorePath",
				"src/org/restlet/example/book/restlet/ch05/clientTrust.jks");
		parameters.add("truststorePassword", "password");
		parameters.add("truststoreType", "JKS");
		clientResource.setNext(client);
		AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);

		IdSecret myIdSecret;
		try {
			myIdSecret = appsResource.newApp(appName);
		} 
		catch(ResourceException re){
			Status st = re.getStatus();
			if(st.equals(Status.CONNECTOR_ERROR_COMMUNICATION))
				myIdSecret = appsResource.newApp(appName);
			else
				throw re;
		}
		/*
		 * Get the information stored in the server. 
		 */
		app = new GrcBoxApp(myIdSecret.getAppId(), appName, System.currentTimeMillis());
		keepAliveTime = myIdSecret.getUpdatePeriod();
		
		/*
		 * Add authentication information to clientResource class
		 */
		ChallengeResponse authentication = new ChallengeResponse(
    			ChallengeScheme.HTTP_BASIC, 
    			Integer.toString(myIdSecret.getAppId()), 
    			Integer.toString(myIdSecret.getSecret()).toCharArray());
		
    	clientResource.setChallengeResponse(authentication);
    	keepAliveMonitor = scheduler.scheduleAtFixedRate(
    			sendKeepAlive, keepAliveTime/3, 
    			keepAliveTime/3, 
    			TimeUnit.MILLISECONDS);
		registered = true;
		return true;
	}

	public static void deregister(){
		registered = false;
		keepAliveMonitor.cancel(true);
	}
	
	/*
	 * get a list of the available interfaces from the server
	 */
	public List<GrcBoxInterface> getInterfaces(){
		IfacesResource ifaces = clientResource.getChild("/ifaces", IfacesResource.class);
		GrcBoxInterfaceList list = ifaces.getList();
		return list.getList();
	}
	
	/*
	 * Register a new rule in the server.
	 * Low level method for compatibility with third party communication libraries.
	 * The application must remove the rule after communication have finished.
	 */
	public GrcBoxRule registerNewRule(GrcBoxRule rule){
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		return rule;
	}
	
	/*
	 * Remove a certain rule. It is usually called after closing a GrcBox"socket"
	 */
	public void removeRule(GrcBoxRule rule){
		RuleResource ruleRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules/"+rule.getId(), RuleResource.class);
		ruleRes.remove();
		return;
	}
	
	
	/*
	 * Register an incoming flow in the server using the provided information
	 * Return a GrcServerSocket ready to be used 
	 */
	public GrcBoxServerSocket createServerSocket(int port, GrcBoxInterface iface) throws IOException{
		GrcBoxRule rule = new GrcBoxRuleIn(-1, Protocol.TCP, app.getAppId(), iface.getName(), 0, -1, port, null, iface.getIpAddress(), port);
		registerNewRule(rule);
		ServerSocket socket = new ServerSocket(port);
		GrcBoxServerSocket grcSocket = new GrcBoxServerSocket(this, rule, socket);
		return grcSocket;
	}

	/*
	 * register an outgoing flow at the server using the destination addr and the destination port.
	 * Return a socket already connected.
	 */
	public GrcBoxSocket createSocket(InetAddress addr, int port, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(addr, port);
		GrcBoxRule rule = new GrcBoxRuleOut(-1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, addr.getHostAddress());
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	/*
	 * Register an outgoing flow at the server using the destination address, 
	 * the destination port, and the local port.
	 */
	public GrcBoxSocket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(address, port, localAddr, localPort);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, address.getHostAddress());
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 * Register an outgoing flow at the server using the destination host.
	 */
	public GrcBoxSocket createSocket(String host, int port, GrcBoxInterface iface) throws UnknownHostException, IOException{
		Socket socket = new Socket(host, port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, socket.getInetAddress().getHostAddress());
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	public GrcBoxSocket createSocket(String host, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(host, port, localAddr, localPort);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, socket.getInetAddress().getHostAddress());
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	public GrcBoxDatagramSocket createDatagramSocket(GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket();
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null);
		registerNewRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}

	public GrcBoxDatagramSocket createDatagramSocket(int port, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null);
		registerNewRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 *	Create a new grcBoxDatagram Socket bounded to the local port.
	 *	The traffic flow registered on the server is restricted to the specifics remote address
	 *	and remote port. 
	 */
	public GrcBoxDatagramSocket createDatagramSocket(int port, InetAddress remoteAddr, int remotePort, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remotePort, remoteAddr.getHostAddress());
		registerNewRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}

	/*
	 *	Create a new grcBoxDatagram Socket bounded to the local port.
	 *	The traffic flow registered on the server is restricted to the specifics remote address
	 *	and remote port. 
	 */
	public GrcBoxDatagramSocket createDatagramSocket(int port, InetSocketAddress remoteHost, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remoteHost.getPort(), remoteHost.getAddress().getHostAddress());
		registerNewRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 * Create a new multicast socket.
	 * No rule will be registered in the server until 
	 * subscribe or bind are called 
	 * TODO This feature is not supported yet
	 */
	public GrcBoxMulticasSocket createMulticasSocket(GrcBoxInterface iface) throws IOException{
		MulticastSocket socket = new MulticastSocket();
		GrcBoxMulticasSocket grcSocket = new GrcBoxMulticasSocket(this, socket);
		return grcSocket;
	}
	
	/*
	 * Create a new multicast socket bound to the local specific port
	 * No rule will be registered in the server until 
	 * subscribe or bind are called 
	 * TODO This feature is not supported yet
	 */
	public GrcBoxMulticasSocket createMulticasSocket(int port, GrcBoxInterface iface) throws IOException{
		MulticastSocket socket = new MulticastSocket(port);
		GrcBoxMulticasSocket grcSocket = new GrcBoxMulticasSocket(this, socket);
		return grcSocket;
	}
}
