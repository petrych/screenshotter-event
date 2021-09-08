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
import java.net.HttpURLConnection;
import java.net.URL;

public class ScreenshotMaker {
	
	// Directory with Chromedriver
	private static final String DRIVER_DIR = "tools";
	
	private static final String IMAGE_FORMAT_NAME = "png";
	
	private String storageDir;
	
	public ScreenshotMaker(String storageDir) {
		
		this.storageDir = storageDir;
	}
	
	public String createFromUrl(String urlString) throws InvalidURLException {
		
		isUrlValid(urlString);
		
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
		} finally {
			driver.quit();
		}
		
		return fileName;
	}
	
	public static String createFileName(String urlString) throws InvalidURLException {
		
		return parseUrlString(urlString).concat(getFileExt());
	}
	
	private static String parseUrlString(String urlString) throws InvalidURLException {
		
		isUrlValid(urlString);
		
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
	
	private static boolean isUrlValid(String urlString) throws InvalidURLException {
		
		try {
			URL siteURL = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000);
			connection.connect();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			}
			
		} catch (IOException e) {
			throw new InvalidURLException("Invalid URL", e.getCause());
		}
		
		return false;
	}
	
}
