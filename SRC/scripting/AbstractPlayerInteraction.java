package scripting;

import client.InnerAbillity;
import client.InnerSkillValueHolder;
import java.awt.Point;
import java.util.List;

import client.inventory.Equip;
import client.SkillFactory;
import constants.GameConstants;
import client.Skill;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.MapleQuestStatus;
import client.MapleTrait.MapleTraitType;
import client.inventory.Item;
import client.inventory.MapleInventory;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.guild.MapleGuild;
import server.Randomizer;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.maps.MapleMapObject;
import server.maps.SavedLocationType;
import server.maps.Event_DojoAgent;
import server.life.MapleMonster;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;
import tools.packet.CField;
import tools.packet.CPet;
import client.inventory.MapleInventoryIdentifier;
import constants.MapConstants;
import database.DatabaseConnection;
import handling.channel.handler.DueyHandler;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import server.CashItemFactory;
import server.MapleSquad;
import server.Timer.CloneTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import tools.FileoutputUtil;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CMonsterCarnival;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.FieldEffect;
import tools.packet.UserEffect;

public abstract class AbstractPlayerInteraction {

    protected MapleClient c;
    protected int id, id2;

    public AbstractPlayerInteraction(final MapleClient c, final int id, final int id2) {
        this.c = c;
        this.id = id;
        this.id2 = id2;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<>();
        for (ChannelServer channel : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : channel.getPartyMembers(getPlayer().getParty())) {
                if (chr != null) {
                    chars.add(chr);
                }
            }
        }
        return chars;
    }

    public final void PartyDayAdd() {
        for (MapleCharacter chr : getPartyMembers()) {
            chr.setDateKey("boss", (Integer.parseInt(chr.getDateKey("boss")) + 1) + "");
        }
    }

    public final boolean PartyDayCheck() {
        for (MapleCharacter chr : getPartyMembers()) {
            if (chr.getDateKey("boss") == null) {
                chr.setDateKey("boss", "0");
            }
            if (Integer.parseInt(chr.getDateKey("boss")) > 14) {
                return false;
            }
        }
        return true;
    }

    public final MapleClient getClient() {
        return c;
    }

    public final MapleClient getC() {
        return c;
    }

    public MapleCharacter getChar() {
        return c.getPlayer();
    }

    public final ChannelServer getChannelServer() {
        return c.getChannelServer();
    }

    public final MapleCharacter getPlayer() {
        return c.getPlayer();
    }

    public final EventManager getEventManager(final String event) {
        return c.getChannelServer().getEventSM().getEventManager(event);
    }

    public final EventInstanceManager getEventInstance() {
        return c.getPlayer().getEventInstance();
    }

    public final void warp(final int map) {
        final MapleMap mapz = getWarpMap(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp_Instanced(final int map) {
        final MapleMap mapz = getMap_Instanced(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp_Instanced(final int map, int pid) {
        final MapleMap mapz = getMap_Instanced(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(pid));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        if (portal != 0 && map == c.getPlayer().getMapId() || map == -1) { //test
            final Point portalPos = new Point(c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getTruePosition()) < 90000.0 || map == -1) {
                c.sendPacket(CUserLocal.teleport((byte) portal));
                if (map != -1) {
                    c.getPlayer().checkFollow();
                }
                c.getPlayer().getMap().movePlayer(c.getPlayer(), portalPos);
            } else {
                c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warp(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == c.getPlayer().getMapId() || map == -1) {
            final Point portalPos = new Point(c.getPlayer().getMap().getPortal(portal).getPosition());
            if (/*portalPos.distanceSq(getPlayer().getTruePosition()) < 90000.0 || */map == -1) {
                if (map != -1) {
                    c.getPlayer().checkFollow();
                }
                c.sendPacket(CUserLocal.teleport((byte) c.getPlayer().getMap().getPortal(portal).getId()));
                c.getPlayer().getMap().movePlayer(c.getPlayer(), new Point(c.getPlayer().getMap().getPortal(portal).getPosition()));
            } else {
                c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warpMap(final int mapid, final int portal) {
        final MapleMap map = getMap(mapid);
        for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
            chr.changeMap(map, map.getPortal(portal));
        }
    }

    public final void playPortalSE() {
        c.getSession().write(EffectPacket.showOwnBuffEffect(0, 10, 1, 1));
    }

    private final MapleMap getWarpMap(final int map) {
        return ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(map);
    }

    public final MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    public final MapleMap getMap(final int map) {
        return getWarpMap(map);
    }

    public final MapleMap getMap_Instanced(final int map) {
        return c.getPlayer().getEventInstance() == null ? getMap(map) : c.getPlayer().getEventInstance().getMapInstance(map);
    }

    public void spawnMonster(final int id, final int qty) {
        spawnMob(id, qty, c.getPlayer().getTruePosition());
    }

    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
        }
    }

    public final void spawnMob(final int id, final int qty, final int x, final int y) {
        spawnMob(id, qty, new Point(x, y));
    }

    public final void spawnMob(final int id, final int x, final int y) {
        spawnMob(id, 1, new Point(x, y));
    }

    private final void spawnMob(final int id, final int qty, final Point pos) {
        for (int i = 0; i < qty; i++) {
            c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public final void killMob(int ids) {
        c.getPlayer().getMap().killMonster(ids);
    }

    public final void killAllMob() {
        c.getPlayer().getMap().killAllMonsters(true);
    }

    public final void addHP(final int delta) {
        c.getPlayer().addHP(delta);
    }

    public final int getPlayerStat(final String type) {
        if (type.equals("LVL")) {
            return c.getPlayer().getLevel();
        } else if (type.equals("STR")) {
            return c.getPlayer().getStat().getStr();
        } else if (type.equals("DEX")) {
            return c.getPlayer().getStat().getDex();
        } else if (type.equals("INT")) {
            return c.getPlayer().getStat().getInt();
        } else if (type.equals("LUK")) {
            return c.getPlayer().getStat().getLuk();
        } else if (type.equals("HP")) {
            return c.getPlayer().getStat().getHp();
        } else if (type.equals("MP")) {
            return c.getPlayer().getStat().getMp();
        } else if (type.equals("MAXHP")) {
            return c.getPlayer().getStat().getMaxHp();
        } else if (type.equals("MAXMP")) {
            return c.getPlayer().getStat().getMaxMp();
        } else if (type.equals("RAP")) {
            return c.getPlayer().getRemainingAp();
        } else if (type.equals("RSP")) {
            return c.getPlayer().getRemainingSp();
        } else if (type.equals("GID")) {
            return c.getPlayer().getGuildId();
        } else if (type.equals("GRANK")) {
            return c.getPlayer().getGuildRank();
        } else if (type.equals("ARANK")) {
            return c.getPlayer().getAllianceRank();
        } else if (type.equals("GM")) {
            return c.getPlayer().isGM() ? 1 : 0;
        } else if (type.equals("ADMIN")) {
            return c.getPlayer().isAdmin() ? 1 : 0;
        } else if (type.equals("GENDER")) {
            return c.getPlayer().getGender();
        } else if (type.equals("FACE")) {
            return c.getPlayer().getFace();
        } else if (type.equals("HAIR")) {
            return c.getPlayer().getHair();
        }
        return -1;
    }

    public final String getName() {
        return c.getPlayer().getName();
    }

    public final boolean haveItem(final int itemid) {
        return haveItem(itemid, 1);
    }

    public final boolean haveItem(final int itemid, final int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public final boolean haveItem(final int itemid, final int quantity, final boolean checkEquipped, final boolean greaterOrEquals) {
        return c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public final boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (c.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    public final boolean canHoldSlots(final int slot) {
        for (int i = 1; i <= 5; i++) {
            if (c.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).isFull(slot)) {
                return false;
            }
        }
        return true;
    }

    public final boolean canHold(final int itemid) {
        return c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public final boolean canHold(final int itemid, final int quantity) {
        return MapleInventoryManipulator.checkSpace(c, itemid, quantity, "");
    }

    public final MapleQuestStatus getQuestRecord(final int id) {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }

    public final MapleQuestStatus getQuestNoRecord(final int id) {
        return c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(id));
    }

    public final byte getQuestStatus(final int id) {
        return c.getPlayer().getQuestStatus(id);
    }

    public final boolean isQuestActive(final int id) {
        return getQuestStatus(id) == 1;
    }

    public final boolean isQuestFinished(final int id) {
        return getQuestStatus(id) == 2;
    }

    public final void showQuestMsg(final String msg) {
        c.getSession().write(CWvsContext.showQuestMsg(msg));
    }

    public final void forceForfeitQuest(final int id) {
        MapleQuest.getInstance(id).forfeit(c.getPlayer());
    }

    public final void forceStartQuest(final int id, final String data) {
        MapleQuest.getInstance(id).forceStart(c.getPlayer(), 0, data);
    }

    public final void forceStartQuest(final int id, final int data, final boolean filler) {
        MapleQuest.getInstance(id).forceStart(c.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    public void forceStartQuest(final int id) {
        MapleQuest.getInstance(id).forceStart(c.getPlayer(), 0, null);
    }

    public void forceCustomDataQuest(final int id, final String cData) {
        MapleQuest.getInstance(id).forceCustomData(c.getPlayer(), cData);
    }

    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(getPlayer(), 0);
    }

    public void completeQuest(int idd) { //필요할거같은데
        MapleQuest.getInstance(idd).complete(getPlayer(), id);
    }

    public void spawnNpc(final int npcId) {
        c.getPlayer().getMap().spawnNpc(npcId, c.getPlayer().getPosition());
    }

    public final void spawnNpc(final int npcId, final int x, final int y) {
        c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public final void spawnNpc(final int npcId, final Point pos) {
        c.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public final void removeNpc(final int mapid, final int npcId) {
        c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    public final void removeNpc(final int npcId) {
        c.getPlayer().getMap().removeNpc(npcId);
    }

    public final void forceStartReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(c);
                break;
            }
        }
    }

    public final void destroyReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    public final void hitReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    public final int getJob() {
        return c.getPlayer().getJob();
    }

    public final void gainNX(final int amount) {
        c.getPlayer().modifyCSPoints(1, amount, true); //theremk you can change it to prepaid yae since cspoiint is xncredit so make it prepaid
    }

    public final void gainItemPeriod(final int id, final short quantity, final int period) { //period is in days
        gainItem(id, quantity, false, period, -1, "");
    }

    public final void gainItemPeriod(final int id, final short quantity, final long period, final String owner) { //period is in days
        gainItem(id, quantity, false, period, -1, owner);
    }

    public final void gainItem(final int id, final short quantity) {
        gainItem(id, quantity, false, 0, -1, "");
    }

    public final void gainItemSilent(final int id, final short quantity) {
        gainItem(id, quantity, false, 0, -1, "", c, false);
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats) {
        gainItem(id, quantity, randomStats, 0, -1, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final int slots) {
        gainItem(id, quantity, randomStats, 0, slots, "");
    }

    public final void gainItem(final int id, final short quantity, final long period) {
        gainItem(id, quantity, false, period, -1, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots) {
        gainItem(id, quantity, randomStats, period, slots, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner) {
        gainItem(id, quantity, randomStats, period, slots, owner, c);
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, final MapleClient cg) {
        gainItem(id, quantity, randomStats, period, slots, owner, cg, true);
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, final MapleClient cg, final boolean show) {
        if (quantity >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                final Equip item = (Equip) (randomStats ? ii.randomizeStats((Equip) ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0) {
                    item.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte) (item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                //item.setGMLog("Received from interaction " + this.id + " (" + id2 + ") on " + FileoutputUtil.CurrentReadable_Time());
                final String name = ii.getName(id);
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, quantity, owner == null ? "" : owner, null, period, "Received from interaction " + this.id + " (" + id2 + ") on " + FileoutputUtil.CurrentReadable_Date());
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        if (show) {
            cg.getSession().write(InfoPacket.getShowItemGain(id, quantity, true));
        }
    }

    public final boolean removeItem(final int id) { //quantity 1
        if (MapleInventoryManipulator.removeById_Lock(c, GameConstants.getInventoryType(id), id)) {
            c.getSession().write(InfoPacket.getShowItemGain(id, (short) -1, true));
            return true;
        }
        return false;
    }

    public final void changeMusic(final String songName) {
        getPlayer().getMap().broadcastMessage(CField.musicChange(songName));
    }

    public final void worldMessage(final int type, final String message) {
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
    }

    // default playerMessage and mapMessage to use type 5
    public final void playerMessage(final String message) {
        playerMessage(5, message);
    }

    public final void mapMessage(final String message) {
        mapMessage(5, message);
    }

    public final void guildMessage(final String message) {
        guildMessage(5, message);
    }

    public final void showInfoOnScreen(final String message) {
        c.getPlayer().dropMessage(-1, message);
    }

    public final void playerMessage(final int type, final String message) {
        c.getPlayer().dropMessage(type, message);
    }

    public final void mapMessage(final int type, final String message) {
        c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(type, message));
    }

    public final void guildMessage(final int type, final String message) {
        if (getPlayer().getGuildId() > 0) {
            World.Guild.guildPacket(getPlayer().getGuildId(), CWvsContext.serverNotice(type, message));
        }
    }

    public final MapleGuild getGuild() {
        return getGuild(getPlayer().getGuildId());
    }

    public final MapleGuild getGuild(int guildid) {
        return World.Guild.getGuild(guildid);
    }

    public final MapleParty getParty() {
        return c.getPlayer().getParty();
    }

    public final int getCurrentPartyId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    public final boolean isLeader() {
        if (getPlayer().getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == c.getPlayer().getId();
    }

    public final boolean isAllPartyMembersAllowedJob(final int job) {
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : c.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }

    public final boolean allMembersHere() {
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : c.getPlayer().getParty().getMembers()) {
            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }

    public final void warpParty(final int map, final int portal, int time) {
        CloneTimer tMan = CloneTimer.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                warpParty(map, portal);
            }
        };
        tMan.schedule(r, time * 1000);
    }

    public void warpParty(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp(mapId, 0);
            return;
        }
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter pUser : getPlayer().getParty().getMembers()) {
            MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(cMap).getCharacterById(pUser.getId());
            if (getPlayer().getEventInstance() != null) {
                cUser = this.getChannelServer().getPlayerStorage().getCharacterById(pUser.getId());
            }
            if (cUser != null) {
                cUser.changeMap(target, target.getPortal(0));
            }
        }
    }

    public final void warpParty(final int mapId, final int portal) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                warp(mapId);
            } else {
                warp(mapId, portal);
            }
            return;
        }
        final boolean rand = portal < 0;
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                if (rand) {
                    try {
                        curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    } catch (Exception e) {
                        curChar.changeMap(target, target.getPortal(0));
                    }
                } else {
                    curChar.changeMap(target, target.getPortal(portal));
                }
            }
        }
    }

    public final void warpParty_Instanced(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp_Instanced(mapId);
            return;
        }
        final MapleMap target = getMap_Instanced(mapId);

        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public final void warpParty_Instanced(final int mapId, int pid) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp_Instanced(mapId, pid);
            return;
        }
        final MapleMap target = getMap_Instanced(mapId);

        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(pid));
            }
        }
    }

    public void gainMeso(int gain) {
        c.getPlayer().gainMeso(gain, true, true);
    }

    public void gainExp(int gain) {
        c.getPlayer().gainExp(gain, true, true, true);
    }

    public void gainExpR(int gain) {
        c.getPlayer().gainExp(gain * c.getChannelServer().getExpRate(), true, true, true);
    }

    public final void givePartyItems(final int id, final short quantity, final List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity, "Received from party interaction " + id + " (" + id2 + ")");
            } else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().getSession().write(InfoPacket.getShowItemGain(id, quantity, true));
        }
    }

    public void addPartyTrait(String t, int e, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.getTrait(MapleTraitType.valueOf(t)).addExp(e, chr);
        }
    }

    public void addPartyTrait(String t, int e) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            addTrait(t, e);
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.getTrait(MapleTraitType.valueOf(t)).addExp(e, curChar);
            }
        }
    }

    public void addTrait(String t, int e) {
        getPlayer().getTrait(MapleTraitType.valueOf(t)).addExp(e, getPlayer());
    }

    public final void givePartyItems(final int id, final short quantity) {
        givePartyItems(id, quantity, false);
    }

    public final void givePartyItems(final int id, final short quantity, final boolean removeAll) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainItem(id, (short) (removeAll ? -getPlayer().itemQuantity(id) : quantity));
            return;
        }

        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                gainItem(id, (short) (removeAll ? -curChar.itemQuantity(id) : quantity), false, 0, 0, "", curChar.getClient());
            }
        }
    }

    public final void givePartyExp_PQ(final int maxLevel, final double mod, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(chr.getLevel() > maxLevel ? (maxLevel + ((maxLevel - chr.getLevel()) / 10)) : chr.getLevel()) / (Math.min(chr.getLevel(), maxLevel) / 5.0) / (mod * 2.0));
            chr.gainExp(amount * c.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public final void gainExp_PQ(final int maxLevel, final double mod) {
        final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? (maxLevel + (getPlayer().getLevel() / 10)) : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0) / mod);
        gainExp(amount * c.getChannelServer().getExpRate());
    }

    public final void givePartyExp_PQ(final int maxLevel, final double mod) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? (maxLevel + (getPlayer().getLevel() / 10)) : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0) / mod);
            gainExp(amount * c.getChannelServer().getExpRate());
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(curChar.getLevel() > maxLevel ? (maxLevel + (curChar.getLevel() / 10)) : curChar.getLevel()) / (Math.min(curChar.getLevel(), maxLevel) / 10.0) / mod);
                curChar.gainExp(amount * c.getChannelServer().getExpRate(), true, true, true);
            }
        }
    }

    public final void givePartyExp(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.gainExp(amount * c.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public final void givePartyExp(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainExp(amount * c.getChannelServer().getExpRate());
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.gainExp(amount * c.getChannelServer().getExpRate(), true, true, true);
            }
        }
    }

    public final void endPartyQuest(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    public final void endPartyQuest(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.endPartyQuest(amount);
            }
        }
    }

    public final void removeFromParty(final int id, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            final int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(id), id, possesed, true, false);
                chr.getClient().getSession().write(InfoPacket.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    public final void removeFromParty(final int id) {
        givePartyItems(id, (short) 0, true);
    }

    public final void useSkill(final int skill, final int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(c.getPlayer());
    }

    public final void useItem(final int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
        c.getSession().write(InfoPacket.getStatusMsg(id));
    }

    public final void cancelItem(final int id) {
        c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), -1);
    }

    public final int getMorphState() {
        return c.getPlayer().getMorphState();
    }

    public final void removeAll(final int id) {
        c.getPlayer().removeAll(id);
    }

    public final void removeAllParty(final int id) {
        if (c.getPlayer().getParty() != null) {
            for (MaplePartyCharacter pchr : c.getPlayer().getParty().getMembers()) {
                MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pchr.getId());
                if (chr != null) {
                    chr.removeAll(id, true);
                }
            }
        } else {
            c.getPlayer().removeAll(id, true);
        }
    }

    public final void gainCloseness(final int closeness, final int index) {
        final MaplePet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + (closeness * getChannelServer().getTraitRate()));
            getClient().getSession().write(CPet.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
        }
    }

    public final void gainClosenessAll(final int closeness) {
        for (final MaplePet pet : getPlayer().getPets()) {
            if (pet != null && pet.getSummoned()) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().getSession().write(CPet.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            }
        }
    }

    public final void resetMap(final int mapid) {
        getMap(mapid).resetFully();
    }

    public final void openNpc(final int id) {
        getClient().removeClickedNPC();
        NPCScriptManager.getInstance().start(getClient(), id);
    }

    public final void openNpc(final int id, final String name) {
        getClient().removeClickedNPC();
        NPCScriptManager.getInstance().start(getClient(), id, name);
    }

    public final void openNpc(final MapleClient cg, final int id) {
        cg.removeClickedNPC();
        NPCScriptManager.getInstance().start(cg, id);
    }

    public final int getMapId() {
        return c.getPlayer().getMap().getId();
    }

    public final boolean haveMonster(final int mobid) {
        for (MapleMapObject obj : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
            final MapleMonster mob = (MapleMonster) obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }

    public final int getChannelNumber() {
        return c.getChannel();
    }

    public final int getMonsterCount(final int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
    }

    public final void teachSkill(final int id, final int level, final byte masterlevel) {
        getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    public final void teachSkill(final int id, int level) {
        final Skill skil = SkillFactory.getSkill(id);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSingleSkillLevel(skil, level, (byte) skil.getMaxLevel());
    }

    public final void teachMakerSkill(int level) {
        int skilid = 1007;
        if (GameConstants.isKOC(getPlayer().getJob())) {
            skilid = 10001007; // 시그너스
        } else if (GameConstants.isAran(getPlayer().getJob())) {
            skilid = 20001007; // 아란?
        } else if (GameConstants.isEvan(getPlayer().getJob())) {
            skilid = 20011007; // 에반
        } else if (GameConstants.isMercedes(getPlayer().getJob())) {
            skilid = 20021007; // 메르세데스
        } else if (GameConstants.isPhantom(getPlayer().getJob())) {
            skilid = 20031007; // 팬텀
            // } else if (GameConstants.isLuminous(getPlayer().getJob())) {
            // skilid = 20041007; // 루미너스
        } else if (GameConstants.isResist(getPlayer().getJob())) {
            if (GameConstants.isXenon(getPlayer().getJob())) {
                skilid = 30021007; // 제논
            } else if (GameConstants.isDemon(getPlayer().getJob()) || GameConstants.isDemonAvenger(getPlayer().getJob())) {
                skilid = 30011007; // 데몬
            } else {
                skilid = 30001007; // 레지스탕스
            }
        } else if (GameConstants.isMihile(getPlayer().getJob())) {
            skilid = 50001007; // 미하일
        } else if (GameConstants.isHayato(getPlayer().getJob())) {
            skilid = 60001007; // 카이저 엔젤릭버스터
        }
        final Skill skil = SkillFactory.getSkill(skilid);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSingleSkillLevel(skil, level, (byte) skil.getMaxLevel());
    }

    public final void teachMakerSoulSkill(int level) {
        int skilid = 1003;
        if (GameConstants.isKOC(getPlayer().getJob())) {
            skilid = 10001003; // 시그너스
        } else if (GameConstants.isAran(getPlayer().getJob())) {
            skilid = 20001003; // 아란?
        } else if (GameConstants.isEvan(getPlayer().getJob())) {
            skilid = 20011003; // 에반
        } else if (GameConstants.isMercedes(getPlayer().getJob())) {
            skilid = 20021003; // 메르세데스
        } else if (GameConstants.isPhantom(getPlayer().getJob())) {
            skilid = 20031003; // 팬텀
            //} else if (GameConstants.isLuminous(getPlayer().getJob())) {
            //skilid = 20041003; // 루미너스
        } else if (GameConstants.isResist(getPlayer().getJob())) {
            if (GameConstants.isXenon(getPlayer().getJob())) {
                skilid = 30021003; // 제논
            } else if (GameConstants.isDemon(getPlayer().getJob()) || GameConstants.isDemonAvenger(getPlayer().getJob())) {
                skilid = 30011003; // 데몬
            } else {
                skilid = 30001003; // 레지스탕스
            }
        } else if (GameConstants.isMihile(getPlayer().getJob())) {
            skilid = 50001003; // 미하일
        } else if (GameConstants.isHayato(getPlayer().getJob())) {
            skilid = 60001003; // 카이저 엔젤릭버스터
        }
        final Skill skil = SkillFactory.getSkill(skilid);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSingleSkillLevel(skil, level, (byte) skil.getMaxLevel());
    }

    public final int getPlayerCount(final int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }

    public final void dojo_getUp() {
        c.getSession().write(InfoPacket.updateInfoQuest(1207, "pt=1;min=4;belt=1;tuto=1")); //todo
        c.getSession().write(EffectPacket.Mulung_DojoUp2());
        c.sendPacket(CUserLocal.teleport((byte) 6));
    }

    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(c.getPlayer(), fromresting, c.getPlayer().getMap());
        }
        return Event_DojoAgent.warpNextMap_Agent(c.getPlayer(), fromresting);
    }

    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting, final int mapid) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(c.getPlayer(), fromresting, getMap(mapid));
        }
        return Event_DojoAgent.warpNextMap_Agent(c.getPlayer(), fromresting);
    }

    public final int dojo_getPts() {
        return c.getPlayer().getIntNoRecord(GameConstants.DOJO);
    }

    public final MapleEvent getEvent(final String loc) {
        return c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
    }

    public String getSquadProperty(String type, String key) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return "null";
        } else {
            return squad.getProperty(key);
        }
    }

    public void setSquadProperty(String type, String key, String value) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
        } else {
            squad.setProperty(key, value);
        }
    }

    public final int getSavedLocation(final String loc) {
        final Integer ret = c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 100000000;
        }
        return ret;
    }

    public final void saveLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public final void saveReturnLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc), c.getPlayer().getMap().getReturnMap().getId());
    }

    public final void clearSavedLocation(final String loc) {
        c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    public final void summonMsg(final String msg) {
        if (!c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        c.sendPacket(CUserLocal.tutorMsg(msg, 200, 10000));
    }

    public final void summonMsg(final int type) {
        if (!c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        c.sendPacket(CUserLocal.tutorMsg(type, 7000));
    }

    public final void showInstruction(final String msg, final int width, final int height) {
        c.sendPacket(CUserLocal.balloonMsg(msg, width, height));
    }

    public final void playerSummonHint(final boolean summon) {
        c.getPlayer().setHasSummon(summon);
        c.sendPacket(CUserLocal.hireTutor(summon));
    }

    public final String getInfoQuest(final int id) {
        return c.getPlayer().getInfoQuest(id);
    }

    public final void updateInfoQuest(final int id, final String data) {
        c.getPlayer().updateInfoQuest(id, data);
    }

    public final boolean getEvanIntroState(final String data) {
        return getInfoQuest(22013).equals(data);
    }

    public final void updateEvanIntroState(final String data) {
        updateInfoQuest(22013, data);
    }

    public final void Aran_Start() {
        c.getSession().write(CField.Aran_Start());
    }

    public final void Aran_ComboAttack(String data) {
        c.getSession().write(CField.Aran_ComboAttack(data));
    }

    public final void evanTutorial(final String data, final int v1) {
        c.getSession().write(NPCPacket.getEvanTutorial(data));
    }

    public final void AranTutInstructionalBubble(final String data) {
        c.getSession().write(EffectPacket.AranTutInstructionalBalloon(data));
    }

    public final void ShowWZEffect(final String data) {
        c.getSession().write(EffectPacket.AranTutInstructionalBalloon(data));
    }

    public final void showWZEffect(final String data) {
        c.getSession().write(EffectPacket.ShowWZEffect(data));
    }

    public final void EarnTitleMsg(final String data) {
        c.getSession().write(CWvsContext.getTopMsg(data));
    }

    public final void EnableUI(final short i) {
        c.sendPacket(CUserLocal.setInGameDirectionMode(i));
    }

    public final void DisableUI(final boolean enabled) {
        c.sendPacket(CUserLocal.setStandAloneMode(enabled));
    }

    public final void MovieClipIntroUI(final boolean enabled) {
        c.sendPacket(CUserLocal.setStandAloneMode(enabled));
        c.sendPacket(CUserLocal.setDirectionMode(enabled));
    }

    public final void playVideoByScript(String uol) {
        c.sendPacket(CUserLocal.videoByScript(uol, true));
    }

    public final void enterAzwan() {
        //c.getSession().write(UIPacket.startAzwan());
    }

    public MapleInventoryType getInvType(int i) {
        return MapleInventoryType.getByType((byte) i);
    }

    public String getItemName(final int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }

    public void gainPet(int id, String name, int level, int closeness, int fullness, long period, short flags) {
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        try {
            MapleInventoryManipulator.addById(c, id, (short) 1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0, flags, 0), 45, "Pet from interaction " + id + " (" + id2 + ")" + " on " + FileoutputUtil.CurrentReadable_Date());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(c, getInvType(invType), slot, quantity, true);
    }

    public void removeEquipFromSlot(byte slot) {
        Item tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        MapleInventoryManipulator.removeAranPole(c, MapleInventoryType.EQUIPPED, slot, tempItem.getQuantity());
    }

    public void gainGP(final int gp) {
        if (getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.gainGP(getPlayer().getGuildId(), gp); //1 for
    }

    public int getGP() {
        if (getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return World.Guild.getGP(getPlayer().getGuildId()); //1 for
    }

    public void showMapEffect(String path) {
        getClient().getSession().write(CField.MapEff(path));
    }

    public int itemQuantity(int itemid) {
        return getPlayer().itemQuantity(itemid);
    }

    public EventInstanceManager getDisconnected(String event) {
        EventManager em = getEventManager(event);
        if (em == null) {
            return null;
        }
        for (EventInstanceManager eim : em.getInstances()) {
            if (eim.isDisconnected(c.getPlayer()) && eim.getPlayerCount() > 0) {
                return eim;
            }
        }
        return null;
    }

    public boolean isAllReactorState(final int reactorId, final int state) {
        boolean ret = false;
        for (MapleReactor r : getMap().getAllReactorsThreadsafe()) {
            if (r.getReactorId() == reactorId) {
                ret = r.getState() == state;
            }
        }
        return ret;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void spawnMonster(int id) {
        spawnMonster(id, 1, getPlayer().getTruePosition());
    }

    // summon one monster, remote location
    public void spawnMonster(int id, int x, int y) {
        spawnMonster(id, 1, new Point(x, y));
    }

    // multiple monsters, remote location
    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    // handler for all spawnMonster
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public void sendNPCText(final String text, final int npc) {
        getMap().broadcastMessage(NPCPacket.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
    }

    public void logPQ(String text) {
//	FileoutputUtil.log(FileoutputUtil.PQ_Log, text);
    }

    public void outputFileError(Throwable t) {
        FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, t);
    }

    public int nextInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public MapleQuest getQuest(int arg0) {
        return MapleQuest.getInstance(arg0);
    }

    public void achievement(int a) {
        c.getPlayer().getMap().broadcastMessage(CField.achievementRatio(a));
    }

    public final MapleInventory getInventory(int type) {
        return c.getPlayer().getInventory(MapleInventoryType.getByType((byte) type));
    }

    public int randInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public void sendDirectionStatus(int key, int value) {
        c.getSession().write(UIPacket.getDirectionInfo((byte) key, value));
        c.getSession().write(UIPacket.getDirectionStatus(true));
    }

    public void sendDirectionInfo(String data) {
        c.getSession().write(UIPacket.getDirectionInfo(data, 2000, 0, -100, 0, 0));
        c.getSession().write(UIPacket.getDirectionInfo(1, 2000));
    }

    public final void prepareAswanMob(int mapid, EventManager eim) {
        MapleMap map = eim.getMapFactory().getMap(mapid);
        if (c.getPlayer().getParty() != null) {
            map.setChangeableMobOrigin(ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getCharacterById(c.getPlayer().getParty().getLeader().getId()));
        } else {
            map.setChangeableMobOrigin(c.getPlayer());
        }
        map.killAllMonsters(false);
        map.respawn(true);
    }

    public int getAzwanCoinsAvail() {
        return c.getPlayer().getAzwanCoinsAvail();
    }

    public int getAzwanCoinsRedeemed() {
        return c.getPlayer().getAzwanCoinsRedeemed();
    }

    public void setAzwanCoinsAvail(int quantity) {
        c.getPlayer().setAzwanCoinsAvail(quantity);
    }

    public void setAzwanCoinsRedeemed(int quantity) {
        c.getPlayer().setAzwanCoinsRedeemed(quantity);
    }

    public int getAzwanECoinsAvail() {
        return c.getPlayer().getAzwanECoinsAvail();
    }

    public int getAzwanCCoinsAvail() {
        return c.getPlayer().getAzwanCCoinsAvail();
    }

    public void setAzwanECoinsAvail(int quantity) {
        c.getPlayer().setAzwanECoinsAvail(quantity);
    }

    public void setAzwanCCoinsAvail(int quantity) {
        c.getPlayer().setAzwanCCoinsAvail(quantity);
    }

    public void testInner(byte x) {
        InnerSkillValueHolder inner = null;
        byte ability = x;
        inner = InnerAbillity.getInstance().TESTrenewSkill((byte) x);
        c.getPlayer().innerSkills.add(inner);
        c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(inner.getSkillId()), inner.getSkillLevel(), inner.getSkillLevel());
        c.getSession().write(CWvsContext.characterPotentialSet(ability, inner.getSkillId(), inner.getSkillLevel(), inner.getRank(), ability == 3));
        c.getPlayer().applyInners();
    }

    public void logCust(String file, String text) throws IOException {
        FileWriter fw = new FileWriter(new File("logs/" + file + "_log.txt"), true);
        fw.write(text + "\r\n");
        fw.flush();
        fw.close();
    }

    public final void dueyItem(final int itemid) {
        DueyHandler.addNewItemToDb(itemid, 1, 0, c.getPlayer().getId(), "[이벤트]", "보상지급 ", true);
    }

    public final void showinfoMessage(final String message) {
        c.getPlayer().dropMessage(-1, message);
    }

    public final void showInfo(final String message) {
        c.getPlayer().dropMessage(-1, message);
    }

    public final void showinfo(final String message) {
        c.getPlayer().dropMessage(-1, message);
    }

    public final void TimeMoveMap(final int movemap, final int destination, final int time) {
        timeMoveMap(destination, movemap, time);
    }

    public void cancelBuff(int skill) {
        c.getPlayer().cancelEffect(SkillFactory.getSkill(skill).getEffect(1), -1);
    }

    public final void timeMoveMap(final int destination, final int movemap, final int time) {
        warp(movemap, 0);
        getClient().getSession().write(CField.getClock(time));
        CloneTimer tMan = CloneTimer.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (getPlayer() != null) {
                    if (getPlayer().getMapId() == movemap) {
                        warp(destination);
                        cancelBuff(80001027);
                        cancelBuff(80001028);
                    }
                }
            }
        };
        tMan.schedule(r, time * 1000);
    }

    public final void scheduleTimeMoveMap(final int destid, final int fromid, final int time, final boolean reset) {
        final MapleMap dest = c.getChannelServer().getMapFactory().getMap(destid);
        final MapleMap from = c.getChannelServer().getMapFactory().getMap(fromid);
        from.broadcastMessage(CField.getClock(time));
        //  from.setMapTimer(System.currentTimeMillis() + ((long) time) * 1000); // 딱히 필요없는거같고 
        CloneTimer tMan = CloneTimer.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                List<MapleCharacter> chr = new ArrayList<MapleCharacter>();
                for (MapleMapObject chrz : from.getCharactersAsMapObjects()) {
                    chr.add((MapleCharacter) chrz);
                }
                for (MapleCharacter chrz : chr) {
                    chrz.changeMap(dest, dest.getPortal(0));
                    chrz.dropMessage(-1, "시간이 초과되었습니다.");
                }
                if (reset) {
                    from.resetReactors();
                    from.killAllMonsters(false);
                    for (final MapleMapObject i : from.getAllItems()) {
                        from.removeMapObject(i);
                    }
                }
            }
        };
        tMan.schedule(r, ((long) time) * 1000);
    }

    public void updateQuest(int qid, String data) {
        this.c.getPlayer().updateQuest(qid, data);
    }

    public void resetEquip() {
        List<Short> del = new ArrayList<Short>();
        for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            del.add(item.getPosition());
        }
        List<Short> del2 = new ArrayList<Short>();
        for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP).list()) {
            del2.add(item.getPosition());
        }
        for (Short delz : del) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(delz);
        }
        for (Short delzz : del2) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(delzz);
        }
    }

    public void removeEquip(int olditemid) {
        short slot = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(olditemid).getPosition();
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(slot);
    }

    public void addEquip(short pos, int itemid, short watk, short wdef, short mdef, byte upslot, short hp, short mp) {
        MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        Equip eq = new Equip(itemid, pos, (byte) 0);
        eq.setWatk(watk);
        eq.setWdef(wdef);
        eq.setMdef(mdef);
        eq.setMp(mp);
        eq.setHp(hp);
        if (itemid == 1099004) {
            eq.setStr((short) 12);
            eq.setDex((short) 12);
        }
        if (itemid == 1098002) {
            eq.setStr((short) 7);
            eq.setDex((short) 7);
        }
        if (itemid == 1098003) {
            eq.setStr((short) 12);
            eq.setDex((short) 12);
        }
        eq.setUpgradeSlots(upslot);
        eq.setExpiration(-1);
        equip.addFromDB(eq.copy());
    }

    public void updateChar() {
        MapleMap currentMap = c.getPlayer().getMap();
        currentMap.removePlayer(c.getPlayer());
        currentMap.addPlayer(c.getPlayer());
    }

    public void fakeRelog() {
        c.getSession().write(CField.getCharInfo(c.getPlayer()));

    }

    public final int getCurrentExpedId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    public final boolean isExpedLeader() {
        if (getPlayer().getParty() == null) {
            return false;
        }
        return World.Party.getExped(c.getPlayer().getParty().getExpeditionId()).getLeader() == c.getPlayer().getId();
    }

    public final boolean getExpeds() {
        MapleExpedition exped;
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        exped = World.Party.getExped(c.getPlayer().getParty().getExpeditionId());
        if (exped != null) {
            return true;
        }
        return false;
    }

    public final MapleExpedition getExpedition() {
        if (getPlayer().getParty() == null || getPlayer().getParty().getExpeditionId() <= 0) {
            return null;
        }
        return World.Party.getExped(getPlayer().getParty().getExpeditionId());
    }

    public final boolean isLeader_Expedition() {
        if (getPlayer().getParty() == null || getPlayer().getParty().getExpeditionId() <= 0) {
            return false;
        }
        return getExpedition().getLeader() == c.getPlayer().getId();
    }

    public final boolean allMembersHere_Expedition() {
        MapleExpedition exped = getExpedition();
        if (exped == null) {
            return false;
        }
        for (int p : exped.getParties()) {
            MapleParty party = World.Party.getParty(p);
            if (party != null) {
                for (MaplePartyCharacter pUser : party.getMembers()) {
                    final MapleCharacter user = c.getPlayer().getMap().getCharacterById(pUser.getId());
                    if (user == null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public final boolean isType_Expedition(int type) {
        MapleExpedition exped = getExpedition();
        if (exped != null) {
            if (exped.getType().exped == type) {
                return true;
            }
        }
        return false;
    }

    public void setTimer(final int out, int time) {
        MapTimer tMan = MapTimer.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                warp(out);
            }
        };
        tMan.schedule(r, time * 1000);
    }

    public final void openNpcT(final int id, final int time) {
        CloneTimer tMan = CloneTimer.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                openNpc(id);
            }
        };
        tMan.schedule(r, time * 1000);
    }

    public final int AllPlayerCount() {
        int 동접 = 0;
        for (ChannelServer chn : ChannelServer.getAllInstances()) {
            동접 += chn.getPlayerStorage().getConnectedClients();
        }
        return 동접;
    }

    public final void setGender(final byte gender) {
        c.setGender(gender);
    }

    private final void spawnMobSex(final int id, final int qty, final Point pos, int time) {
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                for (int i = 0; i < qty; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
                }

            }
        }, time * 1000); // 1000이 1초임
    }

    public final void lockInGameUI(final boolean i) {
        c.sendPacket(CUserLocal.setInGameDirectionMode(i == true ? 1 : 0));
    }

    public void sendDelay(int v8) {
        c.sendPacket(UIPacket.getDirectionInfo(1, v8));
    }

    public void forcedInput(int v42) {
        c.sendPacket(CField.UIPacket.forcedInput(v42));
    }

    public void forcedAction(int v3, int v5) {
        c.sendPacket(CField.UIPacket.forcedAction(v3, v5));
    }

    public void patternInputRequest(String v67, int v43, int v59, int v44) {
        c.sendPacket(CField.UIPacket.patternInputRequest(v67, v43, v59, v44));
    }

    public void cameraMove(boolean v64, int v9, int x, int y) {
        c.sendPacket(CField.UIPacket.cameraMove(new Point(x, y), v64, v9));
    }

    public void showFieldEffect(boolean broadcast, String effect) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.showEffect(effect));
        } else {
            c.sendPacket(CField.showEffect(effect));
        }
    }

    public void objectEnableEffect(boolean broadcast, String effect) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(FieldEffect.objectEnableEffect(effect));
        } else {
            c.sendPacket(FieldEffect.objectEnableEffect(effect));
        }
    }

    public void showDirectionEffect(String effect, int v1, int v2, int v3, int v4, int v5) {
        c.sendPacket(UIPacket.getDirectionInfo(effect, v1, v2, v3, v4, v5));
    }

    public final void movieClipInGameUI(final boolean enabled) {
        c.sendPacket(CUserLocal.setStandAloneMode(enabled));
        c.sendPacket(CUserLocal.setDirectionMode(enabled));
    }

    public void reservedEffect(final String data) {
        c.sendPacket(UserEffect.sceneEffect(data));
    }

    public void delayEffect(String UOL, int unknown) {
        c.sendPacket(UserEffect.delayEffect(UOL, unknown));
    }

    public void blindEffect(boolean active) {
        c.sendPacket(UserEffect.blindEffect(active));
    }

    public void summonEffect(byte type, int x, int y) {
        c.sendPacket(FieldEffect.summonEffect(type, x, y));
    }

    public void trembleEffect(byte type, int delay) {
        c.sendPacket(FieldEffect.trembleEffect(type, delay));
    }

    public void fadeInOut(int v278, int v381, int v378, byte v298) {
        c.sendPacket(EffectPacket.fadeInOut(v278, v381, v378, (byte) v298));
    }

    public void effectPlay(String v67, int v59, Point v57, boolean v14, int v65, boolean v15, int v17, byte v61, byte v20) {
        c.sendPacket(UIPacket.effectPlay(v67, v59, v57, v14, v65, v15, v17 > 0 ? c.getPlayer().getMap().getNPCById(v17).getObjectId() : 0, (byte) v61, (byte) v20));
    }

    public void setFuncKey(int v1, int v3) {
        c.getPlayer().changeKeybinding(v1, (byte) 1, v3);
        c.sendPacket(CField.getKeymap(c.getPlayer().getKeyLayout()));
    }

    public void sendTutorialUI(String args[]) {
        c.sendPacket(CField.NPCPacket.tutorialUI(args));
    }

    public void showNpcSpecialActionByTemplateId(int npcTemplateId, String effectName) {
        for (MapleNPC npc : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
            if (npc.getId() == npcTemplateId) {
                c.sendPacket(NPCPacket.npcSetSpecialAction(npc.getObjectId(), effectName, 0, false));
            }
        }
        //c.sendPacket(NPCPacket.npcSetSpecialAction(c.getPlayer().getMap().getNPCById(npcTemplateId).getObjectId(), effectName, 0, false));
    }

    public void moveNpcByTemplateId(int npcTemplateId, String direction, int delay) {
        c.sendPacket(NPCPacket.npcSetForceMove(c.getPlayer().getMap().getNPCById(npcTemplateId).getObjectId(), direction, delay));
    }

    public void userEmotion(int emotion, int duration, boolean byItemOption) {
        c.sendPacket(CUserLocal.setEmotion(emotion, duration, byItemOption));
    }

    public void floatMessage(String message, int type) {
        c.getPlayer().getMap().floatNotice(message, type, false);
    }

    public void topMessage(String message) {
        c.getPlayer().getMap().broadcastMessage(CWvsContext.getTopMsg(message));
    }

    public String getPNPCName() {
        if (id >= 9901000) {
            for (PlayerNPC pnpc : c.getChannelServer().getAllPlayerNPC()) {
                if (pnpc.getId() == id) {
                    return pnpc.getName();
                }
            }
        }
        return "";
    }

    public void setPlayerNPC(MapleCharacter user, int map, int npc, int x, int y, int f, int fh, boolean update) {
        MapleMap nmap = getClient().getChannelServer().getMapFactory().getMap(map);
        MapleNPC npctemplate = nmap.getNPCById(npc);
        if (null != npctemplate) {
            if (update == false) {
                PlayerNPC newpnpc = new PlayerNPC(user, user.getName(), npc, nmap, x, y, f, fh);
                newpnpc.addToServer();
            }
            PlayerNPC.sendBroadcastModifiedNPC(user, nmap, npc, update);
        }
    }

    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.playSound(sound));
        } else {
            c.getPlayer().getClient().sendPacket(CField.playSound(sound));
        }
    }

    public void changeBGM(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(FieldEffect.changeBGM(sound));
        } else {
            c.getPlayer().getClient().sendPacket(FieldEffect.changeBGM(sound));
        }
    }

    public String getMonsterName(int a) {
        MapleMonster mob = null;
        mob = MapleLifeFactory.getMonster(a);
        if (mob == null) {
            return "Lv.??? : ???";
        }
        return "Lv." + mob.getStats().getLevel() + " : " + mob.getStats().getName();
    }

    public String itemDrop(int dropperid) {
        StringBuilder sb = new StringBuilder("");
        boolean check = false;
        int ch = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid = " + dropperid).executeQuery();
            while (rs.next()) {
                check = true;
                ch = rs.getInt("chance") * 2;
                sb.append(MapleItemInformationProvider.getInstance().getName(rs.getInt("itemid"))).append(" | 아이템 코드 : ").append(rs.getInt("itemid")).append(" | 확률 : ").append((Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0)).append("%").append("\r\n");
            }
            rs.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (check) {
            return sb.toString();
        } else {
            return "#r드롭 아이템정보를 찾지 못하였습니다.#k";
        }
    }

    public String MonsterDrop(int itemid) {
        StringBuilder sb = new StringBuilder("");
        boolean check = false;
        int ch = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs = con.prepareStatement("SELECT * FROM drop_data WHERE itemid = " + itemid).executeQuery();
            while (rs.next()) {
                check = true;
                ch = rs.getInt("chance") * 2;
                sb.append(getMonsterName(rs.getInt("dropperid"))).append(" | 몬스터코드 : ").append(rs.getInt("dropperid")).append(" | 확률 : ").append((Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0)).append("%").append("\r\n");
            }
            rs.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (check) {
            return sb.toString();
        } else {
            return "#r드롭 몬스터정보를 찾지 못하였습니다.#k";
        }
    }

    public String getMonsterNames(String a) {
        String b = "";
        try {
            for (Pair<Integer, String> monsterPair : MapleLifeFactory.getInstance().getAllStringData()) {
                if (monsterPair.getRight().contains(a)) {
                    if (itemDrop(monsterPair.getLeft()).equals("#r드롭 아이템정보를 찾지 못하였습니다.#k")) {
                        continue;
                    }
                    b = b + "\r\n#L" + monsterPair.getLeft() + "#(Lv. " + MapleLifeFactory.getMonster(monsterPair.getLeft()).getStats().getLevel() + ") " + monsterPair.getRight() + "#l";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!b.equals("")) {
            return b;
        } else {
            return "#r잘못된 몬스터 이름입니다.#k";
        }
    }

    public String getItemNames(String a) {
        String b = "";
        try {
            for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllStringData()) {
                if (itemPair.getRight().contains(a)) {
                    if (MonsterDrop(itemPair.getLeft()).equals("#r드롭 몬스터정보를 찾지 못하였습니다.#k")) {
                        continue;
                    }
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    int reqLevel = ii.getReqLevel(itemPair.getLeft());
                    b = b + "\r\n#L" + itemPair.getLeft() + "#(Lv." + reqLevel + ") " + itemPair.getRight() + "#l";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!b.equals("")) {
            return b;
        } else {
            return "#r잘못된 아이템 이름입니다.#k";
        }
    }

    public final void handlePinkbeanSummon(int millisecs) {
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                c.getPlayer().getMap().removeNpc(2141000);
                forceStartReactor(270050100, 2709000);
            }
        }, millisecs);
    }

    public final void gainPartyItem(MapleCharacter user, int nItemID, int nCount) {
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pMember.getName());
                if (cUser != null) {
                    if (nCount > 0) {
                        MapleInventoryManipulator.addById(cUser.getClient(), nItemID, (short) nCount, "", null, -1, null);
                    } else {
                        MapleInventoryManipulator.removeById(cUser.getClient(), GameConstants.getInventoryType(nItemID), nItemID, -nCount, true, false);
                    }
                    cUser.getClient().sendPacket(InfoPacket.getShowItemGain(nItemID, (short) nCount, true));
                }
            }
        }
    }

    public final String enterLimitUserByMap(MapleCharacter user, int nMapID) {
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                boolean isCorrect = true;
                if (user.getClient().getChannel() != pMember.getChannel()) {
                    isCorrect = false;
                }
                if (user.getMapId() != pMember.getMapid()) {
                    isCorrect = false;
                }
                if (!isCorrect) {
                    return pMember.getName();
                }
            }
        }
        return null;
    }

    public final String enterLimitUserByItem(MapleCharacter user, int nItemID, int nCount) {
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pMember.getName());
                if (cUser != null) {
                    if (!cUser.haveItem(nItemID, nCount)) {
                        return pMember.getName();
                    }
                }
            }
        }
        return null;
    }
    
    public final String enterLimitUserByLevel(MapleCharacter user, int nLV) {
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pMember.getName());
                if (cUser != null) {
                    if (cUser.getLevel() < nLV) {
                        return pMember.getName();
                    }
                }
            }
        }
        return null;
    }

    public final String enterLimitUserByCount(MapleCharacter user, String nType, int nCount) {
        int nQRecord = 30000000;
        String nDate = Calendar.getInstance().get(Calendar.YEAR) % 100 + "/" + StringUtil.getLeftPaddedStr(Calendar.getInstance().get(Calendar.MONTH) + "", '0', 2) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pMember.getName());
                if (cUser != null) {
                    if (cUser.getOneInfoQuest(nQRecord, nType + "_enter").equals(nCount + "") && cUser.getOneInfoQuest(nQRecord, nType + "_date").equals(nDate)) {
                        return pMember.getName();
                    }
                }
            }
        }
        return null;
    }

    public final String enterLimitUserByTime(MapleCharacter user, String nType) {
        int nQRecord = 30000000;
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pMember.getName());
                if (cUser != null) {
                    if (cUser.getOneInfoQuest(nQRecord, nType + "_time") == null) {
                        cUser.updateOneInfoQuest(nQRecord, nType + "_time", "0");
                    }
                    if (cUser.getOneInfoQuest(nQRecord, nType + "_time").equals("")) {
                        cUser.updateOneInfoQuest(nQRecord, nType + "_time", "0");
                    }
                    if (!cUser.isGM() && Long.parseLong(cUser.getOneInfoQuest(nQRecord, nType + "_time")) > this.getCurrentTime()) {
                        return pMember.getName();
                    }
                }
            }
        }
        return null;
    }

    public final void handleEnter(boolean eCount, MapleCharacter user, String nType, int nMin, int eMapID) {
        int nQRecord = 30000000;
        String nDate = Calendar.getInstance().get(Calendar.YEAR) % 100 + "/" + StringUtil.getLeftPaddedStr(Calendar.getInstance().get(Calendar.MONTH) + "", '0', 2) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (MaplePartyCharacter pMember : user.getParty().getMembers()) {
            if (pMember != null) {
                MapleCharacter cUser = this.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pMember.getName());
                if (cUser != null) {
                    if (eCount) {
                        if (cUser.getOneInfoQuest(nQRecord, nType + "_date").equals(nDate) == false) {
                            cUser.updateOneInfoQuest(nQRecord, nType + "_enter", "1");
                            cUser.updateOneInfoQuest(nQRecord, nType + "_date", nDate);
                        } else {
                            int nCount = Integer.parseInt(cUser.getOneInfoQuest(nQRecord, nType + "_enter"));
                            cUser.updateOneInfoQuest(nQRecord, nType + "_enter", (nCount + 1) + "");
                        }
                    } else {
                        cUser.updateOneInfoQuest(nQRecord, nType + "_time", (this.getCurrentTime() + nMin * 60000) + "");
                    }
                    if (MapConstants.isDeathCountMap(eMapID) != -1) {
                        cUser.setDeathCount(MapConstants.isDeathCountMap(eMapID));
                    }
                }
            }
        }
        EventManager eScript = this.getEventManager("expedition_" + nType);
        if (eScript != null) {
            eScript.setProperty("map", eMapID + "");
            eScript.startInstance_Party(eMapID + "", user);
        }
    }
}
