package es.upv.grc.grcbox.common;


/**
 * Write a description of class ShellCommands2 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public final class ShellCommands2 {
    
    public static final String ConnectionUUID = "nmcli -t -f UUID,DEVICES con status";
    public static final String WifiMode = "nmcli -t -f 802-11-wireless con list uuid ";
    //public static final String WifiMode = "nmcli con list uuid ";
    
}
