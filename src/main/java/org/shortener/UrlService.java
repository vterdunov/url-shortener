package org.shortener;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

public class UrlService {
    private final UrlRepository repository;
    private final UrlGenerator generator;
    private final UrlValidator validator;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
        this.generator = new UrlGenerator();
        this.validator = new UrlValidator();
    }

    public UrlRepository getRepository() {
        return repository;
    }

    public ShortUrl createShortUrl(String originalUrl, String userId, LocalDateTime expiresAt, int clickLimit) {
        validator.validateUrl(originalUrl);

        String shortUrl = generator.generateUniqueShortUrl(repository);
        ShortUrl url = new ShortUrl(shortUrl, originalUrl, userId, expiresAt, clickLimit);
        repository.save(url);
        return url;
    }

    public String getOriginalUrl(String shortUrl) {
        ShortUrl url = repository.findByShortUrl(shortUrl);
        if (url == null) {
            throw new UrlNotFoundException("URL not found: " + shortUrl);
        }
        if (url.isExpired()) {
            throw new UrlNotFoundException("URL has expired: " + shortUrl);
        }
        if (url.isLimitReached()) {
            throw new UrlNotFoundException("Click limit reached for URL: " + shortUrl);
        }

        url.incrementClickCount();
        repository.save(url); // Сохраняем обновленный счетчик
        return url.getOriginalUrl();
    }

    public void openUrl(String shortUrl) {
        try {
            String originalUrl = getOriginalUrl(shortUrl);
            Desktop.getDesktop().browse(new URI(originalUrl));
        } catch (Exception e) {
            throw new UrlException("Failed to open URL: " + shortUrl, e);
        }
    }

    public List<ShortUrl> getUserUrls(String userId) {
        return repository.findByUserId(userId);
    }

    public void removeUrl(String shortUrl, String userId) {
        ShortUrl url = repository.findByShortUrl(shortUrl);
        if (url == null) {
            throw new UrlNotFoundException("URL not found: " + shortUrl);
        }
        if (!url.getUserId().equals(userId)) {
            throw new UrlAccessDeniedException("Access denied for URL: " + shortUrl);
        }
        try {
            repository.remove(shortUrl);
        } catch (Exception e) {
            throw new UrlStorageException("Failed to remove URL: " + shortUrl, e);
        }
    }

    public void removeExpiredUrls() {
        repository.removeExpired();
    }
}