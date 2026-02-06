package handling.cashshop.handler;

import java.util.Map;
import java.util.HashMap;
import constants.GameConstants;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleQuestStatus;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CCashShop;
import tools.Triple;
import tools.data.LittleEndianAccessor;

public class CashShopOperation {

    public static void LeaveCS(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.getSession().write(CField.getChannelChange(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
            //c.getSession().close();
        }
    }

    public static void EnterCS(final int playerid, final MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);

        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        c.getSession().write(CCashShop.warpCS(c));
        CSUpdate(c);
    }

    public static void CSUpdate(final MapleClient c) {
        c.getSession().write(CCashShop.getCSGifts(c));
        doCSPackets(c);
        c.getSession().write(CCashShop.sendWishList(c.getPlayer(), false));
    }

    public static void CouponCode(final String code, final MapleClient c) {
        //System.out.println("Received coupon code: " + code);
        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;

        info = MapleCharacterUtil.getNXCodeInfo(code);

        if (info != null && info.left) {
            int type = info.mid, item = info.right;

            MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);

            /*
             * Explanation of type!
             * Basically, this makes coupon codes do
             * different things!
             *
             * Type 1: A-Cash,
             * Type 2: Maple Points
             * Type 3: Item.. use SN
             * Type 4: Mesos
             */
            Map<Integer, Item> itemz = new HashMap<Integer, Item>();
            int maplePoints = 0, mesos = 0;
            switch (type) {
                case 1:
                case 2:
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                case 3:
                    CashItemInfo itez = CashItemFactory.getInstance().getItem(item);

                    if (itez == null) {
                        //System.out.println("Item: " + item + " was null.");
                        c.getSession().write(CCashShop.sendCSFail(1));
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "", "Cash shop: coupon code" + " on " + FileoutputUtil.CurrentReadable_Date());
                    if (slot <= -1) {
                        c.getSession().write(CCashShop.sendCSFail(0));
                        return;
                    } else {
                        itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    }
                    break;
                case 4:
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
            }
            c.getSession().write(CCashShop.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            c.getSession().write(CCashShop.sendCSFail(info == null ? 14 : info.left == false ? 17 : 65));
            //System.out.println("Error");
        }
    }

    public static final void BuyCashItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int action = slea.readByte();
        if (action == 0) {
            slea.skip(2);
            CouponCode(slea.readMapleAsciiString(), c);
        } else if (action == 3) { // 구매
            int toCharge = slea.readByte() + 1;
            int itemSN = slea.readInt();
            switch (itemSN) {
                case 20000556: // 다크 기사 단장 모자 상자
                case 10002622: // 다크 기사 단장 모자 상자
                    itemSN = Randomizer.rand(20000551, 20000555);
                    break;
                case 10002695: // 9주년 스페셜 박스
                case 20000565: // 9주년 스페셜 박스
                    itemSN = Randomizer.rand(20000551, 20000555);
                    break;
                case 10002696: // 앵콜! 8주년 기념 모자 상자
                case 20000566: // 앵콜! 8주년 기념 모자 상자
                    itemSN = Randomizer.rand(20000462, 20000469);
                    break;
                case 10002770: // 환상 무기 상자
                case 20800294: // 환상 무기 상자
                    itemSN = Randomizer.rand(20800290, 20800293);
                    break;
                case 10002369: // 블레이즈 빔 무기 상자
                case 20800282: // 블레이즈 빔 무기 상자
                    itemSN = Randomizer.rand(20800258, 20800268);
                    break;
                case 10002630: // 파라다이스 날개 상자
                case 21100156: // 파라다이스 날개 상자
                    itemSN = Randomizer.rand(21100152, 21100155);
                    break;
                case 10002552: // 프쉬케의 날개 상자
                case 21100148: // 프쉬케의 날개 상자
                    itemSN = Randomizer.rand(10002553, 10002555);
                    break;
                case 10002236: // 기사 단장 무기 상자
                case 20800275: // 기사 단장 무기 상자
                    itemSN = Randomizer.rand(10001943, 10001947);
                    break;
            }
            CashItemInfo item = CashItemFactory.getInstance().getItem(itemSN);
            if (item != null && chr.getCSPoints(toCharge) >= item.getPrice()) {
                if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                    return;
                }
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (!ii.isCash(item.getId())) {
                    doCSPackets(c);
                    return;
                }
                if (GameConstants.isEquip(item.getId())) {
                    final Equip v1 = (Equip) ii.getEquipById(item.getId());
                    if (v1.getStr() > 0 || v1.getDex() > 0 || v1.getInt() > 0 || v1.getLuk() > 0 || v1.getWatk() > 0 || v1.getMatk() > 0) {
                        doCSPackets(c);
                        return;
                    }
                }
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.getSession().write(CCashShop.sendCSFail(0xA7));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.getSession().write(CCashShop.sendCSFail(0xB2));
                    doCSPackets(c);
                    return;
                }
                chr.modifyCSPoints(toCharge, -item.getPrice(), false);
                Item itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    if (toCharge == 1 && itemz.getType() == 1) {
                        itemz.setFlag((short) (ItemFlag.KarmasCissors.getValue()));
                    } else if (toCharge == 1 && itemz.getType() != 1) {
                        itemz.setFlag((short) (ItemFlag.PreventSlipping.getValue()));
                    }
                    chr.getCashInventory().addToInventory(itemz);
                    c.getSession().write(CCashShop.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                } else {
                    c.getSession().write(CCashShop.sendCSFail(0));
                }
            } else {
                c.getSession().write(CCashShop.sendCSFail(0));
            }
        } else if (action == 4 || action == 34) { // 선물
            slea.skip(5);
            CashItemInfo csItem = CashItemFactory.getInstance().getItem(slea.readInt());
            String toName = slea.readMapleAsciiString();
            String toMessage = slea.readMapleAsciiString();
            Triple<Integer, Integer, Integer> toUser = MapleCharacterUtil.getInfoByName(toName, c.getPlayer().getWorld());
            if (toUser == null || csItem.genderEquals(toUser.getRight())) {
                c.getSession().write(CCashShop.sendCSFail((byte) 0xA9));
                return;
            }
            if (GameConstants.isNotSellCashItem(csItem.getItemId()) == true) {
                return;
            }
            c.getPlayer().getCashInventory().gift(toUser.getLeft(), c.getPlayer().getName(), toMessage, csItem.getSN(), MapleInventoryIdentifier.getInstance());
            c.getPlayer().modifyCSPoints(2, -csItem.getPrice(), false);
            c.getSession().write(CCashShop.sendGift(csItem.getPrice(), csItem.getId(), csItem.getCount(), toName, false));
            MapleCharacterUtil.sendNote(toName, ServerConstants.server_Name_Source, c.getPlayer().getName() + "님으로 부터의 선물이 도착 하였습니다. 캐시 샵을 확인하여 주세요.", 0);
        } else if (action == 5) {
            chr.clearWishlist();
            if (slea.available() < 40) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            int[] wishlist = new int[10];
            for (int i = 0; i < 10; i++) {
                wishlist[i] = slea.readInt();
            }
            chr.setWishlist(wishlist);
            c.getSession().write(CCashShop.sendWishList(chr, true));
        } else if (action == 6) {
            final int toCharge = slea.readByte() + 1;
            final boolean coupon = slea.readByte() > 0;
            if (coupon) {
                int check = slea.readShort();
                byte typeNumber = 0;
                if (check == -560) {
                    typeNumber = 1;
                }
                if (check == -559) {
                    typeNumber = 2;
                }
                if (check == -473) {
                    typeNumber = 3;
                }
                if (check == -557) {
                    typeNumber = 4;
                }
                final MapleInventoryType type = MapleInventoryType.getByType(typeNumber);
                String value = (type == MapleInventoryType.EQUIP ? "장비" : type == MapleInventoryType.USE ? "소비" : type == MapleInventoryType.SETUP ? "설치" : type == MapleInventoryType.ETC ? "기타" : "캐쉬");
                if (chr.getCSPoints(toCharge) >= 7600 && chr.getInventory(type).getSlotLimit() <= 88) {
                    chr.modifyCSPoints(toCharge, -7600, false);
                    chr.getInventory(type).addSlot((byte) 8);
                    c.getSession().write(CCashShop.increasedInvSlots(type.getType(), chr.getInventory(type).getSlotLimit()));
                    chr.dropMessage(1, value + " 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 " + value + " 인벤토리 공간 : " + chr.getInventory(type).getSlotLimit() + "칸");
                } else {
                    c.getSession().write(CCashShop.sendCSFail(141));
                }
            } else {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                String value = (type == MapleInventoryType.EQUIP ? "장비" : type == MapleInventoryType.USE ? "소비" : type == MapleInventoryType.SETUP ? "설치" : type == MapleInventoryType.ETC ? "기타" : "캐쉬");
                if (chr.getCSPoints(toCharge) >= 3800 && chr.getInventory(type).getSlotLimit() <= 92) {
                    chr.modifyCSPoints(toCharge, -3800, false);
                    chr.getInventory(type).addSlot((byte) 4);
                    c.getSession().write(CCashShop.increasedInvSlots(type.getType(), chr.getInventory(type).getSlotLimit()));
                    chr.dropMessage(1, value + " 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 " + value + " 인벤토리 공간 : " + chr.getInventory(type).getSlotLimit() + "칸");
                } else {
                    c.getSession().write(CCashShop.sendCSFail(141));
                }
            }
        } else if (action == 7) {
            final int toCharge = slea.readByte() + 1;
            final int coupon = slea.readByte() > 0 ? 2 : 1;
            if (chr.getCSPoints(toCharge) >= 3800 * coupon && chr.getStorage().getSlots() <= (48 - (4 * coupon))) {
                chr.modifyCSPoints(toCharge, -3800 * coupon, false);
                chr.getStorage().increaseSlots((byte) (4 * coupon));
                chr.getStorage().saveToDB();
                c.getSession().write(CCashShop.increasedStorageSlots(chr.getStorage().getSlots(), false));
                chr.dropMessage(1, "창고 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 창고 인벤토리 공간 : " + chr.getStorage().getSlots() + "칸");
            } else {
                c.getSession().write(CCashShop.sendCSFail(141));
            }
        } else if (action == 8) {
            final int toCharge = slea.readByte() + 1;
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            int slots = c.getCharacterSlots();
            if (item == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || slots > 15 || item.getId() != 5430000) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                return;
            }
            if (c.gainCharacterSlot()) {
                c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                c.getSession().write(CCashShop.increasedStorageSlots(slots + 1, true));
            } else {
                c.getSession().write(CCashShop.sendCSFail(0));
            }
        } else if (action == 10) {
            final int toCharge = slea.readByte() + 1;
            final int sn = slea.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(sn);
            int slots = c.getCharacterSlots();
            if (item == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || item.getId() / 10000 != 555) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                return;
            }
            MapleQuestStatus marr = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
                chr.dropMessage(1, "이미 추가 펜던트 슬롯이 확장되어 있습니다.\r\n\r\n잔여 시간 : " + StringUtil.getReadableMillis(System.currentTimeMillis(), Long.parseLong(marr.getCustomData())) + "");
            } else {
                c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) 30 * 24 * 60 * 60000)));
                final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
                c.getPlayer().updateQuest(stats, true);
                c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                chr.dropMessage(1, "추가 펜던트 슬롯을 성공적으로 확장 하였습니다.\r\n\r\n잔여 시간 : 29일 23시간 59분 59초");
            }
        } else if (action == 14) {
            Item item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                    return;
                }
                Item item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(pos);
                        c.getPlayer().addPet(item_.getPet());
                    }
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                    c.getSession().write(CCashShop.confirmFromCSInventory(item_, pos));
                } else {
                    c.getSession().write(CCashShop.sendCSFail(0xB1));
                }
            } else {
                c.getSession().write(CCashShop.sendCSFail(0xB1));
            }
        } else if (action == 15) {
            int uniqueid = (int) slea.readLong();
            MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
            Item item = c.getPlayer().getInventory(type).findByUniqueId(uniqueid);
            if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                    return;
                }
                Item item_ = item.copy();
                MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), false, false, false);
                if (item_.getPet() != null) {
                    c.getPlayer().removePetCS(item_.getPet());
                }
                item_.setPosition((byte) 0);
                c.getPlayer().getCashInventory().addToInventory(item_);
                c.getSession().write(CCashShop.confirmToCSInventory(item, c.getAccID(), -1));
            } else {
                c.getSession().write(CCashShop.sendCSFail(0));
            }
        } else if (action == 32 || action == 33) {
            slea.skip(6);
            final int toCharge = 1;
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            final String partnerName = slea.readMapleAsciiString();
            final String msg = slea.readMapleAsciiString();
            if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                return;
            }
            if (!item.genderEquals(c.getPlayer().getGender())) {
                c.getSession().write(CCashShop.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                c.getSession().write(CCashShop.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId()) {
                c.getSession().write(CCashShop.sendCSFail(0xB4)); //9E v75
                doCSPackets(c);
                return;
            } else if (info.getMid() == c.getAccID()) {
                c.getSession().write(CCashShop.sendCSFail(0xA3)); //9D v75
                doCSPackets(c);
                return;
            } else {
                if (info.getRight() == c.getPlayer().getGender() && action == 30) {
                    c.getSession().write(CCashShop.sendCSFail(0xA1)); //9B v75
                    doCSPackets(c);
                    return;
                }
                int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info.getLeft().intValue(), item.getSN());
                if (err != 1) {
                    c.getSession().write(CCashShop.sendCSFail(0)); //9E v75
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
            }
        } else if (action == 33) {
            final int toCharge = slea.readByte() + 1;
            int itemSN = slea.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(itemSN);
            List<Integer> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice()) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                return;
            }
            if (!item.genderEquals(c.getPlayer().getGender())) {
                c.getSession().write(CCashShop.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                c.getSession().write(CCashShop.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            Map<Integer, Item> ccz = new HashMap<>();

            MapleInventoryType type;
            // 환상 무기 상자
            if (itemSN == 20800297 || itemSN == 10002783) {
                ccc = Collections.unmodifiableList(Arrays.asList(10002782, Randomizer.rand(20800290, 20800293)));
            }
            switch (itemSN) {
                case 10002748: // 인벤토리 스페셜 세트 1
                case 70000464: // 인벤토리 스페셜 세트 1
                    type = MapleInventoryType.getByType((byte) 1);
                    chr.getInventory(type).addSlot((byte) (itemSN == 10002748 ? 8 : 8));
                    c.sendPacket(CCashShop.increasedInvSlots(type.getType(), chr.getInventory(type).getSlotLimit()));
                    chr.dropMessage(1, "장비 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 장비 인벤토리 공간 : " + chr.getInventory(type).getSlotLimit() + "칸");
                    break;
                case 10002749: // 인벤토리 스페셜 세트 2
                case 70000465: // 인벤토리 스페셜 세트 2
                    type = MapleInventoryType.getByType((byte) 2);
                    chr.getInventory(type).addSlot((byte) (itemSN == 10002749 ? 8 : 8));
                    c.sendPacket(CCashShop.increasedInvSlots(type.getType(), chr.getInventory(type).getSlotLimit()));
                    chr.dropMessage(1, "소비 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 소비 인벤토리 공간 : " + chr.getInventory(type).getSlotLimit() + "칸");
                    break;
                case 10002750: // 인벤토리 스페셜 세트 3
                case 70000466: // 인벤토리 스페셜 세트 3
                    type = MapleInventoryType.getByType((byte) 3);
                    chr.getInventory(type).addSlot((byte) (itemSN == 10002750 ? 8 : 8));
                    c.sendPacket(CCashShop.increasedInvSlots(type.getType(), chr.getInventory(type).getSlotLimit()));
                    chr.dropMessage(1, "설치 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 설치 인벤토리 공간 : " + chr.getInventory(type).getSlotLimit() + "칸");
                    break;
                case 10002751: // 인벤토리 스페셜 세트 4
                case 70000467: // 인벤토리 스페셜 세트 4
                    type = MapleInventoryType.getByType((byte) 4);
                    chr.getInventory(type).addSlot((byte) (itemSN == 10002751 ? 8 : 8));
                    c.sendPacket(CCashShop.increasedInvSlots(type.getType(), chr.getInventory(type).getSlotLimit()));
                    chr.dropMessage(1, "기타 인벤토리 공간을 성공적으로 확장하였습니다.\r\n\r\n현재 기타 인벤토리 공간 : " + chr.getInventory(type).getSlotLimit() + "칸");
                    break;
            }
            for (int i : ccc) {
                CashItemInfo cii = CashItemFactory.getInstance().getSimpleItem(i);
                if (cii == null) {
                    continue;
                }
                Item itemz = c.getPlayer().getCashInventory().toItem(cii);
                if (itemz == null || itemz.getUniqueId() <= 0) {
                    continue;
                }
                if (GameConstants.isNotSellCashItem(itemz.getItemId()) == true) {
                    return;
                }
                if (itemz.getItemId() == 5533017
                        || // 환상 무기 상자 패키지
                        itemz.getItemId() == 5530241
                        || // 인벤토리 스페셜 세트 1
                        itemz.getItemId() == 5530242
                        || // 인벤토리 스페셜 세트 2
                        itemz.getItemId() == 5530243
                        || // 인벤토리 스페셜 세트 3
                        itemz.getItemId() == 5530244) { // 인벤토리 스페셜 세트 4
                    continue;
                }
                ccz.put(i, itemz);
            }
            for (Item itemsa : ccz.values()) {
                c.getPlayer().getCashInventory().addToInventory(itemsa);
            }
            chr.modifyCSPoints(toCharge, -item.getPrice(), false);
            c.getSession().write(CCashShop.showBoughtCSPackage(ccz, c.getAccID()));
        } else if (action == 35) {
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                return;
            }
            if (c.getPlayer().getMeso() < item.getPrice()) {
                c.getSession().write(CCashShop.sendCSFail(0xB8));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                c.getSession().write(CCashShop.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null, "Cash shop: quest item" + " on " + FileoutputUtil.CurrentReadable_Date());
            if (pos < 0) {
                c.getSession().write(CCashShop.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            chr.gainMeso(-item.getPrice(), false);
            c.getSession().write(CCashShop.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
        } else if (action == 45) {
            c.getSession().write(CCashShop.updatePurchaseRecord());
        } else if (action == 91) {
            final int uniqueid = (int) slea.readLong();
        } else if (action == 63) {
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item == null) {
                c.getSession().write(CCashShop.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (GameConstants.isNotSellCashItem(item.getItemId()) == true) {
                return;
            }
            if (c.getPlayer().getMeso() < item.getPrice()) {
                c.getSession().write(CCashShop.sendCSFail(0xB8));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                c.getSession().write(CCashShop.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            Item itemz = chr.getCashInventory().toItem(item);
            chr.gainMeso(-item.getPrice(), false);
            chr.getCashInventory().addToInventory(itemz);
            c.getSession().write(CCashShop.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
        } else {
            c.getSession().write(CCashShop.sendCSFail(0));
        }
        doCSPackets(c);
    }

    private static final MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 50200094:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static final void doCSPackets(MapleClient c) {
        c.getSession().write(CCashShop.enableCSUse());
        c.getSession().write(CCashShop.getCSInventory(c));
        c.getSession().write(CCashShop.showNXMapleTokens(c.getPlayer()));
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
