package org.freedesktop;
import java.util.List;
import java.util.Map;

import org.freedesktop.dbus.*;
import org.freedesktop.dbus.exceptions.*;

@DBusInterfaceName("org.freedesktop.NetworkManager") 
public interface NetworkManagerIface extends DBusInterface
{
   public static final String _WIRELESS_IFACE = "org.freedesktop.NetworkManager.Device.Wireless";
   public static final String _WIRED_IFACE = "org.freedesktop.NetworkManager.Device.Wired";
   public static final String _DEVICE_IFACE = "org.freedesktop.NetworkManager.Device";
   public static final String _IP4CONFIG_IFACE = "org.freedesktop.NetworkManager.IP4Config";
   public static final String _ACTIVE_IFACE = "org.freedesktop.NetworkManager.Connection.Active";
   public static final String _AP_IFACE = "org.freedesktop.NetworkManager.AccessPoint";
   public static final String _CONNECTION_IFACE = "org.freedesktop.NetworkManager.Settings.Connection";
   public static final String _SETTINGS_IFACE = "org.freedesktop.NetworkManager.Settings";
   public static final String _NM_IFACE = "org.freedesktop.NetworkManager";
   
   public static final String _NM_PATH = "/org/freedesktop/NetworkManager";
	
   public static class DeviceRemoved extends DBusSignal
   {
      public final DBusInterface a;
      public DeviceRemoved(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class DeviceAdded extends DBusSignal
   {
      public final DBusInterface a;
      public DeviceAdded(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class PropertiesChanged extends DBusSignal
   {
      public final Map<String,Variant> a;
      public PropertiesChanged(String path, Map<String,Variant> a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class StateChanged extends DBusSignal
   {
      public final UInt32 a;
      public StateChanged(String path, UInt32 a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class CheckPermissions extends DBusSignal
   {
      public CheckPermissions(String path) throws DBusException
      {
         super(path);
      }
   }

  public UInt32 state();
  public Pair<String, String> GetLogging();
  public void SetLogging(String level, String domains);
  public Map<String,String> GetPermissions();
  public void Enable(boolean enable);
  public void Sleep(boolean sleep);
  public void DeactivateConnection(DBusInterface active_connection);
  public Pair<DBusInterface, DBusInterface> AddAndActivateConnection(Map<String,Map<String,Variant>> connection, DBusInterface device, DBusInterface specific_object);
  public DBusInterface ActivateConnection(DBusInterface connection, DBusInterface device, DBusInterface specific_object);
  public DBusInterface GetDeviceByIpIface(String iface);
  public List<Path> GetDevices();

}
