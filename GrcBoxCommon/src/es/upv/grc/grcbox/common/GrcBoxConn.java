package es.upv.grc.grcbox.common;

/*
 * This connection represent an AP for WiFi networks
 * or a Network provider for 3g dongles.
 */
public class GrcBoxConn {
	enum AuthMethod{
		NONE,//Without authentication 
		SECRET,//A valid password
		NOT_SUPPORTED;//The method is not supported.
	}
	private String provider; //Name of the AP or the provider
	private boolean active;//True when the connection is active
	private boolean authCached;//Indicates whether the auth info was cached
	private AuthMethod method;//Indicates if a password is needed
	
	GrcBoxConn(){
	}

	public GrcBoxConn(String provider, boolean active, boolean authCached, AuthMethod method) {
		super();
		this.provider = provider;
		this.active = active;
		this.authCached = authCached;
		this.method = method;
	}

	public String getProvider() {
		return provider;
	}

	public boolean isAuthCached() {
		return authCached;
	}

	public boolean isActive() {
		return active;
	}

	public AuthMethod getMethod() {
		return method;
	}
}
