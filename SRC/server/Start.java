package server;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import constants.GameConstants;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;

import java.sql.SQLException;

import database.DatabaseConnection;
import handling.channel.handler.EventList;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.awt.Point;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import server.Timer.*;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.quest.MapleQuest;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import connector.connectorServer;
import handling.ChatType;
import handling.channel.handler.DueyHandler;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javafx.scene.chart.PieChart.Data;
import javax.script.Invocable;
import javax.script.ScriptException;
import scripting.EtcScriptInvoker;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import tools.MemoryUsageWatcher;
import tools.PacketTester;

import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CPlayerShop;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);
    public static NewJFrame njf;
    public static int addplayer = 10;

    public void run() throws IOException, Exception {

        DatabaseConnection.init();

        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE accounts SET loggedin = 0, allowed = 0");
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        ClientTimer.getInstance().start();
        LoadingThread WorldLoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleGuildRanking.getInstance().load();
                MapleGuild.loadAll();
            }
        }, "WorldLoader", this);
        LoadingThread FamilyLoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleFamily.loadAll(); //(this);
            }
        }, "FamilyLoader", this);
        LoadingThread QuestLoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleLifeFactory.loadQuestCounts();
                MapleQuest.initQuests();
            }
        }, "QuestLoader", this);
        LoadingThread ProviderLoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleItemInformationProvider.getInstance().runEtc();
            }
        }, "ProviderLoader", this);
        LoadingThread MonsterLoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleMonsterInformationProvider.getInstance().load();
            }
        }, "MonsterLoader", this);
        LoadingThread ItemLoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleItemInformationProvider.getInstance().runItems();
            }
        }, "ItemLoader", this);
        LoadingThread SkillFactoryLoader = new LoadingThread(new Runnable() {
            public void run() {
                SkillFactory.load();
            }
        }, "SkillFactoryLoader", this);
        LoadingThread BasicLoader = new LoadingThread(new Runnable() {
            public void run() {
                LoginInformationProvider.getInstance();
                RandomRewards.load();
                //  MapleOxQuizFactory.getInstance();
                MapleCarnivalFactory.getInstance();
                CharacterCardFactory.getInstance().initialize();
                SpeedQuizFactory.getInstance().initialize();
                MobSkillFactory.getInstance();
                SpeedRunner.loadSpeedRuns();
                MapleQuestFactory.getInstance();
            }
        }, "BasicLoader", this);
        LoadingThread MIILoader = new LoadingThread(new Runnable() {
            public void run() {
                MapleInventoryIdentifier.getInstance();
            }
        }, "MIILoader", this);
        /*  LoadingThread CustomLifeLoader = new LoadingThread(new Runnable() {
         public void run() {
         MapleMapFactory.loadCustomLife();
         }
         }, "CustomLifeLoader", this);*/
        LoadingThread CashItemLoader = new LoadingThread(new Runnable() {
            public void run() {
                CashItemFactory.getInstance().initialize();
            }
        }, "CashItemLoader", this);
        LoadingThread[] LoadingThreads = {WorldLoader, FamilyLoader, QuestLoader, ProviderLoader, SkillFactoryLoader,
            BasicLoader, CashItemLoader, MIILoader, MonsterLoader, ItemLoader};

        for (Thread t : LoadingThreads) {
            t.start();
        }
        synchronized (this) {
            wait();
        }
        while (CompletedLoadingThreads.get() != LoadingThreads.length) {
            synchronized (this) {
                wait();
            }
        }
        LoginServer.run_startup_configurations();
        ChannelServer.startChannel_Main();
        System.out.println("\r\n");
        CashShopServer.run_startup_configurations();
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
        //new registerRespawn().start();
        ShutdownServer.registerMBean();
        PlayerNPC.loadAll();
        //MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.setOn();

        RankingWorker.run();
        //System.out.print("Initializing Auto Save, AzwanDailyCheck, and Ardentmill reset timers.");

        //World.runAzwanDailyCheck();
        //World.runArdentmillReset();
        //World.runTodayTrait();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long time = cal.getTimeInMillis();
        long schedulewait = 0;
        if (time > System.currentTimeMillis()) {
            schedulewait = time - System.currentTimeMillis();
        } else {
            schedulewait = time/* + 86400000L*/ - System.currentTimeMillis();
        }
        if (schedulewait < 3600000) {
            schedulewait += 86400000L;
        }
        //openHiredMerchant();
        //System.out.println("일일초기화까지 " + StringUtil.getReadableMillis(0, schedulewait).replace("일", "days ").replace("시간", "hours ").replace("분", "mins ").replace("초", "secs ") + ".");
        //Timer.WorldTimer.getInstance().schedule(new Start.DayByDayReset(), schedulewait);
        //System.out.println("..Initialized.");
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET azwanCoinsAvail = 0, azwanCoinsRedeemed = 0");
            ps.execute();
            //System.out.println("Successfully set azwanCoinsAvail and azwanCoinsRedeemed to zero.");
        } catch (SQLException e) {
            System.err.println("ERROR Running AzwanDailyCheck error: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE ardentmill SET mineNovice = 0, mineIntermediate = 0, mineAdvanced = 0, mineExpert = 0, herbNovice = 0, herbIntermediate = 0, herbAdvanced = 0, herbExpert = 0");
            ps.execute();
            //System.out.println("Successfully reset Ardentmill's portals to zero.");
        } catch (SQLException e) {
            System.err.println("ERROR Running ArdentmillReset error: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET todaycharm = 0, todaycraft = 0, todaycharisma = 0, todaywill = 0, todaysense = 0, todayinsight = 0, todayrep = 0");
            ps.execute();
            //System.out.println("Completed All Character Expertise Reputation & Stat Reset\r\n");
        } catch (SQLException e) {
            System.err.println("체인지에러..: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        World.runAutoSave();
        Start.clean();

        try {
            //new connectorServer().run_startup_configurations();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //new Debugger().setVisible(true);
        new PacketTester().setVisible(true);
        //njf = new NewJFrame();
        //njf.setVisible(true);
        new MemoryUsageWatcher(88).start();
        WorldBoss(60);
        WorldReward(60);
        autoMsg();
        System.gc();
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }

    public static void startGC(long start) {
        float end = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        //System.out.println("이 멀티쓰레드 : " + ((start - end) / (1024 * 1024)) + "메모리정리완료!");
    }

    /*
    public static void openHiredMerchant() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM hiredmerch");
            rs = ps.executeQuery();
            while (rs.next()) {
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    if (rs.getInt("map") > 0) {
                        final MapleMap map = cs.getMapFactory().getMap(rs.getInt("map"));
                        if (map != null) {
                            boolean a = cs.containsMerchant(rs.getInt("characterid"), rs.getInt("accountid"));
                            System.out.println("3번 : " + a);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
     */
    public static void clean() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int nu = 0;
            Calendar ocal = Calendar.getInstance();
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM acheck WHERE day = 1");
            rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString("keya");
                String day = ocal.get(ocal.YEAR) + "" + (ocal.get(ocal.MONTH) + 1) + "" + ocal.get(ocal.DAY_OF_MONTH);
                String da[] = key.split("_");
                if (!da[0].equals(day)) {
                    ps = con.prepareStatement("DELETE FROM acheck WHERE keya = ?");
                    ps.setString(1, key);
                    ps.executeUpdate();
                    nu++;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public static class DayByDayReset implements Runnable {

        @Override
        public void run() {
            Connection con = null;
            PreparedStatement ps = null;
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE characters SET azwanCoinsAvail = 0, azwanCoinsRedeemed = 0");
                ps.execute();
                System.out.println("Successfully set azwanCoinsAvail and azwanCoinsRedeemed to zero.");
            } catch (SQLException e) {
                System.err.println("ERROR Running AzwanDailyCheck error: " + e);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE ardentmill SET mineNovice = 0, mineIntermediate = 0, mineAdvanced = 0, mineExpert = 0, herbNovice = 0, herbIntermediate = 0, herbAdvanced = 0, herbExpert = 0");
                ps.execute();
                System.out.println("Successfully reset Ardentmill's portals to zero.");
            } catch (SQLException e) {
                System.err.println("ERROR Running ArdentmillReset error: " + e);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE characters SET todaycharm = 0, todaycraft = 0, todaycharisma = 0, todaywill = 0, todaysense = 0, todayinsight = 0, todayrep = 0");
                ps.execute();
                System.out.println("[All Character] 전문 기술 및 능력치 RESET");
            } catch (SQLException e) {
                System.err.println("체인지에러..: " + e);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE accounts SET Fanclub = 0");
                ps.execute();
                System.out.println("[All Character] 라이징 스타 RESET");
            } catch (SQLException e) {
                System.err.println("체인지에러..: " + e);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
    }

    private static class LoadingThread extends Thread {

        protected String LoadingThreadName;

        private LoadingThread(Runnable r, String t, Object o) {
            super(new NotifyingRunnable(r, o, t));
            LoadingThreadName = t;
        }

        @Override
        public synchronized void start() {
            System.out.println("[Loading...] Started " + LoadingThreadName + " Thread");
            super.start();
        }
    }

    private static class NotifyingRunnable implements Runnable {

        private String LoadingThreadName;
        private long StartTime;
        private Runnable WrappedRunnable;
        private final Object ToNotify;

        private NotifyingRunnable(Runnable r, Object o, String name) {
            WrappedRunnable = r;
            ToNotify = o;
            LoadingThreadName = name;
        }

        public void run() {
            StartTime = System.currentTimeMillis();
            WrappedRunnable.run();
            System.out.println("[Loading Completed] " + LoadingThreadName + " | Completed in " + (System.currentTimeMillis() - StartTime) + " Milliseconds. (" + (CompletedLoadingThreads.get() + 1) + "/10)");
            synchronized (ToNotify) {
                CompletedLoadingThreads.incrementAndGet();
                ToNotify.notify();
            }
        }
    }

    public static void main(final String args[]) throws InterruptedException, Exception {
        instance.run();
    }

    public static transient ScheduledFuture<?> msg;

    public static void autoMsg() {
        if (msg == null) {
            msg = Timer.WorldTimer.getInstance().register(new Runnable() {
                public void run() {
                    try {
                        Invocable iv = EtcScriptInvoker.getInvocable("etc/autoMsg.js");
                        iv.invokeFunction("run");
                    } catch (ScriptException | NoSuchMethodException ex) {
                        Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 1000 * 60 * 10);
        }
    }

    public static transient ScheduledFuture<?> WorldBoss;

    public static void WorldBoss(int set) {
        if (WorldBoss == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 0);
            long time = cal.getTimeInMillis();
            long schedulewait = 0;
            if (time > System.currentTimeMillis()) {
                schedulewait = time - System.currentTimeMillis();
            } else {
                while (cal.getTimeInMillis() < System.currentTimeMillis()) {
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                }
                schedulewait = cal.getTimeInMillis() - System.currentTimeMillis();
            }
            WorldBoss = Timer.WorldTimer.getInstance().register(new Runnable() {
                public void run() {
                    Date date = new Date();
                    if (date.getHours() == 22) {
                        for (ChannelServer cs : ChannelServer.getAllInstances()) {
                            for (MapleMap mm : cs.getMapFactory().getAllLoadedMaps()) {
                                cs.getMapFactory().getMap(cs.getWBMap()).resetFully();
                                if (cs.getChannel() == cs.getWBChannel()) {
                                    cs.getMapFactory().getMap(cs.getWBMap()).spawnMonster_sSack(MapleLifeFactory.getMonster(cs.getWBMob()), new Point(0, 0), -2);
                                }
                                mm.broadcastMessage(CWvsContext.yellowChat("(월드 보스) CH." + cs.getWBChannel() + " 채널의 " + cs.getMapFactory().getMap(cs.getWBMap()).getMapName() + "에 '" + MapleLifeFactory.getMonster(cs.getWBMob()).getName() + "'(이)가 출몰하였습니다."));
                            }
                        }
                    }
                }
            }, 1000 * 60 * set, schedulewait);
        }
    }

    public static transient ScheduledFuture<?> worldReward;

    public static void WorldReward(int set) {
        if (worldReward == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 0);
            long time = cal.getTimeInMillis();
            long schedulewait = 0;
            if (time > System.currentTimeMillis()) {
                schedulewait = time - System.currentTimeMillis();
            } else {
                while (cal.getTimeInMillis() < System.currentTimeMillis()) {
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                }
                schedulewait = cal.getTimeInMillis() - System.currentTimeMillis();
            }
            worldReward = Timer.WorldTimer.getInstance().register(new Runnable() {
                public void run() {
                    Date date = new Date();
                    if (date.getHours() == 21) {
                        for (ChannelServer ch : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                                DueyHandler.addNewItemToDb(2431156, 1, chr.getId(), ServerConstants.server_Name_Source, "", true);
                                chr.getClient().sendPacket(CField.receiveParcel(ServerConstants.server_Name_Source, true));
                                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "핫 타임 보상으로 [" + ii.getName(2431156) + "] " + "1개를 획득하였습니다."));
                            }
                        }
                    }
                    if (date.getHours() == 0 || date.getHours() == 24) {
                        resetDailyQuest();
                        for (ChannelServer ch : ChannelServer.getAllInstances()) {
                            for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                                for (int i = 34129; i < 34150; i++) {
                                    if (chr.getQuestStatus(i) != 0) {
                                        MapleQuest v1 = MapleQuest.getInstance(i);
                                        v1.forfeit(chr);
                                        String say = "" + v1.getName() + " 퀘스트가 초기화되었습니다.";
                                        chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, say));
                                    }
                                }
                            }
                        }
                    }
                }
            }, 1000 * 60 * set, schedulewait);
        }
    }

    public static void resetDailyQuest() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("DELETE FROM queststatus WHERE quest > 34128 AND quest < 34150");
            ps.execute();
            System.out.println("[Reset] ArcaneDayByDayQuest");
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
