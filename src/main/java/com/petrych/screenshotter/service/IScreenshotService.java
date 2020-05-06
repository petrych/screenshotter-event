package com.petrych.screenshotter.service;

import com.petrych.screenshotter.persistence.model.Screenshot;

import java.util.Optional;

public interface IScreenshotService {
	
	Iterable<Screenshot> findAll();
	
	Optional<Screenshot> findById(Long id);
	
	Iterable<Screenshot> findByName(String name);
	
}
