package com.petrych.screenshotter.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@Profile("gcp")
class ScreenshotServiceGCP extends AbstractScreenshotService implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceGCP.class);
	
	private Storage storage;
	
	private String bucketName;
	
	@Autowired
	private IScreenshotRepository screenshotRepo;
	
	@Autowired
	private IStorageProperties properties;
	
	public ScreenshotServiceGCP(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		super(screenshotRepo, properties);
		this.storage = properties.getStorageObjectGCP();
		this.bucketName = properties.getStorageDir();
	}
	
	@Override
	public byte[] getScreenshotFileById(Long id) {
		
		byte[] content = null;
		
		Optional<Screenshot> screenshotEntity = screenshotRepo.findById(id);
		
		if (screenshotEntity.isPresent()) {
			String fileName = screenshotEntity.get().getFileName();
			content = downloadObjectAsByteArray(fileName);
			
			if (content.length <= 0) {
				LOG.debug("Screenshot file not found with screenshot id={} and name='{}'.", id,
				          screenshotEntity.get().getName());
			}
		}
		
		return content;
	}
	
	private byte[] downloadObjectAsByteArray(String objectName) {
		
		return storage.readAllBytes(bucketName, objectName);
	}
	
	// helper methods
	
	@Override
	protected void deleteFile(String fileName) {
		
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
	
}
