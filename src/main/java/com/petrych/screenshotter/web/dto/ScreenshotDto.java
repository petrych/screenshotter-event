package com.petrych.screenshotter.web.dto;

import java.time.LocalDate;

public class ScreenshotDto {
	
	private Long id;
	
	private String name;
	
	private String uri;
	
	private LocalDate dateCreated;
	
	public ScreenshotDto() {
	
	}
	
	public ScreenshotDto(Long id, String name, String uri) {
		
		this(id,
		     name,
		     uri,
		     LocalDate.now());
	}
	
	public ScreenshotDto(Long id, String name, String uri, LocalDate dateCreated) {
		
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.dateCreated = dateCreated;
	}
	
	
	public Long getId() {
		
		return id;
	}
	
	public void setId(Long id) {
		
		this.id = id;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getUri() {
		
		return uri;
	}
	
	public void setUri(String uri) {
		
		this.uri = uri;
	}
	
	public LocalDate getDateCreated() {
		
		return dateCreated;
	}
	
	public void setDateCreated(LocalDate dateCreated) {
		
		this.dateCreated = dateCreated;
	}
	
}
