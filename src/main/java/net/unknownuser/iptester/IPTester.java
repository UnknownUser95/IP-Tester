package net.unknownuser.iptester;

public class IPTester {
	public static boolean test(String ip) {
		String[] fullIP = ip.split("/");
		
		if(fullIP.length != 2 || !isValidIP(fullIP[0]) || !isInteger(fullIP[1])) {
			// not enough params or not a valid IP
			return false;
		}
		
		int netMask = Integer.parseInt(fullIP[1]);
		if(0 > netMask || netMask > 32) {
			// technically 32 is the limit, so...
			return false;
		}
		
		return test(fullIP[0], netMask);
	}
	
	public static boolean test(String ip, int netmask) {
		return !(0 > netmask || netmask > 32 || !isValidIP(ip)); 
	}
	
	public static String getNetMask(String ip) {
		if(!isValidIP(ip)) {
			return "";
		}
		
		int classNr = Integer.parseInt(ip.substring(0, ip.indexOf('.')));
		System.out.println(classNr);
		return "";
	}
	
	private static boolean isValidIP(String ip) {
		String[] bytes = ip.split("[.]");
		if(bytes.length != 4) {
			return false;
		}
		
		for(String str : bytes) {
			if(!isInteger(str) || !is8Bit(Integer.parseInt(str))) {
				// a part is not a number or not 8 bit
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean is8Bit(int i) {
		return 0 <= i && i <= 255;
	}
	
	private static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException exc) {
			return false;
		}
	}
}
