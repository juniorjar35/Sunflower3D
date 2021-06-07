package juniorjar35.sunflower3d.Audio;

import java.nio.ByteBuffer;

import org.lwjgl.openal.AL10;

import juniorjar35.sunflower3d.Audio.Decoder.AudioDecoder;

public class SoundFile {
	
	protected int buffer = 0;
	
	public SoundFile(ByteBuffer data, Class<? extends AudioDecoder> decoderClass) {
		
		try (AudioDecoder decoder = AudioDecoder.getDecoder(decoderClass);){
			decoder.decode(data);
			this.buffer = AL10.alGenBuffers();
			AL10.alBufferData(buffer, decoder.getChannels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, decoder.getPCM(), decoder.getSampleRate());
			SoundManager.SOUNDFILES.add(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void delete() {
		if (buffer != 0) {
			AL10.alDeleteSources(buffer);
			if (SoundManager.SOUNDFILES.contains(this)) {
				SoundManager.SOUNDFILES.remove(this);
			}
		}
	}
	
}
