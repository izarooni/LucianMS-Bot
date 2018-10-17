package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.utils.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

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
public class Strip extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Strip.class);

    @Override
    public String getDescription() {
        return "Strip a specified offline character";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        Command.CommandArg[] args = command.args;
        if (args.length == 1) {
            String username = args[0].toString();
            try {
                int playerId; // id of the player
                int equipSlots; // total slots the player has
                int equippedItems = 0; // items currently equipped
                int inventoryItems = 0; // items in the equip inventory

                Connection connection = Database.getConnection();

                // get equip slots and id of player
                try (PreparedStatement ps = connection.prepareStatement("select count(*) as total, id, equipslots from characters where name = ?")) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt("total") == 1) {
                            equipSlots = rs.getInt("equipslots");
                            playerId = rs.getInt("id");
                        } else {
                            createResponse(event).appendContent("Could not find any player named ").appendContent(username, MessageBuilder.Styles.INLINE_CODE).build();
                            return;
                        }
                    }
                }

                // get item count of equipped items
                try (PreparedStatement ps = connection.prepareStatement("select count(*) as total from inventoryitems where characterid = ? and inventorytype = -1")) {
                    ps.setInt(1, playerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            equippedItems = rs.getInt("total");
                        }
                    }
                }
                // get item count in equip inventory
                try (PreparedStatement ps = connection.prepareStatement("select count(*) as total from inventoryitems where characterid = ? and inventorytype = 1")) {
                    ps.setInt(1, playerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            inventoryItems = rs.getInt("total");
                        }
                    }
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
                        createResponse(event).appendContent(username, MessageBuilder.Styles.INLINE_CODE).appendContent(" is not wearing an items").build();
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
                                createResponse(event).appendContent("A processing error occurred").build();
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
                        createResponse(event).appendContent("Success!").build();
                    }
                    equip.clear();
                    equipped.clear();
                } else {
                    // @formatter:off
                    createResponse(event)
                            .appendContent("Unable to strip due to ")
                            .appendContent(Integer.toString(equippedItems), MessageBuilder.Styles.INLINE_CODE)
                            .appendContent(" items equipped and ")
                            .appendContent(Integer.toString(inventoryItems), MessageBuilder.Styles.INLINE_CODE)
                            .appendContent(" in their inventory, this player has only ")
                            .appendContent(Integer.toString(equipSlots), MessageBuilder.Styles.INLINE_CODE)
                            .appendContent(" inventory slots").build();
                    // @formatter:on
                }
            } catch (SQLException e) {
                createResponse(event).appendContent("An SQL error occurred").build();
                e.printStackTrace();
            }
        } else {
            String trigger = Discord.getConfig().getString("CommandTrigger");
            createResponse(event).appendContent(trigger + "strip <username>", MessageBuilder.Styles.CODE).build();
        }
    }
}
