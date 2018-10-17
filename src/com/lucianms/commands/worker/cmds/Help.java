package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.Discord;
import com.lucianms.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public class Help extends BaseCommand {

    public Help() {
        super(false);
    }

    @Override
    public String getDescription() {
        return "Display a list of commands that you have permission for";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        String[][] commands = new String[][]{
                {"Bind", "Connect your Discord and Lucian account"},
                {"Connect", "Connect the Bot to the server"},
                {"Disconnect", "Disconnect any online player"},
                {"Online", "View Lucian's online players"},
                {"Permission", "Modify a Discord user's permission"},
                {"Restart", "Restart the Lucian server"},
                {"SafeShutdown", "Safely shutdown the Lucian server"},
                {"Search", "Search for a Maple related thing"},
                {"SetFace", "Change an in-game player's face"},
                {"SetHair", "Change an in-game players' hair"},
                {"Strip", "Strip an offline player of all equips"},
                {"Warp", "Warp an offline player to a map"},
                {"Job", "Change the job of an offline player"}
        };
        MessageBuilder mb = new MessageBuilder(Discord.getBot().getClient()).withChannel(event.getChannel());
        EmbedBuilder eb = new EmbedBuilder();
        eb.withColor(52, 152, 219).withTitle("[ Available Commands ]");
        for (String[] info : commands) {
            if (canExecute(event, info[0])) {
                eb.appendField(info[0], info[1], false);
            }
        }
        mb.withEmbed(eb.build()).build();
    }
}
