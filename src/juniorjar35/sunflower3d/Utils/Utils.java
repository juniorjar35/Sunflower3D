package juniorjar35.sunflower3d.Utils;

public final class Utils {
	private Utils() { };
	
	public static boolean isMainThread() {
		return (Thread.currentThread().getId() == 1);
	}
	
	
	public static Thread getMainThread() {
		Thread main = null;
		for (Thread a : Thread.getAllStackTraces().keySet()) {
			if (a.getId() == 1) main = a;
		}
		return main;
	}
	
}
