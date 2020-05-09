package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public class CmdForbid extends BaseCommand {

    public CmdForbid(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Forbids a word from being said";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Mono<MessageChannel> ch = event.getMessage().getChannel();
        DGuild guild = Discord.getGuild(event.getGuild());
        ArrayList<String> blacklist = guild.getGuildConfig().getWordBlackList();

        if (command.args.length > 0) {
            for (Command.CommandArg arg : command.args) {
                blacklist.add(arg.toString());
            }
            guild.getGuildConfig().getWordBlackList().save(guild);
            ch.blockOptional().ifPresent(c -> c.createMessage("Word(s) successfully blacklisted.").block());
        } else {
            ch.blockOptional().ifPresent(c -> c.createEmbed(e -> {
                e.setTitle("Forbidden words for this server");
                StringBuilder sb = new StringBuilder();
                blacklist.forEach(sb::append);
                e.setDescription(sb.toString());
                sb.setLength(0);
            }).block());
        }
    }
}
