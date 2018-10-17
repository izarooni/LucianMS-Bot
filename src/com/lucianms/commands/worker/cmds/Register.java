package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.utils.Database;
import com.lucianms.utils.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Register extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Register.class);

    public Register() {
        super(false, CommandType.PrivateMessage);
    }

    @Override
    public String getDescription() {
        return "Register for an account by telling me your username and password!";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        IChannel channel = event.getChannel();
        Command.CommandArg[] args = command.getArgs();

        if (args.length == 2) {
            long userID = event.getAuthor().getLongID();
            String username = args[0].toString();
            String password = args[1].toString();
            if (!username.matches(Pattern.compile("[a-zA-Z0-9]{4,}").pattern())) {
                channel.sendMessage("Please make sure your username contains only alpha-numeric characters");
                return;
            }
            if (username.length() >= 4 && username.length() <= 13) {
                if (password.length() >= 6) {
                    Connection connection = Database.getConnection();
                    try {
                        try (PreparedStatement ps = connection.prepareStatement("select name from accounts where discord_id = ?")) {
                            ps.setLong(1, userID);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    EmbedBuilder embed = createEmbed()
                                            .withTitle("You have already registered!")
                                            .appendDesc("It seems that you have already registered an account. That means you can play! So, what are you waiting for?")
                                            .appendField("username", rs.getString("name"), false);
                                    createResponse(event).withEmbed(embed.build()).build();
                                    return;
                                }
                            }
                        }
                        try (PreparedStatement ps = connection.prepareStatement("select count(*) as total from accounts where name = ?")) {
                            ps.setString(1, username);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next() && rs.getInt("total") != 0) {
                                    EmbedBuilder embed = createEmbed()
                                            .withTitle("Too slow!")
                                            .appendDesc("It seems that name is already being used. Please try another one!");
                                    createResponse(event).withEmbed(embed.build()).build();
                                    return;
                                }
                            }
                        }
                        try (PreparedStatement ps = connection.prepareStatement("insert into accounts (name, password, discord_id) values (?, ?, ?)")) {
                            ps.setString(1, username);
                            ps.setString(2, getHash(password));
                            ps.setLong(3, userID);
                            ps.executeUpdate();
                            EmbedBuilder embed = createEmbed()
                                    .withTitle("Success!")
                                    .appendDesc("I made your account!! You may now login our game and create your character!")
                                    .appendDesc("\r\n**username**:").appendDesc(username);
                            createResponse(event).withEmbed(embed.build()).build();
                            LOGGER.info("Created account '{}'", username);
                        }
                    } catch (SQLException e) {
                        channel.sendMessage("Wait what? An error occurred. Please the let the developer know ASAP!");
                        e.printStackTrace();
                    }
                } else {
                    channel.sendMessage("That password is too short!");
                }
            } else {
                channel.sendMessage("That username is either too short or too long");
            }
        } else {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <username> <password>`");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }

    private static String getHash(String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            digester.update(password.getBytes(StandardCharsets.UTF_8), 0, password.length());
            return HexUtil.toString(digester.digest()).replace(" ", "").toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
    }
}
