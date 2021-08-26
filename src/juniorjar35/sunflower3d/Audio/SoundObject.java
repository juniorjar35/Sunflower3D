package juniorjar35.sunflower3d.Audio;

import java.util.Objects;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import juniorjar35.sunflower3d.Utils.Deleteable;

public class SoundObject implements Deleteable {
	
	private int source;
	private SoundProperties props;
	
	protected SoundObject(int buffer, SoundProperties properties) {
		this.source = AL10.alGenSources();
		AL10.alSourcei(this.source, AL10.AL_BUFFER, buffer);
		this.props = properties;
		applyProperties();
		SoundManager.SOURCES.add(this);
	}
	
	public void setSoundProperties(SoundProperties properties) {
		Objects.requireNonNull(properties).objects.add(this);
		if (this.props != null) {
			this.props.objects.remove(this);
		}
		this.props = properties;
		applyProperties();
	}
	
	public SoundProperties getProperties() {
		return this.props;
	}
	
	protected void applyProperties() {
		AL10.alSource3f(source, AL10.AL_POSITION, this.props.pos.x, this.props.pos.y, this.props.pos.z);
		AL10.alSourcef(source, AL11.AL_GAIN, this.props.gain);
		AL10.alSourcef(source, AL10.AL_PITCH, this.props.pitch);
		AL10.alSource3f(source, AL10.AL_VELOCITY, this.props.vel.x, this.props.vel.y, this.props.vel.z);
		AL10.alSourcei(source, AL10.AL_LOOPING, this.props.looped ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	
	public void playSound() {
		if (source == -1) throw new IllegalStateException("Sound object has been deleted!");
		if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING && AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PAUSED) AL10.alSourceStop(source);
		AL10.alSourcePlay(source);
	}
	
	public void stopSound() {
		if (source == -1) throw new IllegalStateException("Sound object has been deleted!");
		if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_STOPPED) {
			AL10.alSourceStop(source);
		}
	}
	
	public void pauseSound() {
		if (source == -1) throw new IllegalStateException("Sound object has been deleted!");
		if ( AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PAUSED) {
			AL10.alSourcePause(source);
		}
	}
	
	public int getSource() {
		return source;
	}
	
	public void delete() {
		if (source != -1) {
			stopSound();
			SoundManager.SOURCES.remove(this);
			AL10.alDeleteBuffers(source);
			source = -1;
		}
	}
	
}
