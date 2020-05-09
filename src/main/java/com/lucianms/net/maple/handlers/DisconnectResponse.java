package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import discord4j.core.object.entity.PrivateChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

/**
 * @author izarooni
 */
public class DisconnectResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        boolean privateChannel = reader.readByte() == 1;
        long ID = reader.readLong();
        byte disconnectResult = reader.readByte();

        if (privateChannel) {
            Mono<User> user = Discord.getBot().getClient().getUserById(Snowflake.of(ID));
            if (user.blockOptional().isPresent()) {
                PrivateChannel dm = user.block().getPrivateChannel().block();
                if (disconnectResult == 0) {
                    dm.createMessage("Successfully disconnected `" + reader.readMapleAsciiString() + "`").block();
                } else if (disconnectResult == 2) {
                    dm.createMessage("You have not bound your Discord account.").block();
                }
            }
        } else {
            Mono<TextChannel> chm = Discord.getBot().getClient().getChannelById(Snowflake.of(ID)).ofType(TextChannel.class);
            if (disconnectResult == 0) {
                chm.blockOptional().ifPresent(ch -> ch.createMessage("Successfully disconnected").block());
            } else if (disconnectResult == 1) {
                chm.blockOptional().ifPresent(ch -> ch.createMessage("Unable to find the player").block());
            } else {
                chm.blockOptional().ifPresent(ch -> ch.createMessage("An error occurred").block());
            }
        }
    }
}
