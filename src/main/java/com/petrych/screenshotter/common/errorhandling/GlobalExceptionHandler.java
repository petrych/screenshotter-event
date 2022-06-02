package com.petrych.screenshotter.common.errorhandling;

import com.petrych.screenshotter.common.FileUtil;
import com.petrych.screenshotter.service.UrlUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	public GlobalExceptionHandler() {
		
		super();
	}
	
	
	@ExceptionHandler(MalformedURLException.class)
	public ResponseEntity<Object> handleMalformedURLException(MalformedURLException ex, HttpServletRequest request) {
		
		String rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);
		HttpStatus httpStatus;
		
		if (rootCauseMessage.contains(UrlUtil.URL_IS_TOO_LONG_MESSAGE)) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else {
			httpStatus = HttpStatus.BAD_GATEWAY;
		}
		
		ApiError apiError = new ApiError(httpStatus.value(), ex.getMessage(), request.getRequestURI());
		
		LOG.warn(apiError.toString());
		
		return new ResponseEntity<>(apiError, Objects.requireNonNull(httpStatus));
	}
	
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<Object> handleScreenshotFileNotFoundException(FileNotFoundException ex,
	                                                                    HttpServletRequest request) {
		
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		String errorMessage = removeStoragePathFromErrorMessage(ex.getMessage());
		ApiError apiError = new ApiError(httpStatus.value(), errorMessage, request.getRequestURI());
		
		LOG.warn(apiError.toString());
		
		return new ResponseEntity<>(apiError, Objects.requireNonNull(httpStatus));
	}
	
	private String removeStoragePathFromErrorMessage(String errorMessage) {
		
		if (errorMessage == null || errorMessage.isEmpty()) {
			return ScreenshotFileNotFoundException.DEFAULT_MESSAGE;
		}
		
		if (errorMessage.endsWith(FileUtil.IMAGE_FORMAT_NAME)) {
			int separatorCharFirstIndex = errorMessage.indexOf(File.separatorChar);
			return errorMessage.substring(0, separatorCharFirstIndex);
		} else {
			return errorMessage;
		}
	}
	
}
