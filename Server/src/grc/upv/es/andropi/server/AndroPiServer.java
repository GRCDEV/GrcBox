package grc.upv.es.andropi.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class AndroPiServer extends Application {

	public AndroPiServer() {
		setName("RESTful AndroPi Server");
		setDescription("AndroPiServer to enable Android ad-hoc communication");
		setOwner("grc.upv.es");
		setAuthor("Sergio Martínez Tornell");	
	}

	@Override
	public Restlet createInboundRoot(){
		Router router = new Router(getContext());
		router.attach("/", RootServerResource.class);
		router.attach("/accounts/", AccountsServerResource.class);
		router.attach("/accounts/{accountId}", AccountServerResource.class);
		return router;
	}
}
