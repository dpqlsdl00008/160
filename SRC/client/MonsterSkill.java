package client;

import handling.Buffstat;
import java.io.Serializable;
import server.Randomizer;

public enum MonsterSkill implements Serializable, Buffstat {

    PowerUp(100),
    MagicUp(101),
    PGuardUp(102),
    MGuardUp(103),
    Haste(104),
    MobConsume(105),
    PowerUpM(110),
    MagicUpM(111),
    PGuardUpM(112),
    MGuardUpM(113),
    HealM(114),
    HasteM(115),
    
    Seal(0x80000, 1, 120),
    Darkness(0x100000, 1, 121),
    Weakness(0x40000000, 1, 122),
    Stun(0x20000, 1, 123),
    Curse(0x80000000, 1, 124),
    Poison(0x40000, 1, 125),
    Slow(0x1, 2, 126),
    Dispel(0x8, 5, 127), // ?
    Attract(0x80, 2, 128),
    //BanMap(0x4000, 2, 129),
    ReverseInput(0x80000, 2, 132),
    Undead(0x4000, 2, 133), // ?
    StopPotion(0x200, 3, 134),
    StopMotion(0x400, 3, 135),
    Fear(0x800, 3, 136),
    Frozen(0x20000, 3, 137),
    DispelItemOption(0x1000000, 4, 138),
    
    PhysicalImmune(140),
    MagicImmune(141),
    Hardskin(142),
    PCounter(143),
    MCounter(144),
    PMCounter(145),
    Invincible(146),
    Pad(150),
    Mad(151),
    Pdr(152),
    Mdr(153),
    Acc(154),
    Eva(155),
    Speed(156),
    SealSkill(157),
    Teleport(170),
    
    UserBomb(0x40000000, 4, 171),
    UserMorph(0x2, 2, 172),
    DarkTornado(0x200, 5, 173),
    Lapidification(0x2, 7, 174),
    DeathMark(0x80000000, 6, 175),
    Damage(/*0x800000, 5, */176),
    VenomSnake(0x4, 7, 177),
    PainMark(0x2, 8, 179),
    
    AreaPoison(0x40000, 1, 131),
    SlowAttack(0x200, 7, 178),
    VampDeath(0x10, 8, 180),
    Magnet(0x4, 8, 181),
    
    MagnetArea(0x8, 8, -1),
    VampDeathSummon(0x80000, 8, -1),
    Explosion(0x4, 5, -1),
    
    Summon_01(200),
    Summon_02(201);

    private static final long serialVersionUID = 0L;
    private int i;
    private int first;
    private int disease;

    private MonsterSkill(int disease) {
        this.disease = disease;
    }

    private MonsterSkill(int i, int first, int disease) {
        this.i = i;
        this.first = first;
        this.disease = disease;
    }

    public int getPosition() {
        return first;
    }

    public int getValue() {
        return i;
    }

    public int getDisease() {
        return disease;
    }

    public static final MonsterSkill getRandom() {
        while (true) {
            for (MonsterSkill dis : MonsterSkill.values()) {
                if (Randomizer.nextInt(MonsterSkill.values().length) == 0) {
                    return dis;
                }
            }
        }
    }

    public static final MonsterSkill getBySkill(final int skill) {
        for (MonsterSkill d : MonsterSkill.values()) {
            if (d.getDisease() == skill) {
                return d;
            }
        }
        return null;
    }
}
