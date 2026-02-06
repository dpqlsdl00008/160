package server;

import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import client.inventory.Item;
import constants.GameConstants;
import client.CharacterTemporaryStat;
import client.MapleBuffStatStackedValueHolder;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MonsterSkill;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.MapleStat;
import client.MapleTrait.MapleTraitType;
import client.SkillFactory;
import client.PlayerStats;
import client.Skill;
import client.status.MonsterTemporaryStat;
import client.status.MonsterTemporaryStatEffect;
import handling.ChatType;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;

import java.awt.Point;
import java.util.*;

import provider.MapleData;
import provider.MapleDataType;
import provider.MapleDataTool;
import server.life.MapleMonster;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;

import java.util.Map.Entry;

import server.MapleCarnivalFactory.MCSkill;
import server.Timer.BuffTimer;
import server.life.MapleLifeFactory;
import server.life.MobSkillFactory;
import server.maps.MapleExtractor;
import server.maps.MechDoor;
import tools.Pair;
import tools.CaltechEval;
import tools.FileoutputUtil;
import tools.Triple;
import tools.packet.CField.EffectPacket;
import tools.packet.CField;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuffPacket;

public class MapleStatEffect implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private Map<MapleStatInfo, Integer> info;
    private Map<MapleTraitType, Integer> traits;
    private boolean overTime, skill, noDelay, partyBuff = true;
    private EnumMap<CharacterTemporaryStat, Integer> statups;
    private ArrayList<Pair<Integer, Integer>> availableMap;
    private EnumMap<MonsterTemporaryStat, Integer> monsterStatus;
    private Point lt, rb;
    private byte level;
    //    private List<Pair<Integer, Integer>> randomMorph;
    private List<MonsterSkill> cureDebuffs;
    private List<Integer> petsCanConsume, randomPickup;
    private List<Triple<Integer, Integer, Integer>> rewardItem;
    private byte expR, familiarTarget, recipeUseCount, recipeValidDay, reqSkillLevel, slotCount, effectedOnAlly, effectedOnEnemy, type, preventslip, immortal, bs;
    private short mpCon, ignoreMob, mesoR, thaw = 0, fatigueChange, lifeId, imhp, immp, inflation, useLevel, indiePdd, indieMdd, incPVPdamage, mobSkill, mobSkillLevel, criticalR = 0, evadeProb = 0;
    private double hpR, mpR;
    private int sourceid, recipe, moveTo, moneyCon, morphId = 0, expinc, exp, consumeOnPickup, charColor, interval, rewardMeso, totalprob, cosmetic;
    private int expBuff, itemup, mesoup, cashup, berserk, illusion, booster, berserk2, cp, nuffSkill, selfDestruction;
    private long hakuBufftime;
    private int respectPimmune, respectMimmune, defenseAtt, defenseState, indieExp, indieBooster, expX, indieCriDamR;

    public static MapleStatEffect loadSkillEffectFromData(final MapleData source, final int skillid, final boolean overtime, final int level, final String variables) {
        return loadFromData(source, skillid, true, overtime, level, variables);
    }

    public static MapleStatEffect loadItemEffectFromData(final MapleData source, final int itemid) {
        return loadFromData(source, itemid, false, false, 1, null);
    }

    private static void addBuffStatPairToListIfNotZero(final EnumMap<CharacterTemporaryStat, Integer> list, final CharacterTemporaryStat buffstat, final Integer val) {
        if (val != 0) {
            list.put(buffstat, val);
        }
    }

    public static int parseEval(String data, int level) {
        String variables = "x";
        String dddd = data.replace(variables, String.valueOf(level));
        if (dddd.substring(0, 1).equals("-")) { //-30+3*x
            if (dddd.substring(1, 2).equals("u") || dddd.substring(1, 2).equals("d")) { //-u(x/2)
                dddd = "n(" + dddd.substring(1, dddd.length()) + ")"; //n(u(x/2))
            } else {
                dddd = "n" + dddd.substring(1, dddd.length()); //n30+3*x
            }
        } else if (dddd.substring(0, 1).equals("=")) { //lol nexon and their mistakes
            dddd = dddd.substring(1, dddd.length());
        }
        return (int) (new CaltechEval(dddd).evaluate());
    }

    private static int parseEval(String path, MapleData source, int def, String variables, int level) {
        if (variables == null) {
            return MapleDataTool.getIntConvert(path, source, def);
        } else {
            final MapleData dd = source.getChildByPath(path);
            if (dd == null) {
                return def;
            }
            if (dd.getType() != MapleDataType.STRING) {
                return MapleDataTool.getIntConvert(path, source, def);
            }
            String dddd = MapleDataTool.getString(dd).replace(variables, String.valueOf(level));
            if (dddd.substring(0, 1).equals("-")) { //-30+3*x
                if (dddd.substring(1, 2).equals("u") || dddd.substring(1, 2).equals("d")) { //-u(x/2)
                    dddd = "n(" + dddd.substring(1, dddd.length()) + ")"; //n(u(x/2))
                } else {
                    dddd = "n" + dddd.substring(1, dddd.length()); //n30+3*x
                }
            } else if (dddd.substring(0, 1).equals("=")) { //lol nexon and their mistakes
                dddd = dddd.substring(1, dddd.length());
            } else if (dddd.contains("y")) { // AngelicBuster Exception
                dddd = "0";
            }
            return (int) (new CaltechEval(dddd).evaluate());
        }
    }

    public final boolean isBindSkill(int skillID) {
        switch (skillID) {
            case 2221011:   // 프리징 브레스
            case 11121004:  // 소울 페네트레이션
            case 14121004:  // 쉐도우 스티치
            case 21121029:  // 헌터즈 타겟팅
            case 31121006:  // 다크 바인드
            case 33121017:  // 재규어 소울
            case 62121004:  // 퇴마 유성부
            case 65111004:  // 무영각
            case -1: {
                return true;
            }
        }
        return false;
    }

    private static MapleStatEffect loadFromData(final MapleData source, final int sourceid, final boolean skill, final boolean overTime, final int level, final String variables) {
        final MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = (byte) level;
        if (source == null) {
            return ret;
        }
        ret.info = new EnumMap<>(MapleStatInfo.class);
        for (final MapleStatInfo i : MapleStatInfo.values()) {
            if (i.isSpecial()) {
                ret.info.put(i, parseEval(i.name().substring(0, i.name().length() - 1), source, i.getDefault(), variables, level));
            } else {
                ret.info.put(i, parseEval(i.name(), source, i.getDefault(), variables, level));
            }
        }
        ret.hpR = parseEval("hpR", source, 0, variables, level) / 100.0;
        ret.mpR = parseEval("mpR", source, 0, variables, level) / 100.0;
        ret.ignoreMob = (short) parseEval("ignoreMobpdpR", source, 0, variables, level);
        ret.thaw = (short) parseEval("thaw", source, 0, variables, level);
        ret.interval = parseEval("interval", source, 0, variables, level);
        ret.expinc = parseEval("expinc", source, 0, variables, level);
        ret.exp = parseEval("exp", source, 0, variables, level);
        ret.morphId = parseEval("morph", source, 0, variables, level);
        ret.cp = parseEval("cp", source, 0, variables, level);
        ret.cosmetic = parseEval("cosmetic", source, 0, variables, level);
        ret.slotCount = (byte) parseEval("slotCount", source, 0, variables, level);
        ret.preventslip = (byte) parseEval("preventslip", source, 0, variables, level);
        ret.useLevel = (short) parseEval("useLevel", source, 0, variables, level);
        ret.nuffSkill = parseEval("nuffSkill", source, 0, variables, level);
        ret.familiarTarget = (byte) (parseEval("familiarPassiveSkillTarget", source, 0, variables, level) + 1);
        ret.immortal = (byte) parseEval("immortal", source, 0, variables, level);
        ret.type = (byte) parseEval("type", source, 0, variables, level);
        ret.bs = (byte) parseEval("bs", source, 0, variables, level);
        ret.indiePdd = (short) parseEval("indiePdd", source, 0, variables, level);
        ret.indieMdd = (short) parseEval("indieMdd", source, 0, variables, level);
        ret.indieExp = (short) parseEval("indieExp", source, 0, variables, level);
        ret.indieBooster = (short) parseEval("indieBooster", source, 0, variables, level);
        ret.indieCriDamR = (short) parseEval("indieCriDamR", source, 0, variables, level);
        ret.expX = (short) parseEval("expX", source, 0, variables, level);
        ret.evadeProb = (short) parseEval("evadeProb", source, 0, variables, level);
        ret.expBuff = parseEval("expBuff", source, 0, variables, level);
        ret.cashup = parseEval("cashBuff", source, 0, variables, level);
        ret.itemup = parseEval("itemup", source, 0, variables, level);
        ret.mesoup = parseEval("mesoup", source, 0, variables, level);
        ret.berserk = parseEval("berserk", source, 0, variables, level);
        ret.berserk2 = parseEval("berserk2", source, 0, variables, level);
        ret.criticalR = (short) parseEval("criticalProb", source, 0, variables, level);
        ret.booster = parseEval("booster", source, 0, variables, level);
        ret.lifeId = (short) parseEval("lifeId", source, 0, variables, level);
        ret.inflation = (short) parseEval("inflation", source, 0, variables, level);
        ret.imhp = (short) parseEval("imhp", source, 0, variables, level);
        ret.immp = (short) parseEval("immp", source, 0, variables, level);
        ret.illusion = parseEval("illusion", source, 0, variables, level);
        ret.consumeOnPickup = parseEval("consumeOnPickup", source, 0, variables, level);
        ret.setMpCon((short) parseEval("mpCon", source, 0, variables, level));
        ret.selfDestruction = (short) parseEval("selfDestruction", source, 0, variables, level);
        if (ret.consumeOnPickup == 1) {
            if (parseEval("party", source, 0, variables, level) > 0) {
                ret.consumeOnPickup = 2;
            }
        }
        ret.recipe = parseEval("recipe", source, 0, variables, level);
        ret.recipeUseCount = (byte) parseEval("recipeUseCount", source, 0, variables, level);
        ret.recipeValidDay = (byte) parseEval("recipeValidDay", source, 0, variables, level);
        ret.reqSkillLevel = (byte) parseEval("reqSkillLevel", source, 0, variables, level);
        ret.effectedOnAlly = (byte) parseEval("effectedOnAlly", source, 0, variables, level);
        ret.effectedOnEnemy = (byte) parseEval("effectedOnEnemy", source, 0, variables, level);
        ret.incPVPdamage = (short) parseEval("incPVPDamage", source, 0, variables, level);
        ret.moneyCon = parseEval("moneyCon", source, 0, variables, level);
        ret.moveTo = parseEval("moveTo", source, -1, variables, level);

        ret.respectPimmune = parseEval("respectPimmune", source, -1, variables, level);
        ret.respectMimmune = parseEval("respectMimmune", source, -1, variables, level);
        final MapleData da = source.getChildByPath("defenseAtt");
        if (da != null) {
            ret.defenseAtt = 100;
        }
        final MapleData ds = source.getChildByPath("defenseState");
        if (ds != null) {
            ret.defenseState = 100;
        }

        if (sourceid == 30021235) {
            ret.moveTo = parseEval("x", source, -1, variables, level);
        }
        ret.charColor = 0;
        String cColor = MapleDataTool.getString("charColor", source, null);
        if (cColor != null) {
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(0, 2));
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(2, 4) + "00");
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(4, 6) + "0000");
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(6, 8) + "000000");
        }
        ret.traits = new EnumMap<>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            int expz = parseEval(t.name() + "EXP", source, 0, variables, level);
            if (expz != 0) {
                ret.traits.put(t, expz);
            }
        }
        List<MonsterSkill> cure = new ArrayList<>(5);
        if (parseEval("poison", source, 0, variables, level) > 0) {
            cure.add(MonsterSkill.Poison);
        }
        if (parseEval("seal", source, 0, variables, level) > 0) {
            cure.add(MonsterSkill.Seal);
        }
        if (parseEval("darkness", source, 0, variables, level) > 0) {
            cure.add(MonsterSkill.Darkness);
        }
        if (parseEval("weakness", source, 0, variables, level) > 0) {
            cure.add(MonsterSkill.Weakness);
        }
        if (parseEval("curse", source, 0, variables, level) > 0) {
            cure.add(MonsterSkill.Curse);
        }
        ret.cureDebuffs = cure;
        ret.petsCanConsume = new ArrayList<>();
        for (int i = 0; true; i++) {
            final int dd = parseEval(String.valueOf(i), source, 0, variables, level);
            if (dd > 0) {
                ret.petsCanConsume.add(dd);
            } else {
                break;
            }
        }
        final MapleData mdd = source.getChildByPath("0");
        if (mdd != null && mdd.getChildren().size() > 0) {
            ret.mobSkill = (short) parseEval("mobSkill", mdd, 0, variables, level);
            ret.mobSkillLevel = (short) parseEval("level", mdd, 0, variables, level);
        } else {
            ret.mobSkill = 0;
            ret.mobSkillLevel = 0;
        }
        final MapleData pd = source.getChildByPath("randomPickup");
        if (pd != null) {
            ret.randomPickup = new ArrayList<Integer>();
            for (MapleData p : pd) {
                ret.randomPickup.add(MapleDataTool.getInt(p));
            }
        }
        final MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point) ltd.getData();
            ret.rb = (Point) source.getChildByPath("rb").getData();
        }
        final MapleData ltc = source.getChildByPath("con");
        if (ltc != null) {
            ret.availableMap = new ArrayList<Pair<Integer, Integer>>();
            for (MapleData ltb : ltc) {
                ret.availableMap.add(new Pair<Integer, Integer>(MapleDataTool.getInt("sMap", ltb, 0), MapleDataTool.getInt("eMap", ltb, 999999999)));
            }
        }
        int totalprob = 0;
        final MapleData lta = source.getChildByPath("reward");
        if (lta != null) {
            ret.rewardMeso = parseEval("meso", lta, 0, variables, level);
            final MapleData ltz = lta.getChildByPath("case");
            if (ltz != null) {
                ret.rewardItem = new ArrayList<Triple<Integer, Integer, Integer>>();
                for (MapleData lty : ltz) {
                    ret.rewardItem.add(new Triple<Integer, Integer, Integer>(MapleDataTool.getInt("id", lty, 0), MapleDataTool.getInt("count", lty, 0), MapleDataTool.getInt("prop", lty, 0))); // todo: period (in minutes)
                    totalprob += MapleDataTool.getInt("prob", lty, 0);
                }
            }
        } else {
            ret.rewardMeso = 0;
        }
        ret.totalprob = totalprob;
        // start of server calculated stuffs
        if (ret.skill) {
            final int priceUnit = ret.info.get(MapleStatInfo.priceUnit); // Guild skills
            if (priceUnit > 0) {
                final int price = ret.info.get(MapleStatInfo.price);
                final int extendPrice = ret.info.get(MapleStatInfo.extendPrice);
                ret.info.put(MapleStatInfo.price, price * priceUnit);
                ret.info.put(MapleStatInfo.extendPrice, extendPrice * priceUnit);
            }
            switch (sourceid) {
                case 1100002:
                case 51100002: // Mihile || Final Attack
                case 1200002:
                case 1300002:
                case 3100001:
                case 3200001:
                case 11101002:
                case 13101002:
                case 2111007:
                case 2211007:
                case 2311007:
                case 32111010:
                case 22161005:
                case 12111007:
                case 33100009:
                case 22150004:
                case 22181004: //All Final Attack
                case 1120013:
                case 51120002: // Mihile || Advanced Final Attack
                case 3120008:
                case 23100006:
                case 23120012:
                case 21100010:
                case 21120012:
                case 61110008:
                case 62100007:
                case 65121007:
                case 15100027:
                    ret.info.put(MapleStatInfo.mobCount, 6);
                    break;
                case 35121005:
                case 35111004:
                case 35121013:
                    ret.info.put(MapleStatInfo.attackCount, 6);
                    ret.info.put(MapleStatInfo.bulletCount, 6);
                    break;
                case 24100003: // TODO: for now, or could it be card stack? (1 count)
                case 24120002:
                    ret.info.put(MapleStatInfo.attackCount, 15);
                    break;
            }
            if (GameConstants.isNoDelaySkill(sourceid)) {
                ret.info.put(MapleStatInfo.mobCount, 6);
            }
        }
        if (!ret.skill && ret.info.get(MapleStatInfo.time) > -1) {
            ret.overTime = true;
        } else {
            ret.info.put(MapleStatInfo.time, (ret.info.get(MapleStatInfo.time) * 1000)); // items have their times stored in ms, of course
            ret.info.put(MapleStatInfo.subTime, (ret.info.get(MapleStatInfo.subTime) * 1000));
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || /*ret.isFinalAttack() || */ ret.isAngel() || ret.getSummonMovementType() != null;
        }
        if (ret.skill && (sourceid == 65120006 || sourceid == 65101002 || sourceid == 36111003 || sourceid == 36110005 || sourceid == 31201001)) {
            ret.noDelay = true;
        }
        ret.monsterStatus = new EnumMap<>(MonsterTemporaryStat.class);
        //final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
        ret.statups = new EnumMap<>(CharacterTemporaryStat.class);
        if (ret.overTime && ret.getSummonMovementType() == null && !ret.isEnergyCharge()) {
            if (sourceid == 2022746 || sourceid == 2022747 || sourceid == 2022764 || sourceid == 2022823) {
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Unk_PMadR_02, ret.info.get(MapleStatInfo.pad));
            } else {
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Pad, ret.info.get(MapleStatInfo.pad));
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Mad, ret.info.get(MapleStatInfo.mad));
            }
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Pdd, ret.info.get(MapleStatInfo.pdd));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Mdd, ret.info.get(MapleStatInfo.mdd));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Acc, ret.info.get(MapleStatInfo.acc));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Eva, ret.info.get(MapleStatInfo.eva));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Speed, sourceid == 32120001 || sourceid == 32101003 ? ret.info.get(MapleStatInfo.x) : ret.info.get(MapleStatInfo.speed));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Jump, ret.info.get(MapleStatInfo.jump));
            if (sourceid != 22131001 && sourceid != 32121010 && sourceid != 60001216 && sourceid != 61100005 && sourceid != 61120010 && sourceid != 61110005) {
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.MaxHp, ret.info.get(MapleStatInfo.mhpR));
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.MaxMp, ret.info.get(MapleStatInfo.mmpR));
            }
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Booster, Integer.valueOf(ret.booster));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Thaw, Integer.valueOf(ret.thaw));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.ExpBuffRate, Integer.valueOf(ret.expBuff)); // EXP

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.DropUpByItem, Integer.valueOf(ret.itemup)); // defaults to 2x
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.MesoUpByItem, Integer.valueOf(ret.mesoup)); // defaults to 2x

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.DojangBerserk, Integer.valueOf(ret.berserk2));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.HowlingCritical, Integer.valueOf(ret.criticalR));
            //addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.DefenseState, Integer.valueOf(ret.illusion));
            //addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Unk_0x80_3, Integer.valueOf(ret.berserk));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.EnhancedMaxHp, ret.info.get(MapleStatInfo.emhp));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.EnhancedMaxMp, ret.info.get(MapleStatInfo.emmp));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.epad));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.EnhancedMad, ret.info.get(MapleStatInfo.emad));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.EnhancedPdd, ret.info.get(MapleStatInfo.epdd));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.EnhancedMdd, ret.info.get(MapleStatInfo.emdd));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.GiantPotion, Integer.valueOf(ret.inflation));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Str, ret.info.get(MapleStatInfo.str));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Dex, ret.info.get(MapleStatInfo.dex));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Int, ret.info.get(MapleStatInfo.int_));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Luk, ret.info.get(MapleStatInfo.luk));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndiePad, ret.info.get(MapleStatInfo.indiePad));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieMad, ret.info.get(MapleStatInfo.indieMad));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Str, ret.info.get(MapleStatInfo.indieSTR));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Dex, ret.info.get(MapleStatInfo.indieDEX));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Int, ret.info.get(MapleStatInfo.indieINT));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Luk, ret.info.get(MapleStatInfo.indieLUK));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieMaxHpR, ret.info.get(MapleStatInfo.indieMhpR));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieMaxMpR, ret.info.get(MapleStatInfo.indieMmpR));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieMaxHp, ret.info.get(MapleStatInfo.indieMhp));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieMaxMp, ret.info.get(MapleStatInfo.indieMmp));

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieAcc, ret.info.get(MapleStatInfo.indieAcc));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieEva, ret.info.get(MapleStatInfo.indieEva));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieJump, ret.info.get(MapleStatInfo.indieJump));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieSpeed, ret.info.get(MapleStatInfo.indieSpeed));

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieAllStat, ret.info.get(MapleStatInfo.indieAllStat));

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndiePdd, ret.info.get(MapleStatInfo.indiePdd));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieMdd, ret.info.get(MapleStatInfo.indieMdd));

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.PlusExpRate, Integer.valueOf(ret.indieExp));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IndieBooster, Integer.valueOf(ret.indieBooster));
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.IncCriticalDam, Integer.valueOf(ret.indieCriDamR));

            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.CarnivalExpUp, Integer.valueOf(ret.expX));

            if (sourceid != 27111004 && sourceid != 33121054 && sourceid != 65121053) {
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.AsrR, ret.info.get(MapleStatInfo.asrR));
                addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.TerR, ret.info.get(MapleStatInfo.terR));
            }
            addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.Stance, ret.info.get(MapleStatInfo.stance));

            //addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.RespectPImmune, Integer.valueOf(ret.respectPimmune));
            //addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.RespectMImmune, Integer.valueOf(ret.respectMimmune));
            //addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.DefenseState, Integer.valueOf(ret.defenseState));
            //addBuffStatPairToListIfNotZero(ret.statups, CharacterTemporaryStat.DefenseAtt, Integer.valueOf(ret.defenseAtt));
        }
        if (ret.skill) { // hack because we can't get from the datafile...
            switch (sourceid) {
                case 80001035:
                    ret.info.put(MapleStatInfo.time, 0);
                    break;
                case 23121054:
                    ret.statups.put(CharacterTemporaryStat.Stance, ret.info.get(MapleStatInfo.x));
                    break;
                case 80001155:
                    ret.statups.put(CharacterTemporaryStat.CounterAttack, ret.info.get(MapleStatInfo.indieDamR));
                    break;
                case 20040216:
                    ret.statups.put(CharacterTemporaryStat.Larkness, ret.info.get(MapleStatInfo.x)); //?
                    break;
                case 20040217:
                    ret.statups.put(CharacterTemporaryStat.Larkness, ret.info.get(MapleStatInfo.y)); //?
                    break;
                case 20040219:
                case 20040220:
                    ret.statups.put(CharacterTemporaryStat.Larkness, 2);
                    ret.info.put(MapleStatInfo.time, 1000);
                    break;
                case 27121006:
                    ret.statups.put(CharacterTemporaryStat.ElementalReset, ret.info.get(MapleStatInfo.y));
                    break;
                case 27100003:
                    ret.statups.put(CharacterTemporaryStat.BlessOfDarkness, ret.info.get(MapleStatInfo.y));
                    break;
                case 27111004:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.AntiMagicShell, 3);
                    break;
                case 27101202:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, ret.info.get(MapleStatInfo.x));
                    break;
                case 2111011:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.AntiMagicShell, 5);
                    break;
                case 2211012:
                    ret.info.put(MapleStatInfo.time, 1000 * 30);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 1221015:
                    ret.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 2311012:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    ret.statups.put(CharacterTemporaryStat.AntiMagicShell, 5);
                    break;
                case 27121005:
                    ret.statups.put(CharacterTemporaryStat.StackBuff, 1);
                    break;
                case 24111003://미스포츈프토텍션 체력, 엠피 스킬캐싱
                    ret.statups.put(CharacterTemporaryStat.AsrR, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.TerR, ret.info.get(MapleStatInfo.y));
                    break;
                case 24121007:
                    ret.statups.put(CharacterTemporaryStat.DamAbsorbShield, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.epad));
                    ret.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                    ret.statups.put(CharacterTemporaryStat.PowerGuard, ret.info.get(MapleStatInfo.y));
                    break;
                case 20031209:
                case 20031210:
                    ret.overTime = true;
                    ret.info.put(MapleStatInfo.time, 30 * 1000);
                    ret.statups.put(CharacterTemporaryStat.Judgement, 0);
                    break;
                case 2001002: // magic guard
                case 12001001:
                case 22111001:
                    ret.statups.put(CharacterTemporaryStat.MagicGuard, ret.info.get(MapleStatInfo.x));
                    break;
                case 2301003: // invincible
                    ret.statups.put(CharacterTemporaryStat.Invincible, ret.info.get(MapleStatInfo.x));
                    break;
                case 35120000:
                case 35001002:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    break;
                case 9101004:
                case 9001004: // hide
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    break;
                case 13101006: // Wind Walk
                    ret.statups.put(CharacterTemporaryStat.WindWalk, ret.info.get(MapleStatInfo.x));
                    break;
                case 4330001:
                    ret.statups.put(CharacterTemporaryStat.DarkSight, (int) ret.level);
                    break;
                case 4001003: // Dark Sight
                case 14001003: // cygnus ds
                case 20031211:
                    ret.statups.put(CharacterTemporaryStat.DarkSight, ret.info.get(MapleStatInfo.x)); // d
                    break;
                case 4211003: // pickpocket
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.PickPocket, ret.info.get(MapleStatInfo.x));
                    break;
                case 4211005: // mesoguard
                    ret.statups.put(CharacterTemporaryStat.MesoGuard, ret.info.get(MapleStatInfo.x));
                    break;
                case 4111001: // mesoup
                    ret.statups.put(CharacterTemporaryStat.MesoUp, ret.info.get(MapleStatInfo.x));
                    break;
                case 4111002: // shadowpartner
                case 14111000: // cygnus
                case 4331002:
                case 36111006:
                    ret.statups.put(CharacterTemporaryStat.ShadowPartner, (int) ret.level);
                    //ret.statups.put(MapleBuffStat.SHADOWPARTNER, ret.info.get(MapleStatInfo.x))); // d
                    break;
                case 4211008:
                    ret.statups.put(CharacterTemporaryStat.ShadowPartner, (int) ret.level);
                    break;
                case 33100009:
                case 51120002:
                case 21100010:
                case 21120012:
                case 1300002:
                case 1120013:
                case 1100002:
                case 51100002:
                case 23100006:

                case 23120012:
                case 3120008:
                case 3100001:
                case 1200002:
                case 11101002: // All Final attack
                case 13101002:
                case 61110008:
                case 62100007:
                case 13121017:
                    ret.statups.put(CharacterTemporaryStat.FinalAttack, ret.info.get(MapleStatInfo.x));
                    break;
                case 15100027:
                    ret.statups.put(CharacterTemporaryStat.FinalAttack, ret.info.get(MapleStatInfo.x));
                    break;
                case 65121007:
                    ret.statups.put(CharacterTemporaryStat.FinalAttack, ret.info.get(MapleStatInfo.x));
                    break;
                case 22161004:
                    ret.statups.put(CharacterTemporaryStat.OnixGodBless, ret.info.get(MapleStatInfo.x));
                    break;
                case 3101004: // soul arrow
                case 3201004:
                case 2311002: // mystic door - hacked buff icon
                case 35101005:
                case 13101003:
                case 33101003:
                    ret.statups.put(CharacterTemporaryStat.SoulArrow, ret.info.get(MapleStatInfo.x));
                    break;
                case 2321010:
                case 2221009:
                case 2121009:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.MasterMagic, ret.info.get(MapleStatInfo.x));
                    break;
                case 2320011:
                case 2220010:
                case 2120010:
                    ret.info.put(MapleStatInfo.time, 1999);
                    ret.statups.put(CharacterTemporaryStat.ArcaneAim, ret.info.get(MapleStatInfo.x));
                    break;
                case 60000067:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.ArcaneAim, ret.info.get(MapleStatInfo.x));
                    break;
                case 1211008:
                case 1211006: // wk charges
                case 1211004:
                case 1221004:
                case 21101006:
                case 21111005:
                case 15101006:
                case 62101004:
                case 62111006:
                case 62121008:
                    ret.statups.put(CharacterTemporaryStat.WeaponCharge, ret.info.get(MapleStatInfo.x));
                    break;
                case 2111008:
                case 2211008:
                case 12111008:
                case 22131002:
                    ret.statups.put(CharacterTemporaryStat.ElementalReset, ret.info.get(MapleStatInfo.x));
                    break;
                case 3111000:
                case 3121008:
                case 13111001:
                    ret.statups.put(CharacterTemporaryStat.Concentration, ret.info.get(MapleStatInfo.x));
                    break;
                case 5110001: // Energy Charge
                case 15100004:
                    ret.statups.put(CharacterTemporaryStat.EnergyCharge, 0);
                    break;
                case 1101005: // booster
                case 2311006: //프리스트 부스터
                case 1101004:
                case 1201005:
                case 1201004:
                case 1301005:
                case 1301004:
                case 3101002:
                case 3201002:
                case 4101003:
                case 4201002:
                case 2111005: // spell booster, do these work the same?
                case 2211005:
                case 5101006:
                case 5201003:
                case 11101001:
                case 12101004:
                case 13101001:
                case 14101002:
                case 15101002:
                case 22141002: // Magic Booster
                case 23101002: //듀얼보우건 부스터
                case 24101005: //케인 부스터
                case 31001001: //데몬 부스터
                case 31201002: //데몬 부스터
                case 36101004:
                case 5301002:  //캐논 부스터
                case 32101005: //스태프 부스터
                case 33001003: //크로스보우 부스터
                case 35101006: //메카닉 부스터
                case 51101003: //소드 부스터
                case 27101004: //매직 부스터
                case 4311009:
                case 21001003: // Aran - Pole Arm Booster
                    ret.statups.put(CharacterTemporaryStat.Booster, -ret.info.get(MapleStatInfo.y));
                    break;
                case 5111007:
                case 5120012:
                case 5211007:
                case 5220014:
                case 35111013:
                case 35120014: //double lucky
                case 15111011:
                case 5311005:
                case 5320007:
                    ret.statups.put(CharacterTemporaryStat.Dice, 1);
                    break;
                case 5120011:
                case 5220012:
                    ret.info.put(MapleStatInfo.cooltime, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.CounterAttack, ret.info.get(MapleStatInfo.indieDamR)); //i think
                    break;
                case 5121009:
                case 15121005:
                    ret.statups.put(CharacterTemporaryStat.IndieBooster, ret.info.get(MapleStatInfo.x));
                    //ret.statups.put(CharacterTemporaryStat.WindBooster, ret.info.get(MapleStatInfo.x));
                    break;
                case 4321000: //tornado spin uses same buffstats
                    ret.info.put(MapleStatInfo.time, 1000);
                    ret.statups.put(CharacterTemporaryStat.DashSpeed, 100 + ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.DashJump, ret.info.get(MapleStatInfo.y)); //always 0 but its there
                    break;
                case 5001005: // Dash
                case 15001003:
                    ret.statups.put(CharacterTemporaryStat.DashSpeed, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.DashJump, ret.info.get(MapleStatInfo.y));
                    break;
                case 1101007: // pguard
                case 1201007:
                    ret.statups.put(CharacterTemporaryStat.Guard, ret.info.get(MapleStatInfo.x));
                    break;
                case 12111023: // 플레임 배리어
                    ret.statups.put(CharacterTemporaryStat.DamAbsorbShield, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 11121005: // 솔루나 타임
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.y));
                    ret.statups.put(CharacterTemporaryStat.EnhancedMad, ret.info.get(MapleStatInfo.y));
                    ret.statups.put(CharacterTemporaryStat.CriticalBuff, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 32111004: //conversion
                    ret.statups.put(CharacterTemporaryStat.Conversion, ret.info.get(MapleStatInfo.x));
                    break;
                case 1301007: // hyper body
                case 9001008:
                case 9101008:
                    ret.statups.put(CharacterTemporaryStat.MaxHp, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.MaxMp, ret.info.get(MapleStatInfo.x));
                    break;
                case 1111002: // combo
                case 11111001: // combo
                    ret.info.put(MapleStatInfo.time, Integer.MAX_VALUE);
                    ret.statups.put(CharacterTemporaryStat.ComboCounter, 1);
                    break;
                case 21120007: //combo barrier
                    ret.statups.put(CharacterTemporaryStat.ComboBarrier, ret.info.get(MapleStatInfo.x));
                    break;
                case 5111010: // Admiral's Wings
                    //ret.statups.put(MapleBuffStat.COMBO_BARRIER, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.HowlingDefence, ret.info.get(MapleStatInfo.x));
                    break;
                case 5221015: // 컨티뉴얼 에이밍
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.GuidedBullet, ret.info.get(MapleStatInfo.x));
                    break;
                case 22151002: //killer wings
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.GuidedBullet, ret.info.get(MapleStatInfo.x));
                    break;
                case 4341007:
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.epad));
                    ret.statups.put(CharacterTemporaryStat.EnhancedMad, ret.info.get(MapleStatInfo.epad));
                    ret.statups.put(CharacterTemporaryStat.Stance, (int) ret.info.get(MapleStatInfo.prop));
                    break;
                case 21111009: //combo recharge
                case 1311006: //dragon roar
                case 1311005: //NOT A BUFF - Sacrifice
                    ret.hpR = -ret.info.get(MapleStatInfo.x) / 100.0;
                    break;
                case 1211010: //NOT A BUFF - HP Recover
                    ret.hpR = ret.info.get(MapleStatInfo.x) / 100.0;
                    break;
                case 4341002:
                    ret.info.put(MapleStatInfo.time, 60 * 1000);
                    ret.hpR = -ret.info.get(MapleStatInfo.x) / 100.0;
                    //ret.statups.put(CharacterTemporaryStat.FinalCut, ret.info.get(MapleStatInfo.y));
                    break;
                case 2111007:
                case 2211007:
                case 2311007:
                case 32111010:
                case 22161005:
                case 12111007:
                    ret.info.put(MapleStatInfo.mpCon, ret.info.get(MapleStatInfo.y));
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.TeleportMastery, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Stun, Integer.valueOf(1));
                    break;
                case 51111007:
                case 51121008:
                    ret.monsterStatus.put(MonsterTemporaryStat.Stun, Integer.valueOf(1));
                    break;
                case 4331003:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.DrawBack, ret.info.get(MapleStatInfo.y));
                    break;
                case 51121005:
                case 4121000:
                case 21121000:
                case 1321000:
                case 3221000:
                case 5321005:
                case 36121008:
                case 35121007:
                case 23121005:
                case 31221008:
                case 2321000:
                case 24121008:
                case 33121007:
                case 1121000:
                case 5121000:
                case 22171000:
                case 32121007:
                case 4221000:
                case 2121000:
                case 31121004:
                case 3121000:
                case 5221000:
                case 27121009:
                case 2221000:
                case 4341000:
                case 1221000:
                case 61121014:
                case 11121000:
                case 12121000:
                case 13121000:
                case 14121000:
                case 15121000:
                case 52121010:
                    ret.statups.put(CharacterTemporaryStat.MapleWarrior, ret.info.get(MapleStatInfo.x));
                    break;
                case 15121004: //spark
                    ret.statups.put(CharacterTemporaryStat.ShadowPartner, ret.info.get(MapleStatInfo.x));
                    break;
                case 3121002: // sharp eyes bow master
                case 3221002: // sharp eyes marksmen
                case 33121004:
                case 13121005:
                    ret.statups.put(CharacterTemporaryStat.SharpEyes, (ret.info.get(MapleStatInfo.x) << 8) + ret.info.get(MapleStatInfo.y));
                    break;
                case 22151003: //magic resistance
                    ret.statups.put(CharacterTemporaryStat.MagicResistance, ret.info.get(MapleStatInfo.x));
                    break;
                case 2000007:
                case 12000006:
                case 22000002:
                case 32000012:
                    ret.statups.put(CharacterTemporaryStat.WeaknessMdamage, ret.info.get(MapleStatInfo.x));
                    break;
                case 21101003: // Body Pressure
                    ret.statups.put(CharacterTemporaryStat.BodyPressure, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.BodyPressure, ret.info.get(MapleStatInfo.x));
                    break;
                case 21000000: // Aran Combo
                    ret.statups.put(CharacterTemporaryStat.ComboAbilityBuff, 100);
                    break;
                case 23101003:
                    ret.statups.put(CharacterTemporaryStat.SpiritInfusion, ret.info.get(MapleStatInfo.damage));
                    ret.statups.put(CharacterTemporaryStat.CriticalBuff, ret.info.get(MapleStatInfo.x));
                    break;
                case 32101004:
                    ret.info.put(MapleStatInfo.time, Integer.MAX_VALUE);
                    ret.statups.put(CharacterTemporaryStat.ComboDrain, ret.info.get(MapleStatInfo.x));
                    break;
                case 21100005: // Combo Drain
                case 31121002:
                    ret.statups.put(CharacterTemporaryStat.ComboDrain, ret.info.get(MapleStatInfo.x));
                    break;
                //case 21111001: // Smart Knockback ret.info.get(MapleStatInfo.epdd)
                //ret.statups.put(CharacterTemporaryStat.SMART_KNOCKBACK, ret.info.get(MapleStatInfo.x));//물방
                //break;
                case 23121004://TODO LEGEND
                    ret.statups.put(CharacterTemporaryStat.DamR, (int) ret.info.get(MapleStatInfo.damR));
                    break;
                case 1211009:
                case 1111007:
                case 11111008:
                case 1311007: //magic crash
                case 51111005: // Mihile || Magic Crash
                    ret.monsterStatus.put(MonsterTemporaryStat.MagicCrash, Integer.valueOf(1));
                    break;
                case 1220013:
                    ret.statups.put(CharacterTemporaryStat.BlessingArmor, ret.info.get(MapleStatInfo.x));
                    ret.info.put(MapleStatInfo.attacksRemaining, ret.info.get(MapleStatInfo.x));
                    break;
                case 1211011:
                    ret.statups.put(CharacterTemporaryStat.CombatOrders, ret.info.get(MapleStatInfo.x));
                    break;
                case 23111005:
                    ret.statups.put(CharacterTemporaryStat.DamAbsorbShield, ret.info.get(MapleStatInfo.x));
                    break;
                case 51111004: // Mihile || Enduring Spirit
                    ret.statups.put(CharacterTemporaryStat.AsrR, ret.info.get(MapleStatInfo.y));
                    ret.statups.put(CharacterTemporaryStat.TerR, ret.info.get(MapleStatInfo.z));
                    ret.statups.put(CharacterTemporaryStat.PMddR, ret.info.get(MapleStatInfo.x));
                    break;
                case 22131001: //magic shield
                    ret.statups.put(CharacterTemporaryStat.MagicShield, ret.info.get(MapleStatInfo.x));
                    break;
                case 22181003: //soul stone
                    ret.statups.put(CharacterTemporaryStat.SoulStone, 1);
                    break;
                case 24111002:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.ReviveOnce, 1);
                    break;
                case 32121003: //twister
                    ret.statups.put(CharacterTemporaryStat.Cyclone, ret.info.get(MapleStatInfo.x));
                    break;
                case 2311009: //holy magic
                    ret.statups.put(CharacterTemporaryStat.HolyMagicShell, ret.info.get(MapleStatInfo.x));
                    //ret.info.put(MapleStatInfo.cooltime, ret.info.get(MapleStatInfo.y));
                    ret.hpR = ret.info.get(MapleStatInfo.z) / 100.0;
                    ret.info.put(MapleStatInfo.attacksRemaining, ret.info.get(MapleStatInfo.x));
                    break;
                case 32111005:
                    ret.info.put(MapleStatInfo.time, 60000);
                    ret.statups.put(CharacterTemporaryStat.SuperBody, (int) ret.level);
                    ret.statups.put(CharacterTemporaryStat.IndieBooster, -2);
                    ret.statups.put(CharacterTemporaryStat.DamR, (int) ret.level);
                    break;
                case 11001005:
                    ret.statups.put(CharacterTemporaryStat.Stance, ret.info.get(MapleStatInfo.x));
                    break;
                case 13001022:
                case 15001022:
                    ret.info.put(MapleStatInfo.time, 1000 * 180);
                    ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.x));
                    break;
                case 32110009:
                    ret.info.put(MapleStatInfo.time, 10000);
                    ret.statups.put(CharacterTemporaryStat.NotDamaged, 1); //lots of variables
                    break;
                case 22141003: // Slow
                    ret.statups.put(CharacterTemporaryStat.Slow, ret.info.get(MapleStatInfo.x));
                    break;
                case 4001002: // disorder
                case 14001002: // cygnus disorder
                    ret.monsterStatus.put(MonsterTemporaryStat.Pad, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, ret.info.get(MapleStatInfo.y));
                    break;
                case 5221009: // Mind Control
                    ret.monsterStatus.put(MonsterTemporaryStat.Dazzle, 1);
                    break;
                case 4341003: // Monster Bomb
                    ret.monsterStatus.put(MonsterTemporaryStat.MonsterBomb, (int) ret.info.get(MapleStatInfo.damage));
                    break;
                case 1201006: // threaten
                    ret.monsterStatus.put(MonsterTemporaryStat.Pad, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Blind, ret.info.get(MapleStatInfo.z));
                    break;
                case 31221002:
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, ret.info.get(MapleStatInfo.x));
                    break;
                case 22141001:
                //case 1211002: // charged blow
                case 1111008: // shout
                case 4211002: // assaulter
                case 3101005: // arrow bomb
                case 1111005: // coma: sword
                case 4221007: // boomerang step
                case 5101002: // Backspin Blow
                case 5101003: // Double Uppercut
                case 5121004: // Demolition
                case 5121007: // Barrage
                case 5201004: // pirate blank shot
                case 4121008: // Ninja Storm
                case 22151001:
                case 4201004: //steal, new
                case 33101001:
                case 33101002:
                case 32101001:
                case 32111011:
                case 32121004:
                case 33111002:
                case 33121002:
                case 35101003:
                case 35111015:
                case 5111002: //energy blast
                case 15101005:
                case 4331005:
                case 1121001: //magnet
                case 1221001:
                case 1321001:
                case 9001020:
                case 31111001:
                case 31101002:
                case 9101020:
                case 2211003:
                case 2311004:
                case 22181001:
                case 22171002:
                case 21110006:
                case 22131000:
                case 5301001:
                case 5311001:
                case 5311002:
                case 2221006:
                case 5310008:
                case 27101101: //인바이러빌리티    
                    ret.monsterStatus.put(MonsterTemporaryStat.Stun, 1);
                    break;
                case 90001004:
                case 4321002:
                    ret.monsterStatus.put(MonsterTemporaryStat.Blind, ret.info.get(MapleStatInfo.x));
                case 1111003:
                case 11111002:
                    ret.monsterStatus.put(MonsterTemporaryStat.Blind, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pad, ret.info.get(MapleStatInfo.z));
                    break;
                case 62120003:
                    ret.monsterStatus.put(MonsterTemporaryStat.Blind, ret.info.get(MapleStatInfo.expR));
                    break;
                case 62121004:
                    ret.monsterStatus.put(MonsterTemporaryStat.Frozen, 1);
                    break;
                case 4221003:
                case 4121003:
                case 33121005:
                    ret.monsterStatus.put(MonsterTemporaryStat.ShowDown, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Mdr, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, ret.info.get(MapleStatInfo.x));
                    break;
                case 31121003:
                    ret.monsterStatus.put(MonsterTemporaryStat.Blind, ret.info.get(MapleStatInfo.w)); // 수정
                    ret.monsterStatus.put(MonsterTemporaryStat.Mdr, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Mad, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pad, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Acc, ret.info.get(MapleStatInfo.z));
                    break;
                case 31121006:
                case 31221003://블러디임프리즌
                case 36121053:
                    ret.monsterStatus.put(MonsterTemporaryStat.Frozen, 1);
                    break;
                case 23121002: //not sure if negative
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, -ret.info.get(MapleStatInfo.x));
                    break;
                case 1121015:
                case 32111003:
                    ret.monsterStatus.put(MonsterTemporaryStat.HitCriDamR, ret.info.get(MapleStatInfo.x));
                    break;
                case 2201004: // cold beam
                case 2211002: // ice strike
                case 3211003: // blizzard
                case 2221007: // Blizzard
                case 5211005: // Ice Splitter
                case 2121006: // Paralyze
                case 21120006: // Tempest
                case 22121000:
                case 90001006:
                case 2221001:
                    ret.monsterStatus.put(MonsterTemporaryStat.Frozen, 1);
                    ret.info.put(MapleStatInfo.time, ret.info.get(MapleStatInfo.time) * 2); // freezing skills are a little strange
                    break;
                case 2101003: // fp slow
                case 2201003: // il slow
                case 12101001:
                case 90001002:
                    ret.monsterStatus.put(MonsterTemporaryStat.Speed, ret.info.get(MapleStatInfo.x));
                    break;
                case 5011002:
                    ret.monsterStatus.put(MonsterTemporaryStat.Speed, ret.info.get(MapleStatInfo.z));
                    break;
                case 1121010: //enrage
                case 32121010:
                case 51121006: // Mihile || Roiling Soul
                    ret.statups.put(CharacterTemporaryStat.Enrage, ret.info.get(MapleStatInfo.x) * 100 + ret.info.get(MapleStatInfo.mobCount));//ret.info.get(MapleStatInfo.z)));//크리티컬까지 같이올라감                       
                    ret.statups.put(CharacterTemporaryStat.EnrageCr, ret.info.get(MapleStatInfo.z));
                    ret.statups.put(CharacterTemporaryStat.EnrageCrDamMin, ret.info.get(MapleStatInfo.y));
                    //ret.statups.put(MapleBuffStat.ㅇㅇㅇㅇㅇㅇㅇㅇㅇ, 500000));                    
                    break;
                case 51111003: // Mihile || Radiant Charge
                    ret.statups.put(CharacterTemporaryStat.CounterAttack, ret.info.get(MapleStatInfo.indieDamR));
                    // ret.statups.put(MapleBuffStat.RADIANT_CHARGE, ret.info.get(MapleStatInfo.x) * 100 + ret.info.get(MapleStatInfo.mobCount));
                    break;
                case 22161002:
                    ret.monsterStatus.put(MonsterTemporaryStat.GhostLettering, ret.info.get(MapleStatInfo.x));
                    break;
                case 23111002:
                    ret.monsterStatus.put(MonsterTemporaryStat.AddDamParty, 300);
                    break;
                case 90001003:
                    ret.monsterStatus.put(MonsterTemporaryStat.Poison, 1);
                    break;
                case 4121004: // Ninja ambush
                case 4221004:
                    ret.monsterStatus.put(MonsterTemporaryStat.NinjaAmbush, (int) ret.info.get(MapleStatInfo.damage));
                    break;
                case 2311005:
                    ret.monsterStatus.put(MonsterTemporaryStat.Doom, 1);
                    break;
                case 32111006:
                    ret.statups.put(CharacterTemporaryStat.Reaper, 1);
                    break;
                case 35121003:
                    //ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    break;
                case 35111001:
                case 35111010:
                case 35111009:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.Puppet, 1);
                    break;
                case 4341006:
                case 3120012:
                case 3220012:
                case 3111002: // puppet ranger
                case 3211002: // puppet sniper
                case 13111004: // puppet cygnus
                case 5211001: // Pirate octopus summon
                case 5220002: // wrath of the octopi
                case 33111003:
                case 5321003:
                case 5211014:
                case 13120007:
                    ret.statups.put(CharacterTemporaryStat.Puppet, 1);
                    break;
                case 3120006:
                case 3220005:
                    ret.statups.put(CharacterTemporaryStat.SpiritInfusion, 1);
                    break;
                case 5220019:
                    ret.info.put(MapleStatInfo.time, 120000);
                    break;
                case 5211011:
                case 5211015:
                case 5211016:
                case 5711001: // turret
                case 2121005: // elquines
                case 3201007:
                case 3101007:
                case 3111005: // golden hawk
                case 33111005:
                case 33101011:
                case 35111002:
                case 3121006: // phoenix
                case 23111008:
                case 23111009:
                case 23111010:
                    ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    ret.monsterStatus.put(MonsterTemporaryStat.Stun, Integer.valueOf(1));
                    break;
                //case 3221005: // frostprey
                case 2221005: // ifrit
                case 3211005: //프리저                   
                    ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    ret.monsterStatus.put(MonsterTemporaryStat.Frozen, Integer.valueOf(1));
                    break;
                case 35111005:
                    //ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    ret.monsterStatus.put(MonsterTemporaryStat.Speed, ret.info.get(MapleStatInfo.x));
                    ret.monsterStatus.put(MonsterTemporaryStat.Pdr, ret.info.get(MapleStatInfo.y));
                    break;
                case 1321007: // Beholder
                    ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    ret.statups.put(CharacterTemporaryStat.Beholder, (int) ret.level);
                    break;
                case 5121015:
                    ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.x));
                    break;
                case 2321003: // bahamut
                case 5211002: // Pirate bird summon
                case 11001004:
                case 12001004:
                case 12111004: // Itrit
                case 13001004:
                case 14001005:
                case 15001004:
                case 1196: // Mihile's Soul
                case 1197: // Oz's Flame
                case 1198: // Irena's Wind
                case 1199: // Eckhart's Darkness
                case 1200: // Hawkeye's Lightning
                //case 35111011:
                case 35121009:
                case 35121011:
                case 33101008: //summon - its raining mines
                case 4111007: //dark flare
                case 4211007: //dark flare
                case 5320011:
                case 5321004:
                case 22171052: // 서먼 오닉스드래곤    
                case 36121002:
                case 36121013:
                case 36121014:
                case 1210: // 테스트
                case 1211:
                case 62111003:
                case 13111024:
                case 52101003:
                case 21111034:
                    ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    break;
                case 35121010:
                    ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.x));
                    break;
                case 31121005:
                    ret.statups.put(CharacterTemporaryStat.DamR, (int) ret.info.get(MapleStatInfo.damR));
                    ret.statups.put(CharacterTemporaryStat.IndieMaxHpR, ret.info.get(MapleStatInfo.indieMhpR));
                    ret.statups.put(CharacterTemporaryStat.DevilishPower, 6); // mob count
                    break;
                case 2311003: // hs
                case 9001002: // GM hs
                case 9101002:
                    ret.statups.put(CharacterTemporaryStat.HolySymbol, ret.info.get(MapleStatInfo.x));
                    break;
                case 33101009:
                    ret.statups.put(CharacterTemporaryStat.EchoOfHero, ret.info.get(MapleStatInfo.x));
                    break;
                case 80001034: //virtue
//                case 80001035: //virtue
                case 80001036: //virtue
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 2211004: // il seal
                case 2111004: // fp seal
                case 12111002: // cygnus seal
                case 90001005:
                    ret.monsterStatus.put(MonsterTemporaryStat.Seal, 1);
                    break;
                case 24121003:
                    ret.info.put(MapleStatInfo.damage, ret.info.get(MapleStatInfo.v));
                    ret.info.put(MapleStatInfo.attackCount, ret.info.get(MapleStatInfo.w));
                    ret.info.put(MapleStatInfo.mobCount, ret.info.get(MapleStatInfo.x));
                    break;
                case 4111003: // shadow web
                case 14111001:
                    ret.monsterStatus.put(MonsterTemporaryStat.ShadowWeb, 1);
                    break;
                case 4111009: // Shadow Stars
                case 5201008:
                case 14111007:
                    ret.statups.put(CharacterTemporaryStat.NoBulletConsume, 0);
                    break;
                case 2121004:
                case 2221004:
                case 2321004: // Infinity
                    ret.hpR = ret.info.get(MapleStatInfo.y) / 100.0;
                    ret.mpR = ret.info.get(MapleStatInfo.y) / 100.0;
                    ret.statups.put(CharacterTemporaryStat.Infinity, ret.info.get(MapleStatInfo.damage));
                    ret.statups.put(CharacterTemporaryStat.Stance, ret.info.get(MapleStatInfo.prop));
                    break;
                case 22181004:
                    ret.statups.put(CharacterTemporaryStat.OnixWill, (int) ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.Stance, (int) ret.info.get(MapleStatInfo.prop));
                    break;
                case 1121002:
                case 1221002:
                case 1321002: // Stance
                case 21121003: // Aran - Freezing Posture
                case 32121005:
                case 32111014: // Battle Mage || Stance
                case 51121004: // Mihile || Stance
                case 50001214:
                case 80001140:
                    ret.statups.put(CharacterTemporaryStat.Stance, (int) ret.info.get(MapleStatInfo.prop));
                    break;
                case 5321010:
                    ret.statups.put(CharacterTemporaryStat.Stance, (int) ret.info.get(MapleStatInfo.x));
                    break;
                case 2121002: // mana reflection
                case 2221002:
                case 2321002:
                    ret.statups.put(CharacterTemporaryStat.ManaReflection, 1);
                    break;
                case 2321005: // holy shield, TODO JUMP
                    ret.statups.put(CharacterTemporaryStat.AdvancedBless, ret.info.get(MapleStatInfo.x));
                    break;
                case 3121007: // Hamstring
                    ret.statups.put(CharacterTemporaryStat.IllusionStep, ret.info.get(MapleStatInfo.x));
                    break;
                case 3221006: // 일루젼스탭됨
                    ret.statups.put(CharacterTemporaryStat.IllusionStep, ret.info.get(MapleStatInfo.x));
                    break;
                case 33111004:
                    ret.statups.put(CharacterTemporaryStat.Blind, ret.info.get(MapleStatInfo.x));
                    break;
                case 33111007: //feline berserk
                    ret.statups.put(CharacterTemporaryStat.BeastFormMaxHP, ret.info.get(MapleStatInfo.x));//ret.info.get(MapleStatInfo.x)));//체력값올라감                    
                    ret.statups.put(CharacterTemporaryStat.Speed, ret.info.get(MapleStatInfo.y));
                    ret.statups.put(CharacterTemporaryStat.BeastFormDamageUp, ret.info.get(MapleStatInfo.z));
                    ret.statups.put(CharacterTemporaryStat.IndieBooster, ret.info.get(MapleStatInfo.indieBooster));
                    break;
                case 2301004:
                case 9001003:
                case 9101003:
                    ret.statups.put(CharacterTemporaryStat.Bless, (int) ret.level);
                    break;
                case 32120000:
                    ret.monsterStatus.put(MonsterTemporaryStat.Poison, ret.info.get(MapleStatInfo.damage));
                case 32001003: //dark aura
                    ret.statups.put(CharacterTemporaryStat.DarkAura, ret.info.get(MapleStatInfo.x));
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.overTime = true;
                    break;
                case 32111012:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.overTime = true;
                    break;
                case 32110000:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    //ret.statups.put(MapleBuffStat.AURA, (int) ret.level);
                    ret.statups.put(CharacterTemporaryStat.BlueAura, (int) ret.level);
                    ret.overTime = true;
                    break;
                case 32120001:
                    ret.monsterStatus.put(MonsterTemporaryStat.Speed, ret.info.get(MapleStatInfo.speed));
                    ret.statups.put(CharacterTemporaryStat.OnixGodBless, ret.info.get(MapleStatInfo.z));
                case 32101003: //yellow aura
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.YellowAura, (int) ret.level);
                    break;
                case 33101004: //it's raining mines
                    ret.statups.put(CharacterTemporaryStat.RainingMines, ret.info.get(MapleStatInfo.x)); //x?
                    break;
                case 35101007: //perfect armor
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.Guard, ret.info.get(MapleStatInfo.x));
                    break;
                case 31101003:
                    ret.statups.put(CharacterTemporaryStat.PowerGuard, ret.info.get(MapleStatInfo.y));
                    break;
                case 35121006:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.SafetyProc, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.SafetyAbsorb, ret.info.get(MapleStatInfo.y));
                    break;
                case 80001040:
                case 20021110:
                case 20031203:
                    ret.moveTo = ret.info.get(MapleStatInfo.x);
                    break;
                case 5311004:
                    ret.statups.put(CharacterTemporaryStat.Roulette, 0);
                    break;
                case 80001089: // Soaring
                case 42111000:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.Flying, 1);
                    break;
                case 20031205:
                case 154001003: // 보이드 러쉬
                    ret.statups.put(CharacterTemporaryStat.Invisible, ret.info.get(MapleStatInfo.x));
                    break;
                case 5221018:
                    ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.damR));
                    ret.statups.put(CharacterTemporaryStat.Stance, ret.info.get(MapleStatInfo.z));
                    break;
                case 5211009:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.y));
                    ret.statups.put(CharacterTemporaryStat.EnhancedMad, ret.info.get(MapleStatInfo.y));
                    ret.info.put(MapleStatInfo.attacksRemaining, ret.info.get(MapleStatInfo.x));
                    break;
                case 35001001: //flame
                case 35101009:
                case 35100008:
                    ret.info.put(MapleStatInfo.time, 12000);
                    ret.statups.put(CharacterTemporaryStat.Mechanic, (int) level); //ya wtf
                    break;
                case 35111004:
                case 35121013:
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.Mechanic, (int) level); //ya wtf
                    break;
                case 35121005: //missile
                    ret.info.put(MapleStatInfo.time, 2100000000);
                    ret.statups.put(CharacterTemporaryStat.Mechanic, (int) level); //ya wtf
                    break;
                case 35121054:
                    ret.statups.put(CharacterTemporaryStat.DamAbsorbShield, ret.info.get(MapleStatInfo.y)); //ya wtf
                    break;
                case 1311008:
                    ret.statups.put(CharacterTemporaryStat.Str, ret.info.get(MapleStatInfo.str));
                    //ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.x));
                    break;
                case 10001075: // Cygnus Echo
                    ret.statups.put(CharacterTemporaryStat.EchoOfHero, ret.info.get(MapleStatInfo.x));
                    break;
                case 31121007: // boundless rage
                    ret.statups.put(CharacterTemporaryStat.InfinityForce, 1); // for now
                    break;
                case 31111004: // black hearted strength					
                    ret.statups.put(CharacterTemporaryStat.AsrR, ret.info.get(MapleStatInfo.y));
                    ret.statups.put(CharacterTemporaryStat.TerR, ret.info.get(MapleStatInfo.z));
                    ret.statups.put(CharacterTemporaryStat.PMddR, ret.info.get(MapleStatInfo.x));
                    break;
                case 1321015:
                    ret.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, ret.info.get(MapleStatInfo.x));
                    break;
                case 24121004:
                    ret.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.damR));
                    break;
                /* Hayato */
                case 60001002:
                case 60011023:
                    ret.info.put(MapleStatInfo.time, ret.info.get(MapleStatInfo.time));
                    ret.statups.put(CharacterTemporaryStat.DamR, ret.info.get(MapleStatInfo.indieDamR));
                    break;
                case 61001001:
                    ret.statups.put(CharacterTemporaryStat.Stance, ret.info.get(MapleStatInfo.prop));
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 61101003:
                    ret.statups.put(CharacterTemporaryStat.MaxHp, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.MaxMp, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.Speed, ret.info.get(MapleStatInfo.speed));
                    ret.statups.put(CharacterTemporaryStat.Jump, ret.info.get(MapleStatInfo.jump));
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.padX));
                    ret.statups.put(CharacterTemporaryStat.EnhancedMad, ret.info.get(MapleStatInfo.padX));
                    break;
                case 61101005:
                case 62101003:
                case 65101008:
                    ret.statups.put(CharacterTemporaryStat.Booster, ret.info.get(MapleStatInfo.x));
                    break;
                case 61121002:
                    ret.info.put(MapleStatInfo.time, ret.info.get(MapleStatInfo.time));
                    ret.statups.put(CharacterTemporaryStat.CriticalBuff, ret.info.get(MapleStatInfo.cr));
                    break;
                case 61121003:
                    ret.statups.put(CharacterTemporaryStat.AsrR, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.TerR, ret.info.get(MapleStatInfo.y));
                    break;
                case 61121005:
                case 62121006:
                case 65121011:
                    ret.statups.put(CharacterTemporaryStat.MapleWarrior, ret.info.get(MapleStatInfo.x));
                    break;
                case 65121009:
                    ret.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 65121010:
                    ret.statups.put(CharacterTemporaryStat.IncCriticalDam, ret.info.get(MapleStatInfo.y));
                    break;
                case 52001001:
                    ret.statups.put(CharacterTemporaryStat.MagicGuard, 1);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 52120001:
                    ret.statups.put(CharacterTemporaryStat.MagicGuard, 1);
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.EnhancedMad, ret.info.get(MapleStatInfo.x));
                    break;
                case 52111008:
                    ret.statups.put(CharacterTemporaryStat.HolyMagicShell, ret.info.get(MapleStatInfo.x));
                    ret.hpR = ret.info.get(MapleStatInfo.z) / 100.0;
                    ret.info.put(MapleStatInfo.attacksRemaining, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 52111009:
                    ret.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, ret.info.get(MapleStatInfo.x));
                    break;
                case 13111023:
                case 13120008:
                case 21111016:
                case 21120003:
                    ret.statups.put(CharacterTemporaryStat.CriticalBuff, ret.info.get(MapleStatInfo.x));
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 62101002:
                case 62120011:
                    ret.statups.put(CharacterTemporaryStat.PvPScoreBonus, 1);
                    break;
                case 62001100: // 귀야차Ⅰ
                case 62101006: // 귀야차Ⅱ
                case 62111005: // 귀야차Ⅲ
                case 62121009: // 귀야차Ⅳ
                    ret.statups.put(CharacterTemporaryStat.Summon, 1);
                    break;
                case 35121055:
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 8001080:
                case 8001084:
                    ret.statups.put(CharacterTemporaryStat.WeaknessMdamage, 1);
                    break;
                case 8001081:
                case 8001085:
                    ret.statups.put(CharacterTemporaryStat.PowerGuard, 800);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 8001082:
                case 8001086:
                    ret.statups.put(CharacterTemporaryStat.DamR, 10);
                    break;
                case 8001083:
                case 8001087:
                    ret.statups.put(CharacterTemporaryStat.ArcaneAim, 1);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                case 8001060: // 시그너스
                case 8001061: // 발록
                case 8001062: // 반 레온
                case 8001063: // 핑크빈
                case 8001064: // 아카이럼
                case 8001065: // 피아누스
                case 8001066: // 힐라
                case 8001067: // 자쿰
                case 8001068: // 블랙 슬라임
                case 8001069: // 매그너스
                case 8001070: // 무르무르
                case 8001076: // 피에르
                case 8001077: // 반반
                case 8001078: // 블러디 퀸
                case 8001079: // 벨룸
                case 8001680: // 스우
                case 8001681: // 스우
                case 8001682: // 교도관 아니
                case 8001683: // 드래곤라이더
                case 8001684: // 락 스피릿
                case 8001685: // 무공
                case 8001686: // 데미안
                case 8001687: // 데미안
                case 8001688: // 루시드
                case 8001689: // 루시드
                case 8001690: // 파풀라투스
                case 8001691: // 윌
                case 8001692: // 윌
                case 8001693: // 진 힐라
                case 8001694: // 진 힐라
                case 8001695: // 듄켈
                case 8001696: // 듄켈
                    ret.statups.put(CharacterTemporaryStat.EventRate, 1);
                    ret.statups.put(CharacterTemporaryStat.SaintSaver, 1);
                    break;
                default:
                    break;
            }
            if (GameConstants.isBeginnerJob(sourceid / 10000)) {
                switch (sourceid % 10000) {
                    //angelic blessing: HACK, we're actually supposed to use the passives for atk/matk buff
                    case 1105:
                        ret.statups.put(CharacterTemporaryStat.PvPDamageSkill, 1);
                        ret.info.put(MapleStatInfo.time, 2100000000);
                        break;
                    case 93:
                        ret.statups.put(CharacterTemporaryStat.PvPScoreBonus, 1);
                        break;
                    case 8001:
                        ret.statups.put(CharacterTemporaryStat.SoulArrow, ret.info.get(MapleStatInfo.x));
                        break;
                    case 1005: // Echo of Hero
                        ret.statups.put(CharacterTemporaryStat.EchoOfHero, ret.info.get(MapleStatInfo.x));
                        break;
                    case 1011: // Berserk fury
                        ret.statups.put(CharacterTemporaryStat.DojangBerserk, ret.info.get(MapleStatInfo.x));
                        break;
                    case 1010:
                        ret.statups.put(CharacterTemporaryStat.DojangInvincible, 1);
                        break;
                    case 1001:
                        if (sourceid / 10000 == 3001 || sourceid / 10000 == 3000) {
                            ret.statups.put(CharacterTemporaryStat.Infltrate, ret.info.get(MapleStatInfo.x));
                        } else {
                            ret.statups.put(CharacterTemporaryStat.Regen, ret.info.get(MapleStatInfo.x));
                        }
                        break;
                    case 8003:
                        ret.statups.put(CharacterTemporaryStat.MaxHp, ret.info.get(MapleStatInfo.x));
                        ret.statups.put(CharacterTemporaryStat.MaxMp, ret.info.get(MapleStatInfo.x));
                        break;
                    case 10008004:
                    case 20008004:
                    case 20018004:
                    case 20028004:
                    case 20038004:
                    case 30008004:
                    case 30018004:
                    case 8004:
                        ret.statups.put(CharacterTemporaryStat.CombatOrders, ret.info.get(MapleStatInfo.x));
                        break;
                    case 10008005:
                    case 20008005:
                    case 20018005:
                    case 20028005:
                    case 20038005:
                    case 30008005:
                    case 30018005:
                    case 8005:
                        ret.statups.put(CharacterTemporaryStat.AdvancedBless, 1);
                        break;
                    case 8006:
                        ret.statups.put(CharacterTemporaryStat.WindBooster, ret.info.get(MapleStatInfo.x));
                        break;
                    case 103:
                        ret.monsterStatus.put(MonsterTemporaryStat.Stun, 1);
                        break;
                    case 99:
                    case 104:
                        ret.monsterStatus.put(MonsterTemporaryStat.Frozen, 1);
                        ret.info.put(MapleStatInfo.time, ret.info.get(MapleStatInfo.time) * 2); // freezing skills are a little strange
                        break;
                    case 10008002:
                    case 20008002:
                    case 20018002:
                    case 20028002:
                    case 20038002:
                    case 30008002:
                    case 30018002:
                    case 8002:
                        ret.statups.put(CharacterTemporaryStat.SharpEyes, (ret.info.get(MapleStatInfo.x) << 8) + ret.info.get(MapleStatInfo.y));
                        break;
                    case 1026: // Soaring
                    case 1142: // Soaring
                        ret.info.put(MapleStatInfo.time, 2100000000);
                        ret.statups.put(CharacterTemporaryStat.Flying, 1);
                        break;
                }
            }
        }
        if (!ret.isSkill()) {
            switch (sourceid) {
                case 2022125:
                    ret.statups.put(CharacterTemporaryStat.EnhancedPdd, 1);
                    break;
                case 2022126:
                    ret.statups.put(CharacterTemporaryStat.EnhancedMdd, 1);
                    break;
                case 2022127:
                    ret.statups.put(CharacterTemporaryStat.Acc, 1);
                    break;
                case 2022128:
                    ret.statups.put(CharacterTemporaryStat.Eva, 1);
                    break;
                case 2022129:
                    ret.statups.put(CharacterTemporaryStat.EnhancedPad, 1);
                    break;
                case 2022746://아크엔젤
                case 2022764:
                case 2022747://다크엔젤
                case 2022823://화이트엔젤    
                    ret.statups.put(CharacterTemporaryStat.RepeatEffect, 1);//아이템이펙트 ...                      
                    break;
            }
        }
        if (ret.isBindSkill(sourceid)) {
            ret.monsterStatus.put(MonsterTemporaryStat.Frozen, 1);
        }
        if (ret.isPoison()) {
            ret.monsterStatus.put(MonsterTemporaryStat.Poison, 1);
        }
        if (ret.isMorph() || ret.isPirateMorph()) {
            ret.statups.put(CharacterTemporaryStat.Morph, ret.getMorph());
        }
        if (ret.isMonsterRiding()) {
            ret.statups.put(CharacterTemporaryStat.MonsterRiding, 1);
        }
        /*
        if (ret.isHeroWill()) {
            ret.info.put(MapleStatInfo.time, 3000);
            ret.statups.put(CharacterTemporaryStat.Unk_0x2_4, 1);
        }
         */
        return ret;
    }

    /**
     * @param applyto
     * @param obj
     * @param attack damage done by the skill
     */
    public final void applyPassive(final MapleCharacter applyto, final MapleMapObject obj) {
        if (makeChanceResult() && !GameConstants.isDemon(applyto.getJob())) { // demon can't heal mp
            switch (sourceid) { // MP eater
                case 2100000:
                case 2200000:
                case 2300000:
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    final MapleMonster mob = (MapleMonster) obj; // x is absorb percentage
                    if (!mob.getStats().isBoss()) {
                        final int absorbMp = Math.min((int) (mob.getMobMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.getStat().setMp(applyto.getStat().getMp() + absorbMp, applyto);
                            applyto.getClient().getSession().write(EffectPacket.showOwnBuffEffect(sourceid, 1, applyto.getLevel(), level));
                            applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffeffect(applyto.getId(), sourceid, 1, applyto.getLevel(), level), false);
                        }
                    }
                    break;
            }
        }
    }

    public final boolean applyTo(MapleCharacter chr) {
        return applyTo(chr, chr, true, null, info.get(MapleStatInfo.time));
    }

    public final boolean applyTo(MapleCharacter chr, int ratetime) {
        return applyTo(chr, chr, true, null, (int) ratetime);
    }

    public final boolean applyTo(MapleCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, info.get(MapleStatInfo.time));
    }

    public final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos, int newDuration) {
        if (isHeal() && (applyfrom.getMapId() == 749040100 || applyto.getMapId() == 749040100)) {
            applyfrom.getClient().getSession().write(CWvsContext.enableActions());
            return false; //z
        } else if ((isSoaring_Mount() && applyfrom.getBuffedValue(CharacterTemporaryStat.MonsterRiding) == null)) {
            applyfrom.getClient().getSession().write(CWvsContext.enableActions());
            return false;
        } else if (sourceid == 4341006 && applyfrom.getBuffedValue(CharacterTemporaryStat.ShadowPartner) == null) {
            applyfrom.getClient().getSession().write(CWvsContext.enableActions());
            return false;
        } else if (isShadow() && applyfrom.getJob() / 100 % 10 != 4) { //pirate/shadow = dc
            //System.out.println("여기인거같은데?");
            applyfrom.getClient().getSession().write(CWvsContext.enableActions());
            return false;
        } else if (sourceid == 33101004 && applyfrom.getMap().isTown()) {
            applyfrom.getClient().getSession().write(CWvsContext.enableActions());
            return false;
        }
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);
        //System.out.println("여기인거같은데? 1");
        final PlayerStats stat = applyto.getStat();
        if (primary) {
            if (info.get(MapleStatInfo.itemConNo) != 0 && !applyto.isClone() && !applyto.inPVP()) {
                if (!applyto.haveItem(info.get(MapleStatInfo.itemCon), info.get(MapleStatInfo.itemConNo), false, true)) {
                    applyto.getClient().getSession().write(CWvsContext.enableActions());
                    return false;
                }
                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(info.get(MapleStatInfo.itemCon)), info.get(MapleStatInfo.itemCon), info.get(MapleStatInfo.itemConNo), false, true);
            }
        } else if (!primary && isResurrection()) {
            hpchange = stat.getMaxHp();
            applyto.setStance(0); //TODO fix death bug, player doesnt spawn on other screen
        }
        if (isHeroWill()) {
            applyto.dispelDebuffs();
        } else if (cureDebuffs.size() > 0) {
            for (final MonsterSkill debuff : cureDebuffs) {
                applyfrom.dispelDebuff(debuff);
            }
        } else if (isMPRecovery()) {
            final int toDecreaseHP = ((stat.getMaxHp() / 100) * 10);
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP; // -10% of max HP
                mpchange += ((toDecreaseHP / 100) * getY());
            } else {
                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
            }
        }
        // 폭풍의 시
        applyto.OnUseMPHPCon(hpchange, mpchange, 0, sourceid, 0, primary, isHeal());
        /*
        final Map<MapleStat, Integer> hpmpupdate = new EnumMap<>(MapleStat.class);
        if (hpchange != 0) {
            if (!primary && applyfrom.getId() != applyto.getId() && isHeal()) {
                int realHealedHp = Math.max(0, Math.min(stat.getCurrentMaxHp() - stat.getHp(), hpchange));
                if (realHealedHp > 0) {
                    int maxmp = applyfrom.getStat().getCurrentMaxMp(applyfrom.getJob()) / 256;
                    int expa = 20 * (realHealedHp) / (8 * maxmp + 190);
                    applyfrom.gainExp(expa, true, false, true);
                }
            }
            stat.setHp((stat.getHp() + hpchange), applyto);
            hpmpupdate.put(MapleStat.HP, Integer.valueOf(stat.getHp()));
        }
        if (mpchange != 0) {
            stat.setMp(stat.getMp() + mpchange, applyto);
            hpmpupdate.put(MapleStat.MP, Integer.valueOf(stat.getMp()));
        }
        applyto.getClient().getSession().write(CWvsContext.updatePlayerStats(hpmpupdate, true, applyto));
         */

        if (expinc != 0) {
            applyto.gainExp(expinc, true, true, false);
            applyto.getClient().getSession().write(EffectPacket.showForeignEffect(20));
        } else if (sourceid / 10000 == 238) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final int mobid = ii.getCardMobId(sourceid);
            if (mobid > 0) {
                applyto.getMonsterBook().monsterCaught(applyto.getClient(), mobid, sourceid, MapleLifeFactory.getMonsterStats(mobid).getName());
            }
        } else if (isReturnScroll()) {
            applyReturnScroll(applyto);
        } else if (useLevel > 0 && !skill) {
            applyto.setExtractor(new MapleExtractor(applyto, sourceid, useLevel * 50, 1440)); //no clue about time left
            applyto.getMap().spawnExtractor(applyto.getExtractor());
        } else if (isMistEruption()) {
            int i = (this.info.get(MapleStatInfo.y));
            for (MapleMist m : applyto.getMap().getAllMistsThreadsafe()) {
                if (m.getOwnerId() == applyto.getId() && m.getSourceSkill().getId() == 2111003) {
                    if (m.getSchedule() != null) {
                        m.getSchedule().cancel(false);
                        m.setSchedule(null);
                    }
                    if (m.getPoisonSchedule() != null) {
                        m.getPoisonSchedule().cancel(false);
                        m.setPoisonSchedule(null);
                    }
                    applyto.getMap().broadcastMessage(CField.removeMist(m.getObjectId(), true));
                    applyto.getMap().removeMapObject(m);
                    i--;
                    if (i <= 0) {
                        break;
                    }
                }
            }
        } else if (cosmetic > 0) {
            if (cosmetic >= 30000) {
                applyto.setHair(cosmetic);
                applyto.updateSingleStat(MapleStat.Hair, cosmetic);
            } else if (cosmetic >= 20000) {
                applyto.setFace(cosmetic);
                applyto.updateSingleStat(MapleStat.Face, cosmetic);
            } else if (cosmetic < 100) {
                applyto.setSkinColor((byte) cosmetic);
                applyto.updateSingleStat(MapleStat.Skin, cosmetic);
            }
            applyto.equipChanged();
        } else if (bs > 0) {
            if (!applyto.inPVP()) {
                return false;
            }
            final int x = Integer.parseInt(applyto.getEventInstance().getProperty(String.valueOf(applyto.getId())));
            applyto.getEventInstance().setProperty(String.valueOf(applyto.getId()), String.valueOf(x + bs));
            applyto.getClient().getSession().write(CField.getPVPScore(x + bs, false));
        } else if (info.get(MapleStatInfo.iceGageCon) > 0) {
            if (!applyto.inPVP()) {
                return false;
            }
            final int x = Integer.parseInt(applyto.getEventInstance().getProperty("icegage"));
            if (x < info.get(MapleStatInfo.iceGageCon)) {
                return false;
            }
            applyto.getEventInstance().setProperty("icegage", String.valueOf(x - info.get(MapleStatInfo.iceGageCon)));
            applyto.getClient().getSession().write(CField.getPVPIceGage(x - info.get(MapleStatInfo.iceGageCon)));
            applyto.applyIceGage(x - info.get(MapleStatInfo.iceGageCon));
        } else if (recipe > 0) {
            if (applyto.getSkillLevel(recipe) > 0 || applyto.getProfessionLevel((recipe / 10000) * 10000) < reqSkillLevel) {
                return false;
            }
            applyto.changeSingleSkillLevel(SkillFactory.getCraft(recipe), Integer.MAX_VALUE, recipeUseCount, (long) (recipeValidDay > 0 ? (System.currentTimeMillis() + recipeValidDay * 24L * 60 * 60 * 1000) : -1L));
        } else if (isComboRecharge()) {
            applyto.setCombo((short) Math.min(30000, applyto.getCombo() + info.get(MapleStatInfo.y)));
            applyto.setLastCombo(System.currentTimeMillis());
            applyto.getClient().sendPacket(CUserLocal.incComboResponseByComboRecharge(applyto.getCombo()));
            SkillFactory.getSkill(21000000).getEffect(10).applyComboBuff(applyto, applyto.getCombo());
        } else if (isDragonBlink()) {
            final MaplePortal portal = applyto.getMap().getPortal(Randomizer.nextInt(applyto.getMap().getPortals().size()));
            if (portal != null) {
                applyto.getClient().sendPacket(CUserLocal.randomTeleport((byte) portal.getId()));
                applyto.getMap().movePlayer(applyto, portal.getPosition());
                applyto.checkFollow();
            }
        } else if (isSpiritClaw()) {
            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            boolean itemz = false;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                Item item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 100) {
                        MapleInventoryManipulator.removeFromSlot(applyto.getClient(), MapleInventoryType.USE, (short) i, (short) 100, false, true);
                        itemz = true;
                        break;
                    }
                }
            }
            if (!itemz) {
                return false;
            }
        } else if (isSpiritBlast()) {
            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            boolean itemz = false;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                Item item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isBullet(item.getItemId()) && item.getQuantity() >= 100) {
                        MapleInventoryManipulator.removeFromSlot(applyto.getClient(), MapleInventoryType.USE, (short) i, (short) 100, false, true);
                        itemz = true;
                        break;
                    }
                }
            }
            if (!itemz) {
                return false;
            }
        } else if (cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
            for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
            }
        } else if (nuffSkill != 0 && applyto.getParty() != null) {
            final MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(nuffSkill);
            if (skil != null) {
                final MonsterSkill dis = skil.getDisease();
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (applyto.getParty() == null || chr.getParty() == null || (chr.getParty().getId() != applyto.getParty().getId())) {
                        if (skil.targetsAll || Randomizer.nextBoolean()) {
                            if (dis == null) {
                                chr.dispel();
                            } else if (skil.getSkill() == null) {
                                chr.giveDebuff(dis, 1, 30000, dis.getDisease(), 1, 0);
                            } else {
                                chr.giveDebuff(dis, skil.getSkill(), 0);
                            }
                            if (!skil.targetsAll) {
                                break;
                            }
                        }
                    }
                }
            }
        } else if ((effectedOnEnemy > 0 || effectedOnAlly > 0) && primary && applyto.inPVP()) {
            final int eventType = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
            if (eventType > 0 || effectedOnEnemy > 0) {
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (chr.getId() != applyto.getId() && (effectedOnAlly > 0 ? (chr.getTeam() == applyto.getTeam()) : (chr.getTeam() != applyto.getTeam() || eventType == 0))) {
                        applyTo(applyto, chr, false, pos, newDuration);
                    }
                }
            }
        } else if (mobSkill > 0 && mobSkillLevel > 0 && primary && applyto.inPVP()) {
            if (effectedOnEnemy > 0) {
                final int eventType = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (chr.getId() != applyto.getId() && (chr.getTeam() != applyto.getTeam() || eventType == 0)) {
                        chr.disease(mobSkill, mobSkillLevel, 0);
                    }
                }
            } else {
                if (sourceid == 2910000 || sourceid == 2910001) { //red flag
                    applyto.getClient().getSession().write(EffectPacket.showOwnBuffEffect(sourceid, 13, applyto.getLevel(), level));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffeffect(applyto.getId(), sourceid, 13, applyto.getLevel(), level), false);

                    applyto.getClient().getSession().write(EffectPacket.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Effect", 0, 0));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Effect", 0, 0), false);
                    if (applyto.getTeam() == (sourceid - 2910000)) { //restore duh flag
                        if (sourceid == 2910000) {
                            applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's flag has been restored.");
                        } else {
                            applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's flag has been restored.");
                        }
                        applyto.getMap().spawnAutoDrop(sourceid, applyto.getMap().getGuardians().get(sourceid - 2910000).left);
                    } else {
                        applyto.disease(mobSkill, mobSkillLevel, 0);
                        if (sourceid == 2910000) {
                            applyto.getEventInstance().setProperty("redflag", String.valueOf(applyto.getId()));
                            applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's flag has been captured!");
                            applyto.getClient().getSession().write(EffectPacket.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Tail/Red", 600000, 0));
                            applyto.getMap().broadcastMessage(applyto, EffectPacket.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Tail/Red", 600000, 0), false);
                        } else {
                            applyto.getEventInstance().setProperty("blueflag", String.valueOf(applyto.getId()));
                            applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's flag has been captured!");
                            applyto.getClient().getSession().write(EffectPacket.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Tail/Blue", 600000, 0));
                            applyto.getMap().broadcastMessage(applyto, EffectPacket.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Tail/Blue", 600000, 0), false);
                        }
                    }
                } else {
                    applyto.disease(mobSkill, mobSkillLevel, 0);
                }
            }
        } else if (randomPickup != null && randomPickup.size() > 0) {
            MapleItemInformationProvider.getInstance().getItemEffect(randomPickup.get(Randomizer.nextInt(randomPickup.size()))).applyTo(applyto);
        }
        for (Entry<MapleTraitType, Integer> t : traits.entrySet()) {
            applyto.getTrait(t.getKey()).addExp(t.getValue(), applyto);
        }
        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null && (sourceid != 32111006 || (applyfrom.getBuffedValue(CharacterTemporaryStat.Reaper) != null && !primary))) {
            if (sourceid == 5321004) {
                pos.x -= 25;
            } else if (sourceid == 5320011) {
                pos.x += 25;
            }
            int summId = sourceid;
            switch (sourceid) {
                case 3111002: {
                    final Skill a1 = SkillFactory.getSkill(3120012);
                    if (applyfrom.getTotalSkillLevel(a1) > 0) {
                        return a1.getEffect(applyfrom.getTotalSkillLevel(a1)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                    }
                    break;
                }
                case 3111005: {
                    final Skill a1 = SkillFactory.getSkill(3120006);
                    if (a1 != null) {
                        int a2 = applyto.getTotalSkillLevel(a1);
                        if (a2 > 0) {
                            final MapleStatEffect a3 = a1.getEffect(a2);
                            if (a3 != null) {
                                EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                                localstatups.put(CharacterTemporaryStat.SpiritInfusion, 3120006);
                                applyto.getClient().sendPacket(BuffPacket.giveBuff(3120006, newDuration, localstatups, null));
                                final long starttime = System.currentTimeMillis();
                                final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(applyto, a3, starttime, localstatups);
                                final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, newDuration);
                                applyto.registerEffect(a3, starttime, schedule, localstatups, false, newDuration, applyto.getId());
                            }
                        }
                    }
                    break;
                }
                case 3211002: {
                    final Skill a1 = SkillFactory.getSkill(3220012);
                    if (applyfrom.getTotalSkillLevel(a1) > 0) {
                        return a1.getEffect(applyfrom.getTotalSkillLevel(a1)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                    }
                    break;
                }
                case 3211005: {
                    final Skill a1 = SkillFactory.getSkill(3220005);
                    if (a1 != null) {
                        int a2 = applyto.getTotalSkillLevel(a1);
                        if (a2 > 0) {
                            final MapleStatEffect a3 = a1.getEffect(a2);
                            if (a3 != null) {
                                EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                                localstatups.put(CharacterTemporaryStat.SpiritInfusion, 3220005);
                                applyto.getClient().sendPacket(BuffPacket.giveBuff(3220005, newDuration, localstatups, null));
                                final long starttime = System.currentTimeMillis();
                                final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(applyto, a3, starttime, localstatups);
                                final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, newDuration);
                                applyto.registerEffect(a3, starttime, schedule, localstatups, false, newDuration, applyto.getId());
                            }
                        }
                    }
                    break;
                }
            }
            final MapleSummon tosummon = new MapleSummon(applyfrom, summId, getLevel(), new Point(pos == null ? applyfrom.getTruePosition() : pos), summonMovementType);
            if (!tosummon.isPuppet()) {
                applyfrom.getCheatTracker().resetSummonAttack();
            }

            if (!isMechDoor()) {
                applyfrom.cancelEffect(this, false, -1);
            }

            switch (sourceid) {
                case 5211014:
                case 5211011:
                case 5211015:
                case 5211016:
                case 35101005:
                case 35111001:
                case 35111002:
                case 35111009:
                case 35111010:
                case 35111011:
                case 35111005:
                case 35121003:
                case 35121009:
                case 35121010: {
                    break;
                }
                default: {
                    applyfrom.cancelEffectFromBuffStat(CharacterTemporaryStat.Summon);
                    break;
                }
            }
            applyfrom.getMap().spawnSummon(tosummon, true);
            applyfrom.addSummon(tosummon);
            tosummon.addHP(info.get(MapleStatInfo.x).shortValue());

            if (isBeholder()) {
                tosummon.addHP((short) 1);
            } else if (sourceid == 4341006) {
                applyfrom.cancelEffectFromBuffStat(CharacterTemporaryStat.ShadowPartner);
            } else if (sourceid == 32111006) {
                return true; //no buff
            } else if (sourceid == 35111002) {
                List<Integer> count = new ArrayList<>();
                final List<MapleSummon> ss = applyfrom.getSummonsReadLock();
                try {
                    for (MapleSummon s : ss) {
                        if (s.getSkill() == sourceid) {
                            count.add(s.getObjectId());
                        }
                    }
                } finally {
                    applyfrom.unlockSummonsReadLock();
                }
                if (count.size() != 3) {
                    return true;
                }
                if (!applyfrom.isGM()) {
                    applyfrom.getClient().sendPacket(CUserLocal.skillCooltimeSet(sourceid, getCooldown(applyfrom)));
                    applyfrom.addCooldown(sourceid, System.currentTimeMillis(), getCooldown(applyfrom) * 1000);
                }
                applyfrom.getMap().broadcastMessage(CField.teslaTriangle(applyfrom.getId(), count.get(0), count.get(1), count.get(2)));
            } else if (sourceid == 35121003) {
                applyfrom.getClient().getSession().write(CWvsContext.enableActions()); //doubt we need this at all
            } else if (sourceid == 5321004) {
                final MapleSummon twinMonkey = new MapleSummon(applyfrom, 5320011, this.getLevel(), new Point(pos.x + 50, pos.y), SummonMovementType.STATIONARY);
                applyfrom.getMap().spawnSummon(twinMonkey, true);
                applyfrom.addSummon(twinMonkey);
                //SkillFactory.getSkill(5320011).getEffect(applyfrom.getSkillLevel(5321004)).applyTo(applyfrom, applyfrom.getPosition());
            }
        } else if (isMechDoor()) {
            int newId = 0;
            boolean applyBuff = false;
            if (applyto.getMechDoors().size() >= 2) {
                final MechDoor remove = applyto.getMechDoors().remove(0);
                newId = remove.getId();
                applyto.getMap().broadcastMessage(CField.removeMechDoor(remove, true));
                applyto.getMap().removeMapObject(remove);
            } else {
                for (MechDoor d : applyto.getMechDoors()) {
                    if (d.getId() == newId) {
                        applyBuff = true;
                        newId = 1;
                        break;
                    }
                }
            }
            final MechDoor door = new MechDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), newId);
            applyto.getMap().spawnMechDoor(door);
            applyto.addMechDoor(door);
            applyto.getClient().getSession().write(CWvsContext.mechPortal(door.getTruePosition()));
            if (!applyBuff) {
                return true;
            }
        }
        if (primary && availableMap != null) {
            for (Pair<Integer, Integer> e : availableMap) {
                if (applyto.getMapId() < e.left || applyto.getMapId() > e.right) {
                    applyto.getClient().getSession().write(CWvsContext.enableActions());
                    return true;
                }
            }
        }
        //System.out.println("여기인거같은데? 7");
        if (sourceid == 35100008) {
            sourceid = 35101009;
        }
        if (sourceid == 5120012 || sourceid == 5720005 || sourceid == 5220014 || sourceid == 5320007) { // sourceid == 4330009
            overTime = true;
        }
        if (sourceid / 1000 == 2257) {
            overTime = true;
        }
        if (sourceid == 2258114 || sourceid == 27100003 || sourceid == 13111020 || sourceid == 51121052 /*|| sourceid == 35001001 || sourceid == 35101009*/) {
            overTime = true;
        }
        if (sourceid == 80001140) {
            overTime = true;
        }
        if (sourceid >= 80001034 && sourceid <= 80001036) {
            //applyto.addSaintSaver(-applyto.getSaintSaver()); // 세인트 세이버 스택 소모
        }
        if (sourceid > 8001059 && sourceid < 8002000 || sourceid == 62101002) {
            overTime = true;
        }
        if (overTime && !isEnergyCharge()) {
            applyBuffEffect(applyfrom, applyto, primary, newDuration);
        }
        //System.out.println("여기인거같은데? 8");
        if (skill) {
            removeMonsterBuff(applyfrom);
        }
        if (primary) {
            if ((overTime || isHeal()) && !isEnergyCharge()) {
                applyBuff(applyfrom, newDuration);
                // System.out.println("여기인거같은데? 9");
            }
            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }
        if (isMagicDoor()) {
            MapleDoor door = new MapleDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), sourceid); // Current Map door
            if (door.getTownPortal() != null) {

                door.updateTownDoorPosition(applyto.getParty());

                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);

                MapleDoor townDoor = new MapleDoor(door);
                applyto.addDoor(townDoor);
                door.getTown().spawnDoor(townDoor);

                if (applyto.getParty() != null) {
                    applyto.silentPartyUpdate();
                    applyto.addDoor(door);
                }
            } else {
                applyto.dropMessage(5, "You may not spawn a door because all doors in the town are taken.");
            }
        } else if (isMist()) {
            final Rectangle bounds = calculateBoundingBox(pos != null ? pos : applyfrom.getPosition(), applyfrom.isFacingLeft());
            final MapleMist mist = new MapleMist(bounds, applyfrom, this);
            /*
            for (final MapleMist currentMist : applyfrom.getMap().getAllMistsThreadsafe()) {
                if (currentMist != null) {
                    if (currentMist.getSchedule() != null) {
                        currentMist.getSchedule().cancel(false);
                        currentMist.setSchedule(null);
                    }
                    if (currentMist.getPoisonSchedule() != null) {
                        currentMist.getPoisonSchedule().cancel(false);
                        currentMist.setPoisonSchedule(null);
                    }
                    applyfrom.getMap().broadcastMessage(CField.removeMist(currentMist.getObjectId(), false));
                    applyfrom.getMap().removeMapObject(currentMist);
                }
            }
             */
            applyfrom.getMap().spawnMist(mist, getDuration(), false);
            if (isTimeCapsule()) {
                applyfrom.setChair(3010587);
                applyfrom.getClient().getSession().write(CField.TimeCapsule());
                applyfrom.getMap().broadcastMessage(applyfrom, CField.showChair(applyfrom.getId(), applyfrom.getChair()), false);
            }
        } else if (isTimeLeap()) { // Time Leap
            for (MapleCoolDownValueHolder i : applyto.getCooldowns()) {
                if (i.skillId != 5121010 && i.skillId != 61121054 && i.skillId != 61121053 && i.skillId != 61121052) {
                    applyto.removeCooldown(i.skillId);
                    applyto.getClient().sendPacket(CUserLocal.skillCooltimeSet(i.skillId, 0));
                }
            }
        } else if (isReleaseOverLoad()) {
            applyto.addHP((int) (applyto.getStat().getCurrentMaxHp() * (getX() / 100.0D)));
        } else if (isDiaboliqueRecovery()) {
            applyto.startDiaboliqueRecovery(this);
        }
        if (rewardMeso != 0) {
            applyto.gainMeso(rewardMeso, false);
        }
        if (rewardItem != null && totalprob > 0) {
            for (Triple<Integer, Integer, Integer> reward : rewardItem) {
                if (MapleInventoryManipulator.checkSpace(applyto.getClient(), reward.left, reward.mid, "") && reward.right > 0 && Randomizer.nextInt(totalprob) < reward.right) { // Total prob
                    if (GameConstants.getInventoryType(reward.left) == MapleInventoryType.EQUIP) {
                        final Item item = MapleItemInformationProvider.getInstance().getEquipById(reward.left);
                        //item.setGMLog("Reward item (effect): " + sourceid + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.addbyItem(applyto.getClient(), item);
                    } else {
                        MapleInventoryManipulator.addById(applyto.getClient(), reward.left, reward.mid.shortValue(), "Reward item (effect): " + sourceid + " on " + FileoutputUtil.CurrentReadable_Date());
                    }
                }
            }
        }
        //System.out.println("여기인거같은데? 10");
        return true;
    }

    public final boolean applyReturnScroll(final MapleCharacter applyto) {
        if (moveTo != -1) {
            if (applyto.getMap().getReturnMapId() != applyto.getMapId() || sourceid == 2031012 || sourceid == 2031010 || sourceid == 2030021 || sourceid == 20021110 || sourceid == 2030028 || sourceid == 20031203 || sourceid == 30021235) {
                MapleMap target;
                if (moveTo == 999999999) {
                    target = applyto.getMap().getReturnMap();
                } else if (sourceid == 2030028 && moveTo == 103020000) {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                } else if (sourceid == 20031203 && moveTo == 150000000) {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                } else if (sourceid == 30021235 && moveTo == 230050000) {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                } else if (sourceid == 2031012) {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                } else {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                    if (target.getId() / 10000000 != 60 && applyto.getMapId() / 10000000 != 61) {
                        if (target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20) {
                        }
                    }
                }
                int pNumber = 0;
                switch (target.getId()) {
                    case 211070000: {
                        pNumber = 1;
                        break;
                    }
                }
                applyto.changeMap(target, target.getPortal(pNumber));
                return true;
            }
        }
        return false;
    }

    private boolean isSoulStone() {
        return skill && sourceid == 22181003 || sourceid == 24111002;
    }

    private void applyBuff(final MapleCharacter applyfrom, int newDuration) {
        if (isSoulStone() && sourceid != 24111002) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                    if (!chr.isClone() && chr.getParty() != null && chr.getParty().getId() == applyfrom.getParty().getId() && chr.isAlive()) {
                        membrs++;
                    }
                }
                List<MapleCharacter> awarded = new ArrayList<>();
                while (awarded.size() < Math.min(membrs, info.get(MapleStatInfo.y))) {
                    for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                        if (chr != null && !chr.isClone() && chr.isAlive() && chr.getParty() != null && chr.getParty().getId() == applyfrom.getParty().getId() && !awarded.contains(chr) && Randomizer.nextInt(info.get(MapleStatInfo.y)) == 0) {
                            awarded.add(chr);
                        }
                    }
                }
                for (MapleCharacter chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    chr.getClient().getSession().write(EffectPacket.showOwnBuffEffect(sourceid, 2, applyfrom.getLevel(), level));
                    chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), sourceid, 2, applyfrom.getLevel(), level), false);
                }
            }
        } else if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff() || applyfrom.inPVP())) {
            final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
            final List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
            for (final MapleMapObject affectedmo : affecteds) {
                final MapleCharacter affected = (MapleCharacter) affectedmo;

                if (affected.getId() != applyfrom.getId() && (isGmBuff() || (applyfrom.inPVP() && affected.getTeam() == applyfrom.getTeam() && Integer.parseInt(applyfrom.getEventInstance().getProperty("type")) != 0) || (applyfrom.getParty() != null && affected.getParty() != null && applyfrom.getParty().getId() == affected.getParty().getId()))) {
                    if ((isResurrection() && !affected.isAlive()) || (!isResurrection() && affected.isAlive())) {

                        if ((sourceid == 32001003 || sourceid == 32120000) && affected.getBuffedValue(CharacterTemporaryStat.DarkAura) != null) {
                            continue;
                        }
                        if ((sourceid == 32110000 || sourceid == 32111012) && affected.getBuffedValue(CharacterTemporaryStat.BlueAura) != null) {
                            continue;
                        }
                        if ((sourceid == 32101003 || sourceid == 32120001) && affected.getBuffedValue(CharacterTemporaryStat.YellowAura) != null) {
                            continue;
                        }
                        applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().getSession().write(EffectPacket.showOwnBuffEffect(sourceid, 2, applyfrom.getLevel(), level));
                        affected.getMap().broadcastMessage(affected, EffectPacket.showBuffeffect(affected.getId(), sourceid, 2, applyfrom.getLevel(), level), false);

                    }
                    if (isTimeLeap()) {
                        for (MapleCoolDownValueHolder i : affected.getCooldowns()) {
                            if (i.skillId != 5121010 && i.skillId != 61121054 && i.skillId != 61121053 && i.skillId != 61121052) {
                                affected.removeCooldown(i.skillId);
                                affected.getClient().sendPacket(CUserLocal.skillCooltimeSet(i.skillId, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeMonsterBuff(final MapleCharacter applyfrom) {
        List<MonsterTemporaryStat> cancel = new ArrayList<>();
        switch (sourceid) {
            case 1111007:
            case 1211009:
            case 1311007:
            case 11111008:
            case 51111005:
                cancel.add(MonsterTemporaryStat.PGuardUp);
                cancel.add(MonsterTemporaryStat.MGuardUp);
                cancel.add(MonsterTemporaryStat.PowerUp);
                cancel.add(MonsterTemporaryStat.MagicUp);
                cancel.add(MonsterTemporaryStat.PCounter);
                cancel.add(MonsterTemporaryStat.MCounter);
                cancel.add(MonsterTemporaryStat.PImmune);
                cancel.add(MonsterTemporaryStat.MImmune);
                break;
            default:
                return;
        }
        final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (MonsterTemporaryStat stat : cancel) {
                    ((MapleMonster) mo).cancelStatus(stat);
                }
            }
            i++;
            if (i >= info.get(MapleStatInfo.mobCount)) {
                break;
            }
        }
    }

    public final void applyMonsterBuff(final MapleCharacter applyfrom) {
        final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        final boolean pvp = applyfrom.inPVP();
        final MapleMapObjectType objType = MapleMapObjectType.MONSTER;
        final List<MapleMapObject> affected = sourceid == 35111005 ? applyfrom.getMap().getMapObjectsInRange(applyfrom.getTruePosition(), Double.POSITIVE_INFINITY, Arrays.asList(objType)) : applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(objType));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (Map.Entry<MonsterTemporaryStat, Integer> stat : getMonsterStati().entrySet()) {
                    if (pvp) {
                        MapleCharacter chr = (MapleCharacter) mo;
                        MonsterSkill d = MonsterTemporaryStat.getAffectedCTS(stat.getKey());
                        if (d != null) {
                            chr.giveDebuff(d, stat.getValue(), getDuration(), d.getDisease(), 1, 0);
                        }
                    } else {
                        MapleMonster mons = (MapleMonster) mo;
                        if (sourceid == 35111005 && mons.getStats().isBoss()) {
                            break;
                        }
                        mons.applyStatus(applyfrom, new MonsterTemporaryStatEffect(stat.getKey(), stat.getValue(), sourceid, null, false), isPoison(), isSubTime(sourceid) ? getSubTime() : getDuration(), true, this);
                    }
                }
                if (pvp && skill) {
                    MapleCharacter chr = (MapleCharacter) mo;
                    handleExtraPVP(applyfrom, chr);
                }
            }
            i++;
            if (i >= info.get(MapleStatInfo.mobCount) && sourceid != 35111005) {
                break;
            }
        }
    }

    public final boolean isSubTime(final int source) {
        switch (source) {
            case 1201006: // threaten
            case 23111008: // spirits
            case 23111009:
            case 23111010:
            case 31101003:
            case 31121003:
            case 31121005:
                return true;//u there?
        }
        return false;
    }

    public final void handleExtraPVP(MapleCharacter applyfrom, MapleCharacter chr) {
        if (sourceid == 2311005 || sourceid == 5121005 || sourceid == 1201006 || (GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 104)) { //doom, threaten, snatch
            final long starttime = System.currentTimeMillis();

            final int localsourceid = sourceid == 5121005 ? 90002000 : sourceid;
            final Map<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
            if (sourceid == 2311005) {
                localstatups.put(CharacterTemporaryStat.Morph, 7);
            } else if (sourceid == 1201006) {
                localstatups.put(CharacterTemporaryStat.Thread, (int) level);
            } else if (sourceid == 5121005) {
                localstatups.put(CharacterTemporaryStat.Unk_0x400000_5, 1);
            } else {
                localstatups.put(CharacterTemporaryStat.Morph, info.get(MapleStatInfo.x));
            }
            chr.getClient().getSession().write(BuffPacket.giveBuff(localsourceid, getDuration(), localstatups, this));
            chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, localstatups), isSubTime(sourceid) ? getSubTime() : getDuration()), localstatups, false, getDuration(), applyfrom.getId());
        }
    }

    public final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft) {
        return calculateBoundingBox(posFrom, facingLeft, lt, rb, info.get(MapleStatInfo.range));
    }

    public final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft, int addedRange) {
        return calculateBoundingBox(posFrom, facingLeft, lt, rb, info.get(MapleStatInfo.range) + addedRange);
    }

    public static Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft, final Point lt, final Point rb, final int range) {
        if (lt == null || rb == null) {
            return new Rectangle((facingLeft ? (-200 - range) : 0) + posFrom.x, (-100 - range) + posFrom.y, 200 + range, 100 + range);
        }
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x - range, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x + range, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    public final double getMaxDistanceSq() { //lt = infront of you, rb = behind you; not gonna distanceSq the two points since this is in relative to player position which is (0,0) and not both directions, just one
        final int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        final int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        return (maxX * maxX) + (maxY * maxY);
    }

    public final void setDuration(int d) {
        this.info.put(MapleStatInfo.time, d);
    }

    public final void silentApplyBuff(final MapleCharacter chr, final long starttime, final int localDuration, final Map<CharacterTemporaryStat, Integer> statup, final int cid) {
        chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, statup),
                ((starttime + localDuration) - System.currentTimeMillis())), statup, true, localDuration, cid);

        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(chr, this, chr.getTruePosition(), summonMovementType);
            if (!tosummon.isPuppet()) {
                chr.getCheatTracker().resetSummonAttack();
                chr.getMap().spawnSummon(tosummon, true);
                chr.addSummon(tosummon);
                tosummon.addHP(info.get(MapleStatInfo.x).shortValue() * 10);
                if (isBeholder()) {
                    tosummon.addHP((short) 1);
                }
            }
        }
    }

    public final void applyComboBuff(final MapleCharacter applyto, short combo) {
        EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.ComboAbilityBuff, (int) combo);
        applyto.getClient().getSession().write(BuffPacket.giveBuff(sourceid, 99999, stat, this));
        final long starttime = System.currentTimeMillis();
        applyto.registerEffect(this, starttime, null, applyto.getId());
    }

    public final void applyEnergyBuff(final MapleCharacter applyto, final boolean infinity) {
        final long starttime = System.currentTimeMillis();
        if (infinity) {
            applyto.getClient().getSession().write(BuffPacket.giveEnergyChargeTest(0, info.get(MapleStatInfo.time) / 1000));
            applyto.registerEffect(this, starttime, null, applyto.getId());
        } else {
            final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
            stat.put(CharacterTemporaryStat.EnergyCharge, 10000);
            applyto.cancelEffect(this, true, -1, stat);
            applyto.getClient().getSession().write(BuffPacket.giveEnergyChargeTest(10000, info.get(MapleStatInfo.time) / 1000));
            applyto.getMap().broadcastMessage(applyto, BuffPacket.giveEnergyChargeTest(applyto.getId(), 10000, info.get(MapleStatInfo.time) / 1000), false);
            final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, stat);
            final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, ((starttime + info.get(MapleStatInfo.time)) - System.currentTimeMillis()));
            applyto.registerEffect(this, starttime, schedule, stat, false, info.get(MapleStatInfo.time), applyto.getId());

        }
    }

    public void applySunfireBuff(final MapleCharacter applyto, boolean used, int attackSkill) {
        applyto.dropMessage(5, "applySunfire");
        Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.Larkness, 1);
        final MapleStatEffect eff = SkillFactory.getSkill(20040216).getEffect(1);
        final long startTime = System.currentTimeMillis();
        if (used && (applyto.getBuffedValue(CharacterTemporaryStat.Larkness) != null && applyto.getBuffedValue(CharacterTemporaryStat.Larkness) == 20040216)) {
            if (GameConstants.isLightSkills(attackSkill)) {
                applyto.dropMessage(5, "applySunfire 2");
                applyto.getClient().sendPacket(CUserLocal.OnSunfireGauge(applyto.addMinusOfGlassMorph(-GameConstants.isLightSkillsGaugeCheck(attackSkill))));
            }
        } else {
            if (GameConstants.isDarkSkills(attackSkill)) {
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, eff), false);
                applyto.getClient().sendPacket(CUserLocal.OnSunfire(stat, 10000, 15000));
                applyto.getClient().sendPacket(CUserLocal.OnSunfireGauge(9999));
                applyto.registerEffect(this, startTime, null, applyto.getId());
                applyto.setBuffedValue(CharacterTemporaryStat.Larkness, 20040216, -1);
            }
        }
    }

    public void applyEclipseBuff(final MapleCharacter applyto, boolean used, int attackSkill) {
        applyto.dropMessage(5, "applyEclipse");
        Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.Larkness, 100);
        final MapleStatEffect eff = SkillFactory.getSkill(20040217).getEffect(1);
        final long startTime = System.currentTimeMillis();
        if (used && (applyto.getBuffedValue(CharacterTemporaryStat.Larkness) != null && applyto.getBuffedValue(CharacterTemporaryStat.Larkness) == 20040217)) {
            if (GameConstants.isDarkSkills(attackSkill)) {
                applyto.getClient().sendPacket(CUserLocal.OnEclipseGauge(applyto.addPlusOfGlassMorph(GameConstants.isDarkSkillsGaugeCheck(attackSkill))));
            }
        } else {
            if (GameConstants.isLightSkills(attackSkill)) {
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, eff), false);
                applyto.getClient().sendPacket(CUserLocal.OnEclipse(stat, -1, 15000));
                applyto.getClient().sendPacket(CUserLocal.OnEclipseGauge(1));
                applyto.registerEffect(this, startTime, null, applyto.getId());
                applyto.setBuffedValue(CharacterTemporaryStat.Larkness, 20040217, -1);
            }
        }
    }

    /*
    public void applyequilibriumBuff(final MapleCharacter applyto, boolean sunfire) {
        Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.Larkness, 101);
        final long startTime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, startTime, stat);
        ScheduledFuture schedule = BuffTimer.getInstance().schedule(cancelAction, startTime + 15000L - System.currentTimeMillis());
        applyto.registerEffect(this, startTime, schedule, applyto.getId());
        if (sunfire) {
            final MapleStatEffect eff = SkillFactory.getSkill(20040220).getEffect(1);
            applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, eff), false);
            //applyto.getClient().sendPacket(CUserLocal.giveEquilibriumBuff(20040220, 20040216, 20040217));
            applyto.getClient().sendPacket(CUserLocal.OnSunfireGauge(1));
            applyto.setBuffedValue(CharacterTemporaryStat.Larkness, -1, 20040219);
        } else {
            final MapleStatEffect eff = SkillFactory.getSkill(20040219).getEffect(1);
            applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, eff), false);
            //applyto.getClient().sendPacket(CUserLocal.giveEquilibriumBuff(20040219, 20040217, 20040216));
            applyto.getClient().sendPacket(CUserLocal.OnEclipseGauge(1));
            applyto.setBuffedValue(CharacterTemporaryStat.Larkness, -1, 20040218);
        }
    }
     */
    public void applyBuffEffect(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final int newDuration) {
        applyBuffEffect(applyfrom, applyto, primary, newDuration, false);
    }

    private void applyBuffEffect(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final int newDuration, final boolean light) {
        int localDuration = newDuration;
        if (primary) {
            localDuration = Math.max(newDuration, alchemistModifyVal(applyfrom, localDuration, false));
        }
        Map<CharacterTemporaryStat, Integer> localstatups = statups, maskedStatups = null;
        boolean normal = true, showEffect = primary;
        int maskedDuration = 0;
        switch (sourceid) {
            /*
            case 1311008: {
                int nDamR = (80 - (applyto.getStat().getHPPercent()));
                statups.clear();
                statups.put(CharacterTemporaryStat.DamR, nDamR);
                break;
            }
             */
            case 1321015: {
                int nHP = ((applyto.getMaxHp() / 100) * this.info.get(MapleStatInfo.y));
                applyto.addHP(nHP);
                break;
            }
            case 62101002:
            case 62120011: {
                applyto.getMap().broadcastMessage(CField.spawnHaku(applyto));
                int nSLV = applyto.getTotalSkillLevel(62120011);
                if (nSLV > 0) {
                    sourceid = 62120011;
                }
                applyto.setHakuBuff();
                break;
            }
            case 22161005: {
                this.info.put(MapleStatInfo.mobCount, 6);
                this.info.put(MapleStatInfo.mpCon, this.info.get(MapleStatInfo.y));
                this.info.put(MapleStatInfo.time, 2100000000);
                boolean added = false;
                if (statups.containsKey(CharacterTemporaryStat.TeleportMastery)) {
                    added = true;
                }
                if (!added) {
                    statups.put(CharacterTemporaryStat.TeleportMastery, this.info.get(MapleStatInfo.x));
                }
                this.monsterStatus.put(MonsterTemporaryStat.Stun, Integer.valueOf(1));
                break;
            }
            case 80001035: {
                statups.clear();
                localDuration = 0;
                int[] getKey = {713420, 713421, 713422};
                String[] getKeyVal = {"Pad", "Mad", "AllStat"};
                CharacterTemporaryStat[] buffStats = {CharacterTemporaryStat.IndiePad, CharacterTemporaryStat.IndieMad, CharacterTemporaryStat.IndieAllStat};
                for (int i = 0; i < getKey.length; i++) {
                    int value;
                    if ((value = (int) applyto.getKeyValue(getKey[i], "Indie" + getKeyVal[i])) > 0) {
                        statups.put(buffStats[i], value);
                    }
                }
                break;
            }
            case 1321007: {
                applyto.getClient().getSession().write(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                break;
            }
            case 33101006: {//jaguar oshi
                applyto.clearLinkMid();
                switch (Randomizer.nextInt(6)) {
                    case 0:
                        statups.clear();
                        statups.put(CharacterTemporaryStat.HowlingCritical, getY());
                        break;
                    case 1:
                        statups.clear();
                        statups.put(CharacterTemporaryStat.HowlingMaxMp, getX());
                        break;
                    case 2:
                        statups.clear();
                        statups.put(CharacterTemporaryStat.HowlingDefence, getX());
                        break;
                    case 3:
                        statups.clear();
                        statups.put(CharacterTemporaryStat.HowlingEva, getX());
                        break;
                    case 4:
                        statups.clear();
                        statups.put(CharacterTemporaryStat.HowlingAttackDamage, getX());
                        break;
                    case 5:
                        statups.clear();
                        statups.put(CharacterTemporaryStat.HowlingAttackDamage, getY());
                        break;
                }
                break;
            }
            case 2259171: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.HowlingCritical, 5);
                break;
            }
            case 2259173: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.HowlingCritical, 10);
                break;
            }
            case 2259175: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.AsrR, 20);
                localstatups.put(CharacterTemporaryStat.TerR, 20);
                break;
            }
            case 4201011: {
                int mesoMasteryBoost = applyto.getTotalSkillLevel(4210012);
                int mesoGuardRate = getX() - mesoMasteryBoost;
                statups.clear();
                statups.put(CharacterTemporaryStat.MesoGuard, mesoGuardRate);
                break;
            }
            case 4221013: {
                statups.clear();
                statups.put(CharacterTemporaryStat.IndiePad, info.get(MapleStatInfo.x) + (info.get(MapleStatInfo.kp) * applyfrom.currentBattleshipHP()));
                applyfrom.setBattleshipHP(0);
                //applyfrom.refreshBattleshipHP();
                break;
            }
            case 5311004: {
                int Oakid = Randomizer.rand(1, 4);
                applyto.getMap().broadcastMessage(applyto, CField.EffectPacket.showDiceEffect(applyto.getId(), sourceid, Oakid, -1, (int) level, (byte) 0), false);
                applyto.getClient().getSession().write(CField.EffectPacket.showOwnDiceEffect(sourceid, Oakid, -1, (int) level, (byte) 0));
                statups.clear();
                statups.put(CharacterTemporaryStat.Roulette, Oakid);
                break;
            }
            case 5211011:
            case 5211015:
            case 5211016: {
                applyfrom.cancelEffectFromBuffStat(CharacterTemporaryStat.Summon, 5211011);
                applyfrom.cancelEffectFromBuffStat(CharacterTemporaryStat.Summon, 5211015);
                applyfrom.cancelEffectFromBuffStat(CharacterTemporaryStat.Summon, 5211016);
                if (applyfrom.getTotalSkillLevel(5220019) > 0) {
                    localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                    localstatups.put(CharacterTemporaryStat.IndiePad, 3 * applyto.getTotalSkillLevel(5220019));
                    switch (sourceid) {
                        case 5211015:
                            localstatups.put(CharacterTemporaryStat.HowlingCritical, applyto.getTotalSkillLevel(5220019));
                            break;
                        case 5211016:
                            localstatups.put(CharacterTemporaryStat.EnhancedMaxHp, (int) (0.02d * applyto.getTotalSkillLevel(5220019) * applyto.getMaxHp()));
                            localstatups.put(CharacterTemporaryStat.EnhancedMaxMp, (int) (0.02d * applyto.getTotalSkillLevel(5220019) * applyto.getMaxMp()));
                            localstatups.put(CharacterTemporaryStat.Speed, 2 * applyto.getTotalSkillLevel(5220019));
                            break;
                    }
                    if (!localstatups.isEmpty()) {
                        applyto.getClient().getSession().write(BuffPacket.giveBuff(5220019, 120000, localstatups, null));
                    }
                }
                break;
            }
            /*
            case 35121010: {
                if (applyfrom.getParty() != null) {
                    for (final MaplePartyCharacter chr : applyfrom.getParty().getMembers()) {
                        final MapleCharacter curChar = applyfrom.getClient().getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
                        if (curChar != null && curChar.getMapId() == applyfrom.getMapId()) {
                            Skill piratesRevenge = SkillFactory.getSkill(sourceid);
                            long piratesRevengeDuration = piratesRevenge.getEffect(applyfrom.getSkillLevel(sourceid)).getDuration();
                            localstatups.put(CharacterTemporaryStat.DamR, info.get(MapleStatInfo.x));
                            curChar.getClient().getSession().write(BuffPacket.giveBuff(sourceid, (int) piratesRevengeDuration, localstatups, this));
                            final long starttime = System.currentTimeMillis();
                            final CancelEffectAction cancelAction = new CancelEffectAction(curChar, piratesRevenge.getEffect(applyfrom.getSkillLevel(sourceid)), starttime, localstatups);
                            final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, piratesRevengeDuration);
                            applyto.registerEffect(piratesRevenge.getEffect(applyfrom.getSkillLevel(sourceid)), starttime, schedule, applyfrom.getId());
                        }
                    }
                }
                break;
            }
             */
            case 5211009: { //cross cut blast
                int attacksToRemove = -applyto.getCData(applyto, sourceid); //remove all remaining counters
                applyto.setCData(sourceid, getAttacksRemaining() + attacksToRemove);
                break;
            }
            case 2311009: {
                int attacksToRemove = -applyto.getCData(applyto, sourceid);
                applyto.setCData(sourceid, getAttacksRemaining() + attacksToRemove);
                if (applyto.isHidden()) {
                    break;
                }
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), maskedStatups == null ? localstatups : maskedStatups, this), false);
                break;
            }
            case 5111007:
            case 5120012:
            case 5211007:
            case 5220014:
            case 35111013:
            case 35120014:
            case 15111011:
            case 5311005:
            case 5320007: {
                statups.clear();
                int diceid = 0;
                int doublediceid = 0;
                int dice1 = 0, dice2 = 0;
                if (isDoubledice(sourceid) && makeChanceResult()) {
                    dice1 = Randomizer.rand(1, 6);
                    dice2 = Randomizer.rand(1, 6);
                    doublediceid = (dice2 * 10) + dice1;
                } else {
                    diceid = Randomizer.rand(1, 6);
                }
                if (doublediceid > 0) {
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), sourceid, dice1, dice2, level, (byte) 0), false);
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), sourceid, dice2, dice2, level, (byte) 1), false);
                    applyto.getClient().getSession().write(EffectPacket.showOwnDiceEffect(sourceid, dice1, dice2, level, (byte) 0));
                    applyto.getClient().getSession().write(EffectPacket.showOwnDiceEffect(sourceid, dice2, dice2, level, (byte) 1));
                    if (dice1 == 1 && dice2 == 1) {
                        applyto.dropMessage(5, "더블 럭키 다이스 스킬이 실패하였습니다.");
                        return;
                    } else if (dice1 == 1) {
                        applyto.dropMessage(5, "더블 럭키 다이스 스킬이 [" + dice2 + "] 번 효과를 발동 시켰습니다.");
                    } else if (dice2 == 1) {
                        applyto.dropMessage(5, "더블 럭키 다이스 스킬이 [" + dice1 + "] 번 효과를 발동 시켰습니다.");
                    } else {
                        applyto.dropMessage(5, "더블 럭키 다이스 스킬이 [" + dice2 + "], [" + dice1 + "] 번 효과를 발동 시켰습니다.");
                    }
                    statups.put(CharacterTemporaryStat.Dice, doublediceid);
                    applyto.getClient().getSession().write(BuffPacket.giveDoubleDice(doublediceid, sourceid, localDuration, statups));
                } else {
                    int realeffect = realDice(sourceid);
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), realeffect, diceid, dice2 > 0 ? -1 : 0, level, (byte) 0), false);
                    applyto.getClient().getSession().write(EffectPacket.showOwnDiceEffect(realeffect, diceid, dice2 > 0 ? -1 : 0, level, (byte) 0));
                    if (diceid <= 1) {
                        applyto.dropMessage(5, "럭키 다이스 스킬이 실패하였습니다.");
                        return;
                    }
                    statups.put(CharacterTemporaryStat.Dice, diceid);
                    applyto.getClient().getSession().write(BuffPacket.giveDice(diceid, realeffect, localDuration, statups));
                    applyto.dropMessage(5, "럭키 다이스 스킬이 [" + diceid + "] 번 효과를 발동 시켰습니다.");
                }
                normal = false;
                showEffect = false;
                break;
            }
            case 5121054: {
                applyto.getClient().getSession().write(BuffPacket.giveEnergyChargeTest(10000, 120 * 1000));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveEnergyChargeTest(applyto.getId(), 11000, 120 * 1000), false);
                break;
            }
            case 20031209: {
                int skillid = (applyto.getSkillLevel(24120002) > 0 ? 24120002 : applyto.getSkillLevel(24100003) > 0 ? 24100003 : 0);
                int randomBuff = Randomizer.rand(1, 2);
                applyto.setCardStack((byte) 0);
                applyto.addRunningStack((skillid == 24100003) ? 5 : 10);
                applyto.getMap().broadcastMessage(CField.createForceAtom(false, skillid, applyto.getId(), 1, 1, skillid == 24120002 ? 2 : 1, Randomizer.rand(35, 50), Randomizer.rand(5, 7), 0));
                applyto.getMap().broadcastMessage(applyto, CField.EffectPacket.showDiceEffect(applyto.getId(), this.sourceid, randomBuff, -1, this.level, (byte) 0), false);
                applyto.getClient().sendPacket(CField.EffectPacket.showOwnDiceEffect(this.sourceid, randomBuff, -1, this.level, (byte) 0));
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.Judgement, randomBuff);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                showEffect = false;
                break;
            }
            case 20031210: {
                int skillid2 = 0;
                if (applyto.getSkillLevel(24120002) > 0) {
                    skillid2 = 24120002;
                } else if (applyto.getSkillLevel(24100003) > 0) {
                    skillid2 = 24100003;
                }
                int rand3 = Randomizer.rand(1, 5);
                applyto.setCardStack((byte) 0);
                applyto.addRunningStack((skillid2 == 24100003) ? 5 : 10);
                applyto.getMap().broadcastMessage(CField.createForceAtom(false, skillid2, applyto.getId(), 1, 1, skillid2 == 24120002 ? 2 : 1, Randomizer.rand(35, 50), Randomizer.rand(5, 7), 0));
                applyto.getMap().broadcastMessage(applyto, CField.EffectPacket.showDiceEffect(applyto.getId(), this.sourceid, rand3, -1, this.level, (byte) 0), false);
                applyto.getClient().sendPacket(CField.EffectPacket.showOwnDiceEffect(this.sourceid, rand3, -1, this.level, (byte) 0));
                //statups.clear();
                //if (rand3 == 5) {
                //localstatups.put(MapleBuffStat.ABSORB_DAMAGE_HP, 3);
                //}
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.Judgement, rand3);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                showEffect = false;
                break;
            }
            case 8006:
            case 10008006:
            case 20008006:
            case 20018006:
            case 20028006:
            case 30008006:
            case 30018006: {
                int boost = applyto.getBuffedValue(CharacterTemporaryStat.Booster) == null ? 0 : applyto.getBuffedValue(CharacterTemporaryStat.Booster);
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                int speedBonus = 0;
                if (boost < 0) {
                    localstatups.put(CharacterTemporaryStat.Booster, -3 + speedBonus);
                } else {
                    localstatups.put(CharacterTemporaryStat.Booster, -1 + speedBonus);
                }
                localstatups.put(CharacterTemporaryStat.KeyDownTimeIgnore, 1);
                break;
            }
            case 5121009:
            case 15121005: {
                if (applyto.isHidden()) {
                    return;
                }
                if (applyto.getBuffedValue(CharacterTemporaryStat.SaintSaver) != null) {
                    if (applyto.getBuffSource(CharacterTemporaryStat.SaintSaver) == 13111023 || applyto.getBuffSource(CharacterTemporaryStat.SaintSaver) == 13120008) {
                        return;
                    }
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.IndieBooster, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 5001005:
            case 15001003: {
                applyto.getClient().getSession().write(BuffPacket.givePirate(statups, localDuration / 1000, sourceid));
                if (!applyto.isHidden()) {
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignPirate(statups, localDuration / 1000, applyto.getId(), sourceid), false);
                }
                normal = false;
                break;
            }
            case 5221015: // Homing Beacon
            case 22151002: {// Bullseye
                if (applyto.getFirstLinkMid() > 0) {
                    applyto.getClient().getSession().write(BuffPacket.cancelHoming());
                    applyto.getClient().getSession().write(BuffPacket.giveHoming(sourceid, applyto.getFirstLinkMid(), 1));
                } else {
                    return;
                }
                normal = false;
                break;
            }
            /*
            case 2120010:
            case 2220010:
            case 2320011: {
                applyto.dropMessage(6, "?");
                int iDamR = (getX() * applyto.getArcaneAim());
                //applyto.getClient().sendPacket(BuffPacket.giveBuff(iDamR, sourceid, localDuration));
                normal = false;
                break;
            }
             */
            case 30011001:
            case 30001001: { // Wind Walk
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Infltrate, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 13101006: { // Wind Walk
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.WindWalk, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 4001003:
            case 4330001:
            case 20031211:
            case 14001003: {
                if (applyto.isHidden()) {
                    return;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.DarkSight, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 52001001: { // 치유
                if (applyfrom.getTotalSkillLevel(52120001) > 0) {
                    SkillFactory.getSkill(52120001).getEffect(applyfrom.getTotalSkillLevel(52120001)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
                break;
            }
            case 52120001: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.MagicGuard, 1);
                localstatups.put(CharacterTemporaryStat.EnhancedPad, info.get(MapleStatInfo.x));
                localstatups.put(CharacterTemporaryStat.EnhancedMad, info.get(MapleStatInfo.x));
                break;
            }
            case 11121005: // 솔루나 타임
            case 12111023: // 플레임 배리어
            case 13111023: // 알바트로스
            case 13120008: // 알바트로스 맥시멈
            case 21111016: // 아드레날린 부스트
            case 21120003: // 아드레날린 부스트 맥시멈
            //case 52001001: // 치유
            case 52111008: // 정화
            case 61001001: // 발도술
            case 65121009: // 호신강기

            case 8001060: // 시그너스
            case 8001061: // 발록
            case 8001062: // 반 레온
            case 8001063: // 핑크빈
            case 8001064: // 아카이럼
            case 8001065: // 피아누스
            case 8001066: // 힐라
            case 8001067: // 자쿰
            case 8001068: // 블랙 슬라임
            case 8001069: // 매그너스
            case 8001070: // 무르무르
            case 8001076: // 피에르
            case 8001077: // 반반
            case 8001078: // 블러디 퀸
            case 8001079: // 벨룸
            case 8001680: // 스우
            case 8001681: // 스우
            case 8001682: // 교도관 아니
            case 8001683: // 드래곤라이더
            case 8001684: // 락 스피릿
            case 8001685: // 무공
            case 8001686: // 데미안
            case 8001687: // 데미안
            case 8001688: // 루시드
            case 8001689: // 루시드
            case 8001690: // 파풀라투스
            case 8001691: // 윌
            case 8001692: // 윌
            case 8001693: // 진 힐라
            case 8001694: // 진 힐라
            case 8001695: // 듄켈
            case 8001696: // 듄켈

            case 80001034: {
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.SaintSaver, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 23111005:
            case 51121054: {
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.DamAbsorbShield, info.get(MapleStatInfo.x));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 23101003: {
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.SpiritInfusion, info.get(MapleStatInfo.x));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32121003: {
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Cyclone, info.get(MapleStatInfo.x));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32111005: {
                statups.clear();//다지웡              
                if (applyfrom.getBuffedValue(CharacterTemporaryStat.DarkAura) != null
                        || applyfrom.getBuffedValue(CharacterTemporaryStat.YellowAura) != null
                        || applyfrom.getBuffedValue(CharacterTemporaryStat.BlueAura) != null) {
                    statups.put(CharacterTemporaryStat.SuperBody, (int) level);
                    if (applyfrom.getBuffedValue(CharacterTemporaryStat.BlueAura) != null
                            && applyfrom.getBuffedValue(CharacterTemporaryStat.DarkAura) == null
                            && applyfrom.getBuffedValue(CharacterTemporaryStat.YellowAura) == null) {
                        localDuration = 10000;
                        SkillFactory.getSkill(32110009).getEffect(1).applyBuffEffect(applyfrom, applyto, primary, 10000);
                    } else {
                        if (applyfrom.getBuffedValue(CharacterTemporaryStat.DarkAura) != null) {
                            statups.put(CharacterTemporaryStat.DamR, (int) (level + 10 + applyto.getTotalSkillLevel(32001003)));
                        }
                        if (applyfrom.getBuffedValue(CharacterTemporaryStat.BlueAura) != null) {
                            SkillFactory.getSkill(32110009).getEffect(1).applyBuffEffect(applyfrom, applyto, primary, 10000);
                        }
                        if (applyfrom.getBuffedValue(CharacterTemporaryStat.YellowAura) != null) {
                            statups.put(CharacterTemporaryStat.IndieBooster, -2);
                        }
                    }
                }
                break;
            }
            case 32001003: {//dark aura
                if (applyfrom.getTotalSkillLevel(32120000) > 0) {
                    SkillFactory.getSkill(32120000).getEffect(applyfrom.getTotalSkillLevel(32120000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32120000: { // adv dark aura
                statups.clear();
                if (applyto.getBuffedValue(CharacterTemporaryStat.DarkAura) != null) {
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.DarkAura);
                    normal = false;
                    break;
                }
                statups.put(CharacterTemporaryStat.DarkAura, info.get(MapleStatInfo.x));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), statups, this), false);
                break;
            }
            case 32121054: {
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Aura, 1);
                stat.put(CharacterTemporaryStat.DarkAura, 1);
                stat.put(CharacterTemporaryStat.BlueAura, 1);
                stat.put(CharacterTemporaryStat.YellowAura, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 27100003: {
                statups.clear();
                statups.put(CharacterTemporaryStat.BlessOfDarkness, (int) applyto.currentFTC());
                if (applyto.isHidden()) {
                    break;
                }
                applyto.getMap().broadcastMessage(BuffPacket.giveForeignBuff(applyto.getId(), statups, this));
                localDuration = 210000000;
                showEffect = false;
                break;
            }
            case 32111012: { // blue aura
                if (applyfrom.getTotalSkillLevel(32110000) > 0) {
                    SkillFactory.getSkill(32110000).getEffect(applyfrom.getTotalSkillLevel(32110000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
                break;
            }
            case 32110000: { // advanced blue aura
                statups.clear();
                if (applyto.getBuffedValue(CharacterTemporaryStat.BlueAura) != null) {
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.BlueAura);
                    normal = false;
                    break;
                }
                statups.put(CharacterTemporaryStat.BlueAura, 20);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), statups, this), false);
                break;
            }
            case 32101003: { // yellow aura
                if (applyfrom.getTotalSkillLevel(32120001) > 0) {
                    SkillFactory.getSkill(32120001).getEffect(applyfrom.getTotalSkillLevel(32120001)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32120001: {
                statups.clear();
                if (applyto.getBuffedValue(CharacterTemporaryStat.YellowAura) != null) {
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.YellowAura);
                    normal = false;
                    break;
                }
                statups.put(CharacterTemporaryStat.YellowAura, (int) level);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), statups, this), false);
                break;
            }
            case 1211004:
            case 1211006:
            case 1221004:
            case 21101006:
            case 21111005:
            case 15101006: { // Soul Arrow
                if (applyto.isHidden()) {
                    break;
                }
                if (applyto.getJob() == 121 || applyto.getJob() == 122) {
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211004);//라이트닝 캔슬..
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211006);//라이트닝 캔슬..
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1221004);//라이트닝 캔슬..
                    if (applyto.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null && applyto.getBuffedValue(CharacterTemporaryStat.AssistCharge) == null) { //라이트닝차지가 기존 차지버프로 걸려있는데 다른차지를쓸경우
                        applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211008);//라이트닝 캔슬..
                        SkillFactory.getSkill(1211008).getEffect(applyto.getSkillLevel(1211008)).applyBuffEffect(applyto, applyto, primary, localDuration, true);
                    }
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.WeaponCharge, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1211008: { //lightning              
                if ((applyto.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null && applyto.getBuffedValue(CharacterTemporaryStat.AssistCharge) == null) || light) {
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211008);
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211004);
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211006);
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1221004);
                    statups.clear();//원래있던 wk_charge 클리어시켜줍시당

                    statups.put(CharacterTemporaryStat.AssistCharge, 1);
                } else {
                    statups.clear();
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.AssistCharge, 1211008);
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211004);
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1211006);
                    applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge, 1221004);
                    statups.put(CharacterTemporaryStat.WeaponCharge, 1);
                    if (!applyto.isHidden()) {
                        Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                        stat.put(CharacterTemporaryStat.WeaponCharge, 1);
                        applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                    }
                }
                applyto.getClient().getSession().write(BuffPacket.giveBuff(sourceid, localDuration, statups, this));
                normal = false;
                break;
            }
            case 35111004:
            case 35121013: {
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Mechanic, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 35001001: //flame
            case 35100008:
            case 35101009:
            case 35101010:
            case 35121005: { //missile
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Mechanic, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1220013: {
                int attacksToRemove = -applyto.getCData(applyto, sourceid);
                applyto.setCData(sourceid, getAttacksRemaining() + attacksToRemove);
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.BlessingArmor, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1111002:
            case 11111001: { // Combo
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.ComboCounter, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 3101004:
            case 3201004:
            case 13101003: { // Soul Arrow
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.SoulArrow, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 2321005: { //holy shield
                if (applyto.getBuffedValue(CharacterTemporaryStat.SaintSaver) != null) {
                    if (applyto.getBuffSource(CharacterTemporaryStat.SaintSaver) == 13111023 || applyto.getBuffSource(CharacterTemporaryStat.SaintSaver) == 13120008) {
                        return;
                    }
                }
                applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.Bless);
                break;
            }
            case 4211008:
            case 4331002:
            case 4111002:
            case 36111006: //버추얼 프로젝션                
            case 14111000: { // Shadow Partner
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.ShadowPartner, info.get(MapleStatInfo.x));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 36111003: {
                statups.clear();
                statups.put(CharacterTemporaryStat.StackBuff, info.get(MapleStatInfo.prop) - (info.get(MapleStatInfo.x) * applyto.currentBattleshipHP()));
                statups.put(CharacterTemporaryStat.DamAbsorbShield, info.get(MapleStatInfo.z) + (info.get(MapleStatInfo.x) * applyto.currentBattleshipHP()));
                if (applyto.currentBattleshipHP() > 0) {
                    showEffect = false;
                }
                break;
            }
            /*
            case 15111006: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.Spark, info.get(MapleStatInfo.x));
                applyto.getClient().getSession().write(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                //normal = false;
                break;
            }
            /*
            case 4341002: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.FinalCut, info.get(MapleStatInfo.y));
                applyto.getClient().getSession().write(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
             */
            case 3120006:
            case 3220005: {
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.SpiritInfusion, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), true);
                break;
            }
            case 31121005: {
                if (applyto.isHidden()) {
                    break;
                }
                Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                if (applyto.getBuffedValue(CharacterTemporaryStat.DevilishPower) == null) {
                    stat.put(CharacterTemporaryStat.DevilishPower, 6);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else {
                    showEffect = false;
                }
                break;
            }
            case 2121004:
            case 2221004:
            case 2321004: { //Infinity
                maskedDuration = alchemistModifyVal(applyfrom, 4000, false);
                break;
            }
            case 4331003: { // Owl Spirit
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.DrawBack, info.get(MapleStatInfo.y));
                applyto.getClient().getSession().write(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                applyto.setBattleshipHP(info.get(MapleStatInfo.x)); //a variable that wouldnt' be used by a db
                normal = false;
                break;
            }
            case 1121010: { // Enrage
                applyto.handleOrbconsume(10);
                break;
            }
            case 2022746:
            case 2022764:
            case 2022747:
            case 2022823: {
                if (applyto.isHidden()) {
                    break;
                }
                applyto.getMap().broadcastMessage(BuffPacket.giveForeignBuffRing(applyto.getId(), maskedStatups == null ? localstatups : maskedStatups, this));
                break;
            }
            case 4330009: {
                localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                localstatups.put(CharacterTemporaryStat.IndiePad, (int) (5 + Math.floor(applyto.getTotalSkillLevel(4330009) / 2)));
                break;
            }
            case 1221054:
            case 30021237: {
                if (applyto.isHidden()) {
                    break;
                }
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), maskedStatups == null ? localstatups : maskedStatups, this), false);
                break;
            }
            case 2121054:
            case 2221054:
            case 4121054:
            case 4341052:
            case 4341054:
            case 20031205:
            case 154001003: {
                if (applyto.isHidden()) {
                    break;
                }
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), maskedStatups == null ? localstatups : maskedStatups, this), false);
                break;
            }
            case 35001002: {
                if (applyfrom.getTotalSkillLevel(35120000) > 0) {
                    SkillFactory.getSkill(35120000).getEffect(applyfrom.getTotalSkillLevel(35120000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            default:
                if (isPirateMorph()) {
                    final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<CharacterTemporaryStat, Integer>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.Morph, getMorph(applyto));
                    localstatups.put(CharacterTemporaryStat.Morph, getMorph(applyto));
                    if (sourceid == 13111005) {
                        localstatups.put(CharacterTemporaryStat.Morph, 4 * level);
                        localstatups.put(CharacterTemporaryStat.EnhancedPad, (int) info.get(MapleStatInfo.epad));
                        localstatups.put(CharacterTemporaryStat.EnhancedPdd, (int) info.get(MapleStatInfo.epdd));
                        localstatups.put(CharacterTemporaryStat.EnhancedMdd, getMorph(applyto));
                        localstatups.put(CharacterTemporaryStat.Speed, (int) info.get(MapleStatInfo.speed));
                        localstatups.put(CharacterTemporaryStat.Jump, (int) info.get(MapleStatInfo.jump));
                    }
                } else if (isMorph()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    if (isIceKnight()) {
                        //odd
                        Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                        stat.put(CharacterTemporaryStat.Team, 2);
                        applyto.getClient().getSession().write(BuffPacket.giveBuff(0, localDuration, stat, this));
                    }
                    Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.Morph, getMorph(applyto));
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isInflation()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.GiantPotion, (int) inflation);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isMonsterRiding()) {
                    localDuration = 2100000000;
                    localstatups = new EnumMap<>(statups);
                    localstatups.put(CharacterTemporaryStat.MonsterRiding, 1);
                    final int mountid = parseMountInfo(applyto, sourceid);
                    final int mountid2 = parseMountInfo_Pure(applyto, sourceid);
                    if (mountid != 0 && mountid2 != 0) {
                        final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                        stat.put(CharacterTemporaryStat.MonsterRiding, 0);
                        applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.PowerGuard);
                        applyto.cancelEffectFromBuffStat(CharacterTemporaryStat.ManaReflection);
                        if (sourceid == 35001002 || sourceid == 35120000) {
                            final EnumMap<CharacterTemporaryStat, Integer> stat1 = new EnumMap<CharacterTemporaryStat, Integer>(CharacterTemporaryStat.class);
                            stat1.clear();
                            stat1.put(CharacterTemporaryStat.EnhancedMaxHp, info.get(MapleStatInfo.emhp));
                            stat1.put(CharacterTemporaryStat.EnhancedMaxMp, info.get(MapleStatInfo.emmp));
                            stat1.put(CharacterTemporaryStat.EnhancedPad, info.get(MapleStatInfo.epad));
                            stat1.put(CharacterTemporaryStat.EnhancedMad, info.get(MapleStatInfo.epad));
                            stat1.put(CharacterTemporaryStat.EnhancedPdd, info.get(MapleStatInfo.epdd));
                            stat1.put(CharacterTemporaryStat.EnhancedMdd, info.get(MapleStatInfo.emdd));
                            stat1.put(CharacterTemporaryStat.IndieSpeed, info.get(MapleStatInfo.indieSpeed));
                            applyto.getClient().getSession().write(BuffPacket.giveBuff(0, localDuration, stat1, this));
                        }
                        applyto.getClient().getSession().write(BuffPacket.giveMount(mountid2, sourceid, stat));
                        applyto.getMap().broadcastMessage(applyto, BuffPacket.showMonsterRiding(applyto.getId(), stat, mountid, sourceid), false);
                    } else {
                        return;
                    }
                    normal = false;
                } else if (isSoaring()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.Flying, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (berserk > 0) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.RepeatEffect, 0);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isBerserkFury() || berserk2 > 0) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.DojangBerserk, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isDivineBody()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                    stat.put(CharacterTemporaryStat.DojangInvincible, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                }
                if (localstatups.containsKey(CharacterTemporaryStat.Booster)) {
                    int speedIncrease = -2;
                    int sourceID = applyto.getBuffSource(CharacterTemporaryStat.Booster);
                    if (sourceID % 10000 == 8006) { //dSI is active
                        speedIncrease = -3;
                    } else if (sourceID == 15111005 || sourceID == 5121009) {
                        speedIncrease = -4;
                    }
                    localstatups.remove(CharacterTemporaryStat.Booster);
                    localstatups.put(CharacterTemporaryStat.Booster, speedIncrease);
                }
                break;
        }
        if (applyto.getMapId() != 910000000 && applyto.getMapId() != 160020000) {
            if (showEffect && !applyto.isHidden()) {
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffeffect(applyto.getId(), sourceid, 1, applyto.getLevel(), level), false);
            }
            if (isMechPassive()) {
                applyto.getClient().getSession().write(EffectPacket.showOwnBuffEffect(sourceid - 1000, 1, applyto.getLevel(), level, (byte) 1));
            }
        }
        if (!isMonsterRiding() && !isMechDoor()) {
            switch (getSourceId()) {
                case 1301007:
                case 9001008:
                    applyto.cancelEffect(this, -1);
                    break;
                case 35121013:
                    break;
                default:
                    applyto.cancelEffect(this, -1);
                    break;
            }
            //applyto.cancelEffect(this, false, -1);
        }
        if (normal && localstatups.size() > 0) {
            applyto.getClient().sendPacket(BuffPacket.giveBuff((skill ? sourceid : -sourceid), localDuration, maskedStatups == null ? localstatups : maskedStatups, this));
        }
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, localstatups);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, maskedDuration > 0 ? maskedDuration : localDuration);
        applyto.registerEffect(this, starttime, schedule, localstatups, false, localDuration, applyfrom.getId());

    }

    public static int parseMountInfo(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 80001000:
            case 1004: // Monster riding
            case 11004: // Monster riding
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -119) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118).getItemId();
                }
                return parseMountInfo_Pure(player, skillid);
            default:
                return GameConstants.getMountItem(skillid, player);
        }
    }

    public static int parseMountInfo_Pure(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 80001000:
            case 1004: // Monster riding
            case 11004: // Monster riding
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18).getItemId();
                }
                return 0;
            default:
                return GameConstants.getMountItem(skillid, player);
        }
    }

    private int calcHPChange(final MapleCharacter applyfrom, final boolean primary) {
        int hpchange = 0;
        if (info.get(MapleStatInfo.hp) != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, info.get(MapleStatInfo.hp), true);
                } else {
                    hpchange += info.get(MapleStatInfo.hp);
                }
                if (applyfrom.hasDisease(MonsterSkill.Undead)) {
                    hpchange /= 2;
                }
            } else { // assumption: this is heal
                hpchange += makeHealHP(info.get(MapleStatInfo.hp) / 100.0, applyfrom.getStat().getTotalMagic(), 3, 5);
                if (applyfrom.hasDisease(MonsterSkill.Undead)) {
                    hpchange = -hpchange;
                }
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * hpR) / (applyfrom.hasDisease(MonsterSkill.Undead) ? 2 : 1);
        }
        // actually receivers probably never get any hp when it's not heal but whatever
        if (primary) {
            if (info.get(MapleStatInfo.hpCon) != 0) {
                hpchange -= info.get(MapleStatInfo.hpCon);
            }
        }
        switch (this.sourceid) {
            case 4211001: // Chakra
                final PlayerStats stat = applyfrom.getStat();
                int v42 = getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
        }
        applyfrom.checkLifeTidal();
        return hpchange;
    }

    private static int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private int calcMPChange(final MapleCharacter applyfrom, final boolean primary) {
        int mpchange = 0;
        if (info.get(MapleStatInfo.mp) != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, info.get(MapleStatInfo.mp), false); // recovery up doesn't apply for mp
            } else {
                mpchange += info.get(MapleStatInfo.mp);
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp(applyfrom.getJob()) * mpR);
        }
        if (GameConstants.isDemon(applyfrom.getJob())) {
            mpchange = 0;
        }
        if (primary) {
            if (info.get(MapleStatInfo.mpCon) != 0 && !GameConstants.isDemon(applyfrom.getJob())) {
                boolean free = false;
                if (applyfrom.getJob() == 411 || applyfrom.getJob() == 412) {
                    final Skill expert = SkillFactory.getSkill(4110012);
                    if (applyfrom.getTotalSkillLevel(expert) > 0) {
                        final MapleStatEffect eff = expert.getEffect(applyfrom.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            free = true;
                        }
                    }
                }
                if (applyfrom.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                    mpchange = 0;
                } else if (applyfrom.getBuffedValue(CharacterTemporaryStat.EnhancedPad) != null && applyfrom.getBuffSource(CharacterTemporaryStat.EnhancedPad) == 33111009) {
                    Skill bx = SkillFactory.getSkill(33111009);
                    int dec = bx.getEffect(applyfrom.getTotalSkillLevel(bx)).getX();
                    mpchange -= ((info.get(MapleStatInfo.mpCon)) - ((double) (info.get(MapleStatInfo.mpCon) * (dec) / 100.0D)));
                } else if (!free) {
                    mpchange -= (info.get(MapleStatInfo.mpCon));
                    /*-(info.get(MapleStatInfo.mpCon) * applyfrom.getStat().mpconReduce / 100)) * (applyfrom.getStat().mpconPercent / 100.0)*/
                }
            } else if (info.get(MapleStatInfo.forceCon) != 0 && GameConstants.isDemon(applyfrom.getJob())) {
                if (applyfrom.getBuffedValue(CharacterTemporaryStat.InfinityForce) != null || (sourceid == 31121005 && applyfrom.getBuffedValue(CharacterTemporaryStat.DevilishPower) != null)) {
                    mpchange = 0;
                } else {
                    mpchange -= info.get(MapleStatInfo.forceCon);
                }
            }
        }
        applyfrom.checkLifeTidal();
        return mpchange;
    }

    public final int alchemistModifyVal(final MapleCharacter chr, final int val, final boolean withX) {
        if (!skill) { // RecoveryUP only used for hp items and skills
            return (val * (100 + (withX ? chr.getStat().RecoveryUP : chr.getStat().BuffUP)) / 100);
        }
        return (val * (100 + (withX ? chr.getStat().RecoveryUP : (chr.getStat().BuffUP_Skill + (getSummonMovementType() == null ? 0 : chr.getStat().BuffUP_Summon)))) / 100);
    }

    public final void setSourceId(final int newid) {
        sourceid = newid;
    }

    public final boolean isGmBuff() {
        switch (sourceid) {
            case 10001075: //Empress Prayer
            case 9001000: // GM dispel
            case 9001001: // GM haste
            case 9001002: // GM Holy Symbol
            case 9001003: // GM Bless
            case 9001005: // GM resurrection
            case 9001008: // GM Hyper body

            case 9101000:
            case 9101001:
            case 9101002:
            case 9101003:
            case 9101005:
            case 9101008:
                return true;
            default:
                return GameConstants.isBeginnerJob(sourceid / 10000) && (sourceid % 10000 == 1005 || sourceid == 50001215);
        }
    }

    public final boolean isInflation() {
        return inflation > 0;
    }

    public final int getInflation() {
        return inflation;
    }

    public final boolean isEnergyCharge() {
        return skill && (sourceid == 5110001 || sourceid == 15100004);
    }

    public boolean isMonsterBuff() {
        switch (sourceid) {
            case 31221003://블러디임프리즌
            case 1201006: // threaten
            case 2101003: // fp slow
            case 2201003: // il slow
            case 5011002:
            case 12101001: // cygnus slow
            case 2211004: // il seal
            case 2111004: // fp seal
            case 12111002: // cygnus seal
            case 2311005: // doom
            case 4111003: // shadow web
            case 14111001: // cygnus web
            case 4121004: // Ninja ambush
            case 4221004: // Ninja ambush
            case 22151001:
            case 22121000:
            case 22161002:
            case 4321002:
            case 4341003:
            case 90001002:
            case 90001003:
            case 90001004:
            case 90001005:
            case 90001006:
            case 1111007:
            case 1211009:
            case 1311007:
            case 35111005:
            case 32120000:
            case 32120001:
            case 31221002://아머브레이크
            case 11111008:
            case 51111005: // Mihile || Magic Crash                
                return skill;
        }
        return false;
    }

    public final void setPartyBuff(boolean pb) {
        this.partyBuff = pb;
    }

    private boolean isPartyBuff() {
        if (lt == null || rb == null || !partyBuff) {
            return isSoulStone() && sourceid != 24111002;
        }
        switch (sourceid) {
            case 1211003:
            case 1211004:
            case 1211005:
            case 1211006:
            case 1211007:
            case 1211008:
            case 1221003:
            case 1221004:
            case 12101005:
            case 4311001:
            case 4331003:
            case 4341002:
            case 32101004:
            case 60001002:
            case 61121002:
            case 60011023:
                return false;
        }
        if (GameConstants.isNoDelaySkill(sourceid)) {
            return false;
        }
        return true;
    }

    public final boolean isArcane() {
        return skill && (sourceid == 2320011 || sourceid == 2220010 || sourceid == 2120010);
    }

    public final boolean isHeal() {
        return skill && (sourceid == 2301002 || sourceid == 9101000 || sourceid == 9001000);
    }

    public final boolean isResurrection() {
        return skill && (sourceid == 9001005 || sourceid == 9101005 || sourceid == 2321006);
    }

    public final boolean isTimeLeap() {
        return skill && (sourceid == 5121010 || sourceid == 61121054);
    }

    public final boolean isReleaseOverLoad() {
        return skill && sourceid == 31011001;
    }

    public final boolean isDiaboliqueRecovery() {
        return skill && sourceid == 31211004;
    }

    public final int getHp() {
        return info.get(MapleStatInfo.hp);
    }

    public final int getMp() {
        return info.get(MapleStatInfo.mp);
    }

    public final int getDOTStack() {
        return info.get(MapleStatInfo.dotSuperpos);
    }

    public final double getHpR() {
        return hpR;
    }

    public final double getMpR() {
        return mpR;
    }

    public final int getMastery() {
        return info.get(MapleStatInfo.mastery);
    }

    public final int getWatk() {
        return info.get(MapleStatInfo.pad);
    }

    public final int getMatk() {
        return info.get(MapleStatInfo.mad);
    }

    public final int getWdef() {
        return info.get(MapleStatInfo.pdd);
    }

    public final int getMdef() {
        return info.get(MapleStatInfo.mdd);
    }

    public final int getAcc() {
        return info.get(MapleStatInfo.acc);
    }

    public final int getAvoid() {
        return info.get(MapleStatInfo.eva);
    }

    public final int getSpeed() {
        return info.get(MapleStatInfo.speed);
    }

    public final int getJump() {
        return info.get(MapleStatInfo.jump);
    }

    public final int getSpeedMax() {
        return info.get(MapleStatInfo.speedMax);
    }

    public final int getPassiveSpeed() {
        return info.get(MapleStatInfo.psdSpeed);
    }

    public final int getPassiveJump() {
        return info.get(MapleStatInfo.psdJump);
    }

    public final int getDuration() {
        return info.get(MapleStatInfo.time);
    }

    public final int getSubTime() {
        return info.get(MapleStatInfo.subTime);
    }

    public final boolean isOverTime() {
        return overTime;
    }

    public final Map<CharacterTemporaryStat, Integer> getStatups() {
        return statups;
    }

    public final boolean sameSource(final MapleStatEffect effect) {
        boolean sameSrc = this.sourceid == effect.sourceid;
        switch (this.sourceid) { // All these are passive skills, will have to cast the normal ones.
            case 32120000: // Advanced Dark Aura
                sameSrc = effect.sourceid == 32120000;
                break;
            case 32110000: // Advanced Blue Aura
                sameSrc = effect.sourceid == 32110000;
                break;
            case 32120001: // Advanced Yellow Aura
                sameSrc = effect.sourceid == 32120001;
                break;
            case 35120000: // Extreme Mech
                sameSrc = effect.sourceid == 35120000;
                break;
            //case 35121013: // Mech: Siege Mode
            //sameSrc = effect.sourceid == 35121013;
            //break;
        }
        return effect != null && sameSrc && this.skill == effect.skill;
    }

    public final int getCr() {
        return info.get(MapleStatInfo.cr);
    }

    public final int getT() {
        return info.get(MapleStatInfo.t);
    }

    public final int getU() {
        return info.get(MapleStatInfo.u);
    }

    public final int getV() {
        return info.get(MapleStatInfo.v);
    }

    public final int getW() {
        return info.get(MapleStatInfo.w);
    }

    public final int getX() {
        return info.get(MapleStatInfo.x);
    }

    public final int getY() {
        return info.get(MapleStatInfo.y);
    }

    public final int getZ() {
        return info.get(MapleStatInfo.z);
    }

    public final Point getLt() {
        return this.lt;
    }

    public final Point getRb() {
        return this.rb;
    }

    public final int getDamage() {
        return info.get(MapleStatInfo.damage);
    }

    public final int getPVPDamage() {
        return info.get(MapleStatInfo.PVPdamage);
    }

    public final int getAttackCount() {
        return info.get(MapleStatInfo.attackCount);
    }

    public final int getBulletCount() {
        return info.get(MapleStatInfo.bulletCount);
    }

    public final int getBulletConsume() {
        return info.get(MapleStatInfo.bulletConsume);
    }

    public final int getMobCount() {
        return info.get(MapleStatInfo.mobCount);
    }

    public final int getMoneyCon() {
        return moneyCon;
    }

    public final int getCooltime() {
        return info.get(MapleStatInfo.cooltime);
    }

    public final int getCooltimeReduceR() {
        return info.get(MapleStatInfo.coolTimeR);
    }

    public final int getMesoAcquisition() {
        return info.get(MapleStatInfo.mesoR);
    }

    public final int getPowerEnergy() {
        return info.get(MapleStatInfo.powerCon);
    }

    public final int getCooldown(final MapleCharacter user) {
        if (user.getStat().coolTimeR > 0) {
            double v1 = info.get(MapleStatInfo.cooltime);
            double v2 = (user.getStat().coolTimeR / 100.0);
            double v3 = (1.0 - v2);
            double v4 = user.getStat().reduceCooltime;
            return (int) Math.max(0, ((v1 * v3) - v4));
        }
        return Math.max(0, (info.get(MapleStatInfo.cooltime) - user.getStat().reduceCooltime));
    }

    public final Map<MonsterTemporaryStat, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public final int getBerserk() {
        return berserk;
    }

    public final boolean isHide() {
        return skill && (sourceid == 9001004 || sourceid == 9101004);
    }

    public final boolean isDragonBlood() {
        return skill && sourceid == 1311008;
    }

    public final boolean isRecovery() {
        return skill && (sourceid == 1001 || sourceid == 10001001 || sourceid == 20001001 || sourceid == 20011001 || sourceid == 20021001 || sourceid == 11001 || sourceid == 35121005);
    }

    public final boolean isBerserk() {
        return skill && sourceid == 1320006;
    }

    public final boolean isBeholder() {
        return skill && sourceid == 1321007;
    }

    public final boolean isMPRecovery() {
        return skill && sourceid == 5101005;
    }

    public final boolean isInfinity() {
        return skill && (sourceid == 2121004 || sourceid == 2221004 || sourceid == 2321004);
    }

    public final boolean isMonsterRiding_() {
        return skill && (sourceid == 1004 || sourceid == 10001004 || sourceid == 20001004 || sourceid == 20011004 || sourceid == 11004 || sourceid == 20021004 || sourceid == 80001000);
    }

    public final boolean isMonsterRiding() {
        return skill && (isMonsterRiding_() || GameConstants.getMountItem(sourceid, null) != 0);
    }

    public final boolean isMagicDoor() {
        return skill && (sourceid == 2311002 || sourceid % 10000 == 8001);
    }

    public final boolean isMesoGuard() {
        return skill && (sourceid == 4211005 || sourceid == 4201011);
    }

    public final boolean isMechDoor() {
        return skill && sourceid == 35101005;
    }

    public final boolean isComboRecharge() {
        return skill && sourceid == 21111009;
    }

    public final boolean isDragonBlink() {
        return skill && sourceid == 22141004;
    }

    public final boolean isCharge() {
        switch (sourceid) {
            case 1211003:
            case 1211008:
            case 12101005:
            case 15101006:
            case 21111005:
                return skill;
        }
        return false;
    }

    public final boolean isTimeCapsule() {
        switch (sourceid) {
            case 36121007:
                return true;
        }
        return false;
    }

    public final boolean isPoison() {
        return (info.get(MapleStatInfo.dot) > 0 && info.get(MapleStatInfo.dotTime) > 0);
    }

    public boolean isMist() {
        return skill && (sourceid == 52111002 || sourceid == 12121005 || sourceid == 8001016 || sourceid == 62111004 || sourceid == 62121005 || sourceid == 2111003 || sourceid == 4221006 || sourceid == 12111005 || sourceid == 22161003 || sourceid == 32121006
                || sourceid == 1076 || sourceid == 11076 || sourceid == 4121015 || sourceid == 36121007 || sourceid == 61121105); // poison mist, smokescreen and flame gear, recovery aura
    }

    private boolean isSpiritClaw() {
        return skill && sourceid == 4111009 || sourceid == 14111007;
    }

    private boolean isSpiritBlast() {
        return skill && sourceid == 5201008;
    }

    private boolean isDispel() {
        return skill && (sourceid == 2311001 || sourceid == 9001000 || sourceid == 9101000 || sourceid == 52121008);
    }

    private boolean isHeroWill() {
        switch (sourceid) {
            case 4121009:
            case 21121008:
            case 1321010:
            case 5321006:
            case 3221008:
            case 36121009:
            case 35121008:
            case 23121008:
            case 24121009:
            case 2321009:
            case 33121008:
            case 1121011:
            case 5121008:
            case 22171004:
            case 32121008:
            case 4221008:
            case 2121008:
            case 3121009:
            case 5221010:
            case 27121010:
            case 2221008:
            case 4341008:
            case 1221012:
            case 52121011:
            case 61121004:
            case 62121007:
            case 65121012:
            case 11121015:
            case 12121015:
            case 13121015:
            case 14121015:
            case 15121015:
                return skill;
        }
        return false;
    }

    public boolean isUndispellable() {
        switch (sourceid) {
            case 4211003:  //Pickpocket
            case 2311007:  //Teleport Mastery
            case 22161005: //Teleport Mastery
            case 2211007:  //Teleport Mastery
            case 2111007:  //Teleport Mastery
            case 32111010: //Teleport Mastery
            case 12111007: //Teleport Mastery
            case 2321010:  //Buff Mastery
            case 2121009:  //Buff Mastery
            case 2221009:  //Buff Mastery
            case 22111001: //Magic Guard
            case 12001001: //Magic Guard
            case 2001002:  //Magic Guard
            case 2257000: //masterskill placeholder
            case 2257027: //overcharge
            case 2257040: //overdrive
            case 2257046: //demonic rage
            case 2257050: //light-bringer
            case 2257054: //heavy fire
            case 2257057: //blood veil
            case 2257061: //elemental unity
            case 32111012: //블루오라
            case 32001003: //다크오라
                return true;
        }
        return false;
    }

    public final boolean isAranCombo() {
        return sourceid == 21000000;
    }

    public final int realDice(int skillid) {
        switch (skillid) {
            case 35120014:
                return 35111013;
            case 5220014: // Combo
                return 5211007;
            case 5320007:
                return 5311005;
            case 5120012:
                return 5111007;
        }
        return skillid;
    }

    public final boolean isCombo() {
        switch (sourceid) {
            case 1111002:
            case 11111001: // Combo
                return skill;
        }
        return false;
    }

    public final boolean isPirateMorph() {
        switch (sourceid) {
            case 13111005:
            case 15111002:
            case 5111005:
            case 5121003:
                return skill;
        }
        return false;
    }

    public final boolean isMorph() {
        return morphId > 0;
    }

    public final boolean isDoubledice(int skillid) {
        switch (skillid) {
            case 5120012:
            case 5220014:
            case 5320007:
            case 35120014:
                return true;
        }
        return false;
    }

    public final int getMorph() {
        switch (sourceid) {
            case 15111002:
            case 5111005:
                return 1000;
            case 5121003:
                return 1001;
            case 5101007:
                return 1002;
            case 13111005:
                return 1003;
        }
        return morphId;
    }

    public final boolean isDivineBody() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1010;
    }

    public final boolean isDivineShield() {
        switch (sourceid) {
            case 1220013:
                return skill;
        }
        return false;
    }

    public final boolean isBerserkFury() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1011;
    }

    public final int getMorph(final MapleCharacter chr) {
        final int morph = getMorph();
        switch (morph) {
            case 1000:
            case 1001:
            case 1003:
                return morph + (chr.getGender() == 1 ? 100 : 0);
        }
        return morph;
    }

    public final byte getLevel() {
        return level;
    }

    public final SummonMovementType getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case 3211002: // puppet sniper
            case 3111002: // puppet ranger
            case 33111003:
            case 13111004: // puppet cygnus
            case 5211001: // octopus - pirate
            case 5220002: // advanced octopus - pirate
            case 4341006:
            case 35111002:
            case 35111005:
            case 35111011:
            case 35121009:
            case 35121010:
            case 35121011:
            case 4111007: //dark flare
            case 4211007: //dark flare         
            case 33101008:
            case 35121003:
            case 3120012:
            case 3220012:
            case 5321003:
            case 5321004:
            case 5320011:
            case 5211014:
            case 5711001: // turret     
            case 36121002:
            case 36121013:
            case 36121014:
            case 62111003:
            case 13111024:
            case 13120007:
            case 21111034:
                return SummonMovementType.STATIONARY;
            case 3211005: // golden eagle
            case 3111005: // golden hawk
            case 3101007:
            case 3201007:
            case 33101011:
            case 33111005:
            //case 3221005: // frostprey
            case 3121006: // phoenix
            case 23111008:
            case 23111009:
            case 23111010:
                return SummonMovementType.CIRCLE_FOLLOW;
            case 5211002: // bird - pirate      
                return SummonMovementType.CIRCLE_STATIONARY;
            case 32111006: //reaper
            case 5211011:
            case 5211015:
            case 5211016:
            case 62001100: // 귀야차Ⅰ
            case 62101006: // 귀야차Ⅱ
            case 62111005: // 귀야차Ⅲ
            case 62121009: // 귀야차Ⅳ
                return SummonMovementType.WALK_STATIONARY;
            case 1321007: // beholder
            case 2121005: // elquines
            case 2221005: // ifrit
            case 2321003: // bahamut
            case 12111004: // Ifrit
            case 11001004: // soul
            case 12001004: // flame
            case 13001004: // storm
            case 14001005: // darkness
            case 15001004: // lightning
            case 1196: // Mihile's Soul
            case 1197: // Oz's Flame
            case 1198: // Irena's Wind
            case 1199: // Eckhart's Darkness
            case 1200: // Hawkeye's Lightning
            case 35111001:
            case 35111010:
            case 35111009:
            case 22171052: //서먼 오닉스 드래곤
            case 1210: // 테스트
            case 1211:
            case 52101003:
                return SummonMovementType.FOLLOW;
        }
        if (isAngel()) {
            return SummonMovementType.FOLLOW;
        }
        return null;
    }

    public final boolean isAngel() {
        return GameConstants.isAngel(sourceid);
    }

    public final boolean isSkill() {
        return skill;
    }

    public final int getSourceId() {
        return sourceid;
    }

    public final boolean isIceKnight() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1105;
    }

    public final boolean isSoaring() {
        return isSoaring_Normal() || isSoaring_Mount();
    }

    public final boolean isSoaring_Normal() {
        return skill && (GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1026) || sourceid == 42111000;
    }

    public final boolean isSoaring_Mount() {
        return skill && ((GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1142) || sourceid == 80001089);
    }

    public final boolean isFinalAttack() {
        switch (sourceid) {
            case 13101002:
            case 11101002:
            case 23100006:
            case 23120012:
            case 33100009:
            case 51120002:
            case 21100010:
            case 21120012:
            case 1300002:
            case 1120013:
            case 1100002:
            case 51100002:

            case 3120008:
            case 3100001:
            case 1200002:
            case 61110008:
            case 62100007:
            case 65121007:
            case 15100027:
            case 13121017:
                return skill;
        }
        return false;
    }

    public final boolean isEpicAdventure() {
        switch (sourceid) {
            case 1121053:
            case 1221053:
            case 1321053:
            case 2121053:
            case 2221053:
            case 2321053:
            case 3121053:
            case 3221053:
            case 4121053:
            case 4221053:
            case 4341053:
            case 5121053:
            case 5221053:
            case 5321053: //에픽 어드벤쳐
            case 27121053: //히어로즈 오쓰[Hyper]
            case 21121053: //[아란-Hyper]
            case 22171053: //[에반-Hyper]
            case 23121053: //[메르세데스-Hyper]
            case 24121053: //[팬텀-Hyper]
                return true;
        }
        return false;
    }

    public final boolean isWillOfSword() {
        switch (sourceid) {
            case -1:
                return true;
        }
        return false;
    }

    public final boolean isStackBuff() {
        switch (sourceid) {
            case 4221054:
            case 5121052:
                return true;
        }
        return false;
    }

    public final boolean isMistEruption() {
        switch (sourceid) {
            case 2121003:
                return skill;
        }
        return false;
    }

    public final boolean isShadow() {
        switch (sourceid) {
            case 4111002: // shadowpartner
            case 14111000: // cygnus
            case 4211008:
            case 4331002:
                return skill;
        }
        return false;
    }

    public final boolean isMechPassive() {
        //switch (sourceid) {
        //case 35121005:
        //case 35121013:
        //return true;
        //}
        return false;
    }

    /**
     * @return true if the effect should happen based on it's probablity, false
     * otherwise
     */
    public final boolean makeChanceResult() {
        return getProb() >= 100 || Randomizer.nextInt(100) < getProb();
    }

    public final boolean makeChanceResultActive(MapleCharacter chr) {
        int bonus = getOnActive() + SkillFactory.getSkill(65000003).getEffect(65000003).getX();
        return bonus >= 100 || Randomizer.nextInt(100) < bonus;
    }

    public final boolean makeChanceResultActiveBonus(MapleCharacter chr, int bonusAffinity) {
        int bonus = getOnActive() + SkillFactory.getSkill(65000003).getEffect(65000003).getX() + bonusAffinity;
        return bonus >= 100 || Randomizer.nextInt(100) < bonus;
    }

    public final boolean makeChanceResultX() {
        return getX() >= 100 || Randomizer.nextInt(100) < getX();
    }

    public final boolean makeChanceResultY() {
        return getY() >= 100 || Randomizer.nextInt(100) < getY();
    }

    public final int getProb() {
        return info.get(MapleStatInfo.prop);
    }

    public final int getOnActive() {
        return info.get(MapleStatInfo.onActive);
    }

    public final short getIgnoreMob() {
        return ignoreMob;
    }

    public final int getEnhancedHP() {
        return info.get(MapleStatInfo.emhp);
    }

    public final int getEnhancedMP() {
        return info.get(MapleStatInfo.emmp);
    }

    public final int getEnhancedWatk() {
        return info.get(MapleStatInfo.epad);
    }

    public final int getEnhancedWdef() {
        return info.get(MapleStatInfo.epdd);
    }

    public final int getEnhancedMatk() {
        return info.get(MapleStatInfo.emad);
    }

    public final int getEnhancedMdef() {
        return info.get(MapleStatInfo.emdd);
    }

    public final int getDOT() {
        return info.get(MapleStatInfo.dot);
    }

    public final int getDOTTime() {
        return info.get(MapleStatInfo.dotTime);
    }

    public final int getCriticalMax() {
        return info.get(MapleStatInfo.criticaldamageMax);
    }

    public final int getCriticalMin() {
        return info.get(MapleStatInfo.criticaldamageMin);
    }

    public final int getASRRate() {
        return info.get(MapleStatInfo.asrR);
    }

    public final int getTERRate() {
        return info.get(MapleStatInfo.terR);
    }

    public final int getDAMRate() {
        return info.get(MapleStatInfo.damR);
    }

    public final int getHpToDamage() {
        return info.get(MapleStatInfo.mhp2damX);
    }

    public final int getMpToDamage() {
        return info.get(MapleStatInfo.mmp2damX);
    }

    public final int getLevelToDamage() {
        return info.get(MapleStatInfo.lv2damX);
    }

    public final int getLevelToWatk() {
        return info.get(MapleStatInfo.lv2pdX);
    }

    public final int getLevelToMatk() {
        return info.get(MapleStatInfo.lv2mdX);
    }

    public final int getEXPLossRate() {
        return info.get(MapleStatInfo.expLossReduceR);
    }

    public final int getBuffTimeRate() {
        return info.get(MapleStatInfo.bufftimeR);
    }

    public final int getSuddenDeathR() {
        return info.get(MapleStatInfo.suddenDeathR);
    }

    public final int getPercentAcc() {
        return info.get(MapleStatInfo.accR);
    }

    public final int getPercentAvoid() {
        return info.get(MapleStatInfo.evaR);
    }

    public final int getSummonTimeInc() {
        return info.get(MapleStatInfo.summonTimeR);
    }

    public final int getMPConsumeEff() {
        return info.get(MapleStatInfo.mpConEff);
    }

    public final short getMesoRate() {
        return mesoR;
    }

    public final int getEXP() {
        return exp;
    }

    public final int getAttackX() {
        return info.get(MapleStatInfo.padX);
    }

    public final int getMagicX() {
        return info.get(MapleStatInfo.madX);
    }

    public final int getPercentHP() {
        return info.get(MapleStatInfo.mhpR);
    }

    public final int getPercentMP() {
        return info.get(MapleStatInfo.mmpR);
    }

    public final int getAttacksRemaining() {
        return info.get(MapleStatInfo.attacksRemaining);
    }

    public final int getConsume() {
        return consumeOnPickup;
    }

    public final int getSelfDestruction() {
        return info.get(MapleStatInfo.selfDestruction);
    }

    public final int getCharColor() {
        return charColor;
    }

    public final List<Integer> getPetsCanConsume() {
        return petsCanConsume;
    }

    public final boolean isReturnScroll() {
        return skill && (sourceid == 80001040 || sourceid == 20021110 || sourceid == 20031203 || sourceid == 30021235);
    }

    public final boolean isMechChange() {
        switch (sourceid) {
            case 35111004:
            case 35001001:
            case 35100008:
            case 35101009:
            case 35121013:
            case 35121005:
                return skill;
        }
        return false;
    }

    public final int getRange() {
        return info.get(MapleStatInfo.range);
    }

    public final int getER() {
        return info.get(MapleStatInfo.er);
    }

    public final int getPrice() {
        return info.get(MapleStatInfo.price);
    }

    public final int getExtendPrice() {
        return info.get(MapleStatInfo.extendPrice);
    }

    public final int getPeriod() {
        return info.get(MapleStatInfo.period);
    }

    public final int getReqGuildLevel() {
        return info.get(MapleStatInfo.reqGuildLevel);
    }

    public final byte getEXPRate() {
        return expR;
    }

    public final short getLifeID() {
        return lifeId;
    }

    public final short getUseLevel() {
        return useLevel;
    }

    public final byte getSlotCount() {
        return slotCount;
    }

    public final int getStr() {
        return info.get(MapleStatInfo.str);
    }

    public final int getStrX() {
        return info.get(MapleStatInfo.strX);
    }

    public final int getDex() {
        return info.get(MapleStatInfo.dex);
    }

    public final int getDexX() {
        return info.get(MapleStatInfo.dexX);
    }

    public final int getInt() {
        return info.get(MapleStatInfo.int_);
    }

    public final int getIntX() {
        return info.get(MapleStatInfo.intX);
    }

    public final int getLuk() {
        return info.get(MapleStatInfo.luk);
    }

    public final int getLukX() {
        return info.get(MapleStatInfo.lukX);
    }

    public final int getMaxHpX() {
        return info.get(MapleStatInfo.mhpX);
    }

    public final int getMaxMpX() {
        return info.get(MapleStatInfo.mmpX);
    }

    public final int getAccX() {
        return info.get(MapleStatInfo.accX);
    }

    public final int getMPConReduce() {
        return info.get(MapleStatInfo.mpConReduce);
    }

    public final int getIndieMHp() {
        return info.get(MapleStatInfo.indieMhp);
    }

    public final int getIndieMMp() {
        return info.get(MapleStatInfo.indieMmp);
    }

    public final int getIndieAllStat() {
        return info.get(MapleStatInfo.indieAllStat);
    }

    public final byte getType() {
        return type;
    }

    public int getBossDamage() {
        return info.get(MapleStatInfo.bdR);
    }

    public int getInterval() {
        return interval;
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMaps() {
        return availableMap;
    }

    public int getWDEFX() {
        return info.get(MapleStatInfo.pddX);
    }

    public int getMDEFX() {
        return info.get(MapleStatInfo.mddX);
    }

    public int getWDEFRate() {
        return info.get(MapleStatInfo.pddR);
    }

    public int getMDEFRate() {
        return info.get(MapleStatInfo.mddR);
    }

    public static class CancelEffectAction implements Runnable {

        private final MapleStatEffect effect;
        private final WeakReference<MapleCharacter> target;
        private final long startTime;
        private final Map<CharacterTemporaryStat, Integer> statup;

        public CancelEffectAction(final MapleCharacter target, final MapleStatEffect effect, final long startTime, final Map<CharacterTemporaryStat, Integer> statup) {
            this.effect = effect;
            this.target = new WeakReference<>(target);
            this.startTime = startTime;
            this.statup = statup;
        }

        @Override
        public void run() {
            final MapleCharacter realTarget = target.get();
            if (realTarget != null) {
                realTarget.cancelEffect(effect, false, startTime, statup);
            }
        }
    }

    public short getMpCon() {
        return mpCon;
    }

    public void setMpCon(short mpCon) {
        this.mpCon = mpCon;
    }

    public long getHakuBuff() {
        return hakuBufftime;
    }

    public void setHakuBuff(long time) {
        this.hakuBufftime = time;
    }
}
