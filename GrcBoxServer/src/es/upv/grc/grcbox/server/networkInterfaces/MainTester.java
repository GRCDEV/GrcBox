package es.upv.grc.grcbox.server.networkInterfaces;

import java.util.LinkedList;
import es.upv.grc.grcbox.common.GrcBoxInterface;
/**
 * Write a description of class MainTester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainTester implements NetworkManagerListener
{
    private static NetworkInterfaceManager manager;
    
    public void getUpdatedDevices(LinkedList<GrcBoxInterface> interfaces)
    {
        if(interfaces != null)
        {
            System.out.println("NEW UPDATE RECEIVED: Device Updated");
            for(int i = 0; i < interfaces.size(); i++)
            {
                System.out.println(interfaces.get(i).getName() + interfaces.get(i).getType()
                                + interfaces.get(i).getState() + interfaces.get(i).getIpAddress()
                                + interfaces.get(i).getGatewayIp());
            }
            System.out.println();
        }
    }
    
    public void getRemovedDevices(String interfaceNames[])
    {
        if(interfaceNames != null)
        {
            System.out.println("NEW UPDATE RECEIVED: Device REMOVED");
            for(int i = 0; i < interfaceNames.length; i++)
            {
                System.out.println(interfaceNames[i] + manager.getType(interfaceNames[i])
                                + manager.getState(interfaceNames[i]) + manager.getIpAddress(interfaceNames[i])
                                + manager.getGatewayIp(interfaceNames[i]));
            }
            System.out.println();
        }
    }
    
    public static void main(String args[]) throws InterruptedException
    {
        LinkedList<GrcBoxInterface> interfaces;
        MainTester tester = new MainTester();
        manager = NetworkInterfaceManager.getObject();
        System.out.println("Trying to get a list of all interfaces before running the thread:");
        try{
            interfaces = manager.getListOfAllInterfaces();
            for(int i = 0; i < interfaces.size(); i++)
            {
                System.out.print(" " + interfaces.get(i).getName());
            }
            System.out.println();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        manager.registerForUpdates(tester);
        manager.start(); //to start the thread
        Thread.sleep(60000);
        System.out.println("Trying to get a list of all interfaces while running the thread:");
        try{
            interfaces = manager.getListOfAllInterfaces();
            for(int i = 0; i < interfaces.size(); i++)
            {
                System.out.print(" " + interfaces.get(i).getName());
            }
            System.out.println();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        manager.interrupt(); //to stop the thread
        System.out.println("End");
        manager.join();
        System.out.println("Trying to get a list of all interfaces after running the thread:");
        try{
            interfaces = manager.getListOfAllInterfaces();
            for(int i = 0; i < interfaces.size(); i++)
            {
                System.out.print(" " + interfaces.get(i).getName());
            }
            System.out.println();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
    
}
