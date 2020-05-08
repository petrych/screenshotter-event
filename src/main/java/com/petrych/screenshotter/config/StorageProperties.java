package com.petrych.screenshotter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {
	
	/**
	 * Folder location for storing screenshot files
	 */
	private String location = "storage";
	
	public String getLocation() {
		
		return location;
	}
	
	public void setLocation(String location) {
		
		this.location = location;
	}
	
}
