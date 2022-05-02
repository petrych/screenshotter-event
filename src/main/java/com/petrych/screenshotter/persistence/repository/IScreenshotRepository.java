package com.petrych.screenshotter.persistence.repository;

import com.petrych.screenshotter.persistence.model.Screenshot;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface IScreenshotRepository extends PagingAndSortingRepository<Screenshot, Long> {
	
	/**
	 * @param name is used to fix an IllegalArgumentException "Parameter value [\] did not match expected type [java.lang.String (n/a)]"
	 *
	 * Source: https://github.com/spring-projects/spring-data-jpa/issues/2472
	 */
	Iterable<Screenshot> findByNameContaining(@Param("name") String name);
	
	Screenshot findByFileName(String fileName);
	
}
