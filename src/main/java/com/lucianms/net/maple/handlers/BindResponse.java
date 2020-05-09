package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;

/**
 * @author izarooni
 */
public class BindResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelId = reader.readLong();
        long authorId = reader.readLong();
        byte result = reader.readByte();

        TextChannel ch = Discord.getBot().getClient().getChannelById(Snowflake.of(channelId)).ofType(TextChannel.class).blockOptional().orElse(null);
        User user = Discord.getBot().getClient().getUserById(Snowflake.of(authorId)).blockOptional().orElse(null);
        if (user != null && ch != null) {
            if (result == 1) {
                String key = reader.readMapleAsciiString();
                String accountUsername = reader.readMapleAsciiString();
                user.getPrivateChannel().blockOptional().ifPresent(pch -> {
                    pch.createMessage("Hey there! I see you're trying to bind the account `" + accountUsername + "`. If it's truly yours, go ahead an shit this code to the world in-game on any character belonging to that account\r\n```" + key + "```").block();
                    ch.createMessage("<@" + user.getId().asString() + "> check your DMs! Don't leave me hanging~").block();
                });
            } else if (result == 2) {
                ch.createMessage("<@" + user.getId().asString() + "> that account is already bound to a Discord account.").blockOptional();
            } else if (result == 3) {
                user.getPrivateChannel().blockOptional().ifPresent(pch -> pch.createMessage("Success! Your Chirithy and Discord account are now bound.").block());
            } else {
                ch.createMessage("The account does not exist or does not have any characters online.").block();
            }
        }
    }
}
