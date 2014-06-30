package es.upv.grc.grcbox.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class GrcBoxInterface {
    
    public enum Type{
        WIFISTA, WIFIAH, CELLULAR, ETHERNET, WIMAX, WIFIP, OTHERS, UNKNOWN
    }
    
    public enum State
    {
        CONNECTED, DISCONNECTED, UNMANAGED, OTHERS, UNKNOWN
    }

    private String name;    
    private GrcBoxInterface.Type type;
    private GrcBoxInterface.State state;
    private String ipAddress;
    private String gatewayIp;
    
    private double cost; //Cost per transfered MB

    private int index;
    private int mtu;    

    private boolean isLoopback;
    private boolean isUp;
    private boolean isMulticast;
    private boolean hasInternet;
    
    private static final double DEFAULT_COST = 100;
    private static final int DEFAULT_INDEX = 100;
    private static final int DEFAULT_MTU = 100;
    private static final boolean DEFAULT_IS_LOOPBACK = false;
    private static final boolean DEFAULT_IS_UP = false;
    private static final boolean DEFAULT_IS_MULTICAST = false;
    private static final boolean DEFAULT_HAS_INTERNET = false;
    
    public GrcBoxInterface(){
        name = null;
        type = GrcBoxInterface.Type.UNKNOWN;
        state = GrcBoxInterface.State.UNKNOWN;
        ipAddress = null;
        gatewayIp = null;
        cost = DEFAULT_COST;
        index = DEFAULT_INDEX;
        mtu = DEFAULT_MTU;
        isLoopback = DEFAULT_IS_LOOPBACK;
        isUp = DEFAULT_IS_UP;
        isMulticast = DEFAULT_IS_MULTICAST;
        hasInternet = DEFAULT_HAS_INTERNET;
    }
    
    public GrcBoxInterface(String name, GrcBoxInterface.Type type, GrcBoxInterface.State state,
                            String ipAddress, String gatewayIp, double cost, int index, int mtu,
                            boolean isLoopback, boolean isUp, boolean isMulticast, boolean hasInternet)
    {
        super();
        this.name = name;
        this.type = type;
        this.state = state;
        this.ipAddress = ipAddress;
        this.gatewayIp = gatewayIp;
        this.cost = cost;
        this.index = index;
        this.mtu = mtu;
        this.isLoopback = isLoopback;
        this.isUp = isUp;
        this.isMulticast = isMulticast;
        this.hasInternet = hasInternet;
    }        
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public GrcBoxInterface.Type getType()
    {
        return type;
    }
    
    public void setType(GrcBoxInterface.Type type)
    {
        this.type = type;
    }
    
    public void setType(String type)
    {
        if(type.equalsIgnoreCase(KnownInterfaceTypes.wifi))
        {
            //here we need to check what type of wifi connection it is
            this.type = GrcBoxInterface.Type.WIFISTA;            
        }
        else if(type.equalsIgnoreCase(KnownInterfaceTypes.ethernet))
        {
            this.type = GrcBoxInterface.Type.ETHERNET;
        }
        else
        {
            this.type = GrcBoxInterface.Type.OTHERS;
        }
    }
    
    public GrcBoxInterface.State getState()
    {
        return state;
    }
    
    public void setState(GrcBoxInterface.State state)
    {
        this.state = state;
    }
    
    public void setState(String state)
    {
        if(state.equalsIgnoreCase(KnownInterfaceStates.connected))
        {
            this.state = GrcBoxInterface.State.CONNECTED;
        }
        else if(state.equalsIgnoreCase(KnownInterfaceStates.disconnected))
        {
            this.state = GrcBoxInterface.State.DISCONNECTED;
        }
        else if(state.equalsIgnoreCase(KnownInterfaceStates.unmanaged))
        {
            this.state = GrcBoxInterface.State.UNMANAGED;
        }
        else
        {
            this.state = GrcBoxInterface.State.OTHERS;
        }
    }
    
    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getGatewayIp()
    {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp)
    {
        this.gatewayIp = gatewayIp;
    }
    
    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    public int getIndex()
    {
        return index;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }
    
    public int getMtu()
    {
        return mtu;
    }
    
    public void setMtu(int mtu)
    {
        this.mtu = mtu;
    }
        
    public boolean isLoopback()
    {
        return isLoopback;
    }
    
    public void setLoopback(boolean isLoopback)
    {
        this.isLoopback = isLoopback;
    }
    
    public boolean isUp()
    {
        return isUp;
    }
    
    public void setUp(boolean isUp)
    {
        this.isUp = isUp;
    }
    
    public boolean isMulticast()
    {
        return isMulticast;
    }
    
    public void setMulticast(boolean isMulticast)
    {
        this.isMulticast = isMulticast;
    }

    public boolean hasInternet()
    {
        return hasInternet;
    }

    public void setHasinternet(boolean hasInternet)
    {
        this.hasInternet = hasInternet;
    }
    
    public GrcBoxInterface cloneInterface()
    {
        return new GrcBoxInterface(getName(), getType(), getState(), getIpAddress(),
                                   getGatewayIp(), getCost(), getIndex(), getMtu(),                                   
                                   isLoopback(), isUp(), isMulticast(), hasInternet());
    }
    
    public boolean isEqual(GrcBoxInterface iface)
    {
        if(getName().compareTo(iface.getName()) != 0)
        {
            return false;
        }
        
        if(getType() != iface.getType())
        {
            return false;
        }
        
        if(getState() != iface.getState())
        {
            return false;
        }
        
        if(getIpAddress() == null)
        {
            if(iface.getIpAddress() != null)
            {
                return false;
            }
        }
        else
        {
            //has proper Ip
            if(iface.getIpAddress() == null)
            {
                return false;
            }
            if(getIpAddress().compareTo(iface.getIpAddress()) != 0)
            {
                return false;
            }
        }
        
        if(getGatewayIp() == null)
        {
            if(iface.getGatewayIp() != null)
            {
                return false;
            }
        }
        else
        {
            //has proper Ip
            if(iface.getGatewayIp() == null)
            {
                return false;
            }
            if(getIpAddress().compareTo(iface.getIpAddress()) != 0)
            {
                return false;
            }
        }
        
        if(getCost() != iface.getCost())
        {
            return false;
        }
        
        if(getIndex() != iface.getIndex())
        {
            return false;
        }
        
        if(getMtu() != iface.getMtu())
        {
            return false;
        }
        
        if(isLoopback() != iface.isLoopback)
        {
            return false;
        }
        
        if(isUp() != iface.isUp())
        {
            return false;
        }
        
        if(isMulticast() != iface.isMulticast())
        {
            return false;
        }
        
        if(hasInternet() != iface.hasInternet())
        {
            return false;
        }
        return true;
    }
}