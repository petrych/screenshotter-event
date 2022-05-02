package com.petrych.screenshotter.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public class StorageProperties {
	
	private String projectId;
	
	private String bucketForImages;
	
	public final Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	
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
	
}
