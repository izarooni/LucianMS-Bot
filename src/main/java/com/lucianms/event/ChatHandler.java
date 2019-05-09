package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.commands.worker.cmds.Apply;
import com.lucianms.server.Guild;
import com.lucianms.server.GuildTicket;
import com.lucianms.server.user.User;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
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
            lGuild.addUserIfAbsent(event.getAuthor());
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
        } else {
            User user = Discord.getUserHandles().get(event.getAuthor().getLongID());
            if (user != null) {
                if (user.getApplicationResponses() != null) {
                    onApplicationResponse(user, event);
                    return;
                }
            }
        }
        if (Command.isValidCommand(event)) {
            CommandExecutor.execute(event);
        }
    }

    private void onApplicationResponse(User user, MessageReceivedEvent event) {
        String content = event.getMessage().getContent();
        if (content.equalsIgnoreCase("cancel")) {
            event.getChannel().sendMessage("You have cancelled the application");
            user.setApplicationResponses(null);
            return;
        }
        // ask a question assigned to the current status
        if (user.getApplicationStatus() < Apply.Questions.length) {
            user.getApplicationResponses()[user.getApplicationStatus()] = content;
            user.setApplicationStatus(Apply.Questions.length);
        }

        if (content.equalsIgnoreCase("send")) {
            event.getChannel().sendMessage("Submitting your application now! Good luck~");
            Guild guild = Discord.getGuilds().get(user.getApplicationGuildID());
            IChannel applications = Discord.getBot().getClient().getChannelByID(Long.parseLong(guild.getGuildConfig().getCIDApplicationDestination()));
            EmbedBuilder embeder = createEmbed();
            for (int i = 0; i < Apply.Questions.length; i++) {
                embeder.appendField(String.format("%d ). %s", (i + 1), Apply.Questions[i]), user.getApplicationResponses()[i], false);
            }
            IUser dUser = user.getUser();
            embeder.withTitle("GM Application submission");
            embeder.withFooterText(String.format("%s#%s / %s", dUser.getName(), dUser.getDiscriminator(), dUser.getLongID()));
            applications.sendMessage(embeder.build());

            user.setApplicationGuildID(0);
            user.setApplicationStatus(0);
            user.setApplicationResponses(null);
            Discord.getUserHandles().remove(user.getUser().getLongID());
        } else {
            try {
                // ask the specified question for a new response
                int responseID = Integer.parseInt(content);
                user.setApplicationStatus(responseID - 1);
                event.getChannel().sendMessage(Apply.Questions[responseID - 1]);
            } catch (NumberFormatException ignore) {
                EmbedBuilder embeder = createEmbed();
                for (int i = 0; i < Apply.Questions.length; i++) {
                    embeder.appendField(String.format("%d ). %s", (i + 1), Apply.Questions[i]), user.getApplicationResponses()[i], false);
                }
                event.getChannel().sendMessage("Before I send this to the Chirithy staff, are you sure you would like to use these responses?"
                        + "\r\nTo edit a response, reply with the number associated to a question you would like to edit."
                        + "\r\nIf you are content with your responses, type `send` to submit this application.", embeder.build());
            }
        }
    }

    private EmbedBuilder createEmbed() {
        return new EmbedBuilder().withColor(26, 188, 156);
    }
}
