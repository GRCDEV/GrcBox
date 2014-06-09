package es.upv.grc.andropi.server.networkManager;

import es.upv.grc.andropi.server.networkManager.Enumerators.InterfaceState;
import es.upv.grc.andropi.server.networkManager.Enumerators.InterfaceType;

public class NetworkInterface {
	
	private String name;
	private InterfaceType type;
	private InterfaceState state;
	private boolean isIP4Available;
	private String interfaceIP4;
	private String gatewayIP4;

	public NetworkInterface()
	{
		name = null;
		type = InterfaceType.UNKNOWN;
		state = InterfaceState.UNKNOWN;
		isIP4Available = false;
		interfaceIP4 = null;
		gatewayIP4 = null;		
	}
	
	public void setInterfaceName(String name)
	{
		this.name = name;
	}
	
	public String getInterfaceName()
	{
		return name;
	}
	
	public void setInterfaceType(String type)
	{
		if(type.equalsIgnoreCase(KnownInterfaceTypes.wifi))
		{
			this.type = InterfaceType.WIFI;
		}
		else if(type.equalsIgnoreCase(KnownInterfaceTypes.ethernet))
		{
			this.type = InterfaceType.ETHERNET;
		}
		else
		{
			this.type = InterfaceType.OTHERS;
		}
	}
	
	public InterfaceType getInterfaceType()
	{
		return type;
	}
	
	public void setInterfaceState(String state)
	{
		if(state.equalsIgnoreCase(KnownInterfaceStates.connected))
		{
			this.state = InterfaceState.CONNECTED;
		}
		else if(state.equalsIgnoreCase(KnownInterfaceStates.disconnected))
		{
			this.state = InterfaceState.DISCONNECTED;
		}
		else if(state.equalsIgnoreCase(KnownInterfaceStates.unmanaged))
		{
			this.state = InterfaceState.UNMANAGED;
		}
		else
		{
			this.state = InterfaceState.OTHERS;
		}
	}
	
	public InterfaceState getInterfaceState()
	{
		return state;
	}
	
	public void setIP4AddressAvailable(boolean bool)
	{
		isIP4Available = bool;
	}
	
	public boolean isIP4AddressAvailable()
	{
		return isIP4Available;		
	}
	
	public void setInterfaceIP4(String addr)
	{
		interfaceIP4 = addr;
	}
	
	public String getInterfaceIP4()
	{
		return interfaceIP4;
	}
	
	public void setGatewayIP4(String addr)
	{
		gatewayIP4 = addr;
	}
	
	public String getGatewayIP4()
	{
		return gatewayIP4;
	}
}
