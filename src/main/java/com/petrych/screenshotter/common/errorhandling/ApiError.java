package com.petrych.screenshotter.common.errorhandling;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ApiError {
	
	private LocalDateTime dateTimeUTC;
	
	private int status;
	
	private String message;
	
	private String path;
	
	public ApiError(final int httpStatusCode, final String message, final String path) {
		
		this.dateTimeUTC = LocalDateTime.now(ZoneOffset.UTC);
		this.status = httpStatusCode;
		this.message = message;
		this.path = path;
	}
	
	public int getStatus() {
		
		return status;
	}
	
	public void setStatus(final int status) {
		
		this.status = status;
	}
	
	public String getMessage() {
		
		return message;
	}
	
	public void setMessage(final String message) {
		
		this.message = message;
	}
	
	public String getPath() {
		
		return path;
	}
	
	public void setPath(final String path) {
		
		this.path = path;
	}
	
	@Override
	public final String toString() {
		
		final StringBuilder builder = new StringBuilder();
		builder.append("ApiError [dateTime = ").append(dateTimeUTC)
		       .append(", status = '").append(status)
		       .append(", message = '").append(message)
		       .append("', path = '").append(path)
		       .append("']");
		
		return builder.toString();
	}
	
}
