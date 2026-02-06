package handling.channel.handler;

import client.InnerAbillity;
import client.InnerSkillValueHolder;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;

import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.Skill;
import client.inventory.ItemFlag;
import client.inventory.MaplePet;
import client.inventory.MaplePet.PetFlag;
import client.inventory.MapleMount;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MonsterSkill;
import client.MapleQuestStatus;
import client.inventory.MapleInventoryType;
import client.inventory.MapleInventory;
import client.MapleStat;
import client.MapleTrait.MapleTraitType;
import client.PlayerStats;
import client.SkillEntry;
import constants.GameConstants;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.messages.MessageType;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.ChatType;
import handling.SendPacketOpcode;
import handling.UIType;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import handling.world.World;

import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import scripting.EtcScriptInvoker;
import scripting.EventInstanceManager;
import scripting.EventManager;

//import javafx.scene.effect.Effect;
import server.Randomizer;
import server.RandomRewards;
import server.MapleShopFactory;
import server.MapleStatEffect;
import server.MapleItemInformationProvider;
import server.MapleInventoryManipulator;
import server.StructRewardItem;
import server.maps.*;
import server.quest.MapleQuest;
import server.life.MapleMonster;
import server.life.MapleLifeFactory;
import scripting.NPCScriptManager;
import server.AutobanManager;
import server.MaplePortal;
import server.StructItemOption;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import tools.FileoutputUtil;
import tools.Pair;
import tools.StringUtil;
import tools.data.OutPacket;
import tools.packet.CCashShop;
import tools.packet.CPet;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CField;
import tools.packet.CField.UIPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.CPlayerShop;

public class InventoryHandler {

    public static final void ItemMove(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) {
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        slea.skip(4);
        final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte()); //04
        final short src = slea.readShort();                                            //01 00
        final short dst = slea.readShort();                                            //00 00
        final short quantity = slea.readShort();                                       //53 01
        if (type.EQUIP != MapleInventoryType.EQUIP && (quantity < 0 || quantity > 99999)) {
            AutobanManager.getInstance().autoban(c.getPlayer().getClient(), "핵감지로 인하여 오토벤 처리되었습니다.78");
            return;
        }

        if (c.getPlayer().getMapId() == 105200610) {
            if (src == -101 || dst == -101) {
                c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                return;
            }
        }

        if (src < 0 && dst > 0) {
            MapleInventoryManipulator.unequip(c, src, dst);
        } else if (dst < 0) {
            MapleInventoryManipulator.equip(c, src, dst);
        } else if (dst == 0) {
            MapleInventoryManipulator.drop(c, type, src, quantity);
        } else {
            MapleInventoryManipulator.move(c, type, src, dst);
        }
    }

    public static final void SwitchBag(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        //69 00 
        //F7 B8 21 00 틱 
        //67 00 00 00 
        //66 00 00 00 
        //04 00 00 00
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(slea.readInt());
        final short src = (short) slea.readInt();                                       //01 00
        final short dst = (short) slea.readInt();                                            //00 00
        if (src < 100 || dst < 100) {
            return;
        }
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, src, dst);
    }

    public static final void MoveBag(final LittleEndianAccessor iPacket, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        user.setScrolledPosition((short) 0);
        user.updateTick(iPacket.DecodeInt());
        int v1 = iPacket.DecodeInt();
        boolean v2 = (v1 == 1 || v1 == 3);
        if (iPacket.available() > 6) {
            short v3 = (short) iPacket.DecodeInt();
            byte v4 = iPacket.DecodeByte();
            short v5 = iPacket.DecodeShort();
            MapleInventoryManipulator.move(c, MapleInventoryType.ETC, v2 ? v3 : v5, v2 ? v5 : v3);
            return;
        }
        if (v2) {
            short v6 = (short) iPacket.DecodeInt();
            short v7 = user.getInventory(MapleInventoryType.ETC).getNextFreeSlot();
            if (v7 < 1) {
                c.sendPacket(CWvsContext.crossHunterQuestResult((byte) 2, (short) 0));
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            MapleInventoryManipulator.move(c, MapleInventoryType.ETC, v6, v7);
            return;
        }
        byte v8 = iPacket.DecodeByte();
        MapleInventory v9 = user.getInventory(MapleInventoryType.getByType((byte) v8));
        short v10 = iPacket.DecodeShort();
        MapleInventory v11 = user.getInventory(MapleInventoryType.ETC);
        short v12 = 0;
        for (short v13 = 204; v13 > 200; v13--) {
            if (v11.getItem(v13) == null) {
                v12 = v13;
            }
        }
        for (short v13 = 304; v13 > 300; v13--) {
            if (v11.getItem(v13) == null) {
                v12 = v13;
            }
        }
        for (short v13 = 408; v13 > 400; v13--) {
            if (v11.getItem(v13) == null) {
                v12 = v13;
            }
        }
        if (v12 < 200) {
            return;
        }
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, v10, v12);
    }

    public static final void ItemSort(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType(slea.readByte());
        if (pInvType == MapleInventoryType.UNDEFINED || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final MapleInventory pInv = c.getPlayer().getInventory(pInvType);
        boolean sorted = false;

        while (!sorted) {
            final byte freeSlot = (byte) pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte) (freeSlot + 1); i <= pInv.getSlotLimit(); i++) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot < 100 && itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        c.getSession().write(CWvsContext.finishedSort(pInvType.getType()));
        c.getSession().write(CWvsContext.gatherSortItem(true, pInvType.getType()));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void ItemGather(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        if (c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final byte mode = slea.readByte();
        final MapleInventoryType invType = MapleInventoryType.getByType(mode);
        MapleInventory Inv = c.getPlayer().getInventory(invType);
        final List<Item> itemMap = new LinkedList<Item>();
        for (Item item : Inv.list()) {
            if (GameConstants.isPet(item.getItemId())) {
                continue;
            }
            if (item.getPosition() > 99) {
                continue;
            }
            itemMap.add(item.copy());
        }
        for (Item itemStats : itemMap) {
            MapleInventoryManipulator.removeFromSlot(c, invType, itemStats.getPosition(), itemStats.getQuantity(), true, false);
        }

        final List<Item> sortedItems = sortItems(itemMap);
        for (Item item : sortedItems) {
            MapleInventoryManipulator.addFromDrop(c, item, false);
        }
        c.getSession().write(CWvsContext.finishedGather(mode));
        c.getSession().write(CWvsContext.gatherSortItem(false, mode));
        c.getSession().write(CWvsContext.enableActions());
        itemMap.clear();
        sortedItems.clear();
        //c.getSession().write(CField.getCharInfo(c.getPlayer()));
    }

    private static final List<Item> sortItems(final List<Item> passedMap) {
        final List<Integer> itemIds = new ArrayList<Integer>(); // empty list.
        for (Item item : passedMap) {
            itemIds.add(item.getItemId());
        }
        Collections.sort(itemIds);

        final List<Item> sortedList = new LinkedList<Item>();

        for (Integer val : itemIds) {
            for (Item item : passedMap) {
                if (val == item.getItemId()) {
                    sortedList.add(item);
                    passedMap.remove(item);
                    break;
                }
            }
        }
        return sortedList;
    }

    public static final boolean UseRewardItem(final byte slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.sendPacket(CWvsContext.enableActions());
        boolean isLootabyss = false;
        if (toUse.getItemId() > 2028153 && toUse.getItemId() < 2028166) {
            isLootabyss = true;
        }
        if (isLootabyss) {
            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
            return true;
        }
        if (toUse.getItemId() == 2028048) {
            int gMeso = Randomizer.rand(1000000, 9999999);
            int cRand = Randomizer.nextInt(999);
            if (cRand < 99) {
                gMeso = Randomizer.rand(10000000, 19999999);
            }
            if (cRand < 33) {
                gMeso = Randomizer.rand(20000000, 29999999);
            }
            if (cRand < 9) {
                gMeso = Randomizer.rand(30000000, 39999999);
            }
            if (cRand < 7) {
                gMeso = Randomizer.rand(40000000, 49999999);
            }
            if (cRand < 3) {
                gMeso = Randomizer.rand(50000000, 59999999);
            }
            if (cRand < 1) {
                gMeso = Randomizer.rand(60000000, 69999999);
            }
            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
            c.getPlayer().gainMeso(gMeso, true, true);
            return true;
        }
        boolean sBook = false;
        switch (toUse.getItemId()) {
            case 2430144:
            case 2430559:
            case 2430606:
            case 2430668: {
                sBook = true;
                break;
            }
        }
        if (toUse.getQuantity() > 0 && (sBook || toUse.getItemId() == itemId)) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                if (rewards != null && rewards.getLeft() > 0) {
                    while (true) {
                        for (StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final Item item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0) {
                                        item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                    }
                                    MapleInventoryManipulator.addbyItem(c, item);
                                } else {
                                    if (sBook) {
                                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, false, false);
                                    }
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity, null);
                                }
                                if (!sBook) {
                                    MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                                }
                                c.getSession().write(EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect));
                                chr.getMap().broadcastMessage(chr, EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect, chr.getId()), false);
                                return true;
                            }
                        }
                    }
                } else {
                    chr.dropMessage(6, "Unknown error.");
                }
            } else {
                chr.dropMessage(6, "인벤토리에 공간이 부족합니다.");
            }
        }
        return false;
    }

    public static final void roulette_start(final int itemId, final MapleClient c) {
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId);
        if (c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() <= -1) {
            c.getPlayer().dropMessage(6, "소비창에 빈공간이 없습니다.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if ((toUse == null || toUse.getQuantity() < 1)) {
            c.getSession().write(EffectPacket.failedRoulette(4, itemId));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int Roulette = Randomizer.rand(0, 360);
        switch (itemId) {
            case 4001780:
                if ((Roulette >= 0 && Roulette <= 107) || (Roulette >= 346 && Roulette <= 360)) {
                    c.getPlayer().setRouletteItem(2028169);
                } else if ((Roulette >= 108 && Roulette <= 152)) {
                    c.getPlayer().setRouletteItem(2028172);
                } else if ((Roulette >= 153 && Roulette <= 181)) {
                    c.getPlayer().setRouletteItem(2028173);
                } else if ((Roulette >= 182 && Roulette <= 196)) {
                    c.getPlayer().setRouletteItem(2028174);
                } else if ((Roulette >= 197 && Roulette <= 284)) {
                    c.getPlayer().setRouletteItem(2028170);
                } else if ((Roulette >= 285 && Roulette <= 345)) {
                    c.getPlayer().setRouletteItem(2028171);
                }
                break;
            case 4001781:
                if ((Roulette >= 0 && Roulette <= 30) || (Roulette >= 330 && Roulette <= 360)) {
                    c.getPlayer().setRouletteItem(2028172);
                } else if ((Roulette >= 31 && Roulette <= 45)) {
                    c.getPlayer().setRouletteItem(2028169);
                } else if ((Roulette >= 46 && Roulette <= 136)) {
                    c.getPlayer().setRouletteItem(2028171);
                } else if ((Roulette >= 137 && Roulette <= 255)) {
                    c.getPlayer().setRouletteItem(2028170);
                } else if ((Roulette >= 256 && Roulette <= 284)) {
                    c.getPlayer().setRouletteItem(2028174);
                } else if ((Roulette >= 285 && Roulette <= 329)) {
                    c.getPlayer().setRouletteItem(2028173);
                }
                break;
            case 4001782:
                if ((Roulette >= 0 && Roulette <= 14) || (Roulette >= 331 && Roulette <= 360)) {
                    c.getPlayer().setRouletteItem(2028174);
                } else if ((Roulette >= 15 && Roulette <= 30)) {
                    c.getPlayer().setRouletteItem(2028170);
                } else if ((Roulette >= 31 && Roulette <= 152)) {
                    c.getPlayer().setRouletteItem(2028171);
                } else if ((Roulette >= 153 && Roulette <= 181)) {
                    c.getPlayer().setRouletteItem(2028169);
                } else if ((Roulette >= 182 && Roulette <= 269)) {
                    c.getPlayer().setRouletteItem(2028172);
                } else if ((Roulette >= 270 && Roulette <= 330)) {
                    c.getPlayer().setRouletteItem(2028173);
                }
                break;
            case 4001783:
                if ((Roulette >= 137 && Roulette <= 152) || (Roulette >= 315 && Roulette <= 330)) {
                    c.getPlayer().setRouletteItem(2028169);
                } else {
                    c.getPlayer().setRouletteItem(2028174);
                }
                break;
        }
        MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, itemId, 1, false, false);
        c.getSession().write(EffectPacket.startRoulette(Roulette));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void roulette_stop(final MapleClient c) {
        MapleInventoryManipulator.addById(c, c.getPlayer().getRouletteItem(), (short) 1, "룰렛보상");
        c.getSession().write(InfoPacket.getShowItemGain(c.getPlayer().getRouletteItem(), (short) 1, true));
        c.getPlayer().setRouletteItem(0);
    }

    public static final void UseItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MonsterSkill.StopPotion) || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "지금은 사용하실 수 없습니다.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if ((itemId / 10000) == 243) {
            NPCScriptManager.getInstance().start(c, 9010000, "consume_" + itemId);
            return;
        }

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        /* MVP의 물약 */
        if (itemId == 2000054 || itemId == 2000055) {
            boolean isUse = true;
            if (chr.getOneInfoQuest(20250227, "macro_potion").equals("")) {
                chr.updateOneInfoQuest(20250227, "macro_potion", "0");
            }
            if (Long.parseLong(chr.getOneInfoQuest(20250227, "macro_potion")) < System.currentTimeMillis()) {
                int aDuration = (itemId == 2000054 ? 179 : 359);
                chr.updateOneInfoQuest(20250227, "macro_potion", (System.currentTimeMillis() + (60000 * aDuration)) + "");
            } else {
                isUse = false;
            }
            final String eDuration = StringUtil.getReadableMillis(System.currentTimeMillis(), Long.parseLong(chr.getOneInfoQuest(20250227, "macro_potion")));
            if (!isUse) {
                chr.dropMessage(1, "'MVP의 물약' 효과를 적용 중에 있습니다.\r\n\r\n잔여 시간 : " + eDuration);
                chr.getClient().sendPacket(CWvsContext.enableActions());
                return;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "MVP의 물약 효과를 적용 중에 있습니다. (잔여 시간 : " + eDuration + ")"));
            chr.getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        if (itemId > 2050009 && itemId < 2050024) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (chr.getOneInfoQuest(10000000, "item_level_" + itemId).equals("")) {
                chr.updateOneInfoQuest(10000000, "item_level_" + itemId, "0");
            }
            int symbolLevel = Integer.parseInt(chr.getOneInfoQuest(10000000, "item_level_" + itemId));
            if (symbolLevel == 0) {
                chr.updateOneInfoQuest(10000000, "item_level_" + itemId, "1");
                chr.updateOneInfoQuest(10000000, "item_exp_" + itemId, "0");
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                chr.changeSingleSkillLevel(SkillFactory.getSkill(8000005 + (itemId - 2050011)), 1, (byte) 20);
                chr.getClient().sendPacket(CWvsContext.enableActions());
                return;
            }
            int symbolExp = Integer.parseInt(chr.getOneInfoQuest(10000000, "item_exp_" + itemId));
            int symbolMaxExp = GameConstants.reqArcaneForceExp(symbolLevel);
            if (symbolMaxExp != 0 && (symbolExp > (symbolMaxExp - 1))) {
                chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "[" + ii.getName(itemId) + "]의 경험치를 획득 할 수 없습니다."));
                chr.getClient().sendPacket(CWvsContext.enableActions());
                return;
            }
            chr.updateOneInfoQuest(10000000, "item_exp_" + itemId, "" + (symbolExp + 1));
            chr.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "[" + ii.getName(itemId) + "]의 경험치를 획득하였습니다. (" + Integer.parseInt(chr.getOneInfoQuest(10000000, "item_exp_" + itemId)) + " / " + symbolMaxExp + ")"));
        }
        if (itemId == 2022337) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            chr.updateQuest(3514, 1, "0");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (itemId == 2450064) {
            chr.updateQuest(2450064, 2, String.valueOf("0")); // 초기화 시키고
            chr.updateQuest(2450064, 2, String.valueOf(System.currentTimeMillis() + (30 * 60 * 1000))); // 다시 넣고
            System.out.print("경험치 2배 쿠폰 사용");
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) { //cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseCosmetic(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 254 || (itemId / 1000) % 10 != chr.getGender()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
    }

    public static final void UseReturnScroll(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.getMapId() == 749040100 || chr.hasBlockedInventory() || chr.isInBlockedMap() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            } else {
                c.getSession().write(CWvsContext.enableActions());
            }
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseMagnify(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getChannelServer().getPotential()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte src = (byte) slea.readShort();
        final byte check = (byte) slea.readShort();
        final boolean insight = src == 127;
        final Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(src);
        final Item toReveal = c.getPlayer().getInventory(check < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(check);
        if (toReveal == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        final Equip eqq = (Equip) toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (reqLevel > 18) {
            reqLevel = 19;
        }
        final long price = GameConstants.getMagnifyPrice(eqq);
        if ((src == 0x7F && price != -1 && c.getPlayer().getMeso() >= price
                || insight || magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12)
                || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            eqq.revealHiddenPotential();
            c.getPlayer().getTrait(MapleTraitType.insight).addExp((insight ? 10 : ((magnify.getItemId() + 2) - 2460000)), c.getPlayer());
            c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eqq.getPosition()));
            if (!insight) {
                c.getSession().write(InventoryPacket.scrolledItem(magnify, toReveal, false, true));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
            } else {
                if (price != -1) {
                    c.getPlayer().gainMeso(-(int) price, false);
                }
                c.getPlayer().forceReAddItem(toReveal, MapleInventoryType.EQUIP);
            }
            c.getSession().write(CWvsContext.enableActions());
        } else {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
    }

    public static boolean isAllowedPotentialStat(Equip eqq, int opID) { //For now
        //if (GameConstants.isWeapon(eqq.getItemId())) {
        //    return !(opID > 60000) || (opID >= 1 && opID <= 4) || (opID >= 9 && opID <= 12) || (opID >= 10001 && opID <= 10006) || (opID >= 10011 && opID <= 10012) || (opID >= 10041 && opID <= 10046) || (opID >= 10051 && opID <= 10052) || (opID >= 10055 && opID <= 10081) || (opID >= 10201 && opID <= 10291) || (opID >= 210001 && opID <= 20006) || (opID >= 20011 && opID <= 20012) || (opID >= 20041 && opID <= 20046) || (opID >= 20051 && opID <= 20052) || (opID >= 20055 && opID <= 20081) || (opID >= 20201 && opID <= 20291) || (opID >= 30001 && opID <= 30006) || (opID >= 30011 && opID <= 30012) || (opID >= 30041 && opID <= 30046) || (opID >= 30051 && opID <= 30052) || (opID >= 30055 && opID <= 30081) || (opID >= 30201 && opID <= 30291) || (opID >= 40001 && opID <= 40006) || (opID >= 40011 && opID <= 40012) || (opID >= 40041 && opID <= 40046) || (opID >= 40051 && opID <= 40052) || (opID >= 40055 && opID <= 40081) || (opID >= 40201 && opID <= 40291);
        //}
        return opID < 60000;
    }

    public static final void addToScrollLog(int accountID, int charID, int scrollID, int itemID, byte oldSlots, byte newSlots, byte viciousHammer, String result, boolean ws, boolean ls, int vega) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("INSERT INTO scroll_log VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, accountID);
            ps.setInt(2, charID);
            ps.setInt(3, scrollID);
            ps.setInt(4, itemID);
            ps.setByte(5, oldSlots);
            ps.setByte(6, newSlots);
            ps.setByte(7, viciousHammer);
            ps.setString(8, result);
            ps.setByte(9, (byte) (ws ? 1 : 0));
            ps.setByte(10, (byte) (ls ? 1 : 0));
            ps.setInt(11, vega);
            ps.execute();
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
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

    public static final boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c, final MapleCharacter chr, final boolean legendarySpirit) {
        return UseUpgradeScroll(slot, dst, ws, c, chr, 0, legendarySpirit);
    }

    public static final boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c, final MapleCharacter chr, final int vegas, final boolean legendarySpirit) {
        boolean whiteScroll = false; // white scroll being used?
        boolean recovery = false; // white scroll being used?
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        chr.setScrolledPosition((short) 0);
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        Equip toScroll = null;
        if (dst < 0) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else if (legendarySpirit) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        if (toScroll == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        final byte oldLevel = toScroll.getLevel();
        final byte oldEnhance = toScroll.getEnhance();
        final byte oldState = toScroll.getState();
        final short oldFlag = toScroll.getFlag();
        final byte oldSlots = toScroll.getUpgradeSlots();

        Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (scroll == null) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(slot);
            if (scroll == null) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }

        int v1 = (scroll.getItemId() / 100);
        boolean innocentScroll = (v1 == 20496);
        if (innocentScroll) {
            final Equip origin = (Equip) MapleItemInformationProvider.getInstance().getEquipById(toScroll.getItemId());
            toScroll.setAcc(origin.getAcc());                       // 명중률
            toScroll.setAvoid(origin.getAvoid());                   // 회피율
            toScroll.setDex(origin.getDex());                       // DEX
            toScroll.setHands(origin.getHands());                   // 손재주
            toScroll.setHp(origin.getHp());                         // HP
            toScroll.setInt(origin.getInt());                       // INT
            toScroll.setJump(origin.getJump());                     // 점프력
            toScroll.setLevel(origin.getLevel());                   // 레벨
            toScroll.setLuk(origin.getLuk());                       // LUK
            toScroll.setMatk(origin.getMatk());                     // 마법 공격력
            toScroll.setMdef(origin.getMdef());                     // 마법 방어력
            toScroll.setMp(origin.getMp());                         // MP
            toScroll.setSpeed(origin.getSpeed());                   // 이동 속도
            toScroll.setStr(origin.getStr());                       // STR
            toScroll.setUpgradeSlots(origin.getUpgradeSlots());     // 업그레이드 가능 횟수
            toScroll.setWatk(origin.getWatk());                     // 물리 공격력
            toScroll.setWdef(origin.getWdef());                     // 물리 방어력
            toScroll.setEnhance((byte) 0);                          // 장비 강화
            toScroll.setViciousHammer((byte) 0);                    // 황금 망치
            toScroll.setFusionAnvil((byte) 0);                      // 신비의 모루
            chr.getInventory(GameConstants.getInventoryType(scroll.getItemId())).removeItem(scroll.getPosition(), (short) 1, false);
            Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
            c.getSession().write(InventoryPacket.scrolledItem(scroll, scrolled, false, false));
            chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, legendarySpirit, whiteScroll, toScroll.getItemId(), scroll.getItemId()), vegas == 0);
            chr.equipChanged();
            return true;
        }
        /* 익셉셔널 해머 */
        if (GameConstants.isExceptional(scroll.getItemId())) {
            List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
            if (scrollReqs != null && scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
                c.getPlayer().dropMessage(1, "'" + ii.getName(scrollReqs.get(0)) + "'에만\r\n사용 할 수 있습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
            if (ItemFlag.PreventSlipping.check(toScroll.getFlag())) {
                c.getPlayer().dropMessage(1, "'" + ii.getName(scroll.getItemId()) + "'(은)는\r\n1회만 사용 할 수 있습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
            chr.getInventory(GameConstants.getInventoryType(scroll.getItemId())).removeItem(scroll.getPosition(), (short) 1, false);
            short flag = toScroll.getFlag();
            flag |= ItemFlag.PreventSlipping.getValue();
            toScroll.setFlag(flag);
            Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
            final MapleInventoryType type = MapleInventoryType.getByType(toScroll.getType());
            c.sendPacket(CWvsContext.enableActions());
            c.getPlayer().forceReAddItem_NoUpdate(scrolled, type);
            c.sendPacket(InventoryPacket.updateSpecialItemUse(scrolled, type.getType(), scrolled.getPosition(), true, c.getPlayer()));
            chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, legendarySpirit, whiteScroll, toScroll.getItemId(), scroll.getItemId()), vegas == 0);
            chr.equipChanged();
            return true;
        }
        Item wscroll = null;
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 30 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            final boolean isEpic = (scroll.getItemId() / 100 == 20497) || (scroll.getItemId() / 100 == 20494);
            if ((!isEpic && toScroll.getState() >= 1) || (isEpic && toScroll.getState() >= 18) || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0 && toScroll.getItemId() / 10000 != 135 && !isEpic) || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                //c.getSession().write(InventoryPacket.getInventoryFull());
                //c.getSession().write(CWvsContext.enableActions());
                //return false;
            }
        } else if (GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (ii.isCash(toScroll.getItemId())) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) { //not a durability item
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs != null && scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (scroll.getItemId() == 2041200 && toScroll.getItemId() != 1122000 && toScroll.getItemId() != 1122076) {
            c.getSession().write(CWvsContext.enableActions());
            chr.dropMessage(1, "드래곤의 돌은 혼테일의 목걸이에만 사용 가능한 아이템입니다.");
            return false;
        }

        if ((scroll.getItemId() == 2046856 || scroll.getItemId() == 2046857) && (toScroll.getItemId() / 1000 == 1152 || !GameConstants.isAccessory(toScroll.getItemId()))) {
            c.getSession().write(CWvsContext.enableActions());
            chr.dropMessage(1, "악세서리에만 사용 가능한 주문서입니다.");
            return false;
        }

        if ((scroll.getItemId() == 2046991 || scroll.getItemId() == 2046992 || scroll.getItemId() == 2046996 || scroll.getItemId() == 2046997) && GameConstants.isTwoHanded(toScroll.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            chr.dropMessage(1, "한손무기에만 사용 가능한 주문서입니다.");
            return false;
        }

        if ((scroll.getItemId() == 2047814 || scroll.getItemId() == 2047818) && !GameConstants.isTwoHanded(toScroll.getItemId()) && (toScroll.getItemId() / 1000 != 1672)) {
            c.getSession().write(CWvsContext.enableActions());
            chr.dropMessage(1, "두손무기에만 사용 가능한 주문서입니다.");
            return false;
        }

        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            chr.dropMessage(1, "악세서리 주문서를 사용하실 수 없는 아이템입니다.");
            return false;
        }

        if (toScroll.getUpgradeSlots() > 0) {
            if (scroll.getItemId() >= 2049370 && scroll.getItemId() <= 2049377) {
                c.getSession().write(CWvsContext.enableActions());
                chr.dropMessage(1, "아직 업그레이드 슬롯이 남아있습니다.");
                return false;
            }
        }
        if (scroll.getQuantity() <= 0) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        if (legendarySpirit && vegas == 0) {
            if (chr.getSkillLevel(SkillFactory.getSkill(chr.getStat().getSkillByJob(1003, chr.getJob()))) <= 0) {
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (!legendarySpirit && chr.getInventory(MapleInventoryType.EQUIPPED).countById(1182996) >= 1) {
            c.getPlayer().dropMessage(5, "Equipment cannot be changed while the Mark of Stasis is equipped.");
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (GameConstants.isEquipScroll(scroll.getItemId()) && !ItemFlag.ProtectScroll.check(oldFlag) && c.getPlayer().getMapId() == 910000000) { // 장강체크
            c.getPlayer().dropMessage(5, "광장에서는 프로텍트 실드가 적용되지 않는 아이템은 장비강화 주문서를 사용할 수 없습니다.");
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
        ScrollResult scrollSuccess;
        if (scrolled == null) {
            scrollSuccess = Equip.ScrollResult.CURSE;
        } else if (GameConstants.isChaosScroll(scrolled.getItemId())) {
            scrollSuccess = Equip.ScrollResult.FAIL;
        } else if ((scroll.getItemId() / 100 == 20497 && scrolled.getState() == 1) || scrolled.getLevel() > oldLevel || scrolled.getEnhance() > oldEnhance || scrolled.getState() > oldState || scrolled.getFlag() > oldFlag) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if ((GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots)) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else {
            scrollSuccess = Equip.ScrollResult.FAIL;
            if (ItemFlag.RecoveryScroll.check(toScroll.getFlag())) {
                recovery = true;
            }
        }
        if (recovery == false) {
            chr.getInventory(GameConstants.getInventoryType(scroll.getItemId())).removeItem(scroll.getPosition(), (short) 1, false);
        }
        if (ItemFlag.SafetyScroll.check(toScroll.getFlag())) {
            toScroll.setFlag((short) (toScroll.getFlag() - ItemFlag.SafetyScroll.getValue()));
        }
        if (ItemFlag.ProtectScroll.check(toScroll.getFlag())) {
            toScroll.setFlag((short) (toScroll.getFlag() - ItemFlag.ProtectScroll.getValue()));
        }
        if (ItemFlag.LuckyDayScroll.check(toScroll.getFlag())) {
            toScroll.setFlag((short) (toScroll.getFlag() - ItemFlag.LuckyDayScroll.getValue()));
        }
        if (ItemFlag.RecoveryScroll.check(toScroll.getFlag())) {
            toScroll.setFlag((short) (toScroll.getFlag() - ItemFlag.RecoveryScroll.getValue()));
        }
        // RecoveryScroll
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        } else if (scrollSuccess == Equip.ScrollResult.FAIL && scrolled.getUpgradeSlots() < oldSlots && c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5640000) != null) {
            chr.setScrolledPosition(scrolled.getPosition());
            if (vegas == 0) {
                c.getSession().write(CWvsContext.pamSongUI());
            }
        }
        if (scrollSuccess == Equip.ScrollResult.CURSE) {
            c.getSession().write(InventoryPacket.scrolledItem(scroll, toScroll, true, false));
            if (recovery) {
                chr.dropMessage(5, "주문서의 효과로 사용된 주문서가 차감되지 않았습니다.");
            } else {
                if (dst < 0) {
                    chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
                } else {
                    chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
                }
            }
        } else if (vegas == 0) {
            c.getSession().write(InventoryPacket.scrolledItem(scroll, scrolled, false, false));
        }
        chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit, whiteScroll, toScroll.getItemId(), scroll.getItemId()), vegas == 0);
        String txt = ii.getName(scroll.getItemId()) + "의 힘이 적용되었습니다. (";
        if (ii.cSTR != 0) {
            txt += "STR : " + (ii.cSTR > 0 ? "+" : "") + "" + ii.cSTR + ", ";
        }
        if (ii.cDEX != 0) {
            txt += "DEX : " + (ii.cDEX > 0 ? "+" : "") + "" + ii.cDEX + ", ";
        }
        if (ii.cINT != 0) {
            txt += "INT : " + (ii.cINT > 0 ? "+" : "") + "" + ii.cINT + ", ";
        }
        if (ii.cLUK != 0) {
            txt += "LUK : " + (ii.cLUK > 0 ? "+" : "") + "" + ii.cLUK + ", ";
        }
        if (ii.cPAD != 0) {
            txt += "공격력 : " + (ii.cPAD > 0 ? "+" : "") + "" + ii.cPAD + ", ";
        }
        if (ii.cMAD != 0) {
            txt += "마력 : " + (ii.cMAD > 0 ? "+" : "") + "" + ii.cMAD + ", ";
        }
        if (ii.cWDEF != 0) {
            txt += "물리 방어력 : " + (ii.cWDEF > 0 ? "+" : "") + "" + ii.cWDEF + ", ";
        }
        if (ii.cMDEF != 0) {
            txt += "마법 방어력 : " + (ii.cMDEF > 0 ? "+" : "") + "" + ii.cMDEF + ", ";
        }
        if (ii.cACC != 0) {
            txt += "명중치 : " + (ii.cACC > 0 ? "+" : "") + "" + ii.cACC + ", ";
        }
        if (ii.cEVA != 0) {
            txt += "회피치 : " + (ii.cEVA > 0 ? "+" : "") + "" + ii.cEVA + ", ";
        }
        if (ii.cSPEED != 0) {
            txt += "이동 속도 : " + (ii.cSPEED > 0 ? "+" : "") + "" + ii.cSPEED + ", ";
        }
        if (ii.cJUMP != 0) {
            txt += "점프력 : " + (ii.cJUMP > 0 ? "+" : "") + "" + ii.cJUMP + ", ";
        }
        if (ii.cHP != 0) {
            txt += "HP : " + (ii.cHP > 0 ? "+" : "") + "" + ii.cHP + ", ";
        }
        if (ii.cMP != 0) {
            txt += "MP : " + (ii.cMP > 0 ? "+" : "") + "" + ii.cMP + ", ";
        }
        txt += ")";
        if (txt.contains(", )")) {
            txt = txt.replace(", )", ")");
        }
        if (GameConstants.isChaosScroll(scroll.getItemId())) {
            if (scrollSuccess == Equip.ScrollResult.SUCCESS) {
                if (!txt.equals(ii.getName(scroll.getItemId()) + "의 힘이 적용되었습니다. ()")) {
                    chr.dropMessage(5, txt + "");
                }
            }
        }
        //addToScrollLog(chr.getAccountID(), chr.getId(), scroll.getItemId(), toScroll.getItemId(), oldSlots, (byte) (scrolled == null ? -1 : scrolled.getUpgradeSlots()), toScroll.getViciousHammer(), scrollSuccess.name(), whiteScroll, legendarySpirit, vegas);
        if (dst < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE) && vegas == 0) {
            chr.equipChanged();
        }

        return true;
    }

    public static final boolean UseSkillBook(final byte slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || chr.hasBlockedInventory()) {
            return false;
        }
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getEquipStats(toUse.getItemId());
        if (skilldata == null) { // Hacking or used an unknown item
            return false;
        }
        boolean canuse = false, success = false;
        int skill = 0, maxlevel = 0;

        final Integer SuccessRate = skilldata.get("success");
        final Integer ReqSkillLevel = skilldata.get("reqSkillLevel");
        final Integer MasterLevel = skilldata.get("masterLevel");

        byte i = 0;
        Integer CurrentLoopedSkillId;
        while (true) {
            CurrentLoopedSkillId = skilldata.get("skillid" + i);
            i++;
            if (CurrentLoopedSkillId == null || MasterLevel == null) {
                break; // End of data
            }
            final Skill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
            if (CurrSkillData != null && CurrSkillData.canBeLearnedBy(chr.getJob()) && (ReqSkillLevel == null || chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel) && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (SuccessRate == null || Randomizer.nextInt(100) <= SuccessRate) {
                    success = true;
                    chr.changeSingleSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData), (byte) (int) MasterLevel);
                } else {
                    success = false;
                }
                MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short) 1, false);
                break;
            }
        }
        c.getPlayer().getMap().broadcastMessage(CWvsContext.useSkillBook(chr, skill, maxlevel, canuse, success));
        c.getSession().write(CWvsContext.enableActions());
        return canuse;
    }

    public static final void UseCatchItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMap map = chr.getMap();

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null && !chr.hasBlockedInventory() && itemid / 10000 == 227 && MapleItemInformationProvider.getInstance().getCardMobId(itemid) == mob.getId()) {
            if (!MapleItemInformationProvider.getInstance().isMobHP(itemid) || mob.getHp() <= mob.getMobMaxHp() / 2) {
                // map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 1));
                map.killMonster(mob, chr, true, false, (byte) 1);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, false);
                if (MapleItemInformationProvider.getInstance().getCreateId(itemid) > 0) {
                    MapleInventoryManipulator.addById(c, MapleItemInformationProvider.getInstance().getCreateId(itemid), (short) 1, "Catch item " + itemid + " on " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                //   map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 0));
                c.getSession().write(CWvsContext.catchMob(mob.getId(), itemid, (byte) 0));
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseMountFood(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt(); //2260000 usually
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();

        if (itemid / 10000 == 226 && toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null && !c.getPlayer().hasBlockedInventory()) {
            final int fatigue = mount.getFatigue();

            boolean levelup = false;
            mount.setFatigue((byte) -30);

            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (level < 30 && mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1)) {
                    mount.setLevel((byte) (level + 1));
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(CWvsContext.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseScriptedNPCItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();

        if (chr != null) {
            NPCScriptManager.getInstance().start(c, 9010000, "consume_" + itemId);
            return;
        }

        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        long expiration_days = 0;
        int mountid = 0;
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory() && !chr.inPVP()) {
            switch (toUse.getItemId()) {
                case 2438100:
                case 2438101:
                case 2438102:
                case 2438103:
                case 2438104:
                case 2438105: { // Energy Cells
                    int num = toUse.getItemId() - 2438100;
                    int reqLevel = (num * 10) + 200;
                    int levelMax = reqLevel == 250 ? 5 : 10;
                    if (chr.getLevel() == reqLevel) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        chr.setCData(ServerConstants.Q_MAXLEVEL, levelMax);
                        chr.dropMessage(MessageType.POPUP, "Your maximum Level has been increased to " + Integer.toString(210 + (10 * num)) + "!");
                        chr.levelUp();
                        chr.setExp(0);
                    } else {
                        chr.dropMessage(MessageType.ERROR, "You do not meet the Level requirement to use this item.");
                    }
                    break;
                }
                case 2430007: { // Blank Compass
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);

                    if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                            && inventory.countById(3994103) >= 20 // Compass Letter "South"
                            && inventory.countById(3994104) >= 20 // Compass Letter "East"
                            && inventory.countById(3994105) >= 20) { // Compass Letter "West"
                        MapleInventoryManipulator.addById(c, 2430008, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()); // Gold Compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    } else {
                        MapleInventoryManipulator.addById(c, 2430007, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()); // Blank Compass
                    }
                    NPCScriptManager.getInstance().start(c, 2084001);
                    break;
                }
                case 2430008: { // Gold Compass
                    chr.saveLocation(SavedLocationType.RICHIE);
                    MapleMap map;
                    boolean warped = false;

                    for (int i = 390001000; i <= 390001004; i++) {
                        map = c.getChannelServer().getMapFactory().getMap(i);

                        if (map.getCharactersSize() == 0) {
                            chr.changeMap(map, map.getPortal(0));
                            warped = true;
                            break;
                        }
                    }
                    if (warped) { // Removal of gold compass
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    } else { // Or mabe some other message.
                        c.getPlayer().dropMessage(5, "All maps are currently in use, please try again later.");
                    }
                    break;
                }
                case 2430691: // nebulite diffuser fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430691) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 5750001, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 5750001, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Nebulite Diffuser.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430748: // premium fusion ticket 
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430748) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 4420000, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 4420000, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Premium Fusion Ticket.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 5680151: //천원 오천원 만원 3만원 5만원 끗 
                case 5680148:
                case 5680149:
                case 5680150:
                case 5680193: {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    c.getPlayer().modifyCSPoints(2, itemId == 5680151 ? 1000 : itemId == 5680148 ? 5000 : itemId == 5680149 ? 10000 : itemId == 5680150 ? 30000 : itemId == 5680193 ? 50000 : 0, false);
                    break;
                }
                case 5680389: {
                    NPCScriptManager.getInstance().start(c, 9000031);
                    break;
                }
                case 5680019: {//starling hair 
                    //if (c.getPlayer().getGender() == 1) {
                    int hair = 32150 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.Hair, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    //}
                    break;
                }
                case 5680020: {//starling hair 
                    //if (c.getPlayer().getGender() == 0) {
                    int hair = 32160 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.Hair, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    //}
                    break;
                }
                case 3994225:
                    c.getPlayer().dropMessage(5, "Please bring this item to the NPC.");
                    break;
                case 2430212: //energy drink
                    MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    long lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 5);
                    }
                    break;
                case 2430213: //energy drink
                    marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 10);
                    }
                    break;
                case 2430220: //energy drink
                case 2430214: //energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 30);
                    }
                    break;
                case 2430227: //energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 50);
                    }
                    break;
                case 2430231: //energy drink
                    marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 40);
                    }
                    break;
                case 2430144: //smb
                    final int itemid = Randomizer.nextInt(373) + 2290000;
                    if (MapleItemInformationProvider.getInstance().itemExists(itemid) && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special") && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                case 2430370:
                    if (MapleInventoryManipulator.checkSpace(c, 2028062, (short) 1, "")) {
                        MapleInventoryManipulator.addById(c, 2028062, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                case 2430158: //lion king
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 100) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310010, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310010, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 50) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310009, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310009, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 50 Purification Totems for a Noble Lion King Medal, 100 for Royal Lion King Medal.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430159:
                    MapleQuest.getInstance(3182).forceComplete(c.getPlayer(), 2161004);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                case 2430014:
                    NPCScriptManager.getInstance().start(c, 1300010); // 킬라 버섯 포자
                    break;
                case 2430015:
                    NPCScriptManager.getInstance().start(c, 1300011); // 가시 넝클
                    break;
                case 2430200: //thunder stone
                    if (c.getPlayer().getQuestStatus(31152) != 2) {
                        c.getPlayer().dropMessage(5, "You have no idea how to use it.");
                    } else {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                            if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000660) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000661) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000662) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000663) >= 1) {
                                if (MapleInventoryManipulator.checkSpace(c, 4032923, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000660, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000661, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000662, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000663, 1, true, false)) {
                                    MapleInventoryManipulator.addById(c, 4032923, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                                } else {
                                    c.getPlayer().dropMessage(5, "Please make some space. You can only carry one Dream Key at a time.");
                                }
                            } else {
                                c.getPlayer().dropMessage(5, "There needs to be 1 of each Stone for a Dream Key.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "Please make some space.");
                        }
                    }
                    break;
                case 2430130:
                case 2430131: //energy charge
                    if (GameConstants.isResist(c.getPlayer().getJob())) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().gainExp(20000 + (c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate()), true, true, false);
                    } else {
                        c.getPlayer().dropMessage(5, "You may not use this item.");
                    }
                    break;
                case 2430132:
                case 2430133:
                case 2430134: //resistance box
                case 2430142:
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getJob() == 3200 || c.getPlayer().getJob() == 3210 || c.getPlayer().getJob() == 3211 || c.getPlayer().getJob() == 3212) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1382101, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else if (c.getPlayer().getJob() == 3300 || c.getPlayer().getJob() == 3310 || c.getPlayer().getJob() == 3311 || c.getPlayer().getJob() == 3312) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1462093, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else if (c.getPlayer().getJob() == 3500 || c.getPlayer().getJob() == 3510 || c.getPlayer().getJob() == 3511 || c.getPlayer().getJob() == 3512) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1492080, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else {
                            c.getPlayer().dropMessage(5, "You may not use this item.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Make some space.");
                    }
                    break;
                case 2430036: //croco 1 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430170: //croco 7 day
                    mountid = 1027;
                    expiration_days = 7;
                    break;
                case 2430037: //black scooter 1 day
                    mountid = 1028;
                    expiration_days = 1;
                    break;
                case 2430038: //pink scooter 1 day
                    mountid = 1029;
                    expiration_days = 1;
                    break;
                case 2430039: //clouds 1 day
                    mountid = 1030;
                    expiration_days = 1;
                    break;
                case 2430040: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 1;
                    break;
                case 2430223: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 15;
                    break;
                case 2430259: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 3;
                    break;
                case 2430242: //motorcycle
                    mountid = 80001018;
                    expiration_days = 10;
                    break;
                case 2430243: //power suit
                    mountid = 80001019;
                    expiration_days = 10;
                    break;
                case 2430261: //power suit
                    mountid = 80001019;
                    expiration_days = 3;
                    break;
                case 2430249: //motorcycle
                    mountid = 80001027;
                    expiration_days = 3;
                    break;
                case 2430225: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 10;
                    break;
                case 2430053: //croco 30 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430054: //black scooter 30 day
                    mountid = 1028;
                    expiration_days = 30;
                    break;
                case 2430055: //pink scooter 30 day
                    mountid = 1029;
                    expiration_days = 30;
                    break;
                case 2430257: //pink
                    mountid = 1029;
                    expiration_days = 7;
                    break;
                case 2430056: //mist rog 30 day
                    mountid = 1035;
                    expiration_days = 30;
                    break;
                case 2430057:
                    mountid = 1033;
                    expiration_days = 30;
                    break;
                case 2430079: //피시방 라이딩
                case 2430072: //ZD tiger 7 day
                    mountid = 1034;
                    expiration_days = 7;
                    break;

                case 2430073: //lion 15 day
                    mountid = 1036;
                    expiration_days = 15;
                    break;
                case 2430074: //unicorn 15 day
                    mountid = 1037;
                    expiration_days = 15;
                    break;
                case 2430272: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 3;
                    break;
                case 2430275: //spiegelmann
                    mountid = 80001033;
                    expiration_days = 7;
                    break;
                case 2430075: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 15;
                    break;
                case 2430076: //red truck 15 day
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430077: //gargoyle 15 day
                    mountid = 1040;
                    expiration_days = 15;
                    break;
                case 2430080: //shinjo 20 day
                    mountid = 1042;
                    expiration_days = 20;
                    break;
                case 2430082: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 7;
                    break;
                case 2430260: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 3;
                    break;
                case 2430091: //nightmare 10 day
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430092: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 10;
                    break;
                case 2430263: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 3;
                    break;
                case 2430093: //ostrich 10 day
                    mountid = 1051;
                    expiration_days = 10;
                    break;
                case 2430101: //pink bear 10 day
                    mountid = 1052;
                    expiration_days = 10;
                    break;
                case 2430102: //transformation robo 10 day
                    mountid = 1053;
                    expiration_days = 10;
                    break;
                case 2430103: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 30;
                    break;
                case 2430266: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 3;
                    break;
                case 2430265: //chariot
                    mountid = 1151;
                    expiration_days = 3;
                    break;
                case 2430258: //law officer
                    mountid = 1115;
                    expiration_days = 365;
                    break;
                case 2430117: //lion 1 year
                    mountid = 1036;
                    expiration_days = 365;
                    break;
                case 2430118: //red truck 1 year
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430119: //gargoyle 1 year
                    mountid = 1040;
                    expiration_days = 365;
                    break;
                case 2430120: //unicorn 1 year
                    mountid = 1037;
                    expiration_days = 365;
                    break;
                case 2430271: //owl 30 day
                    mountid = 1069;
                    expiration_days = 3;
                    break;
                case 2430136: //owl 30 day
                    mountid = 1069;
                    expiration_days = 30;
                    break;
                case 2430137: //owl 1 year
                    mountid = 1069;
                    expiration_days = 365;
                    break;
                case 2430145: //mothership
                    mountid = 1070;
                    expiration_days = 30;
                    break;
                case 2430146: //mothership
                    mountid = 1070;
                    expiration_days = 365;
                    break;
                case 2430147: //mothership
                    mountid = 1071;
                    expiration_days = 30;
                    break;
                case 2430148: //mothership
                    mountid = 1071;
                    expiration_days = 365;
                    break;
                case 2430135: //os4
                    mountid = 1065;
                    expiration_days = 15;
                    break;
                case 2430149: //leonardo 30 day
                    mountid = 1072;
                    expiration_days = 30;
                    break;
                case 2430262: //leonardo 30 day
                    mountid = 1072;
                    expiration_days = 3;
                    break;
                case 2430179: //witch 15 day
                    mountid = 1081;
                    expiration_days = 15;
                    break;
                case 2430264: //witch 15 day
                    mountid = 1081;
                    expiration_days = 3;
                    break;
                case 2430201: //giant bunny 60 day
                    mountid = 1096;
                    expiration_days = 60;
                    break;
                case 2430228: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 60;
                    break;
                case 2430276: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 15;
                    break;
                case 2430277: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 365;
                    break;
                case 2430283: //trojan
                    mountid = 1025;
                    expiration_days = 10;
                    break;
                case 2430291: //hot air
                    mountid = 1145;
                    expiration_days = -1;
                    break;
                case 2430293: //nadeshiko
                    mountid = 1146;
                    expiration_days = -1;
                    break;
                case 2430295: //pegasus
                    mountid = 1147;
                    expiration_days = -1;
                    break;
                case 2430297: //dragon
                    mountid = 1148;
                    expiration_days = -1;
                    break;
                case 2430299: //broom
                    mountid = 1149;
                    expiration_days = -1;
                    break;
                case 2430301: //cloud
                    mountid = 1150;
                    expiration_days = -1;
                    break;
                case 2430303: //chariot
                    mountid = 1151;
                    expiration_days = -1;
                    break;
                case 2430305: //nightmare
                    mountid = 1152;
                    expiration_days = -1;
                    break;
                case 2430307: //rog
                    mountid = 1153;
                    expiration_days = -1;
                    break;
                case 2430309: //mist rog
                    mountid = 1154;
                    expiration_days = -1;
                    break;
                case 2430311: //owl
                    mountid = 1156;
                    expiration_days = -1;
                    break;
                case 2430313: //helicopter
                    mountid = 1156;
                    expiration_days = -1;
                    break;
                case 2430315: //pentacle
                    mountid = 1118;
                    expiration_days = -1;
                    break;
                case 2430317: //frog
                    mountid = 1121;
                    expiration_days = -1;
                    break;
                case 2430319: //turtle
                    mountid = 1122;
                    expiration_days = -1;
                    break;
                case 2430321: //buffalo
                    mountid = 1123;
                    expiration_days = -1;
                    break;
                case 2430323: //tank
                    mountid = 1124;
                    expiration_days = -1;
                    break;
                case 2430325: //viking
                    mountid = 1129;
                    expiration_days = -1;
                    break;
                case 2430327: //pachinko
                    mountid = 1130;
                    expiration_days = -1;
                    break;
                case 2430329: //kurenai
                    mountid = 1063;
                    expiration_days = -1;
                    break;
                case 2430331: //horse
                    mountid = 1025;
                    expiration_days = -1;
                    break;
                case 2430333: //tiger
                    mountid = 1034;
                    expiration_days = -1;
                    break;
                case 2430335: //hyena
                    mountid = 1136;
                    expiration_days = -1;
                    break;
                case 2430337: //ostrich
                    mountid = 1051;
                    expiration_days = -1;
                    break;
                case 2430339: //low rider
                    mountid = 1138;
                    expiration_days = -1;
                    break;
                case 2430341: //napoleon
                    mountid = 1139;
                    expiration_days = -1;
                    break;
                case 2430343: //croking
                    mountid = 1027;
                    expiration_days = -1;
                    break;
                case 2430346: //lovely
                    mountid = 1029;
                    expiration_days = -1;
                    break;
                case 2430348: //retro
                    mountid = 1028;
                    expiration_days = -1;
                    break;
                case 2430350: //f1
                    mountid = 1033;
                    expiration_days = -1;
                    break;
                case 2430352: //power suit
                    mountid = 1064;
                    expiration_days = -1;
                    break;
                case 2430354: //giant rabbit
                    mountid = 1096;
                    expiration_days = -1;
                    break;
                case 2430356: //small rabit
                    mountid = 1101;
                    expiration_days = -1;
                    break;
                case 2430358: //rabbit rickshaw
                    mountid = 1102;
                    expiration_days = -1;
                    break;
                case 2430360: //chicken
                    mountid = 1054;
                    expiration_days = -1;
                    break;
                case 2430362: //transformer
                    mountid = 1053;
                    expiration_days = -1;
                    break;
                case 2430292: //hot air
                    mountid = 1145;
                    expiration_days = 90;
                    break;
                case 2430294: //nadeshiko
                    mountid = 1146;
                    expiration_days = 90;
                    break;
                case 2430296: //pegasus
                    mountid = 1147;
                    expiration_days = 90;
                    break;
                case 2430298: //dragon
                    mountid = 1148;
                    expiration_days = 90;
                    break;
                case 2430300: //broom
                    mountid = 1149;
                    expiration_days = 90;
                    break;
                case 2430302: //cloud
                    mountid = 1150;
                    expiration_days = 90;
                    break;
                case 2430304: //chariot
                    mountid = 1151;
                    expiration_days = 90;
                    break;
                case 2430306: //nightmare
                    mountid = 1152;
                    expiration_days = 90;
                    break;
                case 2430308: //rog
                    mountid = 1153;
                    expiration_days = 90;
                    break;
                case 2430310: //mist rog
                    mountid = 1154;
                    expiration_days = 90;
                    break;
                case 2430312: //owl
                    mountid = 1156;
                    expiration_days = 90;
                    break;
                case 2430314: //helicopter
                    mountid = 1156;
                    expiration_days = 90;
                    break;
                case 2430316: //pentacle
                    mountid = 1118;
                    expiration_days = 90;
                    break;
                case 2430318: //frog
                    mountid = 1121;
                    expiration_days = 90;
                    break;
                case 2430320: //turtle
                    mountid = 1122;
                    expiration_days = 90;
                    break;
                case 2430322: //buffalo
                    mountid = 1123;
                    expiration_days = 90;
                    break;
                case 2430326: //viking
                    mountid = 1129;
                    expiration_days = 90;
                    break;
                case 2430328: //pachinko
                    mountid = 1130;
                    expiration_days = 90;
                    break;
                case 2430330: //kurenai
                    mountid = 1063;
                    expiration_days = 90;
                    break;
                case 2430332: //horse
                    mountid = 1025;
                    expiration_days = 90;
                    break;
                case 2430334: //tiger
                    mountid = 1034;
                    expiration_days = 90;
                    break;
                case 2430336: //hyena
                    mountid = 1136;
                    expiration_days = 90;
                    break;
                case 2430338: //ostrich
                    mountid = 1051;
                    expiration_days = 90;
                    break;
                case 2430340: //low rider
                    mountid = 1138;
                    expiration_days = 90;
                    break;
                case 2430342: //napoleon
                    mountid = 1139;
                    expiration_days = 90;
                    break;
                case 2430344: //croking
                    mountid = 1027;
                    expiration_days = 90;
                    break;
                case 2430347: //lovely
                    mountid = 1029;
                    expiration_days = 90;
                    break;
                case 2430349: //retro
                    mountid = 1028;
                    expiration_days = 90;
                    break;
                case 2430351: //f1
                    mountid = 1033;
                    expiration_days = 90;
                    break;
                case 2430353: //power suit
                    mountid = 1064;
                    expiration_days = 90;
                    break;
                case 2430355: //giant rabbit
                    mountid = 1096;
                    expiration_days = 90;
                    break;
                case 2430357: //small rabit
                    mountid = 1101;
                    expiration_days = 90;
                    break;
                case 2430359: //rabbit rickshaw
                    mountid = 1102;
                    expiration_days = 90;
                    break;
                case 2430361: //chicken
                    mountid = 1054;
                    expiration_days = 90;
                    break;
                case 2430363: //transformer
                    mountid = 1053;
                    expiration_days = 90;
                    break;
                case 2430324: //high way
                    mountid = 1158;
                    expiration_days = -1;
                    break;
                case 2430345: //high way
                    mountid = 1158;
                    expiration_days = 90;
                    break;
                case 2430367: //law off
                    mountid = 1115;
                    expiration_days = 3;
                    break;
                case 2430365: //pony
                    mountid = 1025;
                    expiration_days = 365;
                    break;
                case 2430366: //pony
                    mountid = 1025;
                    expiration_days = 15;
                    break;
                case 2430369: //nightmare
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430392: //speedy
                    mountid = 80001038;
                    expiration_days = 90;
                    break;
                case 2430476: //red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430477: //red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430232: //fortune
                    mountid = 1106;
                    expiration_days = 10;
                    break;
                case 2430511: //spiegel
                    mountid = 80001033;
                    expiration_days = 15;
                    break;
                case 2430512: //rspiegel
                    mountid = 80001033;
                    expiration_days = 365;
                    break;
                case 2430536: //buddy buggy
                    mountid = 80001114;
                    expiration_days = 365;
                    break;
                case 2430537: //buddy buggy
                    mountid = 80001114;
                    expiration_days = 15;
                    break;
                case 2430229: //bunny rickshaw 60 day
                    mountid = 1102;
                    expiration_days = 60;
                    break;
                case 2430199: //santa sled
                    mountid = 1102;
                    expiration_days = 60;
                    break;
                case 2430206: //race
                    mountid = 1089;
                    expiration_days = 7;
                    break;
                case 2430211: //race
                    mountid = 80001009;
                    expiration_days = 30;
                    break;
                case 2350000:
                case 2350001:
                case 2430441: { //Character expansion slot.
                    c.getPlayer().dropMessage(MessageType.POPUP, "Character slots can only be expanded via the Cash Shop.");

                    break;
                }
                default: {
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    int npcId = ii.getScriptedItemNpc(itemId);
                    String script = ii.getScriptedItemScript(itemId);
                    //     String script = "consume_" + itemId; // for now
                    Item item = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
                    if (item == null || item.getItemId() != itemId || item.getQuantity() < 1 || npcId == 0) {
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                    NPCScriptManager.getInstance().start(c, npcId, script.isEmpty() ? null : script);
                    c.getSession().write(CWvsContext.enableActions());
                }
                System.out.println("New scripted item : " + toUse.getItemId());
                break;
            }
        }
        if (mountid > 0) {
            mountid = GameConstants.getSkillByJob(mountid, c.getPlayer().getJob());
            final int fk = GameConstants.getMountItem(mountid, c.getPlayer());
            if (fk > 0 && mountid < 80010000) { //TODO JUMP
                for (int i = 80001001; i < 80001999; i++) {
                    final Skill skill = SkillFactory.getSkill(i);
                    if (skill != null && GameConstants.getMountItem(skill.getId(), c.getPlayer()) == fk) {
                        mountid = i;
                        break;
                    }
                }
            }
            if (c.getPlayer().getSkillLevel(mountid) > 0) {
                c.getPlayer().dropMessage(5, "이미 스킬을 보유하고있습니다.");
            } else if (SkillFactory.getSkill(mountid) == null || GameConstants.getMountItem(mountid, c.getPlayer()) == 0) {
                c.getPlayer().dropMessage(5, "현재 이 아이템으로 스킬을 배울 수 없습니다.");
            } else if (expiration_days > 0) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(mountid), (byte) 1, (byte) 1, System.currentTimeMillis() + (long) (expiration_days * 24 * 60 * 60 * 1000));
                c.getPlayer().dropMessage(5, "새로운 빛이 발생하면서 새로운 스킬을 습득하였습니다.");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void ResetCoreAura(int slot, MapleClient c, MapleCharacter chr) {
        Item starDust = chr.getInventory(MapleInventoryType.USE).getItem((short) (byte) slot);
        if ((starDust == null) || (c.getPlayer().hasBlockedInventory())) {
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
        }
    }

    public static final void useInnerCirculator(LittleEndianAccessor slea, MapleClient c) {
        int itemid = slea.readInt();
        short slot = (short) slea.readInt();
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (item.getItemId() == itemid) {
            List<InnerSkillValueHolder> newValues = new LinkedList<>();
            int i = 0;
            for (InnerSkillValueHolder isvh : c.getPlayer().getInnerSkills()) {
                if (i == 0 && c.getPlayer().getInnerSkills().size() > 1 && itemid == 2701000) { //Ultimate Circulator
                    newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, true));
                } else {
                    newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, false));
                }
                //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), (byte) 0, (byte) 0);
                i++;
            }
            c.getPlayer().getInnerSkills().clear();
            byte ability = 1;
            for (InnerSkillValueHolder isvh : newValues) {
                c.getPlayer().getInnerSkills().add(isvh);
                c.getSession().write(CWvsContext.characterPotentialSet(ability, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank(), ability == 3));
                ability++;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);

            c.getPlayer().dropMessage(5, "이너 어빌리티가 재설정 되었습니다.");
            c.getPlayer().applyInners();
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseSummonBag(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && (c.getPlayer().getMapId() < 910000000 || c.getPlayer().getMapId() > 910000022)) {
            final Map<String, Integer> toSpawn = MapleItemInformationProvider.getInstance().getEquipStats(itemId);

            if (toSpawn == null) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            MapleMonster ht = null;
            int type = 0;
            for (Entry<String, Integer> i : toSpawn.entrySet()) {
                if (i.getKey().startsWith("mob") && Randomizer.nextInt(99) <= i.getValue()) {
                    ht = MapleLifeFactory.getMonster(Integer.parseInt(i.getKey().substring(3)));
                    chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
                }
            }
            if (ht == null) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseTreasureChest(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final short slot = slea.readShort();
        final int itemid = slea.readInt();

        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int reward;
        int keyIDforRemoval = 0;
        String box;

        switch (toUse.getItemId()) {
            case 4280000: // Gold box
                reward = RandomRewards.getGoldBoxReward();
                keyIDforRemoval = 5490000;
                box = "Gold";
                break;
            case 4280001: // Silver box
                reward = RandomRewards.getSilverBoxReward();
                keyIDforRemoval = 5490001;
                box = "Silver";
                break;
            default: // Up to no good
                return;
        }

        // Get the quantity
        int amount = 1;
        switch (reward) {
            case 2000004:
                amount = 200; // Elixir
                break;
            case 2000005:
                amount = 100; // Power Elixir
                break;
        }
        if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) > 0) {
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) amount);

            if (item == null) {
                chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) slot, (short) 1, true);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);
            c.getSession().write(InfoPacket.getShowItemGain(reward, (short) amount, true));

            if (GameConstants.gachaponRareItem(item.getItemId()) > 0) {
                World.Broadcast.broadcastSmega(CWvsContext.getGachaponMega(c.getPlayer().getName(), " : got a(n)", item, (byte) 2, "[" + box + " Chest]"));
            }
        } else {
            chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseCashItem(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || c.getPlayer().inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        //c.getPlayer().updateTick(slea.readInt());
        slea.skip(4);

        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1 || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        boolean used = false, cc = false;

        switch (itemId) {
            case 5040000:
            case 5040001:
            case 5040002:
            case 5040003:
            case 5040004:
            case 5040005:
            case 5040006:
            case 5040007:
            case 5040008:
            case 5041000:
            case 5041001:
            case 5041002:
            case 5041003:
            case 5041004:
            case 5041005:
            case 5041006:
            case 5041007: {
                used = UseTeleRock(slea, c, itemId);
                break;
            }
            case 5450005: {
                c.getPlayer().setConversation(4);
                c.getPlayer().getStorage().sendStorage(c, 1022005);
                break;
            }
            case 5050000: { // AP Reset
                Map<MapleStat, Integer> statupdate = new EnumMap<MapleStat, Integer>(MapleStat.class);
                final int apto = (int) slea.DecodeInt();
                final int apfrom = (int) slea.DecodeInt();

                if (apto == apfrom) {
                    break; // Hack
                }
                final int job = c.getPlayer().getJob();
                final PlayerStats playerst = c.getPlayer().getStat();
                used = true;

                switch (apto) { // AP to
                    case 64: // str
                        if (playerst.getStr() >= Short.MAX_VALUE) {
                            used = false;
                        }
                        break;
                    case 128: // dex
                        if (playerst.getDex() >= Short.MAX_VALUE) {
                            used = false;
                        }
                        break;
                    case 256: // int
                        if (playerst.getInt() >= Short.MAX_VALUE) {
                            used = false;
                        }
                        break;
                    case 512: // luk
                        if (playerst.getLuk() >= Short.MAX_VALUE) {
                            used = false;
                        }
                        break;
                    case 2048: // hp
                        if (playerst.getMaxHp() >= Integer.MAX_VALUE) {
                            used = false;
                        }
                        break;
                    case 8192: // mp
                        if (playerst.getMaxMp() >= Integer.MAX_VALUE) {
                            used = false;
                        }
                        break;
                }
                switch (apfrom) { // AP to
                    case 64: // str
                        if (playerst.getStr() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 1 && playerst.getStr() <= 35)) {
                            used = false;
                        }
                        break;
                    case 128: // dex
                        if (playerst.getDex() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 3 && playerst.getDex() <= 25) || (c.getPlayer().getJob() % 1000 / 100 == 4 && playerst.getDex() <= 25) || (c.getPlayer().getJob() % 1000 / 100 == 5 && playerst.getDex() <= 20)) {
                            used = false;
                        }
                        break;
                    case 256: // int
                        if (playerst.getInt() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 2 && playerst.getInt() <= 20)) {
                            used = false;
                        }
                        break;
                    case 512: // luk
                        if (playerst.getLuk() <= 4) {
                            used = false;
                        }
                        break;
                    case 2048: // hp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            used = false;
                            c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                        }
                        break;
                    case 8192: // mp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            used = false;
                            c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                        }
                        break;
                }
                if (used) {
                    switch (apto) { // AP to
                        case 64: { // str
                            final int toSet = playerst.getStr() + 1;
                            playerst.setStr((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Str, toSet);
                            break;
                        }
                        case 128: { // dex
                            final int toSet = playerst.getDex() + 1;
                            playerst.setDex((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Dex, toSet);
                            break;
                        }
                        case 256: { // int
                            final int toSet = playerst.getInt() + 1;
                            playerst.setInt((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Int, toSet);
                            break;
                        }
                        case 512: { // luk
                            final int toSet = playerst.getLuk() + 1;
                            playerst.setLuk((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Luk, toSet);
                            break;
                        }
                        case 2048: // hp
                            int maxhp = playerst.getMaxHp();
                            int oldhp = maxhp;
                            if (GameConstants.isBeginnerJob(job)) { // Beginner
                                maxhp += Randomizer.rand(4, 8);
                            } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212) || (job >= 1100 && job <= 1112) || (job >= 3100 && job <= 3112)) { // Warrior
                                maxhp += Randomizer.rand(36, 42);
                            } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 1200 && job <= 1212)) { // Magician
                                maxhp += Randomizer.rand(10, 12);
                            } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 2300 && job <= 2312)) { // Bowman
                                maxhp += Randomizer.rand(14, 18);
                            } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) {
                                maxhp += Randomizer.rand(24, 28);
                            } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) { // Pirate
                                maxhp += Randomizer.rand(16, 20);
                            } else if (job >= 2000 && job <= 2112) { // Aran
                                maxhp += Randomizer.rand(34, 38);
                            } else { // GameMaster
                                maxhp += Randomizer.rand(50, 100);
                            }
                            maxhp = Math.min(500000, Math.abs(maxhp));
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxHp(maxhp, c.getPlayer());
                            statupdate.put(MapleStat.MaxHP, (int) maxhp);
                            break;

                        case 8192: // mp
                            int maxmp = playerst.getMaxMp();

                            if (GameConstants.isBeginnerJob(job)) { // Beginner
                                maxmp += Randomizer.rand(6, 8);
                            } else if (job >= 3100 && job <= 3112) {
                                break;
                            } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112) || (job >= 2000 && job <= 2112)) { // Warrior
                                maxmp += Randomizer.rand(4, 9);
                            } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212) || (job >= 1200 && job <= 1212)) { // Magician
                                maxmp += Randomizer.rand(32, 36);
                            } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 532) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 2300 && job <= 2312)) { // Bowman
                                maxmp += Randomizer.rand(8, 10);
                            } else { // GameMaster
                                maxmp += Randomizer.rand(50, 100);
                            }
                            maxmp = Math.min(500000, Math.abs(maxmp));
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxMp(maxmp, c.getPlayer());
                            statupdate.put(MapleStat.MaxMP, (int) maxmp);
                            break;
                    }
                    switch (apfrom) { // AP from
                        case 64: { // str
                            final int toSet = playerst.getStr() - 1;
                            playerst.setStr((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Str, toSet);
                            break;
                        }
                        case 128: { // dex
                            final int toSet = playerst.getDex() - 1;
                            playerst.setDex((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Dex, toSet);
                            break;
                        }
                        case 256: { // int
                            final int toSet = playerst.getInt() - 1;
                            playerst.setInt((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Int, toSet);
                            break;
                        }
                        case 512: { // luk
                            final int toSet = playerst.getLuk() - 1;
                            playerst.setLuk((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.Luk, toSet);
                            break;
                        }
                        case 2048: // HP
                            int maxhp = playerst.getMaxHp();
                            int oldhp = maxhp;
                            if (GameConstants.isBeginnerJob(job)) { // Beginner
                                maxhp -= 12;
                            } else if ((job >= 200 && job <= 232) || (job >= 1200 && job <= 1212)) { // Magician
                                maxhp -= 10;
                            } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512) || (job >= 2300 && job <= 2312)) { // Bowman, Thief
                                maxhp -= 15;
                            } else if ((job >= 500 && job <= 532) || (job >= 1500 && job <= 1512)) { // Pirate
                                maxhp -= 22;
                            } else if (((job >= 100 && job <= 132) || job >= 1100 && job <= 1112) || (job >= 3100 && job <= 3112)) { // Soul Master
                                maxhp -= 32;
                            } else if ((job >= 2000 && job <= 2112) || (job >= 3200 && job <= 3212)) { // Aran
                                maxhp -= 40;
                            } else { // GameMaster
                                maxhp -= 20;
                            }
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMaxHp(maxhp, c.getPlayer());
                            statupdate.put(MapleStat.MaxHP, (int) maxhp);
                            break;
                        case 8192: // MP
                            int maxmp = playerst.getMaxMp();
                            if (GameConstants.isBeginnerJob(job)) { // Beginner
                                maxmp -= 8;
                            } else if (job >= 3100 && job <= 3112) {
                                break;
                            } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112)) { // Warrior
                                maxmp -= 4;
                            } else if ((job >= 200 && job <= 232) || (job >= 1200 && job <= 1212)) { // Magician
                                maxmp -= 30;
                            } else if ((job >= 500 && job <= 532) || (job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512) || (job >= 2300 && job <= 2312)) { // Pirate, Bowman. Thief
                                maxmp -= 10;
                            } else if (job >= 2000 && job <= 2112) { // Aran
                                maxmp -= 5;
                            } else { // GameMaster
                                maxmp -= 20;
                            }
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMaxMp(maxmp, c.getPlayer());
                            statupdate.put(MapleStat.MaxMP, (int) maxmp);
                            break;
                    }
                    c.getSession().write(CWvsContext.OnPlayerStatChanged(statupdate, true, c.getPlayer()));
                }
                break;
            }
            case 5050100: { //AP reset scroll. //MAY NEED CHECKING FOR STATS > 32k
                used = true;
                int str, dex, int_, luk, totalap;
                Map<MapleStat, Integer> statupdate = new EnumMap<MapleStat, Integer>(MapleStat.class);

                str = c.getPlayer().getStat().str - 4;
                dex = c.getPlayer().getStat().dex - 4;
                int_ = c.getPlayer().getStat().int_ - 4;
                luk = c.getPlayer().getStat().luk - 4;
                totalap = str + dex + int_ + luk;

                c.getPlayer().getStat().setStr((short) 4, c.getPlayer());
                c.getPlayer().getStat().setDex((short) 4, c.getPlayer());
                c.getPlayer().getStat().setInt((short) 4, c.getPlayer());
                c.getPlayer().getStat().setLuk((short) 4, c.getPlayer());

                c.getPlayer().gainAp((short) totalap);

                statupdate.put(MapleStat.Str, 4);
                statupdate.put(MapleStat.Dex, 4);
                statupdate.put(MapleStat.Int, 4);
                statupdate.put(MapleStat.Luk, 4);

                c.getSession().write(CWvsContext.OnPlayerStatChanged(statupdate, true, c.getPlayer()));
                break;
            }
            case 5051001: {
                MapleCharacter chr = c.getPlayer();
                Map<Skill, SkillEntry> skills = c.getPlayer().getSkills();
                int[] skillpoints = c.getPlayer().getRemainingSps();
                List<Pair<Skill, Byte>> toRemove = new ArrayList<Pair<Skill, Byte>>();
                for (Entry<Skill, SkillEntry> e : skills.entrySet()) {
                    if (!GameConstants.isBeginnerJob(e.getKey().getId() / 10000) && (e.getKey().getId() / 10000 <= 5112)) {
                        skillpoints[GameConstants.getSkillBookForSkill(e.getKey().getId())] += e.getValue().skillevel;
                        toRemove.add(new Pair<Skill, Byte>(e.getKey(), e.getValue().masterlevel));
                    }
                }
                for (Pair<Skill, Byte> s : toRemove) {
                    c.getPlayer().changeSkillLevel_NoSkip(s.getLeft(), (byte) 0, s.getRight());
                }
                c.getPlayer().setRemainingSps(skillpoints);
                c.getPlayer().updateSingleStat(MapleStat.SP, 0);
                used = true;
                break;
            }
            case 5050001: // SP Reset (1st job)
            case 5050002: // SP Reset (2nd job)
            case 5050003: // SP Reset (3rd job)
            case 5050004:  // SP Reset (4th job)
            case 5050005: //evan sp resets
            case 5050006:
            case 5050007:
            case 5050008:
            case 5050009: {
                if (itemId >= 5050005 && !GameConstants.isEvan(c.getPlayer().getJob())) {
                    c.getPlayer().dropMessage(1, "This reset is only for Evans.");
                    break;
                } //well i dont really care other than this o.o
                if (itemId < 5050005 && GameConstants.isEvan(c.getPlayer().getJob())) {
                    c.getPlayer().dropMessage(1, "This reset is only for non-Evans.");
                    break;
                } //well i dont really care other than this o.o
                int skill1 = slea.readInt();
                int skill2 = slea.readInt();

                Skill skillSPTo = SkillFactory.getSkill(skill1);
                Skill skillSPFrom = SkillFactory.getSkill(skill2);

                if (skillSPTo.isBeginnerSkill() || skillSPFrom.isBeginnerSkill()) {
                    c.getPlayer().dropMessage(1, "You may not add beginner skills.");
                    break;
                }
                if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) { //resistance evan
                    c.getPlayer().dropMessage(1, "You may not add different job skills.");
                    break;
                }
                //if (GameConstants.getJobNumber(skill1 / 10000) > GameConstants.getJobNumber(skill2 / 10000)) { //putting 3rd job skillpoints into 4th job for example
                //    c.getPlayer().dropMessage(1, "You may not add skillpoints to a higher job.");
                //    break;
                //}
                if ((c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel()) && c.getPlayer().getSkillLevel(skillSPFrom) > 0 && skillSPTo.canBeLearnedBy(c.getPlayer().getJob())) {
                    if (skillSPTo.isSkillNeedMasterLevel() && (c.getPlayer().getSkillLevel(skillSPTo) + 1 > c.getPlayer().getMasterLevel(skillSPTo))) {
                        c.getPlayer().dropMessage(1, "You will exceed the master level.");
                        break;
                    }
                    if (itemId >= 5050005) {
                        if (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 && GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1) {
                            c.getPlayer().dropMessage(1, "You may not add this job SP using this reset.");
                            break;
                        }
                    } else {
                        int theJob = GameConstants.getJobNumber(skill2 / 10000);
                        switch (skill2 / 10000) {
                            case 430:
                                theJob = 1;
                                break;
                            case 432:
                            case 431:
                                theJob = 2;
                                break;
                            case 433:
                                theJob = 3;
                                break;
                            case 434:
                                theJob = 4;
                                break;
                        }
                        if (theJob != itemId - 5050000) { //you may only subtract from the skill if the ID matches Sp reset
                            c.getPlayer().dropMessage(1, "You may not subtract from this skill. Use the appropriate SP reset.");
                            break;
                        }
                    }
                    final Map<Skill, SkillEntry> sa = new HashMap<>();
                    sa.put(skillSPFrom, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPFrom) - 1), c.getPlayer().getMasterLevel(skillSPFrom), SkillFactory.getDefaultSExpiry(skillSPFrom)));
                    sa.put(skillSPTo, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPTo) + 1), c.getPlayer().getMasterLevel(skillSPTo), SkillFactory.getDefaultSExpiry(skillSPTo)));
                    c.getPlayer().changeSkillsLevel(sa);
                    used = true;
                }
                break;
            }
            case 5062200:
            case 5062201: {
                final int iLine = slea.DecodeInt();
                c.getPlayer().dropMessage(5, "iLine : " + iLine);
                break;
            }
            case 5500000: { // Magic Hourglass 1 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 1;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500001: { // Magic Hourglass 7 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 7;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5330000:
            case 5330001: {
                if (!c.getPlayer().hasBlockedInventory()) {
                    c.getPlayer().setConversation(2);
                    c.getSession().write(CField.sendDuey((byte) 9, null, null));
                }
                break;
            }
            case 5500002: { // Magic Hourglass 20 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 20;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500005: { // Magic Hourglass 50 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 50;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500006: { // Magic Hourglass 99 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 99;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.
                                getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5060000: { // Item Tag
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());

                if (item != null && item.getOwner().equals("")) {
                    boolean change = true;
                    if (change) {
                        //item.setOwner(c.getPlayer().getName());
                        //c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    }
                }
                break;
            }
            case 5680015: {
                if (c.getPlayer().getFatigue() > 0) {
                    c.getPlayer().setFatigue(0);
                    used = true;
                }
                break;
            }
            case 5534000: { //tims lab
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() == 0) {
                        //   eq.renewPotential(0);
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(InventoryPacket.scrolledItem(toUse, item, false, true));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                    }
                } else {
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                }
                break;
            }
            case 5062006: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() >= 17) {
                        boolean potLock = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000) != null;
                        int line = potLock ? slea.readInt() : 0;
                        int toLock = potLock ? slea.readShort() : 0;
                        potLock = checkPotentialLock(c.getPlayer(), eq, line, toLock);
                        if (potLock) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000).getPosition(), (short) 1, false);
                        }
                        eq.renewPotential_OLD(5, line, toLock, false);
                        //   MapleInventoryManipulator.addById(c, 2430112, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(CWvsContext.InventoryPacket.scrolledItem(toUse, item, false, true));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "이 아이템에는 사용할 수 없습니다.");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "소비창의 슬롯이 부족합니다.");
                }
                break;
            }
            case 5062104:
            case 5062105:
            case 5062106: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null) {
                    final Equip eq = (Equip) item;
                    if (eq.getPotentialByLine(2) < 1) {
                        c.getPlayer().dropMessage(5, "세 번째 잠재 능력이 존재하지 않아 프리미엄 미라클 큐브 사용에 실패 하였습니다.");
                        break;
                    }
                    if (eq.getState() > 16) {
                        final List<List<StructItemOption>> pots = new LinkedList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().values());
                        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        int reqLevel = (ii.getReqLevel(eq.getItemId()) / 10);
                        if (reqLevel > 18) {
                            reqLevel = 19;
                        }
                        boolean v1 = false;
                        while (!v1) {
                            StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                            int line = 0;
                            if (itemId == 5062105) {
                                line = 1;
                            }
                            if (itemId == 5062106) {
                                line = 2;
                            }
                            if (pot != null && GameConstants.optionTypeFits(pot.optionType, eq.getItemId()) && GameConstants.potentialIDFits(pot.opID, eq.getState(), line) && !GameConstants.isBonusPot(pot.opID)) {
                                eq.setPotentialByLine(line, pot.opID);

                                String a = pot.opString;
                                String x = pot.data.values().toString();
                                if (x.contains("[")) {
                                    x = x.replace("[", "");
                                }
                                if (x.contains("]")) {
                                    x = x.replace("]", "");
                                }
                                if (x.contains(",")) {
                                    String[] z = x.split(",");
                                    x = z[0];
                                }
                                for (final String k : StructItemOption.types) {
                                    String b = ("#" + k);
                                    if (a.contains(b)) {
                                        a = a.replace(b, x);
                                    }
                                }
                                if (a.contains(" \\n  ")) {
                                    a = a.replace(" \\n  ", " ");
                                }
                                /*
                                if (a.contains("r%")) {
                                    a = a.replace("r%", "%");
                                }
                                 */
                                c.sendPacket(CWvsContext.yellowChat(a));

                                v1 = true;
                            }
                        }
                    }
                    c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eq.getPosition()));
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                    c.sendPacket(CWvsContext.InventoryPacket.scrolledItem(toUse, item, false, true));
                    c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    used = true;
                }
                break;
            }
            case 5062000:
            case 5062002:
            case 5062005: {
                if (!c.getChannelServer().getPotential()) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() > 0) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() > 16) {
                        final List<List<StructItemOption>> pots = new LinkedList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().values());
                        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        int reqLevel = (ii.getReqLevel(eq.getItemId()) / 10);
                        if (reqLevel > 18) {
                            reqLevel = 19;
                        }
                        int tCube = (itemId - 5062000);
                        final long reqMeso = GameConstants.getMagnifyPrice(eq);
                        if (c.getPlayer().getMeso() > reqMeso) {
                            int newState = 0;
                            int cubeUpgradeChance = 100;
                            switch (eq.getState()) {
                                case 17: {  // 레어 → 에픽 (3%)
                                    cubeUpgradeChance = (tCube == 5 ? 20 : tCube == 0 ? 20 : 40);
                                    break;
                                }
                                case 18: {  // 에픽 → 유니크 (1%)
                                    cubeUpgradeChance = (tCube == 5 ? 0 : tCube == 0 ? 10 : 20);
                                    break;
                                }
                                case 19: {  // 유니크 → 레전드리 (0.5%)
                                    cubeUpgradeChance = (tCube == 5 ? 0 : tCube == 0 ? 0 : 10);
                                    break;
                                }
                            }
                            newState = (Randomizer.nextInt(999) < cubeUpgradeChance ? (eq.getState() + 1) : (eq.getState()));
                            if (newState < 17) {
                                newState = 17;
                            }
                            if (newState > 20) {
                                newState = 20;
                            }
                            if (tCube == 0) {
                                if (newState > 19) {
                                    newState = 19;
                                }
                            }
                            if (tCube == 5) {
                                if (newState > 18) {
                                    newState = 18;
                                }
                            }

                            int potentialByLine = 2;
                            if (eq.getPotentialByLine(2) > 0) {
                                potentialByLine = 3;
                            }
                            c.getPlayer().gainMeso((int) -(reqMeso), true, false);
                            for (int i = 0; i < potentialByLine; i++) {
                                boolean rewarded = false;
                                while (!rewarded) {
                                    StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                                    if (pot != null && GameConstants.optionTypeFits(pot.optionType, eq.getItemId()) && GameConstants.potentialIDFits(pot.opID, newState, i) && !GameConstants.isBonusPot(pot.opID)) {
                                        eq.setPotentialByLine(i, pot.opID);

                                        String a1 = pot.data.values().toString();
                                        if (a1.contains("[")) {
                                            a1 = a1.replace("[", "");
                                        }
                                        if (a1.contains("]")) {
                                            a1 = a1.replace("]", "");
                                        }
                                        String a2 = pot.opString;

                                        if (a2.contains("몬스터 처치 시 #prop% 확률로 #HP의 HP 회복")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#HP", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("몬스터 처치 시 #prop% 확률로 #MP의 MP 회복")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#MP", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #HP의 HP 회복")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#HP", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #MP의 MP 회복")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#MP", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #level레벨 중독효과 적용")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[2]);
                                            a2 = a2.replace("#level", a1.split(", ")[1] + " ");
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #level레벨 스턴효과 적용")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[2]);
                                            a2 = a2.replace("#level", a1.split(", ")[1] + " ");
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #level레벨 슬로우효과 적용")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[2]);
                                            a2 = a2.replace("#level", a1.split(", ")[1] + " ");
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #level레벨 암흑효과 적용")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[2]);
                                            a2 = a2.replace("#level", a1.split(", ")[1] + " ");
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #level레벨 빙결효과 적용")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[2]);
                                            a2 = a2.replace("#level", a1.split(", ")[1] + " ");
                                        }
                                        if (a2.contains("공격 시 #prop% 확률로 #level레벨 봉인효과 적용")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[2]);
                                            a2 = a2.replace("#level", a1.split(", ")[1] + " ");
                                        }
                                        if (a2.contains("피격 시 #prop% 확률로 #ignoreDAM의 데미지 무시")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#ignoreDAM", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("피격 시 #prop% 확률로 데미지의 #ignoreDAMr% 무시")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#ignoreDAMr", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("피격 시 #prop% 확률로 #time초간 무적")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#time", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("#prop% 확률로 받은 피해의 #DAMreflect%를 반사")) {
                                            a2 = a2.replace("#prop", a1.split(", ")[0]);
                                            a2 = a2.replace("#DAMreflect", a1.split(", ")[1]);
                                        }
                                        if (a2.contains("#incSTR")) {
                                            a2 = a2.replace("#incSTR", a1);
                                        }
                                        if (a2.contains("#incDEX")) {
                                            a2 = a2.replace("#incDEX", a1);
                                        }
                                        if (a2.contains("#incINT")) {
                                            a2 = a2.replace("#incINT", a1);
                                        }
                                        if (a2.contains("#incLUK")) {
                                            a2 = a2.replace("#incLUK", a1);
                                        }
                                        if (a2.contains("#incMHP")) {
                                            a2 = a2.replace("#incMHP", a1);
                                        }
                                        if (a2.contains("#incMMP")) {
                                            a2 = a2.replace("#incMMP", a1);
                                        }
                                        if (a2.contains("#incACC")) {
                                            a2 = a2.replace("#incACC", a1);
                                        }
                                        if (a2.contains("#incEVA")) {
                                            a2 = a2.replace("#incEVA", a1);
                                        }
                                        if (a2.contains("#incSpeed")) {
                                            a2 = a2.replace("#incSpeed", a1);
                                        }
                                        if (a2.contains("#incJump")) {
                                            a2 = a2.replace("#incJump", a1);
                                        }
                                        if (a2.contains("#incPAD")) {
                                            a2 = a2.replace("#incPAD", a1);
                                        }
                                        if (a2.contains("#incMAD")) {
                                            a2 = a2.replace("#incMAD", a1);
                                        }
                                        if (a2.contains("#incPDD")) {
                                            a2 = a2.replace("#incPDD", a1);
                                        }
                                        if (a2.contains("#incMDD")) {
                                            a2 = a2.replace("#incMDD", a1);
                                        }
                                        if (a2.contains("#incCr")) {
                                            a2 = a2.replace("#incCr", a1);
                                        }
                                        if (a2.contains("#incCriticaldamageMin")) {
                                            a2 = a2.replace("#incCriticaldamageMin", a1);
                                        }
                                        if (a2.contains("#incCriticaldamageMax")) {
                                            a2 = a2.replace("#incCriticaldamageMax", a1);
                                        }
                                        if (a2.contains("#incDAMr")) {
                                            a2 = a2.replace("#incDAMr", a1);
                                        }
                                        if (a2.contains("#incAllskill")) {
                                            a2 = a2.replace("#incAllskill", a1);
                                        }
                                        if (a2.contains("#incTerR")) {
                                            a2 = a2.replace("#incTerR", a1);
                                        }
                                        if (a2.contains("#incAsrR")) {
                                            a2 = a2.replace("#incAsrR", a1);
                                        }
                                        if (a2.contains("#ignoreTargetDEF")) {
                                            a2 = a2.replace("#ignoreTargetDEF", a1);
                                        }
                                        if (a2.contains("#time")) {
                                            a2 = a2.replace("#time", a1);
                                        }
                                        if (a2.contains("#mpconReduce")) {
                                            a2 = a2.replace("#mpconReduce", a1);
                                        }
                                        if (a2.contains("#RecoveryHP")) {
                                            a2 = a2.replace("#RecoveryHP", a1);
                                        }
                                        if (a2.contains("#RecoveryMP")) {
                                            a2 = a2.replace("#RecoveryMP", a1);
                                        }
                                        if (a2.contains("#RecoveryUP")) {
                                            a2 = a2.replace("#RecoveryUP", a1);
                                        }
                                        if (a2.contains("#reduceCooltime")) {
                                            a2 = a2.replace("#reduceCooltime", a1);
                                        }
                                        if (a2.contains("#incMesoProp")) {
                                            a2 = a2.replace("#incMesoProp", a1);
                                        }
                                        if (a2.contains("#incRewardProp")) {
                                            a2 = a2.replace("#incRewardProp", a1);
                                        }
                                        if (a2.contains("#prop")) {
                                            a2 = a2.replace("#prop", a1);
                                        }
                                        if (a2.contains("#reduceCooltime")) {
                                            a2 = a2.replace("#reduceCooltime", a1);
                                        }
                                        if (a2.contains(" \\n  (최대 5초까지 감소)")) {
                                            a2 = a2.replace(" \\n  (최대 5초까지 감소)", "");
                                        }
                                        if (a2.contains("1, 20")) {
                                            a2 = a2.replace("1, 20", "20");
                                        }
                                        if (a2.contains("1, 25")) {
                                            a2 = a2.replace("1, 25", "25");
                                        }
                                        if (a2.contains("1, 30")) {
                                            a2 = a2.replace("1, 30", "30");
                                        }
                                        if (a2.contains("1, 35")) {
                                            a2 = a2.replace("1, 35", "35");
                                        }
                                        if (a2.contains("1, 40")) {
                                            a2 = a2.replace("1, 40", "40");
                                        }
                                        if (a2.contains("1, 1, 1, 1")) {
                                            a2 = a2.replace("1, 1, 1, 1", "1");
                                        }
                                        if (a2.contains("2, 2, 2, 2")) {
                                            a2 = a2.replace("2, 2, 2, 2", "2");
                                        }
                                        if (a2.contains("3, 3, 3, 3")) {
                                            a2 = a2.replace("3, 3, 3, 3", "3");
                                        }
                                        if (a2.contains("4, 4, 4, 4")) {
                                            a2 = a2.replace("4, 4, 4, 4", "4");
                                        }
                                        if (a2.contains("5, 5, 5, 5")) {
                                            a2 = a2.replace("5, 5, 5, 5", "5");
                                        }
                                        if (a2.contains("6, 6, 6, 6")) {
                                            a2 = a2.replace("6, 6, 6, 6", "6");
                                        }
                                        if (a2.contains("7, 7, 7, 7")) {
                                            a2 = a2.replace("7, 7, 7, 7", "7");
                                        }
                                        if (a2.contains("8, 8, 8, 8")) {
                                            a2 = a2.replace("8, 8, 8, 8", "8");
                                        }
                                        if (a2.contains("9, 9, 9, 9")) {
                                            a2 = a2.replace("9, 9, 9, 9", "9");
                                        }
                                        if (a2.contains("10, 10, 10, 10")) {
                                            a2 = a2.replace("10, 10, 10, 10", "10");
                                        }
                                        if (a2.contains("11, 11, 11, 11")) {
                                            a2 = a2.replace("11, 11, 11, 11", "10");
                                        }
                                        if (a2.contains("r%")) {
                                            a2 = a2.replace("r%", "%");
                                        }
                                        c.sendPacket(CUserLocal.chatMsg(ChatType.SpeakerBridge, "[" + (i + 1) + "] " + a2));
                                        rewarded = true;
                                    }
                                }
                            }
                            c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eq.getPosition()));
                            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                            c.sendPacket(CWvsContext.InventoryPacket.scrolledItem(toUse, item, false, true));
                            c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                            if (tCube != 5) {
                                MapleInventoryManipulator.addById(c, itemId == 5062002 ? 2430481 : 2430112, (short) 1, null);
                            }
                            used = true;
                        } else {
                            c.getPlayer().dropMessage(1, "'" + ii.getName(eq.getItemId()) + "'(을)를 감정하기 위해서는 " + c.getPlayer().getNum(reqMeso) + "메소가 필요합니다.");
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(5, "소비 아이템 여유 공간이 부족하여 잠재 능력 재설정을 실패하였습니다.");
                }
                break;
            }
            /*
             boolean potLock = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000) != null;
             int line = potLock ? slea.readInt() : 0;
             int toLock = potLock ? slea.readShort() : 0;
             potLock = checkPotentialLock(c.getPlayer(), eq, line, toLock);
             if (potLock) {
             MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000).getPosition(), (short) 1, false);
             }
             eq.renewPotential_OLD(itemId == 5062000 ? 0 : itemId == 5062100 ? 1 : itemId == 5062002 ? 5 : 4, line, toLock, false);
             if (itemId != 5062100) {
             MapleInventoryManipulator.addById(c, itemId == 5062000 ? 2430112 : itemId == 5062002 ? 2430481 : 2430759, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
             }
             c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
             c.getSession().write(CWvsContext.InventoryPacket.scrolledItem(toUse, item, false, true));
             c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
             used = true;
             } else {
             c.getPlayer().dropMessage(1, "이 아이템에는 사용 할 수 없습니다.");
             }
             } else {
             c.getPlayer().dropMessage(1, "소비창의 인벤토리 공간이 충분하지 않습니다.");
             }
             break;
             }
             */

            case 5062500: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() >= 17) {
                        //    eq.renewEditionalPotential(1);
                        eq.renewPotential_OLD(6, 0, (short) 0, true);
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(CWvsContext.InventoryPacket.scrolledItem(toUse, item, false, true));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "이 아이템에는 사용할 수 없습니다.");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "소비창의 슬롯이 부족합니다.");
                }
                break;
            }
            case 5521000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                if (item != null && !ItemFlag.TradedOnceWithinAccount.check(item.getFlag())) {
                    if (MapleItemInformationProvider.getInstance().isShareTagEnabled(item.getItemId())) {
                        short flag = item.getFlag();
                        if (ItemFlag.Untradable.check(flag)) {
                            flag -= ItemFlag.Untradable.getValue();
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KarmasCissors.getValue();
                            flag |= ItemFlag.TradedOnceWithinAccount.getValue();
                        }
                        item.setFlag(flag);
                        c.getPlayer().forceReAddItem_NoUpdate(item, type);
                        c.getSession().write(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, c.getPlayer()));
                        used = true;
                    }
                }
                break;
            }
            case 5520001: //p.karma
            case 5520000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                if (item != null && (!ItemFlag.KarmasCissors.check(item.getFlag()) || !ItemFlag.PreventSlipping.check(item.getFlag()))) {
                    if ((itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId())) || (itemId == 5520001 && MapleItemInformationProvider.getInstance().isPKarmaEnabled(item.getItemId()))) {
                        short flag = item.getFlag();
                        if (ItemFlag.Untradable.check(flag)) {
                            flag -= ItemFlag.Untradable.getValue();
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KarmasCissors.getValue();
                        } else {
                            flag |= ItemFlag.PreventSlipping.getValue();
                        }
                        item.setFlag(flag);
                        c.getPlayer().forceReAddItem_NoUpdate(item, type);
                        c.sendPacket(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, c.getPlayer()));
                        used = true;
                    }
                }
                break;
            }
            case 5610001:
            case 5610000: { // Vega 30
                slea.readInt(); // Inventory type, always eq
                final short dst = (short) slea.readInt();
                slea.readInt(); // Inventory type, always use
                final short src = (short) slea.readInt();
                used = UseUpgradeScroll(src, dst, (short) 2, c, c.getPlayer(), itemId, false); //cannot use ws with vega but we dont care
                cc = used;
                break;
            }
            case 5060001: { // Sealing Lock
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.Locked.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061000: { // Sealing Lock 7 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.Locked.getValue();
                    item.setFlag(flag);
                    item.setExpiration(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061001: { // Sealing Lock 30 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.Locked.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061002: { // Sealing Lock 90 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.Locked.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061003: { // Sealing Lock 365 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.Locked.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5063000: {
                Equip toScroll;
                short dst = slea.readShort();
                if (dst < 0) {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                } else {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
                }
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (toScroll != null && toScroll.getType() == 1) { //equip
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.LuckyDayScroll.getValue();
                    toScroll.setFlag(flag);
                    c.getPlayer().forceReAddItem_Flag(toScroll, dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                    used = true;
                }
                break;
            }
            case 5064100:
            case 5064101: {
                Equip toScroll;
                short dst = slea.readShort();
                if (dst < 0) {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                } else {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
                }
                if (toScroll != null && toScroll.getType() == 1) {
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.SafetyScroll.getValue();
                    toScroll.setFlag(flag);
                    c.getPlayer().forceReAddItem_Flag(toScroll, dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                    c.getPlayer().getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), c.getPlayer()));
                    used = true;
                }
                break;
            }
            case 5064300:
            case 5064301: {
                Equip toScroll;
                short dst = slea.readShort();
                if (dst < 0) {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                } else {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
                }
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (toScroll != null && toScroll.getType() == 1) { //equip
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.SafetyScroll.getValue();
                    toScroll.setFlag(flag);
                    c.getPlayer().forceReAddItem_Flag(toScroll, dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                    c.getPlayer().getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), c.getPlayer()));
                    used = true;
                }
                break;
            }
            case 5155000: {
                c.getPlayer().updateInfoQuest(7784, "sw=1");
                used = true;
                break;
            }
            case 5060004: {
                slea.readInt();
                int nSlot = slea.readInt();
                Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) nSlot);
                if (item == null) {
                    return;
                }
                if (item.getQuantity() < 1) {
                    return;
                }
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                boolean cUse = false;
                int rScroll = 0;
                int rChance = 0;
                if (ii.getName(item.getItemId()).contains("60%")) {
                    for (int i = 0; i < GameConstants.scroll_60.length; i++) {
                        if (item.getItemId() == GameConstants.scroll_60[i]) {
                            cUse = true;
                            rChance = (int) Math.floor(Math.random() * GameConstants.scroll_60.length);
                            rScroll = GameConstants.scroll_60[rChance];
                        }
                    }
                }
                if (ii.getName(item.getItemId()).contains("10%")) {
                    for (int i = 0; i < GameConstants.scroll_10.length; i++) {
                        if (item.getItemId() == GameConstants.scroll_10[i]) {
                            cUse = true;
                            rChance = (int) Math.floor(Math.random() * GameConstants.scroll_10.length);
                            rScroll = GameConstants.scroll_10[rChance];
                        }
                    }
                }
                if (ii.getName(item.getItemId()).contains("70%")) {
                    for (int i = 0; i < GameConstants.scroll_70.length; i++) {
                        if (item.getItemId() == GameConstants.scroll_70[i]) {
                            cUse = true;
                            rChance = (int) Math.floor(Math.random() * GameConstants.scroll_70.length);
                            rScroll = GameConstants.scroll_70[rChance];
                        }
                    }
                }
                if (ii.getName(item.getItemId()).contains("30%")) {
                    for (int i = 0; i < GameConstants.scroll_30.length; i++) {
                        if (item.getItemId() == GameConstants.scroll_30[i]) {
                            cUse = true;
                            rChance = (int) Math.floor(Math.random() * GameConstants.scroll_30.length);
                            rScroll = GameConstants.scroll_30[rChance];
                        }
                    }
                }
                if (ii.getName(item.getItemId()).contains("30%")) {
                    for (int i = 0; i < GameConstants.scroll_30.length; i++) {
                        if (item.getItemId() == GameConstants.scroll_30[i]) {
                            cUse = true;
                            rChance = (int) Math.floor(Math.random() * GameConstants.scroll_30.length);
                            rScroll = GameConstants.scroll_30[rChance];
                        }
                    }
                }
                if (item.getItemId() == 2049100) {
                    cUse = true;
                    int cRand = Randomizer.nextInt(100);
                    rScroll = 2049116;
                    if (cRand < 7) {
                        rScroll = 2049122;
                    }
                }
                if (!cUse) {
                    c.getPlayer().dropMessage(1, "해당 [" + ii.getName(item.getItemId()) + "] 주문서는 '주문의 흔적'을 사용 할 수 없습니다.");
                    break;
                }
                if (rScroll != 0) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, item.getPosition(), (short) 1, false);
                    MapleInventoryManipulator.addById(c, rScroll, (short) 1, null);
                    c.getSession().write(InfoPacket.getShowItemGain(rScroll, (short) 1, true));
                    String say = "\r\n#d주문의 흔적#k을 사용하여 #d" + ii.getName(item.getItemId()) + "#k를 성공적으로 변환하였습니다.#d";
                    say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i" + rScroll + "# #z" + rScroll + "# 1개";
                    c.sendPacket(CField.NPCPacket.getNPCTalk(9010000, (byte) 0, say, "00 01", (byte) 0));
                    used = true;
                }
                break;
            }
            case 5060002:   // 프리미엄 부화기
            case 5060005:   // 프리미엄 부화기
            case 5060007:   // 크리스탈 천칭
            case 5060008: { // 매지컬 티포트
                slea.readInt();
                int nSlot = slea.readInt();
                Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((short) nSlot);
                if (item == null || item.getQuantity() <= 0) {
                    return;
                }
                if (getIncubatedItems(c, itemId)) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
                    used = true;
                }
                break;
            }
            case 5065000: {
                MapleItemInformationProvider.getInstance().getItemEffect(2022986).applyTo(c.getPlayer());
                used = true;
                break;
            }
            case 5065100: {
                int eff = Randomizer.rand(2022982, 2022986);
                MapleItemInformationProvider.getInstance().getItemEffect(eff).applyTo(c.getPlayer());
                used = true;
                break;
            }
            /*
            case 5060004:
            case 5060003: {//peanut
                Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId == 5060003 ? 4170023 : 4170024);
                if (item == null || item.getQuantity() <= 0) { // hacking{
                    return;
                }
                if (getIncubatedItems(c, itemId)) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
                    used = true;
                }
                break;
            }
             */
            case 5063100:
            case 5064000:
            case 5064002: {
                // System.out.println("ITEM 5064000: slea..." + slea.toString());
                // ITEM 5064000: slea...Data: F5 FF 00
                //  final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                //  final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                Equip toScroll;
                short dst = slea.readShort();
                if (dst < 0) {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                } else {
                    toScroll = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
                }
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (toScroll != null && toScroll.getType() == 1) { //equip
                    if (((Equip) toScroll).getEnhance() >= 12) {
                        break; //cannot be used
                    }
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.ProtectScroll.getValue();
                    toScroll.setFlag(flag);
                    c.getPlayer().forceReAddItem_Flag(toScroll, dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                    c.getPlayer().getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), c.getPlayer()));
                    used = true;
                }
                break;
            }
            case 5070000: { // Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(2, sb.toString()));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5071000: { // Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    c.getChannelServer().broadcastSmegaPacket(CWvsContext.serverNotice(2, sb.toString()));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5077000: { // 3 line Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final byte numLines = slea.readByte();
                    if (numLines > 3) {
                        return;
                    }
                    final List<String> messages = new LinkedList<String>();
                    String message;
                    for (int i = 0; i < numLines; i++) {
                        message = slea.readMapleAsciiString();
                        if (message.length() > 65) {
                            break;
                        }
                        messages.add(c.getPlayer().getName() + " : " + message);
                    }
                    final boolean ear = slea.readByte() > 0;

                    World.Broadcast.broadcastSmega(CWvsContext.tripleSmega(messages, ear, c.getChannel()));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5073000: { // Heart Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;
                    World.Broadcast.broadcastSmega(CWvsContext.serverNotice(9, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5074000: { // Skull Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;

                    World.Broadcast.broadcastSmega(CWvsContext.serverNotice(22, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5072000: { // Super Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;

                    World.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5076000: { // Item Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() > 0;

                    Item item = null;
                    if (slea.readByte() == 1) { //item
                        byte invType = (byte) slea.readInt();
                        byte pos = (byte) slea.readInt();
                        if (pos <= 0) {
                            invType = -1;
                        }
                        item = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
                    }
                    World.Broadcast.broadcastSmega(CWvsContext.itemMegaphone(sb.toString(), ear, c.getChannel(), item));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5075000: // MapleTV Messenger
            case 5075001: // MapleTV Star Messenger
            case 5075002: { // MapleTV Heart Messenger
                c.getPlayer().dropMessage(5, "There are no MapleTVs to broadcast the message to.");
                break;
            }
            case 5075003:
            case 5075004:
            case 5075005: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                int tvType = itemId % 10;
                if (tvType == 3) {
                    slea.readByte(); //who knows
                }
                boolean ear = tvType != 1 && tvType != 2 && slea.readByte() > 1; //for tvType 1/2, there is no byte. 
                MapleCharacter victim = tvType == 1 || tvType == 4 ? null : c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString()); //for tvType 4, there is no string.
                if (tvType == 0 || tvType == 3) { //doesn't allow two
                    victim = null;
                } else if (victim == null) {
                    c.getPlayer().dropMessage(1, "That character is not in the channel.");
                    break;
                }
                String message = slea.readMapleAsciiString();
                World.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, c.getChannel(), c.getPlayer().getName() + " : " + message, ear));
                used = true;
                break;
            }
            case 5090100: // Wedding Invitation Card
            case 5090000: { // Note
                final String sendTo = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                c.getPlayer().sendNote(sendTo, msg);
                used = true;
                break;
            }
            case 5100000: { // Congratulatory Song
                c.getPlayer().getMap().broadcastMessage(CField.musicChange("Jukebox/Congratulation"));
                used = true;
                break;
            }
            case 5190001:
            case 5190002:
            case 5190003:
            case 5190004:
            case 5190005:
            case 5190006:
            case 5190010:
            case 5190011:
            case 5190012:
            case 5190000: { // Pet Flags
                final int uniqueid = (int) slea.readLong();
                MaplePet pet = null;
                for (Item pe : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (pe.getUniqueId() == uniqueid) {
                        pet = pe.getPet();
                    }
                }
                if (pet == null) {
                    break;
                }
                PetFlag zz = PetFlag.getByAddId(itemId);
                if (zz != null && !zz.check(pet.getFlags())) {
                    pet.setFlags(pet.getFlags() | zz.getValue());
                    c.getSession().write(CPet.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    c.getSession().write(CWvsContext.enableActions());
                    c.sendPacket(CUserLocal.setPetFlag(uniqueid, true, zz.getValue()));
                    used = true;
                }
                break;
            }
            case 5191001:
            case 5191002:
            case 5191003:
            case 5191004:
            case 5191000: { // Pet Flags
                final int uniqueid = (int) slea.readLong();
                MaplePet pet = null;
                for (Item pe : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (pe.getUniqueId() == uniqueid) {
                        pet = pe.getPet();
                    }
                }
                if (pet == null) {
                    break;
                }
                PetFlag zz = PetFlag.getByDelId(itemId);
                if (zz != null && zz.check(pet.getFlags())) {
                    pet.setFlags(pet.getFlags() - zz.getValue());
                    c.getSession().write(CPet.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    c.getSession().write(CWvsContext.enableActions());
                    c.sendPacket(CUserLocal.setPetFlag(uniqueid, false, zz.getValue()));
                    used = true;
                }
                break;
            }
            case 5501001:
            case 5501002: { //expiry mount
                final Skill skil = SkillFactory.getSkill(slea.readInt());
                if (skil == null || skil.getId() / 10000 != 8000 || c.getPlayer().getSkillLevel(skil) <= 0 || !skil.isTimeLimited() || GameConstants.getMountItem(skil.getId(), c.getPlayer()) <= 0) {
                    break;
                }
                final long toAdd = (itemId == 5501001 ? 30 : 60) * 24 * 60 * 60 * 1000L;
                final long expire = c.getPlayer().getSkillExpiry(skil);
                if (expire < System.currentTimeMillis() || (long) (expire + toAdd) >= System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)) {
                    break;
                }
                c.getPlayer().changeSingleSkillLevel(skil, c.getPlayer().getSkillLevel(skil), c.getPlayer().getMasterLevel(skil), (long) (expire + toAdd));
                used = true;
                break;
            }
            case 5170000: { // Pet name change
                final int uniqueid = (int) slea.readLong();
                MaplePet pet = null;
                for (Item pe : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (pe.getUniqueId() == uniqueid) {
                        pet = pe.getPet();
                    }
                }
                if (pet == null) {
                    break;
                }
                String nName = slea.readMapleAsciiString();
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    pet.setName(nName);
                    c.getSession().write(CPet.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    c.getSession().write(CWvsContext.enableActions());
                    c.getPlayer().getMap().broadcastMessage(CCashShop.changePetName(c.getPlayer(), nName, c.getPlayer().getPetIndex(pet)));
                    used = true;
                }
                break;
            }
            case 5700000: {
                slea.skip(8);
                if (c.getPlayer().getAndroid() == null) {
                    break;
                }
                String nName = slea.readMapleAsciiString();
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    c.getPlayer().getAndroid().setName(nName);
                    c.getPlayer().setAndroid(c.getPlayer().getAndroid()); //respawn it
                    used = true;
                }
                break;
            }
            case 5240000:
            case 5240001:
            case 5240002:
            case 5240003:
            case 5240004:
            case 5240005:
            case 5240006:
            case 5240007:
            case 5240008:
            case 5240009:
            case 5240010:
            case 5240011:
            case 5240012:
            case 5240013:
            case 5240014:
            case 5240015:
            case 5240016:
            case 5240017:
            case 5240018:
            case 5240019:
            case 5240020:
            case 5240021:
            case 5240022:
            case 5240023:
            case 5240024:
            case 5240025:
            case 5240026:
            case 5240027:
            case 5240029:
            case 5240030:
            case 5240031:
            case 5240032:
            case 5240033:
            case 5240034:
            case 5240035:
            case 5240036:
            case 5240037:
            case 5240038:
            case 5240039:
            case 5240040:
            case 5240028:
            case 5240059:
            case 5240060:
            case 5240061: {
                MaplePet pet = c.getPlayer().getPet(0);
                if (pet == null) {
                    break;
                }
                if (!pet.canConsume(itemId)) {
                    pet = c.getPlayer().getPet(1);
                    if (pet != null) {
                        if (!pet.canConsume(itemId)) {
                            pet = c.getPlayer().getPet(2);
                            if (pet != null) {
                                if (!pet.canConsume(itemId)) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                final byte petindex = c.getPlayer().getPetIndex(pet);
                pet.setFullness(100);
                if (pet.getCloseness() < 30000) {
                    if (pet.getCloseness() + (100 * c.getChannelServer().getTraitRate()) > 30000) {
                        pet.setCloseness(30000);
                    } else {
                        pet.setCloseness(pet.getCloseness() + (100 * c.getChannelServer().getPetClosenessRate()));
                    }
                    if (pet.getCloseness() >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                        pet.setLevel(pet.getLevel() + 1);
                        c.getSession().write(EffectPacket.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet)));
                        c.getPlayer().getMap().broadcastMessage(CPet.showPetLevelUp(c.getPlayer(), petindex));
                    }
                }
                c.getSession().write(CPet.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CPet.commandResponse(c.getPlayer().getId(), (byte) 1, petindex, true, true), true);
                used = true;
                break;
            }
            case 5230001:
            case 5230000: {// owl of minerva
                final int itemSearch = slea.readInt();
                final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
                if (hms.size() > 0) {
                    c.getSession().write(CWvsContext.getOwlSearched(itemSearch, hms));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(1, "Unable to find the item.");
                }
                break;
            }
            case 5080000:
            case 5080001:
            case 5080002:
            case 5080003: {
                final Point pos = c.getPlayer().getPosition();
                used = true;
                List<MapleMapObjectType> list = new LinkedList<MapleMapObjectType>();
                list.add(MapleMapObjectType.NPC);
                list.add(MapleMapObjectType.MESSAGEBOX);
                list.add(MapleMapObjectType.HIRED_MERCHANT);
                list.add(MapleMapObjectType.SHOP);
                if (!c.getPlayer().getMap().getMapObjectsInRange(pos, 30000, list).isEmpty()) {
                    used = false;
                    OutPacket mplew = new OutPacket(2);
                    mplew.writeShort(SendPacketOpcode.MESSAGEBOX_CREATE_FAILED.getValue());
                    c.getSession().write(mplew.getPacket());
                    break;
                }
                list.clear();
                if (used) {
                    String owner = c.getPlayer().getName();
                    String message = slea.readMapleAsciiString();
                    MapleMessageBox mmb = new MapleMessageBox(itemId, pos, owner, message);
                    c.getPlayer().getMap().spawnMessageBox(mmb);
                }
                break;
            }
            case 5281003:
            case 5281004: {
                Point it = new Point(-110, -82);
                Point rb = new Point(110, 83);
                Rectangle bounds = MapleStatEffect.calculateBoundingBox(c.getPlayer().getPosition(), c.getPlayer().isFacingLeft(), it, rb, 0);
                MapleMist mist = new MapleMist(bounds, itemId);
                c.getSession().write(EffectPacket.showSmellEffect(-1, 45, itemId));
                c.getPlayer().getMap().broadcastMessage(EffectPacket.showSmellEffect(c.getPlayer().getId(), 45, itemId));
                MapleItemInformationProvider.getInstance().getItemEffect(itemId == 5281003 ? 2028151 : 2028152).applyTo(c.getPlayer());
                c.getPlayer().getMap().spawnMist(mist, 100000, true);
                c.getSession().write(CWvsContext.enableActions());
                used = true;
                break;
            }
            case 5370001:
            case 5370000: { // Chalkboard
                for (MapleEventType t : MapleEventType.values()) {
                    final MapleEvent e = ChannelServer.getInstance(c.getChannel()).getEvent(t);
                    if (e.isRunning()) {
                        for (int i : e.getType().mapids) {
                            if (c.getPlayer().getMapId() == i) {
                                c.getPlayer().dropMessage(5, "You may not use that here.");
                                c.getSession().write(CWvsContext.enableActions());
                                return;
                            }
                        }
                    }
                }
                c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                break;
            }
            case 5452001:
            case 5450003:
            case 5450004:
            case 5450006:
            case 5450007:
            case 5450000: { // Mu Mu the Travelling Merchant
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(5, "You may not use this command here.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                }
                if (itemId == 5450000) {
                    c.getPlayer().removeItem(5450000, -1);
                }
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                } else if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getEventInstance() != null || c.getPlayer().getMapId() >= 990000000) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                } else if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                } else {
                    MapleShopFactory.getInstance().getShop(9090000).sendShop(c);
                }
                //used = true;
                break;
            }
            case 5300000:
            case 5300001:
            case 5300002: { // Cash morphs
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                ii.getItemEffect(itemId).applyTo(c.getPlayer());
                used = true;
                break;
            }
            case 5044000: //텔레포트 월드맵
            case 5044001:
            case 5044002:
            case 5044006:
            case 5044007: {
                // 05 0F C3 2B 09 00 
                // 20 F7 4C 00 : itemId
                // 00 
                // 20 2F F6 05
                int portalnum = slea.readByte();
                MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(slea.readInt());
                MaplePortal targetPortal = target.getPortal(portalnum);
                //toUse.setNextConsume(System.currentTimeMillis() + (30 * 60 * 1000));
                //c.sendPacket(CWvsContext.itemCooldown(toUse.getItemId(), toUse.getUniqueId()));
                c.getPlayer().changeMap(target, targetPortal);
                break;
            }
            case 5062402:
            case 5062400: {
                final MapleInventory mInven = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
                Equip shapeID = (Equip) mInven.getItem((byte) slea.readInt());
                Equip functionID = (Equip) mInven.getItem((byte) slea.readInt());
                if (shapeID.getFusionAnvil() != 0) {
                    functionID.setFusionAnvil(shapeID.getFusionAnvil());
                } else {
                    functionID.setFusionAnvil(Integer.valueOf(((Integer) shapeID.getItemId()).toString().substring(3, 7)));
                }
                c.sendPacket(CWvsContext.InventoryPacket.updateSpecialItemUse(functionID, (byte) 1, c.getPlayer()));
                c.sendPacket(CWvsContext.cashLookChangeResult(true, itemId));
                c.getPlayer().equipChanged();
                used = true;
                break;
            }
            default:
                if (itemId / 10000 == 512) {
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    String msg = ii.getMsg(itemId);
                    final String ourMsg = slea.readMapleAsciiString();
//                    if (!msg.contains("%s")) {
//                        msg = ourMsg;
//                    } else {
//                        msg = msg.replaceFirst("%s", c.getPlayer().getName());
//                        if (!msg.contains("%s")) {
//                            msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
//                        } else {
//                            try {
//                                msg = msg.replaceFirst("%s", ourMsg);
//                            } catch (Exception e) {
//                                msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
//                            }
//                        }
//                    }
//                    if (ourMsg.startsWith(msg)) {
                    c.getPlayer().getMap().floatNotice(ourMsg, itemId, false);

                    final int buff = ii.getStateChangeItem(itemId);
                    if (buff != 0) {
                        for (MapleCharacter mChar : c.getPlayer().getMap().getCharactersThreadsafe()) {
                            ii.getItemEffect(buff).applyTo(mChar);
                        }
                    }
                    used = true;
                } else if (itemId / 10000 == 510) {
                    c.getPlayer().getMap().floatNotice(c.getPlayer().getName(), itemId, true);
                    used = true;
                } else if (itemId / 10000 == 520) {
                    final int mesars = MapleItemInformationProvider.getInstance().getMeso(itemId);
                    if (mesars > 0 && c.getPlayer().getMeso() < (Integer.MAX_VALUE - mesars)) {
                        used = true;
                        if (Math.random() > 0.1) {
                            final int gainmes = Randomizer.nextInt(mesars);
                            c.getPlayer().gainMeso(gainmes, false);
                            c.sendPacket(CUserLocal.useLuckyBagSucceded(gainmes));
                        } else {
                            c.sendPacket(CUserLocal.useLuckyBagFailed());
                        }
                    }
                } else if (itemId / 10000 == 562) {
                    if (UseSkillBook(slot, itemId, c, c.getPlayer())) {
                        c.getPlayer().gainSP(1);
                    } //this should handle removing
                } else if (itemId / 10000 == 553) {
                    UseRewardItem(slot, itemId, c, c.getPlayer());// this too
                } else if (itemId / 10000 != 519) {
                    System.out.println("Unhandled CS item : " + itemId);
                    System.out.println(slea.toString(true));
                }
                break;
        }

        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short) 1, false, true);
        }
        c.getSession().write(CWvsContext.enableActions());
        if (cc) {
            if (!c.getPlayer().isAlive() || c.getPlayer().getEventInstance() != null || FieldLimitType.ChannelSwitch.check(c.getPlayer().getMap().getFieldLimit())) {
                c.getPlayer().dropMessage(1, "Auto relog failed.");
                return;
            }
            c.getPlayer().dropMessage(5, "Auto relogging. Please wait.");
            c.getPlayer().fakeRelog();
            if (c.getPlayer().getScrolledPosition() != 0) {
                c.getSession().write(CWvsContext.pamSongUI());
            }
        }
    }

    public static final void Pickup_Player(final LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        chr.updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        slea.skip(1);
        final Point Client_Reportedpos = slea.readPos();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);

        if (ob == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != 1) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 5000 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_CLIENT, String.valueOf(Distance));
            } else if (chr.getPosition().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_SERVER);
            }
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        int mesos = splitMeso / toGive.size();
                        if (mapitem.getDropper() instanceof MapleMonster && m.getStat().incMesoProp > 0) {
                            //mesos += Math.floor((m.getStat().incMesoProp * mesos) / 100.0f);
                        }
                        m.gainMeso(mesos, true);
                        c.getSession().write(CWvsContext.enableActions());
                    }
                    int mesos = mapitem.getMeso() - splitMeso;
                    if (mapitem.getDropper() instanceof MapleMonster && chr.getStat().incMesoProp > 0) {
                        //mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                    c.getSession().write(CWvsContext.enableActions());
                } else {
                    int mesos = mapitem.getMeso();
                    if (chr.getSkillLevel(4210012) > 0) {
                        float addSkillMeso = 1 + ((float) ((2 * chr.getSkillLevel(4210012)) / 100.0));
                        //mesos *= addSkillMeso;
                    }
                    if (mapitem.getDropper() instanceof MapleMonster && chr.getStat().incMesoProp > 0) {
                        //mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                    c.getSession().write(CWvsContext.enableActions());
                }
                removeItem(chr, mapitem, ob);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId())) {
                    c.getSession().write(CWvsContext.enableActions());
                    c.getPlayer().dropMessage(5, "This item cannot be picked up.");
                } else if (c.getPlayer().inPVP() && Integer.parseInt(c.getPlayer().getEventInstance().getProperty("ice")) == c.getPlayer().getId()) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    c.getSession().write(CWvsContext.enableActions());
                } else if (useItem(c, mapitem.getItemId())) {
                    removeItem(c.getPlayer(), mapitem, ob);
                    //another hack
                    if (mapitem.getItemId() / 10000 == 291) {
                        c.getPlayer().getMap().broadcastMessage(CField.getCapturePosition(c.getPlayer().getMap()));
                        c.getPlayer().getMap().broadcastMessage(CField.resetCapture());
                    }
                } else if (mapitem.getItemId() / 10000 != 291 && MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                    if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true); //hack check
                    }
                    if (!"".equals(mapitem.getInstanceOwner())) {
                        if (!mapitem.getInstanceOwner().equals(chr.getName())) {
                            chr.dropMessage(MessageType.ERROR, "You cannot pick up this drop.  It is not instanced for you.");
                            c.getSession().write(CWvsContext.enableActions());
                        } else {
                            mapitem.getItem().setInstanceOwner("");
                            MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster);
                            removeItem(chr, mapitem, ob);
                        }
                    } else {
                        /* 차원의 통행증 */
                        if (mapitem.getItemId() != 4001022) {
                            MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster);
                        } else {
                            c.sendPacket(CWvsContext.enableActions());
                        }
                        removeItem(chr, mapitem, ob);
                    }
                } else {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    c.getSession().write(CWvsContext.enableActions());
                }
            }

            final MapleCharacter user = c.getPlayer();
            /* 월묘의 떡 */
            if (user.getMapId() == 910010000) {
                if (mapitem.getItemId() == 4001101) {
                    final EventManager em = c.getChannelServer().getEventSM().getEventManager("partyquest_henesys");
                    if (em != null) {
                        if (Integer.parseInt(em.getProperty("state")) < 11) {
                            final EventInstanceManager eim = user.getEventInstance();
                            if (eim != null) {
                                em.setProperty("state", (Integer.parseInt(em.getProperty("state")) + 1) + "");
                                user.getMap().broadcastMessage(CWvsContext.getTopMsg("어흥이를 위해 월묘의 떡 " + (Integer.parseInt(em.getProperty("state")) - 1) + "개를 모았습니다."));
                                if ((Integer.parseInt(em.getProperty("state")) - 1) == 10) {
                                    user.getMap().resetFully(false);
                                    user.getMap().setSpawns(false);
                                    user.getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                                    user.getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                                }
                            }
                        }
                    }
                }
            }
            /* 차원의 균열 */
            if (user.getMapId() == 922010100) {
                if (mapitem.getItemId() == 4001022) {
                    final EventManager em = c.getChannelServer().getEventSM().getEventManager("partyquest_ludibrium");
                    if (em != null) {
                        if ((Integer.parseInt(em.getProperty("state")) - 1) < 20) {
                            em.setProperty("state", (Integer.parseInt(em.getProperty("state")) + 1) + "");
                            user.getMap().broadcastMessage(CWvsContext.getTopMsg("통행권 " + (Integer.parseInt(em.getProperty("state")) - 1) + "장을 모았습니다."));
                        }
                        if ((Integer.parseInt(em.getProperty("state")) - 1) == 20) {
                            if (user.getParty() != null) {
                                for (MaplePartyCharacter pUser : user.getParty().getMembers()) {
                                    if (pUser != null) {
                                        MapleCharacter cUser = c.getChannelServer().getMapFactory().getMap(user.getMapId()).getCharacterByName(pUser.getName());
                                        if (cUser != null) {
                                            cUser.updateOneInfoQuest(10200000, "pq_ludibrium_clear", "1");
                                        }
                                    }
                                }
                            }
                            user.getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                            user.getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                            user.getMap().broadcastMessage(CField.environmentChange("gate", 2));
                            user.getMap().broadcastMessage(CWvsContext.getTopMsg("다음 스테이지로 통하는 포탈이 열렸습니다."));
                            user.getMap().floatNotice("통행증을 모두 모았습니다. 레드 벌룬에게 말을 걸어 다음 단계로 이동해 주세요.", 5120018, false);
                        }
                    }
                }
            }
            /*
            if (c.getPlayer().getMapId() == 923040200) {
                if (mapitem.getItemId() == 2430364) {
                    if (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {
                        c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("공기방울 " + c.getPlayer().itemQuantity(2430364) + "개를 모았습니다."));
                        if (c.getPlayer().itemQuantity(2430364) == 20) {
                            c.getPlayer().getMap().setMobGen(9300446, false);
                            c.getPlayer().getMap().setMobGen(9300447, false);
                            c.getPlayer().getMap().killAllMonsters(true);
                            c.getPlayer().removeAll(2430364);
                            c.getPlayer().getMap().floatNotice("숨 쉬기가 점점 어려워져요. 어서 빨리 와주세요.", 5120052, false);
                            c.getPlayer().getEventInstance().setProperty("airBubble", "1");
                            c.getPlayer().getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                            c.getPlayer().getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                        }
                    }
                }
            }
            int lp = 0;
            int check = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (c.getPlayer().getMapId() == 920010400) {
                lp = (check == 1 ? 4001056 : check == 2 ? 4001057 : check == 3 ? 4001058 : check == 4 ? 4001059 : check == 5 ? 4001060 : check == 6 ? 4001061 : 4001062);
                if (mapitem.getItemId() == lp) {
                    c.getPlayer().getMap().floatNotice(MapleItemInformationProvider.getInstance().getName(lp) + "를 얻었습니다. LP를 중앙의 측음기 앞에 떨어트리면, 음악이 연주됩니다.", 5120019, false);
                }
            }
            if (c.getPlayer().getMapId() == 920010200) {
                if (mapitem.getItemId() == 4001050) {
                    if (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {
                        c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("작은 조각 " + c.getPlayer().itemQuantity(4001050) + "개를 모았습니다."));
                        if (c.getPlayer().itemQuantity(4001050) == 30) {
                            c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").setProperty("3stage", "1");
                            c.getPlayer().getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                            c.getPlayer().getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                            c.getPlayer().getMap().floatNotice("모든 작은 조각을 획득 하였습니다. 시종 이크에게서 세 번째 조각을 받고 중앙 룸으로 이동하여 주세요.", 5120019, false);
                        }
                    }
                }
            }
            if (c.getPlayer().getMapId() == 925100100) {
                if (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {
                    if (mapitem.getItemId() == 4001120) {
                        c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("신입 해적의 증표 " + c.getPlayer().itemQuantity(4001120) + " / 10"));
                        if (c.getPlayer().itemQuantity(4001120) == 10) {
                            for (final MaplePartyCharacter chrs : c.getPlayer().getParty().getMembers()) {
                                final MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterById(chrs.getId());
                                curChar.removeAll(4001120);
                            }
                            c.getPlayer().getMap().broadcastMessage(CField.achievementRatio(50));
                            c.getPlayer().getMap().setMobGen(9300114, false);
                            c.getPlayer().getMap().killAllMonsters(true);
                            c.getChannelServer().getEventSM().getEventManager("PQ_Mulung").setProperty("2stage", "1");
                            c.getPlayer().getEventInstance().schedule("spawn", 5000);
                            c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("구옹이 포탈의 첫 번째 봉인을 풀었습니다."));
                            c.getPlayer().getMap().floatNotice("신입 해적의 증표를 모두 구했군. 이제 중급 해적의 증표를 구해야 하네.", 5120020, false);
                        }
                    }
                    if (mapitem.getItemId() == 4001121) {
                        c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("중급 해적의 증표 " + c.getPlayer().itemQuantity(4001121) + " / 10"));
                        if (c.getPlayer().itemQuantity(4001121) == 10) {
                            for (final MaplePartyCharacter chrs : c.getPlayer().getParty().getMembers()) {
                                final MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterById(chrs.getId());
                                curChar.removeAll(4001121);
                            }
                            c.getPlayer().getMap().broadcastMessage(CField.achievementRatio(60));
                            c.getPlayer().getMap().setMobGen(9300115, false);
                            c.getPlayer().getMap().killAllMonsters(true);
                            c.getChannelServer().getEventSM().getEventManager("PQ_Mulung").setProperty("2stage", "2");
                            c.getPlayer().getEventInstance().schedule("spawn", 5000);
                            c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("구옹이 포탈의 두 번째 봉인을 풀었습니다."));
                            c.getPlayer().getMap().floatNotice("중급 해적의 증표를 모두 구했군. 이제 마지막 증표를 구할 차례네.", 5120020, false);
                        }
                    }
                    if (mapitem.getItemId() == 4001122) {
                        c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("고참 해적의 증표 " + c.getPlayer().itemQuantity(4001122) + " / 10"));
                        if (c.getPlayer().itemQuantity(4001122) == 10) {
                            for (final MaplePartyCharacter chrs : c.getPlayer().getParty().getMembers()) {
                                final MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterById(chrs.getId());
                                curChar.removeAll(4001122);
                            }
                            c.getPlayer().getMap().broadcastMessage(CField.achievementRatio(70));
                            c.getPlayer().getMap().setMobGen(9300116, false);
                            c.getPlayer().getMap().killAllMonsters(true);
                            c.getChannelServer().getEventSM().getEventManager("PQ_Mulung").setProperty("2stage", "3");
                            c.getPlayer().getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                            c.getPlayer().getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                            c.getChannelServer().broadcastPacket(CWvsContext.getTopMsg("구옹이 포탈의 마지막 봉인을 풀었습니다."));
                            c.getPlayer().getMap().floatNotice("고참 해적의 증표를 모두 구했군. 이제 마지막 봉인을 풀겠네. 포탈을 타고 이동해주게.", 5120020, false);
                        }
                    }
                }
            }
            if (c.getPlayer().getMapId() == 926100201) {
                if (mapitem.getItemId() == 4001134) {
                    c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Romeo").setProperty("data_a", "1");
                    c.getPlayer().getMap().floatNotice("알카드노의 실험 자료를 획득하셨습니다. 로미오에게 실험 자료를 가져다 주세요.", 5120021, false);
                }
            }
            if (c.getPlayer().getMapId() == 926100202) {
                if (mapitem.getItemId() == 4001135) {
                    c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Romeo").setProperty("data_j", "1");
                    c.getPlayer().getMap().floatNotice("제뉴미스트의 실험 자료를 획득하셨습니다. 로미오에게 실험 자료를 가져다 주세요.", 5120021, false);
                }
            }
            if (c.getPlayer().getMapId() == 926110201) {
                if (mapitem.getItemId() == 4001134) {
                    c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Juliet").setProperty("data_a", "1");
                    c.getPlayer().getMap().floatNotice("알카드노의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.", 5120022, false);
                }
            }
            if (c.getPlayer().getMapId() == 926110202) {
                if (mapitem.getItemId() == 4001135) {
                    c.getChannelServer().getEventSM().getEventManager("PQ_Magatia_Juliet").setProperty("data_j", "1");
                    c.getPlayer().getMap().floatNotice("제뉴미스트의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.", 5120022, false);
                }
            }
             */
        } finally {
            lock.unlock();
        }
    }

    public static final void Pickup_Pet(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        //System.out.println("PETS: " + slea.toString());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte petz = (byte) slea.readInt();
        final MaplePet pet = chr.getPet(petz);
        slea.skip(1); // [4] Zero, [4] Seems to be tickcount, [1] Always zero
        chr.updateTick(slea.readInt());
        final Point Client_Reportedpos = slea.readPos();
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);

        if (ob == null || pet == null) {
            // System.out.println("Ob or pet is null");
            return;

        }
        final MapleMapItem mapitem = (MapleMapItem) ob;

        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 10000 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_CLIENT, String.valueOf(Distance));
            } else if (pet.getPos().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_SERVER);
            }
            ///System.out.println("mapitem.getMeso() : " + mapitem.getMeso());
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null) {
                    int pMember = 0;
                    for (final MapleCharacter mc : chr.getMap().getAllCharactersThreadsafe()) {
                        if (mc != null) {
                            if (chr.getMapId() == mc.getMapId()) {
                                pMember++;
                            }
                        }
                    }
                    int nMeso = (mapitem.getMeso() / pMember);
                    chr.gainMeso(nMeso, true);
                } else {
                    int nMeso = mapitem.getMeso();
                    chr.gainMeso(nMeso, true);
                }
                removeItem_Pet(chr, mapitem, petz);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                    c.getSession().write(CWvsContext.enableActions());
                } else if (useItem(c, mapitem.getItemId())) {
                    removeItem_Pet(chr, mapitem, petz);
                } else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                    if (mapitem.getItem().getQuantity() >= 50 || mapitem.getItemId() == 2340000) {
                        c.setMonitored(true); //hack check
                    }
                    MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster, false);
                    removeItem_Pet(chr, mapitem, petz);
                }
            }

            final MapleCharacter user = c.getPlayer();
            /* 월묘의 떡 */
            if (user.getMapId() == 910010000) {
                if (mapitem.getItemId() == 4001101) {
                    final EventManager em = c.getChannelServer().getEventSM().getEventManager("partyquest_henesys");
                    if (em != null) {
                        if (Integer.parseInt(em.getProperty("state")) < 11) {
                            final EventInstanceManager eim = user.getEventInstance();
                            if (eim != null) {
                                em.setProperty("state", (Integer.parseInt(em.getProperty("state")) + 1) + "");
                                user.getMap().broadcastMessage(CWvsContext.getTopMsg("어흥이를 위해 월묘의 떡 " + (Integer.parseInt(em.getProperty("state")) - 1) + "개를 모았습니다."));
                                if ((Integer.parseInt(em.getProperty("state")) - 1) == 10) {
                                    user.getMap().resetFully(false);
                                    user.getMap().setSpawns(false);
                                    user.getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                                    user.getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                                }
                            }
                        }
                    }
                }
            }

        } finally {
            lock.unlock();
        }
    }

    public static final boolean useItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleStatEffect eff = ii.getItemEffect(id);
            if (eff == null) {
                return false;
            }
            //must hack here for ctf
            if (id / 10000 == 291) {
                boolean area = false;
                for (Rectangle rect : c.getPlayer().getMap().getAreas()) {
                    if (rect.contains(c.getPlayer().getTruePosition())) {
                        area = true;
                        break;
                    }
                }
                if (!c.getPlayer().inPVP() || (c.getPlayer().getTeam() == (id - 2910000) && area)) {
                    return false; //dont apply the consume
                }
            }
            final int consumeval = eff.getConsume();

            if (consumeval > 0) {
                consumeItem(c, eff);
                consumeItem(c, ii.getItemEffectEX(id));
                c.getSession().write(InfoPacket.getShowItemGain(id, (byte) 1));
                return true;
            }

            switch (id) {
                case 2432391:
                case 2432392: {
                    int gExp = ((c.getPlayer().getNeededExp() / 100) * 5);
                    c.getPlayer().gainExp(gExp, true, true, false);
                    c.getSession().write(CWvsContext.enableActions());
                    return true;
                }
                case 2432393: {
                    c.getPlayer().gainMeso(Randomizer.rand(100000, 200000), true);
                    c.getSession().write(CWvsContext.enableActions());
                    return true;
                }
                case 2432394: {
                    c.getPlayer().gainMeso(Randomizer.rand(200000, 400000), true);
                    c.getSession().write(CWvsContext.enableActions());
                    return true;
                }
            }
        }
        return false;
    }

    public static final void consumeItem(final MapleClient c, final MapleStatEffect eff) {
        if (eff == null) {
            return;
        }
        if (eff.getConsume() == 2) {
            if (c.getPlayer().getParty() != null && c.getPlayer().isAlive()) {
                for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                    final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                    if (chr != null && chr.isAlive()) {
                        eff.applyTo(chr);
                    }
                }
            } else {
                eff.applyTo(c.getPlayer());
            }
        } else if (c.getPlayer().isAlive()) {
            eff.applyTo(c.getPlayer());
        }
    }

    public static final void removeItem_Pet(final MapleCharacter chr, final MapleMapItem mapitem, int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), pet));
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    public static final void removeItem(final MapleCharacter chr, final MapleMapItem mapitem, final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    public static final void addMedalString(final MapleCharacter c, final StringBuilder sb) {

        final Item medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -21);
        final Item medal_cash = c.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -121);
        if (medal_cash != null) {
            sb.append("<");
            if (medal_cash.getItemId() == 1142257 && GameConstants.isAdventurer(c.getJob())) {
                MapleQuestStatus stat = c.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
                if (stat != null && stat.getCustomData() != null) {
                    sb.append(stat.getCustomData());
                    sb.append("'s Successor");
                } else {
                    sb.append(MapleItemInformationProvider.getInstance().getName(medal_cash.getItemId()));
                }
            } else {
                sb.append(MapleItemInformationProvider.getInstance().getName(medal_cash.getItemId()));
            }
            sb.append("> ");
        } else {
            if (medal != null) { // Medal
                sb.append("<");
                if (medal.getItemId() == 1142257 && GameConstants.isAdventurer(c.getJob())) {
                    MapleQuestStatus stat = c.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
                    if (stat != null && stat.getCustomData() != null) {
                        sb.append(stat.getCustomData());
                        sb.append("'s Successor");
                    } else {
                        sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                    }
                } else {
                    sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                }
                sb.append("> ");
            }
        }
    }

    private static final boolean getIncubatedItems(MapleClient c, int itemId) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
            c.getPlayer().dropMessage(5, "장비창 1개, 소비창 3개, 기타창 1개의 공간이 필요합니다.");
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        String invoked = null;
        try {
            invoked = (String) EtcScriptInvoker.getInvocable("etc/incubator.js").invokeFunction("run", (itemId % 10));
            int npc = 9050008;
            String txt = "피그미 에그 안에서 아이템이 나왔습니다.";
            if (itemId == 5060007) {
                invoked = (String) EtcScriptInvoker.getInvocable("etc/cLibar.js").invokeFunction("run", (itemId % 10));
                npc = 2007;
                txt = "크리스탈 천칭에서 아이템이 나왔습니다.";
            }
            String[] ids = invoked.split(",");
            if (ids.length < 4) {
                c.getPlayer().dropMessage(1, "현재 부화기를 사용할 수 없는 기간입니다.");
                return false;
            }
            c.getSession().write(CField.NPCPacket.getNPCTalk(npc, (byte) 0, txt + "\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i" + Integer.parseInt(ids[2]) + "# #z" + Integer.parseInt(ids[2]) + "# " + Short.parseShort(ids[3]) + "개", "00 00", (byte) 0));
            //c.getSession().write(MaplePacketCreator.getIncubatorResult(Integer.parseInt(ids[2]), (short) Integer.parseInt(ids[3]), 0,(short) 0,0)); // 피넛 머신
            if (GameConstants.getInventoryType(Integer.parseInt(ids[2])) == MapleInventoryType.EQUIP) {
                final Equip item = (Equip) ii.getEquipById(Integer.parseInt(ids[2]));
                double potential = (int) Math.floor(Math.random() * 100);
                MapleInventoryManipulator.addbyItem(c, item.copy());
            } else {
                MapleInventoryManipulator.addById(c, Integer.parseInt(ids[2]), Short.parseShort(ids[3]), null);
            }
            c.getSession().write(InfoPacket.getShowItemGain(Integer.parseInt(ids[2]), Short.parseShort(ids[3]), true));
            return true;
        } catch (Exception e) {
            System.err.println("Error executing Etc script. Path: " + "etc/incubator.js" + "\nException " + e);
            return false;
        }
    }

    public static final void CharnameCHANGE(final LittleEndianAccessor slea, final MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2350002 && !c.getPlayer().hasBlockedInventory()) {
            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
            c.getSession().write(CWvsContext.serverNotice(1, "캐릭터 이름 변경 신청이 완료 되었습니다.\r\n캐릭터 선택 화면에서 변경을 원하는\r\n캐릭터를 선택한 후, 이름 변경을\r\n 완료해 주세요."));
            //쿼리로 set 1 and 0
        } else {
            c.getPlayer().dropMessage(1, "캐릭터 이름 변경쿠폰을 사용할 수 없습니다.");
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void OwlMinerva(final LittleEndianAccessor slea, final MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2310000 && !c.getPlayer().hasBlockedInventory()) {
            final int itemSearch = slea.readInt();
            final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
            if (hms.size() > 0) {
                c.getSession().write(CWvsContext.getOwlSearched(itemSearch, hms));
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
            } else {
                c.getPlayer().dropMessage(1, "Unable to find the item.");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void Owl(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().haveItem(5230000, 1, true, false) || c.getPlayer().haveItem(2310000, 1, true, false)) {
            if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
                c.getSession().write(CWvsContext.getOwlOpen());
            } else {
                c.getPlayer().dropMessage(5, "This can only be used inside the Free Market.");
                c.getSession().write(CWvsContext.enableActions());
            }
        }
    }

    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static final void OwlWarp(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isAlive()) {
            c.getSession().write(CWvsContext.getOwlMessage(4));
            return;
        } else if (c.getPlayer().getTrade() != null) {
            c.getSession().write(CWvsContext.getOwlMessage(7));
            return;
        }
        if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022 && !c.getPlayer().hasBlockedInventory()) {
            final int id = slea.readInt();
            slea.skip(1);
            final int map = slea.readInt();
            if (map >= 910000001 && map <= 910000022) {
                c.getSession().write(CWvsContext.getOwlMessage(0));
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                List<MapleMapObject> objects;
                switch (OWL_ID) {
                    case 0:
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getOwnerId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getStoreId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        final MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (ob instanceof IMaplePlayerShop) {
                            final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                            if (ips instanceof HiredMerchant) {
                                merchant = (HiredMerchant) ips;
                            }
                        }
                        break;
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getPlayer())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        c.getPlayer().setPlayerShop(merchant);
                        c.getSession().write(CPlayerShop.getHiredMerch(c.getPlayer(), merchant, false));
                    } else {
                        if (!merchant.isOpen() || !merchant.isAvailable()) {
                            c.getPlayer().dropMessage(1, "The owner of the store is currently undergoing store maintenance. Please try again in a bit.");
                        } else {
                            if (merchant.getFreeSlot() == -1) {
                                c.getPlayer().dropMessage(1, "You can't enter the room due to full capacity.");
                            } else if (merchant.isInBlackList(c.getPlayer().getName())) {
                                c.getPlayer().dropMessage(1, "You may not enter this store.");
                            } else {
                                c.getPlayer().setPlayerShop(merchant);
                                merchant.addVisitor(c.getPlayer());
                                c.getSession().write(CPlayerShop.getHiredMerch(c.getPlayer(), merchant, false));
                            }
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The room is already closed.");
                }
            } else {
                c.getSession().write(CWvsContext.getOwlMessage(23));
            }
        } else {
            c.getSession().write(CWvsContext.getOwlMessage(23));
        }
    }

    public static final void PamSong(LittleEndianAccessor slea, MapleClient c) {
        final Item pam = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5640000);
        if (slea.readByte() > 0 && c.getPlayer().getScrolledPosition() != 0 && pam != null && pam.getQuantity() > 0) {
            final MapleInventoryType inv = c.getPlayer().getScrolledPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
            final Item item = c.getPlayer().getInventory(inv).getItem(c.getPlayer().getScrolledPosition());
            c.getPlayer().setScrolledPosition((short) 0);
            if (item != null) {
                final Equip eq = (Equip) item;
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + 1));
                c.getPlayer().forceReAddItem_Flag(eq, inv);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, pam.getPosition(), (short) 1, true, false);
            }
        } else {
            c.getPlayer().setScrolledPosition((short) 0);
        }
    }

    public static final void handleExpConsumeItemResult(LittleEndianAccessor inPacket, MapleClient c) {
        inPacket.skip(4);
        final MapleCharacter user = c.getPlayer();
        short slot = inPacket.readShort();
        int itemId = inPacket.readInt();
        int needExp = user.getNeededExp() - user.getExp();
        Item toUse = user.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (toUse.getItemId() / 10000 != 223) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (toUse.getQuantity() < 1) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (toUse.getItemId() != itemId) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        /*
        int[][] expConsumeList = {
            {2230000, 1, 30, 470940, GameConstants.consume_2230000},
            {2230001, 30, 70, 30570854, GameConstants.consume_2230001},
            {2230002, 1, 30, 470940, GameConstants.consume_2230002},
            {2230003, 30, 70, 30570854, GameConstants.consume_2230003},
            {2230004, 12, 18, 26002, GameConstants.consume_2230004},};
        for (int i = 0; i < expConsumeList.length; i++) {
            MapleQuestStatus qr = user.getQuest(MapleQuest.getInstance(expConsumeList[i][4]));
            if (qr.getCustomData() == null) {
                qr.setCustomData(expConsumeList[i][3] + "");
            }
            if (Integer.parseInt(qr.getCustomData()) < 0) {
                qr.setCustomData(expConsumeList[i][3] + "");
            }
            int remainExp = Integer.parseInt(qr.getCustomData()) - needExp;
            int showRemainExp = remainExp - expConsumeList[i][3];
            if (itemId == expConsumeList[i][0]) {
                c.sendPacket(CWvsContext.expConsumeItemResult((byte) 2, user.getId(), itemId, expConsumeList[i][1], expConsumeList[i][2], showRemainExp));
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                user.dropMessage(6, "<" + ii.getName(itemId) + "> 의 잔여 경험치가 " + user.getNum(remainExp) + " 남았습니다.");
                qr.setCustomData(remainExp + "");
                user.updateQuest(qr);
            }
        }
         */
        while (user.getLevel() < 18) {
            user.levelUp();
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void TeleRock(LittleEndianAccessor slea, MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 232 || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        boolean used = UseTeleRock(slea, c, itemId);
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void UseSpecialScroll(LittleEndianAccessor slea, MapleCharacter chr) {
        byte slot = (byte) slea.readShort();
        byte dst = (byte) slea.readShort();
        slea.skip(1);
        boolean use = false;
        Equip toScroll;
        if (dst < 0) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        Item scroll = chr.getInventory(MapleInventoryType.CASH).getItem(slot);

        if (scroll == null || !GameConstants.isSpecialCSScroll(scroll.getItemId())) {
            scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
            use = true;
        }

        if (!use) {
            switch (scroll.getItemId()) {
                case 5064000:   // 프로텍트 쉴드
                case 5064002: { // 라이트 프로텍트 쉴드
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.ProtectScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
                case 5064100:   // 세이프티 쉴드
                case 5064101: { // 라이트 세이프티 쉴드
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.SafetyScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
                case 5064300:   // 리커버리 쉴드
                case 5064301: { // 라이트 리커버리 쉴드
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.RecoveryScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
                case 5063000: { // 행운의 열쇠
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.LuckyDayScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
                case 5063100: { // 럭키 프로텍트 쉴드
                    short flag = toScroll.getFlag();
                    if (!ItemFlag.LuckyDayScroll.check(flag) && !ItemFlag.ProtectScroll.check(flag)) {
                        flag |= ItemFlag.LuckyDayScroll.getValue();
                        flag |= ItemFlag.ProtectScroll.getValue();
                        toScroll.setFlag(flag);
                        chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    } else {
                        chr.getClient().getSession().write(CWvsContext.enableActions());
                        return;
                    }
                    break;
                }
            }
            chr.getInventory(MapleInventoryType.CASH).removeItem(scroll.getPosition(), (short) 1, false);
        } else {
            switch (scroll.getItemId()) {
                case 2531000: { // 프로텍트 주문서
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.ProtectScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
                case 2532000: { // 세이프티 주문서
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.SafetyScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
                case 2530000:   // 럭키 데이 주문서
                case 2530001:   // HAPPY 럭키 데이 주문서
                case 2530002: { // 럭키 데이 주문서
                    short flag = toScroll.getFlag();
                    flag |= ItemFlag.LuckyDayScroll.getValue();
                    toScroll.setFlag(flag);
                    chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(toScroll, toScroll.getType(), chr));
                    break;
                }
            }
            chr.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short) 1, false);
        }

        chr.getClient().getSession().write(InventoryPacket.scrolledItem(scroll, toScroll, false, true));
        chr.getClient().getPlayer().getMap().broadcastMessage(chr, CField.getScrollEffect(chr.getId(), ScrollResult.SUCCESS, false, false, toScroll.getItemId(), scroll.getItemId()), true);

        if (dst < 0) {
            chr.equipChanged();
        }
    }

    public static boolean checkPotentialLock(MapleCharacter chr, Equip eq, int line, int potential) {
        if (line == 0 || potential == 0) {
            return false;
        }
        if (line < 0 || line > 3) {
            System.out.println("[Hacking Attempt] " + MapleCharacterUtil.makeMapleReadable(chr.getName()) + " Tried to lock potential line which does not exists.");
            return false;
        }
        if (line == 1 && eq.getPotentialByLine(0) != potential - line * 100000
                || line == 2 && eq.getPotentialByLine(1) != potential - line * 100000
                || line == 3 && eq.getPotentialByLine(2) != potential - line * 100000) {
            System.out.println("[Hacking Attempt] " + MapleCharacterUtil.makeMapleReadable(chr.getName()) + " Tried to lock potential which equip doesn't have.");
            return false;
        }
        return true;
    }

    public static final boolean UseTeleRock(LittleEndianAccessor slea, MapleClient c, int itemId) {
        boolean used = false;
        if (itemId == 5040000 || itemId == 5040003 || itemId == 5041000 || itemId == 5041001) {
            used = true;
        }
        if (itemId == 5040004) {
            slea.readByte();
        }
        if (itemId / 10000 == 504) {
            if (slea.readByte() == 0) {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(slea.readInt());
                if (target != null) { //Premium and Hyper rocks are allowed to go anywhere. Blocked maps are checked below.
                    if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) { //Makes sure this map doesn't have a forced return map
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to that place.");
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The place you want to go to does not exist.");
                }
            } else {
                final String name = slea.readMapleAsciiString();
                final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null) {
                    if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit()) && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                        c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to where that person is.");
                    }
                } else {
                    if (victim == null) {
                        c.getPlayer().dropMessage(1, "(" + name + ") is either offline or in a different channel.");
                    } else {
                        c.getPlayer().dropMessage(1, "(" + name + ") is currently difficult to locate, so the teleport will not take place.");
                    }
                }
            }
        } else {
            if (itemId == 5040004) {
                c.getPlayer().dropMessage(1, "You are not able to use this teleport rock.");
            } else {
                c.getPlayer().dropMessage(1, "This teleport rock is currently disabled.");
            }
        }
        return used;
    }

    public final static void UseCube(LittleEndianAccessor slea, MapleClient c) {
        slea.skip(4);
        slea.skip(2);
        final int itemID = slea.readInt();
        if (itemID == 4320000) {
            c.getPlayer().getClient().sendPacket(CUserLocal.openUI(UIType.BATTLERECORD));
        }
        c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
    }

    public final static void 대충헨들러(LittleEndianAccessor slea, MapleClient c) {
        int type = slea.readInt(); // 0 활성화전 1활성화후 
        int mainslot = slea.readInt(); // 시너지 머신 슬롯
        byte a = slea.readByte();
        short slot = slea.readShort(); // 1번 마북인 레후
        byte b = slea.readByte();
        short dst = slea.readShort(); // 2번 마북인 레후
        // 22 01 01 00 00 00 07 00 00 00 02 09 00 02 0A 00
        // 100 1001 성공
        // 101 2001 마스터리북 사용할수없다?
        // 100 2001 사용할수없는 마북
        // 101 2002 인벤토리 공간
        Item maintoUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) mainslot); // 메인 시너지 머신
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot); // 1번 마북
        Item toUse2 = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(dst); // 2번 마북
        if (maintoUse == null || toUse2 == null || toUse == null) { // 없음방지
            c.getSession().write(CWvsContext.대충패킷(c.getPlayer().getId(), 1001, 0, 100, maintoUse.getItemId()));
            return;
        }
        if (c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.대충패킷(c.getPlayer().getId(), 1001, 0, 101, maintoUse.getItemId()));
            return;
        }
        if (type == 0) {
            c.getSession().write(CWvsContext.대충패킷(c.getPlayer().getId(), 0, 0, 100, maintoUse.getItemId()));
        } else {
            MapleInventoryManipulator.addById(c, 2290893, (short) 1, "합성");
            c.getSession().write(CWvsContext.대충패킷(c.getPlayer().getId(), 1, 1, 100, maintoUse.getItemId()));
            // public static byte[] 대충패킷(int cid, int s, int n, int type, int itemid) { // 작동함
            // 아이템 차감
            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse2.getItemId(), 1, true, false);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, maintoUse.getItemId(), 1, true, false);
        }
    }

    public static final void 대충헨들러3(final LittleEndianAccessor slea, final MapleClient c) {
        final byte type = slea.readByte();
        MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(12500)); // 12500 진행중 퀘스트 
        // [R] [Data: 13 01 03] 03도착
        if (type == 0) {
            c.getSession().write(UIPacket.게임(4));
        } else if (type == 1) {
            if (marr.getCustomData() == null) {
                marr.setCustomData("0");
            }
            int a = Randomizer.rand(1, 5);
            if ((Long.parseLong(marr.getCustomData()) + a) >= 30) {
                marr.setCustomData(String.valueOf((Long.parseLong(marr.getCustomData()) + a) - 30));
                c.getPlayer().updateInfoQuest(12501, "state=2;finish=1;cell=" + ((Long.parseLong(marr.getCustomData())) - 30));
            } else {
                marr.setCustomData(String.valueOf(Long.parseLong(marr.getCustomData()) + a));
                c.getPlayer().updateInfoQuest(12501, "state=2;finish=1;cell=" + (Long.parseLong(marr.getCustomData())));
            }
            c.getSession().write(UIPacket.주사위(a));
            // c.getSession().write(UIPacket.게임());
        } else if (type == 3) { // 도착
            c.getPlayer().updateInfoQuest(12501, "state=2;finish=0;cell=" + (Long.parseLong(marr.getCustomData())));
            c.getSession().write(UIPacket.게임(4));
            c.getSession().write(UIPacket.게임(10));
        }
    }

    public static final void 대충헨들러4(final LittleEndianAccessor slea, final MapleClient c) {
        final byte type = slea.readByte();
        String spw = slea.readMapleAsciiString();
        c.getSession().write(UIPacket.메소마켓2(c.getPlayer().getId()));
        System.out.print(spw);
    }

    static boolean suc = false;

    public static final void UseGoldHammer(final LittleEndianAccessor slea, final MapleClient c) {
        boolean used = false;
        final byte slot = (byte) slea.readShort();
        slea.skip(2);
        final int itemId = slea.readInt();
        slea.readInt();
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        final Equip item = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (item != null) {
            if (GameConstants.canHammer(item.getItemId()) && MapleItemInformationProvider.getInstance().getSlots(item.getItemId()) > 0 && item.getViciousHammer() < 2) {
                switch (itemId) {
                    case 2470000:
                    case 2470003:
                        item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                        item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIP);
                        used = true;
                        suc = true;
                        break;
                    case 2470001:
                        if (Randomizer.nextInt(100) < 50) {
                            item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                            item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));
                            c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIP);
                            used = true;
                            suc = true;
                        } else {
                            used = true;
                        }
                        break;
                }
                c.getSession().write(CCashShop.ViciousHammer(true, suc));
            } else {
                c.getPlayer().dropMessage(5, "이미 황금 망치 재련 효과가 부여 된 장비입니다.");
            }
        }
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, true);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void HammerEffect(final LittleEndianAccessor slea, final MapleClient c) {
        slea.skip(8);
        c.getSession().write(CCashShop.ViciousHammer(false, suc));
    }

    public static void useRemoteHiredMerchant(LittleEndianAccessor slea, MapleClient c) {
        short slot = slea.readShort();
        Item item = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (item == null) {
            c.getSession().close();
            return;
        }
        if (item.getItemId() != 5470000 || item.getQuantity() <= 0) {
            c.getSession().close();
            return;
        }
        boolean use = false;
        HiredMerchant merchant = c.getChannelServer().findAndGetMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merchant == null) {
            c.getPlayer().dropMessage(1, "현재 채널에서 열려있는 고용 상점이 없습니다.");
            return;
        }
        MapleCharacter chr = c.getPlayer();
        if (merchant.isOwner(chr) && merchant.isOpen() && merchant.isAvailable()) {
            merchant.setOpen(false);
            merchant.broadcastToVisitors(CWvsContext.serverNotice(1, "고용 상점이 점검중에 있습니다. 나중에 다시 이용해 주세요."));
            merchant.removeAllVisitors((byte) 16, (byte) -2);
            chr.setPlayerShop(merchant);
            c.getSession().write(CPlayerShop.getHiredMerch(chr, merchant, false));
            use = true;
        }
        if (use) {
            //c.sendPacket(PlayerShopPacket.MerchantAlreadyMssage((byte) 0x11, 0, 0, 0));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short) 1, false);
        }
    }

    public static final void handleSeletPQReward(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (user.getParty() == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final byte indexByBox = slea.DecodeByte();
        if (indexByBox < 0) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        for (MaplePartyCharacter pUser : user.getParty().getMembers()) {
            if (pUser != null) {
                final MapleCharacter cUser = c.getChannelServer().getPlayerStorage().getCharacterByName(pUser.getName());
                if (cUser != null) {
                    cUser.getClient().sendPacket(CWvsContext.resultPQReward((byte) 47, user.getName(), (byte) indexByBox, (byte) 0, -1, -1, -1, -1, -1, -1));
                    if (cUser.getId() == user.getId()) {
                        cUser.setPQReward((int) indexByBox);
                    }
                }
            }
        }
    }

    public static final void handleRecivePQReward(final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (user.getParty() == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int v1 = -1, v2 = -1, v3 = -1, v4 = -1, v5 = -1, v6 = -1;
        for (MaplePartyCharacter pUser : user.getParty().getMembers()) {
            if (pUser != null) {
                final MapleCharacter cUser = c.getChannelServer().getPlayerStorage().getCharacterByName(pUser.getName());
                if (cUser != null) {
                    if (cUser.getPQReward() < 0) {
                        c.sendPacket(CWvsContext.enableActions());
                        return;
                    }
                    switch (cUser.getPQReward()) {
                        case 0: {
                            int[] pq_reward_list = {1002357, 1382000, 1312000, 1322000, 1332000, 1492000};
                            int pq_reward = (int) Math.floor(Math.random() * pq_reward_list.length);
                            v1 = pq_reward_list[pq_reward];
                            cUser.gainItem(v1, 1);
                            break;
                        }
                        case 1: {
                            int[] pq_reward_list = {1002357, 1382000, 1312000, 1322000, 1332000, 1492000};
                            int pq_reward = (int) Math.floor(Math.random() * pq_reward_list.length);
                            v2 = pq_reward_list[pq_reward];
                            cUser.gainItem(v2, 1);
                            break;
                        }
                        case 2: {
                            int[] pq_reward_list = {1002357, 1382000, 1312000, 1322000, 1332000, 1492000};
                            int pq_reward = (int) Math.floor(Math.random() * pq_reward_list.length);
                            v3 = pq_reward_list[pq_reward];
                            cUser.gainItem(v3, 1);
                            break;
                        }
                        case 3: {
                            int[] pq_reward_list = {1002357, 1382000, 1312000, 1322000, 1332000, 1492000};
                            int pq_reward = (int) Math.floor(Math.random() * pq_reward_list.length);
                            v4 = pq_reward_list[pq_reward];
                            cUser.gainItem(v4, 1);
                            break;
                        }
                        case 4: {
                            int[] pq_reward_list = {1002357, 1382000, 1312000, 1322000, 1332000, 1492000};
                            int pq_reward = (int) Math.floor(Math.random() * pq_reward_list.length);
                            v5 = pq_reward_list[pq_reward];
                            cUser.gainItem(v5, 1);
                            break;
                        }
                        case 5: {
                            int[] pq_reward_list = {1002357, 1382000, 1312000, 1322000, 1332000, 1492000};
                            int pq_reward = (int) Math.floor(Math.random() * pq_reward_list.length);
                            v6 = pq_reward_list[pq_reward];
                            cUser.gainItem(v6, 1);
                            break;
                        }
                    }
                }
            }
        }
        for (MaplePartyCharacter pUser : user.getParty().getMembers()) {
            if (pUser != null) {
                final MapleCharacter cUser = c.getChannelServer().getPlayerStorage().getCharacterByName(pUser.getName());
                if (cUser != null) {
                    cUser.getClient().sendPacket(CWvsContext.resultPQReward((byte) 49, null, (byte) 0, (byte) 0, v1, v2, v3, v4, v5, v6));
                }
            }
        }
    }

    public static void QuestPotOpen(LittleEndianAccessor slea, MapleClient c) {
        final int questID = slea.readUShort();
        final MapleQuest qInstance = MapleQuest.getInstance(questID);
        if (qInstance == null) {
            return;
        }
        final MapleQuestStatus qStatus = c.getPlayer().getQuestNAdd(qInstance);
        if (qStatus.getCustomData() == null) {
            qStatus.setCustomData("0");
        }
        c.sendPacket(InfoPacket.updateQuest(qStatus));
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void QuestPotFeed(LittleEndianAccessor slea, MapleClient c) {
        short slotID = slea.readShort();
        int itemID = slea.readInt();
        Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slotID);
        if (item == null || item.getQuantity() <= 0) {
            return;
        }
        if (item.getItemId() != itemID) {
            return;
        }
        int qid = slea.readUShort();
        MapleQuest q = MapleQuest.getInstance(qid);
        if (q != null && c.getPlayer().getQuestNoAdd(q) != null) {
            MapleQuestStatus qs = c.getPlayer().getQuestNoAdd(q);
            if (qs.getCustomData() == null || qs.getCustomData().isEmpty()) {
                return;
            }
            final MapleQuestStatus aranQuest = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(21763));
            if (aranQuest.getCustomData() != null) {
                aranQuest.setCustomData(String.valueOf(Double.parseDouble(aranQuest.getCustomData()) + 100.0));
                if (aranQuest.getCustomData().equals("800.0")) {
                    aranQuest.setCustomData("800");
                }
                c.getPlayer().updateQuest(aranQuest);
            }
            //qs.setCustomData((Integer.parseInt(qs.getCustomData()) + 1) + "");
            qs.setCustomData("3000");
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slotID, (short) 1, false);
            c.sendPacket(InfoPacket.updateQuest(qs));
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void handleUseSoulEnchant(LittleEndianAccessor slea, MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        user.updateTick(slea.readInt());
        final short v1 = slea.readShort();
        final short v2 = slea.readShort();
        final boolean v3 = slea.readByte() > 0;
        Item soulEnchant = user.getInventory(MapleInventoryType.USE).getItem(v1);
        if (soulEnchant == null) {
            return;
        }
        Equip enchantEquip = null;
        if (v2 < 0) {
            enchantEquip = (Equip) user.getInventory(MapleInventoryType.EQUIPPED).getItem(v2);
        }
        if (v3) {
            enchantEquip = (Equip) user.getInventory(MapleInventoryType.EQUIP).getItem(v2);
        }
        if (enchantEquip == null) {
            return;
        }
        if (!GameConstants.isWeapon(enchantEquip.getItemId())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        String enchantName = ii.getName(soulEnchant.getItemId());
        if (enchantName.contains("의 소울")) {
            enchantName = enchantName.replace("의 소울", "");
        }
        enchantEquip.setOwner(enchantName);
        if (v2 < 0) {
            user.forceReAddItem(enchantEquip, MapleInventoryType.EQUIPPED);
        }
        if (v3) {
            user.forceReAddItem(enchantEquip, MapleInventoryType.EQUIP);
        }
        user.getInventory(MapleInventoryType.USE).removeItem(soulEnchant.getPosition(), (short) 1, false);
        user.getClient().sendPacket(InventoryPacket.scrolledItem(soulEnchant, enchantEquip, false, false));
        user.getMap().broadcastMessage(user, CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, v3, false, enchantEquip.getItemId(), soulEnchant.getItemId()), true);
        user.setSoulEnchantSkill();
        c.sendPacket(CWvsContext.enableActions());
    }
}
