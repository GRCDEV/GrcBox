package org.freedesktop.NetworkManager.Constants;

import org.freedesktop.dbus.UInt32;

public final class NM_802_11_MODE {
	public static final UInt32 UNKNOWN= new UInt32(0);
	public static final UInt32 ADHOC= new UInt32(1);
	public static final UInt32 INFRA= new UInt32(2);
	public static final UInt32 AP= new UInt32(3);
}
