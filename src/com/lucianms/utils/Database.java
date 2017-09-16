package com.lucianms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private static final ThreadLocal<Connection> con = new ThreadLocalConnection();

    private static boolean initialized = false;
    private static String URL = "jdbc:mysql://%s:3306/%s?autoReconnect=true&useSSL=true";
    private static String Username = null, Password = null;

    public static void init(String host, String schema, String username, String password) {
        if (initialized) {
            throw new IllegalStateException("Database already initialized");
        }
        URL = String.format(URL, host, schema);
        Username = username;
        Password = password;
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static Connection getConnection() {
        Connection c = con.get();
        try {
            c.getMetaData();
        } catch (SQLException e) {
            con.remove();
            c = con.get();
        }
        return c;
    }

    private static class ThreadLocalConnection extends ThreadLocal<Connection> {

        @Override
        protected Connection initialValue() {
            try {
                return DriverManager.getConnection(URL, Username, Password);
            } catch (SQLException e) {
                LOGGER.error("Unable to create database connection: {}", e.getMessage());
                return null;
            }
        }
    }
}
