package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.model.Screenshot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Optional;

public interface IScreenshotService {
	
	Iterable<Screenshot> findAll();
	
	Optional<Screenshot> findById(Long id);
	
	Iterable<Screenshot> findByName(String name);
	
	byte[] getScreenshotFileById(Long id);
	
	Screenshot storeScreenshot(String urlString) throws IOException;
	
	void updateScreenshot(String urlString) throws IOException;
	
	Collection<String> findScreenshotFileNamesByUrl(String urlString) throws MalformedURLException;
	
	void deleteScreenshot(String urlString) throws IOException;
	
}
