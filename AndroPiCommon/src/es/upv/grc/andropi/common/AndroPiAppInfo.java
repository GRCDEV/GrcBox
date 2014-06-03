package es.upv.grc.andropi.common;

public class AndroPiAppInfo {
	private int appId;
	private String name;
	private long keepAlivePeriod;
	
	public AndroPiAppInfo() {
	}
	
	public AndroPiAppInfo(int appId, String name, long keepAlivePeriod) {
		super();
		this.appId = appId;
		this.name = name;
		this.keepAlivePeriod = keepAlivePeriod;
	}
	
	public int getAppId() {
		return appId;
	}
	
	public String getName() {
		return name;
	}

	public long getKeepAlivePeriod() {
		return keepAlivePeriod;
	}
}
