package org.freedesktop.NetworkManager.Constants;

				

				import org.freedesktop.dbus.UInt32;
				

				public class NM_DEVICE_STATE{
public static final UInt32 UNKNOWN= new UInt32(0);
public static final UInt32 UNMANAGED= new UInt32(10);
public static final UInt32 UNAVAILABLE= new UInt32(20);
public static final UInt32 DISCONNECTED= new UInt32(30);
public static final UInt32 PREPARE= new UInt32(40);
public static final UInt32 CONFIG= new UInt32(50);
public static final UInt32 NEED_AUTH= new UInt32(60);
public static final UInt32 IP_CONFIG= new UInt32(70);
public static final UInt32 IP_CHECK= new UInt32(80);
public static final UInt32 SECONDARIES= new UInt32(90);
public static final UInt32 ACTIVATED= new UInt32(100);
public static final UInt32 DEACTIVATING= new UInt32(110);
public static final UInt32 FAILED= new UInt32(120);
}
