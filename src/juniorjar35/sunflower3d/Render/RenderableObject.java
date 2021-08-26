package juniorjar35.sunflower3d.Render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Utils.Deleteable;

public class RenderableObject implements Deleteable {
	
	private FloatBuffer verts, tc, normals;
	private IntBuffer indices;
	boolean init = false, lighting = true;
	int vao, vbo, ibo, tbo, nbo, indicesCount, texture;

	public int getVao() {
		return vao;
	}

	public int getVbo() {
		return vbo;
	}

	public int getIbo() {
		return ibo;
	}

	public int getTbo() {
		return tbo;
	}

	public int getNbo() {
		return nbo;
	}

	public int getIndicesCount() {
		return indicesCount;
	}

	public int getTexture() {
		return texture;
	}

	public RenderableObject(float[] verts, int[] indices, float[] textureCoords, float[] normals) {
		this(FloatBuffer.wrap(verts), IntBuffer.wrap(indices), FloatBuffer.wrap(textureCoords), FloatBuffer.wrap(normals));
	}
	
	private static IntBuffer getIntBuffer(IntBuffer buffer) {
		IntBuffer out = MemoryUtil.memAllocInt(buffer.capacity());
		out.put(buffer).flip();
		return out;
	}
	
	private static FloatBuffer getFloatBuffer(FloatBuffer buffer) {
		FloatBuffer out = MemoryUtil.memAllocFloat(buffer.capacity());
		out.put(buffer).flip();
		return out;
	}
	
	public RenderableObject(FloatBuffer verts, IntBuffer indices, FloatBuffer textureCoords, FloatBuffer normals) {
		this.verts = getFloatBuffer(verts);
		this.indices = getIntBuffer(indices);
		this.tc = getFloatBuffer(textureCoords);
		this.normals = getFloatBuffer(normals);
	}
	
	private int buffer(FloatBuffer buffer, int size, int index) {
		int rbuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, rbuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		MemoryUtil.memFree(buffer);
		return rbuffer;
	}
	
	public void texture(int texture) {
		if (texture <= 0) {
			texture = 0;
		}
		if (!GL11.glIsTexture(texture)) throw new RuntimeException("Not a texture!");
		this.texture = texture;
	}
	
	public boolean initialized() {
		return init;
	}
	
	public void enableLighting(boolean lightning) {
		this.lighting = lightning;
	}
	
	public boolean isLightingEnabled() {
		return this.lighting;
	}
	
	public void init() {
		if (init) return;
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		ibo = GL15.glGenBuffers(); 
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		vbo = buffer(verts, 3, 0);
		tbo = buffer(tc, 2, 1);
		nbo = buffer(normals, 3, 2);
		
		indicesCount = indices.capacity();
		MemoryUtil.memFree(indices);
		GL30.glBindVertexArray(0);
		this.init = true;
	}
	
	public void delete() {
		if (this.init) {
			GL30.glDeleteBuffers(vbo);
			GL30.glDeleteBuffers(ibo);
			GL30.glDeleteBuffers(tbo);
			GL30.glDeleteBuffers(nbo);
			GL30.glDeleteVertexArrays(vao);
			this.init = false;
		}
	}
	
}
