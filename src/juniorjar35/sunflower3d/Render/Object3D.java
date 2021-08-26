package juniorjar35.sunflower3d.Render;

import java.util.Objects;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Object3D {
	
	RenderableObject obj;
	Vector3f pos, rot, scale;
	float reflectivity, shineAngle;
	
	private Matrix4f transformation = new Matrix4f();
	
	public Object3D(RenderableObject object, Vector3f position, Vector3f orientation, Vector3f scale, float reflectivity, float shineAngle) {
		this.obj = Objects.requireNonNull(object);
		this.pos = Objects.requireNonNull(position);
		this.rot = Objects.requireNonNull(orientation);
		this.scale = scale;
		this.reflectivity = (reflectivity);
		this.shineAngle = (shineAngle);
	}
	
	
	
	public Matrix4f transform() {
		transformation.identity();
		transformation.translate(pos);
		transformation.rotateX((float) Math.toRadians(rot.x));
		transformation.rotateY((float) Math.toRadians(rot.y));
		transformation.rotateZ((float) Math.toRadians(rot.z));
		transformation.scale(scale);
		return transformation;
	}
	
	public Vector3f getPosition() {
		return pos;
	}

	public void setPosition(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getOrientation() {
		return rot;
	}

	public void setOrientation(Vector3f rot) {
		this.rot = rot;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public RenderableObject getObject() {
		return this.obj;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getShineAngle() {
		return shineAngle;
	}

	public void setShineAngle(float shineAngle) {
		this.shineAngle = shineAngle;
	}
	
}
