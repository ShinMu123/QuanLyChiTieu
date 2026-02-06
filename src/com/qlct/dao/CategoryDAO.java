package com.qlct.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.qlct.db.DBConnection;
import com.qlct.model.Category;

public class CategoryDAO {
    private static final String SELECT_BY_USER_SQL = """
            SELECT CategoryID, CategoryName, Type
            FROM CATEGORIES
            WHERE UserID = ?
            ORDER BY CategoryName
            """;

    private static final String SELECT_BY_USER_AND_TYPE_SQL = """
            SELECT CategoryID, CategoryName, Type
            FROM CATEGORIES
            WHERE UserID = ? AND Type = ?
            ORDER BY CategoryName
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT CategoryID, CategoryName, Type
            FROM CATEGORIES
            WHERE CategoryID = ?
            """;

        private static final String SELECT_BY_NAME_SQL = """
            SELECT CategoryID, CategoryName, Type
            FROM CATEGORIES
            WHERE UserID = ? AND Type = ? AND CategoryName = ?
            """;

        private static final String INSERT_SQL = """
            INSERT INTO CATEGORIES (UserID, Type, CategoryName, CreatedAt)
            VALUES (?, ?, ?, SYSDATETIME())
            """;

    public List<Category> getByUser(int userId) {
        return queryCategories(userId, null);
    }

    public List<Category> getByUserAndType(int userId, String type) {
        if (type == null || type.isBlank()) {
            return getByUser(userId);
        }
        return queryCategories(userId, type.trim());
    }

    public Category getById(int categoryId) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setInt(1, categoryId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load category", e);
        }
    }

    public Category findOrCreate(int userId, String type, String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
        String normalizedType = type == null ? "" : type.trim();
        String normalizedName = rawName.trim();

        Category existing = findByName(userId, normalizedType, normalizedName);
        if (existing != null) {
            return existing;
        }
        return insert(userId, normalizedType, normalizedName);
    }

    private List<Category> queryCategories(int userId, String type) {
        List<Category> categories = new ArrayList<>();
        String sql = type == null ? SELECT_BY_USER_SQL : SELECT_BY_USER_AND_TYPE_SQL;
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            if (type != null) {
                statement.setString(2, type);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load categories", e);
        }
        return deduplicate(categories);
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("CategoryID"));
        category.setName(rs.getNString("CategoryName"));
        category.setType(rs.getString("Type"));
        return category;
    }

    private Category findByName(int userId, String type, String name) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_SQL)) {
            statement.setInt(1, userId);
            statement.setString(2, type);
            statement.setNString(3, name);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to query category by name", e);
        }
    }

    private Category insert(int userId, String type, String name) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setString(2, type);
            statement.setNString(3, name);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Category(keys.getInt(1), name, type);
                }
            }
            throw new SQLException("Failed to retrieve generated key for category");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create category", e);
        }
    }

    private List<Category> deduplicate(List<Category> categories) {
        Map<String, Category> unique = new LinkedHashMap<>();
        for (Category category : categories) {
            String key = normalizeKey(category.getName(), category.getType());
            unique.putIfAbsent(key, category);
        }
        return new ArrayList<>(unique.values());
    }

    private String normalizeKey(String name, String type) {
        String safeName = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        String safeType = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
        return safeType + "|" + safeName;
    }
}








