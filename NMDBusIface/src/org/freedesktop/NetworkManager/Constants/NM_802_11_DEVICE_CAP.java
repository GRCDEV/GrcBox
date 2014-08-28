package org.freedesktop.NetworkManager.Constants;

import org.freedesktop.dbus.UInt32;
public class NM_802_11_DEVICE_CAP{
public static final UInt32 NONE= new UInt32(0x0);
public static final UInt32 CIPHER_WEP40= new UInt32(0x1);
public static final UInt32 CIPHER_WEP104= new UInt32(0x2);
public static final UInt32 CIPHER_TKIP= new UInt32(0x4);
public static final UInt32 CIPHER_CCMP= new UInt32(0x8);
public static final UInt32 WPA= new UInt32(0x10);
public static final UInt32 RSN= new UInt32(0x20);
public static final UInt32 AP= new UInt32(0x40);
public static final UInt32 ADHOC= new UInt32(0x80);
}
