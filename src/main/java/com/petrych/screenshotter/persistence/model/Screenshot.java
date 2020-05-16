package com.petrych.screenshotter.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Screenshot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String uri;
	
	private LocalDate dateCreated;
	
	protected Screenshot() {
	
	}
	
	public Screenshot(String name, String uri) {
		
		this.name = name;
		this.uri = uri;
		this.dateCreated = LocalDate.now();
	}
	
	public Screenshot(Long id, String name, String uri, LocalDate dateCreated) {
		
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
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Screenshot that = (Screenshot) o;
		
		return id.equals(that.id) &&
				name.equals(that.name) &&
				uri.equals(that.uri) &&
				dateCreated.equals(that.dateCreated);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id, name, uri, dateCreated);
	}
	
	@Override
	public String toString() {
		
		return "Screenshot [" +
				"id=" + id +
				", name='" + name + '\'' +
				", uri='" + uri + '\'' +
				", dateCreated=" + dateCreated +
				']';
	}
	
}
