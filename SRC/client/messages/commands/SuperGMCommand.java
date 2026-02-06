/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.world.World;
import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.Timer;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.MapleSquad;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.WorldTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.OverrideMonsterStats;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.quest.MapleQuest;
import tools.HexTool;
import tools.Pair;
import tools.packet.CField;
import tools.StringUtil;
import tools.packet.CField.NPCPacket;
import tools.packet.CMobPool;

/**
 *
 * @author Emilyx3
 */
public class SuperGMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.SUPERGM;
    }

    public static class 쇼패킷 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String type = splitted[1];
            if (type.equalsIgnoreCase("TRUE")) {
                GameConstants.showPacket = true;
                c.getPlayer().dropMessage(6, "리시브패킷뷰 활성화");
                c.getPlayer().dropMessage(6, "센드킷뷰 활성화");
            } else if (type.equalsIgnoreCase("FALSE")) {
                GameConstants.showPacket = false;
                c.getPlayer().dropMessage(6, "리시브패킷뷰 비활성화");
                c.getPlayer().dropMessage(6, "센드킷뷰 비활성화");
            } else {
                c.getPlayer().dropMessage(6, "!쇼패킷 [true, false]");
            }
            return 1;
        }
    }

    public static class 스킬주기 extends CommandExecute {
        // 사용법: !스킬주기 캐릭터이름 스킬코드 [레벨] [마스터레벨]

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            victim.changeSingleSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }

    public static class 인벤초기화 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Pair<Short, Short>, MapleInventoryType> eqs = new HashMap<Pair<Short, Short>, MapleInventoryType>();
            if (splitted[1].equals("모두")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (Item item : c.getPlayer().getInventory(type)) {
                        eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), type);
                    }
                }
            } else if (splitted[1].equals("장착")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                }
            } else if (splitted[1].equals("장비")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIP);
                }
            } else if (splitted[1].equals("소비")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.USE);
                }
            } else if (splitted[1].equals("설치")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.SETUP);
                }
            } else if (splitted[1].equals("기타")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.ETC);
                }
            } else if (splitted[1].equals("캐시")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    eqs.put(new Pair<Short, Short>(item.getPosition(), item.getQuantity()), MapleInventoryType.CASH);
                }
            } else {
                c.getPlayer().dropMessage(6, "[모두/장착/장비/소비/설치/기타/캐시]");
            }
            for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            return 1;
        }
    }

    public static class 인벤토리잠금 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Item, MapleInventoryType> eqs = new HashMap<Item, MapleInventoryType>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("모두")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (Item item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.Locked.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                            add = true;
                            //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.Untradable.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                            add = true;
                            //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted[1].equals("장착")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).newList()) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.Untradable.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("장비")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.Untradable.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("소비")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.Untradable.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("설치")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.Untradable.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("기타")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.Untradable.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("캐시")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Locked.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.Untradable.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.Untradable.getValue()));
                        add = true;
                        //c.getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                c.getPlayer().dropMessage(6, "[모두/장착/장비/소비/설치/기타/캐시]");
            }

            for (Entry<Item, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return 1;
        }
    }

    public static class 결혼 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "사용법 : !결혼 <캐릭터명> <반지 ID>");
                return 0;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "잘못된 아이템 ID입니다.");
            } else {
                MapleCharacter fff = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "대상 플레이어가 접속 중이어야 합니다.");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId, ringID[i]);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "잘못된 아이템 ID입니다.");
                                return 0;
                            }
                            MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                            chrz[i].dropMessage(6, chrz[i == 0 ? 1 : 0].getName() + "님과 결혼에 성공했습니다.");
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 1;
        }
    }

    public static class 채팅타입 extends CommandExecute {
        // GM 채팅 말풍선 색상 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "사용법 : !채팅타입 <청록색, 흰색, 진분홍, 전체진분홍, 숨김, 일반, 노랑, 공지, 팝업, 연파랑, 연분홍, 시스템>");
                return 0;
            }
            switch (splitted[1].toLowerCase()) {
                case "청록색":
                    c.getPlayer().setChatType(-7);
                    break;
                case "흰색":
                    c.getPlayer().setChatType(-6);
                    break;
                case "진분홍":
                    c.getPlayer().setChatType(-5);
                    break;
                case "전체진분홍":
                    c.getPlayer().setChatType(-10);
                    break;
                case "숨김":
                    c.getPlayer().setChatType(-4);
                    break;
                case "일반":
                    c.getPlayer().setChatType(-3);
                    break;
                case "reg":
                    c.getPlayer().setChatType(-3);
                    break;
                case "regular":
                    c.getPlayer().setChatType(-3);
                    break;
                case "노랑":
                    c.getPlayer().setChatType(-1);
                    break;
                case "공지":
                    c.getPlayer().setChatType(0);
                    break;
                case "팝업":
                    c.getPlayer().setChatType(1);
                    break;
                case "연파랑":
                    c.getPlayer().setChatType(2);
                    break;
                case "연분홍":
                    c.getPlayer().setChatType(5);
                    break;
                case "시스템":
                    c.getPlayer().setChatType(6);
                    break;
                default:
                    c.getPlayer().dropMessage(6, "사용법 : !채팅타입 <청록색, 흰색, 진분홍, 전체진분홍, 숨김, 일반, 노랑, 공지, 팝업, 연파랑, 연분홍, 시스템>");
                    break;
            }
            return 1;
        }
    }

    public static class SpeakMap extends CommandExecute {
        // 같은 맵의 모든 유저가 강제로 채팅하게 하기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
    }

    public static class SpeakChn extends CommandExecute {
        // 같은 채널의 모든 유저가 강제 채팅

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                }
            }
            return 1;
        }
    }

    public static class SpeakWorld extends CommandExecute {
        // 서버 전체 모든 유저가 강제 채팅

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                    }
                }
            }
            return 1;
        }
    }

    public static class Monitor extends CommandExecute {
        // 특정 플레이어 감시 모드 켜기/끄기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter target = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, target.getName() + "님의 감시를 시작합니다.");
                } else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, target.getName() + "님의 감시를 해제합니다.");
                }
            } else {
                c.getPlayer().dropMessage(5, "대상을 채널에서 찾을 수 없습니다.");
                return 0;
            }
            return 1;
        }
    }

    public static class 퀘스트포기 extends CommandExecute {
        // !퀘스트포기 캐릭터이름 퀘스트코드 : 다른 플레이어의 퀘스트 포기(리셋)

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forfeit(ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]));
            return 1;
        }
    }

    public static class 상대강제퀘스트시작 extends CommandExecute {
        // !강제퀘스트시작 캐릭터이름 퀘스트코드 NPC코드 [데이터] : 다른 플레이어의 퀘스트 강제 시작

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length > 4 ? splitted[4] : null);
            return 1;
        }
    }

    public static class 상대강제퀘스트완료 extends CommandExecute {
        // !강제퀘스트완료 캐릭터이름 퀘스트코드 NPC코드 : 다른 플레이어의 퀘스트 강제 완료

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return 1;
        }
    }

    public static class 스레드 extends CommandExecute {
        // !스레드 [필터] : 서버 스레드(작업 목록) 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return 1;
        }
    }

    public static class 스레드추적 extends CommandExecute {
        // !스레드추적 스레드번호 : 특정 스레드의 실행 경로 보기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                throw new IllegalArgumentException();
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return 1;
        }
    }

    public static class ToggleOffense extends CommandExecute {
        // 핵 감지 항목 켜기/끄기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return 1;
        }
    }

    public static class 확성기토글 extends CommandExecute {
        // 확성기(메가폰) 사용 금지/허용 전환

        @Override
        public int execute(MapleClient c, String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "확성기 상태 : " + (c.getChannelServer().getMegaphoneMuteState() ? "사용 가능" : "사용 금지"));
            return 1;
        }
    }

    public static class 리액터소환 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleReactor reactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(splitted[1])), Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            c.getPlayer().getMap().spawnReactorOnGroundBelow(reactor, new Point(c.getPlayer().getTruePosition().x, c.getPlayer().getTruePosition().y - 20));
            return 1;
        }
    }

    public static class ClearSquads extends CommandExecute {
        // 현재 채널의 모든 원정대 초기화

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final Collection<MapleSquad> squadz = new ArrayList<MapleSquad>(c.getChannelServer().getAllSquads().values());
            for (MapleSquad squads : squadz) {
                squads.clear();
            }
            return 1;
        }
    }

    public static class 몬스터타격 extends CommandExecute {
        // !몬스터타격 오브젝트ID 데미지량 : 특정 몬스터(오브젝트ID)에 데미지 주기
        // 오브젝트ID는 맵에 소환된 개별 몬스터마다 다른 고유번호

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            int damage = Integer.parseInt(splitted[2]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(CMobPool.damaged(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
    }

    public static class 전체공격 extends CommandExecute {
        // !전체공격 데미지량 & !전체공격 범위 맵ID

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
            int damage = Integer.parseInt(splitted[1]);
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.broadcastMessage(CMobPool.damaged(mob.getObjectId(), damage));
                mob.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
    }

    public static class 지정몬스터타격 extends CommandExecute {
        //  !진정몬스터타격 데미지량 몬스터코드 : 특정 몬스터에만 데미지

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            int damage = Integer.parseInt(splitted[1]);
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[2])) {
                    map.broadcastMessage(CMobPool.damaged(mob.getObjectId(), damage));
                    mob.damage(c.getPlayer(), damage, false);
                }
            }
            return 1;
        }
    }

    public static class 킬몬스터 extends CommandExecute {
        // !킬몬스터 몬스터코드

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return 1;
        }
    }

    public static class 킬올드롭 extends CommandExecute {

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
                map.killMonster(mob, c.getPlayer(), true, false, (byte) 1);
            }
            return 1;
        }
    }

    public static class 킬올경험치 extends CommandExecute {

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
                mob.damage(c.getPlayer(), mob.getHp(), false);
            }
            return 1;
        }
    }

    public static class 엔피시 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null) {
                npc.setPosition(c.getPlayer().getTruePosition());
                npc.setCy(c.getPlayer().getTruePosition().y);
                npc.setRx0(c.getPlayer().getTruePosition().x + 50);
                npc.setRx1(c.getPlayer().getTruePosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getTruePosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true, -1));
            } else {
                c.getPlayer().dropMessage(6, "잘못된 NPC 아이디입니다.");
                return 0;
            }
            return 1;
        }
    }

    public static class 고정엔피시 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy((c.getPlayer().getPosition()).y);
                npc.setRx0((c.getPlayer().getPosition()).x);
                npc.setRx1((c.getPlayer().getPosition()).x);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true, -1));
            } else {
                c.getPlayer().dropMessage(6, "WZ에 존재하지 않는 NPC를 입력했습니다.");
            }
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String sql = "INSERT INTO `spawn`(`lifeid`, `rx0`, `rx1`, `cy`, `fh`, `type`, `dir`, `mapid`, `mobTime`) VALUES (? ,? ,? ,? ,? ,? ,? ,? ,?)";
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement(sql);
                ps.setInt(1, npcId);
                ps.setInt(2, (c.getPlayer().getPosition()).x - 50);
                ps.setInt(3, (c.getPlayer().getPosition()).x + 50);
                ps.setInt(4, (c.getPlayer().getPosition()).y);
                ps.setInt(5, c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                ps.setString(6, "n");
                ps.setInt(7, (c.getPlayer().getFacingDirection() == 1) ? 0 : 1);
                ps.setInt(8, c.getPlayer().getMapId());
                ps.setInt(9, 0);
                ps.executeUpdate();
                ps.close();
                con.close();
            } catch (Exception e) {
                System.err.println("[오류] 엔피시를 고정 등록하는데 실패했습니다.");
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
            return 1;
        }
    }

    public static class 플레이어엔피시생성 extends CommandExecute {
        // !플레이어엔피시생성 캐릭터이름 NPC코드 : 플레이어 NPC 생성

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "플레이어 NPC를 생성 중...");
                MapleCharacter chhr = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + "님이 접속 중이 아닙니다.");
                    return 0;
                }
                PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                npc.addToServer();
                c.getPlayer().dropMessage(6, "완료");
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC 생성에 실패했습니다. : " + e.getMessage());
                e.printStackTrace();
            }
            return 1;
        }
    }

    public static class 플레이어엔피시삭제 extends CommandExecute {
        // !플레이어엔피시삭제 오브젝트ID

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "플레이어 엔피시를 삭제 중...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC) npc).destroy(true);
                    c.getPlayer().dropMessage(6, "완료");
                } else {
                    c.getPlayer().dropMessage(6, "!플레이어엔피시삭제 [오브젝트 ID]");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC 삭제에 실패했습니다. : " + e.getMessage());
                e.printStackTrace();
            }
            return 1;
        }
    }

    public static class ServerMessage extends CommandExecute {
        // 게임 화면 상단에 흐르는 공지 메시지(노란색 띠)를 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.setServerMessage(outputMessage);
            }
            return 1;
        }
    }

    public static class 몹 extends CommandExecute {
        // !몹 몬스터코드 [수량] [옵션]
        // 추가 옵션:
        // - lvl = 숫자 → 몬스터 레벨 변경
        // - hp = 숫자 → HP 직접 지정
        // - exp = 숫자 → 경험치 직접 지정
        // - php = 숫자 → HP를 원래의 N% 로 설정
        // - pexp = 숫자 → 경험치를 원래의 N% 로 설정

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int mid = Integer.parseInt(splitted[1]);
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            Integer level = CommandProcessorUtil.getNamedIntArg(splitted, 1, "lvl");
            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");

            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "에러 : " + e.getMessage());
                return 0;
            }
            if (onemob == null) {
                c.getPlayer().dropMessage(5, "존재하지 않는 몬스터입니다.");
                return 0;
            }
            long newhp = 0;
            int newexp = 0;
            if (hp != null) {
                newhp = hp.longValue();
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php.doubleValue() / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (exp != null) {
                newexp = exp.intValue();
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp.doubleValue() / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                if (level != null) {
                    mob.changeLevel(level.intValue(), false);
                } else {
                    mob.setOverrideStats(overrideStats);
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
                c.getPlayer().dropMessage(6, "몬스터 스폰 | ID : " + mob.getId() + " | 오브젝트 ID : " + mob.getObjectId());
            }
            return 1;
        }
    }

    public static class PS extends CommandExecute {
        // 패킷 직접 전송 도구 (개발자 전용)
        // 기능 : 패킷 전송 + 쌓아둔 데이터를 한 번에 내 클라이언트로 보냄

        protected static StringBuilder builder = new StringBuilder();

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (builder.length() > 1) {
                c.getSession().write(CField.getPacketFromHexString(builder.toString()));
                builder = new StringBuilder();
            } else {
                c.getPlayer().dropMessage(6, "패킷 데이터를 입력해주세요!");
            }
            return 1;
        }
    }

    public static class APS extends PS {
        // 패킷 직접 전송 도구 (개발자 전용)
        // 기능 : 패킷 데이터(16진수) 추가 + 보낼 데이터를 조금씩 이어붙임

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                builder.append(StringUtil.joinStringFrom(splitted, 1));
                c.getPlayer().dropMessage(6, "현재 데이터 : " + builder.toString());
            } else {
                c.getPlayer().dropMessage(6, "패킷 데이터를 입력해주세요!");
            }
            return 1;
        }
    }

    public static class CPS extends PS {
        // 패킷 직접 전송 도구 (개발자 전용)
        // 기능 : 패킷 초기화

        @Override
        public int execute(MapleClient c, String[] splitted) {
            builder = new StringBuilder();
            return 1;
        }
    }

    public static class P extends CommandExecute {
        // 패킷 직접 전송 도구 (개발자 전용)
        // 기능 : 즉시 패킷 전송

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                c.getSession().write(CField.getPacketFromHexString(StringUtil.joinStringFrom(splitted, 1)));
            } else {
                c.getPlayer().dropMessage(6, "패킷 데이터를 입력해주세요!");
            }
            return 1;
        }
    }

    public static class 맵리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
    }

    public static class 엔피시리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().scriptClear();
            return 1;
        }
    }

    public static class 리스폰 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().respawn(true);
            return 1;
        }
    }

    public abstract static class TestTimer extends CommandExecute {

        protected Timer toTest = null;

        @Override
        public int execute(final MapleClient c, String[] splitted) {
            final int sec = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(5, "Message will pop up in " + sec + " seconds.");
            c.getPlayer().dropMessage(5, "Active: " + toTest.getSES().getActiveCount() + " Core: " + toTest.getSES().getCorePoolSize() + " Largest: " + toTest.getSES().getLargestPoolSize() + " Max: " + toTest.getSES().getMaximumPoolSize() + " Current: " + toTest.getSES().getPoolSize() + " Status: " + toTest.getSES().isShutdown() + toTest.getSES().isTerminated() + toTest.getSES().isTerminating());
            final long oldMillis = System.currentTimeMillis();
            toTest.schedule(new Runnable() {

                public void run() {
                    c.getPlayer().dropMessage(5, "Message has popped up in " + ((System.currentTimeMillis() - oldMillis) / 1000) + " seconds, expected was " + sec + " seconds");
                    c.getPlayer().dropMessage(5, "Active: " + toTest.getSES().getActiveCount() + " Core: " + toTest.getSES().getCorePoolSize() + " Largest: " + toTest.getSES().getLargestPoolSize() + " Max: " + toTest.getSES().getMaximumPoolSize() + " Current: " + toTest.getSES().getPoolSize() + " Status: " + toTest.getSES().isShutdown() + toTest.getSES().isTerminated() + toTest.getSES().isTerminating());
                }
            }, sec * 1000);
            return 1;
        }
    }

    public static class TestEventTimer extends TestTimer {

        public TestEventTimer() {
            toTest = EventTimer.getInstance();
        }
    }

    public static class TestCloneTimer extends TestTimer {

        public TestCloneTimer() {
            toTest = CloneTimer.getInstance();
        }
    }

    public static class TestEtcTimer extends TestTimer {

        public TestEtcTimer() {
            toTest = EtcTimer.getInstance();
        }
    }

    public static class TestMapTimer extends TestTimer {

        public TestMapTimer() {
            toTest = MapTimer.getInstance();
        }
    }

    public static class TestWorldTimer extends TestTimer {

        public TestWorldTimer() {
            toTest = WorldTimer.getInstance();
        }
    }

    public static class TestBuffTimer extends TestTimer {

        public TestBuffTimer() {
            toTest = BuffTimer.getInstance();
        }
    }

    public static class Crash extends CommandExecute {
        // 특정 플레이어 클라이언트 강제 크래시

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = ChannelServer.getInstance(World.Find.findChannel(splitted[1])).getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                victim.getClient().getSession().write(HexTool.getByteArrayFromHexString("1A 00")); //give_buff with no data :D
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "The victim does not exist.");
                return 0;
            }
        }
    }

    public static class Rev extends CommandExecute {
        // 서버 소스 리비전(버전) 확인

        private static int revision = -1;

        public static int getRevision() {
            if (revision != -1) {
                return revision;
            } else {
                InputStream svninfo = AdminCommand.class.getResourceAsStream("/all-wcprops");
                if (svninfo == null) {
                    return revision;
                }
                Scanner sc = new Scanner(svninfo);
                while (sc.hasNext()) {
                    String[] s = sc.next().split("/");
                    if (s.length > 1 && s[1].equals("svn")) {
                        revision = Integer.parseInt(s[5]);
                        break;
                    }
                }
                sc.close();
            }
            return revision;
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (getRevision() != -1) {
                c.getPlayer().dropMessage(5, "현재 리비전 : " + revision + ".");
            } else {
                c.getPlayer().dropMessage(5, "리비전을 찾을 수 없습니다.");
            }
            return 1;
        }
    }

    public static class 몬스터북 extends CommandExecute {
        // 몬스터북 전부 채우기

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (int e : MapleItemInformationProvider.getInstance().getMonsterBook().keySet()) {
                c.getPlayer().getMonsterBook().getCards().put(e, 2);
            }
            c.getPlayer().getMonsterBook().changed();
            c.getPlayer().dropMessage(5, "완료");
            return 1;
        }
    }

    public static class 몬스터북목록 extends CommandExecute {
        // 몬스터북 목록 보기 (페이지별)

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final List<Entry<Integer, Integer>> mbList = new ArrayList<Entry<Integer, Integer>>(MapleItemInformationProvider.getInstance().getMonsterBook().entrySet());
            Collections.sort(mbList, new BookComparator());
            final int page = Integer.parseInt(splitted[1]);
            for (int e = (page * 8); e < Math.min(mbList.size(), (page + 1) * 8); e++) {
                c.getPlayer().dropMessage(6, e + ": " + mbList.get(e).getKey() + " - " + mbList.get(e).getValue());
            }

            return 0;
        }

        public static class BookComparator implements Comparator<Entry<Integer, Integer>>, Serializable {

            @Override
            public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return 1;
                } else if (o1.getValue() == o2.getValue()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
    }

    public static class Subcategory extends CommandExecute {
        // 캐릭터 서브카테고리 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSubcategory(Byte.parseByte(splitted[1]));
            return 1;
        }
    }

    public static class 풀메소 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return 1;
        }
    }

    public static class 캐쉬 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "금액을 입력해주세요.");
                return 0;
            }
            c.getPlayer().modifyCSPoints(1, Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }

    public static class 메이플포인트 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "금액을 입력해주세요.");
                return 0;
            }
            c.getPlayer().modifyCSPoints(2, Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }

    public static class 저장 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage(6, "캐릭터 정보가 저장 완료 되었습니다.");
            return 1;
        }
    }

    public static class 옵코드리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return 1;
        }
    }

    public static class 드롭리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(5, "드롭 테이블이 새로고침되었습니다.");
            return 1;
        }
    }

    public static class 포탈리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            PortalScriptManager.getInstance().clearScripts();
            return 1;
        }
    }

    public static class 상점리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory.getInstance().clear();
            return 1;
        }
    }

    public static class 이벤트리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return 1;
        }
    }

    public static class 퀘스트리셋 extends CommandExecute {
        // !퀘스트리셋 퀘스트코드

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return 1;
        }
    }

    public static class 퀘스트시작 extends CommandExecute {
        // !퀘스트시작 퀘스트코드 NPC코드

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
    }

    public static class 퀘스트완료 extends CommandExecute {
        // !퀘스트완료 퀘스트코드 NPC코드 선택번호
        // 선택번호는 보상 선택지가 있을 때 사용

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return 1;
        }
    }

    public static class 퀘스트강제시작 extends CommandExecute {
        // !퀘스트강제시작 퀘스트코드 NPC코드 [데이터]

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return 1;
        }
    }

    public static class 퀘스트강제완료 extends CommandExecute {
        // !퀘스트강제완료 퀘스트코드 NPC코드

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
    }

    public static class 리엑터공격 extends CommandExecute {
        // !리엑터히트 오브젝트ID

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return 1;
        }
    }

    public static class 리엑터강제공격 extends CommandExecute {
        // !리엑터강제공격 오브젝트ID 상태값 → 특정 상태로 강제 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).forceHitReactor(Byte.parseByte(splitted[2]));
            return 1;
        }
    }

    public static class 리엑터파괴 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return 1;
        }
    }

    public static class 리엑터변경 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return 1;
        }
    }

    public static class 리엑터리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetReactors();
            return 1;
        }
    }

    public static class SendAllNote extends CommandExecute {
        // 현재 채널 모든 유저에게 쪽지 보내기

        @Override
        public int execute(MapleClient c, String[] splitted) {

            if (splitted.length >= 1) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
            } else {
                c.getPlayer().dropMessage(6, "사용법: !SendAllNote <내용>");
                return 0;
            }
            return 1;
        }
    }

    public static class 갓모드 extends CommandExecute {
        // 팔라딘의 디바인 프로텍션(1221054) 스킬을 자신에게 걸어서 일정 시간 동안 죽지 않는 상태 됨

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(1221054).getEffect(1).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class 크리티컬버프 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(36101002).getEffect(10).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class 버프스킬 extends CommandExecute {
        // !버프스킬 스킬코드 레벨 : 아무 스킬이나 원하는 레벨로 자신에게 버프로 적용

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class 버프아이템 extends CommandExecute {
        // !버프아이템 아이템코드 : 해당 아이템의 사용 효과를 내 캐릭터에 적용

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class 버프아이템확장 extends CommandExecute {
        // !버프아이템확장 아이템코드 : 아이템 확장 효과 사용
        // 일부 아이템은 기본 효과 외에 추가 효과가 있는데, 그걸 적용하는 것

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class ItemSize extends CommandExecute { //test
        // 서버에 등록된 총 아이템 수 확인

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "등록된 아이템 수 : " + MapleItemInformationProvider.getInstance().getAllItems().size());
            return 0;
        }
    }

    public static class 스킬마스터 extends CommandExecute {
        // !스킬마스터 직업코드앞3자리

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                MapleData data = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Skill.wz")).getData(StringUtil.getLeftPaddedStr(splitted[1], '0', 3) + ".img");
                byte maxLevel = 0;
                for (MapleData skill : data) {
                    if (skill != null) {
                        for (MapleData skillId : skill.getChildren()) {
                            if (!skillId.getName().equals("icon")) {
                                maxLevel = (byte) MapleDataTool.getIntConvert("maxLevel", skillId.getChildByPath("common"), 0);
                                //if (MapleDataTool.getIntConvert("invisible", skillId, 0) == 0) { //스킬창에 안보이는 스킬은 올리지않음
                                //c.getPlayer().dropMessage(5, Integer.parseInt(skillId.getName()) + "");
                                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(Integer.parseInt(skillId.getName())), maxLevel, maxLevel); //임시처리
                                //}
                            }
                        }
                    }
                }
                c.getPlayer().dropMessage(-1, "해당 직업의 스킬을 모두 최대레벨로 올렸습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 1;
        }

        public static class 맥스스킬 extends CommandExecute {

            @Override
            public int execute(MapleClient c, String[] splitted) {
                for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File("wz/String.wz")).getData("Skill.img").getChildren()) {
                    try {
                        Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
                        if ((skill.getId() < 1009 || skill.getId() > 1011)) ;
                        c.getPlayer().changeSingleSkillLevel(skill, (byte) skill.getMaxLevel(), (byte) skill.getMaxLevel());
                    } catch (NumberFormatException nfe) {
                        break;
                    } catch (NullPointerException npe) {
                        continue;
                    }
                }
                return 1;
            }
        }

        public static class 스킬제거 extends CommandExecute {

            @Override
            public int execute(MapleClient c, String[] splitted) {
                for (Skill skill : SkillFactory.getAllSkills()) {
                    c.getPlayer().changeSkillLevel(skill, 0, (byte) 0);
                }
                return 1;
            }
        }
    }
}
