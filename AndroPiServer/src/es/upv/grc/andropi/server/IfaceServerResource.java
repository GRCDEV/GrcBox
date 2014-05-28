package es.upv.grc.andropi.server;

import org.restlet.resource.ServerResource;

import es.upv.grc.andropi.common.AndroPiInterface;
import es.upv.grc.andropi.common.IfaceResource;

public class IfaceServerResource extends ServerResource implements IfaceResource {

	@Override
	public AndroPiInterface retrieve() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean modify(AndroPiInterface iface) {
		// TODO Auto-generated method stub
		return false;
	}

}
