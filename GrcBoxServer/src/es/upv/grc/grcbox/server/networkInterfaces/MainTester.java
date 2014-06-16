package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class MainTester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainTester extends NetworkInterfaceManager
{
    private static NetworkInterfaceManager nim;
    public void getUpdates(String interfaceNames[])
    {
        if(interfaceNames != null)
        {
            System.out.println("NEW UPDATE RECEIVED");
            for(int i = 0; i < interfaceNames.length; i++)
            {
                System.out.println(interfaceNames[i] + nim.getType(interfaceNames[i])
                                + nim.getState(interfaceNames[i]) + nim.getIpAddress(interfaceNames[i])
                                + nim.getGatewayIp(interfaceNames[i]));
            }
            System.out.println();
        }
    }
    
    public static void main(String args[]) throws InterruptedException
    {
        nim = new MainTester();
        nim.registerForUpdates(nim);
        nim.start();
        Thread.sleep(60000);
        nim.interrupt();
        System.out.println("End");
    }
    
}
