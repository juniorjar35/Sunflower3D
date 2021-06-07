package juniorjar35.sunflower3d.Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowMaximizeCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.Callback;

import juniorjar35.sunflower3d.Utils.OpenGLUtils;
import juniorjar35.sunflower3d.Utils.Timer;

public class Window {
	
	private static final float FAR = 10000.0f, NEAR = 0.01f;
	
	private Vector2i size, pos;
	Vector2d mouse;
	private String title;
	
	private long window = 0;
	
	private boolean mt = false,
					created = false,
					vsync = true;
	
	private Vector3f bgcolor = new Vector3f(0.0f, 0.808f, 0.82f);
	
	private double fpsCap = 60.0d;
	
	private float fov = 70.0f;
	
	private List<WindowEvents> events = new ArrayList<WindowEvents>();
	private Timer fpsTimer = new Timer();
	Renderer renderer;
	Camera camera;
	
	private Matrix4f projection = new Matrix4f();
	
	public Window(int width, int height, String title) {
		this(new Vector2i(width, height), title);
	}
	
	public Window(Vector2i dimensions, String title) {
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
	
	
	
	public void create(Renderer renderer) {
		
		this.renderer = Objects.requireNonNull(renderer);
		this.renderer.setWindow(this);
		
		this.camera = new Camera(new Vector3f(), new Vector3f(),this);
		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint());
		
		if (!GLFW.glfwInit()) {
			throw new RuntimeException("Failed to initialized GLFW!");
		}
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		
		window = GLFW.glfwCreateWindow(size.x, size.y, title, 0, 0);
		
		if (window == 0) {
			throw new RuntimeException("Window creation failed!");
		}
		
		GLCapabilities cap = OpenGLUtils.makeContext(window);
		if (!cap.OpenGL33) {
			throw new IllegalStateException("OpenGL 3.3 is not supported!");
		}
		
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		this.pos.x = (mode.width() - size.x) / 2;
		this.pos.y = (mode.height() - size.y) / 2;
		
		GLFW.glfwSetKeyCallback(window, new GLFWKeyCallbackI() {
			
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					for (WindowEvents event : events) event.keyDown(key);
				} else {
					for (WindowEvents event : events) event.keyUp(key);
				}
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallbackI() {
			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					for (WindowEvents event : events) event.mouseButtonDown(button);
				} else {
					for (WindowEvents event : events) event.mouseButtonUp(button);
				}
			}
		});
		
		GLFW.glfwSetWindowFocusCallback(window, new GLFWWindowFocusCallbackI() {
			
			@Override
			public void invoke(long window, boolean focused) {
				for (WindowEvents event : events) event.focus(focused);
			}
		});
		
		GLFW.glfwSetWindowMaximizeCallback(window, new GLFWWindowMaximizeCallbackI() {
			
			@Override
			public void invoke(long window, boolean maximized) {
				if (maximized) {
					for (WindowEvents event : events) event.maximize();
				} else {
					for (WindowEvents event : events) event.minimize();
				}
			}
		});
		
		GLFW.glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallbackI() {
			
			@Override
			public void invoke(long window, int width, int height) {
				GL11.glViewport(0, 0, width, height);
			}
		});
		
		GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallbackI() {
			
			@Override
			public void invoke(long window, int width, int height) {
				size.x = width;
				size.y = height;
				for (WindowEvents event : events) event.resize(width, height);
			}
		});
		
		GLFW.glfwSetWindowPosCallback(window, new GLFWWindowPosCallbackI() {
			
			@Override
			public void invoke(long window, int xpos, int ypos) {
				pos.x = xpos;
				pos.y = ypos;
				for (WindowEvents event : events) event.position(xpos, ypos);
			}
		});
		
		GLFW.glfwSetScrollCallback(window, new GLFWScrollCallbackI() {
			
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				for (WindowEvents event : events) event.scroll(xoffset, yoffset);
			}
		});
		
		GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallbackI() {
			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mouse.set(xpos, ypos);
				for (WindowEvents event : events) event.cursorPosition(xpos,ypos);
			}
		});
		
		GLFW.glfwSetCursorEnterCallback(window, new GLFWCursorEnterCallbackI() {
			
			@Override
			public void invoke(long window, boolean entered) {
				for (WindowEvents event : events) event.cursorEntered(entered);
			}
		});
		
		GLFW.glfwSetWindowPos(window, this.pos.x, this.pos.y);
		
		GLFW.glfwShowWindow(window);
		
		GLFW.glfwSwapInterval(1);
		fpsTimer.init();
		this.created = true;
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
	
	public Vector2i getSize() {
		return new Vector2i(size);
	}
	
	public void setSize(Vector2i size) {
		GLFW.glfwSetWindowSize(window, size.x, size.y);
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
			GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
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
	
	public void update() throws InterruptedException {
		if (!mt) GLFW.glfwPollEvents(); else GLFW.glfwWaitEvents();
		if (!vsync) fpsTimer.syncFrames(fpsCap);
		if ((fps = fpsTimer.getFPS()) != -1) {
			for (WindowEvents event : events) {
				event.FPS(fps);
			}
		}
		this.camera.update();
	}
	
	public void render() {
		GL11.glClearColor(bgcolor.x, bgcolor.y, bgcolor.z, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		renderer.renderAll();
		GLFW.glfwSwapBuffers(window);
	}
	
	public void setBackgroundColor(float red, float green, float blue) {
		this.bgcolor.x = red;
		this.bgcolor.y = green;
		this.bgcolor.z = blue;
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
	
	public void setFPSCap(int cap) {
		this.fpsCap = (double) cap;
	}
	
	public void delete() {
		if (created) {
			Callbacks.glfwFreeCallbacks(window);
			long a = GLFW.nglfwSetErrorCallback(0);
			if (a != 0) {
				Callback.free(a);
			}
			close();
			GLFW.glfwDestroyWindow(window);
			GLFW.glfwTerminate();
		}
	}
	
	Matrix4f projection() {
		projection.identity();
		projection.perspective(fov, getAspectRatio(), NEAR, FAR);
		return projection;
	}
	
}
