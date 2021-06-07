package juniorjar35.sunflower3d.Audio.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public interface AudioDecoder extends AutoCloseable{
	
	public static AudioDecoder getDecoder(Class<? extends AudioDecoder> decoderClass) {
		AudioDecoder decoder = null;
		
		try {
			decoder = decoderClass.newInstance();
		} catch (Exception e) {
		}
		
		return decoder;
		
	}
	
	void decode(ByteBuffer buffer) throws IOException;
	String getType();
	int getChannels();
	int getSampleRate();
	ByteBuffer getPCM();
	boolean initialized();
	default ShortBuffer getPCMShorts() {
		return getPCM().asShortBuffer();
	}
}
