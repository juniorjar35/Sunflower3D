package juniorjar35.sunflower3d.Render;

import java.util.Objects;

import org.joml.Vector3f;

import juniorjar35.sunflower3d.Utils.Maths;

public class LightSource {
	
	Vector3f pos = new Vector3f(), clr = new Vector3f();
	
	public LightSource() {};
	
	public LightSource(Vector3f position, Vector3f color) {
		pos = Objects.requireNonNull(position);
		clr = Maths.clampVector3f(Objects.requireNonNull(color), new Vector3f(0,0,0), new Vector3f(1,1,1));
	}
	
	public void setPosition(Vector3f position) {
		pos = Objects.requireNonNull(position);
	}
	
	public void setPosition(float x, float y, float z) {
		this.pos.set(x, y, z);
	}
	
	public void setColor(Vector3f color) {
		clr = Objects.requireNonNull(color);
	}
	
	public void setColor(float r, float g, float b) {
		this.clr.set(r, g, b);
	}
	
}
