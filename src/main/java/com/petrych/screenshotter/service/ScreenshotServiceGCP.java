package com.petrych.screenshotter.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.model.Screenshot;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Profile("gcp")
class ScreenshotServiceGCP implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceGCP.class);
	
	private Storage storage;
	
	private String bucketName;
	
	@Autowired
	private IScreenshotRepository screenshotRepo;
	
	@Autowired
	private IStorageProperties properties;
	
	public ScreenshotServiceGCP(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		this.screenshotRepo = screenshotRepo;
		this.properties = properties;
		this.storage = properties.getStorageObjectGCP();
		this.bucketName = properties.getStorageDir();
	}
	
	// find - all
	
	@Override
	public Iterable<Screenshot> findAll() {
		
		return screenshotRepo.findAll();
	}
	
	// find - one
	
	@Override
	public Optional<Screenshot> findById(Long id) {
		
		return screenshotRepo.findById(id);
	}
	
	@Override
	public Iterable<Screenshot> findByName(String name) {
		
		return screenshotRepo.findByNameContaining(name);
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
	
	public byte[] downloadObjectAsByteArray(String objectName) {
		
		byte[] content = storage.readAllBytes(bucketName, objectName);
		
		return content;
	}
	
	// store
	
	@Override
	public Screenshot storeScreenshot(String urlString) throws IOException {
		
		UrlUtil.isUrlValid(urlString);
		
		String fileName = "";
		boolean fileNameUnique = false;
		while (!fileNameUnique) {
			fileName = FileUtil.generateFileName();
			fileNameUnique = !screenshotFileExists(fileName);
		}
		
		Pair<String, ByteArrayOutputStream> pair = ScreenshotMaker.createScreenshotWithNameAndFile(urlString);
		
		Screenshot screenshot = new Screenshot(pair.getLeft(), fileName);
		screenshotRepo.save(screenshot);
		uploadScreenshotFileToGCP(pair.getRight(), fileName);
		
		LOG.debug("Stored screenshot: {}", screenshot);
		
		return screenshot;
	}
	
	public void uploadScreenshotFileToGCP(ByteArrayOutputStream baos, String screenshotFileName) throws IOException {
		
		BlobId blobId = BlobId.of(bucketName, screenshotFileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		storage.createFrom(blobInfo, new ByteArrayInputStream(baos.toByteArray()));
		
		LOG.debug("Screenshot file uploaded to bucket {} as {}", bucketName, screenshotFileName);
	}
	
	// update
	
	@Override
	public void updateScreenshot(String urlString) throws IOException {
		
		Collection<String> fileNameToSearchFor = findScreenshotFileNamesByUrl(urlString);
		
		if (fileNameToSearchFor.isEmpty()) {
			// create if doesn't exist
			this.storeScreenshot(urlString);
			
		} else {
			UrlUtil.isUrlValid(urlString);
			// update if exists
			Set<String> fileNames = (Set<String>) findScreenshotFileNamesByUrl(urlString);
			if (fileNames.isEmpty()) {
				return;
			}
			
			Pair<String, ByteArrayOutputStream> pair = ScreenshotMaker.createScreenshotWithNameAndFile(urlString);
			
			Screenshot screenshot = ((ArrayList<Screenshot>) screenshotRepo
					.findByNameContaining(pair.getLeft())).get(0);
			screenshot.setDateTimeCreated(LocalDateTime.now(ZoneOffset.UTC));
			
			screenshotRepo.save(screenshot);
			LOG.debug("Updated screenshot: {}", screenshot);
		}
	}
	
	@Override
	public void deleteScreenshot(String urlString) throws IOException {
		
		UrlUtil.isUrlValid(urlString);
		
		Collection<String> fileNamesToSearchFor = findScreenshotFileNamesByUrl(urlString);
		
		if (fileNamesToSearchFor.isEmpty()) {
			throw new FileNotFoundException();
		} else {
			for (String fileName : fileNamesToSearchFor) {
				Screenshot screenshot = screenshotRepo.findByFileName(fileName);
				
				if (screenshot != null) {
					screenshotRepo.delete(screenshot);
					this.deleteFile(fileName);
					LOG.debug("Removed screenshot: {}", screenshot);
				}
			}
			
		}
	}
	
	@Override
	public Collection<String> findScreenshotFileNamesByUrl(String urlString) throws MalformedURLException {
		
		UrlUtil.isUrlValid(urlString);
		String screenshotNameToSearchFor = UrlUtil.parseUrlString(urlString);
		
		Collection<String> screenshotFiles = new HashSet<>();
		Iterable<Screenshot> screenshots = findByName(screenshotNameToSearchFor);
		
		if (CollectionUtils.isEmpty((Collection) screenshots)) {
			return Collections.emptyList();
		}
		
		for (Screenshot screenshot : screenshots) {
			String fileName = screenshot.getFileName();
			
			if (fileName != null) {
				screenshotFiles.add(screenshot.getFileName());
			}
		}
		
		return screenshotFiles;
	}
	
	// helper methods
	
	private void deleteFile(String fileName) {
		
		storage.delete(bucketName, fileName);
	}
	
	private boolean screenshotFileExists(String fileName) {
		
		Page<Blob> blobs = storage.list(bucketName);
		
		for (Blob blob : blobs.iterateAll()) {
			if (blob.getName().contains(fileName)) {
				return true;
			}
		}
		
		return false;
	}
	
}
