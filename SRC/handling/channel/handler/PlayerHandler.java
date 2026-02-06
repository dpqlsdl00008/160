package handling.channel.handler;

import client.CalcDamage;
import client.CharacterTemporaryStat;
import client.MapleCharacter;

import client.MapleClient;
import client.MapleJob;
import client.MapleQuestStatus;
import client.MapleStat;
import client.MonsterSkill;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.SkillMacro;
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.status.MonsterTemporaryStat;
import client.status.MonsterTemporaryStatEffect;
import constants.GameConstants;
import constants.MapConstants;
import handling.ChatType;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.Randomizer;
import server.Timer;
import server.Timer.BuffTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball.MapleSnowballs;
import server.life.BanishInfo;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import static tools.FileoutputUtil.getDCurrentTime;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.SummonPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuffPacket;
import tools.packet.CCashShop;
import tools.packet.CMobPool;

public class PlayerHandler {

    public static int isFinisher(int skillid) {
        switch (skillid) {
            case 1111003://패닉
                return 2;
            case 1111005://코마
                return 1;
            case 11111002://시그너스패닉
                return 2;
            case 11111003://시그너스코마
                return 1;
        }
        return 0;
    }

    public static void ChangeSkillMacro(LittleEndianAccessor slea, MapleCharacter chr) {
        int num = slea.readByte();

        for (int i = 0; i < num; i++) {
            String name = slea.readMapleAsciiString();
            int shout = slea.readByte();
            int skill1 = slea.readInt();
            int skill2 = slea.readInt();
            int skill3 = slea.readInt();

            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }

    public static final void ChangeKeymap(LittleEndianAccessor slea, MapleCharacter chr) {
        if ((slea.available() > 8L) && (chr != null)) {
            slea.skip(4);
            int numChanges = slea.readInt();
            for (int i = 0; i < numChanges; i++) {
                int key = slea.readInt();
                byte type = slea.readByte();
                int action = slea.readInt();
                if ((type == 1) && (action >= 1000)) {
                    Skill skil = SkillFactory.getSkill(action);
                    if (skil != null
                            && ((!skil.isSkillNeedMasterLevel() && !skil.isBeginnerSkill() && skil.isInvisible() && chr.getSkillLevel(skil) <= 0) || (GameConstants.isLinkedAranSkill(action)) || (action >= 91000000))) {
                        continue;
                    }
                }
                chr.changeKeybinding(key, type, action);
            }
        } else if (chr != null) {
            int type = slea.readInt();
            int data = slea.readInt();
            switch (type) {
                case 1: {
                    if (data > 0) {
                        chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.HP_ITEM)).setCustomData(String.valueOf(data));
                    } else {
                        chr.getQuestRemove(MapleQuest.getInstance(GameConstants.HP_ITEM));
                    }
                    break;
                }
                case 2: {
                    if (data > 0) {
                        chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.MP_ITEM)).setCustomData(String.valueOf(data));
                    } else {
                        chr.getQuestRemove(MapleQuest.getInstance(GameConstants.MP_ITEM));
                    }
                    break;
                }
            }
            chr.updatePetAuto();
            chr.getClient().sendPacket(CWvsContext.enableActions());
        }
    }

    public static final void UseTitle(LittleEndianAccessor slea, int itemId, MapleClient c, MapleCharacter chr) {
        int slot = slea.readInt();
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        Item toSlot = chr.getInventory(MapleInventoryType.SETUP).getItem((short) slot);
        if (toUse == null && toSlot == null) {
            return;
        }

        chr.updateOneInfoQuest(19019, "id", "0");
        chr.getQuestRemove(MapleQuest.getInstance(7290));
        if (itemId > 0) {
            chr.updateOneInfoQuest(19019, "id", itemId + "");
            chr.setQuestAdd(MapleQuest.getInstance(7290), (byte) 1, String.valueOf(itemId));
        }

        chr.setTitle(itemId);
        chr.getMap().broadcastMessage(chr, CField.showTitle(chr.getId(), itemId), false);
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseChair(final int itemId, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            return;
        }
        if (GameConstants.isFishingMap(chr.getMapId()) && itemId == 3011000) {
            chr.startFishingTask();
        }
        chr.setChair(itemId);
        chr.getMap().broadcastMessage(chr, CField.showChair(chr.getId(), itemId), false);
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void CancelChair(short id, MapleClient c, MapleCharacter chr) {
        if (id == -1) {
            if (chr.getChair() == 3010587) {
                for (MapleMist m : chr.getMap().getAllMistsThreadsafe()) {
                    if (m.getOwnerId() == chr.getId() && m.getSourceSkill().getId() == 36121007) {
                        if (m.getSchedule() != null) {
                            m.getSchedule().cancel(false);
                            m.setSchedule(null);
                        }
                        if (m.getPoisonSchedule() != null) {
                            m.getPoisonSchedule().cancel(false);
                            m.setPoisonSchedule(null);
                        }
                        chr.getMap().broadcastMessage(CField.removeMist(m.getObjectId(), true));
                        chr.getMap().removeMapObject(m);
                    }
                }
            }
            chr.setChair(0);
            c.sendPacket(CUserLocal.setChair(false, 0));
            if (chr.getMap() != null) {
                chr.getMap().broadcastMessage(chr, CField.showChair(chr.getId(), 0), false);
            }
        } else {
            chr.setChair(id);
            c.sendPacket(CUserLocal.setChair(true, id));
        }
    }

    public static void TrockAddMap(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte addrem = slea.readByte();
        byte vip = slea.readByte();
        /*vip:
         * 1 = Teleport Rock (original)
         * 2 = VIP?
         * 3 = Premium teleport rock
         * 4 = ??
         * 5 = Hyper Teleport Rock
         */
        if (vip == 1) {
            if (addrem == 0) {
                chr.deleteFromRegRocks(slea.readInt());
            } else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addRegRockMap();
                } else {
                    chr.dropMessage(1, "This map is not available to enter for the list.");
                }
            }
        } else if (vip == 2) {
            if (addrem == 0) {
                chr.deleteFromRocks(slea.readInt());
            } else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addRockMap();
                } else {
                    chr.dropMessage(1, "This map is not available to enter for the list.");
                }
            }
        } else if (vip == (byte) 3 || vip == (byte) 5) {
            if (addrem == 0) {
                chr.deleteFromHyperRocks(slea.readInt());
            }
            if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addHyperRockMap();
                } else {
                    chr.dropMessage(1, "This map is not available to enter for the list.");
                }
            }
        }
        c.getSession().write(CCashShop.OnMapTransferResult(chr, vip, addrem == 0));
    }

    public static final void CharInfoRequest(int objectid, MapleClient c, MapleCharacter chr) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        MapleCharacter player = c.getPlayer().getMap().getCharacterById(objectid);
        c.getSession().write(CWvsContext.enableActions());
        if ((player != null) && ((!player.isGM()) || (c.getPlayer().isGM()))) {
            c.getSession().write(CWvsContext.charInfo(player, c.getPlayer().getId() == objectid));
        }
    }

    public static final void TakeDamage(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(4);
        if (chr == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        byte damageType = slea.readByte();
        byte elementType = slea.readByte();
        int takeDamage = slea.readInt();
        slea.skip(2);
        int objectID = 0;
        int monsterID = 0;
        int dotgeSkill = 0;
        int reduceMpAttack = 0;
        int skillID = 0;
        int pID = 0;
        int pDMG = 0;
        byte pDirection = 0;
        byte pType = 0;
        Point pPos = new Point(0, 0);
        boolean isDeadlyAttack = false;
        boolean isNotMissAttack = false;
        boolean pPhysical = false;
        int attackCount = 1;
        MapleMonster attackMob = null;
        if (chr.isHidden()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (chr.isInvincible()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        PlayerStats userStat = chr.getStat();

        if ((damageType != -2) && (damageType != -3) && (damageType != -4)) {
            monsterID = slea.readInt();
            objectID = slea.readInt();
            attackMob = MapleLifeFactory.getMonster(monsterID);
            //attackMob = chr.getMap().getMonsterByOid(objectID);
            pDirection = slea.readByte();
            if (attackMob == null) {
                return;
            }
            if (attackMob.getId() != monsterID) {
                return;
            }
            if (attackMob.getLinkCID() > 0) {
                return;
            }
            if (attackMob.isFake()) {
                return;
            }
            if (attackMob.getStats().isFriendly()) {
                return;
            }
            if (damageType != -1 && damageType != 0 && takeDamage > 0) {
                final BanishInfo banInfo = attackMob.getStats().getBanishInfo();
                if (banInfo != null) {
                    //chr.changeMapBanish(banInfo.getMap(), banInfo.getPortal(), banInfo.getMsg());
                }
            }
            if (damageType != -1 && takeDamage > 0 && attackMob.getStats().getSelfDHp() < 1) {
                final MobAttackInfo attackInfo = attackMob.getStats().getMobAttack(damageType);
                if (attackInfo != null) {
                    if (attackInfo.isDeadlyAttack()) {
                        isDeadlyAttack = true;
                        reduceMpAttack = userStat.getMp() - 1;
                    } else {
                        reduceMpAttack += attackInfo.getMpBurn();
                    }
                    attackMob.setMp(attackMob.getMp() - attackInfo.getMpCon());
                }
            }
            if (takeDamage > 0 && chr.getMapId() / 10000 == 92502) {
                chr.mulung_EnergyModify(false);
            }
        }
        if (damageType == -3) {
            if (slea.available() > 1) {
                int objectLevel = slea.readByte() & 0xFF;
                int objectSkill = slea.readByte() & 0xFF;
                MonsterSkill objectDebuff = MonsterSkill.getBySkill(objectSkill);
                if (objectLevel > 0 && objectSkill > 0 && objectDebuff != null) {
                    c.getPlayer().giveDebuff(objectDebuff, MobSkillFactory.getMobSkill(objectSkill, objectLevel), (short) 0);
                }
            }
        }

        if (takeDamage == 0) {
            chr.getClient().sendPacket(CUserLocal.dodgeSkillReady());
        }

        if (takeDamage == -1) {
            dotgeSkill = 4020002 + (chr.getJob() / 10 - 40) * 100000;
            if ((dotgeSkill != 4120002) && (dotgeSkill != 4220002)) {
                dotgeSkill = 4120002;
            }
            if ((damageType == -1) && (chr.getJob() == 122) && (attackMob != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10) != null)
                    && (chr.getTotalSkillLevel(1220006) > 0)) {
                MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(chr.getTotalSkillLevel(1220006));
                if (eff.makeChanceResult()) {
                    attackMob.applyStatus(chr, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Stun, Integer.valueOf(1), 1220006, null, false), false, eff.getDuration(), true, eff);
                    dotgeSkill = 1220006;
                }
            }
            if (chr.getJob() > 6000 && chr.getJob() < 6113) {
                final Skill Jinsoku = SkillFactory.getSkill(61120006);
                if (Jinsoku != null) {
                    int JinsokuLevel = chr.getTotalSkillLevel(Jinsoku);
                    if (JinsokuLevel > 0) {
                        final MapleStatEffect eff = Jinsoku.getEffect(JinsokuLevel);
                        if (eff != null) {
                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                                localstatups.put(CharacterTemporaryStat.HowlingDefence, eff.getY());
                                chr.getClient().sendPacket(BuffPacket.giveBuff(61120006, eff.getDuration(), localstatups, null));
                                final long starttime = System.currentTimeMillis();
                                final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(chr, eff, starttime, localstatups);
                                final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, eff.getDuration());
                                chr.registerEffect(eff, starttime, schedule, localstatups, false, eff.getDuration(), chr.getId());
                            }
                        }
                    }
                }
            }
            if (chr.getTotalSkillLevel(dotgeSkill) <= 0) {
                return;
            }
        } else if (takeDamage < -1) {
            //c.getSession().write(CWvsContext.enableActions());
            //return;
        }
        if ((pPhysical) && (skillID == 1201007) && (chr.getTotalSkillLevel(1201007) > 0)) {
            takeDamage -= pDMG;
            if (takeDamage > 0) {
                MapleStatEffect eff = SkillFactory.getSkill(1201007).getEffect(chr.getTotalSkillLevel(1201007));
                long enemyDMG = Math.min(takeDamage * (eff.getY() / 100), attackMob.getMobMaxHp() / 2L);
                if (enemyDMG > pDMG) {
                    enemyDMG = pDMG;
                }
                if (enemyDMG > 1000L) {
                    enemyDMG = 1000L;
                }
                attackMob.damage(chr, enemyDMG, true, 1201007);
            } else {
                takeDamage = 1;
            }
        }
        Pair modifyDamageTaken = chr.modifyDamageTaken(takeDamage, attackMob, !pPhysical);
        takeDamage = ((Double) modifyDamageTaken.left).intValue();

        if (attackMob != null) {
            final MobAttackInfo attackInfo = attackMob.getStats().getMobAttack(damageType);
            if (attackInfo != null) {
                if (attackInfo.getFixDamR() > 0) {
                    takeDamage = ((chr.getStat().getMaxHp() / 100) * attackInfo.getFixDamR());
                    if (attackInfo.getFixDamR() > 99) {
                        takeDamage = 99999;
                    }
                }
                if (attackInfo.getFixDamage() > 0) {
                    takeDamage = attackInfo.getFixDamage();
                }
                if (attackInfo.getAttackCount() > 1) {
                    attackCount = attackInfo.getAttackCount();
                }
                if (attackInfo.getDiseaseSkill() > 0) {
                    MonsterSkill attackDisease = MonsterSkill.getBySkill(attackInfo.getDiseaseSkill());
                    if (attackDisease != null) {
                        if (takeDamage > 0) {
                            MobSkill ms = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                            if (ms != null) {
                                c.getPlayer().giveDebuff(attackDisease, ms, (short) 0);
                            }
                        }
                    }
                }
            }
            if (chr.setPercentDamage(attackMob.getId()) > 0) {
                takeDamage = ((chr.getStat().getMaxHp() / 100) * chr.setPercentDamage(attackMob.getId()));
            }
        }

        if (takeDamage > 0) {
            chr.getCheatTracker().setAttacksWithoutHit(false);
            if (chr.getBuffedValue(CharacterTemporaryStat.Morph) != null) {
                chr.cancelMorphs();
            }
            boolean mpAttack = (chr.getBuffedValue(CharacterTemporaryStat.Mechanic) != null) && (chr.getBuffSource(CharacterTemporaryStat.Mechanic) != 35121005);

            if (chr.getBuffedValue(CharacterTemporaryStat.MagicGuard) != null || chr.getSkillLevel(27000003) > 0 || chr.getSkillLevel(12000024) > 0) {
                int hploss = 0;
                int mploss = 0;
                if (isDeadlyAttack) {
                    if (userStat.getHp() > 1L) {
                        hploss = (int) (userStat.getHp() - 1L);
                    }
                    if (userStat.getMp() > 1L) {
                        mploss = (int) (userStat.getMp() - 1L);
                    }
                    if (chr.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                        mploss = 0;
                    }
                    chr.addMPHP(-hploss, -mploss);
                } else {
                    if (chr.getSkillLevel(27000003) > 0) {
                        Skill skill = SkillFactory.getSkill(27000003);
                        MapleStatEffect eff = skill.getEffect(chr.getSkillLevel(skill));
                        mploss = (int) ((double) takeDamage * ((double) eff.getX() / 100.0)) + reduceMpAttack;
                    } else if (chr.getBuffedValue(CharacterTemporaryStat.MagicGuard) != null) {
                        mploss = (int) ((double) takeDamage * (chr.getBuffedValue(CharacterTemporaryStat.MagicGuard).doubleValue() / 100.0)) + reduceMpAttack;
                    }
                    hploss = takeDamage - mploss;
                    if (chr.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                        mploss = 0;
                    } else if (chr.getSkillLevel(12000024) > 0) {
                        MapleStatEffect eff = SkillFactory.getSkill(12000024).getEffect(chr.getSkillLevel(12000024));
                        mploss = (int) ((double) takeDamage * ((double) eff.getX() / 100.0));
                        hploss = takeDamage - mploss + reduceMpAttack;
                    } else if ((long) mploss > userStat.getMp()) {
                        mploss = (int) userStat.getMp();
                        hploss = takeDamage - mploss + reduceMpAttack;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }
            } else {
                if (chr.getBuffedValue(CharacterTemporaryStat.MesoGuard) != null) {
                    int mesoloss = (int) (takeDamage * (chr.getStat().mesoGuardMeso / 100.0D));
                    takeDamage = (int) (takeDamage * (chr.getBuffedValue(CharacterTemporaryStat.MesoGuard) / 100.0d));
                    if (chr.getMeso() < mesoloss) {
                        chr.gainMeso(-chr.getMeso(), false);
                        chr.cancelBuffStats(true, new CharacterTemporaryStat[]{CharacterTemporaryStat.MesoGuard});
                    } else {
                        chr.gainMeso(-mesoloss, false);
                    }
                    if ((isDeadlyAttack) && (userStat.getMp() > 1)) {
                        reduceMpAttack = userStat.getMp() - 1;
                    }
                    chr.addMPHP(-takeDamage, -reduceMpAttack);
                } else if (chr.getBuffedValue(CharacterTemporaryStat.BlueAura) != null) {
                    if (chr.getBuffSource(CharacterTemporaryStat.BlueAura) == 32110000) {
                        double damageReduce = (10.98 + chr.getBuffedValue(CharacterTemporaryStat.BlueAura).byteValue());
                        chr.addMPHP(-((int) (takeDamage + damageReduce)), mpAttack ? 0 : -reduceMpAttack);
                    }
                } else if (isDeadlyAttack) {
                    int hploss = userStat.getHp() > 1 ? -(userStat.getHp() - 1) : 0;
                    chr.addMPHP(hploss, (userStat.getMp() > 1) && (!mpAttack) ? -(userStat.getMp() - 1) : 0);
                } else {
                    chr.addMPHP(-takeDamage, mpAttack ? 0 : -reduceMpAttack);
                }
                if ((chr.inPVP()) && (chr.getStat().getHPPercent() <= 20)) {
                    chr.getStat();
                    SkillFactory.getSkill(PlayerStats.getSkillByJob(93, chr.getJob())).getEffect(1).applyTo(chr);
                }
            }
        }

        byte offset = 0;
        int offset_d = 0;
        if (slea.available() == 1L) {
            offset = slea.readByte();
            if ((offset == 1) && (slea.available() >= 4L)) {
                offset_d = slea.readInt();
            }
            if ((offset < 0) || (offset > 2)) {
                offset = 0;
            }
        }

        boolean isGuard = false;
        if (slea.available() == 11) {
            slea.skip(8);
            isGuard = slea.readByte() > 0;
            slea.skip(2);
        }

        if (!isGuard && damageType != -4 && takeDamage != -1) {
            chr.getClient().sendPacket(CField.OnDamageByUser(chr.getId(), attackCount, takeDamage));
            chr.getMap().broadcastMessage(chr, CField.OnDamageByUser(chr.getId(), attackCount, takeDamage), chr.getTruePosition());
        } else {
            chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), dotgeSkill, 7, chr.getLevel(), 1), chr.getTruePosition());
        }

        if (attackMob != null) {
            if (attackMob.getId() == 8880315) {
                chr.getMap().killMonster(attackMob, -1);
            }
        }
    }

    public static final void AranCombo(MapleClient c, MapleCharacter chr, int toAdd) {
        if ((chr != null) && (chr.getJob() >= 2000) && (chr.getJob() <= 2112)) {
            short combo = chr.getCombo();
            long curr = System.currentTimeMillis();
            if ((combo > 0) && (curr - chr.getLastCombo() > 7000L)) {
                combo = 0;
            }
            combo = (short) Math.min(30000, combo + toAdd);
            chr.setLastCombo(curr);
            chr.setCombo(combo);
            c.sendPacket(CUserLocal.modComboResponse(combo));
            switch (combo) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 70:
                case 80:
                case 90:
                case 100:
                    if (chr.getSkillLevel(21000000) < combo / 10) {
                        break;
                    }
                    SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
            }
        }
    }

    public static void BlessOfDarkness(MapleCharacter chr) {
        if (chr.getJob() >= 2710 && chr.getJob() <= 2712) {
            if (chr.currentFTC() < 3) {
                chr.setFTC((byte) (chr.currentFTC() + 1));
                SkillFactory.getSkill(27100003).getEffect(chr.getSkillLevel(27100003)).applyTo(chr);
            }
        }
    }

    public static final void HandleShowItemEffect(int itemId, MapleClient c, MapleCharacter chr) {
        Item toUse = chr.getInventory((itemId == 4290001) || (itemId == 4290000) ? MapleInventoryType.ETC : MapleInventoryType.CASH).findById(itemId);
        if (itemId == 0) {
            chr.setItemEffect(itemId);
            chr.getMap().broadcastMessage(chr, CField.itemEffect(chr.getId(), itemId), false);
        }
        if ((toUse == null) || (toUse.getItemId() != itemId) || (toUse.getQuantity() < 1)) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        chr.getMap().broadcastMessage(chr, CField.itemEffect(chr.getId(), itemId), false);
    }

    public static void WheelOfFortuneEffect(int itemId, MapleCharacter chr) {
        switch (itemId) {
            case 5510000: {
                chr.getMap().broadcastMessage(chr, CField.showSpecialEffect(chr.getId(), itemId), false);
                break;
            }
            default:
                break;
        }
    }

    public static final void CancelItemEffect(int id, MapleCharacter chr) {
        if (id <= -2257000 && id >= -2257999) {
            return;
        }
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), -1L);
    }

    public static final void CancelBuffHandler(int sourceid, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
            if (sourceid == 35101009 || sourceid == 35001001 || sourceid == 13111020) {
                chr.cancelEffect(skill.getEffect(1), -1L);
            }
        } else {
            if (sourceid == 35001002) {
                if (chr.getBuffSource(CharacterTemporaryStat.MonsterRiding) == 35120000) {
                    skill = SkillFactory.getSkill(35120000);
                }
            }
            chr.cancelEffect(skill.getEffect(1), -1L);
        }
    }

    public static final void CancelMech(LittleEndianAccessor slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        int sourceid = slea.readInt();
        int skillLevel = slea.readByte();
        int effectId = slea.readByte();
        if (sourceid != 35100004 && (SkillFactory.getSkill(sourceid) == null)) {
            sourceid += 1000;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0);
        }
        chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
        chr.cancelEffect(skill.getEffect(effectId), -1);
        switch (sourceid) {
            case 35121013: {
                SkillFactory.getSkill(35121005).getEffect(chr.getTotalSkillLevel(35121005)).applyTo(chr);
                break;
            }
            default: {
                chr.cancelEffect(skill.getEffect(skillLevel), true, -1L);
                sourceid -= 1000;
                chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sourceid, 1, skillLevel, skillLevel, (byte) 1));
                chr.getMap().broadcastMessage(EffectPacket.showBuffeffect(chr.getId(), sourceid, 1, skillLevel, skillLevel, (byte) 1));
                if (sourceid == 35110004) {
                    chr.cancelBuffStats(false, CharacterTemporaryStat.Mechanic);
                    chr.getClient().getSession().write(CWvsContext.giveMisileToHeavy(0));
                }
                break;
            }
        }
    }

    public static final void QuickSlot(LittleEndianAccessor slea, MapleCharacter chr) {
        if ((slea.available() == 32L) && (chr != null)) {
            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                ret.append(slea.readInt()).append(",");
            }
            ret.deleteCharAt(ret.length() - 1);
            chr.getQuestNAdd(MapleQuest.getInstance(123000)).setCustomData(ret.toString());
        }
    }

    public static final void HandleSkillPrepare(LittleEndianAccessor slea, MapleCharacter chr) {
        int v4 = slea.readInt();
        byte a2 = slea.readByte();
        short v10 = slea.readShort();
        int v11 = (v10 >> 15) & 1;
        int v18 = v10 & 32767;
        byte v12 = slea.readByte();

        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(v4));
        MapleStatEffect effect = chr.inPVP() ? skill.getPVPEffect(a2) : skill.getEffect(a2);

        if (chr.getBuffedValue(CharacterTemporaryStat.KeyDownTimeIgnore) != null) {
            chr.cancelBuffStats(false, CharacterTemporaryStat.KeyDownTimeIgnore);
        }

        if (v4 == 33101005 || skill.isChargeSkill() || v4 == 35001001 || v4 == 35101009 || v4 == 13111020 || v4 == 27111100 || v4 == 35100008) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            if (v18 != 32767) {
            }
            if (v4 == 33101005) {
                chr.setLinkMid(slea.readInt(), 0);
            }
            effect.applyTo(chr);
            chr.getMap().broadcastMessage(chr, CField.OnSkillPrepare(chr, v4, a2, v10, v12), false);
        }
    }

    public static final void SpecialMove(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.hasBlockedInventory()) || (chr.getMap() == null) || (slea.available() < 9L)) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        slea.skip(4);
        int skillid = slea.readInt();
        if (skillid == 5211011 || skillid == 5211015 || skillid == 5211016) {
            chr.dispelSummons();
            int mobToSummon = Randomizer.nextInt(3); //0 for muirhat, 1 for valerie, 2 for jack
            if (mobToSummon == 1) {
                skillid = 5211015;
            } else if (mobToSummon == 2) {
                skillid = 5211016;
            } else {
                skillid = 5211011;
            }
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.showBuffeffect(c.getPlayer().getId(), 5211011, 1, c.getPlayer().getLevel(), 1), chr.getTruePosition());
        }
        if (skillid == 5121052 && (chr.getBuffedValue(CharacterTemporaryStat.EnergyCharge) != null)) {
            chr.cancelEffectFromBuffStat(CharacterTemporaryStat.EnergyCharge);
        }
        if (skillid >= 91000000) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (skillid == 23111008) {
            skillid += Randomizer.nextInt(3);
        }
        int skillLevel = slea.readByte();
        Skill skill = SkillFactory.getSkill(skillid);
        if ((skill == null) || ((GameConstants.isAngel(skillid)) && (chr.getStat().equippedSummon % 10000 != skillid % 10000)) || ((chr.inPVP()) && (skill.isPVPDisabled()))) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int levelCheckSkill = 0;
        if ((GameConstants.isPhantom(chr.getJob())) && (!MapleJob.getById(skillid / 10000).isPhantom())) {
            int skillJob = skillid / 10000;
            if (skillJob % 100 == 0) {
                levelCheckSkill = 24001001;
            } else if (skillJob % 10 == 0) {
                levelCheckSkill = 24101001;
            } else if (skillJob % 10 == 1) {
                levelCheckSkill = 24111001;
            } else {
                levelCheckSkill = 24121001;
            }
        }
        if ((levelCheckSkill == 0)) {
            if ((!GameConstants.isMulungSkill(skillid)) && (!GameConstants.isPyramidSkill(skillid)) && (GameConstants.isLinkedAranSkill(skillid))) {
                if (chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0) {
                    c.getSession().close();
                    return;
                }
            }
            if (GameConstants.isMulungSkill(skillid)) {
                if (chr.getMapId() / 10000 != 92502) {
                    //return;
                }
                if (chr.getMulungEnergy() < 10000) {
                    //return;
                }
                chr.mulung_EnergyModify(false);
            } else if ((GameConstants.isPyramidSkill(skillid)) && (chr.getMapId() / 10000 != 92602) && (chr.getMapId() / 10000 != 92601)) {
                return;
            }
        }
        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!chr.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "You may not use that here.");
                            return;
                        }
                    }
                }
            }
        }
        skillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid));
        MapleStatEffect effect = chr.inPVP() ? skill.getPVPEffect(skillLevel) : skill.getEffect(skillLevel);
        if ((effect.isMPRecovery()) && (chr.getStat().getHp() < chr.getStat().getMaxHp() / 100 * 10)) {
            c.getPlayer().dropMessage(5, "You do not have the HP to use this skill.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (effect.getCooldown(chr) > 0 && !chr.isGM() && skillid != 35111002) {
            if (chr.skillisCooling(skillid)) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            c.sendPacket(CUserLocal.skillCooltimeSet(skillid, effect.getCooldown(chr)));
            chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
        }
        int mobID;
        MapleMonster mob;
        switch (skillid) {
            case 4211016:    // 메소 익스플로젼
            case 12121001: { // 도트 퍼니셔
                chr.handleAtomAttack();
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case 35101004:
            case 60001218: {
                chr.getMap().broadcastMessage(EffectPacket.showBuffeffect(chr.getId(), skillid, 1, skillLevel, skillLevel, (byte) 1));
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case 4331006: {
                int v1 = slea.readInt();// 몹카운트?
                mobID = v1 > 0 ? slea.readInt() : 0;
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.showDualBlade(c.getPlayer().getId(), skillid, 1, c.getPlayer().getLevel(), skillLevel, (byte) 0, mobID), chr.getTruePosition());
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case 61001002: {
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case 22171054: {
                EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.DamR, 30);
                c.getSession().write(BuffPacket.giveBuff(skillid, 30000, localstatups, effect));
                final long starttime = System.currentTimeMillis();
                final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(chr, effect, starttime, localstatups);
                final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, 30000);
                chr.registerEffect(effect, starttime, schedule, chr.getId());
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020:
            case 9101020:
            case 31111003:
                byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; i++) {
                    int mobId = slea.readInt();
                    mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob == null) {
                        continue;
                    }
                    mob.switchController(chr, mob.isControllerHasAggro());
                    mob.applyStatus(chr, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Stun, Integer.valueOf(1), skillid, null, false), false, effect.getDuration(), true, effect);
                }
                chr.getMap().broadcastMessage(chr, CField.EffectPacket.showBuffeffect(chr.getId(), skillid, 1, chr.getLevel(), skillLevel, slea.readByte()), chr.getTruePosition());
                if (skillid == 31111003) {
                    c.getSession().write(CField.EffectPacket.showOwnBuffEffect(skillid, 2, chr.getLevel(), skillLevel));
                }
                c.getSession().write(CWvsContext.enableActions());
                break;
            case 30001061:
                mobID = slea.readInt();
                mob = chr.getMap().getMonsterByOid(mobID);
                if (mob != null) {
                    boolean success = mob.getHp() <= mob.getMobMaxHp() / 2;
                    chr.getMap().broadcastMessage(chr, CField.EffectPacket.showBuffeffect(chr.getId(), skillid, 1, chr.getLevel(), skillLevel, (byte) (success ? 1 : 0)), chr.getTruePosition());
                    if (success) {
                        if ((mob.getId() >= 9304000) && (mob.getId() < 9305000)) {
                            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR)).setCustomData(String.valueOf((mob.getId() - 9303999) * 10));
                            final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.JAGUAR));
                            c.getPlayer().updateQuest(stats, true);
                        } else {
                            if (chr.getIntNoRecord(111113) == 0) {
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR_2)).setCustomData(String.valueOf(mob.getId()));
                                final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.JAGUAR_2));
                                c.getPlayer().updateQuest(stats, true);
                            } else if (chr.getIntNoRecord(111114) == 0) {
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR_3)).setCustomData(String.valueOf(mob.getId()));
                                final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.JAGUAR_3));
                                c.getPlayer().updateQuest(stats, true);
                            } else if (chr.getIntNoRecord(111115) == 0) {
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR_4)).setCustomData(String.valueOf(mob.getId()));
                                final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.JAGUAR_4));
                                c.getPlayer().updateQuest(stats, true);
                            } else if (chr.getIntNoRecord(111116) == 0) {
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR_5)).setCustomData(String.valueOf(mob.getId()));
                                final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.JAGUAR_5));
                                c.getPlayer().updateQuest(stats, true);
                            } else if (chr.getIntNoRecord(111117) == 0) {
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR_6)).setCustomData(String.valueOf(mob.getId()));
                                final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.JAGUAR_6));
                                c.getPlayer().updateQuest(stats, true);
                            } else {
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR_7)).setCustomData(String.valueOf(chr.getIntNoRecord(GameConstants.JAGUAR_7) + 1));
                                chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR + (chr.getIntNoRecord(GameConstants.JAGUAR_7) % 5))).setCustomData(String.valueOf((mob.getId())));
                            }
                        }
                        chr.getMap().killMonster(mob, chr, true, false, (byte) 1);
                        c.getSession().write(CWvsContext.updateJaguar(chr));
                        chr.saveToDB(false, false);
                    } else {
                        chr.dropMessage(5, "몬스터의 체력이 너무많아 포획할 수 없습니다.");
                    }
                }
                c.getSession().write(CWvsContext.enableActions());
                break;
            case 30001062:
                slea.readInt();//멀랑
                Point posi = slea.readPos();
                slea.readByte();//왼쪽보는지 오른쪽보는지 체크
                List<Integer> checkCaughtMob = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (chr.getIntNoRecord(111113 + i) != 0) {
                        checkCaughtMob.add(chr.getIntNoRecord(111113 + i));
                    }
                }
                int dd = chr.getIntNoRecord(111113 + Randomizer.nextInt(checkCaughtMob.size()));
                mob = MapleLifeFactory.getMonster(dd);
                mob.setPosition(posi);
                mob.setStance(2);
                mob.getStats().setExp(0);
                mob.setStat(new MonsterTemporaryStatEffect(MonsterTemporaryStat.Dazzle, chr.getId(), 30001062, null, false), 20000);
                chr.getMap().spawnMonsterOnGroundBelow(mob, posi);
                c.getSession().write(CWvsContext.enableActions());
                break;
            case 33101005: //jaguar oshi
                mobID = chr.getFirstLinkMid();
                mob = chr.getMap().getMonsterByOid(mobID);
                chr.setKeyDownSkill_Time(0);
                chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, skillid), false);
                if (mob != null) {
                    boolean success = mob.getStats().getLevel() < chr.getLevel() && mob.getId() < 9000000 && !mob.getStats().isBoss();
                    if (success) {
                        chr.getMap().broadcastMessage(CMobPool.suckMonster(mob.getObjectId(), chr.getId()));
                        chr.getMap().killMonster(mob, chr, false, false, (byte) -1);
                    } else {
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                } else {
                    chr.dropMessage(5, "No monster was sucked. The skill failed.");
                }
                c.getSession().write(CWvsContext.enableActions());
                break;
            case 2311001:
                slea.skip(4);//좌표
                slea.skip(4);//왜있는건지는모르겠는데 80언노운바이트 58 02(600)딜레이? 00모르겠음
                byte mobcount = slea.readByte();
                for (int i = 0; i < mobcount; i++) {
                    int mobId = slea.readInt();
                    mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob.getStatiSize() > 0) {
                        mob.cancelStatus(MonsterTemporaryStat.PGuardUp);
                        mob.cancelStatus(MonsterTemporaryStat.MGuardUp);
                        mob.cancelStatus(MonsterTemporaryStat.PowerUp);
                        mob.cancelStatus(MonsterTemporaryStat.MagicUp);
                        mob.cancelStatus(MonsterTemporaryStat.PImmune);
                        mob.cancelStatus(MonsterTemporaryStat.MImmune);
                    }
                }
                chr.dispelDebuff(MonsterSkill.Curse);
                chr.dispelDebuff(MonsterSkill.Darkness);
                chr.dispelDebuff(MonsterSkill.Poison);
                chr.dispelDebuff(MonsterSkill.Seal);
                chr.dispelDebuff(MonsterSkill.Slow);
                chr.dispelDebuff(MonsterSkill.Weakness);
                c.getSession().write(CWvsContext.enableActions());
                break;
            case 24121007: {
                slea.skip(4);
                byte mobcountPhantom = slea.readByte();
                byte buffbyte = 1;
                boolean defenseup = false;
                boolean attackup = false;
                boolean immunityup = false;
                boolean reflect = false;
                for (int i = 0; i < mobcountPhantom; i++) {
                    int mobId = slea.readInt();
                    mob = chr.getMap().getMonsterByOid(mobId);
                    if (!defenseup) {
                        defenseup = mob.isBuffed(MonsterTemporaryStat.PGuardUp) || mob.isBuffed(MonsterTemporaryStat.MGuardUp);
                    }
                    if (!attackup) {
                        attackup = mob.isBuffed(MonsterTemporaryStat.PowerUp) || mob.isBuffed(MonsterTemporaryStat.MagicUp);
                    }
                    if (!immunityup) {
                        immunityup = mob.isBuffed(MonsterTemporaryStat.PImmune) || mob.isBuffed(MonsterTemporaryStat.MImmune);
                    }
                    if (!reflect) {
                        reflect = mob.isBuffed(MonsterTemporaryStat.PCounter) || mob.isBuffed(MonsterTemporaryStat.MCounter);
                    }
                    if (mob.getStatiSize() > 0) {
                        mob.cancelStatus(MonsterTemporaryStat.PGuardUp);
                        mob.cancelStatus(MonsterTemporaryStat.MGuardUp);
                        mob.cancelStatus(MonsterTemporaryStat.PowerUp);
                        mob.cancelStatus(MonsterTemporaryStat.MagicUp);
                        mob.cancelStatus(MonsterTemporaryStat.PImmune);
                        mob.cancelStatus(MonsterTemporaryStat.MImmune);
                        mob.cancelStatus(MonsterTemporaryStat.PCounter);
                        mob.cancelStatus(MonsterTemporaryStat.MCounter);
                    }
                }
                if (defenseup || attackup || immunityup || reflect) {
                    EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                    if (defenseup && !attackup && !immunityup && !reflect) {
                        buffbyte = 3;
                        localstatups.put(CharacterTemporaryStat.DamAbsorbShield, skillLevel * 2);
                    } else if (attackup && !immunityup && !reflect) {
                        buffbyte = 4;
                        localstatups.put(CharacterTemporaryStat.EnhancedPad, skillLevel * 2);
                    } else if (immunityup && !reflect) {
                        buffbyte = 2;
                        localstatups.put(CharacterTemporaryStat.NotDamaged, skillLevel * 2);
                    } else if (reflect) {
                        buffbyte = 1;
                        localstatups.put(CharacterTemporaryStat.PowerGuard, skillLevel * 30);
                    }
                    int duration = (5 + (skillLevel / 4)) * 1000;
                    c.getSession().write(BuffPacket.soulSteel(skillid, duration, buffbyte, localstatups));
                    c.getSession().write(CField.EffectPacket.ShowOwnSoulSteelEffect(buffbyte, skillid, skillLevel));
                    chr.getMap().broadcastMessage(CField.EffectPacket.ShowSoulSteelEffect(chr.getId(), buffbyte, skillid, skillLevel));
                    final long starttime = System.currentTimeMillis();
                    final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(chr, effect, starttime, localstatups);
                    final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, duration);
                    chr.registerEffect(effect, starttime, schedule, chr.getId());
                }
                c.getSession().write(CWvsContext.enableActions());
                /*
                short posX = slea.readShort();
                short posY = slea.readShort();
                byte mobCountFromBuff = slea.readByte();
                int monsterObjectId = slea.readInt();
                short unk1 = slea.readShort();
                byte unk2 = slea.readByte();

                MapleMonster monster = chr.getMap().getMonsterByOid(monsterObjectId);
                Skill fSkill = SkillFactory.getSkill(skillid);
                MapleStatEffect eSkill = fSkill.getEffect(skillLevel);

                int epad = eSkill.getEnhancedWatk();
                int x = eSkill.getX();
                int y = eSkill.getY();
                int z = 1;

                EnumMap<MapleBuffStat, Integer> localstatups = new EnumMap<>(MapleBuffStat.class);

                if (monster == null) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }

                if (mobCountFromBuff < 1) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                
                for (int i = 0; i < mobCountFromBuff; i++) {
                    
                }

                if (monster.isBuffed(MonsterStatus.WEAPON_DEFENSE_UP) == true || monster.isBuffed(MonsterStatus.MAGIC_DEFENSE_UP) == true) {
                    localstatups.put(MapleBuffStat.WATER_SHIELD, x);
                }

                localstatups.put(MapleBuffStat.WATER_SHIELD, x);
                c.sendPacket(BuffPacket.soulSteel(skillid, 10 * unk1, 1, localstatups));
                chr.dropMessage(6, "[1] soulSteel : " + x);
                effect.applyTo(c.getPlayer(), null);
                c.getSession().write(CWvsContext.enableActions());
                 */
                break;
            }
            case 4341003:
                chr.setKeyDownSkill_Time(0L);
                chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, skillid), false);
                break;
            case 35111004:
                if (chr.getBuffSource(CharacterTemporaryStat.Mechanic) == 35121005) {
                    effect.setSourceId(35121013);
                } else {
                    effect.setSourceId(35111004);
                }
                effect.applyTo(c.getPlayer(), null);
                break;
            /*
            case 35121013:
            case 35111004: {
                if (chr.getBuffSource(CharacterTemporaryStat.Mechanic) == 35121005) {
                    if (!chr.isHidden()) {
                        effect.setSourceId(35121013);
                        EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                        stat.put(CharacterTemporaryStat.Mechanic, 1);
                        chr.getMap().broadcastMessage(chr, BuffPacket.giveForeignBuff(chr.getId(), stat, effect), false);
                    }
                    chr.getClient().getSession().write(CWvsContext.giveMisileToHeavy(35121013));
                    c.getSession().write(CWvsContext.enableActions());
                    //effect.setSourceId(35121013);
                    //effect.applyTo(c.getPlayer(), null);
                } else {
                    if (!chr.isHidden()) {
                        effect.setSourceId(35111004);
                        EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                        stat.put(CharacterTemporaryStat.Mechanic, 1);
                        chr.getMap().broadcastMessage(chr, BuffPacket.giveForeignBuff(chr.getId(), stat, effect), false);
                    }
                    chr.getClient().getSession().write(CWvsContext.giveMisileToHeavy(35111004));
                    c.getSession().write(CWvsContext.enableActions());
                    //effect.setSourceId(35111004);
                    //effect.applyTo(c.getPlayer(), null);
                }
                break;
            }
             */
            case 22161003: {
                int x = slea.readShort();
                int y = slea.readShort();
                int z = slea.readByte();
                effect.applyTo(c.getPlayer(), new Point(x, y));
                break;
            }
            default:

                boolean voice = false;
                int voiceID = 1;
                switch (skillid) {
                    case 62111003: {
                        voice = true;
                        voiceID = 4;
                        break;
                    }
                    case 62111004: {
                        voice = true;
                        voiceID = 5;
                        break;
                    }
                    case 62121005: {
                        voice = true;
                        voiceID = 8;
                        break;
                    }
                }
                if (voice) {
                    String gender = (chr.getGender() == 0 ? "M" : "F");
                    chr.getMap().broadcastMessage(CField.playSound("kanna/" + gender + "" + voiceID + ""));
                }

                boolean voice_02 = false;
                int voiceID_02 = 1;
                switch (skillid) {
                    case 65101008: {
                        voice_02 = true;
                        voiceID_02 = 5;
                        break;
                    }
                    case 65121009: {
                        voice_02 = true;
                        voiceID_02 = 10;
                        break;
                    }
                    case 65121010: {
                        voice_02 = true;
                        voiceID_02 = 11;
                        break;
                    }
                }
                if (voice_02) {
                    String gender = (chr.getGender() == 0 ? "M" : "F");
                    chr.getMap().broadcastMessage(CField.playSound("mukhyun/" + gender + "" + voiceID_02 + ""));
                }

                boolean voice_04 = false;
                int voiceID_04 = 1;
                switch (skillid) {
                    case 52001001: {
                        voice_04 = true;
                        voiceID_04 = 1;
                        break;
                    }
                    case 52111005: {
                        voice_04 = true;
                        voiceID_04 = 3;
                        break;
                    }
                    case 52111008: {
                        voice_04 = true;
                        voiceID_04 = 4;
                        break;
                    }
                    case 52121005: {
                        voice_04 = true;
                        voiceID_04 = 7;
                        break;
                    }
                }
                if (voice_04) {
                    String gender = (chr.getGender() == 0 ? "M" : "F");
                    chr.getMap().broadcastMessage(CField.playSound("lynn/" + gender + "" + voiceID_04 + ""));
                }
                if (skillid == 9001004) {
                    if (!chr.isHidden()) {
                        final CharacterTemporaryStat cts = CharacterTemporaryStat.DarkSight;
                        final MapleStatEffect mse = SkillFactory.getSkill(skillid).getEffect(1);
                        int duration = Integer.MAX_VALUE;
                        chr.setTemporaryStat(cts, 1, mse, skillid, duration);
                        chr.getMap().broadcastMessage(chr, CField.removePlayerFromMap(chr.getId()), false);
                    } else {
                        chr.dispelSkill(9001004);
                    }
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                Point pos = null;
                if ((slea.available() == 5) || (slea.available() == 7)) {
                    pos = slea.readPos();
                }
                /*
                slea.readShort();
                boolean byPet = slea.readByte() != 0;
                if (byPet) {
                    if (chr.getBuffSource(MapleBuffStat.SPIRIT_CLAW) == 4111009 ||
                        chr.getBuffSource(MapleBuffStat.SPIRIT_CLAW) == 14111007 ||
                        chr.getBuffSource(MapleBuffStat.SPEED) == 4001005 ||
                        chr.getBuffSource(MapleBuffStat.SPEED) == 4301003 ||
                        chr.getBuffSource(MapleBuffStat.SPIRIT_CLAW) == 5201008) {
                        c.sendPacket(CWvsContext.enableActions());
                        return;
                    }
                }
                 */
                if (effect.isMagicDoor()) {
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(c.getPlayer(), pos);
                    } else {
                        c.getSession().write(CWvsContext.enableActions());
                    }
                } else {
                    int mountid = MapleStatEffect.parseMountInfo(c.getPlayer(), skill.getId());
                    if ((mountid != 0) && (mountid != GameConstants.getMountItem(skill.getId(), c.getPlayer())) && (!c.getPlayer().isIntern()) && (c.getPlayer().getBuffedValue(CharacterTemporaryStat.MonsterRiding) == null)
                            && (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118) == null) && (!GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob()))) {
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                    effect.applyTo(c.getPlayer(), pos);
                }
        }
    }

    public static final void tryDoingMeleeAttack(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean energy) {
        if (chr.hasBlockedInventory() || chr.getMap() == null) {
            return;
        }
        AttackInfo attack = DamageParse.parseDmgM(slea, chr);
        if (attack == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (attack.skill == 1092 && attack.targets == 0) {
            return;
        }
        final boolean mirror = chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner) != null;
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        final Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        int attackCount = (shield != null && shield.getItemId() / 10000 == 134 ? 2 : 1);
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            if (skill == null || (GameConstants.isAngel(attack.skill) && (chr.getStat().equippedSummon % 10000) != (attack.skill % 10000))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    final MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                    if (e.isRunning() && !chr.isGM()) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "You may not use that here.");
                                return; //non-skill cannot use
                            }
                        }
                    }
                }
            }
            if (skill.getId() == 31011000 || skill.getId() == 31201000 || skill.getId() == 31211000 || skill.getId() == 31221000) {
                SkillFactory.getSkill(skill.getId()).getEffect(chr.getSkillLevel(skill.getId())).applyBuffEffect(chr, chr, false, 15000);
            }
            //Additional check to see if Advanced Dark Sight is active, if so, apply the additional DamageIncreases provided by PlayerStats->HandlePassiveSkills()
            if ((attack.skill / 10000 == 433) || (attack.skill / 10000 == 434)) {
                if (chr.getBuffedValue(CharacterTemporaryStat.DarkSight) != null) {
                    maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0;
                } else {
                    maxdamage *= (effect.getDamage()) / 100.0;
                }
            } else {
                maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0;
            }
            attackCount = effect.getAttackCount();
            boolean noCooldown = false;
            if (effect.getCooldown(chr) > 0 && !energy && !chr.isGM()) {
                if (attack.skill == 1311012) {
                    if (chr.getBuffedValue(CharacterTemporaryStat.IgnoreTargetDEF) != null) {
                        if (chr.getBuffSource(CharacterTemporaryStat.IgnoreTargetDEF) == 1321015) {
                            noCooldown = true;
                        }
                    }
                }
                if (!noCooldown) {
                    c.sendPacket(CUserLocal.skillCooltimeSet((attack.skill == 15120003 ? 15111022 : attack.skill), effect.getCooldown(chr)));
                    chr.addCooldown((attack.skill == 15120003 ? 15111022 : attack.skill), System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
                }
            }
        }
        //attack = DamageParse.Modify_AttackCrit(attack, chr, 1, effect);
        //DamageParse.critModify(chr, attack);
        //if (attack.skill != 4211006) {
        //chr.getCalcDamage().PDamage(chr, attack);
        //}
        if (mirror == true) {
            attackCount *= 2;
        }

        if (!energy) {
            if ((chr.getMapId() == 109060000 || chr.getMapId() == 109060002 || chr.getMapId() == 109060004) && attack.skill == 0) {
                MapleSnowballs.hitSnowball(chr);
            }
            // handle combo orbconsume
            int numFinisherOrbs = 0;
            final Integer comboBuff = chr.getBuffedValue(CharacterTemporaryStat.ComboCounter);

            if (isFinisher(attack.skill) > 0) { // finisher
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff - 1;
                }
                if (numFinisherOrbs <= 0) {

                    return;
                }
                chr.handleOrbconsume(isFinisher(attack.skill));
            }
        }
        chr.checkFollow();
        boolean voice = false;
        int voiceID = 1;
        switch (attack.skill) {
            case 8001003:
            case 8001008:
            case 8001011:
            case 8001013: {
                voice = true;
                voiceID = 1;
                break;
            }
            case 61001002: {
                voice = true;
                voiceID = 2;
                break;
            }
            case 8001004: {
                voice = true;
                voiceID = 3;
                break;
            }
            case 8001005: {
                voice = true;
                voiceID = 4;
                break;
            }
            case 8001006: {
                voice = true;
                voiceID = 5;
                break;
            }
            case 61111003: {
                voice = true;
                voiceID = 6;
                break;
            }
            case 61121002: {
                voice = true;
                voiceID = 7;
                break;
            }
        }
        if (voice) {
            String gender = (chr.getGender() == 0 ? "M" : "F");
            chr.getMap().broadcastMessage(CField.playSound("hayato/" + gender + "" + voiceID + ""));
        }

        boolean voice_02 = false;
        int voiceID_02 = 1;
        switch (attack.skill) {
            case 65101001:
            case 65101002:
            case 65111001:
            case 65121001:
            case 65121018: {
                voice_02 = true;
                voiceID_02 = 1;
                break;
            }
            case 65001003: {
                voice_02 = true;
                voiceID_02 = 2;
                break;
            }
            case 65101003: {
                voice_02 = true;
                voiceID_02 = 3;
                break;
            }
            case 65101005: {
                voice_02 = true;
                voiceID_02 = 4;
                break;
            }
            case 65111004: {
                voice_02 = true;
                voiceID_02 = 7;
                break;
            }
            case 65111006: {
                voice_02 = true;
                voiceID_02 = 8;
                break;
            }
            case 65121007: {
                voice_02 = true;
                voiceID_02 = 9;
                break;
            }
        }
        if (voice_02) {
            String gender = (chr.getGender() == 0 ? "M" : "F");
            chr.getMap().broadcastMessage(CField.playSound("mukhyun/" + gender + "" + voiceID_02 + ""));
        }
        chr.getClient().sendPacket(CField.OnAttack(!energy ? 0 : 4, chr, attack.allDamage, attack.skill, attack.tbyte, attack.unk, attack.display, attack.speed, attack.charge, attack.position));
        chr.getMap().broadcastMessage(chr, CField.OnAttack(!energy ? 0 : 4, chr, attack.allDamage, attack.skill, attack.tbyte, attack.unk, attack.display, attack.speed, attack.charge, attack.position), chr.getTruePosition());
        DamageParse.applyAttack(attack, skill, chr, attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        if (attack.skill == 4341002) {
            final CharacterTemporaryStat cts = CharacterTemporaryStat.FinalCut;
            int skillID = 4341002;
            int skillLv = chr.getTotalSkillLevel(4341002);
            final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
            int ctv = mse.getY();
            int duration = 1000 * 60;
            chr.setTemporaryStat(cts, ctv, mse, skillID, duration);
        }
    }

    public static final void tryDoingShootAttack(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {

        if (chr == null) {
            return;
        }
        if ((chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        AttackInfo attack = DamageParse.parseDmgR(slea, chr);
        if (attack == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int bulletCount = 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        boolean AOE = attack.skill == 4111004;
        boolean noBullet = ((chr.getJob() >= 3500) && (chr.getJob() <= 3512))
                || (GameConstants.isCannon(chr.getJob()))
                || (GameConstants.isPhantom(chr.getJob()))
                || (GameConstants.isMercedes(chr.getJob()))
                || (GameConstants.isHayato(chr.getJob()))
                || (GameConstants.isKanna(chr.getJob()))
                || (GameConstants.isMukhyun(chr.getJob()))
                || (GameConstants.isBeastTamer(chr.getJob()))
                || (GameConstants.isLynn(chr.getJob()));
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!chr.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "You may not use that here.");
                                return;
                            }
                        }
                    }
                }
            }
            switch (attack.skill) {
                case 13101005:
                case 21110004: // Ranged but uses attackcount instead
                case 14101006: // Vampure
                case 21120006:
                case 11101004:
                case 51001004: // Mihile || Soul Blade
                case 1077:
                case 1078:
                case 1079:
                case 11077:
                case 11078:
                case 11079:
                case 15111006:
                case 15111007:
                case 13111007: //Wind Shot
                case 33101007:
                case 33101002:
                case 33121002:
                case 33121001:
                case 21100004:
                case 21110011:
                case 21100007:
                case 21000004:
                case 5121002:
                case 5921002:
                case 4121003:
                case 4221003:
                case 5221017:

                case 5721007:

                case 5221016:
                case 5721006:
                case 5211008:
                case 5201001:
                case 5721003:
                case 5711000:
                case 4111013:
                case 5121016:
                case 15111008://시그너스 에너지블라스트
                case 51111007: // Mihile || Radiant Buster
                case 51121008: // Mihile || Radiant Buster
                case 5121013:
                case 5221013:
                case 5721004:
                case 5721001:
                case 5321001:
                case 14111008:
                case 60011216:
                case 65001100:
                case 4121006:
                case 1111012: // 어퍼 차지
                case 1211012: // 어퍼 차지
                case 1311013: // 어퍼 차지
                case 4321004: // 어퍼 스탭
                case 15111020: // 승천
                case 21110003: // 파이널 토스
                case 21110020: // 파이널 블로우

                    AOE = true;
                    bulletCount = effect.getAttackCount();
                    break;
                case 35121005:
                case 35111004:
                case 35121013:
                    AOE = true;
                    bulletCount = 6;
                    break;
                default:
                    bulletCount = effect.getBulletCount();
                    break;
            }
            if (noBullet && effect.getBulletCount() < effect.getAttackCount()) {
                bulletCount = effect.getAttackCount();
            }
            if ((noBullet) && (effect.getBulletCount() < effect.getAttackCount())) {
                bulletCount = effect.getAttackCount();
            }
            if ((effect.getCooldown(chr) > 0 && !chr.isGM()) && (attack.skill != 35111004 && attack.skill != 35121013)) {
                if (chr.skillisCooling(attack.skill)) {
                    //c.getSession().write(CWvsContext.enableActions());
                    //return;
                }
                boolean nocd = false;
                if (!nocd) {
                    c.sendPacket(CUserLocal.skillCooltimeSet(attack.skill, effect.getCooldown(chr)));
                    chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
                }
            }
        }
        Integer ShadowPartner = chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner);
        //chr.getCalcDamage().PDamage(chr, attack);
        if (ShadowPartner != null) {
            bulletCount *= 2;
        }
        int projectile = 0;
        int visProjectile = 0;
        if ((!AOE) && (chr.getBuffedValue(CharacterTemporaryStat.SoulArrow) == null) && (!noBullet)) {
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem((short) attack.slot);
            if (ipp == null) {
                return;
            }
            projectile = ipp.getItemId();

            if (attack.csstar > 0) {
                if (chr.getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar) == null) {
                    return;
                }
                visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar).getItemId();
            } else {
                visProjectile = projectile;
            }
            if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
                int bulletConsume = bulletCount;
                if ((effect != null) && (effect.getBulletConsume() != 0)) {
                    bulletConsume = effect.getBulletConsume() * (ShadowPartner != null ? 2 : 1);
                }
                //claw mastery
                int slotMax = MapleItemInformationProvider.getInstance().getSlotMax(projectile);
                int masterySkill = chr.getTotalSkillLevel(GameConstants.getMasterySkill(chr.getJob()));
                if (chr.getJob() >= 410 && chr.getJob() <= 412 && masterySkill > 0) {
                    slotMax += 10 * masterySkill;
                }
                if ((chr.getJob() == 412 || chr.getJob() == 411) && (bulletConsume > 0) && (ipp.getQuantity() < slotMax)) {
                    Skill expert = SkillFactory.getSkill(4110012);
                    if (chr.getTotalSkillLevel(expert) > 0) {
                        MapleStatEffect eff = expert.getEffect(chr.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            ipp.setQuantity((short) (ipp.getQuantity() + 1));
                            c.getSession().write(CWvsContext.InventoryPacket.updateInventorySlot(MapleInventoryType.USE, ipp, false));
                            bulletConsume = 0;
                            c.getSession().write(CWvsContext.InventoryPacket.getInventoryStatus());
                        }
                    }
                }
                if ((bulletConsume > 0)
                        && (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true))) {
                    chr.dropMessage(5, "You do not have enough projectiles");
                    return;
                }
            }
        } else if ((chr.getJob() >= 3500) && (chr.getJob() <= 3512)) {
            visProjectile = 2333000;
        } else if (GameConstants.isCannon(chr.getJob())) {
            visProjectile = 2333001;
        }

        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }
        PlayerStats statst = chr.getStat();
        double basedamage;
        switch (attack.skill) {
            case 4001344:
            case 4121007:
            case 14001004:
            case 14111005:
                basedamage = Math.max(statst.getCurrentMaxBaseDamage(), statst.getTotalLuk() * 5.0F * (statst.getTotalWatk() + projectileWatk) / 100.0F);
                break;
            case 4111004:
                basedamage = 53000.0D;
                break;
            default:
                basedamage = statst.getCurrentMaxBaseDamage();
                switch (attack.skill) {
                    case 3101005:
                        basedamage *= effect.getX() / 100.0D;
                }

        }

        if (effect != null) {
            basedamage *= (effect.getDamage() + statst.getDamageIncrease(attack.skill)) / 100.0D;
            int money = effect.getMoneyCon();
            if (money != 0) {
                if (money > chr.getMeso()) {
                    money = chr.getMeso();
                }
                chr.gainMeso(-money, false);
            }
        }
        chr.checkFollow();
        boolean bAttack = GameConstants.getBonusAttackCount(attack.skill) > 1;
        chr.getClient().sendPacket(CField.OnAttack(!bAttack ? 1 : 2, chr, attack.allDamage, attack.skill, attack.tbyte, attack.unk, attack.display, attack.speed, visProjectile, attack.position));
        chr.getMap().broadcastMessage(chr, CField.OnAttack(!bAttack ? 1 : 2, chr, attack.allDamage, attack.skill, attack.tbyte, attack.unk, attack.display, attack.speed, visProjectile, attack.position), chr.getTruePosition());

        DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, ShadowPartner != null ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);
    }

    public static final void tryDoingMagicAttack(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        AttackInfo attack = DamageParse.parseDmgMa(slea, chr);
        if (attack == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
        if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int skillLevel = chr.getTotalSkillLevel(skill);
        MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
        if (effect == null) {
            return;
        }
        //attack = DamageParse.Modify_AttackCrit(attack, chr, 3, effect);
        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!chr.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "You may not use that here.");
                            return;
                        }
                    }
                }
            }
        }
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage() * (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0D;
        if (GameConstants.isPyramidSkill(attack.skill)) {
            maxdamage = 1.0D;
        } else if ((GameConstants.isBeginnerJob(skill.getId() / 10000)) && (skill.getId() % 10000 == 1000)) {
            maxdamage = 40.0D;
        }
        if ((effect.getCooldown(chr) > 0) && (!chr.isGM())) {
            if (chr.skillisCooling(attack.skill)) {
                //c.getSession().write(CWvsContext.enableActions());
                //return;
            }
            int cd = effect.getCooldown(chr);
            c.sendPacket(CUserLocal.skillCooltimeSet(attack.skill, cd));
            chr.addCooldown(attack.skill, System.currentTimeMillis(), cd * 1000);
        }
        chr.checkFollow();
        //chr.getCalcDamage().PDamage(chr, attack);
        boolean voice = false;
        int voiceID = 1;
        switch (attack.skill) {
            case 8001015: {
                voice = true;
                voiceID = 1;
                break;
            }
            case 62101005: {
                voice = true;
                voiceID = 2;
                break;
            }
            case 62101002: {
                voice = true;
                voiceID = 3;
                break;
            }
            case 62121002: {
                voice = true;
                voiceID = 6;
                break;
            }
            case 62121004: {
                voice = true;
                voiceID = 7;
                break;
            }
        }
        if (voice) {
            String gender = (chr.getGender() == 0 ? "M" : "F");
            chr.getMap().broadcastMessage(CField.playSound("kanna/" + gender + "" + voiceID + ""));
        }

        boolean voice_04 = false;
        int voiceID_04 = 1;
        switch (attack.skill) {
            case 52111001: {
                voice_04 = true;
                voiceID_04 = 2;
                break;
            }
            case 52121001: {
                voice_04 = true;
                voiceID_04 = 5;
                break;
            }
        }
        if (voice_04) {
            String gender = (chr.getGender() == 0 ? "M" : "F");
            chr.getMap().broadcastMessage(CField.playSound("lynn/" + gender + "" + voiceID_04 + ""));
        }

        if (GameConstants.isKanna(chr.getJob())) {
            if (chr.getTotalSkillLevel(62121002) > 0) {
                final MapleStatEffect skill_62121002 = SkillFactory.getSkill(62121002).getEffect(chr.getTotalSkillLevel(62121002));
                if (skill_62121002 != null) {
                    if (chr.getParty() != null) {
                        for (final MaplePartyCharacter pUser : chr.getParty().getMembers()) {
                            final MapleCharacter cUser = chr.getMap().getCharacterById(pUser.getId());
                            if (cUser != null) {
                                cUser.addHP((cUser.getStat().getMaxHp() / 100) * skill_62121002.getX());
                            }
                        }
                    } else {
                        chr.addHP((chr.getStat().getMaxHp() / 100) * skill_62121002.getX());
                    }
                }
            }
        }

        if (attack.skill == 62101005) {
            if (chr.getTotalSkillLevel(8001016) > 0) {
                final MapleStatEffect cEffect = SkillFactory.getSkill(8001016).getEffect(chr.getTotalSkillLevel(8001016));
                final Rectangle cBounds = cEffect.calculateBoundingBox(chr.getPosition(), chr.isFacingLeft());
                final MapleMist cMist = new MapleMist(cBounds, chr, cEffect);
                chr.getMap().spawnMist(cMist, cEffect.getDuration(), false);
            }
        }

        if (attack.skill == 52111001) {
            if (chr.getTotalSkillLevel(8001048) > 0) {
                final MapleStatEffect cEffect = SkillFactory.getSkill(8001048).getEffect(chr.getTotalSkillLevel(8001048));

                final Rectangle cBounds = cEffect.calculateBoundingBox(chr.getPosition(), chr.isFacingLeft());
                final MapleMist cMist = new MapleMist(cBounds, chr, cEffect);
                chr.getMap().spawnMist(cMist, cEffect.getDuration(), false);

            }
        }

        chr.getClient().sendPacket(CField.OnAttack(3, chr, attack.allDamage, attack.skill, attack.tbyte, attack.unk, attack.display, attack.speed, attack.charge, attack.position));
        chr.getMap().broadcastMessage(chr, CField.OnAttack(3, chr, attack.allDamage, attack.skill, attack.tbyte, attack.unk, attack.display, attack.speed, attack.charge, attack.position), chr.getTruePosition());

        DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect, maxdamage);
    }

    public static final void DropMeso(int meso, MapleCharacter chr) {
        if ((!chr.isAlive()) || (meso < 10) || (meso > 50000) || (meso > chr.getMeso())) {
            chr.getClient().getSession().write(CWvsContext.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getTruePosition(), chr, chr, true, (byte) 0);
        chr.getClient().getSession().write(CWvsContext.enableActions());
    }

    public static final void ChangeAndroidEmotion(int emote, MapleCharacter chr) {
        if ((emote > 0) && (chr != null) && (chr.getMap() != null) && (!chr.isHidden()) && (emote <= 17) && (chr.getAndroid() != null)) {
            chr.getMap().broadcastMessage(CField.showAndroidEmotion(chr.getId(), emote));
        }
    }

    public static void xenonball(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {

        short v1 = slea.readShort();//COutPacket::encode2((int)&v119, 0);
        int cid = slea.readInt();//COutPacket::encode4(HIDWORD(v109));
        if (v1 != 3) {
            byte posx = slea.readByte();//COutPacket::encode1((int)&v119, 4);
            if (posx == 4) {
                short posy = slea.readShort();//COutPacket::encode2((int)&v119, SWORD2(v109));
                short v5 = slea.readShort();//COutPacket::encode2((int)&v119, SWORD2(v109));
                short v6 = slea.readShort();//COutPacket::encode2((int)&v119, SWORD2(v109));
                short v7 = slea.readShort();//COutPacket::encode2((int)&v119, SWORD2(v109));
                short v8 = slea.readShort();//COutPacket::encode2((int)&v119, v95);
                int sid = slea.readInt();//COutPacket::encode4(HIDWORD(v109));
                short skilllevel = slea.readShort();//COutPacket::encode2((int)&v119, SWORD2(v109));
                byte unkbyte = slea.readByte();//
                short unk30000 = slea.readShort();
                short wtf = slea.readShort();//0
                short v10 = slea.readShort();//언제나 10임    
                chr.getMap().broadcastMessage(SummonPacket.XenonBall(v1, cid, posx, posy, v5, v6, v7, v8, unk30000, wtf, v10, sid, skilllevel, chr, unkbyte));
            } else {
                short t1 = slea.readShort();
                short t2 = slea.readShort();
                short t3 = slea.readShort();
                int sid = slea.readInt();//COutPacket::encode4(HIDWORD(v109));            
                short skilllevel = slea.readShort();//COutPacket::encode2((int)&v119, SWORD2(v109));
                byte unkbyte = slea.readByte();
                short unk30000 = slea.readShort();
                short wtf = slea.readShort();//0
                short v10 = slea.readShort();//언제나 10임    
                chr.getMap().broadcastMessage(SummonPacket.XenonBall2(v1, cid, posx, unkbyte, t1, t2, t3, unk30000, wtf, v10, sid, skilllevel, chr));
            }
        } else {
            int unk2 = slea.readInt();//2 ??
            int sid = slea.readInt();//COutPacket::encode4(HIDWORD(v109)); 
            int skilllevel = slea.readInt();//1E
            short wtf = slea.readShort();//09 XenonBall5
            short v10 = slea.readShort();//04               XenonBall4         
            chr.getMap().broadcastMessage(SummonPacket.XenonBall3(v1, unk2, cid, sid, wtf, v10, chr));
        }
    }

    public static void StarFall(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {//어택이펙트
        int position_x = slea.readInt();
        int position_y = slea.readInt();
        int unk_01 = slea.readInt(); // = position_y
        int unk_02 = slea.readInt();
        int skill_id = slea.readInt();
        byte facing_left = slea.readByte();
        int unk_03 = slea.readInt();
        int unk_04 = slea.readInt();

        final Skill skill = SkillFactory.getSkill(skill_id);
        if (skill != null) {
            final int skillLevel = chr.getTotalSkillLevel(skill);
            if (skillLevel > 0) {
                final MapleStatEffect statEffect = skill.getEffect(skillLevel);
                if (statEffect != null) {
                    statEffect.applyTo(chr, chr.getTruePosition());
                }
            }
        }
        if (skill_id != 4321002) {
            chr.getMap().broadcastMessage(CMobPool.MorningStarFall(chr, position_x, position_y, unk_01, skill_id, unk_02, facing_left, unk_03));
        } else {
            chr.getMap().broadcastMessage(CMobPool.MorningStarFall(chr, position_x, position_y, unk_02, skill_id, unk_02, facing_left, unk_03));
        }
        chr.getMap().broadcastMessage(CField.EffectPacket.showBuffeffect(chr.getId(), skill_id, 1, chr.getLevel(), chr.getSkillLevel(skill_id), (byte) 3));
    }

    public static void MoveAndroid(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(8);
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 3);

        if (res != null && chr != null && !res.isEmpty() && chr.getMap() != null && chr.getAndroid() != null) { // map crash hack
            final Point pos = new Point(chr.getAndroid().getPos());
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveAndroid(chr.getId(), pos, res), false);
        }
    }

    public static final void ChangeEmotion(int emote, MapleCharacter chr) {
        if (emote > 7) {
            int emoteid = 5159992 + emote;
            MapleInventoryType type = GameConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
                return;
            }
        }
        if ((emote > 0) && (chr != null) && (chr.getMap() != null) && (!chr.isHidden())) {
            chr.getMap().broadcastMessage(chr, CUserLocal.setEmotion(emote, 10000, false), false);
        }
    }

    public static final void Heal(LittleEndianAccessor slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        //chr.dropMessage(5, "힐중");
        chr.updateTick(slea.readInt());
        slea.skip(4);
        int healHP = slea.readShort();
        int healMP = slea.readShort();
        PlayerStats stats = chr.getStat();

        if (stats.getHp() <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if ((healHP != 0) && (chr.canHP(now + 1000L))) {
            if (healHP > stats.getHealHP()) {
                healHP = (int) stats.getHealHP();
            }
            chr.addHP(healHP);
        }
        if ((healMP != 0) && (!GameConstants.isDemon(chr.getJob())) && (chr.canMP(now + 1000L))) {
            if (healMP > stats.getHealMP()) {
                healMP = (int) stats.getHealMP();
            }
            chr.addMP(healMP);
        }
    }

    public static final void MovePlayer(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        Point Original_Pos = chr.getPosition();
        slea.skip(17);
        List res;
        res = MovementParse.parseMovement(slea, 1);
        if ((res != null) && (c.getPlayer().getMap() != null)) {
            if ((slea.available() < 11L) || (slea.available() > 26L)) {
                System.out.println("리턴?");
                return;
            }
            MapleMap map = c.getPlayer().getMap();
            if (chr.isHidden()) {
                chr.setLastRes(res);
                c.getPlayer().getMap().broadcastGMMessage(chr, CField.movePlayer(chr.getId(), res, Original_Pos), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.movePlayer(chr.getId(), res, Original_Pos), false);
            }
            MovementParse.updatePosition(res, chr, 0);
            map.movePlayer(chr, chr.getPosition());
            Point pos = chr.getTruePosition();
            if ((chr.getFollowId() > 0) && (chr.isFollowOn()) && (chr.isFollowInitiator())) {
                MapleCharacter fol = map.getCharacterById(chr.getFollowId());
                if (fol != null) {
                    Point original_pos = fol.getPosition();
                    fol.getClient().getSession().write(CField.moveFollow(Original_Pos, original_pos, pos, res));
                    MovementParse.updatePosition(res, fol, 0);
                    map.movePlayer(fol, pos);
                    map.broadcastMessage(fol, CField.movePlayer(fol.getId(), res, original_pos), false);
                } else {
                    chr.checkFollow();
                }
            }
            int count = c.getPlayer().getFallCounter();
            boolean samepos = (pos.y > c.getPlayer().getOldPosition().y) && (Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5);
            if ((samepos) && ((pos.y > map.getBottom() + 250) || (map.getFootholds().findBelow(pos) == null))) {
                if (count > 5) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    count++;
                    c.getPlayer().setFallCounter(count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(pos);
            if ((!samepos) && (c.getPlayer().getBuffedValue(CharacterTemporaryStat.YellowAura) != null)) {
                /*
                 for (MaplePartyCharacter partychar : c.getPlayer().getParty().getMembers()) {
                 if (partychar != null && partychar.getMapid() == c.getPlayer().getMapId() && partychar.getChannel() == c.getChannel()) {
                 final MapleCharacter other = c.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                 if (other != null && other.getBuffedValue(MapleBuffStat.YELLOW_AURA) == null) {                    
                 c.getPlayer().getStatForBuff(MapleBuffStat.YELLOW_AURA).applyTo(other);
                 }
                 }
                 } 
                 */
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.YellowAura).applyMonsterBuff(c.getPlayer());
            }
        }
    }

    public static final void ChangeMapSpecial(String portal_name, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(portal_name);

        // if (chr.getGMLevel() > ServerConstants.PlayerGMRank.GM.getLevel()) {
//  chr.dropMessage(6, new StringBuilder().append(portal.getScriptName()).append(" accessed").toString());
        //  }
        if ((portal != null) && (!chr.hasBlockedInventory())) {
            portal.enterPortal(c);
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void ChangeMap(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        if (chr.getMap() == null) {
            return;
        }
        if (slea.available() != 0) {
            slea.skip(7);
            int targetid = slea.readInt();
            MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            if (slea.available() > 6) {
                chr.updateTick(slea.readInt());
            }
            boolean wheel = (slea.readShort() > 0) && ((chr.haveItem(5510000, 1, false, true)) || chr.currentSoulStone());
            if ((targetid != -1) && (!chr.isAlive())) {
                //chr.cancelAllBuffs();
                //chr.cancelAllBuffs_();
                chr.setStance(1);
                if ((chr.getEventInstance() != null) && (chr.getEventInstance().revivePlayer(chr)) && (chr.isAlive())) {
                    return;
                }
                if (chr.getPyramidSubway() != null) {
                    chr.getStat().setHp(50, chr);
                    chr.getPyramidSubway().fail(chr);
                    return;
                }
                if (MapConstants.isDeathCountMap(chr.getMapId()) != -1) {
                    chr.setDeathCount(chr.getDeathCount() - 1);
                    if (chr.getDeathCount() < 1) {
                        chr.getStat().setHp(50, chr);
                        chr.updateSingleStat(MapleStat.HP, chr.getStat().getMaxHp());
                        chr.changeMap(chr.getMap().getForcedReturnMap());
                        chr.setSoulStone(false);
                        c.sendPacket(CWvsContext.enableActions());
                    } else {
                        chr.getStat().setHp(50, chr);
                        chr.updateSingleStat(MapleStat.HP, chr.getStat().getMaxHp());
                        chr.setSoulStone(false);
                        c.sendPacket(CWvsContext.enableActions());
                    }
                } else {
                    if (!wheel) {
                        chr.getStat().setHp(50, chr);
                        chr.updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                        chr.changeMap(chr.getMap().getReturnMap(), chr.getMap().getReturnMap().getPortal(0));
                        chr.setSoulStone(false);
                        c.getSession().write(CWvsContext.enableActions());
                    } else {
                        chr.getStat().setHp(chr.getStat().getMaxHp(), chr);
                        chr.updateSingleStat(MapleStat.HP, chr.getStat().getMaxHp());
                        chr.setSoulStone(false);
                        c.getSession().write(CField.EffectPacket.useWheel((byte) (chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1)));
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);
                        c.getSession().write(CWvsContext.enableActions());
                    }
                }

                if (chr.getBuffedValue(CharacterTemporaryStat.EnergyCharge) != null) {
                    final Skill v1 = SkillFactory.getSkill(chr.getBuffSource(CharacterTemporaryStat.EnergyCharge));
                    final int v2 = chr.getTotalSkillLevel(v1);
                    final MapleStatEffect v3 = v1.getEffect(v2);
                    v3.applyEnergyBuff(chr, false);
                }

            } else if (portal != null && !chr.hasBlockedInventory()) {
                portal.enterPortal(c);
            } else {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                if (to != null) {
                    chr.changeMap(to);
                } else {
                    chr.dropMessage(1, "잠시 후에 다시 시도하여 주시길 바랍니다.");
                }
                c.getSession().write(CWvsContext.enableActions());
            }
        }
    }

    public static final void InnerPortal(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
        int toX = slea.readShort();
        int toY = slea.readShort();

        if (portal == null) {
            return;
        }
        if (chr.isGM() == true) {
            chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "(InnerPortal) Portal : " + portal.getName() + "(" + portal.getId() + ")"));
        }
        if ((portal.getPosition().distanceSq(chr.getTruePosition()) > 22500.0D) && (!chr.isGM())) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }

        switch (chr.getMapId()) {
            case 450003720: {
                if (portal.getId() == 4) {
                    if (chr.getQuestStatus(34302) == 1) {
                        if (chr.getOneInfoQuest(34302, "try").equals("")) {
                            chr.updateOneInfoQuest(34302, "try", "0");
                        }
                        int v1 = Integer.parseInt(c.getPlayer().getOneInfoQuest(34302, "try"));
                        if (v1 > 4) {
                            v1 = 5;
                            MapleQuest.getInstance(34302).forceCustomData(c.getPlayer(), "1");
                        }
                        chr.updateOneInfoQuest(34302, "try", (v1 + 1) + "");
                    }
                }
                break;
            }
        }

        chr.getMap().movePlayer(chr, new Point(toX, toY));
        chr.checkFollow();
    }

    public static final void snowBall(LittleEndianAccessor slea, MapleClient c) {
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void leftKnockBack(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMapId() / 10000 == 10906) {
            c.getSession().write(CField.leftKnockBack());
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void ReIssueMedal(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MapleQuest q = MapleQuest.getInstance(slea.readShort());
        int itemid = q.getMedalItem();
        if ((itemid != slea.readInt()) || (itemid <= 0) || (q == null) || (chr.getQuestStatus(q.getId()) != 2)) {
            c.sendPacket(CUserLocal.medalReissueResult(itemid, 4));
            return;
        }
        if (chr.haveItem(itemid, 1, true, true)) {
            c.sendPacket(CUserLocal.medalReissueResult(itemid, 3));
            return;
        }
        if (!MapleInventoryManipulator.checkSpace(c, itemid, 1, "")) {
            c.sendPacket(CUserLocal.medalReissueResult(itemid, 2));
            return;
        }
        if (chr.getMeso() < 100) {
            c.sendPacket(CUserLocal.medalReissueResult(itemid, 1));
            return;
        }
        chr.gainMeso(-100, true, true);
        MapleInventoryManipulator.addById(c, itemid, (byte) 1, new StringBuilder().append("Redeemed item through medal quest ").append(q.getId()).append(" on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
        c.sendPacket(CUserLocal.medalReissueResult(itemid, 0));
    }

    public static void CrossChapter(LittleEndianAccessor slea, MapleCharacter chr) { // 임시크로스 헌터
        int chapter = slea.readShort();
        switch (Integer.parseInt(chr.getKeyValue("cross"))) {
            case 6:
                //   chr.addRewardDB(chr.getId(),3700031,1);
                //    chr.addRewardDB(chr.getId(),4310029, 10);
                chr.setKeyValue("chapter", chr.getKeyValue("chapter") + ";r=1");
                chr.getClient().getSession().write(CField.UIPacket.UpdateCrossHunter(chr.getKeyValue("chapter"), 0x670));
                chr.setKeyValue("cross", "7");
                break;
            case 12:
                //   chr.addRewardDB(chr.getId(),3700032,1);
                //   chr.addRewardDB(chr.getId(),4310029, 100);
                chr.setKeyValue("chapter2", chr.getKeyValue("chapter2") + ";r=1");
                chr.getClient().getSession().write(CField.UIPacket.UpdateCrossHunter(chr.getKeyValue("chapter2"), 0x671));
                chr.setKeyValue("cross", "13");
                break;
            case 18:
                //   chr.addRewardDB(chr.getId(),3700033,1);
                //   chr.addRewardDB(chr.getId(),4310029, 20);
                chr.setKeyValue("chapter3", chr.getKeyValue("chapter3") + ";r=1");
                chr.getClient().getSession().write(CField.UIPacket.UpdateCrossHunter(chr.getKeyValue("chapter3"), 0x672));
                chr.setKeyValue("cross", "19");
                break;
            case 24:
                //   chr.addRewardDB(chr.getId(),3700034,1);
                //   chr.addRewardDB(chr.getId(),4310029, 30);
                MapleQuest.getInstance(31179).forceComplete(chr, 2100);
                chr.setKeyValue("chapter4", chr.getKeyValue("chapter4") + ";r=1");
                chr.getClient().getSession().write(CField.UIPacket.UpdateCrossHunter(chr.getKeyValue("chapter4"), 0x673));
                //chr.send(UIPacket.showInfo("아카이럼 선행 퀘스트가 완료 되었습니다."));
                chr.setKeyValue("cross", "25");
                break;
            default:
                //    chr.getClient().getSession().writeAndFlush(MainPacketCreator.getNPCTalk(2144004, (byte) 0, "이미 챕터를 완료 하셨습니다.", "00 00", (byte) 0));
                chr.getClient().getSession().write(CWvsContext.enableActions());
                return;
        }
        chr.getClient().getSession().write(CWvsContext.enableActions());
    }

    public static void handleDodgeSkillReady(LittleEndianAccessor slea, MapleClient c, MapleCharacter player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
    public static void handleRepacker(MapleCharacter user, int skillID) {
        final Skill nSkill = SkillFactory.getSkill(skillID);
        if (nSkill != null) {
            final int nSLV = user.getTotalSkillLevel(skillID);
            if (nSLV > 0) {
                final MapleStatEffect nMSE = nSkill.getEffect(nSLV);
                if (nMSE != null) {
                    switch (skillID) {
                        case 1001005: { // 파워 스트라이크
                            final Point lt = null;
                            final Point rb = null;
                            int damage = (200 + (5 * nSLV));
                            if (user.isGM()) {
                                user.dropMessage(5, "skill : " + nSkill.getName() + "(" + skillID + ") | lt : " + lt + " | rb : " + rb + " | damage : " + damage);
                                user.dropMessage(6, "skill : " + nSkill.getName() + "(" + skillID + ") | lt : " + nMSE.getLt() + " | rb : " + nMSE.getRb() + " | damage : " + nMSE.getDamage());
                            }
                            if (damage != nMSE.getDamage()) {
                                user.dropMessage(5, "값 불일치");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
     */
}
