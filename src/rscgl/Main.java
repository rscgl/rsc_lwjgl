package rscgl;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import cc.morgue.lwjgl2x.Config;
import cc.morgue.lwjgl2x.Engine;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.texture.TextureManager.FilterType;
import cc.morgue.lwjgl2x.util.Utils;
import rscgl.client.GameState;
import rscgl.client.PreloadState;

/**
 * An attempt to recreate some of RuneScape Classic using LWJGL.
 * 
 * Credits:
 * Morgue (https://morgue.cc)										Application Developer
 * RSC-Remastered (https://github.com/Danjb1/rsc-remastered)		RSC Cache Loader & Miscellaneous Code
 * Open RSC (https://github.com/Open-RSC/Core-Framework)			Community Help via Discord (https://discord.gg/KGvudZ9)
 * 2003Scape (https://github.com/2003scape/rsc-sounds)				Miscellaneous Game Assets
 * 2003Scape (https://github.com/2003scape/rsc-sprites)				Miscellaneous Game Assets
 * RSCD Sprite Editor (												Texture dump.
 */
public class Main {

	/**
	 * The directory in which all of the game assets and computer generated files are stored.
	 */
	public static final String ASSET_DIRECTORY = getFileStorage();

	public static void main(String[] args) {
		/*
		// Log output to file:
		try {
			System.setOut(new PrintStream(new File("log.txt")));
		} catch (FileNotFoundException ignore) {
		}
		*/
		
		/*
		 * Define some engine configurations.
		 */
		Config.EXTENDED_VERBOSE = false;
		Config.USE_ANTIALIASING = true;
		Config.EMERGENCY_SHUTDOWN_HOTKEY = false;
		Config.RENDER_DEBUG_HUD = false;
		Config.RENDER_FPS_MEM_USAGE_HUD = true;
		Config.FRAME_RATE = 50;
		Config.FIELD_OF_VIEW = GameConfigs.FIELD_OF_VIEW;
		Config.Z_FAR = GameConfigs.RENDER_DISTANCE;
		Config.Z_NEAR = 0.01f;
		Config.FORCE_WHITE_PRE_TEXTURE = true;

		/*
		 * Determine which LWJGL native libraries to load.
		 */
		switch (Utils.getOperatingSystem()) {
		case WINDOWS:
			System.setProperty("org.lwjgl.librarypath", new File("lib/natives/windows/").getAbsolutePath());
			break;
		case MAC:
			System.setProperty("org.lwjgl.librarypath", new File("lib/natives/macosx/").getAbsolutePath());
			break;
		default:
			System.setProperty("org.lwjgl.librarypath", new File("lib/natives/linux/").getAbsolutePath());
			break;
		}

		/*
		 * Construct the Engine instance.
		 */
		Engine engine = new Engine("RSC GL", GameConfigs.DISPLAY_WIDTH, GameConfigs.DISPLAY_HEIGHT, GameConfigs.DISPLAY_RESIZABLE);

		/*
		 * Load the RSC logo image.
		 */
		TextureManager.getInstance().addTexture("rsclogo", new File(Main.ASSET_DIRECTORY + "rsclogo.png"), FilterType.NEAREST);

		/*
		 * Register the application states, then enable one of them.
		 */
		engine.registerApplicationState(new PreloadState(engine));
		engine.registerApplicationState(new GameState(engine));
		engine.setApplicationState(0);

		/*
		 * Finally, start the main loop..
		 */
		engine.run();
	}

	/**
	 * Determines the file storage directory.
	 */
	private static String getFileStorage() {
		final String directory = "./cache/";

		// /user_home/token/
		//return Platform.getUserHomeDirectory() + "rscgl_cache" + File.separator;

		// ~/Desktop/token/
		//return Platform.getDesktopDirectory() + "rscgl_cache" + File.separator;
		
		// Make sure the directory exists.
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdir();
			Logger.getLogger(Main.class.getName()).log(Level.INFO, "Created directory : " + ASSET_DIRECTORY);
		}

		
		return directory;
	}

}
