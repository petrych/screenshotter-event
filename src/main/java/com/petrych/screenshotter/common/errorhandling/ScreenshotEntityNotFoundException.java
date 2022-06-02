package com.petrych.screenshotter.common.errorhandling;

public class ScreenshotEntityNotFoundException extends RuntimeException {
	
	private static final String MESSAGE_WITH_ID_TEMPLATE = "Screenshot with id=%d does not exist";
	
	
	public ScreenshotEntityNotFoundException(Long screenshotId) {
		
		super(String.format(MESSAGE_WITH_ID_TEMPLATE, screenshotId));
	}
	
}
