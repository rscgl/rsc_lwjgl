package cc.morgue.lwjgl2x.gl.threed;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.Config;
import cc.morgue.lwjgl2x.Engine;

/**
 * A simple LWJGL fog wrapper with anti-aliasing support.
 */
public class Fog {

	private FloatBuffer fogBuffer;
	
	public Fog() {
		this.fogBuffer = BufferUtils.createFloatBuffer(4);
	}

	/**
	 * Creates an OpenGL fog effect using the given color parameters.
	 * Position is based on <code>Config.Z_FAR</code>.
	 * For more control, use the other configuration method.
	 * 
	 * @param r The fog's red color value.
	 * @param g The fog's green color value.
	 * @param b The fog's blue color value.
	 * @param alpha 
	 */
	public void configure(float r, float g, float b, float alpha) {
		float start = Config.Z_FAR - (Config.Z_FAR / 7.5f);
		float end = Config.Z_FAR - (Config.Z_FAR / 10);
		this.configure(r, g, b, alpha, start, end);
	}

	/**
	 * Creates an OpenGL fog effect using the given color parameters.
	 * Position is based on the given parameters.
	 * 
	 * @param r The fog's red color value.
	 * @param g The fog's green color value.
	 * @param b The fog's blue color value.
	 * @param start The fog's near plain.
	 * @param end The fog's far plain.
	 */
	public void configure(float r, float g, float b, float alpha, float start, float end) {
		// Reset the fog buffer.
		fogBuffer.clear();

		// Enable linear fogging (per vertex).
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
		
		// Fog can be calculated per pixel (GL_NICEST), or per vertex (GL_FASTEST)
		GL11.glHint(GL11.GL_FOG_HINT, Engine.getHint());
		
		// Adjust the fog positioning.
		GL11.glFogf(GL11.GL_FOG_START, start);
		GL11.glFogf(GL11.GL_FOG_END, end);

		// Configure the OpenGL fog buffer.
		fogBuffer.put(r).put(g).put(b).put(alpha).flip();
		GL11.glFog(GL11.GL_FOG_COLOR, fogBuffer);
		
//		System.out.println("true r = " + r);
//		System.out.println("true g = " + g);
//		System.out.println("true b = " + b);
//		System.out.println("true a = " + alpha);
//		System.out.println("buff r = " + fogBuffer.get(0));
//		System.out.println("buff g = " + fogBuffer.get(1));
//		System.out.println("buff b = " + fogBuffer.get(2));
//		System.out.println("buff a = " + fogBuffer.get(3));
	}
	
	/**
	 * @return The red color value from the fog buffer.
	 */
	public float getRed() {
		return fogBuffer.get(0);
	}

	/**
	 * @return The green color value from the fog buffer.
	 */
	public float getGreen() {
		return fogBuffer.get(1);
	}

	/**
	 * @return The blue color value from the fog buffer.
	 */
	public float getBlue() {
		return fogBuffer.get(2);
	}

	/**
	 * @return The alpha value from the fog buffer.
	 */
	public float getAlpha() {
		return fogBuffer.get(3);
	}
	
}
