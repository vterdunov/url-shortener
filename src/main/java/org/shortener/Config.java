package org.shortener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Config instance;
    private final Properties properties;

    private Config() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public int getDefaultUrlLifetimeDays() {
        return Integer.parseInt(properties.getProperty("default_url_lifetime_days", "7"));
    }

    public int getDefaultUrlClicks() {
        return Integer.parseInt(properties.getProperty("default_url_clicks", "10"));
    }
}