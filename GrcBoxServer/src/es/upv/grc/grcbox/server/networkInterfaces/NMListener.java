package es.upv.grc.grcbox.server.networkInterfaces;


public class NMListener implements NetworkManagerListener{
	NetworkInterfaceManager nm;
	public void getUpdates(String interfaceNames[])
	{
		if(interfaceNames != null)
		{
			System.out.println("NEW UPDATE RECEIVED");
			for(int i = 0; i < interfaceNames.length; i++)
			{
				System.out.println(interfaceNames[i] + nm.getType(interfaceNames[i])
						+ nm.getState(interfaceNames[i]) + nm.getIpAddress(interfaceNames[i])
						+ nm.getGatewayIp(interfaceNames[i]));
			}
			System.out.println();
		}
	}
	
	public void start(NetworkInterfaceManager nm){
		this.nm = nm;
		this.nm.registerForUpdates(this);
        this.nm.start();
	}
}