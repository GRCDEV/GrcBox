package es.upv.grc.grcbox.server.networkInterfaces;

import es.upv.grc.grcbox.common.GrcBoxInterface;

public interface NetworkManagerListener {
	public void interfaceRemoved(GrcBoxInterface iface);
	public void interfaceAdded(GrcBoxInterface iface);
	public void interfaceChanged(GrcBoxInterface iface);
}
