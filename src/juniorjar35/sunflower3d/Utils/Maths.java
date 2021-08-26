package juniorjar35.sunflower3d.Utils;

import java.util.Objects;
import java.util.Random;

import org.joml.Vector3f;

public class Maths {
	private Maths() {};
	
	private static Random RANDOM = new Random();
	
	public static int rangedRandom(int min, int max) {
		return (int) ((RANDOM.nextDouble() * (max - min)) + min);
	}
	
	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static Vector3f clampVector3f(Vector3f value, Vector3f min, Vector3f max) {
		value.x = clamp(value.x, min.x, max.x);
		value.y = clamp(value.y, min.y, max.y);
		value.z = clamp(value.z, min.z, max.z);
		return value;
	}
	
	public static boolean chance(int percent) {
		percent = clamp(percent, 0, 100);
		percent = 100 - percent;
		int number = rangedRandom(0, 100);
		return number >= percent;
	}
	
	public static void setSeed(long seed) {
		RANDOM.setSeed(seed);
	}
	
	public static void setRandom(Random random) {
		RANDOM = Objects.requireNonNull(random);
	}
}