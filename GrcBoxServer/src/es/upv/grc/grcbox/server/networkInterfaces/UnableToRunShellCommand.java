package es.upv.grc.grcbox.server.networkInterfaces;


/**
 * Write a description of class e here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UnableToRunShellCommand extends Exception{

	private static final long serialVersionUID = 1L;

	public UnableToRunShellCommand(String message)
	{
		super(message);
	}
}
