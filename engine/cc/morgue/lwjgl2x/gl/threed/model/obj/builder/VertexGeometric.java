package cc.morgue.lwjgl2x.gl.threed.model.obj.builder;

public class VertexGeometric {

	public float x = 0;
	public float y = 0;
	public float z = 0;

	public VertexGeometric(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return x + "," + y + "," + z;
	}
}