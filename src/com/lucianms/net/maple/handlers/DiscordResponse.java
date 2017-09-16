package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;

/**
 * @author izarooni
 */
public abstract class DiscordResponse {

    public abstract void handle(MaplePacketReader reader);
}
