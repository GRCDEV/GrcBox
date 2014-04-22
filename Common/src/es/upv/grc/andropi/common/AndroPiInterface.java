package es.upv.grc.andropi.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class AndroPiInterface {
	
	public enum Type{
		WIFISTA, WIFIAH, CELLULAR, ETHERNET, WIMAX, WIFIP 
	}
	
    private Type type;
	private String name;
	private int	index;
	private int mtu;
	
	byte[] ipAddress;
	AddressesList addressesList;
	private boolean isLoopback;
	private boolean isUp;
	private boolean isMulticast;
	
	public AndroPiInterface(String name, int index,
			int mtu, Enumeration<InetAddress> inetAddresses, boolean isLoopback, boolean isUp,
			boolean isMulticast) {
		super();
		this.name = name;
		this.index = index;
		this.mtu = mtu;
		this.isLoopback = isLoopback;
		this.isUp = isUp;
		this.isMulticast = isMulticast;
		this.type = Type.WIFIP;
		this.ipAddress = inetAddresses.nextElement().getAddress();
		List<String> addressesList = new ArrayList<String>();
		while(inetAddresses.hasMoreElements()){
			addressesList.add(inetAddresses.nextElement().getHostAddress());
		}
		this.addressesList= new AddressesList(addressesList);
	}
	
	public AndroPiInterface(NetworkInterface nIf, Type type) throws SocketException{
		this(nIf.getName(), nIf.getIndex(), nIf.getMTU(), nIf.getInetAddresses(), nIf.isLoopback(), nIf.isUp(), nIf.supportsMulticast()); 
	}
	
	public AndroPiInterface(){
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getMtu() {
		return mtu;
	}
	public void setMtu(int mtu) {
		this.mtu = mtu;
	}
	
	public byte[] getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress( byte[] ipAddress){
		this.ipAddress = ipAddress;
	}
	
	public AddressesList getAdressesList() {
		return addressesList;
	}

	public void setAdressesList(AddressesList adressesList) {
		this.addressesList = adressesList;
	}

	public boolean isLoopback() {
		return isLoopback;
	}
	public void setLoopback(boolean isLoopback) {
		this.isLoopback = isLoopback;
	}
	public boolean isUp() {
		return isUp;
	}
	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	public boolean isMulticast() {
		return isMulticast;
	}
	public void setMulticast(boolean isMulticast) {
		this.isMulticast = isMulticast;
	}
}
