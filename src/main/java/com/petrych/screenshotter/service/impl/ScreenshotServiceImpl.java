package com.petrych.screenshotter.service.impl;

import com.petrych.screenshotter.config.StorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import com.petrych.screenshotter.service.IScreenshotService;
import com.petrych.screenshotter.service.ScreenshotMaker;
import com.petrych.screenshotter.web.controller.ScreenshotController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Service
class ScreenshotServiceImpl implements IScreenshotService {
	
	private final Path rootLocation;
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceImpl.class);
	
	@Autowired
	private IScreenshotRepository screenshotRepo;
	
	@Autowired
	private StorageProperties properties;
	
	public ScreenshotServiceImpl(IScreenshotRepository screenshotRepo, StorageProperties properties) {
		
		this.screenshotRepo = screenshotRepo;
		this.rootLocation = Paths.get(properties.getLocation());
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
	public File getScreenshotFileById(Long id) {
		
		Optional<Screenshot> entity = screenshotRepo.findById(id);
		
		if (entity.isPresent()) {
			String screenshotName = entity.get().getName();
			boolean fileExists = Files.exists(Paths.get(getStorageDir(), screenshotName));
			
			if (fileExists) {
				return new File(getStorageDir() + File.separatorChar + screenshotName);
			} else {
				LOG.debug("Screenshot file not found with id={} and name '{}'.", id, screenshotName);
			}
		}
		
		return null;
	}
	
	
	@Override
	public Stream<Path> loadAllFiles() {
		
		try {
			return Files.walk(this.rootLocation, 1)
			            .filter(path -> !path.equals(this.rootLocation))
			            .map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
		
	}
	
	@Override
	public String store(String urlString) {
		
		String fileName = new ScreenshotMaker(getStorageDir()).createFromUrl(urlString);
		
		Screenshot screenshot = new Screenshot(fileName, buildUriForFileName(fileName));
		screenshotRepo.save(screenshot);
		
		return fileName;
	}
	
	private String getStorageDir() {
		
		return rootLocation.toString();
	}
	
	private String buildUriForFileName(String fileName) {
		
		UriComponentsBuilder builder = MvcUriComponentsBuilder.fromController(ScreenshotController.class);
		
		return builder.path("/" + fileName)
		              .build()
		              .toUriString();
	}
	
}
