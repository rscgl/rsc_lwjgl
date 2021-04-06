package cc.morgue.lwjgl2x.gl.threed.model.obj;

import java.awt.Color;

import org.lwjgl.util.vector.Vector3f;

public abstract class Model {

	protected Vector3f position;
	protected float scale = 1;
	protected float color[] = { -1, -1, -1 };
	
	protected Model(Vector3f position) {
		this.position = position;
	}
	
//	public float x;
	public void update() {
//		position.y += Math.sin((float) (x++ / 100f));
//		
//		if (x > 20) { 
//			x *= -1;
//		}
	}
	
	public abstract void render();
	public abstract void destroy();

	public void setPosition(float x, float z, float height) {
		this.position = new Vector3f(x, height, z);
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * FIXME: Color values must be set before mesh generation!
	 */
	public void setColor(Color color) {
		this.color[0] = color.getRed() / 255;
		this.color[1] = color.getGreen() / 255;
		this.color[2] = color.getBlue() / 255;
	}

	/**
	 * FIXME: Color values must be set before mesh generation!
	 */
	public void setColor(float red, float green, float blue) {
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;
	}

	/**
	 * FIXME: Color values must be set before mesh generation!
	 */
	public void setColor(int red, int green, int blue) {
		this.color[0] = red / 255;
		this.color[1] = green / 255;
		this.color[2] = blue / 255;
	}
	
}
