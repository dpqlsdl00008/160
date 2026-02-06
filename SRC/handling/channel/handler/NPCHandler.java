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
package handling.channel.handler;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.MapleClient;
import client.MapleCharacter;
import constants.GameConstants;
import client.MapleQuestStatus;
import client.RockPaperScissors;
import client.inventory.ItemFlag;
import constants.shop.Shop;
import constants.shop.Shop;
import handling.SendPacketOpcode;
import handling.UIType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import server.MapleShop;
import server.MapleInventoryManipulator;
import server.MapleStorage;
import server.life.MapleNPC;
import server.quest.MapleQuest;
import scripting.NPCScriptManager;
import scripting.NPCConversationManager;
import server.MapleItemInformationProvider;
import server.MapleQuestFactory;
import server.MapleShopFactory;
import server.maps.MapScriptMethods;
import tools.packet.CField;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.data.OutPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class NPCHandler {

    public static final void NPCAnimation(final LittleEndianAccessor slea, final MapleClient c) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.NPC_MOVE.getValue());
        final int length = (int) slea.available();
        if (length == 10) { // NPC Talk
            mplew.writeInt(slea.readInt());
            mplew.writeShort(slea.readShort());
            mplew.writeInt(slea.readInt());
        } else if (length > 10) { // NPC Move
            mplew.write(slea.read(length - 9));
        } else {
            return;
        }
        c.getSession().write(mplew.getPacket());
    }

    public static final void NPCShop(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte bmode = slea.readByte();
        if (chr == null) {
            return;
        }
        c.getPlayer().setEndSay(new Pair<>(0, 0));
        c.getPlayer().setEndStatus(false);

        switch (bmode) {
            case 0: {
                final MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                //slea.skip(2);
                final short itemPos = slea.readShort();
                final int itemId = slea.readInt();
                final short quantity = slea.readShort();
                shop.buy(c, itemId, quantity);
                break;
            }
            case 1: {
                final MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                final byte slot = (byte) slea.readShort();
                final int itemId = slea.readInt();
                final short quantity = slea.readShort();
                //     shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                shop.sell(c, itemId, slot, quantity);
                break;
            }
            case 2: {
                final MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                final byte slot = (byte) slea.readShort();
                shop.recharge(c, slot);
                break;
            }
            default:
                chr.setConversation(0);
                break;
        }
    }

    public static final void NPCTalk(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter user) {
        if (user == null || user.getMap() == null) {
            return;
        }
        final MapleNPC npc = user.getMap().getNPCByOid(slea.readInt());
        if (user.isGM()) {
            user.dropMessage(6, "[NPC SCRIPTS] " + npc.getName() + " (" + npc.getId() + ")");
        }
        if (npc == null) {
            return;
        }
        if (user.hasBlockedInventory()) {
            return;
        }
        user.setEndSay(new Pair<>(0, 0));
        user.setEndStatus(false);
        boolean isNotShop = false;
        if (npc.getId() == 2103013) {
            isNotShop = true;
        }
        if (npc.getId() == 9103001) {
            isNotShop = true;
        }
        if (npc.hasShop() && !isNotShop) {
            user.setConversation(1);
            npc.sendShop(c);
            return;
        }
        if (npc.getId() == 9030000) {
            user.setConversation(3);
            HiredMerchantHandler.displayMerch(c);
            return;
        }
        if (npc.getId() == 9030100 || npc.getFunc().equals("창고지기")) {
            user.setConversation(4);
            user.getStorage().sendStorage(c, npc.getId());
            return;
        }
        boolean repairNPC = false;
        switch (npc.getId()) {
            case 1012002:
            case 1022003:
            case 1022004:
            case 1032002:
            case 1052002:
            case 1052003:
            case 1061000:
            case 1091003:
            case 2010003:
            case 2020000:
            case 2020002:
            case 2040016:
            case 2040020:
            case 2040021:
            case 2040022:
            case 2080000:
            case 2100001: {
                repairNPC = true;
                break;
            }
        }
        if (repairNPC) {
            user.getClient().sendPacket(CUserLocal.openUIWithOption(UIType.REPAIRDURABILITY, npc.getId()));
            return;
        }
        NPCScriptManager.getInstance().start(c, npc.getId(), null, npc.getObjectId());
    }

    public static final void QuestAction(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte action = slea.readByte();
        int quest = slea.readUShort();
        if (chr == null) {
            return;
        }
        if (chr.isGM() == true) {
            chr.dropMessage(6, "QUEST : " + quest + " | ACTION : " + action);
        }
        final MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: { // Restore lost item
                //chr.updateTick(slea.readInt());
                slea.readInt();
                final int itemid = slea.readInt();
                q.RestoreLostItem(chr, itemid);
                break;
            }
            case 1: { // Start Quest
                int npc = slea.readInt();
                q.start(chr, npc);
                break;
            }
            case 2: { // Complete Quest
                final int npc = slea.readInt();
                slea.readInt();
                if (q.hasEndScript()) {
                    return;
                }
                c.getPlayer().setEndSay(new Pair<>(0, 0));
                c.getPlayer().setEndFlow(0);
                if (slea.available() >= 4) {
                    q.complete(chr, npc, slea.readInt());
                } else {
                    if (chr.isGM() == true) {
                        chr.dropMessage(6, "QUEST COMPLETED (QUEST ACTION : CASE 2)");
                    }
                    q.complete(chr, npc);
                }
                if (npc == 1101000 && quest == 20014) { //인포업데이트...by몽킾
                    final MapleQuestStatus status = chr.getQuestNAdd(MapleQuest.getInstance(20022));
                    status.setStatus((byte) 1);
                    status.setCustomData("1");
                    chr.updateQuest(status);
                }
                break;
            }
            case 3: { // Forefit Quest
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                } else {
                    chr.dropMessage(1, "이 퀘스트는 포기할 수 없습니다.");
                }
                break;
            }
            case 4: { // Scripted Start Quest
                final int npc = slea.readInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                break;
            }
            case 5: { // Scripted End Quest
                final int npc = slea.readInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                //c.getPlayer().updateTick(slea.readInt());
                if (chr.isGM() == true) {
                    chr.dropMessage(6, "QUEST COMPLETED (QUEST ACTION : CASE 5)");
                }
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                break;
            }
        }
    }

    public static final void Storage(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte mode = slea.readByte();
        if (chr == null) {
            return;
        }
        final MapleStorage storage = chr.getStorage();

        switch (mode) {
            case 4: { // Take Out
                final byte type = slea.readByte();
                final byte slot = storage.getSlot(MapleInventoryType.getByType(type), slea.readByte());
                final Item item = storage.takeOut(slot);

                if (item != null) {
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        storage.store(item);
                        chr.dropMessage(1, "Your inventory is full");
                    } else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                        storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                        tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ") Withdrew ItemID " + item.getItemId() + " x" + item.getQuantity());
                    }
                } else {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                break;
            }
            case 5: { // Store
                final byte slot = (byte) slea.readShort();
                final int itemId = slea.readInt();
                MapleInventoryType type = GameConstants.getInventoryType(itemId);
                short quantity = slea.readShort();
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (quantity < 1) {
                    //AutobanManager.getInstance().autoban(c, "Trying to store " + quantity + " of " + itemId);
                    tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ")  WARNING! Tried to store " + quantity + " + of " + itemId);
                    return;
                }
                if (storage.isFull()) {
                    c.getSession().write(NPCPacket.getStorageFull());
                    return;
                }
                if (chr.getInventory(type).getItem(slot) == null) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }

                if (chr.getMeso() < 100) {
                    chr.dropMessage(1, "You don't have enough mesos to store the item.");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                } else {
                    Item item = chr.getInventory(type).getItem(slot).copy();

                    if (GameConstants.isPet(item.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                    final short flag = item.getFlag();
                    if (ii.isPickupRestricted(item.getItemId()) && storage.findById(item.getItemId()) != null) {
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                    if (item.getItemId() == itemId && (item.getQuantity() >= quantity || GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId))) {
                        if (ii.isDropRestricted(item.getItemId())) {
                            if (ItemFlag.TradedOnceWithinAccount.check(flag)) {
                                item.setFlag((short) (flag - (ItemFlag.TradedOnceWithinAccount.getValue() + ItemFlag.KarmasCissors.getValue())));
                            } else if (ItemFlag.Unk_01.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.Unk_01.getValue()));
                            } else if (ItemFlag.KarmasCissors.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KarmasCissors.getValue()));
                            } else if (ItemFlag.PreventSlipping.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.PreventSlipping.getValue()));
                            } else {
                                chr.dropMessage(1, "해당 아이템은 창고를 이용 할 수 없습니다.");
                                c.getSession().write(CWvsContext.enableActions());
                                return;
                            }
                        }
                        if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                            quantity = item.getQuantity();
                        }
                        chr.gainMeso(-100, false, false);
                        MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                        item.setQuantity(quantity);
                        storage.store(item);
                        //tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ") Deposited ItemID " + item.getItemId() + " x" + item.getQuantity());
                    } else {
                        //AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store non-matching itemid (" + itemId + "/" + item.getItemId() + ") or quantity not in posession (" + quantity + "/" + item.getQuantity() + ")");
                        //tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ") WARNING! Trying to store non-matching itemid (" + itemId + "/" + item.getItemId() + ") or quantity not in posession (" + quantity + "/" + item.getQuantity() + ")");
                        return;
                    }
                }
                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                break;
            }
            case 6: { //arrange
                storage.arrange();
                tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ")  Re-arranged Storage.");
                storage.update(c);
                break;
            }
            case 7: {
                int meso = slea.readInt();
                final int storageMesos = storage.getMeso();
                final int playerMesos = chr.getMeso();

                if ((meso > 0 && storageMesos >= meso) || (meso < 0 && playerMesos >= -meso)) {
                    if (meso < 0 && (storageMesos - meso) < 0) { // storing with overflow
                        meso = -(Integer.MAX_VALUE - storageMesos);
                        if ((-meso) > playerMesos) { // should never happen just a failsafe
                            return;
                        }
                    } else if (meso > 0 && (playerMesos + meso) < 0) { // taking out with overflow
                        meso = (Integer.MAX_VALUE - playerMesos);
                        if ((meso) > storageMesos) { // should never happen just a failsafe
                            return;
                        }
                    }
                    storage.setMeso(storageMesos - meso);
                    chr.gainMeso(meso, false, false);
                    //tools.FileoutputUtil.log("logs\\storage\\"+chr.getName()+".txt", "["+tools.FileoutputUtil.CurrentReadable_Time()+"] Withdrew "+ meso +" mesos.");
                } else {
                    //AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");
                    tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ") Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");

                    return;
                }
                storage.sendMeso(c);
                if (meso > 0) {
                    tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ") " + Math.abs(meso) + " mesos were taken OUT of storage.");
                } else {
                    tools.FileoutputUtil.log("logs\\storage\\" + chr.getName() + ".txt", "[" + tools.FileoutputUtil.CurrentReadable_Time() + "] " + chr.getName() + " (CID: " + chr.getId() + ") " + Math.abs(meso) + " mesos were put INTO storage.");
                }
                break;
            }
            case 8: {
                storage.close();
                chr.setConversation(0);
                break;
            }
            default:
                System.out.println("Unhandled Storage mode : " + mode);
                break;
        }
    }

    public static final void NPCMoreTalk(final LittleEndianAccessor slea, final MapleClient c) {
        //  System.out.println("lastmasg6"); // 5A 00 00 01]
        // 5A 00 15 01 00 00 00 00]
        // 5A 00 15 01 01 00 00 00]
        final byte lastMsg = slea.readByte(); // 00 (last msg type I think)
        byte action = 0; // 01
        if (lastMsg == 9 && slea.available() >= 4) {
            slea.readShort();
        }
        if (lastMsg != 7) {
            action = slea.readByte(); // 00 = end chat, 01 == follow
        }
        if (lastMsg == 0x11) {
            NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
            if (cm != null) {
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, (byte) 1, (byte) 0, (byte) 0);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, (byte) 1, (byte) 0, (byte) 0);
                } else {
                    NPCScriptManager.getInstance().action(c, (byte) 1, (byte) 0, (byte) 0);
                }
            }
            return;
        }
        if ((lastMsg == 0x12 && c.getPlayer().getJob() == 3001)) {
            //byte lastbyte = slea.readByte(); // 00 = end chat, 01 == follow
            if (action == 0) {
                c.getSession().write(CWvsContext.enableActions());
            } else {
                //    System.out.println("lastmasg9");
                MapScriptMethods.startDirectionInfo(c.getPlayer(), lastMsg == 0x12);
                c.getSession().write(CWvsContext.enableActions());
            }
            return;
        }
        
        if (c.getPlayer().getEndSay() != null) {
            if (c.getPlayer().getEndSay().left != 0 && c.getPlayer().getEndSay().right != 0) {
                final List<Pair<String, Integer>> say = MapleQuestFactory.getInstance().getQuest(c.getPlayer().getEndSay().right);
                if (action == 0) {
                    if (say.size() == 2) {
                        c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(0).left, "00 01", (byte) 0));
                        c.getPlayer().setEndStatus(false);
                    } else if (say.size() > 2) {
                        c.getPlayer().setEndFlow(-1);
                        if (c.getPlayer().getEndFlow() == say.size() - 2) {
                            c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(c.getPlayer().getEndFlow()).left, "01 01", (byte) 0));
                        } else if (c.getPlayer().getEndFlow() < say.size() - 2) {
                            if (c.getPlayer().getEndFlow() == 0) {
                                c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(c.getPlayer().getEndFlow()).left, "00 01", (byte) 0));
                            } else {
                                c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(c.getPlayer().getEndFlow()).left, "01 01", (byte) 0));
                            }
                        }
                    }
                } else if (action == 1) {
                    if (say.size() == 2) {
                        if (!c.getPlayer().getEndStatus()) {
                            c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(1).left, "01 00", (byte) 0));
                            c.getPlayer().setEndStatus(true);
                        } else {
                            c.getPlayer().setEndSay(new Pair<>(0, 0));
                            c.getPlayer().setEndStatus(false);
                        }
                    } else if (say.size() > 2) { // not handling yet.
                        c.getPlayer().setEndFlow(1);
                        if (c.getPlayer().getEndFlow() == say.size() - 1) {
                            c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(c.getPlayer().getEndFlow()).left, "01 00", (byte) 0));
                        } else if (c.getPlayer().getEndFlow() < say.size() - 1) {
                            c.getSession().write(NPCPacket.getNPCTalk(c.getPlayer().getEndSay().left, (byte) 0, say.get(c.getPlayer().getEndFlow()).left, "01 01", (byte) 0));
                        } else {
                            c.getPlayer().setEndSay(new Pair<>(0, 0));
                            c.getPlayer().setEndFlow(0);
                        }
                    }
                }
            } else {
            }
        }

        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        if (cm == null || c.getPlayer().getConversation() == 0 || (cm.getLastMsg() != lastMsg && c.getPlayer().getQuestStatus(24068) != 1)) {
            return;
        }
        cm.setLastMsg((byte) -1);
        if (lastMsg == 3) {
            if (action != 0) {
                cm.setGetText(slea.readMapleAsciiString());
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                }
            } else {
                cm.dispose();
            }
        } else if (lastMsg == 7) { // Speed Quiz 
            if (c.getPlayer().getSpeedQuiz() == null) {
                return;
            }
            c.getPlayer().getSpeedQuiz().nextRound(c, slea.readMapleAsciiString());
        } else {
            int selection = -1;
            if (slea.available() >= 4) {
                selection = slea.readInt();
            } else if (slea.available() > 0) {
                selection = slea.readByte();
            }
            if (lastMsg == 4 && selection == -1) {
                cm.dispose();
                return;//h4x
            }
            if (action != -1) { // selection >= -1 && 
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
                }
            } else {
                cm.dispose();
            }
        }
    }

    public static final void repairAll(final MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000) {
            return;
        }
        Equip eq;
        double rPercentage;
        int price = 0;
        Map<String, Integer> eqStats;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<Equip, Integer> eqs = new HashMap<Equip, Integer>();
        final MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};
        for (MapleInventoryType type : types) {
            for (Item item : c.getPlayer().getInventory(type).newList()) {
                if (item instanceof Equip) { //redundant
                    eq = (Equip) item;
                    if (eq.getDurability() >= 0) {
                        eqStats = ii.getEquipStats(eq.getItemId());
                        if (eqStats.containsKey("durability") && eqStats.get("durability") > 0 && eq.getDurability() < eqStats.get("durability")) {
                            rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
                            eqs.put(eq, eqStats.get("durability"));
                            price += (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
                        }
                    }
                }
            }
        }
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        Equip ez;
        for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            ez = eqqz.getKey();
            ez.setDurability(eqqz.getValue());
            c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }

    public static final void repair(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getMapId() != 240000000 || slea.available() < 4) { //leafre for now
            return;
        }
        final int position = slea.readInt(); //who knows why this is a int
        final MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        final Item item = c.getPlayer().getInventory(type).getItem((byte) position);
        if (item == null) {
            return;
        }
        final Equip eq = (Equip) item;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || !eqStats.containsKey("durability") || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        final double rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
        //drpq level 105 weapons - ~420k per %; 2k per durability point
        //explorer level 30 weapons - ~10 mesos per %
        final int price = (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0)); // / 100 for level 30?
        //TODO: need more data on calculating off client
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }

    public static final void UpdateQuest(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

    public static final void UseItemQuest(final LittleEndianAccessor slea, final MapleClient c) {
        final short slot = slea.readShort();
        final int itemId = slea.readInt();
        final Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        final int qid = slea.readInt();
        final MapleQuest quest = MapleQuest.getInstance(qid);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight() != null && questItemInfo.getRight().contains(itemId)) {
                    found = true;
                    break; //i believe it's any order
                }
            }
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            final int newData = slea.readInt();
            final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short) 1, false);
            }
        }
    }

    public static final void RPSGame(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() == 0 || c.getPlayer() == null || c.getPlayer().getMap() == null || !c.getPlayer().getMap().containsNPC(9000019)) {
            if (c.getPlayer() != null && c.getPlayer().getRPS() != null) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        final byte mode = slea.readByte();
        switch (mode) {
            case 0: //start game
            case 5: //retry
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                } else {
                    c.getSession().write(CField.getRPSMode((byte) 0x08, -1, -1, -1));
                }
                break;
            case 1: //answer
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().answer(c, slea.readByte())) {
                    c.getSession().write(CField.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 2: //time over
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().timeOut(c)) {
                    c.getSession().write(CField.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 3: //continue
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().nextRound(c)) {
                    c.getSession().write(CField.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case 4: //leave
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                } else {
                    c.getSession().write(CField.getRPSMode((byte) 0x0D, -1, -1, -1));
                }
                break;
        }

    }

    public static final void handleMakeEnterFieldPacketForQuickMove(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        if (user.hasBlockedInventory()) {
            return;
        }
        if (user.isInBlockedMap()) {
            return;
        }
        final int templateID = slea.readInt();
        if (templateID < 9000000) {
            //return;
        }
        NPCScriptManager.getInstance().start(c, templateID);
    }

    public static final void AdminShop(final LittleEndianAccessor slea, final MapleClient c) {
        //public static final int OpenShop = 0x0, Trade = 0x1, Close = 0x2, WishItem = 0x3;
        byte mode = slea.readByte();
        final MapleShop shop = c.getPlayer().getShop();
        switch (mode) {
            case 1: {
                // 64 00 01 65 00 00 00 01 00 00 00
                final int itemId = slea.readInt();// nSN
                final short quantity = slea.readShort();// quantity
                final short slot = slea.readShort();// quantity
                //
                //	slea.readInt();// nSN
                //	slea.readInt();// quantity
                if (slot == 0) {
                    shop.buy(c, itemId, (short) quantity);
                } else {
                    //     shop.buy(c, itemId, (short) quantity);
                    //판매 (MapleClient c, MapleInventoryType type, byte slot, short quantity) {
                    shop.sell(c, itemId, (byte) slot, quantity);
                }
                break;
            }
            case 2: {
                //64 00 01 64 00 00 00 01 00 0E 00
                //
                break;
            }
        }
    }

    public static final void handleCrossHunterQuestRequest(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        short tabID = slea.readShort();
        if (tabID < 0) {
            return;
        }
        if (user.getInfoQuest(1648 + tabID).contains("r=1")) {
            return;
        }
        int reward_01 = 0;
        int reward_02 = 0;
        int reward_03 = 0;
        int quantity = 0;
        switch (tabID) {
            case 0: {
                reward_01 = 3700031;
                reward_02 = 4310029;
                quantity = 10;
                break;
            }
            case 1: {
                reward_01 = 3700032;
                reward_02 = 4310029;
                reward_03 = 2430669;
                quantity = 15;
                break;
            }
            case 2: {
                reward_01 = 3700033;
                reward_02 = 4310029;
                reward_03 = 2430668;
                quantity = 20;
                break;
            }
            case 3: {
                reward_01 = 3700034;
                reward_02 = 4310029;
                reward_03 = 2049309;
                quantity = 30;
                break;
            }
        }
        for (int i = 1; i < 6; i++) {
            if (user.getInventory(MapleInventoryType.getByType((byte) i)).getNumFreeSlot() < 1) {
                c.sendPacket(CWvsContext.crossHunterQuestResult((byte) 2, (short) 0));
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        if (reward_01 > 0) {
            MapleInventoryManipulator.addById(c, reward_01, (short) 1, "CROSS HUNTER QUEST");
            c.sendPacket(CWvsContext.InfoPacket.getShowItemGain(reward_01, (short) 1, true));
        }
        if (reward_02 > 0) {
            MapleInventoryManipulator.addById(c, reward_02, (short) quantity, "CROSS HUNTER QUEST");
            c.sendPacket(CWvsContext.InfoPacket.getShowItemGain(reward_02, (short) quantity, true));
        }
        if (reward_03 > 0) {
            MapleInventoryManipulator.addById(c, reward_03, (short) 1, "CROSS HUNTER QUEST");
            c.sendPacket(CWvsContext.InfoPacket.getShowItemGain(reward_03, (short) 1, true));
        }
        user.updateInfoQuest(1648 + tabID, user.getInfoQuest(1648) + ";r=1");
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void handleCrossHunterShopRequest(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        short itemIndexInShop = slea.readShort();
        int itemId = slea.readInt();
        short itemQuantity = slea.readShort();

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int reqCoin = 0;

        switch (itemIndexInShop) {
            case 0:
                reqCoin = 50;
                break;
            case 1:
                reqCoin = 40;
                break;
            case 2:
                reqCoin = 60;
                break;
            case 3:
                reqCoin = 20;
                break;
            case 4:
                reqCoin = 20;
                break;
            case 5:
                reqCoin = 15;
                break;
            case 6:
                reqCoin = 15;
                break;
            case 7:
                reqCoin = 25;
                break;
            case 8:
                reqCoin = 25;
                break;
        }
        if (user.haveItem(4310029, reqCoin) == false) {
            c.sendPacket(CWvsContext.crossHunterShopResult((byte) 1));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (user.canHold(itemId) == false) {
            c.sendPacket(CWvsContext.crossHunterShopResult((byte) 2));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(4310029), 4310029, reqCoin * itemQuantity, false, false);
        if (type.equals(MapleInventoryType.EQUIP)) {
            Equip item = (Equip) ii.getEquipById(itemId);
            item.renewPotential_OLD(3, 0, 0, false);
            MapleInventoryManipulator.addbyItem(c, item.copy());
        } else {
            MapleInventoryManipulator.addById(c, itemId, (short) itemQuantity, "CROSS HUNTER SHOP");
        }
        c.sendPacket(CWvsContext.crossHunterShopResult((byte) 0));
        c.sendPacket(CWvsContext.enableActions());
    }
}
