package es.upv.grc.grcbox.common;

/**
 * The Class GrcBoxStatus.
 * This class represents the status of the server
 */
public class GrcBoxStatus {
	
	/** The name. */
	String name;
	
	/** The supported multicast plugins. 
	 * This is the most important member of this class and should be checked
	 * by any client before requesting a multicast rule
	 * */
	StringList supportedMulticastPlugins;
	
	/** The number of interfaces. */
	int numIfaces;
	
	/** The number of apps currently registered. */
	int numApps;
	
	/** The number rules currently registered. */
	int numRules;
	
	/**
	 * Instantiates a new grc box status.
	 */
	public GrcBoxStatus(){
	}
	
	/**
	 * Instantiates a new grc box status.
	 *
	 * @param name the name
	 * @param numIfaces the num ifaces
	 * @param numApps the num apps
	 * @param numRules the num rules
	 * @param list the list
	 */
	public GrcBoxStatus(String name, int numIfaces, int numApps, int numRules, StringList list) {
		super();
		this.name = name;
		this.numIfaces = numIfaces;
		this.numApps = numApps;
		this.numRules = numRules;
		this.supportedMulticastPlugins = list;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the num ifaces.
	 *
	 * @return the num ifaces
	 */
	public int getNumIfaces() {
		return numIfaces;
	}
	
	/**
	 * Gets the num apps.
	 *
	 * @return the num apps
	 */
	public int getNumApps() {
		return numApps;
	}
	
	/**
	 * Gets the num rules.
	 *
	 * @return the num rules
	 */
	public int getNumRules() {
		return numRules;
	}
	
	/**
	 * Gets the supported multicast plugins.
	 *
	 * @return the supported multicast plugins
	 */
	public StringList getSupportedMulticastPlugins() {
		return supportedMulticastPlugins;
	}
}
