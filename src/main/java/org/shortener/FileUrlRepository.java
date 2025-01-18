package org.shortener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.nio.file.*;

// FileUrlRepository saves links into files
public class FileUrlRepository implements UrlRepository {
    private static final String STORAGE_DIR = "url_storage";
    private final Gson gson;
    private Map<String, ShortUrl> urls;
    private String currentUserId;

    public FileUrlRepository() {
        this.gson = createGson();
        this.urls = new HashMap<>();
        createStorageDirectory();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    private void createStorageDirectory() {
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        } catch (IOException e) {
            throw new UrlStorageException("Failed to create storage directory", e);
        }
    }

    public void loadUserData(String userId) {
        this.currentUserId = userId;
        Path filePath = getUserFilePath();

        if (Files.exists(filePath)) {
            try (Reader reader = Files.newBufferedReader(filePath)) {
                Type type = new TypeToken<HashMap<String, ShortUrl>>(){}.getType();
                urls = gson.fromJson(reader, type);
                if (urls == null) {
                    urls = new HashMap<>();
                }
            } catch (IOException e) {
                throw new UrlStorageException("Failed to load user data", e);
            }
        } else {
            urls = new HashMap<>();
        }
    }

    private void saveUserData() {
        if (currentUserId == null) {
            throw new UrlStorageException("No user selected");
        }

        try (Writer writer = Files.newBufferedWriter(getUserFilePath())) {
            gson.toJson(urls, writer);
        } catch (IOException e) {
            throw new UrlStorageException("Failed to save user data", e);
        }
    }

    private Path getUserFilePath() {
        return Paths.get(STORAGE_DIR, currentUserId + ".json");
    }

    @Override
    public void save(ShortUrl url) {
        urls.put(url.getShortUrl(), url);
        saveUserData();
    }

    @Override
    public ShortUrl findByShortUrl(String shortUrl) {
        return urls.get(shortUrl);
    }

    @Override
    public List<ShortUrl> findByUserId(String userId) {
        return urls.values().stream()
                .filter(url -> url.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void remove(String shortUrl) {
        urls.remove(shortUrl);
        saveUserData();
    }

    @Override
    public boolean exists(String shortUrl) {
        return urls.containsKey(shortUrl);
    }

    @Override
    public void removeExpired() {
        List<String> expiredUrls = urls.values().stream()
                .filter(ShortUrl::isExpired)
                .map(ShortUrl::getShortUrl)
                .toList();

        expiredUrls.forEach(urls::remove);

        if (!expiredUrls.isEmpty()) {
            saveUserData();
        }
    }
}
