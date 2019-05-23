package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.util.List;

/**
 * <p>
 * !permission give/revoke ID permission
 * </p>
 *
 * @author izarooni
 */
public class CmdPermission extends BaseCommand {

    public CmdPermission(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Add or remove permissions for a specified Discord user";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().get(event.getChannel().getGuild().getLongID());

        Command.CommandArg[] args = command.args;
        if (args.length >= 3) {
            String action = args[0].toString();
            Long ID = args[1].parseUnsignedNumber();
            String permission = args[2].toString();
            if (ID == null) {
                List<IUser> mentions = event.getMessage().getMentions();
                if (!mentions.isEmpty()) {
                    ID = mentions.get(0).getLongID();
                } else {
                    createResponse(event).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid ID").build();
                    return;
                }
            }
            if (guild.getGuild().getRoleByID(ID) != null) {
                if (action.equalsIgnoreCase("give") || action.equalsIgnoreCase("add")) {
                    if (permission.equals("*")) {
                        for (CommandUtil cperms : CommandUtil.values()) {
                            guild.getPermissions().give(ID, cperms.name().toLowerCase());
                        }
                    } else {
                        for (int i = 2; i < args.length; i++) {
                            guild.getPermissions().give(ID, args[i].toString());
                        }
                    }
                    guild.getPermissions().save();
                    createResponse(event).appendContent("Success!").build();
                } else if (action.equalsIgnoreCase("revoke") || action.equalsIgnoreCase("remove")) {
                    if (permission.equals("*")) {
                        for (CommandUtil cperms : CommandUtil.values()) {
                            guild.getPermissions().revoke(ID, cperms.name().toLowerCase());
                        }
                    } else {
                        for (int i = 2; i < args.length; i++) {
                            guild.getPermissions().revoke(ID, args[i].toString());
                        }
                    }
                    guild.getPermissions().save();
                    createResponse(event).appendContent("Success!").build();
                }
            } else if (guild.getGuild().getUserByID(ID) != null) {
                IUser iu = guild.getGuild().getUserByID(ID);
                User target = guild.addUserIfAbsent(iu);
                if (action.equalsIgnoreCase("give") || action.equalsIgnoreCase("add")) {
                    if (permission.equals("*")) {
                        for (CommandUtil cperms : CommandUtil.values()) {
                            target.getPermissions().give(guild.getId(), cperms.name().toLowerCase());
                        }
                    } else {
                        for (int i = 2; i < args.length; i++) {
                            target.getPermissions().give(guild.getGuild().getLongID(), args[i].toString());
                        }
                    }
                    target.getPermissions().save();
                    createResponse(event).appendContent("Success!").build();
                } else if (action.equalsIgnoreCase("revoke") || action.equalsIgnoreCase("remove")) {
                    if (permission.equals("*")) {
                        for (CommandUtil cperms : CommandUtil.values()) {
                            target.getPermissions().revoke(guild.getId(), cperms.name().toLowerCase());
                        }
                    } else {
                        for (int i = 2; i < args.length; i++) {
                            target.getPermissions().revoke(guild.getGuild().getLongID(), args[i].toString());
                        }
                    }
                    target.getPermissions().save();
                    createResponse(event).appendContent("Success!").build();
                }
            } else {
                createResponse(event).appendContent("Could not find a ROLE or USER with the specified ID").build();
            }
        } else {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: ").appendDesc(getName()).appendDesc(" <add/remove> <role/user ID> <permission/*>");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
