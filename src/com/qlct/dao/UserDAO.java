package com.qlct.dao;

import com.qlct.db.DBConnection;
import com.qlct.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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

        private static final String UPDATE_PROFILE_SQL = """
            UPDATE USERS
            SET FullName = ?, Avatar = ?
            WHERE UserID = ?
            """;

        private static final String INSERT_SQL = """
            INSERT INTO USERS (Username, PasswordHash, FullName, Avatar, CreatedAt)
            VALUES (?, ?, ?, ?, SYSDATETIME())
            """;

        private static final String CHECK_USERNAME_SQL = """
            SELECT 1 FROM USERS WHERE Username = ?
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

    public void updateProfile(int userId, String fullName, String avatarPath) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_PROFILE_SQL)) {
            if (fullName == null || fullName.isBlank()) {
                statement.setNull(1, Types.NVARCHAR);
            } else {
                statement.setNString(1, fullName);
            }
            if (avatarPath == null || avatarPath.isBlank()) {
                statement.setNull(2, Types.NVARCHAR);
            } else {
                statement.setString(2, avatarPath);
            }
            statement.setInt(3, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể cập nhật hồ sơ người dùng", e);
        }
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String normalized = username.trim();
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(CHECK_USERNAME_SQL)) {
            statement.setString(1, normalized);
            try (ResultSet rs = statement.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kiểm tra tên đăng nhập", e);
        }
    }

    public User register(String username, String rawPassword, String fullName) {
        String normalizedUsername = validateUsername(username);
        String normalizedFullName = normalizeFullName(fullName);
        validatePassword(rawPassword);

        try (Connection connection = DBConnection.getConnection()) {
            if (!isUsernameAvailableInternal(connection, normalizedUsername)) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
            }
            try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, normalizedUsername);
                statement.setString(2, hashPassword(rawPassword));
                if (normalizedFullName == null) {
                    statement.setNull(3, Types.NVARCHAR);
                } else {
                    statement.setNString(3, normalizedFullName);
                }
                statement.setNull(4, Types.NVARCHAR);
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        int userId = keys.getInt(1);
                        return new User(userId, normalizedUsername, normalizedFullName, null);
                    }
                }
                throw new SQLException("Không thể tạo tài khoản mới");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không thể đăng ký người dùng", e);
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

    private boolean isUsernameAvailableInternal(Connection connection, String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CHECK_USERNAME_SQL)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return !rs.next();
            }
        }
    }

    private String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        String normalized = username.trim();
        if (normalized.length() < 4) {
            throw new IllegalArgumentException("Tên đăng nhập phải có ít nhất 4 ký tự");
        }
        return normalized;
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null) {
            return null;
        }
        String normalized = fullName.trim();
        return normalized.isEmpty() ? null : normalized;
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








