package es.upv.grc.grcbox.server.networkInterfaces;

import java.util.LinkedList;
import es.upv.grc.grcbox.common.GrcBoxInterface;
/**
 * Write a description of interface NetworkManagerListener here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface NetworkManagerListener
{
    /**
     * An example of a method header - replace this comment with your own
     * 
     * @param  y    a sample parameter for a method
     * @return        the result produced by sampleMethod 
     */
    public abstract void getUpdatedDevices(LinkedList<GrcBoxInterface> interfaces);
    
    public abstract void getRemovedDevices(String interfaceNames[]);
}
