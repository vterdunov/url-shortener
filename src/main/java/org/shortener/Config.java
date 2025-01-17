package org.shortener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static Config instance;

    private Config() {
        loadProperties();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Error loading config.properties: " + ex.getMessage());
        }
    }

    public int getDefaultUrlLifetimeDays() {
        return Integer.parseInt(properties.getProperty("default_url_lifetime_days", "30"));
    }

    public int getMaxUrlLifetimeDays() {
        return Integer.parseInt(properties.getProperty("max_url_lifetime_days", "60"));
    }

    public int getDefaultUrlClicks() {
        return Integer.parseInt(properties.getProperty("default_url_clicks", "10"));
    }

    public int getMaxUrlClicks() {
        return Integer.parseInt(properties.getProperty("max_url_clicks", "1000"));
    }
}