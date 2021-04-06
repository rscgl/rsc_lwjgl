package cc.morgue.lwjgl2x.gl.threed.model.obj.builder;

public class VertexNormal {
	
	public float x = 0;
	public float y = 0;
	public float z = 0;

	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public VertexNormal(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return x+","+y+","+z;
	}
}