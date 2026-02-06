
package handling.world;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.TimerTask;

public class AzwanDailyCheck extends TimerTask {
    public void run() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET azwanCoinsAvail = 0, azwanCoinsRedeemed = 0");
            ps.execute();
            System.out.println("Successfully set azwanCoinsAvail and azwanCoinsRedeemed to zero.");
        } catch (SQLException e) {
            System.err.println("ERROR Running AzwanDailyCheck error: " + e);
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
