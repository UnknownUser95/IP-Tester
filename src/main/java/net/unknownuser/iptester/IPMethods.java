package net.unknownuser.iptester;

/**
 * Contains methods for transforming IP addresses.
 */
public interface IPMethods {
	
	/**
	 * Creates an IP address in binary form.
	 * 
	 * @param ip The IP address.
	 * @return The binary form of the IP address with dots in proper places.
	 */
	static String insertDots(int ip) {
		StringBuilder sb = new StringBuilder();
		String ipStr = Integer.toBinaryString(ip);
		sb.append("0".repeat(32 - ipStr.length()));
		sb.append(ipStr);
		
		sb.insert(24, ".");
		sb.insert(16, ".");
		sb.insert(8, ".");
		
		return sb.toString();
	}
	
	/**
	 * Creates an IP address in binary form. The IP address has to be in <b>binary form</b>.
	 * 
	 * @param ip The IP address.
	 * @return The binary form of the IP address with dots in proper places.
	 */
	static String insertDots(String ipStr) {
		StringBuilder sb = new StringBuilder(ipStr);
		sb.insert(24, '.');
		sb.insert(16, '.');
		sb.insert(8, '.');
		
		return sb.toString();
	}
	
	/**
	 * Turns a standard IP address into an int. This method does not handle binary versions of IP
	 * addresses.
	 * 
	 * @param ip The IP address in standard form.
	 * @return The value of the IP address.
	 * @see {@link IPMethods#binIntStrToInt}
	 */
	static int ipToInt(String ip) {
		if(ip.contains("/")) {
			ip = ip.substring(0, ip.indexOf('/'));
		}
		
		int result = 0;
		String[] parts = ip.split("[.]");
		
		for(int i = 3; i >= 0; i--) {
			int res = (Integer.parseInt(parts[i]) << ((3 - i) * 8));
			result += res;
		}
		
		return result;
	}
	
	/**
	 * Turns any binary integer into its actual value.
	 * 
	 * @param intStr The integer in its binary form.
	 * @return The integer as its actual value.
	 */
	static int binIntStrToInt(String intStr) {
		intStr = intStr.replaceAll("[.]", "");
		int res = 0;
		
		for(int i = 0; i < intStr.length(); i++) {
			if(intStr.charAt(i) == '1') {
				res += (int) Math.round(Math.pow(2, intStr.length() - 1 - i));
			}
		}
		
		return res;
	}
	
	/**
	 * Creates a network mask from the given length.
	 * 
	 * @param masklength The length of the network mask.
	 * @return The network mask of the given length.
	 */
	static int getNetMask(int masklength) {
		String mask = "1".repeat(masklength) + "0".repeat(32 - masklength);
		int res = 0;
		
		for(int i = 0; i < 32; i++) {
			if(mask.charAt(i) == '1') {
				res += (int) Math.round(Math.pow(2, 31 - i));
			}
		}
		
		return res;
	}
	
	/**
	 * Returns the length of the default network mask of the given IP address.
	 * 
	 * @param firstByte The first byte of the IP address.
	 * @return The length of the default network mask.
	 */
	static int getDefaultNetmask(int firstByte) {
		int res = 8;
		
		if(firstByte >= 0b11000000) {
			// class C and higher
			res = 24;
		} else if(firstByte >= 0b10000000) {
			// class B
			res = 16;
		}
		
		return res;
	}
	
	/**
	 * Converts a binary IP to the number form of that IP.
	 * 
	 * @param ip The IP, which should be converted.
	 * @return The number version of the given IP.
	 */
	public static String binaryIPtoNumbersStaticLength(String ip) {
		// IP either with or without dots
		ip = ip.replaceAll("[.]", "");
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < 32; i += 8) {
			String number = String.valueOf(binIntStrToInt(ip.substring(i, i + 8)));
			number = "0".repeat(3 - number.length()) + number;
			sb.append(number);
			sb.append('.');
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	/**
	 * Converts a binary IP to the number form of that IP.
	 * 
	 * @param ip The IP, which should be converted.
	 * @return The number version of the given IP.
	 */
	public static String binaryIPtoNumbers(String ip) {
		// IP either with or without dots
		ip = ip.replaceAll("[.]", "");
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < 32; i += 8) {
			String number = String.valueOf(binIntStrToInt(ip.substring(i, i + 8)));
			sb.append(number);
			sb.append('.');
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	public static String getIP(int ip) {
		String ipStr = Integer.toBinaryString(ip);
		ipStr = "0".repeat(32 - ipStr.length()) + ipStr;
		return insertDots(ipStr);
	}
	
	/**
	 * Tests whether a given string is an integer.
	 * 
	 * @param str The string to be tested.
	 * @return {@code true} id the given string is a number, {@code false} otherwise
	 */
	public static boolean isInteger(String str) {
		str = str.replaceAll("[ ]", "");
		if(str.equals(".".repeat(str.length()))) {
			return false;
		}
		
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException exc) {
			return false;
		}
	}
}
