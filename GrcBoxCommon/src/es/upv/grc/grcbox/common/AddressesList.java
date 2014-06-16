package es.upv.grc.grcbox.common;


import java.util.ArrayList;
import java.util.List;


public class AddressesList{
	private final List<byte[]> ipAdresses;

    public AddressesList() {
    	this.ipAdresses = new ArrayList<>();
	}
	
	public AddressesList(List<byte[]> ipAdresses){
		super();
		this.ipAdresses = ipAdresses;
	}

	public List<byte[]> getIpAdresses() {
		return ipAdresses;
	}
}