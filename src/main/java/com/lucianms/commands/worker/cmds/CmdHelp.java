package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandCategory;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author izarooni
 */
public class CmdHelp extends BaseCommand {

    private static final EnumMap<CommandCategory, ArrayList<CommandUtil>> COMMANDS = new EnumMap<>(CommandCategory.class);

    public CmdHelp(CommandUtil permission) {
        super(permission);
        for (CommandUtil perm : CommandUtil.values()) {
            if (perm.command != null) {
                COMMANDS.computeIfAbsent(perm.category, cat -> new ArrayList<>()).add(perm);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Display a list of commands that you have permission for";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        TextChannel ch = event.getMessage()
                .getChannel().ofType(TextChannel.class)
                .blockOptional().orElse(null);
        if (ch == null) return;

        boolean isPrivate = ch.getType() == Channel.Type.DM;

        ch.createEmbed(e -> {
            e.setTitle("Available commands");
            for (Map.Entry<CommandCategory, ArrayList<CommandUtil>> entry : COMMANDS.entrySet()) {
                StringBuilder sb = new StringBuilder();
                for (CommandUtil perms : entry.getValue()) {
                    if (perms.type == CommandType.Both
                            || (perms.type == CommandType.Private && isPrivate)
                            || (perms.type == CommandType.Public && !isPrivate)) {
                        BaseCommand cmd = CommandExecutor.getCommand(perms.name().toLowerCase());
                        if (!cmd.getPermission().needsPermission || canExecute(event, perms)) {
                            sb.append("**").append(cmd.getName()).append("** - ").append(cmd.getDescription()).append("\r\n");
                        }
                    }
                }
                if (sb.length() > 0) {
                    e.addField(entry.getKey().name(), sb.toString(), false);
                    sb.setLength(0);
                }
            }
        }).block();
    }
}
