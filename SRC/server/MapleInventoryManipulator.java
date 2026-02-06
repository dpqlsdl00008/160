package server;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.inventory.MapleInventoryIdentifier;
import constants.GameConstants;
import client.inventory.Equip;
import client.inventory.InventoryException;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.PlayerStats;
import client.CharacterTemporaryStat;
import client.inventory.MaplePet;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleTrait.MapleTraitType;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.*;
import server.maps.AramiaFireWorks;
import tools.packet.CCashShop;
import tools.StringUtil;
import java.util.HashMap;
import server.quest.MapleQuest;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;

public class MapleInventoryManipulator {

    public static void addRing(MapleCharacter chr, int itemId, int ringId, int sn, String partner) {
        CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        Item ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.getClient().getSession().write(CCashShop.sendBoughtRings(GameConstants.isCrushRing(itemId), ring, sn, chr.getClient().getAccID(), partner));
    }

    public static boolean addbyItem(final MapleClient c, final Item item) {
        return addbyItem(c, item, false) >= 0;
    }

    public static short addbyItem(final MapleClient c, final Item item, final boolean fromcs) {
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        final short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(InventoryPacket.getShowInventoryFull());
            }
            return newSlot;
        }
        if (GameConstants.isHarvesting(item.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        if (MapleItemInformationProvider.getInstance().getItemInformation(item.getItemId()).flag == 1200) {
        }

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        /* 리첸시아 아이템 대여 */
        if (GameConstants.isWeapon(item.getItemId())) {
            Equip eqp = (Equip) item;
            if (ii.getEquipStats(item.getItemId()).get("expireOnLogout") != null) {
                int pCode = 10051;
                if ((int) (item.getItemId() / 10000) == 137
                        || (int) (item.getItemId() / 10000) == 138
                        || (int) (item.getItemId() / 10000) == 155) {
                    pCode = 10052;
                }
                eqp.setPotentialByLine(0, pCode);
                eqp.setPotentialByLine(1, pCode);
                eqp.setPotentialByLine(2, pCode);
                c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
                return newSlot;
            }
        }

        switch (item.getItemId()) {
            case 1302071:
            case 1322056:
            case 1332059:
            case 1382042:
            case 1402041:
            case 1412029:
            case 1422033:
            case 1432042:
            case 1442053:
            case 1452048:
            case 1462043:
            case 1472058:
            case 1482025:
            case 1492026:
            case 1522054:
            case 1532058: {
                item.setExpiration(System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000));
                break;
            }
        }

        switch (item.getItemId()) {

            case 1112427:
            case 1112428:
            case 1112429: {
                item.setExpiration(System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000));
                break;
            }

            case 2430117: // 이노시스 시즌 패스 티켓 (7일)

            case 1012540:
            case 1012541:
            case 1012542:
            case 1012543:

            case 1132112:
            case 1132113:
            case 1132114:
            case 1132115:
            case 1022135:
            case 1022136:

            case 1132013:
            case 1072619:
            case 1112682:

            case 5044002: {
                item.setExpiration(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
                break;
            }
        }

        switch (item.getItemId()) {
            case 1012540:
            case 1012541:
            case 1012542:
            case 1012543: {
                Equip eqp = (Equip) item;
                eqp.setPotentialByLine(0, 20086);
                eqp.setPotentialByLine(1, 20086);
                eqp.setPotentialByLine(2, 20086);
                c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
                return newSlot;
            }
        }

        boolean qReward = false;
        int qPotential = 0;
        switch (item.getItemId()) {
            /* 머쉬킹의 레더 글러브 */
            case 1082254:
            /* 엘리넬의 팔찌 */
            case 1082533: {
                qReward = true;
                qPotential = 10055;
                break;
            }
            /* 옥토퍼스 이어링 */
            case 1032254:
            /* 페어리 마크 */
            case 1022254:
            /* 엘리넬의 날개 */
            case 1102590:
            /* 세일러 벨트 */
            case 1132292:
            /* 세일러 마스크 */
            case 1012389: {
                qReward = true;
                qPotential = 20086;
                break;
            }
        }
        if (qReward) {
            Equip eqp = (Equip) item;
            eqp.setPotentialByLine(0, qPotential);
            eqp.setPotentialByLine(1, qPotential);
            eqp.setPotentialByLine(2, qPotential);
            c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
            return newSlot;
        }

        boolean badge = false;
        short badgePMad = 0;
        short badgeStr = 0;
        short badgeDex = 0;
        short badgeInt = 0;
        short badgeLuk = 0;

        switch (item.getItemId()) {
            case 1182059: { // 하프링 탐사대 뱃지
                badgePMad += Randomizer.rand(0, 3);
                badgeStr += Randomizer.rand(0, 3);
                badgeDex += Randomizer.rand(0, 3);
                badgeInt += Randomizer.rand(0, 3);
                badgeLuk += Randomizer.rand(0, 3);
                badge = true;
                break;
            }
            case 1182000:
            case 1182001: { // 슈피겔만의 브론즈 뱃지
                badgePMad += Randomizer.rand(0, 1);
                badgeStr += Randomizer.rand(0, 1);
                badgeDex += Randomizer.rand(0, 1);
                badgeInt += Randomizer.rand(0, 1);
                badgeLuk += Randomizer.rand(0, 1);
                badge = true;
                break;
            }
            case 1182002:
            case 1182003: { // 슈피겔만의 실버 뱃지
                badgePMad += Randomizer.rand(1, 2);
                badgeStr += Randomizer.rand(1, 2);
                badgeDex += Randomizer.rand(1, 2);
                badgeInt += Randomizer.rand(1, 2);
                badgeLuk += Randomizer.rand(1, 2);
                badge = true;
                break;
            }
            case 1182004:
            case 1182005: { // 슈피겔만의 골드 뱃지
                badgePMad += Randomizer.rand(2, 3);
                badgeStr += Randomizer.rand(2, 3);
                badgeDex += Randomizer.rand(2, 3);
                badgeInt += Randomizer.rand(2, 3);
                badgeLuk += Randomizer.rand(2, 3);
                badge = true;
                break;
            }
        }
        if (badge) {
            Equip eqp = (Equip) item;
            eqp.setWatk(badgePMad);
            eqp.setMatk(badgePMad);
            eqp.setStr(badgeStr);
            eqp.setDex(badgeDex);
            eqp.setInt(badgeInt);
            eqp.setLuk(badgeLuk);
            c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
            return newSlot;
        }

        boolean supportMedal = false;
        short madalPMad = 0;
        short medalAllStat = 0;

        switch (item.getItemId()) {
            case 1142984: { // 원티드 프레시
                madalPMad = 3;
                medalAllStat = 5;
                supportMedal = true;
                break;
            }
            case 1142985: { // 원티드 엘리트
                madalPMad = 5;
                medalAllStat = 7;
                supportMedal = true;
                break;
            }
        }

        if (supportMedal) {
            Equip eqp = (Equip) item;
            eqp.setWatk(madalPMad);
            eqp.setMatk(madalPMad);
            eqp.setStr(medalAllStat);
            eqp.setDex(medalAllStat);
            eqp.setInt(medalAllStat);
            eqp.setLuk(medalAllStat);
            c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
            return newSlot;
        }

        if (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP) {
            if (ii.isCash(item.getItemId())) {
                item.setFlag((short) 16);
            }
        }

        c.getSession().write(InventoryPacket.addInventorySlot(type, item));
        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            } else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) { //less work to do
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        return uniqueid;
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String gmLog) {
        return addById(c, itemId, quantity, null, null, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
        return addById(c, itemId, quantity, owner, null, 0, gmLog);
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
        return addId(c, itemId, quantity, owner, null, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, String gmLog) {
        return addById(c, itemId, quantity, owner, pet, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
        return addId(c, itemId, quantity, owner, pet, period, gmLog) >= 0;
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        switch (itemId) {
            case 1302071:
            case 1322056:
            case 1332059:
            case 1382042:
            case 1402041:
            case 1412029:
            case 1422033:
            case 1432042:
            case 1442053:
            case 1452048:
            case 1462043:
            case 1472058:
            case 1482025:
            case 1492026:
            case 1522054:
            case 1532058: {
                period = 3;
                break;
            }
        }

        switch (itemId) {

            case 1112427:
            case 1112428:
            case 1112429: {
                period = 3;
                break;
            }

            case 2430117: // 이노시스 시즌 패스 티켓 (7일)

            case 1012540:
            case 1012541:
            case 1012542:
            case 1012543:

            case 1132112:
            case 1132113:
            case 1132114:
            case 1132115:
            case 1022135:
            case 1022136:

            case 1132013:
            case 1072619:
            case 1112682:

            case 5044002: {
                period = 7;
                break;
            }
        }

        if ((ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) || (!ii.itemExists(itemId))) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(InventoryPacket.showItemUnavailable());
            return -1;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            Item eItem = (Item) i.next();
                            short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1) {
                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                c.getSession().write(InventoryPacket.updateInventorySlot(type, eItem, false));
                            }
                        } else {
                            break;
                        }
                    }
                }
                Item nItem;
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0, uniqueid);
                        newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            c.getSession().write(InventoryPacket.getInventoryFull());
                            c.getSession().write(InventoryPacket.getShowInventoryFull());
                            return -1;
                        }
                        if (gmLog != null) {
                            nItem.setGMLog(gmLog);
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0) {
                            nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.setInventoryPosition(newSlot);
                            c.getPlayer().addPet(pet);
                        }
                        c.getSession().write(InventoryPacket.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        c.getPlayer().havePartyQuest(itemId);
                        c.getSession().write(CWvsContext.enableActions());
                        return (byte) newSlot;
                    }
                }
            } else {
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    return -1;
                }
                if (period > 0) {
                    nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (gmLog != null) {
                    nItem.setGMLog(gmLog);
                }
                //nItem.setUniqueId(MapleInventoryIdentifier.getInstance());
                c.getSession().write(InventoryPacket.addInventorySlot(type, nItem));
                c.getSession().write(CWvsContext.enableActions());
            }
        } else {
            if (quantity == 1) {
                final Item nEquip = ii.getEquipById(itemId, uniqueid);
                if (owner != null) {
                    nEquip.setOwner(owner);
                }
                if (gmLog != null) {
                    nEquip.setGMLog(gmLog);
                }
                if (period > 0) {
                    nEquip.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }

                boolean qReward = false;
                int qPotential = 0;
                switch (nEquip.getItemId()) {
                    /* 머쉬킹의 레더 글러브 */
                    case 1082254:
                    /* 엘리넬의 팔찌 */
                    case 1082533: {
                        qReward = true;
                        qPotential = 10055;
                        break;
                    }
                    /* 옥토퍼스 이어링 */
                    case 1032254:
                    /* 페어리 마크 */
                    case 1022254:
                    /* 엘리넬의 날개 */
                    case 1102590:
                    /* 세일러 벨트 */
                    case 1132292:
                    /* 세일러 마스크 */
                    case 1012389: {
                        qReward = true;
                        qPotential = 20086;
                        break;
                    }
                }
                if (qReward) {
                    Equip eqp = (Equip) nEquip;
                    eqp.setPotentialByLine(0, qPotential);
                    eqp.setPotentialByLine(1, qPotential);
                    eqp.setPotentialByLine(2, qPotential);
                    c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
                }

                switch (itemId) {
                    case 1012540:
                    case 1012541:
                    case 1012542:
                    case 1012543: {
                        Equip eqp = (Equip) nEquip;
                        eqp.setPotentialByLine(0, 20086);
                        eqp.setPotentialByLine(1, 20086);
                        eqp.setPotentialByLine(2, 20086);
                        c.sendPacket(InventoryPacket.addInventorySlot(type, eqp));
                    }
                }

                if (ii.isCash(itemId)) {
                    nEquip.setFlag((short) 16);
                }

                //nEquip.setUniqueId(MapleInventoryIdentifier.getInstance());
                newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
                if (newSlot == -1) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    return -1;
                }
                c.getSession().write(InventoryPacket.addInventorySlot(type, nEquip));
                if (GameConstants.isHarvesting(itemId)) {
                    c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
                }
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        c.getPlayer().havePartyQuest(itemId);
        c.getPlayer().handleSymbolStat();
        return (byte) newSlot;
    }

    public static Item addbyId_Gachapon(final MapleClient c, final int itemId, short quantity) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) || (!ii.itemExists(itemId))) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(InventoryPacket.showItemUnavailable());
            return null;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);

        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);

            if (!GameConstants.isRechargable(itemId)) {
                Item nItem = null;
                boolean recieved = false;

                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            nItem = (Item) i.next();
                            short oldQ = nItem.getQuantity();

                            if (oldQ < slotMax) {
                                recieved = true;

                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                nItem.setQuantity(newQ);
                                c.getSession().write(InventoryPacket.updateInventorySlot(type, nItem, false));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0);
                        final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1 && recieved) {
                            return nItem;
                        } else if (newSlot == -1) {
                            return null;
                        }
                        recieved = true;
                        c.getSession().write(InventoryPacket.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (recieved) {
                    c.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0);
                final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    return null;
                }
                c.getSession().write(InventoryPacket.addInventorySlot(type, nItem));
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
        } else {
            if (quantity == 1) {
                final Item item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                final short newSlot = c.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    return null;
                }
                c.getSession().write(InventoryPacket.addInventorySlot(type, item, true));
                c.getPlayer().havePartyQuest(item.getItemId());
                return item;
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        return null;
    }

    public static boolean addFromDrop(final MapleClient c, final Item item, final boolean show) {
        return addFromDrop(c, item, show, false);
    }

    public static boolean addFromDrop(final MapleClient c, final Item item, final boolean show, final boolean enhance) {
        return addFromDrop(c, item, show, enhance, true);
    }

    public static boolean addFromDrop(final MapleClient c, Item item, final boolean show, final boolean enhance, final boolean human) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c.getPlayer() == null || (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) || (!ii.itemExists(item.getItemId()))) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(InventoryPacket.showItemUnavailable());
            return false;
        }
        final int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(item.getItemId());
            final List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) { //wth
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.showItemUnavailable());
                    return false;
                }

                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            final Item eItem = (Item) i.next();
                            final short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                                final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                c.getSession().write(InventoryPacket.updateInventorySlot(type, eItem, human));
                            }
                        } else {
                            break;
                        }
                    }
                }
                while (quantity > 0) {
                    final short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    final Item nItem = new Item(item.getItemId(), (byte) 0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    nItem.setGMLog(item.getGMLog());
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(InventoryPacket.getShowInventoryFull());
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    c.getSession().write(InventoryPacket.addInventorySlot(type, nItem, human));
                }
            } else {
                Item nItem = new Item(item.getItemId(), (byte) 0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                nItem.setGMLog(item.getGMLog());
                //nItem.setUniqueId(MapleInventoryIdentifier.getInstance());
                final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                c.getSession().write(InventoryPacket.addInventorySlot(type, nItem));
                c.getSession().write(CWvsContext.enableActions());
            }
        } else {
            if (quantity == 1) {
                if (enhance) {
                    item = checkEnhanced(item, c.getPlayer());
                }
                final short newSlot = c.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                c.getSession().write(InventoryPacket.addInventorySlot(type, item, human));
                if (GameConstants.isHarvesting(item.getItemId())) {
                    c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
                }
            } else {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case AramiaFireWorks.KEG_ID:
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                case AramiaFireWorks.SUN_ID:
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                case AramiaFireWorks.DEC_ID:
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
            }
        }
        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write(InfoPacket.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }

    private static Item checkEnhanced(final Item before, final MapleCharacter chr) {
        if (before instanceof Equip) {
            final Equip eq = (Equip) before;
            if (eq.getState() == 0 && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1) && GameConstants.canScroll(eq.getItemId()) && Randomizer.nextInt(100) >= 80) { //20% chance of pot?
                eq.resetPotential();
            }
        }
        return before;
    }

    public static boolean checkSpace(final MapleClient c, final int itemid, int quantity, final String owner) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c.getPlayer() == null || (ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false)) || (!ii.itemExists(itemid))) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(itemid);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    for (Item eItem : existing) {
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                            final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity -= (newQ - oldQ);
                        }
                        if (quantity <= 0) {
                            break;
                        }
                    }
                }
            }
            // add new slots if there is still something left
            final int numSlotsNeeded;
            if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
                numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
                numSlotsNeeded = 1;
            }
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        } else {
            return !c.getPlayer().getInventory(type).isFull();
        }
    }

    public static boolean removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, final short quantity, final boolean fromDrop) {
        return removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static boolean removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume, boolean packet) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);

            if (item.getQuantity() == 0 && !allowZero) {
                if (packet) {
                    c.getSession().write(InventoryPacket.clearInventoryItem(type, item.getPosition(), fromDrop));
                }
            } else {
                if (packet) {
                    c.getSession().write(InventoryPacket.updateInventorySlot(type, (Item) item, fromDrop));
                }
            }
            return true;
        }
        return false;
    }

    public static boolean removeFromSlot(final MapleClient c, final MapleInventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = (consume && GameConstants.isRechargable(item.getItemId())) || GameConstants.isExtractorBag(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (GameConstants.isHarvesting(item.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            if (item.getQuantity() == 0 && !allowZero) {
                c.getSession().write(InventoryPacket.clearInventoryItem(type, item.getPosition(), fromDrop));
            } else {
                c.getSession().write(InventoryPacket.updateInventorySlot(type, (Item) item, fromDrop));
            }
            return true;
        } else {
            c.sclose();
            System.out.print("복사 시도 : " + c.getPlayer().getName());
        }
        return false;
    }

    public static boolean removeAranPole(final MapleClient c, final MapleInventoryType type, final short slot, short quantity) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            c.getPlayer().getInventory(type).removeItem(slot, quantity, false);
            c.getSession().write(InventoryPacket.removearanpol(item.getPosition()));
            return true;
        }
        return false;
    }

    public static boolean removeById(final MapleClient c, final MapleInventoryType type, final int itemId, final int quantity, final boolean fromDrop, final boolean consume) {
        int remremove = quantity;
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            int theQ = item.getQuantity();
            if (remremove <= theQ && removeFromSlot(c, type, item.getPosition(), (short) remremove, fromDrop, consume)) {
                remremove = 0;
                break;
            } else if (remremove > theQ && removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume)) {
                remremove -= theQ;
            }
        }
        return remremove <= 0;
    }

    public static boolean removeFromSlot_Lock(final MapleClient c, final MapleInventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            if (ItemFlag.Locked.check(item.getFlag()) || ItemFlag.Untradable.check(item.getFlag())) {
                return false;
            }
            return removeFromSlot(c, type, slot, quantity, fromDrop, consume);
        }
        return false;
    }

    public static boolean removeById_Lock(final MapleClient c, final MapleInventoryType type, final int itemId) {
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (removeFromSlot_Lock(c, type, item.getPosition(), (short) 1, false, false)) {
                return true;
            }
        }
        return false;
    }

    public static void move(final MapleClient c, final MapleInventoryType type, final short src, final short dst) {
        if (src < 0 || dst < 0 || src == dst || type == MapleInventoryType.EQUIPPED) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        final Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        boolean bag = false, switchSrcDst = false, bothBag = false;
        short eqIndicator = -1;
        if (dst > c.getPlayer().getInventory(type).getSlotLimit()) {
            if (type == MapleInventoryType.ETC && dst > 100 && dst % 100 != 0) {
                final int eSlot = c.getPlayer().getExtendedSlot((dst / 100) - 1);
                if (eSlot > 0) {
                    final MapleStatEffect ee = ii.getItemEffect(eSlot);
                    if (dst % 100 > ee.getSlotCount() || ee.getType() != ii.getBagType(source.getItemId()) || ee.getType() <= 0) {
                        c.getPlayer().dropMessage(1, ii.getBagType(source.getItemId()) + "You may not move that item to the bag." + ee.getType());
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    } else {
                        eqIndicator = 0;
                        bag = true;
                    }
                } else {
                    c.getPlayer().dropMessage(1, "You may not move it to that bag.");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
            } else {
                c.getPlayer().dropMessage(1, "You may not move it there.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
        }
        if (src > c.getPlayer().getInventory(type).getSlotLimit() && type == MapleInventoryType.ETC && src > 100 && src % 100 != 0) {
            //source should be not null so not much checks are needed
            if (!bag) {
                switchSrcDst = true;
                eqIndicator = 0;
                bag = true;
            } else {
                bothBag = true;
            }
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        final short oldsrcQ = source.getQuantity();
        final short slotMax = ii.getSlotMax(source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        if (GameConstants.isHarvesting(source.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        if (!type.equals(MapleInventoryType.EQUIP) && initialTarget != null
                && initialTarget.getItemId() == source.getItemId()
                && initialTarget.getOwner().equals(source.getOwner())
                && initialTarget.getExpiration() == source.getExpiration()
                && !GameConstants.isRechargable(source.getItemId())
                && !type.equals(MapleInventoryType.CASH)) {
            if (GameConstants.isHarvesting(initialTarget.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            if ((olddstQ + oldsrcQ) > slotMax) {
                c.getSession().write(InventoryPacket.moveAndMergeWithRestInventoryItem(type, src, dst, (short) ((olddstQ + oldsrcQ) - slotMax), slotMax, bag, switchSrcDst, bothBag));
            } else {
                c.getSession().write(InventoryPacket.moveAndMergeInventoryItem(type, src, dst, ((Item) c.getPlayer().getInventory(type).getItem(dst)).getQuantity(), bag, switchSrcDst, bothBag));
            }
        } else {
            c.getSession().write(InventoryPacket.moveInventoryItem(type, switchSrcDst ? dst : src, switchSrcDst ? src : dst, eqIndicator, bag, bothBag));
        }
        //c.getPlayer().setSoulEnchantSkill();
    }

    public static void equip(final MapleClient c, final short src, short dst) {

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleCharacter chr = c.getPlayer();
        if (chr == null || dst == -55) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
        final PlayerStats statst = c.getPlayer().getStat();
        statst.recalcLocalStats(c.getPlayer());
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null || source.getDurability() == 0 || GameConstants.isHarvesting(source.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final Map<String, Integer> stats = ii.getEquipStats(source.getItemId());

        if (stats == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (dst > -1200 && dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId())
                && !GameConstants.isMechanicItem(source.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        } else if (((dst < -5003) || (dst <= -1200 || (dst >= -999 && dst < -99))) && (!stats.containsKey("cash"))) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        } else if (dst > -5000 && dst <= -1400) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        } else if (dst > -5100 && dst <= -5000 && source.getItemId() / 10000 != 120) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        int v1 = ((source.getItemId() / 10000) % 100);
        switch (v1) {
            case 54: {
                if (!GameConstants.isHayato(chr.getJob())) {
                    chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                break;
            }
            case 55: {
                if (!GameConstants.isKanna(chr.getJob())) {
                    chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                break;
            }
        }
        if (GameConstants.isHayato(chr.getJob())) {
            if (GameConstants.isWeapon(source.getItemId()) && v1 != 54) {
                chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            if (GameConstants.isShield(source.getItemId())) {
                chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        if (GameConstants.isKanna(chr.getJob())) {
            if (GameConstants.isWeapon(source.getItemId()) && v1 != 55) {
                chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            if (GameConstants.isShield(source.getItemId())) {
                chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }

        int a1 = (source.getItemId() / 1000);
        if ((!GameConstants.isMercedes(chr.getJob()) && !GameConstants.isDemon(chr.getJob()) && !GameConstants.isMihile(
                chr.getJob()) && !GameConstants.isPhantom(chr.getJob())) && (a1 == 1093 || a1 == 1094)) {
            if (!GameConstants.isjobFromSubWeapon(chr.getJob(), source.getItemId())) {
                chr.dropMessage(1, "현재 능력치가 낮아서 선택 된 아이템을 장착 할 수 없습니다.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }

        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), c.getPlayer().getStat().levelBonus) && !GameConstants.isXenon(chr.getJob()) && !GameConstants.isDemonAvenger(chr.getJob())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11 && source.getItemId() != 1342069) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (dst == -18 && !GameConstants.isMountItemAvailable(source.getItemId(), c.getPlayer().getJob())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (dst == -118 && source.getItemId() / 10000 != 190) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (dst == -59) { //pendant
            MapleQuestStatus stat = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            if (stat == null || stat.getCustomData() == null || Long.parseLong(stat.getCustomData()) < System.currentTimeMillis()) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
        }
        if (GameConstants.isKatara(source.getItemId()) || source.getItemId() / 10000 == 135) {
            dst = (byte) -10; //Shield Slot
        }
        //[CUSTOM]: CashCover flag, no longer affects cash shop items.
        if (source.getCashCover() == 1 && !MapleItemInformationProvider.getInstance().isCash(source.getItemId())) {
            dst -= 100;
        }
        if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        if (GameConstants.isMechanicItem(source.getItemId()) && (chr.getJob() < 3500 || chr.getJob() > 3512)) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (source.getItemId() / 1000 == 1112) { //ring
            for (RingSet s : RingSet.values()) {
                if (s.id.contains(Integer.valueOf(source.getItemId()))) {
                    List<Integer> theList = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).listIds();
                    for (Integer i : s.id) {
                        if (theList.contains(i)) {
                            c.getPlayer().dropMessage(1, "You may not equip this item because you already have a " + (StringUtil.makeEnumHumanReadable(s.name())) + " equipped.");
                            c.getSession().write(CWvsContext.enableActions());
                            return;
                        }
                    }
                }
            }
        }
        switch (dst) {
            case -6: { // Top
                final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                if (top != null && GameConstants.isOverall(top.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(CWvsContext.serverNotice(1, "You must first make room in your inventory to allow space for equipped items to unequip."));
                        return;
                    }
                    unequip(c, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -5: {
                final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                final Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -6);
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull(bottom != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(CWvsContext.serverNotice(1, "You must first make room in your inventory to allow space for equipped items to unequip."));
                        return;
                    }
                    unequip(c, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(CWvsContext.serverNotice(1, "You must first make room in your inventory to allow space for equipped items to unequip."));
                        return;
                    }
                    unequip(c, (byte) -6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -10: {
                Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() < 430 || chr.getJob() > 434) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                } else if (weapon != null && GameConstants.isTwoHanded(weapon.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    //unequip(c, (byte) -11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -11: {
                Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
                if (shield != null && GameConstants.isTwoHanded(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(InventoryPacket.getShowInventoryFull());
                        return;
                    }
                    //unequip(c, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -152: { //secondary items
                Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
                if (shield != null) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write(InventoryPacket.getInventoryFull());
                        c.getSession().write(CWvsContext.serverNotice(1, "You must first make room in your inventory to allow space for equipped items to unequip."));
                        return;
                    }
                    unequip(c, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
        }

        int a2 = (source.getItemId() / 10000);
        if (a2 == 119) {
            dst = -20;
        }

        if (source.getItemId() == 1342069) {
            dst = -110;
        }
        source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src); // Equip
        target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst); // Currently equipping
        if (source == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        short flag = source.getFlag();
        if (ItemFlag.KarmasCissors.check(flag)) {
            flag |= ItemFlag.KarmasCissors.getValue();
            source.setFlag((byte) (flag - ItemFlag.KarmasCissors.getValue()));
            c.getSession().write(InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
        }
        /*
        if (stats.get("equipTradeBlock") != null || source.getItemId() / 10000 == 167) { // Block trade when equipped.
            final Item android = (Item) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27);
            if (!ItemFlag.Untradable.check(flag)) {
                flag |= ItemFlag.Untradable.getValue();
                source.setFlag(flag);
                c.getSession().write(InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
                if (android != null) {
                    chr.setAndroid(source.getAndroid());
                }
            }
        }
        */
        /*
        if (source.getItemId() / 10000 == 167) { //Heart
            final Equip android = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27);
            chr.removeAndroid();
            if (android != null) {
                if (ItemFlag.Used.check(flag)) {

                    if (android.getAndroid() != null) {
                        chr.setAndroid(android.getAndroid());
                    } else {
                        chr.dropMessage(1, "오류가 발생했습니다!");
                        System.err.println("[오류] " + chr.getName() + "의 " + android.getUniqueId() + " 유니크 아이디의 안드로이드에서 NULL 포인터가 발생했습니다.");
                        return;
                    }
                } else {
                    int uid = MapleInventoryIdentifier.getInstance();
                    source.setUniqueId(uid);
                    source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                    //flag |= ItemFlag.ANDROID_ACTIVATED.getValue();
                    //flag |= ItemFlag.UNTRADEABLE.getValue();
                    source.setFlag(flag);
                    chr.removeAndroid();
                    chr.setAndroid(source.getAndroid());
                    c.getSession().write(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
                }
            }
        }
        if (source.getItemId() / 10000 == 166) {
            final Item heart = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -28);
            chr.removeAndroid();
            if (heart != null) {
                if (ItemFlag.Used.check(flag)) {
                    if (source.getAndroid() != null) {
                        chr.setAndroid(source.getAndroid());
                    } else {
                        chr.dropMessage(1, "오류가 발생했습니다!");
                        System.err.println("[오류] " + chr.getName() + "의 " + source.getUniqueId() + " 유니크 아이디의 안드로이드에서 NULL 포인터가 발생했습니다.");
                        return;
                    }
                } else {
                    int uid = MapleInventoryIdentifier.getInstance();
                    source.setUniqueId(uid);
                    source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                    flag |= ItemFlag.Used.getValue();
                    flag |= ItemFlag.Untradable.getValue();
                    source.setFlag(flag);
                    chr.removeAndroid();
                    chr.setAndroid(source.getAndroid());
                    c.getSession().write(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
                }
            }

            if (source.getAndroid() == null) {
                int uid = MapleInventoryIdentifier.getInstance();
                source.setUniqueId(uid);
                source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                //flag = (short) (flag | ItemFlag.Locked.getValue());
                flag = (short) (flag | ItemFlag.Untradable.getValue());
                flag = (short) (flag | ItemFlag.Used.getValue());
                source.setFlag(flag);
                c.getSession().write(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
            }
            chr.removeAndroid();
            chr.setAndroid(source.getAndroid());
            
        } else if ((dst <= -1200) && (chr.getAndroid() != null)) { // -1300
            chr.updateAndroid();
        }
        */
        /*
        if (source.getItemId() / 10000 == 166 || source.getItemId() / 10000 == 167) {
            final Item heart = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -28);
            final Item android = (Item) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27);
            if (heart != null && android != null) {

                if (source.getAndroid() == null && android != null) {
                    int uid = MapleInventoryIdentifier.getInstance();
                    source.setUniqueId(uid);
                    source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                    //flag = (short) (flag | ItemFlag.Locked.getValue());
                    flag = (short) (flag | ItemFlag.Untradable.getValue());
                    flag = (short) (flag | ItemFlag.Used.getValue());
                    source.setFlag(flag);
                    c.getSession().write(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
                }
                chr.removeAndroid();
                chr.setAndroid(source.getAndroid());
            }
        } else if ((dst <= -1200) && (chr.getAndroid() != null)) {
            chr.setAndroid(source.getAndroid());
        }
        */
        if (source.getCharmEXP() > 0 && !ItemFlag.NoNonCombatStatGain.check(flag)) {
            chr.getTrait(MapleTraitType.charm).addExp(source.getCharmEXP(), chr);
            source.setCharmEXP((short) 0);
            flag |= ItemFlag.NoNonCombatStatGain.getValue();
            source.setFlag(flag);
            c.getSession().write(InventoryPacket.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType(), c.getPlayer()));
            if (ItemFlag.KarmasCissors.check(flag)) {
                flag |= ItemFlag.KarmasCissors.getValue();
                source.setFlag((byte) (flag - ItemFlag.KarmasCissors.getValue()));
                c.getSession().write(InventoryPacket.updateSpecialItemUse_(source, MapleInventoryType.EQUIP.getType(), c.getPlayer()));
            }
        }

        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.Booster);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.NoBulletConsume);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.SoulArrow);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.AssistCharge);
        }
        if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.MonsterRiding);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.Mechanic);
        } else if (source.getItemId() == 1122334) {
            chr.dropMessage(5, "정령의 펜던트 장착으로 인해 몬스터 사냥시 보너스 경험치 15%를 추가로 획득하게 됩니다.");
            //chr.startFairySchedule(true, true);
        }

        if (target != null) {
            if (target.getState() > 16) {
                int[] potentials = {target.getPotentialByLine(0), target.getPotentialByLine(1), target.getPotentialByLine(2)};
                for (int i : potentials) {
                    if (i > 0) {
                        int rLevel = ii.getReqLevel(target.getItemId()) / 10;
                        StructItemOption pot = ii.getPotentialInfo(i).get(rLevel > 19 ? 19 : rLevel);
                        if (pot != null && pot.get("skillID") > 0) {
                            c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), c.getPlayer().getJob())), (byte) -1, (byte) -1);
                        }
                    }
                }
            }
        }
        if (source.getState() > 16) {
            int[] potentials = {source.getPotentialByLine(0), source.getPotentialByLine(1), source.getPotentialByLine(2)};
            for (int i : potentials) {
                if (i > 0) {
                    int rLevel = ii.getReqLevel(source.getItemId()) / 10;
                    StructItemOption pot = ii.getPotentialInfo(i).get(rLevel > 19 ? 19 : rLevel);
                    if (pot != null && pot.get("skillID") > 0) {
                        c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), c.getPlayer().getJob())), (byte) 1, (byte) 0);
                    }
                }
            }
        }
        c.getSession().write(InventoryPacket.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (byte) 2, false, false));
        chr.equipChanged();
        //chr.setSoulEnchantSkill();
    }

    public static void unequip(final MapleClient c, final short src, final short dst) {
        Equip source = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);

        if (dst < 0 || source == null) { // || (src == -55)
            return;
        }
        if (target != null && src <= 0) { // do not allow switching with equip
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }

        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }

        if (GameConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.Booster);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.NoBulletConsume);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.SoulArrow);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.WeaponCharge);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.AssistCharge);
        } else if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.MonsterRiding);
            c.getPlayer().cancelEffectFromBuffStat(CharacterTemporaryStat.Mechanic);
            //  } else if (source.getItemId() / 10000 == 166) {
            //      c.getPlayer().removeAndroid();
            //  } else if (src <= -1300 && c.getPlayer().getAndroid() != null) {
            //      c.getPlayer().setAndroid(c.getPlayer().getAndroid());

        } else if (source.getItemId() / 10000 == 166 || source.getItemId() / 10000 == 167) {
            //System.out.print("해제");
            c.getPlayer().removeAndroid();
        } else if (src <= -1300 && c.getPlayer().getAndroid() != null) {
            //   c.getPlayer().updateAndroid();
        } else if (source.getItemId() == 1122334) {
            c.getPlayer().cancelFairySchedule(true);
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (source.getState() > 16) {
            int[] potentials = {source.getPotentialByLine(0), source.getPotentialByLine(1), source.getPotentialByLine(2)};
            for (int i : potentials) {
                if (i > 0) {
                    int rLevel = ii.getReqLevel(source.getItemId()) / 10;
                    StructItemOption pot = ii.getPotentialInfo(i).get(rLevel > 19 ? 19 : rLevel);
                    if (pot != null && pot.get("skillID") > 0) {
                        c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), c.getPlayer().getJob())), (byte) -1, (byte) -1);
                    }
                }
            }
        }
        c.getSession().write(InventoryPacket.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (byte) 1, false, false));
        c.getPlayer().equipChanged();
        //c.getPlayer().setSoulEnchantSkill();
    }

    public static boolean drop(final MapleClient c, MapleInventoryType type, final short src, final short quantity) {
        return drop(c, type, src, quantity, false);
    }

    public static boolean drop(final MapleClient c, MapleInventoryType type, final short src, short quantity, final boolean npcInduced) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return false;
        }
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        if (quantity < 0 || source == null || src == -55 || (!npcInduced && GameConstants.isPet(source.getItemId())) || (quantity == 0 && !GameConstants.isRechargable(source.getItemId())) || c.getPlayer().inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (source.getItemId() == 2430117 || source.getItemId() == 2430118) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        final short flag = source.getFlag();
        if (quantity > source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (ItemFlag.Locked.check(flag) || (quantity != 1 && type == MapleInventoryType.EQUIP)) { // hack
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        final Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            final Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));
            c.getSession().write(InventoryPacket.dropInventoryItemUpdate(type, source));

            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                boolean v1 = false;
                if (ItemFlag.KarmasCissors.check(flag)) {
                    v1 = true;
                    target.setFlag((byte) (flag - ItemFlag.KarmasCissors.getValue()));
                }
                if (ItemFlag.PreventSlipping.check(flag)) {
                    v1 = true;
                    target.setFlag((byte) (flag - ItemFlag.PreventSlipping.getValue()));
                }
                if (v1) {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            } else {
                if (GameConstants.isPet(source.getItemId()) || ItemFlag.Untradable.check(flag)) {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                }
            }
        } else {
            c.getPlayer().getInventory(type).removeSlot(src);
            if (GameConstants.isHarvesting(source.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            c.getSession().write(InventoryPacket.dropInventoryItem((src < 0 ? MapleInventoryType.EQUIP : type), src));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                boolean v1 = false;
                if (ItemFlag.KarmasCissors.check(flag)) {
                    v1 = true;
                    source.setFlag((byte) (flag - ItemFlag.KarmasCissors.getValue()));
                }
                if (ItemFlag.PreventSlipping.check(flag)) {
                    v1 = true;
                    source.setFlag((byte) (flag - ItemFlag.PreventSlipping.getValue()));
                }
                if (v1) {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            } else {
                if (GameConstants.isPet(source.getItemId()) || ItemFlag.Untradable.check(flag)) {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                }
            }
        }
        //c.getPlayer().setSoulEnchantSkill();
        return true;
    }
}
