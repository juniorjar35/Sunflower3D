package juniorjar35.sunflower3d.Audio.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import juniorjar35.sunflower3d.Utils.Deleteable;

public abstract class AbstractAudioDecoder implements Deleteable {
	
	public static final int MONO8 = AL10.AL_FORMAT_MONO8,
							STEREO8 = AL10.AL_FORMAT_STEREO8,
							MONO16 = AL10.AL_FORMAT_MONO16,
							STEREO16 = AL10.AL_FORMAT_STEREO16;
	
	public static AbstractAudioDecoder getDecoder(Class<? extends AbstractAudioDecoder> decoderClass) {
		AbstractAudioDecoder decoder = null;
		
		try {
			decoder = decoderClass.newInstance();
		} catch (Exception e) {
		}
		
		return decoder;
		
	}
	
	public abstract void decode(ByteBuffer buffer) throws IOException;
	public abstract String getType();
	public abstract int getFormat();
	public abstract int getSampleRate();
	public abstract ByteBuffer getPCM();
	public abstract boolean initialized();
}
