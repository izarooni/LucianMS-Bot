package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author izarooni
 */
public class CmdDisconnect extends BaseCommand {

    public CmdDisconnect(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Disconnect a specified player from LucianMS in-game";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        if (ServerSession.getSession() == null) {
            createResponse(event).withContent("The server is currently not online!").build();
            return;
        }

        Command.CommandArg[] args = command.args;
        IChannel channel = event.getChannel();

        MaplePacketWriter writer = new MaplePacketWriter();
        writer.write(Headers.Disconnect.value);
        writer.write(channel.isPrivate() ? 1 : 0);

        if (channel.isPrivate()) {
            writer.writeLong(event.getAuthor().getLongID());
            ServerSession.sendPacket(writer.getPacket());
        } else if (args.length == 1) {
            String username = args[0].toString();
            writer.writeLong(channel.getLongID());
            writer.writeMapleString(username);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <ign>`");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
