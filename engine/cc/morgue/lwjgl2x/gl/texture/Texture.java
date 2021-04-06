package cc.morgue.lwjgl2x.gl.texture;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

/**
 * Represents a cached texture.
 */
public class Texture {

	private final String name;
	private final int textureId;
	private final int imageWidth;
	private final int imageHeight;
	
	// Sprite sheet configurations (optional)
	private boolean isSpriteSheet = false;
	private int columns = -1;
	private int rows = -1;
	private int frameWidth = -1;
	private int frameHeight = -1;
	
	// Extended data
	private String fileName;
	
	public Texture(String name, BufferedImage bufferedImage, int textureId) {
		this.name = name;
		this.textureId = textureId;
		this.imageWidth = bufferedImage.getWidth();
		this.imageHeight = bufferedImage.getHeight();
	}

	public Texture(String name, int width, int height, int textureId) {
		this.name = name;
		this.textureId = textureId;
		this.imageWidth = width;
		this.imageHeight = height;
	}
	
	/**
	 * Use if the texture requires additional configurations, for example, if it is meant to be used as a sprite sheet...
	 * @param columns The number of columns.
	 * @param rows The number of rows.
	 * @param frameWidth The width of a single frame.
	 * @param frameHeight The height of a single frame.
	 */
	public void configureSpriteSheet(int columns, int rows, int frameWidth, int frameHeight) {
		this.isSpriteSheet = true;
		this.columns = columns;
		this.rows = rows;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Get the name for this texture (which is set by the user).
	 * @return The name of this texture
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the texture id for this texture (which is set by the machine).
	 * @return The GL texture id
	 */
	public int getTextureId() {
		return textureId;
	}

	/**
	 * @return The pixel width of the texture
	 */
	public int getWidth() {
		return imageWidth;
	}

	/**
	 * @return The pixel height of the texture
	 */
	public int getHeight() {
		return imageHeight;
	}

	/**
	 * Whether or not this texture is configured to be used as a sprite sheet.
	 */
	public boolean isSpriteSheet() {
		return isSpriteSheet;
	}
	
	/**
	 * return The number of sprite sheet columns.
	 */
	public float getColumns() {
		return columns;
	}

	/**
	 * return The number of sprite sheet rows.
	 */
	public float getRows() {
		return rows;
	}

	/**
	 * return The "animation frame width", if this texture is configured to act as a sprite sheet.
	 */
	public float getFrameWidth() {
		return frameWidth;
	}

	/**
	 * return The "animation frame height", if this texture is configured to act as a sprite sheet.
	 */
	public float getFrameHeight() {
		return frameHeight;
	}
	
	/**
	 * Binds the texture to a 3D object.
	 */
	public void bind() {
		//GraphicsGL.setColor2D(Color.WHITE); // disabled. not sure if thats good or bad? only time will tell
		// added a feature to the texture manager, with a toggle, to automatically apply white if desired....
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}

	/**
	 * Unload the object from memory.
	 */
	protected void delete() {
        GL11.glDeleteTextures(textureId);
	}

}