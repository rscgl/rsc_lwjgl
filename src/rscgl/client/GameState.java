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
import cc.morgue.lwjgl2x.gl.texture.TextureManager.FilterType;
import cc.morgue.lwjgl2x.gl.threed.Directions;
import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.threed.Water;
import cc.morgue.lwjgl2x.gl.threed.model.obj.*;
import cc.morgue.lwjgl2x.gl.util.MouseUtil;
import cc.morgue.lwjgl2x.util.Utils;
import rscgl.GameConfigs;
import rscgl.Main;
import rscgl.client.game.SceneBuilder;
import rscgl.client.game.Sector;
import rscgl.client.game.world.World;
import rscgl.client.game.world.WorldLoader;
import rscgl.client.threed.Sector3D;
import rscgl.client.threed.Skybox;
import rscgl.client.threed.Tile3D;
import rscgl.game.scene.Scene;

/**
 * GameState uses data loaded from the Jagex cache files, using code from RSC-Remastered.
 * The data is then managed by the LWJGL software and drawn using our own rendering methods.
 * We are essentially just loading cache data, storing it in memory, and then re-creating the entire 3D aspect of the RSC client. It's gross, but there's no way around it...
 * It isn't a perfect replica of the RuneScape Classic game world, but it's essentially going to be the same thing... eventually?
 * 
 * This class manages the 4 nearby sectors (regions), game objects (3D models), etc.
 */
public class GameState extends AbstractState {

	// 2D
	private TrueTypeFont debugFont; // debug hud
	private TrueTypeFont consoleFont;
	private boolean readConsoleInput = false;
	private String consoleInput = "";
	private String[] consoleLog = new String[8];
	private boolean asterisk = true;

	// 3D
	private Camera camera;
	//private final Frustum frustum;
	private Skybox skybox;

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
	//private ModelRenderManager scene;

	// TODO
	// THIS IS ONLY USED ONCE AT STARTUP, TO PREVENT MAPS FROM CONSTANTLY RELOADING!!!
	// IN THE FUTURE THIS NEEDS TO BE REMOVED AND REPLACED WITH A SYSTEM THAT ONLY RELOADS MAPS IF THE PLAYER/CAMERA HAS TRANSLATED
	private boolean generateTerrain = true;

	/**
	 * Enable wireframe.
	 */
	public static final boolean DRAW_WIRE_FRAME = false;

	/**
	 * A custom feature which will be enabled in the far future...
	 * This will render a transparent water plane at GameConfigs.WATER_LEVEL and will render above the authentic RSC water.
	 * It can be used to create the illusion of real water in areas below WATER_LEVEL.
	 */
	public static final boolean DRAW_WATER_PLANE = false;

	// The 4 water tiles.
	// These are large tiles which are generated once at startup and will follow the camera.
	// This is better than creating and rendering thousands of water tiles across the world.
	private Water[] waterTileList = new Water[4];

	// The size of a water tile.
	private final int WATER_TILE_SIZE = 48;
	
	/**
	 * Contains four 3D sectors.
	 */
	private Sector3D[] sector3D = new Sector3D[World.NUM_SECTORS];
	
	// Model related:
	private ModelLoader loader;
	private ModelRenderManager modelRenderManager;
	
	public GameState(final Engine engine) {
		super(engine);
		
		// Generate a pink texture.
		// This will be used to create a visual representation of errors in 3D space.
		TextureManager.getInstance().createTexture("pink", Color.PINK, 1, 1, 1f);
		
		// Load the RSC texture dump.
		// These will be used to texture the 3D world.
		final String texturePath = Main.ASSET_DIRECTORY + "sprites/img/texture/";
		for (int i = 3220; i < 3274; i++) {
			TextureManager.getInstance().addTexture("rsctexture-" + i, new File(texturePath + i + ".png"), FilterType.MIPMAP);
		}
		
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
		
		// Initialize the water tiles.
		if (DRAW_WATER_PLANE) {
			// Load the water tile texture.
			TextureManager.getInstance().addTexture("water", new File(Main.ASSET_DIRECTORY + "terrain/water.png"), FilterType.NEAREST);

			// Construct the 4 water tiles.
			for (int i = 0; i < 4; i++) {
				waterTileList[i] = new Water(WATER_TILE_SIZE, GameConfigs.WATER_LEVEL, TextureManager.getInstance().getTextureId("water"));
			}
		}

		// Initialize the skybox.
		//this.skybox = new Skybox();
		
		// Initialize the array with generic data.
		for (int i = 0; i < sector3D.length; i++) {
			sector3D[i] = new Sector3D(-1, -1, -1);
		}
		
		// Initialize 3D model support.
		this.modelRenderManager = new ModelRenderManager();
		this.loader = new ModelLoader();
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
			debugFont.drawString(5, 280, "");
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

		// Render the skybox.
		if (skybox != null) {
			skybox.render();
		}

		// Water Mesh
		if (DRAW_WATER_PLANE) {
			float x = camera.getAbsoluteX();
			float z = camera.getAbsoluteZ();
			waterTileList[0].render(x, z);
			waterTileList[1].render(x, z - WATER_TILE_SIZE);
			waterTileList[2].render(x - WATER_TILE_SIZE, z);
			waterTileList[3].render(x - WATER_TILE_SIZE, z - WATER_TILE_SIZE);
		}
		
		// Render the 3D models.
		modelRenderManager.render(DRAW_WIRE_FRAME);
		
		// Render the sectors.
		for (int i = 0; i < sector3D.length; i++) {
			if (sector3D[i] != null) {
				sector3D[i].render(camera, engine.getFrustum());
			}
		}

		// Update the 3D sectors.
		// XXX NOTE: This is only done once at startup. If the generateTerrain boolean is always set to true, the map will reload.
		// I currently only load this once at startup because I need to copy the logic from RSC only updates the map AS NEEDED, instead of every frame.
		if (generateTerrain) {
			generateTerrain = false;
			
			loadSectors();
			
			// Update the sector list.
			System.out.println("Updating 3D sectors: " + Utils.formatTimeMillis(Utils.getCurrentTimeMillis()));
			
			for (Sector3D sector : sector3D) {
				sector.unload();
			}
			
			// Convert the 2D sector data into a 3D object.
			Sector sectors2D[] = world.getSectors();
			for (int i = 0; i < sectors2D.length; i++) {
				if (sectors2D[i] != null) {
					this.sector3D[i].build(this, world, sectors2D[i]);
				}
			}
		}

		// Render the NPCs.
		// TODO ... long term goal?
		
		// Render the players.
		// TODO ... long term goal?
		
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

		camera.pollInput(keysDown, this);
	}

	@Override
	public void pollKeyboardInput(int keyCode, char keyChar, boolean keyReleased) {
		if (!keyReleased) {
			return;
		}

		// Change levels
		if (keyCode == Keyboard.KEY_EQUALS) {
			worldLoader.ascend(this);
			loadSectors();
			this.generateTerrain = true;
			addMessage("Changed height level (add).");
			return;
		}
		if (keyCode == Keyboard.KEY_MINUS) {
			worldLoader.descend(this);
			loadSectors();
			this.generateTerrain = true;
			addMessage("Changed height level (minus).");
			return;
		}
		if (keyCode == Keyboard.KEY_F5) {
			loadSectors();
			this.generateTerrain = true;
			addMessage("Reloading map sectors.");
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
		addMessage("Key controls: - and + to change map height, F5 to reload sectors.");

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
		//worldLoader.loadSector(client, client.player.x, client.player.z);
		worldLoader.loadSector(this, SPAWN_SECTOR_X, SPAWN_SECTOR_Z);
		//worldLoader.loadSector(client, camera.getX(), camera.getZ());
		this.generateTerrain = true;
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
			worldLoader.loadSector(this, world.getSectorX() - 1, world.getSectorZ());
			x += Sector.WIDTH;
			//this.generateTerrain = true;
		} else if (x > 80) {
			worldLoader.loadSector(this, world.getSectorX() + 1, world.getSectorZ());
			x -= Sector.WIDTH;
			//this.generateTerrain = true;
		}
		if (z < 16) {
			worldLoader.loadSector(this, world.getSectorX(), world.getSectorZ() - 1);
			z += Sector.DEPTH;
			//this.generateTerrain = true;
		} else if (z > 80) {
			worldLoader.loadSector(this, world.getSectorX(), world.getSectorZ() + 1);
			z -= Sector.DEPTH;
			//this.generateTerrain = true;
		}
	}

	public Tile3D getTile(int x, int z) {
		for (Sector3D sector : sector3D) {
			for (Tile3D tile : sector.getListList()) {
				if (tile.getX() == x && tile.getZ() == z) {
					return tile;
				}
			}
		}
		return null;
	}

	public float getTileHeight(int x, int z) {
		return getTile(x, z).getHeight();
	}

	/**
	 * Loads a wavefront (.obj) model and adds it to the ModelRenderManager.
	 * 
	 * @param filename The model file name.
	 * @param defaultTextureMaterial The default model texture material.
	 */
	public Model loadObjModel(String filename, String defaultTextureMaterial) {
		try {
			Model model = loader.loadModel(filename, defaultTextureMaterial);
			modelRenderManager.addModel(model);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error loading model: " + filename);
			System.exit(1);
		}
		return null;
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
