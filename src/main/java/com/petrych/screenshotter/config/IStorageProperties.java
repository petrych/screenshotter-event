package com.petrych.screenshotter.config;

import com.google.cloud.storage.Storage;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface IStorageProperties {
	
	Storage getStorageObjectGCP();
	
	String getStorageDir();
	
	default Path getStorageDirAbsolutePath() {
		
		return Paths.get(getStorageDir()).toAbsolutePath();
	}
	
}
