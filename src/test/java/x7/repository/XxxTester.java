package x7.repository;

import x7.base.util.JsonX;

public class XxxTester {

	public static void main(String[] args) {
		Xxx xxx = new Xxx();
		xxx.setEnd(true);
		xxx.setFull(true);
		String test = JsonX.toJson(xxx);
		System.out.println(test);
	}
}
