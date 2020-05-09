package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.server.DGuild;
import com.lucianms.server.user.DUser;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Entity;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

/**
 * @author izarooni
 */
public class CmdBind extends BaseCommand {

    public CmdBind(CommandUtil permissions) {
        super(permissions);
    }

    @Override
    public String getDescription() {
        return "Connects your Discord account to your in-game account";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Mono<TextChannel> chm = message.getChannel().ofType(TextChannel.class);
        DGuild guild = Discord.getGuild(event.getGuild());
        DUser user = guild.getUser(message.getAuthor().map(User::getId).get().asString());

        Command.CommandArg[] args = command.args;
        if (args.length == 1) {
            event.getMessage().delete();
            if (ServerSession.getSession() == null) {
                chm.blockOptional().ifPresent(c -> c.createMessage("Binding accounts not possible because the server is not available."));
                return;
            }
            String accountUsername = args[0].toString();
            MaplePacketWriter writer = new MaplePacketWriter();
            writer.write(Headers.Bind.value);
            writer.writeLong(message.getChannel().map(Entity::getId).block().asLong());
            writer.writeLong(user.getId().asLong());
            writer.writeMapleString(accountUsername);
            ServerSession.sendPacket(writer.getPacket());
        } else {
            chm.blockOptional().ifPresent(c -> {
                c.createEmbed(e -> {
                    e.setTitle("How to use the command");
                    e.addField("description", getDescription(), false);
                    e.setDescription("\r\n**syntax**: `" + getName() + " <account name>`");
                }).block();
            });
        }
    }
}
