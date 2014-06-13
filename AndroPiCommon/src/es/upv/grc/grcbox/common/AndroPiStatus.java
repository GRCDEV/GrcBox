package es.upv.grc.grcbox.common;

import java.util.ArrayList;
import java.util.List;


public class AndroPiStatus {
	String name;
	List<AndroPiInterface> interfaces;
	List<AndroPiRule> rules;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AndroPiStatus() {
		super();
		this.interfaces = new ArrayList<>(); 
		this.rules = new ArrayList<>();
	}

	public List<AndroPiInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<AndroPiInterface> ifList) {
		this.interfaces = ifList;
	}
	
	public List<AndroPiRule> getFlows() {
		return rules;
	}

	public void setFlows(List<AndroPiRule> flows) {
		this.rules = flows;
	}

	public void addInterface(AndroPiInterface iface){
		interfaces.add(iface);
	}
	
	public void addFlow(AndroPiRule flow){
		rules.add(flow);
	}
}
