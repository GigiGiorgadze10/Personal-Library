package com.example.library.personallibrarymanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://DESKTOP-CM61FL6\\SQLEXPRESS;databaseName=PersonalLibraryDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
