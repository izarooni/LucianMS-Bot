package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.server.Guild;
import com.lucianms.server.GuildTicket;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class Ticket extends BaseCommand {

    public Ticket() {
        super(false, CommandType.Both);
    }

    @Override
    public String getDescription() {
        return "Create a report for bugs or if you need support";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().get(event.getGuild().getLongID());
        String ticketCreationCID = guild.getGuildConfig().getCIDTicketCreation();
        String ticketDestinationCID = guild.getGuildConfig().getCIDTicketDestination();
        IMessage eventMessage = event.getMessage();

        if (ticketCreationCID.isEmpty() || ticketDestinationCID.isEmpty()) {
            eventMessage.reply("The ticket system has not been configured yet.");
            return;
        } else if (!event.getChannel().getStringID().equalsIgnoreCase(ticketCreationCID)) {
            eventMessage.reply(String.format("This command can only be used in <#%s>", ticketCreationCID));
            return;
        }
        String content = command.concatFrom(0, " ");
        if (content.isEmpty()) {
            eventMessage.reply("You must specify a message for your ticket");
            return;
        }
        final int ticketID = guild.getTickets().getTicketUID().getAndIncrement();
        String description = String.format(":tickets: A ticket has been created (No. #%04d)", ticketID);
        createResponse(event).withEmbed(createEmbed().withDescription(description).build()).build();

        IChannel destination = guild.getGuild().getChannelByID(Long.parseLong(ticketDestinationCID));
        IUser author = event.getAuthor();
        EmbedObject embed = createEmbed()
                .withTitle(String.format("Ticket #%04d", ticketID))
                .withDescription(content)
                .withFooterText(String.format("by %s#%s / %s", author.getName(), author.getDiscriminator(), author.getLongID()))
                .build();
        final long creationMessageID = createResponse(event).withChannel(destination)
                .withContent(String.format("https://discordapp.com/channels/%d/%d/%d",
                        event.getGuild().getLongID(),
                        event.getChannel().getLongID(),
                        eventMessage.getLongID()
                ))
                .withEmbed(embed).build().getLongID();

        guild.getTickets().put(creationMessageID, new GuildTicket(ticketID, author.getLongID(), creationMessageID, eventMessage.getLongID()));
    }
}
