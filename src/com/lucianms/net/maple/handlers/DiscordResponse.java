package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public abstract class DiscordResponse {

    public final MessageBuilder createResponse(int channelID) {
        return new MessageBuilder(Discord.getBot().getClient()).withChannel(channelID);
    }

    public final EmbedBuilder createEmbed() {
        return new EmbedBuilder().withColor(26, 188, 156);
    }

    public abstract void handle(MaplePacketReader reader);
}
