package client;

import handling.Buffstat;

public enum CharacterTemporaryStat implements Buffstat {

    Pad(0x1, 1),
    Pdd(0x2, 1),
    Mad(0x4, 1),
    Mdd(0x8, 1),
    Acc(0x10, 1),
    Eva(0x20, 1),
    Craft(0x40, 1),
    Speed(0x80, 1),
    Jump(0x100, 1),
    MagicGuard(0x200, 1),
    DarkSight(0x400, 1),
    Booster(0x800, 1),
    PowerGuard(0x1000, 1),
    MaxHp(0x2000, 1),
    MaxMp(0x4000, 1),
    Invincible(0x8000, 1),
    SoulArrow(0x10000, 1),
    DragonRoar(0x20000, 1),
    Poison(0x40000, 1),
    Seal(0x80000, 1),
    Darkness(0x100000, 1),
    ComboCounter(0x200000, 1),
    Summon(0x200000, 1),        // 왜 summon이랑 같이...?
    WeaponCharge(0x400000, 1),
    DragonBlood(0x800000, 1),   // idb 상은 ElementalCharge
    HolySymbol(0x1000000, 1),
    MesoUp(0x2000000, 1),
    ShadowPartner(0x4000000, 1),
    PickPocket(0x8000000, 1),
    Puppet(0x8000000, 1),       // 왜 PickPocket이랑 같이...?
    MesoGuard(0x10000000, 1),
    Thaw(0x20000000, 1),
    Weakness(0x40000000, 1),
    Curse(0x80000000, 1),
    
    Slow(0x1, 2),
    Morph(0x2, 2),
    Regen(0x4, 2),
    MapleWarrior(0x8, 2),
    Stance(0x10, 2),
    SharpEyes(0x20, 2),
    ManaReflection(0x40, 2),
    Attract(0x80, 2),
    NoBulletConsume(0x100, 2),
    Infinity(0x200, 2),
    AdvancedBless(0x400, 2),
    IllusionStep(0x800, 2),
    Blind(0x1000, 2),
    Concentration(0x2000, 2),
    BanMap(0x4000, 2),
    EchoOfHero(0x8000, 2),
    MesoUpByItem(0x10000, 2),
    Ghost(0x20000, 2),
    Barrier(0x40000, 2),
    ReverseInput(0x80000, 2),
    DropUpByItem(0x100000, 2),
    RespectPImmune(0x200000, 2),
    RespectMImmune(0x400000, 2),
    DefenseAtt(0x800000, 2),
    DefenseState(0x1000000, 2),
    DojangBerserk(0x2000000, 2),
    DojangInvincible(0x4000000, 2),
    Spark(0x8000000, 2),
    DojangShield(0x10000000, 2),
    FinalAttack(0x20000000, 2),
    FinalAttackWindBreaker(0x40000000, 2),
    ElementalReset(0x80000000, 2),
    
    WindWalk(0x1, 3),
    EventRate(0x2, 3),
    ComboAbilityBuff(0x4, 3),
    ComboDrain(0x8, 3),
    ComboBarrier(0x10, 3),
    BodyPressure(0x20, 3),
    KnockBack(0x40, 3),
    RepeatEffect(0x80, 3),
    ExpBuffRate(0x100, 3),
    StopPotion(0x200, 3),
    StopMotion(0x400, 3),
    Fear(0x800, 3),
    HiddenPieceOn(0x1000, 3),
    MagicShield(0x2000, 3),
    MagicResistance(0x4000, 3),
    SoulStone(0x8000, 3),
    Flying(0x10000, 3),
    Frozen(0x20000, 3),
    AssistCharge(0x40000, 3),
    Enrage(0x80000, 3),
    DrawBack(0x100000, 3),
    NotDamaged(0x200000, 3),
    FinalCut(0x400000, 3),
    HowlingAttackDamage(0x800000, 3),
    BeastFormDamageUp(0x1000000, 3),
    RainingMines(0x2000000, 3),
    EnhancedMaxHp(0x4000000, 3, false, true),
    EnhancedMaxMp(0x8000000, 3, false, true),
    EnhancedPad(0x10000000, 3, false, true),
    EnhancedMad(0x20000000, 3, false, true),
    EnhancedPdd(0x40000000, 3, false, true),
    EnhancedMdd(0x80000000, 3, false, true),
    
    Guard(0x1, 4),
    SafetyProc(0x2, 4),
    SafetyAbsorb(0x4, 4),
    Cyclone(0x8, 4),
    HowlingCritical(0x10, 4),
    HowlingMaxMp(0x20, 4),
    HowlingDefence(0x40, 4),
    HowlingEva(0x80, 4),
    Conversion(0x100, 4),
    Reaper(0x200, 4),
    Infltrate(0x400, 4),
    Mechanic(0x800, 4),
    Aura(0x1000, 4),            // INFILTRATE...?
    DarkAura(0x2000, 4),
    BlueAura(0x4000, 4),
    YellowAura(0x8000, 4),
    SuperBody(0x10000, 4),
    BeastFormMaxHP(0x20000, 4),
    Dice(0x40000, 4),
    BlessingArmor(0x80000, 4),
    DamR(0x100000, 4),
    TeleportMastery(0x200000, 4),
    CombatOrders(0x400000, 4),
    Beholder(0x800000, 4),
    DispelItemOption(0x1000000, 4),
    GiantPotion(0x2000000, 4),
    OnixGodBless(0x4000000, 4),
    OnixWill(0x8000000, 4),
    Web(0x10000000, 4),
    Bless(0x20000000, 4),
    TimeBomb(0x40000000, 4),
    DisOrder(0x80000000, 4),
    
    Thread(0x1, 5),
    Team(0x2, 5),
    Explosion(0x4, 5),
    BuffLimit(0x8, 5),
    Str(0x10, 5),
    Int(0x20, 5),
    Dex(0x40, 5),
    Luk(0x80, 5),
    DispelItemOptionByField(0x100, 5),
    DarkTornado(0x200, 5),
    IndiePad(0x400, 5, true),
    IndieMad(0x800, 5, true),
    IndieMaxHp(0x1000, 5, true),
    IndieMaxMp(0x2000, 5, true),
    IndieAcc(0x4000, 5, true),
    IndieEva(0x8000, 5, true),
    IndieJump(0x10000, 5, true),
    IndieSpeed(0x20000, 5, true),
    IndieAllStat(0x40000, 5, true),
    PvPDamage(0x80000, 5),
    PvPScoreBonus(0x100000, 5),
    
    WeaknessMdamage(0x200000, 5),   // idb 상은 PvPInvincible...
    
    Unk_0x400000_5(0x400000, 5),    // idb 상은 PvPRaceEffect
    AffectedEffect(0x800000, 5),    // 이거 쏘니까 affected 출력... (idb 상은 이게 WeaknessMdamage 같은데...)
    
    EnhancedFrozen(0x1000000, 5),
    PvPDamageSkill(0x2000000, 5),
    
    AmplifyDamage(0x4000000, 5),
    Unk_0x8000000_5(0x8000000, 5, true),  // idb 상은 IceKnight...
    
    Shock(0x10000000, 5),
    InfinityForce(0x20000000, 5),
    IncMaxHp(0x40000000, 5),
    IncMaxMp(0x80000000, 5),

    HolyMagicShell(0x1, 6),
    KeyDownTimeIgnore(0x2, 6),
    ArcaneAim(0x4, 6),
    MasterMagic(0x8, 6),
    AsrR(0x10, 6),
    TerR(0x20, 6),
    DamAbsorbShield(0x40, 6),
    DevilishPower(0x80, 6),
    Roulette(0x100, 6),
    SpiritInfusion(0x200, 6),
    SpiritLink(0x400, 6, true),
    AsrRByItem(0x800, 6),
    SaintSaver(0x1000, 6),
    IndiePdd(0x2000, 6, true),
    IndieMdd(0x4000, 6, true),
    CriticalBuff(0x8000, 6),
    DropRate(0x10000, 6),
    PlusExpRate(0x20000, 6),
    InvincibleByItem(0x40000, 6),
    Awake(0x80000, 6),
    CriticalByItem(0x100000, 6),
    Event(0x200000, 6),
    VampiricTouch(0x400000, 6),
    DDR(0x800000, 6),
    IncCriticalDam(0x1000000, 6),
    Unk_0x2000000_6(0x2000000, 6),
    PMddR(0x4000000, 6),
    Unk_0x8000000_6(0x8000000, 6),
    Unk_0x10000000_6(0x10000000, 6),
    Unk_0x20000000_6(0x20000000, 6),
    Unk_0x40000000_6(0x40000000, 6),
    DeathMark(0x80000000, 6),
    
    UsefulAdvancedBless(0x1, 7),
    Lapidification(0x2, 7),
    VenomSnake(0x4, 7),
    IndieMaxHpR(0x8, 7, true),
    Unk_0x10_7(0x10, 7, true),
    CarnivalPMadR(0x20, 7),
    CarnivalPMddR(0x40, 7),
    CarnivalExpUp(0x80, 7),
    IndieBooster(0x100, 7, true),
    SlowAttack(0x200, 7, true),
    Unk_PMadR_02(0x400, 7, true),
    Unk_0x800_7(0x800, 7, true),
    Unk_0x1000_7(0x1000, 7, true),
    Unk_0x2000_7(0x2000, 7, true),
    Unk_0x4000_7(0x4000, 7, true),
    PyramidEffect(0x8000, 7),
    Unk_0x10000_7(0x10000, 7, true),
    KillingPoint(0x20000, 7),
    HollowPointBullet(0x40000, 7),
    IndieMaxMpR(0x80000, 7, true),
    FixedSpeed(0x100000, 7),
    IndieStr(0x200000, 7, true),
    IndieDex(0x400000, 7, true),
    IndieInt(0x800000, 7, true),
    IndieLuk(0x1000000, 7, true),
    IgnoreTargetDEF(0x2000000, 7),
    ReviveOnce(0x4000000, 7),
    Invisible(0x8000000, 7),
    EnrageCr(0x10000000, 7),
    EnrageCrDamMin(0x20000000, 7),
    Judgement(0x40000000, 7),
    CounterAttack(0x80000000, 7, true),
    
    DojangLuckyBonus(0x1, 8),
    PainMark(0x2, 8),
    Magnet(0x4, 8),
    MagnetArea(0x8, 8),
    VampDeath(0x10, 8),
    DemonAwakening(0x20, 8),
    BlessingArmorIncPAD(0x40, 8),
    Unk_0x80_8(0x80, 8),
    KeyDownAreaMoving(0x100, 8),
    Larkness(0x200, 8),
    StackBuff(0x400, 8),
    BlessOfDarkness(0x800, 8),
    AntiMagicShell(0x1000, 8),
    LifeTidal(0x2000, 8),
    HitCriDamR(0x4000, 8),
    KaiserMorphGauge(0x8000, 8),
    PartyBarrier(0x10000, 8),
    ReshuffleSwitch(0x20000, 8),
    SpecialAction(0x40000, 8),
    VampDeathSummon(0x80000, 8),
    Unk_0x100000_8(0x100000, 8),    // StopForceAtomInfo
    Unk_0x200000_8(0x200000, 8),    // SoulGazeCriDamR
    Unk_0x400000_8(0x400000, 8),    // SoulRageCount
    Unk_0x800000_8(0x800000, 8),
    Unk_0x1000000_8(0x1000000, 8),
    EnergyCharge(0x2000000, 8),
    DashSpeed(0x4000000, 8),
    DashJump(0x8000000, 8),
    MonsterRiding(0x10000000, 8),
    WindBooster(0x20000000, 8),
    GuidedBullet(0x40000000, 8),
    Undead(0x80000000, 8),
    ;

    private static final long serialVersionUID = 0L;
    private final int buffstat;
    private final int first;
    private boolean stacked = false;
    private boolean enhanced = false;

    private CharacterTemporaryStat(int buffstat, int first) {
        this.buffstat = buffstat;
        this.first = first;
    }

    private CharacterTemporaryStat(int buffstat, int first, boolean stacked) {
        this.buffstat = buffstat;
        this.first = first;
        this.stacked = stacked;
    }

    private CharacterTemporaryStat(int buffstat, int first, boolean stacked, boolean enhanced) {
        this.buffstat = buffstat;
        this.first = first;
        this.stacked = stacked;
        this.enhanced = enhanced;
    }

    public final int getPosition() {
        return getPosition(false);
    }

    public final int getPosition(boolean fromZero) {
        if (!fromZero) {
            return first;
        }
        if (first < 1 || first > 8) {
            return 0;
        }
        return (first - 1);
    }

    public final int getValue() {
        return buffstat;
    }

    public final boolean canStack() {
        return stacked;
    }

    public final boolean Enhanced() {
        return enhanced;
    }
}