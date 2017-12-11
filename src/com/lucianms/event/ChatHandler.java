package com.lucianms.event;

import com.lucianms.commands.CommandManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class ChatHandler {

    @EventSubscriber
    public void onGuildMessageReceived(MessageReceivedEvent event) {
        if (CommandManager.isValidCommand(event)) {
            CommandManager.getManagers().forEach(m -> m.onCommand(event));
        }
    }
}
