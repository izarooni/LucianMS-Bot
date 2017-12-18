package com.lucianms;


import com.lucianms.commands.CommandManager;
import com.lucianms.io.Config;
import com.lucianms.io.Defaults;
import com.lucianms.lang.DuplicateEntryException;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.server.Guild;
import com.lucianms.utils.Database;
import com.lucianms.commands.AbstractCommandHelper;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author izarooni
 */
public class Discord {

    private static final Logger LOGGER = LoggerFactory.getLogger(Discord.class);

    private static final Bot bot = new Bot();
    private static Config config;
    private static ConcurrentHashMap<Long, Guild> guilds = new ConcurrentHashMap<Long, Guild>();

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
            config = new Config(new JSONObject(new JSONTokener(new FileInputStream("config.json"))));
            LOGGER.info("Config file loaded");
            Discord.getBot().login();
            LOGGER.info("Discord bot is now online");

            // start the maple server
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/k", "start " + config.getString("launcher"));
            File file = new File(Discord.getConfig().getString("ServerDirectory"));
            processBuilder.directory(file);
            Process process = processBuilder.start();
            Discord.setServer(process);

            TaskExecutor.executeLater(ServerSession::connect, 10000);

            LOGGER.info("The server is now starting up!");
        } catch (LoginException | InterruptedException | DiscordException | IOException e) {
            e.printStackTrace();
        }

        JSONObject dbJson = config.getJsonObject("Database");
        if (dbJson.getBoolean("Enabled")) {
            Database.init(dbJson.getString("Host"), dbJson.getString("Schema"), dbJson.getString("Username"), dbJson.getString("Password"));
        }

        //region loading command managers
        try {
            File cmds = new File("data");
            if (cmds.mkdirs()) {
                LOGGER.info("External command management directory created");
            }
            try {
                File[] files = cmds.listFiles();
                if (files != null) {
                    for (File file : files) {
                        JarFile jar = new JarFile(file);
                        ZipEntry zip = jar.getEntry("info.ini");
                        if (zip == null) {
                            LOGGER.error("Command manager doesn't contain an information file {}" + file.getName());
                            continue;
                        }
                        try (InputStream in = jar.getInputStream(zip)) {
                            Properties props = new Properties();
                            props.load(in); // load info file properties

                            String name = (String) props.get("name");
                            String main = (String) props.get("main");

                            if (CommandManager.getCommandManager(name) != null) {
                                throw new DuplicateEntryException(String.format("Command processor with name %s already exists", name));
                            }

                            URL[] urls = Collections.singletonList(file.toURI().toURL()).toArray(new URL[1]);
                            URLClassLoader loader = new URLClassLoader(urls);
                            // info properties must contain the package path to the CommandHelper sub-class
                            Class toLoad = Class.forName(main, true, loader);
                            AbstractCommandHelper helper = (AbstractCommandHelper) toLoad.newInstance();
                            helper.onLoad();

                            CommandManager.addCommandManager(name, helper);
                        }
                    }
                    LOGGER.info("Loaded {} command modules!", CommandManager.getManagers().size());
                }
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                LOGGER.error("Unable to load external commands: {}", e.getMessage());
                System.exit(0);
            }
        } catch (SecurityException e) {
            LOGGER.error("Unable to create folder for command modules", e);
            System.exit(0);
        }
        //endregion
    }
}
