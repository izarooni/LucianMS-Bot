package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.MessageBuilder;

public class GetRoles extends BaseCommand {

    public GetRoles() {
        super(true);
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        StringBuilder content = new StringBuilder();
        for (IRole role : event.getGuild().getRoles()) {
            content.append(String.format("%s - %s\r\n", role.getName(), role.getStringID()));
        }
        createResponse(event).appendContent(content.toString(), MessageBuilder.Styles.CODE).build();
    }
}
