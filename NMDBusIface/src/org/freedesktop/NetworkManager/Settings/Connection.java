package org.freedesktop.NetworkManager.Settings;
import java.util.Map;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Connection extends DBusInterface
{
   public static class PropertiesChanged extends DBusSignal
   {
      public final Map<String,Variant> a;
      public PropertiesChanged(String path, Map<String,Variant> a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class Removed extends DBusSignal
   {
      public Removed(String path) throws DBusException
      {
         super(path);
      }
   }
   public static class Updated extends DBusSignal
   {
      public Updated(String path) throws DBusException
      {
         super(path);
      }
   }

  public void Save();
  public Map<String,Map<String,Variant>> GetSecrets(String setting_name);
  public Map<String,Map<String,Variant>> GetSettings();
  public void Delete();
  public void UpdateUnsaved(Map<String,Map<String,Variant>> properties);
  public void Update(Map<String,Map<String,Variant>> properties);

}
