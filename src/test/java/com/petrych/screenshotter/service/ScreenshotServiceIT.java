package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.model.Screenshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@SpringBootTest
public class ScreenshotServiceIT {
	
	@Autowired
	private IScreenshotService screenshotService;
	
	@Test
	public void givenScreenshotsExist_whenfindAll_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findAll();
		
		assertThat(screenshots, is(notNullValue()));
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
	
}
