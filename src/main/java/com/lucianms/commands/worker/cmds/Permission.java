package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CPermissions;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;

/**
 * <p>
 * !permission give/revoke ID permission
 * </p>
 *
 * @author izarooni
 */
public class Permission extends BaseCommand {

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
            Long ID = args[1].parseNumber();
            String permission = args[2].toString();
            if (ID == null) {
                createResponse(event).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid ID").build();
                return;
            }
            if (guild.getGuild().getRoleByID(ID) != null) {
                if (action.equalsIgnoreCase("give") || action.equalsIgnoreCase("add")) {
                    if (permission.equals("*")) {
                        for (CPermissions cperms : CPermissions.values()) {
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
                        for (CPermissions cperms : CPermissions.values()) {
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
                        for (CPermissions cperms : CPermissions.values()) {
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
                        for (CPermissions cperms : CPermissions.values()) {
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
        }
    }
}
