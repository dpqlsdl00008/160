package tools.packet;

import handling.SendPacketOpcode;
import tools.data.OutPacket;

public class CUserAction {

    /* 209 */
    public static byte[] showPQReward(int userID, int itemID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.SHOW_PQ_REWARD.getValue());
        oPacket.EncodeInt(userID);
        for (int i = 0; i < 6; i++) {
            oPacket.EncodeByte(i);
        }
        return oPacket.getPacket();
    }

    /* 213 */
    public static byte[] OnUserExplode(int userID, int itemID) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(213);
        oPacket.EncodeInt(userID);
        oPacket.EncodeInt(1121000);
        oPacket.EncodeShort(1);
        oPacket.EncodeShort(1);
        return oPacket.getPacket();
    }
}
