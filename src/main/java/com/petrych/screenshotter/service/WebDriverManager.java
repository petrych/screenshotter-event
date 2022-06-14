package com.petrych.screenshotter.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

class WebDriverManager {
	
	static WebDriver getWebDriver(String urlString) {
		
		io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
		
		WebDriver driver = new ChromeDriver(getChromeOptions());
		
		driver.get(urlString);
		new WebDriverWait(driver, 15);
		
		return driver;
	}
	
	private static ChromeOptions getChromeOptions() {
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox"); // Bypass OS security model
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--headless"); // should be disabled for Instagram urls because of NoSuchElementException for 'Not now' [Instagram] button
		options.addArguments("--start-maximized");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-extensions");
		options.addArguments("--verbose");
		
		return options;
	}
	
}
