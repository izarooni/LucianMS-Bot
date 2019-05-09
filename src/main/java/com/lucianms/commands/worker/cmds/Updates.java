package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class Updates extends BaseCommand {

    public Updates() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Create a news update for the website";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
    }
}
