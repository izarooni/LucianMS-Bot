package com.lucianms.scheduler.tasks;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.Collections;

/**
 * @author izarooni
 */
public class DelayedMessageDelete implements Runnable {

    private final IMessage[] messages;

    public DelayedMessageDelete(IMessage... messages) {
        this.messages = messages;
    }

    @Override
    public void run() {
        for (IMessage message : messages) {
            RequestBuffer.request(() -> message.getChannel().bulkDelete(Collections.singletonList(message)));
        }
    }
}
