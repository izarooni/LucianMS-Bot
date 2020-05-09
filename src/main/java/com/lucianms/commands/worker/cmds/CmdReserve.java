package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CmdReserve extends BaseCommand {

    public CmdReserve(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Reserve an in-game username for a specified account";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String account = args[0].toString();
            String username = args[1].toString();
            try (Connection con = Discord.getMapleConnection()) {
                try (PreparedStatement ps = con.prepareStatement("select count(*) as total from characters where name = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("total") != 0) {
                                ch.createMessage("The username `" + username + "` is already being used.").block();
                                return;
                            }
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("select count(*) as total from accounts where name = ?")) {
                    ps.setString(1, account);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("total") != 1) {
                                ch.createMessage("Could not find an account named `" + username + "` but will reserve it anyways.").block();
                            }
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("select * from ign_reserves where reserve = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ch.createMessage("The username `" + username + "` is already reserved by account `" + rs.getString("username") + "`");
                            return;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ch.createMessage("An error occurred while trying to find that account.").block();
                return;
            }
            try (Connection con = Discord.getMapleConnection();
                 PreparedStatement ps = con.prepareStatement("insert into ign_reserves values (?, ?)")) {
                ps.setString(1, account);
                ps.setString(2, username);
                ps.executeUpdate();
                ch.createMessage("IGN `" + username + "` reserved for account `" + account + "`").block();
            } catch (SQLException e) {
                e.printStackTrace();
                ch.createMessage("An error ocurred while trying to reserve the IGN.").block();
            }
        } else if (args.length == 1) {
            String username = args[0].toString();
            try (Connection con = Discord.getMapleConnection();
                 PreparedStatement ps = con.prepareStatement("select * from ign_reserves where username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getString("reserve")).append("\r\n");
                    }
                    if (sb.length() == 0) {
                        ch.createMessage("There is no IGNs reserved with that username").block();
                    } else {
                        ch.createMessage("The username(s) resvered by `" + username + "` `" + sb.toString() + "`").block();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <account name> <IGN>`");
            }).block();
        }
    }
}
