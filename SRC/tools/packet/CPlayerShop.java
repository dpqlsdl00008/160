package tools.packet;

import client.MapleCharacter;
import client.inventory.Item;
import client.MapleClient;

import handling.SendPacketOpcode;
import java.util.List;
import server.MerchItemPackage;
import server.shops.AbstractPlayerStore.BoughtItem;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import server.shops.MaplePlayerShop;
import server.shops.MaplePlayerShopItem;
import tools.Pair;
import tools.data.OutPacket;

public class CPlayerShop {

    public static final byte[] MerchantClose(final int error, final int type) {
        final OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x20);
        mplew.write(type);
        mplew.write(error);
        return mplew.getPacket();
    }

    public static byte[] requestShopPic(final int oid) {
        final OutPacket mplew = new OutPacket(17);

        mplew.writeShort(SendPacketOpcode.OnEntrustedShopCheckResult.getValue());
        mplew.write(17);
        mplew.writeInt(oid);
        mplew.writeShort(0); // idk
        mplew.writeLong(0); // idk also

        return mplew.getPacket();
    }

    public static final byte[] addCharBox(final MapleCharacter c, final int type) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MINIROOM_BALLOON.getValue());
        mplew.writeInt(c.getId());
        PacketHelper.addAnnounceBox(mplew, c);

        return mplew.getPacket();
    }

    public static final byte[] removeCharBox(final MapleCharacter c) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MINIROOM_BALLOON.getValue());
        mplew.writeInt(c.getId());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static final byte[] sendPlayerShopBox(final MapleCharacter c) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MINIROOM_BALLOON.getValue());
        mplew.writeInt(c.getId());
        PacketHelper.addAnnounceBox(mplew, c);

        return mplew.getPacket();
    }

    public static final byte[] getHiredMerch(final MapleCharacter chr, final HiredMerchant merch, final boolean firstTime) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10); // 12는 가위바위보 시발아!!
        mplew.write(5);
        mplew.write(7); // ?
        mplew.writeShort(merch.getVisitorSlot(chr));
        mplew.writeInt(merch.getItemId());
        mplew.writeMapleAsciiString("고용 상인");
        for (final Pair<Byte, MapleCharacter> storechr : merch.getVisitors()) {
            mplew.write(storechr.left);
            PacketHelper.addCharLook(mplew, storechr.right, false);
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(-1);
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            mplew.writeInt(merch.getTimeLeft());
            mplew.write(firstTime ? 1 : 0);
            mplew.write(merch.getBoughtItems().size());
            for (BoughtItem SoldItem : merch.getBoughtItems()) {
                mplew.writeInt(SoldItem.id);
                mplew.writeShort(SoldItem.quantity); // number of purchased
                mplew.writeInt(SoldItem.totalPrice); // total price
                mplew.writeMapleAsciiString(SoldItem.buyer); // name of the buyer
            }
            mplew.writeInt(merch.getMeso());

        }
        //mplew.writeInt(0);
        mplew.writeMapleAsciiString(merch.getDescription());
        mplew.write(16); // size
        mplew.writeInt(merch.getMeso()); // meso
        mplew.write(merch.getItems().size());
        for (final MaplePlayerShopItem item : merch.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            PacketHelper.addItemInfo(mplew, item.item);
        }
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static final byte[] getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        IMaplePlayerShop ips = chr.getPlayerShop();
        mplew.write(10);
        switch (ips.getShopType()) {
            case 2:
                mplew.write(4); // 이건 확실
                mplew.write(5); // ?
                break;
            case 3:
                mplew.write(2);
                mplew.write(2);
                break;
            case 4:
                mplew.write(1);
                mplew.write(2);
                break;
        }
        mplew.writeShort(ips.getVisitorSlot(chr));
        PacketHelper.addCharLook(mplew, ((MaplePlayerShop) ips).getMCOwner(), false);
        mplew.writeMapleAsciiString(ips.getOwnerName());
        mplew.writeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
            mplew.write(storechr.left);
            PacketHelper.addCharLook(mplew, storechr.right, false);
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(0xFF);
        //mplew.writeInt(0);
        mplew.writeMapleAsciiString(ips.getDescription());
        mplew.write(0x10);
        mplew.write(ips.getItems().size());
        for (final MaplePlayerShopItem item : ips.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            PacketHelper.addItemInfo(mplew, item.item);
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static final byte[] shopChat(final String message, final int slot) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(14);
        mplew.write(15);
        mplew.write(slot);
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static final byte[] shopErrorMessage(final int error, final int type) {
        final OutPacket mplew = new OutPacket();
        // 1, 1
        // 2, 1
        // 3, 1 방이 닫혔습니다
        // 4, 1 경기가 끝났습니다 10초 후 방이 닫힙니다.
        // 5, 1 강제 퇴장
        // 6, 1 제한시간이 지나 상점을 열지 못했습니다.
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(18);
        mplew.write(type);
        mplew.write(error);

        return mplew.getPacket();
    }

    public static final byte[] spawnHiredMerchant(final HiredMerchant hm) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.EMPLOYEE_ENTER_FIELD.getValue());
        mplew.writeInt(hm.getOwnerId());
        mplew.writeInt(hm.getItemId());
        mplew.writePos(hm.getTruePosition());
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(hm.getOwnerName());
        PacketHelper.addInteraction(mplew, hm);
        return mplew.getPacket();
    }

    public static final byte[] destroyHiredMerchant(final int id) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.EMPLOYEE_LEAVE_FIELD.getValue());
        mplew.writeInt(id);

        return mplew.getPacket();
    }

    public static final byte[] shopItemUpdate(final IMaplePlayerShop shop) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(46);
        if (shop.getShopType() == 1) {
            mplew.writeInt(0);
        }
        mplew.write(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            PacketHelper.addItemInfo(mplew, item.item);
        }
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static final byte[] shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(9);
        mplew.write(slot);
        PacketHelper.addCharLook(mplew, chr, false);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeShort(chr.getJob());

        return mplew.getPacket();
    }

    public static final byte[] shopVisitorLeave(final byte slot) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(18);
        if (slot > 0) {
            mplew.write(slot);
        }
        return mplew.getPacket();
    }

    public static final byte[] Merchant_Buy_Error(final byte message) {
        final OutPacket mplew = new OutPacket();

        // 2 = You have not enough meso
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(44);
        mplew.write(message);

        return mplew.getPacket();
    }

    public static final byte[] updateHiredMerchant(final HiredMerchant shop) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.EMPLOYEE_MINIROOM_BALLON.getValue());
        mplew.writeInt(shop.getOwnerId());
        PacketHelper.addInteraction(mplew, shop);

        return mplew.getPacket();
    }

    public static final byte[] merchItem_Message(final int op) {
        final OutPacket mplew = new OutPacket();

        //32: You have retrieved your items and mesos.
        //33: Unable to retrieve mesos and items due to\r\ntoo much money stored\r\nat the Store Bank.
        //34: Unable to retrieve mesos and items due to\r\none of the items\r\nthat can only be possessed one at a time.
        //35: Due to the lack of service fee, you were unable to \r\nretrieve mesos or items. 
        //36: Unable to retrieve mesos and items\r\ndue to full inventory.
        mplew.writeShort(SendPacketOpcode.MERCH_ITEM_MSG.getValue());
        mplew.write(op);

        return mplew.getPacket();
    }

    public static final byte[] merchItemStore(final byte op, final int days, final int fees) {
        final OutPacket mplew = new OutPacket();

        // 40: This is currently unavailable.\r\nPlease try again later
        mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        mplew.write(op);
        switch (op) {
            case 39:
                mplew.writeInt(999999999); // ? 
                mplew.writeInt(999999999); // mapid
                mplew.write(0); // >= -2 channel
                // if cc -1 or map = 999,999,999 : I don't think you have any items or money to retrieve here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.
                //Your Personal Store is open #bin Channel %s, Free Market %d#k.\r\nIf you need me, then please close your personal store first before seeing me.
                break;
            case 38:
                mplew.writeInt(days); // % tax or days, 1 day = 1%
                mplew.writeInt(fees); // feees
                break;
        }

        return mplew.getPacket();
    }

    public static final byte[] merchItemStore_ItemData(final MerchItemPackage pack) {
        final OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        mplew.write(38);
        mplew.writeInt(9030000); // Fredrick
        mplew.write(16); // max items?
        mplew.writeLong(126); // ?
        mplew.writeInt(pack.getMesos());
        mplew.write(0);
        mplew.write(pack.getItems().size());
        for (final Item item : pack.getItems()) {
            PacketHelper.addItemInfo(mplew, item);
        }
        mplew.writeZeroBytes(3);

        return mplew.getPacket();
    }

    public static byte[] getMiniGame(MapleClient c, MapleMiniGame minigame) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10);
        mplew.write(minigame.getGameType());
        mplew.write(minigame.getMaxSize());
        mplew.writeShort(minigame.getVisitorSlot(c.getPlayer()));
        PacketHelper.addCharLook(mplew, minigame.getMCOwner(), false);
        mplew.writeMapleAsciiString(minigame.getOwnerName());
        mplew.writeShort(minigame.getMCOwner().getJob());
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            PacketHelper.addCharLook(mplew, visitorz.getRight(), false);
            mplew.writeMapleAsciiString(visitorz.getRight().getName());
            mplew.writeShort(visitorz.getRight().getJob());
        }
        mplew.write(-1);
        mplew.write(0);
        addGameInfo(mplew, minigame.getMCOwner(), minigame);
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            addGameInfo(mplew, visitorz.getRight(), minigame);
        }
        mplew.write(-1);
        mplew.writeMapleAsciiString(minigame.getDescription());
        mplew.writeShort(minigame.getPieceType());
        return mplew.getPacket();
    }

    public static byte[] getMiniGameReady(boolean ready) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(ready ? 62 : 63);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameExitAfter(boolean ready) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(ready ? 60 : 61);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameStart(int loser) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(65);
        mplew.write(loser == 1 ? 0 : 1);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameSkip(int slot) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(67);
        //owner = 1 visitor = 0?
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameRequestTie() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(54);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameDenyTie() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(55);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameRequestRedo() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(58);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameDenyRedo() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(59);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameFull() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.writeShort(10);
        mplew.write(2);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameMoveOmok(int move1, int move2, int move3) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(68);
        mplew.writeInt(move1);
        mplew.writeInt(move2);
        mplew.write(move3);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameMoveOmok1(int move1, int move2, int move3) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(69);
        mplew.writeInt(move1);
        mplew.writeInt(move2);
        mplew.write(move3);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(9);
        mplew.write(slot);
        PacketHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());
        addGameInfo(mplew, c, game);
        return mplew.getPacket();
    }

    public static void addGameInfo(OutPacket mplew, MapleCharacter chr, MapleMiniGame game) {
        mplew.writeInt(game.getGameType()); // start of visitor; unknown
        mplew.writeInt(game.getWins(chr));
        mplew.writeInt(game.getTies(chr));
        mplew.writeInt(game.getLosses(chr));
        mplew.writeInt(game.getScore(chr)); // points
    }

    public static byte[] getMiniGameClose(byte number) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(18);
        mplew.write(1);
        mplew.write(number);
        return mplew.getPacket();
    }

    public static byte[] getMatchCardStart(MapleMiniGame game, int loser) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(65);
        mplew.write(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 1 ? 20 : (game.getPieceType() == 2 ? 30 : 12);
        mplew.write(times);
        for (int i = 1; i <= times; i++) {
            mplew.writeInt(game.getCardId(i));
        }
        return mplew.getPacket();
    }

    public static byte[] getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(72); // 
        mplew.write(turn);
        mplew.write(slot);
        if (turn == 0) {
            mplew.write(firstslot);
            mplew.write(type);
        }
        return mplew.getPacket();
    }

    public static byte[] getMiniGameResult(MapleMiniGame game, int type, int x) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x42); // 추측
        mplew.write(type); //lose = 0, tie = 1, win = 2
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                mplew.write(x == 1 ? 0 : 1); //who did it?
            } else {
                mplew.write(x);
            }
        }
        addGameInfo(mplew, game.getMCOwner(), game);
        for (Pair<Byte, MapleCharacter> visitorz : game.getVisitors()) {
            addGameInfo(mplew, visitorz.right, game);
        }

        return mplew.getPacket();
    }

    public static final byte[] MerchantView(short type, List<String> list) {
        final OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        oPacket.EncodeByte(type);
        oPacket.EncodeShort(list.size());
        for (String character : list) {
            oPacket.EncodeString(character);
        }
        switch (type) {
            case 0x24: {
                oPacket.EncodeInt(1);
                break;
            }
            case 0x25: {
                break;
            }
        }
        return oPacket.getPacket();
    }
    
    public static final byte[] MerchantAlreadyMssage(byte type, int a, int b) {
        final OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        oPacket.EncodeByte(type);
        switch (type) {
            case 0x27: {
                oPacket.EncodeInt(a);
                oPacket.EncodeInt(b);
                break;
            }
            case 0x28: {
                oPacket.EncodeInt(9030000);
                oPacket.EncodeInt(b);
                oPacket.EncodeByte(0);
                break;
            }
            case 0x25:
            case 0x26:
            case 0x29: {
                break;
            }
        }
        return oPacket.getPacket();
    }

    public static byte[] MerchantAlreadyMssage(byte type, int channel, int room, int check) {
        final OutPacket oPacket = new OutPacket(8);
        oPacket.EncodeShort(SendPacketOpcode.OnEntrustedShopCheckResult.getValue());
        oPacket.EncodeByte(type);
        switch (type) {
            case 0x07: {
                break;
            }
            // %s채널 자유 시장 %d에 상점이 열려있습니다. 개설된 상점을 닫고 이용하세요.
            case 0x08: {
                oPacket.EncodeInt(room);
                oPacket.EncodeByte(channel - 1);
                break;
            }
            case 0x0D: {
                oPacket.EncodeInt(0);
                break;
            }
            // 변경 성공.
            case 0x0E: {
                oPacket.EncodeByte(0);
                break;
            }
            // 자유 시장 입구의 프레드릭에게서 물건을 찾은 다음 이용하세요.
            case 0x09:
            // 다른 캐릭터가 아이템을 사용 중입니다. 다른 캐릭터로 접속해서 상점을 닫거나 스토어 뱅크를 비워 주세요.
            case 0x0A:
            // 지금은 상점을 개설 할 수 없습니다.
            case 0x0B:
            // 프레드릭에게서 물건을 찾아가시기 바랍니다.
            case 0x0F: {
                break;
            }
            // 고용 상점이 열려 있지 않아 사용 할 수 없습니다.
            // %s채널에 상점이 열려있습니다. 해당 채널로 이동하시겠습니까?
            case 0x10: {
                oPacket.EncodeInt(0);
                oPacket.EncodeByte(channel);
                break;
            }
            // 관리 시에는 물품을 판매 할 수 없습니다. 상점 관리를 시작하시겠습니까?
            case 0x11: {
                oPacket.EncodeInt(0);
                oPacket.EncodeShort(0);
                break;
            }
        }
        return oPacket.getPacket();
    }
}
