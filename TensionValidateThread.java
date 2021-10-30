import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

			Main.now = LocalDateTime.now();
			System.out.println(Main.dtf.format(Main.now) + " found tension warning");
			Main.tensionThread = true;
			this.interrupt();

		}		 catch (IOException | AWTException e1) {

			e1.printStackTrace();
		}
	}
}