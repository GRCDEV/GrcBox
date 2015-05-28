package es.upv.grc.grcbox.server.networkInterfaces;

import es.upv.grc.grcbox.common.GrcBoxInterface;

/**
 * The listener interface for receiving networkManager events.
 * The class that is interested in processing a networkManager
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addNetworkManagerListener<code> method. When
 * the networkManager event occurs, that object's appropriate
 * method is invoked.
 *
 * @see NetworkManagerEvent
 */
public interface NetworkManagerListener {
	
	/**
	 * Interface removed.
	 *
	 * @param iface the iface
	 */
	public void interfaceRemoved(GrcBoxInterface iface);
	
	/**
	 * Interface added.
	 *
	 * @param iface the iface
	 */
	public void interfaceAdded(GrcBoxInterface iface);
	
	/**
	 * Interface changed.
	 *
	 * @param iface the iface
	 */
	public void interfaceChanged(GrcBoxInterface iface);
}
