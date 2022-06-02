package com.petrych.screenshotter.service;

import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.common.errorhandling.ScreenshotEntityNotFoundException;
import com.petrych.screenshotter.common.errorhandling.ScreenshotFileNotFoundException;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public abstract class AbstractScreenshotService implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractScreenshotService.class);
	
	@Autowired
	protected IScreenshotRepository screenshotRepo;
	
	@Autowired
	protected IStorageProperties properties;
	
	
	protected AbstractScreenshotService(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		this.screenshotRepo = screenshotRepo;
		this.properties = properties;
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
		
		Optional<Screenshot> screenshotEntity = screenshotRepo.findById(id);
		
		if (screenshotEntity.isPresent()) {
			String fileName = screenshotEntity.get().getFileName();
			
			if (screenshotFileExists(fileName)) {
				return readScreenshotFileContent(fileName);
			} else {
				String message = String.format(
						"Screenshot file not found for screenshot id=%d and name='%s' in storage location '%s'",
						id, fileName, properties.getStorageDir());
				LOG.warn(message);
				
				throw new ScreenshotFileNotFoundException(id);
			}
		} else {
			
			throw new ScreenshotEntityNotFoundException(id);
		}
	}
	
	@Override
	public Screenshot storeScreenshot(String urlString) throws IOException {
		
		UrlUtil.isUrlValid(urlString);
		
		String fileName = "";
		boolean fileNameUnique = false;
		while (!fileNameUnique) {
			fileName = FileUtil.generateFileName();
			fileNameUnique = !screenshotFileExists(fileName);
		}
		
		Pair<String, ByteArrayOutputStream> pair = ScreenshotMaker.createScreenshotWithNameAndFile(urlString);
		
		Screenshot screenshot = new Screenshot(pair.getLeft(), fileName);
		saveScreenshotFile(pair.getRight(), fileName);
		screenshotRepo.save(screenshot);
		
		LOG.debug("Stored screenshot: {}", screenshot.toLogString());
		
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
			screenshot.setDateTimeCreated(LocalDateTime.now(ZoneOffset.UTC));
			
			screenshotRepo.save(screenshot);
			LOG.debug("Updated screenshot: {}", screenshot.toLogString());
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
	@Transactional(rollbackFor = {IOException.class, RuntimeException.class})
	public void deleteScreenshot(String urlString) throws IOException {
		
		UrlUtil.isUrlValid(urlString);
		
		Collection<String> fileNamesToSearchFor = findScreenshotFileNamesByUrl(urlString);
		
		if (fileNamesToSearchFor.isEmpty()) {
			throw new ScreenshotFileNotFoundException();
		} else {
			for (String fileName : fileNamesToSearchFor) {
				Screenshot screenshot = screenshotRepo.findByFileName(fileName);
				
				if (screenshot != null) {
					this.deleteScreenshotFile(fileName);
					screenshotRepo.delete(screenshot);
					LOG.debug("Removed screenshot: {}", screenshot.toLogString());
				}
			}
			
		}
	}
	
	protected abstract void deleteScreenshotFile(String fileName) throws IOException;
	
	protected abstract void saveScreenshotFile(ByteArrayOutputStream baos, String fileName) throws IOException;
	
	protected abstract boolean screenshotFileExists(String fileName);
	
	protected abstract byte[] readScreenshotFileContent(String fileName) throws IOException;
	
}
