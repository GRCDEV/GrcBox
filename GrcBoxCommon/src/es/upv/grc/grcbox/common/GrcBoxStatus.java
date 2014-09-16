package es.upv.grc.grcbox.common;

public class GrcBoxStatus {
	String name;
	StringList supportedMulticastPlugins;
	int numIfaces;
	int numApps;
	int numRules;
	
	public GrcBoxStatus(){
	}
	public GrcBoxStatus(String name, int numIfaces, int numApps, int numRules, StringList list) {
		super();
		this.name = name;
		this.numIfaces = numIfaces;
		this.numApps = numApps;
		this.numRules = numRules;
		this.supportedMulticastPlugins = list;
	}
	
	public String getName() {
		return name;
	}
	public int getNumIfaces() {
		return numIfaces;
	}
	public int getNumApps() {
		return numApps;
	}
	public int getNumRules() {
		return numRules;
	}
	public StringList getSupportedMulticastPlugins() {
		return supportedMulticastPlugins;
	}
}
