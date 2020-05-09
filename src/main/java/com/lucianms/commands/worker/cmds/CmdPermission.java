package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import com.lucianms.server.user.DUser;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * <p>
 * !permission add/remove ID permission
 * </p>
 *
 * @author izarooni
 */
public class CmdPermission extends BaseCommand {

    public CmdPermission(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Add or remove permissions for a specified Discord user";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Mono<MessageChannel> ch = message.getChannel();
        Optional<User> author = message.getAuthor();
        DGuild guild = Discord.getGuild(event.getGuild());
        DUser user = guild.getUser(author.map(User::getId).get().asString());

        TextChannel tch = ch.ofType(TextChannel.class).block();
        Command.CommandArg[] args = command.args;
        if (args.length >= 3) {
            String action = args[0].toString();
            Flux<CommandUtil> permissions;
            if (args[1].toString().equals("*")) permissions = Flux.just(CommandUtil.values());
            else permissions = Flux.fromArray(args[1].toString().split(","))
                    .flatMap(s -> Flux.just(CommandUtil.fromName(s)));

            if (action.equalsIgnoreCase("add")) {
                message.getUserMentions()
                        .flatMap(u -> Flux.just(guild.getGuild().getClient().getUserById((u.getId()))))
                        .flatMap(u -> Flux.just(guild.addUserIfAbsent(u.block())))
                        .subscribe(u -> {
                            permissions.subscribe(c -> u.getPermissions().give(guild.getId().asString(), c));
                            u.getPermissions().save();
                        });
                message.getRoleMentionIds().forEach(roleID -> permissions.subscribe(c -> guild.getPermissions().give(roleID.asString(), c)));
                guild.getPermissions().save();
                tch.createMessage("Success!").block();
            } else if (action.equalsIgnoreCase("remove")) {
                message.getUserMentions()
                        .flatMap(u -> Flux.just(guild.getGuild().getClient().getUserById((u.getId()))))
                        .flatMap(u -> Flux.just(guild.addUserIfAbsent(u.block())))
                        .subscribe(u -> {
                            permissions.subscribe(c -> u.getPermissions().give(guild.getId().asString(), c));
                            u.getPermissions().save();
                        });
                message.getRoleMentionIds().forEach(id -> permissions.subscribe(c -> guild.getPermissions().revoke(id.asString(), c)));
                guild.getPermissions().save();
                tch.createMessage("Success!").block();
            }
        } else {
            tch.createEmbed(c -> {
                c.setTitle("How to use the command");
                c.addField("description", getDescription(), false);
                c.setDescription(String.format("\r\n**syntax**: %s <add/remove> <permissions/*> <role/user>", getName()));
            }).block();
        }
    }
}
