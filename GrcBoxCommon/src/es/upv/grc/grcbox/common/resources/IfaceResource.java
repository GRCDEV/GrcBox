package es.upv.grc.grcbox.common.resources;

import org.restlet.resource.Get;

import es.upv.grc.grcbox.common.GrcBoxInterface;

/**
 * This resource return information about the status of the interface
 */
public interface IfaceResource {
    /**
	 * Retrieve.
	 *
	 * @return The information about the interface
	 */
	@Get("json")
    public GrcBoxInterface retrieve();
}
