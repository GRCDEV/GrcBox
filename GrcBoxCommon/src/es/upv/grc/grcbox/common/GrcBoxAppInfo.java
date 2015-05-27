package es.upv.grc.grcbox.common;

/**
 * The Class GrcBoxAppInfo.
 * This class is used to send App information between server and client
 */
public class GrcBoxAppInfo {
	
	/** The app id. */
	private int appId;
	
	/** The name. */
	private String name;
	
	/** The keep alive period. 
	 *  It is set by the server when filling a request to inform the 
	 *  recommended maximum time for keep alive messages
	 * */
	private long keepAlivePeriod;
	
	/**
	 * Instantiates a new grc box app info.
	 */
	public GrcBoxAppInfo() {
	}
	
	/**
	 * Instantiates a new grc box app info.
	 *
	 * @param appId the app id
	 * @param name the name
	 * @param keepAlivePeriod the keep alive period
	 */
	public GrcBoxAppInfo(int appId, String name, long keepAlivePeriod) {
		super();
		this.appId = appId;
		this.name = name;
		this.keepAlivePeriod = keepAlivePeriod;
	}
	
	/**
	 * Gets the app id.
	 *
	 * @return the app id
	 */
	public int getAppId() {
		return appId;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Gets the keep alive period.
	 *
	 * @return the keep alive period
	 */
	public long getKeepAlivePeriod() {
		return keepAlivePeriod;
	}
}
