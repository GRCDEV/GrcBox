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
	
	public GrcBoxSsid(){
		
	}
	
	public GrcBoxSsid(GrcBoxSsid other){
		super();
		this.ssid = other.ssid;
		this.freq = other.freq;
		this.mode = other.mode;
		this.bitrate = other.bitrate;
		this.strength = other.strength;
		this.security = other.security;
		this.configured = other.configured;
		this.autoConnect = other.autoConnect;
	}
	
	public GrcBoxSsid(String ssid, int freq, MODE mode, int bitrate,
			int strength, boolean security, boolean configured,
			boolean autoConnect) {
		super();
		this.ssid = ssid;
		this.freq = freq;
		this.mode = mode;
		this.bitrate = bitrate;
		this.strength = strength;
		this.security = security;
		this.configured = configured;
		this.autoConnect = autoConnect;
	}
	
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
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + (security ? 1231 : 1237);
		result = prime * result + ((ssid == null) ? 0 : ssid.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GrcBoxSsid other = (GrcBoxSsid) obj;

		if (mode != other.mode) {
			return false;
		}
		if (security != other.security) {
			return false;
		}
		if (ssid == null) {
			if (other.ssid != null) {
				return false;
			}
		} else if (!ssid.equals(other.ssid)) {
			return false;
		}
		return true;
	}
}
