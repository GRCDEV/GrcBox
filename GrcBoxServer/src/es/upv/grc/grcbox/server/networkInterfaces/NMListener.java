package es.upv.grc.grcbox.server.networkInterfaces;

import java.util.LinkedList;
import es.upv.grc.grcbox.common.GrcBoxInterface;
/**
 * Write a description of class NMListener here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NMListener implements NetworkManagerListener{
    NetworkInterfaceManager manager;    
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
    
    public void start(NetworkInterfaceManager nm)
    {
        this.manager = nm;
        this.manager.registerForUpdates(this);
        this.manager.start();
    }
}