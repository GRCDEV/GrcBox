package es.upv.grc.grcbox.common;

import java.util.Collection;

/**
 * The Class StringList, this class is only used for encapsulation
 */
public class StringList {
	
	/** The list. */
	Collection<String> list;
	
	/**
	 * Instantiates a new string list.
	 */
	public StringList(){
		
	};
	
	/**
	 * Instantiates a new string list.
	 *
	 * @param list the list
	 */
	public StringList(Collection<String> list) {
		super();
		this.list = list;
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public Collection<String> getList() {
		return list;
	}
}
