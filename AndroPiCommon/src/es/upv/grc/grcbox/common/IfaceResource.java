package es.upv.grc.grcbox.common;

import org.restlet.resource.Get;
import org.restlet.resource.Put;

public interface IfaceResource {
	/*
	 * Return rule information
	 */
    @Get
    public AndroPiInterface retrieve();
    
    /*
     * Modify rule
     */
    @Put
    public boolean modify(AndroPiInterface iface);
}
