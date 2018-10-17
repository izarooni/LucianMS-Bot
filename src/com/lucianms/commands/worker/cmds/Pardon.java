package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

public class Pardon extends BaseCommand {

    public Pardon() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Allows a word that was once forbidden to be spoken";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.getArgs();
        if (args.length > 0) {
            for (Command.CommandArg arg : args) {
                String word = arg.toString();
                Discord.getBlacklistedWords().removeIf(black -> black.equalsIgnoreCase(word));
            }
            Discord.updateBlacklistedWords();
            createResponse(event).withContent("Word(s) successfully removed from the blacklist!", MessageBuilder.Styles.ITALICS).build();
        } else {
            EmbedObject embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: ").appendDesc(getName()).appendDesc(" [words...]")
                    .appendDesc("\r\n**example**: ").appendDesc(getName()).appendDesc(" word1 word2 word3").build();
            createResponse(event).withEmbed(embed).build();
        }
    }
}
