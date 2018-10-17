package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

public class Forbid extends BaseCommand {

    public Forbid() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Forbids a word from being said";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length > 0) {
            for (Command.CommandArg arg : args) {
                Discord.getBlacklistedWords().add(arg.toString());
            }
            Discord.updateBlacklistedWords();
            createResponse(event).withContent("Word(s) successfully blacklisted!", MessageBuilder.Styles.ITALICS).build();
        } else {
            StringBuilder sb = new StringBuilder();
            Discord.getBlacklistedWords().forEach(w -> sb.append(w).append(" "));
            EmbedObject embed = createEmbed(event)
                    .withTitle("Every forbidden word")
                    .appendDesc(sb.toString()).build();
            createResponse(event).withEmbed(embed).build();
        }
    }
}
