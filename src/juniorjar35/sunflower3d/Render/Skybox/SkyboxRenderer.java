package juniorjar35.sunflower3d.Render.Skybox;

import java.util.Objects;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import juniorjar35.sunflower3d.Render.Shader;
import juniorjar35.sunflower3d.Render.Window;
import juniorjar35.sunflower3d.Render.Renderer.AbstractRenderer;
import juniorjar35.sunflower3d.Utils.OpenGLUtils;

public class SkyboxRenderer extends AbstractRenderer {
	
	private Shader shader = null;
	private Skybox sb = null;
	private int vao, vbo;
	
	private static final float POS = 500.0f;
	
	private static final float[] POSITIONS = new float[] {
			-POS,  POS, -POS,
		    -POS, -POS, -POS,
		     POS, -POS, -POS,
		     POS, -POS, -POS,
		     POS,  POS, -POS,
		    -POS,  POS, -POS,
		    -POS, -POS,  POS,
		    -POS, -POS, -POS,
		    -POS,  POS, -POS,
		    -POS,  POS, -POS,
		    -POS,  POS,  POS,
		    -POS, -POS,  POS,
		     POS, -POS, -POS,
		     POS, -POS,  POS,
		     POS,  POS,  POS,
		     POS,  POS,  POS,
		     POS,  POS, -POS,
		     POS, -POS, -POS,
		    -POS, -POS,  POS,
		    -POS,  POS,  POS,
		     POS,  POS,  POS,
		     POS,  POS,  POS,
		     POS, -POS,  POS,
		    -POS, -POS,  POS,
		    -POS,  POS, -POS,
		     POS,  POS, -POS,
		     POS,  POS,  POS,
		     POS,  POS,  POS,
		    -POS,  POS,  POS,
		    -POS,  POS, -POS,
		    -POS, -POS, -POS,
		    -POS, -POS,  POS,
		     POS, -POS, -POS,
		     POS, -POS, -POS,
		    -POS, -POS,  POS,
		     POS, -POS,  POS
	};
	
	private static final int COUNT = POSITIONS.length / 3;
	
	public SkyboxRenderer(Window window,Shader shader) {
		super(window);
		this.shader = Objects.requireNonNull(shader);
		shader.findUniform("pm");
		shader.findUniform("vm");
		vao = OpenGLUtils.genVertexArrayAndBind();
		vbo = OpenGLUtils.genBufferAndBind();
		OpenGLUtils.storeFloatsInVertexArray(POSITIONS, 0, 3);
		OpenGLUtils.unbindBuffer();
		OpenGLUtils.unbindVertexArray();
	}
	
	public void setSkybox(Skybox sb) {
		this.sb = sb;
	}
	
	public Skybox getSkybox() {
		return this.sb;
	}
	
	@Override
	public void delete() {
		if (shader.isCreated()) shader.delete();
		if (sb != null) sb.delete();
		if (GL30.glIsVertexArray(vao)) GL30.glDeleteVertexArrays(vao);
		if (GL15.glIsBuffer(vbo)) GL15.glDeleteBuffers(vbo);
	}

	@Override
	protected void render() {
		if (sb == null) return;
		shader.start();
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, sb.tex);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		Matrix4f view = this.getViewMatrix();
		view.m30(0);
		view.m31(0);
		view.m32(0);
		shader.setUniformMatrix4f("vm", view, false);
		shader.setUniformMatrix4f("pm", this.getProjectionMatrix(), false);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, COUNT);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

}
