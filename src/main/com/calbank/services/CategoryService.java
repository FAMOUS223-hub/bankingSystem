package com.calbank.services;

import com.calbank.database.DatabaseManager;
import com.calbank.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class CategoryService {

    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public List<Category> getDefaultCategories() {
        return getCategoriesByUserId(0);
    }

    public List<Category> getCategoriesByUserId(int userId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE user_id = 0 OR user_id = ? ORDER BY is_default DESC, name ASC";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch categories: " + e.getMessage(), e);
        }
        return categories;
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCategory(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch category: " + e.getMessage(), e);
        }
        return null;
    }

    public Category createCategory(int userId, String name, String icon, String color) {
        String sql = "INSERT INTO categories (user_id, name, icon, color, is_default) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, icon != null ? icon : "\u2753");
            ps.setString(4, color != null ? color : "#4CAF50");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return getCategoryById(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create category: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean updateCategory(int categoryId, String name, String icon, String color) {
        String sql = "UPDATE categories SET name = ?, icon = ?, color = ? WHERE category_id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, icon);
            ps.setString(3, color);
            ps.setInt(4, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update category: " + e.getMessage(), e);
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ? AND is_default = 0";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete category: " + e.getMessage(), e);
        }
    }

    public Category findOrCreateCategory(int userId, String name) {
        String sql = "SELECT * FROM categories WHERE (user_id = 0 OR user_id = ?) AND name = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCategory(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category: " + e.getMessage(), e);
        }
        return createCategory(userId, name, "\u2753", "#4CAF50");
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setName(rs.getString("name"));
        c.setIcon(rs.getString("icon"));
        c.setColor(rs.getString("color"));
        c.setDefault(rs.getInt("is_default") == 1);
        return c;
    }
}
