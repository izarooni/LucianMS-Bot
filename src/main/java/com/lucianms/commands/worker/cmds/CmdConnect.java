package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.Promise;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author izarooni
 */
public class CmdConnect extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdConnect.class);

    public CmdConnect(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Connects the Discord bot to the server";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Mono<TextChannel> chm = message.getChannel().ofType(TextChannel.class);

        chm.blockOptional().ifPresent(c -> {
            Message msg = c.createMessage("Attempting to connect to the server...").block();
            ServerSession.connect(new Promise() {
                @Override
                public void fail() {
                    msg.edit(m -> m.setContent("Failed to connect to the server")).block();
                }

                @Override
                public void success() {
                    msg.edit(m -> m.setContent("Successfully connected to the server")).block();
                }
            });
        });
    }
}
