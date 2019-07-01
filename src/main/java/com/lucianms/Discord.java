package com.lucianms;


import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.io.Defaults;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import com.lucianms.utils.Database;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.ini4j.Profile;
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
    private static ConcurrentHashMap<Long, User> userHandles = new ConcurrentHashMap<>();
    private static HikariDataSource mapleDataSource;
    private static HikariDataSource discordDataSource;

    private static Process server;

    private Discord() {
    }

    public static Wini loadConfiguration() throws IOException {
        return new Wini(new File("config.ini"));
    }

    public static Connection getMapleConnection() throws SQLException {
        return mapleDataSource.getConnection();
    }

    public static Connection getDiscordConnection() throws SQLException {
        return discordDataSource.getConnection();
    }

    public static Bot getBot() {
        return bot;
    }

    public static Wini getConfig() {
        return config;
    }

    public static void setConfig(Wini config) {
        Discord.config = config;
    }

    public static ConcurrentHashMap<Long, Guild> getGuilds() {
        return guilds;
    }

    public static ConcurrentHashMap<Long, User> getUserHandles() {
        return userHandles;
    }

    public static Process getServer() {
        return server;
    }

    public static void setServer(Process server) {
        Discord.server = server;
    }

    public static void main(String[] args) {
        if (Defaults.tryCreateDefault("", "config.ini")) {
            LOGGER.info("Created config file. Make changes and then restart program");
            return;
        }
        try {
            config = loadConfiguration();
            LOGGER.info("Config file loaded");

            CommandExecutor.CMD_PREFIX = config.get("global", "cmd_prefix", String.class);

            Profile.Section databaseSection = config.get("database");
            mapleDataSource = Database.createDataSource(databaseSection, databaseSection.get("maple_schema"));
            discordDataSource = Database.createDataSource(databaseSection, databaseSection.get("discord_schema"));
            Flyway flyway = Flyway.configure().dataSource(discordDataSource).schemas(discordDataSource.getSchema()).load();
            flyway.repair();
            flyway.migrate();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Guild guild : Discord.getGuilds().values()) {
                        guild.getGuildConfig().save(guild);
                        guild.getTickets().save(guild);
                        LOGGER.info("Saving guild {}", guild.toString());
                    }
                }
            }, "Shutdown_Hook"));

            Discord.getBot().login();
            LOGGER.info("Discord bot is now online");

            ServerSession.connect(null);

            LOGGER.info("The server is now starting up!");
        } catch (DiscordException | IOException e) {
            e.printStackTrace();
        }
    }
}
