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
 * @version 0.0.5 using Threads
 */

public abstract class NetworkInterfaceManager extends Thread
{
    
    private static boolean isNetworkManagerWorking = false;

    private static LinkedList<NetworkInterface> interfaces = null;
    
    private static LinkedList<NetworkInterfaceManager> registeredClasses = null;
   
    private static final String NetworkManagerRunning = "running";
    
    private static final String space = " ";
    private static final String theLineWithIP4 = "IP4.ADDRESS[1]:";
    private static final String spaceAndSlash = " /";
    private static final String dot = ".";
    private static final String colon = ":";
    
    private static final int UPDATE_INTERVAL = 10000;

    
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
    
    
    private boolean compareWithPreviousList(LinkedList<NetworkInterface> list)
    {
        if(interfaces == null && list != null)
        {
            return false;
        }
        else if(interfaces.size() != list.size())
        {
            return false;
        }
        else
        {
            for(int i = 0; i < interfaces.size(); i++)
            {
                String name = interfaces.get(i).getInterfaceName();
                boolean found = false;
                //now search for the same interface in the new list
                for(int j = 0; j < list.size(); j++)
                {
                    if(list.get(j).getInterfaceName().compareToIgnoreCase(name) == 0)
                    {
                        found = true;
                        if(interfaces.get(i).getInterfaceType() != list.get(j).getInterfaceType())
                        {
                            return false;
                        }
                        else if(interfaces.get(i).getInterfaceState() != list.get(j).getInterfaceState())
                        {
                            return false;
                        }
                        else if(interfaces.get(i).isIP4AddressAvailable() != list.get(j).isIP4AddressAvailable())
                        {
                            return false;
                        }
                        else
                        {
                            if(interfaces.get(i).getInterfaceIP4() == null && list.get(j).getInterfaceIP4() == null)
                            {
                                //do nothing
                            }
                            else if(interfaces.get(i).getInterfaceIP4() == null && list.get(j).getInterfaceIP4() != null)
                            {
                                return false;
                            }
                            else if(interfaces.get(i).getInterfaceIP4() != null && list.get(j).getInterfaceIP4() == null)
                            {
                                return false;
                            }
                            else if(interfaces.get(i).getInterfaceIP4().compareToIgnoreCase(list.get(j).getInterfaceIP4()) != 0)
                            {
                                return false;
                            }
                            else
                            {}
                            
                            if(interfaces.get(i).getGatewayIP4() == null && list.get(j).getGatewayIP4() == null)
                            {
                                //do nothing
                            }
                            else if(interfaces.get(i).getGatewayIP4() == null && list.get(j).getGatewayIP4() != null)
                            {
                                return false;
                            }
                            else if(interfaces.get(i).getGatewayIP4() != null && list.get(j).getGatewayIP4() == null)
                            {
                                return false;
                            }
                            else if(interfaces.get(i).getGatewayIP4().compareToIgnoreCase(list.get(j).getGatewayIP4()) != 0)
                            {
                                return false;
                            }
                            else
                            {}
                        }                        
                        break; //you dont want it to go on checking
                    }
                }
                if(!found)
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String[] getListOfNetworkInterfaceNames() throws NetworkManagerNotRunning, UnableToRunShellCommand
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
            if(!compareWithPreviousList(list))
            {
                //results of recent scan is different from the previous results
                //save changes and notify registered classes
                interfaces = list;
                String updatedInterfaceNames[] = null;
                try{
                     updatedInterfaceNames = getListOfNetworkInterfaceNames();
                }
                catch(Exception e)
                {
                    System.err.println("Class NetworkInterfaceManager: Error while parsing interface names.");
                }
                if(registeredClasses != null)
                {
                    for(int i = 0; i < registeredClasses.size(); i++)
                    {
                        registeredClasses.get(i).getUpdates(updatedInterfaceNames);
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
    
    public void registerForUpdates(NetworkInterfaceManager object)
    {
        if(registeredClasses == null)
        {
            registeredClasses = new LinkedList<NetworkInterfaceManager>();
        }
        registeredClasses.add(object);
    }
    
    public abstract void getUpdates(String interfaceNames[]);
}
