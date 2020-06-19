package com.lucianms.commands.worker;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * @author izarooni
 */
public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);
    public static String CMD_PREFIX;

    private static HashMap<String, BaseCommand> COMMANDS = new HashMap<>();

    static {
        for (CommandUtil cmd : CommandUtil.values()) {
            try {
                Class<? extends BaseCommand> clazz = cmd.command;
                if (clazz != null) {
                    Constructor<? extends BaseCommand> constructor = clazz.getConstructor(cmd.getClass());
                    BaseCommand baseCommand = constructor.newInstance(cmd);
                    COMMANDS.put(cmd.name().toLowerCase(), baseCommand);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to instantiate command {}", cmd, e);
            }
        }
    }

    public static BaseCommand getCommand(String cmd) {
        return COMMANDS.get(cmd);
    }

    public static void execute(MessageCreateEvent event) {
        Command command = Command.parse(event.getMessage().getContent().orElse(null));
        if (command == null) return; // not a command

        TextChannel ch = event.getMessage().getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return; // whatever man

        BaseCommand base = COMMANDS.get(command.getCommand());
        if (base != null) {
            CommandType cmdType = base.getCommandType();
            // channel type (private or public) matches command requirement
            if (cmdType.isChannelApplicable(ch)) {
                if (ch.getType() == Channel.Type.GUILD_TEXT) { // is a public discord channel
                    if (!base.getPermission().needsPermission || base.canExecute(event, base.getPermission())) {
                        try {
                            base.invoke(event, command);
                        } catch (Exception e) {
                            LOGGER.error("Failed to invoke command '{}'", command.getCommand(), e);
                            ch.createMessage("Error occurred within the command.").block();
                        }
                    } else {
                        ch.createMessage("You don't have permission to use this command.").block();
                    }
                } else { // is private message
                    base.invoke(event, command);
                }
            } else {
                ch.createMessage("This command cannot be used here.").block();
            }
        }
    }
}
