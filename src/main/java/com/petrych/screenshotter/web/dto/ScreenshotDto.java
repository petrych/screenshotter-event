package com.petrych.screenshotter.web.dto;

import java.time.LocalDate;

public class ScreenshotDto {
	
	private Long id;
	
	private String name;
	
	private LocalDate dateCreated;
	
	public ScreenshotDto() {
	
	}
	
	public ScreenshotDto(Long id, String name) {
		
		this(id,
		     name,
		     LocalDate.now());
	}
	
	public ScreenshotDto(Long id, String name, LocalDate dateCreated) {
		
		this.id = id;
		this.name = name;
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
	
	public LocalDate getDateCreated() {
		
		return dateCreated;
	}
	
	public void setDateCreated(LocalDate dateCreated) {
		
		this.dateCreated = dateCreated;
	}
	
}
