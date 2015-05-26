package es.upv.grc.grcbox.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxApp extends GrcBoxAppInfo {
	private int secret;
	private long lastKeepAlive;
		
	
	public GrcBoxApp() {
	}
	
	public GrcBoxApp(int id, String name, long lastUpdate) {
		super(id,name,-1);
	}

	@JsonIgnore
	public int getSecret() {
		return secret;
	}
	@JsonIgnore
	public void setSecret(int secret) {
		this.secret = secret;
	}
	@JsonIgnore
	public long getLastKeepAlive() {
		return lastKeepAlive;
	}
	@JsonIgnore
	public void setLastKeepAlive(long lastKeepAlive) {
		this.lastKeepAlive = lastKeepAlive;
	}
}
