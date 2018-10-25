package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.server.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

public class Pardon extends BaseCommand {

    @Override
    public String getDescription() {
        return "Allows a word that was once forbidden to be spoken";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().computeIfAbsent(event.getGuild().getLongID(), l -> new Guild(event.getGuild()));
        Command.CommandArg[] args = command.getArgs();
        if (args.length > 0) {
            for (Command.CommandArg arg : args) {
                String word = arg.toString();
                guild.getBlacklistedWords().removeIf(black -> black.equalsIgnoreCase(word));
            }
            guild.updateBlacklistedWords();
            createResponse(event).withContent("Word(s) successfully removed from the blacklist!", MessageBuilder.Styles.ITALICS).build();
        } else {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: ").appendDesc(getName()).appendDesc(" <words...>")
                    .appendDesc("\r\n**example**: ").appendDesc(getName()).appendDesc(" word1 word2 word3");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
