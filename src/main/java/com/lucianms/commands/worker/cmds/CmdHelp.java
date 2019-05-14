package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandCategory;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.commands.worker.CommandUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author izarooni
 */
public class CmdHelp extends BaseCommand {

    private static final EnumMap<CommandCategory, ArrayList<CommandUtil>> COMMANDS = new EnumMap<>(CommandCategory.class);

    public CmdHelp(CommandUtil permission) {
        super(permission);
        for (CommandUtil perm : CommandUtil.values()) {
            COMMANDS.computeIfAbsent(perm.category, cat -> new ArrayList<>()).add(perm);
        }
    }

    @Override
    public String getDescription() {
        return "Display a list of commands that you have permission for";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        EmbedBuilder embed = createEmbed().withTitle("Available Commands");
        for (Map.Entry<CommandCategory, ArrayList<CommandUtil>> entry : COMMANDS.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (CommandUtil perms : entry.getValue()) {
                if (perms.type == CommandType.Both
                        || (perms.type == CommandType.PrivateMessage && event.getChannel().isPrivate())
                        || (perms.type == CommandType.Public && !event.getChannel().isPrivate())) {
                    String cmdName = perms.name().toLowerCase();
                    BaseCommand cmd = CommandExecutor.getCommand(cmdName);
                    if (!cmd.getPermission().requirePermission || canExecute(event, cmdName)) {
                        sb.append("**").append(cmdName).append("** - ").append(cmd.getDescription()).append("\r\n");
                    }
                }
            }
            embed.appendField(entry.getKey().name(), sb.toString(), false);
            sb.setLength(0);
        }
        RequestBuffer.request(() -> createResponse(event).withEmbed(embed.build()).build());
    }
}
