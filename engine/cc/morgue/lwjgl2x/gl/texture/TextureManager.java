package cc.morgue.lwjgl2x.gl.texture;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

import cc.morgue.lwjgl2x.Config;
import cc.morgue.lwjgl2x.gl.GraphicsGL;

public class TextureManager {

	private final String CGI_DIRECTORY = System.getProperty("java.io.tmpdir");//"./output/tmpio/"

	public enum FilterType {
		NEAREST, LINEAR, MIPMAP;
	}

	private static TextureManager instance;

	/**
	 * A list of cached textures.
	 */
	private List<Texture> textureCache;

	public TextureManager() {
		File tmpdir = new File(CGI_DIRECTORY);
		if (!tmpdir.exists()) {
			tmpdir.mkdir();
			// TODO loop through directory and delete all files with "cgi-"+"png" in the name?
		}

		if (textureCache == null) {
			this.textureCache = new CopyOnWriteArrayList<Texture>();

			// The engine requires GPU textureID # 0 to be a 1x1 solid white pixel.
			createTexture("white", Color.WHITE, 1, 1, 0);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		if (Config.EXTENDED_VERBOSE) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Textures enabled");
		}
	}

	/**
	 * Used to create a computer generated texture based on the given parameters.
	 * 
	 * @param name The name of the new texture.
	 * @param color The color of the new texture.
	 * @param width The width of the new texture.
	 * @param height The height of the new texture.
	 * @param opacity Opacity level of the texture (<= 0 for no opacity).
	 */
	public void createTexture(String name, Color color, int width, int height, float opacity) {
		// Create a BufferedImage instance.
		BufferedImage colorTexture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Creates a graphics instance.
		Graphics2D graphics2D = colorTexture.createGraphics();	

		// Use the graphics to draw.

		Composite oldComp = graphics2D.getComposite();
		if (opacity > 0) {
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		}
		graphics2D.setColor(color);
		graphics2D.fillRect(0, 0, width, height);
		graphics2D.setComposite(oldComp);

		// Dispose and free the system resources.
		graphics2D.dispose();

		// Generate a file name.
		String textureName = "cgi-" + UUID.randomUUID().toString();

		// Create an empty file.
		File newImageFile = new File(CGI_DIRECTORY, textureName + ".png");

		// Try to stream the buffered image data into the new image file on disk.
		try {
			ImageIO.write(colorTexture, "PNG", newImageFile);
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error generating texture '" + textureName + "'! Output file: " + newImageFile.getAbsolutePath(), e);
			System.exit(1);
		}

		// Create an OpenGL texture using the image data.
		addTexture(name, newImageFile, FilterType.MIPMAP);

		// Delete the new image file from disk.
		newImageFile.delete();
	}

	/**
	 * Creates a new colored texture using the given parameters.
	 * 
	 * @param font The text font.
	 * @param color The text color.
	 * @param text The text.
	 */
	public String createTextTexture(Font font, Color color, String text) {
		// We need to use FontMetrics to determine the texture dimensions.
		Graphics2D graphics2D = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		graphics2D.setFont(font);

		FontMetrics fm = graphics2D.getFontMetrics();

		if (text.contains("probably")) {
			System.out.println(text);
			System.out.println(fm.stringWidth(text));
			System.out.println((int) Math.ceil(fm.getStringBounds(text, graphics2D).getWidth()));
		}
		//int width = 1000;
		int width = fm.stringWidth(text);

		int height = fm.getHeight();

		// Make sure the dimensions are both even numbers,
		// and add an extra pixel if not.
		if ((width % 2) != 0) {
			width++;
		}
		if ((height % 2) != 0) {
			height++;
		}

		// Dispose and free the system resources.
		graphics2D.dispose();

		// Create a BufferedImage instance.
		BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Creates a graphics instance.
		graphics2D = texture.createGraphics();	

		// Use the graphics to draw.
		//Composite oldComp = graphics2D.getComposite();
		//Composite newComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		//graphics2D.setComposite(newComp);
		//graphics2D.setComposite(oldComp);

		boolean sizeDebug = false;
		if (sizeDebug) {
			graphics2D.setColor(Color.BLACK);
			graphics2D.fillRect(0, 0, width, height);
		}

		// Paint the text onto the image.
		graphics2D.setColor(color);
		graphics2D.setFont(font);
		graphics2D.drawString(text, 0, font.getSize());

		// Dispose and free the system resources.
		graphics2D.dispose();

		// Generate a file name.
		String textureName = "cgi-" + UUID.randomUUID().toString();

		// Create an empty file.
		File newImageFile = new File(CGI_DIRECTORY, textureName + ".png");

		// Try to stream the buffered image data into the new image file on disk.
		try {
			ImageIO.write(texture, "PNG", newImageFile);
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error generating texture '" + textureName + "'! Output file: " + newImageFile.getAbsolutePath(), e);
			System.exit(1);
		}

		// Create an OpenGL texture using the image data.
		addTexture(textureName, newImageFile, FilterType.NEAREST);

		// Delete the new image file from disk.
		newImageFile.delete();

		return textureName;
	}

	/**
	 * Converts a system resource into a texture file. Example: TextureManager.getInstance().addTexture("logo", ClassLoader.getSystemResource("media/logo.png"), FilterType.NEAREST);
	 * 
	 * @see #addTexture(String, File, FilterType)
	 */
	public void addTexture(String name, URL url, FilterType type) {
		try {
			addTexture(name, new File(url.toURI()), type);
		} catch (URISyntaxException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to load resource ' " + name + "'", e);
		}
	}

	public void addTexture(String name, String file, FilterType type) {
		addTexture(name, new File(file), type);
	}

	/**
	 * Converts an image file into a texture instance, and caches it in memory for later use.
	 * @param name The texture name (which is used to draw the texture during runtime)
	 * @param image The image file which will be converted into a texture instance
	 */
	public void addTexture(String name, File image, FilterType type) {
		if (contains(name)) {
			if (Config.EXTENDED_VERBOSE) {
				System.err.println("Texture '" + name + "' already exists!");
			}
			return;
		}
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(image);
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to add texture '" + name + "! Input file: " + image.getAbsolutePath(), e);
			System.exit(1);
		}
		ByteBuffer buffer = getBufferFromImageData(bufferedImage);

		final int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		// Setup filtering, i.e. how OpenGL will interpolate the pixels when scaling up or down
		switch (type) {
		case LINEAR:
			// Setup wrap mode, i.e. how OpenGL will handle pixels outside of the expected range
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			break;
		case NEAREST:
			// Setup wrap mode, i.e. how OpenGL will handle pixels outside of the expected range
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			break;
		case MIPMAP:
			// Setup wrap mode, i.e. how OpenGL will handle pixels outside of the expected range
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, 0); // pick mipmap level 7 or lower
			GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA16, bufferedImage.getWidth(), bufferedImage.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			break;
		default:
			break;
		}
		Texture texture = new Texture(name, bufferedImage, textureId);
		texture.setFileName(image.getName());
		textureCache.add(texture);
	}

	/*
	private Texture decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
			buffer.flip();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Texture(buffer, width, height);
	}
	 */

	private ByteBuffer getBufferFromImageData(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int[] pixels = new int[width * height];
		bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
		boolean hasAlpha = ((RenderedImage) bufferedImage).getColorModel().hasAlpha();

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				if (hasAlpha) {
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				} else {
					buffer.put((byte) ((pixel) & 0xFF));
				}
			}
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Draws texture <name> at the given 2D coordinates <x> <y>.
	 * @param name The name of the texture
	 * @param x The 2D x coordinate of the texture
	 * @param y The 2D y coordinate of the texture
	 */
	public void draw(String name, float x, float y) { // XXX changed from int to float on 7/29/2019
		Texture texture = getTexture(name);
		if (texture == null) {
			throw new RuntimeException("Texture '" + name + "' does not exist! It has not been registered.");
		}

		if (Config.FORCE_WHITE_PRE_TEXTURE) {
			GraphicsGL.setColor2D(Color.WHITE);
		}

		GL11.glPushMatrix();
		texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(x + texture.getWidth(), y);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(x + texture.getWidth(), y + texture.getHeight());
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(x, y + texture.getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	/**
	 * Draws texture <name> at the given 2D coordinates <x> <y> and rotates
	 * the texture around it's center, based on the given rotation offset.
	 * @param name The name of the texture
	 * @param x The 2D x coordinate of the texture
	 * @param y The 2D y coordinate of the texture
	 * 
	 * DOES NOT WORK PROPERLY -- IMAGE DOES NOT ROTATE AROUND ITS CENTER POINT :(
	 */
	public void rotateAndDraw(String name, int x, int y, float angle) {
		Texture texture = getTexture(name);
		if (texture == null) {
			throw new RuntimeException("Texture '" + name + "' does not exist! It has not been registered.");
		}

		if (!Config.FORCE_WHITE_PRE_TEXTURE) {
			GraphicsGL.setColor2D(Color.WHITE);
		}
		GL11.glPushMatrix();
		texture.bind();

		GL11.glTranslatef(x, y, 0.0f);
		GL11.glRotatef(angle, 0, 0, 1);
		GL11.glTranslatef(-x, -y, 0.0f); // TODO rotate around center of img

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(x + texture.getWidth(), y);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(x + texture.getWidth(), y + texture.getHeight());
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(x, y + texture.getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	// TODO documentation -- this just returns a texture
	public Texture getTextureFromSpriteSheet(String name, int row, int column) {
		Texture texture = getTexture(name);
		if (texture == null) {
			//throw new RuntimeException("Texture '" + name + "' has not been registered.");
			throw new RuntimeException("Texture '" + name + "' does not exist! It has not been registered.");
		}
		if (!texture.isSpriteSheet()) {
			throw new RuntimeException("Texture '" + name + " has not been configured to function as a sprite sheet.");
		}
		return null;
	}

	/**
	 * Used to draw a partial sprite, using a texture which has been configured to be used as a sprite sheet.
	 * @note This method is intended for 2D games.. If you wish to use it in 3D space, you will need to write a more suitable method.
	 *
	 * @param name The name of the texture.
	 * @param x The X coordinate on the screen, where the texture should be drawn.
	 * @param y The Y coordinate on the screen, where the texture should be drawn.
	 * @param row Row is the horizontal direction (left to right).
	 * @param column Column is the vertical direction (up and down).
	 */
	public void getTextureFromSpriteSheet(String name, int x, int y, int row, int column) {
		Texture texture = getTexture(name);
		if (texture == null) {
			//throw new RuntimeException("Texture '" + name + "' has not been registered.");
			throw new RuntimeException("Texture '" + name + "' does not exist! It has not been registered.");
		}
		if (!texture.isSpriteSheet()) {
			throw new RuntimeException("Texture '" + name + " has not been configured to function as a sprite sheet.");
		}

		if (Config.FORCE_WHITE_PRE_TEXTURE) {
			GraphicsGL.setColor2D(Color.WHITE);
		}
		GL11.glPushMatrix();
		texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			float h, v = 0f;
			float hs = 1 / texture.getColumns();
			float vs = 1 / texture.getRows();
			float width = texture.getFrameWidth();
			float height = texture.getFrameHeight();
			//System.out.println(width + " " + height);

			h = column * hs;
			v = row * vs;

			GL11.glTexCoord2f(h, v);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(h + hs, v);
			GL11.glVertex2f(x + width, y);
			GL11.glTexCoord2f(h + hs, v + vs);
			GL11.glVertex2f(x + width, y + height);
			GL11.glTexCoord2f(h, v + vs);
			GL11.glVertex2f(x, y + height);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	/**
	 * Used to load textures for .obj models.
	 * 
	 * @param name
	 * @param fileName
	 * @param useTextureAlpha
	 * @return
	 * @throws IOException
	 */
	public Texture load(String name, String fileName, boolean useTextureAlpha) throws IOException {
		Texture texture = getTexture(name);
		if (texture != null) {
			return texture;
		}

		File imageFile = new File(fileName);
		if (!imageFile.exists()) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "File '" + fileName + "' does not exist.");
			return null;
		}
		if (!imageFile.canRead()) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "File '" + fileName + "' not readable.");
			return null;
		}

		BufferedImage img = null;
		img = ImageIO.read(imageFile);

		int[] pixels = new int[img.getWidth() * img.getHeight()];
		PixelGrabber grabber = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "InterruptedException while trying to grab pixels!", e);
			return null;
		}

		int bufLen = 0;
		if (useTextureAlpha) {
			bufLen = pixels.length * 4;
		} else {
			bufLen = pixels.length * 3;
		}
		ByteBuffer oglPixelBuf = BufferUtils.createByteBuffer(bufLen);

		for (int y = img.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < img.getWidth(); x++) {
				int pixel = pixels[y * img.getWidth() + x];
				oglPixelBuf.put((byte) ((pixel >> 16) & 0xFF));
				oglPixelBuf.put((byte) ((pixel >> 8) & 0xFF));
				oglPixelBuf.put((byte) ((pixel >> 0) & 0xFF));
				if (useTextureAlpha) {
					oglPixelBuf.put((byte) ((pixel >> 24) & 0xFF));
				}
			}
		}

		oglPixelBuf.flip();

		ByteBuffer temp = ByteBuffer.allocateDirect(4);
		temp.order(ByteOrder.nativeOrder());
		IntBuffer textBuf = temp.asIntBuffer();
		GL11.glGenTextures(textBuf);
		int textureID = textBuf.get(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		// TODO: We take  a flag for whether or not to store the alpha, but if we don't check that flag for the options 
		// below - they assume alpha, and if useTextureAlpha is false we only allocate num pixels * 3, so the 
		// call below then throws an exception.   Long story short either always assuem we want alpha or fix the
		// code below to change those options depending on flag setting.
		if(useTextureAlpha) {
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, oglPixelBuf);
		} else {
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, oglPixelBuf);
		}

		texture = new Texture(name, img.getWidth(), img.getHeight(), textureID);
		texture.setFileName(fileName);
		textureCache.add(texture);
		return texture;
	}

	/**
	 * Returns A texture instance.
	 * @param name The name of the requested texture
	 */
	public Texture getTexture(String name) {
		for (Texture t : textureCache) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Returns A texture instance.
	 * @param textureId The id of the requested texture
	 */
	public Texture getTexture(int textureId) {
		for (Texture t : textureCache) {
			if (t.getTextureId() == textureId) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Returns the <textureId> for texture <name>.
	 * @param name The name of the requested texture
	 */
	public int getTextureId(String name) {
		for (Texture t : textureCache) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t.getTextureId();
			}
		}
		return 0;
	}

	/**
	 * A direct binding method.
	 * @param textureId The texture to bind
	 */
	public void bind(int textureId) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}

	/**
	 * A texture binding method.
	 * @param textureId The texture to bind
	 */
	public void bind(String texture) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId(texture));
	}

	/**
	 * Returns true if texture <name> exists.
	 * @param name The name of the requested texture
	 */
	public boolean contains(String name) {
		for (Texture t : textureCache) {
			if (t.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public void unload(String name) {
		for (Texture t : textureCache) {
			if (t.getName().equalsIgnoreCase(name)) {
				textureCache.remove(t);
				t.delete();
			}
		}
	}

	public void unload(int textureId) {
		for (Texture t : textureCache) {
			if (t.getTextureId() == textureId) {
				textureCache.remove(t);
				t.delete();
			}
		}
	}

	public void cleanup() {
		for (Texture texture : textureCache) {
			texture.delete();
			texture = null;
		}
		textureCache.clear();
	}

	/**
	 * Returns the encapsulated TextureManager instance.
	 */
	public static TextureManager getInstance() {
		if (instance == null) {
			instance = new TextureManager();
		}
		return instance;
	}


}