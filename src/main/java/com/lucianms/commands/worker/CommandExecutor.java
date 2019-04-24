package com.lucianms.commands.worker;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.cmds.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author izarooni
 */
public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private static HashMap<String, BaseCommand> commands = new HashMap<>();

    static {
        // @formatter:off
        commands.put("bind",         new Bind());
        commands.put("connect",      new Connect());
        commands.put("disconnect",   new Disconnect());
        commands.put("forbid",       new Forbid());
        commands.put("getroles",     new GetRoles());
        commands.put("help",         new Help());
        commands.put("job",          new Job());
        commands.put("online",       new Online());
        commands.put("pardon",       new Pardon());
        commands.put("permission",   new Permission());
        commands.put("register",     new Register());
        commands.put("reloadcs",     new ReloadCS());
        commands.put("reserve",      new Reserve());
        commands.put("safeshutdown", new SafeShutdown());
        commands.put("search",       new Search());
        commands.put("setface",      new SetFace());
        commands.put("sethair",      new SetHair());
        commands.put("strip",        new Strip());
        commands.put("unstuck",      new Unstuck());
        commands.put("warp",         new Warp());
        commands.put("sql",          new Sql());
        commands.put("ticket",       new Ticket());
        commands.put("set",          new Set());
        // @formatter:on
    }

    public static List<BaseCommand> getCommands() {
        ArrayList<BaseCommand> list = new ArrayList<>(commands.values());
        list.sort(Comparator.comparing(BaseCommand::getName));
        return list;
    }

    public static void execute(MessageReceivedEvent event) {
        Command command = Command.parse(event.getMessage().getContent());
        if (command == null) {
            // not a command
            return;
        }
        BaseCommand base = commands.get(command.getCommand());
        if (base != null) {
            CommandType commandType = base.getCommandType();
            // channel type (private or public) matches command requirement
            if (commandType.canUseCommand(event.getChannel())) {
                if (!event.getChannel().isPrivate()) { // is a public discord channel
                    if (!base.isPermissionRequired() || base.canExecute(event, command.getCommand().toLowerCase())) {
                        base.invoke(event, command);
                    } else {
                        event.getChannel().sendMessage("You do not have permission to use this command");
                    }
                } else { // is private message
                    base.invoke(event, command);
                }
            } else {
                event.getChannel().sendMessage("This command cannot be used here");
            }
        }
    }
}
