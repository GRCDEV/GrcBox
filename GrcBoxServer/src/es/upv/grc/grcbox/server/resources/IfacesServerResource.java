/**
 * 
 */
package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.resources.IfacesResource;
import es.upv.grc.grcbox.server.RulesDB;

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
		GrcBoxInterfaceList list = new GrcBoxInterfaceList();
		list.setList(RulesDB.getOutInterfaces());
		return list;
	}
}
