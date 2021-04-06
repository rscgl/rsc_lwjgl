package cc.morgue.lwjgl2x.gl.util;

import java.awt.MouseInfo;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseUtil {

	/**
	 * @return true if the mouse is inside of the <code>org.lwjgl.opengl.Display</code> area.
	 */
	public static boolean isMouseInsideOfDisplay() {
		int mx = MouseInfo.getPointerInfo().getLocation().x;
		int my = MouseInfo.getPointerInfo().getLocation().y;
		int minX = Display.getX();
		int minY = Display.getY();
		int maxX = minX + Display.getWidth();
		int maxY = minY + Display.getHeight();
		return Display.isActive() && !(mx < minX || mx > maxX || my < minY || my > maxY);
	}

	public static int getX() {
		if (!Display.isCreated()) {
			return 0;
		}
		return Mouse.getX();
	}

	public static int getY() {
		if (!Display.isCreated()) {
			return 0;
		}
		return Display.getHeight() - Mouse.getY();
	}

}