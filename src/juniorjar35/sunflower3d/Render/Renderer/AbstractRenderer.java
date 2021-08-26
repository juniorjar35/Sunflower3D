package juniorjar35.sunflower3d.Render.Renderer;

import java.util.Objects;

import org.joml.Matrix4f;

import juniorjar35.sunflower3d.Render.Window;
import juniorjar35.sunflower3d.Utils.Deleteable;

public abstract class AbstractRenderer implements Deleteable {
	
	protected final Window window;
	
	public AbstractRenderer(Window window) {
		this.window = Objects.requireNonNull(window);
	}
	
	public Window getWindow() {
		return this.window;
	}
	
	protected Matrix4f getProjectionMatrix() {
		return window.projection();
	}
	
	protected Matrix4f getViewMatrix() {
		return window.getCamera().getView();
	}
	
	public abstract void renderAll();
	
}
