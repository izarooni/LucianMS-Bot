package com.lucianms.commands.worker;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.server.DGuild;
import com.lucianms.server.user.DUser;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author izarooni
 */
public abstract class BaseCommand {

    private final CommandUtil permission;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCommand.class);

    public BaseCommand(CommandUtil permission) {
        this.permission = permission;
    }

    public final String getName() {
        return CommandExecutor.CMD_PREFIX + permission.name().toLowerCase();
    }

    public abstract String getDescription();

    public final CommandUtil getPermission() {
        return permission;
    }

    public CommandType getCommandType() {
        return permission.type;
    }

    /**
     * Check if the user is allowed to use the specified command
     *
     * @return true if the command can be executed by the command user, false otherwise
     */
    public boolean canExecute(MessageCreateEvent event, CommandUtil cmd) {
        Message message = event.getMessage();
        Optional<User> author = message.getAuthor();
        Mono<MessageChannel> channel = message.getChannel();
        if (!author.isPresent()) {
            return false;
        }

        boolean isAdmin = channel.ofType(GuildChannel.class)
                .flatMap(c -> c.getEffectivePermissions(author.map(User::getId).get()))
                .map(set -> set.containsAll(PermissionSet.of(Permission.ADMINISTRATOR))).blockOptional().orElse(null);
        if (isAdmin) return true;

        // is the message being created in a PrivateChannel
        if (channel.ofType(PrivateChannel.class).blockOptional().isPresent()) {
            // only allow if the command can be used privately and cmd doesn't require any permission
            return !cmd.needsPermission && (cmd.type == CommandType.Private || cmd.type == CommandType.Both);
        }
        DGuild guild = event.getGuild().map(g -> Discord.getGuilds().get(g.getId().asString())).block();
        String authorID = author.map(User::getId).get().asString();
        DUser user = guild.getUser(authorID);

        // if the user is the owner of the bot (as defined in the configration file)
        if (authorID.equals(Discord.getConfig().get("global", "owner_id", String.class))) {
            return true;
        }

        // if user has permission for the command
        boolean userPermission = event.getGuild()
                .map(g -> g.getRoles())
                .flatMap(r -> r.collectList())
                .map(c -> c.stream().anyMatch(r -> user.getPermissions().contains(r.getId().asString(), cmd)))
                .blockOptional().orElse(false);
        boolean guildPermission = event.getGuild()
                .map(g -> g.getRoles())
                .flatMap(r -> r.collectList())
                .map(c -> c.stream().anyMatch(r -> guild.getPermissions().contains(r.getId().asString(), cmd)))
                .blockOptional().orElse(false);
        return userPermission || guildPermission;
    }

    /**
     * Execute the called command
     *
     * @param event   discord message received event
     * @param command a command object containing the event object, command name and command arguments
     */
    public abstract void invoke(MessageCreateEvent event, Command command);
}
