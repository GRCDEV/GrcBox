package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class MainTester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainTester
{
    public static void main(String args[]) throws InterruptedException
    {
        NetworkInterfaceManager nim = new NetworkInterfaceManager();
        NMListener nml = new NMListener();
        nml.start(nim);
        Thread.sleep(60000);
        nim.interrupt();
        System.out.println("End");
    }
}

