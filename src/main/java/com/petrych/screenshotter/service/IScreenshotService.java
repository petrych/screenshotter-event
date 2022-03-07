package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.model.Screenshot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface IScreenshotService {
	
	Iterable<Screenshot> findAll();
	
	Optional<Screenshot> findById(Long id);
	
	Iterable<Screenshot> findByName(String name);
	
	File getScreenshotFileById(Long id);
	
	Stream<Path> loadAllFiles();
	
	String storeFile(String urlString) throws MalformedURLException;
	
	void update(String urlString) throws MalformedURLException;
	
	String findFileNameByUrl(String urlString) throws MalformedURLException;
	
	void delete(String urlString) throws IOException;
	
}
