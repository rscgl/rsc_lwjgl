package rscgl.client;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.lwjgl.opengl.Display;

import cc.morgue.lwjgl2x.AbstractState;
import cc.morgue.lwjgl2x.Engine;
import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.font.TrueTypeFont;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.util.MouseUtil;
import client.entityhandling.defs.AnimationDef;
import client.entityhandling.defs.DoorDef;
import client.entityhandling.defs.ElevationDef;
import client.entityhandling.defs.GameObjectDef;
import client.entityhandling.defs.ItemDef;
import client.entityhandling.defs.NpcDef;
import client.entityhandling.defs.PrayerDef;
import client.entityhandling.defs.SpellDef;
import client.entityhandling.defs.TextureDef;
import client.entityhandling.defs.TileDef;
import client.res.ResourceLoader;
import client.res.Resources;
import client.res.Sprite;
import client.res.Texture;
import client.util.DataUtils;

public class PreloadState extends AbstractState {

	// 2D
	private TrueTypeFont consoleFont;

	private static final String LANDSCAPE_FILENAME = "Landscape.zip";
	private static final String SPRITES_FILENAME = "Sprites.zip";

	private String message = "Loading...";
	private int progress = -1;

	private int numInvImages;

	private List<String> models = new ArrayList<>();

	public PreloadState(final Engine engine) {
		super(engine);
		this.consoleFont = new TrueTypeFont(new Font("Helvetica", Font.BOLD, 15));
		
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Loading game cache..");

		// Load sprites
		Resources.spriteArchive = ResourceLoader.loadZipData(SPRITES_FILENAME);
	}

	@Override
	public void render2D(boolean resized, int width, int height, boolean clickedL, boolean clickedR) {
		// Handle resize event.
		if (resized) {
		}
		
		TextureManager.getInstance().draw("rsclogo", (Display.getWidth() / 2) - 150, -50);

		// Daw the loading text.
		GraphicsGL.setColor2D(Color.WHITE);
		consoleFont.drawCenteredString(width, 100, "Loading - Please wait..");
		consoleFont.drawCenteredString(width, 120, message + " (%" + progress + ")");

		// Draw some centered text.
		GraphicsGL.setColor2D(Color.GREEN);
		consoleFont.drawCenteredString(width, 20, Display.getTitle());

	}

	@Override
	public void render3D(boolean resized, boolean clickedL, boolean clickedR) {
		// Handle resize event.
		if (resized) {
		}
	}

	@Override
	public void updateLogic(long currentTime, float delta) {
		if (!isLoaded()) {
			continueLoading();
			return;
		}
		
		// Finished. Switch application states.
		if (isLoaded()) {
			engine.setApplicationState(1);
			return;
		}
	}

	@Override
	public void oneSecondTimeLapse() {
	}

	@Override
	public void pollKeyboardInput(boolean[] keysDown) {
	}

	@Override
	public void pollKeyboardInput(int keyCode, char keyChar, boolean keyReleased) {

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
	}

	@Override
	public void onStateExit() {
	}

	@Override
	public void dispose() {
	}

	/**
	 * Loads the game and all required resources.
	 */
	public void continueLoading() {

		if (progress == -1) {
			// Don't do anything the first time, to ensure the loading screen
			// is rendered before we start loading stuff
			progress = 0;
			return;
		}

		if (progress == 0) {
			updateProgress(15, "Unpacking configuration");
			loadGameData();
			return;

		} else if (progress == 15) {
			updateProgress(30, "Unpacking media");
			loadMedia();
			return;

		} else if (progress == 30) {
			updateProgress(45, "Unpacking entities");
			return;

		} else if (progress == 45) {
			updateProgress(60, "Unpacking textures");
			loadTextures();
			return;

		} else if (progress == 60) {
			updateProgress(75, "Loading 3d models");
			return;

		} else if (progress == 75) {
			updateProgress(90, "Unpacking sound effects");
			return;

		} else if (progress == 90) {
			updateProgress(100, "Starting game...");
		}

	}

	private void loadGameData() {

		Resources.tileArchive = ResourceLoader.loadZipData(LANDSCAPE_FILENAME);

		Resources.animations = (AnimationDef[]) ResourceLoader.loadGzipData("Animations.xml.gz");
		Resources.doors = (DoorDef[]) ResourceLoader.loadGzipData("Doors.xml.gz");
		Resources.elevation = (ElevationDef[]) ResourceLoader.loadGzipData("Elevation.xml.gz");
		Resources.items = (ItemDef[]) ResourceLoader.loadGzipData("Items.xml.gz");
		Resources.npcs = (NpcDef[]) ResourceLoader.loadGzipData("NPCs.xml.gz");
		Resources.objects = (GameObjectDef[]) ResourceLoader.loadGzipData("Objects.xml.gz");
		Resources.prayers = (PrayerDef[]) ResourceLoader.loadGzipData("Prayers.xml.gz");
		Resources.spells = (SpellDef[]) ResourceLoader.loadGzipData("Spells.xml.gz");
		Resources.textureDefs = (TextureDef[]) ResourceLoader.loadGzipData("Textures.xml.gz");
		Resources.tiles = (TileDef[]) ResourceLoader.loadGzipData("Tiles.xml.gz");

		// Initialise objects
		for (int id = 0; id < Resources.objects.length; id++) {
			Resources.objects[id].modelID = getModelIndex(Resources.objects[id].getObjectModel());
		}
	}

	private int getModelIndex(String name) {
		if (name.equalsIgnoreCase("na")) {
			return 0;
		}
		if (models.contains(name)) {
			models.add(name);
			return models.size() - 1;
		}
		return -1;
	}

	private void loadMedia() {
		final int SPRITE_MEDIA_START = 2000;
		final int SPRITE_UTIL_START = 2100;
		final int SPRITE_ITEM_START = 2150;
		final int SPRITE_LOGO_START = 2010;
		final int SPRITE_PROJECTILE_START = 3160;

		loadSprite(SPRITE_MEDIA_START, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 1, "media", 6);
		loadSprite(SPRITE_MEDIA_START + 9, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 10, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 11, "media", 3);
		loadSprite(SPRITE_MEDIA_START + 14, "media", 8);
		loadSprite(SPRITE_MEDIA_START + 22, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 23, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 24, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 25, "media", 2);
		loadSprite(SPRITE_UTIL_START, "media", 2);
		loadSprite(SPRITE_UTIL_START + 2, "media", 4);
		loadSprite(SPRITE_UTIL_START + 6, "media", 2);
		loadSprite(SPRITE_PROJECTILE_START, "media", 7);
		loadSprite(SPRITE_LOGO_START, "media", 1);

		// Load item sprites.
		int i = numInvImages;
		for (int j = 1; i > 0; j++) {
			int k = i;
			i -= 30;
			if (k > 30) {
				k = 30;
			}
			loadSprite(SPRITE_ITEM_START + (j - 1) * 30, "media.object", k);
		}
	}

	// packageName currently unused!
	private void loadSprite(int id, String packageName, int amount) {
		for (int i = id; i < id + amount; i++) {
			boolean success = loadSprite(i, packageName);
			if (!success) {
				throw new NullPointerException("Sprite " + packageName + "[" + i + "] failed to load");
			}
		}
	}

	public boolean loadSprite(int id, String packageName) {
		try {
			ZipEntry e = Resources.spriteArchive.getEntry(String.valueOf(id));
			if (e == null) {
				System.err.println("Missing sprite: " + id);
				return false;
			}
			ByteBuffer data = DataUtils
					.streamToBuffer(new BufferedInputStream(Resources.spriteArchive.getInputStream(e)));
			Resources.sprites[id] = Sprite.deserialise(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void loadTextures() {
		final int SPRITE_TEXTURE_START = 3220;

		Resources.initialiseArrays(Resources.textureDefs.length, 7, 11);
		for (int i = 0; i < Resources.textureDefs.length; i++) {
			loadSprite(SPRITE_TEXTURE_START + i, "texture", 1);
			Sprite sprite = Resources.getSprite(SPRITE_TEXTURE_START + i);

			int length = sprite.getWidth() * sprite.getHeight();
			int[] pixels = sprite.getPixels();
			int ai1[] = new int[32768];
			for (int k = 0; k < length; k++) {
				ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6) + ((pixels[k] & 0xf8) >> 3)]++;
			}
			int[] dictionary = new int[256];
			dictionary[0] = 0xff00ff;
			int[] temp = new int[256];
			for (int i1 = 0; i1 < ai1.length; i1++) {
				int j1 = ai1[i1];
				if (j1 > temp[255]) {
					for (int k1 = 1; k1 < 256; k1++) {
						if (j1 <= temp[k1]) {
							continue;
						}
						for (int i2 = 255; i2 > k1; i2--) {
							dictionary[i2] = dictionary[i2 - 1];
							temp[i2] = temp[i2 - 1];
						}
						dictionary[k1] = ((i1 & 0x7c00) << 9) + ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3) + 0x40404;
						temp[k1] = j1;
						break;
					}
				}
				ai1[i1] = -1;
			}
			byte[] indices = new byte[length];
			for (int l1 = 0; l1 < length; l1++) {
				int j2 = pixels[l1];
				int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6) + ((j2 & 0xf8) >> 3);
				int l2 = ai1[k2];
				if (l2 == -1) {
					int i3 = 0x3b9ac9ff;
					int j3 = j2 >> 16 & 0xff;
						int k3 = j2 >> 8 & 0xff;
						int l3 = j2 & 0xff;
						for (int i4 = 0; i4 < 256; i4++) {
							int j4 = dictionary[i4];
							int k4 = j4 >> 16 & 0xff;
						int l4 = j4 >> 8 & 0xff;
						int i5 = j4 & 0xff;
						int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4) + (l3 - i5) * (l3 - i5);
						if (j5 < i3) {
							i3 = j5;
							l2 = i4;
						}
						}

						ai1[k2] = l2;
				}
				indices[l1] = (byte) l2;
			}
			boolean large = (sprite.getTextureWidth() / 64 - 1) == 1;
			Resources.textures[i] = new Texture(indices, dictionary, large);
			Resources.prepareTexture(i);
		}
	}

	protected final void updateProgress(int progress, String message) {
		this.progress = progress;
		this.message = message;
		System.out.println("[Preload]" + message);
	}

	public boolean isLoaded() {
		return progress >= 100;
	}

}
