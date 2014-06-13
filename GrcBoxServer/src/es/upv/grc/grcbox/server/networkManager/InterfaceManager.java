package es.upv.grc.grcbox.server.networkManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class InterfaceManager{

	private static boolean isNetworkManagerWorking;

	private static LinkedList<NetworkInterface> interfaces = null;

	private static final String NetworkManagerRunning = "running";
	
	private static final String space = " ";
	private static final String theLineWithIP4 = "IP4.ADDRESS[1]:";
	private static final String spaceAndSlash = " /";
	private static final String dot = ".";
	private static final String colon = ":";
	
	private static void checkNetworkManager() throws NetworkManagerNotRunning, UnableToRunShellCommand{
		isNetworkManagerWorking = false;
		try {
			Process process = Runtime.getRuntime().exec(ShellCommands.VerifyNetworkManager);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			if(br.readLine().compareToIgnoreCase(NetworkManagerRunning) != 0) {
				process.waitFor();
				process = null;
				br = null;
				throw new NetworkManagerNotRunning("Exception: The NetworkManager has been checked if running using shell commands." +
						" It has been found that the Network Manager is not running. Please check if it has been properly " +
						"installed or else restart the Network Manager!");
			}
			else
			{
				process = null;
				br = null;
				isNetworkManagerWorking = true;
			}
		}catch(Exception e) {
			throw new UnableToRunShellCommand("Exception: Unable to run the shell command to verify if Network Manager is running!");
		}
	}


	public static void networkRestart() throws UnableToRunShellCommand{
		try {
			Process process = Runtime.getRuntime().exec(ShellCommands.RestartNetworkManager);
			process.waitFor();
		} catch(Exception e) {
			throw new UnableToRunShellCommand("Exception: Unable to restart the Network Mananger!");
		}
	}
	
	private static void getBasicInfoOfInterfaces() throws UnableToRunShellCommand
	{
		interfaces = new LinkedList<NetworkInterface>();
		String temp = null;
		try {
			Process process = Runtime.getRuntime().exec(ShellCommands.ListInferfaces);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			br.readLine(); //the first line is the heading so discard it
			while ((temp = br.readLine()) != null) {
				NetworkInterface iface = new NetworkInterface();
				StringTokenizer st = new StringTokenizer(temp, space);
				iface.setInterfaceName(st.nextToken());
				iface.setInterfaceType(st.nextToken());
				iface.setInterfaceState(st.nextToken());
				interfaces.add(iface);
			}
		}
		catch(Exception e)
		{
			throw new UnableToRunShellCommand("Exception: Unable to find/update the interface information!");
		}
	}
	
	private static void getIPInfoOfInterfaces() throws UnableToRunShellCommand
	{
		String temp = null;
		try {
			for(int i = 0; i < interfaces.size(); i++)
			{
				boolean interfaceIPSaved = false;
				Process process = Runtime.getRuntime().exec(ShellCommands.DetailedInterfaceInformation + interfaces.get(i).getInterfaceName());
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((temp = br.readLine()) != null) {
					if (temp.contains(theLineWithIP4)) {
						interfaces.get(i).setIP4AddressAvailable(true);
						StringTokenizer st = new StringTokenizer(temp, spaceAndSlash);
						while (st.hasMoreTokens()) {
							String s = st.nextToken();
							if (s.contains(dot)) {
								if (!s.contains(colon)) {
									// the first token of the string is discarded as it is "IP4.ADDRESS[1]:"
									if (interfaceIPSaved) {
										interfaces.get(i).setGatewayIP4(s);
									} else {
										interfaces.get(i).setInterfaceIP4(s);
										interfaceIPSaved = true;
									}
								}
							}
						}
					}
				}
				temp = null;
				process.waitFor();
				process = null;
				br = null;
			}			
		}
		catch(Exception e)
		{
			throw new UnableToRunShellCommand("Exception: Unable to find/update the interface information!");
		}
	}
	
	public static void updateNetworkInterfaceInformation() throws NetworkManagerNotRunning, UnableToRunShellCommand
	{
		checkNetworkManager();		
		if(isNetworkManagerWorking)
		{
			getBasicInfoOfInterfaces();
			//the names, type and states of the network interfaces has been found, now find the ip addresses
			getIPInfoOfInterfaces();
		}
	}
	
	public static LinkedList<NetworkInterface> getNetworkInterfaceInformation()
	{
		return interfaces;
	}
	
}