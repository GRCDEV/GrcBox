package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class MainTester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainTester implements NetworkManagerListener
{
    private static NetworkInterfaceManager manager;
    public void getUpdates(String interfaceNames[])
    {
        if(interfaceNames != null)
        {
            System.out.println("NEW UPDATE RECEIVED");
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
        MainTester tester = new MainTester();
        manager = NetworkInterfaceManager.getObject();
        manager.registerForUpdates(tester);
        manager.start(); //to start the thread
        Thread.sleep(60000);
        manager.interrupt(); //to stop the thread
        System.out.println("End");
    }
    
}
