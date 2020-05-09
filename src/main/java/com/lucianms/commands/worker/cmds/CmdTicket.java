package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import com.lucianms.server.GuildTicket;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class CmdTicket extends BaseCommand {

    public CmdTicket(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Create a report for bugs or if you need support";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Optional<User> author = message.getAuthor();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        DGuild guild = Discord.getGuild(event.getGuild());
        String ticketCreationCID = guild.getGuildConfig().getCIDTicketCreation();
        String ticketDestinationCID = guild.getGuildConfig().getCIDTicketDestination();

        if (ticketCreationCID.isEmpty() || ticketDestinationCID.isEmpty()) {
            ch.createEmbed(e -> e.setDescription("The ticket system has not been configured.")).block();
            return;
        } else if (!ch.getId().asString().equals(ticketCreationCID)) {
            ch.createEmbed(e -> e.setDescription(String.format("This command can only be used in <#%s>", ticketCreationCID))).block();
            return;
        }
        String content = command.concatFrom(0, " ");
        if (content.isEmpty()) {
            ch.createEmbed(e -> e.setDescription("You must specify a message for your ticket.")).block();
            return;
        }
        final int ticketID = guild.getTickets().getTicketUID().getAndIncrement();
        String description = String.format(":tickets: A ticket has been created (No. #%04d)", ticketID);
        ch.createEmbed(e -> e.setDescription(description)).block();

        Mono<TextChannel> dest = Discord.getBot().getClient().getChannelById(Snowflake.of(ticketDestinationCID)).ofType(TextChannel.class);
        dest.blockOptional().ifPresent(destCh -> {
            User user = author.get();
            Message ticket = destCh.createMessage(e -> {
                e.setContent(String.format("https://discordapp.com/channels/%s/%s/%s",
                        guild.getId().asString(),
                        ch.getId().asString(),
                        message.getId().asString()
                ));
                e.setEmbed(c -> {
                    c.setTitle(String.format("Ticket #%04d", ticketID));
                    c.setDescription(content);
                    c.setFooter(String.format("by %s#%s / %s", user.getUsername(), user.getDiscriminator(), user.getId().asString()), null);
                });
            }).block();
            guild.getTickets().put(ticket.getId().asString(), new GuildTicket(ticketID, user.getId().asString(), ticket.getId().asString(), message.getId().asString()));
        });
    }
}
