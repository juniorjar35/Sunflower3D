package juniorjar35.sunflower3d.Image.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import juniorjar35.sunflower3d.Utils.Deleteable;

public interface ImageDecoder extends Deleteable {
	
	public static ImageDecoder getClassDecoder(Class<? extends ImageDecoder> decoder) {
		ImageDecoder decoder2 = null;
		
		try {
			decoder2 = decoder.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
		}
		
		return decoder2;
	}
	
	void decode(ByteBuffer buffer) throws IOException;
	int getHeight();
	int getWidth();
	String getType();
	ByteBuffer getData();
}
