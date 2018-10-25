package com.lucianms.utils;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {

    public static HikariDataSource createMapleDataSource(String name) {
        HikariConfig config = new HikariConfig("maple-db.properties");
        config.setPoolName(name);
        return new HikariDataSource(config);
    }

    public static HikariDataSource createDiscordDataSource(String name) {
        HikariConfig config = new HikariConfig("discord-db.properties");
        config.setPoolName(name);
        return new HikariDataSource(config);
    }
}
