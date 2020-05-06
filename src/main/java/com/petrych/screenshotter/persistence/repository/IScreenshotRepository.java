package com.petrych.screenshotter.persistence.repository;

import com.petrych.screenshotter.persistence.model.Screenshot;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IScreenshotRepository extends PagingAndSortingRepository<Screenshot, Long> {
	
	Iterable<Screenshot> findByNameContaining(String name);
	
}
