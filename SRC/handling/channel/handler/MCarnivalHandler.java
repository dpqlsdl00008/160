package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import java.util.List;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.CMonsterCarnival;

/*
    KMS 1.2.160 BEATRICE DEVELOPMENT
*/
public class MCarnivalHandler {

    /*
    0 : 공격력 & 마력 UP
    1 : 시야 제한
    2 : 물리 방어력 & 마법 방어력 UP
    3 : 슬로우
    4 : 경험치 획득 UP
    5 : 스킬 봉인
    */
    public static final void useCP(final LittleEndianAccessor iPacket, final MapleClient c) {
        MapleCharacter user = c.getPlayer();
        if ((user.getMapId() / 10000000) != 98) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (user.getParty() == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte tab = iPacket.readByte();
        int team = 0;
        int currentCP = 1000;
        int totalCP = 1000;
        int conCP = (1 + tab) * 100;
        if (conCP > currentCP) {
            c.sendPacket(CMonsterCarnival.showCPMessage((byte) 1));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        switch (tab) {
            case 0: {
                int monsterNumber = 10;
                MapleMonster monster = MapleLifeFactory.getMonster(9300134);
                if (monster != null) {
                    if (user.getMap().makeCarnivalSpawn(team, monster, monsterNumber)) {
                        c.sendPacket(CMonsterCarnival.updateCP(1000 - conCP, 1000));
                        c.sendPacket(CMonsterCarnival.showCPMessage((byte) 0, (byte) monsterNumber, user));
                        c.sendPacket(CWvsContext.enableActions());
                    }
                }
                break;
            }
            case 1: {
                // 1, 0
                // 1, 1
                for (MapleCharacter other : user.getMap().getAllCharactersThreadsafe()) {
                    if (other != null) {
                        if (user.getParty() != other.getParty()) {
                            // 디버프
                        }
                    }
                }
                break;
            }
            case 2: {
                // 2, 2
                // 2, 3
                break;
            }
            case 3: {
                break;
            }
        }
        c.sendPacket(CWvsContext.enableActions());

        /*
        if (tab == 0) {
            final List<Pair<Integer, Integer>> mobs = c.getPlayer().getMap().getMobsToSpawn();
            if (num >= mobs.size() || c.getPlayer().getAvailableCP() < mobs.get(num).right) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            final MapleMonster mons = MapleLifeFactory.getMonster(mobs.get(num).left);
            if (mons != null && c.getPlayer().getMap().makeCarnivalSpawn(c.getPlayer().getCarnivalParty().getTeam(), mons, num)) {
                c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), mobs.get(num).right);
                c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                }
                //c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                c.getSession().write(CWvsContext.enableActions());
            } else {
                c.getPlayer().dropMessage(5, "You may no longer summon the monster.");
                c.getSession().write(CWvsContext.enableActions());
            }

        } else if (tab == 1) { //debuff
	    final List<Integer> skillid = c.getPlayer().getMap().getSkillIds();
	    if (num >= skillid.size()) {
                c.getPlayer().dropMessage(5, "An error occurred.");
                c.getSession().write(CWvsContext.enableActions());
		return;
	    }
            final MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(skillid.get(num)); //ugh wtf
            if (skil == null || c.getPlayer().getAvailableCP() < skil.cpLoss) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            final MapleDisease dis = skil.getDisease();
            boolean found = false;
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (chr.getParty() == null || (c.getPlayer().getParty() != null && chr.getParty().getId() != c.getPlayer().getParty().getId())) {
                    if (skil.targetsAll || Randomizer.nextBoolean()) {
                        found = true;
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
            if (found) {
                c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), skil.cpLoss);
                c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    //chr.dropMessage(5, "[" + (c.getPlayer().getCarnivalParty().getTeam() == 0 ? "Red" : "Blue") + "] " + c.getPlayer().getName() + " has used a skill. [" + dis.name() + "].");
                }
                //c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                c.getSession().write(CWvsContext.enableActions());
            } else {
                c.getPlayer().dropMessage(5, "An error occurred.");
                c.getSession().write(CWvsContext.enableActions());
            }
        } else if (tab == 2) { //skill
            final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            if (skil == null || c.getPlayer().getAvailableCP() < skil.cpLoss) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (c.getPlayer().getMap().makeCarnivalReactor(c.getPlayer().getCarnivalParty().getTeam(), num)) {
                c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), skil.cpLoss);
                c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                }
                //c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                c.getSession().write(CWvsContext.enableActions());
            } else {
                c.getPlayer().dropMessage(5, "You may no longer summon the being.");
                c.getSession().write(CWvsContext.enableActions());
            }
        }
         */
    }
}
