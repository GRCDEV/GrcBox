package es.upv.grc.grcbox.common;

public class ApAuth {
	private String password;
	private boolean autoconnect;
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the autoconnect
	 */
	public boolean isAutoconnect() {
		return autoconnect;
	}
	/**
	 * @param autoconnect the autoconnect to set
	 */
	public void setAutoconnect(boolean autoconnect) {
		this.autoconnect = autoconnect;
	}
	
}
