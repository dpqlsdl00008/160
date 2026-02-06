package handling;

import java.util.Arrays;

public enum UserEffectType {
    USER_LEVEL_UP(0),
    SKILL_USE(1),
    SKILL_UNKNOWN_1(2),
    SKILL_UNKNOWN_2(3),
    SKILL_UNKNOWN_3(4),
    SHOW_ITEM_MESSAGE(5),
    PET(6),
    SKILL_UNKNOWN_4(7),
    RESIST(8),
    PROTECT_ON_DIE_ITEM_USE_MESSAGE(9),
    PORTAL_SE(10),
    JOB_CHANGED(11),
    QUEST_CLEAR(12),
    INC_DESC_HP_EFFECT(13),
    ITEM_EFFECT(14),
    SQUIB_EFFECT(15),
    MONSTER_BOOK_CARD_GET(16),
    USE_LOTTERY(17),
    ITEM_LEVEL_UP(18),
    ITEM_MAKER(19),
    INC_EXP(20),
    SCENE_EFFECT(21),
    USE_WHEEL(22),
    UNKNOWN_2(23),
    DELAY_EFFECT(24),
    SOUND_UNKNOWN_1(25),
    SOUND_UNKNOWN_2(26),
    SOUND_UNKNOWN_3(27),
    USE_SOUL_STONE(28),
    UNKNOWN_4(29),
    UNKNOWN_5(30),
    EFFECT_UOL(31),
    PVP_RAGE(32),
    PVP_CHAMPION(33),
    PVP_GRADE_UP(34),
    PVP_REVIVE(35),
    SKILL_UNKNOWN_5(36),
    FADE_IN_OUT(37),
    MONSTER_SKILL_HIT(38),
    HONOR_LEVEL_UP(39),
    ASWAN_DEFENSE(40),
    BLIND_EFFECT(41);

    private byte a;

    UserEffectType(int value) {
        this.a = (byte) value;
    }

    public byte getValue() {
        return a;
    }

    public static UserEffectType getByValue(int value) {
        return Arrays.stream(values()).filter(uet -> uet.getValue() == value).findAny().orElse(null);
    }
}
