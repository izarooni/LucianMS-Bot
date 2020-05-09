package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import com.lucianms.server.user.DUser;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;
        DGuild guild = Discord.getGuild(event.getGuild());
        DUser user = message.getAuthor().map(u -> guild.getUser(u.getId().asString())).orElse(null);
        if (user == null) return;
        int accountID = user.getBoundAccountID();

        String voteURL = guild.getGuildConfig().getVoteURL();
        if (voteURL.isEmpty()) {
            ch.createEmbed(e -> e.setDescription("The voting link has not been set yet.")).block();
            return;
        }

        if (accountID == 0) {
            try (Connection con = Discord.getMapleConnection()) {
                try (PreparedStatement ps = con.prepareStatement("select id from accounts where discord_id = ?")) {
                    ps.setString(1, user.getUser().getId().asString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            accountID = rs.getInt("id");
                            user.setBoundAccountID(accountID);
                        }
                    }
                }
            } catch (SQLException ex) {
                LOGGER.error("Failed to get bound discord account", ex);
                ch.createEmbed(e -> e.setDescription("Failed to retrieve the voting URL")).block();
                return;
            }
        }

        if (accountID > 0) {
            String URL = voteURL + "&pingUsername=" + accountID;
            ch.createEmbed(e -> e.setDescription(URL +
                    "\r\n\r\nYou will receive 1 vote point for voting."
                    + "\r\nPlease complete the captcha and submit your vote")).block();
        } else {
            ch.createEmbed(e -> e.setDescription(voteURL
                    + "\r\n\r\nYou will not receive any rewards for voting with this link."
                    + "\r\nPlease complete the captcha and submit your vote"
                    + "\r\nIf would like rewards, please bind your game account to your discord account")).block();
        }
    }
}
