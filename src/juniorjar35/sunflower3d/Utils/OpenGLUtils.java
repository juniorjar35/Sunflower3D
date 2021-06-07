package juniorjar35.sunflower3d.Utils;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;

import juniorjar35.sunflower3d.Image.Decoder.ImageDecoder;

public final class OpenGLUtils {
	private OpenGLUtils() {};
	
	private volatile static GLCapabilities cap = null;
	private volatile static Thread contextThread = null;
	
	public static GLCapabilities makeContext(long windowHandle) {
		GLFW.glfwMakeContextCurrent(windowHandle);
		contextThread = Thread.currentThread();
		cap = GL.createCapabilities();
		return cap;
	}
	
	public static Thread getThreadWithContext() {
		return contextThread;
	}
	
	public static void deleteContext() {
		cap = null;
		contextThread = null;
		GLFW.glfwMakeContextCurrent(0);
	};
	
	public static boolean currentThreadHasContext() {
		return GLFW.glfwGetCurrentContext() != 0;
	}
	
	public static int loadTexture(ByteBuffer image, Class<? extends ImageDecoder> decoderClass, int filter, int wrap) {
		
		try (ImageDecoder decoder = ImageDecoder.getClassDecoder(decoderClass)){
			decoder.decode(image);
			int texture = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrap);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrap);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decoder.getData());
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			return texture;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	};
	
	
	public static String getErrorString(int error) {
		switch(error) {
		case GL11.GL_INVALID_ENUM:
			return "Invalid enum";
		case GL11.GL_INVALID_VALUE:
			return "Invalid value";
		case GL11.GL_INVALID_OPERATION:
			return "Invalid operation";
		case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
			return "Invalid framebuffer operation";
		default:
			return "Unknown";
		}
	}
	
}
