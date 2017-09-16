package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;
import com.lucianms.Discord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public class HairChangeResponse extends DiscordResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HairChangeResponse.class);

    @Override
    public void handle(MaplePacketReader reader) {
        long channelId = reader.readLong();
        String username = reader.readMapleAsciiString();
        byte result = reader.readByte();

        IChannel channel = Discord.getBot().getClient().getChannelByID(channelId);

        if (channel == null) {
            LOGGER.warn("Invalid channel ID received {}", channelId);
            return;
        }

        if (result == 1) {
            new MessageBuilder(Discord.getBot().getClient())
                    .withChannel(channel)
                    .appendContent("Updated ")
                    .appendContent(username + "'s", MessageBuilder.Styles.INLINE_CODE)
                    .appendContent(" hair").build();
        } else if (result == 2) {
            new MessageBuilder(Discord.getBot().getClient())
                    .withChannel(channel)
                    .appendContent("Updated ")
                    .appendContent(username + "'s" , MessageBuilder.Styles.INLINE_CODE)
                    .appendContent(" hair offline").build();
        } else if (result == 0) {
            new MessageBuilder(Discord.getBot().getClient())
                    .withChannel(channel)
                    .appendContent("Unable to find any player named ")
                    .appendContent(username, MessageBuilder.Styles.INLINE_CODE).build();
        } else if (result == -1) {
            new MessageBuilder(Discord.getBot().getClient())
                    .withChannel(channel)
                    .appendContent("An error occurred while trying to update ")
                    .appendContent(username + "'s", MessageBuilder.Styles.INLINE_CODE)
                    .appendContent(" hair").build();
        }
    }
}
