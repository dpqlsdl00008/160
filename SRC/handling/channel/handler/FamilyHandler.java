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

import client.MapleCharacterUtil;
import client.MapleCharacter;
import client.MapleClient;
import handling.channel.ChannelServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Timer;
import server.maps.FieldLimitType;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.FamilyPacket;

public class FamilyHandler {

    public static final void RequestFamily(final LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (chr != null) {
            c.getSession().write(FamilyPacket.getFamilyPedigree(chr));
        }
    }

    public static final void OpenFamily(final LittleEndianAccessor slea, MapleClient c) {
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
    }

    public static final void UseFamily(final LittleEndianAccessor slea, MapleClient c) {
        int type = slea.readInt();
        if (MapleFamilyBuff.values().length <= type) {
            return;
        }
        MapleFamilyBuff entry = MapleFamilyBuff.values()[type];
        boolean success = c.getPlayer().getFamilyId() > 0 && c.getPlayer().canUseFamilyBuff(entry) && c.getPlayer().getCurrentRep() > entry.rep;
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (entry) {
            case Teleport:
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "대상이 이동 불가능한 지역에 있습니다.");
                    success = false;
                } else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "대상을 발견할 수 없습니다.");
                    success = false;
                } else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId() && !victim.isInBlockedMap()) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                } else {
                    c.getPlayer().dropMessage(5, "그 곳으로는 이동할 수 없습니다.");
                    success = false;
                }
                break;
            case Summon:
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "대상을 소환할 수 없습니다.");
                } else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "대상을 발견할 수 없습니다.");
                } else if (victim.getTeleportName().length() > 0) {
                    c.getPlayer().dropMessage(1, "현재 다른 사람의 소환 요청을 처리중 입니다.");
                } else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId() && !victim.isInBlockedMap()) {
                    victim.getClient().getSession().write(FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
                    victim.setTeleportName(c.getPlayer().getName());
                } else {
                    c.getPlayer().dropMessage(5, "소환에 실패하였습니다.");
                }
                return;
            case Drop_12_15:
                c.getPlayer().setFamilyBuffDayByDay(2);
                c.getPlayer().setFamilyBuffDropEffect(20);
                c.getPlayer().setFamilyBuffDropDuration(System.currentTimeMillis() + (entry.duration * 60000));
                final String dDuration = StringUtil.getReadableMillis(System.currentTimeMillis(), c.getPlayer().getFamilyBuffDropDuration());
                c.getPlayer().dropMessage(5, "나만의 드롭율 1.2배를 사용하였습니다. (잔여 시간 : " + dDuration + ")");
                c.sendPacket(FamilyPacket.familyBuff((entry.type == 5 ? 2 : entry.type == 6 ? 3 : entry.type), type, entry.effect, entry.duration * 60000));
                break;
            case EXP_12_15:
                c.getPlayer().setFamilyBuffDayByDay(3);
                c.getPlayer().setFamilyBuffExpEffect(20);
                c.getPlayer().setFamilyBuffExpDuration(System.currentTimeMillis() + (entry.duration * 60000));
                final String eDuration = StringUtil.getReadableMillis(System.currentTimeMillis(), c.getPlayer().getFamilyBuffExpDuration());
                c.getPlayer().dropMessage(5, "나만의 경험치 1.2배를 사용하였습니다. (잔여 시간 : " + eDuration + ")");
                c.sendPacket(FamilyPacket.familyBuff((entry.type == 5 ? 2 : entry.type == 6 ? 3 : entry.type), type, entry.effect, entry.duration * 60000));
                break;
            case Bonding:
                final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
                List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
                if (chrs.size() < 7 && !c.getPlayer().isGM()) {
                    success = false;
                } else {
                    for (MapleFamilyCharacter chrz : chrs) {
                        int chr = World.Find.findChannel(chrz.getId());
                        if (chr == -1) {
                            continue;
                        }
                        MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
                        c.getPlayer().setFamilyBuffDayByDay(4);
                        chrr.setFamilyBuffDropEffect(50);
                        chrr.setFamilyBuffDropDuration(System.currentTimeMillis() + (entry.duration * 60000));
                        chrr.setFamilyBuffExpEffect(50);
                        chrr.setFamilyBuffExpDuration(System.currentTimeMillis() + (entry.duration * 60000));
                        chrr.getClient().getSession().write(FamilyPacket.familyBuff(entry.type, type, entry.effect, entry.duration * 60000));
                    }
                }
                break;
        }
        if (success) { //again
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
            c.sendPacket(FamilyPacket.changeRep(-entry.rep, c.getPlayer().getName()));
            c.getPlayer().useFamilyBuff(entry);
            c.sendPacket(FamilyPacket.getFamilyInfo(c.getPlayer()));
        } else {
            c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
        }
    }

    public static final void FamilyOperation(final LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (addChr == null) {
            c.getPlayer().dropMessage(1, "대상을 찾을 수 없습니다. 대상과 같은 채널에 있어야 합니다.");
        } else if (addChr.getFamilyId() == c.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
            c.getPlayer().dropMessage(1, "이미 같은 패밀리에 소속되어 있습니다.");
        } else if (addChr.getMapId() != c.getPlayer().getMapId()) {
            c.getPlayer().dropMessage(1, "주니어로 추가할 사람과 같은 지역에 있어야 합니다.");
        } else if (addChr.getSeniorId() != 0) {
            c.getPlayer().dropMessage(1, "이미 다른 사람의 주니어로 가입되어 있습니다.");
        } else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
            c.getPlayer().dropMessage(1, "주니어가 될 대상은 자신보다 레벨이 낮아야 합니다.");
        } else if (addChr.getLevel() < c.getPlayer().getLevel() - 20) {
            c.getPlayer().dropMessage(1, "주니어와 레벨 차이는 20 미만이어야 합니다.");
            //} else if (c.getPlayer().getFamilyId() != 0 && c.getPlayer().getFamily().getGens() >= 1000) {
            //	c.getPlayer().dropMessage(5, "Your family cannot extend more than 1000 generations from above and below.");
        } else if (addChr.getLevel() < 10) {
            c.getPlayer().dropMessage(1, "레벨 10 이상의 캐릭터를 주니어로 등록할 수 있습니다.");
        } else if (c.getPlayer().getJunior1() > 0 && c.getPlayer().getJunior2() > 0) {
            c.getPlayer().dropMessage(1, "이미 두명의 주니어가 등록되어 있습니다.");
        } else if (c.getPlayer().isGM() || !addChr.isGM()) {
            addChr.getClient().getSession().write(FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void FamilyPrecept(final LittleEndianAccessor slea, MapleClient c) {
        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        if (fam == null || fam.getLeaderId() != c.getPlayer().getId()) {
            return;
        }
        fam.setNotice(slea.readMapleAsciiString());
    }

    public static final void FamilySummon(final LittleEndianAccessor slea, MapleClient c) {
        MapleFamilyBuff cost = MapleFamilyBuff.Summon;
        MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (c.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(tt.getMap().getFieldLimit())
                && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && tt.canUseFamilyBuff(cost)
                && c.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep && !c.getPlayer().isInBlockedMap() && !tt.isInBlockedMap()) {
            //whew lots of checks
            boolean accepted = slea.readByte() > 0;
            if (accepted) {
                c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.getClient().getSession().write(FamilyPacket.changeRep(-cost.rep, tt.getName()));
                tt.useFamilyBuff(cost);
            } else {
                tt.dropMessage(5, c.getPlayer().getName() + "님께서 소환을 거절하셨습니다.");
            }
        } else {
            c.getPlayer().dropMessage(5, "소환에 실패하였습니다.");
        }
        c.getPlayer().setTeleportName("");
    }

    public static final void DeleteJunior(final LittleEndianAccessor slea, MapleClient c) {
        int juniorid = slea.readInt();
        if (c.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || (c.getPlayer().getJunior1() != juniorid && c.getPlayer().getJunior2() != juniorid)) {
            return;
        }
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter other = fam.getMFC(juniorid);
        if (other == null) {
            return;
        }
        final MapleFamilyCharacter oth = c.getPlayer().getMFC();
        boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        } else {
            oth.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);
        if (!other.isOnline()) {
            MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getTodayRep(), other.getId());
        }
        c.getSession().write(FamilyPacket.getFamilyData());
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
        c.getPlayer().familyUpdate();

        if (!fam.splitFamily(juniorid, other)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }

        MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " 님이 당신과 결별하였습니다. 패밀리 관계가 끊어집니다.", 0);

        MapleCharacter otherChr = ChannelServer.getInstance(other.getChannel()).getPlayerStorage().getCharacterById(other.getId());
        if (otherChr != null) {
            otherChr.saveFamilyStatus();
            otherChr.familyUpdate();
            otherChr.getClient().getSession().write(FamilyPacket.getFamilyData());
            otherChr.getClient().getSession().write(FamilyPacket.getFamilyInfo(otherChr));
            otherChr.getClient().getSession().write(CWvsContext.enableActions());
        }

        List<MapleFamilyCharacter> check = oth.getAllJuniors(fam);
        for (MapleFamilyCharacter allMembers : check) {
            int ch = World.Find.findChannel(allMembers.getName());
            if (ch > 0) {
                MapleCharacter mem = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(allMembers.getId());
                if (mem != null) {
                    if (mem != otherChr && mem != c.getPlayer()) {
                        mem.saveFamilyStatus();
                        mem.familyUpdate();
                        mem.getClient().getSession().write(FamilyPacket.getFamilyData());
                        mem.getClient().getSession().write(FamilyPacket.getFamilyInfo(mem));
                        mem.getClient().getSession().write(CWvsContext.enableActions());
                    }
                }
            }
        }

        c.getPlayer().dropMessage(1, "(" + other.getName() + ") 님과 결별했습니다.\r\n패밀리 관계가 끊어졌습니다.");
        c.getSession().write(FamilyPacket.getFamilyData());
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void DeleteSenior(final LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getFamilyId() <= 0 || c.getPlayer().getSeniorId() <= 0) {
            return;
        }
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
        final MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        boolean junior2 = mgc.getJunior2() == c.getPlayer().getId();

        if (junior2) {
            mgc.setJunior2(0);
        } else {
            mgc.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        if (!mgc.isOnline()) {
            MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getTodayRep(), mgc.getId());
        }

        c.getSession().write(FamilyPacket.getFamilyData());
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
        c.getPlayer().familyUpdate();
        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }

        MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " 님이 당신과 결별하였습니다. 패밀리 관계가 끊어집니다.", 0);

        MapleCharacter otherChr = ChannelServer.getInstance(mgc.getChannel()).getPlayerStorage().getCharacterById(mgc.getId());
        if (otherChr != null) {
            otherChr.saveFamilyStatus();
            otherChr.familyUpdate();
            otherChr.getClient().getSession().write(FamilyPacket.getFamilyData());
            otherChr.getClient().getSession().write(FamilyPacket.getFamilyInfo(otherChr));
            otherChr.getClient().getSession().write(CWvsContext.enableActions());
        }

        List<MapleFamilyCharacter> check = mgc.getAllJuniors(fam);
        for (MapleFamilyCharacter allMembers : check) {
            int ch = World.Find.findChannel(allMembers.getName());
            if (ch > 0) {
                MapleCharacter mem = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(allMembers.getId());
                if (mem != null) {
                    mem.saveFamilyStatus();
                    mem.familyUpdate();
                    mem.getClient().getSession().write(FamilyPacket.getFamilyData());
                    mem.getClient().getSession().write(FamilyPacket.getFamilyInfo(mem));
                    mem.getClient().getSession().write(CWvsContext.enableActions());
                }
            }
        }

        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "(" + mgc.getName() + ") 님과 결별했습니다.\r\n패밀리 관계가 끊어졌습니다.");
        c.getSession().write(FamilyPacket.getFamilyData());
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void AcceptFamily(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (inviter != null && c.getPlayer().getSeniorId() == 0 && (c.getPlayer().isGM() || !inviter.isHidden()) && inviter.getLevel() - 20 <= c.getPlayer().getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(slea.readMapleAsciiString()) && inviter.getNoJuniors() < 2 /*&& inviter.getFamily().getGens() < 1000*/ && c.getPlayer().getLevel() >= 10) {
            boolean accepted = slea.readByte() > 0;
            inviter.getClient().getSession().write(FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
            if (accepted) {
                c.getSession().write(FamilyPacket.getSeniorMessage(inviter.getName()));
                int old = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getFamilyId();
                int oldj1 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior1();
                int oldj2 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior2();
                if (inviter.getFamilyId() > 0 && World.Family.getFamily(inviter.getFamilyId()) != null) {
                    MapleFamily fam = World.Family.getFamily(inviter.getFamilyId());
                    c.getPlayer().setFamily(old <= 0 ? inviter.getFamilyId() : old, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                    MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(c.getPlayer().getId());
                    } else {
                        mf.setJunior1(c.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if (old > 0 && World.Family.getFamily(old) != null) { //has junior
                        MapleFamily.mergeFamily(fam, World.Family.getFamily(old), inviter.getId());
                    } else {
                        c.getPlayer().setFamily(inviter.getFamilyId(), inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        c.getPlayer().saveFamilyStatus();
                    }
                    List<MapleFamilyCharacter> check = mf.getAllJuniors(fam);
                    for (MapleFamilyCharacter allMembers : check) {
                        int ch = World.Find.findChannel(allMembers.getName());
                        if (ch > 0) {
                            MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(allMembers.getId());
                            if (chr != null) {
                                chr.saveFamilyStatus();
                                chr.getClient().getSession().write(FamilyPacket.getFamilyData());
                                chr.getClient().getSession().write(FamilyPacket.getFamilyInfo(chr));
                            }
                        }
                    }
                    if (fam != null) {
                        if (inviter.getNoJuniors() == 1 || old > 0) {
                            fam.resetDescendants();
                        }
                        fam.resetPedigree();
                    }
                } else {
                    int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getTodayRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2, c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getTodayRep(), c.getPlayer().getId());
                        inviter.setFamily(id, 0, c.getPlayer().getId(), 0);
                        inviter.finishAchievement(36);
                        c.getPlayer().setFamily(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        MapleFamily fam = World.Family.getFamily(id);
                        fam.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
                        if (old > 0 && World.Family.getFamily(old) != null) {
                            MapleFamily.mergeFamily(fam, World.Family.getFamily(old), inviter.getId());
                        } else {
                            fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        }
                        fam.resetDescendants();
                        fam.resetPedigree();
                    }
                }
                inviter.getClient().getSession().write(FamilyPacket.getFamilyData());
                inviter.getClient().getSession().write(FamilyPacket.getFamilyInfo(inviter));
                c.getSession().write(FamilyPacket.getFamilyData());
                c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
            }
        }
        c.getSession().write(FamilyPacket.getFamilyData());
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
    }
}
