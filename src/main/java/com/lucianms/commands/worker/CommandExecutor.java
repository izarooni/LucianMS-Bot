package com.lucianms.commands.worker;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;

/**
 * @author izarooni
 */
public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);
    public static String CMD_PREFIX;

    private static HashMap<String, BaseCommand> COMMANDS = new HashMap<>();

    static {
        for (CommandUtil cutil : CommandUtil.values()) {
            try {
                BaseCommand baseCommand = cutil.command.getConstructor(cutil.getClass()).newInstance(cutil);
                COMMANDS.put(cutil.name().toLowerCase(), baseCommand);
            } catch (Exception e) {
                LOGGER.error("Failed to instantiate command {}", cutil, e);
            }
        }
    }

    public static BaseCommand getCommand(String cmd) {
        return COMMANDS.get(cmd);
    }

    public static void execute(MessageReceivedEvent event) {
        Command command = Command.parse(event.getMessage().getContent());
        if (command == null) {
            // not a command
            return;
        }
        BaseCommand base = COMMANDS.get(command.getCommand());
        if (base != null) {
            CommandType commandType = base.getCommandType();
            // channel type (private or public) matches command requirement
            if (commandType.canUseCommand(event.getChannel())) {
                if (!event.getChannel().isPrivate()) { // is a public discord channel
                    if (!base.getPermission().requirePermission || base.canExecute(event, command.getCommand().toLowerCase())) {
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
