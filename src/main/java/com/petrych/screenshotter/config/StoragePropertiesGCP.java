package com.petrych.screenshotter.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties
@Profile("gcp")
public class StoragePropertiesGCP implements IStorageProperties {
	
	private String projectId;
	
	private String bucketForImages;
	
	private final Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	
	public String getProjectId() {
		
		return projectId;
	}
	
	public void setProjectId(String projectId) {
		
		this.projectId = projectId;
	}
	
	public String getBucketForImages() {
		
		return bucketForImages;
	}
	
	public void setBucketForImages(String bucketForImages) {
		
		this.bucketForImages = bucketForImages;
	}
	
	@Override
	public Storage getStorageObjectGCP() {
		
		return storage;
	}
	
	@Override
	public String getStorageDir() {
		
		return bucketForImages;
	}
	
}
