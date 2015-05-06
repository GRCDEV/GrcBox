package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxSsid;
import es.upv.grc.grcbox.common.resources.SsidResource;

public class SsidServerResource extends ServerResource implements SsidResource {

	@Override
	public GrcBoxSsid retrieve() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect(String password, boolean autoConnect) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
}
