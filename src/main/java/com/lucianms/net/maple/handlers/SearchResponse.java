package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;

/**
 * @author izarooni
 */
public class SearchResponse extends DiscordResponse {
    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        int count = reader.readInt();

        if (count < 0) {
            createEmbedResponse(channelID, e -> e.setDescription(reader.readMapleAsciiString()));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("```\r\n");
            for (int i = 0; i < count; i++) {
                sb.append(reader.readMapleAsciiString()).append("\r\n");
            }
            sb.append("```");
            if (sb.length() < 2000) createMessageResponse(channelID, e -> e.setContent(sb.toString()));
            else
                createMessageResponse(channelID, e -> e.setContent("There are too many results. Please be more specific with your search."));
        }
    }
}
