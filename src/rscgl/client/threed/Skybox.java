package rscgl.client.threed;

import java.awt.Color;
import java.io.File;

import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.texture.TextureManager.FilterType;
import cc.morgue.lwjgl2x.gl.threed.Fog;
import rscgl.Main;
import rscgl.client.game.Sector;

/**
 * A generic skybox class originally based on this tutorial: https://sidvind.com/wiki/Skybox_tutorial
 */
public class Skybox {

	private final int[] mesh = new int[6];
	private final float skyboxScale = 0.50f;
	private final float skyboxHeightOffset = 0.25f;

	/**
	 * Builds a skybox using 6 quads, and 6 textures in this order: zpos, xneg, zneg, xpos, yneg, ypos
	 * @note: zpos = back
	 * @note: xneg = right
	 * @note: zneg = left
	 * @note: xpos = front
	 * @note: yneg = bottom
	 * @note: ypos = top
	 */
	public Skybox() {
		Color skybox_top = new Color(0x2200ff);
		Color skybox_btm = new Color(0x9d8cfe);
		
		FilterType filter = FilterType.MIPMAP;
		TextureManager.getInstance().addTexture("skybox", new File(Main.ASSET_DIRECTORY + "skybox/skybox.png"), filter);
		TextureManager.getInstance().createTexture("skybox_top", skybox_top, 1, 1, 0.5f);
		TextureManager.getInstance().createTexture("skybox_btm", skybox_btm, 1, 1, 0.5f);
		
		int[] textures = new int[6];
		textures[0] = TextureManager.getInstance().getTextureId("skybox");
		textures[1] = TextureManager.getInstance().getTextureId("skybox");
		textures[2] = TextureManager.getInstance().getTextureId("skybox");
		textures[3] = TextureManager.getInstance().getTextureId("skybox");
		textures[4] = TextureManager.getInstance().getTextureId("skybox_btm");
		textures[5] = TextureManager.getInstance().getTextureId("skybox_top");
		buildSkyboxObject(textures);
		
		//Fog fog = new Fog();
		//float fogEnd = 48;
		//float fogStart = fogEnd - (fogEnd / 2);
		//fog.configure(skybox_btm.getRed(), skybox_btm.getGreen(), skybox_btm.getBlue(), 0.1f, fogStart, fogEnd);
	}

	private void buildSkyboxObject(int[] textures) {
		for (int i = 0; i < 6; i++) {
			this.mesh[i] = GL11.glGenLists(1);
			GL11.glNewList(mesh[i], GL11.GL_COMPILE);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i]);

			// Enable/Disable features while building the skybox objects
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GraphicsGL.setColor3D(Color.WHITE, 1f);

			// Build the physical model
			GL11.glBegin(GL11.GL_QUADS);
			{
				switch (i) {
				case 0: // Front quad (zpos)
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex3f(skyboxScale, -skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex3f(-skyboxScale, -skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex3f(-skyboxScale, skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex3f(skyboxScale, skyboxScale, -skyboxScale);
					break;
				case 1: // 
					// Render the left quad (xneg)
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex3f(skyboxScale, -skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex3f(skyboxScale, -skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex3f(skyboxScale, skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex3f(skyboxScale, skyboxScale, skyboxScale);
					break;
				case 2: // Back quad (zneg)
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex3f(-skyboxScale, -skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex3f(skyboxScale, -skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex3f(skyboxScale, skyboxScale, skyboxScale);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex3f(-skyboxScale, skyboxScale, skyboxScale);
					break;
				case 3: // Right quad (xpos)
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex3f(-skyboxScale, -skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex3f(-skyboxScale, -skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex3f(-skyboxScale, skyboxScale, skyboxScale);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex3f(-skyboxScale, skyboxScale, -skyboxScale);
					break;
				case 4: // Bottom quad (yneg)
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex3f(-skyboxScale, -skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex3f(-skyboxScale, -skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex3f(skyboxScale, -skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex3f(skyboxScale, -skyboxScale, -skyboxScale);
					break;
				case 5: // Top quad (ypos)
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex3f(-skyboxScale, skyboxScale, -skyboxScale);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex3f(-skyboxScale, skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex3f(skyboxScale, skyboxScale, skyboxScale);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex3f(skyboxScale, skyboxScale, -skyboxScale);
					break;
				}
			}
			GL11.glEnd();
			GL11.glDisable(GL11.GL_CULL_FACE); // Disable the culling property so that other objects aren't effected
			GL11.glEndList();
		}
	}

	/*
	public void render(float pitch, float yaw, float roll) {
		GraphicsGL.setColor3D(Color.WHITE, 1f);				// Reset color

		GL11.glPushMatrix();								// Store the current matrix
		GL11.glLoadIdentity();								// Reset and transform the matrix.

		GL11.glRotated(pitch, 1, 0, 0);
		GL11.glRotated(yaw, 0, 1, 0);
		GL11.glRotated(roll, 0, 0, 1);

		GL11.glTranslatef(0, skyboxHeightOffset, 0);		// Can be used to apply height offset

		// Draw the 6 sides of the skybox
		for (int i = 0; i < mesh.length; i++) {
			GL11.glCallList(mesh[i]);
		}

		// Restore enable bits and matrix
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	*/

	public void render() {
		GraphicsGL.setColor3D(Color.WHITE, 1f);				// Reset color

		GL11.glPushMatrix();								// Store the current matrix
		GL11.glLoadIdentity();								// Reset and transform the matrix.

		// Draw the 6 sides of the skybox
		for (int i = 0; i < mesh.length; i++) {
			GL11.glCallList(mesh[i]);
		}

		// Restore enable bits and matrix
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}