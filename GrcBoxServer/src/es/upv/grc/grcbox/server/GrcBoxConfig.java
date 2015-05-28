package es.upv.grc.grcbox.server;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class GrcBoxConfig.
 * This simple class is used only to parse the json configuration file from
 * config.json
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxConfig {
	
	/** The inner interfaces. */
	private LinkedList<String> innerInterfaces = new LinkedList<>();
	
	/** The outer interfaces. */
	private LinkedList<String> outerInterfaces = new LinkedList<>();
	
	/** The debug. */
	private boolean debug;
	
	/** The keep alive time. */
	private long keepAliveTime;

	/**
	 * Checks if is debug.
	 *
	 * @return true, if is debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Sets the debug.
	 *
	 * @param debug the new debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	
	/**
	 * Adds the in interface.
	 *
	 * @param name the name
	 */
	public  void addInInterface(String name){
		innerInterfaces.add(name);
	}
	
	/**
	 * Adds the out interface.
	 *
	 * @param name the name
	 */
	public  void addOutInterface(String name){
		outerInterfaces.add(name);
	}

	
	/**
	 * Gets the keep alive time.
	 *
	 * @return the keep alive time
	 */
	public  long getKeepAliveTime() {
		return keepAliveTime;
	}

	/**
	 * Sets the keep alive time.
	 *
	 * @param keepAliveTime the new keep alive time
	 */
	public  void setKeepAliveTime(final long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	/**
	 * Gets the inner interfaces.
	 *
	 * @return the inner interfaces
	 */
	public  LinkedList<String> getInnerInterfaces() {
		return (LinkedList<String>) innerInterfaces.clone();
	}

	/**
	 * Sets the inner interfaces.
	 *
	 * @param innerInterfaces the new inner interfaces
	 */
	public  void setInnerInterfaces(final LinkedList<String> innerInterfaces) {
		this.innerInterfaces = (LinkedList<String>) innerInterfaces.clone();
	}

	/**
	 * Gets the outer interfaces.
	 *
	 * @return the outer interfaces
	 */
	public  LinkedList<String> getOuterInterfaces() {
		return (LinkedList<String>) outerInterfaces.clone();
	}

	/**
	 * Sets the outer interfaces.
	 *
	 * @param outerInterfaces the new outer interfaces
	 */
	public  void setOuterInterfaces(final LinkedList<String> outerInterfaces) {
		this.outerInterfaces = (LinkedList<String>) outerInterfaces.clone();
	}
}
