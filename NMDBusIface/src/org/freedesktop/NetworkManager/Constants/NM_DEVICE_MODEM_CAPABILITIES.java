package org.freedesktop.NetworkManager.Constants;

import org.freedesktop.dbus.UInt32;
public class NM_DEVICE_MODEM_CAPABILITIES{
public static final UInt32 NONE= new UInt32(0x0);
public static final UInt32 POTS= new UInt32(0x1);
public static final UInt32 CDMA_EVDO= new UInt32(0x2);
public static final UInt32 GSM_UMTS= new UInt32(0x4);
public static final UInt32 LTE= new UInt32(0x8);
}
