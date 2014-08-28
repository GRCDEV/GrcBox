package org.freedesktop.NetworkManager;
import java.util.List;
import java.util.Map;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
@DBusInterfaceName("org.freedesktop.NetworkManager.Settings") 
public interface SettingsInterface extends DBusInterface
{
   public static class NewConnection extends DBusSignal
   {
      public final DBusInterface a;
      public NewConnection(String path, DBusInterface a) throws DBusException
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

  public void SaveHostname(String hostname);
  public DBusInterface AddConnection(Map<String,Map<String,Variant>> connection);
  public DBusInterface GetConnectionByUuid(String uuid);
  public List<DBusInterface> ListConnections();

}
