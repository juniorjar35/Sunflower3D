package juniorjar35.sunflower3d.Render;

public interface WindowEvents {
	void keyDown(int key);
	void keyUp(int key);
	void mouseButtonDown(int button);
	void mouseButtonUp(int button);
	void scroll(double x, double y);
	void cursorPosition(double x, double y);
	void focus(boolean focused);
	void maximize();
	void minimize();
	void cursorEntered(boolean entered);
	void resize(int width, int height);
	void position(int x, int y);
	void FPS(int fps);
	void close();
}
