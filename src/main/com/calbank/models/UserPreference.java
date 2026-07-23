package com.calbank.models;

public final class UserPreference {

    private int prefId;
    private int userId;
    private String theme;
    private boolean notificationsEnabled;

    public UserPreference() {
        this.theme = "LIGHT";
        this.notificationsEnabled = true;
    }

    public UserPreference(int userId) {
        this.userId = userId;
        this.theme = "LIGHT";
        this.notificationsEnabled = true;
    }

    public int getPrefId()                           { return prefId; }
    public void setPrefId(int id)                    { this.prefId = id; }
    public int getUserId()                           { return userId; }
    public void setUserId(int id)                    { this.userId = id; }
    public String getTheme()                         { return theme; }
    public void setTheme(String theme)               { this.theme = theme; }
    public boolean isNotificationsEnabled()          { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean en)  { this.notificationsEnabled = en; }
}
