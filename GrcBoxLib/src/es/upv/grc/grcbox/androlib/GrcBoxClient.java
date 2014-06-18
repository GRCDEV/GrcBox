package es.upv.grc.grcbox.androlib;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.DatagramChannel;
import java.util.List;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.resource.*;
import org.restlet.util.Series;

import es.upv.grc.grcbox.common.*;
import es.upv.grc.grcbox.common.AppsResource.IdSecret;

public class GrcBoxClient {
	private static String SERVER_URL = "http://grcbox:8080";
	
	ClientResource clientResource;
	AppResource appResource = null;
	GrcBoxApp app;
	long updatePeriod;
	boolean registered;
	
	
	
	
	public GrcBoxClient() {
		super();
		clientResource = new ClientResource("http://localhost:8080");
	}

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
    	Client client = new Client(new Context(), Protocol.HTTPS);
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
        
        registered = true;
        return true;
	}
	
	/*
	 * get a list of the available interfaces from the server
	 */
	List<GrcBoxInterface> getInterfaces(){
		
	}
	
	/*
	 * Register a flow in the server using the provided information
	 * Return a ServerSocket ready to be used 
	 */
	ServerSocket createServerSocket(int port, GrcBoxInterface iface){
		
	}
	
	Socket createSocket(InetAddress addr, int port, GrcBoxInterface iface){
		
	}
	
	Socket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface){
		
	}
	
	Socket createSocket(String host, int port, GrcBoxInterface iface){
		
	}
	
 	Socket createSocket(String host, int port, InetAddress localAddr, int localPort, GrcBoxInterface iface){
 		
 	}
 	
 	DatagramSocket createDatagramSocket(int port, GrcBoxInterface iface){
 		
 	}
 	
 	DatagramSocket createDatagramSocket(int port, InetAddress laddr, GrcBoxInterface iface){
 		
 	}
 	
 	DatagramSocket createDatagramSocket(SocketAddress bindaddr, GrcBoxInterface iface){
 		
 	}
}
