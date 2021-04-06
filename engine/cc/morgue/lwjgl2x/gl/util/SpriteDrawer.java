package cc.morgue.lwjgl2x.gl.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SpriteDrawer extends JPanel {

	private Image image = null;

	public static void main(String[] args) {
		new SpriteDrawer();
	}

	public SpriteDrawer() {
		JFrame frame = new JFrame("Sprite Drawer");
		frame.add(this);
		frame.setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		frame.setVisible(true);
		try {
			List<String> base64 = Files.readAllLines(Paths.get(this.getClass().getResource("/media/b64.txt").toURI()), Charset.defaultCharset());
			String test = base64.get(0);
			//image = ImageIO.read(new File("..//images//sprite.png"));
			image = Base64Reader.base64ToImageFile(test);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.repaint();
	}

	@Override
	public void paint(Graphics graphics) {
		((Graphics2D) graphics).drawImage(image, 0, 0, image.getWidth(this), image.getHeight(this), null);
	}

}
