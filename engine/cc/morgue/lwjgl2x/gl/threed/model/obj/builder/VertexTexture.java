package cc.morgue.lwjgl2x.gl.threed.model.obj.builder;

public class VertexTexture {

	public float u = 0;
	public float v = 0;

	VertexTexture(float u, float v) {
		this.u = u;
		this.v = v;
	}

	public String toString() {
		return u + "," + v;
	}
}