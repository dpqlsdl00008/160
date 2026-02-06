package client.inventory;

public enum ItemFlag {

    Locked(0x01),
    PreventSlipping(0x02),
    PreventColdness(0x04),
    Untradable(0x08),
    KarmasCissors(0x10),
    NoNonCombatStatGain(0x20),
    Used(0x40),
    Crafted(0x80),
    ProtectScroll(0x100),
    LuckyDayScroll(0x200),
    Unk_01(0x400),
    Unk_02(0x800),
    TradedOnceWithinAccount(0x1000),
    SafetyScroll(0x2000),
    RecoveryScroll(0x4000);

    private final int i;

    private ItemFlag(int i) {
        this.i = i;
    }

    public final int getValue() {
        return i;
    }

    public final boolean check(int flag) {
        return (flag & i) == i;
    }
}