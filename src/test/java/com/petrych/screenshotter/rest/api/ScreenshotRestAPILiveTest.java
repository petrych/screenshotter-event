package com.petrych.screenshotter.rest.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.JsonPathAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.hamcrest.Matchers.*;

@SpringBootTest
public class ScreenshotRestAPILiveTest {
	
	private static final String BASE_URL = "http://localhost:8080/screenshotter/screenshots/";
	
	private static WebTestClient client;
	
	@BeforeAll
	public static void init() {
		
		client = WebTestClient
				.bindToServer()
				.baseUrl(BASE_URL)
				.responseTimeout(Duration.ofMillis(30000))
				.build();
	}
	
//	@Test
	public void givenScreenshotsExist_whenfindAll_thenSuccess() {
		
		WebTestClient.BodyContentSpec bodyContentSpec =
				client.get()
				      .exchange()
				      .expectStatus()
				      .isOk()
				      .expectBody();
		
		bodyContentSpec
				.jsonPath("$")
				.isArray();
		
		JsonPathAssertions jsonPath = bodyContentSpec.jsonPath("$.[0].uri");
		
		jsonPath.value(matchesPattern(BASE_URL.concat("\\d+")));
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
		
		WebTestClient.BodyContentSpec bodyContentSpec =
				client.get()
				      .uri(uriBuilder -> uriBuilder
						      .path("/by-name/")
						      .queryParam("name",
						                  "goo")
						      .build())
				      .exchange()
				      .expectStatus()
				      .isOk()
				      .expectBody();
		
		bodyContentSpec
				.jsonPath("$")
				.isArray();
		
		JsonPathAssertions jsonPath = bodyContentSpec.jsonPath("$[0].name");
		
		jsonPath.value(containsStringIgnoringCase("goo"));
		jsonPath.value(endsWithIgnoringCase("com.png"));
	}
	
//	@Test
	public void givenValidUrl_whenStore_thenSuccess() {
		
		client.mutate()
		      .responseTimeout(Duration.ofMillis(30000))
		      .build()
		      .post()
		      .bodyValue("https://www.apple.com/")
		      .exchange()
		      .expectStatus()
		      .isEqualTo(HttpStatus.CREATED);
		
		client.get()
		      .uri(uriBuilder -> uriBuilder
				      .path("/by-name/")
				      .queryParam("name",
				                  "apple")
				      .build())
		      .exchange()
		      .expectStatus()
		      .isOk()
		      .expectBody()
		      .jsonPath("$[0].name")
		      .value(containsStringIgnoringCase("apple"));
	}
	
	@Test
	void givenScreenshotWithUrlExists_whenUpdate_thenUpdateExistingScreenshot() {
		
		client.put()
		      .bodyValue("https://www.drive.google.com/")
		      .exchange()
		      .expectStatus()
		      .isOk();
		
		client.get()
		      .uri(uriBuilder -> uriBuilder
				      .path("/by-name/")
				      .queryParam("name",
				                  "drive")
				      .build())
		      .exchange()
		      .expectStatus()
		      .isOk()
		      .expectBody()
		      .jsonPath("$[0].name")
		      .value(containsStringIgnoringCase("drive"));
		
		client.put()
		      .bodyValue("https://www.drive.google.com/")
		      .exchange()
		      .expectStatus()
		      .isOk();
	}
	
	@Test
	void givenScreenshotWithUrlNotExists_whenUpdate_thenCreateNewScreenshot() {
		
		client.get()
		      .uri(uriBuilder -> uriBuilder
				      .path("/by-name/")
				      .queryParam("name",
				                  "spring")
				      .build())
		      .exchange()
		      .expectStatus()
		      .isOk()
		      .expectBody()
		      .json("[]");
		
		client.put()
		      .bodyValue("https://docs.spring.io/")
		      .exchange()
		      .expectStatus()
		      .isOk();
		
		client.get()
		      .uri(uriBuilder -> uriBuilder
				      .path("/by-name/")
				      .queryParam("name",
				                  "spring")
				      .build())
		      .exchange()
		      .expectStatus()
		      .isOk()
		      .expectBody()
		      .jsonPath("$[0].name")
		      .value(containsStringIgnoringCase("spring"));
	}
	
}
