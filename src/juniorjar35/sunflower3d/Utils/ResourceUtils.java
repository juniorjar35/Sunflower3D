package juniorjar35.sunflower3d.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Image.Decoder.ImageDecoder;

public final class ResourceUtils {
	
	private static ResourceLoader LOADER = new ResourceLoader() {
		
		@Override
		public InputStream load(String resource) throws IOException {
			
			if (isFileReadable(resource)) {
				Logger.debug("Loading from file system! File: " + resource);
				return new FileInputStream(resource);
			} else {
				Logger.debug("Loading from resources! Resource: " + resource);
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
		public int read() {
			if (!buffer.hasRemaining()) {
				return -1;
			}
			return buffer.get() & 0xFF;
		}
		
		@Override
		public int read(byte[] b, int off, int len) {
			if (!buffer.hasRemaining()) {
				return -1;
			}
			len = Math.min(len, buffer.remaining());
			buffer.get(b, off, len);
			return len;
		}
		
		@Override
		public int available() throws IOException {
			return buffer.remaining();
		}
		
	}
	
	public static class ByteBufferOutputStream extends OutputStream {
		
		private ByteBuffer buffer;
		
		public ByteBufferOutputStream(ByteBuffer buffer) {
			this.buffer = Objects.requireNonNull(buffer);
		}
		
		@Override
		public void write(int b) throws IOException {
			buffer.put((byte) b);	
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			buffer.put(b, off, len);
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
	
	public static ByteBuffer copyInputStream(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(in, baos);
		ByteBuffer result = ByteBuffer.allocate(baos.size()).order(ByteOrder.nativeOrder()).put(baos.toByteArray());
		result.flip();
		return result;
	}
	
	public static ByteBuffer copyInputStreamDirect(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(in, baos);
		ByteBuffer result = MemoryUtil.memAlloc(baos.size()).put(baos.toByteArray());
		result.flip();
		return result;
	}
	
	public static ByteBuffer loadBuffer(String resource) throws IOException {
		ByteBuffer result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = LOADER.load(resource);
		copy(is,baos);
		byte[] w = baos.toByteArray();
		result = ByteBuffer.wrap(w);
		result.flip();
		is.close();
		baos.close();
		return result.slice();
	}
	
	public static void loadInto(String resource, OutputStream out) throws IOException {
		InputStream in = loadStream(resource);
		copy(in, out);
		in.close();
	}
	
	public static void copy(InputStream from, OutputStream to) throws IOException {
		int x;
		byte[] block = new byte[1024];
		while((x = from.read(block)) != -1) {
			to.write(block, 0, x);
		}
	}
	
	public static void copy(InputStream from, ByteBuffer to) throws IOException {
		byte[] block = new byte[1024];
		int x;
		while((x = from.read(block)) != -1) {
			to.put(block, 0, x);
		}
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
		is.close();
		baos.close();
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
	
	public static InputStream ByteBufferToInputStream(ByteBuffer buffer) {
		return new ByteBufferInputStream(buffer);
	}
	
	public static OutputStream OutputStreamToByteBuffer(ByteBuffer buffer) {
		return new ByteBufferOutputStream(buffer);
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
	
	public static ImageDecoder decodeImage(ByteBuffer buffer, Class<? extends ImageDecoder> clazz) throws IOException {
		ImageDecoder decoder = ImageDecoder.getClassDecoder(clazz);
		decoder.decode(buffer);
		return decoder;
	}
	
	public static File getCurrentLocation() {
		return new File(ResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	}
	
	public static String getFilename(String path) {
		int max = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
		return path.substring(max + 1);
	}
	
}
