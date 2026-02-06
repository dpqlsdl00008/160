package connector.netty;

import tools.data.OutPacket;

public class packetCreator {
    public static byte[] Ping() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(sendOpcode.PING.getValue());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] Login(int RESULT) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(sendOpcode.LOGIN_RESULT.getValue());
        mplew.write(RESULT);
        return mplew.getPacket();
    }

    public static byte[] Register(int RESULT) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(sendOpcode.REGISTER_RESULT.getValue());
        mplew.write(RESULT);
        return mplew.getPacket();
    }
}
