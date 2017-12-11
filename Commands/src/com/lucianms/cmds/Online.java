package com.lucianms.cmds;

import com.lucianms.utils.packet.send.MaplePacketWriter;
import com.lucianms.BaseCommand;
import com.lucianms.commands.Command;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class Online extends BaseCommand {

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        MaplePacketWriter writer = new MaplePacketWriter(1);
        writer.write(Headers.Online.value);
        writer.writeLong(event.getChannel().getLongID());
        ServerSession.sendPacket(writer.getPacket());
    }
}
