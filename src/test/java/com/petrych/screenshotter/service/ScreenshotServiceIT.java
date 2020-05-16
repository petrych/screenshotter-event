package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.model.Screenshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class ScreenshotServiceIT {
	
	@Autowired
	private IScreenshotService screenshotService;
	
	@Value("${storage.location}")
	private String storageDir;
	
	private static final String URL_VALID = "https://www.apple.com/";
	
	@Test
	public void givenScreenshotsExist_whenfindAll_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findAll();
		
		assertTrue(screenshots.size() > 0);
		assertTrue(screenshots.get(0).getUri().contains(".png"));
	}
	
	@Test
	public void givenScreenshotExists_whenfindById_thenSuccess() {
		
		Screenshot screenshot = screenshotService.findById(4L).get();
		
		assertEquals(4L, (long) screenshot.getId());
	}
	
	@Test
	public void givenScreenshotExists_whenfindByName_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findByName("screen");
		
		assertTrue(screenshots.get(0).getName().contains("screen"));
		assertTrue(screenshots.get(0).getUri().contains(".png"));
	}
	
	@Test
	public void givenFileExists_whenGetScreenshotFileById_thenSuccess() {
		
		File screenshotFile = screenshotService.getScreenshotFileById(4L);
		
		assertTrue(screenshotFile.getName().contains("screenshot-1"));
	}
	
	@Test
	public void givenFilesExist_whenLoadAllFiles_thenSuccess() {
		
		Stream<Path> pathStream = screenshotService.loadAllFiles();
		
		assertTrue(pathStream.count() >= 1L);
	}
	
	@Test
	void givenValidUrl_whenStore_thenSuccess() throws IOException {
		
		String fileName = screenshotService.storeFile(URL_VALID);
		Path filePath = Paths.get(storageDir, fileName);
		
		assertFalse(fileName.isEmpty());
		assertTrue(Files.exists(filePath));
		
		// Clean up after the test
		Files.delete(filePath);
		assertFalse(Files.exists(Paths.get(storageDir, fileName)));
	}
	
}
