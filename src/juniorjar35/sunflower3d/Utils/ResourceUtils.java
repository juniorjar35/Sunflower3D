package juniorjar35.sunflower3d.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public final class ResourceUtils {
	
	private static ResourceLoader LOADER = new ResourceLoader() {
		
		@Override
		public InputStream load(String resource) throws IOException {
			
			if (isFileReadable(resource)) {
				System.out.println("Loading from file system! File: " + resource);
				return new FileInputStream(resource);
			} else {
				System.out.println("Loading from resources! Resource: " + resource);
				return ResourceUtils.class.getResourceAsStream(resource);
			}
			
		}
	};
	
	public static class ByteBufferInputStream extends InputStream{
		
		private ByteBuffer buffer;
		
		public ByteBufferInputStream(ByteBuffer buffer) {
			this.buffer = Objects.requireNonNull(buffer);
		}
		
		@Override
		public synchronized int read() throws IOException {
			if (!buffer.hasRemaining()) {
				return -1;
			}
			return buffer.get();
		}
		
		@Override
		public synchronized int read(byte[] b, int off, int len) throws IOException {
			len = Math.min(len, buffer.remaining());
			buffer.get(b, off, len);
			return len;
		}
		
		@Override
		public int available() throws IOException {
			return buffer.remaining();
		}
	}
	
	public static interface ResourceLoader {
		InputStream load(String resource) throws IOException;
	}
	
	private ResourceUtils() { throw new UnsupportedOperationException(); };
	
	public static void setResourceLoader(ResourceLoader loader) {
		LOADER = Objects.requireNonNull(loader);
	}
	
	public static InputStream loadStream(String resource) throws IOException {
		return LOADER.load(resource);
	}
	
	public static String getFileExtension(String path) {
		String[] string = path.split("[.]");
		return string[string.length - 1];
	}
	
	public static boolean isFileReadable(String path) {
		return Files.isReadable(Paths.get(path));
	}
	
	public static void InputStreamToBuffer(ByteBuffer buffer, InputStream is) throws IOException {
		byte[] block = new byte[512];
		int x;
		while((x = is.read(block)) != -1) {
			buffer.put(block, 0, x);
		}
	}
	
	public static ByteBuffer loadBuffer(String resource) throws IOException {
		ByteBuffer result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = LOADER.load(resource);
		byte[] a = new byte[4096];
		int x;
		while((x = is.read(a)) != -1) {
			baos.write(a, 0, x);
		}
		byte[] w = baos.toByteArray();
		result = BufferUtils.createByteBuffer(w.length);
		result.put(w);
		result.flip();
		return result.slice();
	}
	
	public static FloatBuffer getFloatBuffer(FloatBuffer buffer) {
		FloatBuffer out = MemoryUtil.memAllocFloat(buffer.capacity());
		out.put(buffer).flip();
		
		return out;
	}
	
	public static FloatBuffer getFloatBuffer(float[] array) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(array.length);
		buffer.put(array).flip();
		return buffer;
	}
	
	public static IntBuffer getIntBuffer(IntBuffer buffer) {
		IntBuffer out = MemoryUtil.memAllocInt(buffer.capacity());
		out.put(buffer).flip();
		
		return out;
	}
	
	public static IntBuffer getIntBuffer(int[] array) {
		IntBuffer buffer = MemoryUtil.memAllocInt(array.length);
		buffer.put(array).flip();
		return buffer;
	}
	
	public static ByteBuffer loadBufferDirect(String resource) throws IOException {
		ByteBuffer result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = LOADER.load(resource);
		byte[] a = new byte[4096];
		int x;
		while((x = is.read(a)) != -1) {
			baos.write(a, 0, x);
		}
		byte[] w = baos.toByteArray();
		result = MemoryUtil.memAlloc(w.length);
		result.put(w);
		result.flip();
		return result;
	}
	
	public static String loadString(String resource) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(LOADER.load(resource)));
		StringBuilder result = new StringBuilder();
		String line = "";
		while((line = reader.readLine()) != null) {
			result.append(line).append('\n');
		}
		reader.close();
		return result.toString();
	}
	
	public static ByteBuffer loadFileBuffer(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ReadableByteChannel rbc = fis.getChannel();
		ByteBuffer buffer = BufferUtils.createByteBuffer(fis.available());
		rbc.read(buffer);
		fis.close();
		return buffer;
	}
	
	public static ByteBuffer loadFileBufferDirect(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ByteBuffer buffer = MemoryUtil.memAlloc(fis.available());
		fis.getChannel().read(buffer);
		fis.close();
		return buffer;
	}
	
	public static InputStream ByteBufferToInputStream(ByteBuffer buffer) {
		return new ByteBufferInputStream(buffer);
	}
	
	public static void copyResource(String res, File output) throws IOException {
		FileOutputStream fos = new FileOutputStream(output);
		fos.getChannel().write(loadBuffer(res));
		fos.flush();
		fos.close();
	}
	
	public static boolean doesFileExist(File directory, String filename) {
		if (!directory.isDirectory()) return false;
		return new File(directory, filename).exists();
	}
	
}
