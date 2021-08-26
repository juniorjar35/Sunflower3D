package juniorjar35.sunflower3d.Utils;

import juniorjar35.sunflower3d.Application;

public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Application.stop(e);
	}

}
