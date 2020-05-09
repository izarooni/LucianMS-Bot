package com.lucianms.commands;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.TextChannel;

public enum CommandType {
    Private, Public, Both;

    public boolean isChannelApplicable(TextChannel channel) {
        return (channel.getType() == Channel.Type.GUILD_TEXT ? 1 : 0) == ordinal() || this == Both;
    }
}
