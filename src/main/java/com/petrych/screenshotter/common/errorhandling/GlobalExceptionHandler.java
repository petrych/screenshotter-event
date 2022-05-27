package com.petrych.screenshotter.common.errorhandling;

import com.petrych.screenshotter.service.UrlUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
	public ResponseEntity<Object> handleMalformedURLException(MalformedURLException ex) {
		
		String devMessage = ExceptionUtils.getRootCauseMessage(ex);
		HttpStatus httpStatus = null;
		
		if (devMessage.contains(UrlUtil.URL_IS_TOO_LONG_MESSAGE)) {
			httpStatus = HttpStatus.BAD_REQUEST;
			
		}
		if (devMessage.contains(UrlUtil.CANNOT_REACH_THE_URL_MESSAGE)) {
			httpStatus = HttpStatus.BAD_GATEWAY;
		}
		ApiError apiError = new ApiError(httpStatus, ex.getMessage(), "", ex.toString());
		
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(apiError, Objects.requireNonNull(httpStatus));
		LOG.error(ex.toString());
		
		return responseEntity;
	}
	
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException ex) {
		
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		ApiError apiError = new ApiError(httpStatus, ex.getClass().getCanonicalName(), "", ex.getMessage());
		
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(apiError, Objects.requireNonNull(httpStatus));
		LOG.error(ex.toString());
		
		return responseEntity;
	}
	
}
