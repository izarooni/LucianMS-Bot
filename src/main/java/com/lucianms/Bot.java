package com.lucianms;

import com.lucianms.event.ClientReadyEvent;
import com.lucianms.event.MessageChannelEvents;
import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author izarooni
 */
public class Bot {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private DiscordClient client;

    void login() {
        String botToken = Discord.getConfig().get("global", "user_token", String.class);
        client = DiscordClient.create(botToken);
        EventDispatcher ed = client.getEventDispatcher();
        ed.on(MessageCreateEvent.class).onErrorResume(e -> Mono.empty()).subscribe(MessageChannelEvents::execute);
        ed.on(ReactionAddEvent.class).onErrorResume(e -> Mono.empty()).subscribe(MessageChannelEvents::execute);
        ed.on(ReadyEvent.class).onErrorResume(e -> Mono.empty()).subscribe(ClientReadyEvent::execute);
        client.login().block();
    }

    public DiscordClient getClient() {
        return client;
    }
}
