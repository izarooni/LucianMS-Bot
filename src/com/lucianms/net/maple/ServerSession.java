package com.lucianms.net.maple;

import com.lucianms.net.maple.proto.RawDecoder;
import com.lucianms.net.maple.proto.RawEncoder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author izarooni
 */
public class ServerSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSession.class);
    private static IoSession session = null;

    private ServerSession() {
    }

    public static synchronized void connect() {
        if (session != null) {
            session.closeNow();
            session = null;
        }
        NioSocketConnector client = new NioSocketConnector();
        client.setHandler(new SessionHandler());
        client.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {
            RawEncoder encoder = new RawEncoder();
            RawDecoder decoder = new RawDecoder();

            @Override
            public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
                return encoder;
            }

            @Override
            public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
                return decoder;
            }
        }));
        client.connect(new InetSocketAddress("localhost", 8483));
    }

    public static void sendPacket(byte[] packet) {
        if (session == null) {
            LOGGER.error("Currently not connected to the server");
            return;
        }
        session.write(packet);
    }

    public static IoSession getSession() {
        return session;
    }

    public static void setSession(IoSession session) {
        ServerSession.session = session;
    }
}
