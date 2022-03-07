package com.petrych.screenshotter.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {
	
	public static final int URL_LENGTH_MAX = 2048;
	public static final String URL_IS_TOO_LONG_MESSAGE = String.format("URL length is over %d characters.", URL_LENGTH_MAX);
	public static final String CANNOT_REACH_THE_URL_MESSAGE = "Cannot reach the URL: ";
	
	private UrlUtil() {}
	
	public static String parseUrlString(String urlString) {
		
		return urlString.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "")
		                .replaceAll("\\W|_", "-")
		                .replaceAll("-{2,}", "-")
		                .replaceAll("-$", "");
	}
	
	public static boolean isUrlValid(String urlString) throws MalformedURLException {
		
		if (urlString.length() >= URL_LENGTH_MAX) {
			throw new MalformedURLException(URL_IS_TOO_LONG_MESSAGE);
		}
		
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
			String message = String.format(CANNOT_REACH_THE_URL_MESSAGE + " %s", urlString);
			throw new MalformedURLException(message);
		}
		
		return false;
	}
}
