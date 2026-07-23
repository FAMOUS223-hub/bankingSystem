package com.calbank.models;

import java.time.LocalDateTime;

public final class Category {

    private int categoryId;
    private int userId;
    private String name;
    private String icon;
    private String color;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public Category() {}

    public Category(int userId, String name, String icon, String color) {
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.isDefault = false;
    }

    public int getCategoryId()                 { return categoryId; }
    public void setCategoryId(int id)          { this.categoryId = id; }
    public int getUserId()                     { return userId; }
    public void setUserId(int id)              { this.userId = id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getIcon()                    { return icon; }
    public void setIcon(String icon)           { this.icon = icon; }
    public String getColor()                   { return color; }
    public void setColor(String color)         { this.color = color; }
    public boolean isDefault()                 { return isDefault; }
    public void setDefault(boolean def)        { this.isDefault = def; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }

    @Override
    public String toString() {
        return (icon != null ? icon + " " : "") + name;
    }
}
