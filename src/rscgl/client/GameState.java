package rscgl.client;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import cc.morgue.lwjgl2x.AbstractState;
import cc.morgue.lwjgl2x.Config;
import cc.morgue.lwjgl2x.Engine;
import cc.morgue.lwjgl2x.audio.Sound;
import cc.morgue.lwjgl2x.audio.TinySound;
import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.font.TrueTypeFont;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.Directions;
import cc.morgue.lwjgl2x.gl.util.MouseUtil;
import cc.morgue.lwjgl2x.util.Utils;
import rscgl.Main;
import rscgl.client.game.SceneBuilder;
import rscgl.client.game.Sector;
import rscgl.client.game.world.World;
import rscgl.client.game.world.WorldLoader;
import rscgl.client.threed.MudClient3D;
import rscgl.game.scene.Scene;

public class GameState extends AbstractState {

	// 2D
	private TrueTypeFont debugFont; // debug hud
	private TrueTypeFont consoleFont;
	private boolean readConsoleInput = false;
	private String consoleInput = "";
	private String[] consoleLog = new String[8];
	private boolean asterisk = true;

	// 3D
	private MudClient3D client;
	private Camera camera;

	// Audio
	private String[] audioFile = { "advance", "anvil", "chisel", "click", "closedoor", "coins", "combat1a", "combat1b",
			"combat2a", "combat2b", "combat3a", "combat3b", "cooking", "death", "dropobject", "eat", "filljug", "fish",
			"foundgem", "mechanical", "mine", "mix", "opendoor", "outofammo", "potato", "prayeroff", "prayeron",
			"prospect", "recharge", "retreat", "secretdoor", "shoot", "spellfail", "spellok", "takeobject",
			"underattack", "victory" };
	private Sound[] audioStream = new Sound[audioFile.length];

	// RSC-Remastered
	public static final int SPAWN_SECTOR_X = 50;
	public static final int SPAWN_SECTOR_Z = 51;
	private WorldLoader worldLoader;
	private World world;
	private Scene scene;

	//private World world;
	private SceneBuilder sceneBuilder;
	//private Scene scene;
	
	public GameState(final Engine engine) {
		super(engine);
		
		// Generate a pink texture.
		// This will be used to create a visual representation of errors in 3D space.
		TextureManager.getInstance().createTexture("pink", Color.PINK, 1, 1, 1f);
		
		// Load the fonts. Reset the chat console.
		this.debugFont = new TrueTypeFont(new Font("Consoles", Font.PLAIN, 12));
		this.consoleFont = new TrueTypeFont(new Font("Helvetica", Font.BOLD, 15));
		resetConsoleLog();

		// Load the audio files.
		TinySound.init();
		for (int i = 0; i < audioStream.length; i++) {
			audioStream[i] = TinySound.loadSound(new File(Main.ASSET_DIRECTORY + "audio/" + audioFile[i] + ".wav").getAbsoluteFile(), true);
		}
		audioStream[0].play(0.25);

		// Initialize RSC-Remastered...
		scene = new Scene();
		world = new World(scene);
		worldLoader = new WorldLoader(world);
		sceneBuilder = new SceneBuilder(scene, world);
	}

	@Override
	public void render2D(boolean resized, int width, int height, boolean clickedL, boolean clickedR) {
		// Handle resize event.
		if (resized) {
		}

		// Draw the RSC logo.
		//TextureManager.getInstance().draw("rsclogo", (Display.getWidth() / 2) - 150, -50);

		// Daw the debug hud.
		//if (Config.RENDER_DEBUG_HUD) {
			GraphicsGL.setColor2D(Color.ORANGE);
			debugFont.drawString(5, 200, "Sector: " + world.getSectorX() + "," + world.getSectorZ() + ", plane: " + world.getCurrentLayer());
			debugFont.drawString(5, 220, "Camera: " + camera.getX() + "," + camera.getZ() + ", height: " + camera.getHeight());
			debugFont.drawString(5, 240, "Camera Rotation: " + camera.getYaw());
			debugFont.drawString(5, 260, "Camera Direction: " + Directions.getDirectionAsString(camera.getYaw()));
			debugFont.drawString(5, 280, "Player: " + client.player.x + "," + client.player.z);
		//}

		// Draw some centered text.
		//GraphicsGL.setColor2D(Color.GREEN);
		//consoleFont.drawCenteredString(width, 20, Display.getTitle());

		// Draw the chatbox console text.
		GraphicsGL.setColor2D(Color.YELLOW);
		for (int i = 0; i < consoleLog.length; i++) {
			consoleFont.drawShadowedString(Color.YELLOW, Color.BLACK, 1, 5, height - (i * 15) - 20 - 20, consoleLog[i]);
			//consoleFont.drawString(5, height - (i * 15) - 20 - 20, consoleLog[i]);
		}
		GraphicsGL.setColor2D(Color.WHITE);
		if (readConsoleInput) {
			consoleFont.drawShadowedString(Color.YELLOW, Color.BLACK, 1, 0, height - 20, consoleInput + (asterisk ? "*" : ""));
			//consoleFont.drawString(0, height - 20, consoleInput + (asterisk ? "*" : ""));
		} else {
			consoleFont.drawShadowedString(Color.YELLOW, Color.BLACK, 1, 0, height - 20, "Press [ENTER] to use the chatbox.");
			//consoleFont.drawString(0, height - 20, "Press [ENTER] to toggle console input.");
		}

		// Handle button clicking..
		if (clickedL) {
		}

	}

	@Override
	public void render3D(boolean resized, boolean clickedL, boolean clickedR) {
		// Handle resize event.
		if (resized) {
		}

		// Update the camera.
		camera.translate();
		
		// Don't render until the world is loaded
		if (!world.isLoaded()) {
			return;
		}

		// Build the scene
		sceneBuilder.build();

		// Render the client.
		client.render(this, camera, world);
	}

	@Override
	public void updateLogic(long currentTime, float delta) {
	}

	@Override
	public void oneSecondTimeLapse() {
		this.asterisk = !asterisk;
	}

	@Override
	public void pollKeyboardInput(boolean[] keysDown) {
		if (readConsoleInput) {
			return;
		}
		
		//client.pollKeyboardInput(keysDown);
		
		/*
		 * TODO: Feed these parameters to the camera in the future.
		int x = player.x + cameraPositionX;
		int z = player.z + cameraPositionZ;
		int y = -world.getAveragedElevation(x, z);

		int pitch = Camera.DEFAULT_PITCH;
		int yaw = cameraRotation * 4;
		int roll = 0;
		int height = cameraHeight * 2;

		scene.getCamera().set(x, y, z, pitch, yaw, roll, height);

		// Update fog distance based on camera height
		scene.fogZDistance = Camera.DEFAULT_FOG_DISTANCE + (cameraHeight * 2);
		*/

		camera.pollInput(keysDown, client);
	}

	@Override
	public void pollKeyboardInput(int keyCode, char keyChar, boolean keyReleased) {
		if (!keyReleased) {
			return;
		}

		// Change levels
		if (keyCode == Keyboard.KEY_EQUALS) {
			worldLoader.ascend(client);
			loadSectors();
			System.out.println("Changed height level (add).");
			return;
		}
		if (keyCode == Keyboard.KEY_MINUS) {
			worldLoader.descend(client);
			loadSectors();
			System.out.println("Changed height level (minus).");
			return;
		}
		
		// xxx
		// Change sectors using WASD.
		
		// Chatbox is listening for keyboard input.
		if (readConsoleInput) {

			// Convert the keyboard input into a chatbox string.
			consoleInput = Utils.formatInputString(consoleInput, keyCode, keyChar, 60, Keyboard.KEY_BACK);

			// User is canceling chatbox input.
			if (keyCode == Keyboard.KEY_ESCAPE) {
				readConsoleInput = false;
				return;
			}

			// A chatbox input has been submitted.
			if (keyCode == Keyboard.KEY_RETURN) {
				readConsoleInput = false; // reset input flag

				// User has submitted a ::command.
				if (consoleInput.startsWith("::")) {
					String command = consoleInput.replaceFirst("::", "");
					addMessage("<Command>" + command);
					if (command.equals("noclip")) {
						addMessage("Hah! Did you really think I managed to get that far?");
					}
					consoleInput = ""; // Clear the console input text.
					return;
				}

				// User has submitted a /command.
				if (consoleInput.startsWith("/")) {
					String command = consoleInput.replaceFirst("/", "");
					addMessage("<Command>" + command);
					if (command.equals("clear")) {
						resetConsoleLog();
					}
					if (command.equals("help")) {
						addMessage("Commands: /cam <int: height_offset>");
					}
					if (command.startsWith("cam")) {
						int height_offset = Integer.parseInt(command.replace("cam ", ""));
						//camera = new Camera(66, 32);
						camera.setHeightOffset(height_offset);
					}
					consoleInput = ""; // Clear the console input text.
					return;
				}

				// User has submitted a chatbox command.
				if (consoleInput.length() > 0) {
					addMessage("<Message>" + consoleInput);
					consoleInput = ""; // Clear the console input text.
					return;
				}

			}

		}

		// Chatbox is not listening for keyboard input.
		// User is enabling console input.
		if (!readConsoleInput && keyCode == Keyboard.KEY_RETURN) {
			readConsoleInput = true;
			return;
		}

	}

	@Override
	public void pollMouseInput() {
		@SuppressWarnings("unused")
		int mouseX = MouseUtil.getX();
		@SuppressWarnings("unused")
		int mouseY = MouseUtil.getY();
	}

	@Override
	public void onStateOpen() {		
		addMessage("Welcome to RuneScape Classic OpenGL.");
		addMessage("Use /help for commands & information.");
		addMessage("Use WASD to move around. Arrow Keys or Middle Mouse to rotate camera.");

		// Initialize the client.
		this.client = new MudClient3D(engine.getFrustum());

		// Player position is relative to the World origin
		/*
		player = new Player();
		player.x = 66 * World.TILE_WIDTH;
		player.z = 32 * World.TILE_DEPTH;
		worldLoader.loadSector(SPAWN_SECTOR_X, SPAWN_SECTOR_Z);
		*/

		//camera = new Camera(66 * World.TILE_WIDTH, 32 * World.TILE_DEPTH);
		camera = new Camera(64, 64);
		camera.setHeightOffset(32);
		client.onStateOpen();
		//worldLoader.loadSector(client, client.player.x, client.player.z);
		worldLoader.loadSector(client, SPAWN_SECTOR_X, SPAWN_SECTOR_Z);
		//worldLoader.loadSector(client, camera.getX(), camera.getZ());
	}

	@Override
	public void onStateExit() {
		resetConsoleLog();
	}

	@Override
	public void dispose() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, Utils.gc());
	}

	/**
	 * Adds a message to the chat console.
	 * @param message The message to add.
	 */
	public void addMessage(String message) {
		int l = consoleLog.length;
		for (int i = 1; i < consoleLog.length; i++) {
			consoleLog[l - i] = consoleLog[l - i - 1];
		}
		consoleLog[0] = message;
	}

	/**
	 * Resets all of the chat console messages.
	 */
	private void resetConsoleLog() {
		for (int i = 0; i < consoleLog.length; i++) {
			consoleLog[i] = "";
		}
		consoleInput = "";
	}

	// XXX THIS METHOD IS ONLY CALLED INTERNALLY, OR BY MUDCLIENT3D.JAVA
	public void loadSectors() {
		/*
		if (player.x < 16 * World.TILE_WIDTH) {
			worldLoader.loadSector(world.getSectorX() - 1, world.getSectorZ());
			player.x += Sector.WIDTH * World.TILE_WIDTH;
		} else if (player.x > 80 * World.TILE_WIDTH) {
			worldLoader.loadSector(world.getSectorX() + 1, world.getSectorZ());
			player.x -= Sector.WIDTH * World.TILE_WIDTH;
		}
		if (player.z < 16 * World.TILE_DEPTH) {
			worldLoader.loadSector(world.getSectorX(), world.getSectorZ() - 1);
			player.z += Sector.DEPTH * World.TILE_DEPTH;
		} else if (player.z > 80 * World.TILE_DEPTH) {
			worldLoader.loadSector(world.getSectorX(), world.getSectorZ() + 1);
			player.z -= Sector.DEPTH * World.TILE_DEPTH;
		}
		*/
		int x = camera.getX();
		int z = camera.getZ();
		//int x = client.player.x;
		//int z = client.player.z;
		if (x < 0) {
			x = 0;
		}
		if (z < 0) {
			z = 0;
		}
		if (x < 16) {
			worldLoader.loadSector(client, world.getSectorX() - 1, world.getSectorZ());
			x += Sector.WIDTH;
		} else if (x > 80) {
			worldLoader.loadSector(client, world.getSectorX() + 1, world.getSectorZ());
			x -= Sector.WIDTH;
		}
		if (z < 16) {
			worldLoader.loadSector(client, world.getSectorX(), world.getSectorZ() - 1);
			z += Sector.DEPTH;
		} else if (z > 80) {
			worldLoader.loadSector(client, world.getSectorX(), world.getSectorZ() + 1);
			z -= Sector.DEPTH;
		}
	}

	public Scene getScene() {
		return scene;
	}
	
	public World getWorld() {
		return world;
	}
	
	public WorldLoader getWorldLoader() {
		return worldLoader;
	}
	
	public SceneBuilder getSceneBuilder() {
		return sceneBuilder;
	}
	
}
