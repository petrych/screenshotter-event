package com.petrych.screenshotter.service;

import com.google.common.collect.Iterables;
import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.config.StorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
class ScreenshotServiceImpl implements IScreenshotService {
	
	private final Path storageLocation;
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceImpl.class);
	
	@Autowired
	private IScreenshotRepository screenshotRepo;
	
	@Autowired
	private StorageProperties properties;
	
	public ScreenshotServiceImpl(IScreenshotRepository screenshotRepo, StorageProperties properties) {
		
		this.screenshotRepo = screenshotRepo;
		this.storageLocation = Paths.get(properties.getLocation());
	}
	
	// find - all
	
	@Override
	public Iterable<Screenshot> findAll() {
		
		return screenshotRepo.findAll();
	}
	
	// find - one
	
	@Override
	public Optional<Screenshot> findById(Long id) {
		
		return screenshotRepo.findById(id);
	}
	
	@Override
	public Iterable<Screenshot> findByName(String name) {
		
		return screenshotRepo.findByNameContaining(name);
	}
	
	@Override
	public File getScreenshotFileById(Long id) {
		
		Optional<Screenshot> screenshotEntity = screenshotRepo.findById(id);
		
		if (screenshotEntity.isPresent()) {
			String fileName = screenshotEntity.get().getFileName();
			boolean fileExists = Files.exists(Paths.get(getStorageLocation(), fileName));
			
			if (fileExists) {
				return new File(getStorageLocation() + File.separatorChar + fileName);
			} else {
				LOG.debug("Screenshot file not found with screenshot id={} and name='{}'.", id, fileName);
			}
		}
		
		return null;
	}
	
	// other
	
	@Override
	public Stream<Path> loadAllScreenshotFilePaths() {
		
		try {
			return Files.walk(this.storageLocation, 1)
			            .filter(path -> !path.equals(this.storageLocation))
			            .map(this.storageLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
		
	}
	
	// store
	
	@Override
	public Screenshot storeScreenshot(String urlString) throws MalformedURLException {
		
		UrlUtil.isUrlValid(urlString);
		
		String fileName = "";
		boolean fileNameUnique = false;
		while (!fileNameUnique) {
			fileName = FileUtil.generateFileName();
			fileNameUnique = !screenshotFileExists(fileName);
		}
		
		String screenshotName = new ScreenshotMaker(
				getStorageLocation()).createScreenshotWithNameAndFile(urlString, fileName);
		
		Screenshot screenshot = new Screenshot(screenshotName, fileName);
		screenshotRepo.save(screenshot);
		
		LOG.debug("Stored screenshot: {}", screenshot);
		
		return screenshot;
	}
	
	// update
	
	@Override
	public void updateScreenshot(String urlString) throws MalformedURLException {
		
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
			
			String fileNameToUpdate = Iterables.get(fileNames, 0);
			
			String screenshotName = new ScreenshotMaker(getStorageLocation()).createScreenshotWithNameAndFile(urlString,
			                                                                                                  fileNameToUpdate);
			
			Screenshot screenshot = ((ArrayList<Screenshot>) screenshotRepo
					.findByNameContaining(screenshotName)).get(0);
			screenshot.setDateTimeCreated(LocalDateTime.now());
			
			screenshotRepo.save(screenshot);
			LOG.debug("Updated screenshot: {}", screenshot);
		}
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
					screenshotRepo.delete(screenshot);
					this.deleteFile(fileName);
					LOG.debug("Removed screenshot: {}", screenshot);
				}
			}
			
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
	
	// helper methods
	
	private String getStorageLocation() {
		
		return storageLocation.toString();
	}
	
	private Collection<String> findAllScreenshotUris() {
		
		return this.loadAllScreenshotFilePaths()
		           .map(this::convertFilePathToUriString)
		           .collect(Collectors.toList());
	}
	
	private String convertFilePathToUriString(Path path) {
		
		UriComponentsBuilder builder = MvcUriComponentsBuilder.fromController(this.getClass()).path("/");
		
		return builder.path(path.getFileName().toString())
		              .build().toUriString();
	}
	
	private void deleteFile(String fileName) throws IOException {
		
		File fileToDelete = FileUtils.getFile(getStorageLocation() + File.separatorChar + fileName);
		FileUtils.forceDelete(fileToDelete);
	}
	
	private boolean screenshotFileExists(String fileName) {
		
		List<Path> list = this.loadAllScreenshotFilePaths()
		                      .filter(path -> path.getFileName().toString().contains(fileName))
		                      .collect(Collectors.toList());
		
		return !list.isEmpty();
	}
	
}
