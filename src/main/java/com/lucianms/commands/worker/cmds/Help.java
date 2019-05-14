package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandCategory;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CPermissions;
import com.lucianms.commands.worker.CommandExecutor;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author izarooni
 */
public class Help extends BaseCommand {

    private static final EnumMap<CommandCategory, ArrayList<CPermissions>> COMMANDS = new EnumMap<>(CommandCategory.class);

    public Help() {
        super(false, CommandType.Both);
        for (CPermissions perm : CPermissions.values()) {
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
        for (Map.Entry<CommandCategory, ArrayList<CPermissions>> entry : COMMANDS.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (CPermissions perms : entry.getValue()) {
                if (perms.type == CommandType.Both
                        || (perms.type == CommandType.PrivateMessage && event.getChannel().isPrivate())
                        || (perms.type == CommandType.Public && !event.getChannel().isPrivate())) {
                    String cmdName = perms.name().toLowerCase();
                    BaseCommand cmd = CommandExecutor.getCommand(cmdName);
                    if (!cmd.isPermissionRequired() || canExecute(event, cmdName)) {
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
