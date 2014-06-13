package es.upv.grc.grcbox.common;

public class GrcBoxApp {
	private int appId;
	private int secret;
	private String name;
	private long lastKeepAlive;
		
	
	public GrcBoxApp() {
	}
	
	public GrcBoxApp(int appId, String name, long lastKeepAlive) {
		super();
		this.appId = appId;
		this.name = name;
		this.lastKeepAlive = lastKeepAlive;
	}
	
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public int getSecret() {
		return secret;
	}
	public void setSecret(int secret) {
		this.secret = secret;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getLastKeepAlive() {
		return lastKeepAlive;
	}
	public void setLastKeepAlive(long lastKeepAlive) {
		this.lastKeepAlive = lastKeepAlive;
	}
}
