package com.lucianms.commands.worker;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

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
    public boolean canExecute(MessageReceivedEvent event, CommandUtil permission) {
        IChannel ch = event.getChannel();

        if (ch.isPrivate()) {
            return !permission.requirePermission
                    && (permission.type == CommandType.Private || permission.type == CommandType.Both);
        }

        IGuild ig = ch.getGuild();
        IUser iu = event.getAuthor();

        if (iu.getStringID().equals(Discord.getConfig().get("global", "owner_id", String.class))) {
            return true;
        }

        Guild guild = Discord.getGuilds().get(ig.getLongID());
        User user = guild.addUserIfAbsent(event.getAuthor());
        String permissionName = permission.name().toLowerCase();

        for (IRole role : event.getAuthor().getRolesForGuild(ig)) {
            if (role.getPermissions().contains(Permissions.ADMINISTRATOR)
                    || guild.getPermissions().contains(role.getLongID(), permissionName)) {
                return true;
            }
        }
        return user.getPermissions().contains(ig.getLongID(), permissionName);
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

    public final EmbedBuilder createEmbed(IEmbed bed) {
        EmbedBuilder embed = new EmbedBuilder().withColor(bed.getColor());
        if (bed.getTitle() != null) embed.withTitle(bed.getTitle());
        if (bed.getDescription() != null) embed.withTitle(bed.getDescription());
        if (bed.getFooter() != null) embed.withTitle(bed.getFooter().getText());
        if (bed.getEmbedFields() != null) bed.getEmbedFields().forEach(embed::appendField);
        return embed;
    }
}
