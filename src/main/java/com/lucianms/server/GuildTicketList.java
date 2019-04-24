package com.lucianms.server;

import com.lucianms.Discord;
import com.lucianms.utils.Saveable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GuildTicketList extends HashMap<Long, GuildTicket> implements Saveable<Guild> {

    private final AtomicInteger ticketUID = new AtomicInteger(1);

    public AtomicInteger getTicketUID() {
        return ticketUID;
    }

    @Override
    public boolean save(Guild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("delete from tickets where guild_id = ?")) {
                ps.setLong(1, guild.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                getLogger().error("Failed to delete ticket history for guild {}", guild.toString());
            }
            try (PreparedStatement ps = con.prepareStatement("insert into tickets values (?, ?, ?, ?, ?, ?)")) {
                ps.setLong(1, guild.getId());
                for (GuildTicket ticket : values()) {
                    ps.setLong(2, ticket.getUserID());
                    ps.setLong(3, ticket.getCreationMessageID());
                    ps.setLong(4, ticket.getDestinationMessageID());
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
    public boolean load(Guild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("select * from tickets where guild_id = ? and ticket_state = 0 order by ticket_id asc")) {
                ps.setLong(1, guild.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    int lastTicketUID = 0;
                    while (rs.next()) {
                        int ticketID = lastTicketUID = rs.getInt("ticket_id");
                        long userID = rs.getLong("user_id");
                        long creationMessageID = rs.getLong("creation_message_id");
                        long destinationMessageID = rs.getLong("destination_message_id");
                        put(destinationMessageID, new GuildTicket(ticketID, userID, destinationMessageID, creationMessageID));
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