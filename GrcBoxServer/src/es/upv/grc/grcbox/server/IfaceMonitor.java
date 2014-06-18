package es.upv.grc.grcbox.server;

import java.io.IOException;
import java.util.HashMap;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.server.networkInterfaces.*;



public class IfaceMonitor implements NetworkManagerListener{
	NetworkInterfaceManager nm;
	HashMap<String, Integer> nameIndex;
	
	public IfaceMonitor(NetworkInterfaceManager nm) {
		super();
		this.nm = nm;
	}

	public void getUpdates(String interfaceNames[])
	{
		if(interfaceNames != null)
		{
			System.out.println("Network interfaces changed");
			for (String string : interfaceNames) {
				updateDefaultRoute(string);
			}
			System.out.println();
		}
	}
	
	private void updateDefaultRoute(String ifaceName){
		
		String delRoute = "ip route del table "+nameIndex.get(ifaceName) + " default ";
//		try {
			System.out.println(delRoute);
//			Runtime.getRuntime().exec(delRoute);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String iproute = "ip route add table "+ nameIndex.get(ifaceName) + "default dev " + ifaceName + " via " + nm.getGatewayIp(ifaceName);
//		try {
			System.out.println(iproute);
//			//Runtime.getRuntime().exec(iproute);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}