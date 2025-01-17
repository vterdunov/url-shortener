package org.shortener;

import java.time.LocalDateTime;

public class ShortUrl {
    private final String shortUrl;
    private final String originalUrl;
    private final String userId;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private final int clickLimit; // максимальное количество переходов
    private int clickCount; // текущее количество переходов

    public ShortUrl(String shortUrl, String originalUrl, String userId, LocalDateTime expiresAt, int clickLimit) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.clickLimit = clickLimit;
        this.clickCount = 0;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getClickCount() {
        return clickCount;
    }

    public int getClickLimit() {
        return clickLimit;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isLimitReached() {
        return clickCount >= clickLimit;
    }
}