import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TensionValidateThread extends Thread {
	public void run() {

		BufferedImage currentImage;

		try {

			do {

				currentImage = Main.takeScreenshotTension();

			}
			while(!Main.checkForTension(currentImage));

			Main.logger.info(" found tension warning");
			Main.tensionThread = true;
			this.interrupt();

		}		 catch (IOException | AWTException e1) {

			e1.printStackTrace();
		}
	}
}