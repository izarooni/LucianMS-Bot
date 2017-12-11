package com.lucianms.net.maple.handlers;

import com.lucianms.utils.packet.receive.MaplePacketReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author izarooni
 */
public class ServerShutdown extends DiscordResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerShutdown.class);

    @Override
    public void handle(MaplePacketReader reader) {
        LOGGER.info("Shutting down... Good bye!");
        System.exit(0);
    }
}
