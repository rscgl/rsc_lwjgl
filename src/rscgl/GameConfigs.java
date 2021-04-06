package rscgl;

import cc.morgue.lwjgl2x.gl.threed.Directions;

/**
 * These variables are pulled from the RSC client and are used to create an application that looks and feels similar to RuneScape Classic.
 * Variables with an "XXX" comment above them are a generic solution or "best guess", and probably aren't accurate... These values should be adjusted for an authentic look and feel.
 */
public class GameConfigs {
	
	// Default is 530.
	public static final int DISPLAY_WIDTH = 512;
	
	// Default is 320.
	public static final int DISPLAY_HEIGHT = 384;
	
	// Default is false.
	public static final boolean DISPLAY_RESIZABLE = true;
	
	// XXX
	// The field of view.
	public static final int FIELD_OF_VIEW = 70;

	// XXX
	// The maximum distance in which objects in 3D space will be rendered.
	public static final int RENDER_DISTANCE = 1000;

	// The size (in OpenGL world space units) of a single tile.
	// This value shouldn't be changed, even if RSC was constructed with a different scaling metric...
	public static final float TILE_SIZE = 1;
	
	// This value will either brighten or darken the tiles.
	// TODO: Implement this on ALL game world objects, inc: walls, roofs, prop models, etc.
	public static final float TERRAIN_BRIGHTNESS_MODIFIER = 0.75f;

	// XXX
	// This will set the height of the walls.
	// There's no official way to determine the proper value, since LWJGL world units differ from RSC's world units, so this will have to be determined by eyeballing it.
	public static final float WALL_HEIGHT = 2.5f;
	
	// XXX
	// This will set the depth of the walls.
	// I'm sure there's an official value in the RSC client, but I don't know how to get it... (Also the walls look better thicker).
	public static final float WALL_DEPTH = 0.05f;
	
	//XXX
	// The height value in which water is rendered.
	// In real life, I guess this would be called "sea level".
	public static final float WATER_LEVEL = -0.1f;

	// XXX
	// Default camera pitch offset. This should be applied whenever the user logs into the game.
	public static final float DEFAULT_CAMERA_PITCH_OFFSET = 0;

	// Default camera yaw offset. This should be applied whenever the user logs into the game.
	public static final float DEFAULT_CAMERA_YAW_OFFSET = Directions.ROTY_NORTH;
	
	// XXX
	// Camera controller yaw modifier value.
	public static final float CAMERA_YAW_MODIFIER = 4.0f;

	// XXX
	// Camera controller pitch modifier value.
	public static final float CAMERA_PITCH_MODIFIER = 2.5f;

	// XXX
	// Limit the maximum camera pitch.
	public static final float CAMERA_MAXIMUM_PITCH = -75;

	// XXX
	// Limit the minimum camera pitch.
	public static final float CAMERA_MINIMUM_PITCH = -35;

	// The camera zoom wasn't able to be adjusted by the user in RSC, so the min/max value should be equal to whatever was in the jagex client.
	public static final float CAMERA_MINIMUM_ZOOM = 3.5f;
	public static final float CAMERA_MAXIMUM_ZOOM = 35f;

}
