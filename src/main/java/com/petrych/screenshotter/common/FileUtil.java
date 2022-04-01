package com.petrych.screenshotter.common;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.SecureRandom;

import static java.nio.file.Files.createDirectories;

public class FileUtil {
	
	public static final String IMAGE_FORMAT_NAME = "png";
	
	public static void copyFolder(Path source, Path target, CopyOption... options) throws IOException {
		
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				
				createDirectories(target.resolve(source.relativize(dir)));
				
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				Files.copy(file, target.resolve(source.relativize(file)), options);
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	public static String generateFileName() {
		
		int randomInt = new SecureRandom().nextInt();
		String fileName = String.valueOf(randomInt);
		String fileExtension = getFileExtension();
		
		return fileName.concat(fileExtension);
	}
	
	private static String getFileExtension() {
		
		return "." + IMAGE_FORMAT_NAME;
	}
	
}
