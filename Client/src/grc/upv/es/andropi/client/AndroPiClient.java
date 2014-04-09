package grc.upv.es.andropi.client;

import grc.upv.es.andropi.common.RootResource;

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
		RootResource mailRoot = ClientResource.create("http://localhost:8111/", RootResource.class);
		String result = mailRoot.represent();
		System.out.println(result);
	}
}
