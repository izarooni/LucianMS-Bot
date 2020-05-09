package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.TextChannel;

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
    public void invoke(MessageCreateEvent event, Command command) {
        TextChannel ch = event.getMessage().getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        Command.CommandArg[] args = command.args;
        if (args.length > 1) {
            String iType = args[0].toString();
            String message = command.concatFrom(1, " ");

            MaplePacketWriter writer = new MaplePacketWriter(1 + (iType.length() + message.length()));
            writer.write(Headers.Search.value);
            writer.writeLong(ch.getId().asLong());
            writer.writeMapleString(iType);
            writer.writeMapleString(message);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <item> <name>`" +
                        "\r\n**example**: `" + getName() + " item red potion`" +
                        "\r\nThe `<type>` parameter can be any of the following: `map`, `use`,  `etc`, `cash`, `equip` or `mob`");
            }).block();
        }
    }
}
