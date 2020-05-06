package com.petrych.screenshotter.service.impl;

import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import com.petrych.screenshotter.service.IScreenshotService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScreenshotServiceImpl implements IScreenshotService {
	
	private IScreenshotRepository screenshotRepo;
	
	public ScreenshotServiceImpl(IScreenshotRepository screenshotRepo) {
		
		this.screenshotRepo = screenshotRepo;
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
	
}
