package rscgl.client.threed;

import java.util.ArrayList;
import java.util.List;

import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.threed.VectorObject;
import rscgl.GameConfigs;
import rscgl.client.Camera;
import rscgl.client.GameState;
import rscgl.client.game.Sector;
import rscgl.client.game.Tile;
import rscgl.client.game.world.World;
import rscgl.client.threed.obj.GameObject3D;
import rscgl.client.threed.util.TileTextureConstants;

/**
 * A 3D representation of a sector which has been loaded into memory using RSC-Remastered.
 * @author Corey
 *
 */
public class Sector3D {

	private final int x;
	private final int z;

	/**
	 * An array of 3D tiles.
	 */
	private List<Tile3D> tileList = new ArrayList<Tile3D>();

	/**
	 * Refer to RSC-Remastered for this value. It's also known as layer.
	 */
	private int plane;

	/**
	 * An array of primitive 3D objects.
	 */
	private List<VectorObject> objectList = new ArrayList<VectorObject>();
	
	public Sector3D(int x, int z, int plane) {
		this.x = x;
		this.z = z;
		this.plane = plane;
	}

//	public void add(Tile3D tile) {
//		tileList.add(tile);
//	}

	/**
	 * Build a 3D sector using the 2D sector data.
	 * @param sector2D The 2D sector object from RSC-Remastered.
	 * 
	 * This is basically a custom (probably bad) recreation of WorldLoader->loadLayer
	 */
	public void build(GameState client, World world, Sector sector2D) {
		constructTerrainAndBuildings(client, world, sector2D);
	}

	private void constructTerrainAndBuildings(GameState client, World world, Sector sector2D) {
		this.plane = sector2D.getPlane();
		
		// This data is only used during sector generation, then it gets disposed.
		float groundElevationList[][] = new float[Sector.WIDTH][Sector.DEPTH];
		int tileTextureList[][] = new int[Sector.WIDTH][Sector.DEPTH];
		int roofTextureList[][] = new int[Sector.WIDTH][Sector.DEPTH];
		int horizontalWallList[][] = new int[Sector.WIDTH][Sector.DEPTH];
		int verticalWallList[][] = new int[Sector.WIDTH][Sector.DEPTH];
		int diagonalWallsList[][] = new int[Sector.WIDTH][Sector.DEPTH];
		int groundOverlayList[][] = new int[Sector.WIDTH][Sector.DEPTH];
		
		// Generate the base values so there are no nullpointer errors if something doesn't exist.
		// These values will adjusted using data from RSC-Remastered.
		for (int x = 0; x < Sector.WIDTH; x ++) {
			for (int z = 0; z < Sector.DEPTH; z++) {
				groundElevationList[x][z] = GameConfigs.WATER_LEVEL; // default = water level
				tileTextureList[x][z] = Short.MAX_VALUE; // default = black
				roofTextureList[x][z] = -1;
				horizontalWallList[x][z] = -1;
				verticalWallList[x][z] = -1;
				diagonalWallsList[x][z] = -1;
				groundOverlayList[x][z] = -1;
			}
		}
		
		TextureManager.getInstance().createTexture("tile-" + Short.MAX_VALUE, TileTextureConstants.getTileColor(Short.MAX_VALUE), 1, 1, 1f);

		// Store the RSC-Remastered data in memory.
		// Get the tile data from the 2D sector.
		Tile[] tile2D = sector2D.getTileData();
		
		for (int i = 0; i < tile2D.length; i++) {
			
			int x = tile2D[i].x;
			int z = tile2D[i].z;
			
			// The elevation of this tile.
			groundElevationList[x][z] = (tile2D[i].groundElevation & 0xff);

			// The texture ID of this tile.
			tileTextureList[x][z] = tile2D[i].texture & 0xff;

			// The texture ID of the roof of this tile.
			roofTextureList[x][z] = tile2D[i].roofTexture & 0xff;

			// The texture ID of any horizontal wall on this tile.
			horizontalWallList[x][z] = tile2D[i].horizontalWall & 0xff;

			// The texture ID of any vertical wall on this tile.
			verticalWallList[x][z] = tile2D[i].verticalWall & 0xff;

			// The ID of any diagonal walls on this tile.
			diagonalWallsList[x][z] = tile2D[i].diagonalWalls;

			// The overlay texture ID.
			groundOverlayList[x][z] = tile2D[i].groundOverlay & 0xff;
			
			// Lower the ground if the tile is supposed to render water.
			// TODO This definitely isn't the right value but it's a good starting point.
			if (groundOverlayList[x][z] == TileTextureConstants.GROUND_OVERLAY_WATER_BLUE) {
				groundElevationList[x][z] = 0;
			}
			
			if (x != 0 && z != 0) {
				//System.out.println("Tile Coordinates: " + x + "," + z + ", elevation: " + groundElevation);
			}

			// Generate a 1x1 pixel texture for each unique tile color.
			if (tileTextureList[x][z] > -1) {
				if (!TextureManager.getInstance().contains("tile-" + tileTextureList[x][z])) {
					TextureManager.getInstance().createTexture("tile-" + tileTextureList[x][z], TileTextureConstants.getTileColor(tileTextureList[x][z]), 1, 1, 1f);
				}
			}
			
		}

		/**
		 * Offset each tile so the sectors are stacked on top of eachother.
		 */
		int sectorOffsetX = 0;
		int sectorOffsetZ = 0;
		// sectors[0] = (x - 1, z - 1)
		// sectors[1] = (x, z - 1)
		// sectors[2] = (x - 1, z)
		// sectors[3] = (x, z)
		if (sector2D == world.getSector(0)) {
			sectorOffsetX = -1;
			sectorOffsetZ = -1;
		}
		if (sector2D == world.getSector(1)) {
			sectorOffsetX = 0;
			sectorOffsetZ = -1;
		}
		if (sector2D == world.getSector(2)) {
			sectorOffsetX = -1;
			sectorOffsetZ = 0;
		}
		if (sector2D == world.getSector(3)) {
			sectorOffsetX = 0;
			sectorOffsetZ = 0;
		}
		sectorOffsetX = sectorOffsetX * Sector.WIDTH;
		sectorOffsetZ = sectorOffsetZ * Sector.DEPTH;
		
		// Generate the 3D tiles objects.
		Tile3D tile3D = null;
		for (int x = 0; x < Sector.WIDTH; x ++) {
			for (int z = 0; z < Sector.DEPTH; z++) {
				
				// Tile position in 3D space.
				float[] start = {
					x + sectorOffsetX,
					z + sectorOffsetZ,
					0
				};
				
				// Tile connections (this merges tiles even with varying heights!)
				int rm1 = Math.max(x - 1, 0);
				int cm1 = Math.max(z - 1, 0);
				int rp1 = Math.min(x + 1, Sector.WIDTH - 1); // rows
				int cp1 = Math.min(z + 1, Sector.DEPTH - 1); // cols
				float h1 = avg(groundElevationList[rm1][z], groundElevationList[rm1][cm1], groundElevationList[x][cm1], groundElevationList[x][z]);
				float h2 = avg(groundElevationList[x][cm1], groundElevationList[rp1][cm1], groundElevationList[rp1][z], groundElevationList[x][z]);
				float h3 = avg(groundElevationList[rm1][cp1], groundElevationList[x][cp1], groundElevationList[rm1][z], groundElevationList[x][z]);
				float h4 = avg(groundElevationList[rp1][cp1], groundElevationList[rp1][z], groundElevationList[x][cp1], groundElevationList[x][z]);
				float[] vertexHeights = { h1, h2, h3, h4 };
				start[2] = getCenteredHeight(vertexHeights);

				// Figure out which texture the tile needs.
				String texture = tileTextureList[x][z] > -1 ? "tile-" + tileTextureList[x][z] : "tile-" + Short.MAX_VALUE;

				// Finally, build the 3D tile object.
				try {
					tile3D = new Tile3D(texture, plane, start, vertexHeights, roofTextureList[x][z], horizontalWallList[x][z], verticalWallList[x][z], diagonalWallsList[x][z], groundOverlayList[x][z]);
					tileList.add(tile3D);
					
					boolean testModelPerformance = false;
					if (testModelPerformance) {
						try {
							int id = 50;//new Random(System.currentTimeMillis()).nextInt(452) + 1;
							GameObject3D obj3D = new GameObject3D(client, id, tile3D.getX(), tile3D.getZ(), tile3D.getHeight() + 1, world.getCurrentLayer());
						} catch (Exception ignore) {
							// MODEL DOES NOT EXIST
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Tile texture not found! " + texture);
//					System.exit(1);
				}
				
			}
		}
		
		// Cleanup.
		groundElevationList = null;
		tileTextureList = null;
		roofTextureList = null;
		horizontalWallList = null;
		verticalWallList = null;
		diagonalWallsList = null;
		groundOverlayList = null;
		
	}

	public void render(Camera camera, Frustum frustum) {
		for (Tile3D tile : tileList) {
			if (tile.getPlane() != plane) {
				continue;
			}
			tile.render(camera, frustum, GameState.DRAW_WIRE_FRAME, plane);
		}
		for (VectorObject object : objectList) {
			object.render(camera, frustum, GameState.DRAW_WIRE_FRAME);
		}
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public List<Tile3D> getListList() {
		return tileList;
	}

	public Tile3D get(int x, int z) {
		for (Tile3D tile : tileList) {
			if (tile.getX() == x && tile.getZ() == z) {
				return tile;
			}
		}
		return null;
	}

	public void unload() {
		for (Tile3D tile : tileList) {
			tile.cleanup();
		}
		tileList.clear();
	}

	private static float avg(float v1, float v2, float v3, float v4) {
		float total = v1 + v2 + v3 + v4;
		return total / 128f; // The lower the divider, the higher the terrain will appear to be.
	}

	public static float getCenteredHeight(float[] vertexHeights) {
		//return avg(vertexHeights[0], vertexHeights[1], vertexHeights[2], vertexHeights[3]);
		
		/*
		float H = -Integer.MAX_VALUE;
		float L = Integer.MAX_VALUE;
		for (int i = 0; i < vertexHeights.length; i++) {
			if (vertexHeights[i] > H) {
				H = vertexHeights[i];
			}
		}
		for (int i = 0; i < vertexHeights.length; i++) {
			if (vertexHeights[i] < L) {
				L = vertexHeights[i];
			}
		}
		return ((H + L) / 2);
		*/
		float H = -Integer.MAX_VALUE;
		for (int i = 0; i < vertexHeights.length; i++) {
			if (vertexHeights[i] > H) {
				H = vertexHeights[i];
			}
		}
		return H;
	}

}