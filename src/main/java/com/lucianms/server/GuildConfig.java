package com.lucianms.server;

import com.lucianms.Discord;
import com.lucianms.utils.Saveable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildConfig implements Saveable<DGuild> {

    private static final String TicketCreationKey = "cid_ticket_creation";
    private static final String TicketDestinationKey = "cid_ticket_destination";
    private static final String ApplicationDestinationKey = "cid_app_destination";
    private static final String ServerVoteURLKey = "guild_gtop_vote_link";

    private final GuildWordBlackList wordBlackList;
    private String CIDTicketCreation, CIDTicketDestination;
    private String CIDApplicationDestination;

    private String voteURL;

    GuildConfig() {
        wordBlackList = new GuildWordBlackList();

        CIDApplicationDestination = "";
        CIDTicketCreation = "";
        CIDTicketDestination = "";

        voteURL = "";
    }

    @Override
    public boolean save(DGuild DGuild) {
        try (Connection con = Discord.getDiscordConnection()) {
            con.setAutoCommit(false);
            saveGuildProperty(DGuild, con, TicketDestinationKey, CIDTicketDestination);
            saveGuildProperty(DGuild, con, TicketCreationKey, CIDTicketCreation);
            saveGuildProperty(DGuild, con, ApplicationDestinationKey, CIDApplicationDestination);
            saveGuildProperty(DGuild, con, ServerVoteURLKey, voteURL);
            con.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            getLogger().warn("Failed to establish connection to Discord SQL", e);
            return false;
        }
    }

    private void saveGuildProperty(DGuild guild, Connection con, String propertyKey, String propertyValue) {
        try (PreparedStatement ps = con.prepareStatement("insert into configuration values (?, ?, ?) ON duplicate key update property_value = ?")) {
            ps.setString(1, guild.getId().asString());
            ps.setString(2, propertyKey);
            ps.setString(3, propertyValue);
            ps.setString(4, propertyValue);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("Failed to save property {} for {}", propertyKey, guild.toString(), e);
        }
    }

    @Override
    public boolean load(DGuild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("select * from configuration where guild_id = ?")) {
                ps.setString(1, guild.getId().asString());
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
                            case ServerVoteURLKey:
                                setVoteURL(property_value);
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

    public String getVoteURL() {
        return voteURL;
    }

    public void setVoteURL(String voteURL) {
        this.voteURL = voteURL;
    }
}
