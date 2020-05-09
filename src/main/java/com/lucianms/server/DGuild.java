package com.lucianms.server;

import com.lucianms.server.user.DUser;
import com.lucianms.utils.Permissions;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author izarooni
 */
public class DGuild {

    private static final Logger LOGGER = LoggerFactory.getLogger(DGuild.class);

    private final Guild guild;
    private final Permissions permissions;
    private final GuildConfig guildConfig;
    private final GuildTicketList tickets;
    private final ConcurrentHashMap<String, DUser> users = new ConcurrentHashMap<>();

    public DGuild(Guild guild) {
        this.guild = guild;
        this.permissions = new Permissions(guild);

        guildConfig = new GuildConfig();
        tickets = new GuildTicketList();

        guildConfig.load(this);
        guildConfig.getWordBlackList().load(this);
        tickets.load(this);

        this.permissions.load();
        LOGGER.info("Loaded permissions for {}", toString());
    }

    @Override
    public String toString() {
        return String.format("Guild{name=%s, ID=%s}", guild.getName(), guild.getId());
    }

    public Guild getGuild() {
        return guild;
    }

    public Snowflake getId() {
        return guild.getId();
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public GuildConfig getGuildConfig() {
        return guildConfig;
    }

    public GuildTicketList getTickets() {
        return tickets;
    }

    public DUser getUser(String id) {
        return users.get(id);
    }

    public DUser removeUser(String id) {
        return users.remove(id);
    }

    public DUser addUserIfAbsent(User user) {
        DUser uhh = users.get(user.getId().asString());
        if (uhh == null) {
            // putIfAbsent is creation an object of User
            // thus loading permission's for every time this method is called
            // to my future self, leave this be
            users.put(user.getId().asString(), uhh = new DUser(user));
        }
        return uhh;
    }
}
