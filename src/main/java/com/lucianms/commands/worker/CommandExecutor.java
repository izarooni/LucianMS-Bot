package com.lucianms.commands.worker;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.cmds.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;

/**
 * @author izarooni
 */
public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private static HashMap<String, BaseCommand> COMMANDS = new HashMap<>();

    static {
        // @formatter:off
        COMMANDS.put("bind",         new Bind());
        COMMANDS.put("connect",      new Connect());
        COMMANDS.put("disconnect",   new Disconnect());
        COMMANDS.put("forbid",       new Forbid());
        COMMANDS.put("getroles",     new GetRoles());
        COMMANDS.put("help",         new Help());
        COMMANDS.put("job",          new Job());
        COMMANDS.put("online",       new Online());
        COMMANDS.put("pardon",       new Pardon());
        COMMANDS.put("permission",   new Permission());
        COMMANDS.put("register",     new Register());
        COMMANDS.put("reloadcs",     new ReloadCS());
        COMMANDS.put("reserve",      new Reserve());
        COMMANDS.put("safeshutdown", new SafeShutdown());
        COMMANDS.put("search",       new Search());
        COMMANDS.put("setface",      new SetFace());
        COMMANDS.put("sethair",      new SetHair());
        COMMANDS.put("strip",        new Strip());
        COMMANDS.put("unstuck",      new Unstuck());
        COMMANDS.put("warp",         new Warp());
        COMMANDS.put("sql",          new Sql());
        COMMANDS.put("ticket",       new Ticket());
        COMMANDS.put("set",          new Set());
        COMMANDS.put("apply",        new Apply());
        COMMANDS.put("embed",        new Embed());
        COMMANDS.put("news",         new News());
        COMMANDS.put("updates",      new Updates());
        // @formatter:on
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
