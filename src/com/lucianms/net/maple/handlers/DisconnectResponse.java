package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sx.blah.discord.handle.obj.IChannel;

/**
 * @author izarooni
 */
public class DisconnectResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelID = reader.readLong();
        boolean disconnect = reader.readByte() == 1;
        IChannel channel = Discord.getBot().getClient().getChannelByID(channelID);
        if (disconnect) {
            channel.sendMessage("Successfully disconnected");
        } else {
            channel.sendMessage("Failed to disconnect (Perhaps an incorrect username?)");
        }
    }
}
