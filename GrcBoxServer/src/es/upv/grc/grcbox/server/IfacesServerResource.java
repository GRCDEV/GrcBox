/**
 * 
 */
package es.upv.grc.grcbox.server;

import java.util.List;

import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.IfacesResource;

/**
 * @author sertinell
 *
 */
public class IfacesServerResource extends ServerResource implements IfacesResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.IfacesResource#getList()
	 */
	@Override
	public List<GrcBoxInterface> getList() {
		// TODO Auto-generated method stub
		return null;
	}

}
