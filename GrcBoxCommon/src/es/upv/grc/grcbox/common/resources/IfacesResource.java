package es.upv.grc.grcbox.common.resources;


import org.restlet.resource.Get;

import es.upv.grc.grcbox.common.GrcBoxInterfaceList;

/**
 * The Interface IfacesResource.
 */
public interface IfacesResource {
    /**
	 * Gets the list of interfaces in a GRCBox
	 *
	 * @return the list
	 */
	@Get("json")
    public GrcBoxInterfaceList getList();
}
