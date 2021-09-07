package com.petrych.screenshotter.service.impl;

import com.petrych.screenshotter.config.StorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import com.petrych.screenshotter.service.IScreenshotService;
import com.petrych.screenshotter.service.InvalidURLException;
import com.petrych.screenshotter.service.ScreenshotMaker;
import com.petrych.screenshotter.web.controller.ScreenshotController;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
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
		
		Optional<Screenshot> entity = screenshotRepo.findById(id);
		
		if (entity.isPresent()) {
			String screenshotName = entity.get().getName();
			boolean fileExists = Files.exists(Paths.get(getStorageLocation(), screenshotName));
			
			if (fileExists) {
				return new File(getStorageLocation() + File.separatorChar + screenshotName);
			} else {
				LOG.debug("Screenshot file not found with id={} and name '{}'.", id, screenshotName);
			}
		}
		
		return null;
	}
	
	// other
	
	@Override
	public Stream<Path> loadAllFiles() {
		
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
	public String storeFile(String urlString) throws InvalidURLException {
		
		String fileName = new ScreenshotMaker(getStorageLocation()).createFromUrl(urlString);
		
		Screenshot screenshot = new Screenshot(fileName, buildUriForFileName(fileName));
		screenshotRepo.save(screenshot);
		
		return fileName;
	}
	
	// update
	
	@Override
	public void update(String urlString) throws InvalidURLException {
		
		String fileNameToSearchFor = findFileNameByUrl(urlString);
		
		if (fileNameToSearchFor.isEmpty()) {
			// create if doesn't exist
			this.storeFile(urlString);
			
		} else {
			// update if exists
			new ScreenshotMaker(getStorageLocation()).createFromUrl(urlString);
			
			Screenshot screenshot = ((ArrayList<Screenshot>) screenshotRepo
					.findByNameContaining(fileNameToSearchFor)).get(0);
			screenshot.setDateCreated(LocalDate.now());
			
			screenshotRepo.save(screenshot);
		}
	}
	
	@Override
	public void delete(String urlString) throws InvalidURLException, IOException {
		
		String fileNameToSearchFor = findFileNameByUrl(urlString);
		
		if (fileNameToSearchFor.isEmpty()) {
			throw new FileNotFoundException();
		}
		else {
			Screenshot screenshot = ((ArrayList<Screenshot>) screenshotRepo
					.findByNameContaining(fileNameToSearchFor)).get(0);
			
			screenshotRepo.delete(screenshot);
			this.deleteFile(fileNameToSearchFor);
		}
	}
	
	@Override
	public String findFileNameByUrl(String urlString) throws InvalidURLException {
		
		for (String uri : findAllScreenshotUris()) {
			String fileNameToSearchFor = ScreenshotMaker.createFileName(urlString);
			
			if (uri.endsWith(fileNameToSearchFor)) {
				
				return fileNameToSearchFor;
			}
		}
		
		return "";
	}
	
	// helper methods
	
	private String getStorageLocation() {
		
		return storageLocation.toString();
	}
	
	private String buildUriForFileName(String fileName) {
		
		UriComponentsBuilder builder = MvcUriComponentsBuilder.fromController(ScreenshotController.class);
		
		return builder.path("/" + fileName)
		              .build()
		              .toUriString();
	}
	
	private Collection<String> findAllScreenshotUris() {
		
		return this.loadAllFiles()
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
	
}
