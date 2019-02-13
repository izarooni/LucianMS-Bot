package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * @author izarooni
 */
public class DisconnectResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        boolean privateChannel = reader.readByte() == 1;
        long ID = reader.readLong();
        byte disconnectResult = reader.readByte();

        if (privateChannel) {
            IUser user = Discord.getBot().getClient().getUserByID(ID);
            if (user != null) {
                IPrivateChannel channel = Discord.getBot().getClient().getOrCreatePMChannel(user);
                if (disconnectResult == 0) {
                    channel.sendMessage("Successfully disconnected " + reader.readMapleAsciiString());
                } else if (disconnectResult == 2) {
                    channel.sendMessage("I wasn't able to find an account bound to your Discord account");
                }
            }
        } else {
            IChannel channel = Discord.getBot().getClient().getChannelByID(ID);
            if (disconnectResult == 0) {
                channel.sendMessage("Successfully disconnected");
            } else if (disconnectResult == 1) {
                channel.sendMessage("Unable to find any player");
            } else {
                channel.sendMessage("An error occurred");
            }
        }
    }
}
