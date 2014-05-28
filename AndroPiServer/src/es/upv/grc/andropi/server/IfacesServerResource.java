/**
 * 
 */
package es.upv.grc.andropi.server;

import java.util.List;

import org.restlet.resource.ServerResource;

import es.upv.grc.andropi.common.AndroPiInterface;
import es.upv.grc.andropi.common.IfacesResource;

/**
 * @author sertinell
 *
 */
public class IfacesServerResource extends ServerResource implements IfacesResource {

	/* (non-Javadoc)
	 * @see es.upv.grc.andropi.common.IfacesResource#getList()
	 */
	@Override
	public List<AndroPiInterface> getList() {
		// TODO Auto-generated method stub
		return null;
	}

}
