package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;

/**
 * @author izarooni
 */
public class MessageChannel extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        String content = reader.readMapleAsciiString();
        createMessageResponse(channelID, e -> e.setContent(content));
    }
}
