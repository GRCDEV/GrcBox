package es.upv.grc.grcbox.common;

public class GrcBoxSsid {
	public enum MODE{
		INFRASTRUCTURE,
		AD_HOC;
	}
	
	private String ssid; //The ssid value
	private int freq; //The frequency of the network
	private MODE mode; //The mode
	private int bitrate; //Bitrate of the network
	private int strength; //Signal strength
	private boolean security; //Whether the network implements security or not, only shared key is supported now
	private boolean configured; //Whether the ssid has been configured in a connection or not
	private boolean autoConnect; //whether the GRCbox will try to autoconnect to this network, only for configured ssids
	
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public MODE getMode() {
		return mode;
	}
	public void setMode(MODE mode) {
		this.mode = mode;
	}
	public int getBitrate() {
		return bitrate;
	}
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public boolean isConfigured() {
		return configured;
	}
	public void setConfigured(boolean configured) {
		this.configured = configured;
	}
	public boolean isSecurity() {
		return security;
	}
	public void setSecurity(boolean security) {
		this.security = security;
	}
	public boolean isAutoConnect() {
		return autoConnect;
	}
	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}
}
