package com.petrych.screenshotter.service;

import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.config.IStorageProperties;
import com.petrych.screenshotter.persistence.StorageException;
import com.petrych.screenshotter.persistence.model.Screenshot;
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
import java.util.Optional;

@Service
@Profile({"local", "test"})
public class ScreenshotServiceLocal extends AbstractScreenshotService implements IScreenshotService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotServiceLocal.class);
	
	private String storageLocation;
	
	public ScreenshotServiceLocal(IScreenshotRepository screenshotRepo, IStorageProperties properties) {
		
		super(screenshotRepo, properties);
		this.storageLocation = Paths.get(properties.getStorageDir()).toAbsolutePath().toString();
	}
	
	@Override
	public byte[] getScreenshotFileById(Long id) throws IOException {
		
		byte[] content = null;
		
		Optional<Screenshot> screenshotEntity = screenshotRepo.findById(id);
		
		if (screenshotEntity.isPresent()) {
			String fileName = screenshotEntity.get().getFileName();
			boolean fileExists = Files.exists(Paths.get(storageLocation, fileName));
			
			if (fileExists) {
				content = FileUtil.readFileAsBytes(getFilePathString(fileName));
			} else {
				String messageForClients = String.format(
						"Screenshot file not found for screenshot id=%d", id);
				String messageForInternalUse = String.format(
						"%s and name='%s' in storage location '%s'.", messageForClients, fileName, storageLocation);
				LOG.debug(messageForInternalUse);
				
				throw new FileNotFoundException(messageForClients);
			}
		} else {
			String message = String.format("Screenshot not found with id=%d", id);
			LOG.debug(message);
			
			throw new FileNotFoundException(message);
		}
		
		return content;
		
	}
	
	// helper methods
	
	@Override
	protected void deleteScreenshotFile(String fileName) throws IOException {
		
		File fileToDelete = FileUtils.getFile(getFilePathString(fileName));
		FileUtils.forceDelete(fileToDelete);
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
	
}
