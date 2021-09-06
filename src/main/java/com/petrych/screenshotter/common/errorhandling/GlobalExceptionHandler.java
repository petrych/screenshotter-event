package com.petrych.screenshotter.common.errorhandling;

import com.petrych.screenshotter.service.InvalidURLException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	public GlobalExceptionHandler() {
		
		super();
	}
	
	
	@ExceptionHandler(InvalidURLException.class)
	public ResponseEntity<Object> handleInvalidURLException(InvalidURLException ex) {
		
		String devMessage = ExceptionUtils.getRootCauseMessage(ex);
		
		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), devMessage, ex.toString());
		
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}
	
}
