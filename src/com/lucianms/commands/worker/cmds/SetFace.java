package com.lucianms.cmds;

import com.lucianms.BaseCommand;
import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public class SetFace extends BaseCommand {

    public SetFace() {
        super(true);
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_faceId = args[1].parseNumber();
            if (var_faceId == null) {
                new MessageBuilder(Discord.getBot().getClient()).withChannel(event.getChannel()).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid ID").build();
                return;
            }
            int faceId = var_faceId.intValue();

            MaplePacketWriter writer = new MaplePacketWriter(13 + username.length());
            writer.write(Headers.SetFace.value);
            writer.writeLong(event.getChannel().getLongID());
            writer.writeMapleString(username);
            writer.writeInt(faceId);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            new MessageBuilder(Discord.getBot().getClient()).withChannel(event.getChannel()).appendContent("That doesn't look right... Try this").appendCode("", Discord.getConfig().getString("CommandTrigger") + "setface <username> <face_ID>").build();
        }
    }
}
