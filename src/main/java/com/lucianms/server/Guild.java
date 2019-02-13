package com.lucianms.server;

import com.lucianms.Discord;
import com.lucianms.server.user.User;
import com.lucianms.utils.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author izarooni
 */
public class Guild {

    private static final Logger LOGGER = LoggerFactory.getLogger(Guild.class);

    private final IGuild guild;
    private final Permissions permissions;
    private ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private ArrayList<String> blacklistedWords = new ArrayList<>();

    public Guild(IGuild guild) {
        this.guild = guild;
        this.permissions = new Permissions(guild);

        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("select word from forbidden_words where guild_id = ?")) {
                ps.setLong(1, guild.getLongID());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        blacklistedWords.add(rs.getString("word"));
                    }
                    LOGGER.info("Loaded {} forbidden words for guild '{}:{}'", blacklistedWords.size(), guild.getName(), guild.getLongID());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception ignore) {
            LOGGER.warn("Failed to establish connection to Discord SQL. Skipping.");
        }

        this.permissions.load();
        LOGGER.info("Loaded permissions for guild {} {}", guild.getName(), guild.getLongID());
    }

    public void updateBlacklistedWords() {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("delete from forbidden_words where guild_id = ?")) {
                ps.setLong(1, guild.getLongID());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("insert into forbidden_words values (?, ?)")) {
                ps.setLong(1, guild.getLongID());
                for (String word : blacklistedWords) {
                    ps.setString(2, word);
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception ignore) {
            LOGGER.warn("Failed to establish connection to Discord SQL. Skipping.");
        }
    }

    public ArrayList<String> getBlacklistedWords() {
        return blacklistedWords;
    }

    public IGuild getGuild() {
        return guild;
    }

    public long getId() {
        return guild.getLongID();
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public User addUser(IUser user) {
        if (users.containsKey(user.getLongID())) {
            LOGGER.warn("User '{}' {} already exists in guild '{}' {}", user.getName(), user.getLongID(), guild.getName(), guild.getLongID());
        }
        User ret = new User(user);
        users.put(user.getLongID(), ret);
        return ret;
    }

    public User removeUser(long id) {
        return users.remove(id);
    }

    public Permissions getPermissions() {
        return permissions;
    }
}
