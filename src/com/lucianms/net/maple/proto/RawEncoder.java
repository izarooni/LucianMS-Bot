package com.lucianms.net.maple.proto;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author izarooni
 */
public class RawEncoder implements ProtocolEncoder {

    @Override
    public void encode(IoSession ioSession, Object object, ProtocolEncoderOutput out) throws Exception {
        byte[] packet = (byte[]) object;
        out.write(IoBuffer.wrap(packet));
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {

    }
}
