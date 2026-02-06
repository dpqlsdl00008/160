package handling.channel.handler;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleDiseaseValueHolder;
import client.MonsterSkill;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterTemporaryStat;
import client.status.MonsterTemporaryStatEffect;
import constants.GameConstants;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.Randomizer;
import server.Timer;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import tools.AttackPair;
import tools.FileoutputUtil;
import static tools.FileoutputUtil.getDCurrentTime;
import tools.Pair;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CMobPool;
import tools.packet.CUserLocal;

public class DamageParse {

    public static void applyAttack(AttackInfo attack, Skill theSkill, MapleCharacter player, int attackCount, double maxDamagePerMonster, MapleStatEffect effect, AttackType attack_type) {
        if (!player.isAlive()) {
            //   System.out.print("메익6");
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        boolean useAttackCount = (attack.skill != 62111002) && (attack.skill != 4211006) && (attack.skill != 3221007) && (attack.skill != 23121003) && ((attack.skill != 1311001) || (player.getJob() != 132)) && (attack.skill != 3211006);
        if ((attack.hits > 0) && (attack.targets > 0)) {
            //if (!player.getStat().checkEquipDurabilitys(player, -2)) {
            //player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            //return;
            //}
        }
        long totDamage = 0;
        MapleMap map = player.getMap();
        /*
        int time = 1000;
        if (player.getBuffedValue(32110009) == false) {
            if (attack.skill == 5221006) { // 불릿 파티
                time *= 5;
                SkillFactory.getSkill(32110009).getEffect(1).applyBuffEffect(player, player, false, time);
            }
            if (attack.skill == 4341012) { // 스킬 코드
                time *= 10; // 시간 초
                SkillFactory.getSkill(32110009).getEffect(1).applyBuffEffect(player, player, false, time);
            }
            if (attack.skill == 4341012) { // 스킬 코드
                time *= 10; // 시간 초
                SkillFactory.getSkill(32110009).getEffect(1).applyBuffEffect(player, player, false, time);
            }
        }
         */
        if (attack.skill == 4211006) {
            for (AttackPair oned : attack.allDamage) {
                if (oned.attack != null) {
                    continue;
                }
                MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);

                if (mapobject != null) {
                    MapleMapItem mapitem = (MapleMapItem) mapobject;
                    mapitem.getLock().lock();
                    try {
                        if (mapitem.getMeso() > 0) {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapitem);
                            map.broadcastMessage(CField.explodeDrop(mapitem.getObjectId()));
                            //    System.out.print("메익1");
                            mapitem.setPickedUp(true);
                        } else {
                            player.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION);
                            return;
                        }
                    } finally {
                        mapitem.getLock().unlock();
                    }
                }
            }
        }
        long totDamageToOneMonster = 0;
        long hpMob = 0L;
        PlayerStats stats = player.getStat();
        int CriticalDamage = stats.passive_sharpeye_percent();
        int ShdowPartnerAttackPercentage = 0;
        if ((attack_type == AttackType.RANGED_WITH_SHADOWPARTNER) || (attack_type == AttackType.NON_RANGED_WITH_MIRROR)) {
            MapleStatEffect shadowPartnerEffect = player.getStatForBuff(CharacterTemporaryStat.ShadowPartner);
            if (shadowPartnerEffect != null) {
                ShdowPartnerAttackPercentage += shadowPartnerEffect.getX();
                //System.out.println("ShdowPartnerAttackPercentage" + ShdowPartnerAttackPercentage);
            }
            attackCount /= 2;
        }
        ShdowPartnerAttackPercentage *= (CriticalDamage + 100) / 100;
        if (attack.skill == 4221001) {
            ShdowPartnerAttackPercentage *= 10;
        }
        double maxDamagePerHit = 0.0D;
        MapleMonster monster;
        for (AttackPair oned : attack.allDamage) {
            monster = map.getMonsterByOid(oned.objectid);
            if ((monster != null) && (monster.getLinkCID() <= 0)) {
                totDamageToOneMonster = 0;//이쪽과
                hpMob = monster.getMobMaxHp(); //
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                boolean Tempest = (monster.getStatusSourceID(MonsterTemporaryStat.Frozen) == 21120006) || (attack.skill == 21120006) || (attack.skill == 1221011);
                if ((!Tempest) && (!player.isGM())) {
                    if (((player.getJob() >= 3200) && (player.getJob() <= 3212) && (!monster.isBuffed(MonsterTemporaryStat.HardSkin)) && (!monster.isBuffed(MonsterTemporaryStat.MImmune)) && (!monster.isBuffed(MonsterTemporaryStat.MCounter))) || (attack.skill == 3221007) || (attack.skill == 23121003) || (((player.getJob() < 3200) || (player.getJob() > 3212)) && (!monster.isBuffed(MonsterTemporaryStat.HardSkin)) && (!monster.isBuffed(MonsterTemporaryStat.PImmune)) && (!monster.isBuffed(MonsterTemporaryStat.PCounter)))) {
                        maxDamagePerHit = CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect, maxDamagePerMonster, Integer.valueOf(CriticalDamage));
                    } else {
                        maxDamagePerHit = 1.0D;
                    }
                }
                byte overallAttackCount = 0;
                int criticals = 0;
                for (Pair eachde : oned.attack) {
                    Integer eachd = (Integer) eachde.left;
                    overallAttackCount = (byte) (overallAttackCount + 1);
                    if (((Boolean) eachde.right)) {
                        criticals++;
                    }
                    if ((useAttackCount) && (overallAttackCount - 1 == attackCount)) {
                        maxDamagePerHit = maxDamagePerHit / 100.0D * (ShdowPartnerAttackPercentage * (monsterstats.isBoss() ? stats.bossdam_r : stats.dam_r) / 100.0D);
                    }
                    if (fixeddmg != -1 && eachd != 0) {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = attack.skill != 0 ? 0 : fixeddmg;
                        } else {
                            eachd = fixeddmg;
                        }
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = (attack.skill != 0 ? 0 : Math.min(eachd, (int) maxDamagePerHit));
                    } else if (!player.isGM()) {
                        if (player.getLevel() < 49 && eachd > 199999) {
                            FileoutputUtil.log("log/[" + getDCurrentTime() + "] hack_highDamage.txt", "[" + FileoutputUtil.CurrentReadable_Time() + "]\r\n유저 : " + player.getName() + "(Lv." + player.getLevel() + ")\r\n스킬 : " + theSkill.getName() + "(" + theSkill.getId() + ")\r\n데미지 : " + eachd + "\r\n맵 : " + player.getMap().getMapName() + "(" + player.getMapId() + ")\r\n");
                        }
                        if (eachd > maxDamagePerHit) {
                            //eachd = (int) maxDamagePerHit;
                        }
                    }
                    totDamageToOneMonster += eachd;
                    if (((eachd == 0) || (monster.getId() == 9700021)) && (player.getPyramidSubway() != null)) {
                        player.getPyramidSubway().onMiss(player);
                    }
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if (player.getBuffedValue(CharacterTemporaryStat.PickPocket) != null) {
                    switch (attack.skill) {
                        case 0:
                        case 1078:
                        case 4001334:
                        case 4201005:
                        case 4201004:
                        case 4211011:
                        case 4221010:
                        case 4211002:
                        case 4221001:
                        case 4221007:
                            handlePickPocket(player, monster, oned);
                    }
                }
                if (totDamageToOneMonster > 0) {
                    if (GameConstants.isDemon(player.getJob()) == true) {
                        int force_a = monster.getStats().isBoss() == true ? 6 : 2;
                        player.handleForceGain(monster.getObjectId(), attack.skill, force_a);
                        switch (attack.skill) {
                            case 31000004:
                            case 31100007:
                            case 31110010:
                            case 31120011: {
                                if (player.getTotalSkillLevel(31110009) > 0) {
                                    int cRand = (monster.getStats().isBoss() ? 100 : (player.getTotalSkillLevel(31110009) * 15));
                                    if (Randomizer.rand(0, 100) < cRand) {
                                        int force_b = monster.getStats().isBoss() == true ? 10 : 3;
                                        player.handleForceGain(monster.getObjectId(), attack.skill, force_b);
                                    }
                                }
                            }
                            break;
                        }
                    }
                    if (GameConstants.isDemon(player.getJob()) && player.getSkillLevel(30010111) > 0) {
                        MapleStatEffect effs = SkillFactory.getSkill(30010111).getEffect(player.getTotalSkillLevel(30010111));
                        if (effs.makeChanceResult()) {
                            if (!monster.getStats().isBoss() && player.getMapId() != 900000000 && player.getMapId() != 109040004) {
                                totDamageToOneMonster = (int) monster.getHp();
                                player.addHP((int) (player.getStat().getCurrentMaxHp() * (effs.getX() / 100.0D)));
                            }
                        }
                    }
                    if (GameConstants.isPhantom(player.getJob()) && !GameConstants.isAtomSkill(attack.skill)) {
                        player.handleCardStack();
                    }

                    switch (player.getJob()) {
                        case 1312: {
                            if (!GameConstants.isAtomSkill(attack.skill)) {
                                player.handleAtomAttack();
                            }
                            break;
                        }
                    }

                    if (attack.skill != 1221011) {
                        if (player.getACDamage() > 0) {
                            totDamageToOneMonster += player.getACDamage();
                            player.getMap().broadcastMessage(CMobPool.damaged(monster.getObjectId(), player.getACDamage()));
                        }
                        monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    } else {
                        monster.damage(player, (monster.getStats().isBoss() ? 500000 : (monster.getHp() - 1)), true, attack.skill);
                    }

                    if (monster.isBuffed(MonsterTemporaryStat.PCounter)) {
                        int rDamage = 0;
                        if (totDamageToOneMonster > Integer.MAX_VALUE) {
                            rDamage = Integer.MAX_VALUE;
                        } else {
                            rDamage = (int) totDamageToOneMonster;
                        }
                        if (!player.isGM()) {
                            player.addHP(-rDamage);
                        }
                    }
                    player.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.skill, monster.getObjectId(), totDamage, 0);
                    if (GameConstants.isBindSkill(attack.skill)) {
                        monster.setTempEffectiveness(Element.ICE, 1);
                    }

                    switch (attack.skill) {
                        case 4001002:
                        case 4001334:
                        case 4001344:
                        case 4111005:
                        case 4121007:
                        case 4201005:
                        case 4211002:
                        case 4221001:
                        case 4221007:
                        case 4301001:
                        case 4311002:
                        case 4311003:
                        case 4331000:
                        case 4331004:
                        case 4331005:
                        case 4331006:
                        //case 4341002:
                        case 4341004:
                        case 4341005:
                        case 4341009:
                        case 14001004:
                        case 14111002:
                        case 14111005: {
                            int[] skills = {
                                4110011, // 베놈
                                4120011, // 페이탈 베놈
                                4210010, // 베놈
                                4220011, // 페이탈 베놈
                                4320005, // 베놈
                                4340012, // 페이탈 베놈
                                14110004, // 베놈
                            };
                            for (int i : skills) {
                                Skill skill = SkillFactory.getSkill(i);
                                if (player.getTotalSkillLevel(skill) > 0) {
                                    MapleStatEffect venomEffect = skill.getEffect(player.getTotalSkillLevel(skill));
                                    if (!venomEffect.makeChanceResult()) {
                                        break;
                                    }
                                    monster.applyStatus(player, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Poison, Integer.valueOf(1), i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                                    break;
                                }
                            }
                            break;
                        }
                        // 소울 어썰트
                        case 51121007: {
                            Skill soulAssert = SkillFactory.getSkill(51121007);
                            if (soulAssert != null) {
                                int soulAssertLevel = player.getTotalSkillLevel(soulAssert);
                                if (soulAssertLevel > 0) {
                                    MapleStatEffect soulAssertEffet = soulAssert.getEffect(soulAssertLevel);
                                    if (soulAssertEffet != null) {
                                        if (monster.isBuffed(MonsterTemporaryStat.Blind) == false) {
                                            if (Randomizer.rand(0, 100) < (10 + soulAssertLevel)) {
                                                monster.applyStatus(player, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Blind, 1, 51121007, null, false), false, soulAssertEffet.getDuration(), true, soulAssertEffet);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 4201004:
                            monster.handleSteal(player);
                            break;
                    }

                    if (GameConstants.isAran(player.getJob())) {
                        MapleStatEffect eff;
                        if (player.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
                            eff = player.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
                            if (eff != null) {
                                monster.applyStatus(player, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Speed, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((player.getBuffedValue(CharacterTemporaryStat.BodyPressure) != null) && (!(monster.getStats().isBoss()))) {
                            eff = player.getStatForBuff(CharacterTemporaryStat.BodyPressure);
                            if ((eff != null) && (eff.makeChanceResult()) && (!(monster.isBuffed(MonsterTemporaryStat.BodyPressure)))) {
                                monster.applyStatus(player, new MonsterTemporaryStatEffect(MonsterTemporaryStat.BodyPressure, Integer.valueOf(1), eff.getSourceId(), null, false), false, eff.getX() * 1000, true, eff);
                            }
                        }
                    }
                    if (totDamageToOneMonster > 0) {
                        Item weapon_ = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
                        if (weapon_ != null) {
                            MonsterTemporaryStat stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
                            if ((stat != null) && (Randomizer.nextInt(100) < GameConstants.getStatChance())) {
                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(stat, Integer.valueOf(GameConstants.getXForStat(stat)), GameConstants.getSkillForStat(stat), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, 10000L, false, null);
                            }
                        }
                        if (player.getBuffedValue(CharacterTemporaryStat.Blind) != null) {
                            MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.Blind);
                            if ((eff != null) && (eff.makeChanceResult())) {
                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Acc, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((player.getJob() == 121) || (player.getJob() == 122)) {
                            if (attack.skill == 1211002) {
                                int[] skills = {1211004, 1211006, 1211008, 1221004};
                                for (int i : skills) {
                                    Skill skill = SkillFactory.getSkill(i);
                                    if (player.isBuffFrom(CharacterTemporaryStat.WeaponCharge, skill)) {
                                        MapleStatEffect eff = skill.getEffect(player.getTotalSkillLevel(skill));
                                        if (skill.getId() == 1211004) {
                                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Poison, 1, skill.getId(), null, false);
                                                monster.applyStatus(player, monsterStatusEffect, false, eff.getDOTTime() * 1000, true, eff);
                                            }
                                        } else if (skill.getId() == 1211006) {
                                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Frozen, 1, skill.getId(), null, false);
                                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                                            }
                                        } else if (skill.getId() == 1211008) {
                                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Stun, 1, skill.getId(), null, false);
                                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                                            }
                                        } else if (skill.getId() == 1221004) {
                                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Seal, 1, skill.getId(), null, false);
                                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        /* Hayato */
                        if (player.getJob() > 6000 && player.getJob() < 6213) {
                            final Skill mercliessBlade = SkillFactory.getSkill(61110007);
                            final Skill Bloodletter = SkillFactory.getSkill(61120007);
                            if (mercliessBlade != null && Bloodletter != null) {
                                int BloodletterLevel = player.getTotalSkillLevel(Bloodletter);
                                if (BloodletterLevel > 0) {
                                    final MapleStatEffect eff = Bloodletter.getEffect(BloodletterLevel);
                                    if (eff != null) {
                                        if (Randomizer.nextInt(100) < eff.getProb()) {
                                            MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Poison, 1, 61120007, null, false);
                                            monster.applyStatus(player, monsterStatusEffect, true, eff.getDOTTime() * 1000, true, eff);
                                        }
                                    }
                                } else {
                                    int mercliessBladeLevel = player.getTotalSkillLevel(mercliessBlade);
                                    if (mercliessBladeLevel > 0) {
                                        final MapleStatEffect eff = Bloodletter.getEffect(mercliessBladeLevel);
                                        if (eff != null) {
                                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                                MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(MonsterTemporaryStat.Poison, 1, 61110007, null, false);
                                                monster.applyStatus(player, monsterStatusEffect, true, eff.getDOTTime() * 1000, true, eff);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //monster.ca
                    if ((effect != null) && (effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(player, new MonsterTemporaryStatEffect((MonsterTemporaryStat) z.getKey(), (Integer) z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), attack.skill == 5221015 || attack.skill == 1221052 ? false : true, effect);
                        }
                    }
                }
            }
            if (player.haveItem(3994514, 1, false, true)) {
                if (monster != null) {
                    if (monster.getStats() != null) {
                        //  if (monster.getStats().getLevel() + 20 > player.getLevel() && monster.getStats().getLevel() - 20 <= player.getLevel()) {
                        //player.addSaintSaver(1);
                        //  }
                    }
                }
            }
        }

        if (player.getBuffedValue(CharacterTemporaryStat.Unk_0x400000_8) != null) {
            MapleStatEffect powerTransfer = SkillFactory.getSkill(65101002).getEffect(player.getSkillLevel(65101002));
            player.setPowerTransfer((int) (player.getPowerTransfer() + totDamage * 0.01));
            if (player.getPowerTransfer() >= 9999) {
                player.setPowerTransfer(9999);
            }
            powerTransfer.applyTo(player);
        }
        if (attack.skill == 3111008) {
            double ss = (theSkill.getEffect(3111008).getX() / 100d);
            double recoverHP = totDamage * ss;
            player.addHP((int) recoverHP);
            player.getClient().sendPacket(CField.EffectPacket.showOwnBuffEffect(3111008, 7, player.getLevel(), 1));
            player.getMap().broadcastMessage(player, CField.EffectPacket.showBuffeffect(player.getId(), 3111008, 7, player.getLevel(), 1), player.getTruePosition());
        }
        if ((attack.skill == 4331003) && ((hpMob <= 0L) || (totDamageToOneMonster < hpMob))) {
            return;
        }
        if ((hpMob > 0L) && (totDamageToOneMonster > 0)) {
            player.afterAttack(attack.targets, attack.hits, attack.skill);
        }
        if (attack.skill != 0) {
            if (attack.skill != 1311005 && !GameConstants.isAtomSkill(attack.skill)) {
                if (effect != null) {
                    effect.applyTo(player, attack.position);
                }
            }
        }
    }

    public static final void applyAttackMagic(AttackInfo attack, Skill theSkill, MapleCharacter player, MapleStatEffect effect, double maxDamagePerHit) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        if ((attack.hits > 0) && (attack.targets > 0) && (!player.getStat().checkEquipDurabilitys(player, -2))) {
            player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            return;
        }

        if (GameConstants.isMulungSkill(attack.skill)) {
            if (player.getMapId() / 10000 != 92502) {
                return;
            }
            if (player.getMulungEnergy() < 10000) {
                return;
            }
            player.mulung_EnergyModify(false);
        } else if (GameConstants.isPyramidSkill(attack.skill)) {
            if (player.getMapId() / 1000000 != 926) {
                return;
            }
            if ((player.getPyramidSubway() == null) || (!player.getPyramidSubway().onSkillUse(player))) {
                return;
            }
        } else if ((GameConstants.isInflationSkill(attack.skill))
                && (player.getBuffedValue(CharacterTemporaryStat.GiantPotion) == null)) {
            return;
        }
        PlayerStats stats = player.getStat();
        Element element = player.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null ? Element.NEUTRAL : theSkill.getElement();

        double MaxDamagePerHit = 0.0D;
        int totDamage = 0;

        int CriticalDamage = stats.passive_sharpeye_percent();
        Skill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(player.getJob()));
        int eaterLevel = player.getTotalSkillLevel(eaterSkill);

        MapleMap map = player.getMap();

        for (AttackPair oned : attack.allDamage) {
            MapleMonster monster = map.getMonsterByOid(oned.objectid);

            if ((monster != null) && (monster.getLinkCID() <= 0)) {
                boolean Tempest = (monster.getStatusSourceID(MonsterTemporaryStat.Frozen) == 21120006) && (!monster.getStats().isBoss());
                int totDamageToOneMonster = 0;
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                if ((!Tempest) && (!player.isGM())) {
                    if ((!monster.isBuffed(MonsterTemporaryStat.MImmune)) && (!monster.isBuffed(MonsterTemporaryStat.MCounter))) {
                        MaxDamagePerHit = CalculateMaxMagicDamagePerHit(player, theSkill, monster, monsterstats, stats, element, Integer.valueOf(CriticalDamage), maxDamagePerHit, effect);
                    } else {
                        MaxDamagePerHit = 1.0D;
                    }
                }
                byte overallAttackCount = 0;

                for (Pair eachde : oned.attack) {
                    Integer eachd = (Integer) eachde.left;
                    overallAttackCount = (byte) (overallAttackCount + 1);
                    if (fixeddmg != -1 && eachd != 0) {
                        eachd = Integer.valueOf(monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg);
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = Integer.valueOf(0);
                    } else if (!player.isGM()) {
                        if (player.getLevel() < 49 && eachd > 199999) {
                            FileoutputUtil.log("log/[" + getDCurrentTime() + "] hack_highDamage.txt", "[" + FileoutputUtil.CurrentReadable_Time() + "]\r\n캐릭터 : " + player.getName() + "(Lv." + player.getLevel() + ")\r\n스킬 : " + theSkill.getName() + "(" + theSkill.getId() + ")\r\n데미지 : " + eachd + "\r\n맵 : " + player.getMap().getMapName() + "(" + player.getMapId() + ")\r\n");
                        }
                        if (player.getLevel() < 99 && eachd > 799999) {
                            FileoutputUtil.log("log/[" + getDCurrentTime() + "] hack_highDamage.txt", "[" + FileoutputUtil.CurrentReadable_Time() + "]\r\n캐릭터 : " + player.getName() + "(Lv." + player.getLevel() + ")\r\n스킬 : " + theSkill.getName() + "(" + theSkill.getId() + ")\r\n데미지 : " + eachd + "\r\n맵 : " + player.getMap().getMapName() + "(" + player.getMapId() + ")\r\n");
                        }
                        if ((player.getLevel() < 256 && eachd > 99999999)) {
                            FileoutputUtil.log("log/[" + getDCurrentTime() + "] hack_highDamage.txt", "[" + FileoutputUtil.CurrentReadable_Time() + "]\r\n캐릭터 : " + player.getName() + "(Lv." + player.getLevel() + ")\r\n스킬 : " + theSkill.getName() + "(" + theSkill.getId() + ")\r\n데미지 : " + eachd + "\r\n맵 : " + player.getMap().getMapName() + "(" + player.getMapId() + ")\r\n");
                        }
                        if (eachd > maxDamagePerHit) {
                            //eachd = Integer.valueOf((int) MaxDamagePerHit);
                        }
                    }
                    totDamageToOneMonster += eachd.intValue();
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if (totDamageToOneMonster > 0) {
                    if (player.getACDamage() > 0) {
                        totDamageToOneMonster += player.getACDamage();
                        player.getMap().broadcastMessage(CMobPool.damaged(monster.getObjectId(), player.getACDamage()));
                    }
                    monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    if (monster.isBuffed(MonsterTemporaryStat.MCounter)) {
                        Item eqi = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -56);
                        if (!(eqi != null && eqi.getItemId() == 1182999)) {
                            player.addHP(-(7000 + Randomizer.nextInt(8000)));
                        }
                    }
                    if (player.getBuffedValue(CharacterTemporaryStat.Slow) != null) {
                        MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.Slow);

                        if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterTemporaryStat.Speed))) {
                            monster.applyStatus(player, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Speed, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                        }

                    }

                    player.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.skill, monster.getObjectId(), totDamage, 0);

                    if (GameConstants.isLuminous(player.getJob())) {
                        Integer Gauge = player.getBuffedValue(CharacterTemporaryStat.Larkness);
                        if (player.getBuffedValue(CharacterTemporaryStat.Larkness) == null || player.getBuffedValue(CharacterTemporaryStat.Larkness) == -1) {
                            if (GameConstants.isDarkSkills(attack.skill)) {
                                player.getSunfireBuffedValue(20040216, attack.skill, Gauge);
                            } else {
                                player.getEclipseBuffedValue(20040217, attack.skill, Gauge);
                            }
                        } else {
                            if (player.getBuffedValue(CharacterTemporaryStat.Larkness) == 20040216) {
                                player.getSunfireBuffedValue(20040216, attack.skill, Gauge);
                            } else if (player.getBuffedValue(CharacterTemporaryStat.Larkness) == 20040217) {
                                player.getEclipseBuffedValue(20040217, attack.skill, Gauge);
                            }
                        }
                    }
                    switch (player.getJob()) {
                        case 212:
                        case 222:
                        case 232: {
                            int skillID = 2120010;
                            if (player.getJob() == 222) {
                                skillID = 2220010;
                            }
                            if (player.getJob() == 232) {
                                skillID = 2320011;
                            }
                            final Skill aSkill = SkillFactory.getSkill(skillID);
                            if (aSkill != null) {
                                int aLevel = player.getTotalSkillLevel(aSkill);
                                if (aLevel > 0) {
                                    final MapleStatEffect aEffect = aSkill.getEffect(aLevel);
                                    if (aEffect != null) {
                                        int cRand = Randomizer.nextInt(100);
                                        if (player.getArcaneAim() < 6) {
                                            player.setArcaneAim(player.getArcaneAim() < 5 ? (player.getArcaneAim() + 1) : 5);
                                            int aDamR = (aEffect.getX() * player.getArcaneAim());
                                            int aDuration = 1999;
                                            player.getClient().sendPacket(CWvsContext.BuffPacket.giveArcane(aDamR, skillID, aDuration));
                                            EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                                            final long starttime = System.currentTimeMillis();
                                            final CancelEffectAction cancelAction = new CancelEffectAction(player, aEffect, starttime, localstatups);
                                            final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, aDuration);
                                            player.registerEffect(aEffect, starttime, schedule, player.getId());
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }

                    if (GameConstants.isBindSkill(attack.skill)) {
                        monster.setTempEffectiveness(Element.ICE, 1);
                    }

                    switch (attack.skill) {
                        case 2211006:
                        case 2221003:
                        case 22121000: {
                            monster.setTempEffectiveness(Element.ICE, effect.getDuration());
                            break;
                        }
                        case 2111006:
                        case 22171003: {
                            monster.setTempEffectiveness(Element.FIRE, effect.getDuration());
                            break;
                        }
                    }

                    if ((effect != null && attack.skill == 2221003) || (effect != null && attack.skill == 2211006) || (effect != null && attack.skill == 2111006)) { //이래처리해야하는 내가밉다 ㅠㅠㅠ
                        Skill fire = SkillFactory.getSkill(2121007);
                        Skill ice = SkillFactory.getSkill(2221007);
                        MapleStatEffect eff = attack.skill == 2111006 ? fire.getEffect(1) : ice.getEffect(1);
                        MonsterTemporaryStatEffect monsterStatusEffect = new MonsterTemporaryStatEffect(attack.skill == 2111006 ? MonsterTemporaryStat.Blind : MonsterTemporaryStat.Frozen, attack.skill == 2111006 ? Integer.valueOf(0) : Integer.valueOf(1), attack.skill == 2111006 ? fire.getId() : ice.getId(), null, false);
                        monster.applyStatus(player, monsterStatusEffect, false, attack.skill == 2221003 ? effect.getDuration() : effect.getX() * 1000, true, eff);
                    }
                    if ((effect != null) && (effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(player, new MonsterTemporaryStatEffect((MonsterTemporaryStat) z.getKey(), (Integer) z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), attack.skill == 22161002 ? false : true, effect);
                        }
                    }
                    if (eaterLevel > 0) {
                        eaterSkill.getEffect(eaterLevel).applyPassive(player, monster);
                    }
                }
                if (player.haveItem(3994514, 1, false, true)) {
                    if (monster != null) {
                        if (monster.getStats() != null) {
                            if (monster.getStats().getLevel() + 20 > player.getLevel() && monster.getStats().getLevel() - 20 <= player.getLevel()) {
                                //player.addSaintSaver(1);
                            }
                        }
                    }
                }
            }
        }

        effect.applyTo(player);

    }

    private static final double CalculateMaxMagicDamagePerHit(MapleCharacter chr, Skill skill, MapleMonster monster, MapleMonsterStats mobstats, PlayerStats stats, Element elem, Integer sharpEye, double maxDamagePerMonster, MapleStatEffect attackEffect) {
        int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(stats.getAccuracy())) - (int) Math.floor(Math.sqrt(mobstats.getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        if ((HitRate <= 0) && ((!GameConstants.isBeginnerJob(skill.getId() / 10000)) || (skill.getId() % 10000 != 1000))) {
            return 0.0D;
        }

        int CritPercent = sharpEye.intValue();
        ElementalEffectiveness ee = monster.getEffectiveness(elem);
        double elemMaxDamagePerMob;
        switch (ee) {
            case IMMUNE:
                elemMaxDamagePerMob = 1.0D;
                break;
            default:
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * ee.getValue(), stats);
        }

        int MDRate = monster.getStats().getMDRate();
        MonsterTemporaryStatEffect pdr = monster.getBuff(MonsterTemporaryStat.Mdr);
        if (pdr != null) {
            MDRate += pdr.getX().intValue();
        }
        elemMaxDamagePerMob -= elemMaxDamagePerMob * (Math.max(MDRate - stats.ignoreTargetDEF - attackEffect.getIgnoreMob(), 0) / 100.0D);

        elemMaxDamagePerMob += elemMaxDamagePerMob / 100.0D * CritPercent;

        elemMaxDamagePerMob *= (monster.getStats().isBoss() ? chr.getStat().bossdam_r : chr.getStat().dam_r) / 100.0D;
        MonsterTemporaryStatEffect imprint = monster.getBuff(MonsterTemporaryStat.GhostLettering);
        if (imprint != null) {
            elemMaxDamagePerMob += elemMaxDamagePerMob * imprint.getX().intValue() / 100.0D;
        }
        elemMaxDamagePerMob += elemMaxDamagePerMob * chr.getDamageIncrease(monster.getObjectId()) / 100.0D;
        if (GameConstants.isBeginnerJob(skill.getId() / 10000)) {
            switch (skill.getId() % 10000) {
                case 1000:
                    elemMaxDamagePerMob = 40.0D;
                    break;
                case 1020:
                    elemMaxDamagePerMob = 1.0D;
                    break;
                case 1009:
                    elemMaxDamagePerMob = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
            }
        }

        switch (skill.getId()) {
            case 32001000:
            case 32101000:
            case 32111002:
            case 32121002:
                elemMaxDamagePerMob *= 1.5D;
        }

        if (elemMaxDamagePerMob > 999999.0D) {
            elemMaxDamagePerMob = 999999.0D;
        } else if (elemMaxDamagePerMob <= 0.0D) {
            elemMaxDamagePerMob = 1.0D;
        }

        return elemMaxDamagePerMob;
    }

    private static final double ElementalStaffAttackBonus(final Element elem, double elemMaxDamagePerMob, final PlayerStats stats) {
        switch (elem) {
            case FIRE:
                return (elemMaxDamagePerMob / 100) * (stats.element_fire + stats.getElementBoost(elem));
            case ICE:
                return (elemMaxDamagePerMob / 100) * (stats.element_ice + stats.getElementBoost(elem));
            case LIGHTING:
                return (elemMaxDamagePerMob / 100) * (stats.element_light + stats.getElementBoost(elem));
            case POISON:
                return (elemMaxDamagePerMob / 100) * (stats.element_psn + stats.getElementBoost(elem));
            default:
                return (elemMaxDamagePerMob / 100) * (stats.def + stats.getElementBoost(elem));
        }
    }

    private static void handlePickPocket(final MapleCharacter player, final MapleMonster mob, AttackPair oned) {
        final int maxmeso = player.getBuffedValue(CharacterTemporaryStat.PickPocket).intValue();
        int delay = 0;
        for (final Pair<Integer, Boolean> eachde : oned.attack) {
            final Integer eachd = eachde.left;
            if (player.getStat().pickRate >= 100 || Randomizer.nextInt(99) < player.getStat().pickRate) {
                Timer.EtcTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.getMap().spawnMesoDrop(Math.min((int) Math.max(((double) eachd / (double) 20000) * (double) maxmeso, (double) 1), maxmeso), new Point((int) (mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50), (int) (mob.getTruePosition().getY())), mob, player, true, (byte) 0);
                    }
                }, delay);
                delay += 100;
            }
        }
    }

    private static double CalculateMaxWeaponDamagePerHit(MapleCharacter player, MapleMonster monster, AttackInfo attack, Skill theSkill, MapleStatEffect attackEffect, double maximumDamageToMonster, Integer CriticalDamagePercent) {
        int dLevel = Math.max(monster.getStats().getLevel() - player.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(player.getStat().getAccuracy())) - (int) Math.floor(Math.sqrt(monster.getStats().getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        if ((HitRate <= 0) && ((!GameConstants.isBeginnerJob(attack.skill / 10000)) || (attack.skill % 10000 != 1000)) && (!GameConstants.isPyramidSkill(attack.skill)) && (!GameConstants.isMulungSkill(attack.skill)) && (!GameConstants.isInflationSkill(attack.skill))) {
            return 0.0D;
        }
        if ((player.getMapId() / 1000000 == 914) || (player.getMapId() / 1000000 == 927)) {
            return 999999.0D;
        }

        List<Element> elements = new ArrayList<Element>();
        boolean defined = false;
        int CritPercent = CriticalDamagePercent.intValue();
        int PDRate = monster.getStats().getPDRate();
        MonsterTemporaryStatEffect pdr = monster.getBuff(MonsterTemporaryStat.Pdr);
        if (pdr != null) {
            PDRate += pdr.getX().intValue();
        }
        if (theSkill != null) {
            elements.add(theSkill.getElement());
            if (GameConstants.isBeginnerJob(theSkill.getId() / 10000)) {
                switch (theSkill.getId() % 10000) {
                    case 1000:
                        maximumDamageToMonster = 40.0D;
                        defined = true;
                        break;
                    case 1020:
                        maximumDamageToMonster = 1.0D;
                        defined = true;
                        break;
                    case 1009:
                        maximumDamageToMonster = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
                        defined = true;
                }
            }

            switch (theSkill.getId()) {
                case 1311005:
                    PDRate = monster.getStats().isBoss() ? PDRate : 0;
                    break;
                case 3221001:
                case 33101001:
                    maximumDamageToMonster *= attackEffect.getMobCount();
                    defined = true;
                    break;
                case 3101005:
                    defined = true;
                    break;
                case 32001000:
                case 32101000:
                case 32111002:
                case 32121002:
                    maximumDamageToMonster *= 1.5D;
                    break;
                case 1221009:
                case 3221007:
                case 4331003:
                case 23121003:
                    if (monster.getStats().isBoss()) {
                        break;
                    }
                    maximumDamageToMonster = monster.getMobMaxHp();
                    defined = true;
                    break;
                case 1221011:
                case 21120006:
                    maximumDamageToMonster = monster.getStats().isBoss() ? 500000L : monster.getHp() - 1L;
                    defined = true;
                    break;
                case 3211006:
                    if (monster.getStatusSourceID(MonsterTemporaryStat.Frozen) != 3211003) {
                        break;
                    }
                    defined = true;
                    maximumDamageToMonster = 999999.0D;
            }

        }
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if ((player.getJob() == 311) || (player.getJob() == 312) || (player.getJob() == 321) || (player.getJob() == 322)) {
            Skill mortal = SkillFactory.getSkill((player.getJob() == 311) || (player.getJob() == 312) ? 3110001 : 3210001);
            if (player.getTotalSkillLevel(mortal) > 0) {
                MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
                    elementalMaxDamagePerMonster = 999999.0D;
                    defined = true;
                    if (mort.getZ() > 0) {
                        player.addHP(player.getStat().getMaxHp() * mort.getZ() / 100);
                        player.getClient().sendPacket(CMobPool.getMobSkillEffect(monster.getObjectId(), 3110001, player.getId(), 1));
                    }
                }
            }
        } else if ((player.getJob() == 221) || (player.getJob() == 222)) {
            Skill mortal = SkillFactory.getSkill(2210000);
            if (player.getTotalSkillLevel(mortal) > 0) {
                MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
                    elementalMaxDamagePerMonster = 999999.0D;
                    defined = true;
                }
            }
        }
        if ((!defined) || ((theSkill != null) && ((theSkill.getId() == 33101001) || (theSkill.getId() == 3221001)))) {
            if (player.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
                int chargeSkillId = player.getBuffSource(CharacterTemporaryStat.WeaponCharge);
                switch (chargeSkillId) {
                    case 1211003:
                    case 1211004:
                        elements.add(Element.FIRE);
                        break;
                    case 1211005:
                    case 1211006:
                    case 21111005:
                        elements.add(Element.ICE);
                        break;
                    case 1211007:
                    case 1211008:
                    case 15101006:
                        elements.add(Element.LIGHTING);
                        break;
                    case 1221003:
                    case 1221004:
                        elements.add(Element.HOLY);
                        break;
                    case 12101005:
                }

            }

            if (player.getBuffedValue(CharacterTemporaryStat.AssistCharge) != null) {
                elements.add(Element.LIGHTING);
            }
            if (player.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null) {
                elements.clear();
            }
            double elementalEffect;
            if (elements.size() > 0) {
                switch (attack.skill) {
                    case 3111003:
                    case 3211003:
                        elementalEffect = attackEffect.getX() / 100.0D;
                        break;
                    default:
                        elementalEffect = 0.5D / elements.size();
                }

                for (Element element : elements) {
                    switch (monster.getEffectiveness(element)) {
                        case IMMUNE:
                            elementalMaxDamagePerMonster = 1;
                            break;
                        case WEAK:
                            elementalMaxDamagePerMonster *= (1.0 + elementalEffect + player.getStat().getElementBoost(element));
                            break;
                        case STRONG:
                            elementalMaxDamagePerMonster *= (1.0 - elementalEffect - player.getStat().getElementBoost(element));
                            break;
                    }
                }
            }

            elementalMaxDamagePerMonster -= elementalMaxDamagePerMonster * (Math.max(PDRate - Math.max(player.getStat().ignoreTargetDEF, 0) - Math.max(attackEffect == null ? 0 : attackEffect.getIgnoreMob(), 0), 0) / 100.0D);

            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster / 100.0D * CritPercent;

            MonsterTemporaryStatEffect imprint = monster.getBuff(MonsterTemporaryStat.GhostLettering);
            if (imprint != null) {
                elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * imprint.getX().intValue() / 100.0D;
            }

            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * player.getDamageIncrease(monster.getObjectId()) / 100.0D;
            elementalMaxDamagePerMonster *= ((monster.getStats().isBoss()) && (attackEffect != null) ? player.getStat().bossdam_r + attackEffect.getBossDamage() : player.getStat().dam_r) / 100.0D;
        }
        if (elementalMaxDamagePerMonster > 999999.0D) {
            if (!defined) {
                elementalMaxDamagePerMonster = 999999.0D;
            }
        } else if (elementalMaxDamagePerMonster <= 0.0D) {
            elementalMaxDamagePerMonster = 1.0D;
        }
        return elementalMaxDamagePerMonster;
    }

    public static final AttackInfo DivideAttack(final AttackInfo attack, final int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack; //lol
        }
        for (AttackPair p : attack.allDamage) {
            if (p.attack != null) {
                for (Pair<Integer, Boolean> eachd : p.attack) {
                    eachd.left /= rate; //too ex.
                }
            }
        }
        return attack;
    }

    public static final AttackInfo parseDmgMa(LittleEndianAccessor lea, MapleCharacter chr) {
        try {
            AttackInfo ret = new AttackInfo();

            lea.skip(1);
            ret.tbyte = lea.readByte();

            ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
            ret.hits = (byte) (ret.tbyte & 0xF);
            ret.skill = lea.readInt();
            lea.skip(1);//스킬레벨
            if (ret.skill >= 91000000) {
                return null;
            }
            lea.skip(3);
            ret.attackType = lea.readByte();
            if (GameConstants.isMagicChargeSkill(ret.skill)) {
                ret.charge = lea.readInt();
            } else {
                ret.charge = -1;
            }
            ret.unk = lea.readByte();
            //System.out.println("unk : " + ret.unk);
            ret.display = lea.readUShort();
            //System.out.println("display : " + ret.display);
            lea.skip(4);
            lea.skip(1);
            ret.speed = lea.readByte();
            ret.lastAttackTickCount = lea.readInt();
            lea.skip(4);

            ret.allDamage = new ArrayList();
            int oid, damage;
            byte hitAction;
            for (int i = 0; i < ret.targets; i++) {
                oid = lea.readInt();
                hitAction = lea.readByte();
                //lea.readByte();
                lea.readByte();
                lea.readByte();
                lea.readInt();
                lea.readByte();
                lea.readShort();
                lea.readShort();
                lea.readShort();
                lea.readShort();
                lea.readShort();
                //
                List allDamageNumbers = new ArrayList();
                for (int j = 0; j < ret.hits; j++) {
                    damage = lea.readInt();
                    chr.rankatt = (chr.rankatt + damage);

                    boolean critical = showCriticalDamage(chr, damage, oid, ret.skill);
                    allDamageNumbers.add(new Pair(Integer.valueOf(damage), critical));
                }

                lea.readInt();
                //lea.readInt();
                ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers, hitAction));

            }
            ret.position = lea.readPos();
            lea.skip(1);// 엉ㄴ제생긴거지 ㄷㄷ
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final AttackInfo parseDmgM(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();
        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        lea.readInt();
        ret.attackType = lea.readByte();
        switch (ret.skill) {
            case 21101003:  // 바디 프레셔
            case 2111007:   // 텔레포트 마스터리
            case 2211007:   // 텔레포트 마스터리
            case 2311007:   // 텔레포트 마스터리
            case 12111007:  // 텔레포트 마스터리
            case 22161005:  // 텔레포트 마스터리
            case 32111010:  // 텔레포트 마스터리
                ret.charge = 0;
                ret.unk = lea.readByte();
                ret.display = lea.readUShort();
                lea.skip(4);// dunno
                lea.skip(1);//무기타입
                ret.speed = (byte) lea.readByte();
                ret.lastAttackTickCount = lea.readInt();
                lea.skip(4);// looks like zeroes
                ret.allDamage = new ArrayList();
                int oid,
                 damage;
                byte hitAction;
                for (int i = 0; i < ret.targets; i++) {
                    oid = lea.readInt();
                    hitAction = lea.readByte();
                    lea.readByte();
                    lea.readByte();
                    lea.readInt();
                    lea.readByte();
                    lea.readShort();
                    lea.readShort();
                    lea.readShort();
                    lea.readShort();
                    lea.readShort();
                    List allDamageNumbers = new ArrayList();
                    for (int j = 0; j < ret.hits; j++) {
                        damage = lea.readInt();
                        boolean critical = showCriticalDamage(chr, damage, oid, ret.skill);
                        allDamageNumbers.add(new Pair(Integer.valueOf(damage), critical));
                    }
                    lea.skip(4);
                    ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers, hitAction));
                }
                ret.position = lea.readPos();
                return ret;
            case 1311011:   // 라만차 스피어
            case 2221011:   // 프리징 브레스
            case 2221012:   // 프로즌 오브
            case 2321015:   // 디바인 퍼니시먼트
            case 4341002:   // 파이널 컷
            case 4341003:   // 몬스터 봄
            case 4341014:   // 아수라
            case 4341019:   // 블레이드 스톰
            case 5201002:   // 스로잉 봄
            case 5301001:   // 몽키 러쉬붐
            case 5300007:   // 몽키 러쉬붐
            case 11121029:  // 코스모 샤워
            case 12111022:  // 마엘스트롬
            case 12121006:  // 드래곤 슬레이브
            case 12121007:  // 인피니티 플레임 서클
            case 13121001:  // 천공의 노래
            case 14111006:  // 포이즌 봄
            case 14111022:  // 스타 더스트
            case 14121003:  // 다크니스 오멘
            case 14121004:  // 쉐도우 스티치
            case 20041226:  // 스펙트럴 라이트
            case 21121018:  // 스톰 오브 피어
            case 21121029:  // 헌터즈 타겟팅
            case 21121068:  // 마하의 영역
            case 24121000:  // 얼티밋 드라이브
            case 27120211:  // 모닝 스타폴
            case 27121201:  // 모닝 스타폴
            case 31001000:  // 데빌 사이더
            case 31101000:  // 소울 이터
            case 31111005:  // 데모닉 브레스
            case 35121052:  // 디스토션 필드
            case 52121004:  // 페니 토네이도
            case 62111007:  // 노파의 분노
            case 62111008:  // 식신초래
            case 62121000:  // 파사 연격부
            case 62121001:  // 요회해방
            case 62121011:  // 설녀
            case 65111002:  // 난권연격
                ret.charge = lea.readInt();
                break;
            default:
                ret.charge = 0;
                break;
        }
        ret.unk = lea.readByte();
        ret.display = lea.readUShort();
        if ((ret.display < 0 || ret.display > 10)) {
        }
        lea.skip(4);
        lea.skip(1);
        if ((ret.skill == 12121007) || (ret.skill == 14121003) || (ret.skill == 5300007) || (ret.skill == 5101012) || (ret.skill == 5081001) || (ret.skill == 15101010) || (ret.skill == 24121005) || (ret.skill == 5221006)) {
            lea.readInt();
        }
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        if (ret.skill == 32121003 || ret.skill == 0x447) {
            lea.skip(4);
        } else if (ret.skill == 1120013 || GameConstants.isFinalAttackskill(ret.skill)) { // 파이널어택
            lea.skip(9);// ??
        } else {
            lea.readInt();
            lea.readInt();
        }
        if (ret.skill == 31220007) {
            lea.skip(1);
        }

        ret.allDamage = new ArrayList();

        if (ret.skill == 4211006 || ret.skill == 62111002) {
            return parseMesoExplosion(lea, ret, chr);
        }

        int damage, oid;
        byte hitAction, a, b, c;
        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
            hitAction = lea.readByte();
            a = lea.readByte();
            b = lea.readByte();
            ret.mobId = lea.readInt();
            c = lea.readByte();
            lea.readShort();
            lea.readShort();
            lea.readShort();
            lea.readShort();
            lea.readShort();
            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < ret.hits; j++) {
                damage = lea.readInt();
                damage = getDamage(chr, ret.mobId, damage);

                boolean critical = showCriticalDamage(chr, damage, oid, ret.skill);
                allDamageNumbers.add(new Pair(Integer.valueOf(damage), critical));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers, hitAction));

            final MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            if (monster != null) {
                if (monster.isBuffed(MonsterTemporaryStat.PCounter)) {
                    chr.getClient().sendPacket(CField.OnDamageByUser(chr.getId(), ret.hits, ret.allDamage));
                }
            }

        }
        ret.position = lea.readPos();
        if (ret.skill == 14111006) {
            final Skill poisonBomb = SkillFactory.getSkill(14111006);
            final MapleStatEffect poison = poisonBomb.getEffect(chr.getTotalSkillLevel(poisonBomb));
            final Rectangle bounds = poison.calculateBoundingBox(ret.position = lea.readPos(), chr.isFacingLeft());
            final MapleMist mist = new MapleMist(bounds, chr, poison);
            chr.getMap().spawnMist(mist, poison.getDuration(), false);
        }
        return ret;
    }

    public static final AttackInfo parseDmgR(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();
        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        lea.skip(1);
        lea.readInt();
        ret.attackType = lea.readByte();
        switch (ret.skill) {
            case 3121004:   // 폭풍의 시
            case 3221001:   // 피어싱
            case 5221004:   // 래피드 파이어
            case 5221025:   // 불릿 파티
            case 5311002:   // 몽키 웨이브
            case 13111002:  // 폭풍의 시
            case 13111020:  // 서리 바람의 군무
            case 13121001:  // 천공의 노래
            case 14121003:  // 다크니스 오멘
            case 23121000:  // 이슈타르의 링
            case 33121009:  // 와일드 발칸
            case 35001001:  // 플레임 런처
            case 35101009:  // 강화된 플레임 런처
            case 65111002:  // 난권연격
                lea.skip(4);
                break;
        }
        ret.unk = lea.readByte();
        ret.display = lea.readShort();
        lea.skip(4);//crc인가 뭔지모름
        lea.skip(1);//무기클래스 활(03)
        if (ret.skill == 23111001 || ret.skill == 36111010) {
            lea.skip(12);
        }
        ret.speed = lea.readByte();//부스터안쓰면 5 부스터쓰면 3
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);//언제나0이랑께
        ret.slot = (byte) lea.readShort();
        ret.csstar = (byte) lea.readShort();
        ret.AOE = lea.readByte();
        ret.allDamage = new ArrayList();
        int oid, damage;
        byte hitAction;
        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
            hitAction = lea.readByte();
            lea.readByte();
            lea.readByte();
            lea.readInt(); //mobid
            lea.readByte();
            lea.readShort();
            lea.readShort();
            lea.readShort();
            lea.readShort();
            lea.readShort();
            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < ret.hits; j++) {
                damage = lea.readInt();
                if (chr.getBuffSource(CharacterTemporaryStat.Pad) == 5211009) { //cross cut blast
                    int attacksLeft = chr.getCData(chr, 5211009);
                    if (attacksLeft <= 0) {
                        chr.cancelEffectFromBuffStat(CharacterTemporaryStat.Pad);
                    } else {
                        chr.setCData(5211009, -1);
                    }
                }

                boolean critical = showCriticalDamage(chr, damage, oid, ret.skill);
                allDamageNumbers.add(new Pair(Integer.valueOf(damage), critical));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers, hitAction));
        }
        lea.skip(4);//캐릭터좌표
        ret.position = lea.readPos();
        return ret;
    }

    public static final AttackInfo parseDmgSummon(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        ret.skill = lea.readInt();//오브젝트아이디
        final MapleMap map = chr.getMap();
        final MapleMapObject obj = map.getMapObject(ret.skill, MapleMapObjectType.SUMMON);
        final MapleSummon summon = (MapleSummon) obj;
        ret.lastAttackTickCount = lea.readInt();
        ret.display = lea.readByte();//애니메이션
        ret.tbyte = lea.readByte();
        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        //System.out.println("ret.targets : " + ret.targets);
        //System.out.println("ret.hits : " + ret.hits);
        lea.skip(summon.getSkill() == 35111002 ? 24 : 12); //서먼좌표 몹좌표 캐릭터좌표
        ret.allDamage = new ArrayList();
        int oid, damage;
        byte hitAction;
        for (int i = 0; i < ret.tbyte; i++) {
            oid = lea.readInt();
            //System.out.println("oid : " + oid);
            int mobid = lea.readInt();
            hitAction = lea.readByte();
            byte unk1 = lea.readByte();
            //byte unk2 = lea.readByte();
            byte unk3 = lea.readByte();
            int mobid2 = lea.readInt();
            byte unk4 = lea.readByte();
            Point damagePos = lea.readPos();
            Point realDamagePos = lea.readPos();
            short attackDelay = lea.readShort(); // 맞는지 아닌지 모르겠다
            List allDamageNumbers = new ArrayList();
            //for (int j = 0; j < ret.hits; j++) {
            damage = lea.readInt();
            //chr.dropMessage(5, "damage : " + damage);
            //System.out.println("damage : " + damage);
            allDamageNumbers.add(new Pair(Integer.valueOf(damage), Boolean.valueOf(false)));
            // }
            //lea.skip(4); 
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers, hitAction));
        }
        return ret;
    }

    public static final AttackInfo parseMesoExplosion(LittleEndianAccessor lea, AttackInfo ret, MapleCharacter chr) {
        if (ret.hits == 0) {
            lea.skip(4);
            byte bullets = lea.readByte();
            for (int j = 0; j < bullets; j++) {
                ret.allDamage.add(new AttackPair(Integer.valueOf(lea.readInt()), null, (byte) 7));
                lea.skip(1);
            }
            lea.skip(2);
            return ret;
        }

        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            lea.skip(16);
            byte bullets = lea.readByte();
            List allDamageNumbers = new ArrayList();

            for (int j = 0; j < bullets; j++) {
                int damage = lea.readInt();
                allDamageNumbers.add(new Pair(damage, Boolean.valueOf(false)));
            }
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers, (byte) 7));
            lea.skip(4);
        }
        ret.position = lea.readPos();
        byte bullets = lea.readByte();
        for (int j = 0; j < bullets; j++) {
            ret.allDamage.add(new AttackPair(Integer.valueOf(lea.readInt()).intValue(), null, (byte) 7));//돈 oid
            lea.skip(2);
        }
        return ret;
    }

    public static boolean showCriticalDamage(MapleCharacter user, int damage, int objectID, int skillID) {
        int cRate = 5;
        if (user.getTotalSkillLevel(1120012) > 0) {
            cRate += SkillFactory.getSkill(1120012).getEffect(user.getTotalSkillLevel(1120012)).getCr();
        }
        if (user.getTotalSkillLevel(1310009) > 0) {
            cRate += SkillFactory.getSkill(1310009).getEffect(user.getTotalSkillLevel(1310009)).getCr();
        }
        if (user.getTotalSkillLevel(1320006) > 0) {
            cRate += SkillFactory.getSkill(1320006).getEffect(user.getTotalSkillLevel(1320006)).getCr();
        }
        if (user.getTotalSkillLevel(2110009) > 0) {
            cRate += SkillFactory.getSkill(2110009).getEffect(user.getTotalSkillLevel(2110009)).getCr();
        }
        if (user.getTotalSkillLevel(2210009) > 0) {
            cRate += SkillFactory.getSkill(2210009).getEffect(user.getTotalSkillLevel(2210009)).getCr();
        }
        if (user.getTotalSkillLevel(2310008) > 0) {
            cRate += SkillFactory.getSkill(2310008).getEffect(user.getTotalSkillLevel(2310008)).getCr();
        }
        if (user.getTotalSkillLevel(2310010) > 0) {
            cRate += SkillFactory.getSkill(2310010).getEffect(user.getTotalSkillLevel(2310010)).getCr();
        }
        if (user.getTotalSkillLevel(3000001) > 0) {
            cRate += SkillFactory.getSkill(3000001).getEffect(user.getTotalSkillLevel(3000001)).getProb();
        }
        if (user.getTotalSkillLevel(4100001) > 0) {
            cRate += SkillFactory.getSkill(4100001).getEffect(user.getTotalSkillLevel(4100001)).getProb();
        }
        if (user.getTotalSkillLevel(4220012) > 0) {
            cRate += SkillFactory.getSkill(4220012).getEffect(user.getTotalSkillLevel(4220012)).getCr();
        }
        if (user.getTotalSkillLevel(4340010) > 0) {
            cRate += SkillFactory.getSkill(4340010).getEffect(user.getTotalSkillLevel(4340010)).getProb();
        }
        if (user.getTotalSkillLevel(5000007) > 0) {
            cRate += SkillFactory.getSkill(5000007).getEffect(user.getTotalSkillLevel(5000007)).getCr();
        }
        if (user.getTotalSkillLevel(5100008) > 0) {
            cRate += SkillFactory.getSkill(5100008).getEffect(user.getTotalSkillLevel(5100008)).getCr();
        }
        if (user.getTotalSkillLevel(5110000) > 0) {
            final MapleMonster monster = user.getMap().getMonsterByOid(objectID);
            if (monster != null) {
                if (monster.isBuffed(MonsterTemporaryStat.Stun)) {
                    cRate += SkillFactory.getSkill(5110000).getEffect(user.getTotalSkillLevel(5110000)).getProb();
                }
            }
        }
        if (user.getTotalSkillLevel(5200007) > 0) {
            cRate += SkillFactory.getSkill(5200007).getEffect(user.getTotalSkillLevel(5200007)).getCr();
        }
        if (user.getTotalSkillLevel(5110011) > 0) {
            cRate += SkillFactory.getSkill(5110011).getEffect(user.getTotalSkillLevel(5110011)).getCr();
            final MapleMonster monster = user.getMap().getMonsterByOid(objectID);
            if (monster != null) {
                if (monster.getStats().isBoss()) {
                    cRate += SkillFactory.getSkill(5110011).getEffect(user.getTotalSkillLevel(5110011)).getProb();
                }
            }
        }
        if (user.getTotalSkillLevel(5210013) > 0) {
            cRate += SkillFactory.getSkill(5210013).getEffect(user.getTotalSkillLevel(5210013)).getCr();
        }
        if (user.getTotalSkillLevel(5300004) > 0) {
            cRate += SkillFactory.getSkill(5300004).getEffect(user.getTotalSkillLevel(5300004)).getCr();
        }
        if (user.getTotalSkillLevel(11120006) > 0) {
            cRate += SkillFactory.getSkill(11120006).getEffect(user.getTotalSkillLevel(11120006)).getCr();
        }
        if (user.getTotalSkillLevel(12110000) > 0) {
            cRate += SkillFactory.getSkill(12110000).getEffect(user.getTotalSkillLevel(12110000)).getCr();
        }
        if (user.getTotalSkillLevel(14100001) > 0) {
            cRate += SkillFactory.getSkill(14100001).getEffect(user.getTotalSkillLevel(14100001)).getProb();
        }
        if (user.getTotalSkillLevel(15000006) > 0) {
            cRate += SkillFactory.getSkill(15000006).getEffect(user.getTotalSkillLevel(15000006)).getCr();
        }
        if (user.getTotalSkillLevel(15100024) > 0) {
            cRate += SkillFactory.getSkill(15100024).getEffect(user.getTotalSkillLevel(15100024)).getCr();
        }
        if (user.getTotalSkillLevel(15110009) > 0) {
            cRate += SkillFactory.getSkill(15110009).getEffect(user.getTotalSkillLevel(15110009)).getCr();
        }
        if (user.getTotalSkillLevel(15110024) > 0) {
            cRate += SkillFactory.getSkill(15110024).getEffect(user.getTotalSkillLevel(15110024)).getCr();
        }
        if (user.getTotalSkillLevel(15120008) > 0) {
            cRate += SkillFactory.getSkill(15120008).getEffect(user.getTotalSkillLevel(15120008)).getCr();
        }
        if (user.getTotalSkillLevel(20000194) > 0) {
            cRate += SkillFactory.getSkill(20000194).getEffect(user.getTotalSkillLevel(20000194)).getCr();
        }
        if (user.getTotalSkillLevel(20030204) > 0) {
            cRate += SkillFactory.getSkill(20030204).getEffect(user.getTotalSkillLevel(20030204)).getCr();
        }
        if (user.getTotalSkillLevel(21000014) > 0) {
            cRate += SkillFactory.getSkill(21000014).getEffect(user.getTotalSkillLevel(21000014)).getCr();
        }
        if (user.getTotalSkillLevel(21100019) > 0) {
            cRate += SkillFactory.getSkill(21100019).getEffect(user.getTotalSkillLevel(21100019)).getCr();
        }
        if (user.getTotalSkillLevel(21110000) > 0) {
            cRate += SkillFactory.getSkill(21110000).getEffect(user.getTotalSkillLevel(21110000)).getCr();
        }
        if (user.getTotalSkillLevel(22120002) > 0) {
            cRate += SkillFactory.getSkill(22120002).getEffect(user.getTotalSkillLevel(22120002)).getCr();
        }
        if (user.getTotalSkillLevel(22140000) > 0) {
            cRate += SkillFactory.getSkill(22140000).getEffect(user.getTotalSkillLevel(22140000)).getProb();
        }
        if (user.getTotalSkillLevel(23000003) > 0) {
            cRate += SkillFactory.getSkill(23000003).getEffect(user.getTotalSkillLevel(23000003)).getCr();
        }
        if (user.getTotalSkillLevel(24110007) > 0) {
            cRate += SkillFactory.getSkill(24110007).getEffect(user.getTotalSkillLevel(24110007)).getCr();
        }
        if (user.getTotalSkillLevel(31100006) > 0) {
            cRate += SkillFactory.getSkill(31100006).getEffect(user.getTotalSkillLevel(31100006)).getCr();
        }
        if (user.getTotalSkillLevel(32100006) > 0) {
            cRate += SkillFactory.getSkill(32100006).getEffect(user.getTotalSkillLevel(32100006)).getCr();
        }
        if (user.getTotalSkillLevel(35100000) > 0) {
            cRate += SkillFactory.getSkill(35100000).getEffect(user.getTotalSkillLevel(35100000)).getCr();
        }
        if (user.getTotalSkillLevel(52100001) > 0) {
            cRate += SkillFactory.getSkill(52100001).getEffect(user.getTotalSkillLevel(52100001)).getCr();
        }
        if (user.getTotalSkillLevel(52110001) > 0) {
            cRate += SkillFactory.getSkill(52110001).getEffect(user.getTotalSkillLevel(52110001)).getCr();
        }
        if (user.getTotalSkillLevel(62110009) > 0) {
            cRate += SkillFactory.getSkill(62110009).getEffect(user.getTotalSkillLevel(62110009)).getCr();
        }
        if (user.getTotalSkillLevel(65000006) > 0) {
            cRate += SkillFactory.getSkill(65000006).getEffect(user.getTotalSkillLevel(65000006)).getCr();
        }
        if (user.getTotalSkillLevel(70000014) > 0) {
            cRate += SkillFactory.getSkill(70000014).getEffect(user.getTotalSkillLevel(70000014)).getCr();
        }
        if (user.getTotalSkillLevel(71000032) > 0) {
            cRate += SkillFactory.getSkill(71000032).getEffect(user.getTotalSkillLevel(71000032)).getCr();
        }
        if (user.getTotalSkillLevel(80000002) > 0) {
            cRate += SkillFactory.getSkill(80000002).getEffect(user.getTotalSkillLevel(80000002)).getCr();
        }
        if (user.getTotalSkillLevel(91000005) > 0) {
            cRate += SkillFactory.getSkill(91000005).getEffect(user.getTotalSkillLevel(91000005)).getCr();
        }
        if (user.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
            if (user.getTotalSkillLevel(1220010) > 0) {
                cRate += SkillFactory.getSkill(1220010).getEffect(user.getTotalSkillLevel(1220010)).getCr();
            }
        }
        boolean aRate = false;
        switch (skillID) {
            case 1221009:
            case 2221006:
            case 2321001:
            case 2321008:
            case 3221007:
            case 4331006:
            case 5221007:
            case 5221016:
            case 5321012:
            case 21101004:
            case 21111032:
            case 21120005:
            case 21121016:
            case 21121017:
            case 21121022:
            case 22181002:
            case 31111000:
            case 31121001:
            case 32111003: {
                aRate = true;
                break;
            }
        }
        if (aRate) {
            cRate += SkillFactory.getSkill(skillID).getEffect(user.getTotalSkillLevel(skillID)).getCr();
        }
        if (user.getBuffedValue(CharacterTemporaryStat.Mechanic) != null) {
            if (user.getBuffSource(CharacterTemporaryStat.Mechanic) == 35111004) {
                cRate = 100;
            }
            if (user.getBuffSource(CharacterTemporaryStat.Mechanic) == 35121013) {
                cRate = 100;
            }
        }
        switch (skillID) {
            case 3221007:
            case 5221016:
            case 23111002:
            case 23121002: {
                cRate = 100;
            }
        }

        boolean a1 = false;
        if (Randomizer.rand(0, 100) < cRate) {
            a1 = true;
        }
        return a1;
    }

    public static int getDamage(MapleCharacter user, int mobID, int damage) {
        switch (mobID) {
            case 8900001:
            case 8900002: {
                if (user.getMapId() == 105200610) {
                    if (user.getPierreHat() == (mobID == 8900001 ? 1003728 : 1003727)) {
                        return damage *= 2;
                    }
                }
                break;
            }
        }
        return damage;
    }
}
