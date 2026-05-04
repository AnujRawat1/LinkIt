package com.linkit.tests.config;

public final class TestConfig {
    private TestConfig() {
    }

    public static final String API_BASE_URL = read("LINKIT_API_BASE_URL", "http://localhost:8080");
    public static final String UI_BASE_URL = read("LINKIT_UI_BASE_URL", "http://localhost:5173");
    public static final String BROWSER = read("BROWSER", "chrome");
    public static final boolean HEADLESS = Boolean.parseBoolean(read("HEADLESS", "false"));
    public static final int DEFAULT_TIMEOUT_SECONDS = Integer.parseInt(read("DEFAULT_TIMEOUT_SECONDS", "20"));

    private static String read(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }
}

