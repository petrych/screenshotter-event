package com.petrych.screenshotter.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class UrlUtil {
	
	static String parseUrlString(String urlString) {
		
		return urlString.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "")
		                .replaceAll("\\W|_", "-")
		                .replaceAll("-{2,}", "-")
		                .replaceAll("-$", "");
	}
	
	static boolean isUrlValid(String urlString) throws InvalidURLException {
		
		try {
			URL siteURL = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000);
			connection.connect();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			}
			
			
			
		} catch (IOException e) {
			String message = String.format("Invalid URL: %s", urlString);
			throw new InvalidURLException(message, e.getCause());
		}
		
		return false;
	}
}
