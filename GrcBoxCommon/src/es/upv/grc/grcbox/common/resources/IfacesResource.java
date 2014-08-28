package es.upv.grc.grcbox.common.resources;


import org.restlet.resource.Get;

import es.upv.grc.grcbox.common.GrcBoxInterfaceList;

public interface IfacesResource {
	/*
	 * return a list of rules associated to an app
	 */
    @Get("json")
    public GrcBoxInterfaceList getList();
}
