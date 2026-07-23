package com.calbank.models;

import java.time.LocalDateTime;

public final class User {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    private int id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;

    public User() {
        this.role = ROLE_USER;
        this.active = true;
    }

    public User(int id, String username, String email, String fullName,
                String phone, String address) {
        this();
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.fullName = fullName;
        this.phone    = phone;
        this.address  = address;
    }

    public int getId()                                { return id; }
    public void setId(int id)                         { this.id = id; }
    public String getUsername()                       { return username; }
    public void setUsername(String username)           { this.username = username; }
    public String getEmail()                          { return email; }
    public void setEmail(String email)                { this.email = email; }
    public String getFullName()                       { return fullName; }
    public void setFullName(String name)              { this.fullName = name; }
    public String getPhone()                          { return phone; }
    public void setPhone(String phone)                { this.phone = phone; }
    public String getAddress()                        { return address; }
    public void setAddress(String address)            { this.address = address; }
    public String getRole()                           { return role; }
    public void setRole(String role)                  { this.role = role; }
    public boolean isActive()                         { return active; }
    public void setActive(boolean active)             { this.active = active; }
    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)        { this.createdAt = dt; }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public String getDisplayName() {
        return fullName != null ? fullName : username;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s [%s]", fullName, username, email, role);
    }
}
