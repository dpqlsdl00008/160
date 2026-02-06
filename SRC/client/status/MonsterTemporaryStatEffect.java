package client.status;


import client.MapleCharacter;
import java.lang.ref.WeakReference;
import server.life.MapleMonster;
import server.life.MobSkill;

public class MonsterTemporaryStatEffect {

    private MonsterTemporaryStat stati;
    private final int skill;
    private final MobSkill mobskill;
    private final boolean monsterSkill;
    private WeakReference<MapleCharacter> weakChr = null;
    private Integer x;
    private int poisonSchedule = 0;
    private boolean reflect = false;
    private long cancelTime = 0;

    public MonsterTemporaryStatEffect(final MonsterTemporaryStat stat, final Integer x, final int skillId, final MobSkill mobskill, final boolean monsterSkill) {
        this.stati = stat;
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
        this.x = x;
    }

    public MonsterTemporaryStatEffect(final MonsterTemporaryStat stat, final Integer x, final int skillId, final MobSkill mobskill, final boolean monsterSkill, final boolean reflect) {
        this.stati = stat;
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
        this.x = x;
	this.reflect = reflect;
    }

    public final MonsterTemporaryStat getStati() {
        return stati;
    }

    public final Integer getX() {
        return x;
    }

    public final void setValue(final MonsterTemporaryStat status, final Integer newVal) {
        stati = status;
        x = newVal;
    }

    public final int getSkill() {
        return skill;
    }

    public final MobSkill getMobSkill() {
        return mobskill;
    }

    public final boolean isMonsterSkill() {
        return monsterSkill;
    }

    public final void setCancelTask(final long cancelTask) {
        this.cancelTime = System.currentTimeMillis() + cancelTask;
    }

    public final long getCancelTask() {
	return this.cancelTime;
    }
    
    public WeakReference<MapleCharacter> getWeakChr() {
        return weakChr;
    }

    public final void setPoisonSchedule(final int poisonSchedule, MapleCharacter chrr) {
        this.poisonSchedule = poisonSchedule;
	this.weakChr = new WeakReference<MapleCharacter>(chrr);
    }

    public final int getPoisonSchedule() {
	return this.poisonSchedule;
    }

    public final boolean shouldCancel(long now) {
	return (cancelTime > 0 && cancelTime <= now);
    }

    public final void cancelTask() {
	cancelTime = 0;
    }

    public final boolean isReflect() {
        return reflect;
    }

    public final int getFromID() {
	return weakChr == null || weakChr.get() == null ? 0 : weakChr.get().getId();
    }

    public final void cancelPoisonSchedule(MapleMonster mm) {
	mm.doPoison(this, weakChr);
        this.poisonSchedule = 0;
	this.weakChr = null;
    }

    public final static int genericSkill(MonsterTemporaryStat stat) {
	switch(stat) {
	    case Stun:
		return 90001001;
	    case Speed:
		return 90001002;
	    case Poison:
		return 90001003;
	    case Blind:
		return 90001004;
	    case Seal:
		return 90001005;
	    case Frozen:
		return 90001006;
	    case MagicCrash:
		return 1111007;
	    case ShowDown:
		return 4121003;
	    case GhostLettering:
		return 22161002;
	    case ShadowWeb:
		return 4111003;
	    case Venom:
		return 5211004;
	    case Doom:
		return 2311005;
	    case NinjaAmbush:
		return 4121004;

	}
	return 0;
    }
}
