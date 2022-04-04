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

import static com.petrych.screenshotter.common.FileUtil.IMAGE_FORMAT_NAME;
import static com.petrych.screenshotter.service.UrlUtil.parseUrlString;
import static com.petrych.screenshotter.service.WebDriverManager.getWebDriver;

public class ScreenshotMaker {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotMaker.class);
	
	private String storageDir;
	
	
	public ScreenshotMaker(String storageDir) {
		
		this.storageDir = storageDir;
	}
	
	public String createScreenshotWithNameAndFile(String urlString, String fileName) {
		
		LOG.debug("Creating a screenshotFile (1/3): Setting a WebDriver...");
		
		WebDriver driver = getWebDriver(urlString);
		LOG.info("Creating a screenshotFile (2/3): WebDriver created successfully.");
		
		Screenshot screenshotFile = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
		                                       .takeScreenshot(driver);
		
		String relativeFilePath = storageDir + File.separatorChar + fileName;
		File file = new File(relativeFilePath);
		
		try {
			ImageIO.write(screenshotFile.getImage(), IMAGE_FORMAT_NAME, file);
			LOG.debug("Creating a screenshotFile (3/3): Screenshot file written successfully.");
		} catch (IOException e) {
			String message = String.format("Could not write a file '%s'", relativeFilePath);
			throw new StorageException(message, e);
		} finally {
			driver.quit();
		}
		
		String screenshotName = parseUrlString(urlString);
		
		return screenshotName;
	}
	
}
