package juniorjar35.sunflower3d.Utils;

import java.util.Random;

public class Maths {
	private Maths() {};
	
	private static Random RANDOM = new Random();
	
	public static int rangedRandom(int min, int max) {
		return (int) ((RANDOM.nextDouble() * (max - min)) + min);
	}
	
	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static boolean chance(int percent) {
		percent = clamp(percent, 0, 100);
		percent = 100 - percent;
		int number = rangedRandom(0, 100);
		return number >= percent;
	}
}