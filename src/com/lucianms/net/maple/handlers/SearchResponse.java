package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public class SearchResponse extends DiscordResponse {
    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        int count = reader.readInt();

        IChannel channel = Discord.getBot().getClient().getChannelByID(channelID);

        if (count == -1) {
            channel.sendMessage(reader.readMapleAsciiString());
        } else {
            MessageBuilder mb = new MessageBuilder(Discord.getBot().getClient()).withChannel(channel);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(reader.readMapleAsciiString()).append("\r\n");
            }
            mb.appendCode("", sb.toString());
            if (mb.getContent().length() < 2000) {
                mb.build();
            } else {
                channel.sendMessage("There are too many results to display. Please be more specific with your search query");
            }
        }
    }
}
