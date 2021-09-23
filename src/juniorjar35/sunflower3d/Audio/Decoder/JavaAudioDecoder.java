package juniorjar35.sunflower3d.Audio.Decoder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Utils.ResourceUtils;
import juniorjar35.sunflower3d.Utils.ResourceUtils.ByteBufferInputStream;

public class JavaAudioDecoder extends AbstractAudioDecoder {
	
	private int frmt, rate;
	private ByteBuffer buffer;
	private boolean init = false;
	
	
	@Override
	public void delete() {
		if (init) { MemoryUtil.memFree(buffer); init = false;}
	}
	
	@Override
	public void decode(ByteBuffer in) throws IOException {
		delete();
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteBufferInputStream(in)));
			this.buffer = ResourceUtils.copyInputStreamDirect(ais);
			AudioFormat format = ais.getFormat();
			if (format.getSampleSizeInBits() == 8) { 
				if (format.getChannels() == 2) {
					frmt = MONO8;
				} else {
					frmt = STEREO8;
				}
			} else if (format.getSampleSizeInBits() == 16) {
				if (format.getChannels() == 2) {
					frmt = MONO16;
				} else {
					frmt = STEREO16;
				}
			}
			this.rate = (int) format.getSampleRate();
			ais.close();
			this.init = true;
		} catch (UnsupportedAudioFileException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getType() {
		return "Java";
	}

	@Override
	public int getFormat() {
		return frmt;
	}

	@Override
	public int getSampleRate() {
		return rate;
	}

	@Override
	public ByteBuffer getPCM() {
		return this.buffer;
	}

	@Override
	public boolean initialized() {
		return init;
	}

}
