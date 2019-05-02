package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public class OnlineResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelId = reader.readLong();
        IChannel channel = Discord.getBot().getClient().getChannelByID(channelId);

        MessageBuilder mb = new MessageBuilder(Discord.getBot().getClient()).withChannel(channel);
        EmbedBuilder eb = createEmbed().withTitle("[ Online Players ]");

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

                eb.appendField(String.format("World %d - Channel %d", (a + 1), (b + 1)), sb.toString(), false);
            }
        }
        mb.withEmbed(eb.build()).build();
    }
}
