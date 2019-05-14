package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * @author izarooni
 */
public class CmdSafeShutdown extends BaseCommand {

    public CmdSafeShutdown(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Attempt to safely shutdown the server";
    }

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
        }
        System.exit(0);
    }
}
