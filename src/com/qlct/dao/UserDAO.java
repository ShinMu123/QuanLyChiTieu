package com.qlct.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qlct.db.DBConnection;
import com.qlct.model.User;

public class UserDAO {
    private static final String LOGIN_SQL = """
            SELECT UserID, Username, PasswordHash, FullName, Avatar
            FROM USERS
            WHERE Username = ?
            """;

    private static final String GET_BY_ID_SQL = """
            SELECT UserID, Username, FullName, Avatar
            FROM USERS
            WHERE UserID = ?
            """;

    public User login(String username, String rawPassword) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(LOGIN_SQL)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String storedHash = rs.getString("PasswordHash");
                if (!passwordMatches(rawPassword, storedHash)) {
                    return null;
                }
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to authenticate user", e);
        }
    }

    public User getById(int userId) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load user", e);
        }
    }

    private boolean passwordMatches(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        String hashedInput = hashPassword(rawPassword);
        if (storedHash.equalsIgnoreCase(hashedInput)) {
            return true;
        }
        return storedHash.equals(rawPassword);
    }

    private String hashPassword(String rawPassword) {
        if (rawPassword == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }


    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setFullName(rs.getString("FullName"));
        user.setAvatar(rs.getString("Avatar"));
        return user;
    }
}








