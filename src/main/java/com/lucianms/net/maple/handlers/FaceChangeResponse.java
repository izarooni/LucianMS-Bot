package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author izarooni
 */
public class FaceChangeResponse extends DiscordResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(FaceChangeResponse.class);

    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        String username = reader.readMapleAsciiString();
        byte result = reader.readByte();

        if (result == 1) {
            createMessageResponse(channelID, m -> m.setContent("Updated face for `" + username + "`"));
        } else if (result == 2) {
            createMessageResponse(channelID, m -> m.setContent("Updated face offline for `" + username + "`"));
        } else if (result == 0) {
            createMessageResponse(channelID, m -> m.setContent("Unable to find any player named `" + username + "`"));
        } else if (result == -1) {
            createMessageResponse(channelID, m -> m.setContent("Failed to update face for `" + username + "`"));
        }
    }
}
