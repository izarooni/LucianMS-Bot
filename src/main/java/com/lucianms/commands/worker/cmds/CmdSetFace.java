package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

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
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_faceId = args[1].parseUnsignedNumber();
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
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <ign> <face ID>`");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
