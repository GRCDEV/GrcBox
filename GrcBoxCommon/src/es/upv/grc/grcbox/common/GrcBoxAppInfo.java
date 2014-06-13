package es.upv.grc.grcbox.common;

public class GrcBoxAppInfo {
	private int appId;
	private String name;
	private long keepAlivePeriod;
	
	public GrcBoxAppInfo() {
	}
	
	public GrcBoxAppInfo(int appId, String name, long keepAlivePeriod) {
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
