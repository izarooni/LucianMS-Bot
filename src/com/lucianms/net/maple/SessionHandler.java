package com.lucianms.net.maple;

import com.lucianms.net.maple.handlers.DiscordResponse;
import com.lucianms.net.maple.handlers.DiscordResponseManager;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author izarooni
 */
public class SessionHandler implements IoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHandler.class);

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        LOGGER.info("Session #{} created", ioSession.getId());
    }

    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {
        LOGGER.info("Session #{} opened", ioSession.getId());
        ServerSession.setSession(ioSession);
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        LOGGER.info("Session #{} closed", ioSession.getId());
        ServerSession.setSession(null);
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {
        LOGGER.info("Session #{} idle", ioSession.getId());
    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {
        if (throwable instanceof IOException) {
            LOGGER.error("Session #{} caught exception {}", ioSession.getId(), throwable.getMessage());
        } else {
            throwable.printStackTrace();
        }
    }

    @Override
    public void messageReceived(IoSession ioSession, Object o) throws Exception {
        LOGGER.info("Session #{} received message", ioSession.getId());
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

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {
        LOGGER.info("Session #{} message sent", ioSession.getId());
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        LOGGER.info("Session #{} closed input", ioSession.getId());
        ioSession.closeNow();

    }
}
