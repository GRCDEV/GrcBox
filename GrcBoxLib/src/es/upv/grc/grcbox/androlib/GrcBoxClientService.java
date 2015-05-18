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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.text.GetChars;
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
	private final String TAG = this.getClass().getSimpleName(); 
	private static final String SERVER_URL = "http://grcbox:8080";

    private final IBinder mBinder = new GrcBoxBinder();
	
	private static ClientResource clientResource = new ClientResource(SERVER_URL);;
	private static AppResource appResource = null;
	private static GrcBoxApp app = null;
	private String appName = null;
	private static long keepAliveTime;
	private volatile static boolean registered;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	volatile private static ScheduledFuture<?> keepAliveMonitor;
	private static LinkedList<OnRegisteredChangedListener> regListener = new LinkedList<GrcBoxClientService.OnRegisteredChangedListener>();
	private LinkedList<GrcBoxRule> rulesCached = new LinkedList<GrcBoxRule>();
	private BroadcastReceiver wifiReceiver;
	volatile private boolean mustRegister = false;
	
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
		Log.v(TAG, "OnCreate");
		// The service is being created
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		IntentFilter wifiFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, wifiFilter);
		Toast.makeText(this, "New Service Created", Toast.LENGTH_LONG).show();
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.v(TAG, "OnStart");
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
    	Log.v(TAG,"OnDestroy");
		if(wifiReceiver != null){
			Log.v(TAG, "Unregister Wifi Monitor");
			unregisterReceiver(wifiReceiver);
		}
		Toast.makeText(this, "GRCBOX Service Destroyed !!", Toast.LENGTH_LONG).show();
    }

    
	private final Runnable sendKeepAlive = new Runnable() {
		@Override
		public void run() {
			try{
				appResource.keepAlive();
				setRegistered(true);
			}
			catch(ResourceException e){
				parseResourceException(e);
			}
			Log.v("KEEPALIVE", "Keep alive App "+app.getAppId());
		}
	};


	private void cancelKeepAlive(){
		Log.v(TAG,"Canceling Keep alive");
		if(keepAliveMonitor != null){
			keepAliveMonitor.cancel(true);
		}
	}

	
	//=========================================================================//
    // Wifi BroadcastReceiver
    //=========================================================================//
    private class WifiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            	Log.v(TAG, "Wifi State Changed");
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo != null){
                    if(networkInfo.isConnected()){
                    	Log.v(TAG,"Wifi is connected");
                    	if(mustRegister && app == null){
                    		scheduler.schedule(registrator, 0, TimeUnit.MICROSECONDS);
                    	}
                    	else if( app != null && keepAliveMonitor.isCancelled()){
                    		Log.v(TAG, "AfterError:Starting Keep Alive: time " +keepAliveTime);
                    		keepAliveMonitor = scheduler.scheduleAtFixedRate(
                				sendKeepAlive, 0, 
                				keepAliveTime/3,
                				TimeUnit.MILLISECONDS);
                    	}
                    }
                    else{
                    	Log.v(TAG,"Wifi is disconected");
                    	cancelKeepAlive();
                    }
                }
            }
        }
    }
	
    /*
     * Process ResourceExceptions
     * If it is UnknowException, we are not connected to a GRCBOX network
     * if it is FORBIDDEN, our register has expired.
     * In any other case throw exception.
     */
    synchronized private void parseResourceException(ResourceException e){
    	Log.v(TAG, "Error connecting to Server:"+e.toString());
		cancelKeepAlive();
		setRegistered(false);
    	if(e.getCause() instanceof UnknownHostException){
    		return;
		}
		else{
			Status status = e.getStatus();
			/*
			 * The application is not registered
			 * Register again NOW.
			 */
			if(status.equals(Status.CLIENT_ERROR_FORBIDDEN)){
				scheduler.schedule(registrator, 0, TimeUnit.MICROSECONDS);
				return;
			}
		}
    }
    
	/**
	 * @return the registered
	 */
    synchronized public boolean isRegistered() {
		return registered;
	}

	synchronized public void register(String name){
		appName = name;
		mustRegister = true;
		scheduler.schedule(registrator, 0, TimeUnit.MICROSECONDS);
	}
	
	/*
	 * Register this app into the server.
	 */
	private Runnable registrator = new Runnable() {
		
		@Override
		public void run() {
			doRegister();
		}
	};
	
	synchronized private void doRegister(){
		Log.v(TAG, "doRegister");
		if(appName == null || !mustRegister || registered){
			return;
		}
		/*
		 * Register a new application
		 */
		AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);

		IdSecret myIdSecret = null;
		try{
			myIdSecret = appsResource.newApp(appName);
		}
		catch(ResourceException e){
			if(e.getCause() instanceof UnknownHostException){
				return;
			}
			e.printStackTrace();
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
		appResource = clientResource.getChild("/apps/"+app.getAppId(), AppResource.class);
		Log.v(TAG, "Register:Starting Keep Alive: time " +keepAliveTime);
		keepAliveMonitor = scheduler.scheduleAtFixedRate(
				sendKeepAlive, keepAliveTime/3, 
				keepAliveTime/3,
				TimeUnit.MILLISECONDS);
		for(GrcBoxRule rule: rulesCached){
			registerNewRule(rule);
		}
		setRegistered(true);
	}
	
	synchronized public void deregister(){
		setRegistered(false);
		try{
			appResource.rm();
			clientResource.release();
		}
		catch(ResourceException e){
			parseResourceException(e);
		}
		cancelKeepAlive();
	}
	
	synchronized private void setRegistered(boolean newValue){
		if(newValue != registered){
			registered = newValue;
			notifyRegisteredChanged();
		}
	}
	
	synchronized public void subscribeRegisteredChangedListener(OnRegisteredChangedListener listener){
		if(!regListener.contains(listener)){
			regListener.add(listener);
		}
	}
	
	synchronized public void unSubscribeRegisteredChangedListener(OnRegisteredChangedListener listener){
		regListener.remove(listener);
	}
	
	synchronized private void notifyRegisteredChanged() {
		for(OnRegisteredChangedListener listener: regListener){
			listener.onRegisteredChanged(registered);
		}
	}

	public interface OnRegisteredChangedListener{
		public abstract  void onRegisteredChanged(boolean newValue);
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	/*
	 * get a list of the available interfaces from the server
	 */
	public Collection<GrcBoxInterface> getInterfaces(){
		IfacesResource ifaces = clientResource.getChild("/ifaces", IfacesResource.class);
		Collection<GrcBoxInterface> list = new LinkedList<GrcBoxInterface>();
		try{
			list = ifaces.getList().getList();
		}
		catch(ResourceException e){
			parseResourceException(e);
		}
		return list;
	}
	
	/*
	 * get a list of the available interfaces from the server
	 */
	public Collection<GrcBoxApp> getApps(){
		AppsResource apps = clientResource.getChild("/apps", AppsResource.class);
		Collection<GrcBoxApp> list = new LinkedList<GrcBoxApp>();
		try{
			list = apps.getList().getList();
		}
		catch(ResourceException e){
			parseResourceException(e);
		}
		return list;
	}
	
	/*
	 * Get a list of my registered rules.
	 */
	public Collection<GrcBoxRule> getRules(){
		RulesResource rules = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
		Collection<GrcBoxRule> list = new LinkedList<GrcBoxRule>();
		try{
			list = rules.getList().getList();
		}
		catch(ResourceException e){
			
		}
		return list;
	}
	
	/*
	 * Register a new rule in the server.
	 * Low level method for compatibility with third party communication libraries.
	 * The application must remove the rule after communication have finished.
	 */
	public GrcBoxRule registerNewRule(GrcBoxRule rule){
		rulesCached.add(rule);
		try{
			RulesResource rulesRes = clientResource.getChild("/apps/"+app.getAppId()+"/rules", RulesResource.class);
			List<GrcBoxRule> list = rulesRes.newRule(rule).getList();
			rule = list.get(list.size()-1);
			return rule;
		}
		catch(ResourceException e ){
			parseResourceException(e);
		}
		return null;
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
		Collection<String> list = new LinkedList<String>();
		try{
			RootResource rootRes = clientResource.getChild("/", RootResource.class);
			list = rootRes.getGrcBoxStatus().getSupportedMulticastPlugins().getList(); 
		}
		catch(ResourceException e){
			parseResourceException(e);
		}
		return list; 
	}
	
	/*
	 * Register an incoming flow in the server using the provided information
	 * Return a GrcServerSocket ready to be used 
	 */
	public GrcBoxServerSocket createServerSocket(int port, GrcBoxInterface iface) throws IOException{
		GrcBoxRule rule = new GrcBoxRule(-1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, -1, port, null, iface.getAddress(), port, null);
		GrcBoxServerSocket grcSocket = null;
		try{
			registerNewRule(rule);
			ServerSocket socket = new ServerSocket(port);
			grcSocket = new GrcBoxServerSocket(this, rule, socket);
		}
		catch(ResourceException e){
			parseResourceException(e);
		}
		return grcSocket;
	}

	/*
	 * register an outgoing flow at the server using the destination addr and the destination port.
	 * Return a socket already connected.
	 */
	public GrcBoxSocket createSocket(InetAddress addr, int port, GrcBoxInterface iface) throws IOException{
		GrcBoxSocket grcSocket = null;
		Socket socket = new Socket(addr, port);
		try{
			GrcBoxRule rule = new GrcBoxRule(-1, es.upv.grc.grcbox.common.GrcBoxRule.Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, addr.getHostAddress(), null, port, null);
			registerNewRule(rule);
			grcSocket = new GrcBoxSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}

	/*
	 * Register an outgoing flow at the server using the destination address, 
	 * the destination port, and the local port.
	 */
	public GrcBoxSocket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(address, port, localAddr, localPort);
		GrcBoxSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, address.getHostAddress(), null, localPort, null);
			registerNewRule(rule);
			grcSocket = new GrcBoxSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}
	
	/*
	 * Register an outgoing flow at the server using the destination host.
	 */
	public GrcBoxSocket createSocket(String host, int port, GrcBoxInterface iface) throws UnknownHostException, IOException{
		Socket socket = new Socket(host, port);
		GrcBoxSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, socket.getInetAddress().getHostAddress(), host, port, host);
			registerNewRule(rule);
			grcSocket = new GrcBoxSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}

	public GrcBoxSocket createSocket(String host, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface) throws IOException{
		Socket socket = new Socket(host, port, localAddr, localPort);
		GrcBoxSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.TCP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), port, socket.getInetAddress().getHostAddress(), host, localPort, host);
			registerNewRule(rule);
			grcSocket = new GrcBoxSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}

	public GrcBoxDatagramSocket createDatagramSocket(GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket();
		GrcBoxDatagramSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null, null, 0, null);
			registerNewRule(rule);
			grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}

	public GrcBoxDatagramSocket createDatagramSocket(int port, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxDatagramSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), -1, null, null, port, null);
			registerNewRule(rule);
			grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}
	
	/*
	 *	Create a new grcBoxDatagram Socket bounded to the local port.
	 *	The traffic flow registered on the server is restricted to the specifics remote address
	 *	and remote port. 
	 */
	public GrcBoxDatagramSocket createDatagramSocket(int port, InetAddress remoteAddr, int remotePort, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxDatagramSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remotePort, remoteAddr.getHostAddress(), null, remotePort, null);
			registerNewRule(rule);
			grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
		return grcSocket;
	}

	/*
	 *	Create a new grcBoxDatagram Socket bounded to the local port.
	 *	The traffic flow registered on the server is restricted to the specifics remote address
	 *	and remote port. 
	 */
	public GrcBoxDatagramSocket createDatagramSocket(int port, InetSocketAddress remoteHost, GrcBoxInterface iface) throws SocketException{
		DatagramSocket socket = new DatagramSocket(port);
		GrcBoxDatagramSocket grcSocket = null;
		try{
			GrcBoxRule rule = new GrcBoxRule( -1, Protocol.UDP, null, app.getAppId(), iface.getName(), 0, socket.getLocalPort(), remoteHost.getPort(), remoteHost.getAddress().getHostAddress(), null, port, null);
			registerNewRule(rule);
			grcSocket = new GrcBoxDatagramSocket(this, rule, socket);
		}
		catch(ResourceException e){
			socket.close();
			parseResourceException(e);
		}
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
