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
	private double cost; //Cost per transfered MB
	
	private int	index;
	private int mtu;
	
	private String ipAddress;
	private String gatewayIp;
	private boolean isLoopback;
	private boolean isUp;
	private boolean isMulticast;
	private boolean hasInternet;
	
	
	public AndroPiInterface(String name, int index, Type type, double cost,
			int mtu, String ipAddress, boolean isLoopback, boolean isUp,
			boolean isMulticast, boolean hasGateway) {
		super();
		this.name = name;
		this.index = index;
		this.mtu = mtu;
		this.isLoopback = isLoopback;
		this.isUp = isUp;
		this.isMulticast = isMulticast;
		this.type = type;
		this.cost = cost;
		this.ipAddress = ipAddress;
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
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
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
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress( String ipAddress){
		this.ipAddress = ipAddress;
	}

	public String getGatewayIp() {
		return gatewayIp;
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

	public boolean isHasInternet() {
		return hasInternet;
	}

	public void setHasinternet(boolean hasInternet) {
		this.hasInternet = hasInternet;
	}
}
