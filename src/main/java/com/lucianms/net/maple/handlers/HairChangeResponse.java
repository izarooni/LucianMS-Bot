package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author izarooni
 */
public class HairChangeResponse extends DiscordResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HairChangeResponse.class);

    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        String username = reader.readMapleAsciiString();
        byte result = reader.readByte();

        Mono<TextChannel> chm = Discord.getBot().getClient().getChannelById(Snowflake.of(channelID)).ofType(TextChannel.class);

        if (!chm.blockOptional().isPresent()) {
            LOGGER.warn("Invalid channel ID received {}", channelID);
            return;
        }

        TextChannel ch = chm.block();
        if (result == 1) {
            ch.createMessage("Updated hair for `" + username + "`").block();
        } else if (result == 2) {
            ch.createMessage("Updated hair offlien for `" + username + "`").block();
        } else if (result == 0) {
            ch.createMessage("Unable to find any player named `" + username + "`").block();
        } else if (result == -1) {
            ch.createMessage("Failed to update hair for `" + username + "`").block();
        }
    }
}
