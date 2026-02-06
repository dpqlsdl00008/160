package tools.packet;

import handling.FieldEffectType;
import handling.SendPacketOpcode;
import tools.data.OutPacket;

public class FieldEffect {

    public static byte[] summonEffect(byte type, int PositionX, int PositionY) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.SUMMON.getValue());
        oPacket.EncodeByte(type);
        oPacket.EncodeInt(PositionX);
        oPacket.EncodeInt(PositionY);
        return oPacket.getPacket();
    }
    
    public static byte[] trembleEffect(byte type, int delay) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.TREMBLE.getValue());
        oPacket.EncodeByte(type);
        oPacket.EncodeInt(delay);
        return oPacket.getPacket();
    }
    
    public static byte[] objectEnableEffect(String UOL) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.OBJECT_ENABLE.getValue());
        oPacket.EncodeString(UOL);
        return oPacket.getPacket();
    }
    
    public static byte[] objectDisableEffect(String UOL) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.OBJECT_DISABLE.getValue());
        oPacket.EncodeString(UOL);
        return oPacket.getPacket();
    }
    
    public static byte[] playSound(String UOL) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.PLAY_SOUND.getValue());
        oPacket.EncodeString(UOL);
        return oPacket.getPacket();
    }
    
    public static byte[] showMonsterHpTag(int id, int hp, int maxHp, byte tagColor, byte tabBgColor) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.MONSTER_HP_TAG.getValue());
        oPacket.EncodeInt(id);
        oPacket.EncodeInt(hp);
        oPacket.EncodeInt(maxHp);
        oPacket.EncodeByte(tagColor);
        oPacket.EncodeByte(tabBgColor);
        return oPacket.getPacket();
    }
    
    public static byte[] changeBGM(String UOL) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.CHANGE_BGM.getValue());
        oPacket.EncodeString(UOL);
        return oPacket.getPacket();
    }
    
    public static byte[] rewardRoulette(int job, int part, int level) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        oPacket.EncodeByte(FieldEffectType.REWARD_ROULETTE.getValue());
        oPacket.EncodeInt(job);
        oPacket.EncodeInt(part);
        oPacket.EncodeInt(level);
        return oPacket.getPacket();
    }
}