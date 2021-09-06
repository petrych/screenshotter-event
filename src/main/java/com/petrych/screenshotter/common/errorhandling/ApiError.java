package com.petrych.screenshotter.common.errorhandling;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class ApiError {
	
	private HttpStatus status;
	
	private String message;
	
	private String developerMessage;
	
	private List<String> errors;
	
	
	public ApiError(final HttpStatus status, final String message, final String developerMessage,
	                final List<String> errors) {
		
		this.status = status;
		this.message = message;
		this.developerMessage = developerMessage;
		this.errors = errors;
	}
	
	public ApiError(final HttpStatus status, final String message, final String developerMessage, final String error) {
		
		this.status = status;
		this.message = message;
		this.developerMessage = developerMessage;
		this.errors = Arrays.asList(error);
	}
	
	public ApiError(final HttpStatus status, final String message, final List<String> errors) {
		
		this.status = status;
		this.message = message;
		this.errors = errors;
	}
	
	public ApiError(final HttpStatus status, final String message, final String error) {
		
		this.status = status;
		this.message = message;
		this.errors = Arrays.asList(error);
	}
	
	public HttpStatus getStatus() {
		
		return status;
	}
	
	public void setStatus(final HttpStatus status) {
		
		this.status = status;
	}
	
	public String getMessage() {
		
		return message;
	}
	
	public void setMessage(final String message) {
		
		this.message = message;
	}
	
	public String getDeveloperMessage() {
		
		return developerMessage;
	}
	
	public void setDeveloperMessage(final String developerMessage) {
		
		this.developerMessage = developerMessage;
	}
	
	public List<String> getErrors() {
		
		return errors;
	}
	
	public void setErrors(final List<String> errors) {
		
		this.errors = errors;
	}
	
	public void setError(final String error) {
		
		errors = Arrays.asList(error);
	}
	
	
	@Override
	public final String toString() {
		
		final StringBuilder builder = new StringBuilder();
		builder.append("ApiExceptionMessage [status = ").append(status)
		       .append(", message = ").append(message)
		       .append("developerMessage: ").append(developerMessage)
		       .append(errors)
		       .append("]");
		
		return builder.toString();
	}
	
}
