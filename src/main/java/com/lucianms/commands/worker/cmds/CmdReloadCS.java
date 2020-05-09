package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;

/**
 * @author izarooni
 */
public class CmdReloadCS extends BaseCommand {

    public CmdReloadCS(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Reload the in-game Cash Shop commodities";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        if (ServerSession.getSession() == null) {
            ch.createMessage("Unable to contact the server.").block();
        } else {
            MaplePacketWriter writer = new MaplePacketWriter(1);
            writer.write(Headers.ReloadCS.value);
            writer.writeLong(ch.getId().asLong());
            ServerSession.sendPacket(writer.getPacket());
        }
    }
}
