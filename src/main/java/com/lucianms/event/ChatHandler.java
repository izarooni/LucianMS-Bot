package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.server.Guild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class ChatHandler {

    public void onDirectMessage(MessageReceivedEvent event) {
    }

    @EventSubscriber
    public void onGuildMessageReceived(MessageReceivedEvent event) {
        IGuild guild = event.getGuild();
        String content = event.getMessage().getContent();
        if (!event.getChannel().isPrivate()) {
            Guild lGuild = Discord.getGuilds().computeIfAbsent(guild.getLongID(), l -> new Guild(guild));
            if (event.getAuthor().getRolesForGuild(guild).stream().noneMatch(r -> r.getPermissions().contains(Permissions.MANAGE_MESSAGES))) {
                for (String words : lGuild.getBlacklistedWords()) {
                    Pattern pattern = Pattern.compile(words, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        event.getMessage().delete();
                        return;
                    }
                }
            }
        }
        if (Command.isValidCommand(event)) {
            CommandExecutor.execute(event);
        }
    }
}
