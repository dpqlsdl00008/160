/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.world;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.TimerTask;

/**
 * @author Administrator
 */
public class runArdentmillReset extends TimerTask {

    public void run() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE ardentmill SET mineNovice = 0, mineIntermediate = 0, mineAdvanced = 0, mineExpert = 0, herbNovice = 0, herbIntermediate = 0, herbAdvanced = 0, herbExpert = 0");
            ps.execute();
            System.out.println("Successfully reset Ardentmill's portals to zero.");
        } catch (SQLException e) {
            System.err.println("ERROR Running ArdentmillReset error: " + e);
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
}
