package es.upv.grc.grcbox.server.networkInterfaces;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.KnownInterfaceTypes;
import es.upv.grc.grcbox.common.KnownInterfaceStates;
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

    private LinkedList<GrcBoxInterface> interfaces;

    private LinkedList<NetworkManagerListener> registeredClasses;
    
    private LinkedList<GrcBoxInterface> updatedInterfaces;
    
    private LinkedList<GrcBoxInterface> removedInterfaces;

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
        synchronized(this) {
            isNetworkManagerWorking = false;
        }
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
                synchronized(this) {
                    isNetworkManagerWorking = true;
                }
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

    private LinkedList<GrcBoxInterface> getBasicInfoOfInterfaces() throws UnableToRunShellCommand
    {
        LinkedList<GrcBoxInterface> list = null;
        String line = null;
        try {
            Process process = Runtime.getRuntime().exec(ShellCommands.ListInferfaces);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            br.readLine(); //the first line is the heading so discard it
            while((line = br.readLine()) != null) {
                GrcBoxInterface iface = new GrcBoxInterface();
                StringTokenizer st = new StringTokenizer(line, space);
                iface.setName(st.nextToken());
                iface.setType(st.nextToken());
                iface.setState(st.nextToken());
                if(list == null)
                {
                    list = new LinkedList<GrcBoxInterface>();
                }
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

    private LinkedList<GrcBoxInterface> getIPInfoOfInterfaces(LinkedList<GrcBoxInterface> list) throws UnableToRunShellCommand
    {
        String temp = null;
        if(list == null || list.size() == 0)
        {
            return null;
        }
        try {
            for(int i = 0; i < list.size(); i++)
            {
                boolean interfaceIPSaved = false;
                Process process = Runtime.getRuntime().exec(ShellCommands.IpInformation + list.get(i).getName());
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((temp = br.readLine()) != null) {
                    if (temp.contains(theLineWithIP4)) {
                        //list.get(i).setIP4AddressAvailable(true);
                        StringTokenizer st = new StringTokenizer(temp, spaceAndSlash);
                        while (st.hasMoreTokens()) {
                            String s = st.nextToken();
                            if (s.contains(dot)) {
                                if (!s.contains(colon)) {
                                    // the first token of the string is discarded as it is "IP4.ADDRESS[1]:"
                                    if (interfaceIPSaved) {
                                        list.get(i).setGatewayIp(s);
                                    } else {
                                        list.get(i).setIpAddress(s);
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
    
    private LinkedList<GrcBoxInterface> deepCopy(LinkedList<GrcBoxInterface> list)
    {
        LinkedList<GrcBoxInterface> temp = null;
        if(list != null && list.size() > 0)
        {
            temp = new LinkedList<GrcBoxInterface>();
            for(int i = 0; i < list.size(); i++)
            {
                temp.add(list.get(i).cloneInterface());
            }
        }
        return temp;
    }
    
    public LinkedList<GrcBoxInterface> getListOfAllInterfaces() throws NetworkInterfaceManagerThreadNotRunning
    {
        if(manager == null || manager.getState() == Thread.State.NEW || manager.getState() == Thread.State.TERMINATED)
        {
            throw new NetworkInterfaceManagerThreadNotRunning();
        }
        synchronized(this) {
            return deepCopy(interfaces);
        }
    }
    
    private void checkInterfaceChanges(LinkedList<GrcBoxInterface> list)
    {
        if(interfaces == null && list == null)
        {
            return;
        }
        synchronized(this) {
            updatedInterfaces = null;
            removedInterfaces = null;
        }
        LinkedList<GrcBoxInterface> temp = deepCopy(list);
        synchronized(this) {
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
                    String name = interfaces.get(i).getName();
                    boolean deviceFound = false;
                    //now search for the same interface in the new list
                    for(int j = 0; j < list.size(); j++)
                    {
                        if(list.get(j).getName().compareTo(name) == 0)
                        {
                            deviceFound = true;                     
                            if(!interfaces.get(i).isEqual(list.get(j)))
                            {
                                if(updatedInterfaces == null)
                                {
                                    updatedInterfaces = new LinkedList<GrcBoxInterface>();
                                }
                                updatedInterfaces.add(list.get(j));
                            }
                            //removing is necessary as it will help in the end to detect devices that has been recently added
                            list.remove(j);                     
                            break; //you dont want it to go on checking
                        }
                    }
                    if(!deviceFound)
                    {
                        if(removedInterfaces == null)
                        {
                            removedInterfaces = new LinkedList<GrcBoxInterface>();
                        }
                        removedInterfaces.add(interfaces.get(i));
                    }
                }
                if(list.size() > 0)
                {
                    for(int i = 0; i < list.size(); i++)
                    {
                        if(updatedInterfaces == null)
                        {
                            updatedInterfaces = new LinkedList<GrcBoxInterface>();
                        }
                        updatedInterfaces.add(list.get(i));
                    }
                }           
            }   
            interfaces = temp;
        }
    }

    public String[] getListOfNetworkInterfaceNames() throws NetworkManagerNotRunning, UnableToRunShellCommand
    {
        synchronized(this) {
            if(interfaces != null && interfaces.size() > 0)
            {
                String temp[] = new String[interfaces.size()];
                for(int i = 0; i < temp.length; i++)
                {
                    temp[i] = interfaces.get(i).getName();
                }
                return temp;
            }
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
    public GrcBoxInterface.Type getType(String interfaceName)
    {
        synchronized(this) {
            if(interfaces != null)
            {
                for(int i = 0; i < interfaces.size(); i++)
                {
                    if(interfaceName.compareTo(interfaces.get(i).getName()) == 0)
                    {
                        return interfaces.get(i).getType();
                    }
                }
            }
        }
        return GrcBoxInterface.Type.UNKNOWN;
    }

    public GrcBoxInterface.State getState(String interfaceName)
    {
        synchronized(this) {
            if(interfaces != null)
            {
                for(int i = 0; i < interfaces.size(); i++)
                {
                    if(interfaceName.compareTo(interfaces.get(i).getName()) == 0)
                    {
                        return interfaces.get(i).getState();
                    }
                }
            }
        }
        return GrcBoxInterface.State.OTHERS;
    }

    public String getIpAddress(String interfaceName)
    {
        synchronized(this) {
            if(interfaces != null)
            {
                for(int i = 0; i < interfaces.size(); i++)
                {
                    if(interfaceName.compareTo(interfaces.get(i).getName()) == 0)
                    {
                        return interfaces.get(i).getIpAddress();
                    }
                }
            }
        }
        return null;
    }

    public String getGatewayIp(String interfaceName)
    {
        synchronized(this) {
            if(interfaces != null)
            {
                for(int i = 0; i < interfaces.size(); i++)
                {
                    if(interfaceName.compareTo(interfaces.get(i).getName()) == 0)
                    {
                        return interfaces.get(i).getGatewayIp();
                    }
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
            LinkedList<GrcBoxInterface> list = null;
            try{
                list =  getIPInfoOfInterfaces(getBasicInfoOfInterfaces());
            }
            catch(Exception e)
            {
                System.err.println("Class NetworkInterfaceManager: Error while accessing interface information!");
            }
            synchronized(this) {
                updatedInterfaces = removedInterfaces = null;
            }
            checkInterfaceChanges(list);
            synchronized(this) {
                if(updatedInterfaces != null || removedInterfaces != null)
                {
                    //results of recent scan is different from the previous results
                    //save changes and notify registered classes
                    if(registeredClasses != null)
                    {
                        String deviceNamesRemoved[] = null;                    
                        if(removedInterfaces != null && removedInterfaces.size() > 0)
                        {
                            deviceNamesRemoved = new String[removedInterfaces.size()];                  
                            for(int i = 0; i < removedInterfaces.size(); i++)
                            {
                                deviceNamesRemoved[i] = removedInterfaces.get(i).getName();
                            }
                        }
                        for(int i = 0; i < registeredClasses.size(); i++)
                        {
                            registeredClasses.get(i).getUpdatedDevices(deepCopy(updatedInterfaces)); //dont want to send the original list, but just a copy
                            registeredClasses.get(i).getRemovedDevices(deviceNamesRemoved);
                        }
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
        synchronized(this) {
            if(registeredClasses == null)
            {
                registeredClasses = new LinkedList<NetworkManagerListener>();
            }
            if(object != null){
                registeredClasses.add(object);
            }
        }
    }
}