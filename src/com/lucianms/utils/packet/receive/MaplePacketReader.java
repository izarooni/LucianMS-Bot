package com.lucianms.utils.packet.receive;


import com.lucianms.utils.HexUtil;
import com.lucianms.utils.Location;

/**
 * @author izarooni
 */
public class MaplePacketReader extends LittleEndianAccessor {

    public MaplePacketReader(byte[] arr) {
        super(arr);
    }

    public short getHeader() {
        int b1 = (super.arr[0]);
        int b2 = (super.arr[1] & 0xFF) << 8;
        return (short) (b2 + b1);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public byte readByte() {
        return (byte) super.read();
    }

    public String readMapleAsciiString() {
        return readAsciiString(readShort());
    }

    public String readRawBytes(int length) {
        byte[] read = read(length);
        return HexUtil.toString(read);
    }

    public Location readLocation() {
        return new Location(readShort(), readShort());
    }
}
