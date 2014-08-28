package org.freedesktop.NetworkManager.Device;
import java.util.List;
import java.util.Map;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Wireless extends DBusInterface
{
   public static class AccessPointRemoved extends DBusSignal
   {
      public final DBusInterface a;
      public AccessPointRemoved(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class AccessPointAdded extends DBusSignal
   {
      public final DBusInterface a;
      public AccessPointAdded(String path, DBusInterface a) throws DBusException
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

  public void RequestScan(Map<String,Variant> options);
  public List<DBusInterface> GetAccessPoints();

}
