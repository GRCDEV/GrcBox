package es.upv.grc.grcbox.common;


import com.fasterxml.jackson.annotation.*;

/*
 * This class represents a network interface in the GRCBox server.
 * Currently cost variable is not used.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxInterface {
    
    public enum Type{
        WIFISTA, WIFIAH, CELLULAR, ETHERNET, WIMAX, OTHERS, UNKNOWN
    }
    

    private String name;
    private String address;
    private Type type;
    private String connection;
    
    private double cost; //Cost per transfered MB
    private double rate; //Rate in Mbps

    private boolean isUp;
    private boolean isMulticast;
    private boolean hasInternet;
    private boolean isDefault;

    private static final double DEFAULT_COST = 0;
    
    public boolean isHasInternet() {
		return hasInternet;
	}

	public void setHasInternet(boolean hasInternet) {
		this.hasInternet = hasInternet;
	}

	public GrcBoxInterface(){
        name = null;
        type = GrcBoxInterface.Type.UNKNOWN;
        setConnection(null);
        cost = DEFAULT_COST;
    }
    
	
    public GrcBoxInterface(String name, String address, GrcBoxInterface.Type type, String connection, 
    					   double cost, boolean isUp, 
                           boolean isMulticast, boolean hasInternet, boolean isDefault)
    {
        super();
        this.name = name;
        this.address = address;
        this.type = type;
        this.connection = connection;
        this.cost = cost;
        this.isUp = isUp;
        this.isMulticast = isMulticast;
        this.hasInternet = hasInternet;
        this.isDefault = isDefault;
    }        
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public GrcBoxInterface.Type getType()
    {
        return type;
    }
    
    public void setType(GrcBoxInterface.Type type)
    {
        this.type = type;
    }
     
    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
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

    public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public boolean equals(GrcBoxInterface iface)
    {
        if(!getName().equals(iface.getName()))
        {
            return false;
        }
        
        if(getType() != iface.getType())
        {
            return false;
        }
        
         
        if(getCost() != iface.getCost())
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
	
	public String toString(){
		return this.name + ":" +
				" Address:" + this.address +
				" Type:" + this.type +
				" Connection:" + this.connection +
				" Cost:" + this.cost +
				" IsUp:" + this.isUp +
				" Multicast:" + this.isMulticast +
				" HasInternet:" + this.hasInternet +
				" IsDefault:" + this.isDefault; 
	}
}