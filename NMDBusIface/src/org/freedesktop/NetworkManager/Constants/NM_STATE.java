package org.freedesktop.NetworkManager.Constants;

				

				import org.freedesktop.dbus.UInt32;
				

				public class NM_STATE{
public static final UInt32 UNKNOWN= new UInt32(0);
public static final UInt32 ASLEEP= new UInt32(10);
public static final UInt32 DISCONNECTED= new UInt32(20);
public static final UInt32 DISCONNECTING= new UInt32(30);
public static final UInt32 CONNECTING= new UInt32(40);
public static final UInt32 CONNECTED_LOCAL= new UInt32(50);
public static final UInt32 CONNECTED_SITE= new UInt32(60);
public static final UInt32 CONNECTED_GLOBAL= new UInt32(70);
}
