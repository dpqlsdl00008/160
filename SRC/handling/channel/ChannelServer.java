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
package handling.channel;

import client.MapleCharacter;
import handling.ServerType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import handling.login.LoginServer;
import handling.netty.MapleNettyDecoder;
import handling.netty.MapleNettyEncoder;
import handling.netty.MapleNettyHandler;
import handling.world.CheaterData;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.maps.MapleMapFactory;
import server.shops.HiredMerchant;
import server.life.PlayerNPC;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import server.MapleSquad.MapleSquadType;
import server.ServerProperties;
import server.events.*;
import server.maps.AramiaFireWorks;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.shops.AbstractPlayerStore;
import server.shops.HiredMerchantSave;
import server.shops.MaplePlayerShop;
import tools.ConcurrentEnumMap;
import tools.Pair;
import tools.packet.CWvsContext;

public class ChannelServer {

    public static long serverStartTime;
    private int expRate = 1, mesoRate = 1, dropRate = 1, cashRate = 1, traitRate = 1, BossDropRate = 1, PetClosenessRate = 1;
    private int expRate2 = 1;
    private int expRate3 = 1;
    private int expRate4 = 1;
    private int expRate5 = 1;
    private int expRate6 = 1;
    public static int QuestRate = 1;
    private short port = 8486; // 11603 // 8585
    private static final short DEFAULT_PORT = 8585;
    private int channel, running_MerchantID = 0;
    private String serverMessage, ip;
    private boolean shutdown = false, finishedShutdown = false, MegaphoneMuteState = false;
    private PlayerStorage players;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private AramiaFireWorks works = new AramiaFireWorks();
    private static final Map<Integer, ChannelServer> instances = new HashMap<Integer, ChannelServer>();
    private final Map<MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<MapleSquadType, MapleSquad>(MapleSquadType.class);
    private final Map<Integer, HiredMerchant> merchants = new HashMap<Integer, HiredMerchant>();
    private final List<PlayerNPC> playerNPCs = new LinkedList<PlayerNPC>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<MapleEventType, MapleEvent>(MapleEventType.class);
    public boolean eventOn = false;
    public int eventMap = 0;
    public int eventMapwarp = 0;
    public boolean eventstat = false;
    public int eventstatchr = 0;
    private List<String> connectedMacs;
    private static ServerBootstrap bootstrap;
    private boolean usePotential = true, autoChangeJob = true, autoSkillMaster = true;
    private int WBMob = -1, WBChannel = -1, WBMap = -1, WBClock = -1;

    private int AramiaSun = 0;
    private long AramiaClock = 0;

    private ChannelServer(final int channel) {
        this.channel = channel;
        mapFactory = new MapleMapFactory(channel);
    }

    public final List<MapleCharacter> getPartyMembers(final MapleParty party) {
        List<MapleCharacter> partym = new LinkedList<>();
        for (final MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar.getChannel() == getChannel()) { // Make sure the thing doesn't get duplicate plays due to ccing bug.
                MapleCharacter chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    partym.add(chr);
                }
            }
        }
        return partym;
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<Integer>(instances.keySet());
    }

    public final void loadEvents() {
        if (events.size() != 0) {
            return;
        }
        events.put(MapleEventType.코크, new MapleCoconut(channel, MapleEventType.코크)); //yep, coconut. same shit
        events.put(MapleEventType.코코넛, new MapleCoconut(channel, MapleEventType.코코넛));
        events.put(MapleEventType.Fitness, new MapleFitness(channel, MapleEventType.Fitness)); //somehow this fails? wut?
        events.put(MapleEventType.올라올라, new MapleOla(channel, MapleEventType.올라올라));
        events.put(MapleEventType.퀴즈, new MapleOxQuiz(channel, MapleEventType.퀴즈));
        events.put(MapleEventType.스노우볼, new MapleSnowball(channel, MapleEventType.스노우볼));
        events.put(MapleEventType.서바이벌, new MapleSurvival(channel, MapleEventType.서바이벌));
        events.put(MapleEventType.대운동회, new MapleBig(channel, MapleEventType.대운동회));
    }
    public final void run_startup_configurations() throws IOException {
        setChannel(channel); //instances.put
        try {
            expRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.exp"));
            
            expRate2 = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.exp_010_030"));
            expRate3 = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.exp_031_070"));
            expRate4 = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.exp_071_120"));
            expRate5 = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.exp_121_160"));
            expRate6 = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.exp_161_200"));
            
            mesoRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.meso"));
            dropRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.drop"));
            BossDropRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.bossdrop"));
            cashRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.cash"));
            traitRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.trait"));
            PetClosenessRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.pet"));
            QuestRate = Integer.parseInt(ServerProperties.getProperty("gameserver.rate.quest"));
            serverMessage = ServerProperties.getProperty("gameserver.yellowmessage");
            eventSM = new EventScriptManager(this, ServerProperties.getProperty("gameserver.events").split(","));
            usePotential = Boolean.parseBoolean(ServerProperties.getProperty("gameserver.potential"));

            autoChangeJob = Boolean.parseBoolean(ServerProperties.getProperty("gameserver.autochangejob"));
            autoSkillMaster = Boolean.parseBoolean(ServerProperties.getProperty("gameserver.autoskillmaster"));

            WBMob = Integer.parseInt(ServerProperties.getProperty("gameserver.worldboss.mob"));
            WBChannel = Integer.parseInt(ServerProperties.getProperty("gameserver.worldboss.channel"));
            WBMap = Integer.parseInt(ServerProperties.getProperty("gameserver.worldboss.map"));
            WBClock = Integer.parseInt(ServerProperties.getProperty("gameserver.worldboss.clock"));

            this.port = (short) (port + getChannel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ip = ServerProperties.getProperty("channelserver.interface") + ":" + port;
        System.out.println("\r\n");
        System.out.println("<Channel Server " + channel + " Status>");
        System.out.println("ExpRate                    : " + expRate);
        System.out.println("MesoRate                   : " + mesoRate);
        System.out.println("ServerMessage              : " + serverMessage);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new MapleNettyDecoder());
                            ch.pipeline().addLast("encoder", new MapleNettyEncoder());
                            ch.pipeline().addLast("handler", new MapleNettyHandler(ServerType.CHANNEL, channel));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = bootstrap.bind(port).sync(); // (7)ss
            System.out.println("[Success] Channel Server Port : " + port);
        } catch (InterruptedException e) {
            System.out.println("[Fail] Channel Server Port : " + port);
        }
        players = new PlayerStorage(channel);
        //loadEvents();

        eventSM.init();
    }

    public final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        broadcastPacket(CWvsContext.serverNotice(0, "This channel will now shut down."));
        // dc all clients by hand so we get sessionClosed...
        shutdown = true;

        System.out.println("Channel " + channel + ", Saving characters...");

        getPlayerStorage().disconnectAll();

        System.out.println("Channel " + channel + ", Unbinding...");

        //temporary while we dont have !addchannel
        instances.remove(channel);
        setFinishShutdown();
    }

    public final boolean Eventstat() {
        return eventstat;
    }

    public final void setEventstat(final boolean on) {
        this.eventstat = on;
    }

    public final int Eventstatchr() {
        return eventstatchr;
    }

    public final void setEventstatchr(final int on) {
        this.eventstatchr = on;
    }

    public final boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public static final ChannelServer newInstance(final int channel) {
        return new ChannelServer(channel);
    }

    public static final ChannelServer getInstance(final int channel) {
        return instances.get(channel);
    }

    public final void addPlayer(final MapleCharacter chr) {
        getPlayerStorage().registerPlayer(chr);
    }

    public final PlayerStorage getPlayerStorage() {
        if (players == null) { //wth
            players = new PlayerStorage(channel); //wthhhh
        }
        return players;
    }

    public final void removePlayer(final MapleCharacter chr) {
        getPlayerStorage().deregisterPlayer(chr);

    }

    public final void removePlayer(final int idz, final int accidz, final String namez) {
        getPlayerStorage().deregisterPlayer(idz, accidz, namez);

    }

    public final String getServerMessage() {
        return serverMessage;
    }

    public final void setServerMessage(final String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(CWvsContext.serverMessage(serverMessage));
    }

    public final void broadcastPacket(final byte[] data) {
        getPlayerStorage().broadcastPacket(data);
    }

    public final void broadcastSmegaPacket(final byte[] data) {
        getPlayerStorage().broadcastSmegaPacket(data);
    }

    public final void broadcastGMPacket(final byte[] data) {
        getPlayerStorage().broadcastGMPacket(data);
    }

    public final int getExpRate() {
        return expRate;
    }
    
    public final int getExpRate2() {
        return expRate2;
    }
    
    public final int getExpRate3() {
        return expRate3;
    }
    
    public final int getExpRate4() {
        return expRate4;
    }
    
    public final int getExpRate5() {
        return expRate5;
    }
    
    public final int getExpRate6() {
        return expRate6;
    }

    public final void setExpRate(final int expRate) {
        this.expRate = expRate;
    }

    public final int getCashRate() {
        return cashRate;
    }

    public final int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        instances.put(channel, this);
        LoginServer.addChannel(channel);
    }

    public static final ArrayList<ChannelServer> getAllInstances() {
        return new ArrayList<ChannelServer>(instances.values());
    }

    public final String getIP() {
        return ip;
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, ServerProperties.getProperty("gameserver.events").split(","));
        eventSM.init();
    }

    public final int getMesoRate() {
        return mesoRate;
    }

    public final void setMesoRate(final int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public final void setDropRate(final int dropRate) {
        this.dropRate = dropRate;
    }

    public final int getDropRate() {
        return dropRate == 0 ? 1 : dropRate;
    }

    public final int getBossDropRate() {
        return BossDropRate;
    }

    public static final void startChannel_Main() throws IOException {
        serverStartTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.parseInt(ServerProperties.getProperty("channelserver.count", "0")); i++) {
            newInstance(i + 1).run_startup_configurations();
        }
    }

    public Map<MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(mapleSquads);
    }

    public final MapleSquad getMapleSquad(final String type) {
        return getMapleSquad(MapleSquadType.valueOf(type.toLowerCase()));
    }

    public final MapleSquad getMapleSquad(final MapleSquadType type) {
        return mapleSquads.get(type);
    }

    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        final MapleSquadType types = MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !mapleSquads.containsKey(types)) {
            mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public final boolean removeMapleSquad(final MapleSquadType types) {
        if (types != null && mapleSquads.containsKey(types)) {
            mapleSquads.remove(types);
            return true;
        }
        return false;
    }

    public final int closeAllMerchant() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Entry<Integer, HiredMerchant>> merchants_ = merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                HiredMerchant hm = merchants_.next().getValue();
                HiredMerchantSave.QueueShopForSave(hm);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : mapFactory.getMap(i).getAllHiredMerchantsThreadsafe()) {
                HiredMerchantSave.QueueShopForSave((HiredMerchant) mmo);
                ret++;
            }
        }
        return ret;
    }

    public final int addMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();
        try {
            running_MerchantID++;
            merchants.put(running_MerchantID, hMerchant);
            return running_MerchantID;
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final void removeMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        try {
            merchants.remove(hMerchant.getStoreId());
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final boolean containsMerchant(final int accid, int cid) {
        boolean contains = false;

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.getOwnerAccId() == accid || hm.getOwnerId() == cid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return contains;
    }

    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<HiredMerchant>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return MegaphoneMuteState;
    }

    public int getEvent() {
        return eventmap;
    }

    public final void setEvent(final int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(final MapleEventType t) {
        return events.get(t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return playerNPCs;
    }

    public final void addPlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.contains(npc)) {
            return;
        }
        playerNPCs.add(npc);
        getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public final void removePlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.contains(npc)) {
            playerNPCs.remove(npc);
            getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public final int getPort() {
        return port;
    }

    public static final Set<Integer> getChannelServer() {
        return new HashSet<Integer>(instances.keySet());
    }

    public final void setShutdown() {
        this.shutdown = true;
        System.out.println("Channel " + channel + " has set to shutdown and is closing Hired Merchants...");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("Channel " + channel + " has finished shutdown.");
    }

    public final static int getChannelCount() {
        return instances.size();
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (ChannelServer cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = getPlayerStorage().getCheaters();

        Collections.sort(cheaters);
        return cheaters;
    }

    public List<CheaterData> getReports() {
        List<CheaterData> cheaters = getPlayerStorage().getReports();

        Collections.sort(cheaters);
        return cheaters;
    }

    public void broadcastMessage(byte[] message) {
        broadcastPacket(message);
    }

    public void broadcastSmega(byte[] message) {
        broadcastSmegaPacket(message);
    }

    public void broadcastGMMessage(byte[] message) {
        broadcastGMPacket(message);
    }

    public AramiaFireWorks getFireWorks() {
        return works;
    }

    public int getTraitRate() {
        return traitRate;
    }

    public int getPetClosenessRate() {
        return PetClosenessRate;
    }

    public static int getQuestRate() {
        return QuestRate;
    }

    public List<String> getConnectedMacs() {
        return Collections.unmodifiableList(connectedMacs);
    }

    public boolean containsConnectedMacs(String mac) {
        System.out.print("값이 확인중" + connectedMacs);
        // for (ChannelServer cs : getAllInstances()) {
        if (getConnectedMacs().contains(mac)) {
            System.out.print("값이 확인완" + connectedMacs);
            return true;
        }
        // }
        return false;
    }

    public void addConnectedMacs(String mac) {
        connectedMacs.add(mac);
    }

    public void removeConnectedMacs(String mac) {
        connectedMacs.remove(mac);
    }

    public static int getOnlineConnections() {
        int r = 0;
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            r += cserv.getConnectedClients();
        }
        return r;
    }

    public final Pair<Integer, Integer> findMerchant(final int accid, int cid) {

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.getOwnerAccId() == accid || hm.getOwnerId() == cid) {
                    return new Pair<Integer, Integer>(getChannel(), hm.getMap().getId() % 100);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return null;
    }

    public final HiredMerchant findAndGetMerchant(final int accid, int cid) {

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.getOwnerAccId() == accid || hm.getOwnerId() == cid) {
                    return hm;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return null;
    }

    public final List<AbstractPlayerStore> searchShop(final int itemSearch) {
        final List<AbstractPlayerStore> list = new LinkedList<AbstractPlayerStore>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        for (int m = 910000001; m <= 910000022; ++m) {
            MapleMap map = getMapFactory().getMap(m);
            for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                if (chr.getMapId() == m && chr.getPlayerShop() != null && chr.getPlayerShop() instanceof MaplePlayerShop) {
                    MaplePlayerShop shop = (MaplePlayerShop) chr.getPlayerShop();
                    if (shop.isOpen()) {
                        if (shop.searchItem(itemSearch).size() > 0) {
                            list.add(shop);
                        }
                    }
                }
            }
        }
        return list;
    }

    public final void saveMerchant() {
        Iterator merchants_ = this.merchants.values().iterator();
        while (merchants_.hasNext()) {
            HiredMerchant merch = (HiredMerchant) merchants_.next();
            merch.saveItems();
        }
    }

    public boolean getPotential() {
        return this.usePotential;
    }

    public boolean getAutoChangeJob() {
        return this.autoChangeJob;
    }

    public boolean getAutoSkillMaster() {
        return this.autoSkillMaster;
    }

    public int getWBMob() {
        return this.WBMob;
    }

    public int getWBChannel() {
        return this.WBChannel;
    }

    public int getWBMap() {
        return this.WBMap;
    }

    public int getWBClock() {
        return this.WBClock;
    }

    public void setAramiaSun(int a) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.AramiaSun = a;
            if (cs.AramiaSun > 999) {
                for (final MapleMap mm : cs.getMapFactory().getAllLoadedMaps()) {
                    mm.floatNotice("나무가 좋아하는 햇살 1,000개가 모여 [메소 획득량 +50%] 이벤트가 진행 중에 있습니다.", 5120008, false);
                }
            }
        }
    }

    public int getAramiaSun() {
        return this.AramiaSun;
    }

    public void setAramiaClock(long a) {
        this.AramiaClock = a;
    }

    public long getAramiaClock() {
        return this.AramiaClock;
    }
}
