package tools.packet;

import handling.SendPacketOpcode;
import handling.UserEffectType;
import tools.data.OutPacket;

public class UserEffect {
    
    public static byte[] sceneEffect(String scene) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.USER_EFFECT2.getValue());
        oPacket.EncodeByte(UserEffectType.SCENE_EFFECT.getValue());
        oPacket.EncodeString(scene);
        return oPacket.getPacket();
    }
    
    public static byte[] delayEffect(String UOL, int unknown) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.USER_EFFECT2.getValue());
        oPacket.EncodeByte(UserEffectType.DELAY_EFFECT.getValue());
        oPacket.EncodeString(UOL);
        oPacket.EncodeInt(unknown);
        return oPacket.getPacket();
    }
    
    public static byte[] blindEffect(boolean active) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.USER_EFFECT2.getValue());
        oPacket.EncodeByte(UserEffectType.BLIND_EFFECT.getValue());
        oPacket.EncodeByte(active ? 1 : 0);
        return oPacket.getPacket();
    }
}