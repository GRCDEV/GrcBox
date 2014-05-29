package es.upv.grc.andropi.server.networkManager;

import java.util.LinkedList;

public class NetworkTester {
	public static void main(String args[])
	{
		try{
			InterfaceManager.networkRestart();
			InterfaceManager.updateNetworkInterfaceInformation();
			LinkedList<NetworkInterface> ifaces = InterfaceManager.getNetworkInterfaceInformation();
			for(int i = 0; i < ifaces.size(); i++)
			{
				System.out.println(ifaces.get(i).getInterfaceName() + " " +
									ifaces.get(i).getInterfaceType() + " " +
									ifaces.get(i).getInterfaceState() + " " +
									ifaces.get(i).isIP4AddressAvailable() + " " +
									ifaces.get(i).getInterfaceIP4() + " " +
									ifaces.get(i).getGatewayIP4() + " ");
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
}
