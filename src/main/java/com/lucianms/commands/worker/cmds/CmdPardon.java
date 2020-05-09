package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;

public class CmdPardon extends BaseCommand {

    public CmdPardon(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Allows a word that was once forbidden to be spoken";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        DGuild guild = Discord.getGuild(event.getGuild());
        if (ch == null) return;

        Command.CommandArg[] args = command.getArgs();
        if (args.length > 0) {
            for (Command.CommandArg arg : args) {
                String word = arg.toString();
                guild.getGuildConfig().getWordBlackList().removeIf(black -> black.equalsIgnoreCase(word));
            }
            guild.getGuildConfig().getWordBlackList().save(guild);
            ch.createMessage("Word(s) successfully removed from the blacklist").block();
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <words...>`"
                        + "\r\n**example**: `" + getName() + " word1 word2 word3");
            }).block();
        }
    }
}
