package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author izarooni
 */
public class CmdJob extends BaseCommand {

    public CmdJob(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Change the job of a specified offline player";
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
                ch.createMessage(String.format("`%s` is not a valid number.", args[1].toString())).block();
                return;
            }
            int jobId = var_mapId.intValue();
            if (jobId < 0 || !isJobId(jobId)) {
                ch.createMessage(String.format("`%s` is not a valid job.", args[1].toString())).block();
                return;
            }
            try (Connection con = Discord.getMapleConnection()) {
                try (PreparedStatement query = con.prepareStatement("select count(*) as total from characters where name = ?")) {
                    query.setString(1, username);
                    try (ResultSet rs = query.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("total") == 1) {
                                try (PreparedStatement update = con.prepareStatement("update characters set job = ? where name = ?")) {
                                    update.setInt(1, jobId);
                                    update.setString(2, username);
                                    update.executeUpdate();
                                    ch.createMessage("Success!").block();
                                }
                            } else {
                                ch.createMessage(String.format("Could not find any player named `%s`", username)).block();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ch.createMessage(String.format("A(n) `%s` exception occurred", e.getClass().getSimpleName())).block();
                e.printStackTrace();
            }
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <ign> <job ID>`");
            }).block();
        }
    }


    private boolean isJobId(int jobId) {
        int advancement = jobId % 100 % 10;
        return (advancement == 0 || advancement == 1 || advancement == 2) && !(jobId == 2200);
    }
}
