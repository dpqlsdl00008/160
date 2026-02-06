package server.life;

import client.CharacterTemporaryStat;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

import client.MapleCharacter;
import client.MonsterSkill;
import client.status.MonsterTemporaryStat;
import handling.channel.ChannelServer;
import java.util.EnumMap;
import server.Randomizer;
import server.Timer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleReactor;
import tools.Pair;
import tools.packet.CField;
import tools.packet.CMobPool;
import tools.packet.CWvsContext;

public class MobSkill {

    private int skillId, skillLevel, mpCon, spawnEffect, hp, x, y;
    private long duration, cooltime;
    private float prop;
    private short limit;
    private List<Integer> toSummon = new ArrayList<Integer>();
    private Point lt, rb;
    private boolean summonOnce;
    private int skillAfter;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        this.skillLevel = level;
    }

    public void setOnce(boolean o) {
        this.summonOnce = o;
    }

    public boolean onlyOnce() {
        return summonOnce;
    }

    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public void addSummons(List<Integer> toSummon) {
        this.toSummon = toSummon;
    }

    public void setSpawnEffect(int spawnEffect) {
        this.spawnEffect = spawnEffect;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCoolTime(long cooltime) {
        this.cooltime = cooltime;
    }

    public void setProp(float prop) {
        this.prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        this.lt = lt;
        this.rb = rb;
    }

    public void setLimit(short limit) {
        this.limit = limit;
    }

    public boolean checkCurrentBuff(MapleCharacter player, MapleMonster monster) {
        boolean stop = false;
        switch (skillId) {
            case 100:
            case 110:
            case 150:
                stop = monster.isBuffed(MonsterTemporaryStat.PowerUp);
                break;
            case 101:
            case 111:
            case 151:
                stop = monster.isBuffed(MonsterTemporaryStat.MagicUp);
                break;
            case 102:
            case 112:
            case 152:
                stop = monster.isBuffed(MonsterTemporaryStat.PGuardUp);
                break;
            case 103:
            case 113:
            case 153:
                stop = monster.isBuffed(MonsterTemporaryStat.MGuardUp);
                break;
            //154-157, don't stop it
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
                stop = monster.isBuffed(MonsterTemporaryStat.HardSkin) || monster.isBuffed(MonsterTemporaryStat.MImmune) || monster.isBuffed(MonsterTemporaryStat.PImmune);
                break;
            case 200:
            case 201:
                stop = player.getMap().getNumMonsters() >= limit;
                break;
        }
        stop |= monster.isBuffed(MonsterTemporaryStat.MagicCrash);
        return stop;
    }

    public void applyEffect(MapleCharacter player, MapleMonster monster, boolean skill, short option) {
        applyEffect(player, monster, skill, option, null);
    }

    public void applyEffect(MapleCharacter player, MapleMonster monster, boolean skill, short option, Point attackpos) {

        MonsterSkill disease = MonsterSkill.getBySkill(skillId);
        Map<MonsterTemporaryStat, Integer> stats = new EnumMap<>(MonsterTemporaryStat.class);
        List<Integer> reflection = new LinkedList<>();

        switch (skillId) {
            case 100:
            case 110:
            case 150:
                stats.put(MonsterTemporaryStat.PowerUp, Integer.valueOf(x));
                break;
            case 101:
            case 111:
            case 151:
                stats.put(MonsterTemporaryStat.MagicUp, Integer.valueOf(x));
                break;
            case 102:
            case 112:
            case 152:
                stats.put(MonsterTemporaryStat.PGuardUp, Integer.valueOf(x));
                break;
            case 103:
            case 113:
            case 153:
                stats.put(MonsterTemporaryStat.MGuardUp, Integer.valueOf(x));
                break;
            case 154:
                stats.put(MonsterTemporaryStat.Acc, Integer.valueOf(x));
                break;
            case 155:
                stats.put(MonsterTemporaryStat.Eva, Integer.valueOf(x));
                break;
            case 115:
            case 156:
                stats.put(MonsterTemporaryStat.Speed, Integer.valueOf(x));
                break;
            case 157:
                stats.put(MonsterTemporaryStat.Seal, Integer.valueOf(x));
                break;
            case 114:
                switch (monster.getId()) {
                    case 8880010: {
                        MapleMonster cygnus = monster.getMap().getMonsterById(8850011);
                        if (cygnus != null) {
                            cygnus.heal(getX(), getY(), true);
                        }
                        break;
                    }
                    default: {
                        if (lt != null && rb != null && skill && monster != null) {
                            List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                            final int hp = (getX() / 1000) * (int) (950 + 1050 * Math.random());
                            for (MapleMapObject mons : objects) {
                                if (((MapleMonster) mons).getMobMaxHp() > 100000000L) {
                                    ((MapleMonster) mons).heal(hp, getY(), true);
                                }
                            }
                        } else if (monster != null) {
                            if (monster.getMobMaxHp() > 100000000L) {
                                monster.heal(getX(), getY(), true);
                            }
                        }
                        break;
                    }
                }
                // monster.getMap().broadcastMessage(CWvsContext.serverNotice(5, ((MapleMonster) mons).getName() + "이(가) 미니 캐슬 골렘을(를) 흡수하여 HP를 회복합니다."));
                break;
            case 105: //consume..
                if (lt != null && rb != null && skill && monster != null) {
                    List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                    for (MapleMapObject mons : objects) {
                        if (mons.getObjectId() != monster.getObjectId()) {
                            player.getMap().killMonster((MapleMonster) mons, player, true, false, (byte) 1, 0);
                            monster.heal(getX(), getY(), true);
                            break;
                        }
                    }
                } else if (monster != null) {
                    monster.heal(getX(), getY(), true);
                }
                break;
            case 127:
                if (lt != null && rb != null && skill && monster != null && player != null) {
                    for (MapleCharacter character : getPlayersInRange(monster, player)) {
                        character.dispel();
                    }
                } else if (player != null) {
                    player.dispel();
                }
                break;
            case 129:
                /*
                if (monster != null) {
                    if (skillLevel == 14) {
                        if (lt != null && rb != null && skill && player != null) {
                            if (player.getEventInstance() != null) {
                                for (final MapleCharacter user : getPlayersInRange(monster, player)) {
                                    if (user != null) {
                                        if (!user.hasBlockedInventory()) {
                                            final MapleMap bMap = user.getClient().getChannelServer().getMapFactory().getMap(272020300);
                                            if (bMap.getCharactersSize() == 0) {
                                                bMap.resetFully(true);
                                                user.changeMap(bMap);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        final BanishInfo bMap = monster.getStats().getBanishInfo();
                        if (bMap != null) {
                            if (lt != null && rb != null && skill && player != null) {
                                for (MapleCharacter chr : getPlayersInRange(monster, player)) {
                                    if (!chr.hasBlockedInventory()) {
                                        chr.changeMapBanish(bMap.getMap(), bMap.getPortal(), bMap.getMsg());
                                    }
                                }
                            } else if (player != null && !player.hasBlockedInventory()) {
                                player.changeMapBanish(bMap.getMap(), bMap.getPortal(), bMap.getMsg());
                            }
                        }
                    }
                }
                 */
                break;
            case 131:
                if (monster != null) {
                    for (final MapleMist cMist : monster.getMap().getAllMistsThreadsafe()) {
                        if (cMist != null) {
                            if (cMist.isMobMist()) {
                                if (cMist.getSchedule() != null) {
                                    cMist.getSchedule().cancel(false);
                                    cMist.setSchedule(null);
                                }
                                if (cMist.getPoisonSchedule() != null) {
                                    cMist.getPoisonSchedule().cancel(false);
                                    cMist.setPoisonSchedule(null);
                                }
                                monster.getMap().broadcastMessage(CField.removeMist(cMist.getObjectId(), false));
                                monster.getMap().removeMapObject(cMist);
                            }
                        }
                    }
                    switch (this.skillLevel) {
                        case 15:
                        case 16: {
                            int attackpos_x = (int) (player == null ? monster.getTruePosition().x : player.getTruePosition().x);
                            int attackpos_y = (int) monster.getTruePosition().getY();
                            attackpos = (attackpos == null ? monster.getTruePosition() : new Point(attackpos_x, attackpos_y));
                            monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(attackpos, true), monster, this), (int) getDuration(), false);
                            if (this.skillLevel == 15) {
                                attackpos = new Point(attackpos.x + 500, attackpos.y);
                                MobSkill a1 = MobSkillFactory.getMobSkill(131, 16);
                                monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(attackpos, true), monster, a1), (int) getDuration(), false);
                            }
                            if (this.skillLevel == 16) {
                                attackpos = new Point(attackpos.x - 500, attackpos.y);
                                MobSkill a1 = MobSkillFactory.getMobSkill(131, 15);
                                monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(attackpos, true), monster, a1), (int) getDuration(), false);
                            }
                            break;
                        }
                        case 18: {
                            monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(player.getTruePosition(), true), monster, this), (int) getDuration(), false);
                            break;
                        }
                        default: {
                            monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(monster.getTruePosition(), true), monster, this), (int) getDuration(), false);
                            break;
                        }
                    }
                }
                break;
            case 140:
                stats.put(MonsterTemporaryStat.PImmune, Integer.valueOf(x));
                break;
            case 141:
                stats.put(MonsterTemporaryStat.MImmune, Integer.valueOf(x));
                break;
            case 142: // Weapon / Magic Immunity
                stats.put(MonsterTemporaryStat.HardSkin, Integer.valueOf(x));
                break;
            case 143: // Weapon Reflect
                stats.put(MonsterTemporaryStat.PCounter, Integer.valueOf(x));
                stats.put(MonsterTemporaryStat.PImmune, Integer.valueOf(x));
                reflection.add(x);
                break;
            case 144: // Magic Reflect
                stats.put(MonsterTemporaryStat.MCounter, Integer.valueOf(x));
                stats.put(MonsterTemporaryStat.MImmune, Integer.valueOf(x));
                reflection.add(x);
                break;
            case 145: // Weapon / Magic reflect
                if (monster.getId() != 8820001) {
                    stats.put(MonsterTemporaryStat.PCounter, Integer.valueOf(x));
                    stats.put(MonsterTemporaryStat.PImmune, Integer.valueOf(x));
                    stats.put(MonsterTemporaryStat.MCounter, Integer.valueOf(x));
                    stats.put(MonsterTemporaryStat.MImmune, Integer.valueOf(x));
                    reflection.add(x);
                    reflection.add(x);
                }
                break;
            case 146:
                if (monster.getId() == 8850010 || monster.getId() == 8850110) {
                    MapleMonster cyguns = monster.getMap().getMonsterById(8850011);
                    if (cyguns == null) {
                        cyguns = monster.getMap().getMonsterById(8850111);
                    }
                    if (cyguns != null) {
                        stats.put(MonsterTemporaryStat.Invincible, 1);
                        reflection.add(x);
                        cyguns.applyMonsterBuff(stats, 146, getDuration(), this, reflection, 0);
                    }
                    break;
                }
                if (monster.getId() != 8850010) {
                    stats.put(MonsterTemporaryStat.Invincible, 1);
                    reflection.add(x);
                }
                break;
            case 170:
                switch (monster.getId()) {
                    case 8930000: {
                        if (monster.getTruePosition().x < player.getTruePosition().x) {
                            monster.setBellumBreath(0);
                            monster.setPosition(new Point(-1500, 443));
                            monster.getMap().broadcastMessage(CMobPool.OnMobTeleport(monster, new Point(-1500, 443), 3));
                        } else {
                            monster.setBellumBreath(1);
                            monster.setPosition(new Point(703, 443));
                            monster.getMap().broadcastMessage(CMobPool.OnMobTeleport(monster, new Point(703, 443), 3));
                        }
                        Timer.MapTimer.getInstance().schedule(new Runnable() {
                            public void run() {
                                monster.getMap().broadcastMessage(CWvsContext.getTopMsg("벨룸이 깊은 숨을 들이쉽니다."));
                            }
                        }, 1500);
                        break;
                    }
                    case 9450066: {
                        monster.setPosition(new Point(-18, -205));
                        monster.getMap().broadcastMessage(CMobPool.OnMobTeleport(monster, new Point(-18, -205), 3));
                        break;
                    }
                    default: {
                        MapleCharacter user = player;
                        for (MapleCharacter rUser : monster.getMap().getAllCharactersThreadsafe()) {
                            if (rUser != null) {
                                if (rUser.hasDisease(MonsterSkill.PainMark)) {
                                    user = rUser;
                                }
                            }
                        }
                        Point teleportPoint = (user != null ? user.getTruePosition() : monster.getTruePosition());
                        monster.setPosition(teleportPoint);
                        monster.getMap().broadcastMessage(CMobPool.OnMobTeleport(monster, teleportPoint, 3));
                        break;
                    }
                }
                break;
            case 176: {
                switch (skillLevel) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9: {
                        for (MapleMapObject mmo : player.getMap().getAllReactorsThreadsafe()) {
                            MapleReactor mr = (MapleReactor) mmo;
                            if (mr.getReactorId() == 2708001 && mr.getState() == 0) {
                                mr.forceHitReactor((byte) 1);
                                break;
                            }
                            if (mr.getReactorId() == 2708002 && mr.getState() == 0) {
                                mr.forceHitReactor((byte) 1);
                                break;
                            }
                            if (mr.getReactorId() == 2708003 && mr.getState() == 0) {
                                mr.forceHitReactor((byte) 1);
                                break;
                            }
                            if (mr.getReactorId() == 2708004 && mr.getState() == 0) {
                                mr.forceHitReactor((byte) 1);
                                break;
                            }
                        }
                        for (final MapleCharacter user : monster.getMap().getAllCharactersThreadsafe()) {
                            if (user != null) {
                                this.setScreenAttack(user, monster, x, 800, 3000);
                            }
                        }
                        break;
                    }
                    case 10:
                    case 11:
                    case 12:
                    case 13: {
                        for (final MapleCharacter user : monster.getMap().getAllCharactersThreadsafe()) {
                            if (user != null) {
                                this.setScreenAttack(user, monster, x, 800, 5000);
                            }
                        }
                        break;
                    }
                    case 14: {
                        int v1 = 0; // 테제
                        int v2 = 0; // 안티 테제
                        for (final MapleCharacter user : monster.getMap().getAllCharactersThreadsafe()) {
                            if (user != null) {
                                if (user.hasDisease(MonsterSkill.PainMark)) {
                                    if (user.getMark().get(MonsterSkill.PainMark).right == 3) {
                                        v1++;
                                    }
                                    if (user.getMark().get(MonsterSkill.PainMark).right == 4) {
                                        v2++;
                                    }
                                    int v3 = Math.abs(v1 - v2);
                                    int v4 = (x / v3);
                                    this.setScreenAttack(user, monster, v4, Integer.MAX_VALUE, 5000);
                                }
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case 180: {
                break;
            }
            case 181: {
                x = monster.getTruePosition().x;
                break;
            }
            case 200:
            case 201: {
                if (monster == null) {
                    return;
                }
                switch (monster.getId()) {
                    case 8880502: {
                        if (monster.getMap().getId() == 450013930) {
                            for (int i = 0; i < this.getSummons().size(); i++) {
                                if (this.getSummons().get(0) == 8880526) {
                                    monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880526), new Point(-600, monster.getTruePosition().y - 1));
                                }
                                if (this.getSummons().get(1) == 8880526) {
                                    monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880526), new Point(600, monster.getTruePosition().y - 1));
                                }
                            }
                        } else {
                            if (this.getSummons().get(0) == 8880526) {
                                monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880526), monster.getTruePosition());
                            }
                            if (this.getSummons().get(1) == 8880526) {
                                monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880526), monster.getTruePosition());
                            }
                        }
                        break;
                    }
                    case 8910000: {
                        monster.setBanbanClock((byte) 0);
                        MapleMap banbanMap = ChannelServer.getInstance(monster.getMap().getChannel()).getMapFactory().getMap(105200120);
                        MapleMonster banban = MapleLifeFactory.getMonster(8910001);
                        banbanMap.resetFully();
                        banbanMap.spawnMonsterOnGroudBelow(banban, new Point(332, 245));
                        break;
                    }
                    case 8500001: {
                        for (int i = 0; i < this.getSummons().size(); i++) {
                            if (this.getSummons().get(0) == 8500009) {
                                monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8500009), new Point(-562, monster.getTruePosition().y - 1));
                            }
                            if (this.getSummons().get(1) == 8500009) {
                                monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8500009), new Point(-9, monster.getTruePosition().y - 1));
                            }
                            if (this.getSummons().get(2) == 8500009) {
                                monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8500009), new Point(662, monster.getTruePosition().y - 1));
                            }
                        }
                        break;
                    }
                    default: {
                        for (Integer mobId : getSummons()) {
                            MapleMonster toSpawn = null;
                            try {
                                toSpawn = MapleLifeFactory.getMonster(mobId);
                            } catch (RuntimeException e) {
                                continue;
                            }
                            if (toSpawn == null) {
                                continue;
                            }
                            if (skillLevel == 221) {
                                toSpawn = MapleLifeFactory.getMonster(getSummons().get(monster.getSummonCount()));
                            }
                            toSpawn.setPosition(monster.getTruePosition());
                            int ypos = (int) monster.getTruePosition().getY(), xpos = (int) monster.getTruePosition().getX();

                            switch (mobId) {
                                case 8500003: {
                                    toSpawn.setFh((int) Math.ceil(Math.random() * 19.0));
                                    ypos = -590;
                                    break;
                                }
                                case 8920004:
                                case 8500004: {
                                    xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 1000.0) - 500);
                                    ypos = (int) monster.getTruePosition().getY();
                                    break;
                                }
                                case 8510100: {
                                    if (Math.ceil(Math.random() * 5) == 1) {
                                        ypos = 78;
                                        xpos = (int) (0 + Math.ceil(Math.random() * 5)) + ((Math.ceil(Math.random() * 2) == 1) ? 180 : 0);
                                    } else {
                                        xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 1000.0) - 500);
                                    }
                                    break;
                                }
                            }

                            boolean changeSpawn = false;
                            switch (monster.getId()) {
                                case 8880140:
                                case 8880141:
                                case 8880142:
                                case 8880150:
                                case 8880151:
                                case 8880155: {
                                    int a1 = toSpawn.getId();
                                    switch (toSpawn.getId()) {
                                        case 8880165:
                                        case 8880168:
                                        case 8880169:
                                        case 8880175:
                                        case 8880178:
                                        case 8880179: { // 악몽의 나비
                                            if (monster.getId() > 8880149) {
                                                toSpawn = MapleLifeFactory.getMonster(a1 + 10);
                                                a1 = toSpawn.getId();
                                            }
                                            xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 2000.0) - 500);
                                            if (player.getMap().getMonsterById(a1) != null) {
                                                if (player.getMap().getLucidButterFlyCount() > (monster.getId() > 8880149 ? 9 : 4)) {
                                                    changeSpawn = true;
                                                }
                                            }
                                            break;
                                        }
                                        case 8880157:
                                        case 8880164:
                                        case 8880184:
                                        case 8880185: { // 악몽의 독 버섯
                                            xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 2000.0) - 500);
                                            if (player.getMap().getMonsterById(a1) != null) {
                                                if (player.getMap().countMonsterById(a1) > 1) {
                                                    changeSpawn = true;
                                                }
                                            }
                                            break;
                                        }
                                        case 8880160:
                                        case 8880161:
                                        case 8880170:
                                        case 8880171:
                                        case 8880180:
                                        case 8880181:
                                        case 8880182:
                                        case 8880183:
                                        case 8880186:
                                        case 8880187:
                                        case 8880188:
                                        case 8880189: { // 악몽의 골렘
                                            int[][] footHold = {
                                                {72, -693}, {529, -685}, {1098, -619},
                                                {705, -490}, {152, -550}, {378, -375},
                                                {118, -267}, {292, -215}, {789, -194},
                                                {970, -331}, {1144, -143}
                                            };
                                            if (monster.getId() > 8880149) {
                                                int[] pPos = footHold[(int) Math.floor(Math.random() * footHold.length)];
                                                xpos = pPos[0];
                                                ypos = pPos[1];
                                            } else {
                                                xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 2000.0) - 500);
                                            }
                                            if (player.getMap().getMonsterById(8880161) != null) {
                                                if (player.getMap().getLucidGolemCount() > (monster.getId() > 8880149 ? 9 : 4)) {
                                                    changeSpawn = true;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            if (changeSpawn) {
                                toSpawn = MapleLifeFactory.getMonster(8880162); // 속박의 꽃
                            }
                            switch (monster.getMap().getId()) {
                                case 220080001:
                                    if (xpos < -890) {
                                        xpos = (int) (-890 + Math.ceil(Math.random() * 150));
                                    } else if (xpos > 230) {
                                        xpos = (int) (230 - Math.ceil(Math.random() * 150));
                                    }
                                    break;
                                case 230040420:
                                    if (xpos < -239) {
                                        xpos = (int) (-239 + Math.ceil(Math.random() * 150));
                                    } else if (xpos > 371) {
                                        xpos = (int) (371 - Math.ceil(Math.random() * 150));
                                    }
                                    break;
                            }

                            switch (toSpawn.getId()) {
                                case 8870005:
                                case 8870105: {
                                    xpos = (Randomizer.rand(0, 1) == 0 ? -500 : 1000);
                                    break;
                                }
                            }

                            monster.getMap().spawnMonsterWithEffect(toSpawn, getSpawnEffect(), monster.getMap().calcPointBelow(new Point(xpos, ypos - 1)), option);
                        }
                    }
                }
                break;
            }
        }
        if (stats.size() > 0 && monster != null) {
            if (lt != null && rb != null && skill) {
                for (MapleMapObject mons : getObjectsInRange(monster, MapleMapObjectType.MONSTER)) {
                    if (stats.containsKey(MonsterTemporaryStat.Invincible)) {
                        break;
                    } else {
                        ((MapleMonster) mons).applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection, option);
                    }
                }
                /*
                for (MapleMapObject mons : monster.getMap().getAllMonster()) {
                    if (stats.containsKey(MonsterTemporaryStat.Invincible)) {
                        if (((MapleMonster) mons).getId() != 8850010) {
                            if (((MapleMonster) mons).getId() == 8850011) {
                                ((MapleMonster) mons).applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection, option);
                            }
                        }
                    }
                }
                 */
            } else {
                monster.applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection, option);
            }
        }
        if (monster != null) {
            switch (monster.getId()) {
                case 8870100: {
                    if (skillLevel == 251) {
                        stats.put(MonsterTemporaryStat.ExchangeAttack, Integer.valueOf(x));
                        monster.applyMonsterBuff(stats, getSkillId(), Integer.MAX_VALUE, this, reflection, option);
                    }
                    break;
                }
                case 8850011:
                case 8910000: {
                    if (skillLevel == 221 || skillLevel == 222) {
                        stats.put(MonsterTemporaryStat.ExchangeAttack, Integer.valueOf(x));
                        if (skillLevel == 221) {
                            monster.setSummonCount(monster.getSummonCount() + 1);
                        }
                        monster.applyMonsterBuff(stats, getSkillId(), Integer.MAX_VALUE, this, reflection, option);
                    }
                    break;
                }
                case 8930000: {
                    if (skillLevel == 10) {
                        final MobSkill ms = this;
                        Timer.MapTimer.getInstance().schedule(new Runnable() {
                            public void run() {
                                stats.put(MonsterTemporaryStat.ExchangeAttack, Integer.valueOf(x));
                                monster.applyMonsterBuff(stats, getSkillId(), 15000, ms, reflection, option);
                            }
                        }, 4000);
                    }
                    break;
                }
                case 9450066: {
                    stats.put(MonsterTemporaryStat.ExchangeAttack, Integer.valueOf(x));
                    monster.applyMonsterBuff(stats, getSkillId(), Integer.MAX_VALUE, this, reflection, option);
                    for (MapleMonster mm : monster.getMap().getAllMonstersThreadsafe()) {
                        if (mm.getId() == 9450060) {
                            if (mm.getTruePosition().y > -400) {
                                for (int i = 0; i < 5; i++) {
                                    monster.getMap().spawnMonster_sSack(MapleLifeFactory.getMonster(9450053), mm.getTruePosition(), -2);
                                }
                                monster.getMap().spawnMonster_sSack(MapleLifeFactory.getMonster(9450058), mm.getTruePosition(), -2);
                                mm.damage(player, mm.getMobMaxHp(), false);
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (disease != null && player != null) {
            if (lt != null && rb != null && skill && monster != null) {
                for (MapleCharacter chr : getPlayersInRange(monster, player)) {
                    chr.giveDebuff(disease, this, option);
                }
            } else {
                player.giveDebuff(disease, this, option);
            }
        }
        if (monster != null) {
            monster.setMp(monster.getMp() - getMpCon());
            monster.usedSkill(skillId, skillLevel, cooltime);
        }
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getMpCon() {
        return mpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(toSummon);
    }

    public int getSpawnEffect() {
        return spawnEffect;
    }

    public int getHP() {
        return hp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getDuration() {
        return duration;
    }

    public long getCoolTime() {
        return cooltime;
    }

    public Point getLt() {
        return lt;
    }

    public Point getRb() {
        return rb;
    }

    public int getLimit() {
        return limit;
    }

    public boolean makeChanceResult() {
        return prop >= 1.0 || Math.random() < prop;
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point mylt, myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        final Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    private List<MapleCharacter> getPlayersInRange(MapleMonster monster, MapleCharacter player) {
        final Rectangle bounds = calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
        List<MapleCharacter> players = new ArrayList<MapleCharacter>();
        players.add(player);
        return monster.getMap().getPlayersInRectAndInList(bounds, players);
    }

    private List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
        final Rectangle bounds = calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
        List<MapleMapObjectType> objectTypes = new ArrayList<MapleMapObjectType>();
        objectTypes.add(objectType);
        return monster.getMap().getMapObjectsInRect(bounds, objectTypes);
    }

    public void setSkillAfter(int i) {
        this.skillAfter = i;
    }

    public int getSkillAfter() {
        return this.skillAfter;
    }

    public void setScreenAttack(final MapleCharacter tUser, final MapleMonster tMob, final int tX, final int tDistance, final int tDelay) {
        int nDistance = Math.abs(tMob.getTruePosition().x - tUser.getTruePosition().x);
        Timer.MapTimer.getInstance().schedule(new Runnable() {
            public void run() {
                if (nDistance < tDistance) {
                    tUser.addHP(-tX);
                    tUser.getClient().sendPacket(CField.OnDamageByUser(tUser.getId(), 1, tX));
                    tMob.getMap().broadcastMessage(tUser, CField.OnDamageByUser(tUser.getId(), 1, tX), tUser.getTruePosition());
                }
            }
        }, tDelay);
    }
}
