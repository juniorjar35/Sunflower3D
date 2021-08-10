package juniorjar35.sunflower3d.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.lwjgl.system.Library;

public final class Utils {
	
	private static boolean LIBLOADED = false;
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();
	
	private Utils() { };
	
	static {
		try {
			Library.loadSystem("juniorjar35.sunflower3d.Utils.Utils", "Sunflower3d");
			LIBLOADED = true;
		} catch(Error e) {
			Logger.error(e.toString());
		};
	}
	
	public static void loadNativeLibrary() { }
	
	public static boolean Sunflower3DLibLoaded() {
		return LIBLOADED;
	}
	
	public static void checkIfLoaded() {
		if (!LIBLOADED) throw new IllegalStateException("Sunflower3d library not loaded!");
	}
	
	private static native short RDRAND160();
	private static native void RDSEED160(short seed);
	private static native int RDRAND320();
	private static native void RDSEED320(int seed);
	private static native long RDRAND640();
	private static native void RDSEED640(long seed);
	
	public static long RDRAND64() {
		checkIfLoaded();
		return RDRAND640();
	}
	
	public static void RDSEED64(long seed) {
		checkIfLoaded();
		RDSEED640(seed);
	}
	
	public static int RDRAND32() {
		checkIfLoaded();
		return RDRAND320();
	}
	
	public static void RDSEED32(int seed) {
		checkIfLoaded();
		RDSEED320(seed);
	}
	
	public static short RDRAND16() {
		checkIfLoaded();
		return RDRAND160();
	}
	
	public static void RDSEED16(short seed) {
		checkIfLoaded();
		RDSEED160(seed);
	}
	
	
	
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
		System.out.println("----------TRACEBACK----------");
		for (int i = 2; i < ste.length; i++) {
			System.out.println(ste[i].toString());
		}
		System.out.println("----------TRACEBACK----------");
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
