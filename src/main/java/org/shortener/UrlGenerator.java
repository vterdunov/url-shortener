package org.shortener;

import java.security.SecureRandom;


// Generator for short URL strings
public class UrlGenerator {
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int URL_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public String generateShortUrl() {
        StringBuilder shortUrl = new StringBuilder(URL_LENGTH);
        for (int i = 0; i < URL_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARS.length());
            shortUrl.append(ALLOWED_CHARS.charAt(randomIndex));
        }

        return shortUrl.toString();
    }

    // Generates unique short URL across the repository
    public String generateUniqueShortUrl(UrlRepository repository) {
        String shortUrl;
        do {
            shortUrl = generateShortUrl();
        } while (repository.exists(shortUrl));

        return shortUrl;
    }
}
