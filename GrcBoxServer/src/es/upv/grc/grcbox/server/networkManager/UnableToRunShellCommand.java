package es.upv.grc.grcbox.server.networkManager;

public class UnableToRunShellCommand extends Exception{

	private static final long serialVersionUID = 1L;

	public UnableToRunShellCommand(String message)
	{
		super(message);
	}
}
