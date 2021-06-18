package juniorjar35.sunflower3d.Audio;

import java.util.Objects;

import org.joml.Vector3f;

public class SoundProperties {
	
	Vector3f pos = new Vector3f(0), vel = new Vector3f(0);
	float gain = 1, pitch = 1;
	boolean looped = false;
	
	public Vector3f getPos() {
		return pos;
	}

	public void setPosition(Vector3f pos) {
		this.pos = Objects.requireNonNull(pos);
	}
	
	public void setPosition(float x, float y, float z) {
		this.pos.x = x;
		this.pos.y = y;
		this.pos.z = z;
	}

	public Vector3f getVelocity() {
		return vel;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public float getPitch() {
		return pitch;
	}

	public void setVelocity(Vector3f vel) {
		this.vel = Objects.requireNonNull(vel);
	}
	
	public void setVelocity(float x, float y, float z) {
		this.vel.x = x;
		this.vel.y = y;
		this.vel.z = z;
	}
	
	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public boolean isLooped() {
		return looped;
	}

	public void setLooped(boolean looped) {
		this.looped = looped;
	}

	public SoundProperties(Vector3f position, Vector3f velocity, float gain,float pitch, boolean looped) {
		this.pos = Objects.requireNonNull(position);
		this.vel = Objects.requireNonNull(velocity);
		this.gain = gain;
		this.pitch = pitch;
		this.looped = looped;
	}
	
	public SoundProperties() { };
	
}
