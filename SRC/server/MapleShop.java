package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import client.inventory.Item;
import client.SkillFactory;
import constants.GameConstants;
import client.inventory.MapleInventoryIdentifier;
import client.MapleClient;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import database.DatabaseConnection;
import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class MapleShop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet<Integer>();
    private int id;
    private int npcId;
    private List<MapleShopItem> items = new LinkedList<MapleShopItem>();
    private List<Pair<Integer, String>> ranks = new ArrayList<Pair<Integer, String>>();

    static {
        rechargeableItems.add(2070000);
        rechargeableItems.add(2070001);
        rechargeableItems.add(2070002);
        rechargeableItems.add(2070003);
        rechargeableItems.add(2070004);
        rechargeableItems.add(2070005);
        rechargeableItems.add(2070006);
        rechargeableItems.add(2070007);
        rechargeableItems.add(2070008);
        rechargeableItems.add(2070009);
        rechargeableItems.add(2070010);
        rechargeableItems.add(2070011);
        rechargeableItems.add(2070012);
        rechargeableItems.add(2070013);
//	rechargeableItems.add(2070014); // Doesn't Exist [Devil Rain]
//	rechargeableItems.add(2070015); // Beginner Star
        //rechargeableItems.add(2070016); //존재하지않음
//	rechargeableItems.add(2070017); // Doesn't Exist
        //rechargeableItems.add(2070018); // 없음 Balanced Fury
        //rechargeableItems.add(2070019); // 없음 Magic Throwing Star
        rechargeableItems.add(2070023); //플레임 표창
        rechargeableItems.add(2070024);    // 무한의 수리검

        rechargeableItems.add(2330000);
        rechargeableItems.add(2330001);
        rechargeableItems.add(2330002);
        rechargeableItems.add(2330003);
        rechargeableItems.add(2330004);
        rechargeableItems.add(2330005);
//	rechargeableItems.add(2330006); // Beginner Bullet
        rechargeableItems.add(2330008);

        rechargeableItems.add(2331000); // Capsules
        rechargeableItems.add(2332000); // Capsules
    }

    /** Creates a new instance of MapleShop */
    public MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
    }

    public void addItem(MapleShopItem item) {
        items.add(item);
    }

    public List<MapleShopItem> getItems() {
        return items;
    }

    public void sendShop(MapleClient c) {
        c.getPlayer().setShop(this);
        c.getSession().write(NPCPacket.getNPCShop(getNpcId(), this, c));
    }
    
    public void sendAdminShop(MapleClient c) {
        c.getPlayer().setShop(this);
        c.getSession().write(NPCPacket.getAdminShop(getNpcId(), this, c));
    }
    
    public void sendShop(MapleClient c, int customNpc) {
        c.getPlayer().setShop(this);
        c.getSession().write(NPCPacket.getNPCShop(customNpc, this, c));
    }

    public void buy(MapleClient c, int itemId, short quantity) { //레지존 엔피시 참고
        MapleShopItem item;
        if (itemId < 255) {
            item = findSNById(itemId);
        } else {
            item = findById(itemId);
            if (item != null) {
                if (item.getItemId() != itemId) {
                    System.out.println("Wrong slot number in shop " + id);
                    return;
                }
            }
        }
        if (quantity < 1) { 
            return; 
        }
        /*if (itemId / 10000 == 190 && !GameConstants.isMountItemAvailable(itemId, c.getPlayer().getJob())) {
            c.getPlayer().dropMessage(1, "You may not buy this item.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }*/
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int x = 0, index = -1;
        for (Item i : c.getPlayer().getRebuy()) {
            if (i.getItemId() == itemId) {
                index = x;
                break;
            }
            x++;
        }
        if (index >= 0) {
            final Item i = c.getPlayer().getRebuy().get(index);
            final int price = (int) Math.max(Math.ceil(ii.getPrice(itemId) * (GameConstants.isRechargable(itemId) ? 1 : i.getQuantity())), 0);
            if (price >= 0 && c.getPlayer().getMeso() >= price) {
                if (MapleInventoryManipulator.checkSpace(c, itemId, i.getQuantity(), i.getOwner())) {
                    c.getPlayer().gainMeso(-price, false);
                    MapleInventoryManipulator.addbyItem(c, i);
                    c.getPlayer().getRebuy().remove(index);
                     System.out.print("재구매인레후");
                    c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, x));
                } else {
                    c.getPlayer().dropMessage(1, "인벤토리가 부족합니다");
                    c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
                }
            } else {
                c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
            }
            return;
        }
    //    MapleShopItem item = findById(itemId);
        if (item != null && item.getPrice() > 0 && item.getReqItem() == 0) {
           /* if (item.getRank() >= 0) {
                boolean passed = true;
                int y = 0;
                for (Pair<Integer, String> i : getRanks()) {
                    if (c.getPlayer().haveItem(i.left, 1, true, true) && item.getRank() >= y) {
                        passed = true;
                        break;
                    }
                    y++;
                }
                if (!passed) {
                    c.getPlayer().dropMessage(1, "You need a higher rank.");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                } 필요없는 부분같은데 랭크 탭으로 쓰는데
            }*/ 
           
            final int price = GameConstants.isRechargable(itemId) ? item.getPrice() : (item.getPrice() * quantity);
            //BELOW IF BLOCK IS FOR MESO CURRNCY ITEMS BEING BOUGHT.
             boolean ontarde = false;
            if (itemId < 255) { // 골드리치 상점
                quantity = 1;
                itemId = item.getItemId();
                ontarde = true;
            }
            if (itemId == 2430695) {
                if (c.getPlayer().getDateKeyS("1회구매"+2430695) == null) {
                    c.getPlayer().setDateKeyS("1회구매"+2430695, "0");
                }
                if (c.getPlayer().getDateKeyS("1회구매시간"+2430695) == null) {
                    c.getPlayer().setDateKeyS("1회구매시간"+2430695, "0");
                }
                if (Long.parseLong(c.getPlayer().getDateKeyS("1회구매시간"+2430695)) < System.currentTimeMillis()) {
                    if (Integer.parseInt(c.getPlayer().getDateKeyS("1회구매"+2430695)) < 3) {
                        c.getPlayer().setDateKeyS("1회구매"+2430695, (Integer.parseInt(c.getPlayer().getDateKeyS("1회구매"+2430695)) + 1) + "");
                        c.getPlayer().setDateKeyS("1회구매시간"+2430695, String.valueOf(System.currentTimeMillis() + (59 * 60 * 1000)));
                    } else {
                        c.getPlayer().dropMessage(1, "구매 가능한 횟수가 없습니다.");
                        return;
                    }
                } else {
                    c.getPlayer().dropMessage(1, "아직 구매가능한 시간이 아닙니다.");
                    return;
                }
            }
            if (price >= 0 && c.getPlayer().getMeso() >= price) { // 메소 체크 
                short stockQuantity = 1;
                if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                    c.getPlayer().gainMeso(-price, true);
                    if (GameConstants.isPet(itemId)) {
                        MapleInventoryManipulator.addById(c, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1, "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        if (GameConstants.isRechargable(itemId)) {
                            quantity = ii.getSlotMax(item.getItemId());
                        }
                        if(itemId == 2061000 || itemId == 2060000){
                           stockQuantity = 2000;
                        }
                      
                        MapleInventoryManipulator.addById(c, itemId, (short)(quantity * stockQuantity), "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                    }
                } else {
                    c.getPlayer().dropMessage(1, "인벤토리가 부족합니다.");
                }
                if (ontarde) {
                  c.getSession().write(NPCPacket.result(4, 0));  // 
                } else {
                  c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
                }
            }
        } 
        //BELOW IF BLOCK IS FOR SINGLE QUANTITY TRANSACTIONS ONLY FOR ALTERNATE CURRENCY
        if (item != null && item.getReqItem() > 0 && quantity == 1 && c.getPlayer().haveItem(item.getReqItem(), item.getReqItemQ(), false, true)) {
            short stockQuantity = 1;
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ() * quantity, false, false);
                if (GameConstants.isPet(itemId)) {
                    MapleInventoryManipulator.addById(c, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1, "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    if (GameConstants.isRechargable(itemId)) {
                        quantity = ii.getSlotMax(item.getItemId());
                    }
                    if(itemId == 4310036){
                        //Hotfix to fix the Conqueror's Coin only giving 1 instead of 11.
                        stockQuantity = 11;
                    }
                    MapleInventoryManipulator.addById(c, itemId, ((short)(quantity * stockQuantity)), "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                c.getPlayer().dropMessage(1, "Your Inventory is full");
            }
            c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
            //BELOW ELSE IF BLOCK IS FOR ANY QUANTITY OVER ONE FOR ALTERNATE CURRENCY.
        } else if (item != null && item.getReqItem() > 0 && quantity > 1 && c.getPlayer().haveItem(item.getReqItem(), item.getReqItemQ(), false, true)) {
            short stockQuantity = 1;
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ() * quantity, false, false);
                if (GameConstants.isPet(itemId)) { //Pets do not get quantity.
                    c.getPlayer().dropMessage(1, "You can only purchase a single pet at a time!");
                } else {
                    if (GameConstants.isRechargable(itemId)) {
                        quantity = ii.getSlotMax(item.getItemId());
                    }
                       
                    if(itemId == 4310036){
                        //Hotfix to fix the Conqueror's Coin only giving 1 instead of 11.
                        stockQuantity = 11;
                    }
                    MapleInventoryManipulator.addById(c, itemId,((short)(quantity * stockQuantity)), "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                c.getPlayer().dropMessage(1, "Your Inventory is full");
            }
            c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
        }
    }

    public void sell(MapleClient c, int itemid, byte slot, short quantity) {
        // if (quantity == 0xFFFF || quantity == 0) {
        //     quantity = 1;
        // }
        if (quantity == 0xFFFF || quantity == 0) {
            AutobanManager.getInstance().autoban(c.getPlayer().getClient(), "핵감지로 인하여 오토벤 처리되었습니다.12");
            return;
            //  quantity = 1;
        }
        Item item;
        if (itemid < 255) {
            item = c.getPlayer().getInventory(GameConstants.getInventoryType(findSNById(itemid).getItemId())).getItem(slot); // 복잡하네
        } else {
            item = c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getItem(slot);
        }
        if (item == null) {
            return;
        }
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            quantity = item.getQuantity();
        }
        short iQuant = item.getQuantity();
        if (iQuant == 0xFFFF) {
            iQuant = 1;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.cantSell(item.getItemId()) || GameConstants.isPet(item.getItemId())) {
            return;
        }
        if (quantity <= iQuant && iQuant > 0) {
            Item itemm = item.copy();
            itemm.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), slot, quantity, false); // 수정

            double price;
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                price = ii.getWholePrice(item.getItemId()) / (double) ii.getSlotMax(item.getItemId());
            } else if (itemid < 255) {
                price = Math.abs(findSNById(itemid).getPrice());
            } else {
                price = ii.getPrice(item.getItemId());
            }
            final int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0);
            if (c.getPlayer().getMeso() + recvMesos > Integer.MAX_VALUE) {
                c.getPlayer().dropMessage(1, "소지 메소가 '" + Integer.MAX_VALUE + "메소'를 초과하여 판매를 할 수 없습니다.");
                c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0x0, this, c, -1));
                return;
            }
            if (price != -1.0 && recvMesos > 0) {
                c.getPlayer().gainMeso(recvMesos, true);
            }
            if (itemid < 255) { // 어드민샵 판매
                c.getSession().write(NPCPacket.result(4, 0));
            } else {
                //if (c.getPlayer().getRebuy().size() > 49) {
                //c.getPlayer().getRebuy().clear();
                //}
                //c.getPlayer().getRebuy().add(itemm);
                //if (shop.) {
                //c.sendPacket(CWvsContext.enableActions());
                c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0x0, this, c, -1));
                //c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0x4, this, c, -1));
                //}
            }
        }
    }

    public void recharge(final MapleClient c, final byte slot) {
        final Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (item == null || (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId()))) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(item.getItemId());
        final int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());

        if (skill != 0) {
            slotMax += c.getPlayer().getTotalSkillLevel(SkillFactory.getSkill(skill)) * 10;
        }
        if (item.getQuantity() < slotMax) {
            final int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                c.getSession().write(InventoryPacket.updateInventorySlot(MapleInventoryType.USE, (Item) item, false));
                c.getPlayer().gainMeso(-price, false, false);
                c.getSession().write(NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
            }
        }
    }

    protected MapleShopItem findById(int itemId) {
        for (MapleShopItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    protected MapleShopItem findSNById(int Snid) {
        for (MapleShopItem item : items) {
            if (item.getRank() == Snid) {
                return item;
            }
        }
        return null;
    }
    
    protected MapleShopItem findSNById2(int Snid) { // 넘버로 변환
        for (MapleShopItem item : items) {
            if (item.getRank() == Snid) {
                return item;
            }
        }
        return null;
    }
    
    public static MapleShop createFromDB(int id, boolean isShopId) {
        MapleShop ret = null;
        int shopId;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                shopId = rs.getInt("shopid");
                ret = new MapleShop(shopId, rs.getInt("npcid"));
                rs.close();
                ps.close();
            } else {
                rs.close();
                ps.close();
                con.close();
                return null;
            }
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            List<Integer> recharges = new ArrayList<Integer>(rechargeableItems);
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                if (GameConstants.isThrowingStar(rs.getInt("itemid")) || GameConstants.isBullet(rs.getInt("itemid"))) {
                    MapleShopItem starItem = new MapleShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getByte("rank"));
                    ret.addItem(starItem);
                    if (rechargeableItems.contains(starItem.getItemId())) {
                        recharges.remove(Integer.valueOf(starItem.getItemId()));
                    }
                } else {
                    ret.addItem(new MapleShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getByte("rank")));
                }
            }
            if (shopId != 2084001) { // 골드리치 방지
                for (Integer recharge : recharges) {
                    ret.addItem(new MapleShopItem((short) 1, recharge.intValue(), 0, 0, 0, (byte) 0));
                }
            }
           
            rs.close();
            ps.close();

          /*  ps = con.prepareStatement("SELECT * FROM shopranks WHERE shopid = ? ORDER BY rank ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                ret.ranks.add(new Pair<Integer, String>(rs.getInt("itemid"), rs.getString("name")));
            }
            rs.close();
            ps.close();*/
            con.close();
        } catch (SQLException e) {
            System.err.println("Could not load shop");
            e.printStackTrace();
        }
        return ret;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getId() {
        return id;
    }

    public List<Pair<Integer, String>> getRanks() {
        return ranks;
    }
}
