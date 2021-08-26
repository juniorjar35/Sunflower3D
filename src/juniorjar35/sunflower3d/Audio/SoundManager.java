package juniorjar35.sunflower3d.Audio;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.EnumerateAllExt;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Render.Camera;

public final class SoundManager {
	protected static long DEVICE = 0L, CONTEXT = 0L;
	private static boolean INIT = false;
	
	protected static List<SoundFile> SOUNDFILES = new CopyOnWriteArrayList<SoundFile>();
	protected static List<SoundObject> SOURCES = new CopyOnWriteArrayList<SoundObject>();
	
	public static void openDevice(String audioDevice) {
		if (INIT) close();
		if (audioDevice == null) audioDevice = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		DEVICE = ALC10.alcOpenDevice(audioDevice);
		if (DEVICE == 0L) throw new IllegalArgumentException("Unable to find \"" + audioDevice + "\" output device!");
		CONTEXT = ALC10.alcCreateContext(DEVICE, (int[]) null);
		ALC10.alcMakeContextCurrent(CONTEXT);
		ALCCapabilities ALCC = ALC.createCapabilities(DEVICE); 
		ALCapabilities C_AL = AL.createCapabilities(ALCC);
		if (!ALCC.OpenALC11) throw new IllegalStateException("ALC 1.1 is not supported!");
		if (!C_AL.OpenAL11) throw new IllegalStateException("AL 1.1 is not supported!");
		INIT = true;
	}
	 
	public static void close() {
		if (!INIT) return;
		deleteSounds(SOURCES.toArray(new SoundObject[0]));
		for (SoundFile file : SOUNDFILES) {
			file.delete();
		}
		ALC10.alcDestroyContext(CONTEXT);
		CONTEXT = 0;
		ALC10.alcCloseDevice(DEVICE);
		DEVICE = 0;
		INIT = false;
	}
	
	public static boolean initialized() {
		return INIT;
	}
	
	public static SoundObject newSoundObject(SoundFile file, SoundProperties properties) {
		if (!INIT) return null;
		return new SoundObject(file.buffer, properties);
	}
	
	public static SoundObject newSoundObject(SoundFile file) {
		if (!INIT) return null;
		return new SoundObject(file.buffer, new SoundProperties());
	}
	
	public static void stopSounds(SoundObject... objects) {
		if (!INIT) return;
		for (SoundObject object : objects) {
			object.stopSound();
		}
	}
	
	public static void stopSound(SoundObject object) {
		if (!INIT) return;
		object.stopSound();
	}
	
	public static void pauseSounds(SoundObject... objects) {
		if (!INIT) return;
		for (SoundObject object : objects) {
			object.pauseSound();
		}
	}
	
	public static void pauseSound(SoundObject object) {
		if (!INIT) return;
		object.pauseSound();
	}
	
	public static void playSounds(SoundObject... objects) {
		if (!INIT) return;
		for (SoundObject object : objects) {
			object.playSound();
		}
	}
	
	public static void playSound(SoundObject object) {
		if (!INIT) return;
		object.playSound();
	}
	
	public static void modifySounds(SoundProperties properties, SoundObject... objects) {
		if (!INIT) return;
		Objects.requireNonNull(properties);
		for (SoundObject object : objects) {
			object.setSoundProperties(properties);
		}
	}
	
	public static void modifySound(SoundObject object, SoundProperties properties) {
		if (!INIT) return;
		object.setSoundProperties(properties);
	}
	
	public static void deleteSounds(SoundObject... objects) {
		if (!INIT) return;
		for (SoundObject object : objects) {
			object.delete();
		}
	}
	
	public static void deleteSound(SoundObject object) {
		if (!INIT) return;
		object.delete();
	}
	
	public static void listenerData(Camera cam) {
		Vector3f pos = cam.getPosition();
		
		Vector3f at = new Vector3f(0,0,-1);
		Vector3f up = new Vector3f(0,1,0);
		
		Matrix4f view = cam.getView().invert();
		
		view.transformDirection(at);
		view.transformDirection(up);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			FloatBuffer orientation = stack.mallocFloat(6);
			
			orientation.put(0, at.x);
			orientation.put(1, at.y);
			orientation.put(2, at.z);
			orientation.put(3, up.x);
			orientation.put(4, up.y);
			orientation.put(5, up.z);
			
			AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
			AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
		}
	}
	
	public static String[] getAllOutputDevices() {
		if (!ALC10.alcIsExtensionPresent(DEVICE, "ALC_ENUMERATE_ALL_EXT")) throw new IllegalStateException("ALC_ENUMERATE_ALL_EXT extension is not present!");
		long address = AL10.nalGetString(EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER);
		ByteBuffer buffer = MemoryUtil.memByteBufferNT2(address);
		byte[] bytes = new byte[buffer.capacity()];
		buffer.get(bytes);
		return new String(bytes).split("[" + '\u0000' + "]{2}")[0].split("[" + '\u0000' + "]");
	}
	
}
