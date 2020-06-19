package com.lucianms.net.maple;

import com.lucianms.net.maple.handlers.DiscordResponse;
import com.lucianms.net.maple.handlers.DiscordResponseManager;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author izarooni
 */
public class SessionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerSession.connect(null);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Channel context registered to inter server");
        ServerSession.setSession(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Channel unregistered");
        ServerSession.setSession(null);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof byte[]) {
            MaplePacketReader reader = new MaplePacketReader((byte[]) o);
            byte header = reader.readByte();
            DiscordResponse response = DiscordResponseManager.getResponse(header);
            if (response != null) {
                try {
                    LOGGER.info("Message handle {}", response.getClass().getSimpleName());
                    response.handle(reader);
                } catch (Throwable t) {
                    LOGGER.error("Failed to handle packet 0x{}", Integer.toHexString(header));
                    t.printStackTrace();
                }
            }
        } else {
            LOGGER.info("Unhandled message type {}\r\n{}", o.getClass().getSimpleName(), o);
        }
    }
}
