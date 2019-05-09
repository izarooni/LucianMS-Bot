package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class News extends BaseCommand {

    public News() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Create a news article for the website";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
    }
}
