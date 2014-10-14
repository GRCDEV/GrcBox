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
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import es.upv.grc.grcbox.common.resources.AppResource;
import es.upv.grc.grcbox.common.resources.AppsResource;
import es.upv.grc.grcbox.common.resources.AppsResource.IdSecret;
import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxAppList;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleList;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.common.resources.IfacesResource;
import es.upv.grc.grcbox.common.resources.RootResource;
import es.upv.grc.grcbox.common.resources.RuleResource;
import es.upv.grc.grcbox.common.resources.RulesResource;



/*
 * TODO Check if the application is registered an throw an exception if not in all the methods. 
 */
public class GrcBoxClientService extends Service {
	private static final String SERVER_URL = "http://grcbox:8080";

    private final IBinder mBinder = new GrcBoxBinder();
	
	private static ClientResource clientResource = new ClientResource(SERVER_URL);;
	private static AppResource appResource = null;
	private static GrcBoxApp app;
	private static long keepAliveTime;
	private volatile static boolean registered;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static ScheduledFuture<?> keepAliveMonitor;
	private static boolean started = false;

	public class GrcBoxBinder extends Binder {
        public GrcBoxClientService getService() {
            // Return this instance of GrcBoxClientService so clients can call public methods
            return GrcBoxClientService.this;
        }
    }


	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		// The service is being created
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		Toast.makeText(this, "New Service Created", Toast.LENGTH_LONG).show();
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
    	if(started == false){
    		Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
    		started = true;
    	}
    	else{
    		Toast.makeText(this, "Service was already started", Toast.LENGTH_LONG).show();
    	}
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    	clientResource.release();
		Toast.makeText(this, "GRCBOX Service Destroyed !!", Toast.LENGTH_LONG).show();
    }

    
	private final Runnable sendKeepAlive = new Runnable() {
		@Override
		public void run() {
			if(isRegistered()){
				Log.v("KEEPALIVE", "Keep alive App "+app.getAppId());
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
	public boolean isRegistered() {
		return registered;
	}

	/*
	 *  TODO Revise Every method, changes in rules!!
	 */
	/*
	 * Register this app into the server.
	 */
	public boolean register(final String appName){
		/*
		 * Register a new application
		 */
		AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);

		IdSecret myIdSecret;
		myIdSecret = appsResource.newApp(appName);
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
		appResource = clientResource.getChild("/apps/"+app.getAppId(), AppResource.class);
		keepAliveMonitor = scheduler.scheduleAtFixedRate(
				sendKeepAlive, keepAliveTime/3, 
				keepAliveTime/3,
				TimeUnit.MILLISECONDS);
		registered = true;
		return registered;
	}
	
	public void deregister(){
		registered = false;
		try{
			appResource.rm();
			clientResource.release();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Log.v("CANCEL", "Cancel the Keep Alive Monitor");
		keepAliveMonitor.cancel(true);
	}
	
	/*
	 * get a list of the available interfaces from the server
	 */
	public Collection<GrcBoxInterface> getInterfaces(){
		IfacesResource ifaces = clientResource.getChild("/ifaces", IfacesResource.class);
		GrcBoxInterfaceList list = ifaces.getList();
		return list.getList();
	}
	
	/*
	 * get a list of the available interfaces from the server
	 */
	public Collection<GrcBoxApp> getApps(){
		AppsResource apps = clientResource.getChild("/apps", AppsResource.class);
		GrcBoxAppList list = apps.getList();
		return list.getList();
	}
	
	/*
	 * Get a list of my registered rules.
	 */
	public Collection<GrcBoxRule> getRules(){
		RulesResource rules = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		GrcBoxRuleList list = rules.getList();
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
	 * Get a list of available Multicast Plugins
	 */
	public Collection<String> getMulticastPlugins(){
		RootResource rootRes = clientResource.getChild("/", RootResource.class); 
		return rootRes.getGrcBoxStatus().getSupportedMulticastPlugins().getList();
	}
	
	/*
	 * Register an incoming flow in the server using the provided information
	 * Return a GrcServerSocket ready to be used 
	 */
	public GrcBoxServerSocket createServerSocket(int port, GrcBoxInterface iface) throws IOException{
		GrcBoxRule rule = new GrcBoxRule(-1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, -1, port, null, iface.getAddress(), port, null);
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
		GrcBoxRule rule = new GrcBoxRule(-1, es.upv.grc.grcbox.common.GrcBoxRule.Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, addr.getHostAddress(), null, port, null);
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
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, address.getHostAddress(), null, localPort, null);
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}
	
	/*
	 * Register an outgoing flow at the server using the destination host.
	 */
	public GrcBoxSocket createSocket(String host, int port, GrcBoxInterface iface) throws UnknownHostException, IOException{
		Socket socket = new Socket(host, port);
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, socket.getInetAddress().getHostAddress(), host, port, host);
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	public GrcBoxSocket createSocket(String host, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(host, port, localAddr, localPort);
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, socket.getInetAddress().getHostAddress(), host, localPort, host);
		registerNewRule(rule);
		GrcBoxSocket grcSocket = new GrcBoxSocket(this, rule, socket);
		return grcSocket;
	}

	public GrcBoxDatagramSocket createDatagramSocket(GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket();
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null, null, 0, null);
		registerNewRule(rule);
		GrcBoxDatagramSocket grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		return grcSocket;
	}

	public GrcBoxDatagramSocket createDatagramSocket(int port, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null, null, port, null);
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
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remotePort, remoteAddr.getHostAddress(), null, remotePort, null);
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
		GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remoteHost.getPort(), remoteHost.getAddress().getHostAddress(), null, port, null);
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
	 * No rule will be registered in the seGrcBoxClientService grcBoxClient = new GrcBoxClientService();
        grcBoxClient.register("GrcBoxRules", new OnRegisterListener() {
			
			@Override
			public void onRegistered(boolean arg0) {
				registered = arg0;
			}
		});rver until 
	 * subscribe or bind are called 
	 * TODO This feature is not supported yet
	 */
	public GrcBoxMulticasSocket createMulticasSocket(int port, GrcBoxInterface iface) throws IOException{
		MulticastSocket socket = new MulticastSocket(port);
		GrcBoxMulticasSocket grcSocket = new GrcBoxMulticasSocket(this, socket);
		return grcSocket;
	}
}
