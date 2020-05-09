package com.petrych.screenshotter.service.impl;

import com.petrych.screenshotter.config.StorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import com.petrych.screenshotter.service.IScreenshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			boolean fileExists = fileExists(getStorageDir(), screenshotName);
			
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
	
	
	private static boolean fileExists(String dirName, String fileName) {
		
		File[] dirContent = getDirContent(dirName);
		for (File file : dirContent) {
			if (file.getName().contains(fileName)) {
				return true;
			}
		}
		LOG.debug("File '{}' does not exist in the directory '{}'.", fileName, dirName);
		
		return false;
	}
	
	private static File[] getDirContent(String dirName) {
		
		File dir = new File(dirName);
		File[] dirContent = dir.listFiles();
		LOG.debug("Successfully retrieved the list of files in the directory '{}'.", dirName);
		
		return dirContent;
	}
	
	private String getStorageDir() {
		
		return rootLocation.toString();
	}
	
}
