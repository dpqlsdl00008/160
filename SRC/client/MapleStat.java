package client;

public enum MapleStat {

    Skin(0x1),
    Face(0x2),
    Hair(0x4),
    Pet(0x8),
    Level(0x10),
    Job(0x20),
    Str(0x40),
    Dex(0x80),
    Int(0x100),
    Luk(0x200),
    HP(0x400),
    MaxHP(0x800),
    MP(0x1000),
    MaxMP(0x2000),
    AP(0x4000),
    SP(0x8000),
    Exp(0x10000),
    Fame(0x20000),
    Meso(0x40000),
    Fatigue(0x80000),
    Charisma(0x100000),
    Insight(0x200000),
    Will(0x400000),
    Craft(0x800000),
    Sense(0x1000000),
    Charm(0x2000000),
    DayLimit(0x4000000),
    PvpExp(0x8000000),
    PvpRank(0x10000000),
    PvpPoint(0x20000000),
    IceGuage(0x40000000),

    VIRTUE(0x80000000); //160에 없는듯

    private final int i;

    private MapleStat(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }

    public static final MapleStat getByValue(final int value) {
        for (final MapleStat stat : MapleStat.values()) {
            if (stat.i == value) {
                return stat;
            }
        }
        return null;
    }

    public static enum Temp {

        STR(0x1),
        DEX(0x2),
        INT(0x4),
        LUK(0x8),
        WATK(0x10),
        WDEF(0x20),
        MATK(0x40),
        MDEF(0x80),
        ACC(0x100),
        AVOID(0x200),
        SPEED(0x400), // byte
        JUMP(0x800), // byte
	UNKNOWN(0x1000); // byte
		
        private final int i;

        private Temp(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }
}
