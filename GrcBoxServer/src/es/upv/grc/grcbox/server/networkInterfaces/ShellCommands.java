package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class s here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

public final class ShellCommands {
    
    public static final String VerifyNetworkManager = "nmcli -t -f RUNNING nm";
    public static final String RestartNetworkManager = "sudo service network-manager restart";
    public static final String ListInferfaces = "nmcli dev";
    //public static final String DetailedInterfaceInformation = "nmcli dev list iface ";
    public static final String IpInformation = "nmcli -t -f IP4 dev list iface ";
}
