package cc.morgue.lwjgl2x;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import cc.morgue.lwjgl2x.audio.TinySound;
import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.font.TrueTypeFont;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.util.MouseUtil;
import cc.morgue.lwjgl2x.util.Utils;

/**
 * This class manages the application state, and handles all of the OpenGL events.
 */
public class Engine {

	/*
	 * Engine variables.
	 */
	private AtomicBoolean running = new AtomicBoolean(false); // true after run() has been executed
	private final Runtime runtime = Runtime.getRuntime();
	private final long MEGABYTE_VALUE = 1024L * 1024L;
	private long lastReset = 0L;
	private long available = 0L;
	private long free = 0L;
	private int cyclesThisSecond = 0;
	private int framesThisSecond = 0;
	private long secondsRunning = 0L;
	private int frameCount = 0;
	private int cycleCount = 0;
	private String memUsage = "";
	private boolean[] keysDown = new boolean[256];

	/*
	 * 
	 */
	private final int originalWidth;
	private final int originalHeight;

	/*
	 * Mouse click flags.
	 */
	private boolean buttonStateL = false; // left mouse click
	private boolean buttonStateR = false; // left mouse click

	/*
	 * Projection matrixes.
	 */
	private FloatBuffer perspectiveProjectionMatrix;
	private FloatBuffer orthographicProjectionMatrix;

	/*
	 * The application states.
	 */
	private List<AbstractState> stateList = new ArrayList<AbstractState>();
	private AbstractState currentState = null;

	/*
	 * Debug font.
	 */
	private TrueTypeFont font;
	
	/*
	 * Frustum culling is null until requested by the front-end application.
	 * It will automagically cull objects which shouldn't be visible, therefor increasing performance in 3D environments.
	 */
	private Frustum frustum;

	public Engine(String title, int width, int height, boolean resizable) {
		debug("EXTENDED_VERBOSE = " + Config.EXTENDED_VERBOSE);
		this.originalWidth = width;
		this.originalHeight = height;
		try {
			if (Config.FULLSCREEN_MODE) {
				Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
				Display.setResizable(false);
			} else {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.setResizable(resizable);
			}
			Display.setTitle(title);
		} catch (LWJGLException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to execute native function: DisplayMode.",
					e);
			System.exit(1);
		}

		// TODO : turn the 'setIcon' feature into a user-accessible method:
		try {
			ByteBuffer[] list = new ByteBuffer[3];
			list[0] = convertToByteBuffer(ImageIO.read(ClassLoader.getSystemResource("media/icon16.png"))); // 16x16
			list[1] = convertToByteBuffer(ImageIO.read(ClassLoader.getSystemResource("media/icon24.png"))); // 24x24
			list[2] = convertToByteBuffer(ImageIO.read(ClassLoader.getSystemResource("media/icon32.png"))); // 32x32
			Display.setIcon(list);
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to load applet icon.", e);
		}

		/*
		 * Create the OpenGL <code>Display</code> instance.
		 */
		Config.SAMPLE_SIZE = Config.USE_ANTIALIASING ? getSupportedSampleSize() : 0;
		switch (Config.SAMPLE_SIZE) {
		case 0:
			try {
				if (Config.USE_ANTIALIASING && Config.EXTENDED_VERBOSE) {
					Logger.getLogger(getClass().getName()).log(Level.INFO, "This machine does not support GL_AA");
				}
				Display.create();
			} catch (LWJGLException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to execute native function: Display.create()", e);
				System.exit(1);
			}
			break;
		default:
			if (Config.EXTENDED_VERBOSE) {
				Logger.getLogger(getClass().getName()).log(Level.INFO, "This machine supports GL_AA x" + Config.SAMPLE_SIZE);
			}
			break;
		}

		/*
		 * Optimize the OpenGL rendering configurations.
		 */
		GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, getHint()); // hint -> points
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, getHint()); // hint -> lines
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, getHint()); // hint -> polygon edges

		GL11.glHint(GL11.GL_LINE_SMOOTH, getHint()); // enable -> all lines smooth
		GL11.glHint(GL11.GL_POLYGON_SMOOTH, getHint()); // enable -> all polygon edges smooth

		GL11.glHint(GL11.GL_POINT_SMOOTH, getHint()); // required to make points larger that 2 pixels appear round
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, getHint()); // hint for the color and texture interpolation
																		// while
		// generating perspective

		// All surfaces in OpenGL must be approximated with the geometric primitives.
		// If this surface is a square, cube, prism etc, where the surface is made of
		// flat surfaces then the surface can be well described by OpenGL primitives.
		// However if the surface is a sphere, car, hand etc where the surface does not
		// have flat surfaces then the surface can only be approximated with OpenGL
		// primitives.
		// OpenGL offers a shading model that can be used to best approximate a surface
		// with flat faces or a smooth surface.
		// The method for this is listed below.
		// GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		/*
		 * Print some extended information..
		 */
		if (Config.EXTENDED_VERBOSE) {
			Logger.getLogger(getClass().getName()).log(Level.INFO,
					"OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION) + "\n" + "GPU Software Vendor: "
							+ GL11.glGetString(GL11.GL_VENDOR) + "\n" + "GPU Adapter Details: "
							+ GL11.glGetString(GL11.GL_RENDERER) + "\n" + "Bit Depth: "
							+ Display.getDesktopDisplayMode().getBitsPerPixel() + "\n" + "Display Mode Frequency: "
							+ Display.getDesktopDisplayMode().getFrequency() + "\n" + "Java Version: "
							+ System.getProperty("java.version"));
		}
		Display.setLocation(Display.getDesktopDisplayMode().getWidth() / 2 - Display.getWidth() / 2,
				Display.getDesktopDisplayMode().getHeight() / 2 - Display.getHeight() / 2);

		this.font = new TrueTypeFont(new Font("Suego UI", Font.BOLD, 14));
		// GraphicsGL.setClearColor(Color.BLACK, 1);
		initGL(Display.getWidth(), Display.getHeight());
	}

	/**
	 * Initialize the LWJGL implementation.
	 * 
	 * @param height
	 * @param width
	 */
	private void initGL(int width, int height) {
		glClearDepth(1.0);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		// // Transparency
		// glEnable(GL_BLEND);
		// glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//
		// glEnable(GL_ALPHA_TEST);
		// glAlphaFunc(GL_GREATER, 0.0f);

		/*
		 * Sets GL_PROJECTION to an perspective projection matrix. The matrix mode will
		 * be returned it its previous value after execution.
		 */
		this.perspectiveProjectionMatrix = BufferUtils.createFloatBuffer(16);
		this.orthographicProjectionMatrix = BufferUtils.createFloatBuffer(16);

		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		float aspectRatio = (float) (width / height);
		if (aspectRatio <= 0) {
			// throw new IllegalArgumentException("aspectRatio " + aspectRatio + " was 0 or was smaller than 0");
		}
		GLU.gluPerspective(Config.FIELD_OF_VIEW, aspectRatio, Config.Z_NEAR, Config.Z_FAR); // 3D
		GL11.glViewport(0, 0, width, height); // 2D
		glPopAttrib();

		glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
	}

	/**
	 * Starts the application.
	 */
	public void run() {
		this.running.set(true);
		long currentTime = 0L;
		long lastUpdate = 0L; // Limit the number of updates per second.
		float delta1 = 0;
		float delta2 = 0;
		boolean throttle = true;
		while (!Display.isCloseRequested()) {
			currentTime = Utils.getCurrentTimeMillis();

			// Update the delta time.
			delta1 = currentTime - delta2;

			// Update the application.
			if (Config.UPDATE_TIME == -1) {
				cyclesThisSecond++;
				update(currentTime, delta1);
			} else {
				if (currentTime - lastUpdate >= Config.UPDATE_TIME) {
					lastUpdate = currentTime;
					 cyclesThisSecond++;
					 update(currentTime, delta1);
				}
			}

			// Render the application.
			boolean useThrottle = (Config.THROTTLE_WHEN_IDLE && !MouseUtil.isMouseInsideOfDisplay());
			if (useThrottle) {
				throttle = !throttle;
			} else {
				throttle = false;
			}
			if (!throttle) {
				framesThisSecond++;
				render(delta1);
				// Update the window. If the window is visible clears the dirty flag and calls
				// swapBuffers() and finally polls the input devices.
				Display.update();
			}

			// An accurate sync method that will attempt to run at a constant frame rate. It
			// should be called once every frame.
			Display.sync(Config.FRAME_RATE);

			// Update the delta time.
			delta2 = Utils.getCurrentTimeMillis();

		}
		shutdown();
	}

	/**
	 * Cleanup and terminate the application.
	 */
	public void shutdown() {
		// be sure to shutdown TinySound when done
		if (TinySound.isInitialized()) {
			TinySound.shutdown();
		}

		if (Config.EXTENDED_VERBOSE) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Exiting each application state.");
		}
		for (AbstractState state : stateList) {
			state.onStateExit();
		}

		if (Config.EXTENDED_VERBOSE) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Disposing each application state.");
		}
		for (AbstractState state : stateList) {
			state.dispose();
		}

		if (Config.EXTENDED_VERBOSE) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Cleaning up application resources.");
		}
		TextureManager.getInstance().cleanup();

		if (Config.EXTENDED_VERBOSE) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Terminating application.");
		}
		Display.destroy();
		System.exit(0); // Fail-proof
	}

	/**
	 * Updates the application.
	 * 
	 * @param current
	 *            Time The current time in milliseconds.
	 * @param delta
	 *            The delta time.
	 */
	private void update(long currentTime, float delta) {
		if (!running.get()) {
			return;
		}

		// Executed every ~1000 milliseconds:
		if (currentTime - lastReset > 999) {
			lastReset = currentTime;
			secondsRunning++;

			frameCount = framesThisSecond;
			framesThisSecond = 0;
			cycleCount = cyclesThisSecond;
			cyclesThisSecond = 0;
			available = runtime.totalMemory();
			free = runtime.freeMemory();
			memUsage = (available - free) / (MEGABYTE_VALUE) + "MB";

			if (currentState != null) {
				currentState.oneSecondTimeLapse();
			}

		}

		// Handle keyboard input:
		while (Keyboard.next()) {
			for (int i = 0; i < keysDown.length; i++) {
				keysDown[i] = Keyboard.isKeyDown(i);
			}

			if (currentState != null) {
				currentState.pollKeyboardInput(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState());
			}
		}

		// Emergency shutdown:
		if (Config.EMERGENCY_SHUTDOWN_HOTKEY) {
			if (keysDown[Keyboard.KEY_LSHIFT] && keysDown[Keyboard.KEY_RSHIFT]) {
				shutdown();
				return;
			}
		}
		
		// Handle the keys being held down:
		if (currentState != null) {
			currentState.pollKeyboardInput(keysDown);
		}

		// Handle mouse input:
		if (MouseUtil.isMouseInsideOfDisplay()) {
			while (Mouse.next()) {
				if (currentState != null) {
					currentState.pollMouseInput();
				}
			}
		}

		// Let the current state update:
		if (currentState != null) {
			currentState.updateLogic(currentTime, delta);
		}

	}

	/**
	 * Renders the application.
	 * 
	 * @param delta
	 *            The delta time.
	 */
	private void render(float delta) {
		int width = Display.getWidth();
		int height = Display.getHeight();
		if (Config.FULLSCREEN_MODE) {
			width = originalWidth;
			height = originalHeight;
		}

		/*
		 * Resize the viewport and tell the application to execute the local resize
		 * events.
		 */
		boolean wasResized = Display.wasResized();
		if (!Config.FULLSCREEN_MODE && wasResized) {
			width = Display.getWidth();
			height = Display.getHeight();
			initGL(width, height);
		}

		/*
		 * Make sure 2D textures are enabled before each frame is rendered.
		 * This is required...
		 */
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		/*
		 * Clear the frame and reset the projection matrix.
		 */
		GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		/*
		 * Update the mouse click flags.
		 */
		final boolean clickedL = (Mouse.isButtonDown(0) && !buttonStateL);
		buttonStateL = Mouse.isButtonDown(0);
		final boolean clickedR = (Mouse.isButtonDown(1) && !buttonStateR);
		buttonStateR = Mouse.isButtonDown(1);

		/*
		 * Set gl colors to white for the new frame.
		 */
		if (Config.FORCE_WHITE_PRE_TEXTURE) {
			GraphicsGL.setColor2D(Color.WHITE);
			GraphicsGL.setColor3D(Color.WHITE, 1);
		}
		
		/*
		 * Tell the current state to draw the 3D content.
		 */
		if (currentState != null) {

			// Update the frustum culling.
			if (frustum != null) {
				frustum.calculateFrustum();
			}

			// Reset color.
			GraphicsGL.setColor3D(Color.WHITE, 1f);
			
			// Let the current state draw it's own 3D content.
			currentState.render3D(wasResized, clickedL, clickedR);
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrix(orthographicProjectionMatrix);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glLoadIdentity();
		GL11.glDisable(GL_LIGHTING); // XXX delete, and see if 2D gui flicker stops in 3D apps
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		/*
		 * Tell the current state to draw the 2D content.
		 */
		if (currentState != null) {
			currentState.render2D(wasResized, width, height, clickedL, clickedR);
		}

		/*
		 * Render the debug hud on top of everything else.
		 */
		if (Config.RENDER_DEBUG_HUD) {
			GraphicsGL.setColor2D(Config.DEBUG_COLOR);
			font.drawString(15, 10, "TwoD v" + Config.REVISION);
//			font.drawString(15, 25, "Frame Rate: " + frameCount);
//			font.drawString(15, 40, "Cycle Rate: " + cycleCount);
//			font.drawString(15, 55, "Mem Usage: " + memUsage);
			font.drawString(15, 70, "Seconds Running: " + secondsRunning);
			font.drawString(15, 85, "Mouse: " + MouseUtil.getX() + "," + MouseUtil.getY());
			if (Config.FORCE_WHITE_PRE_TEXTURE) {
				font.drawString(15, 100, "Force White 2D: " + Config.FORCE_WHITE_PRE_TEXTURE);
			}
		}
		if (Config.RENDER_FPS_MEM_USAGE_HUD) {
			GraphicsGL.setColor2D(Config.DEBUG_COLOR);
			font.drawString(15, 25, "Frame Rate: " + frameCount);
			font.drawString(15, 40, "Cycle Rate: " + cycleCount);
			font.drawString(15, 55, "Mem Usage: " + memUsage);
		}

		glDisable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_NONE);
		GL11.glEnable(GL_LIGHTING); // XXX delete, and see if 2D gui flicker stops in 3D apps
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrix(perspectiveProjectionMatrix);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	/**
	 * Adds a new application state to the list of usable states.
	 * 
	 * @param state
	 *            The <code>com.twod.AbstractState</code> to register.
	 */
	public void registerApplicationState(AbstractState state) {
		this.stateList.add(state);
	}

	/**
	 * Sets the application state.
	 * 
	 * @param uid
	 *            The uid of the requested application state.
	 */
	public void setApplicationState(int uid) {
		if (stateList.isEmpty()) {
			throw new NullPointerException("No application states have been registered!");
		}
		if (currentState != null) {
			currentState.onStateExit();
			currentState.setEnabled(false);
			if (TinySound.isInitialized()) {
				TinySound.shutdown();
			}
		}
		currentState = stateList.get(uid);
		TinySound.init();
		currentState.onStateOpen();
		currentState.setEnabled(true);
	}

	private static ByteBuffer convertToByteBuffer(BufferedImage image) {
		byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
		int counter = 0;
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				buffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				buffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		}
		return ByteBuffer.wrap(buffer);
	}

	/**
	 * Loops through an array of display samples, to see which ones are compatible
	 * with the machine.
	 * 
	 * @params: bit depth, alpha bits, depth bits, stencil bits, sample size
	 */
	public static int getSupportedSampleSize() {
		int bpp = Display.getDesktopDisplayMode().getBitsPerPixel();
		int[] samples = { 16, 8, 4, 2 };
		for (int i = 0; i < samples.length; i++) {
			try {
				Display.create(new PixelFormat(bpp, 0, 24, 0, samples[i]));
				return samples[i];
			} catch (LWJGLException ignore) {
			}
		}
		return 0;
	}

	public static int getHint() {
		debug("int getHint() = " + Config.SAMPLE_SIZE);
		if (Config.USE_ANTIALIASING) {
			return GL11.GL_NICEST;
		}
		switch (Config.SAMPLE_SIZE) {
		case 0:
			// return GL11.GL_NONE;
			return GL11.GL_FASTEST;
		case 2:
			return GL11.GL_DONT_CARE;
		default:
			return GL11.GL_NICEST;
		}
	}

	public Frustum getFrustum() {
		if (frustum == null) {
			this.frustum = new Frustum();
		}
		return frustum;
	}

	/**
	 * For debugging the engine during development.
	 */
	private static void debug(String string) {
		boolean enabled = false;
		if (!enabled) {
			return;
		}
		System.out.println("[DEBUG]" + string);
	}
	
	public static final String COPYRIGHT = "\u00a9";

}
