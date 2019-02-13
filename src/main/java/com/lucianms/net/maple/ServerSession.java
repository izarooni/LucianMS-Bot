package com.lucianms.net.maple;

import com.lucianms.net.maple.proto.DirectPacketDecoder;
import com.lucianms.net.maple.proto.DirectPacketEncoder;
import com.lucianms.nio.NettyDiscardClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @author izarooni
 */
public class ServerSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSession.class);
    private static Channel session = null;

    private ServerSession() {
    }

    public static void connect() {
        NettyDiscardClient client = new NettyDiscardClient(
                "127.0.0.1", 8483,
                new NioEventLoopGroup(), new SessionHandler(),
                DirectPacketDecoder.class, DirectPacketEncoder.class);
        client.run();
        ChannelFuture connect = client.connect();
        connect.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    connect.channel().eventLoop().schedule(() -> client, 5000, TimeUnit.MILLISECONDS);
                    LOGGER.info("Attempting to reconnect...");
                }
            }
        });
    }

    public static void sendPacket(byte[] packet) {
        if (session == null) {
            LOGGER.error("Currently not connected to the server");
            return;
        }
        session.writeAndFlush(packet);
    }

    public static Channel getSession() {
        return session;
    }

    public static void setSession(Channel session) {
        ServerSession.session = session;
    }
}
