package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author izarooni
 */
public class CmdDisconnect extends BaseCommand {

    public CmdDisconnect(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Disconnect a specified player from in-game";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Optional<User> author = message.getAuthor();
        Mono<TextChannel> chm = message.getChannel().ofType(TextChannel.class);

        if (ServerSession.getSession() == null) {
            chm.blockOptional().ifPresent(c -> c.createMessage("The server is unavailable.").blockOptional());
            return;
        }

        Command.CommandArg[] args = command.args;
        chm.blockOptional().ifPresent(c -> {
            MaplePacketWriter w = new MaplePacketWriter();
            w.write(Headers.Disconnect.value);
            if (w.writeBoolean(c.getType() == Channel.Type.DM)) {
                w.writeLong(author.map(User::getId).get().asLong());
                ServerSession.sendPacket(w.getPacket());
            } else if (args.length == 1) {
                String username = args[0].toString();
                w.writeLong(c.getId().asLong());
                w.writeMapleString(username);
                ServerSession.sendPacket(w.getPacket());
            } else {
                c.createEmbed(e -> {
                    e.setTitle("How to use the command");
                    e.addField("description", getDescription(), false);
                    e.setDescription("\r\n**syntax**: `" + getName() + " <ign>`");
                }).block();
            }
        });
    }
}
