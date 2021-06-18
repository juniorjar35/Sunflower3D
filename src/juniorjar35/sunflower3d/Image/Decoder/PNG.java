package juniorjar35.sunflower3d.Image.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;

public class PNG implements ImageDecoder {
	
	private boolean init = false;
	private int w,h;
	private ByteBuffer data;
	
	@Override
	public void close() throws IOException {
		if (init) STBImage.stbi_image_free(data);
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
		return "PNG";
	}

	@Override
	public ByteBuffer getData() {
		return data;
	}
	@Override
	public void decode(ByteBuffer buffer) throws IOException {
		if (init) close();
		if (!buffer.isDirect()) throw new IOException("Image buffer is not a direct buffer!");
		int[] w = new int[1], h = new int[1], c = new int[1];
		if(!STBImage.stbi_info_from_memory(buffer, w, h, c)) { 
			throw new IOException("Image decoding failed: " + STBImage.stbi_failure_reason());
		};
		
		this.data = STBImage.stbi_load_from_memory(buffer, w, h, c, 4);
		this.w = w[0];
		this.h = h[0];
		this.init = true;
	}
	
}
