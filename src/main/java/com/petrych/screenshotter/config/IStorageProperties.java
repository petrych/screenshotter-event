package com.petrych.screenshotter.config;

import com.google.cloud.storage.Storage;

public interface IStorageProperties {
	
	Storage getStorageObjectGCP();
	
	String getStorageDir();
	
}
