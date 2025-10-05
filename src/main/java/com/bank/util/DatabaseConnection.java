package com.bank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bank_card_management";
    private static final String USER = "hemmi";
    private static final String PASSWORD = "031203";

    private Connection connection = null;

    private static DatabaseConnection instance = null;

    private DatabaseConnection() {
    }


    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection == null || instance.connection.isClosed()) {
            DatabaseConnection dbConnection = new DatabaseConnection();
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            dbConnection.connection = conn;
            instance = dbConnection;
            return dbConnection;
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
