package com.lucianms.server;

import com.lucianms.Discord;
import com.lucianms.utils.Saveable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GuildTicketList extends HashMap<String, GuildTicket> implements Saveable<DGuild> {

    private final AtomicInteger ticketUID = new AtomicInteger(1);

    public AtomicInteger getTicketUID() {
        return ticketUID;
    }

    @Override
    public boolean save(DGuild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("delete from tickets where guild_id = ?")) {
                ps.setString(1, guild.getId().asString());
                ps.executeUpdate();
            } catch (SQLException e) {
                getLogger().error("Failed to delete ticket history for guild {}", guild.toString());
            }
            try (PreparedStatement ps = con.prepareStatement("insert into tickets values (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, guild.getId().asString());
                for (GuildTicket ticket : values()) {
                    ps.setString(2, ticket.getUserID());
                    ps.setString(3, ticket.getCreationMessageID());
                    ps.setString(4, ticket.getDestinationMessageID());
                    ps.setInt(5, ticket.getTicketID());
                    ps.setBoolean(6, ticket.isCompleted());
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                getLogger().error("Failed to save ticket data for guild {}", guild.toString(), e);
            }
            return true;
        } catch (SQLException e) {
            getLogger().error("Failed to establish connection to Discord SQL");
            return false;
        }
    }

    @Override
    public boolean load(DGuild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("select * from tickets where guild_id = ? order by ticket_id asc")) {
                ps.setString(1, guild.getId().asString());
                try (ResultSet rs = ps.executeQuery()) {
                    int lastTicketUID = 0;
                    while (rs.next()) {
                        int ticketID = lastTicketUID = rs.getInt("ticket_id");
                        if (rs.getInt("ticket_state") == 0) {
                            String userID = rs.getString("user_id");
                            String creationMessageID = rs.getString("creation_message_id");
                            String destinationMessageID = rs.getString("destination_message_id");
                            put(destinationMessageID, new GuildTicket(ticketID, userID, destinationMessageID, creationMessageID));
                        }
                    }
                    ticketUID.set(lastTicketUID + 1);
                }
            } catch (SQLException e) {
                getLogger().error("Failed to load tickets for {}", guild.toString(), e);
            }
            return true;
        } catch (SQLException e) {
            getLogger().error("Failed to establish connection to Discord SQL");
            return false;
        }
    }
}