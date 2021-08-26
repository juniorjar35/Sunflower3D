package juniorjar35.sunflower3d.Image.Decoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Utils.ResourceUtils;

public class JavaImageDecoder implements ImageDecoder {
	
	private ByteBuffer buffer;
	private boolean init = false;
	private int w,h;
	
	@Override
	public void delete() {
		if (init) { MemoryUtil.memFree(buffer); init = false; }
	}

	@Override
	public void decode(ByteBuffer buffer) throws IOException {
		delete();
		BufferedImage image = ImageIO.read(ResourceUtils.ByteBufferToInputStream(buffer));
		w = image.getWidth();
		h = image.getHeight();
		this.buffer = MemoryUtil.memAlloc(w * h * 4);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int c = image.getRGB(x, y);
				this.buffer.put((byte) ((c >> 16) & 0xFF));
				this.buffer.put((byte) ((c >> 8) & 0xFF));
				this.buffer.put((byte) (c & 0xFF));
				this.buffer.put((byte) ((c >> 24) & 0xFF));
			}
		}
		this.buffer.flip();
		this.init = true;
	}

	@Override
	public int getHeight() {
		return h;
	}

	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public String getType() {
		return "Java";
	}

	@Override
	public ByteBuffer getData() {
		return this.buffer;
	}

}
