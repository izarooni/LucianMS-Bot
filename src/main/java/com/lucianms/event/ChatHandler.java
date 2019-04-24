package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.server.Guild;
import com.lucianms.server.GuildTicket;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class ChatHandler {

    @EventSubscriber
    public void onReactionAdd(ReactionAddEvent event) {
        IGuild guild = event.getGuild();
        Guild lGuild = Discord.getGuilds().computeIfAbsent(guild.getLongID(), l -> new Guild(guild));
        GuildTicket ticket = lGuild.getTickets().get(event.getMessageID());
        if (ticket != null && !ticket.isCompleted()) {
            IChannel creationChannel = guild.getChannelByID(Long.parseLong(lGuild.getGuildConfig().getCIDTicketCreation()));
            EmbedObject build = new EmbedBuilder().withColor(26, 188, 60)
                    .withTitle(String.format("Ticket #%04d", ticket.getTicketID()))
                    .withDescription(":white_check_mark: Your ticket has been marked as complete!")
                    .build();
            creationChannel.sendMessage("<@" + ticket.getUserID() + ">", build);
            ticket.setCompleted(true);
        }
    }

    @EventSubscriber
    public void onGuildMessageReceived(MessageReceivedEvent event) {
        IGuild guild = event.getGuild();
        String content = event.getMessage().getContent();
        if (!event.getChannel().isPrivate()) {
            Guild lGuild = Discord.getGuilds().computeIfAbsent(guild.getLongID(), l -> new Guild(guild));
            if (event.getAuthor().getRolesForGuild(guild).stream().noneMatch(r -> r.getPermissions().contains(Permissions.MANAGE_MESSAGES))) {
                for (String words : lGuild.getGuildConfig().getWordBlackList()) {
                    Pattern pattern = Pattern.compile(words, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        event.getMessage().delete();
                        return;
                    }
                }
            }
        }
        if (Command.isValidCommand(event)) {
            CommandExecutor.execute(event);
        }
    }
}
