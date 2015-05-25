package es.upv.grc.grcbox.common.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import es.upv.grc.grcbox.common.ApAuth;
import es.upv.grc.grcbox.common.GrcBoxSsid;

public interface SsidResource {
	/*
	 * return SSID information
	 */
	@Get("json")
	public  GrcBoxSsid retrieve();
	
	/*
	 * Connect to the AP pointed by the resource
	 */
	@Post("json")
	public void connect(ApAuth authInfo);
	
	/*
	 * If a connection is associated to this AP, remove it
	 */
	@Delete("json")
	public void remove();

}
