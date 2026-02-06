package com.qlct.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static final String PROPERTIES_FILE = "/config/db.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = resolveConfigStream()) {
            PROPERTIES.load(input);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load database configuration: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("SQL Server JDBC driver not found");
        }
    }

    private static InputStream resolveConfigStream() throws IOException {
        InputStream classpathStream = DBConnection.class.getResourceAsStream(PROPERTIES_FILE);
        if (classpathStream != null) {
            return classpathStream;
        }

        Path externalPath = Path.of("config", "db.properties");
        if (Files.exists(externalPath)) {
            return Files.newInputStream(externalPath);
        }

        throw new IOException("Cannot locate db.properties. Expected on classpath at "
                + PROPERTIES_FILE + " or at config/db.properties relative to the working directory.");
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String username = PROPERTIES.getProperty("db.username");
        String password = PROPERTIES.getProperty("db.password");
        return DriverManager.getConnection(url, username, password);
    }
}








