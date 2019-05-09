package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.scheduler.tasks.DelayedMessageDelete;
import com.lucianms.server.user.User;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.function.Consumer;

/**
 * @author izarooni
 */
public class Embed extends BaseCommand {

    public Embed() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Create your owm embedded message";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        User user = Discord.getGuilds().get(event.getGuild().getLongID()).getUser(event.getAuthor().getLongID());
        EmbedBuilder embed = user.getEmbedBuilder();
        IMessage userMessage = event.getMessage();
        boolean canDeleteMessages = event.getChannel().getModifiedPermissions(Discord.getBot().getClient().getOurUser()).contains(Permissions.MANAGE_MESSAGES);

        if (embed == null) {
            user.setEmbedBuilder(createEmbed());
            String content = "Embed created and can now be configured. Re-use command for options.";
            if (!canDeleteMessages) {
                content += "\r\nFYI, I do not have permission to delete messages in this channel.";
            }
            userMessage.reply(content);
            return;
        }
        if (command.getArgs().length < 2) {
            if (command.getArgs().length == 1) {
                String arg = command.getArgs()[0].toString();
                if (arg.equalsIgnoreCase("cancel")) {
                    user.setEmbedBuilder(null);
                    userMessage.reply("No longer configuring an embedded message.");
                } else if (arg.equalsIgnoreCase("send")) {
                    createResponse(event).withEmbed(embed.build()).build();
                }
            } else {
                embed = createEmbed()
                        .withTitle("How to use the command")
                        .appendField("description", getDescription(), false)
                        .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <action/cancel> [arguments]`")
                        .appendDesc("\r\n\r\n`<action>` may be any of the following:")
                        .appendDesc("\r\ncolor, title, footer and description")
                        .appendDesc("\r\n\r\nUse `").appendDesc(getName()).appendDesc(" send` to create a message without being automatically deleted.");
                createResponse(event).withEmbed(embed.build()).build();
            }
            return;
        }

        IMessage message = null;
        String arg = command.getArgs()[0].toString();
        String content = command.concatFrom(1, " ");
        switch (arg) {
            case "color":
                if (content.equalsIgnoreCase("--reset")) {
                    embed.withColor(26, 188, 156);
                } else {
                    try {
                        Color shit = (Color) Color.class.getField(content).get(null);
                        embed.withColor(shit);
                    } catch (Exception ignore) {
                        int r, g, b;
                        try {
                            String[] sp = content.split(" ");
                            r = Integer.parseInt(sp[0]);
                            g = Integer.parseInt(sp[1]);
                            b = Integer.parseInt(sp[2]);
                            embed.withColor(r, g, b);
                        } catch (NumberFormatException alsoIgnore) {
                            message = userMessage.reply("Either specify a basic color name or provide 3 numbers in the order of R, G and B");
                            break;
                        }
                    }
                }
                message = userMessage.reply("Updated color", embed.build());
                break;
            case "title":
                updateOrReset(content, embed::withTitle);
                message = userMessage.reply("Updated title", embed.build());
                break;
            case "footer":
                updateOrReset(content, embed::withDescription);
                message = userMessage.reply("Updated footer", embed.build());
                break;
            case "description":
                updateOrReset(content, embed::withTitle);
                message = userMessage.reply("Updated description", embed.build());
                break;
        }
        if (canDeleteMessages && message != null) {
            TaskExecutor.executeLater(new DelayedMessageDelete(message, userMessage), 3000);
        }
    }

    private void updateOrReset(String content, Consumer<String> set) {
        if (content.equalsIgnoreCase("--reset")) set.accept("");
        else set.accept(content);
    }
}
