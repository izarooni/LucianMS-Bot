package com.lucianms.commands;

import com.lucianms.utils.Disposable;
import com.lucianms.Discord;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Manages external command managers for a module command system
 *
 * @author izarooni
 */
public class CommandManager implements Disposable {

    private static HashMap<String, AbstractCommandHelper> managers = new HashMap<>();

    @Override
    public void dispose() {
        managers.values().forEach(AbstractCommandHelper::onUnload);
    }

    public static Collection<AbstractCommandHelper> getManagers() {
        return Collections.unmodifiableCollection(managers.values());
    }

    public static AbstractCommandHelper getCommandManager(String name) {
        return managers.get(name);
    }

    public static void addCommandManager(String name, AbstractCommandHelper manager) {
        managers.putIfAbsent(name, manager);
    }

    public static boolean isValidCommand(MessageReceivedEvent event) {
        return event.getMessage().getAuthor().getLongID() != Discord.getBot().getClient().getOurUser().getLongID() // bot mustn't self-execute
                && event.getMessage().getContent().startsWith(Discord.getConfig().getString("CommandTrigger"));
    }
}
