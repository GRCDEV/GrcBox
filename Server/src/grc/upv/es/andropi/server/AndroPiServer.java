package grc.upv.es.andropi.server;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;

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
		return new Restlet(){
			@Override
			public void handle(Request request, Response response) {
				String entity = "Method : " + request.getMethod()
						+ "\nResource URI : "
						+ request.getResourceRef()
						+ "\nIP address : "
						+ request.getClientInfo().getAddress()
						+ "\nAgent name : "
						+ request.getClientInfo().getAgentName()
						+ "\nAgent version: "
						+ request.getClientInfo().getAgentVersion();
				response.setEntity(entity, MediaType.TEXT_PLAIN);
			}
		};
	}
}
