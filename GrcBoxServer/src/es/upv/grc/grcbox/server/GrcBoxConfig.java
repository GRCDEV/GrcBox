package es.upv.grc.grcbox.server;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonTypeInfo;



@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxConfig {
	private LinkedList<String> innerInterfaces = new LinkedList<>();
	private LinkedList<String> outerInterfaces = new LinkedList<>();
	private boolean debug;
	private long keepAliveTime;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	
	public  void addInInterface(String name){
		innerInterfaces.add(name);
	}
	
	public  void addOutInterface(String name){
		outerInterfaces.add(name);
	}

	
	public  long getKeepAliveTime() {
		return keepAliveTime;
	}

	public  void setKeepAliveTime(final long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public  LinkedList<String> getInnerInterfaces() {
		return (LinkedList<String>) innerInterfaces.clone();
	}

	public  void setInnerInterfaces(final LinkedList<String> innerInterfaces) {
		this.innerInterfaces = (LinkedList<String>) innerInterfaces.clone();
	}

	public  LinkedList<String> getOuterInterfaces() {
		return (LinkedList<String>) outerInterfaces.clone();
	}

	public  void setOuterInterfaces(final LinkedList<String> outerInterfaces) {
		this.outerInterfaces = (LinkedList<String>) outerInterfaces.clone();
	}
}
