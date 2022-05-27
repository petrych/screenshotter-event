package com.petrych.screenshotter.service;

import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Profile({"local", "test"})
public class ScreenshotServiceLocal implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceLocal.class);
	
	@Autowired
	private IScreenshotRepository screenshotRepo;
	
	@Autowired
	private IStorageProperties properties;
	
	private String storageLocation;
	
	public ScreenshotServiceLocal(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		this.screenshotRepo = screenshotRepo;
		this.storageLocation = Paths.get(properties.getStorageDir()).toAbsolutePath().toString();
	}
	
	@Override
	public Iterable<Screenshot> findAll() {
		
		return screenshotRepo.findAll();
	}
	
	@Override
	public Optional<Screenshot> findById(Long id) {
		
		return screenshotRepo.findById(id);
	}
	
	@Override
	public Iterable<Screenshot> findByName(String name) {
		
		return screenshotRepo.findByNameContaining(name);
	}
	
	@Override
	public byte[] getScreenshotFileById(Long id) throws IOException {
		
		byte[] content = null;
		
		Optional<Screenshot> screenshotEntity = screenshotRepo.findById(id);
		
		if (screenshotEntity.isPresent()) {
			String fileName = screenshotEntity.get().getFileName();
			boolean fileExists = Files.exists(Paths.get(storageLocation, fileName));
			
			if (fileExists) {
				content = FileUtil.readFileAsBytes(getFilePathString(fileName));
			} else {
				String messageForClients = String.format(
						"Screenshot file not found for screenshot id=%d", id);
				String messageForInternalUse =  String.format(
						"%s and name='%s' in storage location '%s'.", messageForClients, fileName, storageLocation);
				LOG.debug(messageForInternalUse);
				
				throw new FileNotFoundException(messageForClients);
			}
		}
		else {
			String message = String.format("Screenshot not found with id=%d", id);
			LOG.debug(message);
			
			throw new FileNotFoundException(message);
		}
		
		return content;
		
	}
	
	@Override
	public Screenshot storeScreenshot(String urlString) throws IOException {
		
		UrlUtil.isUrlValid(urlString);
		
		String fileName = "";
		boolean fileNameUnique = false;
		while (!fileNameUnique) {
			fileName = FileUtil.generateFileName();
			fileNameUnique = !Files.exists(Paths.get(getFilePathString(fileName)));
		}
		
		Pair<String, ByteArrayOutputStream> pair = ScreenshotMaker.createScreenshotWithNameAndFile(urlString);
		
		Screenshot screenshot = new Screenshot(pair.getLeft(), fileName);
		saveFileLocally(fileName, pair.getRight());
		screenshotRepo.save(screenshot);
		
		LOG.debug("Stored screenshot: {}", screenshot);
		
		return screenshot;
	}
	
	@Override
	public void updateScreenshot(String urlString) throws IOException {
		
		Collection<String> fileNameToSearchFor = findScreenshotFileNamesByUrl(urlString);
		
		if (fileNameToSearchFor.isEmpty()) {
			// create if doesn't exist
			this.storeScreenshot(urlString);
			
		} else {
			UrlUtil.isUrlValid(urlString);
			// update if exists
			Set<String> fileNames = (Set<String>) findScreenshotFileNamesByUrl(urlString);
			if (fileNames.isEmpty()) {
				return;
			}
			
			Pair<String, ByteArrayOutputStream> pair = ScreenshotMaker.createScreenshotWithNameAndFile(urlString);
			
			Screenshot screenshot = ((ArrayList<Screenshot>) screenshotRepo
					.findByNameContaining(pair.getLeft())).get(0);
			screenshot.setDateTimeCreated(LocalDateTime.now());
			
			screenshotRepo.save(screenshot);
			LOG.debug("Updated screenshot: {}", screenshot);
		}
	}
	
	@Override
	public Collection<String> findScreenshotFileNamesByUrl(String urlString) throws MalformedURLException {
		
		UrlUtil.isUrlValid(urlString);
		String screenshotNameToSearchFor = UrlUtil.parseUrlString(urlString);
		
		Collection<String> screenshotFiles = new HashSet<>();
		Iterable<Screenshot> screenshots = findByName(screenshotNameToSearchFor);
		
		if (CollectionUtils.isEmpty((Collection) screenshots)) {
			return Collections.emptyList();
		}
		
		for (Screenshot screenshot : screenshots) {
			String fileName = screenshot.getFileName();
			
			if (fileName != null) {
				screenshotFiles.add(screenshot.getFileName());
			}
		}
		
		return screenshotFiles;
	}
	
	@Override
	public void deleteScreenshot(String urlString) throws IOException {
		
		UrlUtil.isUrlValid(urlString);
		
		Collection<String> fileNamesToSearchFor = findScreenshotFileNamesByUrl(urlString);
		
		if (fileNamesToSearchFor.isEmpty()) {
			throw new FileNotFoundException();
		} else {
			for (String fileName : fileNamesToSearchFor) {
				Screenshot screenshot = screenshotRepo.findByFileName(fileName);
				
				if (screenshot != null) {
					this.deleteFile(fileName);
					screenshotRepo.delete(screenshot);
					LOG.debug("Removed screenshot: {}", screenshot);
				}
			}
			
		}
	}
	
	// helper methods
	
	private void saveFileLocally(String fileName, ByteArrayOutputStream baos) {
		
		String filePathString = getFilePathString(fileName);
		File file = new File(filePathString);
		try {
			FileUtils.writeByteArrayToFile(file, baos.toByteArray());
			LOG.debug("Screenshot file written successfully to path {}.", filePathString);
		} catch (IOException e) {
			String message = String.format("Could not write a file '%s'", filePathString);
			throw new StorageException(message, e);
		}
	}
	
	private void deleteFile(String fileName) throws IOException {
		
		File fileToDelete = FileUtils.getFile(getFilePathString(fileName));
		FileUtils.forceDelete(fileToDelete);
	}
	
	private String getFilePathString(String fileName) {
		
		return storageLocation + File.separatorChar + fileName;
	}
	
}
