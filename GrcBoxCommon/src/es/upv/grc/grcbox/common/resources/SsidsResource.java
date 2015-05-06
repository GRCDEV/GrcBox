package es.upv.grc.grcbox.common.resources;


import org.restlet.resource.Get;

import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.GrcBoxSsid;
import es.upv.grc.grcbox.common.GrcBoxSsidList;

public interface SsidsResource{
	/*
	 * return a list of rules associated to an app
	 */
    @Get("json")
    public GrcBoxSsidList getList();
}
