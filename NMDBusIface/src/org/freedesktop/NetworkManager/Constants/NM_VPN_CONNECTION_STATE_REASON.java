package org.freedesktop.NetworkManager.Constants;

				

				import org.freedesktop.dbus.UInt32;
				

				public class NM_VPN_CONNECTION_STATE_REASON{
public static final UInt32 UNKNOWN= new UInt32(0);
public static final UInt32 NONE= new UInt32(1);
public static final UInt32 USER_DISCONNECTED= new UInt32(2);
public static final UInt32 DEVICE_DISCONNECTED= new UInt32(3);
public static final UInt32 SERVICE_STOPPED= new UInt32(4);
public static final UInt32 IP_CONFIG_INVALID= new UInt32(5);
public static final UInt32 CONNECT_TIMEOUT= new UInt32(6);
public static final UInt32 SERVICE_START_TIMEOUT= new UInt32(7);
public static final UInt32 SERVICE_START_FAILED= new UInt32(8);
public static final UInt32 NO_SECRETS= new UInt32(9);
public static final UInt32 LOGIN_FAILED= new UInt32(10);
public static final UInt32 CONNECTION_REMOVED= new UInt32(11);
}
