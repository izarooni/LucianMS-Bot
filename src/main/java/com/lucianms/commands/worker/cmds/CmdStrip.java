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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author izarooni
 */
public class CmdStrip extends BaseCommand {

    public CmdStrip(CommandUtil permission) {
        super(permission);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdStrip.class);

    @Override
    public String getDescription() {
        return "Strip a specified offline character";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        Command.CommandArg[] args = command.args;
        if (args.length == 1) {
            String username = args[0].toString();
            StringBuilder sb = new StringBuilder();
            try (Connection connection = Discord.getMapleConnection()) {
                int playerId; // id of the player
                int equipSlots; // total slots the player has
                int equippedItems = 0; // items currently equipped
                int inventoryItems = 0; // items in the equip inventory


                // get equip slots and id of player
                try (PreparedStatement ps = connection.prepareStatement("select count(*) as total, id, equipslots from characters where name = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt("total") == 1) {
                            equipSlots = rs.getInt("equipslots");
                            playerId = rs.getInt("id");
                        } else {
                            ch.createEmbed(e -> e.setDescription("Failed to find any player named `" + username + "`")).block();
                            return;
                        }
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement("select petid from inventoryitems where characterid = ? and petid > 0")) {
                    ps.setInt(1, playerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        int count = 0;
                        try (PreparedStatement pets = connection.prepareStatement("update pets set summoned = 0 where petid = ? and summoned = 1")) {
                            while (rs.next()) {
                                pets.setInt(1, rs.getInt("petid"));
                                pets.addBatch();
                                count++;
                            }
                            pets.executeBatch();
                        }
                        if (count > 0) {
                            sb.append("\r\nUnequipped `").append(Integer.toString(count)).append("` pets");
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Failed to un-equip pets", e);
                    sb.append("\r\nFailed to unequip pets");
                }
                // get item count of equipped items
                try (PreparedStatement ps = connection.prepareStatement("select count(*) as total from inventoryitems where characterid = ? and inventorytype = -1")) {
                    ps.setInt(1, playerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            equippedItems = rs.getInt("total");
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Failed to get item count for equipped items", e);
                    sb.append("\r\nFailed to get item count for equipped items");
                }
                // get item count in equip inventory
                try (PreparedStatement ps = connection.prepareStatement("select count(*) as total from inventoryitems where characterid = ? and inventorytype = 1")) {
                    ps.setInt(1, playerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            inventoryItems = rs.getInt("total");
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Failed to determine slot count for the equip tab", e);
                    sb.append("\r\nFailed to get slot count for the equip tab");
                }
                // transfer equips to inventory
                if (inventoryItems + equippedItems <= equipSlots) {
                    LinkedHashMap<Integer, Integer> equip = new LinkedHashMap<>(); // items in the equip inventory
                    LinkedHashMap<Integer, Integer> equipped = new LinkedHashMap<>(); // items currently equipped

                    for (int i = 0; i < equipSlots; i++) {
                        equip.put(i, null);
                    }
                    // get current equipped items
                    try (PreparedStatement ps = connection.prepareStatement("select inventoryitemid, position from inventoryitems where characterid = ? and inventorytype = -1")) {
                        ps.setInt(1, playerId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                equipped.put(rs.getInt("position") - 1, rs.getInt("inventoryitemid"));
                            }
                        }
                    }
                    if (equipped.isEmpty()) {
                        sb.append("\r\nFailed to find any equips on this character");
                        ch.createEmbed(e -> {
                            e.setTitle("Stripping " + username + "...");
                            e.setDescription(sb.toString());
                        }).block();
                        return;
                    }
                    // get used inventory slots
                    try (PreparedStatement ps = connection.prepareStatement("select inventoryitemid, position from inventoryitems where characterid = ? and inventorytype = 1")) {
                        ps.setInt(1, playerId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                equip.put(rs.getInt("position") - 1, rs.getInt("inventoryitemid"));
                            }
                        }
                    }
                    // transfers items
                    try (PreparedStatement ps = connection.prepareStatement("update inventoryitems set inventorytype = 1, position = ? where characterid = ? and inventorytype = -1 and inventoryitemid = ?")) {
                        for (Map.Entry<Integer, Integer> entry : equipped.entrySet()) { // go thru equipped items
                            Optional<Map.Entry<Integer, Integer>> optional = equip.entrySet().stream().filter(e -> e.getValue() == null).findFirst(); // find first available slot in inventory
                            if (!optional.isPresent()) { // if there are no empty slots...
                                LOGGER.warn("Unable to transfer equipped inventory item (id:{}) to inventory", entry.getValue());
                                ch.createEmbed(e -> e.setDescription("A processing error(1) occurred")).block();
                                // no more empty slots
                                return;
                            }
                            equip.put(optional.get().getKey(), 0); // anything but null
                            ps.setInt(1, optional.get().getKey() + 1); // WHY INDEX START AT 1 THO?
                            ps.setInt(2, playerId);
                            ps.setInt(3, entry.getValue()); // inventoryitemid - column to update
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        sb.append("\r\n`").append(Integer.toString(equipped.size())).append("` items moved");
                        ch.createEmbed(e -> {
                            e.setTitle("Stripping " + username + "...");
                            e.setDescription(sb.toString());
                        }).block();
                    }
                    equip.clear();
                    equipped.clear();
                } else {
                    ch.createEmbed(e -> {
                        e.setTitle("Stripping " + username + "...");
                        e.setDescription("Failed to strip. The inventory would overflow with items.");
                    }).block();
                }
            } catch (SQLException ex) {
                LOGGER.error("Failed to strip a character", ex);
                ch.createEmbed(e -> e.setDescription("A processing error(2) occurred")).block();
            }
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <ign>`");
            }).block();
        }
    }
}
