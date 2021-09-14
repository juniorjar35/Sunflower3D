package juniorjar35.sunflower3d.Utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Checksum;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.xxhash.XXHash;

public class XXHash64Checksum implements Checksum {
	
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	@Override
	public void update(int b) {
		baos.write(b);
	}

	@Override
	public void update(byte[] b, int off, int len) {
		baos.write(b, off, len);
	}

	@Override
	public long getValue() {
		long hash = 0;
		ByteBuffer buffer = MemoryUtil.memAlloc(baos.size());
		buffer.put(baos.toByteArray()).flip();
		hash = XXHash.XXH64(buffer, 0xABCD1234);
		MemoryUtil.memFree(buffer);
		return hash;
	}

	@Override
	public void reset() {
		baos.reset();
	}

}
