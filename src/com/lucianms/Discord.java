package com.lucianms;


import com.lucianms.io.Config;
import com.lucianms.io.Defaults;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.server.Guild;
import com.lucianms.utils.Database;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author izarooni
 */
public class Discord {

    private static final Logger LOGGER = LoggerFactory.getLogger(Discord.class);

    private static final Bot bot = new Bot();
    private static Config config;
    private static ConcurrentHashMap<Long, Guild> guilds = new ConcurrentHashMap<>();
    private static ArrayList<String> blacklistedWords = new ArrayList<>();

    private static Process server;

    private Discord() {
    }

    public static Bot getBot() {
        return bot;
    }

    public static Config getConfig() {
        return config;
    }

    public static ConcurrentHashMap<Long, Guild> getGuilds() {
        return guilds;
    }

    public static ArrayList<String> getBlacklistedWords() {
        return blacklistedWords;
    }

    public static Process getServer() {
        return server;
    }

    public static void setServer(Process server) {
        Discord.server = server;
    }

    public static void main(String[] args) {
        File fConfig = new File("config.json");
        if (!fConfig.exists()) {
            try {
                Defaults.createDefault("", "config.json");
                LOGGER.info("Created config file. Make changes and then restart program");
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            File file = new File("blacklist.txt");
            if (!file.exists()) {
                file.createNewFile();
                LOGGER.info("Blacklist file created");
            } else {
                try (FileInputStream stream = new FileInputStream(file)) {
                    try (Scanner scanner = new Scanner(stream)) {
                        String line;
                        while (scanner.hasNext() && (line = scanner.next()) != null) {
                            blacklistedWords.add(line);
                        }
                    }
                }
                LOGGER.info("{} black listed words", blacklistedWords.size());
            }

            config = new Config(new JSONObject(new JSONTokener(new FileInputStream("config.json"))));
            LOGGER.info("Config file loaded");
            Discord.getBot().login();
            LOGGER.info("Discord bot is now online");

            // start the maple server
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/k", "start " + config.getString("launcher"));
            file = new File(Discord.getConfig().getString("ServerDirectory"));
            processBuilder.directory(file);
            if (file.exists()) {
                Process process = processBuilder.start();
                Discord.setServer(process);
            }

            TaskExecutor.executeLater(ServerSession::connect, 10000);

            LOGGER.info("The server is now starting up!");
        } catch (LoginException | InterruptedException | DiscordException | IOException e) {
            e.printStackTrace();
        }

        JSONObject dbJson = config.getJsonObject("Database");
        if (dbJson.getBoolean("Enabled")) {
            Database.init(dbJson.getString("Host"), dbJson.getString("Schema"), dbJson.getString("Username"), dbJson.getString("Password"));
        }
    }
}
