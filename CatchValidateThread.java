import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CatchValidateThread extends Thread {
	public void run() {

		BufferedImage currentImage;

		try {

			Thread.sleep(5000);

			do {

				currentImage = Main.takeScreenshotDone();

			}
			while(!Main.checkForDone(currentImage));

			Main.logger.info(" found done");
			Main.doneThread = true;
			Main.tensionThread = true;
			this.interrupt();

		}		 catch (IOException | AWTException | InterruptedException e1) {

			e1.printStackTrace();

		}
	}
}