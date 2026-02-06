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
package server.maps;

import com.mysql.jdbc.Connection;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import constants.GameConstants;

import database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MaplePortal;
import server.Randomizer;
import server.life.AbstractLoadedMapleLife;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.maps.MapleNodes.DirectionInfo;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;
import tools.Pair;
import tools.StringUtil;

public class MapleMapFactory {

    private final MapleDataProvider source = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Map.wz"));
    private final MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")).getData("Map.img");
    private final HashMap<Integer, MapleMap> maps = new HashMap<Integer, MapleMap>();
    private final HashMap<Integer, MapleMap> instanceMap = new HashMap<Integer, MapleMap>();
    private final ReentrantLock lock = new ReentrantLock();
    private static final Map<Integer, List<AbstractLoadedMapleLife>> customLife = new HashMap<>();
    private int channel;

    public static int loadCustomLife() {
        customLife.clear();
        Connection con = null;
        try {
            con = (Connection) DatabaseConnection.getConnection();
            try (java.sql.PreparedStatement ps = con.prepareStatement("SELECT * FROM `wz_customlife`"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final int mapid = rs.getInt("mid");
                    final AbstractLoadedMapleLife myLife = loadLife(rs.getInt("dataid"), rs.getInt("f"), rs.getByte("hide") > 0, rs.getInt("fh"), rs.getInt("cy"), rs.getInt("rx0"), rs.getInt("rx1"), rs.getInt("x"), rs.getInt("y"), rs.getString("type"), rs.getInt("mobtime"));
                    if (myLife == null) {
                        continue;
                    }
                    final List<AbstractLoadedMapleLife> entries = customLife.get(mapid);
                    final List<AbstractLoadedMapleLife> collections = new ArrayList<>();
                    if (entries == null) {
                        collections.add(myLife);
                        customLife.put(mapid, collections);
                    } else {
                        collections.addAll(entries); //re-add
                        collections.add(myLife);
                        customLife.put(mapid, collections);
                    }
                }
            }
            con.close();
            return customLife.size();
        } catch (SQLException e) {
            System.out.println("Error loading custom life..." + e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return -1;
    }

    public MapleMapFactory(int channel) {
        this.channel = channel;
    }

    public final MapleMap getMap(final int mapid) {
        return getMap(mapid, true, true, true);
    }

    private static AbstractLoadedMapleLife loadLife(int id, int f, boolean hide, int fh, int cy, int rx0, int rx1, int x, int y, String type, int mtime) {
        final AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(id, type);
        if (myLife == null) {
            System.out.println("Custom npc " + id + " is null...");
            return null;
        }
        myLife.setCy(cy);
        myLife.setF(f);
        myLife.setFh(fh);
        myLife.setRx0(rx0);
        myLife.setRx1(rx1);
        myLife.setPosition(new Point(x, y));
        myLife.setHide(hide);
        myLife.setMTime(mtime);
        myLife.setCType(type);
        return myLife;
    }

    //backwards-compatible
    public final MapleMap getMap(final int mapid, final boolean respawns, final boolean npcs) {
        return getMap(mapid, respawns, npcs, true);
    }

    public final MapleMap getMap(final int mapid, final boolean respawns, final boolean npcs, final boolean reactors) {
        Integer omapid = Integer.valueOf(mapid);
        MapleMap map = maps.get(omapid);
        if (map == null) {
            lock.lock();
            try {
                map = maps.get(omapid);
                if (map != null) {
                    return map;
                }
                MapleData mapData = null;
                try {
                    mapData = source.getData(getMapName(mapid));
                } catch (Exception e) {
                    return null;
                }
                if (mapData == null) {
                    return null;
                }
                MapleData link = mapData.getChildByPath("info/link");
                if (link != null) {
                    mapData = source.getData(getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
                }

                float monsterRate = 0;
                if (respawns) {
                    MapleData mobRate = mapData.getChildByPath("info/mobRate");
                    if (mobRate != null) {
                        monsterRate = ((Float) mobRate.getData()).floatValue();
                    }
                }
                map = new MapleMap(mapid, channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
                loadPortals(map, mapData.getChildByPath("portal"));
                map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
                map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
                map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
                map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
                List<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
                Point lBound = new Point();
                Point uBound = new Point();
                MapleFoothold fh;
                for (MapleData footRoot : mapData.getChildByPath("foothold")) {
                    for (MapleData footCat : footRoot) {
                        for (MapleData footHold : footCat) {
                            fh = new MapleFoothold(new Point(
                                    MapleDataTool.getInt(footHold.getChildByPath("x1"), 0), MapleDataTool.getInt(footHold.getChildByPath("y1"), 0)), new Point(
                                    MapleDataTool.getInt(footHold.getChildByPath("x2"), 0), MapleDataTool.getInt(footHold.getChildByPath("y2"), 0)), Integer.parseInt(footHold.getName()));
                            fh.setPrev((short) MapleDataTool.getInt(footHold.getChildByPath("prev"), 0));
                            fh.setNext((short) MapleDataTool.getInt(footHold.getChildByPath("next"), 0));

                            if (fh.getX1() < lBound.x) {
                                lBound.x = fh.getX1();
                            }
                            if (fh.getX2() > uBound.x) {
                                uBound.x = fh.getX2();
                            }
                            if (fh.getY1() < lBound.y) {
                                lBound.y = fh.getY1();
                            }
                            if (fh.getY2() > uBound.y) {
                                uBound.y = fh.getY2();
                            }
                            allFootholds.add(fh);
                        }
                    }
                }
                MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
                for (MapleFoothold foothold : allFootholds) {
                    fTree.insert(foothold);
                }
                map.setFootholds(fTree);
                if (map.getTop() == 0) {
                    map.setTop(lBound.y);
                }
                if (map.getBottom() == 0) {
                    map.setBottom(uBound.y);
                }
                if (map.getLeft() == 0) {
                    map.setLeft(lBound.x);
                }
                if (map.getRight() == 0) {
                    map.setRight(uBound.x);
                }
                int bossid = -1;
                String msg = null;
                if (mapData.getChildByPath("info/timeMob") != null) {
                    bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
                    msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
                }

                // load life data (npc, monsters)
                List<Point> herbRocks = new ArrayList<Point>();
                int lowestLevel = 200, highestLevel = 0;
                String type, limited;
                AbstractLoadedMapleLife myLife;
                for (MapleData life : mapData.getChildByPath("life")) {
                    type = MapleDataTool.getString(life.getChildByPath("type"));
                    limited = MapleDataTool.getString("limitedname", life, "");
                    if ((npcs || !type.equals("n")) && !limited.equals("Stage0")) { //alien pq stuff
                        String mID = MapleDataTool.getString(life.getChildByPath("id"));
                        if (mID.equals("1110100") && Randomizer.nextInt(3) < 1) {
                            mID = "1110130";
                        }
                        myLife = loadLife(life, mID, type);
                        if (myLife instanceof MapleMonster && !GameConstants.isNoSpawn(mapid)) {
                            final MapleMonster mob = (MapleMonster) myLife;
                            herbRocks.add(map.addMonsterSpawn(mob, MapleDataTool.getInt("mobTime", life, 0), (byte) MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null).getPosition());
                            if (mob.getStats().getLevel() > highestLevel && !mob.getStats().isBoss()) {
                                highestLevel = mob.getStats().getLevel();
                            }
                            if (mob.getStats().getLevel() < lowestLevel && !mob.getStats().isBoss()) {
                                lowestLevel = mob.getStats().getLevel();
                            }
                        } else if (myLife instanceof MapleNPC) {
                            map.addMapObject(myLife);
                        }
                    }
                    final List<AbstractLoadedMapleLife> custom = customLife.get(mapid);
                    if (custom != null) {
                        for (AbstractLoadedMapleLife n : custom) {
                            if (n.getCType().equals("n")) {
                                map.addMapObject(n);
                            } else if (n.getCType().equals("m")) {
                                final MapleMonster monster = (MapleMonster) n;
                                map.addMonsterSpawn(monster, n.getMTime(), (byte) -1, null);
                            }
                        }
                    }
                }
                addAreaBossSpawn(map);
                map.setCreateMobInterval((short) MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
                map.loadMonsterRate(true);
                map.setNodes(loadNodes(mapid, mapData));
                //load reactor data
                String id;
                if (reactors && mapData.getChildByPath("reactor") != null) {
                    for (MapleData reactor : mapData.getChildByPath("reactor")) {
                        id = MapleDataTool.getString(reactor.getChildByPath("id"));
                        if (id != null) {
                            if (Integer.parseInt(id) == 9702005) {
                                continue;
                            }
                            map.spawnReactor(loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                        }
                    }
                }
                map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
                map.setUserEnter(mapid == GameConstants.JAIL ? "jail" : MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
                if (reactors && herbRocks.size() > 0 && highestLevel >= 30 && map.getFirstUserEnter().equals("") && map.getUserEnter().equals("")) {
                    final List<Integer> allowedSpawn = new ArrayList<Integer>(24);
                    allowedSpawn.add(100011);
                    allowedSpawn.add(200011);
                    if (highestLevel >= 100) {
                        for (int i = 0; i < 10; i++) {
                            for (int x = 0; x < 4; x++) { //to make heartstones rare
                                allowedSpawn.add(100000 + i);
                                allowedSpawn.add(200000 + i);
                            }
                        }
                    } else {
                        for (int i = (lowestLevel % 10 > highestLevel % 10 ? 0 : (lowestLevel % 10)); i < (highestLevel % 10); i++) {
                            for (int x = 0; x < 4; x++) { //to make heartstones rare
                                allowedSpawn.add(100000 + i);
                                allowedSpawn.add(200000 + i);
                            }
                        }
                    }
                }
                java.sql.Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    con = DatabaseConnection.getConnection();
                    String sql = "SELECT * FROM `spawn` WHERE mapid = " + mapid + " AND type = 'r'";
                    if (con != null) {
                        ps = con.prepareStatement(sql);
                        if (ps != null) {
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                int life = rs.getInt("lifeid");
                                MapleReactor reactor = new MapleReactor(MapleReactorFactory.getReactor(life), life);
                                if (reactor == null) {
                                    System.out.println("리엑터 null 발생!! " + reactor.getName() + ", " + life);
                                    continue;
                                }
                                reactor.setDelay(rs.getInt("mobTime"));
                                reactor.setPosition(new Point(rs.getInt("rx0"), rs.getInt("cy")));
                                map.addMapObject(reactor);
                            }
                            rs.close();
                        }
                        ps.close();
                    }
                    con.close();
                } catch (Exception e) {
                    System.err.println("[오류] 리엑터를 DB로부터 불러오는데 오류가 발생했습니다.");
                    e.printStackTrace();
                } finally {
                    try {
                        if (con != null) {
                            con.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
                try {
                    con = DatabaseConnection.getConnection();
                    String sql = "SELECT * FROM `spawn` WHERE mapid = " + mapid + " AND type = 'n'";
                    if (con != null) {
                        ps = con.prepareStatement(sql);
                        if (ps != null) {
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                MapleNPC npc = MapleLifeFactory.getNPC(rs.getInt("lifeid"));
                                if (npc == null) {
                                    System.out.println("NPC Null 발생!! " + npc + ", " + rs.getInt("lifeid"));
                                    continue;
                                }
                                npc.setRx0(rs.getInt("rx0"));
                                npc.setRx1(rs.getInt("rx1"));
                                npc.setCy(rs.getInt("cy"));
                                npc.setF(rs.getInt("dir"));
                                npc.setFh(rs.getInt("fh"));
                                npc.setPosition(new Point(npc.getRx0(), npc.getCy()));
                                map.addMapObject(npc);
                            }
                            rs.close();
                        }
                        ps.close();
                    }
                    con.close();
                } catch (Exception e) {
                    System.err.println("[오류] 엔피시를 DB로부터 불러오는데 오류가 발생했습니다.");
                    e.printStackTrace();
                } finally {
                    try {
                        if (con != null) {
                            con.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
                try {
                    map.setMapName(MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(omapid)), ""));
                    map.setStreetName(MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(omapid)), ""));
                } catch (Exception e) {
                    map.setMapName("");
                    map.setStreetName("");
                }
                map.setClock(mapData.getChildByPath("clock") != null); //clock was changed in wz to have x,y,width,height
                map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
                map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
                map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
                map.setPersonalShop(MapleDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0);
                map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
                map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
                map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
                map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
                map.setForcedReturnMap(mapid == 0 ? 999999999 : (mapid > 910001000 && mapid < 910001011) ? 910001000 : MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
                map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
                map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
                map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1));
                map.setFieldType(MapleDataTool.getString(mapData.getChildByPath("info/fieldType"), ""));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
                map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
                map.setBarrier(MapleDataTool.getInt(mapData.getChildByPath("info/barrier"), 0));
                map.setBarrierArc(MapleDataTool.getInt(mapData.getChildByPath("info/barrierArc"), 0));
                maps.put(omapid, map);
            } finally {
                lock.unlock();
            }
        }
        return map;
    }

    public MapleMap getInstanceMap(final int instanceid) {
        return instanceMap.get(instanceid);
    }

    public void removeInstanceMap(final int instanceid) {
        lock.lock();
        try {
            if (isInstanceMapLoaded(instanceid)) {
                instanceMap.remove(instanceid);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeMap(final int instanceid) {
        lock.lock();
        try {
            if (isMapLoaded(instanceid)) {
                maps.remove(instanceid);
            }
        } finally {
            lock.unlock();
        }
    }

    public MapleMap CreateInstanceMap(int mapid, boolean respawns, boolean npcs, boolean reactors, int instanceid) {
        lock.lock();
        try {
            if (isInstanceMapLoaded(instanceid)) {
                return getInstanceMap(instanceid);
            }
        } finally {
            lock.unlock();
        }
        MapleData mapData = null;
        try {
            mapData = source.getData(getMapName(mapid));
        } catch (Exception e) {
            return null;
        }
        if (mapData == null) {
            return null;
        }
        MapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            mapData = source.getData(getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
        }

        float monsterRate = 0;
        if (respawns) {
            MapleData mobRate = mapData.getChildByPath("info/mobRate");
            if (mobRate != null) {
                monsterRate = ((Float) mobRate.getData()).floatValue();
            }
        }
        MapleMap map = new MapleMap(mapid, channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
        loadPortals(map, mapData.getChildByPath("portal"));
        map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
        map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
        map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
        map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
        List<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
        Point lBound = new Point();
        Point uBound = new Point();
        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
                for (MapleData footHold : footCat) {
                    MapleFoothold fh = new MapleFoothold(new Point(
                            MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(
                            MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));
                    fh.setPrev((short) MapleDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short) MapleDataTool.getInt(footHold.getChildByPath("next")));

                    if (fh.getX1() < lBound.x) {
                        lBound.x = fh.getX1();
                    }
                    if (fh.getX2() > uBound.x) {
                        uBound.x = fh.getX2();
                    }
                    if (fh.getY1() < lBound.y) {
                        lBound.y = fh.getY1();
                    }
                    if (fh.getY2() > uBound.y) {
                        uBound.y = fh.getY2();
                    }
                    allFootholds.add(fh);
                }
            }
        }
        MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
        for (MapleFoothold fh : allFootholds) {
            fTree.insert(fh);
        }
        map.setFootholds(fTree);
        if (map.getTop() == 0) {
            map.setTop(lBound.y);
        }
        if (map.getBottom() == 0) {
            map.setBottom(uBound.y);
        }
        if (map.getLeft() == 0) {
            map.setLeft(lBound.x);
        }
        if (map.getRight() == 0) {
            map.setRight(uBound.x);
        }
        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }

        // load life data (npc, monsters)
        String type, limited;
        AbstractLoadedMapleLife myLife;

        for (MapleData life : mapData.getChildByPath("life")) {
            type = MapleDataTool.getString(life.getChildByPath("type"));
            limited = MapleDataTool.getString("limitedname", life, "");
            if ((npcs || !type.equals("n")) && limited.equals("")) {
                myLife = loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);

                if (myLife instanceof MapleMonster && !GameConstants.isNoSpawn(mapid)) {
                    final MapleMonster mob = (MapleMonster) myLife;
                    int mobTime = MapleDataTool.getInt("mobTime", life, 0);
                    map.addMonsterSpawn(mob, mobTime, (byte) MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null);

                } else if (myLife instanceof MapleNPC) {
                    map.addMapObject(myLife);
                }
            }
        }
        addAreaBossSpawn(map);
        map.setCreateMobInterval((short) MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
        map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
        map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
        map.loadMonsterRate(true);
        map.setNodes(loadNodes(mapid, mapData));

        //load reactor data
        String id;
        if (reactors && mapData.getChildByPath("reactor") != null) {
            for (MapleData reactor : mapData.getChildByPath("reactor")) {
                id = MapleDataTool.getString(reactor.getChildByPath("id"));
                if (id != null) {
                    if (Integer.parseInt(id) == 9702005) {
                        continue;
                    }
                    map.spawnReactor(loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                }
            }
        }
        try {
            map.setMapName(MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(mapid)), ""));
            map.setStreetName(MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(mapid)), ""));
        } catch (Exception e) {
            map.setMapName("");
            map.setStreetName("");
        }
        map.setClock(MapleDataTool.getInt(mapData.getChildByPath("info/clock"), 0) > 0);
        map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
        map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
        map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
        map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
        map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
        map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
        map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
        map.setForcedReturnMap((mapid > 910001000 && mapid < 910001011) ? 910001000 : MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
        map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
        map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
        map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
        map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
        map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1));
        map.setFieldType(MapleDataTool.getString(mapData.getChildByPath("info/fieldType"), ""));
        map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
        map.setBarrier(MapleDataTool.getInt(mapData.getChildByPath("info/barrier"), 0));
        map.setBarrierArc(MapleDataTool.getInt(mapData.getChildByPath("info/barrierArc"), 0));
        map.setInstanceId(instanceid);
        lock.lock();
        try {
            instanceMap.put(instanceid, map);
        } finally {
            lock.unlock();
        }
        return map;
    }

    public int getLoadedMaps() {
        return maps.size();
    }

    public boolean isMapLoaded(int mapId) {
        return maps.containsKey(mapId);
    }

    public boolean isInstanceMapLoaded(int instanceid) {
        return instanceMap.containsKey(instanceid);
    }

    public void clearLoadedMap() {
        lock.lock();
        try {
            maps.clear();
        } finally {
            lock.unlock();
        }
    }

    public List<MapleMap> getAllLoadedMaps() {
        List<MapleMap> ret = new ArrayList<MapleMap>();
        lock.lock();
        try {
            ret.addAll(maps.values());
            ret.addAll(instanceMap.values());
        } finally {
            lock.unlock();
        }
        return ret;
    }

    public Collection<MapleMap> getAllMaps() {
        return maps.values();
    }

    private AbstractLoadedMapleLife loadLife(MapleData life, String id, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type);
        if (myLife == null) {
            return null;
        }
        myLife.setCy(MapleDataTool.getInt(life.getChildByPath("cy")));
        MapleData dF = life.getChildByPath("f");
        if (dF != null) {
            myLife.setF(MapleDataTool.getInt(dF));
        }
        myLife.setFh(MapleDataTool.getInt(life.getChildByPath("fh")));
        myLife.setRx0(MapleDataTool.getInt(life.getChildByPath("rx0")));
        myLife.setRx1(MapleDataTool.getInt(life.getChildByPath("rx1")));
        myLife.setPosition(new Point(MapleDataTool.getInt(life.getChildByPath("x")), MapleDataTool.getInt(life.getChildByPath("y"))));

        if (MapleDataTool.getInt("hide", life, 0) == 1 && myLife instanceof MapleNPC) {
            myLife.setHide(true);
//		} else if (hide > 1) {
//			System.err.println("Hide > 1 ("+ hide +")");
        }
        return myLife;
    }

    private final MapleReactor loadReactor(final MapleData reactor, final String id, final byte FacingDirection) {
        final MapleReactor myReactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(id)), Integer.parseInt(id));

        myReactor.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(MapleDataTool.getInt(reactor.getChildByPath("x")), MapleDataTool.getInt(reactor.getChildByPath("y"))));
        myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
        myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));

        return myReactor;
    }

    private String getMapName(int mapid) {
        String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
        StringBuilder builder = new StringBuilder("Map/Map");
        builder.append(mapid / 100000000);
        builder.append("/");
        builder.append(mapName);
        builder.append(".img");

        mapName = builder.toString();
        return mapName;
    }

    private String getMapStringName(int mapid) {
        StringBuilder builder = new StringBuilder();
        if (mapid < 100000000) {
            builder.append("maple");
        } else if ((mapid >= 100000000 && mapid < 200000000) || mapid / 100000 == 5540) {
            builder.append("victoria");
        } else if (mapid >= 200000000 && mapid < 300000000) {
            builder.append("ossyria");
        } else if (mapid >= 300000000 && mapid < 400000000) {
            builder.append("3rd");
        } else if (mapid >= 500000000 && mapid < 510000000) {
            builder.append("thai");
        } else if (mapid >= 555000000 && mapid < 556000000) {
            builder.append("SG");
        } else if (mapid >= 540000000 && mapid < 600000000) {
            builder.append("singapore");
        } else if (mapid >= 682000000 && mapid < 683000000) {
            builder.append("HalloweenGL");
        } else if (mapid >= 600000000 && mapid < 670000000) {
            builder.append("MasteriaGL");
        } else if (mapid >= 677000000 && mapid < 678000000) {
            builder.append("Episode1GL");
        } else if (mapid >= 670000000 && mapid < 682000000) {
            builder.append("weddingGL");
        } else if (mapid >= 687000000 && mapid < 688000000) {
            builder.append("Gacha_GL");
        } else if (mapid >= 689000000 && mapid < 690000000) {
            builder.append("CTF_GL");
        } else if (mapid >= 683000000 && mapid < 684000000) {
            builder.append("event");
        } else if (mapid >= 684000000 && mapid < 685000000) {
            builder.append("event_5th");
        } else if (mapid >= 700000000 && mapid < 700000300) {
            builder.append("wedding");
        } else if (mapid >= 701000000 && mapid < 701020000) {
            builder.append("china");
        } else if ((mapid >= 702090000 && mapid <= 702100000) || (mapid >= 740000000 && mapid < 741000000)) {
            builder.append("TW");
        } else if (mapid >= 702000000 && mapid < 742000000) {
            builder.append("CN");
        } else if (mapid >= 800000000 && mapid < 900000000) {
            builder.append("jp");
        } else {
            builder.append("etc");
        }
        builder.append("/");
        builder.append(mapid);

        return builder.toString();
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    private void addAreaBossSpawn(MapleMap map) {
        int mobID = -1;
        int respawnTime = -1;
        String spawnMsg = null;
        boolean shouldSpawn = true;
        Point spawnPosition = null;

        switch (map.getId()) {
            case 104010200: {
                mobID = 2220000;
                respawnTime = 10;
                spawnPosition = new Point(250, 35);
                spawnMsg = "서늘한 기운이 감돌면서 마노가 나타났습니다.";
                break;
            }
            case 102020500: {
                mobID = 3220000;
                respawnTime = 15;
                spawnPosition = new Point(1260, 2145);
                spawnMsg = "바위산을 울리는 발걸음 소리와 함께 스텀피가 나타났습니다.";
                break;
            }
            case 100020101: {
                mobID = 6130101;
                respawnTime = 30;
                spawnPosition = new Point(-505, 215);
                spawnMsg = "어디선가 커다란 버섯이 나타났습니다.";
                break;
            }
            case 100020301: {
                mobID = 8220007;
                respawnTime = 30;
                spawnPosition = new Point(170, -685);
                spawnMsg = "어디선가 커다란 파란 버섯이 나타났습니다.";
                break;
            }
            case 100020401: {
                mobID = 6300005;
                respawnTime = 30;
                spawnPosition = new Point(177, -625);
                spawnMsg = "어디선가 음산한 기운을 풍기는 커다란 버섯이 나타났습니다.";
                break;
            }
            case 103030400: {
                mobID = 6220000;
                respawnTime = 30;
                spawnPosition = new Point(-831, 109);
                spawnMsg = "늪 속에서 카다란 악어 다일이 올라왔습니다.";
                break;
            }
            case 120030500: {
                mobID = 5220001;
                respawnTime = 30;
                spawnPosition = new Point(-355, 179);
                spawnMsg = "모래 사장에 수상한 소라 껍질이 나타났습니다.";
                break;
            }
            case 200010302: {
                mobID = 8220000;
                respawnTime = 30;
                spawnPosition = new Point(274, 83);
                spawnMsg = "검은 돌개 바람과 함께 엘리쟈가 나타났습니다.";
                break;
            }
            case 220050200: {
                mobID = 5220003;
                respawnTime = 30;
                spawnPosition = new Point(-407, 252);
                spawnMsg = "짹각짹각! 불규칙한 시계 소리와 함께 타이머가 나타났습니다.";
                break;
            }
            case 260010201: {
                mobID = 3220001;
                respawnTime = 60;
                spawnPosition = new Point(80, 275);
                spawnMsg = "모래 먼지 속에서 데우가 천천히 모습을 드러냈습니다.";
                break;
            }
            case 240010500: {
                mobID = 9300481;
                respawnTime = 60;
                spawnPosition = new Point(-60, 422);
                spawnMsg = "풀 숲 사이로 마스터 버크가 나타났습니다.";
                break;
            }
            case 240040401: {
                mobID = 8220003;
                respawnTime = 60;
                spawnPosition = new Point(255, 1139);
                spawnMsg = "차가운 바람과 함께 협곡 너머에서 레비아탄이 모습을 드러냈습니다.";
                break;
            }
            case 211050000: {
                mobID = 6090001;
                respawnTime = 60;
                spawnPosition = new Point(-526, -397);
                spawnMsg = "얼음 속에 구속 된 마녀가 눈을 떴습니다.";
                break;
            }
            case 230020100: {
                mobID = 4220000;
                respawnTime = 60;
                spawnPosition = new Point(-165, -580);
                spawnMsg = "해초 수풀 사이로 이상한 조개가 나타났습니다.";
                break;
            }
            case 251010102: {
                mobID = 5220004;
                respawnTime = 60;
                spawnPosition = new Point(528, -436);
                spawnMsg = "물 밑에서 스멀 스멀 대왕 지네가 나타났습니다.";
                break;
            }
            case 251010101: {
                mobID = 6090002;
                respawnTime = 60;
                spawnPosition = new Point(666, 123);
                spawnMsg = "두루마리에 몸을 숨겼던 대나무 무사가 모습을 드러냈습니다.";
                break;
            }
            case 250010304: {
                mobID = 7220000;
                respawnTime = 60;
                spawnPosition = new Point(-300, 33);
                spawnMsg = "나즈막한 휘파람 소리와 함께 태륜이 나타났습니다.";
                break;
            }
            case 250010503: {
                mobID = 7220002;
                respawnTime = 60;
                spawnPosition = new Point(239, 543);
                spawnMsg = "주변을 흐르는 요기가 강해졌습니다. 기분 나쁜 고양이 울음 소리가 들립니다.";
                break;
            }
            case 222010310: {
                mobID = 7220001;
                respawnTime = 60;
                spawnPosition = new Point(-10, 93);
                spawnMsg = "달빛이 흐려지면서 긴 여우 울음 소리와 함께 구미호의 기운이 느껴집니다.";
                break;
            }
            case 101040300: {
                mobID = 5220002;
                respawnTime = 60;
                spawnPosition = new Point(411, -232);
                spawnMsg = "푸른 안개가 짙어지면서 파우스트가 나타났습니다.";
                break;
            }
            case 221040400: {
                mobID = 6220001;
                respawnTime = 60;
                spawnPosition = new Point(-2559, 806);
                spawnMsg = "묵직한 기계음을 울리며 제노가 나타났습니다.";
                break;
            }
            case 261030000: {
                mobID = 8220002;
                spawnPosition = new Point(-652, 181);
                spawnMsg = "지하의 어둠 속에서 안광을 빛내며 키메라가 모습을 드러냈습니다.";
                break;
            }
            case 105020401: {
                mobID = 8220008;
                respawnTime = 60;
                spawnPosition = new Point(1149, 407);
                spawnMsg = "정체 불명의 포장 마차가 나타났습니다.";
                break;
            }
            case 222010300: {
                mobID = 6090003;
                spawnPosition = new Point(1388, 153);
                spawnMsg = "고갯길 사이로 기이한 귀곡성이 울립니다. 선비 귀신이 나타났습니다. 선비 귀신의 원한을 풀고 싶다면 돌 탑 위에 삼미호의 꼬리를 바치세요.";
                // 선비 귀신이 원한이 조금 풀렸다. 선비 귀신을 퇴치 할 수 있을 것 같다.
                break;
            }
            case 211041400: {
                mobID = 6090000;
                respawnTime = 60;
                spawnPosition = new Point(1762, -386);
                spawnMsg = "눈보다 차가운 숨결을 지닌 리치가 나타났습니다.";
                break;
            }
            case 105030500: {
                mobID = 8130100;
                respawnTime = 60;
                spawnPosition = new Point(711, 130);
                spawnMsg = "그릉거리는 소리가 들립니다. 사악한 존재가 모습을 드러냈습니다.";
                break;
            }
            case 211040101: {
                mobID = 8220001;
                respawnTime = 60;
                spawnPosition = new Point(-108, -470);
                spawnMsg = "눈 속에서 스키를 신은 유쾌한 스노우맨이 나타났습니다.";
                break;
            }
            case 931000500: {
                mobID = 9304005;
                respawnTime = 60;
                spawnPosition = new Point(-25, -512);
                spawnMsg = "바위 틈 속에서 제이라가 나타났습니다.";
                break;
            }
            case 910150003: {
                int posX[] = {-1904, -2341, -2586, -2924, -3525, -3498, -2331, -2715, -2131, -1893};
                int posY[] = {1, 1, 1, 1, 1, 1, -330, -330, -612, -612};
                for (int i = 0; i < 10; i++) {
                    map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(9300422), new Point(posX[i], posY[i]), new Point(posX[i], posY[i]), new Point(posX[i], posY[i]), 0, "", false);
                }
                break;
            }
            /* 깊은 바다 협곡 2 */
            case 230040100: {
                map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8140555), new Point(-451, 298), new Point(-451, 298), new Point(-451, 298), 0, "", false);
                map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8140555), new Point(-113, 432), new Point(-113, 432), new Point(-113, 432), 0, "", false);
                map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8140555), new Point(-219, 850), new Point(-219, 850), new Point(-219, 850), 0, "", false);
                map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8140555), new Point(-422, 1456), new Point(-422, 1456), new Point(-422, 1456), 0, "", false);
                map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8140555), new Point(-397, 1818), new Point(-397, 1818), new Point(-397, 1818), 0, "", false);
                map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8140555), new Point(-154, 1795), new Point(-154, 1795), new Point(-154, 1795), 0, "", false);
                break;
            }
        }
        if (mobID > 0) {
            map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(mobID), spawnPosition, spawnPosition, spawnPosition, respawnTime, spawnMsg, shouldSpawn);
        }
        HandleMapPattern(map);
    }

    public void HandleMapPattern(final MapleMap nMap) {
        switch (nMap.getId()) {
            /* 오후의 티 테이블 */
            case 105200210: {
                Point mapPattern = new Point(497, 551);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8900003), mapPattern, mapPattern, mapPattern, 0, "", true);
                break;
            }
            /* 심연의 동굴 */
            case 105200410: {
                Point a1 = new Point(-1328, 443);
                Point a2 = new Point(850, 443);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8930002), a1, a1, a1, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8930002), a2, a2, a2, 0, "", true);
                break;
            }
            /* 시계 탑의 근원 */
            case 220080100: {
                Point a1 = new Point(-743, 179);
                Point a2 = new Point(-461, 179);
                Point a3 = new Point(-272, 179);
                Point a4 = new Point(83, 179);
                Point a5 = new Point(247, 179);
                Point a6 = new Point(930, 179);
                nMap.setMobGen(8500003, false);
                nMap.setMobGen(8500004, false);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8500030), a1, a1, a1, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8500031), a2, a2, a2, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8500032), a3, a3, a3, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8500033), a4, a4, a4, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8500031), a5, a5, a5, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8500030), a6, a6, a6, 0, "", true);
                break;
            }
            /* 폭군의 왕좌 */
            case 401060100: {
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880011), new Point(1427, -1347), new Point(1427, -1347), new Point(1427, -1347), 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880012), new Point(1427, -1347), new Point(1427, -1347), new Point(1427, -1347), 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880013), new Point(1427, -1347), new Point(1427, -1347), new Point(1427, -1347), 0, "", true);
                break;
            }
            /* 왕궁 메인 홀 */
            case 410002020: {
                Point p2 = new Point(500, 275);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880624), p2, p2, p2, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880624), p2, p2, p2, 0, "", true);
                break;
            }
            /* 카로테 마천루 */
            case 410006020: {
                Point p1 = new Point(586, 80);
                Point p2 = new Point(586, 398);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880801), p1, p1, p1, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880801), p2, p2, p2, 0, "", true);
                break;
            }
            /* 격전의 마천루 */
            case 410006060: {
                Point p1 = new Point(568, 83);
                Point p2 = new Point(568, 399);
                Point p3 = new Point(-740, 399);
                Point p4 = new Point(1760, 399);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880801), p1, p1, p1, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880801), p2, p2, p2, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880813), p3, p3, p3, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880812), p4, p4, p4, 0, "", true);
                break;
            }
            /* 욕망의 제단 */
            case 450010930: {
                for (int i = -800; i < 801; i += 250) {
                    Point pXY = new Point(i, 266);
                    nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880408), pXY, pXY, pXY, 0, "", true);
                }
                break;
            }
            /* 어둠의 왕좌 */
            case 450013930: {
                Point pXY = new Point(0, 88);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880516), pXY, pXY, pXY, 0, "", true);
                break;
            }
            /* 시작과 끝의 경계 */
            case 450013950: {
                Point pXY = new Point(0, 85);
                Point tXY = new Point(-460, 85);
                Point nXY = new Point(590, 85);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880509), tXY, tXY, tXY, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880509), nXY, nXY, nXY, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880521), pXY, pXY, pXY, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880522), pXY, pXY, pXY, 0, "", true);
                break;
            }
            /* 존재하지 않는 공간 */
            case 450013970: {
                Point pXY = new Point(0, 218);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880528), pXY, pXY, pXY, 0, "", true);
                nMap.addAreaMonsterSpawn(MapleLifeFactory.getMonster(8880529), pXY, pXY, pXY, 0, "", true);
                break;
            }
        }
    }

    private void loadPortals(MapleMap map, MapleData port) {
        if (port == null) {
            return;
        }
        int nextDoorPortal = 0x80;
        for (MapleData portal : port.getChildren()) {
            MaplePortal myPortal = new MaplePortal(MapleDataTool.getInt(portal.getChildByPath("pt")));
            myPortal.setName(MapleDataTool.getString(portal.getChildByPath("pn")));
            myPortal.setTarget(MapleDataTool.getString(portal.getChildByPath("tn")));
            myPortal.setTargetMapId(MapleDataTool.getInt(portal.getChildByPath("tm")));
            myPortal.setPosition(new Point(MapleDataTool.getInt(portal.getChildByPath("x")), MapleDataTool.getInt(portal.getChildByPath("y"))));
            String script = MapleDataTool.getString("script", portal, null);
            if (script != null && script.equals("")) {
                script = null;
            }
            myPortal.setScriptName(script);

            if (myPortal.getType() == MaplePortal.DOOR_PORTAL) {
                myPortal.setId(nextDoorPortal);
                nextDoorPortal++;
            } else {
                myPortal.setId(Integer.parseInt(portal.getName()));
            }
            map.addPortal(myPortal);
        }
    }

    private MapleNodes loadNodes(final int mapid, final MapleData mapData) {
        MapleNodes nodeInfo = new MapleNodes(mapid);
        if (mapData.getChildByPath("nodeInfo") != null) {
            for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(MapleDataTool.getInt(node, 0));
                        continue;
                    }
                    List<Integer> edges = new ArrayList<Integer>();
                    if (node.getChildByPath("edge") != null) {
                        for (MapleData edge : node.getChildByPath("edge")) {
                            edges.add(MapleDataTool.getInt(edge, -1));
                        }
                    }
                    final MapleNodeInfo mni = new MapleNodeInfo(
                            Integer.parseInt(node.getName()),
                            MapleDataTool.getIntConvert("key", node, 0),
                            MapleDataTool.getIntConvert("x", node, 0),
                            MapleDataTool.getIntConvert("y", node, 0),
                            MapleDataTool.getIntConvert("attr", node, 0), edges);
                    nodeInfo.addNode(mni);
                } catch (NumberFormatException e) {
                } //start, end, edgeInfo = we dont need it
            }
            nodeInfo.sortNodes();
        }
        for (int i = 1; i <= 7; i++) {
            if (mapData.getChildByPath(String.valueOf(i)) != null && mapData.getChildByPath(i + "/obj") != null) {
                for (MapleData node : mapData.getChildByPath(i + "/obj")) {
                    if (node.getChildByPath("SN_count") != null && node.getChildByPath("speed") != null) {
                        int sn_count = MapleDataTool.getIntConvert("SN_count", node, 0);
                        String name = MapleDataTool.getString("name", node, "");
                        int speed = MapleDataTool.getIntConvert("speed", node, 0);
                        if (sn_count <= 0 || speed <= 0 || name.equals("")) {
                            continue;
                        }
                        final List<Integer> SN = new ArrayList<Integer>();
                        for (int x = 0; x < sn_count; x++) {
                            SN.add(MapleDataTool.getIntConvert("SN" + x, node, 0));
                        }
                        final MaplePlatform mni = new MaplePlatform(
                                name, MapleDataTool.getIntConvert("start", node, 2), speed,
                                MapleDataTool.getIntConvert("x1", node, 0),
                                MapleDataTool.getIntConvert("y1", node, 0),
                                MapleDataTool.getIntConvert("x2", node, 0),
                                MapleDataTool.getIntConvert("y2", node, 0),
                                MapleDataTool.getIntConvert("r", node, 0), SN);
                        nodeInfo.addPlatform(mni);
                    } else if (node.getChildByPath("tags") != null) {
                        String name = MapleDataTool.getString("tags", node, "");
                        nodeInfo.addFlag(new Pair<String, Integer>(name, name.endsWith("3") ? 1 : 0)); //idk, no indication in wz
                    }
                }
            }
        }
        // load areas (EG PQ platforms)
        if (mapData.getChildByPath("area") != null) {
            int x1, y1, x2, y2;
            Rectangle mapArea;
            for (MapleData area : mapData.getChildByPath("area")) {
                x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
                y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
                x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
                y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
                mapArea = new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
                nodeInfo.addMapleArea(mapArea);
            }
        }
        if (mapData.getChildByPath("CaptureTheFlag") != null) {
            final MapleData mc = mapData.getChildByPath("CaptureTheFlag");
            for (MapleData area : mc) {
                nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("FlagPositionX")), MapleDataTool.getInt(area.getChildByPath("FlagPositionY"))), area.getName().startsWith("Red") ? 0 : 1);
            }
        }
        if (mapData.getChildByPath("directionInfo") != null) {
            final MapleData mc = mapData.getChildByPath("directionInfo");
            for (MapleData area : mc) {
                DirectionInfo di = new DirectionInfo(Integer.parseInt(area.getName()), MapleDataTool.getInt("x", area, 0), MapleDataTool.getInt("y", area, 0), MapleDataTool.getInt("forcedInput", area, 0) > 0);
                final MapleData mc2 = area.getChildByPath("EventQ"); // 아 대문자.. 시발;
                if (mc2 != null) {
                    for (MapleData event : mc2) {
                        di.eventQ.add(MapleDataTool.getString(event));
                    }
                }
                nodeInfo.addDirection(Integer.parseInt(area.getName()), di);
            }
        }
        if (mapData.getChildByPath("monsterCarnival") != null) {
            final MapleData mc = mapData.getChildByPath("monsterCarnival");
            if (mc.getChildByPath("mobGenPos") != null) {
                for (MapleData area : mc.getChildByPath("mobGenPos")) {
                    nodeInfo.addMonsterPoint(MapleDataTool.getInt(area.getChildByPath("x")),
                            MapleDataTool.getInt(area.getChildByPath("y")),
                            MapleDataTool.getInt(area.getChildByPath("fh")),
                            MapleDataTool.getInt(area.getChildByPath("cy")),
                            MapleDataTool.getInt("team", area, -1));
                }
            }
            if (mc.getChildByPath("mob") != null) {
                for (MapleData area : mc.getChildByPath("mob")) {
                    nodeInfo.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
                }
            }
            if (mc.getChildByPath("guardianGenPos") != null) {
                for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                    nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y"))), MapleDataTool.getInt("team", area, -1));
                }
            }
            if (mc.getChildByPath("skill") != null) {
                for (MapleData area : mc.getChildByPath("skill")) {
                    nodeInfo.addSkillId(MapleDataTool.getInt(area));
                }
            }
        }
        return nodeInfo;
    }
}
