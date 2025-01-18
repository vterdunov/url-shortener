package org.shortener;

import java.util.List;

// UrlRepository is acontract to work witch links data
public interface UrlRepository {
    void save(ShortUrl url);
    ShortUrl findByShortUrl(String shortUrl);
    List<ShortUrl> findByUserId(String userId);
    void remove(String shortUrl);
    boolean exists(String shortUrl);
    void removeExpired();
}
