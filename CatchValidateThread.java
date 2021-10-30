import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

			Main.now = LocalDateTime.now();
			System.out.println(Main.dtf.format(Main.now) + " found done");
			Main.doneThread = true;
			Main.tensionThread = true;
			this.interrupt();

		}		 catch (IOException | AWTException | InterruptedException e1) {

			e1.printStackTrace();

		}
	}
}