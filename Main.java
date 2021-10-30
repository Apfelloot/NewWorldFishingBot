import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

	private static Robot r;
	public static boolean doneThread = false;
	public static boolean tensionThread = false;
	public static boolean miss = false;
	private static int lastCount;
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static LocalDateTime now = LocalDateTime.now();
	public static int width;
	public static int height;
	

	public static void main(String[] args) {	

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		System.out.println(width + "x" + height);
		
		outerLoop:
		while(true) {

			try {
				
				r = new Robot();

				Point p = MouseInfo.getPointerInfo().getLocation();

				BufferedImage currentImage;

				now = LocalDateTime.now();
				System.out.println(dtf.format(now) + " starting");
				
				do {
					
					currentImage = takeScreenshotStart();
					
				}
				while(!checkForStart(currentImage));
				
				do {
					
					p = MouseInfo.getPointerInfo().getLocation();
					
					if(miss) {

						r.mouseMove((int)p.getX()+2500, (int)p.getY());

					}
					
					r.mouseMove((int)p.getX(), (int)p.getY()+1080);
					Thread.sleep(500);
					r.mousePress(InputEvent.BUTTON1_MASK);
					r.delay(2000);
					r.mouseRelease(InputEvent.BUTTON1_MASK); 
					now = LocalDateTime.now();
					System.out.println(dtf.format(now) + " found start");			
					Thread.sleep(2700);
					currentImage = takeScreenshotMiss();
					
				}
				while(checkForMiss(currentImage));
				
				miss = false;

				Thread.sleep(2000);

				long start = System.currentTimeMillis();
				
				do {

					currentImage = takeScreenshotCatch();
					if(System.currentTimeMillis()-start > 20000) continue outerLoop;
					
				}
				while(!checkForCatch(currentImage));
				
				now = LocalDateTime.now();
				System.out.println(dtf.format(now) + " found catch");

				CatchValidateThread cVThread = new CatchValidateThread();
				cVThread.start();

				do {
					
					TensionValidateThread tVThread = new TensionValidateThread();
					tVThread.start();
					r.mousePress(InputEvent.BUTTON1_MASK);
					while(!tensionThread) {

						Thread.sleep(25);

					}
					r.mouseRelease(InputEvent.BUTTON1_MASK);
					Thread.sleep(1300);
					tensionThread = false;

				}

				while(!doneThread);

				doneThread = false;
				
				now = LocalDateTime.now();
				System.out.println(dtf.format(now) + " end of iteration");

			} catch (IOException | AWTException | InterruptedException e1) {

				e1.printStackTrace();
			}

		}

	}

	public static BufferedImage takeScreenshotMiss() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.34375),(int)(height*0.66203),(int)(width*0.32291),(int)(height*0.04166));
//		Rectangle capture = new Rectangle(660,715,620,45);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotTension() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.41667),(int)(height*0),(int)(width*0.18229),(int)(height*1));
//		Rectangle capture = new Rectangle(800,0,350,1080);
//		System.out.println(((int)(width*0.41667) + " " + (int)(height*0) + " " + (int)(width*0.18229) + " " + (int)(height*100)));
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotCatch() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.36979),(int)(height*0.00926),(int)(width*0.21875),(int)(height*0.17593));
//		Rectangle capture = new Rectangle(710,10,420,190);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotDone() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.54167),(int)(height*0.50926),(int)(width*0.03646),(int)(height*0.05556));
//		Rectangle capture = new Rectangle(1040,550,70,60);
		return r.createScreenCapture(capture);

	}

	public static BufferedImage takeScreenshotStart() throws AWTException {

		Rectangle capture = new Rectangle((int)(width*0.54271),(int)(height*0.69352),(int)(width*0.01823),(int)(height*0.03056));
//		Rectangle capture = new Rectangle(1042,749,35,33);
		return r.createScreenCapture(capture);

	}

	public static boolean checkForStart(BufferedImage bImage) throws IOException {		

		for(int x = 1; x< (int)((width*0.01823)-1) ; x++) {

			for(int y = 1; y<(int)((height*0.03056)-1) ; y++) {

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

		int count = 0;

		for(int x = 1; x< (int)((width*0.32291)-1) ; x++) {

			for(int y = 1; y<(int)((height*0.04166)-1) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();

				if(red > 15 && green > 15 && blue > 15 && red < 25 && green < 25 && blue < 25) count++;

			}

		}

//		System.out.println(count);
		if(count > 100) {
			miss = true;
			return true;
		}
		else return false;
		
	}

	public static boolean checkForCatch(BufferedImage bImage) throws IOException {		

		int count = 0;

		for(int x = 1; x< (int)((width*0.21875)-1) ; x++) {

			for(int y = 1; y<(int)((height*0.17593)-1) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();

				if(red <= 8 && green <= 8 && blue <= 8) count++;

			}

		}
		if((count - lastCount > 80) && lastCount != 0) {
//			System.out.println(count + "/" + lastCount);
			lastCount = 0;
			return true;
		}
		else {
//			System.out.println(count);
			lastCount = count;
			return false;
		}

	}

	public static boolean checkForTension(BufferedImage bImage) throws IOException {		

		for(int x = 1; x< 350-1 ; x++) {

			for(int y = 1; y<1080-1 ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red > 242 && green > 117 && blue > 26 && red < 252 && green < 127 && blue < 36) {
//					System.out.println(red + " " + green + " " + blue + " at " + x + "," + y);
					return true;
				}

			}

		}
		
//		System.out.println(System.currentTimeMillis());

		return false;

	}

	public static boolean checkForDone(BufferedImage bImage) throws IOException {		

		for(int x = 1; x < (int)((width*0.03646)-1) ; x++) {

			for(int y = 1; y < (int)((height*0.05556)-1) ; y++) {

				int pixelB = bImage.getRGB(x, y);
				Color color = new Color(pixelB, true);

				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();  

				if(red > 252 && green > 131 && blue > 67 && red < 258 && green < 137 && blue < 73) return true;

			}

		}
		return false;

	}
}