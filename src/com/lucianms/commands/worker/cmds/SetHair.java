package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
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
public class SetHair extends BaseCommand {

    public SetHair() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Change the hair of a specified in-game player";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_hairId = args[1].parseNumber();
            if (var_hairId == null) {
                new MessageBuilder(Discord.getBot().getClient()).withChannel(event.getChannel()).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid ID").build();
                return;
            }
            int hairId = var_hairId.intValue();
            MaplePacketWriter writer = new MaplePacketWriter(1 + username.length());
            writer.write(Headers.SetHair.value);
            writer.writeLong(event.getChannel().getLongID());
            writer.writeMapleString(username);
            writer.writeInt(hairId);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            new MessageBuilder(Discord.getBot().getClient()).withChannel(event.getChannel()).appendContent("That doesn't look right... Try this").appendCode("", Discord.getConfig().getString("CommandTrigger") + "setface <username> <hair_ID>").build();
        }
    }
}
