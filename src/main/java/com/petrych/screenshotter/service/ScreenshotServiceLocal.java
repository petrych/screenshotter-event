package com.petrych.screenshotter.service;

import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.repository.IScreenshotRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Profile({"local", "test"})
public class ScreenshotServiceLocal extends AbstractScreenshotService implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceLocal.class);
	
	private String storageLocation;
	
	public ScreenshotServiceLocal(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		super(screenshotRepo, properties);
		this.storageLocation = Paths.get(properties.getStorageDir()).toAbsolutePath().toString();
	}
	
	// helper methods
	
	@Override
	protected void deleteScreenshotFile(String fileName) throws IOException {
		
		try {
			File fileToDelete = FileUtils.getFile(getFilePathString(fileName));
			FileUtils.forceDelete(fileToDelete);
		} catch (FileNotFoundException ex) {
			LOG.debug("Fail deleting a screenshot file. " + ex.getMessage());
		}
	}
	
	@Override
	protected void saveScreenshotFile(ByteArrayOutputStream baos, String fileName) {
		
		String filePathString = getFilePathString(fileName);
		File file = new File(filePathString);
		try {
			FileUtils.writeByteArrayToFile(file, baos.toByteArray());
			LOG.debug("Screenshot file written successfully to path {}.", filePathString);
		} catch (IOException e) {
			String message = String.format("Could not write a file '%s'", filePathString);
			throw new StorageException(message, e);
		}
	}
	
	private String getFilePathString(String fileName) {
		
		return storageLocation + File.separatorChar + fileName;
	}
	
	@Override
	protected boolean screenshotFileExists(String fileName) {
		
		return Files.exists(Paths.get(getFilePathString(fileName)));
	}
	
	@Override
	protected byte[] readScreenshotFileContent(String fileName) throws IOException {
		
		return FileUtil.readFileAsBytes(getFilePathString(fileName));
	}
	
}
