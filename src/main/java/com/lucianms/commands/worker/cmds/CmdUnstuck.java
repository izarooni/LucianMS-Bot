package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CmdUnstuck extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdUnstuck.class);

    public CmdUnstuck(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Warps your character to the home map";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        Optional<User> author = message.getAuthor();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;
        Command.CommandArg[] args = command.getArgs();
        Snowflake authorID = author.map(User::getId).get();

        if (args.length == 1) {
            try (Connection con = Discord.getMapleConnection()) {
                String username = args[0].toString();
                int accountID;
                try (PreparedStatement ps = con.prepareStatement("select id from accounts where discord_id = ?")) {
                    ps.setString(1, authorID.asString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            ch.createMessage("<@" + authorID.asString() + ">, you have already connected your Discord account.").block();
                            return;
                        } else {
                            accountID = rs.getInt("id");
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("update characters set map = 910000000 where accountid = ? and name = ?")) {
                    ps.setInt(1, accountID);
                    ps.setString(2, username);
                    int updates = ps.executeUpdate();
                    if (updates == 1) {
                        StringBuilder sb = new StringBuilder();
                        if (ch.getType() == Channel.Type.GUILD_TEXT) {
                            sb.append("<@").append(authorID.asString()).append(">, ");
                        }
                        sb.append(" Successfully moved ").append("`").append(username).append("`").append(" to the home map.");
                        ch.createMessage(sb.toString()).block();
                        sb.setLength(0);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        if (ch.getType() == Channel.Type.GUILD_TEXT) {
                            sb.append("<@").append(authorID.asString()).append(">, ");
                        }
                        sb.append("Unable to find any character named ")
                                .append("`").append(username).append("`")
                                .append(" on your account.");
                        ch.createMessage(sb.toString()).block();
                        sb.setLength(0);

                    }
                }
            } catch (SQLException ex) {
                LOGGER.error("Failed to unstuck", ex);
                ch.createEmbed(e -> e.setDescription("Oops! Something went wrong...")).block();
            }
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <username>`"
                        + "\r\n**username** is the IGN of a character on your account!");
            }).block();
        }
    }
}
