package es.upv.grc.grcbox.server.resources;

import org.freedesktop.dbus.exceptions.DBusException;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.ApAuth;
import es.upv.grc.grcbox.common.GrcBoxSsid;
import es.upv.grc.grcbox.common.resources.SsidResource;
import es.upv.grc.grcbox.server.RulesDB;

public class SsidServerResource extends ServerResource implements SsidResource {

	@Override
	public GrcBoxSsid retrieve() {
		String iface = getAttribute("ifaceName");
		String ssid = getAttribute("ssid");
		return RulesDB.getApInIface(ssid, iface);
	}

	@Override
	public void connect(ApAuth authInfo) {
		String iface = getAttribute("ifaceName");
		String ssid = getAttribute("ssid");
		try {
			RulesDB.connect(authInfo.getPassword(), authInfo.isAutoconnect(), iface, ssid);
		} catch (DBusException e) {
			throw new ResourceException(503);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
}
