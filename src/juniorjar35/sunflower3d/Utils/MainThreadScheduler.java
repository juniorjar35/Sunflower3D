package juniorjar35.sunflower3d.Utils;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import juniorjar35.sunflower3d.Application;

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
			} catch(Exception e) {
				Application.stop(e);
			};
		}
	}
	
	public static void schedule(Runnable task) {
		synchronized (lock) {
			Objects.requireNonNull(task);
			if (Utils.isMainThread()) {
				task.run();
				return;
			}
			TASKS.add(task);
		}
	}
	
}
