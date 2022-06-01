package com.petrych.screenshotter.persistence;

public class StorageException extends RuntimeException {
	
	private static final String defaultMessage = "Could not write a file.";
	
	public StorageException(Throwable cause) {
		super(defaultMessage, cause);
	}
	
	public StorageException(String message, Throwable cause) {
		
		super(message, cause);
	}
	
}
