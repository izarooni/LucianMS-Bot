package com.lucianms;

import com.lucianms.cmds.*;
import com.lucianms.commands.AbstractCommandHelper;
import com.lucianms.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;

/**
 * @author izarooni
 */
public class CommandHelper extends AbstractCommandHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHelper.class);

    private HashMap<String, BaseCommand> commands = new HashMap<>();

    @Override
    public void onLoad() {
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
        // @formatter:on
    }

    @Override
    public void onUnload() {
        commands.clear();
    }

    @Override
    public void onCommand(MessageReceivedEvent event) {
        Command command = Command.parse(event.getMessage().getContent());
        if (command == null) {
            // not a command
            return;
        }
        BaseCommand base = commands.get(command.getCommand());
        if (base != null) {
            if (!base.isPermissionRequired() || base.canExecute(event, command.getCommand().toLowerCase())) {
                base.invoke(event, command);
            } else {
                event.getChannel().sendMessage("You do not have permission to use this command");
            }
        } else {
            LOGGER.info("Unable to find handler for command {}", command.getCommand());
        }
    }
}
