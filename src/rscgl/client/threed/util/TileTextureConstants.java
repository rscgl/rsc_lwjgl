package rscgl.client.threed.util;

import java.awt.Color;

/**
 * The data in this file came from 'pyramin', from the OpenRSC discord server.
 */
public class TileTextureConstants {
	
	public TileTextureConstants() {
	}

	// This will be used to fill null requests.
	private static final Color NULL_BLACK = new Color(0, 0, 0);
	
	private static final Color FLOOR_BROWN = new Color(100, 48, 2);
	private static final Color WATER_BLUE = new Color(32, 64, 126);
	private static final Color FLOOR_GRAY = new Color(64, 64, 64);
	private static final Color FLOOR_RED = new Color(119, 0, 17);

	// TODO These color integer values are incomplete (meaning some ground overlays arent added yet) and the existing ones may be incorrect...
	// I'm just guessing here - Morgue
	public static final int GROUND_OVERLAY_FLOOR_BROWN = 4;
	public static final int GROUND_OVERLAY_WATER_BLUE = 2;
	public static final int GROUND_OVERLAY_FLOOR_GRAY = 1;
	public static final int GROUND_OVERLAY_FLOOR_RED = 5;

	public static Color getTileColor(int index) {
		// NOTE: This switch statement is NOT logic that existed in the RSC client.
		// It has been specifically created for RSCGL, because I'm unsure of how some things were handled
		// and this seems to be a decent solution (I guess) - Morgue.
		if (index == Short.MAX_VALUE) {
			// This was added to render black tiles whenever there is an error finding the actual tile color texture.
			// See Sector3D class for implementation.
			return NULL_BLACK;
		}
		return colorArray[index];
	}
	
	public static Color getGroundOverlayColor(int index) {
		switch (index) {
		case GROUND_OVERLAY_FLOOR_BROWN:
			return FLOOR_BROWN;
		case GROUND_OVERLAY_FLOOR_GRAY:
			return FLOOR_GRAY;
		case GROUND_OVERLAY_FLOOR_RED:
			return FLOOR_RED;
		case GROUND_OVERLAY_WATER_BLUE:
			return WATER_BLUE;
		default:
			return NULL_BLACK;
		}
	}
	
	/**
	 * Possible RGB values for tiles.
	 */
	private static final Color[] colorArray = { new Color(255, 255, 255), new Color(251, 254, 251),
			new Color(247, 252, 247), new Color(243, 250, 243), new Color(239, 248, 239), new Color(235, 247, 235),
			new Color(231, 245, 231), new Color(227, 243, 227), new Color(223, 241, 223), new Color(219, 240, 219),
			new Color(215, 238, 215), new Color(211, 236, 211), new Color(207, 234, 207), new Color(203, 233, 203),
			new Color(199, 231, 199), new Color(195, 229, 195), new Color(191, 227, 191), new Color(187, 226, 187),
			new Color(183, 224, 183), new Color(179, 222, 179), new Color(175, 220, 175), new Color(171, 219, 171),
			new Color(167, 217, 167), new Color(163, 215, 163), new Color(159, 213, 159), new Color(155, 212, 155),
			new Color(151, 210, 151), new Color(147, 208, 147), new Color(143, 206, 143), new Color(139, 205, 139),
			new Color(135, 203, 135), new Color(131, 201, 131), new Color(127, 199, 127), new Color(123, 198, 123),
			new Color(119, 196, 119), new Color(115, 194, 115), new Color(111, 192, 111), new Color(107, 191, 107),
			new Color(103, 189, 103), new Color(99, 187, 99), new Color(95, 185, 95), new Color(91, 184, 91),
			new Color(87, 182, 87), new Color(83, 180, 83), new Color(79, 178, 79), new Color(75, 177, 75),
			new Color(71, 175, 71), new Color(67, 173, 67), new Color(63, 171, 63), new Color(59, 170, 59),
			new Color(55, 168, 55), new Color(51, 166, 51), new Color(47, 164, 47), new Color(43, 163, 43),
			new Color(39, 161, 39), new Color(35, 159, 35), new Color(31, 157, 31), new Color(27, 156, 27),
			new Color(23, 154, 23), new Color(19, 152, 19), new Color(15, 150, 15), new Color(11, 149, 11),
			new Color(7, 147, 7), new Color(3, 145, 3), new Color(0, 144, 0), new Color(3, 144, 0),
			new Color(6, 144, 0), new Color(9, 144, 0), new Color(12, 144, 0), new Color(15, 144, 0),
			new Color(18, 144, 0), new Color(21, 144, 0), new Color(24, 144, 0), new Color(27, 144, 0),
			new Color(30, 144, 0), new Color(33, 144, 0), new Color(36, 144, 0), new Color(39, 144, 0),
			new Color(42, 144, 0), new Color(45, 144, 0), new Color(48, 144, 0), new Color(51, 144, 0),
			new Color(54, 144, 0), new Color(57, 144, 0), new Color(60, 144, 0), new Color(63, 144, 0),
			new Color(66, 144, 0), new Color(69, 144, 0), new Color(72, 144, 0), new Color(75, 144, 0),
			new Color(78, 144, 0), new Color(81, 144, 0), new Color(84, 144, 0), new Color(87, 144, 0),
			new Color(90, 144, 0), new Color(93, 144, 0), new Color(96, 144, 0), new Color(99, 144, 0),
			new Color(102, 144, 0), new Color(105, 144, 0), new Color(108, 144, 0), new Color(111, 144, 0),
			new Color(114, 144, 0), new Color(117, 144, 0), new Color(120, 144, 0), new Color(123, 144, 0),
			new Color(126, 144, 0), new Color(129, 144, 0), new Color(132, 144, 0), new Color(135, 144, 0),
			new Color(138, 144, 0), new Color(141, 144, 0), new Color(144, 144, 0), new Color(147, 144, 0),
			new Color(150, 144, 0), new Color(153, 144, 0), new Color(156, 144, 0), new Color(159, 144, 0),
			new Color(162, 144, 0), new Color(165, 144, 0), new Color(168, 144, 0), new Color(171, 144, 0),
			new Color(174, 144, 0), new Color(177, 144, 0), new Color(180, 144, 0), new Color(183, 144, 0),
			new Color(186, 144, 0), new Color(189, 144, 0), new Color(192, 144, 0), new Color(191, 143, 0),
			new Color(189, 141, 0), new Color(188, 140, 0), new Color(186, 138, 0), new Color(185, 137, 0),
			new Color(183, 135, 0), new Color(182, 134, 0), new Color(180, 132, 0), new Color(179, 131, 0),
			new Color(177, 129, 0), new Color(176, 128, 0), new Color(174, 126, 0), new Color(173, 125, 0),
			new Color(171, 123, 0), new Color(170, 122, 0), new Color(168, 120, 0), new Color(167, 119, 0),
			new Color(165, 117, 0), new Color(164, 116, 0), new Color(162, 114, 0), new Color(161, 113, 0),
			new Color(159, 111, 0), new Color(158, 110, 0), new Color(156, 108, 0), new Color(155, 107, 0),
			new Color(153, 105, 0), new Color(152, 104, 0), new Color(150, 102, 0), new Color(149, 101, 0),
			new Color(147, 99, 0), new Color(146, 98, 0), new Color(144, 96, 0), new Color(143, 95, 0),
			new Color(141, 93, 0), new Color(140, 92, 0), new Color(138, 90, 0), new Color(137, 89, 0),
			new Color(135, 87, 0), new Color(134, 86, 0), new Color(132, 84, 0), new Color(131, 83, 0),
			new Color(129, 81, 0), new Color(128, 80, 0), new Color(126, 78, 0), new Color(125, 77, 0),
			new Color(123, 75, 0), new Color(122, 74, 0), new Color(120, 72, 0), new Color(119, 71, 0),
			new Color(117, 69, 0), new Color(116, 68, 0), new Color(114, 66, 0), new Color(113, 65, 0),
			new Color(111, 63, 0), new Color(110, 62, 0), new Color(108, 60, 0), new Color(107, 59, 0),
			new Color(105, 57, 0), new Color(104, 56, 0), new Color(102, 54, 0), new Color(101, 53, 0),
			new Color(99, 51, 0), new Color(98, 50, 0), new Color(96, 48, 0), new Color(95, 49, 0),
			new Color(93, 51, 0), new Color(92, 52, 0), new Color(90, 54, 0), new Color(89, 55, 0),
			new Color(87, 57, 0), new Color(86, 58, 0), new Color(84, 60, 0), new Color(83, 61, 0),
			new Color(81, 63, 0), new Color(80, 64, 0), new Color(78, 66, 0), new Color(77, 67, 0),
			new Color(75, 69, 0), new Color(74, 70, 0), new Color(72, 72, 0), new Color(71, 73, 0),
			new Color(69, 75, 0), new Color(68, 76, 0), new Color(66, 78, 0), new Color(65, 79, 0),
			new Color(63, 81, 0), new Color(62, 82, 0), new Color(60, 84, 0), new Color(59, 85, 0),
			new Color(57, 87, 0), new Color(56, 88, 0), new Color(54, 90, 0), new Color(53, 91, 0),
			new Color(51, 93, 0), new Color(50, 94, 0), new Color(48, 96, 0), new Color(47, 97, 0),
			new Color(45, 99, 0), new Color(44, 100, 0), new Color(42, 102, 0), new Color(41, 103, 0),
			new Color(39, 105, 0), new Color(38, 106, 0), new Color(36, 108, 0), new Color(35, 109, 0),
			new Color(33, 111, 0), new Color(32, 112, 0), new Color(30, 114, 0), new Color(29, 115, 0),
			new Color(27, 117, 0), new Color(26, 118, 0), new Color(24, 120, 0), new Color(23, 121, 0),
			new Color(21, 123, 0), new Color(20, 124, 0), new Color(18, 126, 0), new Color(17, 127, 0),
			new Color(15, 129, 0), new Color(14, 130, 0), new Color(12, 132, 0), new Color(11, 133, 0),
			new Color(9, 135, 0), new Color(8, 136, 0), new Color(6, 138, 0), new Color(5, 139, 0),
			new Color(3, 141, 0), new Color(2, 142, 0) };

}
