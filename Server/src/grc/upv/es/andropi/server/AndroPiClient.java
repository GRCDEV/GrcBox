package grc.upv.es.andropi.server;

import java.io.IOException;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class AndroPiClient extends ClientResource {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ResourceException 
	 */
	public static void main(String[] args) throws ResourceException, IOException {
		ClientResource mailRoot =
				new ClientResource("http://localhost:8111/");
		mailRoot.get().write(System.out);
	}

}
