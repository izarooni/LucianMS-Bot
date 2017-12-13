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
public class Search extends BaseCommand {

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length > 1) {
            String iType = args[0].toString();
            String message = command.concatFrom(1, " ");

            MaplePacketWriter writer = new MaplePacketWriter(1 + (iType.length() + message.length()));
            writer.write(Headers.Search.value);
            writer.writeLong(event.getChannel().getLongID());
            writer.writeMapleString(iType);
            writer.writeMapleString(message);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            event.getChannel().sendMessage("Syntax: !search <type> <name> where type is map, use, etc, cash, equip or mob.");
        }
    }
}
