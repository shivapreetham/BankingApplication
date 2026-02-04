package com.banking.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles MySQL Database Connection
 */
public class DatabaseConnection {

    // Database credentials - Configure these
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Static connection instance
    private static Connection connection = null;

    /**
     * Establish connection to MySQL database
     *
     * @return Connection object
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                Class.forName(DB_DRIVER);
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Test the database connection
     */
    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection test successful!");
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed!");
            e.printStackTrace();
        }
    }
}
