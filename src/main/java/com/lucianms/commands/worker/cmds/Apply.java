package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;

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
            "What could you do to help benefit Chirithy?"
    };

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
        user.setApplicationResponses(new String[Questions.length]);
        user.setApplicationStatus(0);

        IPrivateChannel dm = user.getUser().getOrCreatePMChannel();
        dm.sendMessage(
                "For this GM application, I will ask you a few questions here and have you answer them."
                        + "\r\nThe messages you send will be forwarded to the Chirithy staff where it will be reviewed and discussed."
                        + "\r\nYou may at any time cancel this application by sending the message `cancel`");
        dm.sendMessage(Questions[user.getApplicationStatus()]);
        Discord.getUserHandles().put(user.getUser().getLongID(), user);
    }
}
