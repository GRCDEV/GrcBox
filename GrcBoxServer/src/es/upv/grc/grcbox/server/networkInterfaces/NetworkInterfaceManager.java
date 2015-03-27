package es.upv.grc.grcbox.server.networkInterfaces;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.freedesktop.DBus.Properties;
import org.freedesktop.NetworkManagerIface;
import org.freedesktop.NetworkManager.DeviceInterface.StateChanged;
import org.freedesktop.NetworkManager.Constants.NM_802_11_MODE;
import org.freedesktop.NetworkManager.Constants.NM_DEVICE_STATE;
import org.freedesktop.NetworkManager.Constants.NM_DEVICE_TYPE;
import org.freedesktop.NetworkManager.Settings.Connection;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.ObjectPath;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterface.Type;


public class NetworkInterfaceManager {
	private static final Logger LOG = Logger.getLogger(NetworkInterfaceManager.class.getName()); 
	/*
	 * A map name, GrcBoxInterfaces to cache info from NetworkManager
	 */
	private static volatile Map<String, GrcBoxInterface> cachedInterfaces = new HashMap<>();
	private static volatile Map<String, Device> devices = new HashMap<>();

	/*
	 * List of signal listeners
	 */
	private static Vector<NetworkManagerListener> ifaceSubscribers = new Vector<>();

	private static DBusConnection conn; 
	private static Properties nmProp;
	private static NetworkManagerIface nm;

	private static volatile Boolean initialized = false;



	/*
	 * Constants
	 */
	private static final String _VERSION_SUPPORTED= "0.9.10.0";
	private static final String _WIRELESS_IFACE = "org.freedesktop.NetworkManager.Device.Wireless";
	private static final String _WIRED_IFACE = "org.freedesktop.NetworkManager.Device.Wired";
	private static final String _DEVICE_IFACE = "org.freedesktop.NetworkManager.Device";
	private static final String _IP4CONFIG_IFACE = "org.freedesktop.NetworkManager.IP4Config";
	private static final String _ACTIVE_IFACE = "org.freedesktop.NetworkManager.Connection.Active";
	private static final String _NM_IFACE = "org.freedesktop.NetworkManager";
	private static final String _NM_PATH = "/org/freedesktop/NetworkManager";

	/*
	 * Handlers for NM signals
	 */

	private class PropertiesChangedHandler implements DBusSigHandler<org.freedesktop.NetworkManagerIface.PropertiesChanged>{
		/*
		 * Network Manager Properties Handler
		 * Keeps the list of devices updated
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
							Properties props = conn.getRemoteObject("org.freedesktop.NetworkManager", devPath.getPath(),  Properties.class);
							Map<String, Variant> propsMap = props.GetAll(_DEVICE_IFACE);
							String iface = (String)propsMap.get("Interface").getValue();
							if(!devices.containsKey(iface)){
								LOG.info("New Device found "+ iface);
								Device device = readDeviceFromDbus(devPath.getPath());
								devices.put(iface, device);
								GrcBoxInterface grcIface = device2grcBoxIface(device);
								cachedInterfaces.put(iface, grcIface);
								informInterfaceAdded(grcIface);
							}
						} catch (DBusException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
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
								Map<String, Variant> propsMap = props.GetAll(_DEVICE_IFACE);
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
			LOG.entering(this.getClass().getName(), "handleProp", signal);
		}
	}

	private class StateChangedHandler implements DBusSigHandler<org.freedesktop.NetworkManager.DeviceInterface.StateChanged>{

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
					){
				updateDevStatus(signal.getPath());
			}
			LOG.exiting(this.getClass().getName(), "stateChanged");
		}
	}
	
	private void updateDevStatus(String path) {
		try {
			Device dev = readDeviceFromDbus(path);
			devices.put(dev.getIface(), dev);
			GrcBoxInterface iface = device2grcBoxIface(dev);
			cachedInterfaces.put(dev.getIface(), iface);
			LOG.info("Device "+ dev.getIface() + " has been updated");
			informInterfaceChanged(iface);
		} catch (DBusException e) {
			LOG.severe(e.toString());
		} catch (ExecutionException e) {
			LOG.severe(e.toString());
		}
	}
	
	/*
	 * Return a list with all the managed interfaces
	 */
	public Collection<GrcBoxInterface> getInterfaces(){
		return cachedInterfaces.values();
	}

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

	/*
	 * Subscribe to NM signals to monitor devices status.
	 */
	private void subscribeToNMSignals() throws DBusException {
		conn.addSigHandler(org.freedesktop.NetworkManagerIface.PropertiesChanged.class, new PropertiesChangedHandler());
		conn.addSigHandler(org.freedesktop.NetworkManager.DeviceInterface.StateChanged.class, new StateChangedHandler());
	}

	/*
	 * Read the information from the NetworkManager and stores it in
	 * devices, also populate the cachedInterfaces map;
	 */
	private synchronized void readDevicesInfo() throws DBusException{
		LOG.entering(this.getClass().getName(),"readDevicesInfo");
		List<Path> devList = nm.GetDevices();
		for (Path devInterface : devList) {
			Device dev = readDeviceFromDbus(devInterface.getPath());
			devices.put(dev.getIface(), dev);
			LOG.info("Processing interface "+ dev.getIface());
			if(dev.isManaged()){
				try {
					GrcBoxInterface grcIface = device2grcBoxIface(dev);
					cachedInterfaces.put(grcIface.getName(), grcIface);
				} catch (ExecutionException e) {
					LOG.log(Level.WARNING,"Error processing interface "+dev.getIface(),e);
				}
			}
		}
		LOG.exiting(this.getClass().getName(), "readdevicesInfo");;
	}
	
	/*
	 * Convert a Device object into a GrcBoxInterface object
	 */
	private GrcBoxInterface device2grcBoxIface(Device dev) throws DBusException, ExecutionException{
		LOG.entering(this.getClass().getName(), "device2GrcBoxIface");
		GrcBoxInterface iface = new GrcBoxInterface();
		Properties devProp = (Properties) conn.getRemoteObject(_NM_IFACE, dev.getDbusPath(),  Properties.class);
		iface.setName(dev.getIface());
		

		GrcBoxInterface.Type type;

		/*
		 * Get the interface Type
		 */
		if(dev.getType().equals(NM_DEVICE_TYPE.WIFI)){
			UInt32 wifiMode = devProp.Get(_WIRELESS_IFACE, "Mode");
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
			if((boolean) devProp.Get(_DEVICE_IFACE, "Managed")){
				String ipAddr = getIpAddress(iface.getName());
				iface.setAddress(ipAddr);
				Path activeConnPath = (Path) devProp.Get(_DEVICE_IFACE, "ActiveConnection");
				Properties actConnProp = (Properties) conn.getRemoteObject(_NM_IFACE, activeConnPath.getPath(),  Properties.class);
				Path connPath = actConnProp.Get(_ACTIVE_IFACE, "Connection");
				Connection connIface = (Connection) conn.getRemoteObject(_NM_IFACE, connPath.getPath(),  Connection.class);
				Map<String,Map<String,Variant>> settings = connIface.GetSettings();
				String connection = (String)settings.get("connection").get("id").getValue();
				iface.setConnection(connection);
				
				Boolean isDefault = actConnProp.Get(_ACTIVE_IFACE, "Default");
				iface.setDefault(isDefault);
				
				/*
				 * We assume that devices are connected to Internet when a gateway is defined.
				 */
				Path ip4Path = (Path) devProp.Get(_DEVICE_IFACE, "Ip4Config");
				Properties ip4Prop = (Properties) conn.getRemoteObject(_NM_IFACE, ip4Path.getPath(),  Properties.class);
				Vector<Vector<UInt32>> addresses = ip4Prop.Get(_IP4CONFIG_IFACE, "Addresses");
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
				speed = devProp.Get(_WIRED_IFACE, "Speed");
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
		 * TODO Estimate the real cost in some way.
		 */
		iface.setCost(0);



		/*
		 * TODO Currently there is only support for Wifi or ethernet. Both  interfaces support multicast.
		 */
		iface.setMulticast(true);
		LOG.exiting(this.getClass().getName(), "device2GrcBoxIface", iface);
		return iface;
	}

	private Device readDeviceFromDbus(String path) throws DBusException{
		Device device = new Device();
		Properties props = (Properties) conn.getRemoteObject(_NM_IFACE, path,  Properties.class);
		device.setDbusPath(path);
		if(props instanceof Properties){
			
			Map<String, Variant> propsMap = props.GetAll(_DEVICE_IFACE);
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
				Properties ip4Prop = (Properties) conn.getRemoteObject(_NM_IFACE, device.getIp4Config(),  Properties.class);
				Vector<Vector<UInt32>> addresses = ip4Prop.Get(_IP4CONFIG_IFACE, "Addresses");
				UInt32 ip = addresses.get(0).get(0);
				device.setIfaceIpAddress(ip);
				LOG.info("iface IP " + ip);
			}
		}
		return device;
	}

	private boolean isNMAvailable() throws ExecutionException{
		try {
			conn = DBusConnection.getConnection(DBusConnection.SYSTEM);

			nmProp = (Properties)conn.getRemoteObject(_NM_IFACE, 
					_NM_PATH, 
					Properties.class);
			nm = (NetworkManagerIface)conn.getRemoteObject(_NM_IFACE, 
					_NM_PATH, 
					NetworkManagerIface.class);

			String version = nmProp.Get( _NM_IFACE, "Version");
			if(!version.equals(_VERSION_SUPPORTED)){
				LOG.severe("NM version not supported "+ version);
				throw new ExecutionException("Unsupported NetworkManager version", new Throwable());
			}
		} catch (DBusException e) {
			throw new ExecutionException("Error connecting to NetworkManager", e);
		}
		return true;
	}

	public synchronized int subscribeInterfaces(NetworkManagerListener object){
		ifaceSubscribers.add(object);
		return ifaceSubscribers.size();
	}
	
	public synchronized void unsubscribeInterfaces(int index){
		ifaceSubscribers.remove(index);
	}
	
	public synchronized void informInterfaceAdded(GrcBoxInterface iface){
		for (NetworkManagerListener networkManagerListener : ifaceSubscribers) {
			networkManagerListener.interfaceAdded(iface);
		}
	}

	public synchronized void informInterfaceRemoved(GrcBoxInterface iface){
		for (NetworkManagerListener networkManagerListener : ifaceSubscribers) {
			networkManagerListener.interfaceRemoved(iface);
		}
	}

	public synchronized void informInterfaceChanged(GrcBoxInterface iface){
		for (NetworkManagerListener networkManagerListener : ifaceSubscribers) {
			networkManagerListener.interfaceChanged(iface);
		}
	}

	public synchronized boolean isInitialized() {
		return initialized.booleanValue();
	}

	/*
	 * Returns the gateway associate to interface iface
	 */
	public String getGateway(String iface) {
		LOG.entering(this.getClass().getName(), "getGw");
		Device dev = devices.get(iface);
		Properties prop;
		String gwStr = null;
		if(dev.getState().equals(NM_DEVICE_STATE.ACTIVATED)){
			try {
				prop = (Properties) conn.getRemoteObject(_NM_IFACE, dev.getDbusPath(),  Properties.class);
				Path ip4Path = (Path) prop.Get(_DEVICE_IFACE, "Ip4Config");
				Properties ip4Prop = (Properties) conn.getRemoteObject(_NM_IFACE, ip4Path.getPath(),  Properties.class);
				Vector<Vector<UInt32>> addresses = ip4Prop.Get(_IP4CONFIG_IFACE, "Addresses");
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
	
	/*
	 * Convert from long to ipv4 String
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

	public String getIpAddress(String iface) {
		Device dev = devices.get(iface);
		return int2Ip(dev.getIfaceIpAddress().longValue());
	}
}