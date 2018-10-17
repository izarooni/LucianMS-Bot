package com.lucianms.cmds;

import com.lucianms.utils.Database;
import com.lucianms.BaseCommand;
import com.lucianms.Discord;
import com.lucianms.commands.Command;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author izarooni
 */
public class Warp extends BaseCommand {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BaseCommand.class);

    public Warp() {
        super(true);
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 2) {
            String username = args[0].toString();
            Long var_mapId = args[1].parseNumber();
            if (var_mapId == null) {
                createResponse(event).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid number").build();
                return;
            }
            int mapId = var_mapId.intValue();
            if (mapId < 0) {
                createResponse(event).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid map id").build();
                return;
            }
            if (Database.isInitialized()) {
                try {
                    Connection con = Database.getConnection();
                    try (PreparedStatement query = con.prepareStatement("select count(*) as total from characters where name = ?")) {
                        query.setString(1, username);
                        try (ResultSet rs = query.executeQuery()) {
                            if (rs.next()) {
                                if (rs.getInt("total") == 1) {
                                    try (PreparedStatement update = Database.getConnection().prepareStatement("update characters set map = ? where name = ?")) {
                                        update.setInt(1, mapId);
                                        update.setString(2, username);
                                        update.executeUpdate();
                                        createResponse(event).appendContent("Success!").build();
                                    }
                                } else {
                                    createResponse(event).appendContent("Could not find any player named ").appendContent(username, MessageBuilder.Styles.INLINE_CODE).build();
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    createResponse(event).appendContent("An error occurred!").appendCode("", e.getMessage()).build();
                    e.printStackTrace();
                }
            } else {
                createResponse(event).appendContent("I am currently not connected to the server").build();
            }
        } else {
            String trigger = Discord.getConfig().getString("CommandTrigger");
            createResponse(event).appendContent(trigger + "warp <username> <map_id>", MessageBuilder.Styles.CODE).build();
        }
    }
}