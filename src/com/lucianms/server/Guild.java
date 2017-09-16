package com.lucianms.server;

import com.lucianms.server.user.User;
import com.lucianms.utils.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author izarooni
 */
public class Guild {

    private static final Logger LOGGER = LoggerFactory.getLogger(Guild.class);

    private final IGuild guild;
    private final Permissions permissions;
    private ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    public Guild(IGuild guild) {
        this.guild = guild;
        this.permissions = new Permissions(guild);

        this.permissions.load();
        LOGGER.info("Loaded permissions for guild {} {}", guild.getName(), guild.getLongID());
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
