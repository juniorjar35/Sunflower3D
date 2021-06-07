package juniorjar35.sunflower3d.Render;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import juniorjar35.sunflower3d.Utils.ResourceUtils;

public class Shader {
	private int 
		programId = -1,
		vertexId,
		fragmentId;
	
	private final String vertexCode;
	private final String fragmentCode;
	
	private boolean c = false;
	
	public Shader(String vertex, String fragment) throws IOException {
		this.vertexCode = ResourceUtils.loadString(vertex);
		this.fragmentCode = ResourceUtils.loadString(fragment);
	}
	
	public void init() {
		this.programId = GL20.glCreateProgram();
		this.vertexId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		
		GL20.glShaderSource(this.vertexId, this.vertexCode);
		GL20.glCompileShader(vertexId);
		
		if (GL20.glGetShaderi(vertexId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("VERTEX SHADER COMPILER ERROR!\n" + GL20.glGetShaderInfoLog(vertexId));
		}
		
		this.fragmentId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(this.fragmentId, this.fragmentCode);
		GL20.glCompileShader(fragmentId);
		
		if (GL20.glGetShaderi(fragmentId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("FRAGMENT SHADER COMPILER ERROR!\n" + GL20.glGetShaderInfoLog(fragmentId));
		}
		
		GL20.glAttachShader(programId, vertexId);
		GL20.glAttachShader(programId, fragmentId);
		
		GL20.glLinkProgram(programId);
		
		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("\n" + GL20.glGetProgramInfoLog(programId));
		}
		
		GL20.glValidateProgram(programId);
		
		if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("\n" + GL20.glGetProgramInfoLog(programId));
		}
		
		this.c = true;
	}
	
	public boolean isCreated() {
		return this.c;
	}
	
	int getUniformLocation(String name) {
		int i = GL20.glGetUniformLocation(programId,name);
		if (i == -1) {
			throw new RuntimeException(name + " is not a valid uniform name!");
		}
		return i;
	}
	
	void setUniformFloat(String name, float v) {
		GL20.glUniform1f(this.getUniformLocation(name), v);
	}
	
	void setUniformInteger(String name, int v) {
		GL20.glUniform1i(this.getUniformLocation(name), v);
	}
	
	void setUniformBoolean(String name, boolean v) {
		GL20.glUniform1i(this.getUniformLocation(name), v ? 1 : 0);
	}
	
	void setUniformVector2f(String name, Vector2f v) {
		GL20.glUniform2f(this.getUniformLocation(name), v.x, v.y);
	}
	
	void setUniformVector3f(String name, Vector3f v) {
		GL20.glUniform3f(this.getUniformLocation(name), v.x, v.y, v.z);
	}
	
	void setUniformVector4f(String name, Vector4f v) {
		GL20.glUniform4f(this.getUniformLocation(name), v.x, v.y, v.x, v.w);
	}
	
	void setUniformMatrix4f(String name, Matrix4f v) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
	        FloatBuffer buffer = stack.mallocFloat(16);
	        v.get(buffer);
	        GL20.glUniformMatrix4fv(getUniformLocation(name), false, buffer);
	    }
	}
	
	
	void start() {
		if (this.c) GL20.glUseProgram(programId);
		
	}
	
	void stop() {
		if (this.c) GL20.glUseProgram(0);
	}
	
	public void delete() {
		GL20.glDetachShader(programId, vertexId);
		GL20.glDeleteShader(vertexId);
		GL20.glDetachShader(programId, fragmentId);
		GL20.glDeleteShader(fragmentId);
		GL20.glDeleteProgram(programId);
	}
}
