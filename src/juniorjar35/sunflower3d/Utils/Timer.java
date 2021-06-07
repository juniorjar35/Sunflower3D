package juniorjar35.sunflower3d.Utils;

import org.lwjgl.glfw.GLFW;

public class Timer {
	
	private volatile double then = -1.0D;
	private volatile long fpsCounter = -1;
	private volatile int fps = 0;
	
	public void init() {
		if (then == -1.0D) {
			then = GLFW.glfwGetTime();
			fpsCounter = System.currentTimeMillis();
		}
	}
	
	public int getFPS() {
		fps++;
		if (System.currentTimeMillis() >= fpsCounter + 1000) {
			int fpst = fps;
			fps = 0;
			fpsCounter = System.currentTimeMillis();
			return fpst;
		}
		return -1;
	}
	
	public int syncFrames(double fps) throws InterruptedException {
		double now = GLFW.glfwGetTime();
		int updates = 0;
		
		double dt = 1.0D / fps + then;
		while(dt < now) {
			dt = 1.0D / fps + dt;
			updates++;
		}
		while(dt > now) {
			Thread.sleep(1);
			now = GLFW.glfwGetTime();
		}
		updates++;
		then = dt;
		return updates;
	}
	
}
