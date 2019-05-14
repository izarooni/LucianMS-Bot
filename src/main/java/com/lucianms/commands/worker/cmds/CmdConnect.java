package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

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
    public void invoke(MessageReceivedEvent event, Command command) {
        LOGGER.info("Attempting to create connection to server");
        ServerSession.connect(new Promise() {
            @Override
            public void fail() {
                event.getChannel().sendMessage("Failed to connect to the server");
            }

            @Override
            public void success() {
                event.getChannel().sendMessage("Successfully connected to the server");
            }
        });
    }
}
