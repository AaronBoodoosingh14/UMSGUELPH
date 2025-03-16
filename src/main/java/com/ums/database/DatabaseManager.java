package com.ums.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://34.42.142.37:3306/university?useSSL=true&requireSSL=true";
    private static final String USER = "aaron";
    private static final String PASSWORD = "gtwxbhq8";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);

    }


}
