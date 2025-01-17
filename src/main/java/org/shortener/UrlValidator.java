package org.shortener;

import org.shortener.UrlValidationException;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

/**
 * Validator for URL strings
 */
public class UrlValidator {
    private static final List<String> ALLOWED_PROTOCOLS = Arrays.asList("http", "https");
    private static final int MAX_URL_LENGTH = 2048;

    /**
     * Validates URL string
     * @return true if URL is valid, false otherwise
     */
    public boolean isValid(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        if (url.length() > MAX_URL_LENGTH) {
            return false;
        }

        try {
            URL parsedUrl = new URL(url);
            String protocol = parsedUrl.getProtocol().toLowerCase();

            if (!ALLOWED_PROTOCOLS.contains(protocol)) {
                return false;
            }

            // Проверяем наличие хоста
            if (parsedUrl.getHost().isEmpty()) {
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Validates URL string and throws exception if invalid
     * @throws IllegalArgumentException if URL is invalid
     */
    public void validateUrl(String url) {
        if (!isValid(url)) {
            throw new UrlValidationException("Invalid URL provided: " + url);
        }
    }
}