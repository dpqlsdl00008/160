package server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.Calendar;

import client.inventory.Equip;
import client.inventory.Item;
import constants.GameConstants;
import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MonsterSkill;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.status.MonsterTemporaryStat;
import client.status.MonsterTemporaryStatEffect;
import constants.MapConstants;
import constants.ServerConstants;
import constants.drop.DropBoss;
import constants.drop.DropConsume;
import constants.drop.DropEquip;
import constants.drop.DropEtc;
import constants.shop.Shop;
import database.DatabaseConnection;
import handling.ChatType;
import handling.UIType;

import handling.channel.ChannelServer;
import handling.channel.handler.DueyHandler;
import handling.channel.handler.InventoryHandler;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStatEffect;
import server.Randomizer;
import server.MapleInventoryManipulator;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MapleLifeFactory;
import server.life.Spawns;
import server.life.SpawnPoint;
import server.life.SpawnPointAreaBoss;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.MapleMonsterInformationProvider;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CPet;
import tools.packet.CMobPool;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleCarnivalFactory;
import server.MapleCarnivalFactory.MCSkill;
import server.MapleShopItem;
import server.MapleSquad;
import server.MapleSquad.MapleSquadType;
import server.SpeedRunner;
import server.StructRewardItem;
import server.Timer;
import server.Timer.MapTimer;
import server.Timer.EtcTimer;
import server.events.MapleEvent;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleNodes.DirectionInfo;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;
import server.maps.MapleNodes.MonsterPoint;
import server.quest.MapleQuest;
import tools.Pair;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField;
import tools.packet.CField.SummonPacket;
import tools.packet.CMonsterCarnival;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuffPacket;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.PartyPacket;

public final class MapleMap {

    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final List<MapleCharacter> characters = new ArrayList<MapleCharacter>();
    private final ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();
    private int runningOid = 500000;
    private final Lock runningOidLock = new ReentrantLock();
    public final List<Spawns> monsterSpawn = new ArrayList<Spawns>();
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final Map<Integer, MaplePortal> portals = new HashMap<Integer, MaplePortal>();
    private MapleFootholdTree footholds = null;
    private float monsterRate, recoveryRate;
    private MapleMapEffect mapEffect;
    private WeakReference<MapleCharacter> changeMobOrigin = null;
    private byte channel;
    private short decHP = 0, createMobInterval = 12000, top = 0, bottom = 0, left = 0, right = 0;
    private int consumeItemCoolTime = 0, protectItem = 0, decHPInterval = 10000, mapid, returnMapId, timeLimit,
            fieldLimit, maxRegularSpawn = 0, fixedMob, forcedReturnMap = 999999999, instanceid = -1,
            lvForceMove = 0, lvLimit = 0, permanentWeather = 0, partyBonusRate = 0, barrierArc = 0, barrier = 0;
    private boolean town, clock, personalShop, everlast = false, dropsDisabled = false, gDropsDisabled = false,
            soaring = false, squadTimer = false, isSpawns = true, checkStates = true;
    private String mapName, streetName, onUserEnter, onFirstUserEnter, speedRunLeader = "";
    private List<Integer> dced = new ArrayList<Integer>();
    private ScheduledFuture<?> squadSchedule;
    private long speedRunStart = 0, lastSpawnTime = 0, lastHurtTime = 0;
    private MapleNodes nodes;
    private MapleSquadType squad;
    private Map<String, Integer> environment = new LinkedHashMap<String, Integer>();
    private String fieldType = "";

    public MapleMap(final int mapid, final int channel, final int returnMapId, final float monsterRate) {
        this.mapid = mapid;
        this.channel = (byte) channel;
        this.returnMapId = returnMapId;
        if (this.returnMapId == 999999999 && mapid != 180000001) {
            this.returnMapId = mapid;
        }
        if (GameConstants.getPartyPlay(mapid) > 0) {
            this.monsterRate = (monsterRate - 1.0f) * 2.5f + 1.0f;
        } else {
            this.monsterRate = monsterRate;
        }

        EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> objsMap = new EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>>(MapleMapObjectType.class);
        EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<MapleMapObjectType, ReentrantReadWriteLock>(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap<Integer, MapleMapObject>());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        mapobjects = Collections.unmodifiableMap(objsMap);
        mapobjectlocks = Collections.unmodifiableMap(objlockmap);
    }

    public final void setSpawns(final boolean fm) {
        this.isSpawns = fm;
    }

    public final boolean getSpawns() {
        return isSpawns;
    }

    public final void setFixedMob(int fm) {
        this.fixedMob = fm;
    }

    public final void setForceMove(int fm) {
        this.lvForceMove = fm;
    }

    public final int getForceMove() {
        return lvForceMove;
    }

    public final void setLevelLimit(int fm) {
        this.lvLimit = fm;
    }

    public final int getLevelLimit() {
        return lvLimit;
    }

    public final void setReturnMapId(int rmi) {
        this.returnMapId = rmi;
    }

    public final void setSoaring(boolean b) {
        this.soaring = b;
    }

    public final boolean canSoar() {
        return soaring;
    }

    public final void toggleDrops() {
        this.dropsDisabled = !dropsDisabled;
    }

    public final void setDrops(final boolean b) {
        this.dropsDisabled = b;
    }

    public final void toggleGDrops() {
        this.gDropsDisabled = !gDropsDisabled;
    }

    public final int getId() {
        return mapid;
    }

    public final MapleMap getReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(returnMapId);
    }

    public final int getReturnMapId() {
        return returnMapId;
    }

    public final int getForcedReturnId() {
        return forcedReturnMap;
    }

    public final MapleMap getForcedReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(forcedReturnMap);
    }

    public final void setForcedReturnMap(final int map) {
        this.forcedReturnMap = map;
    }

    public final int getBarrierArc() {
        return barrierArc;
    }

    public final void setBarrierArc(final int arc) {
        this.barrierArc = arc;
    }

    public final int getBarrier() {
        return barrier;
    }

    public final void setBarrier(final int arc) {
        this.barrier = arc;
    }

    public final float getRecoveryRate() {
        return recoveryRate;
    }

    public final void setRecoveryRate(final float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public final int getFieldLimit() {
        return fieldLimit;
    }

    public final void setFieldLimit(final int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public final void setCreateMobInterval(final short createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public final void setTimeLimit(final int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public final void setMapName(final String mapName) {
        this.mapName = mapName;
    }

    public final String getMapName() {
        return mapName;
    }

    public final String getStreetName() {
        return streetName;
    }

    public final void setFirstUserEnter(final String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public final void setUserEnter(final String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public final String getFirstUserEnter() {
        return onFirstUserEnter;
    }

    public final String getUserEnter() {
        return onUserEnter;
    }

    public final boolean hasClock() {
        return clock;
    }

    public final void setClock(final boolean hasClock) {
        this.clock = hasClock;
    }

    public final boolean isTown() {
        return town;
    }

    public final void setTown(final boolean town) {
        this.town = town;
    }

    public final boolean allowPersonalShop() {
        return personalShop;
    }

    public final void setPersonalShop(final boolean personalShop) {
        this.personalShop = personalShop;
    }

    public final void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    public final void setEverlast(final boolean everlast) {
        this.everlast = everlast;
    }

    public final boolean getEverlast() {
        return everlast;
    }

    public final int getHPDec() {
        return decHP;
    }

    public final void setHPDec(final int delta) {
        if (delta > 0 || mapid == 749040100) { //pmd
            lastHurtTime = System.currentTimeMillis(); //start it up
        }
        decHP = (short) delta;
    }

    public final int getHPDecInterval() {
        return decHPInterval;
    }

    public final void setHPDecInterval(final int delta) {
        decHPInterval = delta;
    }

    public final int getHPDecProtect() {
        return protectItem;
    }

    public final void setHPDecProtect(final int delta) {
        this.protectItem = delta;
    }

    public List<MapleMapObject> getCharactersAsMapObjects() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
    }

    public final int getCurrentPartyId() { // Exped
        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (chr.getParty() != null) {
                    return chr.getParty().getId();
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return -1;
    }

    public final void addMapObject(final MapleMapObject mapobject) {
        runningOidLock.lock();
        int newOid;
        try {
            newOid = ++runningOid;
        } finally {
            runningOidLock.unlock();
        }

        mapobject.setObjectId(newOid);

        mapobjectlocks.get(mapobject.getType()).writeLock().lock();
        try {
            if (mapobject.getType() == MapleMapObjectType.NPC) { //Custom object blocking.
                switch (((MapleNPC) mapobject).getId()) { //Mostly just seasonal NPCs.
                    case 2001007:
                    case 2041017:
                    case 9000017:
                    case 2001008:
                    case 9000154:
                    case 9000143://헤네시스에서 엔피시방해개쩜
                    case 9000145://헤네시스에서 엔피시방해개쩜
                    case 9001100:
                    case 9000021://Gaga
                    case 9000042://Gaga
                    case 9070008://Gaga
                    case 9201152:
                    case 9300010:
                    case 9270075:
                    case 9270072:
                    case 9201187:
                    case 9330165:
                    case 9330185:
                    case 9330186:
                    case 9330189:
                    case 9330192:
                    case 9201030:
                    case 9201151:
                    case 9201182:
                    case 9010040:
                    case 9010041:
                    case 9201232: {
                        return;
                    }
                }
            }
            mapobjects.get(mapobject.getType()).put(newOid, mapobject);
        } finally {
            mapobjectlocks.get(mapobject.getType()).writeLock().unlock();
        }
    }

    private void spawnAndAddRangedMapObject(final MapleMapObject mapobject, final DelayedPacketCreation packetbakery) {
        addMapObject(mapobject);
        for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.PLAYER).values()) {
            MapleCharacter chr = (MapleCharacter) obj;
            if ((mapobject.getType() == MapleMapObjectType.MIST || chr.getTruePosition().distanceSq(mapobject.getTruePosition()) <= GameConstants.maxViewRangeSq())) {
                packetbakery.sendPackets(chr.getClient());
                chr.addVisibleMapObject(mapobject);
            }
        }
    }

    /*
    private void spawnAndAddRangedMapObject(final MapleMapObject mapobject, final DelayedPacketCreation packetbakery) {
        addMapObject(mapobject);
        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> itr = characters.iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if ((mapobject.getType() == MapleMapObjectType.MIST || chr.getTruePosition().distanceSq(mapobject.getTruePosition()) <= GameConstants.maxViewRangeSq())) {
                    packetbakery.sendPackets(chr.getClient());
                    chr.addVisibleMapObject(mapobject);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }
     */
    public final String getFieldType() {
        return fieldType;
    }

    public final void setFieldType(final String fieldType) {
        this.fieldType = fieldType;
    }

    public final void removeMapObject(final MapleMapObject obj) {
        mapobjectlocks.get(obj.getType()).writeLock().lock();
        try {
            mapobjects.get(obj.getType()).remove(Integer.valueOf(obj.getObjectId()));
        } finally {
            mapobjectlocks.get(obj.getType()).writeLock().unlock();
        }
    }

    public final Point calcPointBelow(final Point initial) {
        final MapleFoothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            final double s1 = Math.abs(fh.getY2() - fh.getY1());
            final double s2 = Math.abs(fh.getX2() - fh.getX1());
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            } else {
                dropY = fh.getY1() + (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
        }
        return new Point(initial.x, dropY);
    }

    public final Point calcDropPos(final Point initial, final Point fallback) {
        final Point ret = calcPointBelow(new Point(initial.x, initial.y - 99));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    private void dropFromMonster(final MapleCharacter user, final MapleMonster mob) {
        if (dropsDisabled) {
            return;
        }
        if (mob.dropsDisabled()) {
            return;
        }
        if (mob.getStats().getSelfD() != -1) {
            return;
        }
        if (mob.getStats().getHideName() == 1) {
            //return;
        }
        if (user.getPyramidSubway() != null) {
            return;
        }
        final int mesoRate = ChannelServer.getInstance(channel).getMesoRate();
        final int dropRate = ChannelServer.getInstance(channel).getDropRate();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFfaLoot() ? 2 : user.getParty() != null ? 1 : 0);
        final int mobPos = mob.getTruePosition().x;
        final Point dropPos = new Point(0, mob.getTruePosition().y);
        byte dropArray = 1;

        Item idrop;
        final List<MonsterDropEntry> dropEntry = new ArrayList<>();
        int distanceLv = Math.abs(user.getLevel() - mob.getStats().getLevel());
        if (droptype == 3) {
            dropPos.x = (mobPos + (dropArray % 2 == 0 ? (40 * (dropArray + 1) / 2) : -(40 * (dropArray / 2))));
        } else {
            dropPos.x = (mobPos + ((dropArray % 2 == 0) ? (25 * (dropArray + 1) / 2) : -(25 * (dropArray / 2))));
        }
        int cRand = Randomizer.rand(0, 9999);
        int mCount = this.getAllMonstersThreadsafe().size();
        if (mCount > 39) {
            cRand *= 1.5;
        }
        if (mCount > 79) {
            cRand *= 2.0;
        }
        if (mob.getStats().isBoss()) {
            cRand /= 2;
        }
        if (DropEtc.isMustDropMob(mob.getId())) {
            for (int a1 = 0; a1 < DropEtc.drop_must.length; a1++) {
                if (mob.getId() == DropEtc.drop_must[a1][1]) {
                    boolean cDrop = false;
                    if (DropEtc.drop_must[a1][2] == 0) {
                        cDrop = true;
                    }
                    if (DropEtc.drop_must[a1][2] != 0 && user.getQuestStatus(DropEtc.drop_must[a1][2]) == 1) {
                        cDrop = true;
                    }
                    if (cDrop) {
                        int itemID = DropEtc.drop_must[a1][0];
                        dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                    }
                }
            }
        }
        if (cRand < DropEtc.booty_rate) {
            List<Integer> sBooty = new ArrayList<>();
            int itemID = 0;
            for (int a1 = 0; a1 < DropEtc.drop_booty.length; a1++) {
                if (mob.getId() == DropEtc.drop_booty[a1][1]) {
                    itemID = DropEtc.drop_booty[a1][0];
                    if (cRand < 1800) {
                        itemID = DropEtc.drop_specialBooty[a1][0];
                    }
                    sBooty.add(itemID);
                }
            }
            if (!sBooty.isEmpty()) {
                itemID = sBooty.get((int) Math.floor(Math.random() * sBooty.size()));
                if (itemID > 0) {
                    dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                }
            }
        }
        /*
        if (cRand < DropEtc.stuff_rate) {
            int itemID = DropEtc.drop_stuff[(int) Math.floor(Math.random() * DropEtc.drop_stuff.length)];
            dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
        }
         */
        
        /* 아케인 심볼 */
        if (user.getMapId() > 450000000 && user.getMapId() < 460000000) {
            int nSum = Randomizer.rand(2050010, 2050016);
            int pSum = Randomizer.rand(0, 777);
            if (pSum < 10) {
                dropEntry.add(new MonsterDropEntry(nSum, 999999, 1, 1, (short) 0));
            }
        }
        
        if (!mob.getStats().isBoss()) {
            int itemID = 0;
            List<Integer> sQuest = new ArrayList<>();
            if (cRand < DropEtc.quest_rate || mob.getStats().getLevel() < 10) {
                for (int a1 = 0; a1 < DropEtc.drop_quest.length; a1++) {
                    if (mob.getId() != DropEtc.drop_quest[a1][1]) {
                        continue;
                    }
                    if (user.getQuestStatus(DropEtc.drop_quest[a1][2]) != 1) {
                        continue;
                    }
                    itemID = DropEtc.drop_quest[a1][0];
                    sQuest.add(itemID);
                }
            }
            if (!sQuest.isEmpty()) {
                itemID = sQuest.get((int) Math.floor(Math.random() * sQuest.size()));
                if (itemID > 0) {
                    dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                }
            }
        } else {
            for (int a1 = 0; a1 < DropEtc.drop_quest.length; a1++) {
                if (mob.getId() != DropEtc.drop_quest[a1][1]) {
                    continue;
                }
                if (user.getQuestStatus(DropEtc.drop_quest[a1][2]) != 1) {
                    continue;
                }
                dropEntry.add(new MonsterDropEntry(DropEtc.drop_quest[a1][0], 999999, 1, 1, (short) 0));
            }
            List<Integer> sBoss = new ArrayList<>();
            int itemID = 0;
            boolean expedition = true;
            int bootyCount = 0;
            int cubeType = 0;
            switch (mob.getId()) {
                case 8300007: {
                    expedition = false;
                    bootyCount = DropBoss.dragonRider;
                    for (int a2 = 0; a2 < 5; a2++) {
                        /* 드래곤 라이더의 상자 */
                        dropEntry.add(new MonsterDropEntry(2430376 + a2, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8800002: {
                    bootyCount = DropBoss.normalZakum;
                    cubeType = 5062000;
                    for (int a2 = 0; a2 < 1; a2++) {
                        /* 자쿰의 투구 */
                        dropEntry.add(new MonsterDropEntry(1002357, 999999, 1, 1, (short) 0));
                    }
                    /* 자쿰의 나뭇가지 */
                    dropEntry.add(new MonsterDropEntry(1372049, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 1) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8810018: {
                    bootyCount = DropBoss.normalHorntail;
                    cubeType = 5062000;
                    for (int a2 = 0; a2 < 1; a2++) {
                        /* 혼테일의 목걸이 */
                        dropEntry.add(new MonsterDropEntry(1122000, 999999, 1, 1, (short) 0));
                    }
                    /* 드래곤의 돌 */
                    dropEntry.add(new MonsterDropEntry(2041200, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 1) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8820001: {
                    bootyCount = DropBoss.normalPinkbean;
                    cubeType = 5062000;
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 2) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8800102: {
                    bootyCount = DropBoss.chaosZakum;
                    cubeType = 5062002;
                    for (int a2 = 0; a2 < 1; a2++) {
                        /* 카오스 자쿰의 투구 */
                        dropEntry.add(new MonsterDropEntry(1003112, 999999, 1, 1, (short) 0));
                    }
                    /* 카오스 자쿰의 나뭇가지 */
                    dropEntry.add(new MonsterDropEntry(1372073, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 2) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8810122: {
                    bootyCount = DropBoss.chaosHorntail;
                    cubeType = 5062002;
                    for (int a2 = 0; a2 < 1; a2++) {
                        /* 카오스 혼테일의 목걸이 */
                        dropEntry.add(new MonsterDropEntry(1122076, 999999, 1, 1, (short) 0));
                    }
                    /* 드래곤의 돌 */
                    dropEntry.add(new MonsterDropEntry(2041200, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 2) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8820101: {
                    bootyCount = DropBoss.chaosPinkbean;
                    cubeType = 5062002;
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 3) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8840000: {
                    bootyCount = DropBoss.vonleon;
                    cubeType = 5062002;
                    for (int a2 = 0; a2 < 3; a2++) {
                        /* 사자왕의 메달 */
                        dropEntry.add(new MonsterDropEntry(2430158, 999999, 1, 1, (short) 0));
                    }
                    for (int a2 = 0; a2 < 2; a2++) {
                        /* 사자왕의 노블, 로얄 메달 */
                        int mRand = Randomizer.rand(4310009, 4310010);
                        dropEntry.add(new MonsterDropEntry(mRand, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 2) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8870000: {
                    bootyCount = DropBoss.hilla;
                    for (int a2 = 0; a2 < 3; a2++) {
                        /* 컨쿼러스 코인 */
                        dropEntry.add(new MonsterDropEntry(4310036, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 1) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    cubeType = 5062002;
                    break;
                }
                case 8860000: {
                    bootyCount = DropBoss.akayrum;
                    cubeType = 5062002;
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 2) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8850011: {
                    bootyCount = DropBoss.cygnus;
                    cubeType = 5062002;
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 3) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    for (int a2 = 0; a2 < 1; a2++) {
                        /* 검은 파괴의 조각 */
                        dropEntry.add(new MonsterDropEntry(2434588, 999999, 1, 1, (short) 0));
                    }
                    for (int a2 = 0; a2 < 2; a2++) {
                        /* 검은 수호의 조각 */
                        dropEntry.add(new MonsterDropEntry(2434589, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880000: {
                    bootyCount = DropBoss.magnus;
                    cubeType = 5062002;
                    for (int a2 = 0; a2 < 2; a2++) {
                        /* 매그너스 코인 */
                        dropEntry.add(new MonsterDropEntry(4310058, 999999, 1, 1, (short) 0));
                    }
                    for (int a3 = 0; a3 < 3; a3++) {
                        /* 그림자 상인단 코인*/
                        dropEntry.add(new MonsterDropEntry(4310059, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 3) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8920000:
                case 8920001:
                case 8920002:
                case 8920003: {
                    bootyCount = DropBoss.blodyQueen;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(3, 5);
                    for (int a2 = 0; a2 < bRand; a2++) {
                        /* 절규의 조각 */
                        dropEntry.add(new MonsterDropEntry(2434586, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 4) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8900000:
                case 8900001:
                case 8900002: {
                    bootyCount = DropBoss.pierre;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(3, 5);
                    for (int a2 = 0; a2 < bRand; a2++) {
                        /* 조롱의 조각 */
                        dropEntry.add(new MonsterDropEntry(2434585, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 4) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8910000: {
                    bootyCount = DropBoss.banban;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(5, 10);
                    for (int a2 = 0; a2 < bRand; a2++) {
                        /* 시간의 조각 */
                        dropEntry.add(new MonsterDropEntry(2434584, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 4) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8930000: {
                    bootyCount = DropBoss.bellum;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(3, 5);
                    for (int a2 = 0; a2 < bRand; a2++) {
                        /* 파멸의 조각 */
                        dropEntry.add(new MonsterDropEntry(2434587, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 4) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8240099: {
                    bootyCount = DropBoss.lotus;
                    cubeType = 5062002;
                    /* 루즈 컨트롤 머신 마크 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        dropEntry.add(new MonsterDropEntry(1012632, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880101: {
                    bootyCount = DropBoss.demian;
                    cubeType = 5062002;
                    /* 마력이 깃든 안대 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        dropEntry.add(new MonsterDropEntry(1022278, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880156: {
                    bootyCount = DropBoss.lucid;
                    cubeType = 5062002;
                    /* 몽환의 벨트 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        dropEntry.add(new MonsterDropEntry(1132308, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880302: {
                    bootyCount = DropBoss.will;
                    cubeType = 5062002;
                    /* 저주받은 마도서 선택 상자 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        dropEntry.add(new MonsterDropEntry(2437770, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880405: {
                    bootyCount = DropBoss.jinhillah;
                    cubeType = 5062002;
                    /* 고통의 근원 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        dropEntry.add(new MonsterDropEntry(1122430, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8645009: {
                    bootyCount = DropBoss.dunkel;
                    cubeType = 5062002;
                    /* 커맨더 포스 이어링 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        dropEntry.add(new MonsterDropEntry(1032316, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880518: {
                    bootyCount = DropBoss.blackmage;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        /* 창세의 뱃지 */
                        dropEntry.add(new MonsterDropEntry(1182285, 999999, 1, 1, (short) 0));
                    }
                    if (bRand < 1) {
                        /* 악몽의 조각 */
                        //dropEntry.add(new MonsterDropEntry(2049180, 999999, 1, 1, (short) 0));
                    }
                    /* 봉인 된 제네시스 무기 상자 */
                    dropEntry.add(new MonsterDropEntry(2439614, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 10) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 9421586: {
                    bootyCount = DropBoss.ranmaru;
                    cubeType = 5062002;
                    dropEntry.add(new MonsterDropEntry(4310353, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 3) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 9450066: {
                    bootyCount = DropBoss.nohime;
                    cubeType = 5062002;
                    dropEntry.add(new MonsterDropEntry(4310353, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 3) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 9601511: {
                    bootyCount = DropBoss.mitsuhide;
                    cubeType = 5062002;
                    dropEntry.add(new MonsterDropEntry(4310352, 999999, 1, 1, (short) 0));
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 5) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880614: {
                    bootyCount = DropBoss.seren;
                    cubeType = 5062002;
                    /* 그라비티 모듈 */
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        //dropEntry.add(new MonsterDropEntry(2049181, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 10) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880808: {
                    bootyCount = DropBoss.kalos;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        /* 의지의 에테르넬 방어구 상자 */
                        dropEntry.add(new MonsterDropEntry(2430578, 999999, 1, 1, (short) 0));
                    }
                    if (bRand < 1) {
                        /* 파멸의 징표 */
                        //dropEntry.add(new MonsterDropEntry(2049182, 999999, 1, 1, (short) 0));
                    }
                    for (int i = 0; i < 3; i++) {
                        /* 남겨진 칼로스의 의지 */
                        dropEntry.add(new MonsterDropEntry(2436231, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 10) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
                case 8880845: {
                    bootyCount = DropBoss.karing;
                    cubeType = 5062002;
                    int bRand = Randomizer.rand(0, 100);
                    if (bRand < 1) {
                        /* 흉수의 에테르넬 방어구 상자 */
                        dropEntry.add(new MonsterDropEntry(2430579, 999999, 1, 1, (short) 0));
                    }
                    if (bRand < 1) {
                        /* 충정의 투구 */
                        //dropEntry.add(new MonsterDropEntry(2049183, 999999, 1, 1, (short) 0));
                    }
                    for (int i = 0; i < 3; i++) {
                        /* 뒤엉킨 흉수의 고리 */
                        dropEntry.add(new MonsterDropEntry(2436232, 999999, 1, 1, (short) 0));
                    }
                    /* 럭키 데이 주문서 */
                    int luckDayScroll = Randomizer.rand(0, 100);
                    if (luckDayScroll < 10) {
                        dropEntry.add(new MonsterDropEntry(2530000, 999999, 1, 1, (short) 0));
                    }
                    break;
                }
            }
            if (bootyCount > 0) {
                for (int a1 = 0; a1 < DropBoss.drop_boss.length; a1++) {
                    if (mob.getId() != DropBoss.drop_boss[a1][1]) {
                        continue;
                    }
                    itemID = DropBoss.drop_boss[a1][0];
                    sBoss.add(itemID);
                }
                if (!sBoss.isEmpty()) {
                    for (int a2 = 0; a2 < bootyCount; a2++) {
                        itemID = sBoss.get((int) Math.floor(Math.random() * sBoss.size()));
                        if (itemID > 0) {
                            dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                            if (expedition) {
                                /* 수상한 큐브 */
                                dropEntry.add(new MonsterDropEntry(5062005, 999999, 1, 1, (short) 0));
                                /* 미라클 큐브 */
                                if (cubeType > 0) {
                                    dropEntry.add(new MonsterDropEntry(cubeType, 999999, 1, 1, (short) 0));
                                }
                            }
                        }
                    }
                    if (expedition) {
                        int nQr = (cubeType == 5062000 ? 3 : 5);
                        for (int i = 0; i < nQr; i++) {
                            dropEntry.add(new MonsterDropEntry(2432393, 999999, 1, 1, (short) 0));
                        }
                    }
                }
            }
        }
        int bRand = Randomizer.rand(0, 9999);
        if (cRand < DropConsume.potion_rate) {
            List<Integer> sPotion = new ArrayList<>();
            int itemID = 0;
            final int[][] consumeType = (cRand < DropConsume.throw_rate ? DropConsume.drop_consume_throw : DropConsume.drop_consume_potion);
            for (int a1 = 0; a1 < consumeType.length; a1++) {
                int mobLv = mob.getStats().getLevel();
                int conLv = consumeType[a1][1];
                int absLv = (mobLv - conLv);
                if (mobLv > conLv) {
                    itemID = consumeType[a1][0];
                    if (absLv > 20) {
                        continue;
                    }
                    sPotion.add(itemID);
                }
            }
            if (!sPotion.isEmpty()) {
                itemID = sPotion.get((int) Math.floor(Math.random() * sPotion.size()));
                if (itemID > 0) {
                    dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                }
            }
        }
        if (distanceLv < 21 || mob.getStats().getLevel() > 139) {
            if (bRand < DropConsume.scroll_rate) {
                //int itemID = DropConsume.drop_consume_scroll[(int) Math.floor(Math.random() * DropConsume.drop_consume_scroll.length)];
                /*
                if (mob.getStats().getLevel() > 100) {
                    if (bRand < DropConsume.recipe_rate) {
                        itemID = DropConsume.drop_consume_recipe[(int) Math.floor(Math.random() * DropConsume.drop_consume_recipe.length)];
                    }
                }
                 */
                //dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
            }
            int aRand = Randomizer.nextInt(9999);
            /* 아이스 박스 */
            //if (aRand < 299) {
            //dropEntry.add(new MonsterDropEntry(2430016, 999999, 1, 1, (short) 0));
            //}
            if (aRand < 199) {
                if (aRand < 10) {
                    /* 미라클 큐브 */
                    dropEntry.add(new MonsterDropEntry(5062000, 999999, 1, 1, (short) 0));
                } else {
                    /* 수상한 큐브 */
                    dropEntry.add(new MonsterDropEntry(5062005, 999999, 1, 1, (short) 0));
                }
            }
            /* 골드 리프 */
            if (aRand < 9) {
                dropEntry.add(new MonsterDropEntry(4310334, 999999, 1, 1, (short) 0));
            }
        }
        /* 경험치 획득 */
        if (user.getLevel() < 200) {
            int eItem = 2432392;
            int expUPRand = Randomizer.nextInt(9999);
            if (user.getLevel() < 100) {
                eItem = 2432391;
                expUPRand = Randomizer.nextInt(4999);
            }
            if (expUPRand < 9) {
                dropEntry.add(new MonsterDropEntry(eItem, 999999, 1, 1, (short) 0));
            }
        }
        /*
        if (mob.getStats().getLevel() > 99) {
            List<Integer> mBook = new ArrayList<>();
            if (bRand < DropConsume.masteryBook_rate) {
                final Pair<Integer, List<StructRewardItem>> v1 = ii.getRewardItem(DropConsume.drop_consume_masteryBook);
                for (StructRewardItem v2 : v1.getRight()) {
                    if (v2.prob == 0) {
                        continue;
                    }
                    mBook.add(v2.itemid);
                }
                int itemID = mBook.get((int) Math.floor(Math.random() * mBook.size()));
                dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
            }
        }
         */
        if (user.getEventInstance() == null) {
            if (cRand < 4999) {
                int rMeso = Randomizer.rand(mob.getStats().getLevel() * 10, mob.getStats().getLevel() * 15);
                if (rMeso > 0) {
                    double sMeso = 0;
                    /* 이노시스 시즌 패스 티켓 */
                    if (user.haveItem(2430117)) {
                        sMeso += 1.05;
                    }
                    /* 후원 포인트 심볼 */
                    if (user.haveItem(3980004)) {
                        sMeso += 1.20;
                    }
                    /* 홍보 포인트 심볼 */
                    if (user.haveItem(3980010)) {
                        sMeso += 1.20;
                    }
                    /* 보스 원정대 심볼 */
                    if (user.haveItem(3980016)) {
                        sMeso += 1.20;
                    }
                    if (user.getClient().getChannelServer().getAramiaClock() > System.currentTimeMillis()) {
                        sMeso += 1.50;
                    }
                    /* MVP의 물약 */
                    if (!user.getOneInfoQuest(20250227, "macro_potion").equals("")) {
                        if (Long.parseLong(user.getOneInfoQuest(20250227, "macro_potion")) > System.currentTimeMillis()) {
                            sMeso += 1.20;
                        }
                    }
                    rMeso *= (mesoRate + user.getDropMod() + (user.getStat().mesoBuff / 100.0) + sMeso);

                    Date nDate = new Date();
                    if (nDate.getDay() == 0 || nDate.getDay() == 6) {
                        rMeso *= 1.3;
                    } else {
                        if (nDate.getHours() > 19 && nDate.getHours() < 24) {
                            rMeso *= 1.3;
                        }
                    }
                    spawnMobMesoDrop((int) rMeso, calcDropPos(dropPos, mob.getTruePosition()), mob, user, false, droptype);
                    dropArray++;
                }
            }
        }
        int eRand = Randomizer.rand(0, 9999);
        if (eRand < DropEquip.equip_rate) {
            List<Integer> sEquip = new ArrayList<>();
            int itemID = 0;
            for (int a1 = 0; a1 < DropEquip.drop_equip.length; a1++) {
                int mobLv = mob.getStats().getLevel();
                int conLv = DropEquip.drop_equip[a1][1];
                int absLv = (mobLv - conLv);
                if (mobLv > conLv) {
                    itemID = DropEquip.drop_equip[a1][0];
                    if (absLv > 30/* && (mob.getStats().getLevel() > 105 && conLv != 95)*/) {
                        continue;
                    }
                    if (eRand < 9) {
                        itemID = DropEquip.drop_specialEquip[a1][0];
                    }
                    sEquip.add(itemID);
                }
            }
            if (!sEquip.isEmpty()) {
                itemID = sEquip.get((int) Math.floor(Math.random() * sEquip.size()));
                if (itemID > 0) {
                    dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                }
            }
        }
        /* 필드 보스 */
        switch (mob.getId()) {
            case 2220000: // 마노
            case 3220000: // 스텀피
            case 6130101: // 머쉬맘
            case 8220007: // 블루 머쉬맘
            case 6300005: // 좀비 머쉬맘
            case 6220000: // 다일
            case 5220001: // 킹크랑
            case 8220000: // 엘리쟈
            case 5220003: // 타이머
            case 3220001: // 데우
            case 9300481: // 마스터 버크
            case 8220003: // 레비아탄
            case 6090001: // 설산의 마녀
            case 4220000: // 세르프
            case 5220004: // 대왕 지네
            case 6090002: // 대나무 무사
            case 7220000: // 태륜
            case 7220002: // 요괴 선사
            case 7220001: // 구미호
            case 5220002: // 파우스트
            case 6220001: // 제노
            case 8220002: // 키메라
            case 8220008: // 포장 마차
            case 6090003: // 선비 귀신
            case 6090000: // 리치
            case 8130100: // 주니어 발록
            case 8220001: // 스노우 맨
            case 9304005: // 제이라
            case 8220004: // 도도
            case 8220005: // 릴리노흐
            case 8220006: // 라이카
            case -1: {
                int fRand = Randomizer.rand(0, 100);
                if (fRand < 50) {
                    dropEntry.add(new MonsterDropEntry(5060004, 999999, 1, 1, (short) 0));
                }
                dropEntry.add(new MonsterDropEntry(2023130, 999999, 1, 1, (short) 0));
                break;
            }
        }
        /* 테마 던전 */
        switch (mob.getId()) {
            case 6160003: { // 크세르크세스
                for (int itemID = 2028033; itemID < 2028038; itemID++) {
                    dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                }
                break;
            }
            case 3300005:
            case 3300006:
            case 3300007: { // 예티와 페페킹
                for (int itemID = 2022580; itemID < 2022585; itemID++) {
                    dropEntry.add(new MonsterDropEntry(itemID, 999999, 1, 1, (short) 0));
                }
                break;
            }
            case 3401011: { // 캡틴 블랙 슬라임
                dropEntry.add(new MonsterDropEntry(1012346, 999999, 1, 1, (short) 0));
                break;
            }
            case 5250007: { // 에피네아
                dropEntry.add(new MonsterDropEntry(1112683, 999999, 1, 1, (short) 0));
                break;
            }
            case 8800400: { // 타란튤로스
                dropEntry.add(new MonsterDropEntry(2550014, 999999, 1, 1, (short) 0));
                break;
            }
            case 8800200: { // 라바나
                dropEntry.add(new MonsterDropEntry(1003068, 999999, 1, 1, (short) 0));
                break;
            }
        }
        if (user.getEventInstance() != null) {
            for (int a1 = 0; a1 < DropEtc.drop_partyQuest.length; a1++) {
                if (mob.getId() == DropEtc.drop_partyQuest[a1][0]) {
                    dropEntry.add(new MonsterDropEntry(DropEtc.drop_partyQuest[a1][1], 999999, 1, 1, (short) 0));
                }
            }
        }

        /* 루디브리엄 메이즈 */
        if (mob.getId() > 9400208 && mob.getId() < 9400219) {
            dropEntry.add(new MonsterDropEntry(4001106, 999999, 1, 1, (short) 0));
        }

        /* 무릉 도장 */
        int mRand = Randomizer.rand(0, 100);
        if (mob.getId() > 9305299 && mob.getId() < 9305340) {
            dropEntry.clear();
            dropEntry.add(new MonsterDropEntry(2022430, 999999, 1, 1, (short) 0)); // 무릉 도장 마나 엘릭서
            dropEntry.add(new MonsterDropEntry(2022431, 999999, 1, 1, (short) 0)); // 무릉 도장 엘릭서
            dropEntry.add(new MonsterDropEntry(2022432, 999999, 1, 1, (short) 0)); // 무릉 도장 파워 엘릭서
            dropEntry.add(new MonsterDropEntry(2022433, 999999, 1, 1, (short) 0)); // 무릉 도장 만병 통치약
            if (mRand < 30) {
                dropEntry.add(new MonsterDropEntry(4310117, 999999, 1, 1, (short) 0));
            }
        }

        /* 황금 사원 */
        int gRand = Randomizer.rand(0, 100);
        if (this.getId() > 252000000 && this.getId() < 253000000) {
            if (gRand < 1) {
                dropEntry.add(new MonsterDropEntry(4001684, 999999, 1, 1, (short) 0));
            } else {
                if (user.getQuestStatus(3862) == 1) {
                    dropEntry.add(new MonsterDropEntry(4033176, 999999, 1, 1, (short) 0));
                }
            }
        }

        /* 몬스터 카드 */
        int pRand = Randomizer.rand(0, 9999);
        int pSource = Randomizer.rand(2380000, 2389999);
        if (pRand < 99) {
            if (ii.getItemInformation(pSource) != null) {
                int tSource = pSource;
                int pMob = ii.getCardMobId(tSource);
                if (pMob > 0) {
                    MapleMonster tMob = MapleLifeFactory.getMonster(pMob);
                    if (tMob != null) {
                        user.getMonsterBook().monsterCaught(user.getClient(), tMob.getId(), tSource, tMob.getName());
                    }
                }
            }
            for (int i = 2380000; i < 2390000; i++) {
                if (mob.getId() == ii.getCardMobId(i)) {
                    dropEntry.add(new MonsterDropEntry(i, 999999, 1, 1, (short) 0));
                }
            }
        }

        if (!dropEntry.isEmpty()) {
            for (final MonsterDropEntry de : dropEntry) {
                if (!user.getClient().getChannelServer().getPotential()) {
                    if (GameConstants.isPotential(de.itemId)) {
                        continue;
                    }
                }
                if (de.itemId == mob.getStolen()) {
                    continue;
                }
                if (droptype == 3) {
                    dropPos.x = (mobPos + (dropArray % 2 == 0 ? (40 * (dropArray + 1) / 2) : -(40 * (dropArray / 2))));
                } else {
                    dropPos.x = (mobPos + ((dropArray % 2 == 0) ? (25 * (dropArray + 1) / 2) : -(25 * (dropArray / 2))));
                }
                if (de.questid > 0 && user.getQuestStatus(de.questid) != 1) {
                    continue;
                }
                if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                    idrop = ii.getEquipById(de.itemId);
                    //idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    if (user.getClient().getChannelServer().getPotential()) {
                        if (!GameConstants.isMechanicItem(de.itemId) && !GameConstants.isEvanDragonItem(de.itemId)) {
                            if (Randomizer.nextInt(100) > 50) {
                                ((Equip) idrop).setPotentialOpen(((Equip) idrop), 17);
                            }
                        }
                    }
                } else {
                    idrop = new Item(de.itemId, (byte) 0, (short) 1, (byte) 0);
                }
                spawnMobDrop(idrop, calcDropPos(dropPos, mob.getTruePosition()), mob, user, droptype, de.questid);
                dropArray++;
            }
        }
    }

    public void removeMonster(final MapleMonster monster, int animation) {
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();
        broadcastMessage(CMobPool.killMonster(monster.getObjectId(), animation));
        removeMapObject(monster);
        monster.killed();
    }

    public void killMonster(final MapleMonster monster) {
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        if (monster.getLinkCID() <= 0) {
            monster.spawnRevives(this);
        }
        int animation = monster.getStats().getSelfD() < 0 ? 1 : 2/*monster.getStats().getSelfD()*/;
        broadcastMessage(CMobPool.killMonster(monster.getObjectId(), animation));
        removeMapObject(monster);
        monster.killed();
    }

    public void killMonster(final MapleMonster monster, int animation) {
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        if (monster.getLinkCID() <= 0) {
            monster.spawnRevives(this);
        }
        broadcastMessage(CMobPool.killMonster(monster.getObjectId(), animation));
        removeMapObject(monster);
        monster.killed();
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation) {
        killMonster(monster, chr, withDrops, second, animation, 0);
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation, final int lastSkill) {
        if ((monster.getId() == 8810018 || monster.getId() == 8810122) && !second) {
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    killMonster(monster, chr, true, true, (byte) 1);
                    killAllMonsters(true);
                }
            }, 3000);
            return;
        }
        if (monster.getId() == 8820014) {
            killMonster(8820000);
        } else if (monster.getId() == 9300166) {
            animation = 4;
        }
        if (monster.getId() == 9300635) {
            NPCScriptManager.getInstance().start(chr.getClient(), 2159377);
        }
        spawnedMonstersOnMap.decrementAndGet();
        removeMapObject(monster);
        monster.killed();
        int dropOwner = monster.killBy(chr, lastSkill);
        if (animation >= 0) {
            broadcastMessage(CMobPool.killMonster(monster.getObjectId(), animation));
        }
        if (monster.getBuffToGive() > -1) {
            final int buffid = monster.getBuffToGive();
            final MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);
            charactersLock.readLock().lock();
            try {
                for (final MapleCharacter mc : characters) {
                    if (mc.isAlive()) {
                        buff.applyTo(mc);
                        switch (monster.getId()) {
                            case 8810018:
                            case 8810122:
                            case 8820001:
                                mc.getClient().getSession().write(EffectPacket.showOwnBuffEffect(buffid, 14, mc.getLevel(), 1));
                                broadcastMessage(mc, EffectPacket.showBuffeffect(mc.getId(), buffid, 14, mc.getLevel(), 1), false);
                                break;
                        }
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Reaper) != null) {
            final MapleStatEffect eff = chr.getStatForBuff(CharacterTemporaryStat.Reaper);
            if (eff.makeChanceResult()) {
                final MapleSummon summon = new MapleSummon(chr, 32111006, eff.getLevel(), monster.getTruePosition(), SummonMovementType.WALK_STATIONARY);
                this.spawnSummon(summon, true);
                chr.addSummon(summon);
            }
        }
        if (chr.getJob() == 422) {
            //chr.setBattleshipHP(Math.min(5, chr.currentBattleshipHP() + 1)); //max 5
            //chr.refreshBattleshipHP();
        }
        final int mobid = monster.getId();
        if (mobid == 6160003) {
            chr.updateQuest(31018, 1, "1");
        } else if (mobid == 9500517) {
            broadcastMessage(CField.floatNotice("박 터뜨리기 게임에 승리하였습니다! 단상에 서 있는 교장 페르디에게 말을 걸어 남은 시간 내에 퇴장합시다!", 5120006, true));
        } else if (mobid == 6500012) {
            chr.getMap().setReactorState();
            chr.getClient().getSession().write(CField.floatNotice("두꺼비 요괴가 사라졌다. 궤짝을 열어서 영주를 구하자!", 5121022, true));
            if (chr.getQuestStatus(4325) == 1) {
                chr.getQuestNAdd(MapleQuest.getInstance(4325)).setCustomData(String.valueOf("확인"));
            }
        } else if (mapid == 223030210 && !containsNPC(2192000)) {
            spawnNpc(2192000, new Point(-273, 4));
        } else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonstersThreadsafe();
            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMapObject object : monsters) {
                    final MapleMonster mons = ((MapleMonster) object);
                    if (mons.getId() == 8800000) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        final MapleMonster realZakum = MapleLifeFactory.getMonster(8800000);
                        realZakum.setFh(0x46);
                        spawnMonsterOnGroundBelow(realZakum, pos);
                        break;
                    }
                }
            }
        } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonstersThreadsafe();
            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800103 && mons.getId() <= 8800110) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMonster mons : monsters) {
                    if (mons.getId() == 8800100) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                        break;
                    }
                }
            }
        } else if (mobid == 8820008) { //wipe out statues and respawn
            for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getLinkOid() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getId() != 8820000 && mons.getId() != 8820001 && mons.getObjectId() != monster.getObjectId() && mons.isAlive() && mons.getLinkOid() == monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        }
        /* 라바나 */
        if (chr.getQuestStatus(3863) == 1) {
            if (mobid == 8800200) {
                MapleQuestStatus mqs = chr.getQuestNoAdd(MapleQuest.getInstance(3863));
                mqs.setMobKills(9100025, 1);
                chr.getClient().sendPacket(InfoPacket.updateQuestMobKills(mqs));
            }
        }
        /* 힐라 */
        if (mapid == 262031300) {
            if (mobid == 8870107) {
                if (this.getMonsterById(8870107) == null) {
                    for (final MapleMonster m : this.getAllMonstersThreadsafe()) {
                        if (m.isBuffed(MonsterTemporaryStat.ExchangeAttack)) {
                            m.cancelStatus(MonsterTemporaryStat.ExchangeAttack);
                        }
                    }
                }
            }
        }
        /* 시그너스 */
        if (mapid == 271040100) {
            for (final MapleMonster m : this.getAllMonstersThreadsafe()) {
                if (this.getNumMonsters() == 1) {
                    if (m.isBuffed(MonsterTemporaryStat.ExchangeAttack)) {
                        m.cancelStatus(MonsterTemporaryStat.ExchangeAttack);
                    }
                    m.setBlockedAttack(false);
                    m.getMap().broadcastMessage(CMobPool.OnNextAttack(m.getObjectId(), 1));
                    broadcastMessage(CWvsContext.getTopMsg("시그너스가 자신의 심복이 당한것에 분노하여 모든것을 파괴하려 합니다."));
                }
            }
        }
        /* 무릉 도장 */
        if (mobid == 9305339) {
            if ((mapid / 100000) == 9250) {
                chr.dropMessage(-1, "축하합니다. 무릉 도장의 모든 스테이지를 클리어하셨습니다.");
                if (chr.getMulungTime() > 0) {
                    String clearTime = StringUtil.getReadableMillis(chr.getMulungTime(), System.currentTimeMillis());
                    int updateTime = (int) (System.currentTimeMillis() - chr.getMulungTime());
                    chr.setMulungTime(updateTime);
                    chr.updateMulungRanks(updateTime / 1000);
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[무릉 도장] " + chr.getName() + "님이 무릉 도장에서 " + clearTime + " 기록을 기록하였습니다."));
                    //World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[무릉 도장] " + chr.getName() + "님이 무릉 도장 랭킹 모드의 최고 기록을 갱신하였습니다."));
                }
            }
        }
        /* 블랙윙의 간부 */
        if (mobid == 9300500) {
            if (mapid == 910600102) {
                if (chr.getQuestStatus(25419) == 1) {
                    spawnNpc(1404000, new Point(361, 245));
                }
            }
        }
        /* 습격자 1 */
        if (mobid == 9300285 && chr.getQuestStatus(24071) == 1) {
            spawnNpc(1033222, new Point(5675, 454));
        }
        /* 소생의 불꽃이 있는 곳으로 */
        if (mobid == 9300432 && chr.getQuestStatus(24084) == 1) {
            chr.forceCompleteQuest(24084);
        }
        /* 음모의 정체 */
        if (mobid == 8860001) {
            this.floatNotice("고작 네 녀석같은 미천한 존재 때문에 수 백년 간 기다려 온 기회를 놓치다니...!!", 5120055, false);
        }
        /* 차원의 균열 */
        if (mobid == 9300008 || mobid == 9300014) {
            int mCount = 0;
            MapleMapFactory mFactory = chr.getClient().getChannelServer().getMapFactory();
            for (int i = -1; i < 5; i++) {
                mCount += mFactory.getMap(922010401 + i).getNumMonsters();
            }
            if (mCount == 0) {
                if (chr.getParty() != null) {
                    for (MaplePartyCharacter pUser : chr.getParty().getMembers()) {
                        if (pUser != null) {
                            MapleCharacter cUser = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(pUser.getId());
                            if (cUser != null) {
                                cUser.updateOneInfoQuest(10200000, "pq_ludibrium_clear", "2");
                                cUser.getClient().sendPacket(CField.showEffect("quest/party/clear"));
                                cUser.getClient().sendPacket(CField.playSound("Party1/Clear"));
                                cUser.getClient().sendPacket(CWvsContext.getTopMsg("다음 스테이지로 통하는 포탈이 열렸습니다."));
                                cUser.getMap().floatNotice("다크 아이와 쉐도우 아이를 모두 퇴치하였습니다. 옐로 그린 벌룬에게 말을 걸어 다음 스테이지로 이동해 주세요!", 5120018, false);
                            }
                        }
                    }
                }
                mFactory.getMap(922010400).broadcastMessage(CField.environmentChange("gate", 2));
            }
        }
        /* 아카이럼 원정대 */
        if (mobid == 8860003) {
            final MapleMap akayrumReturnMap = chr.getClient().getChannelServer().getMapFactory().getMap(272020200);
            chr.changeMap(akayrumReturnMap);
        }
        /* 사자 왕의 성 */
        if (mapid == 211060201 || mapid == 211060401 || mapid == 211060601) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    int questID = 0;
                    String msg = "";
                    switch (mapid) {
                        case 211060201: {
                            questID = 3139;
                            msg = "첫 번째";
                            break;
                        }
                        case 211060401: {
                            questID = 3140;
                            msg = "두 번째";
                            break;
                        }
                        case 211060601: {
                            questID = 3141;
                            msg = "세 번째";
                            break;
                        }
                    }
                    chr.updateInfoQuest(questID, "clear=1");
                    chr.getClient().sendPacket(CWvsContext.getShowQuestCompletion(questID));
                    chr.dropMessage(-1, "사자 왕의 성 " + msg + " 봉인이 풀렸습니다.");
                }
            }
        }
        /* 포스의 각성 */
        if (mapid == 927000100) {
            if (mobid == 9001040) {
                final MapleMap nMap = chr.getClient().getChannelServer().getMapFactory().getMap(927000101);
                chr.changeMap(nMap);
            }
        }
        if (mapid == 927000101) {
            if (mobid == 9001041) {
                final MapleMap nMap = chr.getClient().getChannelServer().getMapFactory().getMap(310010000);
                chr.changeMap(nMap);
            }
        }
        /* 커닝 스퀘어 : 락 스피릿 */
        if (mobid == 4300013) {
            MapleQuestStatus qs = chr.getQuestNAdd(MapleQuest.getInstance(2288));
            List<Byte> rand = new ArrayList<>();
            for (byte i = 0; i < 10; i++) {
                rand.add((byte) i);
            }
            Collections.shuffle(rand);
            byte count = (byte) Randomizer.rand(0, 2);
            this.broadcastMessage(CField.playSound("quest2288/" + rand.get(count)));
            this.broadcastMessage(CWvsContext.getTopMsg("지금 나오는 음악을 잘 기억하세요!!"));
            chr.dropMessage(5, "지금 나오는 음악을 잘 기억하세요!!");
            qs.setCustomData(rand.get(0) + "" + rand.get(1) + "" + rand.get(2) + "" + count);
        }
        /* 춤추는 무도회장 : 오르골 */
        if (mapid == 450003750) {
            if (mobid == 8643013) {
                this.resetFully();
                NPCScriptManager.getInstance().start(chr.getClient(), 9000019, "enter_450003751");
            }
        }
        /* 길 잃은 나무의 정령 구출 */
        if (mapid > 940200000 && mapid < 940200250) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    this.broadcastMessage(CField.showEffect("monsterPark/clear"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                }
            }
        }
        /* 정령의 나무를 향해 */
        if (mapid > 940200299 && mapid < 940200321) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    this.broadcastMessage(CField.showEffect("monsterPark/clear"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                }
            }
        }
        /* 실로폰 연못으로 */
        if (mapid == 940200255) {
            if (mobid == 8644021) {
                NPCScriptManager.getInstance().start(chr.getClient(), 9000019, "enter_940200256");
            }
        }
        /* 정령의 나무가 있는 곳 */
        if (mapid == 940200260) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    chr.changeMap(940200280, 0);
                }
            }
        }
        /* 반반 */
        if (mapid == 105200120) {
            if (mobid == 8910001) {
                MapleMap banbanMap = ChannelServer.getInstance(this.getChannel()).getMapFactory().getMap(105200110);
                if (banbanMap != null) {
                    MapleMonster vanvan = banbanMap.getMonsterById(8910000);
                    if (vanvan != null) {
                        if (vanvan.isBuffed(MonsterTemporaryStat.ExchangeAttack)) {
                            vanvan.cancelStatus(MonsterTemporaryStat.ExchangeAttack);
                        }
                        this.floatNotice("시공간 붕괴 실패! 잠시 후, 원래 세계로 돌아갑니다.", 5120025, false);
                        Timer.MapTimer.getInstance().schedule(new Runnable() {
                            public void run() {
                                for (MapleCharacter user : getAllCharactersThreadsafe()) {
                                    user.changeMap(banbanMap, banbanMap.getPortal(6));
                                }
                            }
                        }, 2000);
                    }
                }
            }
        }
        /* 캐쉬 획득 */
        if (chr.getEventInstance() == null) {
            int distance = Math.abs(chr.getLevel() - monster.getStats().getLevel());
            if (distance < 16 || monster.getStats().getLevel() > 140) {
                if (Randomizer.nextInt(100) < 1) {
                    int amount = Randomizer.rand(monster.getStats().getLevel() / 2, monster.getStats().getLevel());
                    chr.modifyCSPoints(1, amount, false);
                    chr.getClient().getSession().write(CField.EffectPacket.showBlueGatherMsg("캐쉬를 얻었습니다.", 0, amount, 0, 0));
                }
            }
        }

        /* 패밀리 */
        if (chr.getFamilyId() > 0) {
            int sensen = World.Family.setRep(chr.getFamilyId(), chr.getSeniorId(), monster.getStats().getLevel(), chr.getLevel(), chr.getName());
            if (sensen > 0) {
                int chance = Randomizer.rand(0, 100);
                if (chance < 1) {
                    World.Family.setRep(chr.getFamilyId(), sensen, monster.getStats().getLevel() / 2, chr.getLevel(), chr.getName());
                }
            }
        }
        /* 몬스터 파크 */
        if (mapid > 951000000 && mapid < 955000000) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    if (mapid % 10000 == 500) {
                        this.broadcastMessage(CField.showEffect("monsterPark/clearF"));
                        this.broadcastMessage(CField.playSound("Party1/Clear"));
                        this.broadcastMessage(CWvsContext.getTopMsg("모든 스테이지를 클리어 하셨습니다. 포탈을 통해 밖으로 이동해주세요."));
                    } else {
                        this.broadcastMessage(CField.showEffect("monsterPark/clear"));
                        this.broadcastMessage(CField.playSound("Party1/Clear"));
                    }
                }
            }
        }
        /* 독 안개의 숲 */
        if (mapid == 930000100 || mapid == 930000600) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (mapid == 930000600) {
                    if (mobid == 9300182) {
                        if (chr.getParty() != null) {
                            for (MaplePartyCharacter pUser : chr.getParty().getMembers()) {
                                if (pUser != null) {
                                    final MapleCharacter pMember = chr.getMap().getCharacterByName(pUser.getName());
                                    if (pMember != null) {
                                        pMember.updateQuest(7917, "3");
                                    }
                                }
                            }
                        }
                        this.broadcastMessage(CField.showEffect("quest/party/clear"));
                        this.broadcastMessage(CField.playSound("Party1/Clear"));
                    }
                } else {
                    if (tMob.isEmpty()) {
                        this.broadcastMessage(CField.showEffect("quest/party/clear"));
                        this.broadcastMessage(CField.playSound("Party1/Clear"));
                    }
                }
            }
        }
        /* 해적 데비존 */
        if (mapid == 925100000 || mapid == 925100500) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (mapid == 925100500) {
                    if (mobid == 9300119) {
                        this.broadcastMessage(CField.showEffect("quest/party/clear"));
                        this.broadcastMessage(CField.playSound("Party1/Clear"));
                    }
                } else {
                    if (tMob.isEmpty()) {
                        this.broadcastMessage(CField.showEffect("quest/party/clear"));
                        this.broadcastMessage(CField.playSound("Party1/Clear"));
                    }
                }
            }
        }
        /* 로미오와 줄리엣 */
        if (mapid == 926100001 || mapid == 926110001) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    this.broadcastMessage(CField.showEffect("quest/party/clear"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                }
            }
        }
        if (mapid == 926100401 || mapid == 926110401) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (mobid == 9300139 || mobid == 9300140) {
                    this.broadcastMessage(CField.showEffect("quest/party/clear"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                    final MapleMapFactory mFactory = ChannelServer.getInstance(chr.getClient().getChannel()).getMapFactory();
                    this.resetFully();
                    mFactory.getMap(mapid).spawnNpc(2112004, new java.awt.Point(21, 150));
                    mFactory.getMap(mapid).spawnNpc(2112003, new java.awt.Point(124, 150));
                    mFactory.getMap(mapid + 99).spawnNpc(2112002, new java.awt.Point(232, 150));
                    mFactory.getMap(mapid + 199).spawnNpc(2112003, new java.awt.Point(157, 128));
                    mFactory.getMap(mapid + 199).spawnNpc(2112004, new java.awt.Point(107, 128));
                    mFactory.getMap(mapid + 199).spawnNpc(2112002, new java.awt.Point(320, 128));
                }
            }
        }
        /* 드래곤 라이더 */
        if (mapid > 240080000 && mapid < 240080600) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    this.broadcastMessage(CField.showEffect("quest/party/clear"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                }
            }
        }
        /* 탈출 */
        if (mapid == 921160200 || mapid == 921160400) {
            final List<MapleMonster> tMob = this.getAllMonstersThreadsafe();
            if (tMob != null) {
                if (tMob.isEmpty()) {
                    this.broadcastMessage(CField.showEffect("quest/party/clear"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                }
            }
        }
        /* 시그너스 */
        if (mapid == 271040100) {
            if (mobid == 8850011) {
                monster.getMap().floatNotice("시그너스를 물리치셨습니다. 시그너스의 전당의 정문을 통해 이동해 주시기 바랍니다.", 5120026, false);
            }
        }
        /* 피에르 */
        if (mobid == 8900000 || mobid == 8900001 || mobid == 8900002) {
            for (MapleCharacter user : chr.getMap().getAllCharactersThreadsafe()) {
                if (user != null) {
                    user.removePierreHat();
                }
            }
        }
        /* 윌 */
        if (mobid == 8880301) {
            this.floatNotice("윌이 여유가 없어졌군요. 거울 세계의 가장 깊은 곳이 드러날 것 같아요.", 5120189, false);
        }
        if (mobid == 8880303) {
            this.floatNotice("윌이 진지해졌네요. 거울 속 깊은 곳에 윌의 진심이 비춰질 것 같아요.", 5120189, false);
        }
        /* 진 힐라 */
        if (mobid == 8880405) {
            this.broadcastMessage(CWvsContext.yellowChat("진 힐라 : 내 젊음이...! 내 아름다움이!!!!!!!!"));
        }
        /* 아우프헤벤 */
        switch (mobid) {
            case 9601838:
            case 9601839:
            case 9601840:
            case 9601841: {
                this.broadcastMessage(CWvsContext.getTopMsg("Aufheben prepares for the next battle."));
                break;
            }
            case 9601842: {
                //this.broadcastMessage(CWvsContext.getTopMsg("You will automatically exit in 10 sec."));
                this.floatNotice("You've achieved mission objective!", 5120201, false);
                break;
            }
        }
        /* 골럭스 */
        switch (mobid) {
            case 9390602:
            case 9390610:
            case 9390611:
            case 9390612: {
                this.broadcastMessage(CField.showEffect("monsterPark/clearF"));
                this.broadcastMessage(CField.playSound("Party1/Clear"));
                break;
            }
        }
        switch (mapid) {
            case 863010200:
            case 863010210:
            case 863010220:
            case 863010230:
            case 863010300:
            case 863010310:
            case 863010320:
            case 863010400:
            case 863010410:
            case 863010420: {
                this.broadcastMessage(CWvsContext.getTopMsg("There are " + (this.getNumMonsters() - 3) + " doses of evil energy remaining in this area."));
                if (this.getNumMonsters() < 4) {
                    if (mapid == 863010310 || mapid == 863010410) {
                        this.broadcastMessage(CField.environmentChange("open", 2));
                        this.broadcastMessage(CField.environmentChange("clear", 2));
                    }
                    this.broadcastMessage(CField.showEffect("monsterPark/clearF"));
                    this.broadcastMessage(CField.playSound("Party1/Clear"));
                }
                break;
            }
        }
        /* 가디언 엔젤 슬라임 (월드 보스) */
        if (mobid == 8880700) {
            for (final ChannelServer cs : ChannelServer.getAllInstances()) {
                for (final MapleMap mm : cs.getMapFactory().getAllLoadedMaps()) {
                    Date nDate = new Date();
                    String nMonth = ((nDate.getMonth() + 1) < 10 ? "0" : "");
                    String nDay = ((nDate.getDate() + 1) < 10 ? "0" : "");
                    mm.floatNotice("축하드립니다. " + nMonth + (nDate.getMonth() + 1) + "월 " + nDay + nDate.getDate() + "일 월드 보스의 주인공은 '" + chr.getName() + "' 입니다.", 5120190, false);
                    mm.broadcastMessage(CWvsContext.yellowChat("(월드 보스) 축하드립니다. " + nMonth + (nDate.getMonth() + 1) + "월 " + nDay + nDate.getDate() + "일 월드 보스의 주인공은 '" + chr.getName() + "' 입니다."));
                }
            }
            for (final MapleCharacter mc : this.getAllCharactersThreadsafe()) {
                /* 메소 돈 뭉치 */
                DueyHandler.addNewItemToDb(4310115, Randomizer.rand(1, 2), mc.getId(), ServerConstants.server_Name_Source, "", true);
                /* 럭키 데이 주문서, 장비 강화 주문서, 캐쉬 장비 아이템 1 강화 주문서 */
                if (Randomizer.rand(0, 100) < 3) {
                    int iRand = Randomizer.rand(0, 2);
                    if (iRand == 0) {
                        DueyHandler.addNewItemToDb(2530000, 1, mc.getId(), ServerConstants.server_Name_Source, "", true);
                        String nStr = "[" + ServerConstants.server_Name_Source + "] 축하드립니다. " + mc.getName() + "님께서 월드 보스 3% 참여 보상으로" + "[{" + MapleItemInformationProvider.getInstance().getItemInformation(2530000).name + "}]" + "(을)를 획득하였습니다.";
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, 2530000, nStr));
                    }
                    if (iRand == 1) {
                        DueyHandler.addNewItemToDb(2049300, 1, mc.getId(), ServerConstants.server_Name_Source, "", true);
                        String nStr = "[" + ServerConstants.server_Name_Source + "] 축하드립니다. " + mc.getName() + "님께서 월드 보스 3% 참여 보상으로" + "[{" + MapleItemInformationProvider.getInstance().getItemInformation(2049300).name + "}]" + "(을)를 획득하였습니다.";
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, 2049300, nStr));
                    }
                    if (iRand == 2) {
                        DueyHandler.addNewItemToDb(2430791, 1, mc.getId(), ServerConstants.server_Name_Source, "", true);
                        String nStr = "[" + ServerConstants.server_Name_Source + "] 축하드립니다. " + mc.getName() + "님께서 월드 보스 3% 참여 보상으로" + "[{" + MapleItemInformationProvider.getInstance().getItemInformation(2430791).name + "}]" + "(을)를 획득하였습니다.";
                        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, 2430791, nStr));
                    }
                }
                /* 월드 보스 참여 티켓 */
                DueyHandler.addNewItemToDb(2434733, 1, mc.getId(), ServerConstants.server_Name_Source, "", true);
                mc.getClient().sendPacket(CField.receiveParcel(monster.getName(), true));
            }
            /* 가디언 엔젤 링 */
            DueyHandler.addNewItemToDb(1113313, 1, chr.getId(), ServerConstants.server_Name_Source, "", true);
            chr.getClient().sendPacket(CField.receiveParcel(ServerConstants.server_Name_Source, true));
        }
        /* 심두멸각 */
        if (chr.getJob() > 6100 && chr.getJob() < 6113) {
            final Skill warriorsHeart = SkillFactory.getSkill(61110009);
            if (warriorsHeart != null) {
                int skillLevel = chr.getTotalSkillLevel(warriorsHeart);
                if (skillLevel > 0) {
                    int cRand = Randomizer.nextInt(100);
                    if (cRand < (10 + (2 * skillLevel))) {
                        int healHP = ((chr.getStat().getMaxHp() / 100) * (1 + (skillLevel / 5)));
                        chr.addHP(healHP);
                        chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(61110009, 1, chr.getLevel(), 1, (byte) 1));
                    }
                }
            }
        }
        /* 흡생전기 */
        if (chr.getJob() > 6200 && chr.getJob() < 6213) {
            final Skill warriorsHeart = SkillFactory.getSkill(62110008);
            if (warriorsHeart != null) {
                int skillLevel = chr.getTotalSkillLevel(warriorsHeart);
                if (skillLevel > 0) {
                    int cRand = Randomizer.nextInt(100);
                    if (cRand < skillLevel) {
                        int healHP = ((chr.getStat().getMaxHp() / 100) * skillLevel);
                        chr.addHP(healHP);
                        chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(62110008, 1, chr.getLevel(), 1, (byte) 1));
                    }
                }
            }
        }
        int expedition_coin = 0;
        switch (monster.getId()) {
            case 8800002: { // 자쿰
                expedition_coin = 2;
                break;
            }
            case 8800102:   // 카오스 자쿰
            case 8820001:   // 핑크빈
            case 8840000:   // 반 레온
            case 8860000:   // 아카이럼
            case 8870000: { // 힐라
                expedition_coin = 6;
                break;
            }
            case 8810018: { // 혼테일
                expedition_coin = 4;
                break;
            }
            case 8810122:   // 카오스 혼테일
            case 8880000: { // 매그너스
                expedition_coin = 8;
                break;
            }
            case 8820101:   // 카오스 핑크빈
            case 8850011: { // 시그너스
                expedition_coin = 10;
                break;
            }
            case 8900000:   // 피에르
            case 8900001:   // 피에르
            case 8900002:   // 피에르
            case 8910000:   // 반반
            case 8920000:   // 블러디 퀸
            case 8920001:   // 블러디 퀸
            case 8920002:   // 블러디 퀸
            case 8920003:   // 블러디 퀸
            case 8930000:   // 벨룸
            case 9421586:   // 모리 란 마루
            case 9450066: { // 노히메
                expedition_coin = 15;
                break;
            }
            case 9601511:   // 아케치 미츠히데
            case 8240099:   // 스우
            case 8880101:   // 데미안
            case 8880156:   // 루시드
            case 8880302:   // 윌
            case 8880405:   // 진 힐라
            case 8645009:   // 듄켈
            case 8880518: { // 검은 마법사
                expedition_coin = 20;
                break;
            }
            case 9390600:   // 골럭스
            case 8880614:   // 선택받은 세렌
            case 8880808:   // 감시자 칼로스
            case 8880845: { // 카링
                expedition_coin = 30;
                break;
            }
        }
        if (expedition_coin > 0) {
            int itemList = 0;
            chr.getSpecialItem().clear();
            for (int a1 = 0; a1 < Shop.shop_1064010.length; a1++) {
                if (itemList < 5) {
                    int itemProp = (int) (Math.random() * Shop.shop_1064010.length);
                    int itemID = Shop.shop_1064010[itemProp][0];
                    int itemPrice = Shop.shop_1064010[itemProp][1];
                    MapleShopItem shopItem = new MapleShopItem((short) 1, itemID, itemPrice, 0, 0, (byte) 0);
                    chr.getSpecialItem().add(shopItem);
                    itemList++;
                }
            }
            for (final MapleCharacter user : this.getAllCharactersThreadsafe()) {
                DueyHandler.addNewItemToDb(4310114, expedition_coin, user.getId(), "보스 원정대", "", true);
                switch (monster.getId()) {
                    case 8840000: {
                        DueyHandler.addNewItemToDb(4310009, 1, user.getId(), "보스 원정대", "", true);
                        DueyHandler.addNewItemToDb(4310010, 2, user.getId(), "보스 원정대", "", true);
                        break;
                    }
                }
                DueyHandler.addNewItemToDb(4310031, expedition_coin, user.getId(), "보스 원정대", "", true);
                user.getClient().sendPacket(CField.receiveParcel(monster.getName(), true));
                user.handleClearByMonster(monster.getId());
                user.getClient().sendPacket(CWvsContext.OnPetAutoRoot(720));
            }
        }
        if (mobid == 8870201) {
            for (final MapleCharacter user : this.getAllCharactersThreadsafe()) {
                if (user.hasDisease(MonsterSkill.VampDeath)) {
                    user.dispelDebuff(MonsterSkill.VampDeath);
                    user.dispelDebuff(MonsterSkill.VampDeathSummon);
                }
            }
        }
        if (withDrops) {
            MapleCharacter drop = null;
            if (dropOwner <= 0) {
                drop = chr;
            } else {
                drop = getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            if (drop.getEventInstance() != null && drop.getParty() != null) {
                for (MaplePartyCharacter mpc : chr.getParty().getMembers()) {
                    drop = this.getCharacterById(mpc.getId());
                    dropFromMonster(drop, monster);
                }
            } else {
                dropFromMonster(drop, monster);
            }
        }
        /* 주의 */
        if (!chr.isGM()) {
            if (!monster.getStats().isBoss()) {
                int a27 = (monster.getStats().getLevel() - chr.getLevel());
                if (a27 > 50) {
                    String nStr = "(주의) 캐릭터 : (Lv." + chr.getLevel() + ") " + chr.getName() + " | 몬스터 : (Lv." + monster.getStats().getLevel() + ") " + monster.getName();
                    World.Broadcast.broadcastGMMessage(CUserLocal.chatMsg(ChatType.GroupParty, nStr));
                }
            }
        }
    }

    public List<MapleReactor> getAllReactor() {
        return getAllReactorsThreadsafe();
    }

    public List<MapleReactor> getAllReactorsThreadsafe() {
        ArrayList<MapleReactor> ret = new ArrayList<MapleReactor>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ret.add((MapleReactor) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleSummon> getAllSummonsThreadsafe() {
        ArrayList<MapleSummon> ret = new ArrayList<MapleSummon>();
        mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.SUMMON).values()) {
                if (mmo instanceof MapleSummon) {
                    ret.add((MapleSummon) mmo);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllDoor() {
        return getAllDoorsThreadsafe();
    }

    public List<MapleMapObject> getAllDoorsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.DOOR).values()) {
                if (mmo instanceof MapleDoor) {
                    ret.add(mmo);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMechDoorsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.DOOR).values()) {
                if (mmo instanceof MechDoor) {
                    ret.add(mmo);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMerchant() {
        return getAllHiredMerchantsThreadsafe();
    }

    public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
                ret.add(mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMonster> getAllMonster() {
        return getAllMonstersThreadsafe();
    }

    public List<MapleMonster> getAllMonstersThreadsafe() {
        ArrayList<MapleMonster> ret = new ArrayList<MapleMonster>();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                ret.add((MapleMonster) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public List<String> getAllUniqueMobs() {
        ArrayList<String> retMobs = new ArrayList<String>();
        ArrayList<Integer> ret = new ArrayList<Integer>();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                MapleMonster fmmo = (MapleMonster) mmo;
                final int theId = ((MapleMonster) mmo).getId();
                if (!ret.contains(theId)) {
                    ret.add(theId);
                    retMobs.add("" + fmmo.getName() + " : " + theId);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return retMobs;
    }

    public List<Integer> getAllUniqueMonsters() {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                final int theId = ((MapleMonster) mmo).getId();
                if (!ret.contains(theId)) {
                    ret.add(theId);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public final void killAllMonsters(final boolean animate) {
        for (final MapleMapObject monstermo : getAllMonstersThreadsafe()) {
            final MapleMonster monster = (MapleMonster) monstermo;
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(CMobPool.killMonster(monster.getObjectId(), animate ? 1 : -1));
            removeMapObject(monster);
            monster.killed();
        }
    }

    public final void killMonster(final int monsId) {
        for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
            if (((MapleMonster) mmo).getId() == monsId) {
                spawnedMonstersOnMap.decrementAndGet();
                removeMapObject(mmo);
                broadcastMessage(CMobPool.killMonster(mmo.getObjectId(), 1));
                ((MapleMonster) mmo).killed();
                break;
            }
        }
    }

    private String MapDebug_Log() {
        final StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(FileoutputUtil.CurrentReadable_Time());

        sb.append(" | Mapid : ").append(this.mapid);

        charactersLock.readLock().lock();
        try {
            sb.append(" Users [").append(characters.size()).append("] | ");
            for (MapleCharacter mc : characters) {
                sb.append(mc.getName()).append(", ");
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return sb.toString();
    }

    public final void limitReactor(final int rid, final int num) {
        List<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        Map<Integer, Integer> contained = new LinkedHashMap<Integer, Integer>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (contained.containsKey(mr.getReactorId())) {
                    if (contained.get(mr.getReactorId()) >= num) {
                        toDestroy.add(mr);
                    } else {
                        contained.put(mr.getReactorId(), contained.get(mr.getReactorId()) + 1);
                    }
                } else {
                    contained.put(mr.getReactorId(), 1);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public final void destroyReactors(final int first, final int last) {
        List<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    toDestroy.add(mr);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public final void destroyReactor(final int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        Random r = new Random();
        if (reactor == null) {
            return;
        }
        broadcastMessage(CField.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public final void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public final void reloadReactors() {
        List<MapleReactor> toSpawn = new ArrayList<MapleReactor>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor reactor = (MapleReactor) obj;
                broadcastMessage(CField.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor r : toSpawn) {
            removeMapObject(r);
            respawnReactor(r);
        }
    }

    /*
     * command to reset all item-reactors in a map to state 0 for GM/NPC use - not tested (broken reactors get removed
     * from mapobjects when destroyed) Should create instances for multiple copies of non-respawning reactors...
     */
    public final void resetReactors() {
        setReactorState((byte) 0);
    }

    public final void setReactorState() {
        setReactorState((byte) 1);
    }

    public final void setReactorState(final byte state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor) obj).forceHitReactor((byte) state);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void setReactorDelay(final int state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor) obj).setDelay(state);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /*
     * command to shuffle the positions of all reactors in a map for PQ purposes (such as ZPQ/LMPQ)
     */
    public final void shuffleReactors() {
        shuffleReactors(0, 9999999); //all
    }

    public final void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<Point>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    points.add(mr.getPosition());
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    mr.setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /**
     * Automagically finds a new controller for the given monster from the chars
     * on the map...
     *
     * @param monster
     */
    public final void updateMonsterController(final MapleMonster monster) {
        // 이피아?
        if (!monster.isAlive() || monster.getLinkCID() > 0/* || monster.getStats().isEscort()*/) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() != this) {
                monster.getController().stopControllingMonster(monster);
            } else {
                return;
            }
        }
        int mincontrolled = -1;
        MapleCharacter newController = null;
        for (MapleMapObject _obj : mapobjects.get(MapleMapObjectType.PLAYER).values()) {
            MapleCharacter chr = (MapleCharacter) _obj;
            if (!chr.isHidden() && !chr.isClone() && (chr.getControlledSize() < mincontrolled || mincontrolled == -1)) {
                mincontrolled = chr.getControlledSize();
                newController = chr;
            }
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    public final MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        mapobjectlocks.get(type).readLock().lock();
        try {
            return mapobjects.get(type).get(oid);
        } finally {
            mapobjectlocks.get(type).readLock().unlock();
        }
    }

    public final boolean containsNPC(int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC n = (MapleNPC) itr.next();
                if (n.getId() == npcid) {
                    return true;
                }
            }
            return false;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public MapleNPC getNPCById(int id) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC n = (MapleNPC) itr.next();
                if (n.getId() == id) {
                    return n;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public MapleMonster getMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            MapleMonster ret = null;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                MapleMonster n = (MapleMonster) itr.next();
                if (n.getId() == id) {
                    ret = n;
                    break;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public void setLucidBreath() {
        this.spawnNpc(3005827, new Point(200, 43));
        this.spawnNpc(3005836, new Point(100, 43));
        this.spawnNpc(3005836, new Point(300, 43));
        this.spawnNpc(3005836, new Point(500, 43));
        this.spawnNpc(3005836, new Point(700, 43));
        this.spawnNpc(3005836, new Point(900, 43));
        this.spawnNpc(3005836, new Point(1100, 43));
        this.spawnNpc(3005836, new Point(1300, 43));
        this.spawnNpc(3005836, new Point(1500, 43));
        this.spawnNpc(3005836, new Point(1700, 43));
        final MapleMap v0 = this;
        final MapleNPC v1 = this.getNPCById(3005827);
        final MapleNPC v2 = this.getNPCById(3005836);
        if (v1 != null) {
            v0.broadcastMessage(NPCPacket.npcSetSpecialAction(v1.getObjectId(), "special", 0, false));
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (final MapleNPC v3 : v0.getAllNPCsThreadsafe()) {
                        if (v3.getId() == v2.getId()) {
                            v0.broadcastMessage(NPCPacket.npcSetSpecialAction(v3.getObjectId(), "breath", 3150, false));
                        }
                    }
                }
            }, 1500);
        }
    }

    public int getLucidButterFlyCount() {
        int v1 = 0;
        v1 += this.countMonsterById(8880165);
        v1 += this.countMonsterById(8880168);
        v1 += this.countMonsterById(8880169);
        v1 += this.countMonsterById(8880175);
        v1 += this.countMonsterById(8880178);
        v1 += this.countMonsterById(8880179);
        return v1;
    }

    public int getLucidGolemCount() {
        int v1 = 0;
        v1 += this.countMonsterById(8880160);
        v1 += this.countMonsterById(8880161);
        v1 += this.countMonsterById(8880170);
        v1 += this.countMonsterById(8880171);
        v1 += this.countMonsterById(8880180);
        v1 += this.countMonsterById(8880181);
        v1 += this.countMonsterById(8880182);
        v1 += this.countMonsterById(8880183);
        v1 += this.countMonsterById(8880186);
        v1 += this.countMonsterById(8880187);
        v1 += this.countMonsterById(8880188);
        v1 += this.countMonsterById(8880189);
        return v1;
    }

    public int countMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int ret = 0;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                MapleMonster n = (MapleMonster) itr.next();
                if (n.getId() == id) {
                    ret++;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public MapleReactor getReactorById(int id) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            MapleReactor ret = null;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.REACTOR).values().iterator();
            while (itr.hasNext()) {
                MapleReactor n = (MapleReactor) itr.next();
                if (n.getReactorId() == id) {
                    ret = n;
                    break;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns
     * null
     *
     * @param oid
     * @return
     */
    public final MapleMonster getMonsterByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (MapleMonster) mmo;
    }

    public final MapleNPC getNPCByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC) mmo;
    }

    public final MapleReactor getReactorByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor) mmo;
    }

    public final MapleReactor getReactorByName(final String name) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = ((MapleReactor) obj);
                if (mr.getName().equalsIgnoreCase(name)) {
                    return mr;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void spawnNpc(final int id, final Point pos) {
        final MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        addMapObject(npc);
        broadcastMessage(NPCPacket.spawnNPC(npc, true, -1));
    }

    public final void removeNpc(final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if (npc.isCustom() && (npcid == -1 || npc.getId() == npcid)) {
                    broadcastMessage(NPCPacket.removeNPCController(npc.getObjectId()));
                    broadcastMessage(NPCPacket.removeNPC(npc.getObjectId()));
                    itr.remove();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
        }
    }

    public final void hideNpc(final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if (npcid == -1 || npc.getId() == npcid) {
                    broadcastMessage(NPCPacket.removeNPCController(npc.getObjectId()));
                    broadcastMessage(NPCPacket.removeNPC(npc.getObjectId()));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public final void spawnReactorOnGroundBelow(final MapleReactor mob, final Point pos) {
        mob.setPosition(pos); //reactors dont need FH lol
        mob.setCustom(true);
        spawnReactor(mob);
    }

    public final void spawnMonster_sSack(final MapleMonster mob, final Point pos, final int spawnType) {

        int a1 = Randomizer.rand(0, 1);
        switch (mob.getId()) {
            case 8500001:
            case 8500002: {
                mob.setPapulatusClock((byte) 0);
                break;
            }
            case 8645009: {
                this.floatNotice("친위 대장 듄켈 : 나와 나의 군단이 있는 이상 위대하신 분께는 손 끝 하나 대지 못한다!", 5120180, false);
                break;
            }
            case 8880303: {
                this.floatNotice("전혀 다른 2개의 공간에 있는 윌을 동시에 공격해야 해요. 달빛을 모아서 사용하면 다른 쪽으로 이동이 가능 할 것 같아요.", 5120189, false);
                break;
            }
            case 8880405: {
                this.floatNotice("영혼이 타오르는 양초를 힐라가 일정 시간마다 베어 없앨 것 이다. 영혼을 빼앗기지 않게 조심하자.", 5120188, false);
                break;
            }
            case 8900000:
            case 8900001:
            case 8900002: {
                //this.floatNotice("피에르의 티 파티에 온 것을 진심으로 환영한다네!", 5120098, false);
                mob.setChangeTime(System.currentTimeMillis());
                break;
            }
            case 8920000: {
                mob.setChangeTime(System.currentTimeMillis());
                String say = (a1 == 0 ? "어머, 귀여운 손님들이 찾아왔네." : "내가 상대해주겠어요.");
                this.floatNotice(say, 5120099, false);
                break;
            }
            case 8920001: {
                mob.setChangeTime(System.currentTimeMillis());
                String say = (a1 == 0 ? "무엄하다! 감히 대전을 함부로 드나들다니!" : "모두 불태워주마!");
                this.floatNotice(say, 5120100, false);
                break;
            }
            case 8920002: {
                mob.setChangeTime(System.currentTimeMillis());
                String say = (a1 == 0 ? "킥킥, 여기가 죽을 자리인 줄도 모르고 왔구나." : "킥킥, 다 없애주지.");
                this.floatNotice(say, 5120101, false);
                break;
            }
            case 8920003: {
                mob.setChangeTime(System.currentTimeMillis());
                String say = (a1 == 0 ? "흑흑, 당신의 죽음을 미리 슬퍼해드리지요." : "내 고통을 느끼게 해줄게요.");
                this.floatNotice(say, 5120102, false);
                break;
            }
        }

        boolean v1 = false;
        switch (mob.getId()) {
            case 8880140:
            case 8880141:
            case 8880142: {
                v1 = true;
                break;
            }
        }
        if (v1) {
            int v2 = (mob.getId() == 8880140 ? 8880158 : mob.getId() == 8880141 ? 8880166 : 8880176);
            final MapleMonster v3 = MapleLifeFactory.getMonster(v2);
            v3.setPosition(calcPointBelow(new Point(pos.x, pos.y - 1)));
            spawnMonster(v3, spawnType);
        }

        mob.setPosition(calcPointBelow(new Point(pos.x, pos.y - 1)));
        spawnMonster(mob, spawnType);
    }

    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        spawnMonster_sSack(mob, pos, -2);
    }

    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final int x, final int y) { // 편안
        spawnMonster_sSack(mob, new Point(x, y), -2);
    }

    public final int spawnMonsterWithEffectBelow(final MapleMonster mob, final Point pos, final int effect, final int option) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        return spawnMonsterWithEffect(mob, effect, spos, option);
    }

    public final void spawnZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        mainb.setPosition(spos);
        mainb.setFake(true);
        mainb.setFh(0x46);
        //Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);
        final int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007,
            8800008, 8800009, 8800010};
        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            part.setFh(0x46);
            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnChaosZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800103, 8800104, 8800105, 8800106, 8800107,
            8800108, 8800109, 8800110};

        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);

            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnFakeMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    private void checkRemoveAfter(final MapleMonster monster) {
        final int ra = monster.getStats().getRemoveAfter();

        if (ra > 0 && monster.getLinkCID() <= 0) {
            monster.registerKill(ra * 1000);
        }
    }

    public final void spawnRevives(final MapleMonster monster, final int oid) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write(CMobPool.OnMobEnterField(monster, monster.getStats().getSummonType() <= 1 ? -3 : monster.getStats().getSummonType(), oid)); // TODO effect
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public final void spawnMonster(MapleMonster monster, int spawnType) {
        spawnMonster(monster, spawnType, false);
    }

    public final void spawnMonster(final MapleMonster monster, final int spawnType, final boolean overwrite) {

        monster.setMap(this);
        checkRemoveAfter(monster);

        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write(CMobPool.OnMobEnterField(monster, monster.getStats().getSummonType() <= 1 || monster.getStats().getSummonType() == 27 || overwrite ? spawnType : monster.getStats().getSummonType(), 0));
            }
        });
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void applyMobBuff(int mobID, int skillID, int skillLV, int nDuration) {
        for (MapleMonster mm : this.getAllMonstersThreadsafe()) {
            if (mm.getId() == mobID) {
                final Map<MonsterTemporaryStat, Integer> stats = new EnumMap<>(MonsterTemporaryStat.class);
                stats.put(MonsterTemporaryStat.Invincible, 1);
                List<Integer> reflection = new LinkedList<>();
                MobSkill ms = MobSkillFactory.getMobSkill(skillID, skillLV);
                reflection.add(ms.getX());
                mm.applyMonsterBuff(stats, skillID, nDuration, ms, reflection, 0);
            }
        }
    }

    public final int spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos, final int option) {
        try {
            monster.setMap(this);
            monster.setPosition(pos);

            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
                @Override
                public final void sendPackets(MapleClient c) {
                    c.getSession().write(CMobPool.OnMobEnterField(monster, /*effect*/ monster.getStats().getSummonType(), option));
                }
            });
            updateMonsterController(monster);

            spawnedMonstersOnMap.incrementAndGet();
            return monster.getObjectId();
        } catch (Exception e) {
            return -1;
        }
    }

    public final void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write(CMobPool.OnMobEnterField(monster, -4, 0));
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public final void spawnReactor(final MapleReactor reactor) {

        reactor.setMap(this);
        spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(MapleClient c) {
                c.sendPacket(CField.spawnReactor(reactor));
            }
        });
    }

    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnMessageBox(final MapleMessageBox msgbox) {
        spawnAndAddRangedMapObject(msgbox, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                msgbox.sendSpawnData(c);
            }
        });
    }

    public final void spawnDoor(final MapleDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {
            public final void sendPackets(MapleClient c) {
                door.sendSpawnData(c);
                c.getSession().write(CWvsContext.enableActions());
            }
        });
    }

    public final void spawnMechDoor(final MechDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {
            public final void sendPackets(MapleClient c) {
                c.getSession().write(CField.spawnMechDoor(door, true));
                c.getSession().write(CWvsContext.enableActions());
            }
        });
    }

    public final void spawnSummon(final MapleSummon summon, boolean animation) {
        summon.updateMap(this);
        spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                if (summon != null && c.getPlayer() != null && (!summon.isChangedMap() || summon.getOwnerId() == c.getPlayer().getId())) {
                    // 소환수
                    c.getPlayer().addSummonList(summon);
                    c.getSession().write(SummonPacket.spawnSummon(summon, animation));
                }
            }
        });
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (summon.isReaper()) {
                    broadcastMessage(SummonPacket.removeSummon(summon, true));
                    removeMapObject(summon);
                    summon.getOwner().removeVisibleMapObject(summon);
                    summon.getOwner().removeSummon(summon);
                }
            }
        }, 10000 + 2000 * ((summon.getSkillLevel()) / 2));
        MapTimer.getInstance().schedule(new Runnable() { // 워머신
            @Override
            public void run() {
                if (summon.isWarMachine()) {
                    broadcastMessage(SummonPacket.removeSummon(summon, true));
                    removeMapObject(summon);
                    summon.getOwner().removeVisibleMapObject(summon);
                    summon.getOwner().removeSummon(summon);
                }
            }
        }, 5300);
    }

    public final void spawnExtractor(final MapleExtractor ex) {
        spawnAndAddRangedMapObject(ex, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                ex.sendSpawnData(c);
            }
        });
    }

    public final void spawnMist(final MapleMist mist, final int duration, boolean fake) {
        spawnAndAddRangedMapObject(mist, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                mist.sendSpawnData(c);
            }
        });

        final MapTimer tMan = MapTimer.getInstance();
        ScheduledFuture<?> poisonSchedule;
        switch (mist.isPoisonMist()) {
            case 0:
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            final MapleCharacter chr = ((MapleCharacter) mo);
                            if (chr != null && chr.isAlive()) {
                                if (mist.getMobSkill().getX() > 0) {
                                    chr.addHP(-mist.getMobSkill().getX());
                                    broadcastMessage(CField.playerDamaged(chr.getId(), mist.getMobSkill().getX()));
                                }
                            }
                        }
                        int mLv = mist.getMobSkill().getSkillLevel();
                        if (mLv == 15 || mLv == 16) {
                            for (MapleCharacter user : getAllCharactersThreadsafe()) {
                                if (user != null) {
                                    EventInstanceManager eim = user.getEventInstance();
                                    if (eim != null) {
                                        for (final MapleMapObject moMonster : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                                            final MapleMonster monster = ((MapleMonster) moMonster);
                                            if (monster != null) {
                                                if (monster.getId() == 8910000) {
                                                    if (mLv == 15) {
                                                        eim.restartEventTimer(eim.getTimeLeft() - 5000);
                                                    } else {
                                                        eim.restartEventTimer(eim.getTimeLeft() + 5000);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 1000, 1500);
                break;
            case 1:
                final MapleCharacter owner = getCharacterById(mist.getOwnerId());
                final boolean pvp = owner.inPVP();
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER))) {
                            if (pvp && mist.makeChanceResult() && !((MapleCharacter) mo).hasDOT() && ((MapleCharacter) mo).getId() != mist.getOwnerId()) {
                                ((MapleCharacter) mo).setDOT(mist.getSource().getDOT(), mist.getSourceSkill().getId(), mist.getSkillLevel());
                            } else if (!pvp && mist.makeChanceResult() && !((MapleMonster) mo).isBuffed(MonsterTemporaryStat.Poison)) {
                                ((MapleMonster) mo).applyStatus(owner, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Poison, 1, mist.getSourceSkill().getId(), null, false), true, duration, true, mist.getSource());
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            case 3:
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final MapleCharacter chr = ((MapleCharacter) mo);
                                if (chr != null && chr.isAlive()) {
                                    if (chr.getBuffSource(CharacterTemporaryStat.DamAbsorbShield) != 62111004
                                            || chr.getBuffSource(CharacterTemporaryStat.AsrR) != 62111004) {
                                        final MapleCharacter bLeader = mist.getOwner();
                                        if (bLeader != null) {
                                            final Skill bSkill = SkillFactory.getSkill(62111004);
                                            if (bSkill != null) {
                                                final int bLv = bLeader.getTotalSkillLevel(bSkill);
                                                if (bLv > 0) {
                                                    final MapleStatEffect bEff = bSkill.getEffect(bLv);
                                                    if (bEff != null) {
                                                        EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                                                        final long starttime = System.currentTimeMillis();
                                                        final int buffTime = Integer.MAX_VALUE;
                                                        localstatups.put(CharacterTemporaryStat.DamAbsorbShield, bEff.getX());
                                                        localstatups.put(CharacterTemporaryStat.AsrR, 100);
                                                        chr.getClient().sendPacket(BuffPacket.giveBuff(62111004, Integer.MAX_VALUE, localstatups, bEff));
                                                        final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(chr, bEff, starttime, localstatups);
                                                        final ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, buffTime);
                                                        chr.registerEffect(bEff, starttime, schedule, localstatups, false, buffTime, chr.getId());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            case 4:
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final MapleCharacter chr = ((MapleCharacter) mo);
                                if (chr != null && chr.isAlive()) {
                                    chr.addMP((int) (mist.getSource().getX() * (chr.getStat().getMaxMp() / 100.0)) / 22);
                                    chr.addHP((int) (mist.getSource().getHp() * (chr.getStat().getMaxHp() / 100.0)) / 22);
                                }
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            case 6:
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final MapleCharacter chr = ((MapleCharacter) mo);
                                if (chr != null && chr.isAlive()) {
                                    final MapleCharacter bLeader = mist.getOwner();
                                    if (bLeader != null) {
                                        final Skill bSkill = SkillFactory.getSkill(2321016);
                                        if (bSkill != null) {
                                            final int bLv = bLeader.getTotalSkillLevel(bSkill);
                                            if (bLv > 0) {
                                                final MapleStatEffect bEff = bSkill.getEffect(bLv);
                                                if (bEff != null) {
                                                    if (chr.getBuffSource(CharacterTemporaryStat.IgnoreTargetDEF) != 2321016) {
                                                        final CharacterTemporaryStat cts = CharacterTemporaryStat.IgnoreTargetDEF;
                                                        int ctv = (5 + (bLeader.getStat().getTotalInt() / bEff.getY()));
                                                        if (ctv > 30) {
                                                            ctv = 30;
                                                        }
                                                        int skillID = 2321016;
                                                        int skillLv = bLeader.getTotalSkillLevel(2321016);
                                                        final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
                                                        int duration = Integer.MAX_VALUE;
                                                        chr.setTemporaryStat(cts, ctv, mse, skillID, duration);
                                                    }
                                                    int a27 = (1 + (bLeader.getStat().getTotalInt() / bEff.getY()));
                                                    if (a27 > 10) {
                                                        a27 = 10;
                                                    }
                                                    chr.addMP((int) (a27 * (chr.getStat().getMaxMp() / 100.0)));
                                                    chr.addHP((int) (a27 * (chr.getStat().getMaxHp() / 100.0)));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            default:
                poisonSchedule = null;
                break;
        }
        final MapleMap mistMap = this;
        mist.setPoisonSchedule(poisonSchedule);
        mist.setSchedule(tMan.schedule(new Runnable() {
            @Override
            public void run() {
                for (final MapleCharacter user : mistMap.getAllCharactersThreadsafe()) {
                    if (user != null) {
                        if (mist.isPoisonMist() == 3) {
                            if (user.getBuffSource(CharacterTemporaryStat.AsrR) == 62111004) {
                                user.cancelEffectFromBuffStat(CharacterTemporaryStat.AsrR);
                            }
                            if (user.getBuffSource(CharacterTemporaryStat.DamAbsorbShield) == 62111004) {
                                user.cancelEffectFromBuffStat(CharacterTemporaryStat.DamAbsorbShield);
                            }
                        }
                        if (mist.isPoisonMist() == 6) {
                            if (user.getBuffSource(CharacterTemporaryStat.IgnoreTargetDEF) == 2321016) {
                                user.cancelEffectFromBuffStat(CharacterTemporaryStat.IgnoreTargetDEF);
                            }
                        }
                    }
                }
                broadcastMessage(CField.removeMist(mist.getObjectId(), false));
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, mist.isPoisonMist() == 6 ? duration + 2000 : duration));
    }

    public final void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 1, false);
        broadcastMessage(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 3), drop.getTruePosition());
    }

    public final void spawnMesoDrop(final int meso, final Point position, MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        if (!everlast) {
            mdrop.registerExpire(120000);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000);
            }
        }
    }

    public final void spawnMobMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), position, (byte) 1));
            }
        });
        /*
        if (owner.getEventInstance() == null) {
            int v1 = 0;
            double v2 = 150000.0;
            for (final MaplePet mPet : owner.getPets()) {
                if (mPet != null) {
                    if (mPet.getSummoned()) {
                        if (mPet.getPetItemId() == 5000046) {
                            v1++;
                        }
                        if (mPet.getPetItemId() == 5000231) {
                            v1++;
                        }
                        if (mPet.getPetItemId() == 5000232) {
                            v1++;
                        }
                        if (mPet.getPetItemId() == 5000233) {
                            v1++;
                        }
                    }
                }
            }
            if (v1 > 0) {
                v2 *= v1;
                for (MapleMapObject v3 : owner.getMap().getMapObjectsInRange(owner.getTruePosition(), v2, Arrays.asList(MapleMapObjectType.ITEM))) {
                    if (v3 != null) {
                        final MapleMapItem mapitem = (MapleMapItem) v3;
                        if (mapitem.isPlayerDrop()) {
                            continue;
                        }
                        if (mapitem.getMeso() > 0) {
                            InventoryHandler.removeItem(owner, mapitem, v3);
                            owner.gainMeso(mapitem.meso, true);
                        } else {
                            MapleInventoryManipulator.addFromDrop(owner.getClient(), mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster, false);
                        }
                    }
                }
            }
        }
         */
        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
    }

    public final void spawnMobDrop(final Item idrop, final Point dropPos, final MapleMonster mob, final MapleCharacter chr, final byte droptype, final int questid) {

        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, questid);
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                if (c != null && c.getPlayer() != null && (questid <= 0 || c.getPlayer().getQuestStatus(questid) == 1) && mob != null && dropPos != null) {
                    chr.getClient().getSession().write(CField.dropItemFromMapObject(mdrop, mob.getTruePosition(), dropPos, (byte) 1));
                }
            }
        });
        if (chr.getEventInstance() == null || chr.getParty() == null) {
            broadcastMessage(CField.dropItemFromMapObject(mdrop, mob.getTruePosition(), dropPos, (byte) 1));
        }
        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
        activateItemReactors(mdrop, chr.getClient());
    }

    public final void spawnRandDrop() {
        if (mapid != 910000000 || channel != 1) {
            return; //fm, ch1
        }

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem) o).isRandDrop()) {
                    return;
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        MapTimer.getInstance().schedule(new Runnable() {
            public void run() {
                final Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
                final int theItem = Randomizer.nextInt(1000);
                int itemid = 0;
                if (theItem < 950) { //0-949 = normal, 950-989 = rare, 990-999 = super
                    itemid = GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)];
                } else if (theItem < 990) {
                    itemid = GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)];
                } else {
                    itemid = GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)];
                }
                spawnAutoDrop(itemid, pos);
            }
        }, 20000);
    }

    public final void spawnAutoDrop(final int itemid, final Point pos) {
        Item idrop = null;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        //idrop.setGMLog("Dropped from auto " + " on " + mapid);
        final MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 1));
            }
        });
        broadcastMessage(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 0));
        if (itemid == 4001101) {
            mdrop.registerExpire(6000);
        } else if (itemid / 10000 != 291) {
            mdrop.registerExpire(120000);
        }
    }

    public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 2, playerDrop);

        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        broadcastMessage(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 0));

        if (!everlast) {
            drop.registerExpire(120000);
            activateItemReactors(drop, owner.getClient());
        }
    }

    private void activateItemReactors(final MapleMapItem drop, final MapleClient c) {
        final Item item = drop.getItem();

        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject o : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor react = (MapleReactor) o;

                if (react.getReactorType() == 100) {
                    if (item.getItemId() == GameConstants.getCustomReactItem(react.getReactorId(), react.getReactItem().getLeft()) && react.getReactItem().getRight() == item.getQuantity()) {
                        if (react.getArea().contains(drop.getTruePosition())) {
                            if (!react.isTimerActive()) {
                                MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000);
                                react.setTimerActive(true);
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public int getItemsSize() {
        return mapobjects.get(MapleMapObjectType.ITEM).size();
    }

    public int getMessageBoxSize() {
        return mapobjects.get(MapleMapObjectType.MESSAGEBOX).size();
    }

    public int getExtractorSize() {
        return mapobjects.get(MapleMapObjectType.EXTRACTOR).size();
    }

    public int getMobsSize() {
        return mapobjects.get(MapleMapObjectType.MONSTER).size();
    }

    public List<MapleMapItem> getAllItems() {
        return getAllItemsThreadsafe();
    }

    public List<MapleMapItem> getAllItemsThreadsafe() {
        ArrayList<MapleMapItem> ret = new ArrayList<MapleMapItem>();
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                ret.add((MapleMapItem) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMessageBox> getAllMsgBoxesThreadsafe() {
        ArrayList<MapleMessageBox> ret = new ArrayList<MapleMessageBox>();
        mapobjectlocks.get(MapleMapObjectType.MESSAGEBOX).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MESSAGEBOX).values()) {
                ret.add((MapleMessageBox) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MESSAGEBOX).readLock().unlock();
        }
        return ret;
    }

    public Point getPointOfItem(int itemid) {
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                MapleMapItem mm = ((MapleMapItem) mmo);
                if (mm.getItem() != null && mm.getItem().getItemId() == itemid) {
                    return mm.getPosition();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return null;
    }

    public List<MapleMist> getAllMistsThreadsafe() {
        ArrayList<MapleMist> ret = new ArrayList<MapleMist>();
        mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MIST).values()) {
                ret.add((MapleMist) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
        }
        return ret;
    }

    public final void returnEverLastItem(final MapleCharacter chr) {
        for (final MapleMapObject o : getAllItemsThreadsafe()) {
            final MapleMapItem item = ((MapleMapItem) o);
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);
                broadcastMessage(CField.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getTruePosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeMapObject(item);
            }
        }
        spawnRandDrop();
    }

    public final void talkMonster(final String msg, final int itemId, final int objectid, int time) {
        if (itemId > 0) {
            floatNotice(msg, itemId, false);
        }
        broadcastMessage(CMobPool.talkMonster(objectid, itemId, msg, time));
        broadcastMessage(CMobPool.removeTalkMonster(objectid));
    }

    public final void floatNotice(final String msg, final int itemId, final boolean jukebox) {
        if (mapEffect != null) {
            //return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        mapEffect.setJukebox(jukebox);
        broadcastMessage(mapEffect.makeStartData());
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (mapEffect != null) {
                    broadcastMessage(mapEffect.makeDestroyData());
                    mapEffect = null;
                }
            }
        }, jukebox ? 300000 : 30000);
    }

    /*
    public final void startExtendedMapEffect(final String msg, final int itemId) {
        broadcastMessage(CField.startMapEffect(msg, itemId, true));
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                broadcastMessage(CField.removeMapEffect());
                broadcastMessage(CField.startMapEffect(msg, itemId, false));
                //dont remove mapeffect.
            }
        }, 60000);
    }

    public final void startSimpleMapEffect(final String msg, final int itemId) {
        broadcastMessage(CField.startMapEffect(msg, itemId, true));
    }

    public final void startJukebox(final String msg, final int itemId) {
        startMapEffect(msg, itemId, true);
    }
     */
    boolean onFirstUserEnterScriptRunned = false;

    public final void addPlayer(final MapleCharacter chr) {
        mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
        } finally {
            mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().unlock();
        }
        charactersLock.writeLock().lock();
        try {
            characters.add(chr);
        } finally {
            charactersLock.writeLock().unlock();
        }
        chr.setChangeTime();
        if (GameConstants.isTeamMap(mapid) && !chr.inPVP()) {
            chr.setTeam(getAndSwitchTeam() ? 0 : 1);
        }
        final byte[] packet = CField.OnUserEnterField(chr);
        if (!chr.isHidden()) {
            broadcastMessage(chr, packet, false);
        } else {
            broadcastGMMessage(chr, packet, false);
        }

        if (!onFirstUserEnter.equals("")) {
            NPCScriptManager.getInstance().start(chr.getClient(), 9000019, onFirstUserEnter);
        }
        if (!onUserEnter.equals("")) {
            NPCScriptManager.getInstance().start(chr.getClient(), 9000019, onUserEnter);
        }

        /* 아케인 리버 */
        if (mapid == 450005010) {
            NPCScriptManager.getInstance().start(chr.getClient(), 9000019, "enter_450005010");
        }
        if (mapid == 940200280) {
            NPCScriptManager.getInstance().start(chr.getClient(), 9000019, "enter_940200280");
        }

        /* 길 잃은 나무의 정령 구출, 정령의 나무를 향해 */
        if (mapid == 940200220 || mapid == 940200300) {
            this.broadcastMessage(CField.showEffect("monsterPark/stageEff/stage"));
            this.broadcastMessage(CField.showEffect("monsterPark/stageEff/number/1"));
        }
        if (mapid == 940200230 || mapid == 940200310) {
            this.broadcastMessage(CField.showEffect("monsterPark/stageEff/stage"));
            this.broadcastMessage(CField.showEffect("monsterPark/stageEff/number/2"));
        }
        if (mapid == 940200240 || mapid == 940200320) {
            this.broadcastMessage(CField.showEffect("monsterPark/stageEff/stage"));
            this.broadcastMessage(CField.showEffect("monsterPark/stageEff/number/3"));
        }

        sendObjectPlacement(chr);
        chr.getClient().getSession().write(packet);

        if (this.getBarrier() > 0) {
            this.floatNotice(this.getBarrier() + " 이상의 스타 포스가 필요 한 지역입니다.", 5121158, false);
        }

        if (this.getBarrierArc() > 0) {
            this.floatNotice(this.getBarrierArc() + " 이상의 아케인 포스가 필요 한 지역입니다.", 5121158, false);
        }

        if (mapid == 262031300) {
            chr.getClient().sendPacket(CWvsContext.sendString(4, "TotalDeathCount", "50"));
            chr.getClient().sendPacket(CWvsContext.sendString(4, "DeathCount", "40"));
        }

        if (GameConstants.isTeamMap(mapid) && !chr.inPVP()) {
            chr.getClient().getSession().write(CField.showEquipEffect(chr.getTeam()));
        }
        int petEnhance = 0;
        for (final MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                Item a27 = chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition());
                if (a27.getOwner() != null && !a27.getOwner().equals("")) {
                    petEnhance += Integer.parseInt(a27.getOwner());
                }
                broadcastMessage(chr, CPet.showPet(chr, pet, false, false), true);
                chr.getClient().getSession().write(CPet.loadPetPickupExceptionList(chr, pet));
            }
        }
        if (petEnhance > 0) {
            final Skill petSkill = SkillFactory.getSkill(8000091);
            if (petSkill != null) {
                chr.changeSingleSkillLevel(petSkill, petEnhance, (byte) petEnhance);
            }
        }

        if (chr.getAndroid() != null) {
            chr.getAndroid().setPos(chr.getPosition());
            broadcastMessage(CField.spawnAndroid(chr, chr.getAndroid()));
        }

        if (chr.getParty() != null) {
            chr.silentPartyUpdate();
            chr.getClient().getSession().write(PartyPacket.updateParty(chr.getClient().getChannel(), chr.getParty(), PartyOperation.SILENT_UPDATE, null));
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
        }

        chr.getClient().sendPacket(CField.setQuickMoveInfo(chr));
        if (GameConstants.isPhantom(chr.getJob())) {
            chr.getClient().sendPacket(CUserLocal.incJudgementStack(chr.getCardStack()));
        }
        final List<MapleSummon> ss = chr.getSummonsReadLock();
        try {
            for (MapleSummon summon : ss) {
                summon.setPosition(chr.getTruePosition());
                chr.addVisibleMapObject(summon);
                this.spawnSummon(summon, false);
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        //    System.out.print("테스트5");
        if (timeLimit > 0 && getForcedReturnMap() != null && !chr.isClone()) {
            chr.startMapTimeLimitTask(timeLimit, getForcedReturnMap());
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.MonsterRiding) != null && !GameConstants.isResist(chr.getJob())) {
            if (FieldLimitType.Mount.check(fieldLimit)) {
                chr.cancelEffectFromBuffStat(CharacterTemporaryStat.MonsterRiding);
            }
        }

        /*
        if (mapid == 910000000) {
            this.broadcastMessage(FieldEffect.changeBGM("Bgm53/ForEinherjar"));
        }
         */
        //  System.out.print("테스트6");
        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted() && !chr.isClone()) {
            if (chr.inPVP()) {
                chr.getClient().getSession().write(CField.getPVPClock(Integer.parseInt(chr.getEventInstance().getProperty("type")), (int) (chr.getEventInstance().getTimeLeft() / 1000)));
            } else {
                if (MapConstants.isDeathCountMap(chr.getMapId()) != -1) {
                    chr.getClient().sendPacket(CField.getDeathCountClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
                } else {
                    chr.getClient().sendPacket(CField.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
                }
            }
        }
        if (hasClock()) {
            final Calendar cal = Calendar.getInstance();
            chr.getClient().getSession().write((CField.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
        }

        /* 데스 카운트 */
        if (MapConstants.isDeathCountMap(chr.getMapId()) != -1) {
            chr.setDeathCount(chr.getDeathCount());
        }

        if (chr.getCarnivalParty() != null && chr.getEventInstance() != null) {
            chr.getEventInstance().onMapLoad(chr);
        }
        MapleEvent.mapLoad(chr, channel);
        if (getNumMonsters() > 0 && (mapid == 280030100 || mapid == 280030001 || mapid == 240060201 || mapid == 280030000 || mapid == 240060200 || mapid == 220080001 || mapid == 541020800 || mapid == 541010100)) {
            String music = "Bgm09/TimeAttack";
            switch (mapid) {
                case 240060200:
                case 240060201:
                    music = "Bgm14/HonTale";
                    break;
                case 280030000:
                case 280030001:
                case 280030100:
                    music = "Bgm06/FinalFight";
                    break;
            }
            chr.getClient().getSession().write(CField.musicChange(music));
        }
        if (GameConstants.isEvan(chr.getJob()) && chr.getJob() >= 2200) {
            if (chr.getDragon() == null) {
                chr.makeDragon();
            } else {
                chr.getDragon().setPosition(chr.getPosition());
            }
            if (chr.getDragon() != null) {
                broadcastMessage(CField.spawnDragon(chr.getDragon()));
            }
        }
        if (mapid == 200101000) {
            chr.setKeyValue("정찰", "1");
            chr.dropMessage(-1, "콜로세움을 정찰하였습니다. < 1 / 1 >");
        }
        if (mapid == 252000000) {
            chr.getClient().getSession().write(CField.MapEff("temaD/enter/goldTemple"));
        }
        if (mapid == 103040000) {
            chr.getClient().getSession().write(CField.MapEff("temaD/enter/kerning"));
        }
        if (mapid == 231000000) { // 211060000
            chr.getClient().getSession().write(CField.MapEff("temaD/enter/sakuraCastle"));
        }
        if (mapid == 211060000) { // 211060000
            chr.getClient().getSession().write(CField.MapEff("temaD/enter/lionCastle"));
        }
        if (mapid == 912050000) { // 211060000
            //  chr.getClient().getSession().write(CField.MapEff("temaD/enter/lionCastle"));
            this.floatNotice("야생 돌고래들이 주변으로 몰려 와 점프하기 시작합니다!", 5120063, true);
        }
        if (mapid == 272020200) {
            if (this.getMonsterById(8860000) == null) {
                this.floatNotice("용기와 만용을 구분하지 못하는 자들이여. 목숨이 아깝지 않다면 내게 덤비도록. 후후.", 5120056, false);
            }
        }
        if (mapid == 272020300) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8860003), new Point(16, 95));
            this.floatNotice("자신 속의 추악한 모습을 마주한 기분이 어떠신지요?", 5120057, false);
        }
        if (permanentWeather > 0) {
            chr.getClient().getSession().write(CField.floatNotice("", permanentWeather, false));
        }
        if (getPlatforms().size() > 0) {
            chr.getClient().getSession().write(CField.getMovingPlatforms(this));
        }

        Collection<MapleMonster> mobs = getAllMonster();
        for (MapleMonster mob : mobs) {
            if (mob.getController() == null) {
                updateMonsterController(mob);
            }
        }
        MapleMap fReturnMap = chr.getMap().getReturnMap();
        /* 몬스터 라이딩 */
        if (mapid == 923010000 || mapid == 922200000) {
            chr.startMapTimeLimitTask(60 * 5, fReturnMap);
        }
        /* 쿤의 항해 : 용이 잠든 섬으로 & 리스 항구행 */
        if (mapid == 200090080 || mapid == 200090090) {
            MapleMap mapto = chr.getClient().getChannelServer().getMapFactory().getMap(914100000);
            if (mapid == 200090090) {
                mapto = chr.getClient().getChannelServer().getMapFactory().getMap(104000000);
            }
            chr.startMapTimeLimitTask(60, mapto);
        }
        /* 메르세데스 스토리 퀘스트 : 낯선 세계 */
        if (mapid == 101000000) {
            if (chr.getQuestStatus(24046) == 1) {
                MapleQuest.getInstance(24094).forceStart(chr, 0, "1");
            }
        }
        /* 버섯의 성 : 성벽을 넘어서 (4) */
        if (mapid == 106020600) {
            if (chr.getQuestStatus(2324) == 1) {
                //chr.removeAll(2430015);
                MapleQuest.getInstance(2324).forceCustomData(chr, "1");
            }
        }
        /* 결혼식장 입구 & 결혼식장 */
        if (mapid == 106021500 || mapid == 106021600) {
            //chr.startMapTimeLimitTask(60 * 10, fReturnMap);
        }
        /* 동굴 깊은 곳 & 여왕의 은신처 */
        if (mapid == 300010420 || mapid == 300030310) {
            //chr.startMapTimeLimitTask(60 * 30, fReturnMap);
        }
        /* 한밤의 부둣가 항만 입구 */
        if (mapid == 240070210) {
            //chr.startMapTimeLimitTask(60 / 2, fReturnMap);
        }
        /* 네오 시티 */
        if (mapid == 240070220
                || mapid == 240070203
                || mapid == 240070303
                || mapid == 240070403
                || mapid == 240070503
                || mapid == 240070603) {
            //chr.startMapTimeLimitTask(60 * 30, fReturnMap);
        }
        /* 판타스틱 애니멀 쇼 공연장 */
        if (mapid == 223030210) {
            //chr.startMapTimeLimitTask(60 * 20, fReturnMap);
        }
        /* 변신술사 */
        if (mapid > 913001999 && mapid < 913002401) {
            //chr.startMapTimeLimitTask(60 * 10, fReturnMap);
        }
        /* 모험가 3차 전직 퀘스트 : 차원의 세계 */
        if (mapid > 910540099 && mapid < 910540501) {
            //chr.startMapTimeLimitTask(60 * 15, fReturnMap);
        }
        /* 크로스 헌터 */
        if (mapid > 931050409 && mapid < 931050437) {
            //chr.startMapTimeLimitTask(60 * 7, fReturnMap);
        }
        /* 사자 왕의 성 */
        if (mapid == 211060201 || mapid == 211060401 || mapid == 211060601 || mapid == 921140100 || mapid == 921140000) {
            //chr.startMapTimeLimitTask(60 * 10, fReturnMap);
        }
        /* 인형사의 동굴 */
        if (mapid == 910510202) {
            //chr.startMapTimeLimitTask(60 * 10, fReturnMap);
        }
        /* 아니의 수감실 */
        if (mapid == 211061100) {
            //chr.startMapTimeLimitTask(60 * 30, fReturnMap);
        }
        if (mapid == 200090520) {
            MapleItemInformationProvider.getInstance().getItemEffect(2210070).applyTo(chr);
            chr.getClient().sendPacket(CWvsContext.InfoPacket.getStatusMsg(2210070));
        }
        /* 수상한 바다 */
        if (mapid == 120041900) {
            //chr.startMapTimeLimitTask(60 * 15, fReturnMap);
        }
        /* 인형의 집 */
        if (mapid == 922000010) {
            //chr.startMapTimeLimitTask(60 * 10, fReturnMap);
        }
        /* 아르마의 은신처, 츄릅 나무 서식지, 정령의 나무가 있는 곳 */
        if (mapid == 450001340 || mapid == 450002250) {
            //chr.startMapTimeLimitTask(60 * 20, fReturnMap);
        }
        /* 망각의 호수, 화염 지대 상공 */
        int tarmingMobFromQuestMap = 0;
        switch (mapid) {
            case 450001007:
            case 450001310: {
                tarmingMobFromQuestMap = 1932116;
                chr.getClient().sendPacket(CWvsContext.staticScreenMessage("배를 조작하여 배를 움직일 수 있습니다.", false));
                break;
            }
            case 450001330: {
                tarmingMobFromQuestMap = 1932117;
                break;
            }
        }
        if (tarmingMobFromQuestMap > 0) {
            final CharacterTemporaryStat cts = CharacterTemporaryStat.MonsterRiding;
            int ctv = tarmingMobFromQuestMap;
            int skillID = 80001000;
            int skillLv = 1;
            final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
            int duration = Integer.MAX_VALUE;
            chr.setTemporaryStat(cts, ctv, mse, skillID, duration);
        } else {
            if (chr.getBuffedValue(CharacterTemporaryStat.MonsterRiding) != null) {
                switch (chr.getBuffedValue(CharacterTemporaryStat.MonsterRiding)) {
                    case 1932116:
                    case 1932117: {
                        chr.cancelBuffStats(false, CharacterTemporaryStat.MonsterRiding);
                        break;
                    }
                }
            }
        }
        /* 풀잎 피리의 숲 */
        if (mapid == 940200330) {
            //chr.startMapTimeLimitTask(60 * 20, fReturnMap);
        }
        /* 나무 줄기 하프의 숲 */
        if (mapid == 940200216) {
            if (chr.getQuestStatus(34469) > 0) {
                chr.getClient().sendPacket(NPCPacket.npcSetSpecialAction(this.getNPCById(3003330).getObjectId(), "normal", 0, false));
            }
        }
        /* 노히메 */
        switch (mapid) {
            case 811000100:
            case 811000200:
            case 811000300:
            case 811000400: {
                int v1 = ((mapid % 10000) / 100);
                chr.getClient().sendPacket(CField.showEffect("monsterPark/stageEff/stage"));
                chr.getClient().sendPacket(CField.showEffect("monsterPark/stageEff/number/" + v1));
                break;
            }
        }
        /* 스우 */
        switch (mapid) {
            case 350060500: {
                this.broadcastMessage(CField.environmentChange("swoo2", 2));
                this.broadcastMessage(CField.environmentChange("swoo5", 2));
                break;
            }
            case 350060600: {
                this.broadcastMessage(CField.environmentChange("swoo1", 2));
                this.broadcastMessage(CField.environmentChange("swoo5", 2));
                break;
            }
        }
        /* 검은 마법사 */
        switch (mapid) {
            case 450013910: {
                this.floatNotice("검은 마법사와 대적하기 위해서는 그를 호위하는 창조와 파괴의 기사들을 물리쳐야 한다.", 5120203, false);
                break;
            }
            case 450013950: {
                this.broadcastMessage(CField.environmentChange("foo1", 2));
                this.broadcastMessage(CField.environmentChange("foo2", 2));
                this.broadcastMessage(CField.environmentChange("foo3", 2));
                this.broadcastMessage(CField.environmentChange("foo4", 2));
                this.broadcastMessage(CField.environmentChange("foo5", 2));
                this.broadcastMessage(CField.environmentChange("foo6", 2));
                break;
            }
            case 450013980: {
                this.floatNotice("창세의 알을 파괴하여 기나 긴 싸움을 마무리 하자.", 5120203, false);
                break;
            }
        }
        /* 선택받은 세렌 */
        switch (mapid) {
            case 410002060:
            case 410002065:
            case 410002070:
            case 410002075: {
                chr.dropMessage(5, "시간이 흐르고 태양 또한 정해진 순환에 따라 변화합니다.");
                break;
            }
        }
        /* 카링 */
        switch (mapid) {
            case 410007140: {
                chr.getClient().sendPacket(CField.playSound("Karing/1phase"));
                chr.getClient().sendPacket(CField.showEffect("Karing/1phaseWeather/0"));
                break;
            }
            case 410007180: {
                chr.getClient().sendPacket(CField.showEffect("Karing/1phaseWeather/1"));
                break;
            }
            case 410007220: {
                chr.getClient().sendPacket(CField.showEffect("Karing/1phaseWeather/2"));
                break;
            }
            case 410007260: {
                chr.getClient().sendPacket(CField.playSound("Karing/2phase"));
                chr.getClient().sendPacket(CField.showEffect("Karing/2phaseWeather/0"));
                break;
            }
            case 410007300: {
                chr.getClient().sendPacket(CField.playSound("Karing/3phase"));
                break;
            }
        }
        /* 골럭스 */
        switch (mapid) {
            case 863010240: {
                if (this.getMonsterById(9390612) == null) {
                    this.broadcastMessage(CField.environmentChange("clear1", 2));
                    this.broadcastMessage(CField.environmentChange("clear2", 2));
                    this.broadcastMessage(CField.environmentChange("change", 2));
                }
                break;
            }
            case 863010310:
            case 863010410: {
                if (this.getNumMonsters() < 4) {
                    this.broadcastMessage(CField.environmentChange("open", 2));
                    this.broadcastMessage(CField.environmentChange("clear", 2));
                }
                break;
            }
            case 863010330: {
                if (this.getMonsterById(9390610) == null) {
                    this.broadcastMessage(CField.environmentChange("phase3", 2));
                    this.broadcastMessage(CField.environmentChange("clear", 2));
                }
                break;
            }
            case 863010430: {
                if (this.getMonsterById(9390611) == null) {
                    this.broadcastMessage(CField.environmentChange("phase3", 2));
                    this.broadcastMessage(CField.environmentChange("clear", 2));
                }
                break;
            }
            case 863010600: {
                if (this.getMonsterById(9390600) == null) {
                    this.broadcastMessage(CField.environmentChange("phase2-1", 2));
                    this.broadcastMessage(CField.environmentChange("phase2-2", 2));
                }
                if (this.getMonsterById(9390601) == null) {
                    this.broadcastMessage(CField.environmentChange("phase3", 2));
                }
                break;
            }
        }
        switch (mapid) {
            case 863010000:
            case 863010100:
            case 863010200:
            case 863010210:
            case 863010220:
            case 863010230:
            case 863010240:
            case 863010300:
            case 863010310:
            case 863010320:
            case 863010330:
            case 863010400:
            case 863010410:
            case 863010420:
            case 863010430:
            case 863010500:
            case 863010600:
            case 863010700: {
                //chr.getClient().sendPacket(CField.showEffect("Gollux/miniMap/" + mapid));
                chr.getClient().sendPacket(CField.showEffect("giantBoss/enter/" + mapid));
                break;
            }
        }
        /* 배 */
        boolean shipMap = false;
        int moveMap = 910000000;
        MapleMap moveShip;
        switch (mapid) {
            case 103020010: {
                shipMap = true;
                moveMap = 103020020;
                break;
            }
            case 200090060: {
                shipMap = true;
                moveMap = 140020300;
                break;
            }
            case 200090070: {
                shipMap = true;
                moveMap = 104000000;
                break;
            }
        }
        if (shipMap) {
            moveShip = chr.getClient().getChannelServer().getMapFactory().getMap(moveMap);
            chr.startMapTimeLimitTask(10, moveShip);
        }

        /* 윌 */
        boolean v1 = false;
        boolean v2 = false;
        if (chr.getMapId() == 450008400) {
            v1 = true;
        }
        if (chr.getMapId() == 450008450) {
            v1 = true;
        }
        if (chr.getMapId() == 450008550) {
            v2 = true;
        }
        if (!v1) {
            final Skill nSkill = SkillFactory.getSkill(8001092);
            chr.changeSingleSkillLevel(nSkill, -1, (byte) -1);
        }
        if (!v2) {
            final Skill nSkill = SkillFactory.getSkill(8001093);
            chr.changeSingleSkillLevel(nSkill, -1, (byte) -1);
        }
        /* 잠수 보상 */
        if (mapid == 910000000) {
            chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "1채널의 자유 시장 입구에서 의자 아이템을 사용 시에 [잠수 보상]을 획득 할 수 있습니다."));
        }
        /* 피에르 */
        chr.removePierreHat();

        /* 네트의 피라미드 */
        chr.getClient().sendPacket(CUserLocal.closeUI(UIType.FIELDITEM));

        switch (chr.getMapId()) {
            case 0:
            case 103050900:
            case 130030000:
            case 900090000:
            case 910150000:
            case 914000000:
            case 931000000: {
                if (chr.getLevel() == 1) {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[" + ServerConstants.server_Name_Source + "] " + chr.getName() + "님께서 캐릭터를 생성하였습니다."));
                }
                break;
            }
        }

        /* 펫 오토 루팅 */
        if (chr.getEventInstance() != null) {
            chr.getClient().sendPacket(CWvsContext.OnPetAutoRoot(720));
            chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "현재 맵에서는 펫의 [아이템 줍기] 기능을 사용 할 수 없습니다."));
        } else {
            chr.getClient().sendPacket(CWvsContext.OnPetAutoRoot(721));
        }
    }

    public int getNumItems() {
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            return mapobjects.get(MapleMapObjectType.ITEM).size();
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
    }

    public int getNumMonsters() {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            return mapobjects.get(MapleMapObjectType.MONSTER).size();
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public final void removePlayer(final MapleCharacter chr) {
        if (everlast) {
            returnEverLastItem(chr);
        }
        charactersLock.writeLock().lock();
        try {
            characters.remove(chr);
        } finally {
            charactersLock.writeLock().unlock();
        }
        removeMapObject(chr);
        chr.checkFollow();
        broadcastMessage(CField.removePlayerFromMap(chr.getId()));
        List<MapleSummon> toCancel = new ArrayList<MapleSummon>();
        final List<MapleSummon> ss = chr.getSummonsReadLock();
        try {
            for (final MapleSummon summon : ss) {
                broadcastMessage(SummonPacket.removeSummon(summon, true));
                for (MapleCharacter player : this.getCharacters()) {
                    player.removeSummonList(summon);
                }
                removeMapObject(summon);
                if (summon.getMovementType() == SummonMovementType.STATIONARY || summon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY || summon.getMovementType() == SummonMovementType.WALK_STATIONARY) {
                    toCancel.add(summon);
                } else {
                    summon.setChangedMap(true);
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (chr.getBuffSource(CharacterTemporaryStat.AsrR) == 62111004) {
            chr.cancelEffectFromBuffStat(CharacterTemporaryStat.AsrR);
        }
        if (chr.getBuffSource(CharacterTemporaryStat.DamAbsorbShield) == 62111004) {
            chr.cancelEffectFromBuffStat(CharacterTemporaryStat.DamAbsorbShield);
        }
        if (chr.getBuffSource(CharacterTemporaryStat.IgnoreTargetDEF) == 2321016) {
            chr.cancelEffectFromBuffStat(CharacterTemporaryStat.IgnoreTargetDEF);
        }
        for (MapleSummon summon : toCancel) {
            chr.removeSummon(summon);
            chr.dispelSkill(summon.getSkill()); //remove the buff
        }
        if (!chr.isClone()) {
            if (mapid == 109020001) {
                chr.canTalk(true);
            }
            chr.leaveMap(this);
        }
    }

    public final void broadcastMessage(final byte[] packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    public final void broadcastMessage(final MapleCharacter source, final byte[] packet, final boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getTruePosition());
    }

    /*	public void broadcastMessage(MapleCharacter source, byte[] packet, boolean repeatToSource, boolean ranged) {
     broadcastMessage(repeatToSource ? null : source, packet, ranged ? MapleCharacter.MAX_VIEW_RANGE_SQ : Double.POSITIVE_INFINITY, source.getPosition());
     }*/
    public final void broadcastMessage(final byte[] packet, final Point rangedFrom) {
        broadcastMessage(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public final void broadcastMessage(final MapleCharacter source, final byte[] packet, final Point rangedFrom) {
        broadcastMessage(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public void broadcastMessage(final MapleCharacter source, final byte[] packet, final double rangeSq, final Point rangedFrom) {
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getTruePosition()) <= rangeSq) {
                            chr.getClient().getSession().write(packet);
                        }
                    } else {
                        chr.getClient().getSession().write(packet);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    private void sendObjectPlacement(final MapleCharacter c) {
        if (c == null) {
            return;
        }
        for (final MapleMapObject o : getMapObjectsInRange(c.getTruePosition(), c.getRange(), GameConstants.rangedMapobjectTypes)) {
            if (o != null) {
                if (o.getType() == MapleMapObjectType.REACTOR && !((MapleReactor) o).isAlive()) {
                    continue;
                }
                o.sendSpawnData(c.getClient());
                c.addVisibleMapObject(o);
            }
        }
    }

    public final List<MaplePortal> getPortalsInRange(final Point from, final double rangeSq) {
        final List<MaplePortal> ret = new ArrayList<MaplePortal>();
        for (MaplePortal type : portals.values()) {
            if (from.distanceSq(type.getPosition()) <= rangeSq && type.getTargetMapId() != mapid && type.getTargetMapId() != 999999999) {
                ret.add(type);
            }
        }
        return ret;
    }

    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq) {
        final List<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
            while (itr.hasNext()) {
                MapleMapObject mmo = itr.next();
                if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                    ret.add(mmo);
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        return getMapObjectsInRange(from, rangeSq, Arrays.asList(MapleMapObjectType.ITEM));
    }

    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        if (MapObject_types != null) {
            for (MapleMapObjectType type : MapObject_types) {
                if (type != null) {
                    Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                    while (itr.hasNext()) {
                        MapleMapObject mmo = itr.next();
                        if (mmo != null) {
                            if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                                ret.add(mmo);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    public final List<MapleMapObject> getMapObjectsInRect(final Rectangle box, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
            while (itr.hasNext()) {
                MapleMapObject mmo = itr.next();
                if (box.contains(mmo.getTruePosition())) {
                    ret.add(mmo);
                }
            }
        }
        return ret;
    }

    public final List<MapleCharacter> getCharactersIntersect(final Rectangle box) {
        final List<MapleCharacter> ret = new ArrayList<MapleCharacter>();
        for (MapleMapObject _obj : mapobjects.get(MapleMapObjectType.PLAYER).values()) {
            MapleCharacter chr = (MapleCharacter) _obj;
            if (chr.getBounds().intersects(box)) {
                ret.add(chr);
            }
        }
        return ret;
    }

    public final List<MapleCharacter> getPlayersInRectAndInList(final Rectangle box, final List<MapleCharacter> chrList) {
        final List<MapleCharacter> character = new LinkedList<MapleCharacter>();
        for (MapleMapObject _obj : mapobjects.get(MapleMapObjectType.PLAYER).values()) {
            MapleCharacter a = (MapleCharacter) _obj;
            if (chrList.contains(a) && box.contains(a.getTruePosition())) {
                character.add(a);
            }
        }
        return character;
    }

    public final void addPortal(final MaplePortal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    public final MaplePortal getPortal(final String portalname) {
        for (final MaplePortal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public final MaplePortal getPortal(final int portalid) {
        return portals.get(portalid);
    }

    public final void resetPortals() {
        for (final MaplePortal port : portals.values()) {
            port.setPortalState(true);
        }
    }

    public final void setFootholds(final MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public final MapleFootholdTree getFootholds() {
        return footholds;
    }

    public final int getNumSpawnPoints() {
        return monsterSpawn.size();
    }

    public final void loadMonsterRate(final boolean first) {
        final int spawnSize = monsterSpawn.size();
        maxRegularSpawn = Math.round(spawnSize * monsterRate);
        if (maxRegularSpawn < 2) {
            maxRegularSpawn = 2;
        } else if (maxRegularSpawn > spawnSize) {
            maxRegularSpawn = spawnSize - (spawnSize / 15);
        }

        maxRegularSpawn *= 2; //몹젠

        Collection<Spawns> newSpawn = new LinkedList<Spawns>();
        Collection<Spawns> newBossSpawn = new LinkedList<Spawns>();
        for (final Spawns s : monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) {
                continue; // Remove carnival spawned mobs
            }
            if (s.getMonster().isBoss()) {
                newBossSpawn.add(s);
            } else {
                newSpawn.add(s);
            }
        }
        monsterSpawn.clear();
        monsterSpawn.addAll(newBossSpawn);
        monsterSpawn.addAll(newSpawn);

        if (first && spawnSize > 0) {
            lastSpawnTime = System.currentTimeMillis();
            if (GameConstants.isForceRespawn(mapid)) {
                createMobInterval = 15000;
            }
            respawn(false);//맵들어갔을때 바로뜸
        }
    }

    public final SpawnPoint addMonsterSpawn(final MapleMonster monster, final int mobTime, final byte carnivalTeam, final String msg) {
        final Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        final SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            monsterSpawn.add(0, sp); //at the beginning
        } else {
            monsterSpawn.add(sp);
        }
        return sp;
    }

    public final void addAreaMonsterSpawn(final MapleMonster monster, Point pos1, Point pos2, Point pos3, final int mobTime, final String msg, final boolean shouldSpawn) {
        pos1 = calcPointBelow(pos1);
        pos2 = calcPointBelow(pos2);
        pos3 = calcPointBelow(pos3);
        if (pos1 != null) {
            pos1.y -= 1;
        }
        if (pos2 != null) {
            pos2.y -= 1;
        }
        if (pos3 != null) {
            pos3.y -= 1;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + mapid + ", monster " + monster.getId() + " could not be spawned.");

            return;
        } else if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, shouldSpawn));
    }

    public final List<MapleCharacter> getCharacters() {
        return getCharactersThreadsafe();
    }

    public final List<MapleCharacter> getCharactersThreadsafe() {
        final List<MapleCharacter> chars = new ArrayList<MapleCharacter>();

        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                chars.add(mc);
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return chars;
    }

    public final MapleCharacter getCharacterByName(final String id) {
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                if (mc.getName().equalsIgnoreCase(id)) {
                    return mc;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return null;
    }

    public final MapleCharacter getCharacterById_InMap(final int id) {
        return getCharacterById(id);
    }

    public final MapleCharacter getCharacterById(final int id) {
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                if (mc.getId() == id) {
                    return mc;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return null;
    }

    public final void updateMapObjectVisibility(final MapleCharacter chr, final MapleMapObject mo) {
        if (chr == null) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) { // monster entered view range
            if (mo instanceof MapleReactor || mo.getType() == MapleMapObjectType.PLAYER || mo.getType() == MapleMapObjectType.MIST || mo.getType() == MapleMapObjectType.EXTRACTOR || mo.getType() == MapleMapObjectType.SUMMON || mo instanceof MechDoor || mo.getTruePosition().distanceSq(chr.getTruePosition()) <= Integer.MAX_VALUE) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else {
            if (!(mo instanceof MapleReactor) && !(mo instanceof MechDoor) && mo.getType() != MapleMapObjectType.PLAYER && mo.getType() != MapleMapObjectType.MIST && mo.getType() != MapleMapObjectType.EXTRACTOR && mo.getType() != MapleMapObjectType.SUMMON && mo.getTruePosition().distanceSq(chr.getTruePosition()) > Integer.MAX_VALUE) {
                chr.removeVisibleMapObject(mo);
                mo.sendDestroyData(chr.getClient());
            }
        }
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        for (MapleMapObject _obj : mapobjects.get(MapleMapObjectType.PLAYER).values()) {
            MapleCharacter mc = (MapleCharacter) _obj;
            updateMapObjectVisibility(mc, monster);
        }
    }

    public void movePlayer(final MapleCharacter player, final Point newPosition) {
        player.setPosition(newPosition);
        if (!player.isClone()) {
            try {
                Collection<MapleMapObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();
                ArrayList<MapleMapObject> copy = new ArrayList<>(visibleObjects);
                Iterator<MapleMapObject> itr = copy.iterator();
                while (itr.hasNext()) {
                    MapleMapObject mo = itr.next();
                    if (mo != null && getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                        updateMapObjectVisibility(player, mo);
                    } else if (mo != null) {
                        visibleObjects.remove(mo);
                    }
                }
                for (MapleMapObject mo : getMapObjectsInRange(player.getTruePosition(), player.getRange())) {
                    if (mo != null && !visibleObjects.contains(mo)) {
                        mo.sendSpawnData(player.getClient());
                        visibleObjects.add(mo);
                    }
                }
            } finally {
                player.unlockWriteVisibleMapObjects();
            }
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public MaplePortal findClosestPortal(Point from) {
        MaplePortal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public String spawnDebug() {
        StringBuilder sb = new StringBuilder("Mobs in map : ");
        sb.append(this.getMobsSize());
        sb.append(" spawnedMonstersOnMap: ");
        sb.append(spawnedMonstersOnMap);
        sb.append(" spawnpoints: ");
        sb.append(monsterSpawn.size());
        sb.append(" maxRegularSpawn: ");
        sb.append(maxRegularSpawn);
        sb.append(" actual monsters: ");
        sb.append(getNumMonsters());
        sb.append(" monster rate: ");
        sb.append(monsterRate);
        sb.append(" fixed: ");
        sb.append(fixedMob);
        for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            if (obj instanceof MapleMonster) {
                MapleMonster mob;
                if ((mob = (MapleMonster) obj) != null) {
                    sb.append("HP = " + mob.getHp());
                }
            }
        }
        return sb.toString();
    }

    public int characterSize() {
        return characters.size();
    }

    public final int getMapObjectSize() {
        return mapobjects.size() + getCharactersSize() - characters.size();
    }

    public final int getCharactersSize() {
        int ret = 0;
        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (!chr.isClone()) {
                    ret++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    public void spawnMonsterOnGroudBelow(MapleMonster mob, Point pos) {
        spawnMonsterOnGroundBelow(mob, pos);
    }

    public List<MapleMapObject> getAllShopsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
            ret.add(mmo);
        }
        for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.SHOP).values()) {
            ret.add(mmo);
        }
        return ret;
    }

    private class ActivateItemReactor implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId(), mapitem.getType()) && !mapitem.isPickedUp()) {
                mapitem.expire(MapleMap.this);
                reactor.hitReactor(c);
                reactor.setTimerActive(false);

                if (reactor.getDelay() > 0) {
                    MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            reactor.forceHitReactor((byte) 0);
                        }
                    }, reactor.getDelay());
                }
            } else {
                reactor.setTimerActive(false);
            }
        }
    }

    public void respawn(final boolean force) {
        respawn(force, System.currentTimeMillis());
    }

    public void respawn(final boolean force, final long now) {
        lastSpawnTime = now;
        if (force) {
            final int numShouldSpawn = monsterSpawn.size() - spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                int spawned = 0;
                for (Spawns spawnPoint : monsterSpawn) {
                    if (!blockedMobGen.isEmpty() && blockedMobGen.contains(Integer.valueOf(spawnPoint.getMonster().getId()))) {
                        continue;
                    }
                    spawnPoint.spawnMonster(this);
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        } else {
            int numShouldSpawn = (GameConstants.isForceRespawn(mapid) ? monsterSpawn.size() : maxRegularSpawn) - spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                int spawned = 0;

                final List<Spawns> randomSpawn = new ArrayList<>(monsterSpawn);
                Collections.shuffle(randomSpawn);
                for (Spawns spawnPoint : monsterSpawn) {
                    if (!isSpawns && spawnPoint.getMobTime() > 0) {
                        continue;
                    }
                    if (!blockedMobGen.isEmpty() && blockedMobGen.contains(Integer.valueOf(spawnPoint.getMonster().getId()))) {
                        continue;
                    }
                    if (spawnPoint.shouldSpawn(lastSpawnTime) || GameConstants.isForceRespawn(mapid)) {
                        spawnPoint.spawnMonster(this);
                        spawned++;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    private static interface DelayedPacketCreation {

        void sendPackets(MapleClient c);
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr.getTruePosition().y > -80) {
                    teamss[0]++;
                } else {
                    teamss[1]++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        } else {
            return "st00";
        }
    }

    public boolean isDisconnected(int id) {
        return dced.contains(Integer.valueOf(id));
    }

    public void addDisconnected(int id) {
        dced.add(Integer.valueOf(id));
    }

    public void resetDisconnected() {
        dced.clear();
    }

    public final void disconnectAll() {
        for (MapleCharacter chr : getCharactersThreadsafe()) {
            if (!chr.isGM()) {
                chr.getClient().disconnect(true, false);
                chr.getClient().getSession().close();
            }
        }
    }

    public List<MapleNPC> getAllNPCs() {
        return getAllNPCsThreadsafe();
    }

    public List<MapleNPC> getAllNPCsThreadsafe() {
        ArrayList<MapleNPC> ret = new ArrayList<MapleNPC>();
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.NPC).values()) {
                ret.add((MapleNPC) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
        return ret;
    }

    public final void resetNPCs() {
        removeNpc(-1);
    }

    public final void resetPQ(int level) {
        resetFully();
        for (MapleMonster mons : getAllMonstersThreadsafe()) {
            mons.changeLevel(level, true);
        }
        resetSpawnLevel(level);
    }

    public final void resetSpawnLevel(int level) {
        for (Spawns spawn : monsterSpawn) {
            if (spawn instanceof SpawnPoint) {
                ((SpawnPoint) spawn).setLevel(level);
            }
        }
    }

    public final void resetFully() {
        resetFully(true);
    }

    public final void resetFully(final boolean respawn) {
        killAllMonsters(true);
        //reloadReactors();
        resetReactors();
        removeDrops();
        resetNPCs();
        resetSpawns();
        resetDisconnected();
        //endSpeedRun();
        //cancelSquadSchedule(true);
        resetPortals();
        environment.clear();
        if (respawn) {
            respawn(true);
        }
    }

    public final void cancelSquadSchedule(boolean interrupt) {
        squadTimer = false;
        checkStates = true;
        if (squadSchedule != null) {
            squadSchedule.cancel(interrupt);
            squadSchedule = null;
        }
    }

    public final void removeDrops_Etc() {
        List<MapleMapItem> items = this.getAllItemsThreadsafe();
        for (MapleMapItem i : items) {
            if (GameConstants.getInventoryType(i.getItemId()) == MapleInventoryType.UNDEFINED) {
                continue;
            }
            if (GameConstants.getInventoryType(i.getItemId()) == MapleInventoryType.EQUIP) {
                continue;
            }
            if (GameConstants.getInventoryType(i.getItemId()) == MapleInventoryType.USE) {
                continue;
            }
            i.expire(this);
        }
    }

    public final void removeDrops() {
        List<MapleMapItem> items = this.getAllItemsThreadsafe();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public final void resetAllSpawnPoint(int mobid, int mobTime) {
        Collection<Spawns> sss = new LinkedList<Spawns>(monsterSpawn);
        resetFully();
        monsterSpawn.clear();
        for (Spawns s : sss) {
            MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
            newMons.setF(s.getF());
            newMons.setFh(s.getFh());
            newMons.setPosition(s.getPosition());
            addMonsterSpawn(newMons, mobTime, (byte) -1, null);
        }
        loadMonsterRate(true);
    }

    public final void resetSpawns() {
        boolean changed = false;
        Iterator<Spawns> sss = monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (sss.next().getCarnivalId() > -1) {
                sss.remove();
                changed = true;
            }
        }
        setSpawns(true);
        if (changed) {
            loadMonsterRate(true);
        }
    }

    public final boolean makeCarnivalSpawn(final int team, final MapleMonster newMons, final int num) {
        MonsterPoint ret = null;
        for (MonsterPoint mp : nodes.getMonsterPoints()) {
            if (mp.team == team || mp.team == -1) {
                final Point newpos = calcPointBelow(new Point(mp.x, mp.y));
                newpos.y -= 1;
                boolean found = false;
                for (Spawns s : monsterSpawn) {
                    if (s.getCarnivalId() > -1 && (mp.team == -1 || s.getCarnivalTeam() == mp.team) && s.getPosition().x == newpos.x && s.getPosition().y == newpos.y) {
                        found = true;
                        break; //this point has already been used.
                    }
                }
                if (!found) {
                    ret = mp; //this point is safe for use.
                    break;
                }
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0); //always.
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50); //does this matter
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            final SpawnPoint sp = addMonsterSpawn(newMons, 1, (byte) team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }

    public final boolean makeCarnivalReactor(final int team, final int num) {
        final MapleReactor old = getReactorByName(team + "" + num);
        if (old != null && old.getState() < 5) { //already exists
            return false;
        }
        Point guardz = null;
        final List<MapleReactor> react = getAllReactorsThreadsafe();
        for (Pair<Point, Integer> guard : nodes.getGuardians()) {
            if (guard.right == team || guard.right == -1) {
                boolean found = false;
                for (MapleReactor r : react) {
                    if (r.getTruePosition().x == guard.left.x && r.getTruePosition().y == guard.left.y && r.getState() < 5) {
                        found = true;
                        break; //already used
                    }
                }
                if (!found) {
                    guardz = guard.left; //this point is safe for use.
                    break;
                }
            }
        }
        if (guardz != null) {
            final MapleReactor my = new MapleReactor(MapleReactorFactory.getReactor(9980000 + team), 9980000 + team);
            my.setState((byte) 1);
            my.setName(team + "" + num); //lol
            //with num. -> guardians in factory
            spawnReactorOnGroundBelow(my, guardz);
            final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            for (MapleMonster mons : getAllMonstersThreadsafe()) {
                if (mons.getCarnivalTeam() == team) {
                    skil.getSkill().applyEffect(null, mons, false, (short) 0);
                }
            }
        }
        return guardz != null;
    }

    public final void blockAllPortal() {
        for (MaplePortal p : portals.values()) {
            p.setPortalState(false);
        }
    }

    public boolean getAndSwitchTeam() {
        return getCharactersSize() % 2 != 0;
    }

    public void setSquad(MapleSquadType s) {
        this.squad = s;

    }

    public int getChannel() {
        return channel;
    }

    public int getConsumeItemCoolTime() {
        return consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int ciit) {
        this.consumeItemCoolTime = ciit;
    }

    public void setPermanentWeather(int pw) {
        this.permanentWeather = pw;
    }

    public int getPermanentWeather() {
        return permanentWeather;
    }

    public void setNodes(final MapleNodes mn) {
        this.nodes = mn;
    }

    public final List<MaplePlatform> getPlatforms() {
        return nodes.getPlatforms();
    }

    public Collection<MapleNodeInfo> getNodes() {
        return nodes.getNodes();
    }

    public MapleNodeInfo getNode(final int index) {
        return nodes.getNode(index);
    }

    public boolean isLastNode(final int index) {
        return nodes.isLastNode(index);
    }

    public final List<Rectangle> getAreas() {
        return nodes.getAreas();
    }

    public final Rectangle getArea(final int index) {
        return nodes.getArea(index);
    }

    public final void changeEnvironment(final String ms, final int type) {
        broadcastMessage(CField.environmentChange(ms, type));
    }

    public final void toggleEnvironment(final String ms) {
        if (environment.containsKey(ms)) {
            moveEnvironment(ms, environment.get(ms) == 1 ? 2 : 1);
        } else {
            moveEnvironment(ms, 1);
        }
    }

    public final void moveEnvironment(final String ms, final int type) {
        //broadcastMessage(CField.environmentMove(ms, type));
        environment.put(ms, type);
    }

    public final Map<String, Integer> getEnvironment() {
        return environment;
    }

    public final int getNumPlayersInArea(final int index) {
        return getNumPlayersInRect(getArea(index));
    }

    public final int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;

        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter a;
            while (ltr.hasNext()) {
                if (rect.contains(ltr.next().getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public final int getNumPlayersItemsInArea(final int index) {
        return getNumPlayersItemsInRect(getArea(index));
    }

    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int ret = getNumPlayersInRect(rect);

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (rect.contains(mmo.getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public void broadcastGMMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet);
    }

    private void broadcastGMMessage(MapleCharacter source, byte[] packet) {
        charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (MapleCharacter chr : characters) {
                    if (chr.isStaff()) {
                        chr.getClient().getSession().write(packet);
                    }
                }
            } else {
                for (MapleCharacter chr : characters) {
                    if (chr != source && (chr.getGMLevel() >= source.getGMLevel())) {
                        chr.getClient().getSession().write(packet);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    private List<Integer> blockedMobGen = new LinkedList<Integer>();
    double maxspawns = 1;

    public void setMobGen(int mobid, boolean spawn) {
        Integer value = Integer.valueOf(mobid);
        if (spawn) {
            blockedMobGen.remove(value);
        } else {
            if (blockedMobGen.contains(value)) {
                return;
            }
            blockedMobGen.add(value);
        }
    }

    public final List<Integer> getBlockedMobs() {
        return Collections.unmodifiableList(blockedMobGen);
    }

    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return nodes.getMobsToSpawn();
    }

    public final List<Integer> getSkillIds() {
        return nodes.getSkillIds();
    }

    public final boolean canSpawn(long now) { // 젠률
        return lastSpawnTime > 0 && lastSpawnTime + (createMobInterval / 2) < now;
    }

    public final boolean canHurt(long now) {
        if (lastHurtTime > 0 && lastHurtTime + decHPInterval < now) {
            lastHurtTime = now;
            return true;
        }
        return false;
    }

    public final void resetShammos(final MapleClient c) {
        killAllMonsters(true);
        //broadcastMessage(CWvsContext.serverNotice(6, "호위 대상 몬스터와의 거리가 너무 멀어 미션에 실패하였습니다."));
        EtcTimer.getInstance().schedule(new Runnable() {
            public void run() {
                if (c.getPlayer() != null) {
                    for (MapleCharacter chrz : getCharactersThreadsafe()) {
                        chrz.changeMap(getForcedReturnMap(), getForcedReturnMap().getPortal(0));
                    }
                }
            }
        }, 1000);
    }

    public int getInstanceId() {
        return instanceid;
    }

    public void setInstanceId(int ii) {
        this.instanceid = ii;
    }

    public int getPartyBonusRate() {
        return partyBonusRate;
    }

    public void setPartyBonusRate(int ii) {
        this.partyBonusRate = ii;
    }

    public short getTop() {
        return top;
    }

    public short getBottom() {
        return bottom;
    }

    public short getLeft() {
        return left;
    }

    public short getRight() {
        return right;
    }

    public void setTop(int ii) {
        this.top = (short) ii;
    }

    public void setBottom(int ii) {
        this.bottom = (short) ii;
    }

    public void setLeft(int ii) {
        this.left = (short) ii;
    }

    public void setRight(int ii) {
        this.right = (short) ii;
    }

    public final void setChangeableMobOrigin(MapleCharacter d) {
        this.changeMobOrigin = new WeakReference<MapleCharacter>(d);
    }

    public final MapleCharacter getChangeableMobOrigin() {
        if (changeMobOrigin == null) {
            return null;
        }
        return changeMobOrigin.get();
    }

    public List<Pair<Point, Integer>> getGuardians() {
        return nodes.getGuardians();
    }

    public DirectionInfo getDirectionInfo(int i) {
        return nodes.getDirection(i);
    }

    public List<MapleCharacter> getAllCharactersThreadsafe() {
        ArrayList<MapleCharacter> ret = new ArrayList<MapleCharacter>();
        mapobjectlocks.get(MapleMapObjectType.PLAYER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.PLAYER).values()) {
                ret.add((MapleCharacter) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.PLAYER).readLock().unlock();
        }
        return ret;
    }

    private long forcedRespawnTime = 0;

    public long getForcedRespawnTime() {
        return forcedRespawnTime;
    }

    public void setForcedRespawnTime(long t) {
        this.forcedRespawnTime = t;
    }

    public void forcedRespawn() {

        int xValue = 0;
        double addSpawn = 1.0;
        for (MapleSummon summon : getAllSummonsThreadsafe()) {
            if (summon != null) {
                final MapleStatEffect cEff = SkillFactory.getSkill(summon.getSkill()).getEffect(summon.getSkillLevel());
                if (cEff != null) {
                    if (summon.getSkill() == 62111003) {
                        xValue = cEff.getX();
                        addSpawn = (xValue / 100.0) + 1.0;
                    }
                }
            }
        }

        final int sSpawn = ((int) (maxRegularSpawn * addSpawn) - spawnedMonstersOnMap.get());
        if (sSpawn > 0) {
            int v1 = 0;
            for (Spawns sPoint : monsterSpawn) {
                if (!isSpawns) {
                    continue;
                }
                if (sPoint.getMobTime() > 0 || sPoint.getMobTime() == -1) {
                    continue;
                }
                if (!blockedMobGen.isEmpty() && blockedMobGen.contains(sPoint.getMonster().getId())) {
                    continue;
                }
                if (System.currentTimeMillis() > this.getForcedRespawnTime()) {
                    sPoint.spawnMonster(this);
                    v1++;
                }
                if (v1 >= sSpawn) {
                    this.setForcedRespawnTime(0);
                    break;
                }
            }
            if (this.getForcedRespawnTime() == 0) {
                this.setForcedRespawnTime(System.currentTimeMillis() + 5000);
            }
        }
    }
}
