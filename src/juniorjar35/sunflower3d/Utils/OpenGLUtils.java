package juniorjar35.sunflower3d.Utils;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL45;
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
	
	public static int loadTexture(ByteBuffer image, Class<? extends ImageDecoder> decoderClass, int filter, int wrap, boolean mipmapping, int mmfilter, float lod) {
		
		try (ImageDecoder decoder = ImageDecoder.getClassDecoder(decoderClass)){
			decoder.decode(image);
			int texture = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrap);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrap);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decoder.getData());
			if (mipmapping) {
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mmfilter);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, lod);
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			return texture;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	};
	
	public static int loadCubeMap(String[] textureFiles, Class<? extends ImageDecoder> decoderClass) {
		try(ImageDecoder decoder = ImageDecoder.getClassDecoder(decoderClass)){
			int texture = GL11.glGenTextures();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
			for (int i = 0; i < 6; i++) {
				decoder.decode(ResourceUtils.loadBufferDirect(textureFiles[i]));
				GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decoder.getData());
				decoder.close();
			}
			
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			
			return texture;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static String getErrorString(int error) {
		switch(error) {
		case GL11.GL_NO_ERROR:
			return "No error";
		case GL11.GL_INVALID_ENUM:
			return "Invalid enum";
		case GL11.GL_INVALID_VALUE:
			return "Invalid value";
		case GL11.GL_INVALID_OPERATION:
			return "Invalid operation";
		case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
			return "Invalid framebuffer operation";
		case GL31.GL_INVALID_INDEX:
			return "Invalid index";
		case GL45.GL_CONTEXT_LOST:
			return "Context lost";
		case GL11.GL_STACK_OVERFLOW:
			return "Stack overflow";
		case GL11.GL_STACK_UNDERFLOW:
			return "Stack underflow";
		case GL11.GL_OUT_OF_MEMORY:
			return "Out of memory";
		default:
			return "Unknown";
		}
	}
	
}
