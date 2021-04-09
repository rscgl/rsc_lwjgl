package rscgl.client.threed;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.threed.VectorObject;
import rscgl.GameConfigs;
import rscgl.client.Camera;
import rscgl.client.threed.util.TileTextureConstants;
import rscgl.client.threed.util.WallTextureConstants;

public class Tile3D {

	//XXX GL_TRIANGLES is generally a good way to go. It'll be faster than using outdated functionality. And it's faster in principle, too - quads are broken down into triangles anyway, so you save a bunch of needless work by doing it once CPU side and then saving the GPU work it would have to do. Triangles are a standard and perfectly fine way of specifying shapes.
	//XXX GL_TRIANGLE_STRIP is faster than GL_TRIANGLES because it takes one point, not three, to specify each triangle after the first triangle is specified. The previous two vertices of the triangle before are used. You get connected triangles, but you also use less memory. Because you have to read less, you can process faster. And because your last two vertices were just specified in the last triangle, they're going to be easier to fetch in memory than two other vertices that may come from who knows where. There are also tricks for using degenerate triangle strips for allowing for seamless jumps between triangles.
	//If you want the best performance of the three, GL_TRIANGLE_STRIP is the way to go. Unfortunately, unless your data comes already specified for use in a triangle strip, you'll have to order your vertices yourself and remember where to put your degenerate triangles. A good starting point is with height maps (ie terrain). However, optimizing other parts of your code first (eg determining whether to even draw an object) may be a better use of your time. And I may be wrong, I don't think texture use should have an impact on your choice of vertex specification.
	//	Edit: Here is an example of how rendering a square is done with GL_TRIANGLE_STRIP
	//	Vertices: (0,0) (0,1) (1,0) (1,1)
	//	Triangle 1: (0,0) (0,1) (1,0) (1,1)
	//	Triangle 2: (0,0) (0,1) (1,0) (1,1)
	
	private final float centeredHeight;
	private final VectorObject mesh;
	
	/**
	 * Refer to RSC-Remastered for this value. It's also known as layer.
	 */
	private final int plane;
	
	/**
	 * A list of objects on top of the tile, includes: roof and walls.
	 */
	private final List<VectorObject> objects = new ArrayList<VectorObject>();
	
	/**
	 * 
	 * @param texture
	 * @param start
	 * @param vertexHeights
	 * @param brightness
	 * @param roofTexture The texture ID of the roof of this tile.
	 * @param horizontalWall The texture ID of any horizontal wall on this tile.
	 * @param verticalWall The texture ID of any vertical wall on this tile.
	 * @param diagonalWalls The ID of any diagonal walls on this tile.
	 */
	public Tile3D(String texture, int plane, float[] start, float[] vertexHeights, int roofTexture, int horizontalWall, int verticalWall, int diagonalWalls, int groundOverlay) {
		this.plane = plane;
		
		// Determine tile height
		this.centeredHeight = Sector3D.getCenteredHeight(vertexHeights);
		
		// Build the 3D object
		int meshId = GL11.glGenLists(1);
		GL11.glNewList(meshId, GL11.GL_COMPILE);

		int textureId = TextureManager.getInstance().getTexture(texture).getTextureId();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glColor3f(GameConfigs.TERRAIN_BRIGHTNESS_MODIFIER, GameConfigs.TERRAIN_BRIGHTNESS_MODIFIER, GameConfigs.TERRAIN_BRIGHTNESS_MODIFIER);
		
		float[] end = { start[0] + 1, start[1] + 1, start[2] };

		GraphicsGL.enableCulling(false, true);
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			// ..
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0], vertexHeights[0], start[1]);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(start[0], vertexHeights[2], end[1]);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(end[0], vertexHeights[3], end[1]);

			// ..
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(end[0], vertexHeights[3], end[1]);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(end[0], vertexHeights[1], start[1]);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(start[0], vertexHeights[0], start[1]);
		}
		GL11.glEnd();
		GraphicsGL.disableCulling();
		GL11.glEndList();
		
		this.mesh = new VectorObject(meshId, start, end, false);
		
		
		/*
		 * Create the additional 3D objects.
		 */
	
		// Create the roof.
		if (roofTexture != 0) {
			float[] roofStart = { start[0], start[1], start[2] };
			float[] roofEnd = { -1, -1, -1 };
			roofStart[0] = roofStart[0];
			roofStart[1] = roofStart[1];
			roofStart[2] = roofStart[2] + GameConfigs.WALL_HEIGHT;
			roofEnd[0] = roofStart[0] + 1f; // x (width)
			roofEnd[1] = roofStart[1] + 1f; // z (depth)
			roofEnd[2] = roofStart[2] + 0.1f; // y (height)
			int roofTextureId = TextureManager.getInstance().getTextureId("rsctexture-3226");
			objects.add(new VectorObject(roofStart, roofEnd, roofTextureId));
		}

		// Create the horizontal wall.
		if (horizontalWall != 0) {
			float[] hWallStart = { start[0], start[1], start[2] };
			float[] hWallEnd = { -1, -1, -1 };
			hWallStart[0] = hWallStart[0];
			hWallStart[1] = hWallStart[1];
			hWallStart[2] = hWallStart[2];
			hWallEnd[0] = hWallStart[0] + GameConfigs.WALL_DEPTH; // x (width)
			hWallEnd[1] = hWallStart[1] + 1f; // z (depth)
			hWallEnd[2] = hWallStart[2] + GameConfigs.WALL_HEIGHT; // y (height)
			
			// Get the RSC texture.
			int horizontalWallTextureId = getWallTextureId(horizontalWall)[1];

			// Apply the texture.
			// Set the vector object alpha to true, if needed.
			if (getWallTextureId(horizontalWall)[0] == 1) {
				objects.add(new VectorObject(hWallStart, hWallEnd, horizontalWallTextureId, true));
			} else {
				objects.add(new VectorObject(hWallStart, hWallEnd, horizontalWallTextureId));
			}
		}

		// Create the vertical wall.
		if (verticalWall != 0) {
			float[] vWallStart = { start[0], start[1], start[2] };
			float[] vWallEnd = { -1, -1, -1 };
			vWallStart[0] = vWallStart[0];
			vWallStart[1] = vWallStart[1];
			vWallStart[2] = vWallStart[2];
			vWallEnd[0] = vWallStart[0] + 1; // x (width)
			vWallEnd[1] = vWallStart[1] + GameConfigs.WALL_DEPTH; // z (depth)
			vWallEnd[2] = vWallStart[2] + GameConfigs.WALL_HEIGHT; // y (height)
			
			// Get the RSC texture.
			int verticalWallTextureId = getWallTextureId(verticalWall)[1];

			// Apply the texture.
			// Set the vector object alpha to true, if needed.
			if (getWallTextureId(verticalWall)[0] == 1) {
				objects.add(new VectorObject(vWallStart, vWallEnd, verticalWallTextureId, true));
			} else {
				objects.add(new VectorObject(vWallStart, vWallEnd, verticalWallTextureId));
			}
		}

		// TODO implement hard-coded rotations based on the debug value?
		// Create the diagonal walls.
		if (diagonalWalls != 0) {
			float[] dWallStart = { start[0], start[1], start[2] };
			float[] dWallEnd = { -1, -1, -1 };
			
			// Start coordinates are whatever the RSC cache says.
			dWallStart[0] = dWallStart[0];
			dWallStart[1] = dWallStart[1];
			dWallStart[2] = dWallStart[2];

			// Get the RSC texture.
			int diagonalWallsTextureId = TextureManager.getInstance().getTextureId("pink");//getWallTextureId(diagonalWalls)[1];
			
			// TODO determine rotation
			if (diagonalWalls > 0 && diagonalWalls <= 12000 + 255) { // 255 is just a guess since jagex likes to store the tiniest variable data possible they prob used a byte
				diagonalWalls = diagonalWalls - 12000;
				diagonalWallsTextureId = getWallTextureId(diagonalWalls - 12000)[0];
				//System.out.println("diagonalWalls:" + (diagonalWalls - 12000));
				dWallEnd[0] = dWallStart[0] + 0.5f; // x (width)
				dWallStart[0] = dWallStart[0] - 0.5f;
				dWallEnd[1] = dWallStart[1]; // z (depth)
				dWallEnd[2] = dWallStart[2] + GameConfigs.WALL_HEIGHT; // y (height)
			}
			
			// TODO determine rotation
			if (diagonalWalls >= 48000) {
				diagonalWalls = diagonalWalls - 48000;
				diagonalWallsTextureId = getWallTextureId(diagonalWalls)[0];
				//System.out.println("diagonalWalls:" + (diagonalWalls));
				dWallEnd[0] = dWallStart[0]; // x (width)
				dWallEnd[1] = dWallStart[1] + 0.5f; // z (depth)
				dWallStart[1] = dWallStart[1] - 0.5f;
				dWallEnd[2] = dWallStart[2] + GameConfigs.WALL_HEIGHT; // y (height)
			}
			
			// Apply the texture.
			// Set the vector object alpha to true, if needed.
			if (getWallTextureId(diagonalWalls)[0] == 1) {
				VectorObject object = new VectorObject(dWallStart, dWallEnd, diagonalWallsTextureId, true);
				objects.add(object);
			} else {
				VectorObject object = new VectorObject(dWallStart, dWallEnd, diagonalWallsTextureId);
				objects.add(object);
			}
		}
		
		// Create the ground overlay.
		if (groundOverlay != -1) {
			int groundOverlayTextureId = -1;//TextureManager.getInstance().getTextureId("tile-" + groundOverlay);
			switch (groundOverlay) {
			case 0:
				// Idk what this is... but it's not meant to be used to style the terrain.
				// If you set a texture to overlay 0 a lot of the map will be textured, except for where other ground overlay textures are applied.
				break;
			case 3:
				// wooden, building floor, etc.
				groundOverlayTextureId = TextureManager.getInstance().getTextureId("rsctexture-3223");
				break;
			//case TileTextureConstants.GROUND_OVERLAY_WATER_BLUE:
			case TileTextureConstants.GROUND_OVERLAY_FLOOR_GRAY:
			case TileTextureConstants.GROUND_OVERLAY_FLOOR_BROWN:
			case TileTextureConstants.GROUND_OVERLAY_FLOOR_RED:
				// Make sure the texture has been generated.
				if (!TextureManager.getInstance().contains("overlay-" + groundOverlay)) {
					TextureManager.getInstance().createTexture("overlay-" + groundOverlay, TileTextureConstants.getGroundOverlayColor(groundOverlay), 1, 1, 1f);
				}
				groundOverlayTextureId = TextureManager.getInstance().getTextureId("overlay-" + groundOverlay);
				break;
			default:
				//System.out.println("Unhandled Ground Overlay value: " + groundOverlay);
				break;
			}
			if (groundOverlayTextureId != -1) {
				float[] gOverlayStart = { start[0], start[1], start[2] };
				float[] gOverlayEnd = { -1, -1, -1 };
				gOverlayStart[0] = gOverlayStart[0];
				gOverlayStart[1] = gOverlayStart[1];
				gOverlayStart[2] = gOverlayStart[2] + 0.25f;
				gOverlayEnd[0] = gOverlayStart[0] + 1f; // x (width)
				gOverlayEnd[1] = gOverlayStart[1] + 1f; // z (depth)
				gOverlayEnd[2] = gOverlayStart[2] + 0.1f; // y (height)
				objects.add(new VectorObject(gOverlayStart, gOverlayEnd, groundOverlayTextureId));
			}
		}
	}

	public void render(Camera camera, Frustum frustum, boolean drawWireFrame, int currentLayer) {
		if (this.plane != currentLayer) {
			return;
		}
		mesh.render(camera, frustum, drawWireFrame);
		for (VectorObject object : objects) {
			object.render(camera, frustum, drawWireFrame);
		}
	}
	
	public int getX() {
		return (int) Math.floor(mesh.start()[0] + 0.5f);
	}

	public int getZ() {
		return (int) Math.floor(mesh.start()[1] + 0.5f);
	}

	public float getAbsoluteX() {
		return mesh.start()[0];// + 0.5f;
	}

	public float getAbsoluteZ() {
		return mesh.start()[1];// + 0.5f;
	}

	public float getHeight() {
		return centeredHeight;
	}

	public void cleanup() {
		mesh.cleanup();
		for (VectorObject object : objects) {
			object.cleanup();
		}
	}

	public int getPlane() {
		return plane;
	}
	
	/**
	 * This returns the wall texture data.
	 * @param wallIndex The wall data value read from the jagex cache.
	 * @return alpha, textureId
	 */
	private int[] getWallTextureId(int wallIndex) {
		
		boolean alpha = false;
		int textureId = TextureManager.getInstance().getTextureId("pink");//pink = error texture

		switch (wallIndex) {
		case WallTextureConstants.WOODEN_DOOR_FRAME:
		case WallTextureConstants.STONE_DOOR_FRAME:
		case WallTextureConstants.STONE_DOUBLE_ARCH_DOOR_FRAME:
			// Don't create a wall when it's supposed to be a door.
			// TODO: Construct a door object instead.
			break;


		case WallTextureConstants.PICKET_FENCE:
			textureId = TextureManager.getInstance().getTextureId("rsctexture-3230");
			//alpha = true;
			break;
		case WallTextureConstants.METAL_FENCE_1:
		case WallTextureConstants.METAL_FENCE_2:
			textureId = TextureManager.getInstance().getTextureId("rsctexture-3232");
			//alpha = true;
			break;

		case WallTextureConstants.STONE_WALL:
		case WallTextureConstants.STONE_WINDOW_GLASS:
		case WallTextureConstants.STONE_WINDOW_GLASS_DECORATIVE:
		case WallTextureConstants.STONE_WALL_ARDOUGNE:
		case WallTextureConstants.STONE_HALF_WALL:
		case WallTextureConstants.STONE_WINDOW_ARCH:
		case WallTextureConstants.SWAMP_WALL:
		case WallTextureConstants.STONE_WALL_RUINS:
		case WallTextureConstants.LAVA_ROCK_WALL:
			textureId = TextureManager.getInstance().getTextureId("rsctexture-3222");
			break;

		case WallTextureConstants.WOODEN_WALL_WHITE:
		case WallTextureConstants.WOODEN_WALL_WHITE_WINDOW:
			textureId = TextureManager.getInstance().getTextureId("rsctexture-3241");
			break;
			
		case WallTextureConstants.WOODEN_PANEL_WALL:
		case WallTextureConstants.WOODEN_PANEL_WINDOW:
			textureId = TextureManager.getInstance().getTextureId("rsctexture-3223");
			break;
			
		case WallTextureConstants.JUNGLE_INVISIBLE_WALL:
		case WallTextureConstants.CAVE_INVISIBLE_WALL:
		//case WallTextureConstants.WOODEN_SHACK_WINDOW:
			break;
			
		default:
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Unhandled wall texture requested, index: " + wallIndex);
			break;
		}
		
		return new int[] { alpha ? 1 : 0, textureId };
	}

}