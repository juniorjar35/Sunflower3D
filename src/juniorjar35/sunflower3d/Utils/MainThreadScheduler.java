package juniorjar35.sunflower3d.Utils;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public final class MainThreadScheduler {
	private MainThreadScheduler() {};
	
	private static Queue<Runnable> TASKS = new LinkedBlockingQueue<Runnable>();
	
	private static Object lock = new Object();
	
	public static void executeTasks() {
		if (!Utils.isMainThread()) return;
		Runnable task = null;
		while((task = TASKS.poll()) != null) {
			try {
				task.run();
			} catch(Exception e) {};
		}
	}
	
	public static void schedule(Runnable task) {
		synchronized (lock) {
			Objects.requireNonNull(task);
			TASKS.add(task);
		}
	}
	
}
