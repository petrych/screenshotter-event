package com.petrych.screenshotter.service;

import com.petrych.screenshotter.config.TestConfig;
import com.petrych.screenshotter.persistence.model.Screenshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@SpringJUnitConfig(value = TestConfig.class)
public class ScreenshotServiceIT {
	
	@Autowired
	private IScreenshotService screenshotService;
	
	@Test
	public void givenScreenshotsExist_whenfindAll_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findAll();
		
		assertThat(screenshots, is(notNullValue()));
	}
	
	@Test
	public void givenScreenshotExists_whenfindById_thenSuccess() {
		
		Screenshot screenshot = screenshotService.findById(4L).get();
		
		assertEquals(4L, (long) screenshot.getId());
	}
	
	@Test
	public void givenScreenshotExists_whenfindByName_thenSuccess() {
		
		ArrayList<Screenshot> screenshots = (ArrayList<Screenshot>) screenshotService.findByName("screen");
		
		assertTrue(screenshots.get(0).getName().contains("screen"));
	}
	
}
