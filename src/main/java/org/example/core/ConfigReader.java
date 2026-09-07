package org.example.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        // Reads the active environment, defaulting to 'qa'
        String env = System.getProperty("env", "qa");
        String configPath = Paths.get(System.getProperty("user.dir"), "config.properties").toString();
        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("[ConfigReader] Warning: Could not load configuration from " + configPath + ". " + e.getMessage());
        }
    }

    private ConfigReader() {}

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}