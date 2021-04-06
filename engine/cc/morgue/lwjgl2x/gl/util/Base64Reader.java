package cc.morgue.lwjgl2x.gl.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class Base64Reader {

	public static BufferedImage base64ToImageFile(String data) throws IOException {
		// Split the encoded base64 string.
		// We only need the image data.
		data = data.split(",")[1];

		byte[] imageData = DatatypeConverter.parseBase64Binary(data);

		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));

		return bufferedImage;
	}

}
