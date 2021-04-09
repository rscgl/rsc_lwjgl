package cc.morgue.lwjgl2x.gl.threed;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.Config;
import cc.morgue.lwjgl2x.gl.GraphicsGL;
import rscgl.client.Camera;

/**
 * A primitive 3D object ("mesh") with generic collision detection (AABB).
 */
public class VectorObject {

	private final int meshId;						// Used to draw the mesh
	private final float[] start = new float[3];		// Start of mesh
	private final float[] end = new float[3];		// End of mesh
	private final float width;						// Logic: width = end x - start x
	private final float length;						// Logic: length = end z - start z
	private final float height;						// Logic: height = end y - start y
	private final boolean alpha;
	private boolean visible = true;					// Whether or not the mesh can be rendered

	public VectorObject(int meshId, float[] start, float[] end, boolean alpha) {
		setCollisionSize(start, end);
		this.width = end[0] - start[0]; // end x - start x
		this.length = end[1] - start[1]; // end z - start z
		this.height = end[2] - start[2]; // end height - start height
		this.meshId = meshId;
		this.alpha = alpha;
	}

	public VectorObject(float[] start, float[] end, int textureId, boolean alpha) {
		this(buildMesh(textureId, start, end, alpha), start, end, alpha);
	}

	public VectorObject(float[] start, float[] end, int textureId) {
		this(buildMesh(textureId, start, end, false), start, end, false);
	}
	
	private void setCollisionSize(float[] start, float[] end) {
		for (int i = 0; i < 3; i++) {
			this.start[i] = start[i];
			this.end[i] = end[i];
		}
	}

	/**
	 * Builds a primitive 3D mesh using vector math and a single texture.
	 */
	private static int buildMesh(int textureId, float[] start, float[] end, boolean alpha) {
		float width = end[0] - start[0]; // end x - start x
		float length = end[1] - start[1]; // end z - start z
		float height = end[2] - start[2]; // end height - start height
		
		final int meshId = GL11.glGenLists(1);
		GL11.glNewList(meshId, GL11.GL_COMPILE);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		GraphicsGL.enableCulling(false, false);

		if (alpha) {
			//GraphicsGL.enableAlpha();
		}
		GL11.glBegin(GL11.GL_QUADS);
		{
			if (alpha) {
				//GraphicsGL.enableAlpha();
			}
			// Top
			GL11.glVertex3f(start[0], start[2], start[1]);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0] + width, start[2], start[1]);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(start[0] + width, start[2], start[1] + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(start[0], start[2], start[1] + length);
			GL11.glTexCoord2f(0, 1);
			
			// Bottom
			GL11.glVertex3f(start[0], start[2] + height, start[1]);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0] + width, start[2] + height, start[1]);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(start[0] + width, start[2] + height, start[1] + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(start[0], start[2] + height, start[1] + length);
			GL11.glTexCoord2f(0, 1);

			// Front
			GL11.glVertex3f(start[0], start[2], start[1]);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0] + width, start[2], start[1]);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(start[0] + width, start[2] + height, start[1]);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(start[0], start[2] + height, start[1]);
			GL11.glTexCoord2f(0, 1);

			// Back
			GL11.glVertex3f(start[0], start[2], start[1] + length);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0] + width, start[2], start[1] + length);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(start[0] + width, start[2] + height, start[1] + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(start[0], start[2] + height, start[1] + length);
			GL11.glTexCoord2f(0, 1);
			
			// Left side
			GL11.glVertex3f(start[0], start[2], start[1]);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0], start[2], start[1] + length);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(start[0], start[2] + height, start[1] + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(start[0], start[2] + height, start[1]);
			GL11.glTexCoord2f(0, 1);
			
			// Right side
			GL11.glVertex3f(start[0] + width, start[2], start[1]);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0] + width, start[2], start[1] + length);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(start[0] + width, start[2] + height, start[1] + length);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(start[0] + width, start[2] + height, start[1]);
			GL11.glTexCoord2f(0, 1);
		}
		if (alpha) {
			//GraphicsGL.disableAlpha();
		}
		GL11.glEnd();
		if (alpha) {
			//GraphicsGL.disableAlpha();
		}
		
		GL11.glDisable(GL11.GL_CULL_FACE); // Disable culling
		GL11.glEndList();
		return meshId;
	}

	/**
	 * Update the visibility of the object.
	 * Draws the objects mesh in 3d space.
	 */
	public void render(Camera camera, Frustum frustum, boolean drawWireFrame) {
		this.visible = true;//frustum.cubeInFrustum(getX(), getHeight(), getZ(), 1);
		//this.visible = frustum.cubeInFrustum(camera.getAbsoluteX(), camera.getHeight(), camera.getAbsoluteZ(), 1);
		if (!visible) {
			return;
		}
		if (Config.FORCE_WHITE_PRE_TEXTURE) {
			GraphicsGL.setColor3D(Color.WHITE, 1f);
		}
		if (drawWireFrame) {
			GL11.glLineWidth(1.0f); // width of the outline
			GL11.glPolygonOffset(1.0f, 1.0f); 
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);

			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glColor3f(0, 0, 0); // black

			if (alpha) {
				//GraphicsGL.enableAlpha();
			}
			GL11.glCallList(meshId);
			if (alpha) {
				//GraphicsGL.disableAlpha();
			}
			
			GL11.glColor3f(1, 1, 1); // white
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			return;
		}

		if (alpha) {
			//GraphicsGL.enableAlpha();
		}
		GL11.glCallList(meshId);
		if (alpha) {
			//GraphicsGL.disableAlpha();
		}
	}

	/**
	 * Returns the absolute X coordinate of the mesh.
	 */
	public float getX() {
		return start[0];// + (width / 2);
	}

	/**
	 * Returns the absolute Z coordinate of the mesh.
	 */
	public float getZ() {
		return start[1];// + (length / 2);
	}
	
	/**
	 * Returns the absolute centered X coordinate of the mesh.
	 * @note: return = start x + ((end x - start x) / 2)
	 */
	public float getCenteredX() {
		return start[0] + (width / 2);
	}

	/**
	 * Returns the absolute centered Z coordinate of the mesh.
	 * @note: return = start z + ((end z - start z) / 2)
	 */
	public float getCenteredZ() {
		return start[1] + (length / 2);
	}
	
	/**
	 * 0: x
	 * 1: z
	 * 2: height
	 */
	public float[] start() {
		return start;
	}

	/**
	 * 0: x
	 * 1: z
	 * 2: height
	 */
	public float[] end() {
		return start;
	}

	/**
	 * @return The width of the object from start to finish.
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @return The length of the object from start to finish.
	 */
	public float getLength() {
		return length;
	}

	/**
	 * @return The height of the object from start to finish.
	 */
	public float getHeight() {
		return height;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public int getMeshId() {
		return meshId;
	}

	public void cleanup() {
		GL11.glDeleteLists(meshId, 1);
	}

}