package cc.morgue.lwjgl2x.gl.threed;

/**
 * Mathematically correct rotation values based on a 45 degree rotation scheme.
 * There are 8 pre-defined rotations.
 */
public class Directions {

	private final static float ONE_EIGHTH = (float) 90 / (float) 8;

	public enum Direction {
		NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST;
	}

	public static final float ROTY_NORTH = 270;
	public static final float[] ROTY_EAST = { 360, 0 };
	public static final float ROTY_SOUTH = 90;
	public static final float ROTY_WEST = 180;
	public static final float ROTY_NORTH_EAST = 315;
	public static final float ROTY_NORTH_WEST = 225;
	public static final float ROTY_SOUTH_EAST = 45;
	public static final float ROTY_SOUTH_WEST = 135;

	public static Direction getDirection(float rotationY) {
		final float ONE_EIGHTH = (float) 90 / (float) 8;
		if (rotationY >= ROTY_NORTH - ONE_EIGHTH && rotationY <= ROTY_NORTH + ONE_EIGHTH) {
			return Direction.NORTH;
		} else if (rotationY >= ROTY_EAST[0] - ONE_EIGHTH || rotationY <= ROTY_EAST[1] + ONE_EIGHTH) {
			return Direction.EAST;
		} else if (rotationY >= ROTY_SOUTH - ONE_EIGHTH && rotationY <= ROTY_SOUTH + ONE_EIGHTH) {
			return Direction.SOUTH;
		} else if (rotationY >= ROTY_WEST - ONE_EIGHTH && rotationY <= ROTY_WEST + ONE_EIGHTH) {
			return Direction.WEST;
		} else if (rotationY >= ROTY_NORTH + ONE_EIGHTH && rotationY <= ROTY_EAST[0] - ONE_EIGHTH) {
			return Direction.NORTH_EAST;
		} else if (rotationY <= ROTY_NORTH - ONE_EIGHTH && rotationY >= ROTY_WEST - ONE_EIGHTH) {
			return Direction.NORTH_WEST;
		} else if (rotationY <= ROTY_SOUTH - ONE_EIGHTH && rotationY >= ROTY_EAST[1] + ONE_EIGHTH) {
			return Direction.SOUTH_EAST;
		} else if (rotationY >= ROTY_SOUTH + ONE_EIGHTH && rotationY <= ROTY_WEST - ONE_EIGHTH) {
			return Direction.SOUTH_WEST;
		}
		return null;
	}

	public static float getDirectionAsFloat(float rotationY) {
		final float ONE_EIGHTH = (float) 90 / (float) 8;
		if (rotationY >= ROTY_NORTH - ONE_EIGHTH && rotationY <= ROTY_NORTH + ONE_EIGHTH) {
			return ROTY_NORTH;
		} else if (rotationY >= ROTY_EAST[0] - ONE_EIGHTH || rotationY <= ROTY_EAST[1] + ONE_EIGHTH) {
			return ROTY_EAST[rotationY >= ROTY_EAST[0] - ONE_EIGHTH ? 0 : 1];
		} else if (rotationY >= ROTY_SOUTH - ONE_EIGHTH && rotationY <= ROTY_SOUTH + ONE_EIGHTH) {
			return ROTY_SOUTH;
		} else if (rotationY >= ROTY_WEST - ONE_EIGHTH && rotationY <= ROTY_WEST + ONE_EIGHTH) {
			return ROTY_WEST;
		} else if (rotationY >= ROTY_NORTH + ONE_EIGHTH && rotationY <= ROTY_EAST[0] - ONE_EIGHTH) {
			return ROTY_NORTH_EAST;
		} else if (rotationY <= ROTY_NORTH - ONE_EIGHTH && rotationY >= ROTY_WEST - ONE_EIGHTH) {
			return ROTY_NORTH_WEST;
		} else if (rotationY <= ROTY_SOUTH - ONE_EIGHTH && rotationY >= ROTY_EAST[1] + ONE_EIGHTH) {
			return ROTY_SOUTH_EAST;
		} else if (rotationY >= ROTY_SOUTH + ONE_EIGHTH && rotationY <= ROTY_WEST - ONE_EIGHTH) {
			return ROTY_SOUTH_WEST;
		}
		return 0;
	}

	public static String getDirectionAsString(float rotationY) {
		final float ONE_EIGHTH = (float) 90 / (float) 8;
		if (rotationY >= ROTY_NORTH - ONE_EIGHTH && rotationY <= ROTY_NORTH + ONE_EIGHTH) {
			return "North";
		} else if (rotationY >= ROTY_EAST[0] - ONE_EIGHTH || rotationY <= ROTY_EAST[1] + ONE_EIGHTH) {
			return "East";
		} else if (rotationY >= ROTY_SOUTH - ONE_EIGHTH && rotationY <= ROTY_SOUTH + ONE_EIGHTH) {
			return "South";
		} else if (rotationY >= ROTY_WEST - ONE_EIGHTH && rotationY <= ROTY_WEST + ONE_EIGHTH) {
			return "West";
		} else if (rotationY >= ROTY_NORTH + ONE_EIGHTH && rotationY <= ROTY_EAST[0] - ONE_EIGHTH) {
			return "North East";
		} else if (rotationY <= ROTY_NORTH - ONE_EIGHTH && rotationY >= ROTY_WEST - ONE_EIGHTH) {
			return "North West";
		} else if (rotationY <= ROTY_SOUTH - ONE_EIGHTH && rotationY >= ROTY_EAST[1] + ONE_EIGHTH) {
			return "South East";
		} else if (rotationY >= ROTY_SOUTH + ONE_EIGHTH && rotationY <= ROTY_WEST - ONE_EIGHTH) {
			return "South West";
		}
		return "NaN";
	}

	public static Direction getDirection2D(int x1, int y1, int x2, int y2) {
		double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));

		// Keep angle between 0 and 360
		angle = angle + Math.ceil(-angle / 360) * 360;

		if (angle >= ROTY_NORTH - ONE_EIGHTH && angle <= ROTY_NORTH + ONE_EIGHTH) {
			return Direction.NORTH;
		} else if (angle >= ROTY_EAST[0] - ONE_EIGHTH || angle <= ROTY_EAST[1] + ONE_EIGHTH) {
			return Direction.EAST;
		} else if (angle >= ROTY_SOUTH - ONE_EIGHTH && angle <= ROTY_SOUTH + ONE_EIGHTH) {
			return Direction.SOUTH;
		} else if (angle >= ROTY_WEST - ONE_EIGHTH && angle <= ROTY_WEST + ONE_EIGHTH) {
			return Direction.WEST;
		} else if (angle >= ROTY_NORTH + ONE_EIGHTH && angle <= ROTY_EAST[0] - ONE_EIGHTH) {
			return Direction.NORTH_EAST;
		} else if (angle <= ROTY_NORTH - ONE_EIGHTH && angle >= ROTY_WEST - ONE_EIGHTH) {
			return Direction.NORTH_WEST;
		} else if (angle <= ROTY_SOUTH - ONE_EIGHTH && angle >= ROTY_EAST[1] + ONE_EIGHTH) {
			return Direction.SOUTH_EAST;
		} else if (angle >= ROTY_SOUTH + ONE_EIGHTH && angle <= ROTY_WEST - ONE_EIGHTH) {
			return Direction.SOUTH_WEST;
		}
		return null;
	}

	public static Direction getDirectionBetweenTiles(int currentX, int currentY, int nextX, int nextY) {
		boolean n = currentY > nextY;
		boolean s = currentY < nextY;
		boolean e = currentX < nextX;
		boolean w = currentX > nextX;
		if (!n && !s) {
			if (e) {
				return Direction.EAST;
			}
			if (w) {
				return Direction.WEST;
			}
		}
		if (n && (!e && !w)) {
			return Direction.NORTH;
		}
		if (n && e) {
			return Direction.NORTH_EAST;
		}
		if (n && w) {
			return Direction.NORTH_WEST;
		}
		if (s && (!e && !w)) {
			return Direction.SOUTH;
		}
		if (s && e) {
			return Direction.SOUTH_EAST;
		}
		if (s && w) {
			return Direction.SOUTH_WEST;
		}
		return null;
	}

	public static int[] directionToCoordinate2D(Direction direction) {
		int[] dd = { 0, 0 };
		switch (direction) {
		case NORTH:
			dd[1] -= 1;
			break;
		case EAST:
			dd[0] += 1;
			break;
		case SOUTH:
			dd[1] += 1;
			break;
		case WEST:
			dd[0] -= 1;
			break;
		case NORTH_EAST:
			dd[1] -= 1;
			dd[0] += 1;
			break;
		case NORTH_WEST:
			dd[1] -= 1;
			dd[0] -= 1;
			break;
		case SOUTH_EAST:
			dd[1] += 1;
			dd[0] += 1;
			break;
		case SOUTH_WEST:
			dd[1] += 1;
			dd[0] -= 1;
			break;
		}
		return dd;
	}

}