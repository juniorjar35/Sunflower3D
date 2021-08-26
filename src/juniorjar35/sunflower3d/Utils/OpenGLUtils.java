package juniorjar35.sunflower3d.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Application;
import juniorjar35.sunflower3d.Image.Decoder.ImageDecoder;
import juniorjar35.sunflower3d.Render.Window;

public final class OpenGLUtils {
	private OpenGLUtils() {};
	
	public static GLCapabilities makeContext(Window window) {
		GLFW.glfwMakeContextCurrent(window.getWindowHandle());
		GLCapabilities cap = GL.createCapabilities();
		return cap;
	}
	
	public static String[] getGPUDetails() {
		if (!currentThreadHasContext()) throw new IllegalStateException("Current thread does not have context!");
		String[] details = new String[4];
		details[0] = GL11.glGetString(GL11.GL_RENDERER);
		details[1] = GL11.glGetString(GL11.GL_VENDOR);
		details[2] = GL11.glGetString(GL11.GL_VERSION);
		details[3] = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
		return details;
	}
	
	public static void deleteContext() {
		GL.setCapabilities(null);
		GLFW.glfwMakeContextCurrent(0);
	};
	
	public static GLCapabilities getCapabilities() {
		return GL.getCapabilities();
	}
	
	public static boolean currentThreadHasContext() {
		return GLFW.glfwGetCurrentContext() != 0;
	}
	
	public static int loadTexture(ByteBuffer image, Class<? extends ImageDecoder> decoderClass, int filter, int wrap, boolean mipmapping, int mmfilter, float lod) {
		try (ImageDecoder decoder = ImageDecoder.getClassDecoder(decoderClass)){
			decoder.decode(image);
			return loadTexture(decoder, filter, wrap, mipmapping, mmfilter, lod);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	};
	
	public static int loadTexture(ImageDecoder decoder, int filter, int wrap, boolean mipmapping, int mmfilter, float lod) {
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
	}
	
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
	
	public static void deleteTexture(int texture) {
		if (GL11.glIsTexture(texture)) {
			GL11.glDeleteTextures(texture);
		}
	}
	
	public static void deleteAllTextures() {
		int f = 0;
		for (int i = 0; i < 0x4000; i++) {
			if (f <= 4) break;
			if (GL11.glIsTexture(i)) {
				f = 0;
				GL11.glDeleteTextures(i);
			} else f++;
		}
	}
	
	public static Vector3f toOpenGLRGB(Vector3f rgb) {
		rgb.x = rgb.x / 255f;
		rgb.y = rgb.y / 255f;
		rgb.z = rgb.z / 255f;
		return rgb;
	}
	
	public static Vector3i toOpenGLRGB(Vector3i rgb) {
		rgb.x = rgb.x / 255;
		rgb.y = rgb.y / 255;
		rgb.z = rgb.z / 255;
		return rgb;
	}
	
	public static void dumpTextures() {
		if (!currentThreadHasContext()) throw new IllegalStateException("Current thread does not have a context!");
		File directory = new File(Application.getSavesDirectory(), "TextureDumps");
		directory.mkdir();
		Logger.DEBUG.println("Dumping textures...");
		int k = 0;
		int f = 0;
		for (int i = 0; i < 0x4000; i++) {
			if (f <= 3) break; 
			if (!GL11.glIsTexture(i)) {f++; continue;}
			f = 0;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i);
			Logger.DEBUG.println("Dumping texture " + i);
			int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
			int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			ByteBuffer pixels = MemoryUtil.memAlloc(4 * w * h);
			try {
				GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
				File file = new File(directory, "OpenGL_TextureDump_" + i + ".png");
				BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				for (int x = 0; x < w; x++) {
					for (int y = 0; y < h; y++) {
						int pos = (x + (w * y)) * 4;
						int r = pixels.get(pos) & 0xFF;
						int g = pixels.get(pos + 1) & 0xFF;
						int b = pixels.get(pos + 2) & 0xFF;
						int a = pixels.get(pos + 3) & 0xFF;
						image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b );
					}
				}
				ImageIO.write(image, "PNG", file);
				k++;
			} catch(Exception e) {
				System.err.println("Failed to dump texture " + i);
				continue;
			} finally {
				MemoryUtil.memFree(pixels);
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
		Logger.DEBUG.println("Dumped " + k + " textures!");
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
	
	public static String getFramebufferErrorString(int error) {
		switch(error){
		case GL30.GL_FRAMEBUFFER_COMPLETE:
			return "Complete";
		case GL30.GL_FRAMEBUFFER_UNDEFINED:
			return "Undefined";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			return "Incomplete attachment";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			return "Incomplete missing attachment";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
			return "Incomplete draw buffer";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
			return "Incomplete read buffer";
		case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
			return "Unsupported";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
			return "Incomplete multisample";
		default:
			return "Unknown";
		}
	}
	
}
