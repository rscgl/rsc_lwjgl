package cc.morgue.lwjgl2x.gl.threed.model.obj;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class BasicModel extends Model {

	private VBO vbo;
	
	public BasicModel(VBO vbo, Vector3f position) {
		super(position);
		
		this.vbo = vbo;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(position.x, position.y, position.z);
		GL11.glScalef(scale, scale, scale);
		
		vbo.render();
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void destroy() {
		vbo.destroy();
	}
}
