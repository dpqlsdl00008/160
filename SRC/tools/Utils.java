/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author kwons
 */
public class Utils {

    public static String chargePoint(String name, int value, int rate, boolean dCheck) {
        String returnString = null;
        int ch = World.Find.findChannel(name);
        if (ch > 0) {
            MapleCharacter chr = World.getStorage(ch).getCharacterByName(name);
            if (chr != null) {
                chr.getClient().gainCharge(value, rate);
                chr.dropMessage(1, "후원포인트가 지급되었습니다.");
                returnString = "지급이 완료되었습니다.";
            }
        }

        if (returnString != null) {
            return returnString;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int hpoint = -1;
            int totalhpoint = -1;
            int accountid = 0;
            int cid = 0;

            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM characters WHERE `name` = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountid = rs.getInt("accountid");
                cid = rs.getInt("id");
            } else {
                returnString = "존재하지 않는 캐릭터입니다.";
            }
            if (returnString != null) {
                rs.close();
                ps.close();
                con.close();
                return returnString;
            } else {
                rs.close();
                ps.close();
            }

            int ach = World.Find.findAccChannel(accountid);
            if (ach > 0 && returnString == null) {
                MapleClient c = World.getStorage(ch).getClientById(accountid);
                if (c != null) {
                    c.gainCharge(value, rate);
                    c.getPlayer().dropMessage(1, "후원포인트가 지급되었습니다.");
                    returnString = "지급이 완료되었습니다.";
                }
            }
            if (ach >= 0 && returnString == null) {
                MapleClient c = CashShopServer.getPlayerStorage().getClientById(accountid);
                if (c != null) {
                    c.gainCharge(value, rate);
                    c.getPlayer().dropMessage(1, "후원포인트가 지급되었습니다.");
                    returnString = "지급이 완료되었습니다.";
                }
            }
            if (returnString != null) {
                return returnString;
            }

            ps = con.prepareStatement("SELECT * FROM account_customdata WHERE accid = ? AND (`key` = 'amount' OR `key` = 'total_amount')");
            ps.setInt(1, accountid);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == 5) {
                    hpoint = rs.getInt("value");
                } else if (rs.getInt("id") == 6) {
                    totalhpoint = rs.getInt("value");
                }
            }
            rs.close();
            ps.close();

            if (hpoint >= 0) {
                ps = con.prepareStatement("UPDATE account_customdata SET `value` = ? WHERE accid = ? AND `key` = 'amount'");
                ps.setInt(1, hpoint + value);
                ps.setInt(2, accountid);
            } else {
                ps = con.prepareStatement("INSERT INTO account_customdata VALUES (DEFAULT, ?, ?, ?, ?)");
                ps.setInt(1, accountid);
                ps.setInt(2, 5);
                ps.setString(3, "amount");
                ps.setInt(4, value);
            }
            ps.executeUpdate();
            ps.close();

            if (totalhpoint >= 0) {
                ps = con.prepareStatement("UPDATE account_customdata SET `value` = ? WHERE accid = ? AND `key` = 'total_amount'");
                ps.setInt(1, totalhpoint + value);
                ps.setInt(2, accountid);
            } else {
                ps = con.prepareStatement("INSERT INTO account_customdata VALUES (DEFAULT, ?, ?, ?, ?)");
                ps.setInt(1, accountid);
                ps.setInt(2, 6);
                ps.setString(3, "total_amount");
                ps.setInt(4, value);
            }
            ps.executeUpdate();
            ps.close();
            con.close();
            returnString = "오프라인 지급이 완료되었습니다.";
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
            returnString = "에러가 발생하였습니다.";
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return returnString;
    }

    public static String gainHongboPoint(String name, int value) {
        String returnString = null;
        int ch = World.Find.findChannel(name);
        if (ch >= 0) {
            MapleCharacter chr = World.getStorage(ch).getCharacterByName(name);
            if (chr != null) {
                chr.getClient().gainHPoint(value);
                chr.dropMessage(1, "홍보포인트가 지급되었습니다.");
                returnString = "지급이 완료되었습니다.";
            }
        }

        if (returnString != null) {
            return returnString;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int hpoint = -1;
            int totalhpoint = -1;
            int accountid = 0;
            int cid = 0;
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM characters WHERE `name` = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountid = rs.getInt("accountid");
                cid = rs.getInt("id");
            } else {
                returnString = "존재하지 않는 캐릭터입니다.";
            }

            if (returnString != null) {
                rs.close();
                ps.close();
                con.close();
                return returnString;
            } else {
                rs.close();
                ps.close();
            }

            int ach = World.Find.findAccChannel(accountid);
            if (ach >= 0) {
                MapleClient c = World.getStorage(ch).getClientById(accountid);
                if (c != null && returnString == null) {
                    c.gainHPoint(value);
                    c.getPlayer().dropMessage(1, "홍보포인트가 지급되었습니다.");
                    returnString = "지급이 완료되었습니다.";
                }
            }
            if (ach >= 0 && returnString == null) {
                MapleClient c = CashShopServer.getPlayerStorage().getClientById(accountid);
                if (c != null) {
                    c.gainHPoint(value);
                    c.getPlayer().dropMessage(1, "홍보포인트가 지급되었습니다.");
                    returnString = "지급이 완료되었습니다.";
                }
            }
            if (returnString != null) {
                return returnString;
            }

            ps = con.prepareStatement("SELECT * FROM acckeyvalue WHERE id = ? and (`key` = 'HPoint' or `key` = 'TotalHPoint')");
            ps.setInt(1, accountid);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("key").equals("HPoint")) {
                    hpoint = rs.getInt("value");
                } else if (rs.getString("key").equals("TotalHPoint")) {
                    totalhpoint = rs.getInt("value");
                }
            }
            rs.close();
            ps.close();

            if (hpoint >= 0) {
                ps = con.prepareStatement("UPDATE acckeyvalue SET `value` = ? WHERE id = ? and `key` = 'HPoint'");
                ps.setInt(1, hpoint + value);
                ps.setInt(2, accountid);
            } else {
                ps = con.prepareStatement("INSERT INTO acckeyvalue VALUES (?, ?, ?, DEFAULT)");
                ps.setInt(1, accountid);
                ps.setString(2, "HPoint");
                ps.setInt(3, value);
            }
            ps.executeUpdate();
            ps.close();

            if (totalhpoint >= 0) {
                ps = con.prepareStatement("UPDATE acckeyvalue SET `value` = ? WHERE id = ? and `key` = 'TotalHPoint'");
                ps.setInt(1, totalhpoint + value);
                ps.setInt(2, accountid);
            } else {
                ps = con.prepareStatement("INSERT INTO acckeyvalue VALUES (?, ?, ?, DEFAULT)");
                ps.setInt(1, accountid);
                ps.setString(2, "TotalHPoint");
                ps.setInt(3, value);
            }
            ps.executeUpdate();
            ps.close();
            con.close();
            returnString = "오프라인 지급이 완료되었습니다.";
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
            returnString = "에러가 발생하였습니다.";
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return returnString;
    }
}
