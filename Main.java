import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;

import java.awt.event.KeyEvent;

public class Main {

	private static Robot r;
	public static boolean doneThread;
	public static boolean tensionThread;
	public static boolean miss;
	public static int width;
	public static int height;
	public static long lastStartTime;
	public static Logger logger;
	public static double factorPixel;
	public static int initialBlackPixelCount;
	public static long timeSinceLastCheck;


	public static void main(String[] args) {	

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();

		int amountPixelFullhd = 1920*1080;
		int amountPixelCurrentPc = width*height;
		factorPixel = amountPixelCurrentPc / amountPixelFullhd;

		logger = Logger.getLogger("MyLog");  
		FileHandler fh;  

		try {  

			fh = new FileHandler("MyLogFile.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  

		logger.info(width + "x" + height);

		outerLoop:
			while(true) {


				miss = false;
				doneThread = false;
				tensionThread = false;
				initialBlackPixelCount = 0;
				timeSinceLastCheck = 0;
				lastStartTime = System.currentTimeMillis();

				try {

					r = new Robot();

					Point p = MouseInfo.getPointerInfo().getLocation();

					BufferedImage currentImage;

					logger.info("starting");

					do {

						currentImage = takeScreenshotStart();
						if(System.currentTimeMillis()-lastStartTime > 70000) {
							logger.info("unexpected restart");
							continue outerLoop;
						}
					}
					while(!checkForStart(currentImage));

					if(checkForRepair(takeScreenshotRepair())) {

						r.keyPress(KeyEvent.VK_TAB);
						r.keyRelease(KeyEvent.VK_TAB);
						Thread.sleep(1000);
						if(checkForInventory(takeScreenshotInventory())) {
							r.mouseMove(870, 670);
							Thread.sleep(1000);
							r.keyPress(KeyEvent.VK_R);
							Thread.sleep(1000);
							r.mousePress(InputEvent.BUTTON1_MASK);
							r.mouseRelease(InputEvent.BUTTON1_MASK);
							Thread.sleep(1000);
							r.keyRelease(KeyEvent.VK_R);
							Thread.sleep(1000);
							r.keyPress(KeyEvent.VK_E);
							r.keyRelease(KeyEvent.VK_E);
							Thread.sleep(1000);
							r.keyPress(KeyEvent.VK_TAB);
							r.keyRelease(KeyEvent.VK_TAB);
							Thread.sleep(1000);

							logger.info("repaired fishing rod");
						}

						else {

							r.keyPress(KeyEvent.VK_TAB);
							r.keyRelease(KeyEvent.VK_TAB);
							Thread.sleep(1000);
							logger.info("couldnt open inventory");
						}

						r.keyPress(KeyEvent.VK_F3);
						r.keyRelease(KeyEvent.VK_F3);
						Thread.sleep(1000);

					}

					logger.info(" found start");	

					do {

						p = MouseInfo.getPointerInfo().getLocation();

						if(miss) {

							r.mouseMove((int)p.getX()+2500, (int)p.getY());

						}

						r.mouseMove((int)p.getX(), (int)p.getY()+1080);
						Thread.sleep(500);
						r.mousePress(InputEvent.BUTTON1_MASK);
						Thread.sleep(2000);
						r.mouseRelease(InputEvent.BUTTON1_MASK); 

						Thread.sleep(3000);
						currentImage = takeScreenshotStart();

						//					File outputfile = new File("image.jpg");
						//					ImageIO.write(currentImage, "jpg", outputfile);

						if(System.currentTimeMillis()-lastStartTime > 70000) {
							logger.info("unexpected restart");
							continue outerLoop;
						}
					}
					while(checkForMiss(currentImage));

					miss = false;

					long start = System.currentTimeMillis();

					currentImage = takeScreenshotCatch();
					checkInitialBlackPixelCount(currentImage);

					timeSinceLastCheck = System.currentTimeMillis();

					do {

						currentImage = takeScreenshotCatch();
						if(System.currentTimeMillis()-start > 30000) continue outerLoop;
						if(System.currentTimeMillis()-lastStartTime > 70000) {
							logger.info("unexpected restart");
							continue outerLoop;
						}

					}
					while(!checkForCatch(currentImage));

					logger.info(" found catch");

					Thread.sleep(200);

					CatchValidateThread cVThread = new CatchValidateThread();
					cVThread.start();

					do {

						TensionValidateThread tVThread = new TensionValidateThread();
						tVThread.start();
						r.mousePress(InputEvent.BUTTON1_MASK);
						while(!tensionThread) {

							Thread.sleep(25);
							if(System.currentTimeMillis()-lastStartTime > 70000) {
								logger.info("unexpected restart");
								tVThread.stop();
								cVThread.stop();
								continue outerLoop;
							}

						}
						r.mouseRelease(InputEvent.BUTTON1_MASK);
						Thread.sleep(1300);
						tensionThread = false;
						if(System.currentTimeMillis()-lastStartTime > 70000) {
							logger.info("unexpected restart");
							tVThread.stop();
							cVThread.stop();
							continue outerLoop;
						}
						tVThread.stop();

					}

					while(!doneThread);

					doneThread = false;

					cVThread.stop();
					logger.info(" end of iteration");

				} catch (IOException | AWTException | InterruptedException e1) {

					e1.printStackTrace();
				}

			}

	}

	public static BufferedImage takeScreenshotMiss() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.34375),(int)(height*0.66703),(int)(width*0.32291)+1,(int)(height*0.04166)+1);
		//		Rectangle capture = new Rectangle(660,715,620,45);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotInventory() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.27500),(int)(height*0.87870),(int)(width*0.19219)+1,(int)(height*0.01204)+1);
		//		Rectangle capture = new Rectangle(528,949,369,13);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotRepair() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.88020),(int)(height*0.95648),(int)(width*0.09166)+1,(int)(height*0.00185)+1);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotTension() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.41667),(int)(height*0),(int)(width*0.18229)+1,(int)(height*1)+1);
		//		Rectangle capture = new Rectangle(800,0,350,1080);
		//		System.out.println(((int)(width*0.41667) + " " + (int)(height*0) + " " + (int)(width*0.18229) + " " + (int)(height*100)));
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotCatch() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.50521),0,(int)(width*0.02604)+1,(int)(height*0.12037)+1);
		//		Rectangle capture = new Rectangle(870,0,170,140);
		//		Rectangle capture = new Rectangle(970,0,50,130);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotDone() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.54167),(int)(height*0.50926),(int)(width*0.03646)+1,(int)(height*0.05556)+1);
		//		Rectangle capture = new Rectangle(1040,550,70,60);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotStart() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.54271),(int)(height*0.69352),(int)(width*0.01823)+1,(int)(height*0.03056)+1);
		//		Rectangle capture = new Rectangle(1042,749,35,33);
		return r.createScreenCapture(capture);

	}

	public static boolean checkForInventory(BufferedImage bImage) throws IOException {		

		int counterYellow = 0;
		int counterTotal = 0;

							File outputfile = new File("image.jpg");
							ImageIO.write(bImage, "jpg", outputfile);
		
		for(int x = 1; x<= (int)(width*0.19219) ; x++) {

			for(int y = 1; y<=(int)(height*0.01204) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red > 121 && green > 107 && blue > 42 && red < 224 && green < 195 && blue < 83) {

					counterYellow++;

				}
				counterTotal++;

			}

		}

		if(( (double)counterYellow/(double)counterTotal ) >= 0.6) {

			return true;

		}

		else {
			return false;
		}

	}

	public static boolean checkForRepair(BufferedImage bImage) throws IOException {		

		int counterWhite = 0;
		int counterTotal = 0;

		for(int x = 1; x<= (int)(width*0.09166) ; x++) {

			for(int y = 1; y<=(int)(height*0.00185) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red == 255 && green == 255 && blue == 255) counterWhite++;
				counterTotal++;

			}

		}

		if(counterWhite > 0 && (((double)counterWhite/(double)counterTotal) <= 0.05)) {

			return true;

		}

		else {

			return false;
		}

	}

	public static boolean checkForStart(BufferedImage bImage) throws IOException {		

		for(int x = 1; x<= (int)(width*0.01823) ; x++) {

			for(int y = 1; y<=(int)(height*0.03056) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red <= 10 && green <= 10 && blue <= 10) return true;

			}

		}

		return false;

	}

	public static boolean checkForMiss(BufferedImage bImage) throws IOException {

		for(int x = 1; x<= (int)(width*0.01823) ; x++) {

			for(int y = 1; y<=(int)(height*0.03056) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red <= 10 && green <= 10 && blue <= 10) {
					miss = true;
					return true;

				}
			}

		}

		return false;

	}

	public static boolean checkForCatch(BufferedImage bImage) throws IOException {		

		int count = 0;

		for(int x = 1; x<= (int)(width*0.02604) ; x++) {

			for(int y = 1; y<=(int)(height*0.12037) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();

				if(red <= 12 && green <= 12 && blue <= 12) count++;

			}

		}
		if((count - initialBlackPixelCount) > (120*factorPixel)) {
			logger.info(count + "-" + initialBlackPixelCount + "="+ (count-initialBlackPixelCount) +">"+ 120*factorPixel);
			File outputfile = new File("image.jpg");
			ImageIO.write(bImage, "jpg", outputfile);
			return true;
		}
		else {

			return false;

		}

	}

	public static void checkInitialBlackPixelCount(BufferedImage bImage) throws IOException {		

		int count = 0;

		for(int x = 1; x<= (int)(width*0.02604) ; x++) {

			for(int y = 1; y<=(int)(height*0.12037) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();

				if(red <= 15 && green <= 15 && blue <= 15) count++;

			}

		}

		initialBlackPixelCount = count;

	}

	public static boolean checkForTension(BufferedImage bImage) throws IOException {		

		for(int x = 1; x<= (int)(width*0.18229) ; x++) {

			for(int y = 1; y<=height ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red > 242 && green > 117 && blue > 25 && red < 252 && green < 127 && blue < 36) {
					//					System.out.println(red + " " + green + " " + blue + " at " + x + "," + y);
					return true;
				}

			}

		}

		return false;

	}

	public static boolean checkForDone(BufferedImage bImage) throws IOException {		

		for(int x = 1; x <=(int)(width*0.03646) ; x++) {

			for(int y = 1; y <= (int)(height*0.05556) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red > 237 && green > 116 && blue > 52 && red < 258 && green < 137 && blue < 73) {

					logger.info("done: " + red + "-" + green + "-" + blue + " at " + x + "/" + y);
					return true;

				}

			}

		}
		return false;

	}
}