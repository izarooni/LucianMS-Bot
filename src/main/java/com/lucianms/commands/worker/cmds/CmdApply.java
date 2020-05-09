package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import com.lucianms.server.user.DUser;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

/**
 * @author izarooni
 */
public class CmdApply extends BaseCommand {

    public static final String[] Questions = new String[]{
            "What is your name?",
            "What is your username?",
            "What is your age?",
            "How long have you been playing Chirithy?",
            "Tell me a little bit about yourself",
            "What could you do to help benefit Chirithy?"};

    public CmdApply(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Submit an application for the GM position";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Mono<TextChannel> chm = message.getChannel().ofType(TextChannel.class);
        DGuild guild = Discord.getGuild(event.getGuild());
        DUser user = guild.getUser(message.getAuthor().map(User::getId).get().asString());
        String appDestID = guild.getGuildConfig().getCIDApplicationDestination();

        if (appDestID.isEmpty()) {
            chm.blockOptional().ifPresent(c -> c.createMessage("The GM application system has not been setup yet.").block());
            return;
        }
        user.setApplicationGuildID(guild.getId().asString());
        String[] responses = new String[Questions.length];
        for (int i = 0; i < Questions.length; i++) {
            responses[i] = "_No Response_";
        }
        user.setApplicationResponses(responses);
        user.setApplicationStatus(0);

        chm.blockOptional().ifPresent(c -> c.createMessage(m -> {
            m.setContent("Please answer the following questions."
                    + "\r\nThe messages you send will be forwarded to the Chirithy staff where it will be reviewed and discussed."
                    + "\r\n\r\nWhen you are complete, say the word `send` then your application will be submitted."
                    + "\r\nYou may cancel at any time by saying `cancel`."
                    + "\r\nTo select a question, simply say the number assigned to the question then answer accordingly.");
            m.setEmbed(e -> {
                for (int i = 0; i < CmdApply.Questions.length; i++) {
                    e.addField(String.format("%d ). %s", (i + 1), CmdApply.Questions[i]), user.getApplicationResponses()[i], false);
                }
            });
        }).block());
        Discord.getUserHandles().put(user.getId().asString(), user);
    }
}
