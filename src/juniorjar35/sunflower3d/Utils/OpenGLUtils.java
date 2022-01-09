package juniorjar35.sunflower3d.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Application;
import juniorjar35.sunflower3d.Image.Decoder.AbstractImageDecoder;
import juniorjar35.sunflower3d.Render.Window;

public final class OpenGLUtils {
	private OpenGLUtils() {};
	
	public static GLCapabilities makeContext(Window window) {
		GLFW.glfwMakeContextCurrent(window.getWindowHandle());
		GLCapabilities cap = GL.createCapabilities();
		return cap;
	}
	
	/**
	 * 	<li>Index 0: {@link GL11#GL_RENDERER GL_RENDERER}</li>
	 * 	<li>Index 1: {@link GL11#GL_VENDOR GL_VENDOR}</li>
	 *  <li>Index 2: {@link GL11#GL_VERSION GL_VERSION}</li>
	 *  <li>Index 3: {@link GL20#GL_SHADING_LANGUAGE_VERSION GL_SHADING_LANGUAGE_VERSION}</li>
	 */
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
	
	public static int loadTexture(ByteBuffer image, Class<? extends AbstractImageDecoder> decoderClass, int filter, int wrap, boolean mipmapping, int mmfilter, float lod) {
		try (AbstractImageDecoder decoder = AbstractImageDecoder.getClassDecoder(decoderClass)){
			decoder.decode(image);
			return loadTexture(decoder, filter, wrap, mipmapping, mmfilter, lod);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	};
	
	public static int loadTexture(AbstractImageDecoder decoder, int filter, int wrap, boolean mipmapping, int mmfilter, float lod) {
		if (!currentThreadHasContext()) throw new IllegalStateException("Current thread does not have a context!");
		int texture = OpenGLUtils.genTexAndBind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decoder.getPixels());
		if (mipmapping) {
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mmfilter);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, lod);
		}
		OpenGLUtils.unbindTex();
		return texture;
	}
	
	public static int loadCubeMap(String[] textureFiles, Class<? extends AbstractImageDecoder> decoderClass) {
		if (!currentThreadHasContext()) throw new IllegalStateException("Current thread does not have a context!");
		try(AbstractImageDecoder decoder = AbstractImageDecoder.getClassDecoder(decoderClass)){
			int texture = GL11.glGenTextures();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
			for (int i = 0; i < 6; i++) {
				ByteBuffer img = ResourceUtils.loadBufferDirect(textureFiles[i]);
				decoder.decode(img);
				GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, decoder.getPixels());
				decoder.close();
				MemoryUtil.memFree(img);
			}
			
			
			return texture;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static GLFWImage generateGLFWImage(Class<? extends AbstractImageDecoder> dclass, String resources) throws IOException{
		GLFWImage image = null;
		try (AbstractImageDecoder aid = AbstractImageDecoder.getClassDecoder(dclass)){
			ByteBuffer bytes = ResourceUtils.loadBufferDirect(resources); 
			aid.decode(bytes);
			image = generateGLFWImage(aid.getPixels(), aid.getWidth(), aid.getHeight());
			MemoryUtil.memFree(bytes);
		}
		return image;
	}
	
	public static GLFWImage generateGLFWImage(ByteBuffer pixels, int width, int height) {
		GLFWImage image = GLFWImage.malloc();
		image.set(width, height, pixels);
		return image;
	}
	
	public static void deleteTextures(int max) {
		if (!currentThreadHasContext()) throw new IllegalStateException("Current thread does not have a context!");
		if (max < 10) max = 10;
		for (int i = 0; i < max; i++) {
			if (GL11.glIsTexture(i)) {
				GL11.glDeleteTextures(i);
			};
		}
	}
	
	public static void enableOneMinusAlphaSource() {
		enableAlphaBlending(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void enableAlphaBlending(int sfactor, int dfactor) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(sfactor, dfactor);
	}
	
	public static void disableAlphaBlending() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void enableDefaultDepth() {
		enableDepth(GL11.GL_LESS, true);
	}
	
	public static void enableDepth(int func, boolean mask) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(func);
		GL11.glDepthMask(mask);
	}
	
	public static void disableDepth() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static int genTex() {
		return GL11.glGenTextures();
	}
	
	public static int genTexAndBind() {
		int t = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, t);
		return t;
	}
	
	public static void unbindTex() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public static void deleteTexture(int texture) {
		if (GL11.glIsTexture(texture)) {
			GL11.glDeleteTextures(texture);
		}
	}
	
	public static void TextureImageBind2D(int internal, int format, int width, int height, int type, ByteBuffer pixels) {
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internal, width, height, 0, format, type, pixels);
	}
	
	public static void glTexParameteriMagMin(int mag, int min) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, min);
	}
	
	public static void bindTextureForRendering(int texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
	
	public static int genBuffer() {
		return GL15.glGenBuffers();
	}
	
	public static int genBufferAndBind() {
		int b = genBuffer();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, b);
		return b;
	}
	
	public static void unbindBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public static void deleteBuffer(int buffer) {
		if (GL15.glIsBuffer(buffer)) GL15.glDeleteBuffers(buffer);
	}
	
	public static int genVertexArray() {
		return GL30.glGenVertexArrays();
	}
	
	public static int genVertexArrayAndBind() {
		int v = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(v);
		return v;
	}
	
	public static void storeFloatsInBuffer(FloatBuffer buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeDoublesInBuffer(DoubleBuffer buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeUnsignedBytesInBuffer(ByteBuffer buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeBytesInBuffer(ByteBuffer buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeIntegersInBuffer(IntBuffer buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeFloatsInVertexArray(FloatBuffer buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
	}
	
	public static void storeDoublesInVertexArray(DoubleBuffer buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_DOUBLE, false, 0, 0);
	}
	
	public static void storeUnsignedBytesInVertexArray(ByteBuffer buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_UNSIGNED_BYTE, false, 0, 0);
	}
	
	public static void storeBytesInVertexArray(ByteBuffer buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_BYTE, false, 0, 0);
	}
	
	public static void storeIntegersInVertexArray(IntBuffer buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_UNSIGNED_INT, false, 0, 0);
	}
	
	public static void storeFloatsInBuffer(float[] buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeDoublesInBuffer(double[] buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeIntegersInBuffer(int[] buffer) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeFloatsInVertexArray(float[] buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
	}
	
	public static void storeDoublesInVertexArray(double[] buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_DOUBLE, false, 0, 0);
	}
	
	public static void storeIntegersInVertexArray(int[] buffer, int index, int size) {
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_UNSIGNED_INT, false, 0, 0);
	}
	
	public static void unbindVertexArray() {
		GL30.glBindVertexArray(0);
	}
	
	public static void deleteVertexArray(int vao) {
		if (GL30.glIsVertexArray(vao)) GL30.glDeleteVertexArrays(vao);
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
	
	public static void dumpTextures(int count) {
		if (!currentThreadHasContext()) throw new IllegalStateException("Current thread does not have a context!");
		File directory = new File(Application.getSavesDirectory(), "TextureDumps");
		directory.mkdir();
		Logger.DEBUG.println("Dumping textures...");
		int k = 0;
		for (int i = 0; i < count; i++) {
			if (!GL11.glIsTexture(i)) {continue;}
			Logger.DEBUG.println("Dumping texture " + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i);
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
						int r = (pixels.get(pos) & 0xFF) << 16;
						int g = (pixels.get(pos + 1) & 0xFF) << 8;
						int b = (pixels.get(pos + 2) & 0xFF) << 0;
						int a = (pixels.get(pos + 3) & 0xFF) << 24;
						image.setRGB(x, y, a|r|g|b);
					}
				}
				ImageIO.write(image, "PNG", file);
				k++;
				Logger.DEBUG.println("Dumped");
			} catch(Exception e) {
				System.err.println("Failed to dump texture " + i);
			} finally {
				MemoryUtil.memFree(pixels);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			}
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
	
	public static String getGLFWErrorString(int error) {
		switch(error) {
		case GLFW.GLFW_NO_ERROR:
			return "No error";
		case GLFW.GLFW_NOT_INITIALIZED:
			return "Not initialized";
		case GLFW.GLFW_NO_CURRENT_CONTEXT:
			return "No current context";
		case GLFW.GLFW_INVALID_ENUM:
			return "Invalid enum";
		case GLFW.GLFW_INVALID_VALUE:
			return "Invalid value";
		case GLFW.GLFW_OUT_OF_MEMORY:
			return "Out of memory";
		case GLFW.GLFW_API_UNAVAILABLE:
			return "API unavailable";
		case GLFW.GLFW_VERSION_UNAVAILABLE:
			return "Version unavailable";
		case GLFW.GLFW_PLATFORM_ERROR:
			return "Platform error";
		case GLFW.GLFW_FORMAT_UNAVAILABLE:
			return "Format unavailable";
		case GLFW.GLFW_NO_WINDOW_CONTEXT:
			return "No window context";
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
