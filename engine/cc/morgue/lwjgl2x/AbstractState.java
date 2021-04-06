package cc.morgue.lwjgl2x;

public abstract class AbstractState {
	
	protected final Engine engine;
	private boolean enabled = false;
	
	public AbstractState(final Engine engine) {
		this.engine = engine;
	}

	/**
	 * Executed as many times per second as possible.
	 * 
	 * @param resized true if the <code>org.lwjgl.opengl.Display</code> has been resized.
	 * @param width The <code>org.lwjgl.opengl.Display</code> width.
	 * @param height The <code>org.lwjgl.opengl.Display</code> height.
	 * @param clickedR true if the left mouse button has been clicked.
	 * @param clickedL  true if the right mouse button has been clicked.
	 */
	public abstract void render2D(boolean resized, int width, int height, boolean clickedL, boolean clickedR);
	
	/**
	 * Executed as many times per second as possible.
	 * 
	 * @param resized true if the <code>org.lwjgl.opengl.Display</code> has been resized.
	 * @param clickedR true if the left mouse button has been clicked.
	 * @param clickedL  true if the right mouse button has been clicked.
	 */
	public abstract void render3D(boolean resized, boolean clickedL, boolean clickedR);
	
	/**
	 * Executed ~60 times per second.
	 * 
	 * @param currentTime The current time in milliseconds.
	 * @param delta The time lapse between frames.
	 */
	public abstract void updateLogic(long currentTime, float delta);

	/**
	 * Executed once per second.
	 */
	public abstract void oneSecondTimeLapse();
	
	/**
	 * Poll keyboard input using an array of key state flags.
	 * 
	 * @param keysDown An array of flags which represent if a key is being held down or not.
	 */
	public abstract void pollKeyboardInput(boolean[] keysDown);
	
	/**
	 * Poll keyboard input on a per-key basis.
	 * 
	 * @param keyCode The key code.
	 * @param keyChar The key character.
	 * @param keyReleased true if the key has been released.
	 */
	public abstract void pollKeyboardInput(int keyCode, char keyChar, boolean keyReleased);
	
	/**
	 * Used to execute polled mouse logic.
	 */
	public abstract void pollMouseInput();
	
	/**
	 * Executed when the state is opened.
	 */
	public abstract void onStateOpen();
	
	/**
	 * Executed when the state is exited.
	 */
	public abstract void onStateExit();
	
	/**
	 * Called by the {@link Engine} when the state is opened.
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * @return true if the state is enabled, otherwise false.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Executed when the engine is being terminated.
	 */
	public abstract void dispose();
}
