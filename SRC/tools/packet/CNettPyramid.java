package tools.packet;

import tools.data.OutPacket;

public class CNettPyramid {

    public static byte[] updateWave(int wave) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 433);
        oPacket.EncodeInt(wave);
        return oPacket.getPacket();
    }
    
    public static byte[] updateLife(int life) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 434);
        oPacket.EncodeInt(life);
        return oPacket.getPacket();
    }
    
    public static byte[] updatePoint(int point) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 435);
        oPacket.EncodeInt(point);
        return oPacket.getPacket();
    }
    
    public static byte[] rewardScore(boolean clear, int wave, int life, int point, int exp) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 437);
        oPacket.EncodeByte(clear);
        oPacket.EncodeInt(wave);
        oPacket.EncodeInt(life);
        oPacket.EncodeInt(point);
        oPacket.EncodeInt(exp);
        return oPacket.getPacket();
    }
}