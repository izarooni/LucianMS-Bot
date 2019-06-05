package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.scheduler.tasks.DelayedMessageDelete;
import com.lucianms.server.user.User;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Optional;
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
        IChannel ch = event.getChannel();
        boolean canDeleteMessages = ch.getModifiedPermissions(Discord.getBot().getClient().getOurUser()).contains(Permissions.MANAGE_MESSAGES);
        EmbedBuilder embed = user.getEmbedBuilder();

        if (embed == null) {
            if (command.args.length > 1) {
                // quick embed
                embed = createEmbed();
                String message = command.concatFrom(0, " ");
                int spIdx = message.indexOf('|');
                if (spIdx >= 0) {
                    String title = message.substring(0, spIdx);
                    String content = message.substring(spIdx + 1);
                    embed.withTitle(title).withDescription(content);
                } else {
                    embed.withDescription(message);
                }
                createResponse(event).withEmbed(embed.build()).build();
            } else if (command.args.length == 1) {
                Long ID = command.args[0].parseUnsignedNumber();
                if (ID == null) {
                    createResponse(event).withContent(String.format("Are you sure '%s' is a number?", command.args[0].toString())).build();
                    return;
                }
                IMessage message = ch.fetchMessage(ID);
                if (message != null) {
                    Optional<IEmbed> first = message.getEmbeds().stream().findFirst();
                    if (first.isPresent()) {
                        IEmbed iEmbed = first.get();
                        embed = createEmbed(iEmbed);
                        user.setEmbedBuilder(embed);
                        message.delete();
                        createResponse(event).withContent("You are now in manual edit mode for the message").build();
                    } else {
                        createResponse(event).withContent("I could not find embedded content from that message").build();
                    }
                } else {
                    createResponse(event).withContent("I could not find a message with that ID").build();
                }
            } else {
                // manual customization
                user.setEmbedBuilder(createEmbed());
                String content = "You are now in manual embed creation mode. Use `%s` command for options.";
                if (!canDeleteMessages) {
                    content += "\r\nFYI, I do not have permission to delete messages in this channel.";
                }
                createResponse(event).withContent(String.format(content, getName())).build();
            }
            return;
        }
        if (command.getArgs().length < 2) {
            if (command.getArgs().length == 1) {
                String arg = command.getArgs()[0].toString();
                if (arg.equalsIgnoreCase("cancel")) {
                    user.setEmbedBuilder(null);
                    createResponse(event).withContent("No longer configuring an embedded message.").build();
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
                                message = createResponse(event).withContent("That is not a number.").build();
                                break;
                            }
                        } else {
                            message = createResponse(event).withContent("Either specify a basic color name or provide a hexadecimal color.").build();
                            break;
                        }
                    }
                }
                message = createResponse(event).withContent("Updated color").withEmbed(embed.build()).build();
                break;
            case "title":
                updateOrReset(content, embed::withTitle);
                message = createResponse(event).withContent("Updated title").withEmbed(embed.build()).build();
                break;
            case "footer":
                updateOrReset(content, embed::withFooterText);
                message = createResponse(event).withContent("Updated footer").withEmbed(embed.build()).build();
                break;
            case "desc":
            case "description":
                if (content.isEmpty()) content = command.concatFrom(0, " ").substring(arg.length());
                updateOrReset(content, embed::withDescription);
                message = createResponse(event).withContent("Updated description").withEmbed(embed.build()).build();
                break;
        }
        if (canDeleteMessages && message != null) {
            TaskExecutor.executeLater(new DelayedMessageDelete(message, event.getMessage()), 8000);
        }
    }

    private void updateOrReset(String content, Consumer<String> set) {
        if (content.equalsIgnoreCase("--reset")) set.accept("");
        else set.accept(content);
    }
}
