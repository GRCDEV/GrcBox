package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class NMListener here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NMListener implements NetworkManagerListener{
    NetworkInterfaceManager manager;    
    public void getUpdatedDevices(String interfaceNames[])
    {
        if(interfaceNames != null)
        {
            System.out.println("NEW UPDATE RECEIVED: Device Updated");
            for(int i = 0; i < interfaceNames.length; i++)
            {
                System.out.println(interfaceNames[i] + manager.getType(interfaceNames[i])
                                + manager.getState(interfaceNames[i]) + manager.getIpAddress(interfaceNames[i])
                                + manager.getGatewayIp(interfaceNames[i]));
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