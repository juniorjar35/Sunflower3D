package juniorjar35.sunflower3d.Render.Renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import juniorjar35.sunflower3d.Render.LightSource;
import juniorjar35.sunflower3d.Render.Object3D;
import juniorjar35.sunflower3d.Render.RenderableObject;
import juniorjar35.sunflower3d.Render.Shader;
import juniorjar35.sunflower3d.Render.Window;

public class ObjectRenderer extends AbstractRenderer {
	
	private List<Object3D> objects = new ArrayList<Object3D>();
	private LightSource lightSource = new LightSource();
	private boolean disabled = false;
	
	private volatile Shader 
			modelShader = null;
	
	public ObjectRenderer(Window window, Shader modelShader) {
		super(window);
		this.modelShader = Objects.requireNonNull(modelShader);
		this.modelShader.findUniform("projectionMatrix");
		this.modelShader.findUniform("viewMatrix");
		this.modelShader.findUniform("transformationMatrix");
		this.modelShader.findUniform("lightPosition");
		this.modelShader.findUniform("lightColor");
		this.modelShader.findUniform("reflectivity");
		this.modelShader.findUniform("shineAngle");
		this.modelShader.findUniform("lighting");
	}
	
	public void setLightSource(LightSource source) {
		this.lightSource = Objects.requireNonNull(source);
	}
	
	public void addObject(Object3D object) {
		objects.add(Objects.requireNonNull(object));
	}
	
	public void addObjects(Collection<Object3D> objects) {
		objects.addAll(Objects.requireNonNull(objects));
	}
	
	public void removeObjects(Collection<Object3D> objects) {
		objects.removeAll(Objects.requireNonNull(objects));
	}
	
	public void removeObject(Object3D object) {
		objects.add(Objects.requireNonNull(object));
	}
	
	private void renderModels() {
		this.modelShader.start();
		
		for (Object3D object : objects) {
			RenderableObject obj = object.getObject();
			if (!obj.initialized()) continue;
			this.modelShader.rendering();
			GL30.glBindVertexArray(obj.getVao());
			GL30.glEnableVertexAttribArray(0);
			GL30.glEnableVertexAttribArray(1); 
			GL30.glEnableVertexAttribArray(2);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, obj.getVbo());
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, obj.getIbo());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, obj.getTexture());
			this.modelShader.setUniformMatrix4f("projectionMatrix", this.getProjectionMatrix());
			this.modelShader.setUniformMatrix4f("viewMatrix", this.getViewMatrix());
			this.modelShader.setUniformMatrix4f("transformationMatrix", object.transform());
			this.modelShader.setUniformVector3f("lightPosition", this.lightSource.getPosition());
			this.modelShader.setUniformVector3f("lightColor", this.lightSource.getColor());
			this.modelShader.setUniformFloat("reflectivity", object.getReflectivity());
			this.modelShader.setUniformFloat("shineAngle", object.getShineAngle());
			this.modelShader.setUniformInteger("lighting", obj.isLightingEnabled() ? 1 : 0);
			GL11.glDrawElements(GL11.GL_TRIANGLES, obj.getIndicesCount(), GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL30.glDisableVertexAttribArray(0);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(2);
			this.modelShader.clean();
			GL30.glBindVertexArray(0);
		}
		
		this.modelShader.stop();
		
	}
	
	public void disable(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean disabled() {
		return this.disabled;
	}
	
	public void delete() {
		if (modelShader != null) {
			modelShader.delete();
			modelShader = null;
		}
		objects.clear();
	}

	@Override
	public void renderAll() {
		if (disabled) return;
		if (modelShader != null) renderModels();
	}
	
}
