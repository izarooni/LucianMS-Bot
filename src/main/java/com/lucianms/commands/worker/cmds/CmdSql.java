package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.sql.*;

/**
 * @author izarooni
 */
public class CmdSql extends BaseCommand {

    public CmdSql(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Execute an SQL query";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.getArgs();
        StringBuilder query = new StringBuilder();
        for (Command.CommandArg arg : args) {
            query.append(arg).append(" ");
        }
        try (Connection con = Discord.getMapleConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    result.append(metaData.getColumnName(i)).append(" | ");
                }
                result.append("\r\n");
                while (rs.next()) {
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        result.append(rs.getObject(i).toString()).append(" | ");
                    }
                }
                createResponse(event).withContent(result.toString(), MessageBuilder.Styles.CODE).build();
                result.setLength(0);
            }
        } catch (SQLException e) {
            createResponse(event).withContent(e.toString()).build();
        }
        query.setLength(0);
    }
}
