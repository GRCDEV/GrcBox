package org.freedesktop.NetworkManager;
import org.freedesktop.dbus.*;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("org.freedesktop.NetworkManager.Device") 
public interface DeviceInterface extends DBusInterface
{
   public static class StateChanged extends DBusSignal
   {
      public final UInt32 a;
      public final UInt32 b;
      public final UInt32 c;
      public StateChanged(String path, UInt32 a, UInt32 b, UInt32 c) throws DBusException
      {
         super(path, a, b, c);
         this.a = a;
         this.b = b;
         this.c = c;
      }
   }

  public void Disconnect();

}
