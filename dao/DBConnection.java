package com.unimate.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** The only class in the project that opens a database connection. */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/unimate";
    private static final String USER = "root";
    private static final String PASS = "";     // put your MySQL password here

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found. Add the jar to WEB-INF/lib", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
