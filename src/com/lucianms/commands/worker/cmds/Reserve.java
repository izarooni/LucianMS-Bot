package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.utils.Database;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Reserve extends BaseCommand {

    @Override
    public String getDescription() {
        return "Reserve an in-game username for a specified account";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String account = args[0].toString();
            String username = args[1].toString();
            Connection con = Database.getConnection();
            try {
                try (PreparedStatement ps = con.prepareStatement("select count(*) as total from characters where name = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("total") != 0) {
                                createResponse(event)
                                        .appendContent("The username ")
                                        .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                                        .appendContent(" is already being used by an existing player").build();
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
                                createResponse(event)
                                        .appendContent("No such account with the username ")
                                        .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                                        .appendContent(" but I will reserve it anyways").build();
                            }
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("select * from ign_reserves where reserve = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            createResponse(event)
                                    .appendContent("The username ")
                                    .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                                    .appendContent(" is already reserved by the user ")
                                    .appendContent(rs.getString("username"), MessageBuilder.Styles.INLINE_CODE).build();
                            return;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("An error occurred while trying to find that account");
                return;
            }
            try {
                try (PreparedStatement ps = con.prepareStatement("insert into ign_reserves values (?, ?)")) {
                    ps.setString(1, account);
                    ps.setString(2, username);
                    ps.executeUpdate();
                    createResponse(event)
                            .appendContent("IGN ")
                            .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                            .appendContent(" reserved for the user ")
                            .appendContent(account, MessageBuilder.Styles.INLINE_CODE).build();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("An error occurred.");
            }
        } else if (args.length == 1) {
            Connection con = Database.getConnection();
            String username = args[0].toString();
            try (PreparedStatement ps = con.prepareStatement("select * from ign_reserves where username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getString("reserve")).append("\r\n");
                    }
                    if (sb.length() == 0) {
                        event.getChannel().sendMessage("There is no igns reserved to the username");
                    } else {
                        createResponse(event)
                                .appendContent("The username(s) reserved by ")
                                .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                                .appendContent(" ")
                                .appendContent(sb.toString(), MessageBuilder.Styles.CODE).build();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String trigger = Discord.getConfig().getString("CommandTrigger");
            String syntax = String.format("%sreserve <username> <ign_to_reserve>", trigger);
            createResponse(event)
                    .appendContent("Proper syntax goes like this buddy")
                    .appendCode(syntax, "").build();
        }
    }
}
