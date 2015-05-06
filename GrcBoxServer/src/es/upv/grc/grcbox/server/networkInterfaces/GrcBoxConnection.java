package es.upv.grc.grcbox.server.networkInterfaces;

import org.freedesktop.dbus.Path;

import es.upv.grc.grcbox.common.GrcBoxSsid;

public class GrcBoxConnection extends GrcBoxSsid {
	private Path dbusPath;
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Path getDbusPath() {
		return dbusPath;
	}

	public void setDbusPath(Path dbusPath) {
		this.dbusPath = dbusPath;
	}
	
}
