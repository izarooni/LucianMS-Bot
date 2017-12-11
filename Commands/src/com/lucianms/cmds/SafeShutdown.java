package com.lucianms.cmds;

import com.lucianms.BaseCommand;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import com.lucianms.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * @author izarooni
 */
public class SafeShutdown extends BaseCommand {

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        IMessage message = event.getChannel().sendMessage("Stopping the server...");
        try {
            MaplePacketWriter writer = new MaplePacketWriter(1);
            writer.write(Headers.Shutdown.value);
            ServerSession.sendPacket(writer.getPacket());
            message.edit("Complete... Goodbye!");
        } catch (NullPointerException e) {
            message.edit("No server connection established. Probably already shut down. Goodbye!");
        } System.exit(0);
    }
}
