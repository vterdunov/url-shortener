package org.shortener;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UrlService {
    private final UrlRepository repository;
    private final UrlGenerator generator;
    private final UrlValidator validator;
    private final Config config;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
        this.generator = new UrlGenerator();
        this.validator = new UrlValidator();
        this.config = Config.getInstance();
    }

    public UrlRepository getRepository() {
        return repository;
    }

    public ShortUrl createShortUrl(String originalUrl, String userId, Integer lifetimeDays, Integer clickLimit) {
        validator.validateUrl(originalUrl);

        LocalDateTime now = LocalDateTime.now();

        // Определяем время жизни ссылки
        int actualLifetimeDays;
        if (lifetimeDays == null) {
            actualLifetimeDays = config.getDefaultUrlLifetimeDays();
        } else {
            actualLifetimeDays = Math.min(lifetimeDays, config.getMaxUrlLifetimeDays());
        }
        LocalDateTime expiresAt = now.plusDays(actualLifetimeDays);

        // Определяем лимит кликов
        int actualClickLimit;
        if (clickLimit == null) {
            actualClickLimit = config.getDefaultUrlClicks();
        } else {
            actualClickLimit = Math.min(clickLimit, config.getMaxUrlClicks());
        }

        String shortUrl = generator.generateUniqueShortUrl(repository);
        ShortUrl url = new ShortUrl(shortUrl, originalUrl, userId, expiresAt, actualClickLimit);
        repository.save(url);
        return url;
    }

    public String getOriginalUrl(String shortUrl) {
        ShortUrl url = repository.findByShortUrl(shortUrl);
        if (url == null) {
            throw new UrlNotFoundException("URL not found: " + shortUrl);
        }

        // Проверяем срок действия и удаляем если истек
        if (url.isExpired()) {
            repository.remove(shortUrl);
            throw new UrlNotFoundException("URL has expired: " + shortUrl);
        }
        if (url.isLimitReached()) {
            repository.remove(shortUrl);
            throw new UrlNotFoundException("Click limit reached for URL: " + shortUrl);
        }

        url.incrementClickCount();
        repository.save(url);
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
        List<ShortUrl> userUrls = repository.findByUserId(userId);
        List<ShortUrl> activeUrls = new ArrayList<>();

        // Фильтруем и удаляем просроченные ссылки
        for (ShortUrl url : userUrls) {
            if (url.isExpired() || url.isLimitReached()) {
                repository.remove(url.getShortUrl());
            } else {
                activeUrls.add(url);
            }
        }

        return activeUrls;
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
}