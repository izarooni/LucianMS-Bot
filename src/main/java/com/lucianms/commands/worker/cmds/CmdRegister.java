package com.lucianms.commands.worker.cmds;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CmdRegister extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdRegister.class);

    public CmdRegister(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Register for an account by telling me your username and password!";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;
        Command.CommandArg[] args = command.getArgs();
        String discordID = message.getAuthor().map(User::getId).get().asString();


        if (args.length == 2) {
            String username = args[0].toString();
            String password = args[1].toString();
            if (!username.matches(Pattern.compile("[a-zA-Z0-9]{4,}").pattern())) {
                ch.createMessage("Please make sure your username contains only alpha-numeric characters").block();
                return;
            }
            if (username.length() >= 4 && username.length() <= 13) {
                if (password.length() >= 6) {
                    try (Connection connection = Discord.getMapleConnection()) {
                        try (PreparedStatement ps = connection.prepareStatement("select name from accounts where discord_id = ?")) {
                            ps.setString(1, discordID);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    String accountName = rs.getString("name");
                                    ch.createEmbed(e -> {
                                        e.setTitle("You have already registered!");
                                        e.addField("username", accountName, false);
                                        e.setDescription("It seems that you have already registered an account. That means you can play! So, what are you waiting for?");
                                    }).block();
                                    return;
                                }
                            }
                        }
                        try (PreparedStatement ps = connection.prepareStatement("select count(*) as total from accounts where name = ?")) {
                            ps.setString(1, username);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next() && rs.getInt("total") != 0) {
                                    ch.createEmbed(e -> {
                                        e.setTitle("Too slow!");
                                        e.setDescription("It seems that name is already being used. Please try another one!");
                                    }).block();
                                    return;
                                }
                            }
                        }
                        password = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.toCharArray());
                        try (PreparedStatement ps = connection.prepareStatement("insert into accounts (name, password, discord_id) values (?, ?, ?)")) {
                            ps.setString(1, username);
                            ps.setString(2, password);
                            ps.setString(3, discordID);
                            ps.executeUpdate();
                            ch.createEmbed(e -> {
                                e.setTitle("Success!");
                                e.setDescription("I made your account!! You may now login our game and create your character!"
                                        + "\r\nI've also bound your account so you don't have to worry about doing that silly !bind command"
                                        + "\r\n**username**:" + username);
                            }).block();
                            LOGGER.info("Created account '{}'", username);
                        }
                    } catch (SQLException e) {
                        LOGGER.error("Failed to register an account", e);
                        ch.createMessage("Oops! Something wrong happened!").block();
                    }
                } else {
                    ch.createMessage("The password is too short.").block();
                }
            } else {
                ch.createMessage("The username is too short or too long.").block();
            }
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <username> <password>`");
            }).block();
        }
    }
}
