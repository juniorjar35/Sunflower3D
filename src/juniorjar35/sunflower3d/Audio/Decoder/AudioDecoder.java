package juniorjar35.sunflower3d.Audio.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import juniorjar35.sunflower3d.Utils.Deleteable;

public interface AudioDecoder extends Deleteable {
	
	public static final int MONO8 = AL10.AL_FORMAT_MONO8,
							STEREO8 = AL10.AL_FORMAT_STEREO8,
							MONO16 = AL10.AL_FORMAT_MONO16,
							STEREO16 = AL10.AL_FORMAT_STEREO16;
	
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
	int getFormat();
	int getSampleRate();
	ByteBuffer getPCM();
	boolean initialized();
}
