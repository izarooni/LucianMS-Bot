package com.lucianms;

import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.event.ChatHandler;
import com.lucianms.server.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

/**
 * @author izarooni
 */
public class Bot {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private IDiscordClient client;

    void login() throws DiscordException {
        ClientBuilder builder = new ClientBuilder().withToken(Discord.getConfig().get("global", "user_token", String.class));
        client = builder.login();
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new ChatHandler());
        dispatcher.registerListener(new IListener() {
            @Override
            public void handle(Event event) {
                if (event instanceof ReadyEvent) {
                    client.getGuilds().forEach(g -> Discord.getGuilds().put(g.getLongID(), new Guild(g)));
                    LOGGER.info("Registered " + Discord.getGuilds().size() + " guild wrappers");

                    client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, String.format("Use %shelp for a list of commands", CommandExecutor.CMD_PREFIX));
                }
            }
        });
    }

    public IDiscordClient getClient() {
        return client;
    }
}
