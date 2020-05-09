package com.lucianms.event;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.CommandExecutor;
import com.lucianms.commands.worker.cmds.CmdApply;
import com.lucianms.server.DGuild;
import com.lucianms.server.GuildTicket;
import com.lucianms.server.user.DUser;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class MessageChannelEvents {

    public static void execute(MessageCreateEvent event) {
        Message message = event.getMessage();
        Optional<User> author = message.getAuthor();
        if (!author.isPresent() || !message.getContent().isPresent()) return;

        Mono<GuildChannel> gchm = message.getChannel().ofType(GuildChannel.class);
        if (gchm.blockOptional().isPresent()) {
            DGuild guild = Discord.getGuild(event.getGuild());
            guild.addUserIfAbsent(author.get());
            boolean hasPermission = gchm.flatMap(c -> c.getEffectivePermissions(author.map(User::getId).get()))
                    .map(set -> set.containsAll(PermissionSet.of(Permission.MANAGE_MESSAGES))).blockOptional().orElse(false);
            if (!hasPermission) {
                for (String words : guild.getGuildConfig().getWordBlackList()) {
                    Pattern pattern = Pattern.compile(words, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(message.getContent().get());
                    if (matcher.find()) {
                        message.delete();
                        return;
                    }
                }
            }
        } else {
            DUser user = Discord.getUserHandles().get(author.map(User::getId).get().asString());
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

    public static void execute(ReactionAddEvent event) {
        DGuild guild = Discord.getGuild(event.getGuild());
        GuildTicket ticket = guild.getTickets().get(event.getMessageId().asString());
        if (ticket != null && !ticket.isCompleted()) {
            ticket.setCompleted(true);
            Optional<TextChannel> ch = guild.getGuild()
                    .getChannelById(Snowflake.of(guild.getGuildConfig().getCIDTicketCreation()))
                    .ofType(TextChannel.class).blockOptional();
            ch.ifPresent(t -> t.createEmbed(c -> {
                c.setTitle(String.format("Ticket #%04d", ticket.getTicketID()));
                c.setDescription(":white_check_mark: Your ticket has been marked as complete!");
                c.setFooter("<@" + ticket.getUserID() + ">", null);
            }).block());
        }
    }

    private MessageChannelEvents() {
    }

    private static void onApplicationResponse(DUser user, MessageCreateEvent event) {
        Message message = event.getMessage();
        Mono<PrivateChannel> pchm = message.getChannel().ofType(PrivateChannel.class);
        if (!pchm.blockOptional().isPresent()) return;
        String content = message.getContent().orElse("");

        if (content.equalsIgnoreCase("cancel")) {
            pchm.block().createMessage("You have cancelled the application.").block();
            user.setApplicationResponses(null);
            return;
        }
        // ask a question assigned to the current status
        if (user.getApplicationStatus() < CmdApply.Questions.length) {
            user.getApplicationResponses()[user.getApplicationStatus()] = content;
            user.setApplicationStatus(CmdApply.Questions.length);
        }

        if (content.equalsIgnoreCase("send")) {
            pchm.block().createMessage("Submitting your application! Good luck~").block();
            DGuild guild = Discord.getGuild(Discord.getBot().getClient().getGuildById(Snowflake.of(user.getApplicationGuildID())));
            Mono<Channel> chm = Discord.getBot().getClient().getChannelById(Snowflake.of(guild.getGuildConfig().getCIDApplicationDestination()));
            chm.ofType(TextChannel.class).block().createEmbed(e -> {
                User u = user.getUser();
                e.setTitle("GM Application");
                for (int i = 0; i < CmdApply.Questions.length; i++) {
                    e.addField(String.format("%d ). %s", (i + 1), CmdApply.Questions[i]), user.getApplicationResponses()[i], false);
                }
                e.setFooter(String.format("%s#%s / %s", u.getUsername(), u.getDiscriminator(), u.getId().asString()), null);
            }).block();

            user.setApplicationGuildID("");
            user.setApplicationStatus(0);
            user.setApplicationResponses(null);
            Discord.getUserHandles().remove(user.getUser().getId().asString());
        } else {
            try {
                // ask the specified question for a new response
                int responseID = Integer.parseInt(content);
                user.setApplicationStatus(responseID - 1);
                pchm.block().createMessage(CmdApply.Questions[responseID - 1]).block();
            } catch (NumberFormatException ignore) {
                pchm.block().createMessage(m -> {
                    m.setContent("Before I send this to the Chirithy staff, are you sure you would like to use these responses?"
                            + "\r\nTo edit a response, reply with the number associated to a question you would like to edit."
                            + "\r\nIf you are content with your responses, type `send` to submit this application.");
                    m.setEmbed(e -> {
                        for (int i = 0; i < CmdApply.Questions.length; i++) {
                            e.addField(String.format("%d ). %s", (i + 1), CmdApply.Questions[i]), user.getApplicationResponses()[i], false);
                        }
                    });
                }).block();
            }
        }
    }
}
