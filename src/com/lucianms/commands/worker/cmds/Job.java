package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.utils.Database;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author izarooni
 */
public class Job extends BaseCommand {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BaseCommand.class);

    @Override
    public String getDescription() {
        return "Change the job of a specified offline player";
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
            int jobId = var_mapId.intValue();
            if (jobId < 0 || !isJobId(jobId)) {
                createResponse(event).appendContent(args[1].toString(), MessageBuilder.Styles.INLINE_CODE).appendContent(" is not a valid job").build();
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
                                    try (PreparedStatement update = con.prepareStatement("update characters set job = ? where name = ?")) {
                                        update.setInt(1, jobId);
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
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <ign> <job ID>`");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }


    private boolean isJobId(int jobId) {
        int advancement = jobId % 100 % 10;
        return (advancement == 0 || advancement == 1 || advancement == 2) && !(jobId == 2200);
    }

}
