package es.upv.grc.grcbox.androlib;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.List;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.resource.*;
import org.restlet.util.Series;

import es.upv.grc.grcbox.common.*;
import es.upv.grc.grcbox.common.AppsResource.IdSecret;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;

public class GrcBoxClient {
	private static String SERVER_URL = "http://grcbox:8080";

	private static ClientResource clientResource = new ClientResource(SERVER_URL);;
	private static AppResource appResource = null;
	private static GrcBoxApp app;
	private static long updatePeriod;
	private static boolean registered;

	/*
	 * Check if the GrcBox Server is available
	 */
	boolean isServerAvailable(){
		RootResource rootResource = clientResource.getChild("/", RootResource.class);
		GrcBoxStatus status = rootResource.getAndroPiStatus();
		return true;
	}

	/*
	 * Register this app into the server.
	 */
	boolean register(String appName){
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

		GrcBoxAppInfo myInfo;
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
		appResource = clientResource.getChild("/apps/"+myIdSecret.getAppId(), AppResource.class);

		app = new GrcBoxApp(myIdSecret.getAppId(), appName, System.currentTimeMillis());
		updatePeriod = myIdSecret.getUpdatePeriod();
		
		/*
		 * Add authentication information to clientResource class
		 */
		ChallengeResponse authentication = new ChallengeResponse(
    			ChallengeScheme.HTTP_BASIC, 
    			Integer.toString(myIdSecret.getAppId()), 
    			Integer.toString(myIdSecret.getSecret()).toCharArray());
		
    	clientResource.setChallengeResponse(authentication);
		
		registered = true;
		return true;
	}

	/*
	 * get a list of the available interfaces from the server
	 */
	List<GrcBoxInterface> getInterfaces(){
		IfacesResource ifaces = clientResource.getChild("/ifaces", IfacesResource.class);
		return ifaces.getList();
	}
	
	/*
	 * Remove a certain rule. It is usually called after closing a GrcBox"socket"
	 */
	void removeRule(GrcBoxRule rule){
		RuleResource ruleRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules/"+rule.getId(), RuleResource.class);
		ruleRes.remove();
		return;
	}
	
	/*
	 * Register an incoming flow in the server using the provided information
	 * Return a GrcServerSocket ready to be used 
	 */
	GrcBoxServerSocket createServerSocket(int port, GrcBoxInterface iface) throws IOException{
		GrcBoxRule rule = new GrcBoxRuleIn(-1, Protocol.TCP, app.getAppId(), iface.getName(), 0, -1, port, null, iface.getIpAddress(), port);
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		ServerSocket socket = new ServerSocket(port);
		GrcBoxServerSocket grcSocket = new GrcBoxServerSocket(this, rule, socket);
		return grcSocket;
	}

	/*
	 * register an outgoing flow at the server using the destination addr and the destination port.
	 * Return a socket already connected.
	 */
	GrcBoxSocket createSocket(InetAddress addr, int port, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(addr, port);
		GrcBoxRule rule = new GrcBoxRuleOut(-1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, null, addr.getHostAddress());
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	/*
	 * Register an outgoing flow at the server using the destination address, 
	 * the destination port, and the local port.
	 */
	GrcBoxSocket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(address, port, localAddr, localPort);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, null, address.getHostAddress());
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 * Register an outgoing flow at the server using the destination host.
	 */
	GrcBoxSocket createSocket(String host, int port, GrcBoxInterface iface) throws UnknownHostException, IOException{
		Socket socket = new Socket(host, port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, null, socket.getInetAddress().getHostAddress());
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	GrcBoxSocket createSocket(String host, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(host, port, localAddr, localPort);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.TCP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, null, socket.getInetAddress().getHostAddress());
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	GrcBoxDatagramSocket createDatagramSocket(GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket();
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null, null);
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}

	GrcBoxDatagramSocket createDatagramSocket(int port, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null, null);
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 *	Create a new grcBoxDatagram Socket bounded to the local port.
	 *	The traffic flow registered on the server is restricted to the specifics remote address
	 *	and remote port. 
	 */
	GrcBoxDatagramSocket createDatagramSocket(int port, InetAddress remoteAddr, int remotePort, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remotePort, null, remoteAddr.getHostAddress());
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}

	/*
	 *	Create a new grcBoxDatagram Socket bounded to the local port.
	 *	The traffic flow registered on the server is restricted to the specifics remote address
	 *	and remote port. 
	 */
	GrcBoxDatagramSocket createDatagramSocket(int port, InetSocketAddress remoteHost, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRuleOut( -1, Protocol.UDP, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remoteHost.getPort(), null, remoteHost.getAddress().getHostAddress());
		RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		rule = rulesRes.newRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 * Create a new multicast socket.
	 * No rule will be registered in the server until 
	 * subscribe or bind are called 
	 * TODO This feature is not supported yet
	 */
	GrcBoxMulticasSocket createMulticasSocket(GrcBoxInterface iface) throws IOException{
		MulticastSocket socket = new MulticastSocket();
		GrcBoxMulticasSocket grcSocket = new GrcBoxMulticasSocket(this, socket);
		return grcSocket;
	}
	
	/*
	 * Create a new multicast socket bound to the local specific port
	 */
	GrcBoxMulticasSocket createMulticasSocket(int port, GrcBoxInterface iface) throws IOException{
		MulticastSocket socket = new MulticastSocket(port);
		GrcBoxMulticasSocket grcSocket = new GrcBoxMulticasSocket(this, socket);
		return grcSocket;
	}
}
