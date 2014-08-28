package org.freedesktop.NetworkManager.Constants;

import org.freedesktop.dbus.UInt32;
public class NM_SECRET_AGENT_GET_SECRETS_FLAGS{
public static final UInt32 NONE= new UInt32(0x0);
public static final UInt32 ALLOW_INTERACTION= new UInt32(0x1);
public static final UInt32 REQUEST_NEW= new UInt32(0x2);
public static final UInt32 USER_REQUESTED= new UInt32(0x4);
}
