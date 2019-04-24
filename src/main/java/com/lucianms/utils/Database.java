package com.lucianms.utils;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.ini4j.Profile;

public class Database {

    public static HikariDataSource createDataSource(Profile.Section properties, String schema) {
        HikariConfig config = new HikariConfig();
        config.setPoolName(schema);
        config.setSchema(schema);
        config.setJdbcUrl(String.format("jdbc:mysql://%s:3306/%s", properties.get("host_address"), schema));
        config.setUsername(properties.get("username"));
        config.setPassword(properties.get("password"));
        config.setLeakDetectionThreshold(90000);
        return new HikariDataSource(config);
    }
}
