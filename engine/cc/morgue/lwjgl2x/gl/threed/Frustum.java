package cc.morgue.lwjgl2x.gl.threed;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * Frustum class is used to determine which 3d objects are visible in the cameras viewing area, to increase performance
 * in games which contain a large amount of objects.
 */
public class Frustum {

	// We create an enum of the sides so we don't have to call each side 0 or 1.
	// This way it makes it more understandable and readable when dealing with frustum sides.
	public static final int RIGHT = 0; // The RIGHT side of the frustum
	public static final int LEFT = 1; // The LEFT side of the frustum
	public static final int BOTTOM = 2; // The BOTTOM side of the frustum
	public static final int TOP = 3; // The TOP of the frustum
	public static final int BACK = 4; // The BACK side of the frustum
	public static final int FRONT = 5; // The FRONT side of the frustum

	// Like above, instead of saying a number for the ABC and D of the plane, we
	// want to be more descriptive.
	public static final int A = 0; // The X value of the plane's normal
	public static final int B = 1; // The Y value of the plane's normal
	public static final int C = 2; // The Z value of the plane's normal
	public static final int D = 3; // The distance the plane is from the origin

	/** This holds the A B C and D values for each side of our frustum. **/
	float[][] frustum = new float[6][4];

	/** FloatBuffer to get ModelView matrix. **/
	FloatBuffer modelBuffer;

	/** FloatBuffer to get Projection matrix. **/
	FloatBuffer projectionBuffer;

	public Frustum() {
		modelBuffer = BufferUtils.createFloatBuffer(16);
		projectionBuffer = BufferUtils.createFloatBuffer(16);
	}

	//  This normalizes a plane (A side) from a given frustum.
	private void normalizePlane(float[][] frustum, int side) {
		// Here we calculate the magnitude of the normal to the plane (point A B C)
		// Remember that (A, B, C) is that same thing as the normal's (X, Y, Z).
		// To calculate magnitude you use the equation:  magnitude = sqrt( x^2 + y^2 + z^2)
		float magnitude = (float) Math.sqrt(frustum[side][A] * frustum[side][A] + frustum[side][B] * frustum[side][B] + frustum[side][C] * frustum[side][C]);
		// Then we divide the plane's values by it's magnitude.
		// This makes it easier to work with.
		frustum[side][A] /= magnitude;
		frustum[side][B] /= magnitude;
		frustum[side][C] /= magnitude;
		frustum[side][D] /= magnitude;
	}

	// This extracts our frustum from the projection and modelview matrix.
	public void calculateFrustum() {
		float[] projectionMatrix = new float[16]; // This will hold our projection matrix
		float[] modelMatrix = new float[16]; // This will hold our modelview matrix
		float[] clipMatrix = new float[16]; // This will hold the clipping planes

		projectionBuffer.rewind();
		glGetFloat(GL_PROJECTION_MATRIX, projectionBuffer);
		projectionBuffer.rewind();
		projectionBuffer.get(projectionMatrix);

		// By passing in GL_MODELVIEW_MATRIX, we can abstract our model view matrix.
		// This also stores it in an array of [16].
		modelBuffer.rewind();
		glGetFloat(GL_MODELVIEW_MATRIX, modelBuffer);
		modelBuffer.rewind();
		modelBuffer.get(modelMatrix);

		// Now that we have our modelview and projection matrix, if we combine these 2 matrices,
		// it will give us our clipping planes.  To combine 2 matrices, we multiply them.
		clipMatrix[0] = modelMatrix[0] * projectionMatrix[0] + modelMatrix[1] * projectionMatrix[4] + modelMatrix[2] * projectionMatrix[8] + modelMatrix[3] * projectionMatrix[12];
		clipMatrix[1] = modelMatrix[0] * projectionMatrix[1] + modelMatrix[1] * projectionMatrix[5] + modelMatrix[2] * projectionMatrix[9] + modelMatrix[3] * projectionMatrix[13];
		clipMatrix[2] = modelMatrix[0] * projectionMatrix[2] + modelMatrix[1] * projectionMatrix[6] + modelMatrix[2] * projectionMatrix[10] + modelMatrix[3] * projectionMatrix[14];
		clipMatrix[3] = modelMatrix[0] * projectionMatrix[3] + modelMatrix[1] * projectionMatrix[7] + modelMatrix[2] * projectionMatrix[11] + modelMatrix[3] * projectionMatrix[15];
		clipMatrix[4] = modelMatrix[4] * projectionMatrix[0] + modelMatrix[5] * projectionMatrix[4] + modelMatrix[6] * projectionMatrix[8] + modelMatrix[7] * projectionMatrix[12];
		clipMatrix[5] = modelMatrix[4] * projectionMatrix[1] + modelMatrix[5] * projectionMatrix[5] + modelMatrix[6] * projectionMatrix[9] + modelMatrix[7] * projectionMatrix[13];
		clipMatrix[6] = modelMatrix[4] * projectionMatrix[2] + modelMatrix[5] * projectionMatrix[6] + modelMatrix[6] * projectionMatrix[10] + modelMatrix[7] * projectionMatrix[14];
		clipMatrix[7] = modelMatrix[4] * projectionMatrix[3] + modelMatrix[5] * projectionMatrix[7] + modelMatrix[6] * projectionMatrix[11] + modelMatrix[7] * projectionMatrix[15];

		clipMatrix[8] = modelMatrix[8] * projectionMatrix[0] + modelMatrix[9] * projectionMatrix[4] + modelMatrix[10] * projectionMatrix[8] + modelMatrix[11] * projectionMatrix[12];
		clipMatrix[9] = modelMatrix[8] * projectionMatrix[1] + modelMatrix[9] * projectionMatrix[5] + modelMatrix[10] * projectionMatrix[9] + modelMatrix[11] * projectionMatrix[13];
		clipMatrix[10] = modelMatrix[8] * projectionMatrix[2] + modelMatrix[9] * projectionMatrix[6] + modelMatrix[10] * projectionMatrix[10] + modelMatrix[11] * projectionMatrix[14];
		clipMatrix[11] = modelMatrix[8] * projectionMatrix[3] + modelMatrix[9] * projectionMatrix[7] + modelMatrix[10] * projectionMatrix[11] + modelMatrix[11] * projectionMatrix[15];

		clipMatrix[12] = modelMatrix[12] * projectionMatrix[0] + modelMatrix[13] * projectionMatrix[4] + modelMatrix[14] * projectionMatrix[8] + modelMatrix[15] * projectionMatrix[12];
		clipMatrix[13] = modelMatrix[12] * projectionMatrix[1] + modelMatrix[13] * projectionMatrix[5] + modelMatrix[14] * projectionMatrix[9] + modelMatrix[15] * projectionMatrix[13];
		clipMatrix[14] = modelMatrix[12] * projectionMatrix[2] + modelMatrix[13] * projectionMatrix[6] + modelMatrix[14] * projectionMatrix[10] + modelMatrix[15] * projectionMatrix[14];
		clipMatrix[15] = modelMatrix[12] * projectionMatrix[3] + modelMatrix[13] * projectionMatrix[7] + modelMatrix[14] * projectionMatrix[11] + modelMatrix[15] * projectionMatrix[15];

		// This will extract the LEFT side of the frustum
		frustum[LEFT][A] = clipMatrix[3] + clipMatrix[0];
		frustum[LEFT][B] = clipMatrix[7] + clipMatrix[4];
		frustum[LEFT][C] = clipMatrix[11] + clipMatrix[8];
		frustum[LEFT][D] = clipMatrix[15] + clipMatrix[12];
		normalizePlane(frustum, LEFT);

		// This will extract the RIGHT side of the frustum
		frustum[RIGHT][A] = clipMatrix[3] - clipMatrix[0];
		frustum[RIGHT][B] = clipMatrix[7] - clipMatrix[4];
		frustum[RIGHT][C] = clipMatrix[11] - clipMatrix[8];
		frustum[RIGHT][D] = clipMatrix[15] - clipMatrix[12];
		normalizePlane(frustum, RIGHT);

		// This will extract the BOTTOM side of the frustum
		frustum[BOTTOM][A] = clipMatrix[3] + clipMatrix[1];
		frustum[BOTTOM][B] = clipMatrix[7] + clipMatrix[5];
		frustum[BOTTOM][C] = clipMatrix[11] + clipMatrix[9];
		frustum[BOTTOM][D] = clipMatrix[15] + clipMatrix[13];
		normalizePlane(frustum, BOTTOM);

		// This will extract the TOP side of the frustum
		frustum[TOP][A] = clipMatrix[3] - clipMatrix[1];
		frustum[TOP][B] = clipMatrix[7] - clipMatrix[5];
		frustum[TOP][C] = clipMatrix[11] - clipMatrix[9];
		frustum[TOP][D] = clipMatrix[15] - clipMatrix[13];
		normalizePlane(frustum, TOP);

		// This will extract the FRONT side of the frustum
		frustum[FRONT][A] = clipMatrix[3] + clipMatrix[2];
		frustum[FRONT][B] = clipMatrix[7] + clipMatrix[6];
		frustum[FRONT][C] = clipMatrix[11] + clipMatrix[10];
		frustum[FRONT][D] = clipMatrix[15] + clipMatrix[14];
		normalizePlane(frustum, FRONT);

		// This will extract the BACK side of the frustum
		frustum[BACK][A] = clipMatrix[3] - clipMatrix[2];
		frustum[BACK][B] = clipMatrix[7] - clipMatrix[6];
		frustum[BACK][C] = clipMatrix[11] - clipMatrix[10];
		frustum[BACK][D] = clipMatrix[15] - clipMatrix[14];
		normalizePlane(frustum, BACK);
	}

	// This determines if a point is inside of the frustum
	public boolean pointInFrustum(float x, float y, float z) {
		// Go through all the sides of the frustum
		for (int i = 0; i < 6; i++) {
			// Calculate the plane equation and check if the point is behind a
			// side of the frustum
			if (frustum[i][A] * x + frustum[i][B] * y + frustum[i][C] * z + frustum[i][D] <= 0) {
				// The point was behind a side, so it ISN'T in the frustum
				return false;
			}
		}

		// The point was inside of the frustum (In front of ALL the sides of the
		// frustum)
		return true;
	}

	// This determines if a sphere is inside of our frustum by it's center and radius.
	public boolean sphereInFrustum(float x, float y, float z, float radius) {
		// Go through all the sides of the frustum
		for (int i = 0; i < 6; i++) {
			// If the center of the sphere is farther away from the plane than
			// the radius
			if (frustum[i][A] * x + frustum[i][B] * y + frustum[i][C] * z + frustum[i][D] <= -radius) {
				// The distance was greater than the radius so the sphere is
				// outside of the frustum
				return false;
			}
		}

		// The sphere was inside of the frustum!
		return true;
	}

	// This determines if a cube is in or around our frustum by it's center and 1/2 it's length
	public boolean cubeInFrustum(float x, float y, float z, float size) {
		// This test is a bit more work, but not too much more complicated.
		// Basically, what is going on is, that we are given the center of the cube,
		// and half the length.  Think of it like a radius.  Then we checking each point
		// in the cube and seeing if it is inside the frustum.  If a point is found in front
		// of a side, then we skip to the next side.  If we get to a plane that does NOT have
		// a point in front of it, then it will return false.

		// *Note* - This will sometimes say that a cube is inside the frustum when it isn't.
		// This happens when all the corners of the bounding box are not behind any one plane.
		// This is rare and shouldn't effect the overall rendering speed.
		for (int i = 0; i < 6; i++) {
			if (frustum[i][A] * (x - size) + frustum[i][B] * (y - size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x + size) + frustum[i][B] * (y - size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x - size) + frustum[i][B] * (y + size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x + size) + frustum[i][B] * (y + size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x - size) + frustum[i][B] * (y - size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x + size) + frustum[i][B] * (y - size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x - size) + frustum[i][B] * (y + size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
				continue;
			if (frustum[i][A] * (x + size) + frustum[i][B] * (y + size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
				continue;

			return false;
		}
		return true;
	}

}
