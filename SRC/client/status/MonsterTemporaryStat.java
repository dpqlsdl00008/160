package client.status;

import client.MonsterSkill;
import handling.Buffstat;
import java.io.Serializable;

public enum MonsterTemporaryStat implements Serializable, Buffstat {

    Pad(0x1, 1),
    Pdr(0x2, 1),
    Mad(0x4, 1),
    Mdr(0x8, 1),
    
    Acc(0x10, 1),
    Eva(0x20, 1),
    Speed(0x40, 1),
    Stun(0x80, 1),
    
    Frozen(0x100, 1),
    Poison(0x200, 1),
    Seal(0x400, 1),
    Darkness(0x800, 1),
    
    PowerUp(0x1000, 1),
    PGuardUp(0x2000, 1),
    MagicUp(0x4000, 1),
    MGuardUp(0x8000, 1),
    
    Doom(0x10000, 1),
    ShadowWeb(0x20000, 1),
    PImmune(0x40000, 1),
    MImmune(0x80000, 1),
    
    ShowDown(0x100000, 1),
    HardSkin(0x200000, 1),
    NinjaAmbush(0x400000, 1),
    DamagedElemAttr(0x800000, 1),
    
    Venom(0x1000000, 1),
    Blind(0x2000000, 1),
    SealSkill(0x4000000, 1),
    Burn(0x8000000, 1),    
    
    Dazzle(0x10000000, 1),
    PCounter(0x20000000, 1),
    MCounter(0x40000000, 1),
    InvincibleBalog(0x80000000, 1), // allMiss

    RiseByToss(0x1, 2),
    BodyPressure(0x2, 2),
    GhostLettering(0x4, 2),
    MonsterBomb(0x8, 2),
    
    MagicCrash(0x10, 2),
    ExchangeAttack(0x20, 2),
    Invincible(0x40, 2),
    RareMonster(0x80, 2),
    
    DarkMonster(0x100, 2),
    MysteryMonster(0x200, 2),
    AddDamParty(0x400, 2),
    HitCriDamR(0x800, 2),
    
    ExtraBuffStat(0x1000, 2), // Fatality
    Unk_0x2000_2(0x2000, 2), // Lifting
    ;
    
    static final long serialVersionUID = 0L;
    public int i;
    private final int first;
    private final boolean end;

    private MonsterTemporaryStat(int i, int first) {
        this.i = i;
        this.first = first;
        this.end = false;
    }

    private MonsterTemporaryStat(int i, int first, boolean end) {
        this.i = i;
        this.first = first;
        this.end = end;
    }

    public int getPosition() {
        return first;
    }

    public boolean isEmpty() {
        return end;
    }

    public int getValue() {
        return i;
    }

    public void setValue(int v1) {
        i = v1;
    }
    
    public static final MonsterSkill getAffectedCTS(final MonsterTemporaryStat skill) {
        switch (skill) {
            case Stun:
            case ShadowWeb:
                return MonsterSkill.Stun;
            case Poison:
            case Venom:
                return MonsterSkill.Poison;
            case Seal:
            case MagicCrash:
                return MonsterSkill.Seal;
            case Frozen:
                return MonsterSkill.Frozen;
            case Blind:
                return MonsterSkill.Darkness;
            case Speed:
                return MonsterSkill.Slow;
        }
        return null;
    }
}
