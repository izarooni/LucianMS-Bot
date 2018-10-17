package com.lucianms.commands.worker;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public abstract class BaseCommand {

    private final boolean permissionRequired;
    private final CommandType commandType;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCommand.class);

    public BaseCommand() {
        this(true, CommandType.Public);
    }

    public BaseCommand(boolean permissionRequired) {
        this(permissionRequired, CommandType.Public);
    }

    public BaseCommand(boolean permissionRequired, CommandType commandType) {
        this.permissionRequired = permissionRequired;
        this.commandType = commandType;
    }

    public final String getName() {
        return Discord.getConfig().getString("CommandTrigger") + getClass().getSimpleName().toLowerCase();
    }

    public abstract String getDescription();

    public final boolean isPermissionRequired() {
        return permissionRequired;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Check if the user is allowed to use the specified command
     *
     * @return true if the command can be executed by the command user, false otherwise
     */
    public boolean canExecute(MessageReceivedEvent event, String permission) {
        IGuild ig = event.getChannel().getGuild();
        IUser iu = event.getAuthor();

        if (iu.getLongID() == Discord.getConfig().getLong("OwnerID")) {
            return true;
        }

        Guild guild = Discord.getGuilds().get(ig.getLongID());
        User user = guild.getUser(event.getAuthor().getLongID());
        if (user == null) {
            user = guild.addUser(iu);
        }
        for (IRole role : event.getAuthor().getRolesForGuild(ig)) {
            if (guild.getPermissions().contains(role.getLongID(), permission)) {
                return true;
            }
        }
        return user.getPermissions().contains(ig.getLongID(), permission);
    }

    /**
     * Execute the called command
     *
     * @param event   discord message received event
     * @param command a command object containing the event object, command name and command arguments
     */
    public abstract void invoke(MessageReceivedEvent event, Command command);

    public final MessageBuilder createResponse(MessageReceivedEvent event) {
        return new MessageBuilder(Discord.getBot().getClient()).withChannel(event.getChannel());
    }

    public final EmbedBuilder createEmbed() {
        return new EmbedBuilder().withColor(26, 188, 156);
    }
}
