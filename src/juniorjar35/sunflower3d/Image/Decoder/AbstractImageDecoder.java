package juniorjar35.sunflower3d.Image.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import juniorjar35.sunflower3d.Utils.Deleteable;

public abstract class AbstractImageDecoder implements Deleteable {
	
	public static AbstractImageDecoder getClassDecoder(Class<? extends AbstractImageDecoder> decoder) {
		AbstractImageDecoder decoder2 = null;
		
		try {
			decoder2 = decoder.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
		}
		
		return decoder2;
	}
	
	public abstract void decode(ByteBuffer buffer) throws IOException;
	public abstract int getHeight();
	public abstract int getWidth();
	public abstract String getType();
	public abstract ByteBuffer getPixels();
}
