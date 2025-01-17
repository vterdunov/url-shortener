package org.shortener;

import java.time.LocalDateTime;

public class ShortUrl {
    private final String shortUrl;
    private final String originalUrl;
    private final String userId;
    private LocalDateTime expiresAt;
    private int clickLimit;
    private int clickCount;

    public ShortUrl(String shortUrl, String originalUrl, String userId, LocalDateTime expiresAt, int clickLimit) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.userId = userId;
        this.expiresAt = expiresAt;
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

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getClickLimit() {
        return clickLimit;
    }

    public void setClickLimit(int clickLimit) {
        this.clickLimit = clickLimit;
    }

    public int getClickCount() {
        return clickCount;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isLimitReached() {
        return clickLimit > 0 && clickCount >= clickLimit;
    }

    public void incrementClickCount() {
        clickCount++;
    }
}