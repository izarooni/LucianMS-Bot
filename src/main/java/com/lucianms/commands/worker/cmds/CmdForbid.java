package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

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
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().computeIfAbsent(event.getGuild().getLongID(), l -> new Guild(event.getGuild()));
        ArrayList<String> blacklistedWords = guild.getGuildConfig().getWordBlackList();

        if (command.args.length > 0) {
            for (Command.CommandArg arg : command.args) {
                blacklistedWords.add(arg.toString());
            }
            guild.getGuildConfig().getWordBlackList().save(guild);
            createResponse(event).withContent("Word(s) successfully blacklisted!").build();
        } else {
            StringBuilder sb = new StringBuilder();
            blacklistedWords.forEach(w -> sb.append(w).append(" "));
            EmbedBuilder embed = createEmbed()
                    .withTitle("Every forbidden word for this Discord guild")
                    .appendDesc(sb.toString());
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
