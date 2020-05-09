package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import reactor.core.publisher.Mono;

public class CmdGetRoles extends BaseCommand {

    public CmdGetRoles(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Display a list of the Discord server's roles and ID";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Mono<MessageChannel> ch = event.getMessage().getChannel();

        ch.blockOptional().ifPresent(c -> c.createEmbed(e -> {
            StringBuilder sb = new StringBuilder();
            event.getGuild()
                    .map(Guild::getRoles).blockOptional()
                    .ifPresent(rf -> rf.subscribe(r -> sb.append(String.format("`%s` - %s\r\n", r.getId().asString(), r.getName()))));
            e.setDescription(sb.toString());
        }).block());
    }
}
