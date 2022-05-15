package com.petrych.screenshotter.config;

import com.google.cloud.storage.Storage;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

@Configuration
@ConfigurationProperties
@Profile("test")
public class StoragePropertiesTest implements IStorageProperties {
	
	private String storageDir;
	
	public void setStorageDir(String storageDir) {
		
		this.storageDir = storageDir;
	}
	
	@Override
	public Storage getStorageObjectGCP() {
		
		throw new NotImplementedException("No access to Google Cloud Platform from test environment");
		
	}
	
	@Override
	public String getStorageDir() {
		
		createStorageDirIfNotExists();
		
		return storageDir;
	}
	
	private void createStorageDirIfNotExists() {
		
		File dir = new File(storageDir);
		dir.mkdir();
	}
	
}
