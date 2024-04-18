package com.example.jsonpreferences;

public class AppPreferences {
    private boolean isDarkModeEnabled;
    private String username;
    private int currentLevel;

    AppPreferences() {
    }

    AppPreferences(boolean isDarkModeEnabled, String username, int currentLevel) {
        this.isDarkModeEnabled = isDarkModeEnabled;
        this.username = username;
        this.currentLevel = currentLevel;
    }

    public boolean isDarkModeEnabled() {
        return isDarkModeEnabled;
    }

    public String getUsername() {
        return username;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
