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
public class CmdSetFace extends BaseCommand {

    public CmdSetFace(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Change the face of a specified in-game player";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_faceId = args[1].parseUnsignedNumber();
            if (var_faceId == null) {
                ch.createEmbed(e -> e.setDescription(String.format("`%s` is not a valid ID", args[1]))).block();
                return;
            }
            int faceId = var_faceId.intValue();

            MaplePacketWriter w = new MaplePacketWriter(13 + username.length());
            w.write(Headers.SetFace.value);
            w.writeLong(ch.getId().asLong());
            w.writeMapleString(username);
            w.writeInt(faceId);
            ServerSession.sendPacket(w.getPacket());
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <ign> <face ID>`");
            }).block();
        }
    }
}
