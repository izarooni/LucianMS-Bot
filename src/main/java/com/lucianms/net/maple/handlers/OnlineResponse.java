package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;

/**
 * @author izarooni
 */
public class OnlineResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelId = reader.readLong();
        TextChannel ch = Discord.getBot().getClient().getChannelById(Snowflake.of(channelId)).ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        ch.createEmbed(e -> {
            e.setTitle("[ Online Players ]");
            byte worlds = reader.readByte();
            for (int a = 0; a < worlds; a++) {
                byte channels = reader.readByte();
                for (int b = 0; b < channels; b++) {
                    StringBuilder sb = new StringBuilder();
                    short usernames = reader.readShort();
                    for (int c = 0; c < usernames; c++) {
                        String username = reader.readMapleAsciiString();
                        sb.append(username).append(", ");
                    }
                    if (sb.length() > 2) {
                        sb.setLength(sb.length() - 2);
                    } else if (sb.length() == 0) {
                        sb.append("No players");
                    }

                    e.addField(String.format("World %d - Channel %d", (a + 1), (b + 1)), sb.toString(), false);
                }
            }
        }).block();
    }
}
