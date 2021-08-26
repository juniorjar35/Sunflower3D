package juniorjar35.sunflower3d.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Utils {
	
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();
	
	private Utils() { };
	
	public static boolean isMainThread() {
		return (Thread.currentThread().getId() == 1);
	}
	
	public static Thread getMainThread() {
		for (Thread a : Thread.getAllStackTraces().keySet()) {
			if (a.getId() == 1) return a;
		}
		return null;
	}
	
	public static void traceback() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for (int i = 2; i < ste.length; i++) {
			System.out.println("\tat " + ste[i].toString());
		}
	}
	
	public static String toBase16(byte[] bytes) {
		char[] chars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int h = bytes[i] & 0xFF;
			chars[i * 2] = HEX[h >>> 4];
			chars[i * 2 + 1] = HEX[h & 0x0F];
		}
		return new String(chars);
	}
	
	public static byte[] fromBase16(String hex) {
		int l = hex.length();
		byte[] data = new byte[l / 2];
		for (int i = 0; i < l; i += 2){
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}
	
	public static String toRomanNumerals(int number) {
		StringBuilder builder = new StringBuilder();
		while(number > 0) {
			if (number >= 1000) {
				builder.append('M');
				number -= 1000;
				continue;
			}
			if (number >= 900) {
				builder.append("CM");
				number -= 900;
				continue;
			}
			if (number >= 500) {
				builder.append('D');
				number -= 500;
				continue;
			}
			if (number >= 400) {
				builder.append("CD");
				number -= 400;
				continue;
			}
			if (number >= 100) {
				builder.append('C');
				number -= 100;
				continue;
			}
			if (number >= 90) {
				builder.append("XC");
				number -= 90;
				continue;
			}
			if (number >= 50) {
				builder.append('L');
				number -= 50;
				continue;
			}
			if (number >= 40) {
				builder.append("XL");
				number -= 40;
				continue;
			}
			if (number >= 10) {
				builder.append('X');
				number -= 10;
				continue;
			}
			if (number >= 9) {
				builder.append("IX");
				number -= 9;
				continue;
			}
			if (number >= 5) {
				builder.append('V');
				number -= 5;
				continue;
			}
			if (number >= 4) {
				builder.append("IV");
				number -= 4;
				continue;
			}
			if (number >= 1) {
				builder.append('I');
				number -= 1;
				continue;
			}
		}
		return builder.toString();
	}
	
	public static String fingerprint(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.update(bytes);
		return toBase16(digest.digest());
	}
	
	public static String fingerprint(String resource) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResourceUtils.loadInto(resource, baos);
		digest.update(baos.toByteArray());
		return toBase16(digest.digest());
	}
	
}
