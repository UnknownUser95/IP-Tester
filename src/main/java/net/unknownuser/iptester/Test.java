package net.unknownuser.iptester;

public class Test {
	
	public static void main(String[] args) {
		boolean test1 = IPTester.test("255.123.123.123/32");
		System.out.println(test1);
	}
	
}
