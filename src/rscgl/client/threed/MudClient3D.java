package rscgl.client.threed;

import java.io.File;

import org.lwjgl.input.Keyboard;

import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.texture.TextureManager.FilterType;
import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.threed.Water;
import cc.morgue.lwjgl2x.gl.threed.model.obj.Model;
import cc.morgue.lwjgl2x.gl.threed.model.obj.ModelLoader;
import cc.morgue.lwjgl2x.gl.threed.model.obj.Scene;
import cc.morgue.lwjgl2x.util.Utils;
import rscgl.GameConfigs;
import rscgl.Main;
import rscgl.client.Camera;
import rscgl.client.GameState;
import rscgl.client.Player;
import rscgl.client.game.Sector;
import rscgl.client.game.world.World;

/**
 * MudClient3D uses data pulled from RSC-Remastered to construct a virtual 3D world.
 * It isn't a perfect replica of the RuneScape Classic game world, but it's essentially the same thing...
 * 
 * This class manages the 4 nearby sectors (regions), game objects (3D models), etc.
 */
public class MudClient3D {
	
	// TODO
	// THIS IS ONLY USED ONCE AT STARTUP, TO PREVENT MAPS FROM CONSTANTLY RELOADING!!!
	// IN THE FUTURE THIS NEEDS TO BE REMOVED AND REPLACED WITH A SYSTEM THAT ONLY RELOADS MAPS IF THE PLAYER/CAMERA HAS TRANSLATED
	private boolean generateTerrain = true;
	public Player player = new Player();

	private final Frustum frustum;
	private Skybox skybox;

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

	/**
	 * Throttle the 3D sectors re-building.
	 * This isn't really needed, but might be helpful on older machines.
	 */
	private long lastSectorUpdate = 0L;
	
	// Model related:
	private ModelLoader loader;
	private Scene scene;
	
	public MudClient3D(Frustum frustum) {
		this.frustum = frustum;

		TextureManager.getInstance().addTexture("roof", new File(Main.ASSET_DIRECTORY + "terrain/roof.jpg"), FilterType.NEAREST);
		TextureManager.getInstance().addTexture("stone", new File(Main.ASSET_DIRECTORY + "terrain/stone.jpg"), FilterType.NEAREST);
		TextureManager.getInstance().addTexture("wood", new File(Main.ASSET_DIRECTORY + "terrain/wood.png"), FilterType.NEAREST);
		TextureManager.getInstance().addTexture("wood_white", new File(Main.ASSET_DIRECTORY + "terrain/wood_white.png"), FilterType.NEAREST);
		TextureManager.getInstance().addTexture("fence_metal", new File(Main.ASSET_DIRECTORY + "terrain/fence_metal.png"), FilterType.NEAREST);
		TextureManager.getInstance().addTexture("fence_wooden", new File(Main.ASSET_DIRECTORY + "terrain/fence_wooden.png"), FilterType.NEAREST);
		
		// Load the water tile texture.
		TextureManager.getInstance().addTexture("water", new File(Main.ASSET_DIRECTORY + "terrain/water.png"), FilterType.NEAREST);

		// Initialize the water tiles.
		if (DRAW_WATER_PLANE) {
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
		this.scene = new Scene();
		this.loader = new ModelLoader();
	}
	
	public void render(GameState gameState, Camera camera, World world) {
		final long currentTime = Utils.getCurrentTimeMillis();

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
		scene.render(DRAW_WIRE_FRAME);
		
		// Render the sectors.
		for (int i = 0; i < sector3D.length; i++) {
			if (sector3D[i] != null) {
				sector3D[i].render(camera, frustum);
			}
		}

		// Update visibility lists
		//generateTerrain = camera.hasVisibilityUpdateFlag();
		//generateTerrain = true;
		if (generateTerrain && lastSectorUpdate < currentTime - 1200) {
			generateTerrain = false;
			lastSectorUpdate = currentTime;
			
			gameState.loadSectors();
			
			// Update the sector list.
			System.out.println("Updating 3D sectors: " + Utils.formatTimeMillis(currentTime));
			
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
	 * Loads a wavefront (.obj) model and adds it to the Scene.
	 * 
	 * @param filename The model file name.
	 * @param defaultTextureMaterial The default model texture material.
	 */
	public Model loadObjModel(String filename, String defaultTextureMaterial) {
		try {
			Model model = loader.loadModel(filename, defaultTextureMaterial);
			scene.addModel(model);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error loading model: " + filename);
			System.exit(1);
		}
		return null;
	}

	public void pollKeyboardInput(boolean[] keysDown) {
		if (keysDown[Keyboard.KEY_W]) {
			// forward..
			player.x += 1;
		}
		if (keysDown[Keyboard.KEY_S]) {
			// backward..
			player.x -= 1;
		}
		if (keysDown[Keyboard.KEY_A]) {
			// left..
			player.z -= 1;
		}
		if (keysDown[Keyboard.KEY_D]) {
			// right..
			player.z += 1;
		}
		if (keysDown[Keyboard.KEY_W] || keysDown[Keyboard.KEY_A] || keysDown[Keyboard.KEY_S] || keysDown[Keyboard.KEY_D]) {
		}
		
	}

	public void onStateOpen() {
		player = new Player();
	}

}