package com.petrych.screenshotter.rest.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.containsStringIgnoringCase;

@SpringBootTest
public class ScreenshotRestAPILiveTest {
	
	private static final String BASE_URL = "http://localhost:8083/screenshotter/screenshots/";
	
	private static WebTestClient client;
	
	@BeforeAll
	public static void init() {
		
		client = WebTestClient
				.bindToServer()
				.baseUrl(BASE_URL)
				.build();
	}
	
	@Test
	public void givenScreenshotsExist_whenfindAll_thenSuccess() {
		
		client.get()
		      .exchange()
		      .expectStatus()
		      .isOk()
		      .expectBody()
		      .jsonPath("$[0]")
		      .value(containsStringIgnoringCase("/google-com.png"));
	}
	
	@Test
	public void givenScreenshotExists_whenfindById_thenSuccess() {
		
		client.get()
		      .uri("1")
		      .accept(MediaType.IMAGE_PNG)
		      .exchange()
		      .expectStatus()
		      .isOk();
	}
	
	@Test
	public void givenScreenshotExists_whenfindByName_thenSuccess() {
		
		client.get()
		      .uri(uriBuilder -> uriBuilder
				      .path("/by-name/")
				      .queryParam("name",
				                  "goo")
				      .build())
		      .exchange()
		      .expectStatus()
		      .isOk()
		      .expectBody()
		      .jsonPath("$[0].name")
		      .value(containsStringIgnoringCase("goo"));
	}
	
}
