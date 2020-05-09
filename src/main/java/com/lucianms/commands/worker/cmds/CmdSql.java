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

import java.sql.*;

/**
 * @author izarooni
 */
public class CmdSql extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSql.class);

    public CmdSql(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Execute an SQL query";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

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
                ch.createMessage("`" + result.toString() + "`").block();
                result.setLength(0);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed SQL execution", e);
            ch.createMessage("`" + e.toString() + "`").block();
        }
        query.setLength(0);
    }
}
