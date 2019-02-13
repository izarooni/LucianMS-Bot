package com.lucianms;


import com.lucianms.io.Defaults;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.server.Guild;
import com.lucianms.utils.Database;
import com.zaxxer.hikari.HikariDataSource;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author izarooni
 */
public class Discord {

    private static final Logger LOGGER = LoggerFactory.getLogger(Discord.class);

    private static final Bot bot = new Bot();
    private static Wini config;
    private static ConcurrentHashMap<Long, Guild> guilds = new ConcurrentHashMap<>();
    private static HikariDataSource mapleDataSource = Database.createMapleDataSource("maple");
    private static HikariDataSource discordDataSource = Database.createDiscordDataSource("discord");

    private static Process server;

    private Discord() {
    }

    public static Connection getMapleConnection() throws SQLException {
        return mapleDataSource.getConnection();
    }

    public static Connection getDiscordConnection() throws SQLException {
        if (discordDataSource == null) {
            discordDataSource = Database.createDiscordDataSource("discord");
        }
        return discordDataSource.getConnection();
    }

    public static Bot getBot() {
        return bot;
    }

    public static Wini getConfig() {
        return config;
    }

    public static ConcurrentHashMap<Long, Guild> getGuilds() {
        return guilds;
    }

    public static Process getServer() {
        return server;
    }

    public static void setServer(Process server) {
        Discord.server = server;
    }

    public static void main(String[] args) {
        File fConfig = new File("config.ini");
        if (!fConfig.exists()) {
            try {
                Defaults.createDefault("", "config.ini");
                LOGGER.info("Created config file. Make changes and then restart program");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            config = new Wini(new File("config.ini"));
            LOGGER.info("Config file loaded");
            Discord.getBot().login();
            LOGGER.info("Discord bot is now online");

            TaskExecutor.executeLater(ServerSession::connect, 10000);

            LOGGER.info("The server is now starting up!");
        } catch (DiscordException | IOException e) {
            e.printStackTrace();
        }
    }
}
