package tools.packet;

import client.MapleCharacter;
import client.MonsterSkill;
import client.Skill;
import client.SkillFactory;
import client.status.MonsterTemporaryStat;
import client.status.MonsterTemporaryStatEffect;
import handling.SendPacketOpcode;
import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import server.MapleStatEffect;
import server.Obstacle;
import server.Randomizer;
import server.life.MapleEnergySphere;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.maps.MapleNodes;
import server.movement.LifeMovementFragment;
import tools.Pair;
import tools.data.OutPacket;

public class CMobPool {

    public static void Init(OutPacket oPacket, MapleMonster monster, int spawnType, int linkMob, boolean newSpawn) {
        oPacket.EncodeShort(monster.getTruePosition().x);
        oPacket.EncodeShort(monster.getTruePosition().y);
        oPacket.EncodeByte(monster.getStance());
        oPacket.EncodeShort(monster.getFh() == 0x46 ? 0x46 : 0);
        oPacket.EncodeShort(monster.getFh());
        oPacket.EncodeByte(newSpawn ? -2 : monster.isFake() ? -4 : spawnType);
        if (spawnType == -3 || spawnType >= 0) {
            oPacket.EncodeInt(linkMob);
        }
        oPacket.EncodeByte(monster.getCarnivalTeam());
        oPacket.EncodeInt((int) monster.getMobMaxHp());
        oPacket.EncodeInt(0);
        if (monster.getStats().isPatrolMob()) {
            oPacket.EncodeInt((int) monster.getTruePosition().getX() - monster.getStats().getRange());
            oPacket.EncodeInt((int) monster.getTruePosition().getX() + monster.getStats().getRange());
            oPacket.EncodeInt(monster.getStats().getDetectX());
            oPacket.EncodeInt(monster.getStats().getSenseX());
        }
    }

    public static byte[] damaged(int oid, long damage) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnDamaged.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeByte(0); // type
        oPacket.EncodeInt(Math.min((int) damage, Integer.MAX_VALUE));
        return oPacket.getPacket();
    }

    public static byte[] damageFriendlyMob(MapleMonster mob, long damage, boolean display) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnDamaged.getValue());
        mplew.writeInt(mob.getObjectId());
        mplew.write(display ? 1 : 2);
        if (damage > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) damage);
        }
        if (mob.getHp() > 2147483647L) {
            mplew.writeInt((int) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
        return mplew.getPacket();
    }

    public static byte[] killMonster(int oid, int animation) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMobLeaveField.getValue());
        mplew.writeInt(oid);
        mplew.write(animation);
        if (animation == 4) {
            mplew.writeInt(-1);
        }
        return mplew.getPacket();
    }

    public static byte[] suckMonster(int oid, int chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMobLeaveField.getValue());
        mplew.writeInt(oid);
        mplew.write(4);
        mplew.writeInt(chr);

        return mplew.getPacket();
    }

    public static byte[] healMonster(int oid, int heal) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnDamaged.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);

        return mplew.getPacket();
    }

    public static byte[] MobToMobDamage(int oid, long dmg, int mobid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMobAttackedByMob.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt((int) dmg);
        mplew.writeInt(mobid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] getMobSkillEffect(int oid, int skillid, int cid, int skilllevel) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnSpecialEffectBySkill.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(skillid);
        mplew.writeInt(cid);
        mplew.writeShort(skilllevel);

        return mplew.getPacket();
    }

    public static byte[] steelskilleffect(int oid, int itemid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnStealEffect.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showMonsterHP(int oid, int remhppercentage) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnHPIndicator.getValue());
        mplew.writeInt(oid);
        if (remhppercentage > 0 && remhppercentage < 100) {
            mplew.write(remhppercentage);
        } else if (remhppercentage == 100) {
            mplew.write(99);
        } else {
            mplew.write(0);
        }
        return mplew.getPacket();
    }

    public static byte[] SmartMobNotice(int type, String message, int color) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.SMART_MOB_NOTICE.getValue());
        mplew.writeInt(color);//0:white, 1:yellow,
        mplew.writeInt(type);//1:attack, 2:skill, 3:change controller, 5:mobzone?
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static byte[] OnMobTimeResist(int objectID, int skillID, int v4, int v5) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMobTimeResist.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(skillID);
        oPacket.EncodeShort(v4);
        oPacket.EncodeInt(v5);
        return oPacket.getPacket();
    }

    public static byte[] showBossHP(MapleMonster mob) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(5);
        mplew.writeInt(mob.getId() == 9400589 ? 9300184 : mob.getId());

        int minor = 100;
        if (mob.getMobMaxHp() > 100000000000L) {
            minor = 1000;
        }
        if (mob.getMobMaxHp() > 1000000000000L) {
            minor = 10000;
        }
        if (mob.getMobMaxHp() > 10000000000000L) {
            minor = 100000;
        }
        if (mob.getMobMaxHp() > 100000000000000L) {
            minor = 1000000;
        }
        if (mob.getMobMaxHp() > 1000000000000000L) {
            minor = 10000000;
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            mplew.writeInt((int) ((mob.getHp() / minor)));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            mplew.writeInt((int) (mob.getMobMaxHp() / minor));
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
        /*
        01 : Red
        02 : Blue
        05 : Gray
        07 : Purple
        08 : PurpleBlack
        09 : Yellow
        10 : Pink
        11 : DarkPurple
        12 : Magenta
        13 : LightPink
        14 : LightGreen
        15 : DarkGreen
        16 : White
        17 : LightBlue
        18 : DarkBlue
        19 : GoldYellow
        20 : DarkPink
         */
        if (mob.getId() == 8880303) {
            mplew.write(mob.getHPPercent() < 33 ? 1 : mob.getMap().getId() == 450008400 ? 2 : 7);
        } else {
            mplew.write(mob.getStats().getTagColor());
        }
        if (mob.getId() == 8880303) {
            if (mob.getHPPercent() < 33) {
                mplew.write(mob.getStats().getTagBgColor());
            } else {
                if (mob.getMap().getId() == 450008400) {
                    mplew.write(7);
                } else {
                    mplew.write(2);
                }
            }
        } else {
            mplew.write(mob.getStats().getTagBgColor());
        }
        return mplew.getPacket();
    }

    public static byte[] showBossHP(int monsterId, long currentHp, long maxHp) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(5);
        mplew.writeInt(monsterId);
        if (currentHp > 2147483647L) {
            mplew.writeInt((int) (currentHp / maxHp * 2147483647.0D));
        } else {
            mplew.writeInt((int) (currentHp <= 0L ? -1L : currentHp));
        }
        if (maxHp > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) maxHp);
        }
        mplew.write(6);
        mplew.write(5);
        return mplew.getPacket();
    }

    public static byte[] moveMonster(boolean useskill, int skill, int skillID, int skillLv, int option, int oid, Point startPos, List<LifeMovementFragment> moves, List<Integer> unk2, List<Pair<Integer, Integer>> unk3) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMove.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeByte(useskill ? 1 : 0);
        oPacket.EncodeByte(skill);
        oPacket.EncodeByte(skillID);
        oPacket.EncodeByte(skillLv);
        oPacket.EncodeShort(option);
        oPacket.EncodeByte(unk3 == null ? 0 : unk3.size());     // v19
        if (unk3 != null) {
            for (Pair i : unk3) {
                oPacket.EncodeShort(((Integer) i.left));        // v24
                oPacket.EncodeShort(((Integer) i.right));       // v27
            }
        }
        oPacket.EncodeByte(unk2 == null ? 0 : unk2.size());     // v30
        if (unk2 != null) {
            for (Integer i : unk2) {
                oPacket.EncodeShort(i);                         // v32
            }
        }
        oPacket.writePos(startPos);
        oPacket.EncodeInt(Randomizer.nextInt());
        PacketHelper.serializeMovementList(oPacket, moves);
        return oPacket.getPacket();
    }

    public static byte[] OnMobEnterField(MapleMonster life, int spawnType, int link) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMobEnterField.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);//어그로
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        setTemporaryStat(mplew, life);
        Init(mplew, life, spawnType, link, false);
        /*
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(life.getFh() == 0x46 ? 0x46 : 0);
        mplew.writeShort(life.getFh());
        mplew.write(spawnType);
        if ((spawnType == -3) || (spawnType >= 0)) {
            mplew.writeInt(link);
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt((int) life.getMobMaxHp());
        mplew.writeInt(0);
        if (life.getStats().isPatrolMob() == true) {
            mplew.writeInt((int) life.getTruePosition().getX() - life.getStats().getRange());
            mplew.writeInt((int) life.getTruePosition().getX() + life.getStats().getRange());
            mplew.writeInt(life.getStats().getDetectX());
            mplew.writeInt(life.getStats().getSenseX());
        }
         */
        return mplew.getPacket();
    }

    public static void addMonsterStatus(OutPacket mplew, MapleMonster life) {
        if (life.getStati().size() < 2) {
            life.addEmpty();
        }
        if (life.getMap().getId() == 957010000) {
            mplew.write(1);
            mplew.writeInt(1000000);
            mplew.writeInt(1000000);
            mplew.writeInt(10000);
            mplew.writeInt(10);
            mplew.writeInt(10);
            mplew.writeInt(10);
            mplew.writeInt(10);
            mplew.writeInt(10);
            mplew.writeInt(10);
            mplew.writeInt(10);
            mplew.writeInt(170);
        } else {
            mplew.write(life.getChangedStats() != null ? 1 : 0);
            if (life.getChangedStats() != null) {
                mplew.writeInt(life.getChangedStats().hp > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) life.getChangedStats().hp);
                mplew.writeInt(life.getChangedStats().mp);
                mplew.writeInt(life.getChangedStats().exp);
                mplew.writeInt(life.getChangedStats().watk);
                mplew.writeInt(life.getChangedStats().matk);
                mplew.writeInt(life.getChangedStats().PDRate);
                mplew.writeInt(life.getChangedStats().MDRate);
                mplew.writeInt(life.getChangedStats().acc);
                mplew.writeInt(life.getChangedStats().eva);
                mplew.writeInt(life.getChangedStats().pushed);
                //mplew.writeInt(life.getChangedStats().speed);//스피드가음
                mplew.writeInt(life.getChangedStats().level);
            }
        }
    }

    private static boolean getTemporaryStat(MonsterTemporaryStatEffect ms) {
        return (ms.getStati() == MonsterTemporaryStat.Pad
                || ms.getStati() == MonsterTemporaryStat.Pdr
                || ms.getStati() == MonsterTemporaryStat.Mad
                || ms.getStati() == MonsterTemporaryStat.Mdr
                || ms.getStati() == MonsterTemporaryStat.Acc
                || ms.getStati() == MonsterTemporaryStat.Eva
                || ms.getStati() == MonsterTemporaryStat.Speed
                || ms.getStati() == MonsterTemporaryStat.Stun
                || ms.getStati() == MonsterTemporaryStat.Frozen
                || ms.getStati() == MonsterTemporaryStat.Poison
                || ms.getStati() == MonsterTemporaryStat.Darkness
                || ms.getStati() == MonsterTemporaryStat.PowerUp
                || ms.getStati() == MonsterTemporaryStat.PGuardUp
                || ms.getStati() == MonsterTemporaryStat.MagicUp
                || ms.getStati() == MonsterTemporaryStat.MGuardUp
                || ms.getStati() == MonsterTemporaryStat.PImmune
                || ms.getStati() == MonsterTemporaryStat.MImmune
                || ms.getStati() == MonsterTemporaryStat.Doom
                || ms.getStati() == MonsterTemporaryStat.ShadowWeb
                || ms.getStati() == MonsterTemporaryStat.HardSkin
                || ms.getStati() == MonsterTemporaryStat.NinjaAmbush
                || ms.getStati() == MonsterTemporaryStat.Venom
                || ms.getStati() == MonsterTemporaryStat.Blind
                || ms.getStati() == MonsterTemporaryStat.SealSkill
                || ms.getStati() == MonsterTemporaryStat.Dazzle
                || ms.getStati() == MonsterTemporaryStat.PCounter
                || ms.getStati() == MonsterTemporaryStat.MCounter
                || ms.getStati() == MonsterTemporaryStat.Invincible
                || ms.getStati() == MonsterTemporaryStat.RiseByToss
                || ms.getStati() == MonsterTemporaryStat.BodyPressure
                || ms.getStati() == MonsterTemporaryStat.GhostLettering
                || ms.getStati() == MonsterTemporaryStat.MonsterBomb
                || ms.getStati() == MonsterTemporaryStat.ShowDown
                || ms.getStati() == MonsterTemporaryStat.MagicCrash
                || ms.getStati() == MonsterTemporaryStat.DamagedElemAttr
                || ms.getStati() == MonsterTemporaryStat.AddDamParty
                || ms.getStati() == MonsterTemporaryStat.RareMonster
                || ms.getStati() == MonsterTemporaryStat.DarkMonster
                || ms.getStati() == MonsterTemporaryStat.MysteryMonster
                || ms.getStati() == MonsterTemporaryStat.HitCriDamR
                || ms.getStati() == MonsterTemporaryStat.Unk_0x2000_2);
    }

    public static void setTemporaryStat(OutPacket mplew, MapleMonster life) {
        Collection<MonsterTemporaryStatEffect> buffs = life.getStati().values();
        getLongMask_NoRef(mplew, buffs, false);
        for (MonsterTemporaryStatEffect ms : life.getStati().values()) {
            if (getTemporaryStat(ms)) {
                mplew.EncodeInt(ms.getX()); // nOption
                mplew.EncodeInt(ms.getSkill()); // rOption
                mplew.EncodeShort((int) ((ms.getCancelTask() - System.currentTimeMillis()) / 500)); // tOption
            }
            if (ms.getStati() == MonsterTemporaryStat.Burn) {
                mplew.write(life.getPoisons().size());
                for (MonsterTemporaryStatEffect ps : life.getPoisons()) {
                    mplew.writeInt(ps.getFromID());
                    mplew.writeInt(ps.getSkill());
                    mplew.writeInt(ps.getX());
                    mplew.writeInt(1000);
                    mplew.writeInt(0);
                    if (ps.getWeakChr() != null && !ps.isMonsterSkill()) {
                        Skill skill = SkillFactory.getSkill(ps.getSkill());
                        MapleStatEffect dotTime = skill.getEffect(ps.getWeakChr().get().getTotalSkillLevel(skill));
                        mplew.writeInt(dotTime.getDOTTime() + ps.getWeakChr().get().getStat().dotTime);
                    } else {
                        mplew.writeInt(5);
                    }
                    mplew.writeInt(0);
                }
            }
        }
        if (life.isBuffed(MonsterTemporaryStat.PCounter)) {
            mplew.writeInt(life.getBuff(MonsterTemporaryStat.PCounter).getX());
        }
        if (life.isBuffed(MonsterTemporaryStat.MCounter)) {
            mplew.writeInt(life.getBuff(MonsterTemporaryStat.MCounter).getX());
        }
        for (Integer ref : life.getReflections()) {
            mplew.writeInt(ref);
        }
        if (life.isBuffed(MonsterTemporaryStat.InvincibleBalog)) {
            mplew.write(life.getBuff(MonsterTemporaryStat.InvincibleBalog).getX());
            mplew.write(1);
        }
        if (life.isBuffed(MonsterTemporaryStat.ExchangeAttack)) {
            mplew.write(1);
        }
        if (life.isBuffed(MonsterTemporaryStat.AddDamParty)) {
            mplew.writeInt(life.getBuff(MonsterTemporaryStat.AddDamParty).getX());
            mplew.writeInt(0);
        }
        if (life.isBuffed(MonsterTemporaryStat.ExtraBuffStat)) {
            byte bool = 0;
            mplew.write(bool);
            if (bool == 1) {
                mplew.writeInt(life.getBuff(MonsterTemporaryStat.ExtraBuffStat).getX());
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
    }

    public static byte[] controlMonster(MapleMonster life) {
        return controlMonster(life, false, false, true);
    }

    public static byte[] controlMonster(MapleMonster life, boolean newSpawn, boolean aggro, boolean stop) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnMobChangeController.getValue());
        mplew.write(aggro ? 2 : stop ? 0 : 1);
        mplew.writeInt(life.getObjectId());
        if (stop) {
            return mplew.getPacket();
        }
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        setTemporaryStat(mplew, life);
        Init(mplew, life, -1, -1, newSpawn);
        /*
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(life.getFh() == 0x46 ? 0x46 : 0);
        mplew.writeShort(life.getFh());
        mplew.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt((int) life.getMobMaxHp());//1
        mplew.writeInt(0);
         */
        return mplew.getPacket();
    }

    public static byte[] stopControllingMonster(int oid) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnMobChangeController.getValue());
        mplew.write(0);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] makeMonsterReal(MapleMonster life) {
        return OnMobEnterField(life, -1, 0);
    }

    public static byte[] makeMonsterFake(MapleMonster life) {
        return OnMobEnterField(life, -4, 0);
    }

    public static byte[] makeMonsterEffect(MapleMonster life, int effect) {
        return OnMobEnterField(life, effect, 0);
    }

    public static byte[] ctrlAck(int monster_object_id, short mob_ctrl_sn, int mp, boolean next_attack_possible, int skill_id, int skill_level, int forced_attack) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnCtrlAck.getValue());
        oPacket.EncodeInt(monster_object_id);
        oPacket.EncodeShort(mob_ctrl_sn);
        oPacket.EncodeByte(next_attack_possible ? 1 : 0);
        oPacket.EncodeShort(mp);
        oPacket.EncodeInt(skill_id); // CRC
        oPacket.EncodeInt(skill_level); // CRC
        oPacket.EncodeInt(forced_attack);
        return oPacket.getPacket();
    }

    private static void getLongMask_NoRef(OutPacket mplew, Collection<MonsterTemporaryStatEffect> ss, boolean ignore_imm) {
        int[] mask = new int[8];
        for (MonsterTemporaryStatEffect statup : ss) {
            if ((statup != null)) {
                mask[(statup.getStati().getPosition() - 1)] |= statup.getStati().getValue();
            }
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static byte[] applyMonsterStatus(int oid, MonsterTemporaryStat mse, int x, MobSkill skil, int effectdelay) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnStatSet.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, mse);
        mplew.writeInt(x);
        mplew.writeShort(skil.getSkillId());
        mplew.writeShort(skil.getSkillLevel());
        mplew.writeShort((int) (skil.getDuration() / 500));////duration
        mplew.writeShort(effectdelay);
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, MonsterTemporaryStatEffect ms, MapleCharacter chr) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnStatSet.getValue());
        mplew.writeInt(mons.getObjectId());
        PacketHelper.writeSingleMask(mplew, ms.getStati());
        mplew.writeInt(ms.getX());
        if (ms.isMonsterSkill()) {
            mplew.writeShort(ms.getMobSkill().getSkillId());
            mplew.writeShort(ms.getMobSkill().getSkillLevel());
        } else if (ms.getSkill() > 0) {
            mplew.writeInt(ms.getSkill());
        }
        mplew.writeShort((int) ((ms.getCancelTask() - System.currentTimeMillis()) / 500.0));
        mplew.writeInt(chr.getId());
        mplew.writeShort(ms.getStati().isEmpty() ? 1 : 0);
        mplew.write(0);
        mplew.write(mons.getTriangulation() + 1);
        mplew.writeZeroBytes(30);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static byte[] MorningStarFall(MapleCharacter chr, int v4, int v5, int v6, int skillId, int v11, byte v13, int v8) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(v4);
        mplew.writeInt(v5);
        mplew.writeInt(v6);
        mplew.writeInt(skillId);
        mplew.writeInt(v11);
        mplew.write(v13);
        mplew.writeInt(v8);
        /*
  v4 = CInPacket::Decode4(a2);
  v5 = CInPacket::Decode4(a2);
  v6 = CInPacket::Decode4(a2);
  v11 = CInPacket::Decode4(a2);
  v7 = CInPacket::Decode4(a2);
  v13 = (unsigned __int8)CInPacket::Decode1(a2);
  v8 = CInPacket::Decode4(v2);
         */
        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, List<MonsterTemporaryStatEffect> mse, int Ticks) {
        if (Ticks <= 0) {
            Ticks = 5;
        }
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnStatSet.getValue());
        mplew.writeInt(mons.getObjectId());
        MonsterTemporaryStatEffect ms = (MonsterTemporaryStatEffect) mse.get(0);
        if (ms.getStati() == MonsterTemporaryStat.Poison) {
            PacketHelper.writeSingleMask(mplew, MonsterTemporaryStat.Burn);
            mplew.write(mse.size());
            for (MonsterTemporaryStatEffect m : mse) {
                mplew.writeInt(m.getFromID());
                if (m.isMonsterSkill()) {
                    mplew.writeShort(m.getMobSkill().getSkillId());
                    mplew.writeShort(m.getMobSkill().getSkillLevel());
                } else if (m.getSkill() > 0) {
                    mplew.writeInt(m.getSkill());
                }
                mplew.writeInt(m.getX());
                mplew.writeInt(1000);//인터벌?
                mplew.writeInt(0);
                mplew.writeInt(Ticks);//버프지속시간
                mplew.writeInt(0);
            }
            mplew.writeShort(300);
            mplew.write(0);
            mplew.write(0);
        } else {
            PacketHelper.writeSingleMask(mplew, ms.getStati());
            mplew.writeInt(ms.getX());
            if (ms.isMonsterSkill()) {
                mplew.writeShort(ms.getMobSkill().getSkillId());
                mplew.writeShort(ms.getMobSkill().getSkillLevel());
            } else if (ms.getSkill() > 0) {
                mplew.writeInt(ms.getSkill());
            }
            mplew.writeShort(0);
            mplew.writeShort(ms.getStati().isEmpty() ? 1 : 0);
            mplew.write(1);
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(int oid, Map<MonsterTemporaryStat, Integer> stati, List<Integer> reflection, MobSkill skil, int effectdelay) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnStatSet.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeMask(mplew, stati.keySet());

        for (Map.Entry mse : stati.entrySet()) {
            mplew.writeInt(((Integer) mse.getValue()));
            mplew.writeShort(skil.getSkillId());
            mplew.writeShort(skil.getSkillLevel());
            mplew.writeShort((int) (skil.getDuration() / 500));
        }

        for (Integer ref : reflection) {
            if (skil.getSkillId() == 143) {
                mplew.writeInt(skil.getX());
            }
            if (skil.getSkillId() == 144) {
                mplew.writeInt(skil.getX());
            }
            if (skil.getSkillId() == 145) {
                mplew.writeInt(skil.getX());
            }
            mplew.writeInt(ref);
        }
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(effectdelay);
        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        mplew.write(size);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] cancelMonsterStatus(int oid, MonsterTemporaryStat stat) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnStatReset.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, stat);
        mplew.write(1);
        mplew.write(2);
        return mplew.getPacket();
    }

    public static byte[] cancelPoison(int oid, MonsterTemporaryStatEffect m) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnStatReset.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, MonsterTemporaryStat.Burn);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(m.getFromID());
        if (m.isMonsterSkill()) {
            mplew.writeShort(m.getMobSkill().getSkillId());
            mplew.writeShort(m.getMobSkill().getSkillLevel());
        } else if (m.getSkill() > 0) {
            mplew.writeInt(m.getSkill());
        }
        mplew.write(3);

        return mplew.getPacket();
    }

    public static byte[] talkMonster(int oid, int itemId, String msg, int time) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnEscortStopSay.getValue());
        mplew.writeInt(oid);
        mplew.writeInt((time * 1000));
        mplew.writeInt(itemId);
        mplew.write(itemId <= 2 ? 0 : 1);
        mplew.write((msg == null) || (msg.length() <= 0) ? 0 : 1);
        if ((msg != null) && (msg.length() > 0)) {
            mplew.writeMapleAsciiString(msg);
            mplew.writeInt(1);
        }

        return mplew.getPacket();
    }

    public static byte[] removeTalkMonster(int oid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnEscortStopSay.getValue());
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static final byte[] escortFullPath(MapleMonster objectid, MapleMap map) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnEscortFullPath.getValue());
        oPacket.EncodeInt(objectid.getObjectId());
        oPacket.EncodeInt(map.getNodes().size());
        oPacket.EncodeInt(objectid.getPosition().x);
        oPacket.EncodeInt(objectid.getPosition().y);
        boolean v1 = false;
        for (MapleNodes.MapleNodeInfo mni : map.getNodes()) {
            oPacket.EncodeInt(mni.x);
            oPacket.EncodeInt(mni.y);
            oPacket.EncodeInt(mni.attr);
            if (mni.attr == 2) {
                oPacket.EncodeInt(500);
                //v1 = true;
            }
        }
        int getCurrentDestIndex = 0;
        if (map.getId() == 921140000) {
            getCurrentDestIndex = 6;
        }
        oPacket.EncodeInt(getCurrentDestIndex);
        oPacket.EncodeByte(0);
        if (v1) {
            oPacket.EncodeInt(500);
        }
        oPacket.EncodeByte(0);

        objectid.setNodePacket(oPacket.getPacket());
        return objectid.getNodePacket();
        //return oPacket.getPacket();
    }

    public static byte[] OnEscortStopEndPermmision(int objectid) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.OnEscortStopEndPermmision.getValue());
        mplew.writeInt(objectid);
        return mplew.getPacket();
    }

    public static byte[] showMagnet(int mobid, boolean success) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnCatchEffect.getValue());
        mplew.writeInt(mobid);
        mplew.write(success ? 1 : 0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] catchMonster(int mobid, int itemid, byte success) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnEffectByItem.getValue());
        mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success);

        return mplew.getPacket();
    }

    public static byte[] CreateObstacle(List<Obstacle> obs) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.CREATE_OBSTACLE.getValue());
        mplew.writeInt(0);
        mplew.writeInt(obs.size());
        mplew.write(0);
        for (Obstacle ob : obs) {
            mplew.write(1);
            mplew.writeInt(ob.getKey());
            mplew.writeInt(Randomizer.nextInt()); // crc
            mplew.writeInt(ob.getOldPosition().x);
            mplew.writeInt(ob.getOldPosition().y);
            mplew.writeInt(ob.getNewPosition().x);
            mplew.writeInt(ob.getNewPosition().y);
            mplew.writeInt(ob.getRange());
            mplew.writeInt(ob.getTrueDamage());
            mplew.writeInt(ob.getDelay());
            mplew.writeInt(ob.getHeight());
            mplew.writeInt(ob.getVperSec());
            mplew.writeInt(ob.getMaxP());
            mplew.writeInt(ob.getLength());
            mplew.writeInt(ob.getAngle());
            mplew.writeInt(ob.getUnk());
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] CreateObstacle(MapleMonster mob, List<Obstacle> obs) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.CREATE_OBSTACLE.getValue());
        mplew.writeInt(0x757ACE38);

        mplew.writeInt(obs.size());
        mplew.write(4);

        mplew.writeInt(mob.getId());

        //포지션
        mplew.writeInt(411);
        mplew.writeInt(-496);
        mplew.writeInt(717);
        mplew.writeInt(0);

        for (Obstacle ob : obs) {
            mplew.write(1);
            mplew.writeInt(ob.getKey());
            mplew.writeInt(Randomizer.nextInt()); // crc
            mplew.writeInt(ob.getOldPosition().x);
            mplew.writeInt(ob.getOldPosition().y);
            mplew.writeInt(ob.getNewPosition().x);
            mplew.writeInt(ob.getNewPosition().y);
            mplew.writeInt(ob.getRange());
            mplew.writeInt(ob.getTrueDamage());
            mplew.writeInt(ob.getDelay());
            mplew.writeInt(ob.getHeight());
            mplew.writeInt(ob.getVperSec());
            mplew.writeInt(ob.getMaxP());
            mplew.writeInt(ob.getLength());
            mplew.writeInt(ob.getAngle());
            mplew.writeInt(ob.getUnk());
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] mobAttackBlock(MapleMonster monster, List<Integer> skillsIDS) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.BLOCK_ATTACK.getValue());
        oPacket.EncodeInt(monster.getObjectId());
        oPacket.EncodeInt(skillsIDS.size());
        for (Integer skillID : skillsIDS) {
            oPacket.EncodeInt(skillID);
        }
        return oPacket.getPacket();
    }

    public static byte[] OnMobTeleport(MapleMonster monster, Point point, int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMobTeleport.getValue());
        oPacket.EncodeInt(monster.getObjectId());
        oPacket.EncodeInt(type);
        switch (type) {
            case 1:
            case 2: {
                break;
            }
            case 3: {
                oPacket.EncodeInt(point.x);
                oPacket.EncodeInt(point.y);
                break;
            }
            case 4: {
                oPacket.EncodeInt(point.x);
                break;
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] createBounceAttackSkill(int objectID, int skill_id, int skill_level, MapleEnergySphere msi, boolean afterConvexSkill) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.ENERGY_SPHERE.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(skill_id); // 217
        oPacket.EncodeInt(skill_level);
        oPacket.EncodeByte(afterConvexSkill);
        if (afterConvexSkill) {
            oPacket.EncodeInt(2);
            oPacket.EncodeByte(msi.isDelayed());
            oPacket.EncodeInt(msi.getY());
            oPacket.EncodeInt(msi.getDensity());
            oPacket.EncodeInt(msi.getFriction());
            oPacket.EncodeInt(msi.getStartDelay());
            oPacket.EncodeInt(msi.getDestroyDelay());
            for (int i = 0; i < 2; i++) {
                oPacket.EncodeInt(msi.getObjectId() + i + 1);
            }
        } else {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(msi.getX());
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(msi.getObjectId());
            oPacket.EncodeInt(20);
            oPacket.EncodeInt(20);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(msi.getRetitution());
            oPacket.EncodeInt(msi.getDestroyDelay());
            oPacket.EncodeInt(msi.getStartDelay());
            oPacket.EncodeByte(msi.isNoGravity());
            oPacket.EncodeByte(msi.isNoDeleteFromOthers());
            if (skill_level == 3 || skill_level == 4 || skill_level == 21) {
                oPacket.EncodeInt(5);
                oPacket.EncodeInt(5);
            }
            if (msi.isNoDeleteFromOthers()) {
                oPacket.EncodeInt(msi.getRetitution());
                oPacket.EncodeInt(200);
                oPacket.EncodeInt(24);
                oPacket.EncodeInt(8);
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] OnSuspendReset(int objectID, int v2) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnSuspendReset.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeByte(v2);
        return oPacket.getPacket();
    }

    public static byte[] OnAffected(int objectID, int v5, int v6) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnAffected.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(v5);
        oPacket.EncodeShort(v6);
        return oPacket.getPacket();
    }

    public static byte[] OnMobSpeaking(int objectID, int nAction) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMobSpeaking.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(nAction);
        oPacket.EncodeInt(0);
        return oPacket.getPacket();
    }

    public static byte[] OnMobFancingLeft(int objectID, int FancingLeft) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMobForcedAttack.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(FancingLeft);
        return oPacket.getPacket();
    }

    public static byte[] OnNextAttack(int objectID, int nAction) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnNextAttack.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(nAction);
        return oPacket.getPacket();
    }

    public static byte[] OnMobSkillDelay(int objectID, int skill_after, int skill_id, int skill_level) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.OnMobSkillDelay.getValue());
        oPacket.EncodeInt(objectID);
        oPacket.EncodeInt(skill_after);
        oPacket.EncodeInt(skill_id);
        oPacket.EncodeInt(skill_level);
        return oPacket.getPacket();
    }

    public static byte[] CAswanPacket_006A57B0(int v3, int mobAggro, int mobID, boolean v5, int a1, int a2) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(358);
        oPacket.EncodeInt(v3);
        oPacket.EncodeByte(mobAggro);
        oPacket.EncodeInt(mobID);
        final MapleMonster m = MapleLifeFactory.getMonster(mobID);
        if (v5) {
            oPacket.EncodeByte(a1);
            if (a1 > 0) {
                addMonsterStatus(oPacket, m);
            }
            setTemporaryStat(oPacket, m);
        } else {
            oPacket.EncodeByte(a2);
            if (a2 > 0) {
                addMonsterStatus(oPacket, m);
            }
            setTemporaryStat(oPacket, m);
            Init(oPacket, m, -1, -1, false);
        }
        return oPacket.getPacket();
    }

    public static byte[] CAswanPacket_006A5190(int v4, int v6, int v14) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(359);
        oPacket.EncodeInt(v4);
        oPacket.EncodeByte(v6);
        if (v6 == 4) {
            oPacket.EncodeInt(v14);
        }
        return oPacket.getPacket();
    }

    public static byte[] CAswanPacket_006A5A40(int v4, int v8, int v5, int v7, int mobID, boolean v9, int v11, int v14) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(360);
        oPacket.EncodeByte(v4);
        oPacket.EncodeByte(v8);
        oPacket.EncodeInt(v5);
        if (v4 > 0) {
            oPacket.EncodeByte(v7);
            if (v8 > 0) {
                oPacket.EncodeInt(mobID);
                if (v9) {
                    final MapleMonster m = MapleLifeFactory.getMonster(mobID);
                    oPacket.EncodeByte(v11);
                    if (v11 > 0) {
                        addMonsterStatus(oPacket, m);
                        setTemporaryStat(oPacket, m);
                    } else {
                        oPacket.EncodeByte(v14);
                        if (v14 > 0) {
                            addMonsterStatus(oPacket, m);
                            setTemporaryStat(oPacket, m);
                            Init(oPacket, m, -1, -1, false);
                        }
                    }
                }
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] CAswanPacket_006A5AD0(int v3, int mobAggro, int mobID, int v11) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(361);
        oPacket.EncodeInt(v3);
        oPacket.EncodeByte(mobAggro);
        oPacket.EncodeInt(mobID);
        oPacket.EncodeByte(v11);
        final MapleMonster m = MapleLifeFactory.getMonster(mobID);
        if (v11 > 0) {
            addMonsterStatus(oPacket, m);
        }
        setTemporaryStat(oPacket, m);
        Init(oPacket, m, -1, -1, false);
        return oPacket.getPacket();
    }
}
