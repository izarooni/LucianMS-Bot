package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author izarooni
 */
public class CmdSearch extends BaseCommand {

    public CmdSearch(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Search for a thing's ID via specified name";
    }

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
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <type> <name>`")
                    .appendDesc("\r\n**example**: `").appendDesc(getName()).appendDesc(" item red potion`")
                    .appendDesc("\r\nThe `<type>` parameter can be any of the following: `map`, `use`, `etc`, `cash`, `equip` or `mob`");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
