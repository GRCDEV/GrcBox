package es.upv.grc.grcbox.common;

import java.util.Collection;

/**
 * The Class GrcBoxInterfaceList.
 * This list is used only for jackson serialising purposes
 */
public class GrcBoxInterfaceList {
	
	/** The list. */
	Collection<GrcBoxInterface> list;

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public Collection<GrcBoxInterface> getList() {
		return list;
	}

	/**
	 * Sets the list.
	 *
	 * @param list the new list
	 */
	public void setList(Collection<GrcBoxInterface> list) {
		this.list = list;
	}
}
