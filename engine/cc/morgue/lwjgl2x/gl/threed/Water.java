package cc.morgue.lwjgl2x.gl.threed;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;

/**
 * A generic water tile which can be rendered under the player/camera,
 * and be drawn under a terrain to give the effect of water at lower height levels.
 *
 */
public class Water {

	private final int mesh;
	private final float height;

	public Water(float size, float height, int textureId, boolean alpha) {
		this.height = height;

		float startX = 0;
		float startZ = 0;
		float endX = startX + size;
		float endZ = startZ + size;

		// Generate the compiled gl mesh ID
		this.mesh = GL11.glGenLists(1);
		GL11.glNewList(mesh, GL11.GL_COMPILE);

		// Modify the 3D object's culling properties
		GraphicsGL.enableCulling(false, false);

		GraphicsGL.setColor2D(Color.WHITE);
		GraphicsGL.setColor3D(Color.WHITE, 1f);

		// Enable blending. Without this, transparency may not render as expected.
		if (alpha) {
			GraphicsGL.enableAlpha();
		}
		
		// Bind the given texture to the object
		TextureManager.getInstance().bind(textureId);

		// Build the physical model
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			/*
			 * The left tile half:
			 */
			// A
			// |\
			// | \
			// |  \
			// |   \
			//B|____\C
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX, height, startZ);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(startX, height, endZ);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(endX, height, endZ);

			/*
			 * The right tile half:
			 */
			// C_____B
			// \    |
			//  \   |
			//   \  |
			//    \ |
			//     \|A
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(endX, height, endZ);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(endX, height, startZ);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX, height, startZ);
		}

		if (alpha) {
			GraphicsGL.disableAlpha();
		}
		
		GL11.glEnd();
		GraphicsGL.disableCulling();
		GL11.glEndList();
	}

	public void render(float x, float z) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, height, z);
		GL11.glCallList(mesh);
		GL11.glPopMatrix();
	}

	public void unload() {
		GL11.glDeleteLists(mesh, 1);
	}

}
