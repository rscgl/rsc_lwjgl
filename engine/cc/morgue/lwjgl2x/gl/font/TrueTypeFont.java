package cc.morgue.lwjgl2x.gl.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import cc.morgue.lwjgl2x.Config;
import cc.morgue.lwjgl2x.gl.GraphicsGL;
import cc.morgue.lwjgl2x.util.Utils;

/**
 * A TrueType font implementation.
 */
public class TrueTypeFont {

	/** Array that holds necessary information about the font characters */
	private IntObject[] charArray = new IntObject[256];

	/** Map of user defined font characters (Character <-> IntObject) */
	private Map<Character, IntObject> customChars = new HashMap<Character, IntObject>();

	/** Font's size */
	private int fontSize = 0;

	/** Font's height */
	private int fontHeight = 0;

	/** Texture used to cache the font 0-255 characters */
	private int textureId;

	/** Default font texture width */
	private int textureWidth = 256;

	/** Default font texture height */
	private int textureHeight = textureWidth;

	/** A reference to Java's AWT Font that we create our font texture from */
	private Font font;

	/** The font metrics for our Java AWT font */
	private FontMetrics fontMetrics;

	private int correctL = 9;

	private class IntObject {
		/** Character's width */
		public int width;

		/** Character's height */
		public int height;

		/** Character's stored x position */
		public int storedX;

		/** Character's stored y position */
		public int storedY;
	}

	public TrueTypeFont(Font font, char[] additionalChars) {
		this.font = font;
		this.fontSize = font.getSize() + 3;

		createSet(additionalChars);

		fontHeight -= 1;
		if (fontHeight <= 0) {
			fontHeight = 1;
		}
	}

	public TrueTypeFont(Font font) {
		this(font, null);
	}

	public void setCorrection(boolean on) {
		if (on) {
			correctL = 2;
		} else {
			correctL = 0;
		}
	}

	private BufferedImage getFontImage(char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if (Config.USE_ANTIALIASING) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		this.fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch) + 8;
		if (charwidth <= 0) {
			charwidth = 7;
		}
		int charheight = fontMetrics.getHeight() + 3;
		if (charheight <= 0) {
			charheight = fontSize;
		}
		// Create another image holding the character we are creating
		BufferedImage fontImage = new BufferedImage(charwidth, charheight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (Config.USE_ANTIALIASING) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		// XXX experimental composite code
//		Composite original = gt.getComposite();
//		gt.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
//		gt.drawRect(0, 0, charwidth, charheight);
//		gt.setComposite(original);
		
		gt.setFont(font);
		gt.setColor(Color.WHITE);
		int charx = 3;
		int chary = 1;
		gt.drawString(String.valueOf(ch), (charx), (chary) + fontMetrics.getAscent());
		return fontImage;
	}

	private void createSet(char[] customCharsArray) {
		// If there are custom chars then I expand the font texture twice
		if (customCharsArray != null && customCharsArray.length > 0) {
			textureWidth *= 2;
		}
		// In any case this should be done in other way. Texture with size
		// 512x512
		// can maintain only 256 characters with resolution of 32x32. The
		// texture
		// size should be calculated dynamically by looking at character sizes.
		try {
			BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) imgTemp.getGraphics();

			g.setColor(new Color(0, 0, 0, 1));
			g.fillRect(0, 0, textureWidth, textureHeight);

			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;

			int customCharsLength = (customCharsArray != null) ? customCharsArray.length : 0;

			for (int i = 0; i < 256 + customCharsLength; i++) {
				// get 0-255 characters and then custom characters
				char ch = (i < 256) ? (char) i : customCharsArray[i - 256];

				BufferedImage fontImage = getFontImage(ch);
				IntObject newIntObject = new IntObject();
				newIntObject.width = fontImage.getWidth();
				newIntObject.height = fontImage.getHeight();

				if (positionX + newIntObject.width >= textureWidth) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}

				newIntObject.storedX = positionX;
				newIntObject.storedY = positionY;

				if (newIntObject.height > fontHeight) {
					fontHeight = newIntObject.height;
				}

				if (newIntObject.height > rowHeight) {
					rowHeight = newIntObject.height;
				}

				// Draw it here
				g.drawImage(fontImage, positionX, positionY, null);

				positionX += newIntObject.width;

				if (i < 256) { // standard characters
					charArray[i] = newIntObject;
				} else { // custom characters
					customChars.put(new Character(ch), newIntObject);
				}

				fontImage = null;
			}
			textureId = loadImage(imgTemp);
		} catch (Exception e) {
			System.err.println("Failed to create font.");
			e.printStackTrace();
		}
	}

	private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2, float srcY2) {
		float DrawWidth = drawX2 - drawX;
		float DrawHeight = drawY2 - drawY;
		float TextureSrcX = srcX / textureWidth;
		float TextureSrcY = srcY / textureHeight;
		float SrcWidth = srcX2 - srcX;
		float SrcHeight = srcY2 - srcY;
		float RenderWidth = (SrcWidth / textureWidth);
		float RenderHeight = (SrcHeight / textureHeight);

		GL11.glTexCoord2f(TextureSrcX, TextureSrcY);
		GL11.glVertex2f(drawX, drawY + DrawHeight);
		GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
		GL11.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
		GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
		GL11.glVertex2f(drawX + DrawWidth, drawY);
		GL11.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
		GL11.glVertex2f(drawX, drawY);
	}

	public int getWidth(String whatchars) {
		int totalwidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			currentChar = whatchars.charAt(i);
			if (currentChar < 256) {
				intObject = charArray[currentChar];
			} else {
				intObject = (IntObject) customChars.get(new Character((char) currentChar));
			}
			if (intObject != null) {
				totalwidth += intObject.width;
			}
		}
		return totalwidth;
	}

	public int getHeight() {
		return fontHeight;
	}

	public int getHeight(String HeightString) {
		return fontHeight;
	}

	public int getLineHeight() {
		return fontHeight;
	}

	public void drawCenteredString(int w, int y, String string) {
		drawString(w / 2 - (fontMetrics.stringWidth(string) / 2), y, string);
	}

	public void drawString(int x, int y, String string) {
		drawString(x, y, string, 0, string.length() - 1);
	}

	public void drawShadowedString(Color primary, Color shadow, int shadowOffset, int x, int y, String string) {
		GraphicsGL.setColor2D(shadow);
		drawString(x + shadowOffset, y + shadowOffset, string, 0, string.length() - 1);
		GraphicsGL.setColor2D(primary);
		drawString(x, y, string, 0, string.length() - 1);
	}

	private void drawString(float x, float y, String whatchars, int startIndex, int endIndex) {
		IntObject intObject = null;
		int charCurrent;
		int totalwidth = 0;
		int i = startIndex, d, c;
		float startY = 0;
		d = 1;
		c = correctL;
		
		//if (TextureManager.getInstance().useAutomaticClearing()) {
		//	GraphicsGL.setColor2D(Color.WHITE);
		//}

		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		GL11.glBegin(GL11.GL_QUADS);
		while (i >= startIndex && i <= endIndex) {
			charCurrent = whatchars.charAt(i);
			if (charCurrent < 256) {
				intObject = charArray[charCurrent];
			} else {
				intObject = (IntObject) customChars.get(new Character((char) charCurrent));
			}
			if (intObject != null) {
				if (d < 0)
					totalwidth += (intObject.width - c) * d;
				if (charCurrent == '\n') {
					startY -= fontHeight * d;
					totalwidth = 0;
				} else {
					drawQuad((totalwidth + intObject.width) + x, startY + y, totalwidth + x, (startY + intObject.height) + y, intObject.storedX + intObject.width, intObject.storedY + intObject.height, intObject.storedX, intObject.storedY);
					if (d > 0)
						totalwidth += (intObject.width - c) * d;
				}
				i += d;
			}
		}
		GL11.glEnd();
		//GL11.glDisable(GL11.GL_BLEND);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public static int loadImage(BufferedImage bufferedImage) {
		// TODO : store the bufferedimage as a Texture instance
		// in the GLFont class. we need to keep track of font textures,
		// but cannot store them locally in the TTF class..
		try {
			short width = (short) bufferedImage.getWidth();
			short height = (short) bufferedImage.getHeight();
			// textureLoader.bpp = bufferedImage.getColorModel().hasAlpha() ?
			// (byte)32 : (byte)24;
			int bpp = (byte) bufferedImage.getColorModel().getPixelSize();
			ByteBuffer byteBuffer;
			DataBuffer db = bufferedImage.getData().getDataBuffer();
			if (db instanceof DataBufferInt) {
				//System.out.println("TTF: image contains alpha");
				int intI[] = ((DataBufferInt) (bufferedImage.getData().getDataBuffer())).getData();
				byte newI[] = new byte[intI.length * 4];
				for (int i = 0; i < intI.length; i++) {
					byte b[] = intToByteArray(intI[i]);
					int newIndex = i * 4;
					newI[newIndex] = b[1];
					newI[newIndex + 1] = b[2];
					newI[newIndex + 2] = b[3];
					newI[newIndex + 3] = b[0];
				}
				byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(newI);
			} else {
				byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(((DataBufferByte) (bufferedImage.getData() .getDataBuffer())).getData());
			
			}
			// TODO this may not be necessary if we.. just export the JAR from a JDK 1.6
			if (Utils.getJavaVersion() < 1.9) {
				ByteBuffer fix = (ByteBuffer) byteBuffer.flip();
				byteBuffer = (ByteBuffer) fix;
			} else {
				byteBuffer.flip();
			}
			IntBuffer textureId = BufferUtils.createIntBuffer(1);
			GL11.glGenTextures(textureId);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId.get(0));
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
			return textureId.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return -1;
	}

	public static boolean isSupported(String fontname) {
		Font font[] = getFonts();
		for (int i = font.length - 1; i >= 0; i--) {
			if (font[i].getName().equalsIgnoreCase(fontname)) {
				return true;
			}
		}
		return false;
	}

	public int getFontSize() {
		return fontSize;
	}

	public static Font[] getFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}

	public static byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

	public FontMetrics getMetrics() {
		return fontMetrics;
	}

	/**
	 * Unload from memory.
	 */
	public void unload() {
		GL11.glDeleteTextures(textureId);
	}

}