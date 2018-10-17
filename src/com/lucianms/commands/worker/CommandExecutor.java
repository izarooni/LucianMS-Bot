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
        commands.put("warp",         new Warp());
        commands.put("strip",        new Strip());
        commands.put("restart",      new Restart());
        commands.put("setface",      new SetFace());
        commands.put("sethair",      new SetHair());
        commands.put("safeshutdown", new SafeShutdown());
        commands.put("online",       new Online());
        commands.put("permission",   new Permission());
        commands.put("bind",         new Bind());
        commands.put("search",       new Search());
        commands.put("connect",      new Connect());
        commands.put("disconnect",   new Disconnect());
        commands.put("help",         new Help());
        commands.put("reloadcs",     new ReloadCS());
        commands.put("getroles",     new GetRoles());
        commands.put("reserve",      new Reserve());
        commands.put("forbid",       new Forbid());
        commands.put("pardon",       new Pardon());
        commands.put("job",          new Job());
        commands.put("register",     new Register());
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
