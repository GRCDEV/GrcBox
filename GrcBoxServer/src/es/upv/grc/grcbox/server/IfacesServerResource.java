/**
 * 
 */
package es.upv.grc.grcbox.server;

import java.util.List;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.IfacesResource;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManagerThreadNotRunning;

/**
 * @author sertinell
 *
 */
public class IfacesServerResource extends ServerResource implements IfacesResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.IfacesResource#getList()
	 */
	@Override
	public GrcBoxInterfaceList getList() {
		try {
			GrcBoxInterfaceList list = new GrcBoxInterfaceList();
			list.setList(GrcBoxServerApplication.getDb().getOuterInterfaces());
			return list;
		} catch (NetworkInterfaceManagerThreadNotRunning e) {
			throw new ResourceException(500);
		}
	}
}
