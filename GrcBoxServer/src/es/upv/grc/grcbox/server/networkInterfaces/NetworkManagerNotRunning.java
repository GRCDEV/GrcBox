package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class c here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NetworkManagerNotRunning extends Exception{

	private static final long serialVersionUID = 1L;

	public NetworkManagerNotRunning(String message)
	{
		super(message);
	}
}
