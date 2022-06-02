package com.petrych.screenshotter.common.errorhandling;

public class StorageException extends RuntimeException {
	
	private static final String defaultMessage = "Could not write a file";
	
	public StorageException(Throwable cause) {
		
		super(defaultMessage, cause);
	}
	
}
