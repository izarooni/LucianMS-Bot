package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.sql.*;

/**
 * @author izarooni
 */
public class Updates extends BaseCommand {

    public Updates() {
        super(true);
    }

    @Override
    public String getDescription() {
        return "Create a news update for the website";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        String message = command.concatFrom(0, " ");
        String[] split = message.split(" \\| ");
        if (split.length != 2) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: ").appendDesc(getName()).appendDesc(" title | content")
                    .appendDesc("\r\nFor example: `").appendDesc(getName()).appendDesc(" title for your post | the content for your post`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        String title = split[0];
        String content = split[1];
        try (Connection con = Discord.getMapleConnection()) {
            try (PreparedStatement ps = con.prepareStatement("insert into _web_posts values (default, ?, 'updates', ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, title);
                ps.setString(2, content);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        EmbedBuilder embed = createEmbed()
                                .appendDesc("News post successfully posted.")
                                .appendDesc("\r\nhttp://maplechirithy.com/bulletin/" + rs.getInt(1));
                        createResponse(event).withEmbed(embed.build()).build();
                    }
                }
            }
        } catch (SQLException e) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("Failed to create a news post")
                    .withDescription("```\r\n" + e.toString() + "\r\n```");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
