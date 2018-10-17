package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandExecutor;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class ChatHandler {

    @EventSubscriber
    public void onGuildMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContent();
        for (String words : Discord.getBlacklistedWords()) {
            boolean matches = Pattern.matches(Pattern.compile(words).pattern(), content);
            if (matches) {
                event.getMessage().delete();
                return;
            }
        }
        if (Command.isValid(event)) {
            CommandExecutor.execute(event);
        }
    }
}
