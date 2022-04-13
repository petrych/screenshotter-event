package com.petrych.screenshotter.web.controller;

import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.service.IScreenshotService;
import com.petrych.screenshotter.web.dto.ScreenshotDto;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/screenshots")
public class ScreenshotController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotController.class);
	
	@Autowired
	private IScreenshotService screenshotService;
	
	@Value("${sm://projects/148500988272/secrets/spring_cloud_gcp_sql_instance_connection_name}")
	private String sqlInstanceConnectionName;
	
	public ScreenshotController(IScreenshotService screenshotService) {
		
		this.screenshotService = screenshotService;
	}
	
	// find - all
	
	@GetMapping
	public Collection<ScreenshotDto> findAll() {
		
		Iterable<Screenshot> allScreenshots = screenshotService.findAll();
		List<ScreenshotDto> screenshotDtos = new ArrayList<>();
		allScreenshots.forEach(p -> screenshotDtos.add(convertToDto(p)));
		LOG.debug("Screenshots total: {}", screenshotDtos.size());
		
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
	public void store(@RequestBody String urlString) throws MalformedURLException {
		
		screenshotService.storeScreenshot(urlString);
	}
	
	// update
	
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public void update(@RequestBody String urlString) throws MalformedURLException {
		
		screenshotService.updateScreenshot(urlString);
	}
	
	// delete
	
	@DeleteMapping
	@ResponseStatus(HttpStatus.OK)
	public void delete(@RequestBody String urlString) throws IOException {
		
		screenshotService.deleteScreenshot(urlString);
	}
	
	// This method is only for testing Google Secret Manager on prod
	@GetMapping("test-1-string")
	@ResponseStatus(HttpStatus.OK)
	public String getSqlInstanceName() {
		
		System.out.println(sqlInstanceConnectionName);
		return sqlInstanceConnectionName;
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
		
		long entityId = entity.getId();
		
		return new ScreenshotDto(entityId, entity.getName(), buildUri(entityId), entity.getDateTimeCreated());
	}
	
	private String buildUri(long screenshotId) {
		
		UriComponentsBuilder builder = MvcUriComponentsBuilder.fromController(ScreenshotController.class);
		
		return builder.path("/" + screenshotId)
		              .build()
		              .toUriString();
	}
	
}
