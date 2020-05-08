package com.petrych.screenshotter;

import com.petrych.screenshotter.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ScreenshotterApp {
	
	public static void main(String[] args) {
		
		SpringApplication.run(ScreenshotterApp.class, args);
	}
	
}
