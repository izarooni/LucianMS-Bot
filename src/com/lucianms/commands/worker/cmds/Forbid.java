package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.server.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class Forbid extends BaseCommand {

    @Override
    public String getDescription() {
        return "Forbids a word from being said";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().computeIfAbsent(event.getGuild().getLongID(), l -> new Guild(event.getGuild()));
        Command.CommandArg[] args = command.args;
        if (args.length > 0) {
            for (Command.CommandArg arg : args) {
                guild.getBlacklistedWords().add(arg.toString());
            }
            guild.updateBlacklistedWords();
            createResponse(event).withContent("Word(s) successfully blacklisted!").build();
        } else {
            StringBuilder sb = new StringBuilder();
            guild.getBlacklistedWords().forEach(w -> sb.append(w).append(" "));
            EmbedBuilder embed = createEmbed()
                    .withTitle("Every forbidden word for this Discord guild")
                    .appendDesc(sb.toString());
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
