package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.StorageException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ScreenshotMaker {
	
	// Directory with Chromedriver
	private static final String DRIVER_DIR = "tools";
	
	private static final String IMAGE_FORMAT_NAME = "png";
	
	private String storageDir;
	
	public ScreenshotMaker(String storageDir) {
		
		this.storageDir = storageDir;
	}
	
	public String createFromUrl(String urlString) {
		
		WebDriver driver = getWebDriver(urlString);
		
		Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
		                                   .takeScreenshot(driver);
		
		String fileName = createFileName(urlString);
		String relativeFilePath = storageDir + File.separatorChar + fileName;
		File file = new File(relativeFilePath);
		
		try {
			ImageIO.write(screenshot.getImage(), IMAGE_FORMAT_NAME, file);
		} catch (IOException e) {
			String message = String.format("Could not write a file '%s'", relativeFilePath);
			throw new StorageException(message, e);
		}
		
		driver.quit();
		
		return fileName;
	}
	
	private static String createFileName(String urlString) {
		
		return parseUrlString(urlString).concat(getFileExt());
	}
	
	private static String parseUrlString(String urlString) {
		
		return urlString.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "")
		                .replaceAll("\\W|_", "-")
		                .replaceAll("-{2,}", "-")
		                .replaceAll("-$", "");
	}
	
	private static WebDriver getWebDriver(String urlString) {
		
		System.setProperty("webdriver.chrome.driver", DRIVER_DIR + File.separatorChar + "chromedriver");
		WebDriver driver = new ChromeDriver();
		driver.get(urlString);
		new WebDriverWait(driver, 15);
		
		return driver;
	}
	
	private static String getFileExt() {
		
		return "." + IMAGE_FORMAT_NAME;
	}
	
}
