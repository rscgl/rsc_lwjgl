package cc.morgue.lwjgl2x;

import java.awt.Color;

/**
 * Software configurations.
 */
public class Config {
	
	static {
		System.out.println("THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.");
		System.out.println("LWJGL 2.x game engine / boiler code wrapper was written by \"Morgue\" (https://morgue.cc)");
		//System.out.println("This isn't the greatest 2D/3D engine, but it's good enough to use with \"for fun\" projects, or lightweight 3D applications.");
		System.out.println();
	}

	/**
	 * The Two-D software revision.
	 * 1.0) Found old "engine" source on HDD.
	 * 1.1) Optimized & cleaned the old source.
	 * 1.2) Added better 2D rendering/functions to mask raw LWJGL api and make development easier for beginners.
	 * 1.3) Added basic 3D rendering/functions to mask raw LWJGL api and make development easier for beginners.
	 * 1.5) Added .wav audio support (thanks TinySound).
	 * 1.6) Added extended 3D support. Tuned GL settings.
	 * 1.7) Added 2D collision detection. Updated Engine/Config classes.
	 * 1.71) Small miscellaneous tweaks.
	 * 1.8) Added Directions class. Updated utility class. Removed path finder (and other bloat).
	 * 1.9) Repackaged engine source. Added wavefront (.obj) model support.
	 * 2.0) Patched a 2D font rendering bug that has existed since day 1.
	 */
	public static final double REVISION = 2.0;

	/**
	 * Full screen display mode. Must be set before constructing the
	 * <code>com.twod.Engine</code> class. Default is false
	 */
	public static boolean FULLSCREEN_MODE = false;

	/**
	 * Extended software details and debug output. Default is true
	 */
	public static boolean EXTENDED_VERBOSE = true;

	/**
	 * Whether the debug hud should be rendered or not. Default is false
	 */
	public static boolean RENDER_DEBUG_HUD = false;
	
	/**
	 * Whether or not the FPS, UPS, and mem usage should be rendered or not. Default is true
	 */
	public static boolean RENDER_FPS_MEM_USAGE_HUD = true;

	/**
	 * Anti-aliasing for 2D and 3D rendering. Must be set before constructing the
	 * <code>Engine</code> class. Default is true
	 */
	public static boolean USE_ANTIALIASING = true;

	/**
	 * The far clipping plain. Default is 1000
	 * 
	 * @see https://en.wikipedia.org/wiki/Z-buffering
	 */
	public static float Z_FAR = 1000;

	/**
	 * The near clipping plain. Default is 0.1
	 * 
	 * @see https://en.wikipedia.org/wiki/Z-buffering
	 */
	public static float Z_NEAR = 0.1f;

	/**
	 * The field of view of the camera looking at the game world. Default is 60
	 */
	public static int FIELD_OF_VIEW = 60;

	/**
	 * Limits the frame rate by skipping every other frame (if enabled), when the
	 * mouse is not inside of the display. Default is false.
	 */
	public static boolean THROTTLE_WHEN_IDLE = true;

	/**
	 * Sets the frame rate. Default is 120
	 */
	public static int FRAME_RATE = 120;

	/**
	 * The delay (in milliseconds) between update cycles. This is separate from
	 * frame rate limit, but will be effected if the frame rate is lower than the
	 * update time. Default is 15. If you want to synchronize the update timer with
	 * the frame rate, set this value to -1.
	 */
	public static long UPDATE_TIME = 15;

	/**
	 * An emergency shutdown hotkey that will terminate the application. Hold down
	 * the left and right shift keys at the same time to execute an emergency
	 * shutdown. Default is true
	 */
	public static boolean EMERGENCY_SHUTDOWN_HOTKEY = true;

	/**
	 * The debug hud text color.
	 */
	public static Color DEBUG_COLOR = new Color(0xf0ffff);
	
	/**
	 * The <code>org.lwjgl.Display</code> sample size. Used internally.
	 */
	protected static int SAMPLE_SIZE = 0;

	/**
	 * The texture manager will automatically set the glColor values to WHITE
	 * in order to prevent color conflict.
	 * You can enable or disable the setting if it isn't suitable for your application.
	 * Default is false.
	 */
	public static boolean FORCE_WHITE_PRE_TEXTURE = false;

}
