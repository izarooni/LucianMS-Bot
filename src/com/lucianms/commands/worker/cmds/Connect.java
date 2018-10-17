package com.lucianms.cmds;

import com.lucianms.BaseCommand;
import com.lucianms.commands.Command;
import com.lucianms.net.maple.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class Connect extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connect.class);

    public Connect() {
        super(true);
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        LOGGER.info("Attempting to create connection to server");
        ServerSession.connect();
    }
}
