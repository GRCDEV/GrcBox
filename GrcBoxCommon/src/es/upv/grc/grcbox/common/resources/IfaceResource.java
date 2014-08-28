package es.upv.grc.grcbox.common.resources;

import org.restlet.resource.Get;
import org.restlet.resource.Put;

import es.upv.grc.grcbox.common.GrcBoxInterface;

public interface IfaceResource {
	/*
	 * Return iface information
	 */
    @Get("json")
    public GrcBoxInterface retrieve();
}
