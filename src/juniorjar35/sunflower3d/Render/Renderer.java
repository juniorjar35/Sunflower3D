package juniorjar35.sunflower3d.Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Renderer {
	
	private List<Object3D> objects = new ArrayList<Object3D>();
	private Window window = null;
	private LightSource lightSource = new LightSource();
	private boolean disabled = false;
	
	private volatile Shader 
			modelShader = null;
	
	public Renderer() {
	}
	
	void setWindow(Window window) {
		this.window = window;
	}
	
	public void setLightSource(LightSource source) {
		this.lightSource = Objects.requireNonNull(source);
	}
	
	public void setModelShader(Shader shader) {
		this.modelShader = Objects.requireNonNull(shader);
		if (!shader.isCreated()) shader.init();
		this.modelShader.findUniform("projectionMatrix");
		this.modelShader.findUniform("viewMatrix");
		this.modelShader.findUniform("transformationMatrix");
		this.modelShader.findUniform("lightPosition");
		this.modelShader.findUniform("lightColor");
		this.modelShader.findUniform("reflectivity");
		this.modelShader.findUniform("shineAngle");
	}
	
	public void addObject(Object3D object) {
		objects.add(Objects.requireNonNull(object));
	}
	
	
	
	private void renderModels() {
		this.modelShader.start();
		
		for (Object3D object : objects) {
			if (!object.obj.init) continue;
			GL30.glBindVertexArray(object.obj.vao);
			GL30.glEnableVertexAttribArray(0);
			GL30.glEnableVertexAttribArray(1); 
			GL30.glEnableVertexAttribArray(2);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, object.obj.vbo);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.obj.ibo);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL13.glBindTexture(GL11.GL_TEXTURE_2D, object.obj.texture);
			this.modelShader.setUniformMatrix4f("projectionMatrix", window.projection());
			this.modelShader.setUniformMatrix4f("viewMatrix", window.camera.getView());
			this.modelShader.setUniformMatrix4f("transformationMatrix", object.transform());
			this.modelShader.setUniformVector3f("lightPosition", this.lightSource.pos);
			this.modelShader.setUniformVector3f("lightColor", this.lightSource.clr);
			this.modelShader.setUniformFloat("reflectivity", object.reflectivity);
			this.modelShader.setUniformFloat("shineAngle", object.shineAngle);
			GL11.glDrawElements(GL11.GL_TRIANGLES, object.obj.indicesCount, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL30.glDisableVertexAttribArray(0);
			GL30.glDisableVertexAttribArray(1);
			GL30.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
		
		
		this.modelShader.stop();
		
	}
	
	void renderAll() {
		if (disabled) return;
		if (modelShader != null) renderModels();
	}
	
	public void disable(boolean disabled) {
		this.disabled = disabled;
	}
	
	public void delete() {
		if (modelShader != null) {
			modelShader.delete();
			modelShader = null;
		}
		for (Object3D object : objects) {
			if (object.obj.init) {
				object.obj.delete();
			}
		}
		objects.clear();
	}
	
}
