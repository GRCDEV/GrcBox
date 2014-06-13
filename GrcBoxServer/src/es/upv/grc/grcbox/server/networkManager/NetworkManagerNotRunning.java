package es.upv.grc.grcbox.server.networkManager;

public class NetworkManagerNotRunning extends Exception{

	private static final long serialVersionUID = 1L;

	public NetworkManagerNotRunning(String message)
	{
		super(message);
	}
}
