package com.petrych.screenshotter.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Profile("gcp")
class ScreenshotServiceGCP extends AbstractScreenshotService implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceGCP.class);
	
	private Storage storage;
	
	private String bucketName;
	
	public ScreenshotServiceGCP(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		super(screenshotRepo, properties);
		this.storage = properties.getStorageObjectGCP();
		this.bucketName = properties.getStorageDir();
	}
	
	// helper methods
	
	@Override
	protected void deleteScreenshotFile(String fileName) {
		
		storage.delete(bucketName, fileName);
	}
	
	@Override
	protected void saveScreenshotFile(ByteArrayOutputStream baos, String fileName) throws IOException {
		
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		storage.createFrom(blobInfo, new ByteArrayInputStream(baos.toByteArray()));
		
		LOG.debug("Screenshot file uploaded to bucket {} as {}", bucketName, fileName);
	}
	
	@Override
	protected boolean screenshotFileExists(String fileName) {
		
		Page<Blob> blobs = storage.list(bucketName);
		
		for (Blob blob : blobs.iterateAll()) {
			if (blob.getName().contains(fileName)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected byte[] readScreenshotFileContent(String fileName) {
		
		return storage.readAllBytes(bucketName, fileName);
	}
	
}
