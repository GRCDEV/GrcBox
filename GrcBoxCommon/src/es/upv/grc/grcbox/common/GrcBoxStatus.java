package es.upv.grc.grcbox.common;

import java.util.ArrayList;
import java.util.List;


public class GrcBoxStatus {
	String name;
	List<GrcBoxInterface> interfaces;
	List<GrcBoxRule> rules;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GrcBoxStatus() {
		super();
		this.interfaces = new ArrayList<>(); 
		this.rules = new ArrayList<>();
	}

	public List<GrcBoxInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<GrcBoxInterface> ifList) {
		this.interfaces = ifList;
	}
	
	public List<GrcBoxRule> getFlows() {
		return rules;
	}

	public void setFlows(List<GrcBoxRule> flows) {
		this.rules = flows;
	}

	public void addInterface(GrcBoxInterface iface){
		interfaces.add(iface);
	}
	
	public void addFlow(GrcBoxRule flow){
		rules.add(flow);
	}
}
