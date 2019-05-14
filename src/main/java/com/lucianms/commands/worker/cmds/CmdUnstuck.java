package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CmdUnstuck extends BaseCommand {

    public CmdUnstuck(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Warps your character to the home map";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        IUser author = event.getAuthor();
        long userID = author.getLongID();
        Command.CommandArg[] args = command.getArgs();

        if (args.length == 1) {
            try (Connection con = Discord.getMapleConnection()) {
                String username = args[0].toString();
                int accountID;
                try (PreparedStatement ps = con.prepareStatement("select id from accounts where discord_id = ?")) {
                    ps.setLong(1, userID);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            event.getChannel().sendMessage(author.mention() + " Have you binded your Discord account to your in-game account?");
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
                        MessageBuilder response = createResponse(event);
                        if (!event.getChannel().isPrivate()) {
                            response.appendContent(author.mention());
                        }
                        response.appendContent(" I have moved your character ")
                                .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                                .appendContent(" to the home map").build();
                    } else {
                        MessageBuilder response = createResponse(event);
                        if (!event.getChannel().isPrivate()) {
                            response.appendContent(author.mention());
                        }
                        response.appendContent(" I couldn't find any character named ")
                                .appendContent(username, MessageBuilder.Styles.INLINE_CODE)
                                .appendContent(" on your account").build();
                    }
                }
            } catch (SQLException e) {
                event.getChannel().sendMessage("Oops! Something went wrong...");
                e.printStackTrace();
            }
        } else {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: ").appendDesc(getName()).appendDesc(" <username>")
                    .appendDesc("\r\n**username** is the IGN of a character on your account!");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
