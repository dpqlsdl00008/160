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
package tools.packet;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.List;

import client.MapleClient;
import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.CashShop;
import server.CashItemFactory;
import server.CashItemInfo.CashModInfo;

import handling.SendPacketOpcode;

import java.util.Collection;

import tools.Pair;

import java.util.Map;
import java.util.Map.Entry;

import tools.data.OutPacket;

public class CCashShop {

    private static byte Operation_Code = 0x3E; // We could just change this everytime a version updates

    //private static final byte[] bestItems = HexTool.getByteArrayFromHexString("02 00 00 00 31 00 00 00 0A 00 10 00 12 00 0E 07 E0 3B 8B 0B 60 CE 8A 0B 69 00 6C 00 6C 00 2F 00 35 00 33 00 32 00 30 00 30 00 31 00 31 00 2F 00 73 00 75 00 6D 00 6D 00 6F 00 6E 00 2F 00 61 00 74 00 74 00 61 00 63 00 6B 00 31 00 2F 00 31 00 00 00 00 00 00 00 00 00 02 00 1A 00 04 01 08 07 02 00 00 00 32 00 00 00 05 00 1C 00 06 00 08 07 A0 01 2E 00 58 CD 8A 0B");
    public static byte[] warpCS(MapleClient c) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SET_CASHSHOP.getValue());
        PacketHelper.addCharacterInfo(mplew, c.getPlayer());
        Collection<CashModInfo> cmi = CashItemFactory.getInstance().getAllModInfo();
        mplew.writeShort(cmi.size() + 17);
        for (CashModInfo cm : cmi) {
            addModCashItemInfo(mplew, cm);
        }

        for (int i = 21000000; i <= 21000016; ++i) {
            mplew.writeInt(i);
            mplew.writeInt(0x100);
            mplew.write(0);
        }

        mplew.write(5);
        mplew.writeZeroBytes(120);
        for (int i = 1; i < 9; i++) {
            for (int j = 0; j < 2; j++) {
                mplew.writeInt(i);
                mplew.writeInt(j);
                mplew.writeInt(50000028); // 3위

                mplew.writeInt(i);
                mplew.writeInt(j);
                mplew.writeInt(50000029); // 4위

                mplew.writeInt(i);
                mplew.writeInt(j);
                mplew.writeInt(50000030); // 5위

                mplew.writeInt(i);
                mplew.writeInt(j);
                mplew.writeInt(50000026); // 1위

                mplew.writeInt(i);
                mplew.writeInt(j);
                mplew.writeInt(50000027); // 2위
            }
        }
        
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static void addModCashItemInfo(OutPacket mplew, CashModInfo item) {
        int flags = item.flags;
        mplew.writeInt(item.sn);
        mplew.writeInt(flags);
        if ((flags & 0x1) != 0) {
            mplew.writeInt(item.itemid);
        }
        if ((flags & 0x2) != 0) {
            mplew.writeShort(item.count);
        }
        if ((flags & 0x10) != 0) {
            mplew.write(item.priority);
        }
        if ((flags & 0x4) != 0) {
            mplew.writeInt(item.discountPrice);
        }
        if ((flags & 0x8) != 0) {
            mplew.write(item.unk_1 - 1);
        }
        if ((flags & 0x10) != 0) {
            mplew.write(item.priority);
        }
        if ((flags & 0x20) != 0) {
            mplew.writeShort(item.period);
        }
        if ((flags & 0x40) != 0) {
            mplew.writeInt(0); // maple point
        }
        if ((flags & 0x80) != 0) {
            mplew.writeInt(item.meso);
        }
        if ((flags & 0x100) != 0) {
            mplew.write(item.unk_2 - 1); // ForPremiumUser
        }
        if ((flags & 0x200) != 0) {
            mplew.write(item.gender);
        }
        if ((flags & 0x400) != 0) {
            mplew.write(item.showUp ? 1 : 0); // onSale
        }
        if ((flags & 0x800) != 0) {
            mplew.write(item.mark); // Class
        }
        if ((flags & 0x1000) != 0) {
            mplew.write(item.unk_3 - 1); // reqLevel
        }
        if ((flags & 0x2000) != 0) {
            mplew.writeShort(0); // PbCash
        }
        if ((flags & 0x4000) != 0) {
            mplew.writeShort(0); // PbPoint
        }
        if ((flags & 0x8000) != 0) {
            mplew.writeShort(0); // PbGift
        }
        if ((flags & 0x10000) != 0) {
            List<Integer> pack = CashItemFactory.getInstance().getPackageItems(item.sn);
            if (pack == null) {
                mplew.write(0);
            } else {
                mplew.write(pack.size());
                for (int i = 0; i < pack.size(); i++) {
                    mplew.writeInt(pack.get(i));
                }
            }
        }
        if ((flags & 0x20000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x40000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x80000) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 0x100000) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 0x200000) != 0) {
            mplew.write(0);
        }
        if ((flags & 0x400000) != 0) {
            mplew.write(0);
        }
    }

    public static byte[] enableUse3(MapleCharacter c) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x45);
        mplew.write(c.getGender());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] chargeCash() { // Useless, doesn't do anything.
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_CHARGE_CASH.getValue());
        mplew.writeMapleAsciiString("http://www.google.com");
        mplew.writeMapleAsciiString("http://www.google.com");

        return mplew.getPacket();
    }

    public static byte[] showNXMapleTokens(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_UPDATE.getValue());
        mplew.writeInt(chr.getCSPoints(1)); // NX Credit THIS NIGGER RIGHT HERE LOL finally 
        mplew.writeInt(chr.getCSPoints(2)); // MPoint
        return mplew.getPacket();
    }

    public static byte[] showMapleTokens(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SHOW_MAPLE_POINT.getValue());
        mplew.writeInt(chr.getCSPoints(1)); // MPoint 캐시표시
        return mplew.getPacket();
    }

    public static byte[] LimitGoodsCountChanged() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code);
        mplew.writeInt(0); // SN
        mplew.writeInt(0); // Count
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getCSInventory(MapleClient c) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x42);
        CashShop mci = c.getPlayer().getCashInventory();
        mplew.writeShort(mci.getItemsSize());
        if (mci.getItemsSize() > 0) {
            int size = 0;
            for (Item itemz : mci.getInventory()) {
                addCashItemInfo(mplew, itemz, c.getAccID(), 0);
                if (GameConstants.isPet(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    size++;
                }
            }
            mplew.writeInt(size);
            for (Item itemz : mci.getInventory()) {
                if (GameConstants.isPet(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(mplew, itemz);
                }
            }
        }
        mplew.writeShort(c.getPlayer().getStorage().getSlots());
        mplew.writeShort(c.getCharacterSlots());
        mplew.writeShort(0);
        mplew.writeShort(0); //04 00 <-- added?
        return mplew.getPacket();
    }

    public static byte[] getCSGifts(MapleClient c) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(68);
        List<Pair<Item, String>> mci = c.getPlayer().getCashInventory().loadGifts();
        mplew.writeShort(mci.size());
        for (Pair<Item, String> mcz : mci) { // 70 Bytes, need to recheck.
            mplew.writeLong(mcz.getLeft().getUniqueId());
            mplew.writeInt(mcz.getLeft().getItemId());
            mplew.writeAsciiString(mcz.getLeft().getGiftFrom(), 13);
            mplew.writeAsciiString(mcz.getRight(), 73);
        }

        return mplew.getPacket();
    }

    public static byte[] sendWishList(MapleCharacter chr, boolean update) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(update ? 72 : 70);
        int[] list = chr.getWishlist();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSItem(Item item, int sn, int accid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 12);
        addCashItemInfo(mplew, item, accid, sn);

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSItem(int itemid, int sn, int uniqueid, int accid, int quantity, String giftFrom, long expire) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 12);
        addCashItemInfo(mplew, uniqueid, accid, itemid, sn, quantity, giftFrom, expire);

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSItemFailed(final int mode, final int sn) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 13);
        mplew.write(mode); // 0/1/2 = transfer, Rest = code
        if (mode == 158 || mode == 159 || mode == 160) { // ida에서 나온값
            mplew.writeInt(sn);
        } else if (mode == 205) { // ida에서 나온값
            mplew.write(1);    // Hour?
        } else if (mode == 221) { // ida에서 나온값
            mplew.writeInt(sn);
            mplew.writeLong(System.currentTimeMillis());
        }

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSPackage(Map<Integer, Item> ccc, int accid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(128); //use to be 7a
        mplew.write(ccc.size());
        int size = 0;
        for (Entry<Integer, Item> sn : ccc.entrySet()) {
            addCashItemInfo(mplew, sn.getValue(), accid, sn.getKey().intValue());
            if (GameConstants.isPet(sn.getValue().getItemId()) || GameConstants.getInventoryType(sn.getValue().getItemId()) == MapleInventoryType.EQUIP) {
                size++;
            }
        }
        if (ccc.size() > 0) {
            for (Item itemz : ccc.values()) {
                if (GameConstants.isPet(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(mplew, itemz);
                }
            }
        }
        mplew.writeShort(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] sendGift(int price, int itemid, int quantity, String receiver, boolean packages) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (packages ? 68 : 19));
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);
        if (packages) {
            mplew.writeShort(0); //maplePoints
        }
        mplew.writeInt(price);

        return mplew.getPacket();
    }

    public static byte[] showCouponRedeemedItem(Map<Integer, Item> items, int mesos, int maplePoints, MapleClient c) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 14);
        mplew.write(items.size());
        for (Entry<Integer, Item> item : items.entrySet()) {
            addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey().intValue());
        }
        mplew.writeInt(maplePoints);
        mplew.writeInt(0); // Normal items size
        //for (Pair<Integer, Integer> item : items2) {
        //    mplew.writeInt(item.getRight()); // Count
        //    mplew.writeInt(item.getLeft());  // Item ID
        //}
        mplew.writeInt(mesos);

        return mplew.getPacket();
    }

    public static byte[] showCouponGifted(Map<Integer, Item> items, String receiver, MapleClient c) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 16); // 22 = Failed. [Mode - 0/2 = transfer, 15 = invalid 3 times]
        mplew.writeMapleAsciiString(receiver); // Split by ;
        mplew.write(items.size());
        for (Entry<Integer, Item> item : items.entrySet()) {
            addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey().intValue());
        }
        mplew.writeInt(0); // (amount of receiver - 1)

        return mplew.getPacket();
    }

    public static byte[] increasedInvSlots(int inv, int slots) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 21);
        mplew.write(inv);
        mplew.writeShort(slots);

        return mplew.getPacket();
    }

    public static byte[] increasedStorageSlots(int slots, boolean characterSlots) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (characterSlots ? 25 : 23)); // 32 = Buy Character. O.O
        mplew.writeShort(slots);

        return mplew.getPacket();
    }

    public static byte[] increasedPendantSlots() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 29); // 35 = Failed
        mplew.writeShort(0); // 0 = Add, 1 = Extend
        mplew.writeShort(100); // Related to time->Low/High fileTime
        // The time limit for the %s slot \r\nhas been extended to %d-%d-%d %d:%d.

        return mplew.getPacket();
    }

    public static byte[] confirmFromCSInventory(Item item, short pos) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(93); // 37 = Failed
        mplew.writeShort(pos);
        PacketHelper.addItemInfo(mplew, item);
        mplew.writeInt(0); // For each: 8 bytes(Could be 2 ints or 1 long)

        return mplew.getPacket();
    }

    public static byte[] confirmToCSInventory(Item item, int accId, int sn) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(95); // 39 = Failed
        addCashItemInfo(mplew, item, accId, sn, false);

        return mplew.getPacket();
    }

    public static byte[] cashItemDelete(int uniqueid) {//tocsinventory + 2
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 35); // 41 = Failed
        mplew.writeLong(uniqueid); // or SN?

        return mplew.getPacket();
    }

    public static byte[] rebateCashItem() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 62); // 41 = Failed
        mplew.writeLong(0); // UniqueID
        mplew.writeInt(0); // MaplePoints accumulated
        mplew.writeInt(0); // For each: 8 bytes.

        return mplew.getPacket();
    }

    public static byte[] sendBoughtRings(boolean couple, Item item, int sn, int accid, String receiver) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (couple ? 64 : 74));
        addCashItemInfo(mplew, item, accid, sn);
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(item.getItemId());
        mplew.writeShort(1); // Count

        return mplew.getPacket();
    }

    public static byte[] receiveFreeCSItem(Item item, int sn, int accid) { //없나봄
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 82); // 105 = Buy Name Change, 107 = Transfer world
        addCashItemInfo(mplew, item, accid, sn);

        return mplew.getPacket();
    }

    public static byte[] cashItemExpired(int uniqueid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 37);
        mplew.writeLong(uniqueid);

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSQuestItem(int price, short quantity, byte position, int itemid) { // 몬찾겟다
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 70); // 76 = Failed.
        mplew.writeInt(1); // size. below gets repeated for each.
        mplew.writeInt(quantity);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] updatePurchaseRecord() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 85); //추후에
        mplew.writeInt(0);
        mplew.write(1); // boolean

        return mplew.getPacket();
    }

    public static byte[] sendCashRefund(final int cash) {
        // Your refund has been processed. \r\n(%d NX Refund)
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 92); //추후에
        mplew.writeInt(0); // Item Size.->For each 8 bytes.
        mplew.writeInt(cash); // NX

        return mplew.getPacket();
    }

    public static byte[] sendRandomBox(int uniqueid, Item item, short pos) { // have to revise this
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 92); // 100 = Failed
        mplew.writeLong(uniqueid);
        mplew.writeInt(1302000);
        PacketHelper.addItemInfo(mplew, item);
        mplew.writeShort(0);
        mplew.writeInt(0); // Item Size.->For each 8 bytes.

        return mplew.getPacket();
    }

    public static byte[] sendCashGachapon(final boolean cashItem, int idFirst, Item item, int accid) { // Xmas Surprise, Cash Shop Surprise
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 104); // 110 = Failed.		
        mplew.writeLong(idFirst); //uniqueid of the xmas surprise itself
        mplew.writeInt(0);
        mplew.write(cashItem ? 1 : 0);
        if (cashItem) {
            addCashItemInfo(mplew, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        }
        mplew.writeInt(item.getItemId());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] sendTwinDragonEgg(boolean test1, boolean test2, int idFirst, Item item, int accid) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 106); // 112 = Failed.		
        mplew.write(test1 ? 1 : 0);
        mplew.write(test2 ? 1 : 0);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(3);
        mplew.writeInt(4);
        if (test1 && test2) {
            addCashItemInfo(mplew, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        }

        return mplew.getPacket();
    }

    public static byte[] sendBoughtMaplePoints(final int maplePoints) {
        // You've received %d Maple Points.
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 108);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(maplePoints);

        return mplew.getPacket();
    }

    public static byte[] playCashSong(int itemid, String name) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAY_JUKEBOX.getValue());
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }
    
    public static byte[] ViciousHammer(boolean start, boolean hammered) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());
        mplew.write(start ? 0 : 2);
        mplew.writeInt(hammered ? 0 : 1);
        return mplew.getPacket();
    }

    public static byte[] changePetName(MapleCharacter chr, String newname, int slot) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PET_NAME_CHANGED.getValue());

        mplew.writeInt(chr.getId());
        mplew.writeInt(0);
        mplew.writeMapleAsciiString(newname);
        mplew.writeInt(slot);
        return mplew.getPacket();
    }

    public static byte[] OnMemoResult(final byte act, final byte mode) {
        OutPacket mplew = new OutPacket();

        //04 // The note has successfully been sent 
        //05 00 // The other character is online now. Please use the whisper function. 
        //05 01 // Please check the name of the receiving character. 
        //05 02 // The receiver's inbox is full. Please try again. 
        mplew.writeShort(SendPacketOpcode.OnMemoResult.getValue());
        mplew.write(act);
        if (act == 5) {
            mplew.write(mode);
        }

        return mplew.getPacket();
    }

    public static byte[] showNotes(final ResultSet notes, final int count) throws SQLException {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnMemoResult.getValue());
        mplew.write(3);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(PacketHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(notes.getInt("gift"));
            notes.next();
        }

        return mplew.getPacket();
    }

    public static byte[] useChalkboard(final int charid, final String msg) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.AD_BOARD.getValue());

        mplew.writeInt(charid);
        if (msg == null || msg.length() <= 0) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }

        return mplew.getPacket();
    }

    public static byte[] OnMapTransferResult(MapleCharacter chr, byte vip, boolean delete) {
        OutPacket mplew = new OutPacket();

        // 29 00 05/08 00 // You cannot go to that place.
        // 29 00 06 00 // (null) is currently difficult to locate, so the teleport will not take place.
        // 29 00 09 00 // It's the map you're currently on.
        // 29 00 0A 00 // This map is not available to enter for the list.
        // 29 00 0B 00 // Users below level 7 are not allowed to go out from Maple Island.
        mplew.writeShort(SendPacketOpcode.OnMapTransferResult.getValue());
        mplew.write(delete ? 2 : 3);
        mplew.write(vip);
        if (vip == 1) {
            int[] map = chr.getRegRocks();
            for (int i = 0, j = 0; j < 5; j++) {
                if (map[j] != 999999999) {
                    if (i < j) {
                        int tmp = map[i];
                        map[i] = map[j];
                        map[j] = tmp;
                    }
                    i++;
                }
            }
            for (int i = 0; i < 5; i++) {
                mplew.writeInt(map[i]);
            }
        } else if (vip == 2) {
            int[] map = chr.getRocks();
            for (int i = 0, j = 0; j < 10; j++) {
                if (map[j] != 999999999) {
                    if (i < j) {
                        int tmp = map[i];
                        map[i] = map[j];
                        map[j] = tmp;
                    }
                    i++;
                }
            }
            for (int i = 0; i < 10; i++) {
                mplew.writeInt(map[i]);
            }
        } else if (vip == 3 || vip == 5) {
            int[] map = chr.getHyperRocks();
            for (int i = 0, j = 0; j < 13; j++) {
                if (map[j] != 999999999) {
                    if (i < j) {
                        int tmp = map[i];
                        map[i] = map[j];
                        map[j] = tmp;
                    }
                    i++;
                }
            }
            for (int i = 0; i < 13; i++) {
                mplew.writeInt(map[i]);
            }
        }
        return mplew.getPacket();
    }

    public static final void addCashItemInfo(OutPacket mplew, Item item, int accId, int sn) {
        addCashItemInfo(mplew, item, accId, sn, true);
    }

    public static final void addCashItemInfo(OutPacket mplew, Item item, int accId, int sn, boolean isFirst) {
        addCashItemInfo(mplew, item.getUniqueId(), accId, item.getItemId(), sn, item.getQuantity(), item.getGiftFrom(), item.getExpiration(), isFirst); //owner for the lulz
    }

    public static final void addCashItemInfo(OutPacket mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire) {
        addCashItemInfo(mplew, uniqueid, accId, itemid, sn, quantity, sender, expire, true);
    }

    public static final void addCashItemInfo(OutPacket mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire, boolean isFirst) {
        mplew.writeLong(uniqueid > 0 ? uniqueid : 0);
        mplew.writeLong(accId);
        mplew.writeInt(itemid);
        mplew.writeInt(isFirst ? sn : 0);
        mplew.writeShort(quantity);
        mplew.writeAsciiString(sender, 13); //owner for the lulzlzlzl  
        PacketHelper.addExpirationTime(mplew, expire);
        mplew.writeLong(isFirst ? 0 : sn);
        mplew.writeZeroBytes(10);
    }
    
    public static byte[] sendCSFail(int err) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x7F);
        mplew.write(err);
        // 1: Request timed out.\r\nPlease try again.
        // 3: You don't have enough cash.
        // 4: You can't buy someone a cash item gift if you're under 14.
        // 5: You have exceeded the allotted limit of price\r\nfor gifts.
        // 10: Please check and see if you have exceeded\r\nthe number of cash items you can have.
        // 11: Please check and see\r\nif the name of the character is wrong,\r\nor if the item has gender restrictions.
        // 44/69: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
        // 22: Due to gender restrictions, the coupon \r\nis unavailable for use.
        // 17: This coupon was already used.
        // 16: This coupon has expired.
        // 18: This coupon can only be used at\r\nNexon-affiliated Internet Cafe's.\r\nPlease use the Nexon-affiliated Internet Cafe's.
        // 19: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been used.
        // 20: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been expired.
        // 14: Please check and see if \r\nthe coupon number is right.
        // 23: This coupon is only for regular items, and \r\nit's unavailable to give away as a gift.
        // 24: This coupon is only for MapleStory, and\r\nit cannot be gifted to others.
        // 25: Please check if your inventory is full or not.
        // 26: This item is only available for purchase by a user at the premium service internet cafe.
        // 27: You are sending a gift to an invalid recipient.\r\nPlease check the character name and gender.
        // 28: Please check the name of the receiver.
        // 29: Items are not available for purchase\r\n at this hour.
        // 30: The item is out of stock, and therefore\r\nnot available for sale.
        // 31: You have exceeded the spending limit of NX.
        // 32: You do not have enough mesos.
        // 33: The Cash Shop is unavailable\r\nduring the beta-test phase.\r\nWe apologize for your inconvenience.
        // 34: Check your PIC password and\r\nplease try again.
        // 37: Please verify your 2nd password and\r\ntry again.
        // 21: This is the NX coupon number.\r\nRegister your coupon at www.nexon.net.
        // 38: This coupon is only available to the users buying cash item for the first time.
        // 39: You have already applied for this.
        // 47: You have exceeded the maximum number\r\nof usage per account\for this account.\r\nPlease check the coupon for detail.
        // 49: The coupon system will be available soon.
        // 50: This item can only be used 15 days \r\nafter the account's registration.
        // 51: You do not have enough Gift Tokens \r\nin your account. Please charge your account \r\nwith Nexon Game Cards to receive \r\nGift Tokens to gift this item.
        // 52: Due to technical difficulties,\r\nthis item cannot be sent at this time.\r\nPlease try again.
        // 53: You may not gift items for \r\nit has been less than two weeks \r\nsince you first charged your account.
        // 54: Users with history of illegal activities\r\n may not gift items to others. Please make sure \r\nyour account is neither previously blocked, \r\nnor illegally charged with NX.
        // 55: Due to limitations, \r\nyou may not gift this item as this time. \r\nPlease try again later.
        // 56: You have exceeded the amount of time \r\nyou can gift items to other characters.
        // 57: This item cannot be gifted \r\ndue to technical difficulties. \r\nPlease try again later.
        // 58: You cannot transfer \r\na character under level 20.
        // 59: You cannot transfer a character \r\nto the same world it is currently in.
        // 60: You cannot transfer a character \r\ninto the new server world.
        // 61: You may not transfer out of this \r\nworld at this time.
        // 62: You cannot transfer a character into \r\na world that has no empty character slots.
        // 63: The event has either ended or\r\nthis item is not available for free testing.
        // 6: You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
        // 7: That character could not be found in this world.\r\nGifts can only be sent to character\r\nin the same world.
        // 8: This item has a gender restriction.\r\nPlease confirm the gender of the recipient.
        // 9: The gift cannot be sent because\r\nthe recipient's Inventory is full.
        // 64: This item cannot be purchased \r\nwith MaplePoints.
        // 65: Sorry for inconvinence. \r\nplease try again.
        // 67: This item cannot be\r\npurchased by anyone under 7.
        // 68: This item cannot be\r\nreceived by anyone under 7.
        // 66: You can no longer purchase or gift that Item of the Day.
        // 70: NX use is restricted.\r\nPlease change your settings in the NX Security Settings menu\r\nin the Nexon Portal My Info section.
        // 74: This item is not currently for sale.
        // 81: You have too many Cash Items.\r\nPlease clear 1 Cash slot and try again.
        // 90: You have exceeded the purchase limit for this item.\r\nYou cannot buy anymore.
        // 88: This item is non-refundable.
        // 87: Items cannot be refunded if\r\n7 days have passed from purchase.
        // 89: Refund cannot be processed, as some of the items in this\r\npackage have been used.
        // 86: Refund is currently unavailable.
        // 91: You cannot name change.\r\na character under level 10.
        // default: Due to an unknown error,\r\nthe request for Cash Shop has failed.

        return mplew.getPacket();
    }

    public static byte[] enableCSUse() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(0x0A);
        mplew.write(1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] spawnMessageBox(int oid, int itemid, String sender, String msg, short x, short y) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.MESSAGEBOX_ENTER_FIELD.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(msg);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(x);
        mplew.writeShort(y);
        return mplew.getPacket();
    }

    public static byte[] destroyMessageBox(boolean animation, int oid) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.MESSAGEBOX_LEAVE_FIELD.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);
        return mplew.getPacket();
    }
}
