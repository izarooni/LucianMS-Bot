package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.Guild;
import com.lucianms.server.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author izarooni
 */
public class CmdVote extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdVote.class);

    public CmdVote(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Get the server vote link";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().get(event.getGuild().getLongID());
        User user = guild.getUser(event.getAuthor().getLongID());
        int accountID = user.getBoundAccountID();

        String voteURL = guild.getGuildConfig().getVoteURL();
        if (voteURL.isEmpty()) {
            EmbedBuilder embed = createEmbed()
                    .withDescription("The voting link has not been set yet.");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }

        if (accountID == 0) {
            try (Connection con = Discord.getMapleConnection()) {
                try (PreparedStatement ps = con.prepareStatement("select id from accounts where discord_id = ?")) {
                    ps.setLong(1, user.getUser().getLongID());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            accountID = rs.getInt("id");
                            user.setBoundAccountID(accountID);
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to get bound discord account", e);
                event.getMessage().reply("I was unable to get the vote link");
                return;
            }
        }

        if (accountID > 0) {
            String URL = voteURL + "&pingUsername=" + accountID;
            EmbedBuilder embed = createEmbed()
                    .appendDesc(URL)
                    .appendDesc("\r\n\r\nYou will receive 1 vote point for voting.")
                    .appendDesc("\r\nPlease complete the captcha and submit your vote");
            createResponse(event).withEmbed(embed.build()).build();
        } else {
            EmbedBuilder embed = createEmbed()
                    .appendDesc(voteURL)
                    .appendDesc("\r\n\r\nYou will not receive any rewards for voting with this link.")
                    .appendDesc("\r\nPlease complete the captcha and submit your vote")
                    .appendDesc("\r\nIf would like rewards, please bind your game account to your discord account");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }
}
