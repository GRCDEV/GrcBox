package es.upv.grc.grcbox.server.networkInterfaces;


import org.freedesktop.dbus.UInt32;

class Device {
	private String dbusPath;
	private String iface; //Name of the interface
	private UInt32 ifaceIpAddress; // Ip Adress of the interface
	private UInt32 capabilities; //Capabilities according to NM_DEVICE_CAP
	private UInt32 state; // State of the device NM_DEVICE_STATE
	private String activeConnection; //Active connection of the device
	private String ip4Config;
	private boolean managed;
	private UInt32 type;
	
	public Device(String dbusPath, String iface,
			UInt32 ifaceIpAddress, UInt32 capabilities, UInt32 state,
			String activeConnectionPath, String ip4ConfigPath, boolean managed,
			UInt32 type) {
		super();
		this.setDbusPath(dbusPath);
		this.iface = iface;
		this.ifaceIpAddress = ifaceIpAddress;
		this.capabilities = capabilities;
		this.state = state;
		this.activeConnection = activeConnectionPath;
		this.ip4Config = ip4ConfigPath;
		this.managed = managed;
		this.type = type;
	}

	public Device() {
	}


	public String getDbusPath() {
		return dbusPath;
	}

	public void setDbusPath(String dbusPath) {
		this.dbusPath = dbusPath;
	}

	public String getIface() {
		return iface;
	}

	public void setIface(String iface) {
		this.iface = iface;
	}

	public UInt32 getIfaceIpAddress() {
		return ifaceIpAddress;
	}

	public void setIfaceIpAddress(UInt32 ifaceIpAddress) {
		this.ifaceIpAddress = ifaceIpAddress;
	}

	public UInt32 getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(UInt32 capabilities) {
		this.capabilities = capabilities;
	}

	public UInt32 getState() {
		return state;
	}

	public void setState(UInt32 state) {
		this.state = state;
	}

	public String getActiveConnection() {
		return activeConnection;
	}

	public void setActiveConnection(String activeConnection) {
		this.activeConnection = activeConnection;
	}

	public String getIp4Config() {
		return ip4Config;
	}

	public void setIp4Config(String ip4Config) {
		this.ip4Config = ip4Config;
	}

	public boolean isManaged() {
		return managed;
	}

	public void setManaged(boolean managed) {
		this.managed = managed;
	}

	public UInt32 getType() {
		return type;
	}

	public void setType(UInt32 type) {
		this.type = type;
	}
}
