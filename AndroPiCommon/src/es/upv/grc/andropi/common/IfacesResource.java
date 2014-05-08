package es.upv.grc.andropi.common;

import java.util.List;

import org.restlet.resource.Get;

public interface IfacesResource {
	/*
	 * return a list of rules associated to an app
	 */
    @Get
    public List<AndroPiInterface> getList();
}
