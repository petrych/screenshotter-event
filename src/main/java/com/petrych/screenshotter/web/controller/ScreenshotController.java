package com.petrych.screenshotter.web.controller;

import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.service.IScreenshotService;
import com.petrych.screenshotter.service.InvalidURLException;
import com.petrych.screenshotter.web.dto.ScreenshotDto;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/screenshots")
public class ScreenshotController {
	
	@Autowired
	private IScreenshotService screenshotService;
	
	public ScreenshotController(IScreenshotService screenshotService) {
		
		this.screenshotService = screenshotService;
	}
	
	// find - all
	
	@GetMapping
	public Collection<ScreenshotDto> findAll() {
		
		Iterable<Screenshot> allScreenshots = screenshotService.findAll();
		List<ScreenshotDto> screenshotDtos = new ArrayList<>();
		allScreenshots.forEach(p -> screenshotDtos.add(convertToDto(p)));
		
		return screenshotDtos;
	}
	
	// find - one
	
	@GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] findById(@PathVariable Long id) throws IOException {
		
		File file = screenshotService.getScreenshotFileById(id);
		
		return convertFileToBytes(file);
	}
	
	@GetMapping("/by-name")
	public Collection<ScreenshotDto> findByName(@RequestParam(name = "name", defaultValue = "") String name) {
		
		Iterable<Screenshot> allScreenshots = screenshotService.findByName(name);
		List<ScreenshotDto> screenshotDtos = new ArrayList<>();
		allScreenshots.forEach(p -> screenshotDtos.add(convertToDto(p)));
		
		return screenshotDtos;
	}
	
	// create
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void store(@RequestBody String urlString) throws InvalidURLException {
		
		screenshotService.storeFile(urlString);
	}
	
	// update
	
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public void update(@RequestBody String urlString) throws InvalidURLException {
		
		screenshotService.update(urlString);
	}
	
	// helper methods
	
	private byte[] convertFileToBytes(File file) throws IOException {
		
		if (file == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		InputStream in = new FileInputStream(file.getPath());
		
		return IOUtils.toByteArray(in);
	}
	
	private ScreenshotDto convertToDto(Screenshot entity) {
		
		return new ScreenshotDto(entity.getId(), entity.getName(), entity.getUri(), entity.getDateCreated());
	}
	
	private Screenshot convertToEntity(ScreenshotDto dto) {
		
		Screenshot screenshot = new Screenshot(dto.getName(), dto.getUri());
		
		if (!StringUtils.isEmpty(dto.getId())) {
			screenshot.setId(dto.getId());
		}
		
		if (!StringUtils.isEmpty(dto.getUri())) {
			screenshot.setUri(dto.getUri());
		}
		
		if (!StringUtils.isEmpty(dto.getDateCreated())) {
			screenshot.setDateCreated(dto.getDateCreated());
		}
		
		return screenshot;
	}
	
}
