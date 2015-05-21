package es.upv.grc.grcbox.server.networkInterfaces;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Path;

import es.upv.grc.grcbox.common.GrcBoxSsid;

public class GrcBoxConnection extends GrcBoxSsid {
	private DBusInterface dBusInterface;
	private String password;

	public GrcBoxConnection(GrcBoxSsid ssid, DBusInterface dBusInterface, String password) {
		super(ssid);
		this.dBusInterface = dBusInterface;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public DBusInterface getDbusInterface() {
		return dBusInterface;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dBusInterface == null) ? 0 : dBusInterface.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GrcBoxConnection other = (GrcBoxConnection) obj;
		if (dBusInterface == null) {
			if (other.dBusInterface != null) {
				return false;
			}
		} else if (!dBusInterface.equals(other.dBusInterface)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		return true;
	}
	
}
