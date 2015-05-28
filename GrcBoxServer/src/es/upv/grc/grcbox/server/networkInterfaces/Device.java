package es.upv.grc.grcbox.server.networkInterfaces;


import org.freedesktop.dbus.UInt32;

/**
 * The Class Device, only used in {@link NetworkInterfaceManager} to store
 * devices related to NetworkManager DBus interfaces
 */
class Device {
	
	/** The dbus path. */
	private String dbusPath;
	
	/** The iface. */
	private String iface; //Name of the interface
	
	/** The iface ip address. */
	private UInt32 ifaceIpAddress; // Ip Adress of the interface
	
	/** The capabilities. */
	private UInt32 capabilities; //Capabilities according to NM_DEVICE_CAP
	
	/** The state. */
	private UInt32 state; // State of the device NM_DEVICE_STATE
	
	/** The active connection. */
	private String activeConnection; //Active connection of the device
	
	/** The ip4 config. */
	private String ip4Config;
	
	/** The managed. */
	private boolean managed;
	
	/** The type. */
	private UInt32 type;
	
	/**
	 * Instantiates a new device.
	 *
	 * @param dbusPath the dbus path
	 * @param iface the iface name
	 * @param ifaceIpAddress the iface ip address
	 * @param capabilities the capabilities of the device as defined by NM
	 * @param state the state of the device as defined by NM
	 * @param activeConnectionPath the active connection path
	 * @param ip4ConfigPath the ip4 config path
	 * @param managed true if the device is managed by NM
	 * @param type the type of the interface WIRED or WIRELESS
	 */
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

	/**
	 * Instantiates a new device.
	 */
	public Device() {
	}


	/**
	 * Gets the dbus path.
	 *
	 * @return the dbus path
	 */
	public String getDbusPath() {
		return dbusPath;
	}

	/**
	 * Sets the dbus path.
	 *
	 * @param dbusPath the new dbus path
	 */
	public void setDbusPath(String dbusPath) {
		this.dbusPath = dbusPath;
	}

	/**
	 * Gets the iface.
	 *
	 * @return the iface
	 */
	public String getIface() {
		return iface;
	}

	/**
	 * Sets the iface.
	 *
	 * @param iface the new iface
	 */
	public void setIface(String iface) {
		this.iface = iface;
	}

	/**
	 * Gets the iface ip address.
	 *
	 * @return the iface ip address
	 */
	public UInt32 getIfaceIpAddress() {
		return ifaceIpAddress;
	}

	/**
	 * Sets the iface ip address.
	 *
	 * @param ifaceIpAddress the new iface ip address
	 */
	public void setIfaceIpAddress(UInt32 ifaceIpAddress) {
		this.ifaceIpAddress = ifaceIpAddress;
	}

	/**
	 * Gets the capabilities.
	 *
	 * @return the capabilities
	 */
	public UInt32 getCapabilities() {
		return capabilities;
	}

	/**
	 * Sets the capabilities.
	 *
	 * @param capabilities the new capabilities
	 */
	public void setCapabilities(UInt32 capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public UInt32 getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(UInt32 state) {
		this.state = state;
	}

	/**
	 * Gets the active connection.
	 *
	 * @return the active connection
	 */
	public String getActiveConnection() {
		return activeConnection;
	}

	/**
	 * Sets the active connection.
	 *
	 * @param activeConnection the new active connection
	 */
	public void setActiveConnection(String activeConnection) {
		this.activeConnection = activeConnection;
	}

	/**
	 * Gets the ip4 config.
	 *
	 * @return the ip4 config
	 */
	public String getIp4Config() {
		return ip4Config;
	}

	/**
	 * Sets the ip4 config.
	 *
	 * @param ip4Config the new ip4 config
	 */
	public void setIp4Config(String ip4Config) {
		this.ip4Config = ip4Config;
	}

	/**
	 * Checks if is managed.
	 *
	 * @return true, if is managed
	 */
	public boolean isManaged() {
		return managed;
	}

	/**
	 * Sets the managed.
	 *
	 * @param managed the new managed
	 */
	public void setManaged(boolean managed) {
		this.managed = managed;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public UInt32 getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(UInt32 type) {
		this.type = type;
	}
}
