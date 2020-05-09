package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.server.DGuild;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author izarooni
 */
public class ClientReadyEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientReadyEvent.class);

    public static void execute(ReadyEvent event) {
        DiscordClient client = event.getClient();

        client.getGuilds().subscribe(g -> Discord.getGuilds().put(g.getId().asString(), new DGuild(g)));
        LOGGER.info("Registered " + Discord.getGuilds().size() + " guild wrappers");
        client.updatePresence(Presence.online(Activity.playing(String.format("Use %shelp for a list of commands", CommandExecutor.CMD_PREFIX)))).block();
    }

    private ClientReadyEvent() {
    }
}
