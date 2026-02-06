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

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.MapleClient;
import client.MapleCharacter;
import constants.GameConstants;
import client.inventory.ItemLoader;
import database.DatabaseConnection;
import handling.world.World;
import java.util.Map;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MerchItemPackage;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CPlayerShop;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

public class HiredMerchantHandler {

    public static final boolean UseHiredMerchant(final MapleClient c, final boolean packet) {
        if (c.getPlayer().getMap() != null && c.getPlayer().getMap().allowPersonalShop()) {
            final byte state = checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());
            switch (state) {
                case 1:
                    Pair<Integer, Integer> findMerch = World.findMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                    if (!World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId())) {
                        c.getPlayer().getClient().sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x09, 0, 0, 0));
                    } else {
                        c.getPlayer().getClient().sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x10, (findMerch.getLeft() - 1), 0, 0));
                    }
                    c.getPlayer().setConversation(0);
                    break;
                case 0:
                    if (!World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId())) {
                        if (c.getChannelServer().isShutdown()) {
                            c.getPlayer().getClient().sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x29, 0, 0));
                            c.getPlayer().setConversation(0);
                            return false;
                        }
                        if (packet) {
                            c.getPlayer().getClient().sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x07, 0, 0, 0));
                            c.getPlayer().setConversation(0);
                        }
                        return true;
                    } else {
                        c.getPlayer().getClient().sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x10, 0, 0, 0));
                        c.getPlayer().setConversation(0);
                    }
                    break;
                default:
                    c.getPlayer().getClient().sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x29, 0, 0));
                    c.getPlayer().setConversation(0);
                    break;
            }
        } else {
            //c.getSession().close();
        }
        return false;
    }

    private static final byte checkExistance(final int accid, final int cid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?");
            ps.setInt(1, accid);
            ps.setInt(2, cid);
            rs = ps.executeQuery();
            if (rs.next()) {

                return 1;
            }
            return 0;
        } catch (SQLException se) {
            return -1;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static final void displayMerch(MapleClient c) {
        Pair<Integer, Integer> merch = World.findMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch != null) {
            c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x28, merch.getLeft(), merch.getRight()));
            c.getPlayer().setConversation(0);
        } else if (c.getChannelServer().isShutdown()) {
            c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x29, 0, 0));
            c.getPlayer().setConversation(0);
        } else if (c.getPlayer().getConversation() == 3) {
            final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());
            if (pack == null) {
                c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x28, 0, 999999999));
                c.getPlayer().setConversation(0);
            } else {
                c.getSession().write(CPlayerShop.merchItemStore_ItemData(pack));
            }
        }
    }

    public static final void MerchantItemStore(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final byte operation = slea.readByte();

        final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());
        if (pack == null || c.getChannelServer().isShutdown()) {
            c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x29, 0, 0));
            c.getPlayer().setConversation(0);
            return;
        }
        
        final int days = StringUtil.getDaysAmount(pack.getSavedTime(), System.currentTimeMillis());
        final double percentage = days / 100.0;
        final int fee = (int) Math.ceil(percentage * pack.getMesos());

        switch (operation) {
            case 0x1B: {
                boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                if (merch) {
                    Pair<Integer, Integer> findMerch = World.findMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                    c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x08, findMerch.getLeft(), findMerch.getRight(), 0));
                    c.getPlayer().setConversation(0);
                    return;
                }
                if (days > 0 && percentage > 0 && pack.getMesos() > 0 && fee > 0) {
                    c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x27, days, fee));
                    c.getPlayer().setConversation(0);
                    return;
                }
                if (c.getPlayer().getMeso() + pack.getMesos() > Integer.MAX_VALUE) {
                    c.sendPacket(CPlayerShop.merchItem_Message((byte) 0x21));
                    c.getPlayer().setConversation(0);
                    return;
                }
                if (!check(c.getPlayer(), pack)) {
                    c.sendPacket(CPlayerShop.merchItem_Message((byte) 0x24));
                    c.getPlayer().setConversation(0);
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    if (fee > 0) {
                        c.getPlayer().gainMeso(-fee, true);
                    }
                    
                    /* 메소 동전 */
                    c.getPlayer().gainMeso(pack.getMesos(), true);
                    //c.getPlayer().gainItem(4310114, pack.getMesos());
                    //c.getPlayer().getClient().sendPacket(CWvsContext.InfoPacket.getShowItemGain(4310114, pack.getMesos(), true));
                    
                    for (Item item : pack.getItems()) {
                        MapleInventoryManipulator.addFromDrop(c, item, true);
                    }
                    c.sendPacket(CPlayerShop.merchItem_Message((byte) 0x20));
                    c.getPlayer().setConversation(0);
                } else {
                    c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x29, 0, 0));
                    c.getPlayer().setConversation(0);
                }
                break;
            }
            case 0x1C: {
                if (c.getPlayer().getMeso() < fee) {
                    c.sendPacket(CPlayerShop.merchItem_Message((byte) 0x23));
                    c.getPlayer().setConversation(0);
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    if (fee > 0) {
                        c.getPlayer().gainMeso(-fee, true);
                    }
                    
                    /* 메소 동전 */
                    c.getPlayer().gainMeso(pack.getMesos(), true);
                    //c.getPlayer().gainItem(4310114, pack.getMesos());
                    //c.getPlayer().getClient().sendPacket(CWvsContext.InfoPacket.getShowItemGain(4310114, pack.getMesos(), true));
                    
                    for (Item item : pack.getItems()) {
                        MapleInventoryManipulator.addFromDrop(c, item, true);
                    }
                    c.sendPacket(CPlayerShop.merchItem_Message((byte) 0x20));
                    c.getPlayer().setConversation(0);
                } else {
                    c.sendPacket(CPlayerShop.MerchantAlreadyMssage((byte) 0x29, 0, 0));
                    c.getPlayer().setConversation(0);
                }
                break;
            }
            default: {
                c.getPlayer().setConversation(0);
                break;
            }
        }
    }

    private static final boolean check(final MapleCharacter chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (Item item : pack.getItems()) {
            MapleInventoryType inventoryType = GameConstants.getInventoryType(item.getItemId());
            if (inventoryType != null) {
                if (inventoryType == MapleInventoryType.EQUIP) {
                    eq++;
                } else if (inventoryType == MapleInventoryType.USE) {
                    use++;
                } else if (inventoryType == MapleInventoryType.SETUP) {
                    setup++;
                } else if (inventoryType == MapleInventoryType.ETC) {
                    etc++;
                } else if (inventoryType == MapleInventoryType.CASH) {
                    cash++;
                }
            }
            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
                return false;
            }
        }
        if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return false;
        }
        return true;
    }

    private static final boolean deletePackage(final int accid, final int packageid, final int chrId) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("DELETE from hiredmerch where accountid = ? OR packageid = ? OR characterid = ?");
            ps.setInt(1, accid);
            ps.setInt(2, packageid);
            ps.setInt(3, chrId);
            ps.executeUpdate();
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static final MerchItemPackage loadItemFrom_Database(final int accountid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ?");
            ps.setInt(1, accountid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                con.close();
                return null;
            }
            final int packageid = rs.getInt("PackageId");
            final MerchItemPackage pack = new MerchItemPackage();
            pack.setPackageid(packageid);
            pack.setMesos(rs.getInt("Mesos"));
            pack.setSavedTime(rs.getLong("time"));
            pack.setMap(rs.getInt("map"));
            pack.setX(rs.getInt("x"));
            pack.setY(rs.getInt("y"));
            Map<Long, Pair<Item, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid);
            if (items != null) {
                List<Item> iters = new ArrayList<Item>();
                for (Pair<Item, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }
            rs.close();
            ps.close();
            con.close();
            return pack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
