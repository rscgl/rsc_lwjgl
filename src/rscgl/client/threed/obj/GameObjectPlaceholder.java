package rscgl.client.threed.obj;

import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.threed.VectorObject;
import rscgl.client.Camera;

/**
 */
public class GameObjectPlaceholder {

	private final VectorObject mesh;

	public GameObjectPlaceholder(int startX, int startZ, float tileHeightValue) {
		float endX = startX + 0.5f;
		float endZ = startZ + 0.5f;
		
		int textureId = TextureManager.getInstance().getTextureId("pink");

		// Generate the compiled gl mesh ID
		int meshId = GL11.glGenLists(1);
		GL11.glNewList(meshId, GL11.GL_COMPILE);

		// Bind the given texture to the object
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		float width = endX - startX;
		float height = 0.5f - tileHeightValue;
		float length = endZ - startZ;

		// Build the physical model
		GL11.glBegin(GL11.GL_QUADS);
		{
			
			// Top of wall
			GL11.glVertex3f(startX, tileHeightValue, startZ);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX + width, tileHeightValue, startZ);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(startX + width, tileHeightValue, startZ + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(startX, tileHeightValue, startZ + length);
			GL11.glTexCoord2f(0, 1);

			// Bottom of wall
			GL11.glVertex3f(startX, tileHeightValue + height, startZ);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX + width, tileHeightValue + height, startZ);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(startX + width, tileHeightValue + height, startZ + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(startX, tileHeightValue + height, startZ + length);
			GL11.glTexCoord2f(0, 1);

			// Front of wall
			GL11.glVertex3f(startX, tileHeightValue, startZ);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX + width, tileHeightValue, startZ);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(startX + width, tileHeightValue + height, startZ);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(startX, tileHeightValue + height, startZ);
			GL11.glTexCoord2f(0, 1);
			
			// Back of wall
			GL11.glVertex3f(startX, tileHeightValue, startZ + length);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX + width, tileHeightValue, startZ + length);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(startX + width, tileHeightValue + height, startZ + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(startX, tileHeightValue + height, startZ + length);
			GL11.glTexCoord2f(0, 1);
			
			// Left side of wall
			GL11.glVertex3f(startX, tileHeightValue, startZ);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX, tileHeightValue, startZ + length);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(startX, tileHeightValue + height, startZ + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(startX, tileHeightValue + height, startZ);
			GL11.glTexCoord2f(0, 1);
			
			// Right side of wall
			GL11.glVertex3f(startX + width, tileHeightValue, startZ);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(startX + width, tileHeightValue, startZ + length);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(startX + width, tileHeightValue + height, startZ + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(startX + width, tileHeightValue + height, startZ);
			GL11.glTexCoord2f(0, 1);
			GL11.glEnd();
		}
		GL11.glEnd();
		GL11.glEndList();

		float[] start = { startX, startZ, 0 };
		float[] end = { endX, endZ, 1 };
		this.mesh = new VectorObject(meshId, start, end, false);
	}

	public void render(Camera camera, Frustum frustum, boolean drawWireFrame) {
		mesh.render(camera, frustum, drawWireFrame);
	}

	public void cleanup() {
		mesh.cleanup();
	}

}