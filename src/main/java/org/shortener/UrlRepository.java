package org.shortener;

import java.util.List;

public interface UrlRepository {
    void save(ShortUrl url);
    ShortUrl findByShortUrl(String shortUrl);
    List<ShortUrl> findByUserId(String userId);
    void remove(String shortUrl);
    boolean exists(String shortUrl);
    void removeExpired();
}