package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;

public class GetRoles extends BaseCommand {

    @Override
    public String getDescription() {
        return "Display a list of the Discord server's roles and ID";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        EmbedBuilder embed = createEmbed();
        for (IRole role : event.getGuild().getRoles()) {
            embed.appendDesc(String.format("%d - %s\r\n", role.getLongID(), role.getName()));
        }
        createResponse(event).withEmbed(embed.build()).build();
    }
}
