package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;

/**
 * @author izarooni
 */
public class ReloadCSResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        createEmbedResponse(channelID, e -> e.setDescription("Cash Shop commoities reloaded."));
    }
}
