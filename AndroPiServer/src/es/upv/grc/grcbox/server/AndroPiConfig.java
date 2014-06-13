package es.upv.grc.grcbox.server;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import es.upv.grc.grcbox.common.AndroPiInterface.Type;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class AndroPiConfig {
	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	static class ConfigInterface{
		boolean internal;
		String name;
		double cost;
		Type type;
		
		public boolean isInternal() {
			return internal;
		}
		public void setInternal(boolean internal) {
			this.internal = internal;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public double getCost() {
			return cost;
		}
		public void setCost(double cost) {
			this.cost = cost;
		}
		public Type getType() {
			return type;
		}
		public void setType(Type type) {
			this.type = type;
		}
	}

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	static class ConfigDatabase{
		private String path;
		private boolean flushAtStartup;
		private long updateTime;
		
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public long getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}
		public boolean isFlushAtStartup() {
			return flushAtStartup;
		}
		public void setFlushAtStartup(boolean flushAtStartup) {
			this.flushAtStartup = flushAtStartup;
		}
	}
	
	private boolean debug;
	private List<ConfigInterface> inInterfaces = new ArrayList<>();
	private List<ConfigInterface> outInterfaces = new ArrayList<>();
	private ConfigDatabase database;
	
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public List<ConfigInterface> getInInterfaces() {
		return inInterfaces;
	}
	public void setInInterfaces(List<ConfigInterface> inInterfaces) {
		this.inInterfaces = inInterfaces;
	}
	public List<ConfigInterface> getOutInterfaces() {
		return outInterfaces;
	}
	public void setOutInterfaces(List<ConfigInterface> outInterfaces) {
		this.outInterfaces = outInterfaces;
	}
	public ConfigDatabase getDatabase() {
		return database;
	}
	public void setDatabase(ConfigDatabase database) {
		this.database = database;
	}
}
