package com.lucianms.cmds;

import com.lucianms.BaseCommand;
import com.lucianms.commands.Command;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class Disconnect extends BaseCommand {

    @Override
    public boolean canExecute(MessageReceivedEvent event, String permission) {
        return super.canExecute(event, permission);
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 1) {
            String username = args[0].toString();

            MaplePacketWriter writer = new MaplePacketWriter();
            writer.write(Headers.Disconnect.value);
            writer.writeLong(event.getChannel().getLongID());
            writer.writeMapleString(username);

            ServerSession.sendPacket(writer.getPacket());
        }
    }
}
