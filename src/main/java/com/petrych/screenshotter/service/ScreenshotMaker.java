package com.petrych.screenshotter.service;

import com.petrych.screenshotter.common.errorhandling.StorageException;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.petrych.screenshotter.common.FileUtil.IMAGE_FORMAT_NAME;
import static com.petrych.screenshotter.service.UrlUtil.parseUrlString;
import static com.petrych.screenshotter.service.WebDriverManager.getWebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class ScreenshotMaker {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotMaker.class);
	
	private static final String INSTAGRAM_USERNAME = "";
	
	private static final String INSTAGRAM_PASSWORD = "";
	
	public static Pair<String, ByteArrayOutputStream> createScreenshotWithNameAndFile(String urlString) {
		
		LOG.debug("Creating a screenshotFile (1/3): Setting a WebDriver...");
		
		WebDriver driver = getWebDriver(urlString);
		
		final String logMessage = "Creating a screenshotFile (2/3): WebDriver created successfully.";
		
		Screenshot screenshotFile = null;
		
		if (urlString.toLowerCase().contains("instagram")) {
			getImageElementForSavedMediaPageOnInstagram(driver, INSTAGRAM_USERNAME, INSTAGRAM_PASSWORD);
			
			LOG.debug(logMessage);
			
			screenshotFile = new AShot().takeScreenshot(driver);
			
		} else {
			LOG.debug(logMessage);
			
			screenshotFile = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
			                            .takeScreenshot(driver);
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			
			ImageIO.write(screenshotFile.getImage(), IMAGE_FORMAT_NAME, baos);
			LOG.debug("Creating a screenshotFile (3/3): Screenshot file written successfully.");
		} catch (IOException e) {
			throw new StorageException(e);
		} finally {
			driver.quit();
		}
		
		String screenshotName = parseUrlString(urlString);
		
		return Pair.of(screenshotName, baos);
	}
	
	private static WebElement getImageElementForSavedMediaPageOnInstagram(WebDriver driver,
	                                                                      String instagramUsername,
	                                                                      String instagramPassword) {
		
		if (instagramUsername == null || instagramUsername.isEmpty() ||
				instagramPassword == null || instagramPassword.isEmpty()) {
			LOG.error("Creating a screenshotFile failed. Instagram username and/or password are not set");
		}
		
		WebDriverWait wait = new WebDriverWait(driver, 20);
		
		// 'Allow all cookies' button
		
		wait.until(elementToBeClickable(By.xpath("//button[text()='Allow essential and optional cookies']")))
		    .click();
		
		// Username and password
		
		wait.until(elementToBeClickable(By.name("username")))
		    .sendKeys(instagramUsername);
		wait.until(elementToBeClickable(By.name("password")))
		    .sendKeys(instagramPassword);
		
		// 'Log In' button
		
		String loginButtonXPath = "//div[text()='Log In']";
		wait.until(elementToBeClickable(By.xpath(loginButtonXPath)))
		    .submit();
		
		// 'Not Now' button
		
		String notNowButtonXPath = "//button[text()='Not now']";
		wait.until(presenceOfElementLocated(By.xpath(notNowButtonXPath)));
		wait.until(elementToBeClickable(By.xpath(notNowButtonXPath)))
		    .click();
		
		// Image
		
		String imageXPath = "//img[contains(@src,\"instagram\")]";
		wait.until(presenceOfElementLocated(By.xpath(imageXPath)));
		
		WebElement image = driver.findElement(By.xpath(imageXPath));
		wait.until(visibilityOf(image));
		
		return image;
	}
	
}
