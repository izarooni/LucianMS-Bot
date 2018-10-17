package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandExecutor;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
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
        String content = event.getMessage().getContent();
        if (!event.getChannel().isPrivate()) {
            if (event.getAuthor().getRolesForGuild(event.getGuild()).stream().noneMatch(r -> r.getPermissions().contains(Permissions.MANAGE_MESSAGES))) {
                for (String words : Discord.getBlacklistedWords()) {
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
