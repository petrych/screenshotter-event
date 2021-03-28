package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.model.Screenshot;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collection;
import java.util.stream.Stream;

import static com.petrych.screenshotter.common.FileUtil.copyFolder;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ScreenshotServiceIT {
	
	@Autowired
	private IScreenshotService screenshotService;
	
	@Value("${storage.location}")
	private String storageDir;
	
	private static Path storageDirPath;
	
	private static final String imagesDir = "test-images";
	
	private static final String URL_VALID = "https://www.apple.com/";
	
	private static final String URL_FOR_EXISTING_SCREENSHOT = "https://www.drive.google.com/";
	
	private static final String URL_INVALID = "https://www.sdfghy878.qq/";
	
	
	@BeforeEach
	public void setUp() throws IOException {
		
		Path target = Paths.get(storageDir);
		storageDirPath = target;
		
		// create folder with images for testing if it doesn't exist
		if (!Files.exists(target)) {
			
			Path source = Paths.get(imagesDir);
			copyFolder(source, target);
		}
		
		assertTrue(Files.isDirectory(target));
	}
	
	@AfterAll
	public static void done() throws IOException {
		
		FileUtils.deleteDirectory(storageDirPath.toFile());
		
		assertTrue(Files.notExists(storageDirPath));
	}
	
	@Test
	public void givenScreenshotsExist_whenfindAll_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findAll();
		
		assertTrue(screenshots.size() > 0);
		assertTrue(screenshots.get(0).getUri().matches(".+\\d+$"));
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
		assertTrue(screenshots.get(0).getUri().matches(".+\\d+$"));
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
	void givenValidUrl_whenStore_thenSuccess() throws InvalidURLException, IOException {
		
		String fileName = screenshotService.storeFile(URL_VALID);
		Path filePath = Paths.get(storageDir, fileName);
		
		assertFalse(fileName.isEmpty());
		assertTrue(Files.exists(filePath));
		
		// Clean up after the test
		Files.delete(filePath);
		assertFalse(Files.exists(Paths.get(storageDir, fileName)));
	}
	
	@Test
	void givenScreenshotWithUrlExists_whenFindFileNameByUrl_thenSuccess() throws InvalidURLException {
		
		String fileName = ScreenshotMaker.createFileName(URL_FOR_EXISTING_SCREENSHOT);
		Path filePath = Paths.get(storageDir, fileName);
		
		assertEquals(fileName, screenshotService.findFileNameByUrl(URL_FOR_EXISTING_SCREENSHOT));
		assertTrue(Files.exists(filePath));
	}
	
	@Test
	void givenScreenshotWithUrlExists_whenUpdate_thenUpdateExistingScreenshot() throws InvalidURLException {
		
		String fileName = screenshotService.storeFile(URL_VALID);
		Path filePath = Paths.get(storageDir, fileName);
		int screenshotsTotalBefore = ((Collection<Screenshot>) screenshotService.findAll()).size();
		ArrayList<Screenshot> screenshotsBeforeUpd = new ArrayList<>(
				(Collection<? extends Screenshot>) screenshotService.findByName(fileName));
		
		Screenshot screenshotBefore = screenshotsBeforeUpd.get(0);
		
		assertFalse(screenshotService.findFileNameByUrl(URL_VALID).isEmpty());
		assertTrue(Files.exists(filePath));
		
		screenshotService.update(URL_VALID);
		int screenshotsTotalAfter = ((Collection<Screenshot>) screenshotService.findAll()).size();
		
		ArrayList<Screenshot> screenshotsAfterUpd = new ArrayList<>(
				(Collection<? extends Screenshot>) screenshotService.findByName(fileName));
		
		Screenshot screenshotAfter = screenshotsAfterUpd.get(0);
		
		assertTrue(Files.exists(filePath));
		assertEquals(screenshotsTotalBefore, screenshotsTotalAfter);
		assertEquals(screenshotBefore.getUri(), screenshotAfter.getUri());
		assertTrue(screenshotBefore.getDateCreated().isBefore(screenshotAfter.getDateCreated()));
	}
	
	@Test
	void givenScreenshotWithUrlNotExists_whenUpdate_thenCreateNewScreenshot() throws InvalidURLException, IOException {
		
		String fileName = ScreenshotMaker.createFileName(URL_VALID);
		Path filePath = Paths.get(storageDir, fileName);
		
		int screenshotsTotalBefore = ((Collection<Screenshot>) screenshotService.findAll()).size();
		ArrayList<Screenshot> screenshotsBeforeUpd = new ArrayList<>(
				(Collection<? extends Screenshot>) screenshotService.findByName(fileName));
		
		screenshotService.update(URL_VALID);
		
		int screenshotsTotalAfter = ((Collection<Screenshot>) screenshotService.findAll()).size();
		
		Assertions.assertTrue(screenshotsTotalAfter > screenshotsTotalBefore);
		assertFalse(fileName.isEmpty());
		assertEquals(fileName, screenshotService.findFileNameByUrl(URL_VALID));
		assertTrue(Files.exists(filePath));
		
		// Clean up after the test
		Files.delete(filePath);
		assertFalse(Files.exists(Paths.get(storageDir, fileName)));
	}
	
	@Test
	void givenInvalidUrl_whenStore_thenInvalidURLException() {
		
		assertThrows(InvalidURLException.class, () -> {
			screenshotService.storeFile(URL_INVALID);
		});
	}
	
}
