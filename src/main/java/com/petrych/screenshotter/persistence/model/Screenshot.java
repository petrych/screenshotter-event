package com.petrych.screenshotter.persistence.model;

import javax.persistence.*;
import java.time.LocalDateTime;
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
	
	@Column(unique = true)
	private String fileName;
	
	protected Screenshot() {
	
	}
	
	public Screenshot(String name, String fileName) {
		
		this.name = name;
		this.dateTimeCreated = LocalDateTime.now();
		this.fileName = fileName;
	}
	
	public Screenshot(String name, LocalDateTime dateTimeCreated, String fileName) {
		
		this.name = name;
		this.dateTimeCreated = dateTimeCreated;
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
	
	public void setFileName(String fileName) {
		
		this.fileName = fileName;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Screenshot that = (Screenshot) o;
		
		return id.equals(that.id) &&
				name.equals(that.name) &&
				uri.equals(that.uri) &&
				dateTimeCreated.equals(that.dateTimeCreated) &&
				fileName.equals(that.fileName);
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
				", dateTimeCreated=" + dateTimeCreated +
				']';
	}
	
}
