package cc.morgue.lwjgl2x.gl.threed;

import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;

/**
 * A simple billboard object that supports a single texture.
 */
public class Billboard {

	/**
	 * The size of the billboard.
	 */
	private final float size;
	
	/**
	 * Is transparency enabled?
	 */
	private final boolean alpha;
	
	private float x;
	private float y;
	private float z;
	
	/**
	 * The applied texture id.
	 */
	private int textureId;
	
	/**
	 * Some re-used rotation variables..
	 */
	private float faceAngle, x1, x2, z1, z2;
	
	public Billboard(float size, boolean alpha) {
		this.size = size;
		this.alpha = alpha;
	}
	
	public void setLocation(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void render(float f, float g, Frustum frustum) {
		if (!frustum.cubeInFrustum(x, y, z, size)) {
			return;
		}
		// Face the camera
		faceAngle = calculateAngle(x, z, f, g);
		x1 = (float) Math.sin(Math.toRadians(faceAngle - 90)) * size;
		x2 = (float) Math.sin(Math.toRadians(faceAngle + 90)) * size;
		z1 = (float) -Math.cos(Math.toRadians(faceAngle - 90)) * size;
		z2 = (float) -Math.cos(Math.toRadians(faceAngle + 90)) * size;

		GL11.glColor3f(1, 1, 1);
		GL11.glColor4f(1, 1, 1, 1);
		
		// Modify the 3D object's culling properties
		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glEnable(GL11.GL_CULL_FACE);

		if (alpha) {
			GraphicsGL.enableAlpha();
		}
		TextureManager.getInstance().bind(textureId);

		// Build the physical model
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(x + x1, y, z + z1);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(x + x2, y, z + z2);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x + x2, y + size * 4, z + z2);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x + x1, y + size * 4, z + z1);
		}
		if (alpha) {
			GraphicsGL.disableAlpha();
		}
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_CULL_FACE); // Disable culling
	}
	
	
	/**
	 * Applies a texture to the object. The texture is grabbed from the <code>com.twod.texture.TextureManager</code>
	 * @param textureName The texture name.
	 */
	public void setTexture(String textureName) {
		this.textureId = TextureManager.getInstance().getTextureId(textureName);
	}
	
	/**
	 * @return The object's X coordinate.
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return The object's Y coordinate.
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return The object's Z coordinate.
	 */
	public float getZ() {
		return z;
	}
	
	/**
	 * @return The angle between two pairs of x,z coordinates.
	 */
	private float calculateAngle(float x1, float z1, float x2, float z2) {
		float f1 = (float) Math.toDegrees(Math.atan2(z1 - z2, x1 - x2));
		f1 += 270;
		while (f1 > 360) {
			f1 -= 360;
		}
		while (f1 < 0) {
			f1 += 360;
		}
		return f1;
	}

	public void unload() {
		// TODO stop using immediate mode!!
	}

}