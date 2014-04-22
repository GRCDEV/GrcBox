package es.upv.grc.andropi.common;


import java.util.ArrayList;
import java.util.List;


public class AddressesList{
	private final List<String> ipAdresses;

    public AddressesList() {
    	this.ipAdresses = new ArrayList<>();
	}
	
	public AddressesList(List<String> ipAdresses) {
		super();
		this.ipAdresses = ipAdresses;
	}

	public List<String> getIpAdresses() {
		return ipAdresses;
	}
}