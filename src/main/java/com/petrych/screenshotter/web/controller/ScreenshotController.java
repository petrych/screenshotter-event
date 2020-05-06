package com.petrych.screenshotter.web.controller;

import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.service.IScreenshotService;
import com.petrych.screenshotter.web.dto.ScreenshotDto;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/screenshots")
public class ScreenshotController {
	
	private IScreenshotService screenshotService;
	
	public ScreenshotController(IScreenshotService screenshotService) {
		
		this.screenshotService = screenshotService;
	}
	
	@GetMapping
	public Collection<ScreenshotDto> findAll() {
		
		Iterable<Screenshot> allScreenshots = screenshotService.findAll();
		List<ScreenshotDto> screenshotDtos = new ArrayList<>();
		allScreenshots.forEach(p -> screenshotDtos.add(convertToDto(p)));
		
		return screenshotDtos;
	}
	
	@GetMapping(value = "/{id}")
	public ScreenshotDto findById(@PathVariable Long id) {
		
		Screenshot entity = screenshotService.findById(id)
		                                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		return convertToDto(entity);
	}
	
	@GetMapping("/by-name")
	public Collection<ScreenshotDto> findByName(@RequestParam(name = "name", defaultValue = "") String name) {
		
		Iterable<Screenshot> allScreenshots = screenshotService.findByName(name);
		List<ScreenshotDto> screenshotDtos = new ArrayList<>();
		allScreenshots.forEach(p -> screenshotDtos.add(convertToDto(p)));
		
		return screenshotDtos;
	}
	
	private ScreenshotDto convertToDto(Screenshot entity) {
		
		return new ScreenshotDto(entity.getId(), entity.getName(), entity.getDateCreated());
	}
	
	private Screenshot convertToEntity(ScreenshotDto dto) {
		
		Screenshot screenshot = new Screenshot(dto.getName());
		
		if (!StringUtils.isEmpty(dto.getId())) {
			screenshot.setId(dto.getId());
		}
		
		if (!StringUtils.isEmpty(dto.getDateCreated())) {
			screenshot.setDateCreated(dto.getDateCreated());
		}
		
		return screenshot;
	}
	
}
