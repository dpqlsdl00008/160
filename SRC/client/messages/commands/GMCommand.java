/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import client.messages.MessageType;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import java.awt.Point;
import java.util.ArrayList;
import scripting.EventInstanceManager;
import scripting.EventManager;
import server.MapleCarnivalChallenge;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.packet.CField;
import tools.StringUtil;
import tools.packet.CMonsterCarnival;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
    }

    public static class 칭호값 extends CommandExecute { // 칭호/타이틀 값 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setTitle(Integer.parseInt(splitted[1]));
            c.getPlayer().dropMessage(MessageType.SYSTEM, Integer.parseInt(splitted[1]) + " 로 변경완료");
            return 1;
        }
    }

    public static class 카드스택 extends CommandExecute { // 카드 스택 값 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setCardStack(Byte.parseByte(splitted[1]));
            c.getPlayer().dropMessage(MessageType.SYSTEM, Byte.parseByte(splitted[1]) + " 로 변경완료");
            return 1;
        }
    }

    public static class xx extends CommandExecute { // FTC 변경

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setFTC(Byte.parseByte(splitted[1]));
            c.getPlayer().dropMessage(MessageType.SYSTEM, Byte.parseByte(splitted[1]) + " 로 변경완료");
            return 1;
        }
    }

    public static class 스킬 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            c.getPlayer().changeSingleSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }

    public static class 아이템경험치 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getStat().checkEquipLevels(c.getPlayer(), Integer.parseInt(splitted[1]));
            c.getPlayer().dropMessage(MessageType.SYSTEM, "장착 중인 모든 장비의 아이템 경험치가 " + Integer.parseInt(splitted[1]) + " 만큼 증가했습니다.");
            return 1;
        }
    }

    public static class 패밀리로드 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleFamily.loadAll();
            c.getPlayer().dropMessage(6, ServerConstants.server_Name_Source + "의 모든 패밀리를 재 로드 하였습니다.");
            return 1;
        }
    }

    public static class 아이템폭파드롭 extends CommandExecute {

        @Override
        public int execute(final MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(MessageType.SYSTEM, "사용법: !아이템폭파드롭 <아이템 ID> <개수>");
            } else {
                final int itemID = Integer.parseInt(splitted[1]);
                final int quantity = Integer.parseInt(splitted[2]);
                final int width = 32;
                int dStart = c.getPlayer().getPosition().x - (quantity / 2 * width);
                Item item = new Item(itemID, (byte) 0, (short) 1);
                for (int i = 0; i < quantity; i++) {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += width;
                }
            }
            return 1;
        }
    }

    public static class 인기도 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "!인기도 <캐릭터 명> <값>");
                return 0;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int fame = 0;
            try {
                fame = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "숫자 형식이 올바르지 않습니다.");
                return 0;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.Fame, victim.getFame());
            }
            return 1;
        }
    }

    public static class 무적 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "무적이 비활성화되었습니다.");
            } else {
                player.setInvincible(true);
                player.dropMessage(6, "무적이 활성화되었습니다.");
            }
            return 1;
        }
    }

    public static class 스킬포인트 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getPlayer().updateSingleStat(MapleStat.SP, 0); // we don't care the value here
            return 1;
        }
    }

    public static class 아이템 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "펫은 캐시샵에서 구매해 주세요.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " : 존재하지 않는 아이템입니다.");
            } else {
                Item item;
                short flag = (short) ItemFlag.Locked.getValue();

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = (Equip) ii.getEquipById(itemId);
                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);

                }
                /*if (!c.getPlayer().isSuperGM()) {
                    item.setFlag(flag);
                }*/
                if (!c.getPlayer().isAdmin()) {
                    //item.setGMLog(c.getPlayer().getName() + " used !item");
                }

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return 1;
        }
    }

    public static class 드롭 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "펫은 캐시샵에서 구매해 주세요.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " : 존재하지 않는 아이템입니다.");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    //toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return 1;
        }
    }

    public static class 직업 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (MapleCarnivalChallenge.getJobNameById(Integer.parseInt(splitted[1])).length() == 0) {
                c.getPlayer().dropMessage(5, "유효하지 않은 직업 ID입니다.");
                return 0;
            }
            c.getPlayer().changeJob((short) Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class 상점 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted[1]);
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return 1;
        }
    }

    public static class 레벨업 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainExp(c.getPlayer().getNeededExp() - 1, true, false, true);
            c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            return 1;
        }
    }

    public static class 레벨 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setLevel((short) (Short.parseShort(splitted[1]) - 1));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return 1;
        }
    }

    public static class 이벤트종료 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "진행중인 이벤트를 마감하였습니다.");
                return 1;
            } else {
                c.getPlayer().dropMessage(5, "현재 이벤트가 진행중이거나 이벤트가 진행중이지 않습니다.");
                return 0;
            }
        }
    }

    public static class 이벤트 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("입력 형식이 올바르지 않습니다.");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
                return 0;
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
                return 0;
            }
            return 1;
        }
    }

    public static class 자동시작이벤트 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return 1;
        }
    }

    public static class 이벤트시작 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleEvent.onStartEvent(c.getPlayer());
            return 1;
        }
    }

    public static class 이벤트완료 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleEvent.finishEvent(c.getPlayer());
            return 1;
        }
    }

    public static class 이벤트변경 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), true);
                c.getPlayer().dropMessage(5, "이벤트를 시작하고 입장을 제한했습니다.");
                return 1;
            } else {
                c.getPlayer().dropMessage(5, "먼저 이벤트 예약을 해야 하며, 현재 이벤트 맵에 있어야 합니다.");
                return 0;
            }
        }
    }

    public static class 이벤트시간 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("Wrong syntax: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
                return 0;
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
                return 0;
            }
            return 1;
        }
    }

    public static class 이벤트취소 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("Wrong syntax: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
                return 0;
            }
            final String msg = MapleEvent.cancelEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
                return 0;
            }
            return 1;
        }
    }

    public static class 아이템삭제 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "사용법: !아이템삭제 <캐릭터명> <아이템 ID>");
                return 0;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "대상 캐릭터가 존재하지 않습니다.");
                return 0;
            }
            chr.removeAll(Integer.parseInt(splitted[2]), false);
            c.getPlayer().dropMessage(6, "닉네임 : " + splitted[1] + " 님이 소지한 아이템 " + splitted[2] + "을(를) 모두 제거했습니다.");
            return 1;

        }
    }

    public static class 아이템잠금 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "사용법: !아이템잠금 <캐릭터명> <아이템 ID>");
                return 0;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "대상 캐릭터가 존재하지 않습니다.");
                return 0;
            }
            int itemid = Integer.parseInt(splitted[2]);
            MapleInventoryType type = GameConstants.getInventoryType(itemid);
            for (Item item : chr.getInventory(type).listById(itemid)) {
                item.setFlag((byte) (item.getFlag() | ItemFlag.Locked.getValue()));
                chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, chr));
            }
            if (type == MapleInventoryType.EQUIP) {
                type = MapleInventoryType.EQUIPPED;
                for (Item item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((byte) (item.getFlag() | ItemFlag.Locked.getValue()));
                    //chr.getClient().getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                }
            }
            c.getPlayer().dropMessage(6, "All items with the ID " + splitted[2] + " has been locked from the inventory of " + splitted[1] + ".");
            return 1;
        }
    }

    public static class 올킬 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.getStat().setHp((short) 0, map);
                    map.getStat().setMp((short) 0, map);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
    }

    public static class 확성대리채팅 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            World.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, victim == null ? c.getChannel() : victim.getClient().getChannel(), victim == null ? splitted[1] : victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true));
            return 1;
        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "unable to find '" + splitted[1]);
                return 0;
            } else {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            }
            return 1;
        }
    }
    
    public static class 데스카운트 extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter user = c.getPlayer();
            int deathCount = (Integer.parseInt(splitted[1]) + 1);
            user.getClient().sendPacket(CMonsterCarnival.updateScore(deathCount, 0));
            return 1;
        }
    }

    public static class 테스트 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {

            MapleCharacter user = c.getPlayer();
            user.getClient().sendPacket(CMonsterCarnival.showUserRank(user.getMap()));
            //user.getClient().sendPacket(CField.getPVPClock(1, 60 * 60 * 1));
            //user.getMap().broadcastMessage(CField.showEffect("Karing/1phaseWeather/1"));
            //c.sendPacket(CMobPool.OnNextAttack(monster.getObjectId(), 2));

            /*
            Map<MonsterTemporaryStat, Integer> mts = new EnumMap<>(MonsterTemporaryStat.class);
            List<Integer> reflection = new LinkedList<>();
            reflection.add(5);
            MobSkill ms = MobSkillFactory.getMobSkill(170, 10);
            monster.applyMonsterBuff(mts, 170, 1000, ms, reflection, 0);
             */
            //SkillFactory.getSkill(80001034).getEffect(1).applyTo(c.getPlayer());
            //final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            //final Item item = ii.getEquipById(1002357);
            //c.sendPacket(CWvsContext.OnBroadcastMsg(14, 1, "테스트", "메세지", true, item));
            // 네트의 피라미드
            // c.getPlayer().getClient().sendPacket(CUserLocal.openUI(UIType.FIELDITEM));
            // c.getPlayer().getClient().sendPacket(CUserLocal.openUI(UIType.FIELDITEMINVENTORY));
            // 몬스터 공격 및 스킬 (8850011)
            /*
            for (MapleMonster monster : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                //monster.getMap().broadcastMessage(CMobPool.OnMobSpeaking(monster.getObjectId(), Integer.parseInt(splitted[1])));
                int fancingLeft = 1;
                if (monster.getTruePosition().x < c.getPlayer().getTruePosition().x) {
                    fancingLeft = 0;
                }
                //c.sendPacket(CMobPool.OnMobFancingLeft(monster.getObjectId(), fancingLeft));
                c.sendPacket(CMobPool.OnNextAttack(monster.getObjectId(), 2));
             */
 /*
                Map<MonsterTemporaryStat, Integer> stats = new EnumMap<>(MonsterTemporaryStat.class);
                stats.put(MonsterTemporaryStat.Invincible, 1);
                List<Integer> reflection = new LinkedList<>();
                reflection.add(999);
                MobSkill ms = MobSkillFactory.getMobSkill(146, 1);
                monster.applyMonsterBuff(stats, 146, Long.MAX_VALUE, ms, reflection, 0);
             */
            //}
            //monster.applyStatus(c.getPlayer(), new MonsterTemporaryStatEffect(MonsterTemporaryStat.Invincible, 1, eff.getSourceId(), null, false), false, 9999, true, eff);
            return 1;
        }
    }

    public static class 상태 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //c.getPlayer().giveDebuff(MonsterSkill.SealSkill, MobSkillFactory.getMobSkill(157, 1), 0);
            c.getPlayer().disease(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), 21);
            return 1;
        }
    }

    public static class dropDupe extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter chr = c.getPlayer();
            final Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(Byte.parseByte(splitted[1]));
            final Equip ret = (Equip) target.copy();
            chr.getMap().spawnItemDrop(chr, chr, ret, chr.getPosition(), true, true);
            return 1;
        }
    }

    public static class 아이템세부정보 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "현재 장착 무기의 세부 정보입니다.");
            c.getPlayer().dropMessage(6, "내구도 : " + String.valueOf(((Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11)).getDurability()));
            c.getPlayer().dropMessage(6, "아이템 경험치 : " + String.valueOf(((Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11)).getItemEXP()));
            //c.getPlayer().dropMessage(6, "Item EXP: " + String.valueOf(((Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-56)).getItemEXP()));
            return 1;
        }
    }

   /* public static class SetInstanceProperty extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "No Event Manager instance currently exists with: " + splitted[1]);
            } else {
                em.setProperty(splitted[2], splitted[3]);
                for (EventInstanceManager eim : em.getInstances()) {
                    eim.setProperty(splitted[2], splitted[3]);
                }
            }
            return 1;
        }
    }

    public static class ListInstanceProperty extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "No Event Manager instance currently exists with: " + splitted[1]);
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", eventManager: " + em.getName() + " iprops: " + eim.getProperty(splitted[2]) + ", eprops: " + em.getProperty(splitted[2]));
                }
            }
            return 0;
        }
    }

    public static class LeaveInstance extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().dropMessage(5, "You are not in one");
            } else {
                c.getPlayer().getEventInstance().unregisterPlayer(c.getPlayer());
            }
            return 1;
        }
    }

    public static class WhosThere extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            StringBuilder builder = new StringBuilder("Players on Map (" + c.getPlayer().getMapId() + "): ").append(c.getPlayer().getMap().getCharactersThreadsafe().size()).append("; ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
    }

    public static class StartInstance extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() != null) {
                c.getPlayer().dropMessage(5, "You are in one");
            } else if (splitted.length > 2) {
                EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
                if (em == null || em.getInstance(splitted[2]) == null) {
                    c.getPlayer().dropMessage(5, "Not exist");
                } else {
                    em.getInstance(splitted[2]).registerPlayer(c.getPlayer());
                }
            } else {
                c.getPlayer().dropMessage(5, "!startinstance [eventmanager] [eventinstance]");
            }
            return 1;

        }
    } */

    public static class 몬스터리셋 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return 1;
        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return 1;
        }
    }

    public static class 엔피시삭제 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
    }

    public static class 공지 extends CommandExecute {

        protected static int getNoticeType(String typestring) {
            if (typestring.equals("n")) {
                return 0;
            } else if (typestring.equals("p")) {
                return 1;
            } else if (typestring.equals("l")) {
                return 2;
            } else if (typestring.equals("nv")) {
                return 5;
            } else if (typestring.equals("v")) {
                return 5;
            } else if (typestring.equals("b")) {
                return 6;
            }
            return -1;
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int joinmod = 1;
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            byte[] packet = CWvsContext.serverNotice(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 2;
            }
            byte[] packet = CWvsContext.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class Y extends Yellow {
    }

    public static class WhatsMyIP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            /*final MapleCharacter target = c.getPlayer().getMap().getCharacterByName(splitted[1]);
            c.getPlayer().dropMessage(6, "MaxHP: " + target.getMaxHp());
            c.getPlayer().dropMessage(6, "MaxMP: " + target.getMaxMp());
            c.getPlayer().dropMessage(6, "STR: " + target.getStat().getTotalStr());
            c.getPlayer().dropMessage(6, "DEX: " + target.getStat().getTotalDex());
            c.getPlayer().dropMessage(6, "INTs: " + target.getStat().getTotalInt());
            c.getPlayer().dropMessage(6, "LUK: " + target.getStat().getTotalLuk());
            c.getPlayer().dropMessage(6, "LUK: " + target.getStat().luk);*/
            // MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 1;
        }
    }

    public static class 목표레벨업 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int levelTo = Integer.parseInt(splitted[1]);
            while (c.getPlayer().getLevel() < levelTo) {
                c.getPlayer().levelUp();
            }
            return 1;
        }
    }

    public static class TDrops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().toggleDrops();
            //SkillFactory.getSkill(5121009).getEffect(30).applyTo(c.getPlayer());
            return 1;
        }
    }

    public static class 몬스터확인 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            ArrayList<Integer> mobid = new ArrayList<Integer>();
            ArrayList<MapleMonster> mob = new ArrayList<MapleMonster>();
            c.getPlayer().dropMessage(5, "현재 맵의 몬스터와 ID 목록입니다. : ");

            for (String strmob : c.getPlayer().getMap().getAllUniqueMobs()) {
                c.getPlayer().dropMessage(5, "" + strmob);
            }

            return 1;
        }
    }
}
