package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MonsterSkill;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.status.MonsterTemporaryStat;
import handling.ChatType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.life.OverrideMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMist;
import server.maps.MapleNodes;
import server.movement.LifeMovementFragment;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CMobPool;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.FieldEffect;

public class MobHandler {

    // CMob::OnMove
    public static final void handleMobMove(LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        int objectID = slea.readInt();
        slea.readByte();
        short moveID = slea.readShort();
        List<LifeMovementFragment> res;
        final MapleMonster monster = chr.getMap().getMonsterByOid(objectID);
        byte pNibbles = slea.readByte();
        int pCenterSplit = slea.readByte();

        int pAction = pCenterSplit >> 1;

        int nAction = pCenterSplit;
        int skillID = slea.readByte() & 0xFF;
        int skillLv = slea.readByte() & 0xFF;

        short option = slea.readShort();
        if (nAction < 0) {
            nAction = -1;
        } else {
            nAction = ((pCenterSplit >> 1 & 0xFF) - 13);
        }

        boolean isSkill = nAction > 46;
        boolean bNextAttackPossible = (pNibbles & 1) != 0;
        final boolean v56 = (pNibbles & 0xF0) != 0;
        int nextCastSkill = 0;
        int nextCastSkillLevel = 0;

        int skillid = 0;
        int skilllevel = 0;

        switch (monster.getId()) {
            case 8900000:
            case 8900001:
            case 8900002:
            case 8910000:
            case 8920000:
            case 8920001:
            case 8920002:
            case 8920003:
            case 8930000:
            case 8930001: {
                handleRootabyssMobPattern(chr, monster, nAction, skillID);
                break;
            }
        }
        handleMobNotice(monster, nAction, skillID, skillLv);

        final MobSkill mobSkill = MobSkillFactory.getMobSkill(skillID, skillLv);
        if (mobSkill != null) {
            if (monster.hasSkill(skillID, skillLv)) {
                if (monster.isAlive()) {
                    mobSkill.applyEffect(chr, monster, true, option);
                    c.sendPacket(CMobPool.OnMobSkillDelay(objectID, option, skillID, skillLv));
                }
            }
        }

        boolean madeSkill = (pAction - 13 > 8) && (pAction - 7 > 2) && (pAction - 22 <= 16);
        if (monster.hasSkill(skillID, skillLv) || bNextAttackPossible || madeSkill || nAction < 11) {
            final byte size = monster.getNoSkills();
            if (size > 0) {
                if (madeSkill || nAction < 11) {
                    for (final Pair<Integer, Integer> skillToUse : monster.getSkills()) {
                        skillid = skillToUse.getLeft();
                        skilllevel = skillToUse.getRight();
                        MobSkill toUse = MobSkillFactory.getMobSkill(skillid, skilllevel);
                        if (monster.hasSkill(skillid, skilllevel)) {
                            final MobSkill msi = MobSkillFactory.getMobSkill(skillid, skilllevel);
                            if (msi != null && !msi.checkCurrentBuff(chr, monster)) {
                                if (monster.getLastSkillUsed(nAction) == 0 || (((System.currentTimeMillis() - monster.getLastSkillUsed(nAction)) > msi.getCoolTime()) && !msi.onlyOnce())) {
                                    if (monster.canUseSkill(toUse)) {
                                        final int reqHp = (int) (((float) monster.getHp() / monster.getMobMaxHp()) * 100);
                                        if (reqHp <= msi.getHP()) {
                                            monster.setLastSkillUsed(nAction, System.currentTimeMillis(), msi.getCoolTime());
                                            nextCastSkill = skillid;
                                            nextCastSkillLevel = skilllevel;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        List unk3 = new ArrayList();
        byte size1 = slea.readByte();
        for (int i = 0; i < size1; i++) {
            unk3.add(new Pair(Integer.valueOf(slea.readShort()), Integer.valueOf(slea.readShort())));
        }
        List unk2 = new ArrayList();
        byte size = slea.readByte();
        for (int i = 0; i < size; i++) {
            unk2.add(Integer.valueOf(slea.readShort()));
        }
        if (monster.getController() != null && monster.getController().getId() != c.getPlayer().getId()) {
            if (!v56) {
                c.sendPacket(CMobPool.stopControllingMonster(objectID));
                return;
            } else {
                monster.switchController(chr, true);
            }
        }
        slea.skip(26);
        final Point startPos = monster.getTruePosition();
        res = MovementParse.parseMovement(slea, 2);

        //if (res != null && res.size() > 0) {
        c.sendPacket(CMobPool.ctrlAck(monster.getObjectId(), moveID, monster.getMp(), bNextAttackPossible, nextCastSkill, nextCastSkillLevel, 0));
        MapleMap map = chr.getMap();
        MovementParse.updatePosition(res, monster, -1);
        Point endPos = monster.getTruePosition();
        map.moveMonster(monster, endPos);
        map.broadcastMessage(chr, CMobPool.moveMonster(bNextAttackPossible, pCenterSplit, skillID, skillLv, option, monster.getObjectId(), startPos, res, unk2, unk3), endPos);

        //}
        switch (monster.getId()) {
            case 8645004:
            case 8645005:
            case 8645006:
            case 8645007:
            case 8645008:
            case 8645009:
            case 8645010: {
                handleDunkelMobPattern(monster, nAction);
                break;
            }
            case 9601510:
            case 9601511:
            case 9601512:
            case 9601513: {
                handleMitsuhideMobPattern(monster, chr, nAction);
                break;
            }
        }
    }

    public static void handleMitsuhideMobPattern(MapleMonster monster, MapleCharacter user, int nAction) {
        switch (monster.getId()) {
            case 9601512:
            case 9601513: {
                if (nAction != -1) {
                    monster.getMap().killMonster(monster, (byte) -1);
                }
                break;
            }
            case 9601510:
            case 9601511: {
                if (nAction != -1) {
                    int v1 = Randomizer.rand(0, 1);
                    if (v1 == 0) {
                        int v2 = Randomizer.rand(9601512, 9601513);
                        int v3 = Randomizer.rand(-100, 100);
                        Point v4 = new Point(user.getTruePosition().x, user.getTruePosition().y);
                        monster.getMap().spawnMonster_sSack(MapleLifeFactory.getMonster(v2), v4, -2);
                    }
                }
                break;
            }
        }
    }

    public static void handleDunkelMobPattern(MapleMonster monster, int nAction) {
        switch (monster.getId()) {
            case 8645004:
            case 8645005:
            case 8645006:
            case 8645007:
            case 8645008:
            case 8645010: {
                if (nAction != -1 && nAction != 10) {
                    if (nAction < 3) {
                        monster.getMap().killMonster(monster, (byte) -1);
                    }
                }
                break;
            }
            case 8645009: {
                if (nAction != -1) {
                    int v1 = Randomizer.rand(0, 1);
                    if (v1 == 0) {
                        int v2 = Randomizer.rand(8645004, 8645008);
                        int v3 = Randomizer.rand(-100, 100);
                        Point v4 = new Point(monster.getTruePosition().x + v3, monster.getTruePosition().y);
                        monster.getMap().spawnMonster_sSack(MapleLifeFactory.getMonster(v2), v4, -2);
                    }
                }
                break;
            }
        }
    }

    public static boolean handleMobBlockedAttack(MapleMonster monster, int nAction) {
        switch (monster.getId()) {
            case 8850011: {
                if (nAction == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void handleMobNotice(MapleMonster monster, int nAction, int skillID, int skillLv) {
        switch (monster.getId()) {
            case 8645009: {
                if (nAction == 2) {
                    monster.getMap().floatNotice("친위 대장 듄켈 : 한번 받아 봐라! 별 조차 갈라버리는 내 궁극의 검기를!", 5120180, false);
                }
                break;
            }
            case 8880303: {
                int nMent = Randomizer.rand(0, 10);
                if (nMent == 0) {
                    monster.getMap().floatNotice("거짓의 거울은 공격을 반전시켜요. 균열이 나타나면 공격을 마주하세요.", 5120189, false);
                }
                break;
            }
            case 8880405: {
                if (skillID == 201) {
                    if (skillLv == 29) {
                        monster.getMap().floatNotice("힐라가 죽음의 밑바닥에서 스우의 사령을 끌어올리는 소리가 들린다.", 5120188, false);
                    }
                    if (skillLv == 30) {
                        monster.getMap().floatNotice("힐라가 죽음의 밑바닥에서 데미안의 사령을 끌어올리는 소리가 들린다.", 5120188, false);
                    }
                }
                break;
            }
            case 8870000: {
                int v1 = (nAction == 2 ? 0 : nAction == 4 ? 1 : nAction == 10 ? 2 : nAction == 12 ? 3 : -1);
                if (v1 > -1) {
                    monster.getMap().broadcastMessage(CMobPool.OnMobSpeaking(monster.getObjectId(), v1));
                }
                break;
            }
            case 8870100: {
                int v1 = (skillID == 180 ? 0 : (skillID == 200 && skillLv == 251) ? 1 : -1);
                if (v1 > -1) {
                    monster.getMap().broadcastMessage(CMobPool.OnMobSpeaking(monster.getObjectId(), v1));
                }
                break;
            }
            case 8500001: {
                if (skillID == 201) {
                    monster.getMap().floatNotice("곧 시간의 틈이 생깁니다. 바닥을 조심하세요.", 5120177, false);
                }
                break;
            }
            case 8500002: {
                if (skillID == 171) {
                    monster.getMap().floatNotice("파풀라투스에게 일정 피해를 입히면 시간의 격류를 버틸 수 있습니다.", 5120177, false);
                }
                if (skillID == 201) {
                    monster.getMap().floatNotice("파풀라투스가 이계의 파풀라투스를 흡수하여 HP를 회복합니다.", 5120177, false);
                }
                break;
            }
            case 8840000: {
                if (skillID == 145) {
                    monster.getMap().broadcastMessage(CWvsContext.getTopMsg("근 거리의 포위당한 반 레온이 반격을 시전합니다."));
                }
                if (skillID == 170) {
                    if (monster.getController() != null) {
                        monster.getMap().broadcastMessage(CWvsContext.getTopMsg("반 레온이 자신에게 가장 큰 위협을 준 적에게 순간 이동하여 다가갑니다."));
                    }
                }
                if (skillID == 200) {
                    monster.getMap().broadcastMessage(CWvsContext.getTopMsg("반 레온이 부하 몬스터를 소환해 도움을 받으려 합니다."));
                }
                break;
            }
            case 8850011: {
                if (nAction == 2) {
                    monster.getMap().broadcastMessage(CWvsContext.getTopMsg("시그너스가 불새를 소환해서 불바다를 만들려고 합니다."));
                }
                if (skillID == 172) {
                    monster.getMap().broadcastMessage(CWvsContext.getTopMsg("시그너스가 버프 스킬을 비웃으며 적을 리본 돼지로 변이하려 합니다."));
                }
                if (skillID == 200) {
                    if (skillLv == 223) {
                        monster.getMap().broadcastMessage(CWvsContext.getTopMsg("시그너스가 자신의 기사단을 위해 신수를 소환합니다."));
                    }
                }
                break;
            }
            case 8880140:
            case 8880150: {
                if (nAction > -1 && nAction < 3) {
                    monster.getMap().floatNotice("저 바람을 맞으면 꿈이 강해 질 겁니다!", 5122019, false);
                }
                break;
            }
            case 8880500:
            case 8880501: {
                if (skillID == 179) {
                    monster.getMap().floatNotice("이 지역에서 발생되는 공격은 창조나 파괴의 저주를 거는 것 같다. 만약 두 저주가 동시에 걸린다면 큰 피해를 입으니 조심하자.", 5120203, false);
                }
                break;
            }
            case 8880502: {
                if (skillID == 176) {
                    monster.getMap().floatNotice("검은 마법사의 붉은 번개가 모든 곳을 뒤덮는다. 피할 곳을 찾아야 한다.", 5120203, false);
                }
                if (skillID == 201) {
                    monster.getMap().floatNotice("파멸의 눈이 적을 쫓는다.", 5120203, false);
                }
                break;
            }
            case 9421586: {
                if (skillID == 145) {
                    monster.getMap().floatNotice("The Interno Onmyouji is on a rampage! Watch out for the flames!", 5120192, false);
                }
                break;
            }
            case 9601838:
            case 9601839:
            case 9601840: {
                // Sleep :: Ashray's will has temporarlly suppred Aufheben. (5120200)
                break;
            }
            case 9601841: {
                if (skillID == 176) {
                    monster.getMap().floatNotice("Aufheben splits the sky with a blinding light.", 5120202, false);
                }
                break;
            }
            case 9601842: {
                if (skillID == 176) {
                    monster.getMap().floatNotice("The force of destruction is accelerating. If you don't pair up with another party members who has a different thesis debuff from you. you'll be KO'd in 10 seconds.", 5120201, false);
                }
                break;
            }
            case 9601847:
            case 9601849: {
                //monster.getMap().floatNotice("당신은 분출되는 세계의 에너지에 닿았습니다.", 5120200, false);
                break;
            }
        }
    }

    public static void handleRootabyssMobPattern(MapleCharacter user, MapleMonster monster, int nAction, int skillID) {
        switch (monster.getId()) {
            case 8910000: {
                if (skillID == 131) {
                    monster.getMap().floatNotice("시간의 틈새에 ‘균열’이 발생했습니다.", 5120025, false);
                }
                if (skillID == 200) {
                    monster.getMap().broadcastMessage(FieldEffect.objectEnableEffect("Pt05gate"));
                    monster.getMap().floatNotice("반반이 시공간 붕괴를 사용합니다. 반반의 내면 세계로 들어가 반반의 무의식을 잡아야 합니다.", 5120025, false);
                }
                break;
            }
        }
        if (monster.getChangeTime() > 0) {
            if ((System.currentTimeMillis() / 1000) > (monster.getChangeTime() / 1000) + 10) {
                switch (monster.getId()) {
                    case 8900000:
                    case 8900001:
                    case 8900002: {
                        if (monster.getHPPercent() < 70) {
                            long a1 = monster.getHp();
                            Point a2 = monster.getTruePosition();
                            int a3 = Randomizer.rand(8900000, 8900002);
                            if (monster.getHPPercent() < 30) {
                                a3 = Randomizer.rand(8900001, 8900002);
                            }
                            MapleMonster a4 = MapleLifeFactory.getMonster(a3);
                            if (monster.getId() != a4.getId()) {
                                user.getMap().removeMonster(monster, 1);
                                user.getMap().spawnMonsterOnGroundBelow(a4, a2);
                                a4.damage(user, (a4.getMobMaxHp() - a1), false);
                            }
                            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            int capID = Randomizer.rand(1003727, 1003728);
                            Equip cap = (Equip) ii.getEquipById(capID);
                            cap.setPosition((short) -101);
                            user.setPierreHat(capID);
                            user.getClient().sendPacket(CWvsContext.InventoryPacket.updateSpecialItemUse_(cap, (byte) 1, user));
                            monster.getMap().floatNotice("피에르의 모자가 씌워졌습니다. 모자 색상과 동일한 색상의 피에르를 공격하면 피에르의 HP가 회복됩니다.", 5120098, false);
                        }
                        break;
                    }
                    case 8920000:
                    case 8920001:
                    case 8920002:
                    case 8920003: {
                        if (nAction == -1) {
                            long a1 = monster.getHp();
                            Point a2 = monster.getTruePosition();
                            int a3 = Randomizer.rand(8920000, 8920003);
                            MapleMonster a4 = MapleLifeFactory.getMonster(a3);
                            if (monster.getId() != a4.getId()) {
                                user.getMap().removeMonster(monster, -1);
                                user.getMap().spawnMonsterOnGroundBelow(a4, a2);
                                a4.damage(user, (a4.getMobMaxHp() - a1), false);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public static boolean inRangeInclusive(Byte pVal, Integer pMin, Integer pMax) {
        return !(pVal < pMin) || (pVal > pMax);
    }

    public static final void FriendlyDamage(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4);
        MapleMonster mobto = map.getMonsterByOid(slea.readInt());

        if ((mobfrom != null) && (mobto != null) && (mobto.getStats().isFriendly())) {
            int damage = mobto.getStats().getLevel() * Randomizer.nextInt(mobto.getStats().getLevel()) / 2;
            mobto.damage(chr, damage, true);
            checkShammos(chr, mobto, map);
        }
    }

    public static final void MobBomb(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4);
        slea.readInt();

        if ((mobfrom != null) && (mobfrom.getBuff(MonsterTemporaryStat.MonsterBomb) != null));
    }

    public static final void checkShammos(MapleCharacter chr, MapleMonster mobto, MapleMap map) {
        MapleMap mapp;
        if ((!mobto.isAlive()) && (mobto.getStats().isEscort())) {
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                if ((chrz.getParty() != null) && (chrz.getParty().getLeader().getId() == chrz.getId())) {
                    if (!chrz.haveItem(2022698)) {
                        break;
                    }
                    MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                    mobto.heal((int) mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                    return;
                }
            }
            mapp = chr.getMap().getForcedReturnMap();
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if ((mobto.getStats().isEscort()) && (mobto.getEventInstance() != null)) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }

    public static final void handleMobSelfDestruct(int oid, MapleCharacter chr) {
        MapleMonster mob = chr.getMap().getMonsterByOid(oid);
        byte selfd = mob.getStats().getSelfD();
        boolean isNotDeaded = false;
        switch (mob.getId()) {
            case 8240127:
            case 8240128:
            case 8240129: {
                isNotDeaded = true;
                break;
            }
        }
        if (!isNotDeaded && selfd != -1) {
            chr.getMap().killMonster(mob, chr, false, false, (byte) 2);
        } else {
            chr.getClient().sendPacket(CField.OnDamageByUser(chr.getId(), 1, 99999));
        }
    }

    public static final void AutoAggro(int monsteroid, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.isHidden())) {
            return;
        }
        MapleMonster monster = chr.getMap().getMonsterByOid(monsteroid);

        if (monster != null && chr.getPosition().distance(monster.getPosition()) < 200000) {
            if (monster.getController() != null) {
                if (chr.getMap().getCharacterById_InMap(monster.getController().getId()) == null) {
                    monster.switchController(chr, true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                monster.switchController(chr, true);
            }
        }
    }

    public static final void HypnotizeDmg(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        slea.skip(4);
        int to = slea.readInt();
        slea.skip(2);
        int damage = slea.readInt();

        MapleMonster mob_to = chr.getMap().getMonsterByOid(to);
        if ((mob_from != null) && (mob_to != null)) {
            if (damage > 30000) {
                return;
            }
            mob_to.damage(chr, damage, true);
            checkShammos(chr, mob_to, chr.getMap());
        }
    }

    public static final void handleLapidificationState(LittleEndianAccessor slea, MapleCharacter user) {
        final int state = slea.readInt();
        if (state == 0) {
            if (user.hasDisease(MonsterSkill.Lapidification)) {
                user.dispelDebuff(MonsterSkill.Lapidification);
            }
        }
    }

    public static final void handleMobSkillDelay(LittleEndianAccessor slea, MapleCharacter user) {
        final int objectID = slea.readInt();
        final int skillID = slea.readInt();
        final int skillLv = slea.readInt();
    }

    public static final void handleCygnusMist(LittleEndianAccessor slea, MapleCharacter user) {
        final int objectID = slea.readInt();
        final MapleMonster monster = user.getMap().getMonsterByOid(objectID);
        if (monster != null) {
            final int AttackIndex = slea.readInt();
            final Point AttackPos = slea.readIntPos();
            final int AttackDelay = slea.readInt();
            switch (monster.getId()) {
                case 8850011: {
                    if (AttackIndex == 2) {
                        MobSkillFactory.getMobSkill(131, 13).applyEffect(user, monster, true, (short) AttackDelay, AttackPos);
                    }
                    break;
                }
            }
        }
    }

    public static final void DisplayNode(LittleEndianAccessor slea, MapleCharacter chr) {
        int objectID = slea.readInt();
        MapleMonster mob_from = chr.getMap().getMonsterByOid(objectID);
        if (mob_from != null) {
            chr.getClient().sendPacket(CMobPool.escortFullPath(mob_from, chr.getMap()));
            mob_from.getMap().broadcastMessage(CMobPool.escortFullPath(mob_from, chr.getMap()));
        }
    }

    public static final void handleMobEscortCollision(LittleEndianAccessor iPacket, final MapleCharacter user) {
        if (user == null) {
            return;
        }
        final MapleMap uField = user.getMap();
        if (uField == null) {
            return;
        }
        int objectID = iPacket.DecodeInt();
        final MapleMonster mLife = uField.getMonsterByOid(objectID);
        if (mLife == null) {
            return;
        }
        int collision = iPacket.readInt();
        int escortDest = user.getMap().getNodes().size();
        if (escortDest > 0) {
            MapleNodes.MapleNodeInfo mni = user.getMap().getNode(collision);
            if (mni == null) {
                return;
            }
            user.dropMessage(6, "newNode : " + collision);
            user.dropMessage(5, "mni.attr : " + mni.attr);
            user.dropMessage(5, "mni.edge : " + mni.edge);
            user.dropMessage(5, "mni.key : " + mni.key);
            user.dropMessage(5, "mni.nextNode : " + mni.nextNode);
            user.dropMessage(5, "mni.node : " + mni.node);
            if (mni.attr == 2) {
                switch (user.getMapId() / 100) {
                    case 9211200: {
                        if (collision == 2) {
                            user.getMap().talkMonster("나를 잘 호위 하도록 해. 내가 이름을 불렀을 때 30초 내에 오지 않으면 아마 모든 것은 실패하게 될 거야.", 5120035, mLife.getObjectId(), 4);
                        } else {
                            user.getMap().talkMonster("수고많았어. 모든 파티원이 이 곳으로 모였을 때 파티장이 포탈을 타면, 모두 함께 다른 곳으로 이동 할 수 있게 돼.", 2, mLife.getObjectId(), 4);
                        }
                    }
                    case 9211203: {
                        user.getMap().talkMonster("저런 포스터따위 신경쓰지말고, 어서 출발하자고!", 2, mLife.getObjectId(), 4);
                        break;
                    }
                    case 9211204:
                    case 9211205: {
                        if (collision == 2) {
                            user.getMap().talkMonster("봉인이 잘 되었는지 확인을 해볼까?", 2, mLife.getObjectId(), 4);
                        }
                        if (collision == 4) {
                            user.getMap().talkMonster("이런 자물쇠따위. 낄낄.", 2, mLife.getObjectId(), 4);
                            user.getMap().getReactorById(2118003).forceHitReactor((byte) 1);
                            MapleMonster mob = MapleLifeFactory.getMonster(9300281);
                            user.getMap().spawnMonsterOnGroundBelow(mob, new Point(317, 174));
                        }
                        if (collision == 5) {
                            final String[][] say = {{"날 가둬 놓은 장로들을 절대 용서하지 않겠다! ", "22"}, {"돌진으로 적들을 물리쳐라 렉스여!!", "41"},
                            {"후훗. 저 애송이들 따위 다 없애버려!!", "42"}, {"어리석은 인간들같으니라고! 이제 깨달았나? 너흰 단지 내가 렉스의 봉인을 푸는 것을 도와준 것 뿐이라는 것을!", "41"},
                            {"곧 이 샤모스님의 세상이 될것이다!", "41"}, {"렉스! 어서 빨리 장로들에게 복수를 하자!", "41"}};
                            int rand = Randomizer.rand(0, say.length - 1);
                            user.getMap().talkMonster(say[rand][0], 2, mLife.getObjectId(), 10);
                        }
                        break;
                    }
                    case 9320001: {
                        if (mni.key == 23) {
                            user.getMap().talkMonster("여긴 너무 무서워요. 제게서 너무 멀리 가지 말고 저를 지켜주세요.", 5120051, mLife.getObjectId(), 4);
                        }
                        break;
                    }
                    case 9320002: {
                        if (mni.key == 40) {
                            user.getMap().talkMonster("저 위에 아이스 나이트가 있는 얼음 저주의 벌판으로 가는 포탈이 있어요. 저를 거기까지 데려다 주세요.", 5120051, mLife.getObjectId(), 4);
                        } else if (mni.key == 3) {
                            user.getMap().talkMonster("달팽이가 얼어 붙어 버렸어요. 이것도 다 아이스 나이트의 짓이겠죠?", 0, mLife.getObjectId(), 4);
                        } else if (mni.key == 10) {
                            user.getMap().talkMonster("이건 아이스 맨... 저도 나중에 저렇게 얼어 버리는 건가요? 그건 싫어요..", 0, mLife.getObjectId(), 4);
                        } else if (mni.key == 62) {
                            user.getMap().talkMonster("여기 또... 무서워요. 어서 이 저주가 풀렸으면...", 0, mLife.getObjectId(), 4);
                        } else if (mni.key == 25) {
                            user.getMap().talkMonster("이 동상을 보니 이제 거의 다 온 것 같아요. 조심하세요.", 0, mLife.getObjectId(), 4);
                        }
                        break;
                    }
                }
            }
            mLife.setCurrentDestIndex(collision);
            if (uField.getId() == 921140000) {
                if (collision == 7) {
                    uField.killAllMonsters(true);
                    uField.broadcastMessage(CField.showEffect("quest/party/clear"));
                    uField.broadcastMessage(CField.playSound("Party1/Clear"));
                }
            }
            /*
            if (user.getMap().isLastNode(collision)) {
                switch (user.getMapId() / 100) {
                    case 9211200:
                    case 9211201:
                    case 9211202:
                    case 9211203:
                    case 9211204:
                    case 9320001:
                    case 9320002:
                    case 9320003: {
                        user.getMap().broadcastMessage(CWvsContext.serverNotice(5, "다음 단계로 진행 할 수 있습니다."));
                        user.getMap().removeMonster(mLife);
                        break;
                    }
                }
            }
             */
        }
    }
}
