package com.lucianms.commands;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * @author izarooni
 */
public abstract class AbstractCommandHelper {

    public abstract void onLoad();

    public abstract void onUnload();

    public abstract void onCommand(MessageReceivedEvent event);
}
