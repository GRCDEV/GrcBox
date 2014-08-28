package org.freedesktop.NetworkManager.Constants;

				

				import org.freedesktop.dbus.UInt32;
				

				public class NM_DEVICE_STATE_REASON{
public static final UInt32 UNKNOWN= new UInt32(0);
public static final UInt32 NONE= new UInt32(1);
public static final UInt32 NOW_MANAGED= new UInt32(2);
public static final UInt32 NOW_UNMANAGED= new UInt32(3);
public static final UInt32 CONFIG_FAILED= new UInt32(4);
public static final UInt32 CONFIG_UNAVAILABLE= new UInt32(5);
public static final UInt32 CONFIG_EXPIRED= new UInt32(6);
public static final UInt32 NO_SECRETS= new UInt32(7);
public static final UInt32 SUPPLICANT_DISCONNECT= new UInt32(8);
public static final UInt32 SUPPLICANT_CONFIG_FAILED= new UInt32(9);
public static final UInt32 SUPPLICANT_FAILED= new UInt32(10);
public static final UInt32 SUPPLICANT_TIMEOUT= new UInt32(11);
public static final UInt32 PPP_START_FAILED= new UInt32(12);
public static final UInt32 PPP_DISCONNECT= new UInt32(13);
public static final UInt32 PPP_FAILED= new UInt32(14);
public static final UInt32 DHCP_START_FAILED= new UInt32(15);
public static final UInt32 DHCP_ERROR= new UInt32(16);
public static final UInt32 DHCP_FAILED= new UInt32(17);
public static final UInt32 SHARED_START_FAILED= new UInt32(18);
public static final UInt32 SHARED_FAILED= new UInt32(19);
public static final UInt32 AUTOIP_START_FAILED= new UInt32(20);
public static final UInt32 AUTOIP_ERROR= new UInt32(21);
public static final UInt32 AUTOIP_FAILED= new UInt32(22);
public static final UInt32 MODEM_BUSY= new UInt32(23);
public static final UInt32 MODEM_NO_DIAL_TONE= new UInt32(24);
public static final UInt32 MODEM_NO_CARRIER= new UInt32(25);
public static final UInt32 MODEM_DIAL_TIMEOUT= new UInt32(26);
public static final UInt32 MODEM_DIAL_FAILED= new UInt32(27);
public static final UInt32 MODEM_INIT_FAILED= new UInt32(28);
public static final UInt32 GSM_APN_FAILED= new UInt32(29);
public static final UInt32 GSM_REGISTRATION_NOT_SEARCHING= new UInt32(30);
public static final UInt32 GSM_REGISTRATION_DENIED= new UInt32(31);
public static final UInt32 GSM_REGISTRATION_TIMEOUT= new UInt32(32);
public static final UInt32 GSM_REGISTRATION_FAILED= new UInt32(33);
public static final UInt32 GSM_PIN_CHECK_FAILED= new UInt32(34);
public static final UInt32 FIRMWARE_MISSING= new UInt32(35);
public static final UInt32 REMOVED= new UInt32(36);
public static final UInt32 SLEEPING= new UInt32(37);
public static final UInt32 CONNECTION_REMOVED= new UInt32(38);
public static final UInt32 USER_REQUESTED= new UInt32(39);
public static final UInt32 CARRIER= new UInt32(40);
public static final UInt32 CONNECTION_ASSUMED= new UInt32(41);
public static final UInt32 SUPPLICANT_AVAILABLE= new UInt32(42);
public static final UInt32 MODEM_NOT_FOUND= new UInt32(43);
public static final UInt32 BT_FAILED= new UInt32(44);
public static final UInt32 GSM_SIM_NOT_INSERTED= new UInt32(45);
public static final UInt32 GSM_SIM_PIN_REQUIRED= new UInt32(46);
public static final UInt32 GSM_SIM_PUK_REQUIRED= new UInt32(47);
public static final UInt32 GSM_SIM_WRONG= new UInt32(48);
public static final UInt32 INFINIBAND_MODE= new UInt32(49);
public static final UInt32 DEPENDENCY_FAILED= new UInt32(50);
public static final UInt32 BR2684_FAILED= new UInt32(51);
public static final UInt32 MODEM_MANAGER_UNAVAILABLE= new UInt32(52);
public static final UInt32 SSID_NOT_FOUND= new UInt32(53);
public static final UInt32 SECONDARY_CONNECTION_FAILED= new UInt32(54);
public static final UInt32 DCB_FCOE_FAILED= new UInt32(55);
public static final UInt32 TEAMD_CONTROL_FAILED= new UInt32(56);
public static final UInt32 MODEM_FAILED= new UInt32(57);
public static final UInt32 MODEM_AVAILABLE= new UInt32(58);
public static final UInt32 SIM_PIN_INCORRECT= new UInt32(59);
}
