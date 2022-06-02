package com.petrych.screenshotter.persistence.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
public class Screenshot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@Transient
	private String uri;
	
	private LocalDateTime dateTimeCreated;
	
	@Column(unique = true, updatable = false)
	private final String fileName;
	
	public Screenshot(String name, String fileName) {
		
		this.name = name;
		this.dateTimeCreated = LocalDateTime.now(ZoneOffset.UTC);
		this.fileName = fileName;
	}
	
	public Screenshot(String name, LocalDateTime dateTimeCreated, String fileName) {
		
		this.name = name;
		this.dateTimeCreated = dateTimeCreated;
		this.fileName = fileName;
	}
	
	// For Hibernate only
	private Screenshot() {
		this.fileName = null;
	}
	
	// For Hibernate only
	private Screenshot(String fileName) {
		
		this.fileName = fileName;
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
	
	public LocalDateTime getDateTimeCreated() {
		
		return dateTimeCreated;
	}
	
	public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
		
		this.dateTimeCreated = dateTimeCreated;
	}
	
	public String getFileName() {
		
		return fileName;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Screenshot that = (Screenshot) o;
		
		return id.equals(that.id);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id, name, uri, dateTimeCreated, fileName);
	}
	
	@Override
	public String toString() {
		
		return "Screenshot [" +
				"id=" + id +
				", name='" + name +
				"', dateTimeCreated=" + dateTimeCreated +
				']';
	}
	
	public String toLogString() {
		
		return "Screenshot [" +
				"id=" + id +
				", name='" + name +
				"', fileName='" + fileName +
				"', dateTimeCreated=" + dateTimeCreated +
				']';
	}
	
}
