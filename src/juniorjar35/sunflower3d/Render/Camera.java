package juniorjar35.sunflower3d.Render;

import java.util.Objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import juniorjar35.sunflower3d.Audio.SoundManager;

public class Camera {
	
	Vector3f pos, rot;
	
	private float speed = 0.05f;
	private float sensitivity = 0.15f;
	
	private Matrix4f view = new Matrix4f();
	
	private Window window;
	
	Camera(Vector3f position, Vector3f orientation, Window window){
		this.pos = position;
		this.rot = orientation;
		this.window = window;
	}
	
	public Vector3f getPosition() {
		return pos;
	}
	
	public void setPosition(Vector3f position) {
		this.pos = Objects.requireNonNull(position);
	}
	
	public Vector3f getOrientation() {
		return rot;
	}
	
	public void setOrientation(Vector3f orientation) {
		this.rot = Objects.requireNonNull(orientation);
	}
	
	public Matrix4f getView() {
		view.identity();
		Vector3f npos = new Vector3f(pos).negate();
		view.rotateX(-(float) Math.toRadians(rot.x));
		view.rotateY(-(float) Math.toRadians(rot.y));
		view.rotateZ(-(float) Math.toRadians(rot.z));
		view.translate(npos);
		return view;
	}
	
	private double ox,oy,nx,ny;
	
	void update() {
		
		if (!window.isCursorLocked()) return;
		
		nx = window.mouse.x;
		ny = window.mouse.y;
		
		float x = (float) Math.sin(Math.toRadians(rot.y)) * speed;
		float z = (float) Math.cos(Math.toRadians(rot.y)) * speed;
		
		if (window.isKeyDown(GLFW.GLFW_KEY_A)) 			this.pos.add( -z,  0,   x );
		if (window.isKeyDown(GLFW.GLFW_KEY_D)) 			this.pos.add(  z,  0,  -x );
		if (window.isKeyDown(GLFW.GLFW_KEY_W)) 			this.pos.add( -x,  0,  -z );
		if (window.isKeyDown(GLFW.GLFW_KEY_S)) 			this.pos.add(  x,  0,   z );
		if (window.isKeyDown(GLFW.GLFW_KEY_SPACE)) 		this.pos.add( 0,  speed, 0 );
		if (window.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) this.pos.add( 0, -speed, 0 );
		
		rot.sub((float) (ny - oy) * sensitivity,(float) (nx - ox) * sensitivity, 0 );
		ox = nx;
		oy = ny;
		SoundManager.listenerData(this);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(float sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	
}
