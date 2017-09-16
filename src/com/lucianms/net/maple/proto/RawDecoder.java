package com.lucianms.net.maple.proto;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * @author izarooni
 */
public class RawDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput out) throws Exception {
        byte[] packet = new byte[buffer.remaining()];
        buffer.get(packet, 0, packet.length);
        out.write(packet);
        return true;
    }
}
