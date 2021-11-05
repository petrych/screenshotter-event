package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.StorageException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static com.petrych.screenshotter.service.UrlUtil.parseUrlString;
import static com.petrych.screenshotter.service.WebDriverManager.getWebDriver;

public class ScreenshotMaker {
	
	private static final String IMAGE_FORMAT_NAME = "png";
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotMaker.class);
	
	private String storageDir;
	
	
	public ScreenshotMaker(String storageDir) {
		
		this.storageDir = storageDir;
	}
	
	public String createFromUrl(String urlString) throws InvalidURLException {
		
		LOG.debug("Creating a screenshot (1/3): Setting a WebDriver...");
		
		WebDriver driver = getWebDriver(urlString);
		LOG.info("Creating a screenshot (2/3): WebDriver created successfully.");
		
		Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
		                                   .takeScreenshot(driver);
		
		String fileName = createFileName(urlString);
		String relativeFilePath = storageDir + File.separatorChar + fileName;
		File file = new File(relativeFilePath);
		
		try {
			ImageIO.write(screenshot.getImage(), IMAGE_FORMAT_NAME, file);
			LOG.debug("Creating a screenshot (3/3): Screenshot file written successfully.");
		} catch (IOException e) {
			String message = String.format("Could not write a file '%s'", relativeFilePath);
			throw new StorageException(message, e);
		} finally {
			driver.quit();
		}
		
		return fileName;
	}
	
	public static String createFileName(String urlString) throws InvalidURLException {
		
		String fileName = parseUrlString(urlString);
		String fileExtension = getFileExtension();
		
		return fileName.concat(fileExtension);
	}
	
	private static String getFileExtension() {
		
		return "." + IMAGE_FORMAT_NAME;
	}
	
}
