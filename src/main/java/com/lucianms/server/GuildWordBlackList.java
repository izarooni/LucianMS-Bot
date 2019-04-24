package com.lucianms.server;

import com.lucianms.Discord;
import com.lucianms.utils.Saveable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GuildWordBlackList extends ArrayList<String> implements Saveable<Guild> {

    @Override
    public boolean save(Guild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("delete from forbidden_words where guild_id = ?")) {
                ps.setLong(1, guild.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                getLogger().error("Failed to clear forbidden words for guild {}", guild.toString());
                return false;
            }
            ArrayList<String> blacklistedWords = guild.getGuildConfig().getWordBlackList();
            if (!blacklistedWords.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement("insert into forbidden_words values (?, ?)")) {
                    ps.setLong(1, guild.getId());
                    for (String word : blacklistedWords) {
                        ps.setString(2, word);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                } catch (SQLException e) {
                    getLogger().error("Failed to insert forbidden word data for guild {}", guild.toString());
                }
            }
            con.commit();
            con.setAutoCommit(true);
            return true;
        } catch (SQLException ignore) {
            getLogger().error("Failed to establish connection to Discord SQL");
            return false;
        }
    }

    @Override
    public boolean load(Guild guild) {
        try (Connection con = Discord.getDiscordConnection()) {
            try (PreparedStatement ps = con.prepareStatement("select word from forbidden_words where guild_id = ?")) {
                ps.setLong(1, guild.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        add(rs.getString("word"));
                    }
                    getLogger().info("Loaded {} forbidden words for {}", size(), guild.toString());
                    return true;
                }
            } catch (SQLException e) {
                getLogger().error("Failed to load forbidden words for {}", guild.toString(), e);
            }
        } catch (Exception ignore) {
            getLogger().error("Failed to establish connection to Discord SQL");
        }
        return false;
    }
}
