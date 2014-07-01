package es.upv.grc.grcbox.common;

import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Write a description of class WifiModeEvaluator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class WifiModeEvaluator
{   
    private static final String colon = ":"; 
    private static final String lineWithMode = "802-11-wireless.mode";
    public static boolean isAdhoc(String interfaceName)
    {
        boolean response = false;
        boolean interfaceFound = false;
        String temp1 = null, temp2 = null;
        try
        {
            Process process1 = Runtime.getRuntime().exec(ShellCommands2.ConnectionUUID);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
            while ((temp1 = br1.readLine()) != null)
            {
                StringTokenizer str1 = new StringTokenizer(temp1, colon);
                String uuid = str1.nextToken();
                String name = str1.nextToken();
                if(interfaceName.compareTo(name) == 0)
                {
                    interfaceFound = true;
                    //now check the mode
                    Process process2 = Runtime.getRuntime().exec(ShellCommands2.WifiMode + uuid);
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                    while ((temp2 = br2.readLine()) != null)
                    {
                        if(temp2.contains(lineWithMode))
                        {
                            StringTokenizer str2 = new StringTokenizer(temp2, colon);
                            str2.nextToken(); //first token is "802-11-wireless.mode"
                            if(str2.nextToken().compareTo(KnownWifiModes.adhoc) == 0)
                            {
                                response = true;
                            }
                        }
                    }
                    temp2 = null;
                    process2.waitFor();
                    process2 = null;
                    br2 = null;
                }
            }
            temp1 = null;
			process1.waitFor();
			process1 = null;
			br1 = null;
        }
        catch(Exception e)
        {            
        }
        return response;
    }
}
