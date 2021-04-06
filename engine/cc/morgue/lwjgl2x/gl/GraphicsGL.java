package cc.morgue.lwjgl2x.gl;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import cc.morgue.lwjgl2x.gl.font.TrueTypeFont;

public class GraphicsGL {

	private static TrueTypeFont[] fontCache = new TrueTypeFont[8];

	public GraphicsGL(int maximumFontCount) {
		fontCache = new TrueTypeFont[maximumFontCount];
	}

	// Configure some lwjgl options (ONCE, at startup.. the whole source code is built around retaining these configs)
	public static void initGL() {
		// All surfaces in OpenGL must be approximated with the geometric primitives. If this surface is a square, cube, prism etc, where the surface is made of flat surfaces then the surface can be well described by OpenGL primitves. However if the surface is a sphere, car, hand etc where the surface does not have flat surfaces then the surface can only be approximated with OpenGL primitives. OpenGL offers a shading model that can be used to best approximate a surface with flat faces or a smooth surface. The method for this is listed below.
		GL11.glShadeModel(GL11.GL_SMOOTH); // GL_SMOOTH or GL_FLAT
		
		 // Z-BUFFERING
		GL11.glDepthFunc(GL11.GL_LEQUAL); // GL_LESS or GL_LESS
	}

	public static TrueTypeFont addFont(String name, int weight, int size, boolean useAA) {
		for (int index = 0; index < fontCache.length; index++) {
			if (fontCache[index] == null) {
				for (int slot = 0; slot < 3; slot++) {
					fontCache[index + slot] = new TrueTypeFont(new Font(name, weight, size + (slot * 2)));
				}
				return fontCache[index + 1];
			}
		}
		Logger.getLogger(GraphicsGL.class.getName()).log(Level.WARNING, "Font limit reached! Maximum amount is " + fontCache.length);
		return null;
	}

	/**
	 * Clears color buffer | depth buffer.
	 */
	public static void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear the color/depth buffers
		GL11.glLoadIdentity(); // Reset all translations
	}

	/**
	 * An alternative version of GL11.glClearColor(float r, float g, float b, float alpha);
	 */
	public static void setClearColor(Color color, float alpha) {
		GL11.glClearColor((float) (color.getRed() / 255f), (float) (color.getGreen() / 255f), (float) (color.getBlue() / 255f), alpha);
	}

	/**
	 * An alternative version of GL11.glColor3f(float r, float g, float b);
	 */
	public static void setColor2D(Color color) {
		GL11.glColor3f((float) (color.getRed() / 255f), (float) (color.getGreen() / 255f), (float) (color.getBlue() / 255f));
	}

	/**
	 * An alternative version of GL11.glColor4f(float r, float g, float b, float alpha);
	 */
	public static void setColor3D(Color color, float alpha) {
		GL11.glColor4f((float) (color.getRed() / 255f), (float) (color.getGreen() / 255f), (float) (color.getBlue() / 255f), alpha);
	}

	/**
	 * Configures OpenGL for 2D rendering.
	 * @note: Must be called BEFORE any 2D drawing code.
	 */
	public static void prepareFor2D() {
		resetProjectionMatrics();

		// Update 2D viewport
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		disableDepthTest(); // Disable depth testing before rendering 2D components.
		enableAlpha();
		
		//setColor2D(Color.WHITE); // Reset color/transparency
	}

	/**
	 * Configures OpenGL for 3D rendering.
	 * @note: Must be called BEFORE any 3D drawing code.
	 */
	public static void prepareFor3D(float fov, float zfar, float znear) {
		resetProjectionMatrics();
		
		// Update 3D viewport
		GLU.gluPerspective(fov, Display.getWidth() / Display.getHeight(), znear, zfar);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		enableDepthTest(); // Enable depth testing before rendering 3D components.
		disableAlpha(); // Disable blending.

		//setColor3D(Color.WHITE, 1); // Reset color/transparency
	}

	public static void resetProjectionMatrics() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
	}
	
	public static void enableDepthTest() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthTest() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableAlpha() {
		//GL11.glEnable(GL11.GL_ALPHA);
		GL11.glEnable(GL11.GL_BLEND); // Enable blending. Without this, transparency may not render as expected.
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void disableAlpha() {
		//GL11.glDisable(GL11.GL_ALPHA);
		GL11.glDisable(GL11.GL_BLEND); // Enable blending. Without this, transparency may not render as expected.
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_NONE);
	}

	/**
	 * Used for culling 3D objects and optimizing drawing performance.
	 * @param front true to cull the front face
	 * @param back true to cull the back face
	 */
	public static void enableCulling(boolean front, boolean back) {
		if (front || back) {
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		if (front && back) {
			GL11.glCullFace(GL11.GL_FRONT_AND_BACK);
			return;
		}
		if (front) {
			GL11.glCullFace(GL11.GL_FRONT);
			return;
		}
		if (back) {
			GL11.glCullFace(GL11.GL_BACK);
			return;
		}
	}

	/**
	 * Disables any previous culling configuration.
	 */
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	/**
	 * Converts a BufferedImage into a ByteBuffer of pixel data.
	 * @param image The image to convert.
	 * @return A ByteBuffer that contains the pixel data of the supplied image.
	 */
	public static ByteBuffer convertToByteBuffer(BufferedImage image) {
		byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
		int counter = 0;
		for (int i = 0; i < image.getHeight(); i++)
			for (int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				buffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				buffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		return ByteBuffer.wrap(buffer);
	}

	// TODO make sure objects are disposed
	public static void cleanup() {
		for (int i = 0; i < fontCache.length; i++) {
			if (fontCache[i] != null) {
				//fontCache[i].cleanup();
				fontCache[i] = null;
			}
		}
	}

}