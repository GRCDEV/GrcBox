package es.upv.grc.grcbox.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.server.networkInterfaces.*;

public class IfaceMonitor implements NetworkManagerListener{
	NetworkInterfaceManager nm;
	HashMap<String, Integer> nameIndex;
	boolean initialized;
	
	public IfaceMonitor(NetworkInterfaceManager nm) {
		super();
		initialized = false;
		this.nm = nm;
	}

	public HashMap<String, Integer> getNameIndexMap(){
		return nameIndex;
	}
	
	public void setNameIndexMap(HashMap<String, Integer> map){
		initialized = true;
		nameIndex = map;
	}

	private void updateDefaultRoute(GrcBoxInterface grcBoxInterface){
		
		String delRoute = "ip route del table "+nameIndex.get(grcBoxInterface.getName()) + " default ";
		try {
			System.out.println(delRoute);
			Runtime.getRuntime().exec(delRoute);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String iproute = "ip route add table "+ nameIndex.get(grcBoxInterface.getName()) + " default dev " + grcBoxInterface.getName() + " via " + grcBoxInterface.getGatewayIp();
		try {
			System.out.println(iproute);
			Runtime.getRuntime().exec(iproute);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getRemovedDevices(String[] interfaceNames) {
		/*
		 * If an interface has been removed
		 */
		// TODO Auto-generated method stub Future
		
	}

	@Override
	public void getUpdatedDevices(LinkedList<GrcBoxInterface> interfaces) {
		if(!interfaces.isEmpty())
		{
			System.out.println("Network interfaces changed");
			for (GrcBoxInterface grcBoxInterface : interfaces) {
				updateDefaultRoute(grcBoxInterface);
			}
			System.out.println();
		}
	}
	
}