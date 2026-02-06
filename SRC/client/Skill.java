/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;

import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.Element;
import tools.Pair;

public class Skill {

    private String name = "", psdDamR = "";
    private final List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private final List<Pair<Integer, Byte>> requiredSkill = new ArrayList<Pair<Integer, Byte>>();
    private Element element = Element.NEUTRAL;
    private int id, animationTime = 0, masterLevel = 0, maxLevel = 0, delay = 0, trueMax = 0, eventTamingMob = 0, skillType = 0, psd = 0, psdSkill = 0;
    private boolean invisible = false, chargeskill = false, timeLimited = false, combatOrders = false, pvpDisabled = false, magic = false, casterMove = false, pushTarget = false, pullTarget = false;
    private boolean hyper = false;

    public Skill(final int id) {
        super();
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static final Skill loadFromData(final int id, final MapleData data, final MapleData delayData) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        }
        ret.skillType = skillType;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        ret.psd = MapleDataTool.getInt("psd", data, 0);
        if (ret.psd == 1) {
            final MapleData psdskill = data.getChildByPath("psdSkill");
            if (psdskill != null) {
                ret.psdSkill = Integer.parseInt(data.getChildByPath("psdSkill").getChildren().get(0).getName());
            }
        }
        if ((id == 22111001 || id == 22140000 || id == 22141002)) {
            ret.masterLevel = 5; //hack
        }
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        final MapleData inf = data.getChildByPath("info");
        if (inf != null) {
            ret.pvpDisabled = MapleDataTool.getInt("pvp", inf, 1) <= 0;
            ret.magic = MapleDataTool.getInt("magicDamage", inf, 0) > 0;
            ret.casterMove = MapleDataTool.getInt("casterMove", inf, 0) > 0;
            ret.pushTarget = MapleDataTool.getInt("pushTarget", inf, 0) > 0;
            ret.pullTarget = MapleDataTool.getInt("pullTarget", inf, 0) > 0;
        }
        final MapleData effect = data.getChildByPath("effect");
        if (skillType == 2) {
            isBuff = true;
        } else if (skillType == 3) { //final attack
            ret.animation = new ArrayList<Integer>();
            ret.animation.add(0);
            isBuff = effect != null;
        } else {
            MapleData action_ = data.getChildByPath("action");
            final MapleData hit = data.getChildByPath("hit");
            final MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action_ = data.getChildByPath("prepare/action");
                    action = true;
                }
            }
            isBuff = effect != null && hit == null && ball == null;
            if (action_ != null) {
                String d = null;
                if (action) { //prepare
                    d = MapleDataTool.getString(action_, null);
                } else {
                    d = MapleDataTool.getString("0", action_, null);
                }
                if (d != null) {
                    isBuff |= d.equals("alert2");
                    final MapleData dd = delayData.getChildByPath(d);
                    if (dd != null) {
                        for (MapleData del : dd) {
                            ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                        }
                        if (ret.delay > 30) { //then, faster(2) = (10+2)/16 which is basically 3/4
                            ret.delay = (int) Math.round(ret.delay * 11.0 / 16.0); //fastest(1) lolol
                            ret.delay -= (ret.delay % 30); //round to 30ms
                        }
                    }
                    if (SkillFactory.getDelay(d) != null) { //this should return true always
                        ret.animation = new ArrayList<Integer>();
                        ret.animation.add(SkillFactory.getDelay(d));
                        if (!action) {
                            for (MapleData ddc : action_) {
                                if (!MapleDataTool.getString(ddc, d).equals(d)) {
                                    String c = MapleDataTool.getString(ddc);
                                    if (SkillFactory.getDelay(c) != null) {
                                        ret.animation.add(SkillFactory.getDelay(c));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            switch (id) {
                case 2301002: // heal is alert2 but not overtime...
                case 2111003: // poison mist
                case 12111005: // Flame Gear
                case 22161003: //Evan's Recovery Aura
                case 32121006:
                case 11076:
                case 2111002: // explosion
                case 4211001: // chakra
                case 2121001: // Big bang
                case 2221001: // Big bang
                case 2321001: // Big bang
                case 1076:
                case 62111004:
                case 62121005:
                case 8001016:
                case 12121005:
                case 52111002:
                    isBuff = false;
                    break;
                /*에픽어드벤쳐*/
                case 4121053:
                case 1321053:
                case 3221053:
                case 5321053:
                case 2321053:
                case 1121053:
                case 2121053:
                case 4221053:
                case 3121053:
                case 5221053:
                case 2221053:
                case 5121053:
                case 4341053:
                case 1221053:
                case 23121053:
                case 24121053:
                case 22171053:
                case 27121053:
                case 21121053:
                case 35121053:
                case 31221053:
                case 33121053:
                case 32121053:
                case 31121053:
                /*에픽어드벤쳐끝*/
                case 5221015://호밍
                case 32121054://유니온오라
                case 32121010://배틀레이지
                case 31121054://블루 블러드
                case 31121005://메타모포시스..
                case 21121054://콤보언리미티드
                case 24121054://파이널저지먼트
                case 2121054://파이어 오라
                case 2221054://아이스 오라
                case 3221054://불스아이
                case 4121054://블리딩톡신
                case 4221054://플립더코인
                case 2321052://헤븐즈 도어
                case 2321054://벤전스 오브 엔젤     
                case 4341052://아수라
                case 4341054:
                case 5121054://스타뮬레이트
                case 5121052://유니티오브파워
                case 5221054://언위어링 넥타
                case 5321054://벅샷
                case 24121007:
                case 24111003://미스포츈 프로텍션
                case 1101006:
                case 1004: // monster riding
                case 10001004:
                case 20001004:
                case 20011004:
                case 80001000:
                case 93:
                case 10000093:
                case 20000093:
                case 20010093:
                case 20020093:
                case 30000093:
                case 30010093:
                case 9101004: // hide is a buff -.- atleast for us o.o"
                case 1111002: // combo
                case 4211003: // pickpocket
                case 4111001: // mesoup
                case 15111002: // Super Transformation
                case 5111005: // Transformation
                case 5121003: // Super Transformation
                case 13111005: // Alabtross
                case 21000000: // Aran Combo
                case 21101003: // Body Pressure
                case 5211001: // Pirate octopus summon
                case 5211002:
                case 5220002: // wrath of the octopi
                case 5001005: //dash
                case 15001003:
                case 5211006: //homing beacon
                case 5220011: //bullseye
                case 5110001: //energy charge
                case 15100004:
                case 5121009: //speed infusion
                case 15121005:
                case 1221054://새크로생티티
                case 22121001: //element reset
                case 22131001: //magic shield
                case 22141002: //magic booster
                case 22151002: //killer wing
                case 22151003: //magic resist
                case 22161002: //imprint
                case 22171000: //maple warrior
                case 22171004: //hero will
                case 22181000: //onyx blessing
                case 22181004:
                case 22161004:
                case 22181003: //soul stone
                //case 22121000:
                //case 22141003:
                //case 22151001:
                //case 22161002:
                case 4331003: //owl spirit
                //case 15101006: //spark
                case 15121004: //spark
                case 4321000: //tornado spin
                case 1320009: //beholder's buff.. passive
                case 35120000:
                case 35001002: //TEMP. mech
                case 9001004: // hide
                //case 4341002:
                case 22131002:

                case 32001003: //dark aura
                case 32120000:
                case 32111012: // 블루 오라
                case 32110000: // 어드밴스드 블루 오라
                case 32101003: //yellow aura
                case 32120001:
                case 35101007: //perfect armor
                case 35121006: //satellite safety
                case 35001001: //flame
                case 35100008:
                case 35101009:
                case 35101010:
                case 35121005: //missile
                case 35121013:
                case 35111004: //siege
                case 33111003: //puppet ?
                case 1211009:
                case 1111007:
                case 1311007: //magic,armor,atk crash
                case 51111005:
                case 11111008:
                case 32121003: //twister

                case 5111007:
                case 5211007:
                case 5311005:
                case 5320007:
                case 35111013: //dice
                case 35120014: //double lucky
                case 32111006:
                case 5120011:
                case 5220012:
                case 1220013:
                case 33101006: //jaguar oshi
                case 32110007:
                case 32110008:
                case 32110009:
                case 32111005:

                //case 35121003:
                case 35121009:
                case 35121010:
                //case 35111005:
                case 35111001:
                case 35111010:
                case 35111009:
                case 35111011:
                case 35111002:

                case 3120006:
                case 3220005:

                case 2121009:
                case 2120010:
                case 2221009:
                case 2220010:
                case 2321010:
                case 2320011:
                case 5321003:
                case 5321004:
                case 80001089:

                //Soaring
                case 1026: // Soaring
                case 10001026: // Soaring
                case 20001026: // Soaring
                case 20011026: // Soaring
                case 20021026:
                case 20031026:
                case 20041026:
                case 30001026:
                case 30011026:
                case 50001026:
                case 60001026:
                case 60011026:

                // 루미너스
                case 20040216: // 선파이어
                case 20040217: // 이클립스
                case 20040219: // 이퀄리브리엄
                case 20040220: // 이퀄리브리엄
                case 27001004: // 익스텐드 마나
                case 27100003: // 블레스 오브 다크니스
                case 27101202: // 보이드 프레셔
                case 27111004: // 안티 매직쉘
                case 27111005: // 라이트쉐도우 가드
                case 27111006: // 포틱 메디테이션
                case 27110007: // 라이프 타이달
                case 27121006: // 다크니스 소서리
                case 27121054: // 메모라이즈

                case 31011000:
                case 31201000:
                case 31211000:
                case 31221000:

                case 4330009:
                case 2321003:
                case 3110007:
                    
                case 42111000:
                case 60001002:
                case 61121002:
                case 60011023:
                case 61000001:
                    
                case 13111020:
                case 13120007:
                case 13120008:
                case 21120003:
                case 52101003:
                    isBuff = true;
                    break;
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;
        ret.hyper = (data.getChildByPath("hyper") != null);
        //some skills have old system, some new		
        final MapleData level = data.getChildByPath("common");
        if (level != null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", level, 1); //10 just a failsafe, shouldn't actually happens
            ret.psdDamR = MapleDataTool.getString("damR", level, ""); //for the psdSkill tag
            ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : 0);
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "x"));
                //ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "t"));
            }

        } else {
            for (final MapleData leve : data.getChildByPath("level")) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(leve, id, isBuff, Byte.parseByte(leve.getName()), null));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }
        final MapleData level2 = data.getChildByPath("PVPcommon");
        if (level2 != null) {
            ret.pvpEffects = new ArrayList<MapleStatEffect>();
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.pvpEffects.add(MapleStatEffect.loadSkillEffectFromData(level2, id, isBuff, i, "x"));
            }
        }
        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill.add(new Pair<Integer, Byte>(Integer.parseInt(reqData.getName()), (byte) MapleDataTool.getInt(reqData, 1)));
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public MapleStatEffect getPVPEffect(final int level) {
        if (pvpEffects == null) {
            return getEffect(level);
        }
        if (pvpEffects.size() < level) {
            if (pvpEffects.size() > 0) { //incAllskill
                return pvpEffects.get(pvpEffects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return pvpEffects.get(0);
        }
        return pvpEffects.get(level - 1);
    }

    public int getSkillType() {
        return skillType;
    }

    public List<Integer> getAllAnimation() {
        return animation;
    }

    public int getAnimation() {
        if (animation == null) {
            return -1;
        }
        return animation.get(Randomizer.nextInt(animation.size()));
    }

    public boolean isPVPDisabled() {
        return pvpDisabled;
    }

    public boolean isChargeSkill() {
        return chargeskill;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean hasRequiredSkill() {
        return requiredSkill.size() > 0;
    }

    public int getPsdSkill() {
        return psdSkill;
    }

    public int getPsd() {
        return psd;
    }

    public String getPsdDamR() {
        return psdDamR;
    }

    public List<Pair<Integer, Byte>> getRequiredSkills() {
        return requiredSkill;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTrueMax() {
        return trueMax;
    }

    public boolean combatOrders() {
        return combatOrders;
    }

    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = id / 10000;
        if (skillForJob == 2001) {
            return GameConstants.isEvan(job); //special exception for beginner -.-
        } else if (skillForJob == 400) {
            return GameConstants.isDualBlade(job) || GameConstants.isAdventurer(job);
        } else if (skillForJob == 0) {
            return GameConstants.isAdventurer(job); //special exception for beginner
        } else if (skillForJob == 1000) {
            return GameConstants.isKOC(job); //special exception for beginner
        } else if (skillForJob == 2000) {
            return GameConstants.isAran(job); //special exception for beginner
        } else if (skillForJob == 3000) {
            return GameConstants.isResist(job); //special exception for beginner
        } else if (skillForJob == 1) {
            return GameConstants.isCannon(job); //special exception for beginner
        } else if (skillForJob == 3001) {
            return GameConstants.isDemon(job); //special exception for beginner
        } else if (skillForJob == 3101) {
            return GameConstants.isDemonAvenger(job); //special exception for beginner
        } else if (skillForJob == 2002) {
            return GameConstants.isMercedes(job); //special exception for beginner
        } else if (skillForJob == 2003) {
            return GameConstants.isPhantom(job); //special exception for beginner
        } else if (skillForJob == 5000) {
            return GameConstants.isMihile(job); //special exception for beginner
        } else if (skillForJob == 6000) {
            return GameConstants.isHayato(job); //special exception for beginner
        } else if (skillForJob == 6001) {
            return GameConstants.isKanna(job);
        } else if (skillForJob == 6002) {
            return GameConstants.isMukhyun(job);
        } else if (skillForJob == 4000) {
            return GameConstants.isBeastTamer(job);
        } else if (skillForJob == 5001) {
            return GameConstants.isLynn(job);
        } else if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // wrong job
            return false;
        } else if (GameConstants.isHayato(skillForJob) && !GameConstants.isHayato(job)) {
            return false;
        } else if (GameConstants.isKanna(skillForJob) && !GameConstants.isKanna(job)) {
            return false;
        } else if (GameConstants.isMukhyun(skillForJob) && !GameConstants.isMukhyun(job)) {
            return false;
        } else if (GameConstants.isBeastTamer(skillForJob) && !GameConstants.isBeastTamer(job)) {
            return false;
        } else if (GameConstants.isLynn(skillForJob) && !GameConstants.isLynn(job)) {
            return false;
        } else if (GameConstants.isMihile(skillForJob) && !GameConstants.isMihile(job)) {
            return false;
        } else if (GameConstants.isPhantom(skillForJob) && !GameConstants.isPhantom(job)) {
            return false;
        } else if (GameConstants.isCannon(skillForJob) && !GameConstants.isCannon(job)) {
            return false;
        } else if (GameConstants.isDemon(skillForJob) && !GameConstants.isDemon(job)) {
            return false;
        } else if (GameConstants.isDemonAvenger(skillForJob) && !GameConstants.isDemonAvenger(job)) {
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && (!GameConstants.isAdventurer(job))) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isMercedes(skillForJob) && !GameConstants.isMercedes(job)) {
            return false;
        } else if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
            return false;
        } else if ((jid / 10) % 10 == 0 && (skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
            return false;
        } else if ((skillForJob / 10) % 10 != 0 && (skillForJob / 10) % 10 != (jid / 10) % 10) { //wrong 2nd job
            return false;
        } else if (skillForJob / 10 > jid / 10) {
            return false;
        }
        return true;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public boolean isFourthJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 412:
            case 422:
            case 512:
            case 522:
                return true;
        }
        return false;
    }

    public boolean isThirdJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 111:
            case 121:
            case 131:
            case 211:
            case 221:
            case 231:
            case 311:
            case 321:
            case 411:
            case 421:
            case 511:
            case 521:
                return true;
        }
        return false;
    }

    public boolean isSecondJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 110:
            case 120:
            case 130:
            case 210:
            case 220:
            case 230:
            case 310:
            case 320:
            case 410:
            case 420:
            case 510:
            case 520:
                return true;
        }
        return false;
    }

    public boolean isIgnoreMasterLevelForCommon(int a1) {
        if (a1 > 5120006) {
            if (a1 == 5120011 || a1 == 5120012) {
                return true;
            }
            if (a1 > 22181004) {
                if (a1 <= 33120010) {
                    if (a1 == 33120010 || a1 == 23120011) {
                        return true;
                    }
                    return (a1 == 23121008);
                }
                if (a1 != 33121005) {
                    return (a1 == 51120000);
                }
            } else if (a1 != 22181004) {
                if (a1 <= 5321004) {
                    if (a1 >= 5321003 || a1 == 5220012 || (a1 - 5220012) == 2) {
                        return true;
                    }
                    return ((a1 - 5220012) == 99995);
                }
                if (a1 != 5321006) {
                    return (a1 == 21120011);
                }
            }
            return true;
        }
        if (a1 >= 5120011) {
            return true;
        }
        if (a1 > 3120012) {
            if (a1 > 4210012) {
                if (a1 != 4340010) {
                    return ((a1 - 4340010) == 2);
                }
            } else if (a1 != 4210012) {
                if (a1 > 3220012) {
                    return (a1 == 4110012);
                }
                if (a1 != 3220012 && (a1 < 3220009 || a1 > 3220010)) {
                    return false;
                }
            }
            return true;
        }
        if (a1 >= 3120010) {
            return true;
        }
        if (a1 > 2121009) {
            if (a1 != 2221009) {
                return (a1 == 2321010);
            }
            return true;
        }
        if (a1 == 2121009 || a1 == 1120012 || a1 == 1220013) {
            return true;
        }
        return (a1 == 1320011);
    }

    public boolean isProductionSkill(int a1) {
        if ((a1 / 1000000) != 92 || (a1 % 10000) != 0) {
            int v1 = (10000 * (a1 / 10000));
            if ((v1 / 1000000) == 92 && (v1 % 10000) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isBeginnerJob(int a1) {
        return (a1 % 1000 == 0 || a1 == 2001 || a1 == 2002 || a1 == 3001 || a1 == 2003 || a1 == 5000 || a1 == 2004 || a1 == 6000 || a1 == 6001 || a1 == 6002 || a1 == 4000 || a1 == 5001);
    }

    public int getJobLevel(int arg_0) {
        int result = 0;
        if (isBeginnerJob(arg_0) || arg_0 % 100 == 0 || arg_0 == 501) {
            return 1;
        }
        int v2 = 0;
        if ((arg_0 / 10) == 43) {
            v2 = ((arg_0 - 430) / 2);
        } else {
            v2 = (arg_0 % 10);
        }
        int v3 = (v2 + 2);
        if (v3 >= 2 && (v3 <= 4 || (v3 <= 10 && isEvanJob(arg_0)))) {
            result = v3;
        } else {
            result = 0;
        }
        return result;
    }

    public boolean isEvanJob(int a1) {
        return (a1 / 100 == 22 || a1 == 2001);
    }

    public boolean isSkillNeedMasterLevel() {
        int a1 = this.id;
        if (isIgnoreMasterLevelForCommon(a1) || ((a1 / 1000000) == 92 && (a1 % 10000) == 0) || isProductionSkill(a1)) {
            return false;
        }
        int v2 = (a1 / 10000);
        if (v2 == 8000 || isBeginnerJob(v2)) {
            return false;
        }
        int v3 = getJobLevel(v2);
        if (isEvanJob(v2)) {
            if (v3 != 9 && v3 != 10) {
                return false;
            }
        } else if (a1 != 4311003 && a1 != 4331002 && a1 != 4321006 && a1 != 4330009) {
            return (v3 == 4);
        }
        return true;
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public int getDelay() {
        return delay;
    }

    public int getTamingMob() {
        return eventTamingMob;
    }

    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return GameConstants.isBeginnerJob(jobId);
    }

    public boolean isMagic() {
        return magic;
    }

    public boolean ishyper() {
        return hyper;
    }

    public boolean isMovement() {
        return casterMove;
    }

    public boolean isPush() {
        return pushTarget;
    }

    public boolean isPull() {
        return pullTarget;
    }

    public boolean isSpecialSkill() {
        int jobId = id / 10000;
        return jobId == 900 || jobId == 800 || jobId == 9000 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }

    public boolean is_professional_skill() {
        return ((this.id / 1000000) == 92 && (this.id % 10000) == 0);
    }
}
