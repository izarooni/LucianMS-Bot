package com.lucianms.commands;

import sx.blah.discord.handle.obj.IChannel;

public enum CommandType {
    PrivateMessage, Public, Both;

    public boolean canUseCommand(IChannel channel) {
        return (channel.isPrivate() ? 0 : 1) == ordinal() || this == Both;
    }
}
