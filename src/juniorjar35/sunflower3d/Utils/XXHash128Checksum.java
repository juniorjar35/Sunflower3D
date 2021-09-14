package juniorjar35.sunflower3d.Utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Checksum;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.xxhash.XXH128Hash;
import org.lwjgl.util.xxhash.XXHash;

public class XXHash128Checksum implements Checksum {
	
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	private long high64 = 0;
	private long low64 = 0;
	
	@Override
	public void update(int b) {
		baos.write(b);
	}

	@Override
	public void update(byte[] b, int off, int len) {
		baos.write(b, off, len);
	}
	
	private void calculate() {
		try (MemoryStack stack = MemoryStack.stackPush()){
			XXH128Hash hash = XXH128Hash.mallocStack(stack);
			ByteBuffer buffer = MemoryUtil.memAlloc(baos.size());
			buffer.put(baos.toByteArray()).flip();
			XXHash.XXH128(buffer, 0xABCD1234, hash);
			MemoryUtil.memFree(buffer);
			high64 = hash.high64();
			low64 = hash.low64();
		}
	}

	@Override
	public long getValue() {
		calculate();
		return (high64 & low64);
	}

	@Override
	public void reset() {
		baos.reset();
		high64 = 0;
		low64 = 0;
	}
	
	public long getHigh64() {
		return high64;
	}
	
	public long getLow64() {
		return low64;
	}
	
}
