package es.upv.grc.grcbox.server.resources;

import org.restlet.resource.ServerResource;

import es.upv.grc.grcbox.common.GrcBoxSsidList;
import es.upv.grc.grcbox.common.resources.SsidsResource;
import es.upv.grc.grcbox.server.GrcBoxServerApplication;
import es.upv.grc.grcbox.server.RulesDB;

public class SsidsServerResource extends ServerResource implements SsidsResource{

	@Override
	public GrcBoxSsidList getList() {
		String iface = getAttribute("ifaceName");
		GrcBoxSsidList list = new GrcBoxSsidList();
		list.setList(RulesDB.getAps(iface));
		return list;
	}
}
