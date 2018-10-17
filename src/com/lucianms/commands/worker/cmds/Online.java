package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.Command;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class Online extends BaseCommand {

    public Online() {
        super(false);
    }

    @Override
    public String getDescription() {
        return "Display a list of all online in-game players";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        MaplePacketWriter writer = new MaplePacketWriter(1);
        writer.write(Headers.Online.value);
        writer.writeLong(event.getChannel().getLongID());
        ServerSession.sendPacket(writer.getPacket());
    }
}
