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
public class CmdSetHair extends BaseCommand {

    public CmdSetHair(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Change the hair of a specified in-game player";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_hairId = args[1].parseUnsignedNumber();
            if (var_hairId == null) {
                ch.createEmbed(e -> e.setDescription(String.format("`%s` is not a valid ID", args[1].toString()))).block();
                return;
            }
            int hairId = var_hairId.intValue();
            MaplePacketWriter writer = new MaplePacketWriter(1 + username.length());
            writer.write(Headers.SetHair.value);
            writer.writeLong(ch.getId().asLong());
            writer.writeMapleString(username);
            writer.writeInt(hairId);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <ign> <hair ID>`");
            }).block();
        }
    }
}
