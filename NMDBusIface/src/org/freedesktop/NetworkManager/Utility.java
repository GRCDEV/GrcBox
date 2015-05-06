package org.freedesktop.NetworkManager;

public class Utility {
	/*
	 * Return a valid uuid generated from a string
	 */
	public static String generateUuid(String value){
		/*Very dirty*/
		String valueHash = Integer.toString(Math.abs(value.hashCode()));
		valueHash = valueHash + valueHash + valueHash +valueHash + valueHash; 
		String uuid_data = new String( valueHash.subSequence(0, 8) + "-" + 
					valueHash.subSequence(9, 13) + "-" +
					valueHash.subSequence(14, 18) + "-" +
					valueHash.subSequence(19, 23) + "-" +
					valueHash.subSequence(24, 36));
		return uuid_data;
	}
}
