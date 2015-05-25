package es.upv.grc.grcbox.server.networkInterfaces;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Path;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.upv.grc.grcbox.common.GrcBoxSsid;

public class GrcBoxConnection extends GrcBoxSsid {
	private DBusInterface dBusInterface;
	private String password;

	public GrcBoxConnection(GrcBoxSsid ssid, DBusInterface dBusInterface, String password) {
		super(ssid);
		this.dBusInterface = dBusInterface;
		this.password = password;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	
	@JsonIgnore
	public DBusInterface getDbusInterface() {
		return dBusInterface;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
}
