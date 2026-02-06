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
package client.inventory;

import constants.GameConstants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructItemOption;
import tools.ArrayUtil;

public class Equip extends Item implements Serializable {

    public static enum ScrollResult {

        SUCCESS, FAIL, CURSE
    }

    public static final int ARMOR_RATIO = 1;
    public static final int WEAPON_RATIO = 1;
    //charm: -1 = has not been initialized yet, 0 = already been worn, >0 = has teh charm exp
    private byte upgradeSlots = 0, level = 0, vicioushammer = 0, enhance = 0, cashCover = 0, enhanctBuff = 0, reqLevel = 0, yggdrasilWisdom = 0, bossDamage = 0, ignorePDR = 0, totalDamage = 0, allStat = 0, karmaCount = -1;
    private short str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, charmExp = 0, pvpDamage = 0;
    private int itemEXP = 0, durability = -1, incSkill = -1, potential1 = 0, potential2 = 0, potential3 = 0, potential4 = 0, potential5 = 0, potential6 = 0, fusionAnvil = 0;
    private boolean finalStrike = false;
    private MapleRing ring = null;
    private MapleAndroid android = null;
    private List<EquipStat> stats = new LinkedList();
    private List<EquipSpecialStat> specialStats = new LinkedList();
    public static final int HIDDEN = 1, RARE = 17, EPIC = 18, UNIQUE = 19, LEGENDARY = 20;
    private int[] mainPotential = new int[3];
    private int[] bonusPotential = new int[3];
    private short hpR = 0, mpR = 0;

    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, short flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.enhance = enhance;
        ret.upgradeSlots = upgradeSlots;
        ret.level = level;
        ret.itemEXP = itemEXP;
        ret.durability = durability;
        ret.vicioushammer = vicioushammer;
        /* ret.potential1 = potential1;
         ret.potential2 = potential2;
         ret.potential3 = potential3;
         ret.potential4 = potential4;
         ret.potential5 = potential5;
         ret.potential6 = potential6;*/
        for (int i = 0; i < mainPotential.length; i++) {
            ret.mainPotential[i] = mainPotential[i];
        }
        for (int i = 0; i < bonusPotential.length; i++) {
            ret.bonusPotential[i] = bonusPotential[i];
        }
        ret.fusionAnvil = fusionAnvil;
        ret.charmExp = charmExp;
        ret.pvpDamage = pvpDamage;
        ret.cashCover = cashCover;
        ret.incSkill = incSkill;
        ret.enhanctBuff = enhanctBuff;
        ret.reqLevel = reqLevel;
        ret.yggdrasilWisdom = yggdrasilWisdom;
        ret.finalStrike = finalStrike;
        ret.bossDamage = bossDamage;
        ret.ignorePDR = ignorePDR;
        ret.totalDamage = totalDamage;
        ret.allStat = allStat;
        ret.karmaCount = karmaCount;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        ret.stats = stats;
        ret.specialStats = specialStats;
        ret.hpR = hpR;
        ret.mpR = mpR;
        return ret;
    }

    @Override
    public byte getType() {
        return 1;
    }

    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    public short getStr() {
        return str;
    }

    public short getDex() {
        return dex;
    }

    public short getInt() {
        return _int;
    }

    public short getLuk() {
        return luk;
    }

    public short getHp() {
        return hp;
    }

    public short getMp() {
        return mp;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }

    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getCashCover() {
        return cashCover;
    }

    public void setCashCover(byte cover) {
        cashCover = cover;
    }

    public byte getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        vicioushammer = ham;
    }

    public int getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(int itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    public int getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / WEAPON_RATIO;
        } else {
            return itemEXP / ARMOR_RATIO;
        }
    }

    public int getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        int expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    public int getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        int expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        this.durability = dur;
    }

    public byte getEnhanctBuff() {
        return enhanctBuff;
    }

    public void setEnhanctBuff(byte enhanctBuff) {
        this.enhanctBuff = enhanctBuff;
    }

    public byte getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(byte reqLevel) {
        this.reqLevel = reqLevel;
    }

    public byte getYggdrasilWisdom() {
        return yggdrasilWisdom;
    }

    public void setYggdrasilWisdom(byte yggdrasilWisdom) {
        this.yggdrasilWisdom = yggdrasilWisdom;
    }

    public boolean getFinalStrike() {
        return finalStrike;
    }

    public void setFinalStrike(boolean finalStrike) {
        this.finalStrike = finalStrike;
    }

    public byte getBossDamage() {
        return bossDamage;
    }

    public void setBossDamage(byte bossDamage) {
        this.bossDamage = bossDamage;
    }

    public byte getIgnorePDR() {
        return ignorePDR;
    }

    public void setIgnorePDR(byte ignorePDR) {
        this.ignorePDR = ignorePDR;
    }

    public byte getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(byte totalDamage) {
        this.totalDamage = totalDamage;
    }

    public byte getAllStat() {
        return allStat;
    }

    public void setAllStat(byte allStat) {
        this.allStat = allStat;
    }

    public byte getKarmaCount() {
        return karmaCount;
    }

    public void setKarmaCount(byte karmaCount) {
        this.karmaCount = karmaCount;
    }

    public byte getEnhance() {
        return enhance;
    }

    public void setEnhance(final byte en) {
        this.enhance = en;
    }

    public int getPotential(byte line) {
        if (line == 1) {
            return potential1;
        }
        if (line == 2) {
            return potential2;
        }
        if (line == 3) {
            return potential3;
        }
        if (line == 4) {
            return potential4;
        }
        if (line == 5) {
            return potential5;
        }
        if (line == 5) {
            return potential6;
        }
        return -1;
    }

    public void setPotential(byte line, int potID) {
        if (line == 1) {
            this.potential1 = potID;
        }
        if (line == 2) {
            this.potential2 = potID;
        }
        if (line == 3) {
            this.potential3 = potID;
        }
        if (line == 4) {
            this.potential4 = potID;
        }
        if (line == 5) {
            this.potential5 = potID;
        }
        if (line == 6) {
            this.potential6 = potID;
        }
    }

    public int getPotential1() {
        return potential1;
    }

    public void setPotential1(final int en) {
        this.potential1 = en;
    }

    public int getPotential2() {
        return potential2;
    }

    public void setPotential2(final int en) {
        this.potential2 = en;
    }

    public int getPotential3() {
        return potential3;
    }

    public void setPotential3(final int en) {
        this.potential3 = en;
    }

    public int getPotential4() {
        return potential4;
    }

    public void setPotential4(final int en) {
        this.potential4 = en;
    }

    public int getPotential5() {
        return potential5;
    }

    public void setPotential5(final int en) {
        this.potential5 = en;
    }

    public int getPotential6() {
        return potential6;
    }

    public void setPotential6(final int en) {
        this.potential6 = en;
    }

    public int getPotentialByLine(int line) {
        return mainPotential[line];
    }

    public void setPotentialByLine(int line, int potential) {
        mainPotential[line] = potential;
    }

    public int[] getPotential() {
        return mainPotential;
    }

    public void setPotential(int[] newMainPotential) {
        mainPotential = newMainPotential;
    }

    public int getBonusPotentialByLine(int line) {
        return bonusPotential[line];
    }

    public void setBonusPotentialByLine(int line, int potential) {
        bonusPotential[line] = potential;
    }

    public int[] getBonusPotential() {
        return bonusPotential;
    }

    public void setBonusPotential(int[] newBonusPotential) {
        bonusPotential = newBonusPotential;
    }

    public int getFusionAnvil() {
        return fusionAnvil;
    }

    public void setFusionAnvil(final int en) {
        fusionAnvil = en;
    }

    /* public byte getState() {
     final int pots = potential1 + potential2 + potential3;
     byte state = 0;
     if (potential1 >= 40000 || potential2 >= 40000 || potential3 >= 40000) {
     state = 20; // legendary
     } else if (potential1 >= 30000 || potential2 >= 30000 || potential3 >= 30000) {
     state = 19; // unique
     } else if (potential1 >= 20000 || potential2 >= 20000 || potential3 >= 20000) {
     state = 18; // epic
     } else if (pots >= 1) {
     state = 17; // rare
     } else if (pots < 0) {
     state = 1; // hidden
     }
     return state;
     }    */
    public byte getState() {
        int maxPot = ArrayUtil.absoluteMax(ArrayUtil.concat(getPotential(), getBonusPotential()));
        byte res = 0;
        if (maxPot < 0) {
            res = HIDDEN; //hidden
        } else if (maxPot >= 40000) {
            res = LEGENDARY; //legendary
        } else if (maxPot >= 30000) {
            res = UNIQUE; //unique
        } else if (maxPot >= 20000) {
            res = EPIC; //epic
        } else if (maxPot >= 1) {
            res = RARE; //rare
        }
        return res;
    }

    public byte getStateByPotential(int[] potential) {
        int maxPot = ArrayUtil.absoluteMax(potential);
        byte res = 0;
        byte res2 = 0;
        if (getBonusPotentialByLine(1) < 0) {
            res2 = 32;
        }
        if (maxPot < 0) {
            res = (byte) (HIDDEN + res2); //hidden
        } else if (maxPot >= 40000) {
            res = (byte) (LEGENDARY + res2); //legendary
        } else if (maxPot >= 30000) {
            res = (byte) (UNIQUE + res2); //unique
        } else if (maxPot >= 20000) {
            res = (byte) (EPIC + res2); //epic
        } else if (maxPot >= 1) {
            res = (byte) (RARE + res2); //rare
        }
        return res;
    }

    public void renewPotential_OLD(int type, int line, int toLock, boolean bonus) {
        int miracleRate = 1;
        int rank;
        if (bonus) {
            if (type != 6) {
                return;
            }
            rank = (Randomizer.nextInt(100) < 4 * miracleRate && getStateByPotential(getBonusPotential()) != LEGENDARY ? -(getStateByPotential(getBonusPotential()) + 1) : -(getStateByPotential(getBonusPotential())));
            setBonusPotentialByLine(1, rank);
        } else {
            /*
             2 : 에픽 잠재 능력 부여 주문서
             0 : 미라클 큐브
             1 : 메이플 미라클 큐브
             3 : 필드 몬스터 드랍 / 잠재 능력 부여 주문서
             5 : 어메이징 미라클 큐브
             4 : 마스터 미라클 큐브

             Type == 2 : 에픽(100%) 
             Type == 0 : 레어(90%) / 에픽(10%)
             에픽(95%)일시, 유니크(5%)
             Type == 1 : 레어(83%) / 에픽(17%)
             에픽(91%)일시, 유니크(9%)
             Type == 3 : 레어(85%) / 에픽(15%)
             Type == 5 : 레어(85%) / 에픽(15%)
             에픽(90%)일시, 유니크(10%)
             유니크(95%)일시, 레전드리(2%)
             Type == 4 : 레어(88%)일시, 에픽(12%)
             에픽(94%)일시, 유니크(6%)
             유니크(98%)일시, 레전드리(2%)
             */
            rank = type == 2 ? (-EPIC)
                    : type == 3 ? (Randomizer.nextInt(100) < 90 ? -RARE : -EPIC)
                            : type == 0 ? ((Randomizer.nextInt(100) < 5) && (getState() != LEGENDARY && getState() > 17) ? -UNIQUE : ((Randomizer.nextInt(100) < 10) && (getState() != UNIQUE && getState() != LEGENDARY) ? -EPIC : (getState() != EPIC && getState() != UNIQUE && getState() != LEGENDARY) ? -RARE : -getState()))
                                    : type == 1 ? ((Randomizer.nextInt(100) < 9) && (getState() != LEGENDARY && getState() > 17) ? -UNIQUE : ((Randomizer.nextInt(100) < 17) && (getState() != UNIQUE && getState() != LEGENDARY) ? -EPIC : (getState() != EPIC && getState() != UNIQUE && getState() != LEGENDARY) ? -RARE : -getState()))
                                            : type == 4 ? ((Randomizer.nextInt(100) < 2) && (getState() > 18) ? -LEGENDARY : ((Randomizer.nextInt(100) < 6) && (getState() != LEGENDARY && getState() > 17) ? -UNIQUE : ((Randomizer.nextInt(100) < 12) && (getState() != UNIQUE && getState() != LEGENDARY) ? -EPIC : (getState() != EPIC && getState() != UNIQUE && getState() != LEGENDARY) ? -RARE : -getState())))
                                                    : type == 5 ? ((Randomizer.nextInt(100) < 2) && (getState() > 18) ? -LEGENDARY : ((Randomizer.nextInt(100) < 10) && (getState() != LEGENDARY && getState() > 17) ? -UNIQUE : ((Randomizer.nextInt(100) < 15) && (getState() != UNIQUE && getState() != LEGENDARY) ? -EPIC : (getState() != EPIC && getState() != UNIQUE && getState() != LEGENDARY) ? -RARE : -getState()))) : -getState();
            setPotentialByLine(0, rank); // 첫 번째 잠재 능력 : rank에 의하여, 잠재 능력 등급이 설정 된다.
            setPotentialByLine(1, getPotentialByLine(0)); // 두 번째 잠재 능력 : 첫 번째 잠재 능력 등급에 대한 값이 설정 된다.
            if (!bonus && getPotentialByLine(2) > 0 || (type == 3 && getPotentialByLine(2) == 0 && (Randomizer.nextInt(100) < 50))) {
                setPotentialByLine(2, getPotentialByLine(0)); // 세 번째 잠재 능력 : 첫 번째 잠재 능력 등급에 대한 값이 50% 확률로 설정 된다.
            }
        }
    }
    
    public void revealHiddenPotential() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reqLevel = ii.getReqLevel(getItemId()) / 10;
        final List<List<StructItemOption>> pots = new LinkedList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().values());
        if (reqLevel > 18) {
            reqLevel = 19;
        }
        if (getBonusPotentialByLine(0) < 0) { //hidden bonus
            //TODO make this not as copy-pasty (not high prio)
            int BonusnewState = -getBonusPotentialByLine(0);
            if (BonusnewState > Equip.LEGENDARY) {
                BonusnewState = Equip.LEGENDARY;
            } else if (BonusnewState < Equip.RARE) {
                BonusnewState = Equip.RARE;
            }
            while (getStateByPotential(getBonusPotential()) != BonusnewState) {
                for (int i = 0; i < getBonusPotential().length; i++) {
                    if (getBonusPotentialByLine(i) == 0) {
                        break;
                    }
                    boolean Bonusrewarded = false;
                    while (!Bonusrewarded) {
                        StructItemOption Bonuspot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (Bonuspot != null && Bonuspot.reqLevel <= reqLevel && GameConstants.optionTypeFits(Bonuspot.optionType, getItemId())
                                && GameConstants.potentialIDFits(Bonuspot.opID, BonusnewState, i) && GameConstants.isBonusPot(Bonuspot.opID)) {
                            setBonusPotentialByLine(i, Bonuspot.opID);
                            Bonusrewarded = true;
                        }
                    }
                }
            }
        } else if (getPotentialByLine(0) < 0) { //hidden main
            int newState = -getPotentialByLine(0);
            if (newState > Equip.LEGENDARY) {
                newState = Equip.LEGENDARY;
            } else if (newState < Equip.RARE) {
                newState = Equip.RARE;
            }
            while (getStateByPotential(getPotential()) != newState) {
                for (int i = 0; i < getPotential().length; i++) {
                    if (getPotentialByLine(i) == 0) {
                        break;
                    }
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null) {
                            int potLevel = pot.reqLevel;
                            if (potLevel > 18) {
                                potLevel = 19;
                            }
                            if (potLevel <= reqLevel && GameConstants.optionTypeFits(pot.optionType, getItemId())
                                    && GameConstants.potentialIDFits(pot.opID, newState, i) && !GameConstants.isBonusPot(pot.opID)) {
                                setPotentialByLine(i, pot.opID);
                                rewarded = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public List<EquipStat> getStats() {
        return stats;
    }

    public List<EquipSpecialStat> getSpecialStats() {
        return specialStats;
    }

    public void resetPotential_Fuse(boolean half) { //maker skill - equip first receive
        //no legendary, 0.16% chance unique, 4% chance epic, else rare
        int potentialState = -RARE;
        if (Randomizer.nextInt(100) < 4) {
            potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
        }
        setPotentialByLine(0, potentialState);
        setPotentialByLine(1, potentialState);
        setPotentialByLine(2, (Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));

    }

    public void resetBonusPotential_Fuse(boolean half, int potentialState) { //maker skill - equip first receive
        //no legendary, 0.16% chance unique, 4% chance epic, else rare
        potentialState = -potentialState;
        if (Randomizer.nextInt(100) < 4) {
            potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
        }
        setBonusPotentialByLine(0, potentialState);
        setBonusPotentialByLine(1, (Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
        setBonusPotentialByLine(2, (Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
    }

    /**
     * Resets the current potential. 10% chance on 3rd line if equip currently
     * does not have a 3rd line.
     */
    public void resetPotential() {
        final int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -UNIQUE : -EPIC) : -RARE;
        resetPotentialWithRank(rank, 10);
    }

    /**
     * Sets the current potential with a given rank and chance on a third line.
     *
     * @param rank
     * @param chanceOnThirdLine
     */
    public void resetPotentialWithRank(int rank, int chanceOnThirdLine) {
        setPotentialByLine(0, rank);
        setPotentialByLine(1, rank);
        if (getPotentialByLine(2) == 0) {
            setPotentialByLine(2, (Randomizer.nextInt(100) < chanceOnThirdLine) ? rank : 0);
        } else {
            setPotentialByLine(2, rank);
        }
    }

    /**
     * Resets the bonus potential. 0.16% unique, 0.4% epic, else rare. No chance
     * on 2nd/3rd lines.
     */
    public void resetBonusPotential() {
        final int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -UNIQUE : -EPIC) : -RARE;
        resetBonusPotentialWithRank(rank, false);
    }

    public void resetBonusPotentialWithRank(int rank, boolean threeLines) {
        for (int i = 0; i < getBonusPotential().length; i++) {
            if (getBonusPotentialByLine(i) != 0 || threeLines || i == 0) {
                //first line is always set
                setBonusPotentialByLine(i, -rank);
            } else {
                setBonusPotentialByLine(i, 0);
            }
        }
    }

    /*  public void renewPotential(GameConstants.Cubes cube){
     int miracleRate = 1;
     //   if(EventConstants.DoubleMiracleTime){
     //        miracleRate *= 2;
     //    }
     boolean bonus = cube == GameConstants.Cubes.BONUS;
     int[] pots = bonus ? getBonusPotential() : getPotential();
     int rank = getStateByPotential(pots);
     int maxState = GameConstants.getMaxAvailableState(cube);
     if(rank < RARE || rank > maxState){
     return;
     }else if(rank != maxState && Randomizer.nextInt(100) < GameConstants.getRankUpChanceByCube(cube) * miracleRate){
     rank += 1; //rank up
     }
     if(!bonus){
     resetPotentialWithRank(rank, GameConstants.get3rdLineUpChanceByCube(cube));
     }else{
     resetBonusPotentialWithRank(rank, false);
     }
     }

     public void renewPotential(int type) { // 0 = normal miracle cube, 1 = premium, 2 = epic pot scroll, 3 = super
     // 큐브설정  (type == 3 ? 20 : 19) -> (type == 3 ? 19 : 19) 4가 확률인레후
     final int rank = type == 2 ? -18 : (Randomizer.nextInt(100) < 20 && getState() != (type == 3 ? 19 : 19) ? -(getState() + 1) : -(getState())); // 4 % chance to up 1 tier
     setPotential1(rank);
     if (getPotential3() > 0) {
     setPotential2(rank); // put back old 3rd line
     } else {
     switch (type) {
     case 1: // premium-> suppose to be 25%
     setPotential2(Randomizer.nextInt(10) == 0 ? rank : 0); //1/10 chance of 3 line
     break;
     case 2: // epic pot
     setPotential2(Randomizer.nextInt(10) <= 1 ? rank : 0); //2/10 chance of 3 line
     break;
     case 3: // super
     setPotential2(Randomizer.nextInt(10) <= 2 ? rank : 0); //3/10 chance of 3 line
     break;
     default:
     setPotential2(0);
     break;
     }
     }
     if (type == 3) { // super
     setPotential3(Randomizer.nextInt(100) <= 2 ? rank : 0); // 3/100 to get 4 lines
     } else { // premium cannot get 3 lines.
     setPotential3(0); //just set it theoretically
     }
     }*/
    public void renewEditionalPotential(int type) { // 0 = normal miracle cube, 1 = premium, 2 = epic pot scroll, 3 = super
        int rank = type == 2 ? -18 : (Randomizer.nextInt(100) < 4 && getState() != (type == 3 ? 20 : 19) ? -(getState() + 1) : -(getState())); // 4 % chance to up 1 tier
        rank = rank - 48;
        setPotential4(rank);
        if (getPotential3() > 0) {
            setPotential5(rank); // put back old 3rd line
        } else {
            switch (type) {
                case 1: // premium-> suppose to be 25%
                    setPotential5(Randomizer.nextInt(10) == 0 ? rank : 0); //1/10 chance of 3 line
                    break;
                case 2: // epic pot
                    setPotential5(Randomizer.nextInt(10) <= 1 ? rank : 0); //2/10 chance of 3 line
                    break;
                case 3: // super
                    setPotential5(Randomizer.nextInt(10) <= 2 ? rank : 0); //3/10 chance of 3 line
                    break;
                default:
                    setPotential5(0);
                    break;
            }
        }
        if (type == 3) { // super
            setPotential6(Randomizer.nextInt(100) <= 2 ? rank : 0); // 3/100 to get 4 lines
        } else { // premium cannot get 3 lines.
            setPotential6(0); //just set it theoretically
        }
    }

    public int getIncSkill() {
        return incSkill;
    }

    public void setIncSkill(int inc) {
        this.incSkill = inc;
    }

    public short getCharmEXP() {
        return charmExp;
    }

    public short getPVPDamage() {
        return pvpDamage;
    }

    public void setCharmEXP(short s) {
        this.charmExp = s;
    }

    public void setPVPDamage(short p) {
        this.pvpDamage = p;
    }

    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(getItemId()) || getUniqueId() <= 0) {
            return null;
        }
        if (ring == null) {
            ring = MapleRing.loadFromDb(getUniqueId(), getPosition() < 0);
        }
        return ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    //Doesn't change rank or lines, just reset existing ones.
    public void shufflePotential() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        //Extra check: Level 200+ weapons hotfix
        final int potentialLevel = (ii.getReqLevel(getItemId()) / 10) >= 20 ? 19 : (ii.getReqLevel(getItemId()) / 10);
        final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
        int lines = 1;

        if (getPotential2() != 0) {
            lines++;
        }
        if (getPotential3() != 0) {
            lines++;
        }
        if (getPotential4() != 0) {
            lines++;
        }
        if (getPotential5() != 0) {
            lines++;
        }

        for (int i = 0; i < lines; i++) {
            boolean rewarded = false;
            while (!rewarded) {
                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(potentialLevel);
                if (pot != null && pot.reqLevel / 10 <= potentialLevel && GameConstants.optionTypeFits(pot.optionType, getItemId()) && GameConstants.potentialIDFits(pot.opID, getState(), i)) {
                    if (i == 0) {
                        setPotential1(pot.opID);
                    } else if (i == 1) {
                        setPotential2(pot.opID);
                    } else if (i == 2) {
                        setPotential3(pot.opID);
                    } else if (i == 3) {
                        setPotential4(pot.opID);
                    } else if (i == 4) {
                        setPotential5(pot.opID);
                    }
                    rewarded = true;
                }
            }
        }
    }

    public void shuffleSinglePotential(int line) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int potentialLevel = (ii.getReqLevel(getItemId()) / 10) >= 20 ? 19 : (ii.getReqLevel(getItemId()) / 10);
        final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());

        boolean rewarded = false;
        while (!rewarded) {
            StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(potentialLevel);
            if (pot != null && pot.reqLevel / 10 <= potentialLevel && GameConstants.optionTypeFits(pot.optionType, getItemId()) && GameConstants.potentialIDFits(pot.opID, getState(), line - 1)) {
                if (line == 1) {
                    setPotential1(pot.opID);
                } else if (line == 2) {
                    setPotential2(pot.opID);
                } else if (line == 3) {
                    setPotential3(pot.opID);
                } else if (line == 4) {
                    setPotential4(pot.opID);
                } else if (line == 5) {
                    setPotential5(pot.opID);
                }
                rewarded = true;
            }
        }
    }

    public static Equip calculateEquipStats(Equip eq) {
        eq.getStats().clear();
        eq.getSpecialStats().clear();
        if (eq.getUpgradeSlots() > 0) {
            eq.getStats().add(EquipStat.SLOTS);
        }
        if (eq.getLevel() > 0) {
            eq.getStats().add(EquipStat.LEVEL);
        }
        if (eq.getStr() > 0) {
            eq.getStats().add(EquipStat.STR);
        }
        if (eq.getDex() > 0) {
            eq.getStats().add(EquipStat.DEX);
        }
        if (eq.getInt() > 0) {
            eq.getStats().add(EquipStat.INT);
        }
        if (eq.getLuk() > 0) {
            eq.getStats().add(EquipStat.LUK);
        }
        if (eq.getHp() > 0) {
            eq.getStats().add(EquipStat.MHP);
        }
        if (eq.getMp() > 0) {
            eq.getStats().add(EquipStat.MMP);
        }
        if (eq.getWatk() > 0) {
            eq.getStats().add(EquipStat.WATK);
        }
        if (eq.getMatk() > 0) {
            eq.getStats().add(EquipStat.MATK);
        }
        if (eq.getWdef() > 0) {
            eq.getStats().add(EquipStat.WDEF);
        }
        if (eq.getMdef() > 0) {
            eq.getStats().add(EquipStat.MDEF);
        }
        if (eq.getAcc() > 0) {
            eq.getStats().add(EquipStat.ACC);
        }
        if (eq.getAvoid() > 0) {
            eq.getStats().add(EquipStat.AVOID);
        }
        if (eq.getHands() > 0) {
            eq.getStats().add(EquipStat.HANDS);
        }
        if (eq.getSpeed() > 0) {
            eq.getStats().add(EquipStat.SPEED);
        }
        if (eq.getJump() > 0) {
            eq.getStats().add(EquipStat.JUMP);
        }
        if (eq.getFlag() > 0) {
            eq.getStats().add(EquipStat.FLAG);
        }
        if (eq.getIncSkill() > 0) {
            eq.getStats().add(EquipStat.INC_SKILL);
        }
        if (eq.getEquipLevel() > 0) {
            eq.getStats().add(EquipStat.ITEM_LEVEL);
        }
        if (eq.getItemEXP() > 0) {
            eq.getStats().add(EquipStat.ITEM_EXP);
        }
        if (eq.getDurability() > -1) {
            eq.getStats().add(EquipStat.DURABILITY);
        }
        if (eq.getViciousHammer() > 0) {
            eq.getStats().add(EquipStat.VICIOUS_HAMMER);
        }
        if (eq.getPVPDamage() > 0) {
            eq.getStats().add(EquipStat.PVP_DAMAGE);
        }
        if (eq.getEnhanctBuff() > 0) {
            eq.getStats().add(EquipStat.ENHANCT_BUFF);
        }
        if (eq.getReqLevel() > 0) {
            eq.getStats().add(EquipStat.REQUIRED_LEVEL);
        }
        if (eq.getYggdrasilWisdom() > 0) {
            eq.getStats().add(EquipStat.YGGDRASIL_WISDOM);
        }
        if (eq.getFinalStrike()) {
            eq.getStats().add(EquipStat.FINAL_STRIKE);
        }
        if (eq.getBossDamage() > 0) {
            eq.getStats().add(EquipStat.BOSS_DAMAGE);
        }
        if (eq.getIgnorePDR() > 0) {
            eq.getStats().add(EquipStat.IGNORE_PDR);
        }
        //SPECIAL STATS:
        if (eq.getTotalDamage() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.TOTAL_DAMAGE);
        }
        if (eq.getAllStat() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.ALL_STAT);
        }
        eq.getSpecialStats().add(EquipSpecialStat.KARMA_COUNT); //no count = -1
        return (Equip) eq.copy();
    }

    /*public int getPotential(int line){
     switch(line){
     case 1:
     return potential1;
     case 2:
     return potential2;
     case 3:
     return potential3;
     case 4:
     return potential4;
     case 5:
     return potential5;
     }
     return 0; //um?
     }*/

 /*public void setPotential(int line, int en){
     switch(line){
     case 1:
     this.potential1 = en;
     break;
     case 2:
     this.potential2 = en;
     break;
     case 3:
     this.potential3 = en;
     break;
     case 4:
     this.potential4 = en;
     break;
     case 5:
     this.potential5 = en;
     break;
     }
     }*/
    public MapleAndroid getAndroid() {
        if (getItemId() / 10000 != 166 || getUniqueId() <= 0) {
            return null;
        }
        if (android == null) {
            android = MapleAndroid.loadFromDb(getItemId(), getUniqueId());
        }
        return android;
    }

    public void setAndroid(MapleAndroid ring) {
        this.android = ring;
    }

    public void addStr(short str) {
        if (this.str + str < 0) {
            this.str = 0;
        }
        this.str += str;
    }

    public void addDex(short dex) {
        if (this.dex + dex < 0) {
            this.dex = 0;
        }
        this.dex += dex;
    }

    public void addInt(short dex) {
        if (this._int + dex < 0) {
            this._int = 0;
        }
        this._int += dex;
    }

    public void addLuk(short dex) {
        if (this.luk + dex < 0) {
            this.luk = 0;
        }
        this.luk += dex;
    }

    public void addHp(short dex) {
        if (this.hp + dex < 0) {
            this.hp = 0;
        }
        this.hp += dex;
    }

    public void addMp(short dex) {
        if (this.mp + dex < 0) {
            this.mp = 0;
        }
        this.mp += dex;
    }

    public void addWatk(short watk) {
        if (this.watk + watk < 0) {
            this.watk = 0;
        }
        this.watk += watk;
    }

    public void addMatk(short watk) {
        if (this.matk + watk < 0) {
            this.matk = 0;
        }
        this.matk += watk;
    }

    public void addWdef(short wdef) {
        if (wdef + this.wdef < 0) {
            this.wdef = 0;
        }
        this.wdef += wdef;
    }

    public void addMdef(short mdef) {
        if (mdef + this.mdef < 0) {
            this.mdef = 0;
        }
        this.mdef += mdef;
    }

    public void addAcc(short acc) {
        if (acc + this.acc < 0) {
            this.acc = 0;
        }
        this.acc += acc;
    }

    public void addSpeed(short speed) {
        if (speed + this.speed < 0) {
            this.speed = 0;
        }
        this.speed += speed;
    }

    public void addJump(short jump) {
        if (jump + this.jump < 0) {
            this.jump = 0;
        }
        this.jump += jump;
    }

    public void addUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots += upgradeSlots;
    }

    public void setPotentialOpen(Equip eq, int jgys) {
        final List<List<StructItemOption>> pots = new LinkedList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().values());
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reqEqpLevel = (ii.getReqLevel(eq.getItemId()) / 10);
        if (reqEqpLevel > 19) {
            reqEqpLevel = 19;
        }
        final int lines = Randomizer.rand(2, 3);
        for (int i = 0; i < lines; i++) {
            boolean rewarded = false;
            while (!rewarded) {
                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqEqpLevel);
                if (pot != null && GameConstants.optionTypeFits(pot.optionType, eq.getItemId()) && GameConstants.potentialIDFits(pot.opID, jgys, i)) {
                    eq.setPotentialByLine(i, pot.opID);
                    rewarded = true;
                }
            }
        }
    }
    
    public short getHpR() {
        return hpR;
    }

    public short getMpR() {
        return mpR;
    }
    
    public void setHpR(short hpR) {
        this.hpR = hpR;
    }

    public void setMpR(short mpR) {
        this.mpR = mpR;
    }
}
