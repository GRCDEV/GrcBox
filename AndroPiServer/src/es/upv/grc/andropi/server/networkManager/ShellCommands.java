package es.upv.grc.andropi.server.networkManager;

public final class ShellCommands {
	
	public static final String VerifyNetworkManager = "nmcli -t -f RUNNING nm";
	public static final String RestartNetworkManager = "sudo service network-manager restart";
	public static final String ListInferfaces = "nmcli dev";
	public static final String DetailedInterfaceInformation = "nmcli dev list iface ";

}
