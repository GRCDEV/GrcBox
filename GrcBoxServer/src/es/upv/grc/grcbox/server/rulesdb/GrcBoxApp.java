package es.upv.grc.grcbox.server.rulesdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import es.upv.grc.grcbox.common.GrcBoxAppInfo;
/**
 * This class summarises the information about a GrcBoxApp.
 * It should never be instantiated outside the server
  */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxApp extends GrcBoxAppInfo {
	
	/** The secret use to authenticate the app. */
	private int secret;
	
	/** The last keep alive timestamp. */
	private long lastKeepAlive;
		
	
	/**
	 * Instantiates a new grc box app.
	 */
	public GrcBoxApp() {
	}
	
	/**
	 * Instantiates a new grc box app.
	 *
	 * @param id the id of the app.
	 * @param name the name
	 * @param lastUpdate the last update timestamp
	 */
	public GrcBoxApp(int id, String name, long lastUpdate) {
		super(id,name,-1);
		lastKeepAlive = lastUpdate;
	}

	/**
	 * Gets the secret.
	 *
	 * @return the secret
	 */
	@JsonIgnore
	public int getSecret() {
		return secret;
	}
	
	/**
	 * Sets the secret.
	 *
	 * @param secret the new secret
	 */
	@JsonIgnore
	public void setSecret(int secret) {
		this.secret = secret;
	}
	
	/**
	 * Gets the last keep alive.
	 *
	 * @return the last keep alive
	 */
	@JsonIgnore
	public long getLastKeepAlive() {
		return lastKeepAlive;
	}
	
	/**
	 * Sets the last keep alive.
	 *
	 * @param lastKeepAlive the new last keep alive
	 */
	@JsonIgnore
	public void setLastKeepAlive(long lastKeepAlive) {
		this.lastKeepAlive = lastKeepAlive;
	}
}
