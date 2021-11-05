package com.petrych.screenshotter.service;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class WebDriverManager {
	
	private static final String CHROMEDRIVER_BIN_LOCATION_APP = "tools";
	
	private static final String CHROMEDRIVER_BIN_LOCATION_LINUX = "/usr/bin";
	
	private static final String CHROMEDRIVER_BIN_NAME = "chromedriver";
	
	private static final String CHROMEDRIVER_PROPERTY_NAME = "webdriver.chrome.driver";
	
	private static final String CHROMEDRIVER_WHITELISTED_IPS_PROPERTY_NAME = "webdriver.chrome.whitelistedIps";
	
	private static final String CHROMEDRIVER_WHITELISTED_IPS = "172.17.0.2, 172.17.0.3";
	
	private static final Logger LOG = LoggerFactory.getLogger(WebDriverManager.class);
	
	
	static WebDriver getWebDriver(String urlString) {
		
		setChromeDriverBinaryLocation();
		
		System.setProperty(CHROMEDRIVER_WHITELISTED_IPS_PROPERTY_NAME, CHROMEDRIVER_WHITELISTED_IPS);
		
		WebDriver driver = new ChromeDriver(getChromeOptions());
		
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
		LOG.info("ChromeDriver binary location set to '{}'", binLocation);
	}
	
	private static ChromeOptions getChromeOptions() {
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox"); // Bypass OS security model
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--headless");
		options.addArguments("--start-maximized");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-extensions");
		options.addArguments("--verbose");
		
		return options;
	}
	
}
