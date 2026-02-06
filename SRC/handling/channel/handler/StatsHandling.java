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
package handling.channel.handler;

import constants.GameConstants;

import client.Skill;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleStat;
import client.PlayerStats;
import client.SkillFactory;
import java.util.EnumMap;
import java.util.Map;
import server.Randomizer;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class StatsHandling {

    public static final void DistributeAP(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        Map<MapleStat, Integer> statupdate = new EnumMap<MapleStat, Integer>(MapleStat.class);
        c.getSession().write(CWvsContext.OnPlayerStatChanged(statupdate, true, chr));
        chr.updateTick(slea.readInt());

        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        if (chr.getRemainingAp() > 0) {
            switch (slea.readInt()) {
                case 64: // Str
                    if (stat.getStr() >= Short.MAX_VALUE) {
                        return;
                    }
                    stat.setStr((short) (stat.getStr() + 1), chr);
                    statupdate.put(MapleStat.Str, (int) stat.getStr());
                    break;
                case 128: // Dex
                    if (stat.getDex() >= Short.MAX_VALUE) {
                        return;
                    }
                    stat.setDex((short) (stat.getDex() + 1), chr);
                    statupdate.put(MapleStat.Dex, (int) stat.getDex());
                    break;
                case 256: // Int
                    if (stat.getInt() >= Short.MAX_VALUE) {
                        return;
                    }
                    stat.setInt((short) (stat.getInt() + 1), chr);
                    statupdate.put(MapleStat.Int, (int) stat.getInt());
                    break;
                case 512: // Luk
                    if (stat.getLuk() >= Short.MAX_VALUE) {
                        return;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1), chr);
                    statupdate.put(MapleStat.Luk, (int) stat.getLuk());
                    break;
                case 2048: // HP
                    int maxhp = stat.getMaxHp();
                    if (maxhp >= Integer.MAX_VALUE) {
                        return;
                    }
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxhp += Randomizer.rand(8, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212) || (job >= 1100 && job <= 1112) || (job >= 3100 && job <= 3112)) { // Warrior
                        maxhp += Randomizer.rand(36, 42);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) { // Magician
                        maxhp += Randomizer.rand(10, 20);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 2300 && job <= 2312)) { // Bowman
                        maxhp += Randomizer.rand(16, 20);
                    } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) {
                        maxhp += Randomizer.rand(28, 32);
                    } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) { // Pirate
                        maxhp += Randomizer.rand(18, 22);
                    } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                        maxhp += Randomizer.rand(15, 21);
                    } else if (job >= 2000 && job <= 2112) { // Aran
                        maxhp += Randomizer.rand(38, 42);
                    } else if (GameConstants.isDemonAvenger(job)) { // 어벤져
                        maxhp += 30; // 데몬어벤져 30
                    } else { // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = Math.min(500000, Math.abs(maxhp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxHp(maxhp, chr);
                    statupdate.put(MapleStat.MaxHP, (int) maxhp);
                    break;
                case 8192: // MP
                    int maxmp = stat.getMaxMp();
                    if (chr.getHpApUsed() >= 10000 || stat.getMaxMp() >= Integer.MAX_VALUE) {
                        return;
                    }
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (job >= 3100 && job <= 3112) { // Warrior
                        return;
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212) || (job >= 1200 && job <= 1212)) { // Magician
                        maxmp += Randomizer.rand(38, 40);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 532) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 2300 && job <= 2312)) { // Bowman
                        maxmp += Randomizer.rand(10, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112) || (job >= 2000 && job <= 2112)) { // Soul Master
                        maxmp += Randomizer.rand(6, 9);
                    } else { // GameMaster
                        maxmp += Randomizer.rand(50, 100);
                    }
                    maxmp = Math.min(99999, Math.abs(maxmp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxMp(maxmp, chr);
                    statupdate.put(MapleStat.MaxMP, (int) maxmp);
                    break;
                default:
                    c.getSession().write(CWvsContext.enableActions());
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
            statupdate.put(MapleStat.AP, (int) chr.getRemainingAp());
            c.getSession().write(CWvsContext.OnPlayerStatChanged(statupdate, true, chr));
        }
    }

    public static final void DistributeSP(final int skillid, final MapleClient c, final MapleCharacter chr) {
        boolean isBeginnerSkill = false;
        final int remainingSp;
        if (GameConstants.isBeginnerJob(skillid / 10000) && (skillid % 10000 == 1000 || skillid % 10000 == 1001 || skillid % 10000 == 1002 || skillid % 10000 == 2)) {
            final boolean resistance = skillid / 10000 == 3000 || skillid / 10000 == 3001 || skillid / 10000 == 3101;
            final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + 1000));
            final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + 1001));
            final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + (resistance ? 2 : 1002)));
            remainingSp = Math.min((chr.getLevel() - 1), resistance ? 9 : 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
            isBeginnerSkill = true;
        } else if (GameConstants.isBeginnerJob(skillid / 10000)) {
            return;
        } else {
            remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillid));
        }
        final Skill skill = SkillFactory.getSkill(skillid);
        for (Pair<Integer, Byte> ski : skill.getRequiredSkills()) {
            if (chr.getSkillLevel(SkillFactory.getSkill(ski.left)) < ski.right) {
                return;
            }
        }
        final int maxlevel = skill.isSkillNeedMasterLevel() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);
        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if ((skill.isSkillNeedMasterLevel() && chr.getMasterLevel(skill) == 0) || (!skill.isSkillNeedMasterLevel() && maxlevel < 10 && !isBeginnerSkill && chr.getMasterLevel(skill) <= 0)) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
        }
        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && skill.canBeLearnedBy(chr.getJob())) {
            if (!isBeginnerSkill) {
                final int skillbook = GameConstants.getSkillBookForSkill(skillid);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            chr.updateSingleStat(MapleStat.SP, 0);
            chr.changeSingleSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            chr.setLinkageSkill();
            chr.checkSelfRecovery();
            chr.saveToDB(false, false);
        } else {
            chr.dropMessage(1, "해당 차수의 스킬 포인트가 부족합니다.");
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void DistributeHyperSP(final int skillid, final MapleClient c, final MapleCharacter chr) {
        final Skill skill = SkillFactory.getSkill(skillid);
        final int curLevel = chr.getSkillLevel(skill);
        chr.changeSingleSkillLevel(skill, (byte) (curLevel + 1), (byte) 1);
    }

    public static final void AutoAssignAP(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {

        chr.updateTick(slea.readInt());
        final int count = slea.readInt();
        //   if (slea.available() < 16) {
        //       return;
        //   }

        final int PrimaryStat = slea.readInt();
        final int amount = slea.readInt();
        // final int SecondaryStat = slea.readInt();
        // final int amount2 = slea.readInt();
        final int SecondaryStat = count == 2 ? slea.readInt() : 0;
        final int amount2 = count == 2 ? slea.readInt() : 0;
        if (amount < 0 || amount2 < 0) {

            return;
        }

        final PlayerStats playerst = chr.getStat();

        Map<MapleStat, Integer> statupdate = new EnumMap<MapleStat, Integer>(MapleStat.class);
        c.getSession().write(CWvsContext.OnPlayerStatChanged(statupdate, true, chr));

        if (chr.getRemainingAp() == amount + amount2 || GameConstants.isXenon(chr.getJob())) {
            switch (PrimaryStat) {
                case 64: // Str
                    if (playerst.getStr() + amount > 999) {

                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount), chr);
                    statupdate.put(MapleStat.Str, (int) playerst.getStr());
                    break;
                case 128: // Dex
                    if (playerst.getDex() + amount > 999) {

                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount), chr);
                    statupdate.put(MapleStat.Dex, (int) playerst.getDex());
                    break;
                case 256: // Int
                    if (playerst.getInt() + amount > 999) {

                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount), chr);
                    statupdate.put(MapleStat.Int, (int) playerst.getInt());
                    break;
                case 512: // Luk
                    if (playerst.getLuk() + amount > 999) {

                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount), chr);
                    statupdate.put(MapleStat.Luk, (int) playerst.getLuk());
                    break;
                case 2048: //Max Hp
                    if (playerst.getMaxHp() + (amount * 30) > 500000) {
                        return;
                    }
                    playerst.setMaxHp(playerst.getMaxHp() + (amount * 30), chr);
                    statupdate.put(MapleStat.MaxHP, (int) playerst.getMaxHp());
                    break;
                default:

                    c.getSession().write(CWvsContext.enableActions());
                    return;
            }
            switch (SecondaryStat) {
                case 64: // Str
                    if (playerst.getStr() + amount2 > Short.MAX_VALUE) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2), chr);
                    statupdate.put(MapleStat.Str, (int) playerst.getStr());
                    break;
                case 128: // Dex
                    if (playerst.getDex() + amount2 > Short.MAX_VALUE) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2), chr);
                    statupdate.put(MapleStat.Dex, (int) playerst.getDex());
                    break;
                case 256: // Int
                    if (playerst.getInt() + amount2 > Short.MAX_VALUE) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2), chr);
                    statupdate.put(MapleStat.Int, (int) playerst.getInt());
                    break;
                case 512: // Luk
                    if (playerst.getLuk() + amount2 > Short.MAX_VALUE) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2), chr);
                    statupdate.put(MapleStat.Luk, (int) playerst.getLuk());
                    break;
                case 2048: //Max Hp
                    if (playerst.getMaxHp() + (amount * 30) > Integer.MAX_VALUE) {
                        return;
                    }
                    playerst.setMaxHp(playerst.getMaxHp() + (amount * 30), chr);
                    statupdate.put(MapleStat.MaxHP, (int) playerst.getMaxHp());
                    break;
                default:
                    if (!GameConstants.isXenon(chr.getJob())) {
                        //       System.out.print("처리안됨");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
            }
            //    System.out.print("333들어감");
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            //    System.out.print("444들어감");
            statupdate.put(MapleStat.AP, (int) chr.getRemainingAp());
            c.getSession().write(CWvsContext.OnPlayerStatChanged(statupdate, true, chr));
        }
    }
}
