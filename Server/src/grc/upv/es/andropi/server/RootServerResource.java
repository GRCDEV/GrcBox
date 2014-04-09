package grc.upv.es.andropi.server;

import org.restlet.resource.ServerResource;

import grc.upv.es.andropi.common.RootResource;

public class RootServerResource extends ServerResource implements RootResource{

	public String represent(){
		return "Welcome to the " + getApplication().getName() + " !";
	}
}
