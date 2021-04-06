package rscgl.client.threed.obj;

import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.VectorObject;
import rscgl.client.threed.util.WallTextureConstants;

/**
 * A class that was designed to generate primitive structures for RSCGL, using Vector Objects (voxels).
 * @author Morgue
 */
public class TileObjectBuilder {
	
	public static enum WallType {
		VERTICAL, HORIZONTAL, DIAGONAL;
	}

	public VectorObject buildWall(WallType type, int index, float[] startX) {
		VectorObject object = null;
		int textureId = TextureManager.getInstance().getTextureId("pink");
		
		// ..
		if (type == WallType.VERTICAL) {
			
			return object;
		}

		// ..
		if (type == WallType.HORIZONTAL) {
			
			return object;
		}

		// ..
		if (type == WallType.DIAGONAL) {
			
			return object;
		}
		
		// TODO
		// Generate 3D error object.
		//object = new VectorObject();
		return object;
	}

	private static int getTextureId(WallType type, int index) {
		switch (index) {
			case WallTextureConstants.STONE_WALL:
			case WallTextureConstants.WOODEN_DOOR_FRAME:
			case WallTextureConstants.STONE_DOOR_FRAME:
			case WallTextureConstants.STONE_WINDOW_GLASS:
			case WallTextureConstants.PICKET_FENCE:
			case WallTextureConstants.METAL_FENCE_1:
			case WallTextureConstants.STONE_WINDOW_GLASS_DECORATIVE:
			case WallTextureConstants.STONE_WALL_ARDOUGNE:
			case WallTextureConstants.STONE_HALF_WALL:
			case WallTextureConstants.STONE_WINDOW_ARCH:
			case WallTextureConstants.WOODEN_WALL_WHITE:
			case WallTextureConstants.WOODEN_WALL_WHITE_WINDOW:
			case WallTextureConstants.STONE_DOUBLE_ARCH_DOOR_FRAME:
			case WallTextureConstants.SWAMP_WALL:
			case WallTextureConstants.STONE_WALL_RUINS:
			case WallTextureConstants.LAVA_ROCK_WALL:
			case WallTextureConstants.WOODEN_PANEL_WALL:
			case WallTextureConstants.METAL_FENCE_2:
			case WallTextureConstants.JUNGLE_INVISIBLE_WALL:
			case WallTextureConstants.CAVE_INVISIBLE_WALL:
			//case WallTextureConstants.WOODEN_PANEL_WINDOW:
			//case WallTextureConstants.WOODEN_SHACK_WINDOW:
			return TextureManager.getInstance().getTextureId("wall");
		default:
			System.out.println("Unhandled wall index: " + index);
			return TextureManager.getInstance().getTextureId("pink");
		}
	}
	
}
