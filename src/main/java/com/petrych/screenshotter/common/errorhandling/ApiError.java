package com.petrych.screenshotter.common.errorhandling;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ApiError {
	
	public LocalDateTime dateTimeUTC;
	
	public int status;
	
	public String message;
	
	public String path;
	
	public ApiError(int httpStatusCode, String message, String path) {
		
		this.dateTimeUTC = LocalDateTime.now(ZoneOffset.UTC);
		this.status = httpStatusCode;
		this.message = message;
		this.path = path;
	}
	
	@Override
	public final String toString() {
		
		final StringBuilder builder = new StringBuilder();
		builder.append("ApiError [dateTime = ").append(dateTimeUTC)
		       .append(", status = ").append(status)
		       .append(", message = '").append(message)
		       .append("', path = '").append(path)
		       .append("']");
		
		return builder.toString();
	}
	
}
