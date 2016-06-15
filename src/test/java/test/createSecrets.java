package test;

import com.qz.util.TOTPUtils;

public class createSecrets {
	public static void main(String[] args) {
		for (int i = 0; i < 48; i++) {
			System.out.println(TOTPUtils.generateSecret());
		}
	}
}
