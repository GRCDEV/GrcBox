package es.upv.grc.andropi.server.networkManager;

public class NetworkManagerNotRunning extends Exception{

	private static final long serialVersionUID = 1L;

	public NetworkManagerNotRunning(String message)
	{
		super(message);
	}
}
