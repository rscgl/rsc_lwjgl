package cc.morgue.lwjgl2x.gl.threed.model.obj.builder;

public class FaceVertex {

	int index = -1;
	public VertexGeometric v = null;
	public VertexTexture t = null;
	public VertexNormal n = null;

	public String toString() {
		return v + "|" + n + "|" + t;
	}
}