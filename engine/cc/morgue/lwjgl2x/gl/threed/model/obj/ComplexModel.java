package cc.morgue.lwjgl2x.gl.threed.model.obj;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class ComplexModel extends Model {

	private List<VBO> vbos;
	
	public ComplexModel(List<VBO> vbos, Vector3f position) {
		super(position);
		
		this.vbos = vbos;
	}
	
	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(position.x, position.y, position.z);
		GL11.glScalef(scale, scale, scale);
		
		for(VBO vbo : vbos) {
			vbo.render();
		}
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void destroy() {
		for(VBO vbo : vbos) {
			vbo.destroy();
		}
	}
}
