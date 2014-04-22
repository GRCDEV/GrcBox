package es.upv.grc.andropi.common;

import java.util.ArrayList;
import java.util.List;


public class AndroPiStatus {
	String name;
	List<AndroPiInterface> interfaces;
	List<AndroPiFlow> flows;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AndroPiStatus() {
		super();
		this.interfaces = new ArrayList<>(); 
		this.flows = new ArrayList<>();
	}

	public List<AndroPiInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<AndroPiInterface> ifList) {
		this.interfaces = ifList;
	}
	
	public List<AndroPiFlow> getFlows() {
		return flows;
	}

	public void setFlows(List<AndroPiFlow> flows) {
		this.flows = flows;
	}

	public void addInterface(AndroPiInterface iface){
		interfaces.add(iface);
	}
	
	public void addFlow(AndroPiFlow flow){
		flows.add(flow);
	}
}
