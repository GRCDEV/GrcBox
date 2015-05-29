package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.resources.IfaceResource;
import es.upv.grc.grcbox.server.rulesdb.RulesDB;

/**
 * The Class IfaceServerResource.
 */
public class IfaceServerResource extends ServerResource implements IfaceResource {
	private String ifaceName;
	/* (non-Javadoc)
	 * @see es.upv.grc.grcbox.common.resources.IfaceResource#retrieve()
	 */
	@Override
	public GrcBoxInterface retrieve() {
		GrcBoxInterface iface = RulesDB.getIface(ifaceName);
		if(iface == null){
			throw new ResourceException(404);
		}
		return iface;
	}

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		ifaceName = getAttribute("ifaceName");
	}
}
