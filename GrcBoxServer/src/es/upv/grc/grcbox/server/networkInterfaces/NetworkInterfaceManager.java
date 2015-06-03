package es.upv.grc.grcbox.server.networkInterfaces;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManagerIface;
import org.freedesktop.NetworkManager.AccessPoint;
import org.freedesktop.NetworkManager.DeviceInterface;
import org.freedesktop.NetworkManager.Utility;
import org.freedesktop.NetworkManager.DeviceInterface.StateChanged;
import org.freedesktop.NetworkManager.Constants.NM_802_11_AP_FLAGS;
import org.freedesktop.NetworkManager.Constants.NM_802_11_MODE;
import org.freedesktop.NetworkManager.Constants.NM_DEVICE_STATE;
import org.freedesktop.NetworkManager.Constants.NM_DEVICE_TYPE;
import org.freedesktop.NetworkManager.Device.Wireless;
import org.freedesktop.NetworkManager.Device.Wireless.AccessPointAdded;
import org.freedesktop.NetworkManager.Device.Wireless.AccessPointRemoved;
import org.freedesktop.NetworkManager.Settings.Connection;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.ObjectPath;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.restlet.resource.ResourceException;

import sun.org.mozilla.javascript.UintMap;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterface.Type;
import es.upv.grc.grcbox.common.GrcBoxSsid;
import es.upv.grc.grcbox.common.GrcBoxSsid.MODE;


/**
 * This class manage the interaction between the GRCBox and the NetworkManager
 * The communication is done using the DBus interface.
 */
public class NetworkInterfaceManager {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(NetworkInterfaceManager.class.getName()); 

	/** A map {name, GrcBoxInterfaces} to cache interfaces info from NetworkManager. */
	private static volatile Map<String, GrcBoxInterface> cachedInterfaces = new HashMap<>();

	/** A map {name, Device} to cache interfaces info from NetworkManager. */
	private static volatile Map<String, Device> devices = new HashMap<>();
	/*
	 * Map to cache available the APs DbusPath by interface name, indexed by 
	 * their iface+ssid
	 */
	private static volatile Map<String, Map<String, GrcBoxConnection> > connections = new HashMap<>();

	/*
	 * A map containing configured connections indexed by ssid
	 */
	private static volatile Map<String, GrcBoxConnection> confConnections = new HashMap<String, GrcBoxConnection>();
	/** List of interface signal listeners. */
	private static Vector<NetworkManagerListener> ifaceSubscribers = new Vector<>();

	/** The connection to the DBus system. */
	private static DBusConnection conn; 

	/** The DBus object used to connect with the NM. */
	private static NetworkManagerIface nm;

	/** The initialized. */
	private static volatile Boolean initialized = false;

	/** The Constant _VERSION_SUPPORTED. */
	private static final String _VERSION_SUPPORTED= "1.0.2";





	/**
	 * The Class PropertiesChangedHandler.
	 * Handlers for NM signals
	 */
	private class PropertiesChangedHandler implements DBusSigHandler<org.freedesktop.NetworkManagerIface.PropertiesChanged>{

		/**
		 * Network Manager Properties Handler
		 * Keeps the list of devices updated.
		 *
		 * @param signal the signal
		 */
		@Override
		public synchronized void handle(org.freedesktop.NetworkManagerIface.PropertiesChanged signal) {
			LOG.entering(this.getClass().getName(), "handleProp", signal);
			if(signal.a.containsKey("Devices")){
				LOG.info("Devices have changed");

				List<ObjectPath> devList = (List<ObjectPath>) signal.a.get("Devices").getValue();
				if( devList.size() > devices.size() ){
					LOG.info("There is a new device");
					for (ObjectPath devPath : devList) {
						try {
							Properties props = conn.getRemoteObject(NetworkManagerIface._NM_IFACE, devPath.getPath(),  Properties.class);
							Map<String, Variant> propsMap = props.GetAll(NetworkManagerIface._DEVICE_IFACE);
							String iface = (String)propsMap.get("Interface").getValue();
							if(!devices.containsKey(iface)){
								LOG.info("New Device found "+ iface);
								addDevice(devPath.getPath());
							}
						} catch (DBusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if( devList.size() < devices.size()){
					LOG.info("A device has been removed");
					List<String> toRemove = new LinkedList<>();
					for (Device dev : devices.values()) {
						String iface = dev.getIface();
						boolean exists = false;
						for (ObjectPath devPath : devList) {
							Properties props;
							try {
								props = (Properties) conn.getRemoteObject("org.freedesktop.NetworkManager", devPath.getPath(),  Properties.class);
								Map<String, Variant> propsMap = props.GetAll(NetworkManagerIface._DEVICE_IFACE);
								String iface2 = (String)propsMap.get("Interface").getValue();
								if(iface2.equals(iface)){
									exists = true;
									break;
								}
							} catch (DBusException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(!exists){
							LOG.info("Device removed "+ iface);
							toRemove.add(iface);
						}
					}
					for (String iface : toRemove) {
						devices.remove(iface);
						GrcBoxInterface grcInterface = cachedInterfaces.get(iface);
						cachedInterfaces.remove(iface);
						informInterfaceRemoved(grcInterface);
					}
				}
				else{
					LOG.warning("Devices Properties changed but no device was added or removed");
				}
			}
			LOG.exiting(this.getClass().getName(), "handleProp", signal);
		}
	}

	/**
	 * The Class StateChangedHandler.
	 */
	private class StateChangedHandler implements DBusSigHandler<org.freedesktop.NetworkManager.DeviceInterface.StateChanged>{

		/**
		 * Handler for Device StateChaged signal.
		 *
		 * @param signal the signal
		 */
		@Override
		public void handle(StateChanged signal) {
			LOG.entering(this.getClass().getName(), "stateChanged");
			/*
			 * Only update the device information if the old or the new states are "ACTIVATED"
			 */
			if( !( signal.a.equals(NM_DEVICE_STATE.UNAVAILABLE) || 
					signal.a.equals(NM_DEVICE_STATE.UNMANAGED) ) &&
					(signal.a.equals(NM_DEVICE_STATE.ACTIVATED) || 
							signal.b.equals(NM_DEVICE_STATE.ACTIVATED))
					)
			{
				try {
					Device dev = updateDevStatus(signal.getPath());
					informInterfaceChanged(cachedInterfaces.get(dev.getIface()));
				} catch (DBusException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			LOG.exiting(this.getClass().getName(), "stateChanged");
		}
	}

	private Device addDevice(String path) throws DBusException{
		Device device = updateDevStatus(path);
		if(device.isManaged()){
			GrcBoxInterface grcIface = cachedInterfaces.get(device.getIface());
			LOG.info("New Device Added:"+device.getIface());
			/*
			 * If it is a wifi device, subscribe for AP updates
			 * and request a list of the available APs
			 */
			if(device.getType().equals(NM_DEVICE_TYPE.WIFI) ){
				connections.put(device.getIface(), new HashMap<String, GrcBoxConnection>());
				Wireless wirelessObj = (Wireless) conn.getRemoteObject(
						NetworkManagerIface._NM_IFACE, device.getDbusPath(), Wireless.class);
				List<DBusInterface> apList = wirelessObj.GetAllAccessPoints();
				for (DBusInterface dBusInterface : apList) {
					addAccessPoint(dBusInterface, grcIface.getName());
				}
				subscribeToApSignals(device.getIface());
			}
			informInterfaceAdded(grcIface);
		}
		return device;
	}

	/**
	 * Update dev status.
	 *
	 * @param path the path
	 */
	private Device updateDevStatus(String path) throws DBusException {
		Device dev = readDeviceFromDbus(path);
		devices.put(dev.getIface(), dev);
		if(dev.isManaged()){
			try{
				GrcBoxInterface iface = device2grcBoxIface(dev);
				cachedInterfaces.put(dev.getIface(), iface);
				LOG.info("Device "+ dev.getIface() + " has been updated");
			} catch (ExecutionException e) {
				LOG.log(Level.WARNING,"Error processing interface "+dev.getIface(),e);
			}
		}
		return dev;
	}

	/*
	 * When an AP is removed, update the device's APs list
	 */
	private class AccessPointRemovedHandler 
	implements DBusSigHandler<org.freedesktop.NetworkManager.Device.Wireless.AccessPointRemoved>{

		private String device;

		public AccessPointRemovedHandler(String device) {
			this.device = device;
		}

		@Override
		public void handle(AccessPointRemoved arg0) {
			DBusInterface ap = arg0.a;
			removeAccessPoint(ap, device);
		}


	}

	private class AccessPointAddedHandler
	implements DBusSigHandler<org.freedesktop.NetworkManager.Device.Wireless.AccessPointAdded>{

		private String device;

		public AccessPointAddedHandler(String device) {
			this.device = device;
		}

		@Override
		public void handle(AccessPointAdded arg0) {
			DBusInterface ap = arg0.a;
			addAccessPoint(ap, device);
		}
	}

	private void addAccessPoint(DBusInterface dBusInterface, String iface) {
		Properties apProps = (Properties) dBusInterface;
		Map<String, Variant> apMapProps = apProps.GetAll(NetworkManagerIface._AP_IFACE);
		byte[] ssidByteName =  (byte[]) apMapProps.get("Ssid").getValue();
		String ssid = new String(ssidByteName);
		UInt32 freq = (UInt32) apMapProps.get("Frequency").getValue();
		UInt32 maxBitrate = (UInt32) apMapProps.get("MaxBitrate").getValue();
		Byte strength = (Byte) apMapProps.get("Strength").getValue();
		UInt32 wpaFlags = (UInt32) apMapProps.get("WpaFlags").getValue();
		UInt32 flags = (UInt32) apMapProps.get("Flags").getValue();
		UInt32 nmMode = (UInt32) apMapProps.get("Mode").getValue();
		GrcBoxSsid.MODE mode = nmMode.equals(NM_802_11_MODE.ADHOC)? 
				MODE.AD_HOC: MODE.INFRASTRUCTURE;
		boolean security = flags.equals(NM_802_11_AP_FLAGS.PRIVACY);
		boolean configured = confConnections.containsKey(ssid);
		boolean autoConnect = false;
		if(configured){
			/*
			 * If this connection has been configured, update the dynamic values
			 */
			GrcBoxConnection oldConn = confConnections.get(ssid);
			autoConnect = oldConn.isAutoConnect();
			GrcBoxSsid grcBoxSsid = new GrcBoxSsid(ssid, freq.intValue(), mode,
					maxBitrate.intValue(), strength, security, configured, autoConnect);
			/*
			 * Add this ssid to the ssids map.
			 * update the Configured connection
			 */
			GrcBoxConnection newConn = new GrcBoxConnection(grcBoxSsid, dBusInterface, oldConn.getPassword() );
			connections.get(iface).put(ssid,newConn);
			String password = confConnections.get(ssid).getPassword();
			confConnections.put(ssid, new GrcBoxConnection(grcBoxSsid, dBusInterface, password));
		}
		else{


			GrcBoxSsid grcBoxSsid = new GrcBoxSsid(ssid, freq.intValue(), mode,
					maxBitrate.intValue(), strength, security, configured, autoConnect);
			GrcBoxConnection newConn = new GrcBoxConnection(grcBoxSsid, dBusInterface, null);
			connections.get(iface).put(ssid, newConn);
		}
	}

	private void removeAccessPoint(DBusInterface ap, String iface) {
		Properties apProps = (Properties) ap;
		Map<String, Variant> apMapProps = apProps.GetAll(NetworkManagerIface._AP_IFACE);
		byte[] ssidByteName =  (byte[]) apMapProps.get("Ssid").getValue();
		String ssid = new String(ssidByteName);
		UInt32 freq = (UInt32) apMapProps.get("Frequency").getValue();
		UInt32 maxBitrate = (UInt32) apMapProps.get("MaxBitrate").getValue();
		Byte strength = (Byte) apMapProps.get("Strength").getValue();
		UInt32 wpaFlags = (UInt32) apMapProps.get("WpaFlags").getValue();
		UInt32 flags = (UInt32) apMapProps.get("Flags").getValue();
		UInt32 nmMode = (UInt32) apMapProps.get("Mode").getValue();
		GrcBoxSsid.MODE mode = nmMode.equals(NM_802_11_MODE.ADHOC)? 
				MODE.AD_HOC: MODE.INFRASTRUCTURE;
		boolean security = flags.equals(NM_802_11_AP_FLAGS.PRIVACY);
		connections.get(iface).remove(new GrcBoxSsid(ssid, freq.intValue(), mode, maxBitrate.intValue(), 
				strength, security, false, false));
	}


	/**
	 * Gets the managed interfaces.
	 *
	 * @return the interfaces list
	 */
	public Collection<GrcBoxInterface> getInterfaces(){
		return cachedInterfaces.values();
	}


	/**
	 * Gets the interface.
	 *
	 * @param ifaceName the interface name
	 * @return the interface, null if it does not exist
	 */
	public GrcBoxInterface getInterface(String ifaceName) {
		GrcBoxInterface iface = cachedInterfaces.get(ifaceName);
		return iface;
	}

	/**
	 * Initialize.
	 *
	 * @return true, if successful
	 * @throws DBusException the d bus exception
	 * @throws ExecutionException the execution exception
	 */
	public synchronized boolean initialize() throws DBusException, ExecutionException{
		LOG.entering(this.getClass().getName(), "initialize");
		if(isNMAvailable()){
			readDevicesInfo();
			subscribeToNMSignals();
			initialized = true;
			return true;
		}
		return false;
	}

	/**
	 * Subscribe to NM to monitor devices status signals.
	 *
	 * @throws DBusException a DBusException when there is a problem with the 
	 * Dbus connection
	 */
	private void subscribeToNMSignals() throws DBusException {
		conn.addSigHandler(org.freedesktop.NetworkManagerIface.PropertiesChanged.class, new PropertiesChangedHandler());
		conn.addSigHandler(org.freedesktop.NetworkManager.DeviceInterface.StateChanged.class, new StateChangedHandler());
	}

	/*
	 * Subscribe to AP signals to monitor AP list 
	 */
	private void subscribeToApSignals(String device) throws DBusException {
		conn.addSigHandler(org.freedesktop.NetworkManager.Device.Wireless.AccessPointAdded.class, new AccessPointAddedHandler(device));
		conn.addSigHandler(org.freedesktop.NetworkManager.Device.Wireless.AccessPointRemoved.class, new AccessPointRemovedHandler(device));
	}

	/*
	 * Read the information from the NetworkManager and stores it in
	 * devices, also populate the cachedInterfaces map;.
	 *
	 * @throws DBusException the d bus exception
	 */
	private synchronized void readDevicesInfo() throws DBusException{
		LOG.entering(this.getClass().getName(),"readDevicesInfo");
		List<Path> devList = nm.GetDevices();
		for (Path devInterface : devList) {
			/*
			 * TODO read and add all the devices
			 */
			addDevice(devInterface.getPath());
		}
		LOG.exiting(this.getClass().getName(), "readdevicesInfo");;
	}

	/**
	 * Convert a Device object into a GrcBoxInterface object.
	 *
	 * @param dev the Device object
	 * @return the grc box interface
	 * @throws DBusException Needs to connect to DBus to get all the information,
	 * an exception is thrown when there is a problem in the connection.
	 * @throws ExecutionException the execution exception
	 */
	private GrcBoxInterface device2grcBoxIface(Device dev) throws DBusException, ExecutionException{
		LOG.entering(this.getClass().getName(), "device2GrcBoxIface");
		GrcBoxInterface iface = new GrcBoxInterface();
		Properties devProp = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, dev.getDbusPath(),  Properties.class);
		iface.setName(dev.getIface());


		GrcBoxInterface.Type type;

		/*
		 * Get the interface Type
		 */
		if(dev.getType().equals(NM_DEVICE_TYPE.WIFI)){
			UInt32 wifiMode = devProp.Get(NetworkManagerIface._WIRELESS_IFACE, "Mode");
			if(wifiMode.equals(NM_802_11_MODE.ADHOC)){
				type = Type.WIFIAH;
			}
			else if(wifiMode.equals(NM_802_11_MODE.INFRA)){
				type = Type.WIFISTA;
			}
			else{
				type = Type.UNKNOWN;
			}
		}
		else if(dev.getType().equals(NM_DEVICE_TYPE.ETHERNET)){
			type = Type.ETHERNET; 
		}
		else if(dev.getType().equals(NM_DEVICE_TYPE.MODEM)){
			type = Type.CELLULAR;
		}
		else if(dev.getType().equals(NM_DEVICE_TYPE.ADSL) ||
				dev.getType().equals(NM_DEVICE_TYPE.BT) ||
				dev.getType().equals(NM_DEVICE_TYPE.GENERIC))
			type = Type.OTHERS;
		else{
			type = Type.UNKNOWN;
		}

		iface.setType(type);
		boolean isUp = (dev.getState().equals(NM_DEVICE_STATE.ACTIVATED));
		iface.setUp(isUp);

		/*
		 * Find the used Connection and other values only if the device is up
		 */


		if(isUp){
			if((boolean) devProp.Get(NetworkManagerIface._DEVICE_IFACE, "Managed")){
				String ipAddr = getIpAddress(iface.getName());
				iface.setAddress(ipAddr);
				Path activeConnPath = (Path) devProp.Get(NetworkManagerIface._DEVICE_IFACE, "ActiveConnection");
				Properties actConnProp = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, activeConnPath.getPath(),  Properties.class);
				Path connPath = actConnProp.Get(NetworkManagerIface._ACTIVE_IFACE, "Connection");
				Connection connIface = (Connection) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, connPath.getPath(),  Connection.class);
				Map<String,Map<String,Variant>> settings = connIface.GetSettings();
				String connection = (String)settings.get("connection").get("id").getValue();
				iface.setConnection(connection);

				Boolean isDefault = actConnProp.Get(NetworkManagerIface._ACTIVE_IFACE, "Default");
				iface.setDefault(isDefault);

				/*
				 * We assume that devices are connected to Internet when a gateway is defined.
				 */
				Path ip4Path = (Path) devProp.Get(NetworkManagerIface._DEVICE_IFACE, "Ip4Config");
				Properties ip4Prop = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, ip4Path.getPath(),  Properties.class);
				Vector<Vector<UInt32>> addresses = ip4Prop.Get(NetworkManagerIface._IP4CONFIG_IFACE, "Addresses");
				UInt32 gw = addresses.get(0).get(2);
				iface.setHasinternet(gw.intValue() != 0);
			}
			else{
				iface.setConnection(null);
			}

			/*
			 * TODO Add support for other kind of interfaces.
			 */
			UInt32 speed;
			switch (iface.getType()){
			case ETHERNET:
				speed = devProp.Get(NetworkManagerIface._WIRED_IFACE, "Speed");
				iface.setRate(speed.doubleValue());
				break;
			case WIFISTA:
			case WIFIAH:
				speed = devProp.Get("org.freedesktop.NetworkManager.Device.Wireless", "Bitrate");
				iface.setRate(speed.doubleValue()/1000);
				break;
			default:
				throw new ExecutionException("Unsupported Device type", new Throwable());
			}
		}
		else{
			iface.setConnection(null);
			iface.setRate(0);
			iface.setDefault(false);
		}

		/*
		 * TODO Estimate the real cost
		 */
		iface.setCost(0);



		/*
		 * TODO Currently there is only support for Wifi or ethernet. 
		 * Both  interfaces support multicast.
		 */
		iface.setMulticast(true);
		LOG.exiting(this.getClass().getName(), "device2GrcBoxIface", iface);
		return iface;
	}

	/**
	 * Read device from DBus and create a new Device.
	 *
	 * @param path the path
	 * @return the device
	 * @throws DBusException the d bus exception
	 */
	private Device readDeviceFromDbus(String path) throws DBusException{
		Device device = new Device();
		Properties props = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, path,  Properties.class);
		device.setDbusPath(path);
		if(props instanceof Properties){

			Map<String, Variant> propsMap = props.GetAll(NetworkManagerIface._DEVICE_IFACE);
			if(propsMap.get("Interface") != null) 
				device.setIface((String) propsMap.get("Interface").getValue());
			if(propsMap.get("Capabilities") != null) 
				device.setCapabilities((UInt32) propsMap.get("Capabilities").getValue());
			if(propsMap.get("State") != null) 
				device.setState((UInt32) propsMap.get("State").getValue());
			if(propsMap.get("ActiveConnection") != null) 
				device.setActiveConnection(((ObjectPath)propsMap.get("ActiveConnection").getValue()).getPath());
			if(propsMap.get("Ip4Config") != null) 
				device.setIp4Config(((ObjectPath)propsMap.get("Ip4Config").getValue()).getPath());
			if(propsMap.get("Managed") != null) 
				device.setManaged((Boolean)propsMap.get("Managed").getValue());
			if(propsMap.get("DeviceType") != null) 
				device.setType((UInt32)propsMap.get("DeviceType").getValue());

			if(device.getState().equals(NM_DEVICE_STATE.ACTIVATED)){
				Properties ip4Prop = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, device.getIp4Config(),  Properties.class);
				Vector<Vector<UInt32>> addresses = ip4Prop.Get(NetworkManagerIface._IP4CONFIG_IFACE, "Addresses");
				UInt32 ip = addresses.get(0).get(0);
				device.setIfaceIpAddress(ip);
				LOG.info("iface IP " + ip);
			}
		}
		return device;
	}

	/**
	 * Checks if is NM available.
	 *
	 * @return true, if is NM available
	 * @throws ExecutionException the execution exception
	 */
	private boolean isNMAvailable() throws ExecutionException{
		try {
			conn = DBusConnection.getConnection(DBusConnection.SYSTEM);

			Properties nmProp = (Properties)conn.getRemoteObject(NetworkManagerIface._NM_IFACE, 
					NetworkManagerIface._NM_PATH, 
					Properties.class);
			nm = (NetworkManagerIface)conn.getRemoteObject(NetworkManagerIface._NM_IFACE, 
					NetworkManagerIface._NM_PATH, 
					NetworkManagerIface.class);

			String version = nmProp.Get( NetworkManagerIface._NM_IFACE, "Version");
			if(!version.equals(_VERSION_SUPPORTED)){
				LOG.severe("NM version not supported "+ version);
				throw new ExecutionException("Unsupported NetworkManager version", new Throwable());
			}
		} catch (DBusException e) {
			throw new ExecutionException("Error connecting to NetworkManager", e);
		}
		return true;
	}

	/**
	 * Subscribe interfaces.
	 *
	 * @param object the object
	 * @return the int
	 */
	public synchronized int subscribeInterfaces(NetworkManagerListener object){
		ifaceSubscribers.add(object);
		return ifaceSubscribers.size();
	}

	/**
	 * Unsubscribe interfaces.
	 *
	 * @param index the index
	 */
	public synchronized void unsubscribeInterfaces(int index){
		ifaceSubscribers.remove(index);
	}
	/**
	 * Inform interface added.
	 *
	 * @param iface the iface
	 */
	public synchronized void informInterfaceAdded(GrcBoxInterface iface){
		for (NetworkManagerListener networkManagerListener : ifaceSubscribers) {
			networkManagerListener.interfaceAdded(iface);
		}
	}

	/**
	 * Inform interface removed.
	 *
	 * @param iface the iface
	 */
	private synchronized void informInterfaceRemoved(GrcBoxInterface iface){
		for (NetworkManagerListener networkManagerListener : ifaceSubscribers) {
			networkManagerListener.interfaceRemoved(iface);
		}
	}

	/**
	 * Inform interface changed.
	 *
	 * @param iface the iface
	 */
	private synchronized void informInterfaceChanged(GrcBoxInterface iface){
		for (NetworkManagerListener networkManagerListener : ifaceSubscribers) {
			networkManagerListener.interfaceChanged(iface);
		}
	}

	/**
	 * Checks if is initialized.
	 *
	 * @return true, if is initialized
	 */
	public synchronized boolean isInitialized() {
		return initialized.booleanValue();
	}

	/**
	 * Returns the gateway associate to interface iface from DBus.
	 *
	 * @param iface the iface
	 * @return the gateway
	 */
	public String getGateway(String iface) {
		LOG.entering(this.getClass().getName(), "getGw");
		Device dev = devices.get(iface);
		Properties prop;
		String gwStr = null;
		if(dev.getState().equals(NM_DEVICE_STATE.ACTIVATED)){
			try {
				prop = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, dev.getDbusPath(),  Properties.class);
				Path ip4Path = (Path) prop.Get(NetworkManagerIface._DEVICE_IFACE, "Ip4Config");
				Properties ip4Prop = (Properties) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, ip4Path.getPath(),  Properties.class);
				Vector<Vector<UInt32>> addresses = ip4Prop.Get(NetworkManagerIface._IP4CONFIG_IFACE, "Addresses");
				UInt32 gw = addresses.get(0).get(2);
				if(gw.doubleValue() == 0.0)
					return null;
				gwStr = int2Ip(gw.longValue());
				LOG.finest("The gateway of the device " + iface +" is " + gwStr);
			} catch (DBusException e) {
				gwStr = null;
			}
		}
		return gwStr;
	}

	/**
	 * Convert from long to ipv4 String.
	 *
	 * @param addr the addr
	 * @return the string
	 */
	private String int2Ip(long addr) {
		LOG.info("Long " + addr + " to String" );
		if(addr == 0){
			return "0.0.0.0";
		}
		byte [] gwByte = new byte[4];
		byte [] temp =  (BigInteger.valueOf(addr)).toByteArray();
		gwByte[0] = temp[3];
		gwByte[1] = temp[2];
		gwByte[2] = temp[1];
		gwByte[3] = temp[0];
		String ip = null;
		try {
			ip= Inet4Address.getByAddress(gwByte).getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * Gets the ip address.
	 *
	 * @param iface the iface
	 * @return the ip address
	 */
	public String getIpAddress(String iface) {
		Device dev = devices.get(iface);
		return int2Ip(dev.getIfaceIpAddress().longValue());
	}

	public List<GrcBoxSsid> getAps(String iface){
		ArrayList<GrcBoxSsid> list = new ArrayList<GrcBoxSsid>(connections.get(iface).values());
		return list;
	}

	public void connectToAp(String ssid, String iface, boolean autoconnect, String password) throws DBusException{
		GrcBoxConnection grcBoxConnection = connections.get(iface).get(ssid);

		if(grcBoxConnection == null){
			throw new ResourceException(404);
		}

		if(autoconnect){
			GrcBoxConnection newConnection = new GrcBoxConnection(grcBoxConnection, 
					grcBoxConnection.getDbusInterface(), password);
			confConnections.put(iface+ssid, newConnection);
		}

		Map<String, Variant> wirelessMap = new HashMap<String, Variant>();
		wirelessMap.put("ssid", new Variant<byte[]>(ssid.getBytes()));

		String mode;
		if(grcBoxConnection.getMode().equals(GrcBoxSsid.MODE.INFRASTRUCTURE)){
			mode = "ifrastructure";
		}
		else{
			mode = "ad-hoc";
		}
		wirelessMap.put("mode", new Variant<String>("infrastructure"));
		Map<String, Variant> connectionMap = new HashMap<String, Variant>();
		String connectionName = "GrcBox"+ssid+iface;
		connectionMap.put("id", new Variant<String>(connectionName));
		connectionMap.put("type", new Variant<String>("802-11-wireless"));

		/*
		 * autoconnect is managed by the GRCBox
		 */
		connectionMap.put("autoconnect", new Variant<Boolean>(false));
		connectionMap.put("uuid", new Variant<String>(Utility.generateUuid(connectionName+System.currentTimeMillis())));
		Map<String, Variant> ipv4Map = new HashMap<String, Variant>();
		ipv4Map.put("method", new Variant<String>("auto"));
		Map<String, Map<String,Variant> > newConnection = new HashMap<String, Map<String,Variant> >();				
		newConnection.put("connection", connectionMap);
		newConnection.put("802-11-wireless", wirelessMap);
		newConnection.put("ipv4", ipv4Map);

		if(grcBoxConnection.isSecurity()){
			Map<String, Variant> wifiSecMap = new HashMap<String, Variant>();
			wifiSecMap.put("psk", new Variant<String>(password));
			newConnection.put("802-11-wireless-security", wifiSecMap);
		}

		NetworkManagerIface netMngIface = (NetworkManagerIface) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, NetworkManagerIface._NM_PATH,  NetworkManagerIface.class);
		DeviceInterface ifaceDbusDevice = (DeviceInterface) conn.getRemoteObject(NetworkManagerIface._NM_IFACE, devices.get(iface).getDbusPath(),  DeviceInterface.class);
		netMngIface.AddAndActivateConnection(newConnection, ifaceDbusDevice, grcBoxConnection.getDbusInterface());
	}

	public void removeAp(String ssid){
		confConnections.remove(ssid);
	}
}