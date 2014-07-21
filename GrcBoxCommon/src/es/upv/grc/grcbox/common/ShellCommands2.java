package es.upv.grc.grcbox.common;


/**
 * Write a description of class ShellCommands2 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public final class ShellCommands2 {
    
    public static final String ConnectionUUID = "nmcli -t -f UUID,DEVICE con show";
    public static final String WifiMode = "nmcli -t -f 802-11-wireless con show uuid ";
    //public static final String WifiMode = "nmcli con list uuid ";
}
