package client.messages.commands;

import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import database.DatabaseConnection;

import handling.channel.ChannelServer;
import handling.world.World;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import server.ShutdownServer;
import server.Timer.EventTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import tools.CPUSampler;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    /*    public static class StripEveryone extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     ChannelServer cs = c.getChannelServer();
     for (MapleCharacter mchr : cs.getPlayerStorage().getAllCharacters()) {
     if (mchr.isGM()) {
     continue;
     }
     MapleInventory equipped = mchr.getInventory(MapleInventoryType.EQUIPPED);
     MapleInventory equip = mchr.getInventory(MapleInventoryType.EQUIP);
     List<Short> ids = new ArrayList<Short>();
     for (Item item : equipped.newList()) {
     ids.add(item.getPosition());
     }
     for (short id : ids) {
     MapleInventoryManipulator.unequip(mchr.getClient(), id, equip.getNextFreeSlot());
     }
     }
     return 1;
     }
     }*/

    public static class 고정엔피시 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "!고정엔피시 <엔피시 ID>");
                return 0;
            }
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                npc.setCustom(true);
                Connection con = null;
                try {
                    con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0); // 1 = right , 0 = left
                        ps.setInt(3, 0); // 1 = hide, 0 = show
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "n");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "NPC를 DB에 저장하지 못했습니다.");
                } finally {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                }
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true, -1));
                c.getPlayer().dropMessage(6, "맵을 리로드하면 다음 서버 재시작 전까지 NPC가 사라질 수 있습니다.");
            } else {
                c.getPlayer().dropMessage(6, "유효하지 않은 NPC ID입니다.");
                return 0;
            }
            return 1;
        }
    }

    public static class 고정몬스터 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "!고정몬스터 <몬스터 ID> <리젠 시간>");
                return 0;
            }
            int mobid = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            MapleMonster npc;
            try {
                npc = MapleLifeFactory.getMonster(mobid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "에러: " + e.getMessage());
                return 0;
            }
            if (npc != null) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                Connection con = null;
                try {
                    con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid, mobtime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, mobid);
                        ps.setInt(2, 0); // 1 = right , 0 = left
                        ps.setInt(3, 0); // 1 = hide, 0 = show
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "m");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.setInt(12, mobTime);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "몬스터 스폰 정보를 DB에 저장하지 못했습니다.");
                } finally {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                }
                c.getPlayer().getMap().addMonsterSpawn(npc, mobTime, (byte) -1, null);
                c.getPlayer().dropMessage(6, "맵을 리로드하면 다음 서버 재시작 전까지 몬스터가 사라질 수 있습니다.");
            } else {
                c.getPlayer().dropMessage(6, "유효하지 않은 몬스터 ID입니다.");
                return 0;
            }
            return 1;
        }
    }

    public static class 모두메소 extends CommandExecute {

         @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Integer.parseInt(splitted[1]), true);
            }
        }
            return 1;
        }
     }

    public static class 경험치배율 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("모두")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                } else {
                    c.getChannelServer().setExpRate(rate);
                }
                c.getPlayer().dropMessage(6, "경험치 배율이 " + rate + "배로 변경되었습니다.");
            } else {
                c.getPlayer().dropMessage(6, "사용법: !경험치배율 <배율> [모두], 현재 경험치 배율: " + c.getChannelServer().getExpRate() + "배");
            }
            return 1;
        }
    }

    public static class 메소배율 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("모두")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                } else {
                    c.getChannelServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "메소 배율이 " + rate + "배로 변경되었습니다.");
            } else {
                c.getPlayer().dropMessage(6, "사용법: !메소배율 <배율> [모두], 현재 메소 배율: " + c.getChannelServer().getMesoRate() + "배");
            }
            return 1;
        }
    }

    public static class 전부팅 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int range = -1;
            if (splitted[1].equals("m")) { // 현재 맵
                range = 0;
            } else if (splitted[1].equals("c")) { // 현재 채널(기본값)
                range = 1;
            } else if (splitted[1].equals("w")) { // 월드 전체
                range = 2;
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(true);
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
            }
            return 1;
        }
    }

    public static class Shutdown extends CommandExecute {

        protected static Thread t = null;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            /*c.getPlayer().dropMessage(6, "Shutting down...");
             if (t == null || !t.isAlive()) {
             t = new Thread(ShutdownServer.getInstance());
             ShutdownServer.getInstance().shutdown();
             t.start();
             } else {
             c.getPlayer().dropMessage(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
             }*/
            c.getPlayer().dropMessage(5, "!서버종료시간 <분> 명령어를 사용해 주세요.");
            return 1;
        }
    }

    public static class 서버종료시간 extends Shutdown {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "서버 종료까지 " + minutesLeft + " 분 남았습니다.");
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "서버가 " + minutesLeft + " 분 후 종료됩니다. 안전한 위치에서 로그아웃해 주세요."));
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {

                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().shutdown();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        if (minutesLeft <= 10) {
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "서버가 " + minutesLeft + " 분 후 종료됩니다. 안전한 위치에서 로그아웃해 주세요."));
                        } else if (minutesLeft <= 60 && minutesLeft % 5 == 0) {
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "서버가 " + minutesLeft + " 분 후 종료됩니다. 안전한 위치에서 로그아웃해 주세요."));
                        } else if (minutesLeft % 30 == 0) {
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "서버가 " + minutesLeft + " 분 후 종료됩니다. 안전한 위치에서 로그아웃해 주세요."));
                        }
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, "이미 종료 예약이 진행 중입니다. 잠시 후 다시 시도해 주세요.");
            }
            return 1;
        }
    }

    public static class 프로파일링시작 extends CommandExecute { // 서버 렉/병목 분석

        @Override
        public int execute(MapleClient c, String[] splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
             sampler.addIncluded("client");
             sampler.addIncluded("constants"); //or should we do Packages.constants etc.?
             sampler.addIncluded("database");
             sampler.addIncluded("handling");
             sampler.addIncluded("provider");
             sampler.addIncluded("scripting");
             sampler.addIncluded("server");
             sampler.addIncluded("tools");
             sampler.start();

            return 1;
        }
    }

    public static class 프로파일링종료 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "같은 이름의 파일이 이미 있습니다. 다른 파일명을 입력해 주세요.");
                    return 0;
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            } catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return 1;
        }
    }

    public static class 맥스스탯 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getStat().setDex((short) 32767, c.getPlayer());
            c.getPlayer().getStat().setInt((short) 32767, c.getPlayer());
            c.getPlayer().getStat().setLuk((short) 32767, c.getPlayer());
            c.getPlayer().getStat().setStr((short) 32767, c.getPlayer());
            c.getPlayer().getStat().setMaxHp(99999, c.getPlayer());
            c.getPlayer().getStat().setHp(99999, c.getPlayer());
            c.getPlayer().getStat().setMaxMp(99999, c.getPlayer());
            c.getPlayer().getStat().setMp(99999, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.Str, 32767);
            c.getPlayer().updateSingleStat(MapleStat.Dex, 32767);
            c.getPlayer().updateSingleStat(MapleStat.Int, 32767);
            c.getPlayer().updateSingleStat(MapleStat.Luk, 32767);
            c.getPlayer().updateSingleStat(MapleStat.MaxHP, 99999);
            c.getPlayer().updateSingleStat(MapleStat.HP, 99999);
            c.getPlayer().updateSingleStat(MapleStat.MaxMP, 99999);
            c.getPlayer().updateSingleStat(MapleStat.MP, 99999);
            return 1;
        }
    }

    public static class 스탯초기화 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getStat().setStr((short) 100, c.getPlayer());
            c.getPlayer().getStat().setDex((short) 100, c.getPlayer());
            c.getPlayer().getStat().setInt((short) 100, c.getPlayer());
            c.getPlayer().getStat().setLuk((short) 100, c.getPlayer());
            c.getPlayer().getStat().setMaxHp(10000, c.getPlayer());
            c.getPlayer().getStat().setHp(10000, c.getPlayer());
            c.getPlayer().getStat().setMaxMp(10000, c.getPlayer());
            c.getPlayer().getStat().setMp(10000, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.Str, 100);
            c.getPlayer().updateSingleStat(MapleStat.Dex, 100);
            c.getPlayer().updateSingleStat(MapleStat.Int, 100);
            c.getPlayer().updateSingleStat(MapleStat.Luk, 100);
            c.getPlayer().updateSingleStat(MapleStat.MaxHP, 10000);
            c.getPlayer().updateSingleStat(MapleStat.MaxMP, 10000);
            c.getPlayer().updateSingleStat(MapleStat.HP, 10000);
            c.getPlayer().updateSingleStat(MapleStat.MP, 10000);
            return 1;
        }
    }
}
