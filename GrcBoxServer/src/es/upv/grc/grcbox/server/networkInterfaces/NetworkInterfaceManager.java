package es.upv.grc.grcbox.server.networkInterfaces;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

import es.upv.grc.grcbox.server.networkInterfaces.Enumerators.*;
import es.upv.grc.grcbox.server.networkInterfaces.UnableToRunShellCommand;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkManagerNotRunning;

/**
 * Abstract class NetworkInterfaceManager - write a description of the class here
 * 
 * @author Subhadeep Patra
 * @version 0.0.6 using Threads
 */


/*
 * TODO
 * Please, use the types I created under GrcBoxInterface
 */
public class NetworkInterfaceManager extends Thread
{
    private static NetworkInterfaceManager manager = null;

	private boolean isNetworkManagerWorking;

	private LinkedList<NetworkInterface> interfaces;

	private LinkedList<NetworkManagerListener> registeredClasses;
	
	private LinkedList<NetworkInterface> updatedInterfaces;
	
	private LinkedList<NetworkInterface> removedInterfaces;

	private static final String NetworkManagerRunning = "running";

	private static final String space = " ";
	private static final String theLineWithIP4 = "IP4.ADDRESS[1]:";
	private static final String spaceAndSlash = " /";
	private static final String dot = ".";
	private static final String colon = ":";

	private static final int UPDATE_INTERVAL = 10000;

	private NetworkInterfaceManager()
	{
	    isNetworkManagerWorking = false;
	    interfaces = null;
	    registeredClasses = null;
	    updatedInterfaces = null;
	    removedInterfaces = null;
	}
	
	public static NetworkInterfaceManager getObject()
	{
	    if(manager == null || manager.getState() == Thread.State.TERMINATED)
	    {
	        manager = new NetworkInterfaceManager();
	    }
	    return manager;
	}
	
	
	/**
	 * Method to restart the network manager of the system
	 * 
	 * @param   None                        Not required
	 * @return  void                        Nothing
	 * @throws  UnableToRunShellCommand     An exception showing that the command could not be run
	 * 
	 */

	public void networkRestart() throws UnableToRunShellCommand{
		try {
			Process process = Runtime.getRuntime().exec(ShellCommands.RestartNetworkManager);
			process.waitFor();
		} catch(Exception e) {
			throw new UnableToRunShellCommand("Exception: Unable to restart the Network Mananger!");
		}
	}


	/**
	 * Method to check the activity of the system network manager
	 * 
	 * @param   None                        Not required
	 * @return  void                        Nothing
	 * @throws  NetworkManagerNotRunning    An exception showing that the system network manager is not running
	 * @throws  UnableToRunShellCommand     An exception showing that the command could not be run
	 * 
	 */

	private void checkNetworkManager() throws NetworkManagerNotRunning, UnableToRunShellCommand{
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


	/**
	 * Method to get the list of available network interfaces, their names, types and state.
	 * 
	 * @param   None                        Not required
	 * @return  LinkedList                  List of NetworkInterfaces
	 * @throws  UnableToRunShellCommand     An exception showing that the command could not be run
	 * 
	 */

	private LinkedList<NetworkInterface> getBasicInfoOfInterfaces() throws UnableToRunShellCommand
	{
		LinkedList<NetworkInterface> list = new LinkedList<NetworkInterface>();
		String line = null;
		try {
			Process process = Runtime.getRuntime().exec(ShellCommands.ListInferfaces);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			br.readLine(); //the first line is the heading so discard it
			while((line = br.readLine()) != null) {
				NetworkInterface iface = new NetworkInterface();
				StringTokenizer st = new StringTokenizer(line, space);
				iface.setInterfaceName(st.nextToken());
				iface.setInterfaceType(st.nextToken());
				iface.setInterfaceState(st.nextToken());
				list.add(iface);
			}
		}
		catch(Exception e)
		{
			throw new UnableToRunShellCommand("Exception: Unable to find/update the interface information!");
		}
		return list;
	}


	/**
	 * Method to get the IP addresses of the network interfaces and the gateways being used.
	 * 
	 * @param   LinkedList                  List of NetworkInterfaces
	 * @return  LinkedList                  List of NetworkInterfaces with IP information updated
	 * @throws  UnableToRunShellCommand     An exception showing that the command could not be run
	 * 
	 */

	private LinkedList<NetworkInterface> getIPInfoOfInterfaces(LinkedList<NetworkInterface> list) throws UnableToRunShellCommand
	{
		String temp = null;
		if(list == null)
		{
			return null;
		}
		try {
			for(int i = 0; i < list.size(); i++)
			{
				boolean interfaceIPSaved = false;
				Process process = Runtime.getRuntime().exec(ShellCommands.DetailedInterfaceInformation + list.get(i).getInterfaceName());
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((temp = br.readLine()) != null) {
					if (temp.contains(theLineWithIP4)) {
						list.get(i).setIP4AddressAvailable(true);
						StringTokenizer st = new StringTokenizer(temp, spaceAndSlash);
						while (st.hasMoreTokens()) {
							String s = st.nextToken();
							if (s.contains(dot)) {
								if (!s.contains(colon)) {
									// the first token of the string is discarded as it is "IP4.ADDRESS[1]:"
									if (interfaceIPSaved) {
										list.get(i).setGatewayIP4(s);
									} else {
										list.get(i).setInterfaceIP4(s);
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
		return list;
	}
	
	private LinkedList<NetworkInterface> deepCopy(LinkedList<NetworkInterface> list)
	{
	    LinkedList<NetworkInterface> temp = null;
	    if(list != null)
	    {
	        temp = new LinkedList<NetworkInterface>();
	        for(int i = 0; i < list.size(); i++)
	        {
	            NetworkInterface iface = new NetworkInterface();
	            iface.setInterfaceName(list.get(i).getInterfaceName());
	            iface.setInterfaceType(list.get(i).getInterfaceType());
	            iface.setInterfaceState(list.get(i).getInterfaceState());
	            iface.setIP4AddressAvailable(list.get(i).isIP4AddressAvailable());
	            iface.setInterfaceIP4(list.get(i).getInterfaceIP4());
	            iface.setGatewayIP4(list.get(i).getGatewayIP4());
	            temp.add(iface);
	        }
	    }
	    return temp;
	}

	private void checkInterfaceChanges(LinkedList<NetworkInterface> list)
	{
	    updatedInterfaces = new LinkedList<NetworkInterface>();
	    removedInterfaces = new LinkedList<NetworkInterface>();
	    LinkedList<NetworkInterface> temp = deepCopy(list);
		if(interfaces == null && list != null)
		{
		    //the previous list did not have any devices so only updation possible.
		    updatedInterfaces = list;
		}
		else if(interfaces != null && list == null)
		{
		    //all of the interfaces has been removed
		    removedInterfaces = interfaces;
		}
		else
		{
		    //both the list of interfaces: previous and current has elements	
		    for(int i = 0; i < interfaces.size(); i++)
			{
				String name = interfaces.get(i).getInterfaceName();
				boolean deviceFound = false, deviceUpdated = false;
				//now search for the same interface in the new list
				for(int j = 0; j < list.size(); j++)
				{
					if(list.get(j).getInterfaceName().compareToIgnoreCase(name) == 0)
					{
						deviceFound = true;
						if(interfaces.get(i).getInterfaceType() != list.get(j).getInterfaceType())
						{
							deviceUpdated = true;
						}
						else if(interfaces.get(i).getInterfaceState() != list.get(j).getInterfaceState())
						{
							deviceUpdated = true;
						}
						else if(interfaces.get(i).isIP4AddressAvailable() != list.get(j).isIP4AddressAvailable())
						{
							deviceUpdated = true;
						}
						else
						{
							if(interfaces.get(i).getInterfaceIP4() == null && list.get(j).getInterfaceIP4() == null)
							{
								//do nothing
							}
							else if(interfaces.get(i).getInterfaceIP4() == null && list.get(j).getInterfaceIP4() != null)
							{
								deviceUpdated = true;
							}
							else if(interfaces.get(i).getInterfaceIP4() != null && list.get(j).getInterfaceIP4() == null)
							{
								deviceUpdated = true;
							}
							else if(interfaces.get(i).getInterfaceIP4().compareToIgnoreCase(list.get(j).getInterfaceIP4()) != 0)
							{
								deviceUpdated = true;
							}
							else
							{}

							if(interfaces.get(i).getGatewayIP4() == null && list.get(j).getGatewayIP4() == null)
							{
								//do nothing
							}
							else if(interfaces.get(i).getGatewayIP4() == null && list.get(j).getGatewayIP4() != null)
							{
								deviceUpdated = true;
							}
							else if(interfaces.get(i).getGatewayIP4() != null && list.get(j).getGatewayIP4() == null)
							{
								deviceUpdated = true;
							}
							else if(interfaces.get(i).getGatewayIP4().compareToIgnoreCase(list.get(j).getGatewayIP4()) != 0)
							{
								deviceUpdated = true;
							}
							else
							{}
						}
						if(deviceUpdated)
						{
						    updatedInterfaces.add(list.get(j));
						}
						//removing is necessary as it will help in the end to detect devices that has been recently added
						list.remove(j);						
						break; //you dont want it to go on checking
					}
				}
				if(!deviceFound)
				{
					removedInterfaces.add(interfaces.get(i));
				}
			}
			if(list.size() > 0)
			{
			    for(int i = 0; i < list.size(); i++)
			    {
			        updatedInterfaces.add(list.get(i));
			    }
			}			
		}	
		interfaces = temp;
	}

	public String[] getListOfNetworkInterfaceNames() throws NetworkManagerNotRunning, UnableToRunShellCommand
	{        
		if(interfaces != null)
		{
			String temp[] = new String[interfaces.size()];
			for(int i = 0; i < temp.length; i++)
			{
				temp[i] = interfaces.get(i).getInterfaceName();
			}
			return temp;
		}        
		return null;
	}

	/*
	 * TODO
	 * Add support for WIFI Ad-hoc networking. The wifi mode appears on
	 * nmcli dev wifi list iface <ifName>
	 * At the third column, it says "Ifrastructure" or "Ad-Hoc"
	 * The eighth column indicates if the connection is active
	 */
	public InterfaceType getType(String interfaceName)
	{
		if(interfaces != null)
		{
			for(int i = 0; i < interfaces.size(); i++)
			{
				if(interfaceName.compareToIgnoreCase(interfaces.get(i).getInterfaceName()) == 0)
				{
					return interfaces.get(i).getInterfaceType();
				}
			}
		}        
		return InterfaceType.UNKNOWN;
	}

	public InterfaceState getState(String interfaceName)
	{
		if(interfaces != null)
		{
			for(int i = 0; i < interfaces.size(); i++)
			{
				if(interfaceName.compareToIgnoreCase(interfaces.get(i).getInterfaceName()) == 0)
				{
					return interfaces.get(i).getInterfaceState();
				}
			}
		}        
		return InterfaceState.OTHERS;
	}

	public String getIpAddress(String interfaceName)
	{
		if(interfaces != null)
		{
			for(int i = 0; i < interfaces.size(); i++)
			{
				if(interfaceName.compareToIgnoreCase(interfaces.get(i).getInterfaceName()) == 0)
				{
					return interfaces.get(i).getInterfaceIP4();
				}
			}
		}        
		return null;
	}

	public String getGatewayIp(String interfaceName)
	{
		if(interfaces != null)
		{
			for(int i = 0; i < interfaces.size(); i++)
			{
				if(interfaceName.compareToIgnoreCase(interfaces.get(i).getInterfaceName()) == 0)
				{
					return interfaces.get(i).getGatewayIP4();
				}
			}
		}        
		return null;
	}

	public void run()
	{
		try
		{
			checkNetworkManager();
		}
		catch(Exception e)
		{
			System.err.println("Class NetworkInterfaceManager: Error! Network Manager could not be contacted.");
		}
		while(isNetworkManagerWorking) 
		{
			LinkedList<NetworkInterface> list = null;
			try{
				list =  getBasicInfoOfInterfaces();
				list =  getIPInfoOfInterfaces(list);
			}
			catch(Exception e)
			{
				System.err.println("Class NetworkInterfaceManager: Error while accessing interface information!");
			}
			updatedInterfaces = removedInterfaces = null;
			checkInterfaceChanges(list);			
			if(updatedInterfaces != null || removedInterfaces != null)
			{
				//results of recent scan is different from the previous results
				//save changes and notify registered classes
				if(registeredClasses != null)
				{
				    String deviceNamesUpdated[] = null, deviceNamesRemoved[] = null;
				    if(updatedInterfaces.size() > 0)
				    {
				        deviceNamesUpdated = new String[updatedInterfaces.size()];
				        for(int i = 0; i < updatedInterfaces.size(); i++)
				        {
				            deviceNamesUpdated[i] = updatedInterfaces.get(i).getInterfaceName();
				        }
				    }
				    if(removedInterfaces.size() > 0)
				    {
				        deviceNamesRemoved = new String[removedInterfaces.size()];				    
				        for(int i = 0; i < removedInterfaces.size(); i++)
				        {
				            deviceNamesRemoved[i] = removedInterfaces.get(i).getInterfaceName();
				        }
				    }
					for(int i = 0; i < registeredClasses.size(); i++)
					{
						registeredClasses.get(i).getUpdatedDevices(deviceNamesUpdated);
						registeredClasses.get(i).getRemovedDevices(deviceNamesRemoved);
					}
				}
			}
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt(); // very important
				break;
			}
			list = null;
		}
		System.out.println("Shutting down thread");
	}

	public void registerForUpdates(NetworkManagerListener object)
	{
		if(registeredClasses == null)
		{
			registeredClasses = new LinkedList<NetworkManagerListener>();
		}
		if(object != null){
			registeredClasses.add(object);
		}
	}
}