package com.lucianms.utils;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {

    public static HikariDataSource createDataSource(String name) {
        HikariConfig config = new HikariConfig("db.properties");
        config.setPoolName(name);
        return new HikariDataSource(config);
    }
}
