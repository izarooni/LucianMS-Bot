package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.user.DUser;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author izarooni
 */
public class CmdEmbed extends BaseCommand {

    public CmdEmbed(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Create your own embedded message";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Mono<MessageChannel> ch = event.getMessage().getChannel();
        Optional<User> author = event.getMessage().getAuthor();
        DUser user = event.getGuild().map(g -> Discord.getGuilds().get(g.getId().asString()))
                .map(g -> g.getUser(author.map(User::getId).get().asString())).block();
        boolean canDeletedMessages = ch.ofType(GuildChannel.class)
                .flatMap(c -> c.getEffectivePermissions(author.map(User::getId).get()))
                .map(p -> p.containsAll(PermissionSet.of(Permission.MANAGE_MESSAGES)))
                .blockOptional().orElse(false);

        TextChannel tch = ch.ofType(TextChannel.class).block();
        if (command.args.length > 1) {
            // quick embed
            String message = command.concatFrom(0, " ");
            int spIdx = message.indexOf('|');
            if (spIdx >= 0) {
                String title = message.substring(0, spIdx);
                String content = message.substring(spIdx + 1);
                tch.createEmbed(e -> {
                    e.setTitle(title);
                    e.setDescription(content);
                }).block();
            } else {
                tch.createEmbed(e -> e.setDescription(message)).block();
            }
        }
    }
}
