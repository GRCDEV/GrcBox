package es.upv.grc.grcbox.common;


import com.fasterxml.jackson.annotation.*;

/**
 * The Class GrcBoxInterface.
 * This class represents a network interface in the GRCBox server.
 * Currently cost variable is not used.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GrcBoxInterface {
    
    /**
     * The Enum Type.
     */
    public enum Type{
        
        /** The wifista. */
        WIFISTA, 
 /** The wifiah. */
 WIFIAH, 
 /** The cellular. */
 CELLULAR, 
 /** The ethernet. */
 ETHERNET, 
 /** The wimax. */
 WIMAX, 
 /** The others. */
 OTHERS, 
 /** The unknown. */
 UNKNOWN
    }
    

    /** The name. */
    private String name;
    
    /** The address. */
    private String address;
    
    /** The type. */
    private Type type;
    
    /** The connection. */
    private String connection;
    
    /** The cost. */
    private double cost; //Cost per transfered MB
    
    /** The rate. */
    private double rate; //Rate in Mbps

    /** The is up. */
    private boolean isUp;
    
    /** The is multicast. */
    private boolean isMulticast;
    
    /** The has internet. */
    private boolean hasInternet;
    
    /** The is default. */
    private boolean isDefault;

    /** The Constant DEFAULT_COST. */
    private static final double DEFAULT_COST = 0;
    
    /**
     * Checks if the interface is connected to the Internet.
     *
     * @return true, if it is connected to the Internet
     */
    public boolean isHasInternet() {
		return hasInternet;
	}

	/**
	 * Instantiates a new grc box interface.
	 * Empty constructor required for jackson
	 */
	public GrcBoxInterface(){
        name = null;
        type = GrcBoxInterface.Type.UNKNOWN;
        setConnection(null);
        cost = DEFAULT_COST;
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Gets the address.
     *
     * @return the address
     */
    public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public GrcBoxInterface.Type getType()
    {
        return type;
    }
    
    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(GrcBoxInterface.Type type)
    {
        this.type = type;
    }
     
    /**
     * Gets the cost.
     *
     * @return the cost
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Sets the cost.
     *
     * @param cost the new cost
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     * Checks if is up.
     *
     * @return true, if is up
     */
    public boolean isUp()
    {
        return isUp;
    }
    
    /**
     * Sets the up.
     *
     * @param isUp the new up
     */
    public void setUp(boolean isUp)
    {
        this.isUp = isUp;
    }
    
    /**
     * Checks if is multicast.
     *
     * @return true, if is multicast
     */
    public boolean isMulticast()
    {
        return isMulticast;
    }
    
    /**
     * Sets the multicast.
     *
     * @param isMulticast the new multicast
     */
    public void setMulticast(boolean isMulticast)
    {
        this.isMulticast = isMulticast;
    }

    /**
     * Checks for internet.
     *
     * @return true, if successful
     */
    public boolean hasInternet()
    {
        return hasInternet;
    }

    /**
     * Sets the hasinternet.
     *
     * @param hasInternet the new hasinternet
     */
    public void setHasinternet(boolean hasInternet)
    {
        this.hasInternet = hasInternet;
    }

    /**
     * Checks if is default.
     *
     * @return true, if is default
     */
    public boolean isDefault() {
		return isDefault;
	}

	/**
	 * Sets the default.
	 *
	 * @param isDefault the new default
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * Sets the connection.
	 *
	 * @param connection the new connection
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}

	/**
	 * Gets the rate.
	 *
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}

	/**
	 * Sets the rate.
	 *
	 * @param rate the new rate
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}

	/**
	 * Equals.
	 *
	 * @param iface the iface
	 * @return true, if successful
	 */
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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