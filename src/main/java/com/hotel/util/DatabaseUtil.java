package com.hotel.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for obtaining a MySQL JDBC connection.
 * The app bootstraps the expected demo schema automatically when it finds
 * an empty or incompatible hotel_reservation database.
 */
public class DatabaseUtil {
    private static final String DB_NAME = "hotel_reservation";
    private static final String DB_OPTIONS =
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/" + DB_OPTIONS;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + DB_OPTIONS;
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Lakvin(331)@#$";
    private static final Object INIT_LOCK = new Object();
    private static volatile boolean initialized;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /** Returns a new JDBC connection from DriverManager. */
    public static Connection getConnection() throws SQLException {
        ensureSchemaInitialized();
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /** Quietly closes a connection (null-safe). */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    private static void ensureSchemaInitialized() {
        if (initialized) {
            return;
        }

        synchronized (INIT_LOCK) {
            if (initialized) {
                return;
            }

            try (Connection serverConnection = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASS)) {
                createDatabaseIfNeeded(serverConnection);

                try (Connection appConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    if (!isSchemaCompatible(appConnection)) {
                        runSqlScript(serverConnection, "schema.sql");
                    }
                }

                initialized = true;
            } catch (SQLException | IOException e) {
                throw new ExceptionInInitializerError("Database initialization failed: " + e.getMessage());
            }
        }
    }

    private static void createDatabaseIfNeeded(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE DATABASE IF NOT EXISTS " + DB_NAME
                  + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            );
        }
    }

    private static boolean isSchemaCompatible(Connection connection) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        return hasTable(metadata, "users")
                && hasColumn(metadata, "users", "email")
                && hasTable(metadata, "hotels")
                && hasTable(metadata, "rooms")
                && hasColumn(metadata, "rooms", "price_per_night")
                && hasTable(metadata, "reservations")
                && hasColumn(metadata, "reservations", "user_id")
                && hasColumn(metadata, "reservations", "room_id")
                && hasTable(metadata, "payments")
                && hasColumn(metadata, "payments", "reservation_id")
                && hasTable(metadata, "feedback")
                && hasColumn(metadata, "feedback", "rating");
    }

    private static boolean hasTable(DatabaseMetaData metadata, String tableName) throws SQLException {
        try (var tables = metadata.getTables(DB_NAME, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }

    private static boolean hasColumn(DatabaseMetaData metadata, String tableName, String columnName) throws SQLException {
        try (var columns = metadata.getColumns(DB_NAME, null, tableName, columnName)) {
            return columns.next();
        }
    }

    private static void runSqlScript(Connection connection, String resourceName) throws IOException, SQLException {
        InputStream stream = DatabaseUtil.class.getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IOException("Missing SQL resource: " + resourceName);
        }

        StringBuilder statementBuffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }

                statementBuffer.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    executeStatement(connection, statementBuffer.toString());
                    statementBuffer.setLength(0);
                }
            }
        }

        if (statementBuffer.length() > 0) {
            executeStatement(connection, statementBuffer.toString());
        }
    }

    private static void executeStatement(Connection connection, String sql) throws SQLException {
        String statementSql = sql.trim();
        if (statementSql.endsWith(";")) {
            statementSql = statementSql.substring(0, statementSql.length() - 1);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(statementSql);
        }
    }
}
