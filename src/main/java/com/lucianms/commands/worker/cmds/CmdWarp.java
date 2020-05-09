package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
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
public class CmdWarp extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCommand.class);

    public CmdWarp(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Warp a specified offline character";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_mapId = args[1].parseUnsignedNumber();
            if (var_mapId == null) {
                ch.createEmbed(e -> e.setDescription("`" + args[1].toString() + "` is not a valid number.")).block();
                return;
            }
            int mapId = var_mapId.intValue();
            if (mapId < 0) {
                ch.createEmbed(e -> e.setDescription("`" + args[1].toString() + "` is not a map id.")).block();
                return;
            }
            try (Connection con = Discord.getMapleConnection()) {
                try (PreparedStatement query = con.prepareStatement("select count(*) as total from characters where name = ?")) {
                    query.setString(1, username);
                    try (ResultSet rs = query.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("total") == 1) {
                                try (PreparedStatement update = con.prepareStatement("update characters set map = ? where name = ?")) {
                                    update.setInt(1, mapId);
                                    update.setString(2, username);
                                    update.executeUpdate();
                                    ch.createEmbed(e -> e.setDescription("Warped `" + username + "` as an offline character")).block();
                                }
                            } else {
                                ch.createEmbed(e -> e.setDescription("Could not find any player named `" + username + "`")).block();
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                LOGGER.error("Failed to offline warp", ex);
                ch.createEmbed(e -> e.setDescription("Oops! Something wrong happened...")).block();
            }
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <ign> <map ID>`");
            }).block();
        }
    }
}
