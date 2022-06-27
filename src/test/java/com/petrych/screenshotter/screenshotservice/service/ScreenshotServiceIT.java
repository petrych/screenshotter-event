package com.petrych.screenshotter.service;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.petrych.screenshotter.persistence.model.Screenshot;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import static com.petrych.screenshotter.common.FileUtil.copyFolder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/data.sql")
public class ScreenshotServiceIT {
	
	@Autowired
	private IScreenshotService screenshotService;
	
	@Value("${storage-dir}")
	private String storageDir;
	
	private static Path storageDirPath;
	
	private static final String imagesDir = "test-images";
	
	private static final String URL_VALID = "https://www.apple.com/";
	
	private static final String URL_VALID_NON_EXISTING = "https://www.gmail.com/";
	
	private static final String URL_FOR_EXISTING_SCREENSHOT = "https://www.drive.google.com/";
	
	private static final String URL_UNREACHABLE = "https://www.sdfghy878.qq/";
	
	
	@BeforeEach
	public void setUp() throws IOException {
		
		Path target = Paths.get(storageDir);
		storageDirPath = target;
		
		// create folder with images for testing if it doesn't exist
		if (!Files.exists(target) || target.toFile().listFiles().length == 0) {
			
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
	}
	
	@Test
	public void givenScreenshotExists_whenfindById_thenSuccess() {
		
		Screenshot screenshot = screenshotService.findById(1L).get();
		
		assertEquals(1L, (long) screenshot.getId());
	}
	
	@Test
	public void givenScreenshotExists_whenfindByName_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findByName("screen");
		
		assertTrue(screenshots.get(0).getName().contains("screen"));
	}
	
	@Test
	public void givenFileExists_whenGetScreenshotFileById_thenSuccess() throws IOException {
		
		byte[] screenshotFile = screenshotService.getScreenshotFileById(1L);
		
		assertTrue(screenshotFile.length > 0);
	}
	
	@Test
	public void givenFilesExist_whenLoadAllFiles_thenSuccess() {
		
		Iterable<Screenshot> allScreenshots = screenshotService.findAll();
		
		assertTrue(Iterables.size(allScreenshots) > 0);
	}
	
	@Test
	void givenValidUrl_whenStore_thenSuccess() throws IOException {
		
		Screenshot screenshot = screenshotService.storeScreenshot(URL_VALID);
		String fileName = screenshot.getFileName();
		Path filePath = Paths.get(storageDir, fileName);
		
		assertFalse(fileName.isEmpty());
		assertTrue(Files.exists(filePath));
		
		// Clean up after the test
		Files.delete(filePath);
		assertFalse(Files.exists(Paths.get(storageDir, fileName)));
	}
	
	@Test
	void givenScreenshotWithUrlExists_whenFindFileNameByUrl_thenSuccess() throws MalformedURLException {
		
		String fileNameExpected = "3.png";
		Path filePath = Paths.get(storageDir, fileNameExpected);
		
		String fileNameActual = screenshotService.findScreenshotFileNamesByUrl(
				URL_FOR_EXISTING_SCREENSHOT).iterator().next();
		
		assertEquals(fileNameExpected, fileNameActual);
		assertTrue(Files.exists(filePath));
	}
	
	@Test
	void givenScreenshotWithUrlExists_whenUpdate_thenUpdateExistingScreenshot() throws IOException {
		
		Screenshot screenshot = screenshotService.storeScreenshot(URL_VALID);
		String fileName = screenshot.getFileName();
		Path filePath = Paths.get(storageDir, fileName);
		int screenshotsTotalBefore = ((Collection<Screenshot>) screenshotService.findAll()).size();
		ArrayList<Screenshot> screenshotsBeforeUpd = new ArrayList<>(
				(Collection<? extends Screenshot>) screenshotService.findByName(screenshot.getName()));
		
		Screenshot screenshotBefore = screenshotsBeforeUpd.get(0);
		
		assertFalse(screenshotService.findScreenshotFileNamesByUrl(URL_VALID).isEmpty());
		assertTrue(Files.exists(filePath));
		
		screenshotService.updateScreenshot(URL_VALID);
		int screenshotsTotalAfter = ((Collection<Screenshot>) screenshotService.findAll()).size();
		
		ArrayList<Screenshot> screenshotsAfterUpd = new ArrayList<>(
				(Collection<? extends Screenshot>) screenshotService.findByName(screenshot.getName()));
		
		Screenshot screenshotAfter = screenshotsAfterUpd.get(0);
		
		assertTrue(Files.exists(filePath));
		assertEquals(screenshotsTotalBefore, screenshotsTotalAfter);
		assertEquals(screenshotBefore.getUri(), screenshotAfter.getUri());
		assertTrue(screenshotBefore.getDateTimeCreated().isBefore(screenshotAfter.getDateTimeCreated()));
	}
	
	@Test
	void givenScreenshotWithUrlNotExists_whenUpdate_thenCreateNewScreenshot() throws IOException {
		
		Collection<String> screenshotsBeforeUpd = screenshotService.findScreenshotFileNamesByUrl(URL_VALID_NON_EXISTING);
		long filesBeforeUpdCount = Iterables.size(screenshotService.findAll());
		
		assertTrue(screenshotsBeforeUpd.isEmpty());
		
		screenshotService.updateScreenshot(URL_VALID_NON_EXISTING);
		
		Collection<String> screenshotsAfterUpd = screenshotService.findScreenshotFileNamesByUrl(URL_VALID_NON_EXISTING);
		long filesAfterUpdCount = Iterables.size(screenshotService.findAll());
		
		assertFalse(screenshotsAfterUpd.isEmpty());
		assertTrue(filesBeforeUpdCount < filesAfterUpdCount);
	}
	
	@Test
	void givenUnreachableUrl_whenStore_thenMalformedURLException() {
		
		Exception ex = assertThrows(MalformedURLException.class, () -> {
			screenshotService.storeScreenshot(URL_UNREACHABLE);
		});
		
		String actualMessage = ex.getMessage();
		
		assertTrue(actualMessage.contains(UrlUtil.CANNOT_REACH_THE_URL_MESSAGE));
	}
	
	@Test
	void givenTooLongUrl_whenStore_thenMalformedURLException() {
		
		String urlTooLong = Strings.repeat("*", UrlUtil.URL_LENGTH_MAX);
		
		Exception ex = assertThrows(MalformedURLException.class, () -> {
			screenshotService.storeScreenshot(URL_VALID + urlTooLong);
		});
		
		String actualMessage = ex.getMessage();
		
		assertTrue(actualMessage.contains(UrlUtil.URL_IS_TOO_LONG_MESSAGE));
	}
	
	@Test
	void givenScreenshotExists_whenDelete_thenSuccess() throws IOException {
		
		String url = "https://meet.google.com";
		Screenshot screenshot = screenshotService.storeScreenshot(url);
		String fileName = screenshot.getFileName();
		Path filePath = Paths.get(storageDir, fileName);
		
		assertFalse(screenshotService.findScreenshotFileNamesByUrl(url).isEmpty());
		assertTrue(Files.exists(filePath));
		
		screenshotService.deleteScreenshot(url);
		
		assertTrue(screenshotService.findScreenshotFileNamesByUrl(url).isEmpty());
		assertTrue(Files.notExists(filePath));
	}
	
	@Test
	void givenFileNotExists_whenDelete_thenNoException() throws IOException {
		
		Screenshot screenshot = screenshotService.storeScreenshot(URL_VALID);
		String fileName = screenshot.getFileName();
		Path filePath = Paths.get(storageDir, fileName);
		
		FileUtils.forceDelete(filePath.toFile());
		
		assertDoesNotThrow( () -> {
			screenshotService.deleteScreenshot(URL_VALID);
		});
	}
	
}
