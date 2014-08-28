package org.freedesktop.NetworkManager.Constants;

import org.freedesktop.dbus.UInt32;
public class NM_802_11_AP_SEC{
public static final UInt32 NONE= new UInt32(0x0);
public static final UInt32 PAIR_WEP40= new UInt32(0x1);
public static final UInt32 PAIR_WEP104= new UInt32(0x2);
public static final UInt32 PAIR_TKIP= new UInt32(0x4);
public static final UInt32 PAIR_CCMP= new UInt32(0x8);
public static final UInt32 GROUP_WEP40= new UInt32(0x10);
public static final UInt32 GROUP_WEP104= new UInt32(0x20);
public static final UInt32 GROUP_TKIP= new UInt32(0x40);
public static final UInt32 GROUP_CCMP= new UInt32(0x80);
public static final UInt32 KEY_MGMT_PSK= new UInt32(0x100);
public static final UInt32 KEY_MGMT_802_1X= new UInt32(0x200);
}
