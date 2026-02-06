package handling;

import java.util.Arrays;

public enum FieldEffectType {
    
    SUMMON(0),
    TREMBLE(1),
    OBJECT_ENABLE(2),
    OBJECT_DISABLE(3),
    PLAY_SOUND(4),
    MONSTER_HP_TAG(5),
    CHANGE_BGM(6),
    REWARD_ROULETTE(7);

    private byte a;

    FieldEffectType(int value) {
        this.a = (byte) value;
    }

    public byte getValue() {
        return a;
    }

    public static FieldEffectType getByValue(int value) {
        return Arrays.stream(values()).filter(uet -> uet.getValue() == value).findAny().orElse(null);
    }
}

