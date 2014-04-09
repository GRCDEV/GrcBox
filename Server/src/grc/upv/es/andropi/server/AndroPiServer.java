package grc.upv.es.andropi.server;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class AndroPiServer extends Application {

	public AndroPiServer() {
		setName("RESTful AndroPi Server");
		setDescription("AndroPiServer to enable Android ad-hoc communication");
		setOwner("grc.upv.es");
		setAuthor("Sergio Martínez Tornell");	
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Server andropiServer = new Server(Protocol.HTTP, 8111);
		andropiServer.setNext(new AndroPiServer());
		andropiServer.start();
	}

	@Override
	public Restlet createInboundRoot(){
		Tracer tracer = new Tracer (getContext());
		
		Blocker blocker = new Blocker (getContext());
		blocker.getBlockedAddresses().add("0:0:0:0:0:0:0:1");
		blocker.setNext(tracer);
		
		Router router = new Router(getContext());
		router.attach("http://localhost:8111/", tracer);
		router.attach("http://localhost:8111/accounts/", tracer);
		router.attach("http://localhost:8111/accounts/{accountId}", blocker);
		return router;
	}
}
