package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.StorageException;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
	
	private static final String CHROMEDRIVER_BIN_LOCATION_APP = "tools";
	
	public static final String CHROMEDRIVER_BIN_LOCATION_LINUX = "/usr/bin";
	
	public static final String CHROMEDRIVER_BIN_NAME = "chromedriver";
	
	public static final String CHROMEDRIVER_PROPERTY_NAME = "webdriver.chrome.driver";
	
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
	
	private static WebDriver getWebDriver(String urlString) {
		
		setChromeDriverBinaryLocation();
		
		System.setProperty("webdriver.chrome.whitelistedIps", "172.17.0.2, 172.17.0.3");
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox"); // Bypass OS security model
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--headless");
		options.addArguments("--start-maximized");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-extensions");
		options.addArguments("--verbose");
		
		WebDriver driver = new ChromeDriver(options);
		
		driver.get(urlString);
		new WebDriverWait(driver, 15);
		
		return driver;
	}
	
	private static void setChromeDriverBinaryLocation() {
		
		String binLocation = CHROMEDRIVER_BIN_LOCATION_APP + File.separatorChar + CHROMEDRIVER_BIN_NAME;
		
		if (SystemUtils.IS_OS_LINUX) {
			
			binLocation = CHROMEDRIVER_BIN_LOCATION_LINUX + File.separatorChar + CHROMEDRIVER_BIN_NAME;
			
		}
		
		System.setProperty(CHROMEDRIVER_PROPERTY_NAME, binLocation);
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
