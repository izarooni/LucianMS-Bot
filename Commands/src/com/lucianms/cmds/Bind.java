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
public class Bind extends BaseCommand {

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (ServerSession.getSession() == null) {
            event.getChannel().sendMessage("Binding accounts is impossible because the server isn't even online right now!");
            return;
        }
        if (args.length == 1) {
            String accountUsername = args[0].toString();
            MaplePacketWriter writer = new MaplePacketWriter();
            writer.write(Headers.Bind.value);
            writer.writeLong(event.getChannel().getLongID());
            writer.writeLong(event.getAuthor().getLongID());
            writer.writeMapleString(accountUsername);
            ServerSession.sendPacket(writer.getPacket());
        }
    }
}
