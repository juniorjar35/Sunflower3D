package juniorjar35.sunflower3d.Audio.Decoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

public class OGG implements AudioDecoder {
	
	private boolean init = false;
	private ByteBuffer pcm;
	private int chnl, rate;
	
	@Override
	public void decode(ByteBuffer buffer) throws IOException {
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			IntBuffer error = MemoryUtil.memAllocInt(1);
			long decoder = STBVorbis.stb_vorbis_open_memory(buffer, error, null);
			if (decoder == MemoryUtil.NULL) {
				throw new IOException("Failed to open OGG file! Error : " + error.get(0));
			}
			
			STBVorbis.stb_vorbis_get_info(decoder, info);
			int channels = info.channels();
			
			int length = (STBVorbis.stb_vorbis_stream_length_in_samples(decoder) * channels) * 2;
			
			if (length < 0) throw new IOException("Negative length!");
			
			this.pcm = MemoryUtil.memAlloc(length);
			
			STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm.asShortBuffer());
			
			STBVorbis.stb_vorbis_close(decoder);
			MemoryUtil.memFree(error);
			MemoryUtil.memFree(buffer);
			
			this.chnl = info.channels();
			this.rate = info.sample_rate();
			this.init = true;
			
		}
	}

	@Override
	public String getType() {
		return "OGG";
	}

	@Override
	public int getChannels() {
		return chnl;
	}

	@Override
	public int getSampleRate() {
		return rate;
	}

	@Override
	public ByteBuffer getPCM() {
		return pcm;
	}

	@Override
	public void close() throws Exception {
		if (init) MemoryUtil.memFree(pcm);
	}

	@Override
	public boolean initialized() {
		return init;
	}

}
