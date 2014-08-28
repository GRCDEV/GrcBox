package org.freedesktop.NetworkManager.Constants;

				

				import org.freedesktop.dbus.UInt32;
				

				public class NM_VPN_CONNECTION_STATE{
public static final UInt32 UNKNOWN= new UInt32(0);
public static final UInt32 PREPARE= new UInt32(1);
public static final UInt32 NEED_AUTH= new UInt32(2);
public static final UInt32 CONNECT= new UInt32(3);
public static final UInt32 IP_CONFIG_GET= new UInt32(4);
public static final UInt32 ACTIVATED= new UInt32(5);
public static final UInt32 FAILED= new UInt32(6);
public static final UInt32 DISCONNECTED= new UInt32(7);
}
