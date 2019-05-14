package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
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
public class CmdEmbed extends BaseCommand {

    public CmdEmbed(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Create your own embedded message";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        User user = Discord.getGuilds().get(event.getGuild().getLongID()).getUser(event.getAuthor().getLongID());
        EmbedBuilder embed = user.getEmbedBuilder();
        IMessage userMessage = event.getMessage();
        boolean canDeleteMessages = event.getChannel().getModifiedPermissions(Discord.getBot().getClient().getOurUser()).contains(Permissions.MANAGE_MESSAGES);

        if (embed == null) {
            user.setEmbedBuilder(createEmbed());
            String content = "Embed initialized and is now configurable. Use `%s` command for options.";
            if (!canDeleteMessages) {
                content += "\r\nFYI, I do not have permission to delete messages in this channel.";
            }
            userMessage.reply(String.format(content, getName()));
            return;
        }
        if (command.getArgs().length < 2) {
            if (command.getArgs().length == 1) {
                String arg = command.getArgs()[0].toString();
                if (arg.equalsIgnoreCase("cancel")) {
                    user.setEmbedBuilder(null);
                    userMessage.reply("No longer configuring an embedded message.");
                } else if (arg.equalsIgnoreCase("send")) {
                    event.getMessage().delete();
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
        manageEmbed(event, user, command);
    }

    private void manageEmbed(MessageReceivedEvent event, User user, Command command) {
        IMessage userMessage = event.getMessage();
        boolean canDeleteMessages = event.getChannel().getModifiedPermissions(Discord.getBot().getClient().getOurUser()).contains(Permissions.MANAGE_MESSAGES);
        EmbedBuilder embed = user.getEmbedBuilder();

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
                        if (content.startsWith("#")) {
                            try {
                                embed.withColor(Integer.parseInt(content.substring(1), 16));
                            } catch (NumberFormatException e) {
                                message = userMessage.reply("That is not a number.");
                                break;
                            }
                        } else {
                            message = userMessage.reply("Either specify a basic color name or provide a hexadecimal color.");
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
                updateOrReset(content, embed::withFooterText);
                message = userMessage.reply("Updated footer", embed.build());
                break;
            case "desc":
            case "description":
                if (content.isEmpty()) content = command.concatFrom(0, " ").substring(arg.length());
                updateOrReset(content, embed::withDescription);
                message = userMessage.reply("Updated description", embed.build());
                break;
        }
        if (canDeleteMessages && message != null) {
            TaskExecutor.executeLater(new DelayedMessageDelete(message, userMessage), 5000);
        }
    }

    private void updateOrReset(String content, Consumer<String> set) {
        if (content.equalsIgnoreCase("--reset")) set.accept("");
        else set.accept(content);
    }
}
