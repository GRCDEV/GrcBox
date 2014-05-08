package es.upv.grc.andropi.common;

public class AndroPiAppInfo {
	private int appId;
	private String name;
	
	public AndroPiAppInfo() {
	}
	
	public AndroPiAppInfo(int appId, String name) {
		super();
		this.appId = appId;
		this.name = name;
	}
	
	public int getAppId() {
		return appId;
	}
	
	public String getName() {
		return name;
	}
}
