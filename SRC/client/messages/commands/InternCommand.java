package client.messages.commands;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.anticheat.ReportType;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.ChatType;
import handling.channel.ChannelServer;
import handling.channel.handler.DueyHandler;
import handling.world.CheaterData;
import handling.world.World;
import java.awt.Point;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import provider.MapleDataTool;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.ItemInformation;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleSquad;
import server.MapleSquad.MapleSquadType;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.quest.MapleQuest;
import tools.packet.CField;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class InternCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.INTERN;
    }

    public static class 하이드 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());

            return 0;
        }
    }

    public static class 하이드해제 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dispelSkill(9001004);

            return 0;
        }
    }

    /*    public static class LowHP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getStat().setHp((short) 1, c.getPlayer());
            c.getPlayer().getStat().setMp((short) 1, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return 0;
        }
    }*/
    public static class ㅎ extends 힐 {
    }

    public static class 힐 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getStat().heal(c.getPlayer());
            c.getPlayer().dispelDebuffs();
            return 0;
        }
    }

    public static class 힐맵 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            for (MapleCharacter mch : player.getMap().getCharacters()) {
                if (mch != null) {
                    mch.getStat().setHp(mch.getStat().getMaxHp(), mch);
                    mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
                    mch.getStat().setMp(mch.getStat().getMaxMp(), mch);
                    mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
                    mch.dispelDebuffs();
                }
            }
            return 1;
        }
    }

    public static class 정지 extends 밴 {
    }

    public static class 밴 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final MapleCharacter victim = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 20251231);
            victim.tempban(victim, cal);
            c.getPlayer().dropMessage(6, "성공적으로 " + victim.getName() + "(을)를 영구 정지하였습니다.");
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "(영구 정지) " + victim.getName() + "(이)가 영구 정지 처리되었습니다."));
            return 1;
        }
    }

    public static class 채널변경 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().changeChannel(Integer.parseInt(splitted[1])); // !채널변경 <숫자>
            return 1;
        }
    }

    public static class 채널변경유저 extends CommandExecute {
        // !채널변경유저 <캐릭터명> : 해당 캐릭터가 있는 채널로 이동

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().changeChannel(World.Find.findChannel(splitted[1]));
            return 1;
        }
    }

    public static class 접속종료 extends CommandExecute {
        //  !접속종료 <캐릭터명>

        @Override
        public int execute(MapleClient c, String[] splitted) {

            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                if (ch.getPlayerStorage().getCharacterByName(splitted[1]) != null) {
                    ch.getPlayerStorage().getCharacterByName(splitted[1]).getClient().getSession().close();
                    ch.getPlayerStorage().getCharacterByName(splitted[1]).getClient().disconnect(true, true);
                }
            }
            return 0;
        }
    }

    public static class 킬 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "사용법: !킬 <캐릭터명>");
                return 0;
            }
            MapleCharacter victim = null;
            for (int i = 1; i < splitted.length; i++) {
                try {
                    victim = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, splitted[i] + " 님을 찾을 수 없습니다.");
                }
                if (player.allowedToTarget(victim) && player.getGMLevel() >= victim.getGMLevel()) {
                    victim.getStat().setHp((short) 0, victim);
                    victim.getStat().setMp((short) 0, victim);
                    victim.updateSingleStat(MapleStat.HP, 0);
                    victim.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
    }

    public static class 현재맵 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "현재 맵 : " + c.getPlayer().getMap().getId()
                    + " 좌표 : " + c.getPlayer().getPosition()
                    + " 발판 : " + c.getPlayer().getFH());
            return 1;
        }
    }

    public static class 인벤초기화 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Pair<Short, Short>, MapleInventoryType> eqs = new HashMap<>();
            try {
                if (splitted[1].equals("전부")) {
                    for (MapleInventoryType type : MapleInventoryType.values()) {
                        for (Item item : c.getPlayer().getInventory(type)) {
                            eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), type);
                        }
                    }
                } else if (splitted[1].equals("장착장비")) {
                    for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                    }
                } else if (splitted[1].equals("장비")) {
                    for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIP);
                    }
                } else if (splitted[1].equals("소비")) {
                    for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.USE);
                    }
                } else if (splitted[1].equals("설치")) {
                    for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.SETUP);
                    }
                } else if (splitted[1].equals("기타")) {
                    for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.ETC);
                    }
                } else if (splitted[1].equals("캐시")) {
                    for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.CASH);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "사용법 : !인벤토리초기화 <전부, 장착장비, 장비, 소비, 설치, 기타, 캐시>");
                    c.getPlayer().dropMessage(6, "경고 : 선택한 탭의 아이템이 모두 삭제됩니다. 이는 되돌릴 수 없습니다.");
                }
            } catch (ArrayIndexOutOfBoundsException aioob) {
                c.getPlayer().dropMessage(6, "사용법 : !인벤토리초기화 <전부, 장착장비, 장비, 소비, 설치, 기타, 캐시>");
                c.getPlayer().dropMessage(6, "경고 : 선택한 탭의 아이템이 모두 삭제됩니다. 이는 되돌릴 수 없습니다.");
            }
            for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            return 1;
        }
    }

    public static class 온라인 extends CommandExecute {
        // !온라인 채널번호 : 해당 채널에 접속 중인 모든 캐릭터의 레벨, 이름, 현재 있는 맵을 보여줌

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //c.getPlayer().dropMessage(6, "Characters connected to channel " + c.getChannel() + ":");
            //c.getPlayer().dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
            int nCH = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(5, "* 0" + nCH + " 채널 : ");
            for (MapleCharacter nUser : ChannelServer.getInstance(nCH).getPlayerStorage().getAllCharacters()) {
                if (nUser != null) {
                    String nSay = "- (Lv." + nUser.getLevel() + ") " + nUser.getName() + " : " + nUser.getMap().getMapName() + "(" + nUser.getMapId() + ")";
                    c.getPlayer().getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, nSay));
                }
            }
            return 1;
        }
    }

    public static class 채널동접2 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, Integer.parseInt(splitted[1]) + "채널에 접속 중인 캐릭터 : ");
            c.getPlayer().dropMessage(6, ChannelServer.getInstance(Integer.parseInt(splitted[1])).getPlayerStorage().getOnlinePlayers(true));
            return 1;
        }
    }

    public static class 전체동접2 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int channels = ChannelServer.getChannelCount();
            for (int i = 1; i <= channels; i++) {
                c.getPlayer().dropMessage(6, "채널 " + i + ": " + ChannelServer.getInstance(i).getPlayerStorage().getOnlinePlayers(true));
            }
            return 1;
        }
    }

    public static class 후원코인소지 extends CommandExecute {
        // 모든 접속자의 후원코인 보유량 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().dropMessage(5, chr.getName() + " : " + chr.itemQuantity(4310247) + "개");
                }
            }
            return 1;
        }
    }

    public static class 홍보코인소지 extends CommandExecute {
        // 모든 접속자의 홍보코인 보유량 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().dropMessage(5, chr.getName() + " : " + chr.itemQuantity(4310226) + "개");
                }
            }
            return 1;
        }
    }

    public static class 메소소지 extends CommandExecute {
        // 모든 접속자의 메소 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().dropMessage(5, chr.getName() + " : " + chr.getNum(chr.getMeso()) + "메소");
                }
            }
            return 1;
        }
    }

    public static class 아이템확인 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3 || splitted[1] == null || splitted[1].equals("") || splitted[2] == null || splitted[2].equals("")) {
                c.getPlayer().dropMessage(6, "사용법 : !아이템확인 <캐릭터명> <아이템 ID>");
                return 0;
            } else {
                int item = Integer.parseInt(splitted[2]);
                MapleCharacter chr = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
                int itemamount = chr.getItemQuantity(item, true);
                if (itemamount > 0) {
                    c.getPlayer().dropMessage(6, chr.getName() + " 님이 (" + item + ") 아이템을 " + itemamount + "개 보유하고 있습니다.");
                } else {
                    c.getPlayer().dropMessage(6, chr.getName() + " 님이 (" + item + ") 아이템을 보유하고 있지 않습니다.");
                }
            }
            return 1;
        }
    }

    public static class 음악 extends CommandExecute {
        // !음악 <BGM 경로>

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
            return 1;
        }
    }

    public static class Broadcast extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Command syntax: \"!broadcast <Type#> <message>\"");
            } else {
                c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(Integer.parseInt(splitted[1]), StringUtil.joinStringFrom(splitted, 2)));
            }

            return 1;
        }
    }

    public static class GM extends CommandExecute {
        // !GM <메세지 내용> : 서버에 있는 GM 등급 2 이상인 운영자들에게만 메시지를 보냄. 일반 유저에게는 안 보임

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String sender = c.getPlayer().getName();
            String message = sender + "[채널" + c.getChannel() + "] : " + StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGMLevel() >= 2) {
                        chr.dropMessage(5, message);
                    }
                }
            }
            return 1;
        }
    }

    public static class PermWeather extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted[1].equals("id")) {
                c.getPlayer().dropMessage(5, "The current weather effect id is: " + c.getPlayer().getMap().getPermanentWeather());
                return 1;
            }
            if (splitted[1].equals("on") || splitted[1].equals("enable")) {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 2, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    //Weather IDs start with 512xxxx.
                    c.getPlayer().dropMessage(5, "Invalid Weather ID. (512xxxx)");
                } else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(CField.floatNotice("", weather, false));
                    c.getPlayer().dropMessage(5, "Map weather has been enabled. ID: " + weather);
                }
            } else if (splitted[1].equals("off") || splitted[1].equals("disable")) {
                int weather = c.getPlayer().getMap().getPermanentWeather();
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(CField.floatNotice(null, 0, false));
                c.getPlayer().dropMessage(5, "Map weather has been disabled. Prev ID: " + weather);
            } else {
                c.getPlayer().dropMessage(5, "Incorrect syntax: !permweather <on/off> <Weather ID (Default 5120000)>");
            }

            return 1;
        }
    }

    public static class 캐릭터정보 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final StringBuilder builder = new StringBuilder();
            final MapleCharacter other = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            if (other == null) {
                builder.append("존재하지 않는 캐릭터입니다.");
                c.getPlayer().dropMessage(6, builder.toString());
                return 0;
            }
            builder.append(MapleClient.getLogMessage(other, ""));
            builder.append(" 좌표 : ").append(other.getPosition().x);
            builder.append(" /").append(other.getPosition().y);

            builder.append(" || HP : ");
            builder.append(other.getStat().getHp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxHp());

            builder.append(" || MP : ");
            builder.append(other.getStat().getMp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxMp(other.getJob()));

            builder.append(" || 배틀쉽 HP : ");
            builder.append(other.currentBattleshipHP());

            builder.append(" || 물공 : ");
            builder.append(other.getStat().getTotalWatk());
            builder.append(" || 마공 : ");
            builder.append(other.getStat().getTotalMagic());
            builder.append(" || 최대데미지 : ");
            builder.append(other.getStat().getCurrentMaxBaseDamage());
            builder.append(" || 데미지% : ");
            builder.append(other.getStat().dam_r);
            builder.append(" || 보공% : ");
            builder.append(other.getStat().bossdam_r);
            builder.append(" || 크리티컬 확률 : ");
            builder.append(other.getStat().passive_sharpeye_rate());
            builder.append(" || 크리티컬 테미지 : ");
            builder.append(other.getStat().passive_sharpeye_percent());

            builder.append(" || STR : ");
            builder.append(other.getStat().getStr());
            builder.append(" || DEX : ");
            builder.append(other.getStat().getDex());
            builder.append(" || INT : ");
            builder.append(other.getStat().getInt());
            builder.append(" || LUK : ");
            builder.append(other.getStat().getLuk());

            builder.append(" || 합산 STR : ");
            builder.append(other.getStat().getTotalStr());
            builder.append(" || 합산 DEX : ");
            builder.append(other.getStat().getTotalDex());
            builder.append(" || 합산 INT : ");
            builder.append(other.getStat().getTotalInt());
            builder.append(" || 합산 LUK : ");
            builder.append(other.getStat().getTotalLuk());

            builder.append(" || 경험치 : ");
            builder.append(other.getExp());
            builder.append(" || 메소 : ");
            builder.append(other.getMeso());

            builder.append(" || 파티 여부 : ");
            builder.append(other.getParty() == null ? -1 : other.getParty().getId());

            builder.append(" || 거래 여부 : ");
            builder.append(other.getTrade() != null);
            builder.append(" || Latency : ");
            builder.append(other.getClient().getLatency());
            builder.append(" || 핑 : ");
            builder.append(other.getClient().getLastPing());
            builder.append(" || 퐁 : ");
            builder.append(other.getClient().getLastPong());
            builder.append(" || 접속 주소 : ");

            other.getClient().DebugMessage(builder);

            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
    }

    public static class 신고기록 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            List<CheaterData> cheaters = World.getReports();
            int cheaterSize = cheaters.size();
            if (cheaterSize <= 0) {
                c.getPlayer().dropMessage(6, "현재 신고 내역이 없습니다.");
            }
            for (int x = cheaterSize - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
    }

    public static class 신고기록삭제 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                StringBuilder ret = new StringBuilder("report [ign] [all/");
                for (ReportType type : ReportType.values()) {
                    ret.append(type.theId).append('/');
                }
                ret.setLength(ret.length() - 1);
                c.getPlayer().dropMessage(6, ret.append(']').toString());
                return 0;
            }
            final MapleCharacter victim = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "존재하지 않는 캐릭터입니다.");
                return 0;
            }
            final ReportType type = ReportType.getByString(splitted[2]);
            if (type != null) {
                victim.clearReports(type);
            } else {
                victim.clearReports();
            }
            c.getPlayer().dropMessage(5, "Done.");
            return 1;
        }
    }

    public static class Cheaters extends CommandExecute {
        // 핵 사용 의심자 목록 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
    }

    public static class Connected extends CommandExecute {
        // 채널별 접속자 수 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Integer, Integer> connected = World.getConnected();
            StringBuilder conStr = new StringBuilder("Connected Clients: ");
            boolean first = true;
            for (int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("Total: ");
                    conStr.append(connected.get(i));
                } else {
                    conStr.append("Channel");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return 1;
        }
    }

    public static class 근처포탈 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(6, portal.getName() + " ID : " + portal.getId() + " 스크립트 : " + portal.getScriptName());

            return 1;
        }
    }

    public static class 스폰디버그 extends CommandExecute {
        // 현재 맵의 몬스터 스폰 관련 디버그 정보를 보여줌

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return 1;
        }
    }

    public static class 가짜재접속 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().fakeRelog();
            return 1;
        }
    }

    public static class ClearDrops extends 청소 {

    }

    public static class 청소 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "정리된 아이템 : " + c.getPlayer().getMap().getNumItems() + " 개");
            c.getPlayer().getMap().removeDrops();
            return 1;
        }
    }

    public static class 원정대목록 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (Entry<MapleSquad.MapleSquadType, MapleSquad> squads : c.getChannelServer().getAllSquads().entrySet()) {
                c.getPlayer().dropMessage(5, "유형 : " + squads.getKey().name() + ", 리더 : " + squads.getValue().getLeader().getName() + ", 상태 : " + squads.getValue().getStatus() + ", 인원 : " + squads.getValue().getSquadSize() + ", 추방 인원 : " + squads.getValue().getBannedMemberSize());
            }
            return 0;
        }
    }

    public static class ListInstances extends CommandExecute {
        // 이벤트 인스턴스 목록 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(StringUtil.joinStringFrom(splitted, 1));
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", charSize: " + eim.getPlayers().size() + ", dcedSize: " + eim.getDisconnected().size() + ", mobSize: " + eim.getMobs().size() + ", eventManager: " + em.getName() + ", timeLeft: " + eim.getTimeLeft() + ", iprops: " + eim.getProperties().toString() + ", eprops: " + em.getProperties().toString());
                }
            }
            return 0;
        }
    }

    public static class Uptime extends CommandExecute {
        // 서버 가동 시간 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return 1;
        }
    }

    public static class EventInstance extends CommandExecute {
        // 내가 참여 중인 이벤트 정보 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                EventInstanceManager eim = c.getPlayer().getEventInstance();
                c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", charSize: " + eim.getPlayers().size() + ", dcedSize: " + eim.getDisconnected().size() + ", mobSize: " + eim.getMobs().size() + ", eventManager: " + eim.getEventManager().getName() + ", timeLeft: " + eim.getTimeLeft() + ", iprops: " + eim.getProperties().toString() + ", eprops: " + eim.getEventManager().getProperties().toString());
            }
            return 1;
        }
    }

    public static class 마을 extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();

        static {
            gotomaps.put("지엠", 180000000);
            gotomaps.put("사우스페리", 2000000);
            gotomaps.put("암허스트", 1010000);
            gotomaps.put("헤네시스", 100000000);
            gotomaps.put("헤네", 100000000);
            gotomaps.put("엘리니아", 101000000);
            gotomaps.put("페리온", 102000000);
            gotomaps.put("커닝시티", 103000000);
            gotomaps.put("항구", 104000000);
            gotomaps.put("슬리피우드", 105000000);
            gotomaps.put("플로니아", 120000300);
            gotomaps.put("오르비스", 200000000);
            gotomaps.put("빌리지", 209000000);
            gotomaps.put("엘나스", 211000000);
            gotomaps.put("루디브리엄", 220000000);
            gotomaps.put("아쿠아리움", 230000000);
            gotomaps.put("리프레", 240000000);
            gotomaps.put("무릉", 250000000);
            gotomaps.put("백초마을", 251000000);
            gotomaps.put("지구방위본부", 221000000);
            gotomaps.put("아랫마을", 222000000);
            gotomaps.put("뉴리프시티", 600000000);
            gotomaps.put("샤레니안", 990000000);
            gotomaps.put("피아누스", 230040420);
            gotomaps.put("혼테일", 240060200);
            gotomaps.put("카오스혼테일", 240060201);
            gotomaps.put("그리프", 240020101);
            gotomaps.put("마뇽", 240020401);
            gotomaps.put("자쿰", 280030000);
            gotomaps.put("카오스자쿰", 280030001);
            gotomaps.put("파풀라투스", 220080001);
            gotomaps.put("쇼와타운", 801000000);
            gotomaps.put("지팡구", 800000000);
            gotomaps.put("아리안트", 260000100);
            gotomaps.put("노틸러스", 120000000);
            gotomaps.put("보트키타운", 541000000);
            gotomaps.put("말레이시아", 550000000);
            gotomaps.put("에레브", 130000000);
            gotomaps.put("엘린숲", 300000000);
            gotomaps.put("캄풍", 551000000);
            gotomaps.put("싱가포르", 540000000);
            gotomaps.put("웨딩빌리지", 680000000);
            gotomaps.put("시간의신전", 270000000);
            gotomaps.put("핑크빈", 270050100);
            gotomaps.put("자유시장", 910000000);
            gotomaps.put("퀴즈", 109020001);
            gotomaps.put("올라", 109030101);
            gotomaps.put("피트니스", 109040000);
            gotomaps.put("스노우볼", 109060000);
            gotomaps.put("골든", 950100000);
            gotomaps.put("팬텀", 610010000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("리엔", 140000000);
            gotomaps.put("에델슈타인", 310000000);
            gotomaps.put("ardent", 910001000);
            gotomaps.put("강화", 910001000);
            gotomaps.put("pvp", 960000000);
            gotomaps.put("미래", 271000000);
            gotomaps.put("lhc", 211060000);
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "사용법: !마을 <마을 이름>");
            } else {
                if (gotomaps.containsKey(splitted[1])) {
                    MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                        return 0;
                    }
                    MaplePortal targetPortal = target.getPortal(0);
                    c.getPlayer().changeMap(target, targetPortal);
                } else {
                    if (splitted[1].equals("목록")) {
                        c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                        StringBuilder sb = new StringBuilder();
                        for (String s : gotomaps.keySet()) {
                            sb.append(s).append(", ");
                        }
                        c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
                    } else {
                        c.getPlayer().dropMessage(6, "Invalid command syntax - Use !goto <location>. For a list of locations, use !goto locations.");
                    }
                }
            }
            return 1;
        }
    }

    public static class 몬스터디버그 extends CommandExecute {
        // 현재 맵(또는 지정한 맵)에 있는 모든 몬스터의 상세 디버그 정보를 보여줌

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                //&& !splitted[0].equals("!killmonster") && !splitted[0].equals("!hitmonster") && !splitted[0].equals("!hitmonsterbyoid") && !splitted[0].equals("!killmonsterbyoid")) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                return 0;
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                c.getPlayer().dropMessage(6, "몬스터 " + mob.toString());
            }
            return 1;
        }
    }

    public static class 룰렛 extends CommandExecute {
        // 1부터 입력한 숫자 사이에서 랜덤으로 숫자 하나를 뽑아 보여

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "[룰렛] 숫자 : " + Randomizer.rand(1, Integer.parseInt(splitted[1])));
            return 0;
        }
    }

    /*public static class BEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().getClient().getChannelServer().eventOn) {
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    cs.eventOn = true;
                    cs.eventMap = c.getPlayer().getMapId();
                    cs.eventChannel = (byte) c.getPlayer().getClient().getChannel();
                }
                try {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, c.getChannel(), "[Event] " + StringUtil.joinStringFrom(splitted, 1) + " - Type @bevent on channel " + c.getChannel() + " to join."));
                } catch (NumberFormatException nfe) {
                    // c.getChannelServer().reconnectWorld();
                }
            } else {
                for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    cs.eventOn = false;
                    cs.eventMap = 0;
                }
                try {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, c.getChannel(), "[Event] Access to the event has ended since the time finished."));
                } catch (NumberFormatException nfe) {
                    // c.getChannelServer().reconnectWorld();
                }
                return 0;
            }
            return 0;
        }
    }*/
    public static class 엔피시확인 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                MapleNPC reactor2l = (MapleNPC) reactor1l;
                c.getPlayer().dropMessage(5, "엔피시 : 오브젝트 ID: " + reactor2l.getObjectId() + " 엔피시 ID: " + reactor2l.getId() + " 좌표 : " + reactor2l.getPosition().toString() + " 이름 : " + reactor2l.getName());
            }
            return 0;
        }
    }

    public static class 리엑터확인 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                MapleReactor reactor2l = (MapleReactor) reactor1l;
                c.getPlayer().dropMessage(5, "리엑터 : 오브젝트 ID: " + reactor2l.getObjectId() + " 리엑터 ID: " + reactor2l.getReactorId() + " 좌표 : " + reactor2l.getPosition().toString() + " 상태 : " + reactor2l.getState() + " 이름 : " + reactor2l.getName());
            }
            return 0;
        }
    }

    public static class 포탈확인 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "포탈 : ID: " + portal.getId() + " 스크립트 : " + portal.getScriptName() + " 이름 : " + portal.getName() + " 좌표 : " + portal.getPosition().x + "," + portal.getPosition().y + " 연결되는 맵 : " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return 0;
        }
    }

    public static class 좌표 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "맵 : " + c.getPlayer().getMapId() + " (" + c.getPlayer().getMap().getMapName() + ") | X : " + pos.x + " | Y : " + pos.y + " | 우측범위 : " + (pos.x + 50) + " | 좌측범위 : " + (pos.x - 50) + " | 발판 : " + c.getPlayer().getFH());
            return 1;
        }
    }

    public static class 시계 extends CommandExecute {
        // !시계 초 (기본값 60초) : 맵에 시계(타이머) 표시

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().broadcastMessage(CField.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return 1;
        }
    }

    public static class 소환 extends CommandExecute {
        // !소환 캐릭터명 : 특정 플레이어를 내 위치로 소환

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (c.getPlayer().inPVP() || (!c.getPlayer().isGM() && (victim.isInBlockedMap() || victim.isGM()))) {
                    c.getPlayer().dropMessage(5, "나중에 다시 시도하세요.");
                    return 0;
                }
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition()));
            } else {
                int ch = World.Find.findChannel(splitted[1]);
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "캐릭터를 찾을 수 없습니다.");
                    return 0;
                }
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                if (victim == null || victim.inPVP() || (!c.getPlayer().isGM() && (victim.isInBlockedMap() || victim.isGM()))) {
                    c.getPlayer().dropMessage(5, "나중에 다시 시도하세요.");
                    return 0;
                }
                c.getPlayer().dropMessage(5, "대상 캐릭터의 채널을 변경 중입니다.");
                victim.dropMessage(5, "Cross changing channel.");
                if (victim.getMapId() != c.getPlayer().getMapId()) {
                    final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                    victim.changeMap(mapp, mapp.findClosestPortal(c.getPlayer().getTruePosition()));
                }
                victim.changeChannel(c.getChannel());
            }
            return 1;
        }
    }

    public static class 맵 extends CommandExecute {
        // !맵 캐릭터명 : 그 캐릭터가 있는 맵으로 내가 이동
        // !맵 캐릭터명 맵ID : 그 캐릭터를 지정한 맵으로 보내기
        // !맵 맵ID : 나를 해당 맵으로 이동
        // !맵 캐릭터명 맵ID 포탈번호 : 특정 포탈로 보내기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel() && !victim.inPVP() && !c.getPlayer().inPVP()) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getTruePosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                        return 0;
                    }
                    MaplePortal targetPortal = null;
                    if (splitted.length > 3) {
                        try {
                            targetPortal = target.getPortal(Integer.parseInt(splitted[3]));
                        } catch (IndexOutOfBoundsException e) {
                            // noop, assume the gm didn't know how many portals there are
                            c.getPlayer().dropMessage(5, "Invalid portal selected.");
                        } catch (NumberFormatException a) {
                            // noop, assume that the gm is drunk
                        }
                    }
                    if (targetPortal == null) {
                        targetPortal = target.getPortal(0);
                    }
                    victim.changeMap(target, targetPortal);
                }
            } else {
                try {
                    victim = c.getPlayer();
                    int ch = World.Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        if (target == null) {
                            c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                            return 0;
                        }
                        MaplePortal targetPortal = null;
                        if (splitted.length > 2) {
                            try {
                                targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                            } catch (IndexOutOfBoundsException e) {
                                // noop, assume the gm didn't know how many portals there are
                                c.getPlayer().dropMessage(5, "Invalid portal selected.");
                            } catch (NumberFormatException a) {
                                // noop, assume that the gm is drunk
                            }
                        }
                        if (targetPortal == null) {
                            targetPortal = target.getPortal(0);
                        }
                        c.getPlayer().changeMap(target, targetPortal);
                    } else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                        c.getPlayer().dropMessage(6, "채널 변경 중입니다. 잠시 기다려주세요.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "Something went wrong " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }

    public static class Jail extends CommandExecute {
        // 감옥에 가두기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "jail [name] [minutes, 0 = forever]");
                return 0;
            }
            MapleCharacter victim = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            final int minutes = Math.max(0, Integer.parseInt(splitted[2]));
            if (c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                if (victim != null) {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(GameConstants.JAIL);
                    victim.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST)).setCustomData(String.valueOf(minutes * 60));
                    victim.changeMap(target, target.getPortal(0));
                    victim.getClient().getSession().write(CField.getClock(minutes * 60));
                    c.getPlayer().dropMessage(6, "You have jailed <" + victim.getName() + "> for " + minutes + " minutes.");
                } else {
                    c.getPlayer().dropMessage(6, "Please be on their channel.");
                    return 0;
                }
            } else {
                c.getPlayer().dropMessage(6, "That's a GM, foo!");
            }
            return 1;
        }
    }

    public static class ListAllSquads extends CommandExecute {
        // 모든 채널의 원정대 목록 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (Entry<MapleSquad.MapleSquadType, MapleSquad> squads : cserv.getAllSquads().entrySet()) {
                    c.getPlayer().dropMessage(5, "[Channel " + cserv.getChannel() + "] TYPE: " + squads.getKey().name() + ", Leader: " + squads.getValue().getLeader().getName() + ", status: " + squads.getValue().getStatus() + ", numMembers: " + squads.getValue().getSquadSize() + ", numBanned: " + squads.getValue().getBannedMemberSize());
                }
            }
            return 1;
        }
    }

    public static class Say extends CommandExecute {
        // 서버 전체 공지 메시지

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                if (!c.getPlayer().isGM()) {
                    sb.append("Intern ");
                }
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(c.getPlayer().isGM() ? 6 : 5, sb.toString()));
            } else {
                c.getPlayer().dropMessage(6, "Syntax: say <message>");
                return 0;
            }
            return 1;
        }
    }

    public static class Letter extends CommandExecute {
        // 바닥에 글자 아이템으로 글씨 쓰기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "syntax: !letter <color (green/red)> <word>");
                return 0;
            }
            int start, nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "Unknown color!");
                return 0;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<Integer>();
            splitString = splitString.toUpperCase();
            // System.out.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 32767);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 32767);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return 1;
        }
    }

    public static class 엔피시스크립트 extends CommandExecute {
        // !엔피시스크립트 NPC코드 : NPC 대화 스크립트 실행

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, Integer.parseInt(splitted[1]));
            return 0;
        }
    }

    public static class 찾기 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length == 1) {
                c.getPlayer().dropMessage(6, splitted[0] + ": <엔피시> <몹> <아이템> <맵> <스킬> <퀘스트>");
            } else if (splitted.length == 2) {
                c.getPlayer().dropMessage(6, "Provide something to search.");
            } else {
                String type = splitted[1];
                String search = StringUtil.joinStringFrom(splitted, 2);
                MapleData data = null;
                MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
                MapleDataProvider monsterProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Mob.wz"));
                c.getPlayer().dropMessage(6, "* 구분 : " + type + " | 검색어 : " + search);
                if (type.equalsIgnoreCase("엔피시")) {
                    List<String> retNpcs = new ArrayList<>();
                    data = dataProvider.getData("Npc.img");
                    List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData npcIdData : data.getChildren()) {
                        npcPairList.add(new Pair<Integer, String>(Integer.parseInt(npcIdData.getName()), MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> npcPair : npcPairList) {
                        if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            MapleNPC onenpc;
                            onenpc = MapleLifeFactory.getNPC(npcPair.getLeft());
                            if (onenpc == null) {
                                continue;
                            }
                            retNpcs.add("- " + npcPair.getRight() + " (" + npcPair.getLeft() + ")");
                        }
                    }
                    if (retNpcs != null && retNpcs.size() > 0) {
                        for (String singleRetNpc : retNpcs) {
                            c.getPlayer().dropMessage(5, singleRetNpc);
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "No NPC's Found");
                    }

                } else if (type.equalsIgnoreCase("맵")) {
                    List<String> retMaps = new ArrayList<>();
                    data = dataProvider.getData("Map.img");
                    List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mapAreaData : data.getChildren()) {
                        for (MapleData mapIdData : mapAreaData.getChildren()) {
                            mapPairList.add(new Pair<Integer, String>(Integer.parseInt(mapIdData.getName()), MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
                        }
                    }
                    for (Pair<Integer, String> mapPair : mapPairList) {
                        if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapPair.getLeft());
                            if (target == null) {
                                continue;
                            }
                            retMaps.add("- " + mapPair.getRight() + " (" + mapPair.getLeft() + ")");
                        }
                    }
                    if (retMaps != null && retMaps.size() > 0) {
                        for (String singleRetMap : retMaps) {
                            c.getPlayer().dropMessage(5, singleRetMap);
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "No Maps Found");
                    }
                } else if (type.equalsIgnoreCase("몹") || type.equalsIgnoreCase("몬스터")) {
                    List<String> retMobs = new ArrayList<>();
                    data = dataProvider.getData("Mob.img");
                    List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mobIdData : data.getChildren()) {
                        mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> mobPair : mobPairList) {
                        if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            MapleMonster onemob;
                            onemob = MapleLifeFactory.getMonster(mobPair.getLeft());
                            if (onemob == null) {
                                continue;
                            }
                            retMobs.add("- [Lv." + onemob.getStats().getLevel() + "] " + mobPair.getRight() + " (" + mobPair.getLeft() + ")");
                        }
                    }
                    if (retMobs != null && retMobs.size() > 0) {
                        for (String singleRetMob : retMobs) {
                            c.getPlayer().dropMessage(5, singleRetMob);
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "No Mobs Found");
                    }

                } else if (type.equalsIgnoreCase("아이템")) {
                    List<String> retItems = new ArrayList<>();
                    for (ItemInformation itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (itemPair != null && itemPair.name != null && itemPair.name.toLowerCase().contains(search.toLowerCase())) {
                            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            int lv = ii.getReqLevel(itemPair.itemId);
                            retItems.add("- [Lv. " + lv + "] " + itemPair.name + " (" + itemPair.itemId + ")");
                        }
                    }
                    if (retItems != null && retItems.size() > 0) {
                        for (String singleRetItem : retItems) {
                            c.getPlayer().dropMessage(5, singleRetItem);
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "No Items Found");
                    }
                } else if (type.equalsIgnoreCase("퀘스트")) {
                    List<String> retItems = new ArrayList<>();
                    for (MapleQuest itemPair : MapleQuest.getAllInstances()) {
                        if (itemPair.getName().length() > 0 && itemPair.getName().toLowerCase().contains(search.toLowerCase())) {
                            retItems.add("- " + itemPair.getName() + " (" + itemPair.getId() + ")");
                        }
                    }
                    if (retItems != null && retItems.size() > 0) {
                        for (String singleRetItem : retItems) {
                            c.getPlayer().dropMessage(5, singleRetItem);
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "No Quests Found");
                    }
                } else if (type.equalsIgnoreCase("스킬")) {
                    List<String> retSkills = new ArrayList<>();
                    for (Skill skil : SkillFactory.getAllSkills()) {
                        if (skil.getName() != null && skil.getName().toLowerCase().contains(search.toLowerCase())) {
                            retSkills.add("- " + skil.getName() + " (" + skil.getId() + ")");
                        }
                    }
                    if (retSkills != null && retSkills.size() > 0) {
                        for (String singleRetSkill : retSkills) {
                            c.getPlayer().dropMessage(5, singleRetSkill);
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "No Skills Found");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "Sorry, that search call is unavailable");
                }
            }
            return 0;
        }
    }

    public static class 검색 extends 찾기 {
    }

    public static class 코드 extends 찾기 {
    }

    public static class 코드보기 extends 찾기 {
    }

    public static class 지급 extends CommandExecute {
        // !지급 캐릭터명 아이템코드 수량 : 특정 플레이어에게 아이템 지급 (택배)

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String userName = splitted[1];
            int itemID = Integer.parseInt(splitted[2]);
            int itemQr = Integer.parseInt(splitted[3]);
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                MapleCharacter user = ch.getPlayerStorage().getCharacterByName(userName);
                if (user != null) {
                    DueyHandler.addNewItemToDb(itemID, itemQr, user.getId(), ServerConstants.server_Name_Source, "", true);
                    user.getClient().getSession().write(CField.receiveParcel(ServerConstants.server_Name_Source, true));
                }
            }
            return 0;
        }
    }
    
    public static class 오픈핫타임 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    if (chr.getLevel() > 9) {
                        String nStr = "" + ServerConstants.server_Name_Source + "에 오신 것을 진심으로 환영합니다.";
                        DueyHandler.addNewItemToDb(2435457, 5, chr.getId(), "메이플 스토리", nStr, true); // 골드 애플
                        DueyHandler.addNewItemToDb(5064000, 3, chr.getId(), "메이플 스토리", nStr, true); // 프로텍트 쉴드
                        DueyHandler.addNewItemToDb(2023125, 3, chr.getId(), "메이플 스토리", nStr, true); // 익스트림 레드
                        DueyHandler.addNewItemToDb(2023126, 3, chr.getId(), "메이플 스토리", nStr, true); // 익스트림 그린
                        DueyHandler.addNewItemToDb(2023127, 3, chr.getId(), "메이플 스토리", nStr, true); // 익스트림 블루
                        DueyHandler.addNewItemToDb(2023147, 1, chr.getId(), "메이플 스토리", nStr, true); // 파티 퀘스트 퍼플
                        DueyHandler.addNewItemToDb(2023148, 1, chr.getId(), "메이플 스토리", nStr, true); // 파티 퀘스트 레드
                        DueyHandler.addNewItemToDb(2450000, 1, chr.getId(), "메이플 스토리", nStr, true); // 메이플 스토리의 행운
                        DueyHandler.addNewItemToDb(2023130, 3, chr.getId(), "메이플 스토리", nStr, true); // 필드 보스 사냥꾼
                        DueyHandler.addNewItemToDb(2023129, 3, chr.getId(), "메이플 스토리", nStr, true); // 인기인의 행운
                        chr.getClient().sendPacket(CField.receiveParcel("핫 타임", true));
                    }
                }
            }
            return 0;
        }
    }

    public static class 핫타임 extends CommandExecute {
        // !핫타임 아이템코드 수량 : 원하는 아이템으로 핫타임 보상 전체 지급

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int itemID = Integer.parseInt(splitted[1]);
            int itemQr = Integer.parseInt(splitted[2]);
            Item item = new Item(itemID, (byte) 0, (short) itemQr);
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    if (chr.getLevel() > 9) {
                        DueyHandler.addNewItemToDb(itemID, itemQr, chr.getId(), "메이플 스토리", ServerConstants.server_Name_Source + "에 오신 것을 진심으로 환영합니다.", true);
                        chr.getClient().sendPacket(CField.receiveParcel("핫 타임", true));
                    }
                }
            }
            return 0;
        }
    }

    public static class WhosFirst extends CommandExecute {
        // 맵에 먼저 온 순서대로 표시

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //probably bad way to do it
            final long currentTime = System.currentTimeMillis();
            List<Pair<String, Long>> players = new ArrayList<Pair<String, Long>>();
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (!chr.isIntern()) {
                    players.add(new Pair<String, Long>(MapleCharacterUtil.makeMapleReadable(chr.getName()) + (currentTime - chr.getCheatTracker().getLastAttack() > 600000 ? " (AFK)" : ""), chr.getChangeTime()));
                }
            }
            Collections.sort(players, new WhoComparator());
            StringBuilder sb = new StringBuilder("이 맵의 플레이어 목록 (도착 순서, 10분 이미활동 시 AFK 표시) : ");
            for (Pair<String, Long> z : players) {
                sb.append(z.left).append(", ");
            }
            c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
            return 0;
        }

        public static class WhoComparator implements Comparator<Pair<String, Long>>, Serializable {

            @Override
            public int compare(Pair<String, Long> o1, Pair<String, Long> o2) {
                if (o1.right > o2.right) {
                    return 1;
                } else if (o1.right == o2.right) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
    }

    public static class WhosLast extends CommandExecute {
        // !WhosLast 원정대유형 : 원정대 대기열 참가자 목록

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                StringBuilder sb = new StringBuilder("whoslast [type] where type can be:  ");
                for (MapleSquadType t : MapleSquadType.values()) {
                    sb.append(t.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            final MapleSquadType t = MapleSquadType.valueOf(splitted[1].toLowerCase());
            if (t == null) {
                StringBuilder sb = new StringBuilder("whoslast [type] where type can be:  ");
                for (MapleSquadType z : MapleSquadType.values()) {
                    sb.append(z.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            if (t.queuedPlayers.get(c.getChannel()) == null) {
                c.getPlayer().dropMessage(6, "The queue has not been initialized in this channel yet.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "Queued players: " + t.queuedPlayers.get(c.getChannel()).size());
            StringBuilder sb = new StringBuilder("List of participants:  ");
            for (Pair<String, String> z : t.queuedPlayers.get(c.getChannel())) {
                sb.append(z.left).append('(').append(z.right).append(')').append(", ");
            }
            c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
            return 0;
        }
    }

    public static class WhosNext extends CommandExecute {
        // !WhosNext 원정대유형 : 원정대 대기열 다음 순서

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                StringBuilder sb = new StringBuilder("whosnext [type] where type can be:  ");
                for (MapleSquadType t : MapleSquadType.values()) {
                    sb.append(t.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            final MapleSquadType t = MapleSquadType.valueOf(splitted[1].toLowerCase());
            if (t == null) {
                StringBuilder sb = new StringBuilder("whosnext [type] where type can be:  ");
                for (MapleSquadType z : MapleSquadType.values()) {
                    sb.append(z.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            if (t.queue.get(c.getChannel()) == null) {
                c.getPlayer().dropMessage(6, "The queue has not been initialized in this channel yet.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "Queued players: " + t.queue.get(c.getChannel()).size());
            StringBuilder sb = new StringBuilder("List of participants:  ");
            final long now = System.currentTimeMillis();
            for (Pair<String, Long> z : t.queue.get(c.getChannel())) {
                sb.append(z.left).append('(').append(StringUtil.getReadableMillis(z.right, now)).append(" ago),");
            }
            c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
            return 0;
        }
    }

    public static class 워프 extends CommandExecute {
        // !워프 맵ID : 맵 전체 유저 강제 이동

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                    return 0;
                }
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(5, "에러 : " + e.getMessage());
                return 0; //assume drunk GM
            }
            return 1;
        }
    }

    public static class 킬올 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                return 0;
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() != 8850011 && mob.getId() != 9450012) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return 1;
        }
    }

    public static class 데미지 extends CommandExecute {
        // !데미지 데미지량 : 맵의 모든 몬스터에게 지정 데미지 주기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                mob.damage(c.getPlayer(), Long.parseLong(splitted[1]), false);
            }
            return 1;
        }
    }

    public static class 테스트엔피시 extends CommandExecute {
        // 플레이어 NPC 생성 테스트 : 내 캐릭터 모습 그대로 자유시장(910000000)에 NPC로 만들어 세워둠
        // NPC 코드 9901020번으로 생성되고, 좌표는 (-340, 34)에 배치됨

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap nmap = c.getChannelServer().getMapFactory().getMap(910000000);
            PlayerNPC newpnpc = new PlayerNPC(c.getPlayer(), ServerConstants.server_Name_Source, 9901020, nmap, -340, 34, 1, 7);
            newpnpc.addToServer();
            PlayerNPC.sendBroadcastModifiedNPC(c.getPlayer(), nmap, 9901020, false);
            return 1;
        }
    }
}
