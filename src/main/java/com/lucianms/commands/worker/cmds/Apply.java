package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author izarooni
 */
public class Apply extends BaseCommand {

    public static final String[] Questions = new String[]{
            "What is your name?",
            "What is your username?",
            "What is your age?",
            "How long have you been playing Chirithy?",
            "Tell me a little bit about yourself",
            "What could you do to help benefit Chirithy?"};

    public Apply() {
        super(false);
    }

    @Override
    public String getDescription() {
        return "Submit an application for the GM position";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().get(event.getGuild().getLongID());
        User user = guild.getUser(event.getAuthor().getLongID());
        String appDestinationCID = guild.getGuildConfig().getCIDApplicationDestination();
        IMessage eventMessage = event.getMessage();

        if (appDestinationCID.isEmpty()) {
            eventMessage.reply("The GM application system has not been configured yet.");
            return;
        }
        user.setApplicationGuildID(event.getGuild().getLongID());
        String[] responses = new String[Questions.length];
        for (int i = 0; i < Questions.length; i++) {
            responses[i] = "_No Response_";
        }
        user.setApplicationResponses(responses);
        user.setApplicationStatus(0);

        EmbedBuilder embeder = createEmbed();
        for (int i = 0; i < Apply.Questions.length; i++) {
            embeder.appendField(String.format("%d ). %s", (i + 1), Apply.Questions[i]), user.getApplicationResponses()[i], false);
        }

        IPrivateChannel dm = user.getUser().getOrCreatePMChannel();
        dm.sendMessage(
                "Please answer the following questions."
                        + "\r\nThe messages you send will be forwarded to the Chirithy staff where it will be reviewed and discussed."
                        + "\r\n\r\nWhen you are complete, say the word `send` then your application will be submitted."
                        + "\r\nYou may cancel at any time by saying `cancel`."
                        + "\r\nTo select a question, simply say the number assigned to the question then answer accordingly.", embeder.build());
        Discord.getUserHandles().put(user.getUser().getLongID(), user);
    }
}
