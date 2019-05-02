package com.lucianms.server;

import com.lucianms.Discord;
import com.lucianms.utils.Saveable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildConfig implements Saveable<Guild> {

    private static final String TicketCreationKey = "cid_ticket_creation";
    private static final String TicketDestinationKey = "cid_ticket_destination";
    private static final String ApplicationDestinationKey = "cid_app_destination";

    private final GuildWordBlackList wordBlackList;
    private String CIDTicketCreation, CIDTicketDestination;
    private String CIDApplicationDestination;

    GuildConfig() {
        wordBlackList = new GuildWordBlackList();

        CIDApplicationDestination = "";
        CIDTicketCreation = "";
        CIDTicketDestination = "";
    }

    @Override
    public boolean save(Guild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            con.setAutoCommit(false);
            saveGuildProperty(guild, con, TicketDestinationKey, CIDTicketDestination);
            saveGuildProperty(guild, con, TicketCreationKey, CIDTicketCreation);
            saveGuildProperty(guild, con, ApplicationDestinationKey, CIDApplicationDestination);
            con.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            getLogger().warn("Failed to establish connection to Discord SQL", e);
            return false;
        }
    }

    private void saveGuildProperty(Guild guild, Connection con, String propertyKey, String propertyValue) {
        try (PreparedStatement ps = con.prepareStatement("insert into configuration values (?, ?, ?) ON duplicate key update property_value = ?")) {
            ps.setLong(1, guild.getId());
            ps.setString(2, propertyKey);
            ps.setString(3, propertyValue);
            ps.setString(4, propertyValue);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("Failed to save property {} for {}", propertyKey, guild.toString(), e);
        }
    }

    @Override
    public boolean load(Guild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("select * from configuration where guild_id = ?")) {
                ps.setLong(1, guild.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String property_key = rs.getString("property_key");
                        String property_value = rs.getString("property_value");
                        switch (property_key) {
                            case ApplicationDestinationKey:
                                setCIDApplicationDestination(property_value);
                                break;
                            case TicketCreationKey:
                                setCIDTicketCreation(property_value);
                                break;
                            case TicketDestinationKey:
                                setCIDTicketDestination(property_value);
                                break;
                        }
                    }
                    return true;
                }
            } catch (SQLException e) {
                getLogger().error("Failed to load configuration for {}", guild.toString(), e);
            }
        } catch (SQLException e) {
            getLogger().warn("Failed to establish connection to Discord SQL");
        }
        return false;
    }

    public GuildWordBlackList getWordBlackList() {
        return wordBlackList;
    }

    public String getCIDApplicationDestination() {
        return CIDApplicationDestination;
    }

    public void setCIDApplicationDestination(String CIDApplicationDestination) {
        this.CIDApplicationDestination = CIDApplicationDestination;
    }

    public String getCIDTicketCreation() {
        return CIDTicketCreation;
    }

    public void setCIDTicketCreation(String CIDTicketCreation) {
        this.CIDTicketCreation = CIDTicketCreation;
    }

    public String getCIDTicketDestination() {
        return CIDTicketDestination;
    }

    public void setCIDTicketDestination(String CIDTicketDestination) {
        this.CIDTicketDestination = CIDTicketDestination;
    }
}
