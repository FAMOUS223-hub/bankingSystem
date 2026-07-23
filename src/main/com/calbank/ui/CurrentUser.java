package com.calbank.ui;

import com.calbank.models.User;

public final class CurrentUser {

    private static CurrentUser instance;
    private User user;

    private CurrentUser() {}

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public User getUser()             { return user; }
    public void setUser(User user)    { this.user = user; }
    public boolean isLoggedIn()       { return user != null; }
    public boolean isAdmin()          { return user != null && user.isAdmin(); }

    public void logout() {
        this.user = null;
    }
}
