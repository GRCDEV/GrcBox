package es.upv.grc.andropi.server.networkManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class NmInterface {
	private String install = "sudo apt-get install network-manager";
	private String verify = "nmcli -t -f RUNNING nm";
	private String restart_system = "sudo init 6";
	private String restart_nm = "sudo service network-manager restart";
	private String list_devices = "nmcli -t -f DEVICE dev";
	private String list_device_type = "nmcli -t -f TYPE dev";
	private String list_device_state = "nmcli -t -f STATE dev";
	private String IP4 = "nmcli dev list iface ";

	private boolean working = false;

	private LinkedList devices;
	private LinkedList device_type;
	private LinkedList device_state;
	private boolean device_list_available;

	private int selected_device = -1;

	private String interface_IP4;
	private String gateway_IP4;

	/**
	 * Constructor for objects of class RunCmd The function of the constructor
	 * is to check if the Network Manager is accessable. If Network Manager is
	 * not available, which may sometimes be the case, it tries to install the
	 * required packages. But unfortunately, it will not work if there is no
	 * internet connection. (to be added later)
	 */
	public NmInterface() {
		// initialise instance variables
		try {
			interface_IP4 = gateway_IP4 = null;
			Process p_test = Runtime.getRuntime().exec(verify);
			BufferedReader br_temp = new BufferedReader(new InputStreamReader(
					p_test.getInputStream()));
			if (br_temp.readLine().compareToIgnoreCase("running") == 0) {
				working = true;
				p_test.waitFor();
				p_test = null;
				br_temp = null;
			} else {
				System.err
						.println("Class RunCmdV1:The command \"nmcli\" is not working! Trying to install...");
				p_test.waitFor();
				p_test = null;
				br_temp = null;
				p_test = Runtime.getRuntime().exec(install);
				p_test.waitFor();
				p_test = null;
				p_test = Runtime.getRuntime().exec(verify);
				br_temp = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				if (br_temp.readLine().compareToIgnoreCase("running") == 0) {
					working = true;
				} else {
					System.err
							.println("Class RunCmdV1: method RunCmdV1(): Could not install!");
				}
				p_test.waitFor();
				p_test = null;
				br_temp = null;
			}
			device_list_available = false;
		} catch (Exception e) {
			System.err.println("Class RunCmdV1: method RunCmdV1(): "
					+ e.toString());
		}
	}

	/**
	 * A method to restart the Raspberry Pi device.
	 * 
	 * @param Not
	 *            required.
	 * @return boolean: true/ false, depending on execution status of the
	 *         method.
	 */
	public boolean system_restart() {
		try {
			Process p_test = Runtime.getRuntime().exec(restart_system);
			p_test = null;
			return true;
		} catch (Exception e) {
			System.err.println("Class RunCmdV1: method system_restart(): "
					+ e.toString());
			return false;
		}
	}

	/**
	 * A method to restart the Network Manager of the Raspberry Pi.
	 * 
	 * @param Not
	 *            required.
	 * @return boolean: true/ false, depending on execution status of the
	 *         method.
	 */
	public boolean network_restart() {
		try {
			Process p_test = Runtime.getRuntime().exec(restart_nm);
			p_test.waitFor();
			return true;
		} catch (Exception e) {
			System.err.println("Class RunCmdV1: method network_restart(): "
					+ e.toString());
			return false;
		}
	}

	/**
	 * A method to prepare an updated list the available Network Interfaces in
	 * the Raspberry Pi.
	 * 
	 * @param Not
	 *            required.
	 * @return boolean: true/ false, depending on execution status of the
	 *         method.
	 */
	public boolean update_device_list() {
		device_list_available = false;
		devices = device_type = device_state = null;
		devices = new LinkedList();
		device_type = new LinkedList();
		device_state = new LinkedList();

		String temp = null;
		if (working) {
			try {
				Process p_test = Runtime.getRuntime().exec(list_devices);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				while ((temp = br.readLine()) != null) {
					devices.add(temp);
				}
				temp = null;
				p_test.waitFor();
				p_test = null;
				br = null;
				p_test = Runtime.getRuntime().exec(list_device_type);
				br = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				while ((temp = br.readLine()) != null) {
					device_type.add(temp);
				}
				temp = null;
				p_test.waitFor();
				p_test = null;
				br = null;
				p_test = Runtime.getRuntime().exec(list_device_state);
				br = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				while ((temp = br.readLine()) != null) {
					device_state.add(temp);
				}
				temp = null;
				p_test.waitFor();
				p_test = null;
				br = null;
				device_list_available = true;
				return true;
			} catch (Exception e) {
				System.err
						.println("Class RunCmdV1: method update_device_list(): "
								+ e.toString());
			}
		}
		return false; // else
	}

	/**
	 * A method to access the list of available Network Interfaces in the
	 * Raspberry Pi. If an update function has to called if an updated list is
	 * required. But, for the first time/call, if updation is not done... It
	 * updates automatically.
	 * 
	 * @param Not
	 *            required.
	 * @return LinkList of Objects containing String values/ null, depending on
	 *         content of the list.
	 */
	public LinkedList get_device_names() {
		if (device_list_available) {
			return devices;
		} else {
			update_device_list();
			return devices;
		}
	}

	/**
	 * A method to access the list of Type of Network Interfaces in the
	 * Raspberry Pi. If an update function has to called if an updated list is
	 * required. But, for the first time/call, if updation is not done... It
	 * updates automatically.
	 * 
	 * @param Not
	 *            required
	 * @return LinkList of Objects containing String values/ null, depending on
	 *         content of the list.
	 */
	public LinkedList get_device_type() {
		if (device_list_available) {
			return device_type;
		} else {
			update_device_list();
			return device_type;
		}
	}

	/**
	 * A method to access the list of Status of Network Interfaces in the
	 * Raspberry Pi. If an update function has to called if an updated list is
	 * required. But, for the first time/call, if updation is not done... It
	 * updates automatically.
	 * 
	 * @param Not
	 *            required
	 * @return LinkList of Objects containing String values/ null, depending on
	 *         content of the list.
	 */
	public LinkedList get_device_status() {
		if (device_list_available) {
			return device_state;
		} else {
			update_device_list();
			return device_state;
		}
	}

	/**
	 * A method to reset the last selected device to control.
	 * 
	 * @param Not
	 *            required.
	 * @return void.
	 */
	public void reset_selected_device() {
		selected_device = -1;
		interface_IP4 = gateway_IP4 = null;
	}

	/**
	 * A method to select the Network Interfaces in the Raspberry Pi for
	 * controlling in the future.
	 * 
	 * @param integer
	 *            The position of device in the List returned by the
	 *            get_device_names() method.
	 * @return boolean: true/ false, depending on execution status of the
	 *         method.
	 */
	public boolean select_device(int pos) {
		reset_selected_device();
		if (devices == null) {
			return false;
		} else if (pos < 0 || pos >= devices.size()) {
			return false;
		} else {
			selected_device = pos;
			return true;
		}
	}

	/**
	 * A method to select the Network Interfaces in the Raspberry Pi for
	 * controlling in the future.
	 * 
	 * @param String
	 *            The name of device as defined in the List returned by the
	 *            get_device_names() method.
	 * @return boolean: true/ false, depending on execution status of the
	 *         method.
	 */
	public boolean select_device(String name) {
		reset_selected_device();
		if (devices == null) {
			return false;
		} else {
			selected_device = devices.indexOf((Object) name);
			return true;
		}
	}

	/**
	 * A method to select the Network Interfaces in the Raspberry Pi for
	 * controlling in the future.
	 * 
	 * @param String
	 *            Object The name of device as defined in the List returned by
	 *            the get_device_names() method.
	 * @return boolean: true/ false, depending on execution status of the
	 *         method.
	 */
	public boolean select_device(Object name) {
		reset_selected_device();
		if (devices == null) {
			return false;
		} else {
			selected_device = devices.indexOf(name);
			return true;
		}
	}

	/**
	 * A method to find the IP v.4 Address of the selected interface of the
	 * Raspberry Pi and its gateway.
	 * 
	 * @param Not
	 *            Required.
	 * @return boolean: true or false depending on the successful discovery of
	 *         the IP.
	 */
	public boolean findIP4() {
		boolean interfaceIP_saved = false;
		boolean success = false;
		if (selected_device != -1) {
			try {
				System.out.println(IP4 + devices.get(selected_device));
				Process p_test = Runtime.getRuntime().exec(
						IP4 + devices.get(selected_device));
				BufferedReader br = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) {
					if (output.contains("IP4.ADDRESS[1]:")) {
						System.out.println(output);
						StringTokenizer st = new StringTokenizer(output, " /");
						while (st.hasMoreTokens()) {
							String s = st.nextToken();
							if (s.contains(".")) {
								if (!s.contains(":")) {
									// the first token of the string is
									// discarded as it is "IP4.ADDRESS[1]:"
									if (interfaceIP_saved) {
										gateway_IP4 = s;
										success = true;
									} else {
										interface_IP4 = s;
										interfaceIP_saved = true;
									}
								}
							}
						}
					}
				}
				output = null;
				p_test.waitFor();
				p_test = null;
				br = null;
			} catch (Exception e) {
				System.err.println("Class RunCmdV1: method getIP4(): "
						+ e.toString());
			}
		}
		return success;
	}

	/**
	 * A method to find the IP v.4 Address of the selected interface of the
	 * Raspberry Pi and its gateway.
	 * 
	 * @param integer
	 *            The position of device in the List returned by the
	 *            get_device_names() method.
	 * @return boolean: true or false depending on the successful discovery of
	 *         the IP.
	 */
	public boolean findIP4(int device) {
		boolean interfaceIP_saved = false;
		boolean success = false;
		if (device >= 0 && device < devices.size()) {
			try {
				System.out.println(IP4 + devices.get(device));
				Process p_test = Runtime.getRuntime().exec(
						IP4 + devices.get(device));
				BufferedReader br = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) {
					if (output.contains("IP4.ADDRESS[1]:")) {
						System.out.println(output);
						StringTokenizer st = new StringTokenizer(output, " /");
						while (st.hasMoreTokens()) {
							String s = st.nextToken();
							if (s.contains(".")) {
								if (!s.contains(":")) {
									// the first token of the string is
									// discarded as it is "IP4.ADDRESS[1]:"
									if (interfaceIP_saved) {
										gateway_IP4 = s;
										success = true;
									} else {
										interface_IP4 = s;
										interfaceIP_saved = true;
									}
								}
							}
						}
					}
				}
				output = null;
				p_test.waitFor();
				p_test = null;
				br = null;
			} catch (Exception e) {
				System.err.println("Class RunCmdV1: method getIP4(): "
						+ e.toString());
			}
		}
		return success;
	}

	/**
	 * A method to find the IP v.4 Address of the selected interface of the
	 * Raspberry Pi and its gateway.
	 * 
	 * @param String
	 *            The name of device as defined in the List returned by the
	 *            get_device_names() method.
	 * @return boolean: true or false depending on the successful discovery of
	 *         the IP.
	 */
	public boolean findIP4(String deviceName) {
		boolean interfaceIP_saved = false;
		boolean success = false;
		int device_pos = devices.indexOf((Object) deviceName);
		if (device_pos != -1) {
			try {
				System.out.println(IP4 + deviceName);
				Process p_test = Runtime.getRuntime().exec(IP4 + deviceName);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						p_test.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) {
					if (output.contains("IP4.ADDRESS[1]:")) {
						System.out.println(output);
						StringTokenizer st = new StringTokenizer(output, " /");
						while (st.hasMoreTokens()) {
							String s = st.nextToken();
							if (s.contains(".")) {
								if (!s.contains(":")) {
									// the first token of the string is
									// discarded as it is "IP4.ADDRESS[1]:"
									if (interfaceIP_saved) {
										gateway_IP4 = s;
										success = true;
									} else {
										interface_IP4 = s;
										interfaceIP_saved = true;
									}
								}
							}
						}
					}
				}
				output = null;
				p_test.waitFor();
				p_test = null;
				br = null;
			} catch (Exception e) {
				System.err.println("Class RunCmdV1: method getIP4(): "
						+ e.toString());
			}
		}
		return success;
	}

	/**
	 * A method to find the IP v.4 Address of the selected interface of the
	 * Raspberry Pi.
	 * 
	 * @param Not
	 *            Required.
	 * @return String: IP Address in the format ABC.EFG.HIJ or null if address
	 *         unavailable.
	 */
	public String getInterfaceIP4() {
		return interface_IP4;
	}

	/**
	 * A method to find the IP v.4 Address of the gateway of the selected
	 * interface in the Raspberry Pi.
	 * 
	 * @param Not
	 *            Required.
	 * @return String: IP Address in the format ABC.EFG.HIJ or null if address
	 *         unavailable.
	 */
	public String getGatewayIP4() {
		return gateway_IP4;
	}

}
