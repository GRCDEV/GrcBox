package es.upv.grc.grcbox.androlib;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.DatagramChannel;
import java.util.List;

import org.restlet.resource.ClientResource;

import es.upv.grc.grcbox.common.*;

public class GrcBoxClient {
	private static String SERVER_URL = "http://grcbox:8080";
	
	ClientResource clientResource;
	GrcBoxApp app;
	boolean registered;
	
	/*
	 * Check if the GrcBox Server is available
	 */
	boolean isServerAvailable(){
		
	}
	
	boolean register(String appName){
		
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
