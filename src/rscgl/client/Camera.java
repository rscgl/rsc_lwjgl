package rscgl.client;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.gl.threed.Vector3f;
import rscgl.GameConfigs;
import rscgl.client.threed.MudClient3D;

public class Camera {

	private final float PI_180 = (float) Math.PI / 180;
	
	// The height offset of the camera.
	// This would be pinned to the closest tile in the future, and
	// eventually pinned to the player position instead in the way distant future.
	private float heightOffset = 0;
	
	// The zoom offset modifies the height of the camera.
	// This value is added to the heightOffset.
	private float zoomOffset = 0;
	
	// Camera position in 3D space.
	private Vector3f position;
	
	// Camera orientation in 3D space.
	private float yaw = 0;
	private float pitch = 0;
	private float roll = 0;

	// Camera update flags.
	private int mouseX; // not used for logic - just a flag
	private int mouseY; // not used for logic - just a flag
	private boolean mouseUpdateFlag = true;
	private boolean keyboardUpdateFlag = true;
	
	// A flag used to determine if the Terrain3D class needs updated.
	private boolean visibilityUpdateFlag = true;

	public Camera(int x, int z) {
		this.pitch = GameConfigs.DEFAULT_CAMERA_PITCH_OFFSET;
		this.yaw = GameConfigs.DEFAULT_CAMERA_YAW_OFFSET;
		this.position = new Vector3f(x, 0, z);
	}
	
	public Camera() {
		this(0, 0);
	}

	public void setHeightOffset(float offset) {
		this.heightOffset = offset;
	}

	/**
	 * Applies the camera translations and rotations to GL_MODELVIEW.
	 */
	public void translate() {
		GL11.glPushAttrib(GL_TRANSFORM_BIT);
		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glLoadIdentity();
		//GL11.glRotatef(rotation.x, 1, 0, 0);
		//GL11.glRotatef(rotation.y, 0, 1, 0);
		//GL11.glRotatef(rotation.z, 0, 0, 1);
		GL11.glRotatef(-pitch, 1.0f, 0f, 0f); //camera position x
		GL11.glRotatef(-(yaw + 180), 0f, 1.0f, 0f); // camera position y
		GL11.glTranslatef(-position.x, -position.y - heightOffset, -position.z);
		GL11.glPopAttrib();
	}

	/*
	 * RuneScape camera, but with WASD movement.
	 */
	public void pollInput(boolean[] keysDown, MudClient3D terrain) {
		// XXX
		// Temporary method of moving the camera around in 3D space, until a player object is added to the project.
		final float movementSpeed = 0.25f * ((keysDown[Keyboard.KEY_LSHIFT] || keysDown[Keyboard.KEY_RSHIFT]) ? 2 : 1);
		float rotY = -(yaw + 180);
		if (keysDown[Keyboard.KEY_W]) {
			// forward..
			position.x += Math.sin(rotY * PI_180) * movementSpeed;
			position.z += -Math.cos(rotY * PI_180) * movementSpeed;
		}
		if (keysDown[Keyboard.KEY_S]) {
			// backward..
			position.x -= Math.sin(rotY * PI_180) * movementSpeed;
			position.z -= -Math.cos(rotY * PI_180) * movementSpeed;
		}
		if (keysDown[Keyboard.KEY_A]) {
			// left..
			position.x += Math.sin((rotY - 90) * PI_180) * movementSpeed;
			position.z += -Math.cos((rotY - 90) * PI_180) * movementSpeed;
		}
		if (keysDown[Keyboard.KEY_D]) {
			// right..
			position.x += Math.sin((rotY + 90) * PI_180) * movementSpeed;
			position.z += -Math.cos((rotY + 90) * PI_180) * movementSpeed;
		}
		if (keysDown[Keyboard.KEY_W] || keysDown[Keyboard.KEY_A] || keysDown[Keyboard.KEY_S] || keysDown[Keyboard.KEY_D]) {
			visibilityUpdateFlag = true;
		}
		// --- ENDOF temporary movement method
	
		// Handle mouse input.
		int mx = Mouse.getX();
		int my = Display.getHeight() - Mouse.getY();
		mouseUpdateFlag = (mx != mouseX || (Display.getHeight() - my) != mouseY);
		mouseX = mx;
		mouseY = my;

		// Handle zoom (mouse wheel).
		// This feature will be disabled if the min/max values are equal.
		float newZoomOffset = zoomOffset;
		if (GameConfigs.CAMERA_MINIMUM_ZOOM != GameConfigs.CAMERA_MAXIMUM_ZOOM) {
			int wheel = Mouse.getDWheel();
			if (wheel != 0) {
				mouseUpdateFlag = true;
				newZoomOffset += 0.01f * -wheel;
				if (newZoomOffset < GameConfigs.CAMERA_MINIMUM_ZOOM) {
					newZoomOffset = GameConfigs.CAMERA_MINIMUM_ZOOM;
				}
				if (newZoomOffset > GameConfigs.CAMERA_MAXIMUM_ZOOM) {
					newZoomOffset = GameConfigs.CAMERA_MAXIMUM_ZOOM;
				}
			}
		}

		// Handle drag (mouse wheel).
		boolean grabbed = Mouse.isButtonDown(2);
		if (Mouse.isGrabbed() || grabbed) {
			mouseUpdateFlag = true;
			float mouseDX = Mouse.getDX() * 0.32f;
			float mouseDY = Mouse.getDY() * 0.32f;
			Mouse.setGrabbed(grabbed);
			if (yaw + mouseDX >= 360) {
				yaw = yaw - mouseDX - 360;
			} else if (yaw + mouseDX < 0) {
				yaw = 360 - yaw - mouseDX;
			}
			yaw -= mouseDX;
			pitch += mouseDY;
		}

		// Handle camera rotation.
		// This is designed to emulate RuneScapes arrow key camera controller.
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			keyboardUpdateFlag = true;
			yaw += GameConfigs.CAMERA_YAW_MODIFIER;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			keyboardUpdateFlag = true;
			yaw -= GameConfigs.CAMERA_YAW_MODIFIER;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			keyboardUpdateFlag = true;
			pitch -= GameConfigs.CAMERA_PITCH_MODIFIER;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			keyboardUpdateFlag = true;
			pitch += GameConfigs.CAMERA_PITCH_MODIFIER;
		}

		// Fix the camera orientation.
		if (yaw >= 360) {
			yaw -= 360;
		}
		if (yaw < 0) {
			yaw += 360;
		}

		// Limit the camera orientation.
		if (pitch < GameConfigs.CAMERA_MAXIMUM_PITCH) {
			pitch = GameConfigs.CAMERA_MAXIMUM_PITCH;
		}
		if (pitch > GameConfigs.CAMERA_MINIMUM_PITCH) {
			pitch = GameConfigs.CAMERA_MINIMUM_PITCH;
		}
		
		// Set the camera base height to the current tile.
		float currentTileHeight = position.y;
		if (mouseUpdateFlag) {
			try {
				currentTileHeight = terrain.getTileHeight(getX(), getZ());
			} catch (Exception ignore) {
			}
		}
		this.position.y = currentTileHeight;
		
		/*
		// Apply the zoom modifier to the camera position.
		// NOTE: Zoom is kind of broken and I cba... If the terrain stuff was working I would be willing to invest more time into non-critical features like finishing the camera
		if (newZoomOffset != zoomOffset) {
			// Get the zoom vector.
			Vector3f zoomDirection = this.getDirectionVector();
			
			// Apply the modification.
			zoomDirection.multiply(newZoomOffset > zoomOffset ? newZoomOffset : -newZoomOffset);
			
			// Update the zoom value.
			zoomOffset = newZoomOffset;
			
			// Update the camera position.
			position = position.getAddition(zoomDirection);
		}
		*/
		
	}

	private Vector3f getDirectionVector() {
		double pitchRadians = Math.toRadians(pitch);
		double yawRadians = Math.toRadians(yaw);
		double sinPitch = Math.sin(pitchRadians);
		double cosPitch = Math.cos(pitchRadians);
		double sinYaw = Math.sin(yawRadians);
		double cosYaw = Math.cos(yawRadians);
		return new Vector3f((float) -cosPitch * (float) sinYaw, (float) -sinPitch, (float) -cosPitch * (float) cosYaw);
	}

	public boolean updateRayTracer() {
		boolean flag = (mouseUpdateFlag || keyboardUpdateFlag);
		this.mouseUpdateFlag = false;
		this.keyboardUpdateFlag = false;
		return flag;
	}

	public Vector3f getPosition() {
		return position.copy();
	}

	public int getX() {
		return (int) (position.x + 65536) - 65536;
	}

	public int getZ() {
		return (int) (position.y + 65536) - 65536;
	}

	public float getAbsoluteX() {
		return position.x;
	}

	public float getAbsoluteZ() {
		return position.z;
	}

	public float getHeight() {
		return position.y;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	// TODO : THIS IS JUST A TEMPORARY METHOD UNTIL MOBS ARE ADDED
	public boolean hasVisibilityUpdateFlag() {
		boolean flag = visibilityUpdateFlag;
		this.visibilityUpdateFlag = false;
		return flag;
	}
	
}