package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class NetworkInterfaceManagerThreadNotRunning here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NetworkInterfaceManagerThreadNotRunning extends Exception
{
   
    public NetworkInterfaceManagerThreadNotRunning()
    {
       super("The NetworkInterfaceManager Thread has either not been started or has been terminated.");
    }
}
