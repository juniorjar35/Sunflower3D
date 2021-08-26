package juniorjar35.sunflower3d.Render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Application;
import juniorjar35.sunflower3d.Render.Renderer.AbstractRenderer;
import juniorjar35.sunflower3d.Utils.Deleteable;
import juniorjar35.sunflower3d.Utils.Logger;
import juniorjar35.sunflower3d.Utils.MainThreadScheduler;
import juniorjar35.sunflower3d.Utils.OpenGLUtils;
import juniorjar35.sunflower3d.Utils.Timer;

public class Window implements Deleteable {
	
	public static final float FAR = 10000.0f, NEAR = 0.01f;
	
	private Vector2i size, pos;
	Vector2d mouse;
	private String title;
	
	private long window = 0;
	
	private boolean mt = false,
					created = false,
					vsync = true,
					disableRendering = false;
	
	private Vector3f bgcolor = new Vector3f(0.662f, 0.717f, 0.776f);
	
	private double fpsCap = 60.0d;
	
	private float fov = 90.0f;
	
	private List<WindowEvents> events = new CopyOnWriteArrayList<WindowEvents>();
	private List<AbstractRenderer> renderers = new CopyOnWriteArrayList<AbstractRenderer>();
	private Timer fpsTimer = new Timer();
	private Camera camera;
	
	private Matrix4f projection = new Matrix4f();
	
	public Window(int width, int height, String title) {
		this(new Vector2i(width, height), title);
	}
	
	public Window(Vector2i dimensions, String title) {
		Configuration.LIBRARY_PATH.set(Application.getNativeLibraryDirectory().getAbsolutePath());
		Configuration.SHARED_LIBRARY_EXTRACT_DIRECTORY.set(Application.getNativeLibraryDirectory().getAbsolutePath());
		this.size = new Vector2i(Objects.requireNonNull(dimensions));
		this.pos = new Vector2i();
		this.mouse = new Vector2d();
		this.title = Objects.requireNonNull(title);
	}
	
	public Window(int width, int height) {
		this(width, height, "Window");
	}
	
	public Window(String title) {
		this(900,600,title);
	}
	
	public Window() {
		this(900,600,"Window");
	}
	
	public void addEventListener(WindowEvents event) {
		this.events.add(Objects.requireNonNull(event));
	}
	
	public void removeEventListener(WindowEvents event) {
		this.events.remove(Objects.requireNonNull(event));
	}
	
	public void addRenderer(AbstractRenderer renderer) {
		this.renderers.add(Objects.requireNonNull(renderer));
	}
	
	public void removeRenderer(AbstractRenderer renderer) {
		this.renderers.remove(Objects.requireNonNull(renderer));
	}
	
	public void disableRendering(boolean dr) {
		this.disableRendering = dr;
	}
	
	public boolean isRenderingDisabled() {
		return this.disableRendering;
	}
	
	public void create() {
		this.camera = new Camera(new Vector3f(), new Vector3f(),this);
		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint());
		
		if (!GLFW.glfwInit()) {
			throw new RuntimeException("Failed to initialized GLFW!");
		}
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		window = GLFW.glfwCreateWindow(size.x, size.y, title, 0, 0);
		
		if (window == 0) {
			throw new RuntimeException("Window creation failed! (Driver likely does not support context version 4.3 and above!)");
		}
		
		GLCapabilities cap = OpenGLUtils.makeContext(this);
		if (!cap.OpenGL43) {
			throw new IllegalStateException("OpenGL 4.3 is required!");
		}
		
		GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
		GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
		GL43.glDebugMessageCallback(this::checkErrors, 0);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		this.pos.x = (mode.width() - size.x) / 2;
		this.pos.y = (mode.height() - size.y) / 2;
		
		GLFW.glfwSetKeyCallback(window, this::keyboard);
		GLFW.glfwSetMouseButtonCallback(window, this::mouseButton);
		GLFW.glfwSetWindowFocusCallback(window, this::focusCallback);
		GLFW.glfwSetWindowMaximizeCallback(window, this::maximizeCallback);
		GLFW.glfwSetFramebufferSizeCallback(window, this::framebufferSize);
		GLFW.glfwSetWindowSizeCallback(window, this::windowSize);
		GLFW.glfwSetWindowPosCallback(window, this::windowPosition);
		GLFW.glfwSetScrollCallback(window, this::mouseScroll);
		GLFW.glfwSetCursorPosCallback(window, this::cursorPosition);
		GLFW.glfwSetCursorEnterCallback(window, this::cursorEntered);
		GLFW.glfwSetWindowCloseCallback(window, this::windowClosed);
		
		GLFW.glfwSetWindowPos(window, this.pos.x, this.pos.y);
		
		GLFW.glfwShowWindow(window);
		
		GLFW.glfwSwapInterval(1);
		
		this.created = true;
	}
	
	private void keyboard(long window, int key, int scancode, int action, int mods) {
		if (action == GLFW.GLFW_PRESS) {
			for (WindowEvents event : events) event.keyDown(key);
		} else {
			for (WindowEvents event : events) event.keyUp(key);
		}
	}
	
	private void mouseButton(long window, int button, int action, int mods) {
		if (action == GLFW.GLFW_PRESS) {
			for (WindowEvents event : events) event.mouseButtonDown(button);
		} else {
			for (WindowEvents event : events) event.mouseButtonUp(button);
		}
	}
	
	private void focusCallback(long window, boolean focused) {
		for (WindowEvents event : events) event.focus(focused);
	}
	
	private void maximizeCallback(long window, boolean maximized) {
		//renderer.disable(!maximized);
		if (maximized) {
			for (WindowEvents event : events) event.maximize();
		} else {
			for (WindowEvents event : events) event.minimize();
		}
	}
	
	private void framebufferSize(long window, int width, int height) {
		GL11.glViewport(0, 0, width, height);
	}
	
	private void windowSize(long window, int width, int height) {
		if (width == 0 || height == 0) return;
		size.x = width;
		size.y = height;
		for (WindowEvents event : events) event.resize(width, height);
	}
	
	private void windowPosition(long window, int xpos, int ypos) {
		pos.x = xpos;
		pos.y = ypos;
		for (WindowEvents event : events) event.position(xpos, ypos);
	}
	
	private void mouseScroll(long window, double xoffset, double yoffset) {
		for (WindowEvents event : events) event.scroll(xoffset, yoffset);
	}
	
	private void cursorEntered(long window, boolean entered) {
		for (WindowEvents event : events) event.cursorEntered(entered);
	}
	
	private void cursorPosition(long window, double xpos, double ypos) {
		mouse.set(xpos, ypos);
		for (WindowEvents event : events) event.cursorPosition(xpos,ypos);
	}
	
	private void windowClosed(long window) {
		for (WindowEvents event : events) event.close();
	}
	
	public void lockCursor(boolean locked) {
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, locked ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public boolean isCursorLocked() {
		return GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}
	
	public boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
	}
	
	public boolean isKeyUp(int key) {
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_RELEASE;
	}
	
	public boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(window, button) == GLFW.GLFW_PRESS;
	}
	
	public boolean isMouseButtonUp(int button) {
		return GLFW.glfwGetMouseButton(window, button) == GLFW.GLFW_RELEASE;
	}
	
	public void vsync(boolean enabled) {
		this.vsync = enabled;
		GLFW.glfwSwapInterval(enabled ? 1 : 0);
	}
	
	public boolean vsyncEnabled() {
		return this.vsync;
	}
	
	public boolean isCreated() {
		return this.created;
	}
	
	public Vector2i getSize() {
		return new Vector2i(size);
	}
	
	public void setSize(Vector2i size) {
		GLFW.glfwSetWindowSize(window, size.x, size.y);
	}
	
	public void getFramebufferSize(int[] wh) {
		try (MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer addr = stack.mallocInt(2);
			GLFW.nglfwGetFramebufferSize(window, MemoryUtil.memAddress(addr), MemoryUtil.memAddress(addr) + 4);
			wh[0] = addr.get(0);
			wh[1] = addr.get(1);
		}
	}
	
	public void setFullscreen(boolean fullscreen) {
		if (fullscreen) {
			int[] xw = new int[1], yh = new int[1];
			
			GLFW.glfwGetWindowPos(window, xw, yh);
			this.pos.x = xw[0];
			this.pos.y = yh[0];
			GLFW.glfwGetWindowSize(window, xw, yh);
			this.size.x = xw[0];
			this.size.y = yh[0];
			GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor() , 0, 0,this.size.x, this.size.y, GLFW.GLFW_DONT_CARE);
		} else {
			GLFW.glfwSetWindowMonitor(window, 0, this.pos.x, this.pos.y, this.size.x, this.size.y, 0);
		}
	}
	
	public boolean isFullscreen() {
		return GLFW.glfwGetWindowMonitor(window) != 0;
	}
	
	public void toggleFullscreen() {
		setFullscreen(!isFullscreen());
	}
	
	public Vector2i getPosition() {
		return new Vector2i(pos);
	}
	
	public float getAspectRatio() {
		return size.x / size.y;
	}
	
	public void setFOV(float fov) {
		this.fov = Math.min(fov, 1.0f);
	}
	
	public float getFOV() {
		return fov;
	}
	
	private int fps = 0;
	
	public void multithread(boolean mt) {
		this.mt = mt;
	}
	
	public boolean isMultithreaded() {
		return this.mt;
	}
	
	private void checkErrors(int source, int type, int id, int severity, int length, long message, long userParam) {
		if(type == GL43.GL_DEBUG_TYPE_ERROR) {
			System.err.println("---------OPENGL TRACEBACK---------");
			System.err.println("OpenGL error: " + MemoryUtil.memASCII(message, length));
			Exception traceback = new Exception();
			traceback.fillInStackTrace();
			StackTraceElement[] ste = traceback.getStackTrace();
			for (int i = 2; i < ste.length; i++) {
				if (ste[i].getClassName().startsWith("org.lwjgl.opengl.")) continue;
				System.err.println("\tfrom " + ste[i].toString());
			}
			System.err.println("---------OPENGL TRACEBACK---------");
			switch(id) {
			case GL11.GL_OUT_OF_MEMORY:
				Application.stop(new OutOfMemoryError("OpenGL"));
			case GL11.GL_STACK_OVERFLOW:
				Application.stop(new StackOverflowError("OpenGL"));
			case GL11.GL_STACK_UNDERFLOW:
				Application.stop(new Error("OpenGL stack underflow"));
			}
			return;
		} else if (type == GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR) {
			Logger.WARN.println("---------OPENGL TRACEBACK---------");
			Logger.WARN.println("OpenGL Deprication");
			Exception traceback = new Exception();
			traceback.fillInStackTrace();
			StackTraceElement[] ste = traceback.getStackTrace();
			for (int i = 2; i < ste.length; i++) {
				if (ste[i].getClassName().startsWith("org.lwjgl.opengl.")) continue;
				System.err.println("\tfrom " + ste[i].toString());
			}
			Logger.WARN.println("---------OPENGL TRACEBACK---------");
		}
		
	}
	
	public void update() throws InterruptedException {
		if (!mt) GLFW.glfwPollEvents(); else GLFW.glfwWaitEvents();
		if (!vsync) fpsTimer.syncFrames(fpsCap);
		if ((fps = fpsTimer.getFPS()) != -1) {
			for (WindowEvents event : events) {
				event.FPS(fps);
			}
		}
		this.camera.update();
		MainThreadScheduler.executeTasks();
	}
	
	public void render() {
		GL11.glClearColor(bgcolor.x, bgcolor.y, bgcolor.z, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		if (!disableRendering) {
			for (AbstractRenderer renderer : renderers) {
				renderer.renderAll();
			}
		}
		GLFW.glfwSwapBuffers(window);
	}
	
	public void setBackgroundColor(float red, float green, float blue) {
		this.bgcolor.x = red;
		this.bgcolor.y = green;
		this.bgcolor.z = blue;
	}
	
	public void screenshot(File screenshotFile) throws IOException {
		int fw,fh;
		try (MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer wh = stack.mallocInt(2);
			GLFW.nglfwGetFramebufferSize(window, MemoryUtil.memAddress(wh), MemoryUtil.memAddress(wh) + 4);
			fw = wh.get(0);
			fh = wh.get(1);
		}
		ByteBuffer pixels = MemoryUtil.memAlloc(4 * fw * fh);
		GL11.glReadBuffer(GL11.GL_FRONT);
		GL11.glReadPixels(0, 0, fw, fh, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		BufferedImage image = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < fw; x++) {
			for (int y = 0; y < fh; y++) {
				int pos = (x + (fw * y)) * 4;
				int r = pixels.get(pos) & 0xFF;
				int g = pixels.get(pos + 1) & 0xFF;
				int b = pixels.get(pos + 2) & 0xFF;
				int a = pixels.get(pos + 3) & 0xFF;
				image.setRGB(x, fh - (y + 1), (a << 24) | (r << 16) | (g << 8) | b );
			}
		}
		MemoryUtil.memFree(pixels);
		ImageIO.write(image, "PNG", screenshotFile);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public void close() {
		GLFW.glfwSetWindowShouldClose(window, true);
	}
	
	public void hide() {
		GLFW.glfwHideWindow(window);
	}
	
	public void show() {
		GLFW.glfwShowWindow(window);
	}
	
	public void maximize() {
		GLFW.glfwRestoreWindow(window);
	}
	
	public void minimize() {
		GLFW.glfwIconifyWindow(window);
	}
	
	public void setFPSCap(int cap) {
		this.fpsCap = (double) cap;
	}
	
	public long getWindowHandle() {
		return window;
	}
	
	public void delete() {
		if (created) {
			Callbacks.glfwFreeCallbacks(window);
			long a = GLFW.nglfwSetErrorCallback(0);
			if (a != 0) {
				Callback.free(a);
			}
			close();
			try (MemoryStack stack = MemoryStack.stackPush()){
				PointerBuffer pointer = stack.mallocPointer(1);
				GL43.glGetPointerv(GL43.GL_DEBUG_CALLBACK_FUNCTION, pointer);
				Callback.free(pointer.get());
			}
			GLFW.glfwDestroyWindow(window);
			GLFW.glfwTerminate();
		}
	}
	
	public Matrix4f projection() {
		projection.identity();
		projection.perspective(fov, getAspectRatio(), NEAR, FAR);
		return projection;
	}
	
}
