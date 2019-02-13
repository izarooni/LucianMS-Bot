package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sx.blah.discord.handle.obj.IChannel;

/**
 * @author izarooni
 */
public class ReloadCSResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        IChannel channel = Discord.getBot().getClient().getChannelByID(channelID);
        if (channel != null) {
            channel.sendMessage("Cash Shop commodities reloaded!");
        }
    }
}
