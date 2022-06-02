package com.petrych.screenshotter.common.errorhandling;

import java.io.FileNotFoundException;

public class ScreenshotFileNotFoundException extends FileNotFoundException {
	
	public static final String DEFAULT_MESSAGE = "Screenshot file does not exist";
	
	public static final String DEFAULT_MESSAGE_WITH_ID_TEMPLATE = DEFAULT_MESSAGE + " for screenshot id=%d";
	
	
	public ScreenshotFileNotFoundException() {
		
		super(DEFAULT_MESSAGE);
	}
	
	public ScreenshotFileNotFoundException(Long screenshotId) {
		
		super(String.format(DEFAULT_MESSAGE_WITH_ID_TEMPLATE, screenshotId));
	}
	
}