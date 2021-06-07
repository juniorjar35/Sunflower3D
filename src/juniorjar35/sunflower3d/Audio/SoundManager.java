package juniorjar35.sunflower3d.Audio;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Render.Camera;

public final class SoundManager {
	
	private static long DEVICE = 0L, CONTEXT = 0L;
	private static boolean INIT = false;
	
	protected static List<SoundFile> SOUNDFILES = new CopyOnWriteArrayList<SoundFile>();
	private static List<Integer> SOURCES = new CopyOnWriteArrayList<Integer>();
	
	private static FloatBuffer orientation = null;
	
	public static void openDevice(String device) {
		if (INIT) close();
		if (device == null) device = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		DEVICE = ALC10.alcOpenDevice(device);
		if (DEVICE == 0L) throw new IllegalArgumentException("Unable to find \"" + device + "\" output device!");
		CONTEXT = ALC10.alcCreateContext(DEVICE, (int[]) null);
		ALC10.alcMakeContextCurrent(CONTEXT);
		ALCCapabilities alc = ALC.createCapabilities(DEVICE);
		ALCapabilities cap = AL.createCapabilities(alc);
		
		if (!alc.OpenALC11) throw new IllegalStateException("ALC 1.1 is not supported!");
		if (!cap.OpenAL11) throw new IllegalStateException("AL 1.1 is not supported!");
		orientation = MemoryUtil.memAllocFloat(6);
		
		INIT = true;
	}
	 
	public static void close() {
		if (!INIT) return;
		stopAllSounds();
		MemoryUtil.memFree(orientation);
		orientation = null;
		ALC10.alcDestroyContext(CONTEXT);
		CONTEXT = 0;
		ALC10.alcCloseDevice(DEVICE);
		DEVICE = 0;
		INIT = false;
	}
	
	public static boolean initialized() {
		return INIT;
	}
	
	public static void stopAllSounds() {
		for (int source : SOURCES) {
			AL10.alSourceStop(source);
			AL10.alDeleteSources(source);
		}
		SOURCES.clear();
	}
	
	public static void stopSound(int source) {
		if (AL10.alIsSource(source) && AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_STOPPED) {
			AL10.alSourceStop(source);
			AL10.alDeleteSources(source);
		}
	}
	
	public static void pauseSound(int source) {
		if (AL10.alIsSource(source) && AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PAUSED) {
			AL10.alSourcePause(source);
		}
	}
	
	public static void resumeSound(int source) {
		if (AL10.alIsSource(source) && AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED) {
			AL10.alSourcePlay(source);
		}
	}
	
	public static int playSound(SoundFile sound, SoundProperties properties) {
		int source = AL10.alGenSources();
		
		AL10.alSourcei(source, AL10.AL_BUFFER, sound.buffer);
		AL10.alSource3f(source, AL10.AL_POSITION, properties.pos.x, properties.pos.y, properties.pos.z);
		AL10.alSourcef(source, AL11.AL_GAIN, properties.gain);
		AL10.alSourcef(source, AL10.AL_PITCH, properties.pitch);
		AL10.alSource3f(source, AL10.AL_VELOCITY, properties.vel.x, properties.vel.y, properties.vel.z);
		AL10.alSourcei(source, AL10.AL_LOOPING, properties.looped ? AL10.AL_TRUE : AL10.AL_FALSE);
		AL10.alSourcePlay(source);
		
		return source;
	}
	
	public static void modifySound(int source, SoundProperties properties) {
		
		if (AL10.alIsSource(source)) {
			AL10.alSourcei(source, AL10.AL_BUFFER, source);
			AL10.alSource3f(source, AL10.AL_POSITION, properties.pos.x, properties.pos.y, properties.pos.z);
			AL10.alSourcef(source, AL11.AL_GAIN, properties.gain);
			AL10.alSourcef(source, AL10.AL_PITCH, properties.pitch);
			AL10.alSource3f(source, AL10.AL_VELOCITY, properties.vel.x, properties.vel.y, properties.vel.z);
			AL10.alSourcei(source, AL10.AL_LOOPING, properties.looped ? AL10.AL_TRUE : AL10.AL_FALSE);
		}
		
	}
	
	public static void listenerData(Camera cam) {
		Vector3f pos = cam.getPosition();
		
		Vector3f at = new Vector3f(0,0,-1);
		Vector3f up = new Vector3f(0,1,0);
		
		Matrix4f view = cam.getView().invert();
		
		view.transformDirection(at);
		view.transformDirection(up);
		
		orientation.put(0, at.x);
		orientation.put(1, at.y);
		orientation.put(2, at.z);
		
		orientation.put(3, up.x);
		orientation.put(4, up.y);
		orientation.put(5, up.z);
		
		AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
		AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
	}
	
	public static String[] getAllOutputDevices() {
		long address = AL10.nalGetString(ALC11.ALC_ALL_DEVICES_SPECIFIER);
		ByteBuffer buffer = MemoryUtil.memByteBufferNT2(address);
		byte[] bytes = new byte[buffer.capacity()];
		buffer.get(bytes);
		return new String(bytes).split("[" + '\u0000' + "]{2}")[0].split("[" + '\u0000' + "]");
	}
	
	
	
}
