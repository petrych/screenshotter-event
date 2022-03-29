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
	
	protected Screenshot() {
	
	}
	
	public Screenshot(String name) {
		
		this.name = name;
		this.dateTimeCreated = LocalDateTime.now();
	}
	
	public Screenshot(Long id, String name, String uri, LocalDateTime dateTimeCreated) {
		
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.dateTimeCreated = dateTimeCreated;
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
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Screenshot that = (Screenshot) o;
		
		return id.equals(that.id) &&
				name.equals(that.name) &&
				uri.equals(that.uri) &&
				dateTimeCreated.equals(that.dateTimeCreated);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id, name, uri, dateTimeCreated);
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
