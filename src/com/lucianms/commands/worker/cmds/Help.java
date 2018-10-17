package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandExecutor;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author izarooni
 */
public class Help extends BaseCommand {

    public Help() {
        super(false);
    }

    @Override
    public String getDescription() {
        return "Display a list of commands that you have permission for";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        EmbedBuilder embed = createEmbed().withTitle("Available commands");
        for (BaseCommand base : CommandExecutor.getCommands()) {
            if (!base.isPermissionRequired() || canExecute(event, base.getClass().getSimpleName().toLowerCase())) {
                embed.appendDesc("\r\n**").appendDesc(base.getName()).appendDesc("** - ").appendDesc(base.getDescription());
            }
        }
        createResponse(event).withEmbed(embed.build()).build();
    }
}
